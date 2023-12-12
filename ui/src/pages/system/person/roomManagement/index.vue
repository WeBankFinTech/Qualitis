<template>
    <BTablePage
        :isLoading="isLoading"
        :actionType="actionType"
    >
        <template v-slot:search>
            <BSearch
                v-model:form="searchForm"
                :isReset="false"
                @search="handleSearch"
            >
                <template v-slot:form>
                    <div>
                        <!-- 部门名称 -->
                        <span class="condition-label">{{$t('optionManagePage.departmentName')}}</span>
                        <FSelect
                            v-model="searchForm.department_name"
                            :options="departmentNameList"
                            clearable
                            filterable
                        >
                        </FSelect>
                    </div>
                    <div>
                        <!-- 科室名称 -->
                        <span class="condition-label">{{$t('optionManagePage.roomName')}}</span>
                        <FSelect
                            v-model="searchForm.sub_department_name"
                            :options="roomNameList"
                            clearable
                            filterable
                        >
                        </FSelect>
                    </div>
                </template>
            </BSearch>
        </template>
        <template v-slot:operate>
            <FButton
                type="primary"
                @click="handleAdd"
            >
                <template #icon>
                    <PlusOutlined />
                </template>
                {{$t('optionManagePage.addRoom')}}
            </FButton>
        </template>
        <template v-slot:table>
            <f-table :data="tableData">
                <!-- 科室Code -->
                <f-table-column
                    :formatter="formatterEmptyValue"
                    prop="sub_department_code"
                    :label="$t('optionManagePage.roomCode')"
                    :width="100"
                    ellipsis
                />
                <!-- 科室名称 -->
                <f-table-column
                    :formatter="formatterEmptyValue"
                    prop="sub_department_name"
                    :label="$t('optionManagePage.roomName')"
                    :width="160"
                    ellipsis
                />
                <!-- 所属部门 -->
                <f-table-column
                    :formatter="formatterEmptyValue"
                    prop="department_name"
                    :label="$t('optionManagePage.beDepartment')"
                    :width="160"
                    ellipsis
                />
                <!-- 科室来源 -->
                <f-table-column
                    :formatter="formmatSourceType"
                    prop="source_type"
                    :label="$t('optionManagePage.roomFrom')"
                    :width="88"
                    ellipsis
                />
                <!-- 关联租户 -->
                <f-table-column
                    :formatter="formatterEmptyValue"
                    prop="tenant_user_name"
                    :label="$t('optionManagePage.tenantUserName')"
                    :width="140"
                    ellipsis
                />
                <!-- 创建人 -->
                <f-table-column
                    :formatter="formatterEmptyValue"
                    prop="create_user"
                    :label="$t('optionManagePage.create_user')"
                    :width="120"
                    ellipsis
                />
                <!-- 创建时间 -->
                <f-table-column
                    :formatter="formatterEmptyValue"
                    prop="create_time"
                    :label="$t('optionManagePage.create_time')"
                    :width="182"
                    ellipsis
                />
                <!-- 更新人 -->
                <f-table-column
                    :formatter="formatterEmptyValue"
                    prop="modify_user"
                    :label="$t('optionManagePage.modify_user')"
                    :width="120"
                    ellipsis
                />
                <!-- 修改时间 -->
                <f-table-column
                    :formatter="formatterEmptyValue"
                    prop="modify_time"
                    :label="$t('optionManagePage.modify_time')"
                    :width="182"
                    ellipsis
                />
                <f-table-column
                    :label="$t('common.operate')"
                    :width="104"
                    fixed="right"
                >
                    <template #default="{ row }">
                        <a
                            class="table-operate"
                            @click="handleEdit(row)"
                        >{{$t('common.edit')}}</a>
                        <a
                            class="table-operate delete"
                            @click="handleDelete(row)"
                        >{{$t('common.delete')}}</a>
                    </template>
                </f-table-column>
            </f-table>
        </template>
        <template v-slot:pagination>
            <FPagination
                v-model:currentPage="pagination.current"
                v-model:pageSize="pagination.size"
                class="pagination"
                show-size-changer
                show-total
                :total-count="pagination.total"
                @change="fetchTableData"
            />
        </template>
    </BTablePage>
    <EditModal
        v-model:show="editModalShow"
        v-model:modelData="editData"
        :type="type"
        @save="handleSave"
    ></EditModal>
</template>
<script setup >
import { BTablePage, BSearch, formatterEmptyValue } from '@fesjs/traction-widget';
import {
    onMounted, ref, reactive, nextTick,
} from 'vue';
import { PlusOutlined } from '@fesjs/fes-design/es/icon';
import { useI18n } from '@fesjs/fes';
import { FMessage, FSelect } from '@fesjs/fes-design';
import { cloneDeep, size } from 'lodash-es';
import Modal from '@fesjs/fes-design/es/modal/modal';
import dayjs from 'dayjs';
import EditModal from './editModal.vue';
import { deleteRoom, fetchRoomTabelData } from './api';
import { getDepartments } from '../departmentManagement/api';

const formmatSourceType = ({ cellValue }) => (cellValue ? '自定义' : 'HR系统');

const { t: $t } = useI18n();
const pagination = reactive({
    current: 1,
    size: 10,
    total: 0,
});
const searchForm = ref({});
const isLoading = ref(false);
const actionType = ref('loading');

const departmentNameList = ref([]);
const getDepartmentNameList = async () => {
    try {
        const res = await getDepartments({
            page: 0,
            size: 100000000,
        });
        departmentNameList.value = res.data.map(item => ({
            value: item.department_name,
            label: item.department_name,
            code: item.department_code,
        }));
        // console.log(departmentNameList.value)
    } catch (error) {
        console.log('getDepartmentNameList error:', error);
    }
};

const roomNameList = ref([]);
const getRoomNameList = async () => {
    try {
        const res = await fetchRoomTabelData({
            page: 0,
            size: 100000000,
        });
        roomNameList.value = res.data.map(item => ({
            value: item.sub_department_name,
            label: item.sub_department_name,
        }));
        // console.log(roomNameList.value)
    } catch (error) {
        console.log('getRoomNameList error:', error);
    }
};

const tableData = ref([]);

// 获取表格数据
const fetchTableData = async () => {
    isLoading.value = true;
    actionType.value = 'loading';
    try {
        const result = await fetchRoomTabelData({
            page: pagination.current - 1,
            size: pagination.size,
            ...searchForm.value,
        });
        // console.log(result);
        tableData.value = result.data.map(item => ({
            ...item,
        }));
        pagination.total = result.total;
        isLoading.value = false;
        if (pagination.total === 0) {
            actionType.value = 'emptyQueryResult';
        }
    } catch (e) {
        isLoading.value = false;
        actionType.value = 'emptyQueryResult';
        console.log('fetchTableData error:', e);
    }
};

// 查询操作
const handleSearch = () => {
    fetchTableData();
};

const handleReset = () => {
    pagination.current = 1;
    fetchTableData();
};

const editModalShow = ref(false);
const type = ref('add');
const editData = ref({
    source_type: 0,
});
const handleAdd = () => {
    type.value = 'add';
    editData.value = { source_type: 0 };
    editModalShow.value = true;
};
const handleEdit = (row) => {
    type.value = 'edit';
    editModalShow.value = true;
    editData.value = cloneDeep(row);
    console.log(row);
};
const deleteRow = async (row) => {
    try {
        await deleteRoom(row.sub_department_id);
        FMessage.success($t('toastSuccess.deleteSuccess'));
        fetchTableData();
        getRoomNameList();
    } catch (error) {
        console.log('handleDelete error:', error);
    }
};
const handleDelete = async (row) => {
    Modal.confirm({
        closable: true,
        title: '提示',
        content: `科室数据删除后不可恢复，确定删除当前科室Code为【${row.sub_department_code}】的数据？`,
        onOk: () => {
            deleteRow(row);
        },
        onCancel: () => {
            console.log('cancel', row);
        },
    });
};

const handleSave = () => {
    fetchTableData();
    getRoomNameList();
};

onMounted(() => {
    fetchTableData();
    getDepartmentNameList();
    getRoomNameList();
});

</script>
<style lang='less' scoped>
.pagination {
    justify-content: flex-end;
}

.table-operate {
    margin-right: 16px;
    cursor: pointer;

    &:last-child {
        margin-right: 0;
    }
}

.delete {
    color: #F75F56;
}
</style>
