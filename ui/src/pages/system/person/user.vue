<template>
    <div>
        <BTablePage :isLoading="showLoading" actionType="loading" :loadingText="{ loading: '' }">
            <template v-slot:search>
                <BSearch v-model:form="searchForm" :isReset="false" @search="loadUser">
                    <template v-slot:form>
                        <div>
                            <span class="condition-label">{{$t('system.division')}}</span>
                            <FSelect
                                v-model="searchForm.department_code"
                                clearable
                                filterable
                                :placeholder="$t('system.select')"
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
                                :placeholder="$t('system.select')"
                                :options="roomList"
                            ></FSelect>
                        </div>
                        <div>
                            <span class="condition-label">{{$t('system.userName')}}</span>
                            <FInput
                                v-model="searchForm.user_name"
                                :placeholder="$t('system.enter')"
                            ></FInput>
                        </div>
                    </template>
                </BSearch>
            </template>
            <template v-slot:operate>
                <ActionBar :actions="actions" />
            </template>
            <template v-slot:table>
                <f-table class="table-container" row-key="user_id" :data="tableData">
                    <template #empty>
                        <BPageLoading :actionType="resultByInit ? 'emptyInitResult' : 'emptyQueryResult'" />
                    </template>
                    <f-table-column :formatter="formatterEmptyValue" ellipsis prop="user_id" :label="$t('personnelManagePage.userId')" :minWidth="88" />
                    <f-table-column :formatter="formatterEmptyValue" ellipsis prop="username" :label="$t('personnelManagePage.userName')" :minWidth="120" />
                    <f-table-column :formatter="formatterEmptyValue" ellipsis prop="chinese_name" :label="$t('personnelManagePage.chineseName')" :minWidth="88" />
                    <f-table-column :formatter="formatterEmptyValue" ellipsis prop="department_name" :label="$t('personnelManagePage.beSubDepartment')" :minWidth="170" />
                    <f-table-column :formatter="formatterEmptyValue" ellipsis prop="position_zh" :label="$t('personnelManagePage.position')" :minWidth="88" />
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
                    <f-table-column #default="{ row = {}}" :label="$t('common.operate')" :minWidth="104" fixed="right">
                        <ul class="wd-table-operate-btns">
                            <li class="btn-item" @click="edit(row, $event)">{{$t('common.edit')}}</li>
                            <li class="btn-item btn-delete" @click="del(row, $event)">{{$t('common.delete')}}</li>
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
                    @change="loadUser(true)"
                    @pageSizeChange="loadUser" />
            </template>
        </BTablePage>
    </div>
    <FModal
        v-model:show="showModal"
        :title="mode === 'add' ? $t('personnelManagePage.newUsers') : $t('personnelManagePage.editUsers')"
        displayDirective="if"
        :maskClosable="false"
        width="600px"
        @ok="handleSaveOrEdit"
        @cancel="cancel"
    >
        <FForm ref="formRef" labelWidth="100px" labelPosition="right" :model="form" :rules="rules">
            <FFormItem :label="`${$t('personnelManagePage.userEnName')}`" prop="username">
                <FInput v-model="form.username" :placeholder="$t('common.pleaseEnter')" :disabled="mode === 'edit'" :maxlength="50" showWordLimit />
            </FFormItem>
            <FFormItem :label="`${$t('personnelManagePage.userZhName')}`" prop="chinese_name">
                <FInput v-model="form.chinese_name" :placeholder="$t('common.pleaseEnter')" :maxlength="50" showWordLimit />
            </FFormItem>
            <FFormItem :label="$t('personnelManagePage.beSubDepartment')" prop="department_name">
                <FSelectCascader
                    v-model="form.department_name"
                    :data="devDivisions"
                    :loadData="loadDevDivisions"
                    clearable
                    remote
                    showPath
                    checkStrictly="child"
                ></FSelectCascader>
            </FFormItem>
            <FFormItem label="职位角色" prop="position_en">
                <FSelect
                    v-model="form.position_en"
                    :placeholder="$t('common.pleaseSelect')"
                    :options="positionRoleList">
                </FSelect>
            </FFormItem>
            <FFormItem v-if="showMoreConfig" :label="$t('personnelManagePage.userConfig')" prop="user_config_json" class="token-style">
                <FInput v-model="form.user_config_json" :placeholder="$t('common.pleaseEnter')" clearable type="textarea" />
            </FFormItem>
            <div class="footer">
                <FButton type="link" @click="() => showMoreConfig = !showMoreConfig">{{showMoreConfig ? '收起更多配置' : '展开更多配置'}}</FButton>
            </div>
        </FForm>
    </FModal>
</template>
<script setup>
import {
    ref, reactive, onMounted,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import { FMessage, FModal } from '@fesjs/fes-design';
import { PlusOutlined, MoreCircleOutlined, ExclamationCircleOutlined } from '@fesjs/fes-design/es/icon';
import {
    getUser, delUser, changeUser, modifyUser, getPositionRole, getUserName,
} from '@/pages/system/api';
import ActionBar from '@/pages/projects/components/projectActionBar';
import { getLabelByValue, formatterEmptyValue } from '@/common/utils';
import { COMMON_REG, CONDITIONBUTTONSPACE, MAX_PAGE_SIZE } from '@/assets/js/const';
import { BPageLoading, BTablePage, BSearch } from '@fesjs/traction-widget';
import ColumnDetail from '@/components/ColumnDetail.vue';
import useDivisions from '../hooks/useDivisions';
import getDepId from '../hooks/utils';
import useDepartmentList from '../hooks/useDepartmentList';

const { t: $t } = useI18n();
const showLoading = ref(false);

// const userNameList = ref([]);
// const getUserNameList = async () => {
//     try {
//         const result = await getUserName();
//         userNameList.value = result.map(item => ({
//             value: item,
//             label: item,
//         })).filter(item => item.label);
//     } catch (error) {
//         console.log('getUserNameList Error:', error);
//     }
// };
const searchForm = ref({});
const { departmentList, roomList, getRoomList } = useDepartmentList();
const onDepartmentChange = () => {
    roomList.value = [];
    if (searchForm.value.department_code) {
        getRoomList(searchForm.value.department_code);
    }
};
const showMoreConfig = ref(false);
const showModal = ref(false);
const showDepModal = ref(false);
const tableData = ref([]);
const mode = ref('add'); // add、edit
const pagination = reactive({
    current: 1,
    size: 10,
    total: 0,
});
const {
    divisions: devDivisions,
    loadDivisions: loadDevDivisions,
    curSubDepartData,
} = useDivisions();

const resultByInit = ref(true); // 表格的结果是否是初始状态，搜索后该值变为false
async function loadUser(flag) {
    try {
        showLoading.value = true;
        const result = await getUser({
            page: flag === true ? pagination.current - 1 : 0,
            size: pagination.size,
            ...searchForm.value,
        });
        tableData.value = result.data.map(item => ({
            ...item,
            content: Object.entries(JSON.parse(item.user_config_json || JSON.stringify({}))).map(val => ({
                value: val[1],
                label: val[0],
            })),
        }));
        pagination.total = result.total;
        showLoading.value = false;
        resultByInit.value = false;
    } catch (error) {
        console.error(error);
        showLoading.value = false;
    }
}

const formRef = ref(null);
const form = ref({});
const oldForm = ref({});
const userId = ref('');

async function edit(row, e) {
    console.log('edit', row, e);
    oldForm.value.department_name = row.department;
    oldForm.value.user_config_json = row.user_config_json;
    userId.value = row.user_id;
    form.value.department_name = row.department_name;
    form.value.username = row.username;
    form.value.chinese_name = row.chinese_name;
    form.value.user_config_json = row.user_config_json;
    form.value.position_en = row.position_en;
    // 编辑回显时不显示命中
    if (form.value.department_name && form.value.department_name.search('/') !== -1) {
        const curDepName = form.value.department_name.split('/')[0];
        const curOption = devDivisions.value.find(item => item.label === curDepName);
        await loadDevDivisions(curOption);
    }
    mode.value = 'edit';
    showModal.value = true;
}

function del(row, e) {
    console.log('del', row, e);
    FModal.confirm({
        title: $t('common.prompt'),
        content: $t('personnelManagePage.deleteCurrentUser', { name: row.username }),
        async onOk() {
            await delUser({ user_id: row.user_id });
            FMessage.success($t('toastSuccess.deleteSuccess'));
            await loadUser();
            pagination.current = 1;
        },
    });
}

const positionRoleList = ref([]);
const getPositionRoleList = async () => {
    try {
        const res = await getPositionRole();
        positionRoleList.value = res.map(item => ({ value: item.code, label: item.message }));
    } catch (error) {
        console.log('getPositionRoleList', error);
    }
};

// 按钮配置
const actions = [
    {
        actionType: 'btn',
        type: 'primary',
        icon: PlusOutlined,
        label: $t('personnelManagePage.newUsers'),
        handler: () => {
            mode.value = 'add';
            showModal.value = true;
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


const rules = {
    username: [
        { required: true, message: $t('common.notEmpty'), trigger: ['blur', 'input'] },
        { pattern: COMMON_REG.ONLY_EN_NAME, message: $t('common.lettersNumbers'), trigger: 'blur' },
    ],
    chinese_name: [
        { required: true, message: $t('common.notEmpty'), trigger: ['blur', 'input'] },
        { pattern: COMMON_REG.CN_NAME2, message: $t('common.lettersNumbersCN'), trigger: 'blur' },
    ],
    department_name: [
        {
            required: true, message: $t('common.notEmpty'), trigger: 'change',
        },
    ],
    position_en: [
        {
            required: true, message: $t('common.notEmpty'), trigger: 'change',
        },
    ],
};
async function valid() {
    try {
        await formRef.value.validate();
        return true;
    } catch (error) {
        return false;
    }
}
function reset() {
    form.value = {};
}
async function saveUser() {
    if (!await valid()) return;
    try {
        showLoading.value = true;
        const depId = getDepId(devDivisions.value, form.value.department_name);
        let subDepCode = '';
        if (Array.isArray(curSubDepartData.value)) {
            const curSubDep = curSubDepartData.value.filter(item => item.id === depId)[0].subDep;
            subDepCode = curSubDep.filter(item => item.value === form.value.department_name)[0].code;
        }
        const params = {
            username: form.value.username,
            chinese_name: form.value.chinese_name,
            department: depId,
            department_sub_id: subDepCode,
            department_name: form.value.department_name,
            user_config_json: form.value.user_config_json || '',
            position_en: form.value.position_en,
            position_zh: getLabelByValue(positionRoleList.value, form.value.position_en),
        };
        const msg = $t('toastSuccess.addSuccess');
        const result = await changeUser(params);
        FMessage.success(msg);
        showLoading.value = false;
        reset();
        showModal.value = false;
        await loadUser();
        // await getUserNameList();
        pagination.current = 1;
    } catch (error) {
        console.error(error);
        showLoading.value = false;
    }
}

function cancel() {
    showModal.value = false;
    reset();
}

async function editUser() {
    if (!await valid()) return;
    if (oldForm.value.department_name === form.value.department_name && oldForm.value.user_config_json === form.value.user_config_json) {
        return FMessage.warn($t('toastSuccess.noEdit'));
    }
    try {
        showLoading.value = true;
        const depId = getDepId(devDivisions.value, form.value.department_name);
        let subDepCode = '';
        if (Array.isArray(curSubDepartData.value)) {
            const curSubDep = curSubDepartData.value.filter(item => item.id === depId)[0].subDep;
            subDepCode = curSubDep.filter(item => item.value === form.value.department_name)[0].code;
        }
        const params = {
            user_id: userId.value,
            chinese_name: form.value.chinese_name,
            department: depId,
            department_sub_id: subDepCode,
            department_name: form.value.department_name,
            user_config_json: form.value.user_config_json,
            position_en: form.value.position_en,
            position_zh: getLabelByValue(positionRoleList.value, form.value.position_en),
        };
        const msg = $t('toastSuccess.editSuccess');
        const result = await modifyUser(params);
        FMessage.success(msg);
        showLoading.value = false;
        reset();
        showModal.value = false;
        await loadUser();
        pagination.current = 1;
    } catch (error) {
        console.error(error);
        showLoading.value = false;
    }
}
function handleSaveOrEdit() {
    if (mode.value === 'add') {
        saveUser();
    } else if (mode.value === 'edit') {
        editUser();
    }
}
onMounted(async () => {
    await getPositionRoleList();
    // await getUserNameList();
    await loadUser();
    resultByInit.value = true;
});

</script>
<style lang="less" scoped>
.token-style {
    :deep(.fes-form-item-label) {
        text-align: end;;
    }
}
.footer{
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>
