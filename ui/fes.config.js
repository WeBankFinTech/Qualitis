import i18nConfig from './i18n-config.json'
export default {
    mode: 'vertical', // 可选有vertical、horizontal，默认vertical
    theme: 'dark', // 可选有blue、dark，默认blue
    fesName: 'Qualitis', // 项目名称
    FesHeader: true,
    favicon: 'static/icon-title.ico', // 图标
    // 环境变量配置
    env: {
        // 本地开发环境 fes dev默认使用local环境
        local: {
            // api: 'http://172.21.3.173:8090/qualitis'     //行内
            api: 'http://10.107.118.150:8090/qualitis'    //开源

        },
        // 开发环境机器
        dev: {
            api: 'qualitis',

        },
        // 测试环境 fes build --env=sit 触发使用
        sit: {
            api: '',
        },
        // 测试环境 fes build --env=uat 触发使用
        uat: {
            api: '',
        },
        // 生产环境 fes build默认使用prod环境 fes dev --env=prod 触发使用
        prod: {
        }
    },
    // 配置角色-路由访问权限，使用FesApp.setRole('unLogin')来修改当前用户的角色，控制路由访问权限
    roles: {
        unLogin: ['/home','/dashboard', '/'],
        noauth: ['/dashboard','/home','/taskQuery','/addTechniqueRule','/ruleQuery','/ruleQuery/*','/taskDetail',
        '/ruleTemplateList','/ruleTemplateList/*','/myProject','/projects/*','/myProject/*','/HelpDocument','/customTechnicalRule',
        '/crossTableCheck','/verifyFailData', '/addGroupTechniqueRule', '/'],
        admin: ['/dashboard','*']
    },
    // map
    map: {
        status: [
            ['1', '成功'],
            ['2', '失败']
        ]
    },
    i18n: {
        locale: 'zh-cn', // default zh-cn
        messages: i18nConfig
    },
    menu: []
}
