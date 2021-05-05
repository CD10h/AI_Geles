<script>
  import type { Flower } from "./App.svelte";
  import axios from "axios";
  import Input from "./Input.svelte";
  import { onMount } from "svelte";

  let flower: Omit<Flower, "id"> = {
    name: "",
    price: 0,
    description: "",
    daysToExpire: 0
  };

  export let id: string;

  async function handleSubmit() {
    await axios.put(`http://localhost:8080/flowers/${id}`, flower, {
      withCredentials: true
    });
  }

  onMount(() => {
    axios
      .get(`http://localhost:8080/flowers/${id}`, { withCredentials: true })
      .then(response => (flower = response.data));
  });
</script>

<h2>Redaguoti gėlę</h2>
<form
  on:submit={e => {
    e.preventDefault();
    handleSubmit();
  }}
>
  <Input
    label="Pavadinimas"
    bind:value={flower.name}
    type="text"
    name="name"
  /><br /><br />
  <Input
    label="Kaina"
    bind:value={flower.price}
    type="number"
    min={0}
    step="0.01"
    name="price"
  /><br /><br />
  <Input
    label="Aprašymas"
    bind:value={flower.description}
    type="text"
    name="description"
  /><br /><br />
  <Input
    label="Galiojimo trukmė(dienomis)"
    bind:value={flower.daysToExpire}
    type="number"
    min={1}
    name="expirydate"
  /><br /><br />
  <button>Redaguoti</button>
</form>
