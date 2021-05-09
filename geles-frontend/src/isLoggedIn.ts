import { writable } from "svelte/store";

export const isLoggedIn = writable(
  !!document.cookie.split("; ").find(cookie => cookie.startsWith("auth"))
);
