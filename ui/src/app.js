import './assets/styles/main.scss'
export default function() {
    // 设置路由钩子
    this.setBeforeRouter(function(from, to, next) {
        // dqm 登录后检查登录并设置登录信息
        if (from.path === '/home') return next();
        this.FesApi.fetch("api/v1/projector/role", "get").then(({roles,username}) => {
            if(Array.isArray(roles)){
                roles = roles.map(item => item.toLowerCase())
                let userRole = roles[0];
                this.FesFesx.set('userRole', userRole);
            }
            let role = 'noauth';
            this.FesStorage.set("userLogin", true);
            if (!this.FesStorage.get('simulatedUser')) {
                this.FesApp.set("FesRoleName", "");
                if(roles && roles.indexOf('admin') > -1 ){
                    role = 'admin';
                    this.FesApp.set("FesRoleName", "管理员");
                }
                this.FesApp.set("FesUserName", username);
                this.FesApp.setRole(role, false);
                this.FesStorage.set('firstRole', role);
            }
            if (from.path === '/') {
                next({path: '/dashboard'});
            }else {
                next();
            }
        }).catch((err) => {
            console.error(err);
            next({path: '/home'});
        });
    });

    // 设置AJAX配置
    this.FesApi.option({
        timeout: 1000 * 60
    });
    const that = this;
    const lang = this.FesFesx.get('currentLanguage');
    
    this.FesApi.setError({
        404: function() {
            that.router.replace({path: '/error'})
        },
        401: ({data:{data}}) => {
            let lastRedirect = this.FesStorage.get('redirect_to_um_login');
            if(!data) {
                let mes = lang === 'zh-cn' ? '登录失败，请检查密码或帐号' :'Login failed, please check your password or account';
                window.Toast.error(mes);
                this.FesStorage.set('userLogin', false);
                this.FesApp.setRole('unLogin');
            } else {
                // 防止接口问题引起循环跳转, 正常登录不会有问题如果sso登录回调回来没有登录态就会循环 setBeforeRouter 里有获取角色的信息
                if( (!lastRedirect || +new Date() - lastRedirect > 3000) && data.redirect) {
                    this.FesStorage.set('redirect_to_um_login', +new Date());
                    let splitChar = data.redirect.indexOf('?') > 0 ? '&' : '?';
                    data.redirect = `${data.redirect}${splitChar}link=${encodeURIComponent(window.location.href)}`;
                    this.FesApp.setRole('admin');
                    window.location.href = data.redirect;
                }
            }
        }
    })
    setTimeout(() => {
        let currentLanguage = this.FesFesx.get('currentLanguage');
        if(currentLanguage && currentLanguage === 'en'){
            this.FesApp.setLocale('en')
            this.FesFesx.set('currentLanguage', 'en')
            this.FesFesx.set('Language', 'en_US')
        }else {
            this.FesApp.setLocale('zh-cn')
            this.FesFesx.set('currentLanguage', 'zh-cn')
            this.FesFesx.set('Language', 'zh_CN')
        }
        this.FesApi.setHeader({
            'Content-Language': this.FesFesx.get('Language')
        })
    }, 0)
   
    // 设置响应结构
    this.FesApi.setResponse({
        successCode: '200',
        codePath: 'code',
        messagePath: 'message',
        resultPath: 'data'
    });
}
