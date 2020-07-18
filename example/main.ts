export default (args: unknown) => {
  return {
    message: `Hello, ${args.name || "Whisker"}!`,
  };
};
