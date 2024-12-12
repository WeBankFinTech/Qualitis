<template>
    <div class="query-container">
        <div class="query-item">
            <span class="query-label">{{$t('common.queryCondition')}}</span>
            <FSelect v-model="queryCondition.value" :placeholder="$t('common.pleaseSelect')" :width="160" filterable clearable valueField="value" labelField="label" :options="queryConditionsList" @change="resetAdvanceSearch">
            </FSelect>
        </div>
        <div v-show="queryCondition.value === 'project'" class="query-item">
            <span class="query-label">{{$t('common.project')}}</span>
            <FSelect v-model="queryData.projectName" :placeholder="$t('common.pleaseSelect')" :options="projectsList" filterable clearable @change="resetAdvanceSearch"></FSelect>
        </div>
        <div v-show="queryCondition.value === 'dataSource'" class="query-item">
            <span class="query-label">{{$t('common.cluster')}}</span>
            <FSelect v-model="queryData.dataSource" :placeholder="$t('common.pleaseSelect')" clearable class="query-value" filterable valueField="cluster_name" labelField="cluster_name" :options="dataSourceList" @change="changeOutsideDataSource">
            </FSelect>
        </div>
        <div v-show="queryCondition.value === 'dataSource'" class="query-item">
            <span class="query-label">{{$t('common.databaseList')}}</span>
            <FSelect v-model="queryData.dataBase" :placeholder="$t('common.pleaseSelect')" clearable class="query-value" filterable valueField="database_name" labelField="database_name" :options="dataBaseList" @change="changeOutsideDataBase">
            </FSelect>
        </div>
        <div v-show="queryCondition.value === 'dataSource'" class="query-item">
            <span class="query-label">{{$t('common.tableLibst')}}</span>
            <FSelect v-model="queryData.dataTable" :placeholder="$t('common.pleaseSelect')" clearable class="query-value" filterable valueField="table_name" labelField="table_name" :options="dataTableList" @change="resetAdvanceSearch">
            </FSelect>
        </div>
        <div v-show="queryCondition.value === 'taskStatus'" class="query-item">
            <span class="query-label">{{$t('taskQuery.status')}}</span>
            <FSelect v-model="queryData.taskStatus" :placeholder="$t('common.pleaseSelect')" filterable valueField="value" labelField="label" :options="taskStatusList" @change="resetAdvanceSearch">
            </FSelect>
        </div>
        <div v-show="queryCondition.value === 'taskNumber'" class="query-item">
            <span class="query-label">{{$t('common.number')}}</span>
            <FInput
                ref="taskNumberRef"
                v-model="queryData.taskNumber"
                :placeholder="$t('common.pleaseInput')"
                class="query-input"
                @change="resetAdvanceSearch"
            />
        </div>
        <div v-show="queryCondition.value === 'exceptionRemarks'" class="query-item">
            <span class="query-label">{{$t('taskQuery.exceptionRemark')}}</span>
            <FSelect v-model="queryData.exceptionRemarks" :placeholder="$t('common.pleaseSelect')" filterable valueField="value" labelField="label" :options="exceptionRemarksList" @change="resetAdvanceSearch">
            </FSelect>
        </div>
        <FButton type="primary" class="operation-item white" @click="search">{{$t('common.query')}}</FButton>
        <FButton :class="advanceQuerySelectedCount > 0 ? 'operation-item selected-count' : 'operation-item'" @click="clickAdvanceQuery">{{$t('common.advanceSearch')}}{{advanceQuerySelectedCount > 0 ? `（${$t('common.hasSelect', { count: advanceQuerySelectedCount })}）` : ''}}</FButton>
        <FButton class="operation-item" @click="reset">{{$t('common.reset')}}</FButton>

        <!-- 高级筛选弹框 -->
        <FModal
            :show="showAdvanceSearchModal"
            :width="572"
            :title="$t('taskQuery.advanceSearch')"
            :okText="$t('taskQuery.search')"
            :cancelText="$t('common.cancel')"
            :maskClosable="false"
            @ok="clickAdvanceSearch"
            @cancel="cancelAdvanceSearch"
        >
            <FForm
                ref="advanceQueryForm"
                :label-width="70"
                labelPosition="right"
                class="task-form"
                :model="advanceQueryData"
            >
                <FFormItem :label="$t('common.number')" prop="application_id">
                    <FInput v-model="advanceQueryData.taskNumber" clearable :placeholder="$t('common.pleaseEnter')" @change="resetCommonSearch" />
                </FFormItem>
                <FFormItem :label="$t('common.projectName')" prop="projectName">
                    <FSelect v-model="advanceQueryData.projectName"
                             :placeholder="$t('common.pleaseSelect')"
                             :options="projectsList"
                             filterable
                             clearable
                             @change="getRuleGroupList"
                    ></FSelect>
                </FFormItem>
                <FFormItem :label="$t('taskQuery.status')" prop="taskStatus">
                    <FSelect v-model="advanceQueryData.taskStatus" :placeholder="$t('common.pleaseSelect')" clearable filterable valueField="value" labelField="label" :options="taskStatusList" @change="resetCommonSearch">
                    </FSelect>
                </FFormItem>
                <FFormItem
                    v-show="advanceQueryData.taskStatus && remarkListForAdvanceQuery.length > 0"
                    prop="comment_type"
                    :label="$t('taskQuery.exceptionRemark')"
                >
                    <FSelect v-model="advanceQueryData.comment_type"
                             :placeholder="$t('common.pleaseSelect')"
                             clearable
                             filterable
                             valueField="value"
                             labelField="label"
                             :options="remarkListForAdvanceQuery"
                             @change="resetCommonSearch"
                    >
                    </FSelect>
                </FFormItem>
                <FFormItem :label="$t('taskQuery.groupRuleName')" props="rule_group_id">
                    <FSelect v-model="advanceQueryData.rule_group_id"
                             :placeholder="$t('common.pleaseSelect')"
                             filterable
                             clearable
                             valueField="rule_group_id"
                             labelField="rule_group_name"
                             :options="ruleGroupList"
                             @change="resetCommonSearch"
                    >
                    </FSelect>
                </FFormItem>
                <FFormItem :label="$t('common.cluster')" props="cluster_name">
                    <FSelect v-model="advanceQueryData.dataSource"
                             :placeholder="$t('common.pleaseSelect')"
                             filterable
                             clearable
                             valueField="cluster_name"
                             labelField="cluster_name"
                             :options="dataSourceList"
                             @change="changeAdvanceQueryDataSource"
                    >
                    </FSelect>
                </FFormItem>
                <FFormItem :label="$t('common.databaseList')" props="database_name">
                    <FSelect v-model="advanceQueryData.dataBase"
                             :placeholder="$t('common.pleaseSelect')"
                             clearable
                             filterable
                             valueField="database_name"
                             labelField="database_name"
                             :options="advanceQueryDataBaseList"
                             @change="changeAdvanceQueryDataBase"
                    >
                    </FSelect>
                </FFormItem>
                <FFormItem :label="$t('common.tableLibst')" props="table">
                    <FSelect v-model="advanceQueryData.dataTable" :placeholder="$t('common.pleaseSelect')" clearable filterable valueField="table_name" labelField="table_name" :options="advanceQueryDataTableList" @change="resetCommonSearch">
                    </FSelect>
                </FFormItem>
                <FFormItem :label="$t('common.runUser')" props="execute_user">
                    <FSelect v-model="advanceQueryData.execute_user"
                             :placeholder="$t('common.pleaseSelect')"
                             clearable
                             filterable
                             :options="executeUserList"
                             @change="resetCommonSearch"
                    >
                    </FSelect>
                </FFormItem>
                <FFormItem :label="$t('common.timeRange')" prop="timeRangeOfAdvanceQuery">
                    <FDatePicker
                        v-model="timeRangeOfAdvanceQuery"
                        type="datetimerange"
                        clearable
                        format="yyyy-MM-dd HH:mm:ss"
                        :placeholder="$t('common.pleaseSelect')"
                        @change="resetCommonSearch"
                    />
                </FFormItem>
            </FForm>
        </FModal>
    </div>
</template>
<script setup>
import {
    defineProps, ref, watch, defineExpose, defineEmits,
} from 'vue';

import {
    useI18n,
} from '@fesjs/fes';
import { format } from 'date-fns';

const { t: $t } = useI18n();
const emit = defineEmits(['resetCommonSearch', 'resetAdvanceSearch']);
const props = defineProps({
    queryCondition: {
        type: Object,
        required: true,
    },
    queryData: {
        type: Object,
        required: true,
    },
    projectsList: {
        type: Array,
        required: true,
    },
    dataSourceList: {
        type: Array,
        required: true,
    },
    changeOutsideDataSource: {
        type: Function,
        required: true,
    },
    dataBaseList: {
        type: Array,
        required: true,
    },
    changeOutsideDataBase: {
        type: Function,
        required: true,
    },
    dataTableList: {
        type: Array,
        required: true,
    },
    taskStatusList: {
        type: Array,
        required: true,
    },
    exceptionRemarksList: {
        type: Array,
        required: true,
    },
    search: {
        type: Function,
        required: true,
    },
    advanceQuerySelectedCount: {
        type: Number,
        required: true,
    },
    clickAdvanceQuery: {
        type: Function,
        required: true,
    },
    reset: {
        type: Function,
        required: true,
    },
    showAdvanceSearchModal: {
        type: Boolean,
        required: true,
    },
    advanceSearch: {
        type: Function,
        required: true,
    },
    cancelAdvanceSearch: {
        type: Function,
        required: true,
    },
    advanceQueryData: {
        type: Object,
        required: true,
    },
    remarkListForAdvanceQuery: {
        type: Array,
        required: true,
    },
    changeAdvanceQueryDataSource: {
        type: Function,
        required: true,
    },
    advanceQueryDataBaseList: {
        type: Array,
        required: true,
    },
    changeAdvanceQueryDataBase: {
        type: Function,
        required: true,
    },
    advanceQueryDataTableList: {
        type: Array,
        required: true,
    },
    ruleGroupList: {
        type: Array,
        required: true,
    },
    getRuleGroupList: {
        type: Function,
        required: true,
    },
    executeUserList: {
        type: Array,
        required: true,
    },
});

// 各查询条件
const queryConditionsList = ref([
    {
        value: 'project',
        label: $t('common.project'),
    },
    {
        value: 'dataSource',
        label: $t('common.dataSource'),
    },
    {
        value: 'taskStatus',
        label: $t('taskQuery.status'),
    },
    {
        value: 'taskNumber',
        label: $t('common.number'),
    },
    {
        value: 'exceptionRemarks',
        label: $t('taskQuery.exceptionRemark'),
    },
]);

const advanceQueryForm = ref();

const clickAdvanceSearch = () => {
    advanceQueryForm.value.validate().then((result) => {
        props.advanceSearch();
    }).catch((error) => {
        console.log('表单验证失败: ', error);
    });
};

const timeRangeOfAdvanceQuery = ref([]);
defineExpose({ timeRangeOfAdvanceQuery });

// 高级筛选弹框里的起止时间的watch
watch(timeRangeOfAdvanceQuery, (value) => {
    if (Array.isArray(value)) {
        if (value.length === 0) {
            props.advanceQueryData.start_time = '';
            props.advanceQueryData.end_time = '';
        } else if (value.length === 2) {
            props.advanceQueryData.start_time = format(new Date(value[0]), 'yyyy-MM-dd HH:mm:ss');
            props.advanceQueryData.end_time = format(new Date(value[1]), 'yyyy-MM-dd HH:mm:ss');
        }
    } else {
        props.advanceQueryData.start_time = '';
        props.advanceQueryData.end_time = '';
    }
}, { immediate: true, deep: true });
// 外层查询变化重置高级查询
const resetAdvanceSearch = () => {
    emit('resetAdvanceSearch');
};
// 高级查询重置外层查询
const resetCommonSearch = () => {
    emit('resetCommonSearch');
};
</script>
<style lang="less" scoped>
.query-container {
    display: flex;
    flex-wrap: wrap;
    .query-item,
    .operation-item {
        margin-right: 22px;
        margin-bottom: 22px;
        font-family: PingFangSC-Regular;
        font-size: 14px;
        color: #0F1222;
        letter-spacing: 0;
        line-height: 22px;
        font-weight: 400;
        &.white {
            color: #FFFFFF;
        }
    }
    .selected-count {
        background: rgba(83,132,255,0.06);
        border: 1px solid #5384FF;
        color: #5384FF;
    }
    .query-input {
        width: 160px;
    }
    .query-label {
        margin-right: 16px;
        font-family: PingFangSC-Regular;
        font-size: 14px;
        color: #0F1222;
    }
    .fes-select {
        width: 160px;
    }
}

</style>
