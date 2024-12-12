import Vue from 'vue';
import {
    Form,
    FormItem,
    RadioGroup,
    Radio,
    Input,
    Select,
    Option,
    Switch
} from 'element-ui';
import './assets/styles/main.scss'
import { buildRuleTableFlag } from './assets/js/utils';

/**
 * 获取角色并设置，路由切换时检查角色权限
 * @param {*} ctx
 * @param {Boolean} inIframe
 * @param {*} next
 */
function setOrCheckRole(ctx, inIframe, next = () => {}) {
    ctx.FesApi.fetch("api/v1/projector/role", "get").then(({roles,username, login_random}) => {
        if(Array.isArray(roles)){
            roles = roles.map(item => item.toLowerCase())
            // let userRole = roles[0];
            ctx.FesFesx.set('userRole', roles);
        }
        let role = 'noauth';
        ctx.FesStorage.set("userLogin", true);
        ctx.FesStorage.set("loginRandom", login_random);
        if (!ctx.FesStorage.get('simulatedUser')) {
            ctx.FesApp.set("FesRoleName", "");
            if(roles && roles.indexOf('admin') > -1 ){
                role = 'admin';
                ctx.FesApp.set("FesRoleName", "管理员");
            }
            ctx.FesApp.set("FesUserName", username);
            ctx.FesApp.setRole(role, false);
            ctx.FesStorage.set('firstRole', role);
        }
        next();
    }).catch((err) => {
        console.error(err);
        next({path: '/home'});
    });
}
export default function () {
    Vue.component(Form.name, Form);
    Vue.component(Select.name, Select);
    Vue.component(FormItem.name, FormItem);
    Vue.component(RadioGroup.name, RadioGroup);
    Vue.component(Radio.name, Radio);
    Vue.component(Input.name, Input);
    Vue.component(Select.name, Select);
    Vue.component(Option.name, Option);
    Vue.component(Switch.name, Switch);
    // 设置logo点击事件
    this.set('FesLogoEvent', () => {});
    // 设置路由钩子
    this.setBeforeRouter(function (to, from, next) {
        const isHideHeader = to.query.hideHead || this.FesFesx.get('IS_HIDE_HEADER');
        if (isHideHeader) {
            const layoutBody = document.querySelector('.layout-right-body');
            layoutBody.style.top = 0;
        }
        this.FesFesx.set('IS_HIDE_HEADER', isHideHeader);
        if(from.path === to.path && from.path === "/") {
            next('/Dashboard');
            return
        }
        if (to.path === '/error') {
            next();
        } else {
            if (to.path === '/home') return next();
            setOrCheckRole(this, false, next);
        }
    });

    // 设置AJAX配置
    this.FesApi.option({
        timeout: 1000 * 60
    });
    const that = this;
    this.FesApi.setError({
        404: function () {
            that.router.replace({path: '/error'})
        },
        401: ({
            data: {
                data
            }
        }) => {
            let lastRedirect = this.FesStorage.get('redirect_to_um_login');
            if (!data) {
                this.FesStorage.set('userLogin', false);
                this.FesApp.setRole('unLogin');
                this.FesApp.router.replace('/home');
            } else {
                // 防止接口问题引起循环跳转, 正常登录不会有问题如果sso登录回调回来没有登录态就会循环 setBeforeRouter 里有获取角色的信息
                if ((!lastRedirect || +new Date() - lastRedirect > 3000) && data.redirect) {
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
            this.FesFesx.set('isEn', true);
        }else {
            this.FesApp.setLocale('zh-cn')
            this.FesFesx.set('currentLanguage', 'zh-cn')
            this.FesFesx.set('Language', 'zh_CN')
            this.FesFesx.set('isEn', false);
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

    this.FesUtil.buildRuleTableFlag = buildRuleTableFlag;
}
