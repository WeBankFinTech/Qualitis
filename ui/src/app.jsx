import {
    access, request as FRequest, monaco, defineRuntimeConfig,
} from '@fesjs/fes';
import PageLoading from '@/components/PageLoading';
import {
    FTabs,
    FSwitch,
    FCheckbox,
    FModal,
    FSpin,
    FButton,
    FInput,
    FInputNumber,
    FForm,
    FTag,
    FSelect,
    FTable,
    FPagination,
    FDatePicker,
    FDropdown,
    FDrawer,
    FCheckboxGroup,
    FSelectTree,
    FSelectCascader,
    FTooltip,
    FSpace,
    FRadio,
    FRadioGroup,
    FRadioButton,
    FEllipsis,
    FMessage,
    FEmpty,
    FScrollbar,
    FConfigProvider,
} from '@fesjs/fes-design';
import './style/base.less';
import { getUrlParams, getBaseUrlParam } from '@/common/utils';
import store from '@/store';
import microApp from '@micro-zoe/micro-app';
import { isPlainObject } from 'lodash-es';
// ticket只能用一次，每次进来的时候只带一次
function checkAndUpdateTicket(ticket = '') {
    const currentTicket = sessionStorage.getItem('currentTicket') || '';
    if (currentTicket !== ticket) {
        sessionStorage.setItem('currentTicket', ticket);
        return ticket;
    }
    return '';
}
microApp.start({
    fetch(url, options, appName) {
        const config = {
            credentials: 'include',
        };

        return window.fetch(url, Object.assign(options, config)).then(res => res.text());
    },
});
export default defineRuntimeConfig({

    beforeRender: {
        loading: <PageLoading />,
        action: async () => {
            try {
                if (window.location.hash === '#/home') {
                    access.setRole('noauth');
                    sessionStorage.setItem('firstRole', 'noauth');
                    sessionStorage.setItem('firstUserName', 'noauth');
                    return Promise.resolve({
                        userName: 'noauth',
                    });
                }

                let username = sessionStorage?.getItem('firstUserName') || 'noauth';
                const res = await FRequest('/auth/common/projector/role', {}, { method: 'GET' });
                username = res?.username || 'noauth';
                const roles = res?.roles || ['noauth'];
                // eslint-disable-next-line camelcase
                const overseas_external_version = res.overseas_external_version || false;
                // eslint-disable-next-line camelcase
                const login_random = res.login_random;
                sessionStorage.setItem('login_random', login_random);
                sessionStorage.setItem('overseas_external_version', overseas_external_version);
                if (Array.isArray(roles) && roles.length > 0) {
                    access.setRole(roles[0].toLowerCase());
                }
                let role = 'noauth';
                sessionStorage.setItem('userLogin', true);
                // 如果没有设置模拟账户走原来正常初始化的逻辑
                const isSimulatorMode = !!sessionStorage.getItem('simulatedUser');
                if (!isSimulatorMode) {
                    // 兜底处理
                    if (roles.length && (roles.includes('admin') || roles.includes('ADMIN'))) {
                        role = 'admin';
                    }
                    // 缓存原本的身份
                    sessionStorage.setItem('firstRole', role);
                    // 缓存原本身份的名字
                    sessionStorage.setItem('firstUserName', username);
                    access.setRole(role);
                }
                // 初始化应用的全局状态，可以通过 useModel('@@initialState') 获取
                return {
                    userName: username,
                    role,
                };
            } catch (error) {
                access.setRole('noauth');
                console.error(error);
                sessionStorage.setItem('firstRole', 'noauth');
                sessionStorage.setItem('firstUserName', 'noauth');
                window.location.hash = '#/home';
                return Promise.resolve({
                    userName: 'noauth',
                });
            }
        },
    },
    request: {
        dataField: 'data',
        baseURL: getBaseUrlParam() || BASEURL,
        withCredentials: true,
        timeout: 60000,
        dataHandler(data, response) {
            if (!isPlainObject(data)) {
                return data;
            }
            const result = Object.assign({}, data);
            // 5375用于判断script部分成功的情况
            // 200其他接口默认都使用这个
            // result.code = ['200'].includes(result.code) ? '0' : result.code;
            if (result.code === '5101') result.data = { code: '5101' };
            if (result.code === '5375') {
                result.data.message = result.message;
                result.data.code = result.code;
            }
            result.code = (result.code === '200' || result.code === '5101' || result.retCode === 0 || result.retCode === 3001 || result.status === 0) ? '0' : result.code;
            // if(response)
            if (['0', '5375'].includes(result.code)) {
                if (response?.request?.responseURL.indexOf('api/v1/projector/rule/bash/add') >= 0) {
                    result.data.code = result.code;
                    result.data.message = result.message;
                }
                return (result && 'data' in result) ? result?.data : response;
            }
            if (result.code !== '0') {
                FMessage.error(result.data?.message || result.response?.data?.message || result.message || result.msg);
                throw new Error(response);
            }
        },
        errorHandler(error) {
            console.log('', error);
            if (error.response) {
                // 请求成功发出且服务器也响应了状态码，但状态代码超出了 2xx 的范围
                if (error.response.status === 403) {
                    FMessage.error(error.data?.message || error.response?.data?.message || error.message || error.msg || 'ACCESS IS FORBIDDEN');
                }
                if (error.response.status === 401) {
                    sessionStorage.setItem('userLogin', false);
                    access.setRole('noauth');
                    console.warn(error, error.data?.message, error.response);
                    FMessage.error(error.data?.message || error.response?.data?.message || error.message || error.msg || '认证失败');
                    if (window.location.hash !== '#/home') {
                        window.location.href = '#/home';
                    }
                }
            }
            console.warn('error', error, error.data?.message, error.response);
            // 200的时候error.data?.message
            // 非200的时候error.response?.data?.message
            // 重复请求err.msg
            if (error && error.msg === '重复请求') return;
            FMessage.error(error.data?.message || error.response?.data?.message || error.message || error.msg);
        },
        requestInterceptors: [
            (config) => {
                // eslint-disable-next-line no-restricted-globals
                if (top !== self && !self.location.href.includes('microApp/dqm')) {
                    // eslint-disable-next-line no-restricted-globals, camelcase
                    const entrance_origin = decodeURIComponent(getUrlParams()?.dssurl || '');
                    // eslint-disable-next-line camelcase
                    config.headers.entrance_origin = entrance_origin;
                    return config;
                }
                return config;
            },
        ],
        responseInterceptors: [
            (response) => {
                const loginPropertiesExpired = response.data?.retCode === 3001 || response.data.data?.retCode === 3001; // 登录接口过期
                const otherExpired = response.data?.status_code === '302'; // 其他接口过期
                if (loginPropertiesExpired || otherExpired) {
                    console.log('登录信息过期, 跳转到sso登出地址!');
                    window.location.hash = '#/home';
                    return response;
                }
                return response;
            },
        ],

    },
    onAppCreated({ app }) {
        app.use(FButton);
        app.use(FTabs);
        app.use(FSelect);
        app.use(FSwitch);
        app.use(FCheckbox);
        app.use(FCheckboxGroup);
        app.use(FModal);
        app.use(FInput);
        app.use(FInputNumber);
        app.use(FForm);
        app.use(FTag);
        app.use(FSpin);
        app.use(FTable);
        app.use(FPagination);
        app.use(FDatePicker);
        app.use(FDropdown);
        app.use(FDrawer);
        app.use(FSelectTree);
        app.use(FSelectCascader);
        app.use(FEllipsis);
        app.use(FSpace);
        app.use(FTooltip);
        app.use(FRadioGroup);
        app.use(FRadio);
        app.use(FRadioButton);
        app.use(FEmpty);
        app.use(FScrollbar);
        app.use(FConfigProvider);
        // 引入vuex
        app.use(store);
    },
    patchRoutes({ routes }) {
        routes.unshift({
            path: '/',
            redirect: '/dashboard',
        });
    },
});


// eslint-disable-next-line no-restricted-globals

FDrawer.props.maskClosable.default = false;
FSelect.props.filter.default = (pattern, option) => !pattern || option.label.toLowerCase().includes(pattern.toLowerCase());
// 样式注册
monaco.editor.defineTheme('logview', {
    base: 'vs',
    inherit: true,
    rules: [
        { token: 'log-info', foreground: '4b71ca' },
        { token: 'log-error', foreground: 'ff0000', fontStyle: 'bold' },
        { token: 'log-warn', foreground: 'FFA500' },
        { token: 'log-date', foreground: '008800' },
        { token: 'log-normal', foreground: '808080' },
    ],
    colors: {
        'editor.lineHighlightBackground': '#ffffff',
        'editorGutter.background': '#f7f7f7',
    },
});
// 语言注册
monaco.languages.register({ id: 'log' });

monaco.languages.setMonarchTokensProvider('log', {
    tokenizer: {
        root: [
            [/(^[=a-zA-Z].*|\d\s.*)/, 'log-normal'],
            [/\sERROR\s.*/, 'log-error'],
            [/\sWARN\s.*/, 'log-warn'],
            [/\sINFO\s.*/, 'log-info'],
            [/^([0-9]{4}||[0-9]{2})-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}(.[0-9]{3})?/, 'log-date'],
            [/^[0-9]{2}\/[0-9]{2}\/[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}(.[0-9]{3})?/, 'log-date'],
            [/(^\*\*Waiting queue:.*)/, 'log-info'],
            [/(^\*\*result tips:.*)/, 'log-info'],
        ],
    },
});
