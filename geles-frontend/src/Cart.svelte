<script>
  import type { Flower, FlowerInCart } from "./App.svelte";
  import { onMount } from "svelte";
  import { server_url } from "./index";
  import axios from "axios";

  export let flowers: Flower[] = [];

  interface Cart {
    id: number;
    flowersInCart: [];
  }

  let flowersInCart: FlowerInCart[] = [];

  let cart = {
    id: 0,
    flowersInCart: flowersInCart
  };

  async function getFlowersInCart() {
    // Download data from server
    const response = await axios.get(`${server_url}/carts/flowers/${cart.id}`);
    cart.flowersInCart = response.data;
  }

  function handleDelete(fl: FlowerInCart) {
    let index = cart.flowersInCart.indexOf(fl);
    if (index !== -1) {
      cart.flowersInCart.splice(index, 1);
      cart.flowersInCart = cart.flowersInCart;
    }
  }

  async function handleUpdate() {
    const response = await axios.put(`${server_url}/carts/${cart.id}`, cart);
    cart.flowersInCart = response.data.flowersInCart;
  }

  async function getCartId() {
    const response = await axios.get<Cart>(`${server_url}/users/cart/`, {
      withCredentials: true
    });
    cart = response.data;
    getFlowersInCart();
  }

  // Run code on component mount (once)
  onMount(() => {
    getCartId();
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
    {#each cart.flowersInCart as flowerInCart (flowerInCart.id)}
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
