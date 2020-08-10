import {readLines, exists} from '../deps.js';
const output = await Deno.open('/dev/fd/3', {read: false, write: true});

for await (const line of readLines(Deno.stdin)) {
  let response = {error: "couldn't execute your function"};

  try {
    const input = JSON.parse(line);
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

    if (await exists('./ow_bundle.js')) {
      const {default: main} = await import('./ow_bundle.js');
      response = await main(payload);
      if (Object.prototype.toString.call(response) !== '[object Object]') {
        response = {
          error: 'response returned by the function is not an object'
        };
        console.error(response);
      }
    } else {
      response = {
        error:
          "couldn't find the bundled file. There might be an error during bundling."
      };
    }
  } catch (error) {
    console.error(error);
    response = {
      error: error.message
    };
  }

  await Deno.writeAll(
    output,
    new TextEncoder().encode(JSON.stringify(response) + '\n')
  );
}

Deno.close(output.rid);
Deno.close(Deno.stderr.rid);
Deno.close(Deno.stdout.rid);
