<script context="module">
  // JSON object structure for response from server
  export interface Flower {
    id: number;
    name: string;
    price: number;
    photo?: string;
    description: string;
  }
</script>

<script>
  import { server_url } from "./index.ts";

  import Catalogue from "./Catalogue.svelte";
  import SearchBar from "./SearchBar.svelte";

  // Variable to hold input string
  let query = "";
  // Variable to hold fetched list
  let flowers: Flower[] = [];

  // Dependencies of this block are calculated by Svelte
  // Every time `query` changes, this block of code runs
  $: {
    // Download data from server
    fetch(`${server_url}/flowers/?q=${query /* dependency */}`)
      // Parse as JSON
      .then((response) => response.json())
      // Set `flowers` to the parsed data
      .then((json) => (flowers = json));
  }
</script>

<SearchBar bind:query />
<Catalogue {flowers} />
