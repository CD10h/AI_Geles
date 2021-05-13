<script>
  import { onMount } from "svelte";
  import { server_url } from "./index";
  import axios from "axios";
  import { Link } from "svelte-routing";

  interface CartTemplate {
    id: number;
    name: string;
    flowersInCart: FlowerInCart[];
  }

  let cart: Cart = {
    id: 0,
    flowersInCart: []
  };

  let cartTemplate: CartTemplate = {
    id: 0,
    name: "",
    flowersInCart: []
  };

  let flowers: Flower[] = [];
  let cartTemplates: CartTemplate[] = [];

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
        sum: flower.price * flowerInCart.amount,
        photo: flower.photo
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
    cart.flowersInCart = mapFlowersInCart(response.data.flowersInCart);
  }

  async function getCartTemplates() {
    const response = await axios.get(`${server_url}/templates/`, {
      withCredentials: true
    });
    cartTemplates = response.data.map((cartTemplate: CartTemplate) => {
      cartTemplate.flowersInCart = mapFlowersInCart(cartTemplate.flowersInCart);
      return cartTemplate;
    });
  }

  async function handleSave() {
    if (cartTemplate.name.length > 0) {
      let template = {
        name: cartTemplate.name,
        flowersInCart: cart.flowersInCart
      };
      const response = await axios.post(`${server_url}/templates/`, template, {
        withCredentials: true
      });
      getCartTemplates();
    } else {
      //notification?
    }
  }

  async function handleLoadTemplate(template: CartTemplate) {
    let flowersInTemplate =
      cartTemplates[cartTemplates.indexOf(template)].flowersInCart;

    let c = {
      id: cart.id,
      flowersInCart: flowersInTemplate
    };

    const response = await axios.put(`${server_url}/carts/${c.id}`, c);
    cart.flowersInCart = mapFlowersInCart(response.data.flowersInCart);
  }

  async function handleDeleteTemplate(template: CartTemplate) {
    await axios.delete(`${server_url}/templates/${template.id}`);
    getCartTemplates();
  }

  function calculateSum(template: CartTemplate) {
    let sum = 0;

    for (const flowerInTemplate of template.flowersInCart) {
      sum += flowerInTemplate.amount * flowerInTemplate.price;
    }
    return sum;
  }

  // Run code on component mount (once)
  onMount(() => {
    getFlowers()
      .then(() => getCartId())
      .then(() => getCartTemplates());
  });
</script>

<div class="flowerincart-list">
  <div class="flowercart">
    <h2>Gėlių krepšelis</h2>
    {#if cart.flowersInCart.length > 0}
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
              {#if flowerInCart.photo != null}
                <img
                  class="flower-list-item-photo"
                  src={`${server_url}/static/${flowerInCart.photo}`}
                  alt={flowerInCart.name}
                  width="80"
                  height="80"
                />
              {/if}
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
            <td class="number">{flowerInCart.price} €</td>
            <td class="number">{flowerInCart.sum?.toFixed(2)}€</td>
            <td>
              <button on:click={() => handleDelete(flowerInCart)}
                >Pašalinti</button
              >
            </td>
          </tr>
        {/each}
      </table>
    {:else}
      <p>Krepšelis tuščias!</p>
      <br />
    {/if}
    <button class="savebutton" on:click={() => handleUpdate()}
      >Išsaugoti pakeitimus</button
    >
    <Link to={`/order/${cart.id}`}>Užsakyti</Link><br />
    <div class="savetemplatecontainer">
      <input
        class="outsidecart"
        type="string"
        bind:value={cartTemplate.name}
        placeholder="Šablono pavadinimas"
      />
      <button class="savetemplatebutton" on:click={() => handleSave()}
        >Išsaugoti krepšelį ateičiai</button
      >
    </div>
  </div>
  <div class="carttemplate-list">
    <h2>Išsaugoti krepšeliai</h2>
    {#if cartTemplates.length > 0}
      <table class="template-table">
        <tr>
          <th>Krepšelis</th>
          <th>Gėlės</th>
          <th>Kiekis</th>
          <th>Suma</th>
          <th colspan="2" />
        </tr>
        {#each cartTemplates as template (template.id)}
          {#each template.flowersInCart as flowerInTemplate, i (flowerInTemplate.id)}
            <tr>
              {#if i === 0}
                <td
                  class="templatename"
                  rowspan={template.flowersInCart.length}
                >
                  {template.name}
                </td>
              {/if}
              <td>
                {flowerInTemplate.name}
              </td>
              <td>
                {flowerInTemplate.amount}
              </td>
              {#if i === 0}
                <td rowspan={template.flowersInCart.length}
                  >{calculateSum(template).toFixed(2)} €</td
                >
                <td rowspan={template.flowersInCart.length}>
                  <button on:click={() => handleLoadTemplate(template)}
                    >Naudoti</button
                  >
                  <button on:click={() => handleDeleteTemplate(template)}
                    >Pašalinti</button
                  >
                </td>
              {/if}
            </tr>
          {/each}
        {/each}
      </table>
    {:else}
      <p>Nėra išsaugotų krepšelių!</p>
    {/if}
  </div>
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
    border-top: 2px solid #8ebf42;
  }

  td.number {
    text-align: right;
  }

  img {
    margin: 10px auto 10px;
    padding-left: 5px;
    padding-right: 5px;
    display: block;
  }

  .imagecontainer {
    border-bottom: 2px solid #8ebf42;
    border-top: 2px solid #8ebf42;
    border-right: 2px solid #8ebf42;
    border-left: 2px solid #8ebf42;
  }

  .outsidecart {
    background-color: white;
    /* position: relative;
    top: 30px; */
  }

  .savebutton {
    /* position: relative;
    top: 20px; */
  }

  .savetemplatebutton {
    margin-left: 30px;
  }

  h2 {
    color: #000000;
    font-size: 24px;
    font-weight: 400;
  }

  .amount-list {
    list-style-type: none;
    margin: 0;
    padding: 0;
  }

  .templatename {
    text-align: left;
  }

  .flowerincart-list {
    margin-left: 40px;
  }

  .flowercart {
    margin-bottom: 100px;
  }

  table th,
  table tr,
  table td {
    border-bottom: 2px solid #8ebf42;
    border-top: 2px solid #8ebf42;
    border-right: 2px solid #8ebf42;
    border-left: 2px solid #8ebf42;
    border-collapse: collapse;
  }

  .savetemplatecontainer {
    margin-top: 20px;
  }
</style>
