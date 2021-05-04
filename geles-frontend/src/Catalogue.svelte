<script>
  import { server_url } from "./index.ts";
  import { Router, Link, Route } from "svelte-routing";

  import type { Flower } from "./App.svelte";
  import UpdateFlower from "./UpdateFlower.svelte";

  // Variable to hold fetched list
  export let flowers: Flower[];
</script>

<div class="flower-list">
  <!--
    Map each `flowers` entry as an HTML element `li` 
    When `flowers` updates, so does this block
  -->
  {#each flowers as flower (flower.id)}
    <div class="flower-list-item">
      <img
        class="flower-list-item-photo"
        src={`${server_url}/static/${flower.photo}`}
        alt={flower.name}
      />
      <div class="flower-list-item-name">{flower.name}</div>
      <div class="flower-list-item-price">{flower.price} €</div>
      <p class="flower-list-item-description">
        {flower.description}
      </p>
      <Link to="/update/{flower.id}">Redaguoti</Link>
    </div>
  {/each}
</div>

<style>
  .flower-list {
    display: flex;
    flex-wrap: wrap;
    margin: 0 -16px;
    padding: 0 16px;
  }

  .flower-list-item {
    box-shadow: black 0 0 4px;
    margin: 16px;
    padding: 8px;
    min-width: 200px;
    max-width: 300px;
    flex-basis: calc(25% - 48px);
  }

  .flower-list-item-name {
    font-size: 24px;
  }

  .flower-list-item-photo {
    width: 100%;
  }

  .flower-list-item-price {
  }

  .flower-list-item-description {
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 4;
    -webkit-box-orient: vertical;
    /* text-align: justify; */
  }

  @media (max-width: 1024px) {
    .flower-list-item {
      flex-basis: calc(33.3333333% - 48px);
    }
  }

  @media (max-width: 776px) {
    .flower-list-item {
      flex-basis: calc(50% - 48px);
    }
  }

  @media (max-width: 528px) {
    .flower-list-item {
      flex-basis: calc(100% - 48px);
    }
  }
</style>
