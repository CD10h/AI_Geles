<script>
  import axios from "axios";
  import { navigate } from "svelte-routing";
  import Input from "../Input.svelte";
  import { isAxiosError } from "../util";

  let registerFields = {
    username: "",
    password: ""
  };

  let errors: string[] = [];

  async function handleSubmit() {
    try {
      await axios.post("/users/", registerFields);
      navigate("/");
    } catch (e) {
      if (isAxiosError(e)) {
        if (e.response) {
          if (e.response.status === 400) {
            errors = e.response.data.errors.map(
              error => `${error.field} ${error.defaultMessage}`
            );
          } else if (e.response.status === 409) {
            errors = ["Username already taken"];
          }
        }
      }
    }
  }
</script>

<div class="pagecontent">
  <h2>Užsiregistruoti</h2>
  <form
    on:submit={e => {
      e.preventDefault();
      handleSubmit();
    }}
  >
    <Input
      label="Vartotojo vardas"
      bind:value={registerFields.username}
      name="username"
      type="text"
      autocomplete="nickname"
    /><br /><br />
    <Input
      label="Slaptažodis"
      bind:value={registerFields.password}
      name="password"
      type="password"
      autocomplete="new-password"
    /><br /><br />
    <button>Užsiregistruoti</button>
    {#each errors as error}
      <p class="error">
        <i class="mdi mdi-alert-circle" />
        {error.slice(0, 1).toUpperCase()}{error.slice(1)}
      </p>
    {/each}
  </form>
</div>

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
