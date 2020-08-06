const output = await Deno.open('/dev/fd/3', {read: false, write: true});

for await (const line of Deno.iter(Deno.stdin)) {
  let response = {error: "couldn't execute your function"};

  try {
    const input = JSON.parse(new TextDecoder().decode(line).trim());
    let payload = {};
    for (const [key, value] of Object.entries(input)) {
      if (key === 'value') {
        // extract the payload
        payload = value;
      } else {
        // set the env variables
        Deno.env.set(key, '__OW_' + value.toUpperCase());
      }
    }

    const {default: main} = await import('./ow_bundle.js');
    response = await main(payload);
  } catch (error) {
    Deno.stderr.write(new TextEncoder().encode(error.message));
    response = {
      error: error.message
    };
  }

  await Deno.write(
    output.rid,
    new TextEncoder().encode(JSON.stringify(response) + '\n')
  );
}

Deno.close(output.rid);
Deno.close(Deno.stderr.rid);
Deno.close(Deno.stdout.rid);
