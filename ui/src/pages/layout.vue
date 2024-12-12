<template>
    <div class="wd-page" @click="changeMenusDisplay">
        <BHorizontalLayout v-if="(!isEmbedInFrame || isEmbedInDMS) && !unLogin" v-model:curPath="route.path" :menus="menus" @menuChange="onMenuClick">
            <template v-slot:top>
                <div v-if="!isEmbedInDMS" class="wd-logo">
                    <div class="avatar ava">
                        <img class="ava" src="@/assets/images/icons/avatar.svg" />
                        <div class="user-menus-list ava" :class="{ active: showMenus }">
                            <div class="user-name ava">{{initialState.userName}}</div>
                            <div v-if="isAdminUser" class="wd-menu-item small ava" @click="showUserPanelFn"><img class="wd-menu-icon ava" src="@/assets/images/icons/simulatorUser.svg">{{$t('fesHeader.simulatedUser')}}<span v-if="selectedSUsername" class="s-user-ctn ava">({{selectedSUsername}}) <span class="user-logout-btn ava" @click.stop="exitSimulatorUser">{{$t('fesHeader.exit')}}</span></span></div>
                            <div class="wd-menu-item small ava" @click="languageSwitching"><img class="wd-menu-icon" src="@/assets/images/icons/language.svg">{{$t('fesHeader.language')}}</div>
                            <div class="wd-menu-item small ava" @click="logout"><img class="wd-menu-icon" src="@/assets/images/icons/esc.svg">{{$t('fesHeader.signOut')}}</div>
                        </div>
                        <span v-if="selectedSUsername" class="simulator-badge ava">{{$t('fesHeader.simulated')}}</span>
                    </div>
                </div>
                <FModal v-model:show="showUserPanel" :title="$t('fesHeader.select')" @ok="selectSimulatorUser" @cancel="showMenus = false">
                    <div class="simulator-user-list">
                        <div class="list-label">{{$t('fesHeader.simulatedUser')}}</div>
                        <div class="list-ctn">
                            <FSpin v-if="isLoadingUserData"></FSpin>
                            <FSelect v-else filterable :options="userData" @change="selectUser"></FSelect>
                        </div>
                    </div>
                </FModal>
            </template>
            <template v-slot:container>
                <div style="margin: -16px -16px -32px">
                    <router-view></router-view>
                </div>
            </template>
        </BHorizontalLayout>
        <template v-else>
            <router-view></router-view>
        </template>
    </div>
</template>
<script setup>
import { ref, h, computed } from 'vue';
import { getBaseUrlParam } from '@/common/utils';
import {
    useModel, useI18n, locale, request as FRequest, useRouter, useRoute, access, createWatermark,
} from '@fesjs/fes';
import {
    FMessage, FModal, FMenu, FScrollbar,
} from '@fesjs/fes-design';
import { BHorizontalLayout } from '@fesjs/traction-widget';

const { t: $t } = useI18n();
const router = useRouter();
const route = useRoute();

// 被嵌入其他项目的情况下不显示侧边栏
// 如果url里面showMenus=true，即使被嵌入也要展示左侧菜单
// eslint-disable-next-line no-restricted-globals
const isEmbedInFrame = computed(() => top !== self && route.query.showMenus !== 'true');
// dms里展示侧边栏
// eslint-disable-next-line no-restricted-globals
const isEmbedInDMS = computed(() => self.location.href.includes('microApp/dqm'));
const showUserPanel = ref(false);
const showMenus = ref(false);
function changeMenusDisplay(e) {
    const classlist = [...(e?.target?.classList || [])];
    showMenus.value = (classlist.includes('ava'));
}

const userData = ref([]);

const isLoadingUserData = ref(false);

// 确认是否为管理员用户
const isAdminUser = ref(sessionStorage.getItem('firstRole') === 'admin');
// 未登录
const unLogin = computed(() => route.path === '/home');

const menus = ref([{
    label: $t('dashboard.dashboard'),
    value: '/dashboard',
    icon: () => h(<fes-icon type="dashboard" />),
}, {
    label: $t('myProject.myProject'),
    value: '/projects',
    icon: () => h(<fes-icon type="projects" />),
}, {
    label: $t('taskQuery.taskQuery'),
    value: '/tasks',
    icon: () => h(<fes-icon type="tasks" />),
}, {
    label: $t('projects.projects'),
    icon: () => h(<fes-icon type="rules" />),
    children: [{
        label: $t('ruleQuery.ruleQuery'),
        value: '/rules/query',
        icon: () => h(<fes-icon type="rules_query" />),
    }, {
        label: $t('ruleTemplate.ruleTemplate'),
        icon: () => h(<fes-icon type="rules_template" />),
        value: '/rules/template',
    }],
}, {
    label: $t('indexManagement.title'),
    value: '/metricManagement',
    icon: () => h(<fes-icon type="quota" />),
}, {
    label: $t('dataSourceManagement.title'),
    value: '/dataSourceManagement',
    icon: () => h(<fes-icon type="datasource_management" />),
}, {
    label: $t('engineConfig.title'),
    value: '/engineConfig',
    icon: () => h(<fes-icon type="engine" />),
}]);

if (isAdminUser.value) {
    // 系统设置只有管理员有权限
    menus.value.push({
        label: $t('configureParameter.configureParameter'),
        icon: () => h(<fes-icon type="setting" />),
        children: [{
            label: $t('common.clustetrConfig'),
            value: '/system/cluster',
            icon: () => h(<fes-icon type="cluster_config" />),
        }, {
            label: $t('instanceConfig.title'),
            value: '/system/instance',
            icon: () => h(<fes-icon type="instance" />),
        }, {
            label: $t('personnelManage.personnelManage'),
            value: '/system/person',
            icon: () => h(<fes-icon type="member_management" />),
        }, {
            label: $t('optionManage.optionManage'),
            value: '/system/permission',
            icon: () => h(<fes-icon type="permission_management" />),
        }, {
            label: $t('common.tenantManagement'),
            value: '/system/tenant',
            icon: () => h(<fes-icon type="tenant_manage" />),
        }],
    });
}

const onMenuClick = (e) => {
    const path = e.value;
    if (/^https?:\/\//.test(path)) {
        window.open(path, '_blank');
    } else if (/^\//.test(path)) {
        router.push(path);
    } else {
        console.warn(
            '[plugin-layout]: 菜单的path只能使以http(s)开头的网址或者路由地址',
        );
    }
};

// 展示模拟用户选择面板
const showUserPanelFn = async () => {
    showUserPanel.value = true;

    try {
        if (userData.value.length === 0) {
            const data = await FRequest('/api/v1/admin/user/name/all', {}, 'get');
            userData.value = data.map(item => ({
                value: item,
                label: item,
            }));
            console.log(userData.value);
            isLoadingUserData.value = false;
        }
    } catch (error) {
        isLoadingUserData.value = false;
    }
};

// 从缓存拿信息
const selectedSUsername = ref(sessionStorage.getItem('simulatedUser'));
let selectedSUsernameTemp = null;
const selectUser = (data) => {
    console.log(data);
    selectedSUsernameTemp = data;
};

const getUserRole = async (params) => {
    try {
        const { roles, username } = await FMessage('auth/common/projector/role', {}, { method: 'GET' });

        let role = 'noauth';
        if ((Array.isArray(roles) && roles && roles[0].toLowerCase().indexOf('admin') > -1) || role.toLowerCase().indexOf('admin') > -1) {
            role = 'admin';
        }
        access.setRole(role);
        if (!sessionStorage.getItem('simulatedUser')) {
            // access.set('FesUserName', username);
            sessionStorage.setItem('FesUserName', username);
        }
        router.push({ path: '/dashboard' });
    } catch (error) {
        const role = sessionStorage.getItem('firstRole');
        if (role) {
            access.setRole(role);
        } else {
            access.setRole('noauth');
        }
    }
};

// 选择虚拟账户
const selectSimulatorUser = async () => {
    if (!selectedSUsernameTemp) return FMessage.warn($t('fesHeader.selectUser'));
    selectedSUsername.value = selectedSUsernameTemp;
    await FRequest(`api/v1/admin/transfer_user/${selectedSUsernameTemp}`, {}, { method: 'GET' });
    sessionStorage.setItem('simulatedUser', selectedSUsernameTemp);
    getUserRole();
    showUserPanel.value = false;
    FMessage.success($t('toastSuccess.simulatedUser'));
    showMenus.value = false;
};
// 全局的初始化信息
const initialState = useModel('@@initialState');
console.log(initialState);
createWatermark({
    content: initialState.userName,
    timestamp: 'YYYY-MM-DD HH:mm',
});

// 普通退出
const logout = async () => {
    try {
        const loginRandom = sessionStorage.getItem('login_random');
        sessionStorage.clear();
        window.location.href = `${window.location.origin}/qualitis/api/v1/logout?loginRandom=${loginRandom}`;
    } catch (error) {
        console.warn(error);
    }
};

// 语言切换
const languageSwitching = () => {
    const language = localStorage.getItem('currentLanguage');
    if (language === 'zh-CN') {
        locale.setLocale({
            locale: 'en-US',
        });
        localStorage.setItem('currentLanguage', 'en-US');
    } else {
        locale.setLocale({
            locale: 'zh-CN',
        });
        localStorage.setItem('currentLanguage', 'zh-CN');
    }
    window.location.reload();
};

// 折叠或者展开菜单
const isSideBarCollapse = ref(false);
const toggleSideBar = () => {
    isSideBarCollapse.value = !isSideBarCollapse.value;
};

// 退出模拟账户
const exitSimulatorUser = () => {
    const role = sessionStorage.getItem('firstRole');
    FModal.confirm({
        title: `${$t('fesHeader.exitUser')}`,
        content: `${$t('message.user')}${selectedSUsername.value}`,
        onOk: async () => {
            try {
                await FRequest('api/v1/admin/transfer_user/exit', {}, { method: 'GET' });
                selectedSUsername.value = '';
                sessionStorage.removeItem('simulatedUser');
                getUserRole();
                access.setRole(role);
                FMessage.success($t('toastSuccess.simulatedOut'));
                showMenus.value = false;
                return Promise.resolve();
            } catch (err) {
                console.warn(err);
            }
        },
        onCancel() {
            showMenus.value = false;
            return Promise.resolve();
        },
    });
};
</script>
<style lang="less" scoped>
@import '@/style/varible.less';

.wd-side-menus{
    position: relative;
    width: 220px;
    // display: grid;
    grid-template-rows: 64px 1fr 48px;
    background: #fff;
    transition: all ease .2s;
    &.collapse{
        width: 56px;
        flex: 0 0 56px;
        .collapse-btn{
            .collapse-icon{
                transform: rotate(-90deg);
            }
        }
        .wd-logo{
            background: none;
        }
        .wd-menu-text{
            display: none;
        }
        .copyright{
            display: none;
        }
    }
    .collapse-btn{
        position: absolute;
        right: 16px;
        bottom: 70px;
        width: 24px;
        height: 24px;
        background: #FFFFFF;
        border-radius: 12px;
        box-shadow: 0 2px 6px 0 rgba(0,0,0,0.08);
        text-align: center;
        cursor: pointer;
        user-select: none;
        .collapse-icon{
            transform: rotate(90deg);
            transition: transform ease .2s;
        }
    }
    .wd-logo{
        position: relative;
        background: url(@/assets/images/logo.svg) 16px center no-repeat;
        background-size: 110px;
        height: 64px;
        .simulator-badge{
            font-size: 12px;
            color: #B7B7BC;
        }
        .avatar{
            position: absolute;
            top: 4px;
            right: 0;
            z-index: 10;
            width: 56px;
            height: 40px;
            padding: 14px 16px 0;
            cursor: pointer;
            background: #fff;
            .user-menus-list{
                &.active {
                    display: block;
                }
                display: none;
                position: absolute;
                top: 40px;
                left: 16px;
                min-width: 160px;
                background: #fff;
                border-radius: 4px;
                box-shadow: 0 2px 12px rgba(15,18,34,.1);
                .user-name{
                    padding: 16px;
                    border-bottom: 1px solid rgba(15,18,34,0.06);
                }
            }
        }
    }
    .wd-menus-list{
        height: calc(100% - 112px);
    }
    .wd-menu-item {
        position: relative;
        display: flex;
        align-items: center;
        padding: 0 16px;
        height: 54px;
        line-height: 54px;
        color: #0F1222;
        cursor: pointer;
        transition: background ease .3s;
        user-select: none;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;

        &.small{
            height: 36px;
            line-height: 36px;
            &::after{
                display: none;
            }
        }

        &::after {
            content: '';
            position: absolute;
            right: 0;
            top: 50%;
            transform: translateY(-50%);
            display: block;
            width: 2px;
            height: 22px;
            background: transparent;
            transition: all ease .3s;
        }

        &:hover,
        &.router-link-active {
            color: #5384FF;
            background: rgba(83,132,255,0.06);

            .wd-menu-icon {
                filter: invert(45%) sepia(13%) saturate(4258%) hue-rotate(197deg) brightness(108%) contrast(100%);
            }

            &::after {
                background: #5384FF;
            }
        }

        .wd-menu-icon {
            margin-right: 8px;
            width: 14px;
        }

        .s-user-ctn{
            position: relative;
            padding-right: 50px;
            .user-logout-btn{
                position: absolute;
                z-index: 10;
                top: 0;
                right: 0;
                padding: 0 8px;
                color: @blue-color;
            }
        }
    }
    .copyright{
        height: 48px;
        line-height: 48px;
        font-size: 12px;
        color: rgba(15,18,34,0.20);
        text-align: center;
    }
}
.simulator-user-list{
    display: flex;
    align-items: center;
    .list-label{
        width: 80px;
        padding-right: 16px;
    }
    .list-ctn{
        flex: 1;
    }
}
</style>
