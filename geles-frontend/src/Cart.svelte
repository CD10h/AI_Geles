<script>
  import type { Flower, FlowerInCart } from "./App.svelte";
  import { onMount } from "svelte";
  import { server_url } from "./index";
  import axios from "axios";

  export let flowers: Flower[] = [];

  interface Cart {
    id: number;
    flowersInCart: FlowerInCart[];
  }

  let cart: Cart = {
    id: 0,
    flowersInCart: []
  };

  async function getFlowersInCart() {
    // Download data from server
    const response = await axios.get(`${server_url}/carts/flowers/${cart.id}`);
    cart.flowersInCart = response.data.map((flowerInCart: any) => {
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
        sum: flower.price * flowerInCart.amount
      };
    });
  }

  function updateSum(flower: FlowerInCart, newAmount: string) {
    const index = cart.flowersInCart.findIndex(
      flowerInCart => flowerInCart.id === flower.id
    );
    if (index === -1) {
      return;
    }
    cart.flowersInCart[index].sum = flower.price * +newAmount;
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
    cart.flowersInCart = response.data.flowersInCart.map(
      (flowerInCart: any) => {
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
          sum: flower.price * flowerInCart.amount
        };
      }
    );
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
        <th>{flowerInCart.name}</th>
        <th
          ><input
            type="number"
            bind:value={flowerInCart.amount}
            min="1"
            max="100"
            size="5"
            on:input={e => updateSum(flowerInCart, e.currentTarget.value)}
          /></th
        >
        <th>{flowerInCart.price}</th>
        <th>{flowerInCart.sum?.toFixed(2)}</th>
        <button on:click={() => handleDelete(flowerInCart)}>Pašalinti</button>
      </tr>
    {/each}
  </table>
  <button on:click={() => handleUpdate()}>Išsaugoti pakeitimus</button>
</div>
