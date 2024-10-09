import { defineConfig } from 'vite';
import eslintPlugin from "@nabla/vite-plugin-eslint";

export default defineConfig({
    plugins: [eslintPlugin()],
    build: {
        minify: false,
        sourcemap: true,
        manifest: true,
        rollupOptions: {
            // overwrite default .html entry
            input: 'src/index.js',
            output: {
                entryFileNames: `assets/[name].js`,
                chunkFileNames: `assets/[name].js`,
                assetFileNames: `assets/[name].[ext]`
            }
        },
        // Relative to the root
        outDir: '../../../target/classes',
    },
    // Treat .js files as jsx
    esbuild: {
        include: /\.js$/,
        exclude: [],
        loader: 'jsx',
    },
});
