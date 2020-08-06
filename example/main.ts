export default function main(args: any) {
  return {
    message: `Hello, ${args.name || "World"}!`,
  };
};
