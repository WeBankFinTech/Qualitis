const CspHtmlWebpackPlugin = require('csp-html-webpack-plugin');
module.exports = function (mode, configs, webpack) {
    if (mode === 'build') {
        return {
            plugins: [new CspHtmlWebpackPlugin(
                {
                    'base-uri': "'self'",
                    'object-src': "'none'",
                    'script-src': [
                        "'self'",
                        "'unsafe-eval'"
                    ],
                    'style-src': [
                        "'self'",
                        "'unsafe-inline'"
                    ],
                    'img-src': [
                        'data:',
                        "'self'"
                    ],
                }, 
                {
                    enabled: true,
                    hashingMethod: 'sha256',
                    hashEnabled: {
                        'script-src': true,
                        'style-src': false
                    },
                    nonceEnabled: {
                        'script-src': true,
                        'style-src': false
                    }
                }
            )]
        };
    }
    return {};
};