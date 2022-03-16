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
            api: 'http://127.0.0.1:8090/qualitis'
        },
        // 开发环境机器
        dev: {
            api: 'qualitis',

        },
        // 测试环境 fes build --env=sit 触发使用
        sit: {
            api: 'qualitis',
        },
        // 测试环境 fes build --env=uat 触发使用
        uat: {
            api: 'qualitis',
        },
        // 生产环境 fes build默认使用prod环境 fes dev --env=prod 触发使用
        prod: {
            api: 'qualitis',
        },
        sandbox: {
            api: '/wds/qualitis/qualitis',
            env: 'sandbox',
            path: '/wds/qualitis'
        }
    },
    // 配置角色-路由访问权限，使用FesApp.setRole('unLogin')来修改当前用户的角色，控制路由访问权限
    roles: {
        unLogin: ['/home','/dashboard', '/'],
        noauth: ['/dashboard','/home','/taskQuery', '/taskQuery/verificationDetails', '/taskQuery/ruleDetails', '/addTechniqueRule','/ruleQuery','/ruleQuery/*','/taskDetail',
        '/ruleTemplateList','/ruleTemplateList/*','/myProject','/projects/*','/myProject/*','/HelpDocument','/customTechnicalRule',
        '/crossTableCheck','/verifyFailData', '/addGroupTechniqueRule', '/metricManagement', '/engineConfiguration', '/'],
        admin: ['/dashboard','*']
    },
    // map
    map: {
        status: [
            ['1', '成功'],
            ['2', '失败']
        ],
        booleanList: [
            ['1', '是'],
            ['0', '否']
        ]
    },
    i18n: {
        locale: 'zh-cn', // default zh-cn
        messages: i18nConfig
    },
    menu: []
}
