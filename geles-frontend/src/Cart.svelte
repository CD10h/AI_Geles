<script>
  import type { Flower, FlowerInCart } from "./App.svelte";
  import { onMount } from "svelte";
  import { server_url } from "./index";
  import axios from "axios";
  import { select_multiple_value } from "svelte/internal";

  export let flowers: Flower[] = [];

  // Variable to hold fetched list
  let flowersInCart: FlowerInCart[] = [];

  // TEMPORARY HARDCODED ID, REPLACE WITH ACTUAL FUNCTION
  let id = 1;

  async function getFlowersInCart() {
    // Download data from server
    const response = await axios.get(`${server_url}/carts/flowers/${id}`);
    flowersInCart = response.data;
  }

  function handleDelete(fl: FlowerInCart) {
    let index = flowersInCart.indexOf(fl);
    if (index !== -1) {
      flowersInCart.splice(index, 1);
      flowersInCart = flowersInCart;
    }
  }

  async function handleUpdate() {
    let cart = {
      id: id,
      flowersInCart: flowersInCart
    };
    const response = await axios.put(`${server_url}/carts/${id}`, cart);
    flowersInCart = response.data.flowersInCart;
  }

  // Run code on component mount (once)
  onMount(() => {
    getFlowersInCart();
  });
</script>

<div class="flowerincart-list">
  <table>
    <tr>
      <th>Gėlė</th>
      <th>Kiekis</th>
      <th>Vnt. kaina</th>
      <th>Suma</th>
    </tr>
    {#each flowersInCart as flowerInCart (flowerInCart.id)}
      <tr>
        <th
          >{flowers.find(flower => flower.id === flowerInCart.flowerId)
            ?.name}</th
        >
        <th
          ><input
            type="number"
            bind:value={flowerInCart.amount}
            min="1"
            max="100"
            size="5"
          /></th
        >
        <th
          >{flowers.find(flower => flower.id === flowerInCart.flowerId)
            ?.price}</th
        >
        <th
          >{(flowers.find(flower => flower.id === flowerInCart.flowerId)
            ?.price ?? 0) * flowerInCart.amount}</th
        >
        <button on:click={() => handleDelete(flowerInCart)}>Pašalinti</button>
      </tr>
    {/each}
  </table>
  <button on:click={() => handleUpdate()}>Išsaugoti pakeitimus</button>
</div>
