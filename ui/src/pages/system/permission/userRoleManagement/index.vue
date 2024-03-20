<template>
    <div>
        <BTablePage :isLoading="showLoading" actionType="loading" :loadingText="{ loading: '' }">
            <template v-slot:search>
                <BSearch :form="{}" :isReset="false" @search="refreshUserRoles">
                    <template v-slot:form>
                        <div>
                            <span class="condition-label">用户名</span>
                            <FSelect
                                v-model="userName"
                                clearable
                                filterable
                                placeholder="请选择"
                                :options="userNameList"
                            ></FSelect>
                        </div>
                    </template>
                </BSearch>
            </template>
            <template v-slot:operate>
                <UserRoleManagementActionBar :actions="toolbarActions" />
            </template>
            <template v-slot:table>
                <FTable :data="userRoles">
                    <template #empty>
                        <BPageLoading :actionType="resultByInit ? 'emptyInitResult' : 'emptyQueryResult'" />
                    </template>
                    <FTableColumn :formatter="formatterEmptyValue" prop="uuid" :visible="checkTColShow('uuid')" :label="$t('optionManagePage.userRoleID')" :minWidth="312" ellipsis />
                    <FTableColumn :formatter="formatterEmptyValue" prop="user_name" :visible="checkTColShow('user_name')" :label="$t('common.username')" :minWidth="270" ellipsis />
                    <FTableColumn :formatter="formatterEmptyValue" prop="role_name" :visible="checkTColShow('role_name')" :label="$t('optionManagePage.characterName')" :minWidth="120" ellipsis />
                    <FTableColumn prop="create_user" :visible="checkTColShow('create_user')" :label="$t('optionManagePage.create_user')" :minWidth="120" ellipsis />
                    <FTableColumn prop="create_time" :visible="checkTColShow('create_time')" :label="$t('optionManagePage.create_time')" :minWidth="180" ellipsis />
                    <FTableColumn prop="modify_user" :visible="checkTColShow('modify_user')" :label="$t('optionManagePage.modify_user')" :minWidth="120" ellipsis />
                    <FTableColumn prop="modify_time" :visible="checkTColShow('modify_time')" :label="$t('optionManagePage.modify_time')" :minWidth="180" ellipsis />
                    <FTableColumn :label="$t('common.operate')" :minWidth="104" ellipsis>
                        <template #default="{ row }">
                            <ul class="wd-table-operate-btns">
                                <li class="btn-item"><a @click="handleEditUserRole(row)">{{$t('common.edit')}}</a></li>
                                <li class="btn-item"><a class="btn-delete" @click="handleDeleteUserRole(row)">{{$t('common.delete')}}</a></li>
                            </ul>
                        </template>
                    </FTableColumn>
                </FTable>
            </template>
            <template v-slot:pagination>
                <FPagination
                    v-show="userRoles.length > 0"
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
    <UserRoleEditModal
        v-model:show="showUserRoleEditModal"
        :mode="mode"
        @on-add-success="handleAddUserRoleSuccess"
        @on-edit-success="handleEditUserRoleSuccess" />
    <BTableHeaderConfig
        v-model:headers="tableHeaders"
        v-model:show="showTableHeaderConfig"
        :originHeaders="originHeaders"
        type="user_role_management" />
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useI18n } from '@fesjs/fes';
import { FMessage, FModal } from '@fesjs/fes-design';
import { PlusOutlined, MoreCircleOutlined } from '@fesjs/fes-design/es/icon';
import { debounce } from 'lodash-es';
import eventbus from '@/common/useEvents';
import useTableHeaderConfig from '@/hooks/useTableHeaderConfig';
import UserRoleManagementActionBar from '@/pages/projects/components/projectActionBar';
import {
    BTableHeaderConfig, BPageLoading, BTablePage, BSearch,
} from '@fesjs/traction-widget';
import { FORM_MODE, CONDITIONBUTTONSPACE, MAX_PAGE_SIZE } from '@/assets/js/const';
import { formatterEmptyValue } from '@/common/utils';
import UserRoleEditModal from './editModal';
import { getUserRoles, deleteUserRole, getUserNameList } from './api';


const { t: $t } = useI18n();

const showLoading = ref(false);

const userName = ref('');
const userNameList = ref([]);
const init = async () => {
    try {
        const result = await getUserNameList({
            page: 0,
            size: MAX_PAGE_SIZE,
        });
        userNameList.value = result.data.map(item => ({
            value: item.username,
            label: item.username,
        }));
    } catch (error) {
        console.log('getUserNameList Error:', error);
    }
};

// 工具栏操作按钮配置
const toolbarActions = [
    {
        actionType: 'btn',
        type: 'primary',
        icon: PlusOutlined,
        label: $t('common.add'),
        handler: () => {
            // eslint-disable-next-line no-use-before-define
            handleAddUserRole();
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
    { prop: 'uuid', label: $t('optionManagePage.userRoleID') },
    { prop: 'user_name', label: $t('common.username') },
    { prop: 'role_name', label: $t('optionManagePage.characterName') },
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
const userRoles = ref([]);
const pagination = reactive({
    current: 1,
    size: 10,
    total: 0,
});
const resultByInit = ref(true); // 表格的结果是否是初始状态，搜索后该值变为false
function refreshUserRoles() {
    const params = {
        page: pagination.current - 1,
        size: pagination.size,
        user_name: userName.value,
    };
    getUserRoles(params).then((res) => {
        const { data, total } = res;
        if (!Array.isArray(data)) return;
        userRoles.value = data;
        pagination.total = total;
    }).finally(() => {
        showLoading.value = false;
    });
    showLoading.value = true;
    resultByInit.value = false;
}
const changePage = (reset = false) => {
    if (reset) {
        pagination.current = 1;
        return;
    }
    refreshUserRoles();
};

// 新增、修改部分
const mode = ref('');
const showUserRoleEditModal = ref(false);
const handleAddUserRole = () => {
    mode.value = FORM_MODE.ADD;
    showUserRoleEditModal.value = true;
};
const handleAddUserRoleSuccess = () => {
    const newTotal = pagination.total + 1;
    const newCurrent = Math.ceil(newTotal / pagination.size);
    if (pagination.current === newCurrent) {
        refreshUserRoles();
    } else {
        pagination.current = newCurrent;
    }
};
const handleEditUserRole = (userRole) => {
    mode.value = FORM_MODE.EDIT;
    showUserRoleEditModal.value = true;
    eventbus.emit('userRoleManagement:set-form-model', userRole);
};
const handleEditUserRoleSuccess = () => {
    refreshUserRoles();
};

// 删除部分
const handleDeleteUserRole = (userRole) => {
    const modalRef = FModal.confirm({
        title: $t('common.prompt'),
        content: $t('optionManagePage.deleteRoleID', { name: userRole.uuid }),
        onOk: () => {
            showLoading.value = true;
            return deleteUserRole(userRole.uuid).then(() => {
                FMessage.success($t('toastSuccess.deleteSuccess'));
                if (pagination.total > pagination.size && userRoles.value.length === 1) {
                    pagination.current -= 1;
                } else {
                    refreshUserRoles();
                }
            }).finally(() => {
                showLoading.value = false;
                modalRef.destroy();
            });
        },
        onCancel: () => Promise.resolve(),
    });
};

onMounted(async () => {
    await init();
    await refreshUserRoles();
    resultByInit.value = true;
});
</script>
