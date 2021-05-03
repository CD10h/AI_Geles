<!-- App.svelte -->
<script>
  import { Router, Link, Route } from "svelte-routing";
  import SearchBar from "./SearchBar.svelte";
  import AddFlower from "./AddFlower.svelte";

  export let url = "";
</script>

<Router {url}>
  <nav>
    <Link to="/add">Add Flower</Link>
    <Link to="/search">Search</Link>
  </nav>
  <div>
    <Route path="/add" component={AddFlower} />
    <Route path="/search" component={SearchBar} />
  </div>
</Router>
