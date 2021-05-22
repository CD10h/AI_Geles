<script>
  import { onMount } from "svelte";
  import { server_url } from "./index";
  import axios from "axios";
  import { Link } from "svelte-routing";
  import { bind, text } from "svelte/internal";
  import { OrderStatus } from "./enums";

  export let id: string;

  interface OrderEdit {
    orderFlowers: OrderFlower[];
    address: string;
    contactPhone: string;
  }

  enum OrderEditButton {
    SAVE,
    PAY,
    CANCEL
  };


  let order: Omit<Order, "id" | "userId"> = {
    createdDate: "",
    totalOrderPrice: 0,
    address: "",
    contactPhone: "",
    orderFlowers: [],
    orderStatus: OrderStatus.CANCELED
  };

  let editDto: OrderEdit = {
    orderFlowers: [],
    contactPhone: "",
    address: ""
  };

  let flowers: Flower[];
  let orderTotal: number = 0;

  function flowerFromId(id: number): Flower {

    console.log({id:id,flowers: flowers});
    return flowers.find(f => f.id == id) as Flower;
  }

  function recalcSum() {
    orderTotal = order.orderFlowers
      .map(
        flowerInOrder =>
          flowerFromId(flowerInOrder.flowerId).price * flowerInOrder.quantity
      )
      .reduce((a, b) => a + b, 0);
  }

  function rowSum(flower: OrderFlower) {
    return flower.quantity * flowerFromId(flower.flowerId).price;
  }

  function DisableButton(
    status: OrderStatus,
    button: OrderEditButton
  ): boolean {
    switch (button) {
      case OrderEditButton.SAVE:
        return order.orderStatus != "UNPAID";
      case OrderEditButton.CANCEL:
        return (
          order.orderStatus == OrderStatus.CANCELED ||
          order.orderStatus == OrderStatus.DELIVERED
        );
      case OrderEditButton.PAY:
        return order.orderStatus != "UNPAID";
    }
  }

  // Run code on component mount (once)
  onMount(async () => {
    await axios.get(`/flowers/`, { withCredentials: true }).then(resp => {
      flowers = resp.data;
      console.log(resp.data);
    });
    axios.get(`/orders/${id}`, { withCredentials: true }).then(resp => {
      order = resp.data;
      console.log(resp);
      editDto.orderFlowers = order.orderFlowers;
      editDto.address = order.address;
      editDto.contactPhone = order.contactPhone;
      
      recalcSum();
    });
  });

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

  function mapOrderData(resp: any) {
    order = resp.data;
    editDto.orderFlowers = order.orderFlowers;
    editDto.address = order.address;
    editDto.contactPhone = order.contactPhone;
    recalcSum();
  }

  function handleUpdate() {
    axios
      .put(`/orders/${id}/edit`, editDto, { withCredentials: true })
      .then(resp => mapOrderData(resp));
  }

  function handlePay() {
    axios.post(`/orders/${id}/pay`).then(rsp => mapOrderData(rsp));
  }

  function handleCancel() {
    axios.post(`/orders/${id}/cancel`).then(rsp => mapOrderData(rsp));
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
</script>

<h2>
  Užsakymas nr <strong>{order.id}</strong> Būsena : {orderStatusString(
    order.orderStatus
  )}
</h2>
<div class="row">
  <div class="column">
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
        disabled={DisableButton(order.orderStatus, OrderEditButton.SAVE)}
        on:click={() => handleUpdate()}
      >
        Išsaugoti pakeitimus
      </button>
      <button
        class="button paybutton"
        disabled={DisableButton(order.orderStatus, OrderEditButton.PAY)}
        on:click={() => handlePay()}
      >
        Apmokėti
      </button>

      <button
        class="button cancelbutton"
        disabled={DisableButton(order.orderStatus, OrderEditButton.CANCEL)}
        on:click={() => handleCancel()}
      >
        Atšaukti
      </button>
    </div>
  </div>
  <div class="column">
    <div class="editorder-inputs">
      <div class="editorder-inputrow">
        <label for="adress">Adresas</label>
        {#if order.orderStatus == "UNPAID"}
          <input
            class="editorder-textinput"
            id="address"
            type="text"
            bind:value={editDto.address}
          />
        {:else}
          <div  id="address" class="editorder-textoutput">{order.address}</div>
        {/if}
      </div>
      <div class="editorder-inputrow">
        <label for="phone">Telefonas</label>
        {#if order.orderStatus == "UNPAID"}
          <input
            class="editorder-textinput"
            type="text"
            id="phone"
            bind:value={editDto.contactPhone}
          />
        {:else}
          <div  id="phone" class="editorder-textoutput">
            Telefonas : {order.contactPhone}
          </div>
        {/if}
      </div>
      <br />
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
    background-color: #d9d9d9;
    border: 4px solid #8ebf42;
    margin-bottom: 10px;
  }

  th,
  td {
    padding: 4px 8px;
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
    flex: 50%;
  }
  .button {
    background-color: #8ebf42;
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
