<script>
  import type { Flower } from "./App.svelte";
  import { onMount } from "svelte";
  import axios from "axios";

  import Catalogue from "./Catalogue.svelte";

  // Variable to hold fetched list
  let flowers: Flower[] = [];

  async function getFlowers() {
    // Download data from server
    const response = await axios.get("/flowers/", {
      withCredentials: true
    });
    flowers = response.data;
  }

  async function onFavoriteChange(flower: Flower) {
    const index = flowers.findIndex(_flower => _flower.id === flower.id);
    if (index === -1) {
      return;
    }
    flowers = [...flowers.slice(0, index), flower, ...flowers.slice(index + 1)];
  }

  // Run code on component mount (once)
  onMount(() => {
    getFlowers();
  });

  function onChange() {
    getFlowers();
  }
</script>

<Catalogue {flowers} {onChange} {onFavoriteChange} owner />
