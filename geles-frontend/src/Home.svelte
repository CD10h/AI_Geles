<script>
  import type { Flower } from "./App.svelte";
  import { onMount } from "svelte";
  import Catalogue from "./Catalogue.svelte";
  import { server_url } from "./index";
  import axios from "axios";

  // Variable to hold fetched list
  let flowers: Flower[] = [];

  async function getFlowers() {
    // Download data from server
    const response = await axios.get(`${server_url}/flowers/`);
    flowers = response.data;
  }

  // Run code on component mount (once)
  onMount(() => {
    getFlowers();
  });

  function onChange() {
    getFlowers();
  }
</script>

<Catalogue {flowers} {onChange} owner />
