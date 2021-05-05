var path = require('path');

const PATHS = {
    build: path.join(__dirname, '..', '..', '..', 'target', 'classes')
};

module.exports = {
    entry: './index.js',
    output: {
        path: PATHS.build,
        filename: 'bundle.js'
    },
    devtool: 'source-map',
    resolve: {
        extensions: ['.js', '.jsx']
    },
    module: {
        rules: [
            {
                test: /\.jsx?$/,
                loader: 'babel-loader',
                exclude: /node_modules/,
                query: {
                    cacheDirectory: true,
                    presets: ['@babel/preset-react']
                }
            },
            {
                test: /\.css$/,
                loader: 'style-loader!css-loader'
            },
        ]
    }
};
