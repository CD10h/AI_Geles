<script>
  import { onMount } from "svelte";
  import { server_url } from "./index";
  import axios from "axios";
  import { isLoggedIn } from "./isLoggedIn";
  import { mapFlowerToWithFavorite } from "./util/flower";
  import { Link, navigate } from "svelte-routing";

  export let flowerId: number;

  let flower: Flower;

  let cartId = 0;
  let amount = 1;

  async function getFlower() {
    const response = await axios.get(`/flowers/${flowerId}`);
    flower = response.data;
    flower.favorite = false;

    if ($isLoggedIn) {
      const favoriteResponse = await axios.get<number[]>("/flowers/favorite", {
        withCredentials: true
      });
      flower = mapFlowerToWithFavorite(flower, favoriteResponse.data);
    }
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

  async function handleFavoriteChange() {
    await axios.put(
      `/flowers/${flower.id}/favorite`,
      { favorite: !flower.favorite },
      { withCredentials: true }
    );
    if (flower.favorite) {
      flower.favorite = false;
    } else {
      flower.favorite = true;
    }
  }

  async function handleDelete(id: number, name: string) {
    if (window.confirm(`Ar tikrai norite ištrinti gėlę ${name}?`)) {
      await axios.delete(`${server_url}/flowers/${id}`);
      navigate("/");
    }
  }

  onMount(() => {
    getFlower();
    if ($isLoggedIn) {
      getCartId();
    }
  });
</script>

<div class="flowerInfo">
  {#if flower != null}
    <div class="photoandinfo">
      <div class="imagecontainer">
        <img
          class="flower-photo"
          src={`${server_url}/static/flowers/${flower.photo}`}
          alt={flower.name}
          width="400"
          height="400"
        />
        {#if $isLoggedIn}
          <div
            class="flower-list-item-favorite"
            on:click={() => handleFavoriteChange()}
          >
            {#if flower.favorite}
              <i class="mdi mdi-heart" />
            {:else}
              <i class="mdi mdi-heart-outline" />
            {/if}
          </div>
        {/if}
      </div>
      <div class="infocontainer">
        <div class="nameAndPrice">
          <h2 class="name">{flower.name}</h2>
          <div style="flex-grow:1;" />
          <h2 class="price">{flower.price} €</h2>
          <br />
        </div>
        <p class="description">{flower.description}</p>
        <div class="tocart">
          {#if $isLoggedIn}
            <p class="addText">Pridėti į krepšelį:</p>
            <input
              type="number"
              min="1"
              max="100"
              on:input={e => (amount = +e.currentTarget.value)}
              value="1"
              size="9"
            />
            <button on:click={() => handleToCart(flowerId, amount)}>+</button>
            <br />
            <div class="adminoptions">
              <Link to="/update/{flower.id}">Redaguoti</Link>
              <button on:click={() => handleDelete(flower.id, flower.name)}
                >Ištrinti</button
              >
            </div>
          {/if}
        </div>
      </div>
    </div>
  {/if}
</div>

<style>
  .addText {
    display: inline;
  }

  input {
    margin-left: 10px;
  }

  .nameAndPrice {
    display: flex;
    max-width: 400px;
    min-width: 200px;
  }

  .flowerInfo {
    margin-top: 20px;
    margin-left: 20px;
  }

  .photoandinfo {
    display: flex;
    flex-wrap: wrap;
  }

  .infocontainer {
    max-width: 400px;
    min-width: 200px;
  }

  .imagecontainer {
    position: relative;
    margin-right: 20px;
  }

  .tocart {
    margin-top: 40px;
  }

  .mdi {
    font-size: 48px;
    line-height: 1;
  }
  .mdi.mdi-heart {
    color: red;
  }

  .flower-list-item-favorite {
    background-color: white;
    border: 1px solid grey;
    position: absolute;
    top: 8px;
    right: 8px;
    width: 48px;
    height: 48px;
    cursor: pointer;
  }

  .description {
    text-align: justify;
  }

  .adminoptions {
    margin-top: 20px;
  }
</style>
