<template>
    <div>
        <BTablePage :isLoading="showLoading" actionType="loading" :isDivider="false" :loadingText="{ loading: '' }">
            <template v-slot:operate>
                <RoleManagementActionBar :actions="toolbarActions" />
            </template>
            <template v-slot:table>
                <FTable :data="roles">
                    <template #empty>
                        <BPageLoading actionType="emptyInitResult" />
                    </template>
                    <FTableColumn :formatter="formatterEmptyValue" prop="role_id" :visible="checkTColShow('role_id')" :label="$t('optionManagePage.roleID')" :minWidth="88" ellipsis />
                    <FTableColumn :formatter="formatterEmptyValue" prop="role_name_composition" :visible="checkTColShow('role_name_composition')" :label="$t('optionManagePage.characterName')" :minWidth="270" ellipsis />
                    <FTableColumn :formatter="formatterEmptyValue" prop="role_type_name" :visible="checkTColShow('role_type_name')" :label="$t('optionManagePage.roleType')" :minWidth="116" ellipsis />
                    <FTableColumn :formatter="formatterEmptyValue" prop="department_name" :visible="checkTColShow('department_name')" :label="$t('optionManagePage.beDepartment')" :minWidth="172" ellipsis />
                    <FTableColumn prop="create_user" :visible="checkTColShow('create_user')" :label="$t('optionManagePage.create_user')" :minWidth="120" ellipsis />
                    <FTableColumn prop="create_time" :visible="checkTColShow('create_time')" :label="$t('optionManagePage.create_time')" :minWidth="180" ellipsis />
                    <FTableColumn prop="modify_user" :visible="checkTColShow('modify_user')" :label="$t('optionManagePage.modify_user')" :minWidth="120" ellipsis />
                    <FTableColumn prop="modify_time" :visible="checkTColShow('modify_time')" :label="$t('optionManagePage.modify_time')" :minWidth="180" ellipsis />
                    <FTableColumn :label="$t('common.operate')" :minWidth="104" ellipsis>
                        <template #default="{ row }">
                            <ul class="wd-table-operate-btns">
                                <li class="btn-item"><a @click="handleEditRole(row)">{{$t('common.edit')}}</a></li>
                                <li v-if="!['ADMIN','GeneralUser'].includes(row?.role_name)" class="btn-item"><a class="btn-delete" @click="handleDeleteRole(row)">{{$t('common.delete')}}</a></li>
                            </ul>
                        </template>
                    </FTableColumn>
                </FTable>
            </template>
            <template v-slot:pagination>
                <FPagination
                    v-show="roles.length > 0"
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
    <RoleEditModal
        v-model:show="showRoleEditModal"
        :mode="mode"
        @on-add-success="handleAddRoleSuccess"
        @on-edit-success="handleEditRoleSuccess" />
    <BTableHeaderConfig
        v-model:headers="tableHeaders"
        v-model:show="showTableHeaderConfig"
        :originHeaders="originHeaders"
        type="role_management" />
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useI18n } from '@fesjs/fes';
import { FMessage, FModal } from '@fesjs/fes-design';
import { PlusOutlined, MoreCircleOutlined } from '@fesjs/fes-design/es/icon';
import { debounce } from 'lodash-es';
import eventbus from '@/common/useEvents';
import useTableHeaderConfig from '@/hooks/useTableHeaderConfig';
import RoleManagementActionBar from '@/pages/projects/components/projectActionBar';
import {
    BTableHeaderConfig, BPageLoading, BTablePage, BSearch,
} from '@fesjs/traction-widget';
import { FORM_MODE } from '@/assets/js/const';
import { formatterEmptyValue } from '@/common/utils';
import RoleEditModal from './editModal';
import { getRoles, deleteRole } from './api';

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
            handleAddRole();
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
    { prop: 'role_id', label: $t('optionManagePage.roleID') },
    { prop: 'role_name_composition', label: $t('optionManagePage.characterName') },
    { prop: 'department_name', label: $t('optionManagePage.beDepartment') },
    { prop: 'role_type_name', label: $t('optionManagePage.roleType') },
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
const roles = ref([]);
const pagination = reactive({
    current: 1,
    size: 10,
    total: 0,
});
function refreshRoles() {
    const params = {
        page: pagination.current - 1,
        size: pagination.size,
    };
    getRoles(params).then((res) => {
        const { data, total } = res;
        if (!Array.isArray(data)) return;
        roles.value = data.map(item => ({
            role_id: item.role_id,
            role_name: item.role_name,
            zn_name: item.zn_name,
            role_name_composition: `${item.role_name}(${item.zn_name})`,
            role_type: item.role_type,
            role_type_name: item.role_type_name,
            department_name: item.department_name,
            create_time: item.create_time,
            create_user: item.create_user,
            modify_time: item.modify_time,
            modify_user: item.modify_user,
        }));
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
    refreshRoles();
};

// 新增、修改部分
const mode = ref('');
const showRoleEditModal = ref(false);
const handleAddRole = () => {
    mode.value = FORM_MODE.ADD;
    showRoleEditModal.value = true;
};
const handleAddRoleSuccess = () => {
    const newTotal = pagination.total + 1;
    const newCurrent = Math.ceil(newTotal / pagination.size);
    if (pagination.current === newCurrent) {
        refreshRoles();
    } else {
        pagination.current = newCurrent;
    }
};
const handleEditRole = (role) => {
    mode.value = FORM_MODE.EDIT;
    showRoleEditModal.value = true;
    console.log(role);
    eventbus.emit('roleManagement:set-form-model', role);
};
const handleEditRoleSuccess = () => {
    refreshRoles();
};

// 删除部分
const handleDeleteRole = (role) => {
    const modalRef = FModal.confirm({
        title: $t('common.prompt'),
        content: $t('optionManagePage.deleteCurrentRole', { name: role.role_name }),
        onOk: () => {
            showLoading.value = true;
            return deleteRole(role.role_id).then(() => {
                FMessage.success($t('toastSuccess.deleteSuccess'));
                if (pagination.total > pagination.size && roles.value.length === 1) {
                    pagination.current -= 1;
                } else {
                    refreshRoles();
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
    refreshRoles();
});
</script>
