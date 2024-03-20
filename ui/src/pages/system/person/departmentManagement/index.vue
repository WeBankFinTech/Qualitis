<template>
    <div>
        <BTablePage :isLoading="showLoading" actionType="loading" :loadingText="{ loading: '' }">
            <template v-slot:search>
                <BSearch v-model:form="searchForm" :isReset="false" @search="handleSearch">
                    <template v-slot:form>
                        <div>
                            <span class="condition-label">{{$t('optionManagePage.departmentName')}}</span>
                            <FSelect
                                v-model="searchForm.department_name"
                                clearable
                                filterable
                                placeholder="请选择"
                                :options="departmentNameList"
                            ></FSelect>
                        </div>
                    </template>
                </BSearch>
            </template>
            <template v-slot:operate>
                <DepartmentManagementActionBar :actions="toolbarActions" />
            </template>
            <template v-slot:table>
                <FTable :data="departments">
                    <template #empty>
                        <BPageLoading actionType="emptyInitResult" />
                    </template>
                    <FTableColumn :formatter="formatterEmptyValue" prop="department_id" :visible="checkTColShow('department_id')" :label="$t('optionManagePage.departmentID')" :width="78" ellipsis />
                    <FTableColumn :formatter="formatterEmptyValue" prop="department_name" :visible="checkTColShow('department_name')" :label="$t('optionManagePage.departmentName')" :width="160" ellipsis />
                    <FTableColumn :formatter="formmatSourceType" prop="source_type" :visible="checkTColShow('department_name')" :label="$t('optionManagePage.departmentFrom')" :width="88" ellipsis />
                    <FTableColumn :formatter="formatterEmptyValue" prop="tenant_user_name" :visible="checkTColShow('tenant_user_name')" :label="$t('tenantManagePage.tenantUserName')" :width="140" ellipsis />
                    <FTableColumn :formatter="formatterEmptyValue" prop="create_user" :label="$t('personnelManagePage.create_user')" :minWidth="120" ellipsis />
                    <FTableColumn :formatter="formatterEmptyValue" prop="create_time" :label="$t('personnelManagePage.create_time')" :minWidth="180" ellipsis />
                    <FTableColumn :formatter="formatterEmptyValue" prop="modify_user" :label="$t('personnelManagePage.modify_user')" :minWidth="120" ellipsis />
                    <FTableColumn :formatter="formatterEmptyValue" prop="modify_time" :label="$t('personnelManagePage.modify_time')" :minWidth="180" ellipsis />
                    <FTableColumn :label="$t('common.operate')" :width="104" fixed="right" ellipsis>
                        <template #default="{ row }">
                            <ul class="wd-table-operate-btns">
                                <li class="btn-item"><a @click="handleEditDepartment(row)">{{$t('common.edit')}}</a></li>
                                <li class="btn-item"><a class="btn-delete" @click="handleDeleteDepartment(row)">{{$t('common.delete')}}</a></li>
                            </ul>
                        </template>
                    </FTableColumn>
                </FTable>
            </template>
            <template v-slot:pagination>
                <FPagination
                    v-show="departments.length > 0"
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
    <DepartmentEditModal
        v-model:show="showDepartmentEditModal"
        :mode="mode"
        :departmentNameList="departmentNameList"
        @on-add-success="handleAddDepartmentSuccess"
        @on-edit-success="handleEditDepartmentSuccess" />
    <BTableHeaderConfig
        v-model:headers="tableHeaders"
        v-model:show="showTableHeaderConfig"
        :originHeaders="originHeaders"
        type="department_management" />
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useI18n } from '@fesjs/fes';
import { FMessage, FModal } from '@fesjs/fes-design';
import { PlusOutlined, MoreCircleOutlined } from '@fesjs/fes-design/es/icon';
import { debounce } from 'lodash-es';
import eventbus from '@/common/useEvents';
import useTableHeaderConfig from '@/hooks/useTableHeaderConfig';
import DepartmentManagementActionBar from '@/pages/projects/components/projectActionBar';
import {
    BTableHeaderConfig, BPageLoading, BTablePage, BSearch,
} from '@fesjs/traction-widget';
import { FORM_MODE, CONDITIONBUTTONSPACE, MAX_PAGE_SIZE } from '@/assets/js/const';
import { formatterEmptyValue } from '@/common/utils';
import DepartmentEditModal from './editModal';
import { getDepartments, deleteDepartment, fetchDepartments } from './api';


const { t: $t } = useI18n();
const formmatSourceType = ({ cellValue }) => (cellValue ? '自定义' : 'HR系统');

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
            handleAddDepartment();
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
    { prop: 'department_id', label: $t('optionManagePage.departmentID') },
    { prop: 'department_name', label: $t('optionManagePage.departmentName') },
    { prop: 'source_type', label: $t('optionManagePage.departmentFrom') },
    { prop: 'tenant_user_name', label: $t('tenantManagePage.tenantUserName') },
];
const {
    checkTColShow,
    toggleTColConfig,
    tableHeaders,
    showTableHeaderConfig,
} = useTableHeaderConfig();

const searchForm = ref({});

// 列表数据，分页部分
const departments = ref([]);
const pagination = reactive({
    current: 1,
    size: 10,
    total: 0,
});
function refreshDepartments() {
    const params = {
        page: pagination.current - 1,
        size: pagination.size,
        ...searchForm.value,
    };
    getDepartments(params).then((res) => {
        const { data, total } = res;
        if (!Array.isArray(data)) return;
        departments.value = data;
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
    refreshDepartments();
};

const handleSearch = () => {
    pagination.current = 1;
    refreshDepartments();
};
const departmentNameList = ref([]);
const getDepartmentNameList = async () => {
    try {
        const res = await fetchDepartments({ t: new Date() });
        departmentNameList.value = res.map(item => ({
            value: item.department_name,
            label: item.department_name,
        }));
        // console.log(departmentNameList.value)
    } catch (error) {
        console.log('getDepartmentNameList error:', error);
    }
};

// 新增、修改部分
const mode = ref('');
const showDepartmentEditModal = ref(false);
const handleAddDepartment = () => {
    mode.value = FORM_MODE.ADD;
    showDepartmentEditModal.value = true;
    eventbus.emit('departmentManagement:set-form-model', { source_type: 0 });
};
const handleAddDepartmentSuccess = () => {
    pagination.current = 1;
    refreshDepartments();
    getDepartmentNameList();
};
const handleEditDepartment = (department) => {
    mode.value = FORM_MODE.EDIT;
    showDepartmentEditModal.value = true;
    eventbus.emit('departmentManagement:set-form-model', department);
};
const handleEditDepartmentSuccess = () => {
    refreshDepartments();
    getDepartmentNameList();
};

// 删除部分
const handleDeleteDepartment = (department) => {
    const modalRef = FModal.confirm({
        title: $t('common.prompt'),
        content: $t('optionManagePage.deleteCurrentDepartment', { name: department.department_name }),
        onOk: () => {
            showLoading.value = true;
            return deleteDepartment(department.department_id).then(() => {
                FMessage.success($t('toastSuccess.deleteSuccess'));
                if (pagination.total > pagination.size && departments.value.length === 1) {
                    pagination.current -= 1;
                } else {
                    refreshDepartments();
                }
            }).finally(() => {
                showLoading.value = false;
                modalRef.destroy();
                getDepartmentNameList();
            });
        },
        onCancel: () => Promise.resolve(),
    });
};
onMounted(() => {
    refreshDepartments();
    getDepartmentNameList();
});
</script>
