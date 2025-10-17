// .fes.js 只负责管理编译时配置，只能使用plain Object
export default {
    define: {
        CURRENT_ENV: 'dev',
        BASEURL: '/qualitis',
    },
    dynamicImport: true
};
