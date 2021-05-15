<script>
  import { onMount } from "svelte";
  import { server_url } from "./index";
  import axios from "axios";
  import { Link } from "svelte-routing";
import { bind, text } from "svelte/internal";

  export let id: string;

  interface OrderEdit {
    orderFlowers: OrderFlower[];
    address: string;
    contactPhone: string;
  }

  let order: Omit<Order, "id" | "userId" | "orderStatus"> = {
    createdDate: "",
    totalOrderPrice: 0,
    address: "",
    contactPhone: "",
    orderFlowers: []
  };

  let editDto: OrderEdit = {
    orderFlowers: [],
    contactPhone: "",
    address: ""
  };

  let flowers: Flower[];
  let orderTotal: number = 0;

  function flowerFromId(id: number): Flower {
    return flowers.find(f => f.id == id) as Flower;
  }

  function recalcSum() {
    orderTotal = order.orderFlowers
      .map(
        flowerInOrder =>
          flowerFromId(flowerInOrder.id).price * flowerInOrder.quantity
      )
      .reduce((a, b) => a + b, 0);
  }

  function rowSum(flower: OrderFlower) {
    return flower.quantity * flowerFromId(flower.id).price;
  }


  // Run code on component mount (once)
  onMount(() => {
    axios
      .get(`/flowers/`, { withCredentials: true })
      .then(resp => (flowers = resp.data));
    axios.get(`/orders/${id}`, { withCredentials: true }).then(resp => {
      order = resp.data;
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

  function handleUpdate() {
    axios
      .put(`/orders/${id}/edit`, editDto, { withCredentials: true })
      .then(resp => {
        order = resp.data;
        editDto.orderFlowers = order.orderFlowers;
        editDto.address = order.address;
        editDto.contactPhone = order.contactPhone;
        recalcSum();
      });

  }
</script>

<h2>Užsakymas nr <strong>{order.id}</strong></h2>
<table>
  <tr>
    <th colspan="2">Gėlė</th>
    <th>Kiekis</th>
    <th>Vnt. kaina</th>
    <th>Suma</th>
    <th >Veiksmai</th>
  </tr>
  {#each editDto.orderFlowers as flower (flower.id)}
    <tr>
      <div class="imagecontainer">
        {#if flowerFromId(flower.id).photo != null}
          <img
            class="flower-list-item-photo"
            src={`${server_url}/static/flowers/${
              flowerFromId(flower.id).photo
            }`}
            alt={flowerFromId(flower.id).name}
            width="80"
            height="80"
          />
        {/if}
      </div>
      <td>{flowerFromId(flower.id).name}</td>
      <td>
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
      </td>
      <td class="number">{flowerFromId(flower.id).price} €</td>
      <td class="number">{rowSum(flower).toFixed(2)}€</td>
      <td>
        <button on:click={() => handleDelete(flower)}>Pašalinti</button>
      </td>
    </tr>
  {/each}
</table>




<label for="adress">Adresas</label>
<input
id="address"
  type="text"
  bind:value={editDto.address} />
<br/>
<label for="phone">Telefonas</label>

<input
  type="text"
  id="phone"
  bind:value={editDto.contactPhone} />
<br/>
<button class="savebutton" on:click={() => handleUpdate()}
  >Išsaugoti pakeitimus</button
>

<style>
  .flowerincart-list {
    margin-left: 40px;
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

  .outsidecart {
    background-color: white;
    margin-right: 5px;
  }

  .savetemplatecontainer {
    margin-top: 20px;
  }

  .flowercart {
    margin-bottom: 80px;
  }

  .templatename {
    text-align: left;
  }
</style>
