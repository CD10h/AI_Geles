<script>
  import type { Flower } from "./App.svelte";
  import { onMount } from "svelte";
  import Catalogue from "./Catalogue.svelte";
  import axios from "axios";

  // Variable to hold fetched list
  let flowers: Flower[] = [];

  async function getFlowers() {
    // Download data from server
    const response = await axios.get("/flowers/?favorite=true", {
      withCredentials: true
    });
    flowers = response.data;
  }

  // Run code on component mount (once)
  onMount(() => {
    getFlowers();
  });
</script>

<Catalogue {flowers} />
