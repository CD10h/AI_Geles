<script>
  import { onMount } from "svelte";
  import { server_url } from "./index";
  import axios from "axios";
  import { Link } from "svelte-routing";
  import { create_in_transition } from "svelte/internal";

  let cart: Cart = {
    id: 0,
    flowersInCart: []
  };

  let flowers: Flower[] = [];

  async function getCartId() {
    const response = await axios.get<Cart>("/users/cart/", {
      withCredentials: true
    });
    cart = response.data;
    getFlowersInCart();
  }

  async function getFlowers() {
    // Download data from server
    const response = await axios.get("/flowers/");
    flowers = response.data;
  }

  async function getFlowersInCart() {
    // Download data from server
    const response = await axios.get(`/carts/flowers/${cart.id}`);
    cart.flowersInCart = mapFlowersInCart(response.data);
  }

  function mapFlowersInCart(data: any) {
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
      cart.flowersInCart = [
        ...cart.flowersInCart.slice(0, index),
        ...cart.flowersInCart.slice(index + 1)
      ];
    }
  }

  async function handleUpdate() {
    const response = await axios.put(`${server_url}/carts/${cart.id}`, cart);
    cart.flowersInCart = mapFlowersInCart(response.data);
  }

  // Run code on component mount (once)
  onMount(() => {
    getFlowers().then(() => getCartId());
  });
</script>

<div class="flowerincart-list">
  {#if cart.flowersInCart.length > 0}
    <table>
      <tr>
        <th>Gėlė</th>
        <th>Kiekis</th>
        <th>Vnt. kaina</th>
        <th>Suma</th>
      </tr>
      {#each cart.flowersInCart as flowerInCart (flowerInCart.id)}
        <tr>
          <td>{flowerInCart.name}</td>
          <td
            ><input
              type="number"
              bind:value={flowerInCart.amount}
              min="1"
              max="100"
              size="5"
              on:input={e => updateSum(flowerInCart, e.currentTarget.value)}
            /></td
          >
          <td>{flowerInCart.price}</td>
          <td>{flowerInCart.sum?.toFixed(2)}</td>
          <button on:click={() => handleDelete(flowerInCart)}>Pašalinti</button>
        </tr>
      {/each}
    </table>
    <Link to={`/order/${cart.id}`}>Užsakyti</Link>
  {:else}
    <p>Krepšelis tuščias!</p>
  {/if}
  <button on:click={() => handleUpdate()}>Išsaugoti pakeitimus</button>
</div>
