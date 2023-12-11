<template>
    <div style="padding: 16px 16px 32px">
        <BTablePage :isLoading="showLoading" actionType="loading" :loadingText="{ loading: '' }">
            <template v-slot:search>
                <BSearch :form="{}" :isReset="false" @search="searchTenant">
                    <template v-slot:form>
                        <div>
                            <span class="condition-label">{{$t('tenantManagePage.tenantUserName')}}</span>
                            <FInput v-model="searchTenantKeys" :placeholder="$t('tenantManagePage.searchTenantKeys')" />
                        </div>
                    </template>
                </BSearch>
            </template>
            <template v-slot:operate>
                <ActionBar :actions="actions" />
            </template>
            <template v-slot:table>
                <f-table class="table-container" :data="tableData">
                    <template #empty>
                        <BPageLoading :actionType="resultByInit ? 'emptyInitResult' : 'emptyQueryResult'" />
                    </template>
                    <f-table-column :formatter="formatterEmptyValue" ellipsis prop="tenant_user_id" :label="$t('tenantManagePage.tenantUserId')" :minWidth="88" />
                    <f-table-column :formatter="formatterEmptyValue" ellipsis prop="tenant_user_name" :label="$t('tenantManagePage.tenantUserName')" :minWidth="130" />
                    <f-table-column prop="tenant_user_depts_name" :label="$t('tenantManagePage.relatedDepartment')" :minWidth="288">
                        <template #default="{ row }">
                            <div v-if="row.tenant_user_depts_name">
                                <FEllipsis>
                                    {{getActionRangeString(row.tenant_user_depts_name)}}
                                    <template #tooltip>
                                        <div style="width:300px;word-wrap:break-word">
                                            {{getActionRangeString(row.tenant_user_depts_name)}}
                                        </div>
                                    </template>
                                </FEllipsis>
                            </div>
                            <div v-else>--</div>
                        </template>
                    </f-table-column>
                    <f-table-column prop="tenant_user_services_name" :label="$t('tenantManagePage.relatedInstance')" :minWidth="268">
                        <template #default="{ row }">
                            <div v-if="row.tenant_user_services_name">
                                <FEllipsis>
                                    {{getActionRangeString(row.tenant_user_services_name)}}
                                    <template #tooltip>
                                        <div style="width:300px;word-wrap:break-word">
                                            {{getActionRangeString(row.tenant_user_services_name)}}
                                        </div>
                                    </template>
                                </FEllipsis>
                            </div>
                            <div v-else>--</div>
                        </template>
                    </f-table-column>
                    <f-table-column prop="create_user" :label="$t('tenantManagePage.create_user')" :minWidth="120" ellipsis />
                    <f-table-column prop="create_time" :label="$t('tenantManagePage.create_time')" :minWidth="180" ellipsis />
                    <f-table-column prop="modify_user" :label="$t('tenantManagePage.modify_user')" :minWidth="120" ellipsis />
                    <f-table-column prop="modify_time" :label="$t('tenantManagePage.modify_time')" :minWidth="180" ellipsis />
                    <f-table-column #default="{ row = {}}" :label="$t('common.operate')" :width="104" fixed="right">
                        <ul class="btn-list">
                            <li class="btn-item btn-link edit" @click="edit(row, $event)">
                                {{$t('common.edit')}}
                            </li>
                            <li class="btn-item btn-link del" @click="del(row, $event)">
                                {{$t('common.delete')}}
                            </li>
                        </ul>
                    </f-table-column>
                </f-table>
            </template>
            <template v-slot:pagination>
                <FPagination
                    v-show="tableData.length > 0"
                    v-model:currentPage="pagination.current"
                    v-model:pageSize="pagination.size"
                    show-size-changer
                    show-total
                    :total-count="pagination.total"
                    @change="fetch(true)"
                    @pageSizeChange="fetch" />
            </template>
        </BTablePage>
        <FModal
            v-model:show="showModal"
            :title="$t(addOrEditModalTitle)"
            displayDirective="if"
            :maskClosable="false"
            @ok="save"
            @cancel="cancel"
        >
            <FForm ref="addOrEditTenantFormRef" labelWidth="80px" labelPosition="right" style="padding: 20px 0 0;" :model="addOrEditTenantForm" :rules="addOrEditTenantFormRules">
                <FFormItem :label="$t('tenantManagePage.tenantUserName')" prop="tenantUserName">
                    <FInput v-model="addOrEditTenantForm.tenantUserName" :placeholder="$t('common.pleaseEnter') + '：qualitis_xxxx'"
                            :maxlength="16" showWordLimit />
                </FFormItem>
                <FFormItem :label="$t('tenantManagePage.relatedDepartment')" prop="tenantDept">
                    <FSelect v-model="addOrEditTenantForm.tenantDept" :placeholder="$t('common.pleaseSelect')"
                             filterable multiple collapseTags :collapseTagsLimit="3" :options="tenantDeptList">
                    </FSelect>
                </FFormItem>
                <FFormItem :label="$t('tenantManagePage.relatedInstance')" prop="tenantService">
                    <FSelect v-model="addOrEditTenantForm.tenantService" :placeholder="$t('common.pleaseSelect')"
                             filterable multiple collapseTags :collapseTagsLimit="3" :options="tenantServiceList">
                    </FSelect>
                </FFormItem>
            </FForm>
        </FModal>
    </div>
</template>
<script setup>
import {
    ref, reactive, onMounted, defineProps, computed,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import {
    FMessage, FModal, FTooltip, FEllipsis,
} from '@fesjs/fes-design';
import { CONDITIONBUTTONSPACE, INTMAXVALUE, COMMON_REG } from '@/assets/js/const';
import { PlusOutlined, MoreCircleOutlined } from '@fesjs/fes-design/es/icon';
import {
    getTenantUser, delTenantUser, changeTenantUser,
} from '@/pages/system/api';
import ActionBar from '@/pages/projects/components/projectActionBar';
import { formatterEmptyValue } from '@/common/utils';
import {
    BTableHeaderConfig, BPageLoading, BTablePage, BSearch,
} from '@fesjs/traction-widget';
import { getActionRangeString } from './utils';
import { instanceConfigData } from '../instance/api';
import { getDepartments } from '../person/departmentManagement/api';

const tenantDeptList = ref([]);
const tenantServiceList = ref([]);
const { t: $t } = useI18n();
const showLoading = ref(false);
const showModal = ref(false);
const tableData = ref([]);
const addOrEditTenantFormRef = ref(null);
const pagination = reactive({
    current: 1,
    size: 10,
    total: 0,
});

async function fetch(flag, searchKey) {
    try {
        showLoading.value = true;
        const params = {
            page: flag === true ? pagination.current - 1 : 0,
            size: pagination.size,
        };
        if (searchKey) params.tenant_user_name = searchKey;
        const result = await getTenantUser(params);
        tableData.value = result.data;
        pagination.total = result.total;
        showLoading.value = false;
    } catch (error) {
        console.error(error);
        showLoading.value = false;
    }
}

async function getTenantDeptList() {
    try {
        const params = {
            page: 0,
            size: INTMAXVALUE,
        };
        const res = await getDepartments(params);
        console.log('getDepartmentsRes', res);
        tenantServiceList.value = [];
        res.data.forEach((item) => {
            tenantDeptList.value.push({ value: item.department_id, label: item.department_name });
        });
    } catch (error) {
        console.error(error);
    }
}

async function getTenantServiceList() {
    try {
        const params = {
            ip: '',
            page: 0,
            size: INTMAXVALUE,
        };
        const res = await instanceConfigData(params);
        console.log('instanceConfigDataRes', res);
        tenantServiceList.value = [];
        res.data.forEach((item) => {
            tenantServiceList.value.push({ value: item.id, label: item.ip });
        });
    } catch (error) {
        console.error(error);
    }
}


const addOrEditTenantForm = reactive({
    tenantUserName: '',
    tenantDept: [],
    tenantService: [],
});

function resetAddOrEditTenantForm() {
    const keys = Object.keys(addOrEditTenantForm);
    const obj = {};
    // 如果表单有对象数据，置空逻辑要相应增加
    keys.forEach((item) => {
        if (typeof (addOrEditTenantForm[item]) === 'object') obj[item] = [];
        else {
            obj[item] = '';
        }
    });
    Object.assign(addOrEditTenantForm, obj);
}

const addOrEditTenantFormRules = {
    tenantUserName: [{
        required: true,
        trigger: ['blur', 'input'],
        message: $t('common.notEmpty'),
    }, {
        pattern: COMMON_REG.EN_NAME,
        message: $t('myProject.projectEnNameRegTips'),
    }],
    tenantDept: [
        {
            required: true, message: $t('common.notEmpty'), type: 'array', trigger: 'change',
        },
    ],
    tenantService: [
        {
            required: true, message: $t('common.notEmpty'), type: 'array', trigger: 'change',
        },
    ],
};

const userId = ref('');
const method = ref('put');
const addOrEditModalTitle = ref('tenantManagePage.addTenantUser');

function edit(row, e) {
    console.log('edit', row, e);
    userId.value = row.tenant_user_id;
    addOrEditTenantForm.tenantUserName = row.tenant_user_name;
    addOrEditTenantForm.tenantDept = row.tenant_user_depts;
    addOrEditTenantForm.tenantService = row.tenant_user_services;
    method.value = 'post';
    addOrEditModalTitle.value = 'tenantManagePage.editTenantUser';
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
        label: $t('tenantManagePage.addTenantUser'),
        handler: () => {
            showModal.value = true;
            method.value = 'put';
        },
    },
    // 新增设置表格功能时再加
    // {
    //     actionType: 'dropdown',
    //     type: 'default',
    //     icon: MoreCircleOutlined,
    //     label: $t('myProject.more'),
    //     trigger: 'click',
    //     options: [],
    // },
];

async function save() {
    console.log('save', addOrEditTenantForm.tenantUserName);
    if (!addOrEditTenantForm.tenantUserName) return;
    try {
        console.log(addOrEditTenantForm);
        await addOrEditTenantFormRef.value.validate();
        showLoading.value = true;
        const params = {
            tenant_user_name: addOrEditTenantForm.tenantUserName,
            tenant_user_depts: addOrEditTenantForm.tenantDept,
            tenant_user_services: addOrEditTenantForm.tenantService,
        };
        let msg = $t('toastSuccess.addSuccess');
        // post为修改操作，有对应租户id put为添加操作，无租户id
        if (method.value === 'post') {
            msg = $t('toastSuccess.editSuccess');
            params.tenant_user_id = userId.value;
        }
        const result = await changeTenantUser(params, method.value);
        FMessage.success(msg);
        resetAddOrEditTenantForm();
        showLoading.value = false;
        showModal.value = false;
        fetch();
    } catch (error) {
        console.error(error);
        showLoading.value = false;
    }
}

function cancel() {
    showModal.value = false;
    resetAddOrEditTenantForm();
}
const resultByInit = ref(true); // 表格的结果是否是初始状态，搜索后该值变为false
// 查找租户
const searchTenantKeys = ref('');
function searchTenant() {
    console.log('searchTenant', searchTenantKeys.value);
    fetch(false, searchTenantKeys.value);
    resultByInit.value = false;
}


onMounted(() => {
    getTenantDeptList();
    getTenantServiceList();
    fetch();
    resultByInit.value = true;
});

</script>

<style lang="less" scoped>
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
