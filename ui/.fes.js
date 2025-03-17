// .fes.js 只负责管理编译时配置，只能使用plain Object
import path from 'path';

export default {
    publicPath: './',
    access: {
        roles: {
            noauth: ["*"],
            admin: ["*"],
            errorauth: ['/notAuthorized']
        }
    },
    monacoEditor: {
        languages: ['log', 'sql']
    },
    layout: false,
    define: {
        CURRENT_ENV: 'sit',
        BASEMICROURL: '',
        BASEURL: '',
    },
    devServer: {
        port: 8000
    },
    enums: {
        status: [['0', '无效的'], ['1', '有效的']]
    },
    locale: {
        legacy: false
    },

};
