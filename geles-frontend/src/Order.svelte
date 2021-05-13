<script>
  import axios from "axios";
  import { onMount } from "svelte";
  import { navigate } from "svelte-routing";
  import Input from "./Input.svelte";
  import { isAxiosError } from "./util";

  export let cartId: string;

  let cart: Cart = {
    id: 0,
    flowersInCart: []
  };

  let flowers: Flower[] = [];

  let errors: string[] = [];

  let order: OrderAdd = {
    address: "",
    contactPhone: "",
    cartId: +cartId
  };

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

  async function handleSubmit() {
    try {
      await axios.post("/orders/", order, { withCredentials: true });
      navigate("/");
    } catch (e) {
      if (isAxiosError(e)) {
        if (e.response) {
          if (e.response.status === 400) {
            errors = e.response.data.errors.map(
              error => `${error.field} ${error.defaultMessage}`
            );
          } else if (e.response.status === 500) {
            errors = [`Internal server error: ${e.response.data.message}`];
          }
        }
      }
    }
  }

  // Run code on component mount (once)
  onMount(() => {
    getFlowers().then(() => getCartId());
  });
</script>

<h2>Užsakymas</h2>
<table class="table">
  <tr>
    <th>Gėlė</th>
    <th>Kiekis</th>
    <th>Vnt. kaina</th>
    <th>Suma</th>
  </tr>
  {#each cart.flowersInCart as flowerInCart (flowerInCart.id)}
    <tr>
      <td>{flowerInCart.name}</td>
      <td align="right">{flowerInCart.amount}</td>
      <td align="right">{flowerInCart.price} €</td>
      <td align="right">{flowerInCart.sum?.toFixed(2)} €</td>
    </tr>
  {/each}
  <tr>
    <td align="right" colspan="3"><strong>Viso</strong></td>
    <td align="right">
      {cart.flowersInCart
        .reduce((prevValue, flowerInCart) => prevValue + flowerInCart.sum, 0)
        .toFixed(2)} €
    </td>
  </tr>
</table>
<form
  on:submit={e => {
    e.preventDefault();
    handleSubmit();
  }}
>
  <Input
    label="Pristatymo adresas"
    bind:value={order.address}
    type="text"
    name="address"
  /><br />
  <Input
    label="Kontaktinis telefonas"
    bind:value={order.contactPhone}
    type="text"
    name="contactPhone"
  /><br />
  <button>Užsakyti</button>
  {#each errors as error}
    <p class="error">
      <i class="mdi mdi-alert-circle" />
      {error.slice(0, 1).toUpperCase()}{error.slice(1)}
    </p>
  {/each}
</form>

<style>
  table {
    margin-bottom: 20px;
  }

  table,
  th,
  td {
    border: 1px solid black;
    border-collapse: collapse;
  }

  th,
  td {
    padding: 4px 8px;
  }

  .error {
    color: red;
    display: flex;
    align-items: center;
  }

  .error .mdi {
    font-size: 24px;
    margin-right: 8px;
  }
</style>
