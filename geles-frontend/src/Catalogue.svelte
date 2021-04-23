<script>
  import type { Flower } from "./App.svelte";

  // Variable to hold fetched list
  export let flowers: Flower[];
</script>

<ul>
  <!--
    Map each `flowers` entry as an HTML element `li` 
    When `flowers` updates, so does this block
  -->
  {#each flowers as flower}
    <li>{flower.name}</li>
  {/each}
</ul>
