<script context="module">
  // JSON object structure for response from server
  export interface Flower {
    id: number;
    name: string;
    price: number;
    photo?: string;
    description: string;
    daysToExpire: number;
  }
  export interface FlowerInCart {
    id: number;
    amount: number;
    flowerId: number;
    cartId: number;
  }
</script>

<script>
  import { Router, Link, Route } from "svelte-routing";

  import AddFlower from "./AddFlower.svelte";
  import Home from "./Home.svelte";
  import Login from "./auth/Login.svelte";
  import Register from "./auth/Register.svelte";
  import Search from "./Search.svelte";
  import UpdateFlower from "./UpdateFlower.svelte";
  import Cart from "./Cart.svelte";
  import axios from "axios";
  import { onMount } from "svelte";
  import { server_url } from "./index";

  export let url = "";

  interface Cart {
    id: number;
    flowersInCart: [];
  }

  let cart = {
    id: 0,
    flowersInCart: []
  };

  let isLoggedIn = !!document.cookie
    .split("; ")
    .find(cookie => cookie.startsWith("auth"));

  function onLogin() {
    isLoggedIn = true;
  }

  async function handleLogout() {
    await axios.post("http://localhost:8080/auth/logout", null, {
      withCredentials: true
    });
    isLoggedIn = false;
  }

  async function getCartId() {
    const response = await axios.get<Cart>(`${server_url}/users/cart/`, {
      withCredentials: true
    });
    cart = response.data;
  }

  let flowers: Flower[] = [];

  async function getFlowers() {
    // Download data from server
    const response = await axios.get(`${server_url}/flowers/`);
    flowers = response.data;
  }

  onMount(() => {
    getCartId();
    getFlowers();
  });
</script>

<Router {url}>
  <nav>
    <Link to="/">Home</Link>
    <Link to="/add">Add flower</Link>
    <Link to="/search">Search</Link>
    {#if !isLoggedIn}
      <Link to="/login">Prisijungti</Link>
      <Link to="/register">Užsiregistruoti</Link>
    {/if}
    {#if isLoggedIn}
      <Link to="/cart">Krepšelis</Link>
      <button on:click={handleLogout}>Atsijungti</button>
    {/if}
  </nav>
  <div>
    <Route path="/" component={Home} />
    <Route path="/add" component={AddFlower} />
    <Route path="/search" component={Search} />
    <Route path="/update/:id" component={UpdateFlower} />
    <Route path="/login" component={Login} {onLogin} />
    <Route path="/register" component={Register} />
    <Route path="/cart" component={Cart} {flowers} />
  </div>
</Router>
