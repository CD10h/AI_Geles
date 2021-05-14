<script>
  import { isAxiosError } from "./util";
  import { navigate } from "svelte-routing";
  import { getContext, onMount } from "svelte";
  import axios from "axios";

  import Input from "./Input.svelte";
  import { notificationContextKey } from "./contexts";
  import { AppNotificationType } from "./enums";

  const { addNotification, addLoadingNotification } =
    getContext<AppNotificationContext>(notificationContextKey);

  let flower: Omit<Flower, "id" | "favorite"> = {
    name: "",
    price: 0,
    description: "",
    daysToExpire: 0
  };

  export let id: string;

  let errors: string[] = [];

  async function handleSubmit() {
    try {
      // Creates a loading notification, awaits the passed promise
      // and removes the notification after promise was completed
      await addLoadingNotification(
        "Redaguojama...",
        axios.put(`/flowers/${id}`, flower, {
          withCredentials: true
        })
      );
      addNotification(
        "Gėlė sėkmingai paredaguota",
        AppNotificationType.SUCCESS
      );
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

  onMount(() => {
    axios
      .get(`/flowers/${id}`, { withCredentials: true })
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
