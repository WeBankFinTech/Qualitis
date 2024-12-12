<template>
    <div class="wd-content-body">
        <ActionBar :actions="actions" />
        <div v-if="showLoading" class="loading-block"></div>
        <f-table v-else class="table" :data="tableData">
            <template #empty>
                <BPageLoading actionType="emptyInitResult" />
            </template>
            <f-table-column :formatter="formatterEmptyValue" ellipsis prop="tenant_user_id" :label="$t('tenantManagePage.tenantUserId')" :width="102" />
            <f-table-column :formatter="formatterEmptyValue" ellipsis prop="tenant_user_name" :label="$t('tenantManagePage.tenantUserName')" :width="120" />
            <f-table-column :formatter="formatterEmptyValue" ellipsis prop="tenant_user_members_num" label="成员数量" :width="88" />
            <f-table-column #default="{ row = {}}" :label="$t('common.operate')" :width="176">
                <ul class="btn-list">
                    <li class="btn-item btn-link edit" @click="manage(row, $event)">
                        成员管理
                    </li>
                    <li class="btn-item btn-link edit" @click="edit(row, $event)">
                        编辑
                    </li>
                    <li class="btn-item btn-link del" @click="del(row, $event)">
                        删除
                    </li>
                </ul>
            </f-table-column>
        </f-table>
    </div>
    <div class="table-pagination-container">
        <FPagination
            v-show="tableData.length > 0"
            v-model:currentPage="pagination.current"
            v-model:pageSize="pagination.size"
            show-size-changer
            show-total
            :total-count="pagination.total"
            @change="fetch(true)"
            @pageSizeChange="fetch" />
    </div>
    <FModal
        v-model:show="showModal"
        title="新增租户"
        displayDirective="if"
        :maskClosable="false"
        @ok="save"
        @cancel="cancel"
    >
        <FForm labelWidth="80px" labelPosition="right" style="padding: 20px 0 0;">
            <FFormItem label="租户名" prop="tenant_user_name">
                <FInput v-model="tenantUserName" :placeholder="$t('common.pleaseEnter')" />
            </FFormItem>
        </FForm>
    </FModal>
    <FDrawer v-model:show="showDrawer" :title="$t('tenantManagePage.memberManagement')" width="800px" @cancel="showDrawer = false">
        <div class="operate">
            <div class="label">{{$t('tenantManagePage.userName')}}</div>
            <FInput v-model="userName" class="input" :placeholder="$t('common.pleaseEnter')" />
            <FButton class="add" type="primary" @click="add">{{$t('crossTableCheck.confirmToAdd')}}</FButton>
        </div>

        <f-table row-key="user_tenant_user_id" :data="userList">
            <f-table-column :formatter="formatterEmptyValue" ellipsis prop="username" :label="$t('tenantManagePage.userName')" :width="152" />
            <f-table-column :formatter="formatterEmptyValue" ellipsis prop="tenant_user_name" :label="$t('tenantManagePage.tenantUserName')" :width="192" />
            <f-table-column #default="{ row = {}}" :label="$t('common.operate')" :width="132">
                <ul class="btn-list">
                    <li class="btn-item btn-link del" @click="delUser(row, $event)">
                        删除
                    </li>
                </ul>
            </f-table-column>
        </f-table>
        <div class="table-pagination-container">
            <FPagination
                v-show="userList.length > 0"
                v-model:currentPage="userPage.current"
                v-model:pageSize="userPage.size"
                show-size-changer
                show-total
                :total-count="userPage.total"
                @change="fetchUser(true)"
                @pageSizeChange="fetch" />
        </div>
    </FDrawer>
</template>
<script setup>
import {
    ref, reactive, onMounted, defineProps, computed,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import { FMessage, FModal } from '@fesjs/fes-design';
import { PlusOutlined, MoreCircleOutlined } from '@fesjs/fes-design/es/icon';
import {
    getTenantUser, delTenantUser, changeTenantUser, getUserTenantUser, addUserTenantUser, delUserTenantUser,
} from '@/pages/system/api';
import ActionBar from '@/pages/projects/components/projectActionBar';
import { formatterEmptyValue } from '@/common/utils';

const { t: $t } = useI18n();
const showLoading = ref(false);
const showModal = ref(false);
const showDrawer = ref(false);
const tableData = ref([]);
const userList = ref([]);
const pagination = reactive({
    current: 1,
    size: 10,
    total: 0,
});
const userPage = reactive({
    current: 1,
    size: 10,
    total: 0,
});
async function fetch(flag) {
    try {
        showLoading.value = true;
        const result = await getTenantUser({
            page: flag === true ? pagination.current - 1 : 0,
            size: pagination.size,
        });
        // console.log('result.data', result.data)
        tableData.value = result.data;
        pagination.total = result.total;
        showLoading.value = false;
        // console.log('tableData.value', tableData.value)
    } catch (error) {
        console.error(error);
        showLoading.value = false;
    }
}

const tenantUserName = ref('');
const userId = ref('');
const userName = ref('');
const method = ref('put');


async function fetchUser(flag) {
    try {
        const result = await getUserTenantUser({
            page: flag === true ? userPage.current - 1 : 0,
            size: userPage.size,
        }, tenantUserName.value);
        userList.value = result.data;
        userPage.total = result.total;
    } catch (error) {
        console.error(error);
    }
}
async function add() {
    if (!userName.value) return;
    try {
        const result = await addUserTenantUser({
            tenant_user_name: tenantUserName.value,
            username: userName.value,
        });
        fetchUser();
        fetch(true);
    } catch (error) {
        console.error(error);
    }
}

async function delUser(row, e) {
    console.log('delUser', row, e);
    FModal.confirm({
        title: $t('common.prompt'),
        content: $t('tenantManagePage.deleteCurrentUser', { name: row.username }),
        async onOk() {
            await delUserTenantUser({ user_tenant_user_id: row.user_tenant_user_id });
            FMessage.success($t('toastSuccess.deleteSuccess'));
            fetchUser();
            fetch(true);
        },
    });
}

function manage(row, e) {
    console.log('manage', row, e);
    tenantUserName.value = row.tenant_user_name;
    fetchUser();
    showDrawer.value = true;
}

function edit(row, e) {
    console.log('edit', row, e);
    userId.value = row.tenant_user_id;
    tenantUserName.value = row.tenant_user_name;
    method.value = 'post';
    showModal.value = true;
}

function del(row, e) {
    console.log('del', row, e);
    FModal.confirm({
        title: $t('common.ok'),
        content: $t('message.delete'),
        async onOk() {
            await delTenantUser({ tenant_user_id: row.tenant_user_id });
            FMessage.success($t('toastSuccess.deleteSuccess'));
            fetch();
        },
    });
}

// 按钮配置
const actions = [
    {
        actionType: 'btn',
        type: 'primary',
        icon: PlusOutlined,
        label: $t('tenantManagePage.tenantUser'),
        handler: () => {
            showModal.value = true;
            method.value = 'put';
        },
    },
    {
        actionType: 'dropdown',
        type: 'default',
        icon: MoreCircleOutlined,
        label: $t('myProject.more'),
        trigger: 'click',
        options: [],
    },
];
async function save() {
    if (!tenantUserName.value) return;
    console.log('save', tenantUserName.value);
    try {
        showLoading.value = true;
        const params = {
            tenant_user_name: tenantUserName.value,
        };
        let msg = $t('toastSuccess.addSuccess');
        if (method.value === 'post') {
            msg = $t('toastSuccess.editSuccess');
            params.tenant_user_id = userId.value;
        }
        const result = await changeTenantUser(params, method.value);
        FMessage.success(msg);
        showLoading.value = false;
        tenantUserName.value = '';
        showModal.value = false;
        fetch();
    } catch (error) {
        console.error(error);
        showLoading.value = false;
    }
}

function cancel() {
    showModal.value = false;
    tenantUserName.value = '';
}


onMounted(() => fetch());

</script>

<style lang="less" scoped>
.table {
    margin-top: 24px;
}
.btn-list {
    .edit {
        color: #5384FF;
        cursor: pointer;
    }
    .del {
        color: #F75F56;
        cursor: pointer;
    }
}
.operate {
    display: flex;
    align-items: center;
    font-size: 14px;
    color: #646670;
    padding-bottom: 24px;
    .input {
        width: 138px;
        margin-left: 16px;
        margin-right: 22px;
    }
}
</style>
