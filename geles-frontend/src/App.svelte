<script context="module">
  // JSON object structure for response from server
  export interface Flower {
    id: number;
    name: string;
    price: number;
    photo?: string;
    description: string;
    daysToExpire: number;
    favorite: boolean;
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
  import FavoriteFlowers from "./FavoriteFlowers.svelte";
  import { isLoggedIn } from "./isLoggedIn";

  export let url = "";

  function onLogin() {
    isLoggedIn.set(true);
  }

  async function handleLogout() {
    await axios.post("/auth/logout", null, {
      withCredentials: true
    });
    isLoggedIn.set(false);
  }
</script>

<Router {url}>
  <nav>
    <Link to="/">Pagrindinis</Link>
    <Link to="/add">Pridėti gėlę</Link>
    <Link to="/search">Paieška</Link>
    {#if !$isLoggedIn}
      <Link to="/login">Prisijungti</Link>
      <Link to="/register">Užsiregistruoti</Link>
    {:else}
      <Link to="/flowers/favorite">Mėgstamiausios gėlės</Link>
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
    <Route path="/flowers/favorite" component={FavoriteFlowers} />
  </div>
</Router>
