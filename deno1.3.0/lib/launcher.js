/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        Deno.env.set('__OW_' + key.toUpperCase(), value);
      }
    }

    const sourceCode = './ow_bundle.js';
    if (await exists(sourceCode)) {
      const {default: main} = await import(sourceCode);
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
