module.exports = {
    extends: ['@webank/eslint-config-webank/vue.js'],
    overrides: [
        {
            files: [
                '**/__tests__/*.{j,t}s?(x)',
                '**/tests/unit/**/*.spec.{j,t}s?(x)',
            ],
        },
    ],
    rules: {
        'linebreak-style': ['error', process.platform === ['win32', 'win64'].includes(process.platform) ? 'windows' : 'unix'],
        'import/extensions': 'off',
        'import/no-unresolved': 'warn',
        'import/no-absolute-path': 'warn',
        'no-unused-vars': 'warn',
        'vue/html-closing-bracket-newline': 0,
    },
    env: {
        jest: true,
    },
    globals: {
        BASEURL: false,
    },
};
