import {move, exists, join, fromFileUrl, dirname} from '../deps.js';

async function isBundle(file) {
  return (await Deno.readTextFile(file)).includes(
    'export default __exp["default"];'
  );
}

try {
  const srcDir = join(Deno.cwd(), '..', Deno.args[1]);
  const binDir = join(Deno.cwd(), '..', Deno.args[2]);
  const execPath = join(srcDir, 'exec');
  let sourceEntry = join(srcDir, 'main.ts');
  let compiled = false;

  // Restructure files.
  if (await exists(execPath)) {
    compiled = await isBundle(execPath);
    if (compiled) {
      sourceEntry = join(srcDir, 'ow_bundle.js');
    }

    await move(execPath, sourceEntry, {
      overwrite: true
    });
  }

  if (!compiled) {
    const process = Deno.run({
      cmd: [
        'deno',
        'bundle',
        '--unstable',
        sourceEntry,
        join(srcDir, 'ow_bundle.js')
      ],
      stderr: 'piped',
      stdout: 'piped',
      env: {
        NO_COLOR: 'true'
      }
    });

    const {code} = await process.status();

    if (code !== 0) {
      const rawError = await process.stderrOutput();
      console.log(new TextDecoder().decode(rawError));
    }
  }

  // create /exec file
  const execContent = `#!/bin/sh
cd "$(dirname $0)"
exec env NO_COLOR=true /bin/deno run --unstable -A -q launcher.js`;

  // copy launcher.js
  await Deno.copyFile(
    join(dirname(fromFileUrl(import.meta.url)), '..', 'lib', 'launcher.js'),
    join(srcDir, 'launcher.js')
  );
  await Deno.writeTextFile(execPath, execContent);
  // change permissions
  await Deno.chmod(execPath, 0o777);
  await Deno.remove(binDir, {recursive: true});
  // Finally move everything in source to bin.
  await move(srcDir, binDir);
} catch (error) {
  console.log(error.stack);
}
