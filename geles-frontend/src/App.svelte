<script>
  import { onDestroy } from "svelte";
  const targets = ["World", "AI Generated Name", "Evaldas Pakalnickas"];
  let index = 0;

  let datetime = new Date();
  let seconds = 0;

  let ih1: number;
  window.setTimeout(() => {
    datetime = new Date();
    console.log(datetime.getMilliseconds());
    ih1 = window.setInterval(() => {
      datetime = new Date();
      console.log(datetime.getMilliseconds());
    }, 1000);
  }, 1000 - datetime.getMilliseconds());

  const ih2 = window.setInterval(() => {
    seconds++;
  }, 1000);

  const ih3 = window.setInterval(() => {
    index = index >= 2 ? 0 : index + 1;
  }, 10000);

  onDestroy(() => {
    window.clearInterval(ih1);
    window.clearInterval(ih2);
    window.clearInterval(ih3);
  });
</script>

<h1>
  Hello {targets[index]}!
</h1>
<h2>It is currently {datetime.toLocaleString()}.</h2>
<h2>The page has been open for {seconds} seconds.</h2>
<p>
  To learn to develop with Svelte, check out <a
    href="https://svelte.dev/tutorial/basics"
    target="_blank"
    rel="noreferrer noopener">Svelte basics</a
  >.
</p>
