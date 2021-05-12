<script>
  import { onMount } from "svelte";
  import { server_url } from "./index";
  import axios from "axios";

  interface Cart {
    id: number;
    flowersInCart: FlowerInCart[];
  }

  interface CartTemplate {
    name: string;
    flowersInCart: FlowerInCart[];
  }

  let cart: Cart = {
    id: 0,
    flowersInCart: []
  };

  let cartTemplate: CartTemplate = {
    name: "",
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
      cart.flowersInCart.splice(index, 1);
      cart.flowersInCart = cart.flowersInCart;
    }
  }

  async function handleUpdate() {
    const response = await axios.put(`${server_url}/carts/${cart.id}`, cart);
    cart.flowersInCart = mapFlowersInCart(response.data);
  }

  async function getCartTemplates() {
    const response = await axios.get(`${server_url}/templates/`, {
      withCredentials: true
    });
    cartTemplate = response.data;
  }

  async function handleSave() {
    let cartTemplate = {
      name: "test template",
      flowersInCart: cart.flowersInCart
    };
    const response = await axios.post(
      `${server_url}/templates/`,
      cartTemplate,
      {
        withCredentials: true
      }
    );
    // cart.flowersInCart = mapFlowersInCart(response.data);
  }

  // Run code on component mount (once)
  onMount(() => {
    getFlowers().then(() => getCartId());
    getCartTemplates();
  });
</script>

<div class="flowerincart-list">
  <h2>Gėlių krepšelis</h2>
  <table>
    <tr>
      <th colspan="2">Gėlė</th>
      <th>Kiekis</th>
      <th>Vnt. kaina</th>
      <th>Suma</th>
      <th />
    </tr>
    {#each cart.flowersInCart as flowerInCart (flowerInCart.id)}
      <tr class="flowerincart">
        <div class="imagecontainer">
          <img
            class="flower-list-item-photo"
            src={`${server_url}/static/${
              flowers[flowerInCart.flowerId - 1].photo
            }`}
            alt={flowerInCart.name}
            width="80"
            height="80"
          />
        </div>
        <td>{flowerInCart.name}</td>
        <td>
          <input
            type="number"
            bind:value={flowerInCart.amount}
            min="1"
            max="100"
            size="5"
            on:input={e => {
              updateSum(flowerInCart, e.currentTarget.value);
            }}
          />
        </td>
        <td class="number">{flowerInCart.price}€</td>
        <td class="number">{flowerInCart.sum?.toFixed(2)}€</td>
        <td>
          <button on:click={() => handleDelete(flowerInCart)}>Pašalinti</button>
        </td>
      </tr>
    {/each}
  </table>
  <button class="savebutton" on:click={() => handleUpdate()}
    >Išsaugoti pakeitimus</button
  >
  <!-- <input class="outsidecart" type="string" bind:value={cartTemplate.name} /> -->
  <button class="savetemplatebutton" on:click={() => handleSave()}
    >Išsaugoti krepšelį ateičiai</button
  >
</div>

<style>
  .flowerincart {
    height: 90px;
  }

  table {
    border-style: ridge;
    border-width: 4px;
    border-color: #8ebf42;
    background-color: #d9d9d9;
    left: 40px;
    position: relative;
  }

  input {
    background-color: #d9d9d9;
  }

  tr {
    border-bottom: 2px solid #8ebf42;
    border-top: 2px solid #8ebf42;
  }

  td {
    padding: 10px;
    text-align: center;
    border-bottom: 2px solid #8ebf42;
  }

  td.number {
    text-align: right;
  }

  img {
    margin: 10px auto 10px;
    padding-left: 15px;
    display: block;
  }

  .imagecontainer {
    border-bottom: 2px solid #8ebf42;
  }

  .outsidecart {
    background-color: white;
  }

  .savebutton {
    position: relative;
    top: 20px;
    left: 40px;
  }

  form {
    position: relative;
    top: 40px;
    left: 40px;
  }

  .savetemplatebutton {
    position: relative;
    left: 40px;
    top: 20px;
  }

  h2 {
    position: relative;
    left: 40px;
    color: #000000;
    font-size: 24px;
    font-weight: 400;
  }
</style>
