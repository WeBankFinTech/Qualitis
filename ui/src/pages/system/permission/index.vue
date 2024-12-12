<template>
    <div class="wd-content permission-management">
        <PermissionManagementNavbar v-model="currentPage" :data="pages" />
        <div class="component-container">
            <component :is="currentComponent" />
        </div>
    </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue';
import { useI18n, useRoute } from '@fesjs/fes';
import PermissionManagementNavbar from '@/pages/projects/components/NavBar';
import RoleManagement from './roleManagement';
import PermissionManagement from './permissionManagement';
import UserRoleManagement from './userRoleManagement';
import RolePermissionManagement from './rolePermissionManagement';
import UserSpecialPermissionManagement from './userSpecialPermissionManagement';

const { t: $t } = useI18n();
const route = useRoute();
const pages = [
    { label: $t('optionManagePage.roleManagement'), value: '1' },
    { label: $t('optionManagePage.userRoleManagement'), value: '2' },
    { label: $t('optionManagePage.authorityManagement'), value: '3' },
    { label: $t('optionManagePage.roleRightsManagement'), value: '4' },
    { label: $t('optionManagePage.userSpecialPermission'), value: '5' },
];
const currentPage = ref('');
const componentMapping = {
    [pages[0].value]: RoleManagement,
    [pages[1].value]: UserRoleManagement,
    [pages[2].value]: PermissionManagement,
    [pages[3].value]: RolePermissionManagement,
    [pages[4].value]: UserSpecialPermissionManagement,
};
const currentComponent = computed(() => componentMapping[currentPage.value]);

onMounted(() => {
    currentPage.value = route.query.tab || pages[0].value;
});
</script>
<style lang="less" scoped>
@import "~@/style/varible";

.permission-management {
    .component-container {
        margin-top: 8px;
    }
}
</style>
<style lang="less">
.fes-form.modal-form {
    .fes-form-item {
        &:last-of-type {
            margin-bottom: 0;
        }
    }
}
</style>
