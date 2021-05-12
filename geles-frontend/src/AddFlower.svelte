<script>
  import { isAxiosError } from "./util";
  import { navigate } from "svelte-routing";
  import axios from "axios";

  import Input from "./Input.svelte";

  let flower: Omit<Flower, "id" | "favorite"> = {
    name: "",
    price: 0,
    description: "",
    daysToExpire: 0,
    photo: ""
  };

  let errors: string[] = [];
  let fileName;
  let files: File[];

  async function upload() {
    const formData = new FormData();
    formData.append("file", files[0]);
    try {
      await axios.post("/file/", formData, {
        headers: { "Content-Type": "multipart/form-data" }
      });
    } catch (e) {
      if (isAxiosError(e)) {
        if (e.response) {
          if (e.response.status === 500) {
            errors = [`Internal server error: ${e.response.data.message}`];
          }
        }
      }
    }
  }

  async function handleSubmit() {
    try {
      if (!files[0].type.match(/image\/.*/)) {
        errors = ["Choose valid image type"];
        return;
      }
      flower.photo = files[0].name;
      const response = await axios.post("/flowers/", flower, {
        withCredentials: true
      });
      await upload();
      navigate("/");
    } catch (e) {
      if (isAxiosError(e)) {
        if (e.response) {
          if (e.response.status === 400) {
            errors = e.response.data.errors.map(
              error => `${error.field} ${error.defaultMessage}`
            );
          } else if (e.response.status === 500) {
            errors = [`Internal server error: ${e.response.data.message}`];
          }
        }
      }
    }
  }
</script>

<h2>Pridėti gėlę</h2>
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
  <label>Nuotrauka</label>
  <br />
  <input
    id="fileUpload"
    type="file"
    multiple={false}
    bind:files
    accept="image/*"
  />
  <br /><br />
  <button>Sukurti</button>
  {#each errors as error}
    <p class="error">
      <i class="mdi mdi-alert-circle" />
      {error.slice(0, 1).toUpperCase()}{error.slice(1)}
    </p>
  {/each}
</form>

<style>
  .error {
    color: red;
    display: flex;
    align-items: center;
  }

  .error .mdi {
    font-size: 24px;
    margin-right: 8px;
  }
</style>
