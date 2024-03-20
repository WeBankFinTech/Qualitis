<template>
    <div>
        <BTablePage :isLoading="showLoading" actionType="loading" :loadingText="{ loading: '' }" :isDivider="false">
            <template v-slot:operate>
                <RolePermissionManagementActionBar :actions="toolbarActions" />
            </template>
            <template v-slot:table>
                <FTable :data="rolePermissions">
                    <template #empty>
                        <BPageLoading actionType="emptyInitResult" />
                    </template>
                    <FTableColumn :formatter="formatterEmptyValue" prop="uuid" :visible="checkTColShow('uuid')" :label="$t('optionManagePage.rolePermissionID')" :width="164" ellipsis />
                    <FTableColumn :formatter="formatterEmptyValue" prop="role_name" :visible="checkTColShow('role_name')" :label="$t('optionManagePage.characterName')" :width="78" ellipsis />
                    <FTableColumn :formatter="formatterEmptyValue" prop="permission_name" :visible="checkTColShow('permission_name')" :label="$t('optionManagePage.authorizationName')" :width="78" ellipsis />
                    <FTableColumn prop="create_user" :visible="checkTColShow('create_user')" :label="$t('optionManagePage.create_user')" :minWidth="120" ellipsis />
                    <FTableColumn prop="create_time" :visible="checkTColShow('create_time')" :label="$t('optionManagePage.create_time')" :minWidth="180" ellipsis />
                    <FTableColumn prop="modify_user" :visible="checkTColShow('modify_user')" :label="$t('optionManagePage.modify_user')" :minWidth="120" ellipsis />
                    <FTableColumn prop="modify_time" :visible="checkTColShow('modify_time')" :label="$t('optionManagePage.modify_time')" :minWidth="180" ellipsis />
                    <FTableColumn :label="$t('common.operate')" :width="104" fixed="right" ellipsis>
                        <template #default="{ row }">
                            <ul class="wd-table-operate-btns">
                                <li class="btn-item"><a @click="handleEditRolePermission(row)">{{$t('common.edit')}}</a></li>
                                <li class="btn-item"><a class="btn-delete" @click="handleDeleteRolePermission(row)">{{$t('common.delete')}}</a></li>
                            </ul>
                        </template>
                    </FTableColumn>
                </FTable>
            </template>
            <template v-slot:pagination>
                <FPagination
                    v-show="rolePermissions.length > 0"
                    v-model:currentPage="pagination.current"
                    v-model:pageSize="pagination.size"
                    show-total
                    show-size-changer
                    :total-count="pagination.total"
                    @change="changePage(false)"
                    @pageSizeChange="changePage(true)" />
            </template>
        </BTablePage>
    </div>
    <RolePermissionEditModal
        v-model:show="showRolePermissionEditModal"
        :mode="mode"
        @on-add-success="handleAddRolePermissionSuccess"
        @on-edit-success="handleEditRolePermissionSuccess" />
    <BTableHeaderConfig
        v-model:headers="tableHeaders"
        v-model:show="showTableHeaderConfig"
        :originHeaders="originHeaders"
        type="role_permission_management" />
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useI18n } from '@fesjs/fes';
import { FMessage, FModal } from '@fesjs/fes-design';
import { PlusOutlined, MoreCircleOutlined } from '@fesjs/fes-design/es/icon';
import { debounce } from 'lodash-es';
import eventbus from '@/common/useEvents';
import useTableHeaderConfig from '@/hooks/useTableHeaderConfig';
import RolePermissionManagementActionBar from '@/pages/projects/components/projectActionBar';
import {
    BTableHeaderConfig, BPageLoading, BTablePage, BSearch,
} from '@fesjs/traction-widget';
import { FORM_MODE } from '@/assets/js/const';
import { formatterEmptyValue } from '@/common/utils';
import RolePermissionEditModal from './editModal';
import { getRolePermissions, deleteRolePermission } from './api';

const { t: $t } = useI18n();

const showLoading = ref(false);

// 工具栏操作按钮配置
const toolbarActions = [
    {
        actionType: 'btn',
        type: 'primary',
        icon: PlusOutlined,
        label: $t('common.add'),
        handler: () => {
            // eslint-disable-next-line no-use-before-define
            handleAddRolePermission();
        },
    },
    {
        actionType: 'dropdown',
        type: 'default',
        icon: MoreCircleOutlined,
        label: $t('myProject.more'),
        trigger: 'click',
        options: [
            {
                label: $t('common.setTableHeaderConfig'),
                value: '1',
                handler: () => {
                    // eslint-disable-next-line no-use-before-define
                    toggleTColConfig();
                },
            },
        ],
    },
];

// 表头配置部分
const originHeaders = [
    { prop: 'uuid', label: $t('optionManagePage.rolePermissionID') },
    { prop: 'role_name', label: $t('optionManagePage.characterName') },
    { prop: 'permission_name', label: $t('optionManagePage.authorizationName') },
    { prop: 'create_user', label: $t('optionManagePage.create_user') },
    { prop: 'create_time', label: $t('optionManagePage.create_time') },
    { prop: 'modify_user', label: $t('optionManagePage.modify_user') },
    { prop: 'modify_time', label: $t('optionManagePage.modify_time') },
];
const {
    checkTColShow,
    toggleTColConfig,
    tableHeaders,
    showTableHeaderConfig,
} = useTableHeaderConfig();


// 列表数据，分页部分
const rolePermissions = ref([]);
const pagination = reactive({
    current: 1,
    size: 10,
    total: 0,
});
function refreshRolePermissions() {
    const params = {
        page: pagination.current - 1,
        size: pagination.size,
    };
    getRolePermissions(params).then((res) => {
        const { data, total } = res;
        if (!Array.isArray(data)) return;
        rolePermissions.value = data;
        pagination.total = total;
    }).finally(() => {
        showLoading.value = false;
    });
    showLoading.value = true;
}
const changePage = (reset = false) => {
    if (reset) {
        pagination.current = 1;
        return;
    }
    refreshRolePermissions();
};

// 新增、修改部分
const mode = ref('');
const showRolePermissionEditModal = ref(false);
const handleAddRolePermission = () => {
    mode.value = FORM_MODE.ADD;
    showRolePermissionEditModal.value = true;
};
const handleAddRolePermissionSuccess = () => {
    const newTotal = pagination.total + 1;
    const newCurrent = Math.ceil(newTotal / pagination.size);
    if (pagination.current === newCurrent) {
        refreshRolePermissions();
    } else {
        pagination.current = newCurrent;
    }
};
const handleEditRolePermission = (rolePermission) => {
    mode.value = FORM_MODE.EDIT;
    showRolePermissionEditModal.value = true;
    eventbus.emit('rolePermissionManagement:set-form-model', rolePermission);
};
const handleEditRolePermissionSuccess = () => {
    refreshRolePermissions();
};

// 删除部分
const handleDeleteRolePermission = (rolePermission) => {
    const modalRef = FModal.confirm({
        title: $t('common.prompt'),
        content: $t('optionManagePage.deleteAuthorityID', { name: rolePermission.uuid }),
        onOk: () => {
            showLoading.value = true;
            return deleteRolePermission(rolePermission.uuid).then(() => {
                FMessage.success($t('toastSuccess.deleteSuccess'));
                if (pagination.total > pagination.size && rolePermissions.value.length === 1) {
                    pagination.current -= 1;
                } else {
                    refreshRolePermissions();
                }
            }).finally(() => {
                showLoading.value = false;
                modalRef.destroy();
            });
        },
        onCancel: () => Promise.resolve(),
    });
};

onMounted(() => {
    refreshRolePermissions();
});
</script>
