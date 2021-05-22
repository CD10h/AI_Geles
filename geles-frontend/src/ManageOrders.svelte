<script>
  import { onMount } from "svelte";
  import { user } from "./stores";
  import axios from "axios";
  import { Link } from "svelte-routing";
  import { OrderStatus } from "./enums";
  //import {Order, Flower} from "../types/flower";

  $: isAdmin = $user && $user.admin;

  enum SortBy {
    ORDER_ID,
    ORDER_DATE,
    ORDER_STATUS,
    ORDER_USER
  }

  const orderStatusSortingOrder = {
    [OrderStatus.UNPAID]: 1,
    [OrderStatus.PAID]: 2,
    [OrderStatus.CONFIRMED]: 3,
    [OrderStatus.DELIVERED]: 4,
    [OrderStatus.CANCELED]: 5
  } as const;

  let users: User[] = [];

  let flowers: Flower[] = [];
  let orders: Order[] = [];

 

  async function getFlowers() {
    // Download data from server
    const response = await axios.get("/flowers/");
    flowers = response.data;
  }

  function userFromId(id: number): User {
    return users.find(f => f.id == id) as User;
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


  function mapFlowersInOrder(data: any) {
    return data.map((flowerInCart: any) => {
      const flower = flowers.find(
        flower => flower.id === flowerInCart.flowerId
      );
      if (!flower) {
        return flowerInCart;
      }
      return {
        ...flowerInCart,
        name: flower.name,
        price: flower.price,
        sum: flower.price * flowerInCart.amount,
        photo: flower.photo
      };
    });
  }

  async function handleDelete(order: Order) {
    const response = await axios.delete(`/orders/${order.id}`, {
      withCredentials: true
    });
  }

  async function handlePay(order: Order) {
    const response = await axios.post(`/orders/${order.id}/pay`, {
      withCredentals: true
    });
    if (response.status == 403) alert("Apmokėti neleidžiama. :(");
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

  function sort(sortingOrder: SortBy) {
    switch (sortingOrder) {
      case SortBy.ORDER_DATE:
        orders = orders.sort(
          (a: Order, b: Order) =>
            +new Date(a.createdDate) - +new Date(b.createdDate)
        );
        break;
      case SortBy.ORDER_ID:
        orders = orders.sort((a: Order, b: Order) => a.id - b.id);
        break;
      case SortBy.ORDER_STATUS:
        orders = orders.sort(
          (a, b) =>
            orderStatusSortingOrder[a.orderStatus] -
            orderStatusSortingOrder[b.orderStatus]
        );
        break;
      default:
        throw "Unknown sorting order :(";
    }
  }

  async function getOrders() {
    const response = await axios.get<Order[]>(`/orders/`, {
      withCredentials: true
    });
    console.log(response.data);

    orders = response.data;
    sort(SortBy.ORDER_ID);
  }
  async function loadUsers() {
    let usersRes = await axios.get(`/users/all/`, { withCredentials: true });
    users = usersRes.data;
  }
  // Run code on component mount (once)
  onMount(async () => {
    await verifyLogin();
    if (isAdmin) await loadUsers();
    await getOrders();
  });
</script>

<div class="order-list">
  {#if isAdmin}
    <h2>Visi užsakymai</h2>
  {:else}
    <h2>Mano užsakymai</h2>
  {/if}
  {#if orders.length > 0}
    <table class="orders-table">
      <tr>
        <th on:click={() => sort(SortBy.ORDER_ID)}>
          <div style="cursor:pointer;">Užsakymo ID</div>
        </th>
        <th on:click={() => sort(SortBy.ORDER_DATE)}>
          <div style="cursor:pointer;">Užsakymo data</div>
        </th>
        {#if isAdmin}
          <th on:click={() => sort(SortBy.ORDER_USER)}>
            <div style="cursor:pointer;">Vartotojas</div>
          </th>
        {/if}
        <th>Kiekis</th>
        <th>Suma</th>
        <th on:click={() => sort(SortBy.ORDER_STATUS)}
          ><div style="cursor:pointer;">Statusas</div></th
        >
        <th>Veiksmai</th>
      </tr>
      {#each orders as order, i (order.id)}
        <tr
          class="order-row {order.orderStatus === OrderStatus.UNPAID
            ? 'order-row-unpaid'
            : ''} {i % 2 === 0 ? 'order-row-grey' : ''}"
        >
          <td>
            {order.id}
          </td>
          <td>
            {formatDate(order.createdDate)}
          </td>
          {#if isAdmin}
            <td>
              {userFromId(order.userId).username}
            </td>
          {/if}
          <td>
            {order.orderFlowers.reduce(
              (acc, flower) => acc + flower.quantity,
              0
            )}
          </td>
          <td>
            {order.totalOrderPrice.toFixed(2)} €
          </td>
          <td>
            <span
              class={order.orderStatus === OrderStatus.UNPAID
                ? "order-cell-unpaid"
                : ""}
            >
              {orderStatusString(order.orderStatus)}
            </span>
          </td>
          <td>
            <Link to={"/editOrder/" + order.id}>Daugiau info</Link>
          </td>
        </tr>
      {/each}
    </table>
  {:else}
    <p>Nėra užsakymų!</p>
  {/if}
</div>

<style>
  .order-list {
    margin-left: 40px;
    margin-right: 40px;
  }

  .orders-table {
    width: 100%;
  }

  /* .order-row {
    opacity: 1;
  }

  .order-row-grey {
    opacity: 0.75;
  } */

  .order-row-unpaid {
    background-color: red;
  }

  /* .order-row:hover {
    opacity: 0.75;
  } */

  .order-cell-unpaid {
    font-weight: bold;
  }

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
</style>
