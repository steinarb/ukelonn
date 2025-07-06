import { defineConfig } from 'vite';
import path from 'path';
import fs from 'fs';
import { parse } from '@babel/parser';
import traverse from '@babel/traverse';
import * as t from "@babel/types";

export default defineConfig({
    plugins: [exportRoutesPlugin()],
    build: {
        minify: false,
        sourcemap: true,
        manifest: true,
        rollupOptions: {
            // overwrite default .html entry
            input: 'index.js',
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

function exportRoutesPlugin() {
    const routePaths = new Set();

    return {
        name: 'export-routes',

        async transform(src, id) {
            if (!id.includes('node_modules') && !id.includes('commonjsHelpers') && id.includes('.js')) {
                // This is a rollup plugin that runs after esbuild, so code in src has already been processed
                // from JSX to plain JS (i.e. the JSX stuff is gone...).
                // Have to read the raw file from disk to find the JSX tags.
                fs.readFile(id, 'utf-8', (err, data) => {
                    const ast = parse(data, {
                        sourceType: 'module',
                        plugins: ['jsx'],
                    });

                    traverse(ast, {
                        enter(path) {
                            if (t.isJSXElement(path.node)) {
                                const elementName = path.node.openingElement.name.name;
                                if (elementName === 'Route') {
                                    path.node.openingElement.attributes.forEach((attribute) => {
                                        if (attribute.name.name === 'path') {
                                            routePaths.add( attribute.value.value);
                                        }
                                    });
                                }
                            }
                        }
                    });
                });
            }
        },

        generateBundle(options, bundle) {
            const outputDirectory = options.dir || 'dist';
            const assetsDirectory = path.join(outputDirectory, 'assets');
            fs.mkdirSync(assetsDirectory, { recursive: true });
            const filePath = path.join(assetsDirectory, 'routes.txt');
            const fileContent = Array.from(routePaths).join('\n');
            fs.writeFileSync(filePath, fileContent);
        },
    };
};
