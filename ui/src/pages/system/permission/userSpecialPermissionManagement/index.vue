<template>
    <div>
        <BTablePage :isLoading="showLoading" actionType="loading" :loadingText="{ loading: '' }" :isDivider="false">
            <template v-slot:operate>
                <UserSpecialPermissionManagementActionBar :actions="toolbarActions" />
            </template>
            <template v-slot:table>
                <FTable :data="userSpecialPermissions">
                    <template #empty>
                        <BPageLoading actionType="emptyInitResult" />
                    </template>
                    <FTableColumn :formatter="formatterEmptyValue" ellipsis prop="uuid" :visible="checkTColShow('uuid')" :label="$t('optionManagePage.userPrivilegeID')" />
                    <FTableColumn :formatter="formatterEmptyValue" ellipsis prop="user_name" :visible="checkTColShow('user_name')" :label="$t('common.username')" />
                    <FTableColumn :formatter="formatterEmptyValue" ellipsis prop="permission_name" :visible="checkTColShow('permission_name')" :label="$t('optionManagePage.authorizationName')" />
                    <FTableColumn prop="create_user" :visible="checkTColShow('create_user')" :label="$t('optionManagePage.create_user')" :minWidth="120" ellipsis />
                    <FTableColumn prop="create_time" :visible="checkTColShow('create_time')" :label="$t('optionManagePage.create_time')" :minWidth="180" ellipsis />
                    <FTableColumn prop="modify_user" :visible="checkTColShow('modify_user')" :label="$t('optionManagePage.modify_user')" :minWidth="120" ellipsis />
                    <FTableColumn prop="modify_time" :visible="checkTColShow('modify_time')" :label="$t('optionManagePage.modify_time')" :minWidth="180" ellipsis />
                    <FTableColumn ellipsis :label="$t('common.operate')">
                        <template #default="{ row }">
                            <ul class="wd-table-operate-btns">
                                <li class="btn-item"><a @click="handleEditUserSpecialPermission(row)">{{$t('common.edit')}}</a></li>
                                <li class="btn-item"><a class="btn-delete" @click="handleDeleteUserSpecialPermission(row)">{{$t('common.delete')}}</a></li>
                            </ul>
                        </template>
                    </FTableColumn>
                </FTable>
            </template>
            <template v-slot:pagination>
                <FPagination
                    v-show="userSpecialPermissions.length > 0"
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
    <UserSpecialPermissionEditModal
        v-model:show="showUserSpecialPermissionEditModal"
        :mode="mode"
        @on-add-success="handleAddUserSpecialPermissionSuccess"
        @on-edit-success="handleEditUserSpecialPermissionSuccess" />
    <BTableHeaderConfig
        v-model:headers="tableHeaders"
        v-model:show="showTableHeaderConfig"
        :originHeaders="originHeaders"
        type="user_special_permission_management" />
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useI18n } from '@fesjs/fes';
import { FMessage, FModal } from '@fesjs/fes-design';
import { PlusOutlined, MoreCircleOutlined } from '@fesjs/fes-design/es/icon';
import { debounce } from 'lodash-es';
import eventbus from '@/common/useEvents';
import useTableHeaderConfig from '@/hooks/useTableHeaderConfig';
import UserSpecialPermissionManagementActionBar from '@/pages/projects/components/projectActionBar';
import { FORM_MODE } from '@/assets/js/const';
import { formatterEmptyValue } from '@/common/utils';
import {
    BTableHeaderConfig, BPageLoading, BTablePage, BSearch,
} from '@fesjs/traction-widget';
import UserSpecialPermissionEditModal from './editModal';
import { getUserSpecialPermissions, deleteUserSpecialPermission } from './api';

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
            handleAddUserSpecialPermission();
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
    { prop: 'uuid', label: $t('optionManagePage.userPrivilegeID') },
    { prop: 'user_name', label: $t('common.username') },
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
const userSpecialPermissions = ref([]);
const pagination = reactive({
    current: 1,
    size: 10,
    total: 0,
});
function refreshUserSpecialPermissions() {
    const params = {
        page: pagination.current - 1,
        size: pagination.size,
    };
    getUserSpecialPermissions(params).then((res) => {
        const { data, total } = res;
        if (!Array.isArray(data)) return;
        userSpecialPermissions.value = data;
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
    refreshUserSpecialPermissions();
};

// 新增、修改部分
const mode = ref('');
const showUserSpecialPermissionEditModal = ref(false);
const handleAddUserSpecialPermission = () => {
    mode.value = FORM_MODE.ADD;
    showUserSpecialPermissionEditModal.value = true;
};
const handleAddUserSpecialPermissionSuccess = () => {
    const newTotal = pagination.total + 1;
    const newCurrent = Math.ceil(newTotal / pagination.size);
    if (pagination.current === newCurrent) {
        refreshUserSpecialPermissions();
    } else {
        pagination.current = newCurrent;
    }
};
const handleEditUserSpecialPermission = (userSpecialPermission) => {
    mode.value = FORM_MODE.EDIT;
    showUserSpecialPermissionEditModal.value = true;
    eventbus.emit('userSpecialPermissionManagement:set-form-model', userSpecialPermission);
};
const handleEditUserSpecialPermissionSuccess = () => {
    refreshUserSpecialPermissions();
};

// 删除部分
const handleDeleteUserSpecialPermission = (userSpecialPermission) => {
    const modalRef = FModal.confirm({
        title: $t('common.prompt'),
        content: $t('optionManagePage.deleteUserID', { name: userSpecialPermission.user_id }),
        onOk: () => {
            showLoading.value = true;
            return deleteUserSpecialPermission(userSpecialPermission.uuid).then(() => {
                FMessage.success($t('toastSuccess.deleteSuccess'));
                if (pagination.total > pagination.size && userSpecialPermissions.value.length === 1) {
                    pagination.current -= 1;
                } else {
                    refreshUserSpecialPermissions();
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
    refreshUserSpecialPermissions();
});
</script>
