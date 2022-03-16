module.exports = {
    job: 'web-bdp-dqm',  // Jenkins项目名
    env : {
        uat: { // uat http://test-dqm.weoa.com/dqm-ui/
            ENV: 'uat',
            TPL: '29520',
            DEPLOY_SERVER: 'origin/uat'
        },
        sit: { // sit http://test-dqm.weoa.com/dqm-ui-dev/
            ENV: 'sit',
            TPL: '29878',
            DEPLOY_SERVER: 'origin/dev'
        },
        dev: { // 开发 http://test-dqm.weoa.com/dqm-ui-dev/
            ENV: 'dev',
            TPL: '29878',
            DEPLOY_SERVER: 'origin/dev'
        }
    }
}
