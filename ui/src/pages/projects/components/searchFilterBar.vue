<template>
    <div class="query-container">
        <div class="query-item">
            <span class="query-item-label">{{$t('common.projectName')}}</span>
            <FInput
                ref="taskNumberRef"
                v-model="queryData.project_name"
                placeholder="请输入项目名称"
                class="query-input"
                clearable
            />
        </div>
        <div class="query-item">
            <span class="query-item-label">{{$t('common.founder')}}</span>
            <FSelect v-model="queryData.create_user" class="query-input" clearable filterable>
                <FOption v-for="v in projectUserList" :key="v" :value="v" :label="v"></FOption>
            </FSelect>
        </div>
        <FButton type="primary" class="operation-item white" @click="search">{{$t('common.query')}}</FButton>
        <FButton
            :class="advanceQuerySelectedCount > 0 ? 'operation-item selected-count' : 'operation-item'"
            @click="clickAdvanceQuery"
        >
            {{$t('common.advancedFilter')}}{{advanceQuerySelectedCount > 0 ? `（已选${advanceQuerySelectedCount}项）` : ''}}
        </FButton>
        <FButton class="operation-item" @click="reset">{{$t('common.reset')}}</FButton>

        <!-- 高级筛选弹框 -->
        <FModal
            :show="showAdvanceSearchModal"
            :width="572"
            :title="$t('taskQuery.advanceSearch')"
            :okText="$t('common.ok')"
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
                <FFormItem :label="$t('common.projectName')" prop="projectName">
                    <FInput
                        v-model="advanceQueryData.project_name"
                        placeholder="请输入项目名称"
                        class="query-input"
                        clearable
                    />
                </FFormItem>
                <FFormItem :label="$t('common.founder')" prop="create_user">
                    <FSelect v-model="advanceQueryData.create_user" clearable filterable>
                        <FOption v-for="v in projectUserList" :key="v" :value="v" :label="v"></FOption>
                    </FSelect>
                </FFormItem>
                <FFormItem :label="$t('dataSourceManagement.subSystem')" prop="subsystem_name">
                    <FInput
                        v-model="advanceQueryData.subsystem_name"
                        clearable
                        :placehodler="$t('common.pleaseEnter')"
                    />
                </FFormItem>
                <FFormItem :label="$t('common.databaseList')" prop="db_name">
                    <FSelect
                        v-model="advanceQueryData.db_name"
                        clearable
                        filterable
                        valueField="database_name"
                        labelField="database_name"
                        :options="dataBaseList"
                        @change="changeDataTableList"
                    ></FSelect>
                </FFormItem>
                <FFormItem :label="$t('common.tableLibst')" prop="table_name">
                    <FSelect
                        v-model="advanceQueryData.table_name"
                        clearable
                        filterable
                        labelField="table_name"
                        valueField="table_name"
                        :options="dataTableList"
                    ></FSelect>
                </FFormItem>
                <FFormItem :label="$t('common.createTime')" prop="timeRangeOfAdvanceQuery">
                    <FDatePicker
                        v-model="timeRangeOfAdvanceQuery"
                        type="datetimerange"
                        clearable
                        format="yyyy-MM-dd HH:mm:ss"
                        :placeholder="$t('common.pleaseSelect')"
                    />
                </FFormItem>
            </FForm>
        </FModal>
    </div>
</template>
<script setup>
import {
    reactive, ref, watch, defineProps, onMounted, onUnmounted,
} from 'vue';
import {
    useI18n, useModel, request, useRouter,
} from '@fesjs/fes';
import eventbus from '@/common/useEvents';
import useProjectAuth from '@/pages/projects/hooks/useProjectAuth';
import useDataSource from '../hooks/useDataSource';

const { t: $t } = useI18n();

const props = defineProps({
    workSpace: {
        type: String,
        required: true,
    },
    subSystemList: {
        type: Array,
        required: true,
    },
});

// 获取数据源相关数据
const {
    dataBaseList,
    dataTableList,
} = useDataSource(['dataBaseList']);

// 定义高级筛选参数
const initAdvanceQueryData = () => ({
    project_name: '',
    create_user: '',
    subsystem_id: '',
    db_name: '',
    table_name: '',
    start_time: '',
    end_time: '',
});
const advanceQueryData = reactive(initAdvanceQueryData());

// 查询参数
const initData = () => ({
    project_name: '',
    create_user: '',
});
const queryData = reactive(initData());
const queryDataCopy = ref({});

// 高级筛选弹框里的起止时间的watch
const timeRangeOfAdvanceQuery = ref([]);

watch(timeRangeOfAdvanceQuery, (value) => {
    const preData = Array.isArray(value) ? value : [];
    const [startTime, endTime] = preData;
    if (startTime && endTime) {
        advanceQueryData.start_time = parseInt(new Date(startTime) / 1000);
        advanceQueryData.end_time = parseInt(new Date(endTime) / 1000);
    } else {
        advanceQueryData.start_time = '';
        advanceQueryData.end_time = '';
    }
}, { immediate: true });


// 筛选条件数量的watch,显示放到高级筛选里面
const advanceQuerySelectedCount = ref(0);

watch(advanceQueryData, () => {
    const { start_time: startTime, end_time: endTime, ...args } = advanceQueryData;
    advanceQuerySelectedCount.value = Object.values(args).filter(v => !!v).length;
    if (startTime && endTime) {
        advanceQuerySelectedCount.value++;
    }
}, { immediate: true });

// 弹窗相关操作
const advanceQueryForm = ref();
const showAdvanceSearchModal = ref(false);

// 选择前的数据备份
const advanceQueryDataCopy = ref(initAdvanceQueryData());
const timeRangeOfAdvanceQueryCopy = ref([]);

const clickAdvanceQuery = () => {
    // 顶部查询栏数据带入高级筛选查询栏
    if (queryData.project_name) advanceQueryData.project_name = queryData.project_name;
    if (queryData.create_user) advanceQueryData.create_user = queryData.create_user;
    showAdvanceSearchModal.value = true;
};

const clickAdvanceSearch = () => {
    advanceQueryDataCopy.value = { ...advanceQueryData };
    timeRangeOfAdvanceQueryCopy.value = [...timeRangeOfAdvanceQuery.value];
    eventbus.emit('project:searchfilter', {
        searchType: 'filter',
        ...advanceQueryData,
    });
    // 重置顶部查询框数据，避免混淆
    Object.assign(queryData, initData());
    showAdvanceSearchModal.value = false;
};

const cancelAdvanceSearch = () => {
    Object.assign(advanceQueryData, advanceQueryDataCopy.value);
    timeRangeOfAdvanceQuery.value = [...timeRangeOfAdvanceQueryCopy.value];
    showAdvanceSearchModal.value = false;
};

const search = () => {
    eventbus.emit('project:searchfilter', {
        searchType: 'normal',
        ...queryData,
    });
    queryDataCopy.value = queryData;
    // 重置高级筛选框数据，避免混淆
    Object.assign(advanceQueryData, initAdvanceQueryData());
    advanceQueryDataCopy.value = { ...advanceQueryData };
    timeRangeOfAdvanceQuery.value = [];
    timeRangeOfAdvanceQueryCopy.value = [];
};

// 重置
const reset = () => {
    Object.assign(advanceQueryData, initAdvanceQueryData());
    Object.assign(queryData, initData());
    advanceQueryDataCopy.value = { ...advanceQueryData };
    timeRangeOfAdvanceQuery.value = [];
    timeRangeOfAdvanceQueryCopy.value = [];
    eventbus.emit('project:searchfilter', {
        searchType: 'reset',
        ...queryData,
        ...advanceQueryData,
    });
    sessionStorage.removeItem(props.workSpace);
};

// 获取用户列表
const {
    projectUserList,
    getProjectUserList,
} = useProjectAuth();

onMounted(() => {
    const storage = sessionStorage.getItem(props.workSpace);
    if (storage) {
        const {
            _queryData,
            _advanceQueryData,
            _timeRangeOfAdvanceQuery,
        } = JSON.parse(storage);
        Object.assign(queryData, _queryData);
        Object.assign(advanceQueryData, _advanceQueryData);
        advanceQueryDataCopy.value = _advanceQueryData;
        timeRangeOfAdvanceQuery.value = _timeRangeOfAdvanceQuery;
        timeRangeOfAdvanceQueryCopy.value = _timeRangeOfAdvanceQuery;
    }
    getProjectUserList();
});

onUnmounted(() => {
    const isAdvance = Object.values(advanceQueryDataCopy.value).filter(v => !!v).length > 0;
    const _queryCondition = isAdvance ? advanceQueryDataCopy.value : queryData;
    const storage = {
        _queryData: queryDataCopy.value,
        _advanceQueryData: advanceQueryDataCopy.value,
        _timeRangeOfAdvanceQuery: timeRangeOfAdvanceQueryCopy.value,
        _queryCondition,
    };
    sessionStorage.setItem(props.workSpace, JSON.stringify(storage));
});


// 通过选中数据库来显示数据表
const changeDataTableList = (val) => {
    const target = dataBaseList.value.find(v => v.database_name === val) || {};
    dataTableList.value = target.table || [];
    if (!val) advanceQueryData.table_name = '';
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
        color: #0f1222;
        letter-spacing: 0;
        line-height: 22px;
        font-weight: 400;
        &.white {
            color: #ffffff;
        }
        &-label {
            margin-right: 16px;
            font-family: PingFangSC-Regular;
            font-size: 14px;
            color: #0f1222;
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
}
</style>
