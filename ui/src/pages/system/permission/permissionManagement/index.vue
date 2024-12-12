<template>
    <div>
        <BTablePage :isLoading="showLoading" actionType="loading" :loadingText="{ loading: '' }" :isDivider="false">
            <template v-slot:operate>
                <PermissionManagementActionBar :actions="toolbarActions" />
            </template>
            <template v-slot:table>
                <FTable :data="permissions">
                    <template #empty>
                        <BPageLoading actionType="emptyInitResult" />
                    </template>
                    <FTableColumn :formatter="formatterEmptyValue" prop="permission_id" :visible="checkTColShow('permission_id')" :label="$t('optionManagePage.authorizationId')" :width="78" ellipsis />
                    <FTableColumn :formatter="formatterEmptyValue" prop="url" :visible="checkTColShow('url')" :label="$t('optionManagePage.URLAddress')" :width="200" ellipsis />
                    <FTableColumn :formatter="formatterEmptyValue" prop="method" :visible="checkTColShow('method')" :label="$t('optionManagePage.requestMethod')" :width="88" ellipsis />
                    <FTableColumn prop="create_user" :visible="checkTColShow('create_user')" :label="$t('optionManagePage.create_user')" :minWidth="120" ellipsis />
                    <FTableColumn prop="create_time" :visible="checkTColShow('create_time')" :label="$t('optionManagePage.create_time')" :minWidth="180" ellipsis />
                    <FTableColumn prop="modify_user" :visible="checkTColShow('modify_user')" :label="$t('optionManagePage.modify_user')" :minWidth="120" ellipsis />
                    <FTableColumn prop="modify_time" :visible="checkTColShow('modify_time')" :label="$t('optionManagePage.modify_time')" :minWidth="180" ellipsis />
                    <FTableColumn :label="$t('common.operate')" :width="104" fixed="right" ellipsis>
                        <template #default="{ row }">
                            <ul class="wd-table-operate-btns">
                                <li class="btn-item"><a @click="handleEditPermission(row)">{{$t('common.edit')}}</a></li>
                                <li class="btn-item"><a class="btn-delete" @click="handleDeletePermission(row)">{{$t('common.delete')}}</a></li>
                            </ul>
                        </template>
                    </FTableColumn>
                </FTable>
            </template>
            <template v-slot:pagination>
                <FPagination
                    v-show="permissions.length > 0"
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
    <PermissionEditModal
        v-model:show="showPermissionEditModal"
        :mode="mode"
        @on-add-success="handleAddPermissionSuccess"
        @on-edit-success="handleEditPermissionSuccess" />
    <BTableHeaderConfig
        v-model:headers="tableHeaders"
        v-model:show="showTableHeaderConfig"
        :originHeaders="originHeaders"
        type="permission_management" />
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useI18n } from '@fesjs/fes';
import { FMessage, FModal } from '@fesjs/fes-design';
import { PlusOutlined, MoreCircleOutlined } from '@fesjs/fes-design/es/icon';
import { debounce } from 'lodash-es';
import eventbus from '@/common/useEvents';
import useTableHeaderConfig from '@/hooks/useTableHeaderConfig';
import PermissionManagementActionBar from '@/pages/projects/components/projectActionBar';
import {
    BTableHeaderConfig, BPageLoading, BTablePage, BSearch,
} from '@fesjs/traction-widget';
import { FORM_MODE } from '@/assets/js/const';
import { formatterEmptyValue } from '@/common/utils';
import PermissionEditModal from './editModal';
import { getPermissions, deletePermission } from './api';

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
            handleAddPermission();
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
    { prop: 'permission_id', label: $t('optionManagePage.authorizationId') },
    { prop: 'url', label: $t('optionManagePage.URLAddress') },
    { prop: 'method', label: $t('optionManagePage.requestMethod') },
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
const permissions = ref([]);
const pagination = reactive({
    current: 1,
    size: 10,
    total: 0,
});
function refreshPermissions() {
    const params = {
        page: pagination.current - 1,
        size: pagination.size,
    };
    getPermissions(params).then((res) => {
        const { data, total } = res;
        if (!Array.isArray(data)) return;
        permissions.value = data;
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
    refreshPermissions();
};

// 新增、修改部分
const mode = ref('');
const showPermissionEditModal = ref(false);
const handleAddPermission = () => {
    mode.value = FORM_MODE.ADD;
    showPermissionEditModal.value = true;
};
const handleAddPermissionSuccess = () => {
    const newTotal = pagination.total + 1;
    const newCurrent = Math.ceil(newTotal / pagination.size);
    // 判断下 新页码 与 当前页码一致时，才需要调用刷新，否则 页码改变了 changePage会自动调刷新
    if (pagination.current === newCurrent) {
        refreshPermissions();
    } else {
        pagination.current = newCurrent;
    }
};
const handleEditPermission = (permission) => {
    mode.value = FORM_MODE.EDIT;
    showPermissionEditModal.value = true;
    eventbus.emit('permissionManagement:set-form-model', permission);
};
const handleEditPermissionSuccess = () => {
    refreshPermissions();
};

// 删除部分
const handleDeletePermission = (permission) => {
    const modalRef = FModal.confirm({
        title: $t('common.prompt'),
        content: $t('optionManagePage.deleteLimitID', { name: permission.permission_id }),
        onOk: () => {
            showLoading.value = true;
            return deletePermission(permission.permission_id).then(() => {
                FMessage.success($t('toastSuccess.deleteSuccess'));
                if (pagination.total > pagination.size && permissions.value.length === 1) {
                    pagination.current -= 1;
                } else {
                    refreshPermissions();
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
    refreshPermissions();
});
</script>
