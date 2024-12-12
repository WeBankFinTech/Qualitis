<template>
    <FDrawer
        :show="show"
        :title="$t('myProject.dataAlarmRule')"
        width="50%"
        :maskClosable="false"
        @cancel="() => { emit('update:show', false) }"
    >
        <BTablePage
            :isLoading="isLoading"
            :actionType="actionType"
            layout="drawer"
            :isDivider="true"
        >
            <template v-slot:search>
                <BSearch
                    v-model:form="searchForm"
                    v-model:advanceForm="advanceSearchForm"
                    :isAdvance="true"
                    @search="handleSearch"
                    @reset="handleReset"
                    @advance="showAdvanceQuery"
                >
                    <template v-slot:form>
                        <div>
                            <span class="condition-label">{{$t('myProject.alarmSubject')}}</span>
                            <FInput
                                v-model="searchForm.topic"
                                :placeholder="$t('common.pleaseInput')"
                            >
                            </FInput>
                        </div>
                        <div>
                            <span class="condition-label">{{$t('myProject.alarmTable')}}</span>
                            <FSelect
                                v-model="searchForm.alert_table"
                                clearable
                                filterable
                                :options="alarmTableList"
                            >
                            </FSelect>
                        </div>
                    </template>
                </BSearch>
            </template>
            <template v-slot:table>
                <f-table :data="tableData">
                    <!-- 告警主题 -->
                    <f-table-column
                        v-slot="{ row }"
                        :label="$t('myProject.alarmSubject')"
                        :width="140"
                        ellipsis
                    >
                        <span
                            v-if="row.topic"
                            :style="{ color: '#5384FF', cursor: 'pointer' }"
                            @click="showAlarmDetail(row)"
                        >{{row.topic}}</span>
                        <span v-else>--</span>
                    </f-table-column>
                    <!-- 告警表 -->
                    <f-table-column
                        :formatter="formatterEmptyValue"
                        prop="alert_table"
                        :label="$t('myProject.alarmTable')"
                        :width="180"
                        ellipsis
                    />
                    <!-- 工作空间 -->
                    <f-table-column
                        :formatter="formatterEmptyValue"
                        prop="work_flow_space"
                        :label="$t('myProject.workflowSpace')"
                        :width="140"
                        ellipsis
                    />
                    <!-- 工作流项目 -->
                    <f-table-column
                        :formatter="formatterEmptyValue"
                        prop="work_flow_project"
                        :label="$t('myProject.workflowProject')"
                        :width="160"
                        ellipsis
                    />
                    <!-- 工作流名称 -->
                    <f-table-column
                        :formatter="formatterEmptyValue"
                        prop="work_flow_name"
                        :label="$t('myProject.workflowName')"
                        :width="160"
                        ellipsis
                    />
                    <!-- 工作流任务 -->
                    <f-table-column
                        :formatter="formatterEmptyValue"
                        prop="node_name"
                        :label="$t('myProject.workflowTaks')"
                        :width="160"
                        ellipsis
                    />
                    <!-- 创建人 -->
                    <f-table-column
                        :formatter="formatterEmptyValue"
                        prop="create_user"
                        :label="$t('myProject.creator')"
                        :width="120"
                        ellipsis
                    />
                    <!-- 创建时间 -->
                    <f-table-column
                        :formatter="formatterEmptyValue"
                        prop="create_time"
                        :label="$t('myProject.createTime')"
                        :width="182"
                        ellipsis
                    />
                    <!-- 修改人 -->
                    <f-table-column
                        :formatter="formatterEmptyValue"
                        prop="modify_user"
                        :label="$t('myProject.modifier')"
                        :width="120"
                        ellipsis
                    />
                    <!-- 修改时间 -->
                    <f-table-column
                        :formatter="formatterEmptyValue"
                        prop="modify_time"
                        :label="$t('myProject.modifyTime')"
                        :width="182"
                        ellipsis
                    />
                </f-table>
            </template>
            <template v-slot:pagination>
                <FPagination
                    v-model:currentPage="pagination.current"
                    v-model:pageSize="pagination.size"
                    show-size-changer
                    show-total
                    :total-count="pagination.total"
                    @change="fetchTableData"
                />
            </template>
        </BTablePage>
        <FModal
            :show="alarmDetailShow"
            :title="$t('myProject.alarmDetail')"
            width="484px"
            :maskClosable="false"
            okText="知道了"
            @ok="() => alarmDetailShow = false"
            @cancel="() => alarmDetailShow = false"
        >
            <div class="label-value-box">
                <div
                    v-for="item, index in alarmDetail"
                    :key="index"
                    class="label-value-box-item"
                >
                    <span class="label">{{item.label}}</span>
                    <FEllipsis :line="3">{{item.value || '--'}}</FEllipsis>
                </div>
            </div>
            <template #footer>
                <FButton
                    type="primary"
                    @click="() => alarmDetailShow = false"
                >
                    知道了
                </FButton>
            </template>
        </FModal>
        <!-- 高级搜索弹窗 -->
        <FModal
            :maskClosable="false"
            :title="$t('common.advanceSearch')"
            :width="484"
            :show="advanceQueryShow"
            @cancel="advanceCancel"
            @ok="handleAdvanceSearch"
        >
            <FForm
                ref="advanceSearchFormRef"
                class="modal-form"
                :model="advanceSearchForm"
                labelPosition="right"
                :labelWidth="70"
            >
                <!-- 告警主题 -->
                <FFormItem
                    prop="topic"
                    :label="$t('myProject.alarmSubject')"
                >
                    <FInput
                        v-model="advanceSearchForm.topic"
                        :placeholder="$t('common.pleaseInput')"
                    >
                    </FInput>
                </FFormItem>
                <!-- 告警表 -->
                <FFormItem
                    prop="alert_table"
                    :label="$t('myProject.alarmTable')"
                >
                    <FSelect
                        v-model="advanceSearchForm.alert_table"
                        clearable
                        filterable
                        :options="alarmTableList"
                    >
                    </FSelect>
                </FFormItem>
                <!-- 工作空间 -->
                <FFormItem
                    :label="$t('myProject.workflowSpace')"
                    prop="work_flow_space"
                >
                    <FSelect
                        v-model="advanceSearchForm.work_flow_space"
                        clearable
                        filterable
                        :options="workflowSpaceList"
                    ></FSelect>
                </FFormItem>
                <!-- 工作流项目 -->
                <FFormItem
                    :label="$t('myProject.workflowProject')"
                    prop="work_flow_project"
                >
                    <FSelect
                        v-model="advanceSearchForm.work_flow_project"
                        clearable
                        filterable
                        :options="workflowProjectList"
                    ></FSelect>
                </FFormItem>
                <!-- 工作流名称 -->
                <FFormItem
                    :label="$t('myProject.workflowName')"
                    prop="work_flow_name"
                >
                    <FSelect
                        v-model="advanceSearchForm.work_flow_name"
                        clearable
                        filterable
                        :options="workflowNameList"
                    ></FSelect>
                </FFormItem>
                <!-- 工作流任务 -->
                <FFormItem
                    :label="$t('myProject.workflowTaks')"
                    prop="node_name"
                >
                    <FSelect
                        v-model="advanceSearchForm.node_name"
                        clearable
                        filterable
                        :options="workflowTaskList"
                    ></FSelect>
                </FFormItem>
                <!-- 创建人 -->
                <FFormItem
                    :label="$t('myProject.creator')"
                    prop="create_user"
                >
                    <FSelect
                        v-model="advanceSearchForm.create_user"
                        clearable
                        filterable
                        :options="projectUserList"
                    ></FSelect>
                </FFormItem>
                <!-- 创建时间 -->
                <FFormItem
                    :label="$t('myProject.createTime')"
                    prop="createTime"
                >
                    <FDatePicker
                        v-model="advanceSearchForm.createTime"
                        type="daterange"
                        clearable
                        placeholder="请选择"
                    >
                    </FDatePicker>
                </FFormItem>
                <!-- 修改人 -->
                <FFormItem
                    :label="$t('myProject.modifier')"
                    prop="modify_user"
                >
                    <FSelect
                        v-model="advanceSearchForm.modify_user"
                        clearable
                        filterable
                        :options="projectUserList"
                    ></FSelect>
                </FFormItem>
                <!-- 修改时间 -->
                <FFormItem
                    :label="$t('myProject.modifyTime')"
                    prop="modifyTime"
                >
                    <FDatePicker
                        v-model="advanceSearchForm.modifyTime"
                        type="daterange"
                        clearable
                        placeholder="请选择"
                    >
                    </FDatePicker>
                </FFormItem>
            </FForm>
        </FModal>
    </FDrawer>
</template>
<script setup>
import {
    computed, onMounted, onUpdated, ref, nextTick, reactive,
} from 'vue';
import { BTablePage, BSearch, formatterEmptyValue } from '@fesjs/traction-widget';
import { useI18n, useRoute } from '@fesjs/fes';
import { cloneDeep } from 'lodash-es';
import { formatDatetime } from '@/common/utils';
import useProjectAuth from '../hooks/useProjectAuth';
import useWorkflowRelList from '../hooks/useWorkflowRelList';
import { fetchAlarmRuleTableData } from '../api.js';

const { t: $t } = useI18n();
const route = useRoute();
const projectId = route.query.projectId;
// eslint-disable-next-line no-undef
const props = defineProps({
    show: {
        type: Boolean,
        required: true,
    },
});
// eslint-disable-next-line no-undef
const emit = defineEmits(['update:show']);

const pagination = reactive({
    current: 1,
    size: 10,
    total: 0,
});
const isLoading = ref(true);
const actionType = ref('loading');

const tableData = ref([]);

const searchForm = ref({});
const advanceQueryShow = ref(false);
const advanceSearchForm = ref({});
const advanceSearchFormBak = ref({});

// 获取表格数据
const fetchTableData = async () => {
    isLoading.value = true;
    actionType.value = 'loading';
    try {
        const { createTime, modifyTime } = advanceSearchForm.value;
        const result = await fetchAlarmRuleTableData({
            page: pagination.current - 1,
            size: pagination.size,
            project_id: projectId,
            ...searchForm.value,
            ...advanceSearchForm.value,
            start_create_time: createTime ? formatDatetime(createTime[0]) : '',
            end_create_time: createTime ? formatDatetime(createTime[1]) : '',
            start_modify_time: modifyTime ? formatDatetime(modifyTime[0]) : '',
            end_modify_time: modifyTime ? formatDatetime(modifyTime[1]) : '',
        });
        console.log(result);
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
    advanceSearchForm.value = {};
    advanceSearchFormBak.value = {};
    pagination.current = 1;
    fetchTableData();
};

// 高级搜索操作
const showAdvanceQuery = () => {
    advanceQueryShow.value = true;
};
const handleAdvanceSearch = () => {
    console.log('handleAdvanceSearch', advanceSearchForm.value);
    advanceSearchFormBak.value = cloneDeep(advanceSearchForm.value);
    advanceQueryShow.value = false;
    searchForm.value = {};
    fetchTableData();
};
const advanceCancel = () => {
    advanceQueryShow.value = false;
    advanceSearchForm.value = cloneDeep(advanceSearchFormBak.value);
};

// 重置操作
const handleReset = () => {
    pagination.current = 1;
    advanceSearchFormBak.value = {};
    fetchTableData();
};

const alarmDetailShow = ref(false);
const alarmDetail = ref([]);
const showAlarmDetail = (row) => {
    alarmDetailShow.value = true;
    alarmDetail.value = [
        { value: row.topic || '- -', label: $t('myProject.alarmSubject') },
        { value: row.info_receiver || '- -', label: $t('myProject.defaultAlarmMan') },
        { value: row.major_receiver || '- -', label: $t('myProject.advanceAlarmMan') },
        { value: row.alert_table || '- -', label: $t('myProject.alarmTable') },
        { value: row.filter || '- -', label: $t('myProject.alarmFilterCondition') },
        { value: row.alert_col || '- -', label: $t('myProject.defaultAlarmFilterCol') },
        { value: row.major_alert_col || '- -', label: $t('myProject.advanceAlarmFilterCol') },
        { value: row.content_cols || '- -', label: $t('myProject.contentShowCol') },
    ];
};

// 获取用户列表
const {
    projectUserList,
    getProjectUserList,
} = useProjectAuth();

const {
    workflowSpaceList,
    workflowNameList,
    workflowProjectList,
    workflowTaskList,
    alarmTableList,
} = useWorkflowRelList(projectId, 2);

onMounted(async () => {
    await getProjectUserList();
    projectUserList.value = projectUserList.value.map(item => ({ value: item, label: item }));
});
onUpdated(() => {
    if (props.show) {
        fetchTableData();
    }
});
</script>
<style lang='less' scoped>
.label-value-box {
    font-size: 14px;

    &-item {
        display: flex;
        align-items: flex-start;
        margin-bottom: 16px;

        span {
            display: block;
            margin-right: 16px;
            min-width: 56px;
            flex-shrink: 0;
        }

        .label {
            color: #63656F;
            width: 98px;
        }
    }

    :nth-last-child(1) {
        margin-bottom: 0;
    }

    &-text {}
}</style>
