import axios from "axios";
import "svelte";

import App from "./App.svelte";

export const server_url = "http://localhost:8080";
axios.defaults.baseURL = server_url;

var app = new App({
  target: document.body
});

export default app;

// Hot Module Replacement (HMR) - Remove this snippet to remove HMR.
// Learn more: https://www.snowpack.dev/concepts/hot-module-replacement
if (import.meta.hot) {
  import.meta.hot.accept();
  import.meta.hot.dispose(() => {
    app.$destroy();
  });
}
