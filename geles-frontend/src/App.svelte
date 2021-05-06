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
</script>

<script>
  import { Router, Link, Route } from "svelte-routing";

  import AddFlower from "./AddFlower.svelte";
  import Home from "./Home.svelte";
  import Login from "./auth/Login.svelte";
  import Register from "./auth/Register.svelte";
  import Search from "./Search.svelte";
  import UpdateFlower from "./UpdateFlower.svelte";
  import axios from "axios";

  export let url = "";

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
</script>

<Router {url}>
  <nav>
    <Link to="/">Pagrindinis</Link>
    <Link to="/add">Pridėti gėlę</Link>
    <Link to="/search">Paieška</Link>
    {#if !isLoggedIn}
      <Link to="/login">Prisijungti</Link>
      <Link to="/register">Užsiregistruoti</Link>
    {/if}
    {#if isLoggedIn}
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
  </div>
</Router>
