<script>
  import { navigate } from "svelte-routing";
  import Input from "../Input.svelte";
  import { isAxiosError } from "../util";
  import axios from "axios";

  export let onLogin: () => void;

  let loginFields = {
    username: "",
    password: ""
  };

  let error: string = "";

  async function handleSubmit() {
    try {
      await axios.post("/auth/login/", loginFields, {
        withCredentials: true
      });
      onLogin();
      navigate("/");
    } catch (e) {
      if (isAxiosError(e)) {
        if (e.response) {
          if (e.response.status === 400) {
            error = "Neteisingas vartotojo vardas ar slaptažodis";
          }
        }
      }
    }
  }
</script>

<div class="pagecontent">
  <h2>Prisijungti</h2>
  <form
    on:submit={e => {
      e.preventDefault();
      handleSubmit();
    }}
  >
    <Input
      label="Vartotojo vardas"
      bind:value={loginFields.username}
      name="username"
      type="text"
      autocomplete="nickname"
    /><br /><br />
    <Input
      label="Slaptažodis"
      bind:value={loginFields.password}
      name="password"
      type="password"
      autocomplete="current-password"
    /><br /><br />
    <button class="button">Prisijungti</button>
    {#if error}
      <p class="error">
        <i class="mdi mdi-alert-circle" />
        {error.slice(0, 1).toUpperCase()}{error.slice(1)}
      </p>
    {/if}
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

  .button {
    margin-left: 0;
  }
</style>
