<script>
  import { server_url } from "./index";
  import { Link } from "svelte-routing";
  import { onMount } from "svelte";

  import type { Flower, FlowerInCart } from "./App.svelte";
  import axios from "axios";

  // Variable to hold fetched list
  export let flowers: Flower[];
  export let owner: boolean | undefined = false;
  export let onChange: (() => void) | undefined = undefined;

  interface Cart {
    id: number;
    flowersInCart: [];
  }

  let cart = {
    id: 0,
    flowersInCart: []
  };

  let amount = 1;

  let flowerInCart: Omit<FlowerInCart, "id"> = {
    amount: 0,
    flowerId: 0,
    cartId: 0,
    sum: 0,
    price: 0,
    name: ""
  };

  let isLoggedIn = !!document.cookie
    .split("; ")
    .find(cookie => cookie.startsWith("auth"));

  async function handleDelete(id: number, name: string) {
    if (window.confirm(`Ar tikrai norite ištrinti gėlę ${name}?`)) {
      await axios.delete(`${server_url}/flowers/${id}`);
      if (onChange) onChange();
    }
  }

  async function handleToCart(id: number, amount: number) {
    //await axios.put(`${server_url}/carts/${id}`);
    flowerInCart.amount = amount;
    flowerInCart.flowerId = id;
    flowerInCart.cartId = cart.id;
    axios.post(`${server_url}/carts/flowers/`, flowerInCart);
    if (onChange) onChange();
  }

  async function getCartId() {
    const response = await axios.get<Cart>(`${server_url}/users/cart/`, {
      withCredentials: true
    });
    cart = response.data;
  }

  onMount(() => {
    getCartId();
  });
</script>

<div class="flower-list">
  <!--
    Map each `flowers` entry as an HTML element `li` 
    When `flowers` updates, so does this block
  -->
  {#each flowers as flower (flower.id)}
    <div class="flower-list-item">
      <img
        class="flower-list-item-photo"
        src={`${server_url}/static/${flower.photo}`}
        alt={flower.name}
      />
      <div class="flower-list-item-name">{flower.name}</div>
      <div class="flower-list-item-price">{flower.price} €</div>
      <p class="flower-list-item-description">
        {flower.description}
      </p>
      {#if owner}
        <Link to="/update/{flower.id}">Redaguoti</Link>
        <button on:click={() => handleDelete(flower.id, flower.name)}
          >Ištrinti</button
        >
      {/if}
      {#if isLoggedIn}
        <input type="number" bind:value={amount} min="1" max="100" />
        <button on:click={() => handleToCart(flower.id, amount)}>+</button>
      {/if}
    </div>
  {/each}
</div>

<style>
  .flower-list {
    display: flex;
    flex-wrap: wrap;
    margin: 0 -16px;
    padding: 0 16px;
  }

  .flower-list-item {
    box-shadow: black 0 0 4px;
    margin: 16px;
    padding: 8px;
    min-width: 200px;
    max-width: 300px;
    flex-basis: calc(25% - 48px);
  }

  .flower-list-item-name {
    font-size: 24px;
  }

  .flower-list-item-photo {
    width: 100%;
  }

  .flower-list-item-price {
  }

  .flower-list-item-description {
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 4;
    -webkit-box-orient: vertical;
    /* text-align: justify; */
  }

  @media (max-width: 1024px) {
    .flower-list-item {
      flex-basis: calc(33.3333333% - 48px);
    }
  }

  @media (max-width: 776px) {
    .flower-list-item {
      flex-basis: calc(50% - 48px);
    }
  }

  @media (max-width: 528px) {
    .flower-list-item {
      flex-basis: calc(100% - 48px);
    }
  }
</style>
