// .fes.js 只负责管理编译时配置，只能使用plain Object
import path from 'path';

export default {
    publicPath: './',
    access: {
        roles: {
            noauth: ["*"],
            admin: ["*"],
        }
    },
    monacoEditor: {
        languages: ['log']
    },
    layout: false,
    define: {
        // 请求服务地址，没有明确ip采用相对地址
        BASEURL: '/qualitis',
    },
    alias: {
        '@': path.resolve(__dirname, 'src')
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
    extraBabelPlugins:[
        [
            "import",
            {
                "libraryName": "@fesjs/fes-design",
                camel2DashComponentName: false,
                "customName": (name) => {
                    name = name.slice(1).replace(/([A-Z])/g, "-$1").toLowerCase().slice(1)
                    return `@fesjs/fes-design/es/${name}`;
                },
                "style": (name) => {
                    return `${name}/style`;
                }
            },
            'fes-design'
        ]
    ]
};
