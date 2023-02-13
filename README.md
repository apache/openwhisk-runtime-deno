<!--
#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
-->

# Apache OpenWhisk Runtime for Deno

[![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

This repository contains sources files needed to build the Deno
runtimes for Apache OpenWhisk. The build system will produce a Docker
image for each runtime version. These images are used in the platform
to execute Deno actions.

Try it out using Docker image:

```sh
wsk action create hello main.ts --docker=openwhisk/action-deno-1.3.0
```

The content of the `main.ts` is shown below.
```ts
export default (args: any) => {
  return {
    message: `Hello, ${args.name || 'World'}!`
  }
}
```
For the return result, not only support `dictionary` but also support `array`

So a very simple `hello array` function would be:

```ts
export default (args: any) => {
   return ["a", "b"]
}
```

And support array result for sequence action as well, the first action's array result can be used as next action's input parameter.

So the function can be:

```ts
func main(args: Any) -> Any {
    return args
}
```
When invokes above action, we can pass an array object as the input parameter.

## Development

A Dockerfile for each runtime image is defined in its respective
runtime version directory. Modify this file if you need to add extra
dependencies to a runtime version.

### Build

- Run the `distDocker` command to generate local Docker images for the different runtime versions.

```
./gradlew distDocker
```

### Test

1. Build the local Docker images for the Deno runtime (see the instructions above).

2. Install project dependencies from the top-level Apache OpenWhisk
[project](https://github.com/apache/openwhisk), which ensures correct
versions of dependent libraries are available in the Maven cache.

```
./gradlew install
```

*This command **MUST BE** run from the directory containing the main
 Apache OpenWhisk [repository](https://github.com/apache/openwhisk),
 not this repository's directory.*

3. Run the project tests.

```
./gradlew :tests:test
```
