<script>
  export let label: string | undefined;
  export let value: string | number;
</script>

{#if label}
  <label>{label}</label>
{/if}
<div class="input-field">
  <input bind:value class="input-component" {...$$restProps} />
</div>

<style>
  .input-field {
    box-shadow: black 0 0 4px 0;
    border-radius: 4px;
    display: flex;
    padding: 6px 12px;
    width: 200px;
  }

  .input-component {
    border: none;
    font-size: 1rem;
    flex-grow: 1;
    margin-left: 8px;
    outline: none;
  }
</style>
