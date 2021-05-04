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
  import Register from "./Register.svelte";
  import Search from "./Search.svelte";
  import UpdateFlower from "./UpdateFlower.svelte";

  export let url = "";
</script>

<Router {url}>
  <nav>
    <Link to="/">Home</Link>
    <Link to="/add">Add flower</Link>
    <Link to="/search">Search</Link>
    <Link to="/register">Užsiregistruoti</Link>
  </nav>
  <div>
    <Route path="/" component={Home} />
    <Route path="/add" component={AddFlower} />
    <Route path="/search" component={Search} />
    <Route path="/update/:id" component={UpdateFlower} />
    <Route path="/register" component={Register} />
  </div>
</Router>
