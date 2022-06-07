module.exports = {
    job: 'web-bdp-dqm',  // Jenkins项目名
    env : {
        uat: {
            ENV: 'uat',
            TPL: '29520',
            DEPLOY_SERVER: 'origin/uat'
        },
        sit: {
            ENV: 'sit',
            TPL: '29878',
            DEPLOY_SERVER: 'origin/dev'
        },
        dev: {
            ENV: 'dev',
            TPL: '29878',
            DEPLOY_SERVER: 'origin/dev'
        }
    }
}
