// .fes.js 只负责管理编译时配置，只能使用plain Object
import path from 'path';

export default {
    publicPath: '/',
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
        BASEURL: '/qualitis',
         CURRENT_ENV: 'dev',
    },
    // alias: {
    //     '@': path.resolve(__dirname, 'src')
    // },
    devServer: {
        port: 8000
    },
    enums: {
        status: [['0', '无效的'], ['1', '有效的']]
    },
    locale: {
        legacy: false
    },
    // chainWebpack: config => {
    //     // 专门处理 CSS 中的图片路径
    //     config.module
    //       .rule('css')
    //       .test(/\.css$/)
    //       .use('css-loader')
    //       .loader('css-loader')
    //       .options({
    //         url: {
    //           filter: (url) => {
    //             // 如果 url 以 static 开头，移除开头的 static/
    //             if (url.startsWith('static/')) {
    //               return url.replace('static/', '');
    //             }
    //             return url;
    //           }
    //         }
    //       });
    //   }
};
