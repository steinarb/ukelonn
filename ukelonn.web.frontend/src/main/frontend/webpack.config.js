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
                loader: ['babel-loader?' + JSON.stringify({
                    cacheDirectory: true,
                    presets: ['@babel/preset-react']
                }), 'eslint-loader'],
                exclude: /node_modules/
            },
            {
                test: /\.css$/,
                loader: 'style-loader!css-loader'
            },
            {
                test: /\.(eot|svg|ttf|woff|woff2|otf)(\??\#?v=[.0-9]+)?$/,
                loader: 'file-loader?name=[name].[ext]',
            },
        ]
    }
};
