<script>
  import axios from "axios";
  import { onMount } from "svelte";

  import Catalogue from "./Catalogue.svelte";
  import SearchBar from "./SearchBar.svelte";
  import { mapFlowersToWithFavorite } from "./util/flower";

  interface Filter {
    sort: string;
    sortType: string;
    filters: {
      name: string;
      value: string | number;
    }[];
  }

  let filter: Filter = {
    sort: "price",
    sortType: "asc",
    filters: []
  };

  // Variable to hold input string
  let query = "";
  // Variable to hold fetched list
  let flowers: Flower[] = [];
  let favoriteFlowers: number[];

  // Dependencies of this block are calculated by Svelte
  // Every time `query` changes, this block of code runs
  $: {
    let minPrice = filter.filters.findIndex(
      filter => filter.name === "minPrice"
    );
    let maxPrice = filter.filters.findIndex(
      filter => filter.name === "maxPrice"
    );

    if (minPrice != -1 && maxPrice != -1) {
      if (+filter.filters[minPrice].value <= +filter.filters[maxPrice].value) {
        axios
          .post<Omit<Flower, "favorite">[]>(
            `/flowers/filter/?q=${query}`,
            filter
          )
          .then(response => {
            flowers = mapFlowersToWithFavorite(response.data, favoriteFlowers);
          });
      }
    } else {
      axios.post(`/flowers/filter/?q=${query}`, filter).then(response => {
        flowers = mapFlowersToWithFavorite(response.data, favoriteFlowers);
      });
    }
  }

  interface SortableField {
    field: string;
    label: string;
  }

  const sortableFields: SortableField[] = [
    { field: "price", label: "Kaina" },
    { field: "name", label: "Pavadinimas" }
  ];

  function handleFilter(name: string, value: string | number) {
    const index = filter.filters.findIndex(filter => filter.name === name);
    if (index > -1) {
      if (!value) {
        filter.filters = [
          ...filter.filters.slice(0, index),
          ...filter.filters.slice(index + 1)
        ];
      } else {
        filter.filters[index].value = value;
      }
    } else {
      filter.filters = [...filter.filters, { name, value }];
    }
  }

  async function onFavoriteChange(flower: Flower) {
    const index = flowers.findIndex(_flower => _flower.id === flower.id);
    if (index === -1) {
      return;
    }
    const favoriteResponse = await axios.get<number[]>("/flowers/favorite", {
      withCredentials: true
    });
    flowers = [
      ...flowers.slice(0, index),
      { ...flower, favorite: favoriteResponse.data.includes(flower.id) },
      ...flowers.slice(index + 1)
    ];
  }

  onMount(() => {
    axios
      .get<number[]>("/flowers/favorite", {
        withCredentials: true
      })
      .then(response => {
        favoriteFlowers = response.data;
      });
  });
</script>

<SearchBar bind:query />

<div class="sort">
  <h3>Rikiavimas</h3>
  <select bind:value={filter.sort}>
    {#each sortableFields as field}
      <option value={field.field}>{field.label}</option>
    {/each}
  </select>
  <select bind:value={filter.sortType}>
    <option value="asc">Didėjimo tvarka</option>
    <option value="desc">Mažėjimo tvarka</option>
  </select><br /><br />
  <label>Kaina </label>
  <input
    on:input={e => handleFilter("minPrice", e.currentTarget.value)}
    class="priceInput"
    type="number"
    min={0}
    placeholder="Nuo"
    step={0.1}
  />
  <input
    on:input={e => handleFilter("maxPrice", e.currentTarget.value)}
    class="priceInput"
    type="number"
    min={0}
    placeholder="Iki"
    step={0.1}
  />
  <br />
</div>

<Catalogue {flowers} {onFavoriteChange} />

<!-- <div class="filter">
  <input type="range" id="volume" name="volume" min="0" max="11" />
</div> -->
<style>
  .priceInput {
    width: 60px;
  }
</style>
