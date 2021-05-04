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
  import { server_url } from "./index.ts";

  import AddFlower from "./AddFlower.svelte";
  import Catalogue from "./Catalogue.svelte";
  import UpdateFlower from "./UpdateFlower.svelte";
  import Search from "./Search.svelte";
  import { onMount } from "svelte";

  export let url = "";

  // Variable to hold fetched list
  let flowers: Flower[] = [];

  // Run code on component mount (once)
  onMount(() => {
    // Download data from server
    fetch(`${server_url}/flowers/`)
      // Parse as JSON
      .then((response) => response.json())
      // Set `flowers` to the parsed data
      .then((json) => (flowers = json));
  });
</script>

<Router {url}>
  <nav>
    <Link to="/">Home</Link>
    <Link to="/add">Add flower</Link>
    <Link to="/search">Search</Link>
  </nav>
  <div>
    <Route path="/" component={Catalogue} {flowers} />
    <Route path="/add" component={AddFlower} />
    <Route path="/search" component={Search} />
    <Route path="/update/:id" component={UpdateFlower} />
  </div>
</Router>
