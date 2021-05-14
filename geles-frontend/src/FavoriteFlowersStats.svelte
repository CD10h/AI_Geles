<script>
  import { onMount } from "svelte";
  import { server_url } from "./index";
  import axios from "axios";
  import { Link } from "svelte-routing";

  interface FavoriteFlowers {
    id: number;
    name: string;
    price: number;
    photo?: string;
    description: string;
    amount: number;
  }

  let favoriteFlowers: FavoriteFlowers[] = [];

  async function getFavoriteFlowers() {
    const response = await axios.get("/flowers/favorite/all");
    favoriteFlowers = response.data;
  }

  onMount(() => {
    getFavoriteFlowers();
  });
</script>

<div class="favoriteflowersstats-list">
  <h2>Vartotojų mėgstamiausios gėlės</h2>
  {#if favoriteFlowers.length > 0}
    <table class="favoriteflowers-table">
      <tr>
        <th colspan="2">Gėlė</th>
        <th>Mėgstamiausia</th>
      </tr>
      {#each favoriteFlowers as flower (flower.id)}
        <tr>
          <div class="imagecontainer">
            {#if flower.photo != null}
              <img
                class="flower-list-item-photo"
                src={`${server_url}/static/${flower.photo}`}
                alt={flower.name}
                width="80"
                height="80"
              />
            {/if}
          </div>
          <td>{flower.name}</td>
          <td>
            {flower.amount}
          </td>
        </tr>
      {/each}
    </table>
  {:else}
    <p>Nėra mėgstamų gėlių!</p>
  {/if}
</div>

<style>
  h2 {
    color: #000000;
    font-size: 24px;
    font-weight: 400;
  }

  table,
  th,
  td {
    border: 2px solid #8ebf42;
    border-collapse: collapse;
  }

  table {
    background-color: #d9d9d9;
    border: 4px solid #8ebf42;
    margin-bottom: 10px;
  }

  th,
  td {
    padding: 4px 8px;
  }

  td {
    text-align: center;
  }

  td.number {
    text-align: right;
  }

  td input {
    background-color: white;
  }

  img {
    margin: 8px;
    /* Weird bug with table cell height */
    margin-bottom: 4px;
  }

  .outsidecart {
    background-color: white;
    margin-right: 5px;
  }

  .savetemplatecontainer {
    margin-top: 20px;
  }

  .templatename {
    text-align: left;
  }
</style>
