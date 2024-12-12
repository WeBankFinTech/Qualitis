<template>
    <div>
        <BTablePage :isLoading="showLoading" actionType="loading" :loadingText="{ loading: '' }">
            <template v-slot:search>
                <BSearch v-model:form="searchForm" :isReset="false" @search="fetch(true)">
                    <template v-slot:form>
                        <div>
                            <span class="condition-label">{{$t('system.division')}}</span>
                            <FSelect
                                v-model="searchForm.department_code"
                                clearable
                                filterable
                                placeholder="请选择"
                                :options="departmentList"
                                @change="onDepartmentChange"
                            ></FSelect>
                        </div>
                        <div>
                            <span class="condition-label">{{$t('system.department')}}</span>
                            <FSelect
                                v-model="searchForm.sub_department_code"
                                clearable
                                filterable
                                placeholder="请选择"
                                :options="roomList"
                            ></FSelect>
                        </div>
                        <div>
                            <span class="condition-label">{{$t('system.proxyUserName')}}</span>
                            <FSelect
                                v-model="searchForm.proxy_user_name"
                                clearable
                                filterable
                                placeholder="请选择"
                                :options="proxyUserNameList"
                            ></FSelect>
                        </div>
                    </template>
                </BSearch>
            </template>
            <template v-slot:operate>
                <ActionBar :actions="actions" />
            </template>
            <template v-slot:table>
                <f-table class="table" :data="tableData">
                    <template #empty>
                        <BPageLoading actionType="emptyInitResult" />
                    </template>
                    <f-table-column :formatter="formatterEmptyValue" ellipsis prop="proxy_user_id" :label="$t('personnelManagePage.proxyUserId')" :width="112" />
                    <f-table-column :formatter="formatterEmptyValue" ellipsis prop="proxy_user_name" :label="$t('personnelManagePage.proxyUserName')" :width="120" />
                    <f-table-column :formatter="formatterEmptyValue" ellipsis prop="department_name" :label="$t('personnelManagePage.beSubDepartment')" :width="160" />
                    <f-table-column :formatter="formatterEmptyValue" ellipsis prop="proxy_user_members_num" label="成员数量" :width="88" />
                    <f-table-column v-slot="{ row }" :formatter="formatterEmptyValue" :ellipsis="{ line: 1, tooltip: false }" :label="$t('personnelManagePage.userConfig')" :minWidth="88">
                        <ColumnDetail
                            :content="row.content"
                            :title="$t('personnelManagePage.userConfig')"
                            trigger="click"
                            width="240px"
                        >
                            <div class="a-link ellipsis">{{$t('personnelManagePage.viewConfig')}}</div>
                        </ColumnDetail>
                    </f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" prop="create_user" :label="$t('personnelManagePage.create_user')" :minWidth="120" ellipsis />
                    <f-table-column :formatter="formatterEmptyValue" prop="create_time" :label="$t('personnelManagePage.create_time')" :minWidth="180" ellipsis />
                    <f-table-column :formatter="formatterEmptyValue" prop="modify_user" :label="$t('personnelManagePage.modify_user')" :minWidth="120" ellipsis />
                    <f-table-column :formatter="formatterEmptyValue" prop="modify_time" :label="$t('personnelManagePage.modify_time')" :minWidth="180" ellipsis />
                    <f-table-column #default="{ row = {}}" :label="$t('common.operate')" :width="176" fixed="right">
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
    </div>
    <FModal
        v-model:show="showModal"
        :title="mode === 'add' ? $t('personnelManagePage.proxyUser') : $t('personnelManagePage.editProxyUser')"
        displayDirective="if"
        :maskClosable="false"
        width="600px"
        @ok="proxyFormSave"
        @cancel="proxyFormcancel"
    >
        <FForm ref="proxyFormRef" :model="proxyForm" :rules="proxyFormRules" labelWidth="100px" labelPosition="right">
            <FFormItem :label="$t('personnelManagePage.proxyUserName')" prop="proxyUserName">
                <FInput v-model="proxyForm.proxyUserName" :placeholder="$t('common.pleaseEnter')" :disabled="mode === 'edit'" :maxlength="50" showWordLimit />
            </FFormItem>
            <FFormItem :label="$t('personnelManagePage.beSubDepartment')" prop="userDepartment_name">
                <FSelectCascader
                    v-model="proxyForm.userDepartment_name"
                    :data="devDivisions"
                    :loadData="loadDevDivisions"
                    clearable
                    remote
                    showPath
                    checkStrictly="child"
                    multiple
                    collapseTags
                    :collapseTagsLimit="2"
                    @change="depSelectChange"
                ></FSelectCascader>
            </FFormItem>
            <FFormItem v-if="showMoreConfig" :label="$t('personnelManagePage.userConfig')" prop="user_config_json" class="token-style">
                <FInput v-model="proxyForm.user_config_json" :placeholder="$t('common.pleaseEnter')" clearable type="textarea" />
            </FFormItem>
            <div class="footer">
                <FButton type="link" @click="() => showMoreConfig = !showMoreConfig">{{showMoreConfig ? '收起更多配置' : '展开更多配置'}}</FButton>
            </div>
        </FForm>
    </FModal>
    <FDrawer v-model:show="showDrawer" :title="$t('personnelManagePage.memberManagement')" width="800px" @cancel="showDrawer = false">
        <div class="operate">
            <div class="operate-header" @click="() => expandAddMember = !expandAddMember">
                <div class="title">{{$t('personnelManagePage.addMembers')}}</div>
                <div class="icon">
                    <UpOutlined v-if="expandAddMember" />
                    <DownOutlined v-else />
                </div>
            </div>
            <FForm v-show="expandAddMember" ref="memberFormRef" class="operate-form" :model="memberForm" :rules="memberFormRules" labelWidth="100px" labelPosition="right">
                <FFormItem :label="$t('personnelManagePage.userEnName')" prop="userName">
                    <FInput v-model="memberForm.userName" :placeholder="$t('common.pleaseEnter')" :disabled="mode === 'edit'" :maxlength="50" showWordLimit />
                </FFormItem>
            </FForm>
            <div v-show="expandAddMember" class="operate-footer">
                <FButton class="btn" type="primary" @click="add">{{$t('crossTableCheck.confirmToAdd')}}</FButton>
                <FButton class="btn" @click="handleAddMemberCancel">{{$t('common.cancel')}}</FButton>
            </div>
        </div>

        <f-table row-key="user_proxy_user_id" :data="userList">
            <f-table-column ellipsis prop="username" :label="$t('personnelManagePage.userEnName')" :width="152" />
            <f-table-column ellipsis prop="proxy_user_name" :label="$t('personnelManagePage.proxyUserName')" :width="192" />
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
    ref, reactive, onMounted, defineProps, computed, nextTick,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import { FMessage, FModal } from '@fesjs/fes-design';
import {
    PlusOutlined, MoreCircleOutlined, ExclamationCircleOutlined, DownOutlined, UpOutlined,
} from '@fesjs/fes-design/es/icon';
import {
    getProxyUser, delProxyUser, changeProxyUser, getUserProxyUser, addUserProxyUser, delUserProxyUser, getProxyUserName,
} from '@/pages/system/api';
import ActionBar from '@/pages/projects/components/projectActionBar';
import { differenceWith } from 'lodash-es';
import { formatterEmptyValue } from '@/common/utils';
import { BPageLoading, BTablePage, BSearch } from '@fesjs/traction-widget';
import { MAX_PAGE_SIZE, CONDITIONBUTTONSPACE, COMMON_REG } from '@/assets/js/const';
import ColumnDetail from '@/components/ColumnDetail.vue';
import useDivisions from '../hooks/useDivisions';
import getDepId from '../hooks/utils';
import useDepartmentList from '../hooks/useDepartmentList';

const searchForm = ref({});
const { departmentList, roomList, getRoomList } = useDepartmentList();
const onDepartmentChange = () => {
    roomList.value = [];
    if (searchForm.value.department_code) {
        getRoomList(searchForm.value.department_code);
    }
};
const proxyUserNameList = ref([]);
const getProxyUserNameList = async () => {
    try {
        const result = await getProxyUserName();
        proxyUserNameList.value = result.map(item => ({
            value: item,
            label: item,
        })).filter(item => item.label);
    } catch (error) {
        console.log('getProxyUserNameList Error:', error);
    }
};
const expandAddMember = ref(false);
const showMoreConfig = ref(false);
const { t: $t } = useI18n();
const showLoading = ref(false);
const showModal = ref(false);
const showDrawer = ref(false);
const tableData = ref([]);
const userList = ref([]);
const mode = ref('add');
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
const {
    divisions: devDivisions,
    loadDivisions: loadDevDivisions,
    curSubDepartData,
} = useDivisions();

async function fetch(flag) {
    try {
        showLoading.value = true;
        const result = await getProxyUser({
            page: flag === true ? pagination.current - 1 : 0,
            size: pagination.size,
            ...searchForm.value,
        });
        tableData.value = result.data;
        tableData.value.forEach((item) => {
            if (Array.isArray(item.department_name)) {
                item.department_name = item.department_name.join('、');
            }
        });
        tableData.value = tableData.value.map(item => ({
            ...item,
            content: Object.entries(JSON.parse(item.user_config_json || JSON.stringify({}))).map(val => ({
                value: val[1],
                label: val[0],
            })),
        }));
        pagination.total = result.total;
        showLoading.value = false;
    } catch (error) {
        console.error(error);
        showLoading.value = false;
    }
}

const userId = ref('');
const userName = ref('');
const method = ref('put');
const proxyForm = ref({
    userDepartment_name: [],
});
const resetProxyForm = () => {
    proxyForm.value = {};
    proxyForm.value.userDepartment_name = [];
};
const proxyFormRef = ref('');
const proxyFormRules = {
    proxyUserName: [
        { required: true, message: $t('common.notEmpty'), trigger: ['blur', 'input'] },
    ],
    userDepartment_name: [
        {
            type: 'array', required: true, message: $t('common.notEmpty'), trigger: 'change',
        },
    ],
    // bdp_client_token: [
    //     {
    //         required: true, message: $t('common.notEmpty'), trigger: 'change',
    //     },
    // ],
};

async function fetchUser(flag) {
    try {
        const result = await getUserProxyUser({
            page: flag === true ? userPage.current - 1 : 0,
            size: userPage.size,
        }, proxyForm.value.proxyUserName);
        userList.value = result.data;
        userPage.total = result.total;
    } catch (error) {
        console.error(error);
    }
}
const memberForm = ref({});
const memberFormRef = ref(null);
const memberFormRules = ref({
    userName: [
        { required: true, message: $t('common.notEmpty'), trigger: ['change'] },
        { pattern: COMMON_REG.ONLY_EN_NAME, message: $t('common.lettersNumbers'), trigger: 'blur' },
    ],
});
async function add() {
    try {
        await memberFormRef.value.validate();
        const result = await addUserProxyUser({
            proxy_user_name: proxyForm.value.proxyUserName,
            username: memberForm.value.userName,
        });
        fetchUser();
        fetch(true);
    } catch (error) {
        console.error(error);
    }
}

const handleAddMemberCancel = () => {
    expandAddMember.value = false;
    memberForm.value.userName = '';
};

async function delUser(row, e) {
    console.log('delUser', row, e);
    FModal.confirm({
        title: $t('common.prompt'),
        content: $t('personnelManagePage.deleteCurrentUser', { name: row.username }),
        async onOk() {
            await delUserProxyUser({ user_proxy_user_id: row.user_proxy_user_id });
            FMessage.success($t('toastSuccess.deleteSuccess'));
            fetchUser();
            fetch(true);
        },
    });
}

function manage(row, e) {
    console.log('manage', row, e);
    proxyForm.value.proxyUserName = row.proxy_user_name;
    fetchUser();
    showDrawer.value = true;
}

async function edit(row, e) {
    console.log('edit', row, e);
    mode.value = 'edit';
    userId.value = row.proxy_user_id;
    proxyForm.value.proxyUserName = row.proxy_user_name;
    proxyForm.value.userDepartment_name = row.department_name.split('、');
    proxyForm.value.bdp_client_token = row.bdp_client_token;
    proxyForm.value.user_config_json = row.user_config_json;
    if (proxyForm.value.userDepartment_name.length > 0 && proxyForm.value.userDepartment_name[0].search('/') !== -1) {
        const curDepName = proxyForm.value.userDepartment_name[0].split('/')[0];
        const curOption = devDivisions.value.find(item => item.label === curDepName);
        await loadDevDivisions(curOption);
    }
    method.value = 'post';
    showModal.value = true;
}

function del(row, e) {
    console.log('del', row, e);
    FModal.confirm({
        title: $t('common.ok'),
        content: $t('message.delete'),
        async onOk() {
            await delProxyUser({ proxy_user_id: row.proxy_user_id });
            FMessage.success($t('toastSuccess.deleteSuccess'));
            pagination.current = 1;
            fetch();
            getProxyUserNameList();
        },
    });
}

// 按钮配置
const actions = [
    {
        actionType: 'btn',
        type: 'primary',
        icon: PlusOutlined,
        label: $t('personnelManagePage.proxyUser'),
        handler: () => {
            mode.value = 'add';
            showModal.value = true;
            method.value = 'put';
            resetProxyForm();
        },
    },
    // {
    //     actionType: 'dropdown',
    //     type: 'default',
    //     icon: MoreCircleOutlined,
    //     label: $t('myProject.more'),
    //     trigger: 'click',
    //     options: [],
    // },
];
function handleDepInfo(subDepData, departmentName) {
    const curDepInfo = departmentName.map((item) => {
        const curCode = subDepData.find(e => e.value === item).code;
        return { sub_id: curCode, name: item };
    });
    return curDepInfo;
}
async function proxyFormSave() {
    try {
        showLoading.value = true;
        await proxyFormRef.value.validate();
        const depId = getDepId(devDivisions.value, proxyForm.value.userDepartment_name[0]);
        let curDepInfo = {};
        if (Array.isArray(curSubDepartData.value)) {
            const curSubDep = curSubDepartData.value.filter(item => item.id === depId)[0].subDep;
            curDepInfo = handleDepInfo(curSubDep, proxyForm.value.userDepartment_name);
        }
        const params = {
            proxy_user_name: proxyForm.value.proxyUserName,
            user_config_json: proxyForm.value.user_config_json || '',
            department: depId,
            department_info: curDepInfo,
        };
        let msg = $t('toastSuccess.addSuccess');
        if (method.value === 'post') {
            msg = $t('toastSuccess.editSuccess');
            params.proxy_user_id = userId.value;
        }
        const result = await changeProxyUser(params, method.value);
        console.log('changeProxyUser', result);
        FMessage.success(msg);
        showLoading.value = false;
        resetProxyForm();
        showModal.value = false;
        pagination.current = 1;
        fetch();
        getProxyUserNameList();
    } catch (error) {
        console.error('error', error);
        showLoading.value = false;
    }
}

function proxyFormcancel() {
    console.log('proxyFormcancel');
    showModal.value = false;
    resetProxyForm();
}

// 只能多选同一个部门下的不同科室
const oldUserDepartmentNameCache = ref([]);
function depSelectChange(value) {
    const diffValue = differenceWith(
        value,
        oldUserDepartmentNameCache.value,
        (current, before) => current[0] === before[0],
    );
    // 若存在不同项，则说明有新增其他根节点下子节点的情况
    // 若不存在不同项，则说明选中项取消选中或者选中当前根节点下的其他子节点，此种情况可忽略处理
    if (diffValue.length) {
        const rootNodeValue = diffValue[0][0];
        const formatValue = value.filter(item => item[0] === rootNodeValue);
        nextTick(() => {
            proxyForm.value.userDepartment_name = formatValue;
            oldUserDepartmentNameCache.value = formatValue;
        });
    } else {
        oldUserDepartmentNameCache.value = value;
    }
}
onMounted(() => {
    fetch();
    getProxyUserNameList();
});

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
    border: 1px solid #CFD0D3;
    border-radius: 4px;
    padding: 16px;
    margin-bottom: 24px;

    &-header {
        display: flex;
        justify-content: space-between;
        cursor: pointer;

        .title {
            font-size: 14px;
            color: #0F1222;
            text-align: justify;
            line-height: 22px;
            font-weight: 500;
        }
    }

    &-form {
        margin-top: 16px;
    }

    &-footer {
        padding-left: 116px;

        .btn {
            margin-right: 16px;
        }
    }
}

.token-style {
    :deep(.fes-form-item-label) {
        text-align: end;
        ;
    }
}

.footer {
    display: flex;
    justify-content: center;
    align-items: center;
}</style>
