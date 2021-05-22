<script>
  import { onMount } from "svelte";
  import { user } from "./stores";
  import { server_url } from "./index";
  import axios from "axios";
  import { getContext } from "svelte";
  import { notificationContextKey } from "./contexts";

  import { Link } from "svelte-routing";
  import { bind, text } from "svelte/internal";
  import { OrderStatus } from "./enums";
  import { AppNotificationType } from "./enums";

  const { addLoadingNotification, addNotification } =
    getContext<AppNotificationContext>(notificationContextKey);

  export let id: string;
  $: isAdmin = $user && $user.admin;

  interface OrderEdit {
    orderFlowers: OrderFlower[];
    address: string;
    contactPhone: string;
    version: number;
  }

  enum OrderEditButton {
    SAVE,
    PAY,
    CANCEL,
    ADMIN_CONFIRMPAY,
    ADMIN_CONFIRMDELIVERED
  }

  let order: Omit<Order, "id" | "userId"> = {
    createdDate: "",
    totalOrderPrice: 0,
    address: "",
    contactPhone: "",
    orderFlowers: [],
    orderStatus: OrderStatus.CANCELED,
    version: 0
  };

  let editDto: OrderEdit = {
    orderFlowers: [],
    contactPhone: "",
    address: "",
    version: 0
  };

  let editedUser: User = {
    id: 0,
    username: "",
    photo: null,
    admin: false,
    cartId: 0
  };
  let flowers: Flower[];
  let orderTotal: number = 0;

  function flowerFromId(id: number): Flower {
    return flowers.find(f => f.id == id) as Flower;
  }

  async function verifyLogin() {
    let oldUser: User | null;
    try {
      const response = await axios.get<User>("/users/", {
        withCredentials: true
      });
      oldUser = response.data;
    } catch (e) {
      oldUser = null;
    }
    user.set(oldUser);
  }

  function recalcSum() {
    orderTotal = order.orderFlowers
      .map(
        flowerInOrder =>
          flowerFromId(flowerInOrder.flowerId).price * flowerInOrder.quantity
      )
      .reduce((a, b) => a + b, 0);
  }

  async function loadUser() {
    let userRes = await axios.get(`/users/${order.userId}`, {
      withCredentials: true
    });
    editedUser = userRes.data;
  }

  function rowSum(flower: OrderFlower) {
    return flower.quantity * flowerFromId(flower.flowerId).price;
  }

  function disableButton(
    status: OrderStatus,
    button: OrderEditButton
  ): boolean {
    let enableButton: boolean = false;
    switch (button) {
      case OrderEditButton.SAVE:
        if (isAdmin) {
          enableButton =
            status != OrderStatus.CANCELED && status != OrderStatus.DELIVERED;
        } else {
          enableButton = status == OrderStatus.UNPAID;
        }
        break;
      case OrderEditButton.CANCEL:
        enableButton =
          status != OrderStatus.CANCELED && status != OrderStatus.DELIVERED;
        break;
      case OrderEditButton.PAY:
        enableButton = status == OrderStatus.UNPAID;
        break;
      case OrderEditButton.ADMIN_CONFIRMPAY:
        enableButton = status == OrderStatus.PAID;
        break;
      case OrderEditButton.ADMIN_CONFIRMDELIVERED:
        enableButton = status == OrderStatus.CONFIRMED;
        break;
    }
    return !enableButton;
  }

  function EnableOrderEdits(): boolean {
    let enable: boolean = false;
    if (isAdmin) {
      enable =
        order.orderStatus != OrderStatus.CANCELED &&
        order.orderStatus != OrderStatus.DELIVERED;
    } else {
      enable = order.orderStatus == OrderStatus.UNPAID;
    }
    return enable;
  }

  function handleDelete(fl: OrderFlower) {
    let index = editDto.orderFlowers.indexOf(fl);
    if (index !== -1) {
      editDto.orderFlowers = [
        ...editDto.orderFlowers.slice(0, index),
        ...editDto.orderFlowers.slice(index + 1)
      ];
    }
    recalcSum();
  }

  function handleError(reason: any) {
    let errString: string = "";
    if (reason.response) {
      errString =
        reason.response.data +
        " " +
        reason.response.status +
        " " +
        reason.response.headers;
    } else if (reason.request) {
      let errString = reason.request;
    } else {
      // Something happened in setting up the request that triggered an Error
      let errString = reason;
    }
    console.log("Error", reason);
    addNotification(errString, AppNotificationType.DANGER);
  }

  async function getOrderData() {
    let resp = await axios.get(`/orders/${id}`, { withCredentials: true });
    order = resp.data;
    editDto.orderFlowers = order.orderFlowers;
    editDto.address = order.address;
    editDto.contactPhone = order.contactPhone;
    editDto.version = order.version;
    recalcSum();
  }

  function handleUpdate() {
    axios.put(`/orders/${id}/edit`, editDto)
    .then(getOrderData)
    .then(() => addNotification("Užsakymas atnaujintas",AppNotificationType.SUCCESS))
    .catch(handleError);
  }

  function handlePay() {
    axios
      .post(`/orders/${id}/pay`, { version: order.version })
      .then(getOrderData)
      .then(() => addNotification("Užsakymas apmokėtas",AppNotificationType.SUCCESS))
      .catch(handleError);
  }

  function handleCancel() {
    axios
      .post(`/orders/${id}/cancel`, { version: order.version })
      .then(getOrderData)
      .then(() => addNotification("Užsakymas atšauktas",AppNotificationType.SUCCESS))
      .catch(handleError);
  }
  function handleConfirmOrder() {
    axios
      .post(`/orders/${id}/confirm`, { version: order.version })
      .then(getOrderData)
      .then(() => addNotification("Užsakymas patvirtintas",AppNotificationType.SUCCESS))
      .catch(handleError);
  }
  function handleConfirmDelivered() {
    axios
      .post(`/orders/${id}/confirmDelivery`, { version: order.version })
      .then(getOrderData)
      .then(() => addNotification("Užsakymo pristatymas patvirtintas",AppNotificationType.SUCCESS))
      .catch(handleError);
  }

  function formatDate(date: any) {
    var d = new Date(date),
      month = "" + (d.getMonth() + 1),
      day = "" + d.getDate(),
      year = d.getFullYear();

    if (month.length < 2) month = "0" + month;
    if (day.length < 2) day = "0" + day;

    return [year, month, day].join("-");
  }
  function orderStatusString(status: any): string {
    switch (status) {
      case "UNPAID":
        return "Neapmokėta";
      case "PAID":
        return "Apmokėtas";
      case "CONFIRMED":
        return "Patvirtintas";
      case "DELIVERED":
        return "Pristatytas";
      case "CANCELED":
        return "Atšauktas";
      default:
        return "???????";
    }
  }

  // Run code on component mount (once)
  onMount(async () => {
    await axios.get(`/flowers/`).then(resp => {
      flowers = resp.data;
    });
    await getOrderData();
    await verifyLogin();
    if (isAdmin) await loadUser();
  });
</script>

<h2>Užsakymas</h2>
<div class="row">
  <div class="column">
    <div class="flowers-table">
      <table>
        <tr>
          <th colspan="2">Gėlė</th>
          <th>Kiekis</th>
          <th>Vnt. kaina</th>
          <th>Suma</th>
          {#if order.orderStatus == "UNPAID"}
            <th>Veiksmai</th>
          {/if}
        </tr>
        {#each editDto.orderFlowers as flower (flower.id)}
          <tr>
            <div class="imagecontainer">
              {#if flowerFromId(flower.flowerId).photo != null}
                <img
                  class="flower-list-item-photo"
                  src={`${server_url}/static/flowers/${
                    flowerFromId(flower.flowerId).photo
                  }`}
                  alt={flowerFromId(flower.flowerId).name}
                  width="80"
                  height="80"
                />
              {/if}
            </div>
            <td>{flowerFromId(flower.flowerId).name}</td>
            <td>
              {#if order.orderStatus == "UNPAID"}
                <input
                  type="number"
                  bind:value={flower.quantity}
                  min="1"
                  max="100"
                  size="5"
                  on:input={e => {
                    recalcSum();
                  }}
                />
              {:else}
                {flower.quantity}
              {/if}
            </td>
            <td class="number">{flowerFromId(flower.flowerId).price} €</td>
            <td class="number">{rowSum(flower).toFixed(2)} €</td>
            {#if order.orderStatus == "UNPAID"}
              <td>
                <button on:click={() => handleDelete(flower)}>Pašalinti</button>
              </td>
            {/if}
          </tr>
        {/each}
      </table>
      <div class="row buttonsrow">
        <button
          class="button savebutton"
          disabled={disableButton(order.orderStatus, OrderEditButton.SAVE)}
          on:click={() => handleUpdate()}
        >
          Išsaugoti pakeitimus
        </button>
        <button
          class="button paybutton"
          disabled={disableButton(order.orderStatus, OrderEditButton.PAY)}
          on:click={() => handlePay()}
        >
          Apmokėti
        </button>

        <button
          class="button cancelbutton"
          disabled={disableButton(order.orderStatus, OrderEditButton.CANCEL)}
          on:click={() => handleCancel()}
        >
          Atšaukti
        </button>
      </div>
    </div>
  </div>
  <div class="column">
    <div class="editorder-inputs">
      {#if isAdmin}
        <div class="editorder-inputrow" style="height: 30px;">
          <label for="user">Vartotojas</label>
          <div id="user" class="editorder-textoutput">
            {editedUser.username}
          </div>
        </div>
      {/if}
      <div class="editorder-inputrow">
        <label for="orderId">Užsakymo ID</label>
        <div id="orderId" class="editorder-textoutput">
          <strong>{order.id}</strong>
        </div>
      </div>
      <div class="editorder-inputrow">
        <label for="orderStatus">Užsakymo Būsena</label>
        <div id="orderStatus" class="editorder-textoutput">
          {orderStatusString(order.orderStatus)}
        </div>
      </div>
      <div class="editorder-inputrow">
        <label for="adress">Adresas</label>
        {#if EnableOrderEdits()}
          <input
            class="editorder-textinput"
            id="address"
            type="text"
            bind:value={editDto.address}
          />
        {:else}
          <div id="address" class="editorder-textoutput">
            {order.address}
          </div>
        {/if}
      </div>
      <div class="editorder-inputrow">
        <label for="phone">Telefonas</label>
        {#if EnableOrderEdits()}
          <input
            class="editorder-textinput"
            type="text"
            id="phone"
            bind:value={editDto.contactPhone}
          />
        {:else}
          <div id="phone" class="editorder-textoutput">
            {order.contactPhone}
          </div>
        {/if}
      </div>
      <div class="editorder-inputrow">
        <label for="totalsum"> Bendra užsakymo suma</label>
        <div id="totalsum" class="editorder-textoutput">
          {orderTotal.toFixed(2)} &euro;
        </div>
      </div>
      <div class="editorder-inputrow">
        <label for="orderDate">Užsakymo data</label>
        <div id="orderDate" class="editorder-textoutput">
          {formatDate(order.createdDate)}
        </div>
      </div>

      {#if isAdmin}
        <div class="editorder-inputrow">
          <div style="padding-top:40px;padding-bottom:40px;">
            <strong>Administratoriaus funkcijos</strong>
          </div>
        </div>
        <div class="editorder-inputrow">
          <button
            class="button"
            disabled={disableButton(
              order.orderStatus,
              OrderEditButton.ADMIN_CONFIRMPAY
            )}
            on:click={() => handleConfirmOrder()}
          >
            Patvirtinti apmokėjimą
          </button>
        </div>
        <div class="editorder-inputrow">
          <button
            class="button"
            disabled={disableButton(
              order.orderStatus,
              OrderEditButton.ADMIN_CONFIRMDELIVERED
            )}
            on:click={() => handleConfirmDelivered()}
          >
            Patvirtinti užsakymo užbaigimą (pristatymą/atsiėmimą)
          </button>
        </div>
      {/if}
    </div>
  </div>
</div>

<style>
  h2 {
    color: #000000;
    font-size: 24px;
    font-weight: 400;
  }

  table,
  th,
  td {
    border: 2px solid #8ebf42;
    border-collapse: collapse;
  }

  table {
    width: 100%;
    background-color: #d9d9d9;
    border: 4px solid #8ebf42;
    margin-bottom: 10px;
  }

  th,
  td {
    padding: 4px 8px;
  }
  th {
    margin: auto;
  }

  td {
    text-align: center;
  }

  td.number {
    text-align: right;
  }

  td input {
    background-color: white;
  }

  img {
    margin: 8px;
    /* Weird bug with table cell height */
    margin-bottom: 4px;
  }

  .row {
    display: flex;
  }

  .column {
    flex-basis: 50%;
  }
  .flowers-table {
    width: min-content;
  }

  .editorder-inputs {
    display: table;
  }
  .editorder-inputrow {
    display: table-row;
  }
  .editorder-textinput {
    display: table-cell;
    margin-left: 30px;
  }

  .editorder-textoutput {
    display: table-cell;
    padding-left: 30px;
  }

  label {
    display: table-cell;
  }
</style>
