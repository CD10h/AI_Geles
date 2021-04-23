<script>
  // JSON object structure for response from server
  interface Flower {
    id: number;
    name: string;
  }

  // Variable to hold input string
  let query = "";
  // Variable to hold fetched list
  let flowers: Flower[] = [];

  // Dependencies of this block are calculated by Svelte
  // Every time `query` changes, this block of code runs
  $: {
    // Download data from server
    fetch(`http://localhost:8080/flowers/?q=${query /* dependency */}`)
      // Parse as JSON
      .then((response) => response.json())
      // Set `flowers` to the parsed data
      .then((json) => (flowers = json));
  }
</script>

<div class="search-bar">
  <span>🔍</span>
  <input bind:value={query} class="search-input" autocomplete="off" />
</div>

<ul>
  <!--
    Map each `flowers` entry as an HTML element `li` 
    When `flowers` updates, so does this block
  -->
  {#each flowers as flower}
    <li>{flower.name}</li>
  {/each}
</ul>

<style>
  .search-bar {
    box-shadow: black 0 0 4px 0;
    border-radius: 4px;
    display: flex;
    padding: 8px 12px;
    width: 300px;
  }

  .search-input {
    border: none;
    font-size: 1rem;
    flex-grow: 1;
    margin-left: 8px;
    outline: none;
  }
</style>
