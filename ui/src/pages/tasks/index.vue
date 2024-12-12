<template>
    <div style="padding: 16px 16px 32px" class="dashboard">
        <BTablePage :isLoading="isLoading" actionType="loading" :loadingText="{ loading: '' }">
            <template v-slot:search>
                <firstActionBar
                    ref="firstActionBarRef"
                    :queryCondition="queryCondition"
                    :queryData="queryData"
                    :projectsList="projectsList"
                    :dataSourceList="dataSourceList"
                    :changeOutsideDataSource="changeOutsideDataSource"
                    :dataBaseList="dataBaseList"
                    :changeOutsideDataBase="changeOutsideDataBase"
                    :dataTableList="dataTableList"
                    :taskStatusList="taskStatusList"
                    :exceptionRemarksList="exceptionRemarksList"
                    :search="search"
                    :advanceQuerySelectedCount="advanceQuerySelectedCount"
                    :clickAdvanceQuery="clickAdvanceQuery"
                    :reset="reset"
                    :showAdvanceSearchModal="showAdvanceSearchModal"
                    :advanceSearch="advanceSearch"
                    :cancelAdvanceSearch="cancelAdvanceSearch"
                    :advanceQueryData="advanceQueryData"
                    :remarkListForAdvanceQuery="remarkListForAdvanceQuery"
                    :changeAdvanceQueryDataSource="changeAdvanceQueryDataSource"
                    :advanceQueryDataBaseList="advanceQueryDataBaseList"
                    :changeAdvanceQueryDataBase="changeAdvanceQueryDataBase"
                    :advanceQueryDataTableList="advanceQueryDataTableList"
                    :ruleGroupList="ruleGroupList"
                    :getRuleGroupList="getRuleGroupList"
                    :executeUserList="executeUserList"
                    @resetCommonSearch="resetQueryData"
                    @resetAdvanceSearch="resetAdvanceQueryData"
                ></firstActionBar>
            </template>
            <template v-slot:operate>
                <secondActionBar ref="secondActionBarRef" :exportStatus="exportStatus" :clickbatchReExecution="clickbatchReExecution" :clickMore="clickMore" :confirmSelectItems="confirmSelectItems" :dataSourceList="dataSourceList" :chooseDataSource="chooseDataSource" :chooseDataBase="chooseDataBase" :dataQualityDataBaseList="dataQualityDataBaseList" :dataQualityDataTableList="dataQualityDataTableList" :resetOperation="resetOperation"></secondActionBar>
            </template>
            <template v-slot:table>
                <f-table ref="taskTable" rowKey="application_id" :data="tasksData" :no-data-text="$t('common.noData')" @cellClick="clickTableCell" @selectionChange="tableSelectionChange">
                    <template #empty>
                        <BPageLoading :loadingText="loadingText" :actionType="resultByInit ? 'emptyInitResult' : 'emptyQueryResult'" />
                    </template>
                    <f-table-column :visible="exportStatus !== 'no'" type="selection" :width="32" :selectable="selectable"></f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" :visible="checkTColShow('application_id')" prop="application_id" :label="$t('common.number')" align="left" classes="idlink" :width="164" ellipsis></f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" :visible="checkTColShow('project_name')" prop="project_name" :label="$t('common.projectName')" align="left" :width="160" ellipsis></f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" :visible="checkTColShow('task_group_name')" prop="task_group_name" :label="$t('taskQuery.groupRuleName')" align="left" :width="160" ellipsis></f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" :visible="checkTColShow('table_name')" prop="application_rule_datasource" :label="$t('common.tableLibst')" align="left" :width="140" ellipsis></f-table-column>
                    <f-table-column v-slot="{ row }" :visible="checkTColShow('schedule_project_name')" prop="schedule_project_name" :label="$t('common.relationWorkflow')" align="left" :width="200" ellipsis>{{row.schedule_project_name && row.schedule_workflow_name ? row.schedule_project_name + '/' + row.schedule_workflow_name : '--'}}</f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" :visible="checkTColShow('start_time')" prop="start_time" :label="$t('taskQuery.submissionTime')" align="left" :width="184" ellipsis></f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" :visible="checkTColShow('end_time')" prop="end_time" :label="$t('taskQuery.endTime')" align="left" :width="184" ellipsis></f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" :visible="checkTColShow('partition')" prop="partition" :label="$t('taskQuery.partition')" align="left" :width="184" ellipsis></f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" :visible="checkTColShow('execute_user')" prop="execute_user" :label="$t('common.runUser')" align="left" :width="80" ellipsis></f-table-column>
                    <f-table-column v-slot="{ row }" :visible="checkTColShow('status')" prop="status" :label="$t('taskQuery.status')" align="left" :width="130" ellipsis><div :style="{ color: tempStatusList[row.status] ? tempStatusList[row.status].color : '' }">{{tempStatusList[row.status] ? tempStatusList[row.status].label : row.status}}</div></f-table-column>
                    <f-table-column :visible="checkTColShow('comment')" prop="comment" :label="$t('taskQuery.remark')" :formatter="commentFormatter" align="left" :width="140" ellipsis></f-table-column>
                    <f-table-column :visible="checkTColShow('invoke_type')" prop="invoke_type" :label="$t('taskQuery.scheduling')" align="left" :formatter="formatInvoke" :width="88" ellipsis></f-table-column>
                    <f-table-column #default="{ row } = {}" :label="$t('common.operate')" align="center" fixed="right" :width="60">
                        <FDropdown :options="getTableMoreOptions(row)" trigger="focus" @click="clickTableMore($event, row)">
                            <FButton class="table-operation-item">
                                <template #icon> <MoreCircleOutlined /> </template>
                            </FButton>
                        </FDropdown>
                    </f-table-column>
                </f-table>
            </template>
            <template v-slot:pagination>
                <FPagination
                    v-model:pageSize="tasksPagination.size"
                    v-model:currentPage="tasksPagination.current"
                    show-size-changer
                    show-quick-jumper
                    show-total
                    :total-count="tasksTotal"
                    class="pagination"
                    @change="tasksPageChange"
                    @pageSizeChange="tasksPageChange"
                ></FPagination>
            </template>
        </BTablePage>
        <!-- 日志详情 -->
        <logDrawer ref="logDrawerRef" :tasksPagination="tasksPagination" :trData="trData" :taskId="taskId"></logDrawer>

        <!-- 状态详情 -->
        <statusDrawer ref="statusDrawerRef" :tasksPagination="tasksPagination" :trData="trData" :taskId="taskId"></statusDrawer>

        <!-- 表格设置弹窗 -->
        <BTableHeaderConfig
            v-model:headers="projectTableHeaders"
            v-model:show="showProjectTableColConfig"
            :originHeaders="originProjectHeaders"
            type="task_list" />

        <!-- 任务执行 -->
        <ExecRulePanel
            v-model:show="showExecutation"
            v-model="executationConfig"
            :list="executationRules"
            @ok="clickExecutationOk" />
    </div>
</template>
<script setup>
import {
    onMounted, computed, watch, ref,
} from 'vue';
import { FMessage } from '@fesjs/fes-design';

import {
    useI18n, useRouter, useRoute, useModel,
} from '@fesjs/fes';
import { format } from 'date-fns';
import {
    MoreCircleOutlined,
    DownOutlined,
    UpOutlined,
} from '@fesjs/fes-design/es/icon';
import {
    BTableHeaderConfig, BPageLoading, BTablePage, BSearch,
} from '@fesjs/traction-widget';
import ExecRulePanel from '@/components/ExecutionConfig/ExecRulePanel';
import { getURLQueryParams, formatterEmptyValue } from '@/common/utils';
import { cloneDeep } from 'lodash-es';
import useTableHeaderConfig from '@/hooks/useTableHeaderConfig';
import useExecutation from '../projects/hooks/useExecutation';

import firstActionBar from './components/firstActionBar';
import secondActionBar from './components/secondActionBar';
import logDrawer from './components/logDrawer';
import statusDrawer from './components/statusDrawer';

import {
    fetchTasksData, fetchProjects, fetchSearchData, stopBatch, fetchOptions, fetchRuleGroupList, fetchExecuteUserList,
} from './api';


const { t: $t } = useI18n();

const loadingText = {
    emptyInitResult: $t('common.emptyInitResult'),
    emptyQueryResult: $t('common.emptyQueryResult'),
};

const router = useRouter();
const route = useRoute();

// form和modal
const taskNumberRef = ref();
const taskTable = ref();
const showAdvanceSearchModal = ref(false);

const queryCondition = ref({
    value: '',
});
const projectsList = ref([]);
// 三个地方的dataSouce是同一个，然后每个地方分别根据选择的不同的dataSource来获取不同的dataBase和dataTable
const dataSourceList = ref([]);
const dataBaseList = ref([]);
const dataTableList = ref([]);
const advanceQueryDataBaseList = ref([]);
const advanceQueryDataTableList = ref([]);
const dataQualityDataBaseList = ref([]);
const dataQualityDataTableList = ref([]);
const taskStatusList = ref([
    {
        value: '1',
        label: '已提交执行器',
    },
    {
        value: '3',
        label: '运行中',
    },
    {
        value: '4',
        label: '通过校验',
    },
    {
        value: '7',
        label: '失败',
    },
    {
        value: '8',
        label: '未通过校验',
    },
    {
        value: '9',
        label: '任务初始化失败',
    },
    {
        value: '10',
        label: '任务初始化成功',
    },
    {
        value: '11',
        label: '参数错误',
    },
    {
        value: '12',
        label: '提交阻塞',
    },
]);
const taskNumberValue = ref('');
const exceptionRemarksList = ref([
    {
        value: '7-1',
        label: '数据被修改或者权限问题',
    },
    {
        value: '7-2',
        label: '队列权限问题',
    },
    {
        value: '7-3',
        label: '内存不足问题',
    },
    {
        value: '7-4',
        label: '校验语法问题',
    },
    {
        value: '7-5',
        label: '请求引擎失败',
    },
    {
        value: '7-6',
        label: '未知错误',
    },
    {
        value: '8-7',
        label: '数据不符合校验规则',
    },
    {
        value: '8-8',
        label: '左表存在为空的表或者分区',
    },
    {
        value: '4-9',
        label: '数据符合校验规则',
    },
    {
        value: '4-10',
        label: '两个表都为空',
    },
    {
        value: '9-11',
        label: '元数据信息接口异常，可能是数据不存在导致接口请求失败',
    },
    {
        value: '7-12',
        label: '任务被取消',
    },
    {
        value: '8-13',
        label: '右表存在为空的表或者分区',
    },
    {
        value: '9-14',
        label: '敏感表无法操作',
    },
]);

// 普通查询的数据
const queryData = ref({});
// 高级筛选的数据
const advanceQueryData = ref({
    // 任务编号
    taskNumber: '',
    // 项目名称
    projectName: '',
    // 任务状态
    taskStatus: '',
    // 数据源
    dataSource: '',
    // 数据库
    dataBase: '',
    // 数据表
    dataTable: '',
    // 执行用户
    execute_user: '',
    // 起止时间
    start_time: '',
    end_time: '',
    // 规则组id
    rule_group_id: '',
});


// 任务执行相关
const {
    showExecutation,
    executationConfig,
    executationRules,
    executeTaskPreInTaskQuery,
    executeTaskInTaskQuery,
} = useExecutation();

// 重置外面的查询框
const resetQueryData = () => {
    queryData.value = {
        // 项目
        projectName: '',
        // 数据源
        dataSource: '',
        // 数据库
        dataBase: '',
        // 数据表
        dataTable: '',
        // 任务状态
        taskStatus: '',
        // 任务编号
        taskNumber: '',
        // 异常状态
        exceptionRemarks: '',
    };
};

const firstActionBarRef = ref();

// 重置高级筛选查询框
const resetAdvanceQueryData = () => {
    advanceQueryData.value = {
        // 任务编号
        taskNumber: '',
        // 项目名称
        projectName: '',
        // 任务状态
        taskStatus: '',
        // 数据源
        dataSource: '',
        // 数据库
        dataBase: '',
        // 数据表
        dataTable: '',
        // 起止时间
        start_time: '',
        end_time: '',
        // 规则组id
        rule_group_id: '',
    };
    console.log('firstActionBarRef.value: ', firstActionBarRef.value);
};

// 设置初始的查询空状态
const setInitQueryData = () => {
    queryCondition.value.value = '';

    resetQueryData();

    resetAdvanceQueryData();
};

setInitQueryData();
const isLoading = ref(false);

// 获取项目列表
const getProjects = async () => {
    const params = {
        size: 100,
        page: 0,
    };
    try {
        isLoading.value = true;
        const { data } = await fetchProjects(params);
        isLoading.value = false;
        projectsList.value = (Array.isArray(data) ? data : []).map(item => ({
            label: item.project_name || item.project_id,
            value: item.project_id,
        }));
    } catch (error) {
        isLoading.value = false;
        console.log('error: ', error);
    }
};

// 所有数据库
const allDataBases = ref([]);
// 所有数据表
const allTables = ref([]);
// 所有集群下的库表
const clusterTables = ref([]);

// 获取数据源列表
const getDataSourceList = async () => {
    try {
        isLoading.value = true;
        const {
            clusters, tables, dbs, cluster_dbs: clusterDbs, db_tables: dbTables, cluster_tables: _clusterTables,
        } = await fetchOptions();
        isLoading.value = false;
        console.time('计算时间');
        // 转化所有数据库下表为对象数组[{database_name: [{table_name: m}]}]
        Object.keys(dbTables).forEach((v) => {
            dbTables[v] = dbTables[v].map(m => ({ table_name: m }));
        });
        // 转化所有集群下表为对象数组[{cluster_name: [{table_name: m}]}]
        const tempClusterTables = {};
        Object.keys(_clusterTables).forEach((v) => {
            const tempMap = new Map();
            const tempArr = [];
            _clusterTables[v].forEach((m) => {
                tempMap.set(m, m);
                tempArr.push({ table_name: m });
            });
            tempClusterTables[v] = tempMap;
            _clusterTables[v] = tempArr;
        });
        // 转化所有集群下库为对象数组 [{cluster_name: [{database_name: m, table: dbTables[m]}]}]
        Object.keys(clusterDbs).forEach((v) => {
            clusterDbs[v] = clusterDbs[v].map(m => ({
                database_name: m,
                table: dbTables[m].filter(n => tempClusterTables[v].get(n.table_name)),
            }));
        });
        console.timeEnd('计算时间');
        dataSourceList.value = clusters.filter(v => !!v && v !== 'null').map(n => ({ cluster_name: n, database: clusterDbs[n] }));
        allDataBases.value = dbs.filter(v => !!v && v !== 'null').map(m => ({ database_name: m, table: dbTables[m] }));
        allTables.value = tables.filter(v => !!v && v !== 'null').map(m => ({ table_name: m }));
        dataBaseList.value = allDataBases.value;
        dataTableList.value = allTables.value;
        clusterTables.value = _clusterTables;
    } catch (error) {
        console.log('error: ', error);
        isLoading.value = false;
    }
};


// 选择数据源
const chooseDataSource = (value, type) => {
    const tempDataSource = dataSourceList.value.find(item => item.cluster_name === value);
    switch (type) {
        case 'outside':
            dataBaseList.value = tempDataSource ? tempDataSource.database : allDataBases.value;
            dataTableList.value = tempDataSource ? clusterTables.value[value] : allTables.value;
            break;
        case 'advanceQuery':
            advanceQueryDataBaseList.value = tempDataSource ? tempDataSource.database : [];
            break;
        case 'dataQuality':
            dataQualityDataBaseList.value = tempDataSource ? tempDataSource.database : [];
            break;
        default:
            break;
    }
};

const changeOutsideDataSource = (value) => {
    resetAdvanceQueryData();
    chooseDataSource(value, 'outside');
};

const changeAdvanceQueryDataSource = (value) => {
    resetQueryData();
    chooseDataSource(value, 'advanceQuery');
};


// 选择数据库
const chooseDataBase = (value, type) => {
    let database = [];
    switch (type) {
        case 'outside':
            database = dataBaseList.value.find(item => item.database_name === value);
            dataTableList.value = database
                ? database.table
                : queryData.value.dataSource
                    ? clusterTables.value[queryData.value.dataSource]
                    : allTables.value;
            break;
        case 'advanceQuery':
            database = advanceQueryDataBaseList.value.find(item => item.database_name === value);
            advanceQueryDataTableList.value = database ? database.table : [];
            break;
        case 'dataQuality':
            database = dataQualityDataBaseList.value.find(item => item.database_name === value);
            dataQualityDataTableList.value = database ? database.table : [];
            break;
        default:
            break;
    }
};

const changeOutsideDataBase = (value) => {
    resetAdvanceQueryData();
    queryData.value.dataTable = '';
    chooseDataBase(value, 'outside');
};

const changeAdvanceQueryDataBase = (value) => {
    resetQueryData();
    advanceQueryData.value.dataTable = '';
    chooseDataBase(value, 'advanceQuery');
};

// 高级筛选 里的 异常备注
const remarkListForAdvanceQuery = computed(() => {
    const selectedStatus = advanceQueryData.value.taskStatus;
    return exceptionRemarksList.value.filter(item => selectedStatus !== '' && item.value.startsWith(selectedStatus));
});

// 表格数据
const tasksData = ref([]);
const urlQuery = computed(() => route.query);
const tasksPagination = ref({
    total: 0,
    current: urlQuery.value.page ? +urlQuery.value.page : 1,
    size: urlQuery.value.pageSize ? +urlQuery.value.pageSize : 10,
});
const tasksTotal = ref(0);

// 表格选择某项的事件函数
const tableSelectedItems = ref([]);

const tableSelectionChange = (value) => {
    tableSelectedItems.value = value;
};

const lastQueryParams = ref({}); // 最后一次获取表格数据时的请求参数
const language = localStorage.getItem('currentLanguage');
const getTasksData = async () => {
    const params = {
        current: tasksPagination.value.current,
        size: tasksPagination.value.size,
    };
    try {
        isLoading.value = true;
        const res = await fetchTasksData(params);
        lastQueryParams.value = { page: params.current - 1, size: params.size };
        isLoading.value = false;
        tasksTotal.value = res.total_num;
        tasksPagination.value.total = Math.ceil(res.total_num / tasksPagination.value.size);
        tasksData.value = res.data;
        tasksData.value = tasksData.value.map(item => Object.assign({}, item, { status: String(item.status), comment: language === 'zh-CN' ? item.zhMessage : item.enMessage }));
    } catch (error) {
        console.log('error: ', error);
        isLoading.value = false;
    }
};

// tasksData = {
//     tasks: [{
//         application_id: '1',
//         project_name: '2',
//         task_group_name: '3',
//         table_name: '4',
//         start_time: '6',
//         end_time: '7',
//         status: '8',
//         comment: '9',
//         invoke_type: '10',
//         stopColor: '11',
//     }],
// };


// 表格设置弹框
const originProjectHeaders = [
    { prop: 'application_id', label: $t('common.number') },
    { prop: 'project_name', label: $t('common.projectName') },
    { prop: 'task_group_name', label: $t('common.taskName') },
    { prop: 'table_name', label: $t('common.tableLibst') },
    { prop: 'schedule_project_name', label: $t('common.relationWorkflow') },
    { prop: 'start_time', label: $t('taskQuery.submissionTime') },
    { prop: 'end_time', label: $t('taskQuery.endTime') },
    { prop: 'execute_user', label: $t('common.runUser') },
    { prop: 'status', label: $t('taskQuery.status') },
    { prop: 'comment', label: $t('taskQuery.remark') },
    { prop: 'invoke_type', label: $t('taskQuery.scheduling') },
    { prop: 'partition', label: $t('taskQuery.partition') },
];

// 普通项目表头设置
const {
    checkTColShow,
    toggleTColConfig,
    tableHeaders: projectTableHeaders,
    showTableHeaderConfig: showProjectTableColConfig,
} = useTableHeaderConfig();

// 调用查询接口
const filterAction = async (action, params, batchStopFlag = false) => {
    try {
        isLoading.value = true;
        const { total, data } = await fetchSearchData(action, params);
        lastQueryParams.value = params;
        isLoading.value = false;
        if (!batchStopFlag)FMessage.success($t('toastSuccess.search'));
        tasksTotal.value = total;
        tasksPagination.value.total = Math.ceil(total / tasksPagination.value.size);
        tasksData.value = Array.isArray(data) ? data : [];
        tasksData.value = tasksData.value.map(item => Object.assign({}, item, { status: String(item.status), comment: language === 'zh-CN' ? item.zhMessage : item.enMessage }));
    } catch (error) {
        console.log('error: ', error);
    }
    showAdvanceSearchModal.value = false;
};

const searchMode = ref('');

const resultByInit = ref(true); // 表格的结果是否是初始状态，搜索后该值变为false
// 查询
const search = (current) => {
    let url = '';

    if (!current || typeof current !== 'number') {
        tasksPagination.value.current = 1;
    }
    const params = {
        size: tasksPagination.value.size,
        page: current && typeof current === 'number' ? current - 1 : 0,
    };
    const errs = [];
    // case ‘’ 匹配null 空字符串 undefined这三种状态
    const condition = queryCondition.value.value || 'empty';
    switch (condition) {
        case 'project':
            url = '/api/v1/projector/application/filter/project';
            if (queryData.value.projectName) {
                params.project_id = queryData.value.projectName;
            } else {
                errs.push($t('taskQuery.selectProject0'));
            }
            break;
        case 'dataSource':
            url = '/api/v1/projector/application/filter/datasource';
            if (queryData.value.dataSource) {
                params.cluster_name = queryData.value.dataSource;
            }
            if (queryData.value.dataBase) {
                params.database_name = queryData.value.dataBase;
            }
            if (queryData.value.dataTable) {
                params.table_name = queryData.value.dataTable;
                params.table = queryData.value.dataTable;
            }
            break;
        case 'taskStatus':
            url = '/api/v1/projector/application/filter/status';
            if (typeof queryData.value.taskStatus !== 'undefined' && queryData.value.taskStatus !== '') {
                params.status = parseInt(queryData.value.taskStatus);
                params.selected_status = parseInt(queryData.value.taskStatus);
            }
            // 如果taskStatus的值为数字，才需要下面这些
            // if (queryData.value.taskStatus === '0') {
            //     params.status = '';
            //     params.selected_status = '';
            // } else {
            //     params.status = Number.parseInt(params.status);
            //     params.selected_status = Number.parseInt(params.selected_status);
            // }
            break;
        case 'taskNumber':
            url = '/api/v1/projector/application/filter/application_id';
            if (queryData.value.taskNumber) {
                params.application_id = queryData.value.taskNumber;
            } else {
                errs.push($t('taskQuery.inputTaskNumber'));
            }
            break;
        case 'exceptionRemarks':
            url = '/api/v1/projector/application/filter/status';
            // const data = queryData.value.exceptionRemarks.split('-');
            // if (data.length < 2) {
            //     errs.push($t('taskQuery.searchCriteria'));
            //     break;
            // }
            // params.status = Number.parseInt(data[0]);
            // params.selected_status = Number.parseInt(data[0]);
            // eslint-disable-next-line no-case-declarations
            const [status, commentType] = queryData.value.exceptionRemarks.split('-');
            params.status = parseInt(status);
            params.comment_type = parseInt(commentType);
            break;
        case 'empty':
            url = '/api/v1/projector/application/filter/status';
            break;
        default:
            errs.push($t('taskQuery.searchCriteria'));
            break;
    }

    if (errs.length) {
        return errs.forEach(err => FMessage.warn(err));
    }

    filterAction(url, params);
    searchMode.value = 'normal';
    resetAdvanceQueryData();
    // 这个地方只是重写url，并不会重新加载
    router.replace(`${route.path}?${getURLQueryParams({
        query: route.query,
        params: { page: tasksPagination.value.current, pageSize: tasksPagination.value.size },
    })}`);
    resultByInit.value = false;
};

// 高级筛选弹框里 已选了几项
const advanceQuerySelectedCount = ref(0);

watch(advanceQueryData, () => {
    advanceQuerySelectedCount.value = 0;
    Object.keys(advanceQueryData.value).forEach((key) => {
        if (advanceQueryData.value[key]) {
            if (key !== 'start_time') {
                if (key === 'end_time') {
                    if (advanceQueryData.value.start_time && advanceQueryData.value.end_time) advanceQuerySelectedCount.value++;
                } else {
                    advanceQuerySelectedCount.value++;
                }
            }
        }
    });
}, { immediate: true, deep: true });

const advanceQueryDataBak = ref({});
// 打开高级筛选弹框
const clickAdvanceQuery = () => {
    showAdvanceSearchModal.value = true;
    advanceQueryDataBak.value = cloneDeep(advanceQueryData.value);
};

watch(advanceQueryData, () => {
    advanceQuerySelectedCount.value = 0;
    Object.keys(advanceQueryData.value).forEach((key) => {
        if (advanceQueryData.value[key]) {
            if (key !== 'start_time') {
                if (key === 'end_time') {
                    if (advanceQueryData.value.start_time && advanceQueryData.value.end_time) advanceQuerySelectedCount.value++;
                } else {
                    advanceQuerySelectedCount.value++;
                }
            }
        }
    });
}, { immediate: true, deep: true });

// 高级筛选
const advanceSearch = () => {
    let commentType = null;
    try {
        commentType = advanceQueryData.value.comment_type.split('-')[1];
        commentType = commentType ? parseInt(commentType) : null;
    } catch (e) {
        console.warn(e.message);
    }
    filterAction('/api/v1/projector/application/filter/advance', {
        application_id: advanceQueryData.value.taskNumber,
        project_id: advanceQueryData.value.projectName,
        status: parseInt(advanceQueryData.value.taskStatus),
        selected_status: parseInt(advanceQueryData.value.taskStatus),
        cluster_name: advanceQueryData.value.dataSource,
        database_name: advanceQueryData.value.dataBase,
        table_name: advanceQueryData.value.dataTable,
        start_time: advanceQueryData.value.start_time,
        end_time: advanceQueryData.value.end_time,
        rule_group_id: advanceQueryData.value.rule_group_id,
        execute_user: advanceQueryData.value.execute_user,
        comment_type: commentType,
        size: tasksPagination.value.size,
        page: tasksPagination.value.current - 1,
    });
    searchMode.value = 'advanced';
    resultByInit.value = false;
};

// 取消 高级筛选 弹框
const cancelAdvanceSearch = () => {
    showAdvanceSearchModal.value = false;
    advanceQueryData.value = cloneDeep(advanceQueryDataBak.value);
};

// 重置
const reset = () => {
    setInitQueryData();
    firstActionBarRef.value.timeRangeOfAdvanceQuery = [];
    searchMode.value = '';
    tasksPagination.value.current = 1;
    getTasksData();
    // 这个地方只是重写url，并不会重新加载
    router.replace(`${route.path}?${getURLQueryParams({
        query: route.query,
        params: { page: tasksPagination.value.current, pageSize: tasksPagination.value.size },
    })}`);
    // 回到列表顶部
    document.getElementsByClassName('wd-content')[0].scrollTop = 0;
    resultByInit.value = true;
};


const getData = () => {
    if (searchMode.value === '') {
        getTasksData();
    } else if (searchMode.value === 'normal') {
        search();
    } else {
        advanceSearch();
    }
};

// 判断当前批量执行的状态 'no' 为不执行
const exportStatus = ref('no');

// 批量重新执行
const batchReExecution = () => {
    executeTaskPreInTaskQuery(tableSelectedItems.value, tasksData.value);
};

function clickbatchReExecution() {
    taskTable.value.clearSelection();
    exportStatus.value = 'batchReExecution';
}

const clickExecutationOk = async () => {
    await executeTaskInTaskQuery();
    exportStatus.value = 'no';
    getData();
};

// 点击更多里的操作
const clickMore = (value) => {
    if (value === 'setTable') {
        toggleTColConfig();
    }
    if (value === 'batchStop') {
        taskTable.value.clearSelection();
        console.log('lastQueryParams', lastQueryParams.value);
        filterAction('/api/v1/projector/application/filter/advance', {
            ...lastQueryParams.value,
            stop_able_status: [1, 3, 10],
        }, true);
        exportStatus.value = 'batchStop';
    }
};


function batchStop() {
    const applicationIds = tableSelectedItems.value.map(item => item);
    stopBatch({ applicationIds }).then(() => {
        FMessage.success($t('common.stopSuccess'));
        exportStatus.value = 'no';
        getData();
    });
}

const selectable = ({ row }) => {
    if (exportStatus.value === 'no') {
        return row.status === 1 || row.status === 3 || row.status === 10;
    }
    return 1;
};

// 点击确认按钮根据当前类型执行批量方法
function confirmSelectItems() {
    if (tableSelectedItems.value.length === 0) {
        return FMessage.warn($t('common.selectOne'));
    }
    exportStatus.value === 'batchReExecution' ? batchReExecution() : batchStop();
}

// 表格里的“更多”下拉弹框
const getTableMoreOptions = (row) => {
    const result = [
        {
            value: 'reExecute',
            label: '重新执行',
        }, {
            value: 'viewLog',
            label: '查看日志',
        }, {
            value: 'statusDetail',
            label: '状态详情',
        },
    ];

    if (['1', '3', '10'].includes(row.status)) {
        result.push({
            value: 'stopTask',
            label: '停止任务',
        });
    }

    return result;
};


// 当前点击的行的任务编号
const taskId = ref('');
const trData = ref({});

// 点击表格的某一行
const clickTableCell = (data) => {
    trData.value = data.row;
    taskId.value = data.row.application_id;
};

// 通过，未通过，失败，初始化失败，参数错误 不允许 停止运行
// const stateListOfNotStop = ref(['4', '7', '8', '9', '11']);

const commentFormatter = ({ cellValue }) => {
    const target = exceptionRemarksList.value.find(item => item.value.split('-')[1] === cellValue);
    return target ? target.label || '--' : cellValue || '--';
};

const formatInvoke = ({ row }) => {
    const invoke = {
        1: $t('taskQuery.interfaceScheduling'),
        2: $t('taskQuery.timeSchedule'),
        3: $t('taskQuery.workFlow'),
    };
    return invoke[row.invoke_type] || '--';
};

const tempStatusList = ref({
    0: {
        label: $t('taskQuery.all'),
        color: '#444',
    },
    1: {
        label: $t('taskQuery.submitted'),
        color: '#00CB91',
    },
    3: {
        label: $t('taskQuery.inTheOperation'),
        color: '#00CB91',
    },
    4: {
        label: $t('common.byCheck'),
        color: '#00CB91',
    },
    7: {
        label: $t('taskQuery.failure'),
        color: '#FF4D4F',
    },
    8: {
        label: $t('common.failCheck'),
        color: '#FF9540',
    },
    9: {
        label: $t('taskQuery.tasksFailed'),
        color: '#FF4D4F',
    },
    10: {
        label: $t('taskQuery.tasksSuccessfully'),
        color: '#00CB91',
    },
    11: {
        label: $t('taskQuery.parameterError'),
        color: '#FF4D4F',
    },
    12: {
        label: $t('taskQuery.submitPending'),
        color: '#FF4D4F',
    },
});

// const formatTable = ({ row }) => (row.database_name ? `${row.database_name}.` : '') + (row.table_name ? `${row.table_name}.` : '');

const tasksPageChange = () => {
    // 这个地方只是重写url，并不会重新加载
    router.replace(`${route.path}?${getURLQueryParams({
        query: route.query,
        params: { page: tasksPagination.value.current, pageSize: tasksPagination.value.size },
    })}`);
    if (searchMode.value === '') {
        getTasksData();
    } else if (searchMode.value === 'normal') {
        search(tasksPagination.value.current);
    } else {
        advanceSearch();
    }
    // 回到列表顶部
    document.getElementsByClassName('wd-content')[0].scrollTop = 0;
};
// const tasksPageSizeChange = () => {
//     tasksPagination.value.current = 1;
//     if (searchMode.value === '') {
//         getTasksData();
//     } else if (searchMode.value === 'normal') {
//         search();
//     }
// };

// 获取规则组名称接口
const ruleGroupList = ref([]);
const getRuleGroupList = async (val) => {
    resetQueryData();
    if (!val) {
        ruleGroupList.value = [];
        advanceQueryData.value.rule_group_id = '';
        return;
    }
    try {
        const res = await fetchRuleGroupList({ project_id: val });
        ruleGroupList.value = (res || []).filter(v => v.rule_group_name);
    } catch (error) {
        console.log('error', error);
    }
};

const executeUserList = ref([]);
const getExecuteUserList = async () => {
    try {
        const res = await fetchExecuteUserList();
        executeUserList.value = res.map(item => ({ value: item, label: item }));
    } catch (error) {
        console.log('error', error);
    }
};

const taskNumber = route.query.application_id;
const init = async () => {
    try {
        await getDataSourceList();
    } catch (error) {
        console.log('error: ', error);
    }

    try {
        getProjects();
    } catch (error) {
        console.log('error: ', error);
    }
    if (taskNumber && taskNumber !== 'undefined') {
        await filterAction('/api/v1/projector/application/filter/advance', {
            application_id: taskNumber,
            size: 10,
            page: 0,
        });
    } else {
        await getTasksData();
    }
    await getExecuteUserList();
};

onMounted(() => {
    init();
});


const logDrawerRef = ref();
const statusDrawerRef = ref();

// 点击表格里的更多里的操作
const clickTableMore = (value, row) => {
    if (value === 'reExecute') {
        // 任务执行
        executeTaskPreInTaskQuery([row.application_id], tasksData.value);
    }
    if (value === 'stopTask') {
        // 停止任务

        stopBatch({ applicationIds: [row.application_id] }).then(() => {
            FMessage.success($t('common.stopSuccess'));
            getData();
        }).finally(() => {
            // loading?
        });
    }
    if (value === 'viewLog') {
        // 查看日志
        logDrawerRef.value.handleChange();
        // logDrawerRef.value.logDetailDrawerInit();
    }
    if (value === 'statusDetail') {
        // 状态详情
        statusDrawerRef.value.handleChange();
        // statusDrawerRef.value.statusDetailDrawerInit();
    }
};

// 点击功能按钮之后的重置
const resetOperation = () => {
    taskTable.value?.clearSelection();
    exportStatus.value = 'no';
    filterAction('/api/v1/projector/application/filter/advance', {
        ...lastQueryParams.value,
        stop_able_status: null,
    }, true);
};

</script>
<style lang="less" scoped>
.dashboard {
    .division {
        width: 100%;
        height: 1px;
        background: rgba(15,18,34,0.06);
    }

    .table-container {
        .table-operation-item {
            padding: 0;
            width: 100%;
            min-width: 100%;
            border: 0;
            font-family: PingFangSC-Regular;
            font-size: 14px;
            color: #5384FF;
            letter-spacing: 0;
            line-height: 22px;
            font-weight: 400;
            &:hover {
                color: #5384FF;
            }
        }
    }
    .pagination {
        display: flex;
        justify-content: flex-end;
    }
    .fes-select {
        width: 160px;
    }
}
</style>

<style lang="less">
    .task-form.fes-form {
        padding: 0;
    }

    .task-drawer {
        .fes-drawer-body {
            height: calc(100% - 55px);
            overflow: auto;
        }
    }
</style>

<config>
{
    "name": "tasks",
    "title": "$taskQuery.taskQuery"
}
</config>
