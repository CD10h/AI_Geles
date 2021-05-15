<script>
  import { onMount } from "svelte";
  import axios from "axios";
  import { Link } from "svelte-routing";
import Order from "./Order.svelte";

  interface CartTemplate {
    id: number;
    name: string;
    flowersInCart: FlowerInCart[];
  }

  let flowers: Flower[] = [];
  let orders: Order[] = [];

  async function getFlowers() {
    // Download data from server
    const response = await axios.get("/flowers/");
    flowers = response.data;
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
    const response = await axios.post(`/orders/${order.id}/pay`, {withCredentals: true});
    if (response.status == 403)
        alert('Apmokėti neleidžiama. :(')
  }

  async function getOrders() {
    console.log("Getting orders");
    const response = await axios.get(`/orders/`, {
      withCredentials: true
    });

    orders = response.data.map((order: Order) => {
      //cartTemplate.flowersInCart = mapFlowersInCart(cartTemplate.flowersInCart);
      return order;
    });
    console.log(orders);
  }
  function formatDate(date) {
    var d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2) 
        month = '0' + month;
    if (day.length < 2) 
        day = '0' + day;

    return [year, month, day].join('-');
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
  onMount(() => {
    getOrders();
  });
</script>

<div class="order-list">
  <h2>Mano užsakymai</h2>
  {#if orders.length > 0}
    <table class="orders-table">
      <tr>
        <th>Užsakymo ID</th>
        <th>Užsakymo data</th>
        <th>Kiekis</th>
        <th>Suma</th>
        <th>Statusas</th>
        <th>Veiksmai</th>
      </tr>
      {#each orders as order (order.id)}
        <tr>
          <td>
            {order.id}
          </td>
          <td>
            { formatDate(order.createdDate) }
          </td>
          <td>
            {order.orderFlowers.length}
          </td>
          <td>
            {order.totalOrderPrice.toFixed(2)}
          </td>
          <td>
            {orderStatusString(order.orderStatus)}
          </td>
          <td>
            <a href="{'/editOrder/' + order.id}"> Redaguoti</a> 
            {#if order.orderStatus != "CANCELED"}
            / <a href="#" on:click={() => handleDelete(order).then(getOrders)}> Trinti</a>
            {/if}
            {#if order.orderStatus == "UNPAID"}
            / <a href="#" on:click={() => handlePay(order).then(getOrders)}> Apmokėti</a> 
            {/if}
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
</style>
