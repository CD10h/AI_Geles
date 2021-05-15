<script>
  import { isLoggedIn } from "./isLoggedIn";
  import { Router, Link, Route } from "svelte-routing";
  import axios from "axios";

  import AddFlower from "./AddFlower.svelte";
  import Cart from "./Cart.svelte";
  import FavoriteFlowers from "./FavoriteFlowers.svelte";
  import Home from "./Home.svelte";
  import Login from "./auth/Login.svelte";
  import Register from "./auth/Register.svelte";
  import Search from "./Search.svelte";
  import UpdateFlower from "./UpdateFlower.svelte";
  import Order from "./Order.svelte";
  import ManageOrders from "./ManageOrders.svelte"
  import NotificationContext from "./NotificationContext.svelte";
  import Flower from "./Flower.svelte";
  import FavoriteFlowersStats from "./FavoriteFlowersStats.svelte";
  import EditOrder from "./EditOrder.svelte"

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

<NotificationContext>
  <Router {url}>
    <nav class="navbar">
      <Link class="button" to="/">Pagrindinis</Link>
      <Link class="button" to="/add">Pridėti gėlę</Link>
      <Link class="button" to="/search">Paieška</Link>
      {#if !$isLoggedIn}
        <Link class="button" to="/login">Prisijungti</Link>
        <Link class="button" to="/register">Užsiregistruoti</Link>
      {:else}
        <Link class="button" to="/flowers/favorite">Mėgstamiausios gėlės</Link>
        <Link class="button" to="/cart">Krepšelis</Link>
        <Link class="button" to="/favorite/stats">Mėgstamiausių statistika</Link>
        <Link class="button" to="/orders">Užsakymai</Link>
        <button class="button" on:click={handleLogout}>Atsijungti</button>
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
      <Route path="/cart" component={Cart} />
      <Route path="/order/:cartId" component={Order} />
      <Route path="/flower/:flowerId" component={Flower} />
      <Route path="/favorite/stats" component={FavoriteFlowersStats} />
      <Route path="/orders" component={ManageOrders} />
      <Route path="/editOrder/:id" component={EditOrder}/>
    </div>
  </Router>
</NotificationContext>
