<template>
    <article class="wd-content my-project">
        <header class="my-project-header">
            <NavBar v-model="bar" :data="bars" />
        </header>
        <main>
            <!-- 用户管理 -->
            <template v-if="bar === bars[0].value">
                <User />
            </template>
            <!-- 租户管理 -->
            <!-- <template v-else-if="bar === bars[1].value">
                <Tenant />
            </template> -->
            <!-- 代理用户管理 -->
            <template v-else-if="bar === bars[1].value">
                <Proxy />
            </template>
            <!-- 部门管理 -->
            <template v-else-if="bar === bars[2].value">
                <DepartmentManagement />
            </template>
            <template v-else-if="bar === bars[3].value">
                <RoomManagement />
            </template>
        </main>
    </article>
</template>
<script setup>
import {
    ref, onMounted,
} from 'vue';
import { useI18n, useRoute } from '@fesjs/fes';
import NavBar from '@/pages/projects/components/NavBar';
import { getDepartment } from '@/pages/system/api';
import User from './user';
import Tenant from './tenant';
import Proxy from './proxy';
import DepartmentManagement from './departmentManagement/index.vue';
import RoomManagement from './roomManagement/index.vue';

const { t: $t } = useI18n();
const route = useRoute();
// tab相关
const bar = ref('');
const bars = [
    { label: $t('personnelManagePage.userManagement'), value: '1' },
    // { label: $t('common.tenantManagement'), value: '2' },
    { label: $t('common.proxyUserManagement'), value: '3' },
    { label: $t('optionManagePage.departmentManagement'), value: '4' },
    { label: $t('optionManagePage.roomManagement'), value: '5' },
];

onMounted(() => {
    bar.value = route.query.tab || bars[0].value;
});
</script>
