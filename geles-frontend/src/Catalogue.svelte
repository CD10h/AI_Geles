<script>
  import { server_url } from "./index";
  import { Link } from "svelte-routing";
  import { onMount } from "svelte";

  import axios from "axios";
  import { isLoggedIn } from "./isLoggedIn";

  // Variable to hold fetched list
  export let flowers: Flower[];
  export let owner: boolean | undefined = false;
  export let onChange: (() => void) | undefined = undefined;
  export let onFavoriteChange:
    | ((flower: Flower) => void)
    | undefined = undefined;

  interface Cart {
    id: number;
    flowersInCart: [];
  }

  let cartId = 0;

  let amount = 1;

  async function handleDelete(id: number, name: string) {
    if (window.confirm(`Ar tikrai norite ištrinti gėlę ${name}?`)) {
      await axios.delete(`${server_url}/flowers/${id}`);
      if (onChange) onChange();
    }
  }

  async function handleFavoriteChange(flower: Flower) {
    const newFlower = (
      await axios.put(
        `/flowers/${flower.id}/favorite`,
        { favorite: !flower.favorite },
        { withCredentials: true }
      )
    ).data;
    if (onFavoriteChange) onFavoriteChange(newFlower);
  }

  async function handleToCart(flowerId: number, amount: number) {
    await axios.post("/carts/flowers/", {
      amount,
      flowerId,
      cartId
    });
  }

  async function getCartId() {
    const response = await axios.get<Cart>("/users/cart/", {
      withCredentials: true
    });
    cartId = response.data.id;
  }

  onMount(() => {
    if ($isLoggedIn) {
      getCartId();
    }
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
      {#if $isLoggedIn}
        <div
          class="flower-list-item-favorite"
          on:click={() => handleFavoriteChange(flower)}
        >
          {#if flower.favorite}
            <i class="mdi mdi-heart" />
          {:else}
            <i class="mdi mdi-heart-outline" />
          {/if}
        </div>
      {/if}
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
      {#if $isLoggedIn}
        <input
          type="number"
          min="1"
          max="100"
          on:input={e => (amount = +e.currentTarget.value)}
          value="1"
        />
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
    position: relative;
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

  .flower-list-item-favorite {
    background-color: white;
    border: 1px solid grey;
    width: 48px;
    height: 48px;
    position: absolute;
    top: 8px;
    right: 8px;
    cursor: pointer;
  }

  .mdi {
    font-size: 48px;
    line-height: 1;
  }
  .mdi.mdi-heart {
    color: red;
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
