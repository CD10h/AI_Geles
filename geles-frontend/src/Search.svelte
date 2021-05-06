<script>
  import type { Flower } from "./App.svelte";
  import { server_url } from "./index";

  import Catalogue from "./Catalogue.svelte";
  import SearchBar from "./SearchBar.svelte";
  import axios from "axios";

  // Variable to hold input string
  let query = "";
  // Variable to hold fetched list
  let flowers: Flower[] = [];

  async function handleSearch() {
    const response = await axios.get<Flower[]>(
      `${server_url}/flowers/?q=${query /* dependency */}`
    );
    flowers = response.data;
  }

  // Dependencies of this block are calculated by Svelte
  // Every time `query` changes, this block of code runs
  $: {
    // Download data from server
    if (!query) {
      flowers = [];
    } else {
      handleSearch();
    }
  }
</script>

<SearchBar bind:query />
<Catalogue {flowers} />
