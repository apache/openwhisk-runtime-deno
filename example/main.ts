export default function main(args: {[key: string]: any}) {
  return {
    message: `Hello, ${args.name || "World"}!`,
  };
};
