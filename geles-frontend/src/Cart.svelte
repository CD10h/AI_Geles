<script>
  import type { Flower, FlowerInCart } from "./App.svelte";
  import { onMount } from "svelte";
  import { server_url } from "./index";
  import axios from "axios";
  import { select_multiple_value } from "svelte/internal";

  export let flowers: Flower[] = [];

  // Variable to hold fetched list
  let flowersInCart: FlowerInCart[] = [];
  let id = 1;

  async function getFlowersInCart() {
    // Download data from server
    const response = await axios.get(`${server_url}/carts/flowers/${id}`);
    flowersInCart = response.data;
  }

  // Run code on component mount (once)
  onMount(() => {
    getFlowersInCart();
  });

  function onChange() {
    getFlowersInCart();
  }
</script>

<div class="flowerincart-list">
  <table>
    <tr>
      <th>Flower</th>
      <th>Amount</th>
      <th>Unit price</th>
      <th>Total price</th>
    </tr>
    {#each flowersInCart as flowerInCart (flowerInCart.id)}
      <tr>
        <th>{flowers[flowerInCart.id].name}</th>
        <th>{flowerInCart.amount}</th>
        <th>{flowers[flowerInCart.id].price}</th>
        <th>{flowers[flowerInCart.id].price * flowerInCart.amount}</th>
      </tr>
    {/each}
  </table>
</div>
