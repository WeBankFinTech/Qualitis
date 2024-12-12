<template>
    <div class="wd-content dashboard" style="padding: 16px 16px 32px">
        <BTablePage :isLoading="isLoading" actionType="loading" :loadingText="{ loading: '' }">
            <template v-slot:search>
                <BSearch v-model:form="queryData" v-model:advanceForm="advanceQuery" :isAdvance="true" @search="search" @reset="handleReset" @advance="toggleAdvanceQuery">
                    <template v-slot:form>
                        <div>
                            <span class="condition-label">{{$t('dataSourceManagement.dataSourceType')}}</span>
                            <FSelect v-model="queryData.data_source_type_id" :placeholder="$t('common.pleaseSelect')" clearable filterable @change="handleCommonChange">
                                <FOption v-for="(item, index) in dataSourceTypeList" :key="index" :value="item.id" :label="item.name"></FOption>
                            </FSelect>
                        </div>
                        <div>
                            <span class="condition-label">{{$t('dataSourceManagement.dataSourceName')}}</span>
                            <FSelect v-model="queryData.name" :placeholder="$t('common.pleaseSelect')" filterable clearable :options="dataSourceNameList" @change="handleCommonChange">
                            </FSelect>
                        </div>
                    </template>
                </BSearch>
            </template>
            <template v-slot:operate>
                <FSpace :size="CONDITIONBUTTONSPACE">
                    <FButton type="primary" @click="openAddDataSourceModal">
                        <PlusOutlined />{{$t('dataSourceManagement.addDataSource')}}
                    </FButton>
                    <FDropdown :options="moreOptions" @click="clickMore">
                        <FButton>
                            <template #icon> <MoreCircleOutlined /> </template>{{$t('common.more')}}
                        </FButton>
                    </FDropdown>
                </FSpace>
            </template>
            <template v-slot:table>
                <f-table ref="taskTable" :data="tasksData" :no-data-text="$t('common.noData')" @cellClick="clickTableCell">
                    <!-- <f-table-column type="selection" :width="32"></f-table-column> -->
                    <template #empty>
                        <div class="empty-block">
                            <div v-if="resultByInit">
                                <div class="empty-data"></div>
                                <div class="table-empty-tips">{{$t('common.emptyInitResult')}}</div>
                            </div>
                            <div v-else>
                                <div class="empty-query-result"></div>
                                <div class="table-empty-tips">{{$t('common.emptyQueryResult')}}</div>
                            </div>
                        </div>
                    </template>
                    <f-table-column :formatter="formatterEmptyValue" prop="id" :label="$t('dataSourceManagement.dataSourceId')" align="left" classes="idlink" :width="88" ellipsis></f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" prop="dataSourceName" :label="$t('dataSourceManagement.dataSourceName')" align="left" :width="160" ellipsis></f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" prop="dataSourceType" :visible="checkTColShow('dataSourceType')" :label="$t('dataSourceManagement.dataSourceType')" align="left" :width="102" ellipsis></f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" prop="dataSourceDesc" :visible="checkTColShow('dataSourceDesc')" :label="$t('dataSourceManagement.dataSourceDesc')" align="left" :width="120">
                        <template #default="{ row }">
                            <FEllipsis>
                                {{row.dataSourceDesc || '--'}}
                                <template #tooltip>
                                    <div style="width:300px;word-wrap:break-word">
                                        {{row.dataSourceDesc}}
                                    </div>
                                </template>
                            </FEllipsis>
                        </template>
                    </f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" prop="subSystem" :visible="checkTColShow('subSystem')" :label="$t('dataSourceManagement.associatedSubSystem')" align="left" :width="160" ellipsis></f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" prop="labels" :visible="checkTColShow('labels')" :label="$t('dataSourceManagement.dataSourceLabel')" align="left" :width="160">
                        <template #default="{ row }">
                            <FEllipsis>
                                {{row.labels || '--'}}
                                <template #tooltip>
                                    <div style="width:300px;word-wrap:break-word">
                                        {{row.labels}}
                                    </div>
                                </template>
                            </FEllipsis>
                        </template>
                    </f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" prop="status" :visible="checkTColShow('status')" :label="$t('dataSourceManagement.status')" align="left" :width="74" ellipsis></f-table-column>
                    <f-table-column v-slot="{ row }" prop="versionId" :label="$t('dataSourceManagement.version')" align="left" :width="60" ellipsis>
                        <span v-if="row.versionId" :style="{ color: '#5384FF', cursor: 'pointer' }" @click="logDetailDrawerInit(row.id)">{{row.versionId}}</span>
                        <span v-else>--</span>
                    </f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" prop="createUser" :visible="checkTColShow('createUser')" :label="$t('dataSourceManagement.createUser')" align="left" :width="150" ellipsis></f-table-column>
                    <f-table-column prop="createTime" :visible="checkTColShow('createTime')" :label="$t('dataSourceManagement.createTime')" align="left" :width="150" ellipsis>
                        <template #default="{ row }">{{dayjs(row.createTime).format('YYYY-MM-DD HH:mm:ss') || '--'}}</template>
                    </f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" prop="modifyUser" :visible="checkTColShow('createUser')" :label="$t('dataSourceManagement.modifyUser')" align="left" :width="150" ellipsis></f-table-column>
                    <f-table-column prop="modifyTime" :visible="checkTColShow('modifyTime')" :label="$t('dataSourceManagement.modifyTime')" align="left" :width="150" ellipsis>
                        <template #default="{ row }">{{dayjs(row.modifyTime).format('YYYY-MM-DD HH:mm:ss') || '--'}}</template>
                    </f-table-column>
                    <f-table-column prop="dev_department_name" :visible="checkTColShow('dev_department_name')" :label="$t('dataSourceManagement.developDepartment')" align="left" :width="150" ellipsis>
                        <template #default="{ row }">{{row.dev_department_name || '--'}}</template>
                    </f-table-column>
                    <f-table-column prop="ops_department_name" :visible="checkTColShow('ops_department_name')" :label="$t('dataSourceManagement.maintainDepartment')" align="left" :width="150" ellipsis>
                        <template #default="{ row }">{{row.ops_department_name || '--'}}</template>
                    </f-table-column>
                    <f-table-column ellipsis prop="visibility_department_list" :visible="checkTColShow('visibility_department_list')" :label="$t('dataSourceManagement.visibleRange')" :width="200">
                        <template #default="{ row }">
                            <FEllipsis v-if="row?.visibility_department_list">
                                {{getvisibilityDepartment(row.visibility_department_list)}}
                                <template #tooltip>
                                    <div style="text-align:center">可见范围</div>
                                    <div style="max-width:300px;word-wrap:break-word">
                                        {{getvisibilityDepartment(row.visibility_department_list)}}
                                    </div>
                                </template>
                            </FEllipsis>
                            <div v-else>--</div>
                        </template>
                    </f-table-column>

                    <f-table-column #default="{ row } = {}" :label="$t('common.operate')" align="center" fixed="right" :width="60" ellipsis>
                        <FDropdown arrow :options="tableMoreOptions" trigger="focus" @click="clickTableMore($event, row)">
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
                    @pageSizeChange="tasksPageSizeChange"
                ></FPagination>
            </template>
        </BTablePage>
        <!-- 版本列表 -->
        <FDrawer
            v-model:show="showVersionListDrawer"
            :title="$t('dataSourceManagement.versionList')"
            displayDirective="if"
            :width="940"
            contentClass="task-drawer"
        >
            <f-table ref="versionListTable" :data="versionListTableData" :no-data-text="$t('common.noData')" @cellClick="clickVersionListTableCell">
                <template #empty>
                    <div class="empty-block">
                        <div class="empty-data"></div>
                        <div class="table-empty-tips">这里还没有数据. . .</div>
                    </div>
                </template>
                <f-table-column :formatter="formatterEmptyValue" prop="versionId" :label="$t('dataSourceManagement.version')" align="left" classes="idlink" :width="60" ellipsis></f-table-column>
                <f-table-column :formatter="formatterEmptyValue" prop="versionStatus" :label="$t('dataSourceManagement.status')" align="left" :width="88" ellipsis></f-table-column>
                <f-table-column :formatter="formatterEmptyValue" prop="comment" :label="$t('dataSourceManagement.versionDescription')" align="left" :width="102" ellipsis></f-table-column>
                <f-table-column v-slot="{ row }" :label="$t('common.operate')" align="left" fixed="right" :width="176" ellipsis>
                    <a v-if="row.versionStatus === '未发布'" class="table-operation-item" @click="publishDataSource(row.versionId)"> {{$t('dataSourceManagement.publish')}} </a>
                    <a class="table-operation-item" @click="viewDataSource(row.versionId)"> {{$t('dataSourceManagement.view')}} </a>
                    <a class="table-operation-item" @click="testConnection(row.versionId)"> {{$t('dataSourceManagement.testConnection')}} </a>
                </f-table-column>
            </f-table>
        </FDrawer>
        <FSpin
            :show="spinShow"
            :delay="200"
            style="width: 100%"
            description="加载中"
        />
        <!--新增数据源弹窗、数据源详情弹窗-->
        <AddOrEditManagement
            ref="addOrEditManagementRef"
            v-model:show="showAddDataSourceModal"
            v-model:mode="curMode"
            :curDataSourceDetail="curDataSourceDetail"
            :sid="curSelectedDataSourceId"
            :queryProxyUser="queryData.proxyUser"
            :subSystemList="subSystemList"
            @updateDataSource="getTasksData(0,tasksPagination.size)"
        ></AddOrEditManagement>

        <!-- 表格设置弹窗 -->
        <BTableHeaderConfig
            v-model:headers="projectTableHeaders"
            v-model:show="showProjectTableColConfig"
            :originHeaders="originProjectHeaders"
            type="data_source" />
        <!-- 高级查询 -->
        <AdvancedQuery
            ref="advancedQueryRef"
            v-model:showAdvanceQuery="showAdvanceQuery"
            v-model:advanceQuery="advanceQuery"
            :subSystemList="subSystemList"
            @advanceSearch="search"
        />
        <!-- 连接过期确认 -->
        <FModal
            v-model:show="showExpireFmodalShow"
            :title="$t('common.prompt')"
            displayDirective="if"
            @ok="handleExpire"
        >
            <div>确认继续数据源【{{trData.dataSourceName}}】的过期操作?</div>
        </FModal>
    </div>
</template>

<script setup>
import {
    onMounted, ref, nextTick, computed, provide, reactive,
} from 'vue';
import {
    FMessage, FModal, FTooltip, FEllipsis, FSpin,
} from '@fesjs/fes-design';
import {
    BTableHeaderConfig, BPageLoading, BTablePage, BSearch,
} from '@fesjs/traction-widget';

import { useI18n } from '@fesjs/fes';
import {
    MoreCircleOutlined,
    DownOutlined,
    UpOutlined,
} from '@fesjs/fes-design/es/icon';
import { MAX_PAGE_SIZE, CONDITIONBUTTONSPACE } from '@/assets/js/const';
import dayjs from 'dayjs';
import { PlusOutlined } from '@fesjs/fes-design/icon';
import useTableHeaderConfig from '@/hooks/useTableHeaderConfig';
import { getvisibilityDepartment, formatterEmptyValue } from '@/common/utils';
import AdvancedQuery from './components/advancedQuery.vue';
import {
    fetchProxyUserList, fetchDataSourceName, fetchDataSourceType, fetchVersionList, fetchDataSourceDetail, fetchTestConnection, fetchPublishDataSource, fetchExpireDataSource, fetchEditDataSource, fetchAddDataSource, fetchDataSourceList, fetchSubSystemInfo, queryDataSourceList,
} from './api';
import AddOrEditManagement from './components/addOrEditManagement.vue';

const { t: $t } = useI18n();

const advancedQueryRef = ref(null);
const addOrEditManagementRef = ref('');
// 当前选择的数据源id
const curSelectedDataSourceId = ref(0);
// form和modal
const taskTable = ref();
const versionListTable = ref();

// 各查询条件

// 数据源名称列表
const dataSourceNameList = ref([]);
provide('dataSourceNameList', dataSourceNameList);
// 数据源类型列表
const dataSourceTypeList = ref([]);
provide('dataSourceTypeList', dataSourceTypeList);
// 新增数据源下的数据源类型列表
// const dataSourceAddTypeList = ref([]);

// // 代理用户列表
// const proxyUserList = ref([]);

// 查询的数据
const queryData = ref({});

// 打开弹窗的模式 add-新增数据源、preview-数据源详情
const curMode = ref('add');

// 重置外面的查询框
const resetQueryData = () => {
    queryData.value = {};
};

// 设置初始的查询空状态
const setInitQueryData = () => {
    resetQueryData();
};

setInitQueryData();

const getDataSourceNameList = async () => {
    const url = '/api/v1/projector/meta_data/all/dataSourceName';
    try {
        const { data } = await fetchDataSourceList(url);
        dataSourceNameList.value = [];
        data.forEach((item) => {
            dataSourceNameList.value.push({
                value: item,
                label: item,
            });
        });
    } catch (error) {
        console.log('error: ', error);
    }
};

const advanceQuery = ref({});
const showAdvanceQuery = ref(false);
const toggleAdvanceQuery = async () => {
    showAdvanceQuery.value = true;
    queryData.value = {};
    // eslint-disable-next-line no-use-before-define
    await getSubSystemInfo();
};
const handleReset = () => {
    console.log('handleReset-重置操作，重置页码重新查询');
    advancedQueryRef.value.selectDevDate = '';
    advancedQueryRef.value.selectOpsDate = '';
    advancedQueryRef.value.selectVisDate = [];
    // eslint-disable-next-line no-use-before-define
    search();
};
// 表格新增/详情弹框的数据
const addDataSourceForm = ref({ // modal表单数据结构
    // 代理用户
    proxyUser: '',
    // 数据源类型
    dataSourceType: '',
    // 数据源名称
    dataSourceName: '',
    // Host
    host: '',
    // 端口
    port: '',
    // 用户名
    username: '',
    // 密码
    password: '',
    // 数据源描述
    dataSourceDescription: '',
    // 子系统
    subSystem: '',
    // 标签
    label: '',
    // 认证方式
    authType: '',
});

// 根据选择的集群数据，获取对应的数据源类型下拉框
const getDataSourceTypeAllData = async () => {
    try {
        const url = '/api/v1/projector/meta_data/data_source/types/all';
        const { res } = await fetchDataSourceType(url);
        dataSourceTypeList.value = res && res.typeList ? res.typeList : [];
    } catch (error) {
        console.log('error: ', error);
    }
};

// 获取代理用户列表
// const getProxyUserList = async () => {
//     const params = {
//         size: 100,
//         page: 0,
//     };
//     try {
//         const { res } = await fetchProxyUserList(params);
//         proxyUserList.value = Array.isArray(res) ? res : [];
//     } catch (error) {
//         console.log('error: ', error);
//     }
// };

// 表格数据
const tasksData = ref([]);
const tasksPagination = ref({
    total: 0,
    current: 1,
    size: 10,
});
const tasksTotal = ref(0);

const isLoading = ref(false);

const getDataSourceType = (id) => {
    for (let i = 0; i < dataSourceTypeList.value.length; i++) {
        if (id === parseInt(dataSourceTypeList.value[i].id)) {
            return dataSourceTypeList.value[i].name;
        }
    }
    return '';
};

const assignedQuery = ref({});
const cleanBody = (body) => {
    const keys = Object.keys(body);
    for (let i = 0; i < keys.length; i++) {
        if (!body[keys[i]] || (Array.isArray(body[keys[i]]) && body[keys[i]].length === 0) || keys[i] === 'dev_department_name' || keys[i] === 'ops_department_name') {
            delete body[keys[i]];
        }
    }
    return body;
};
// 获取表格数据
const getTasksData = async (currentPage = 0, pageSize) => {
    // const url = `/api/v1/projector/meta_data/data_source/info?proxyUser=${queryData.value.proxyUser || ''}&currentPage=${currentPage}&pageSize=${pageSize}&typeId=${queryData.value.dataSourceType || ''}&name=${queryData.value.dataSourceName || ''}`;
    isLoading.value = true;
    tasksPagination.value.current = currentPage + 1;
    tasksPagination.value.size = pageSize;
    try {
        const body = assignedQuery.value;
        body.dev_department_id = advancedQueryRef.value.selectDevDate ? Number(advancedQueryRef.value.selectDevDate) : '';
        body.ops_department_id = advancedQueryRef.value.selectOpsDate ? Number(advancedQueryRef.value.selectOpsDate) : '';
        body.visible_department_ids = advancedQueryRef.value.selectVisDate?.map(item => Number(item.id)) || '';
        const cleanedBody = cleanBody(body);
        const { total, data } = await queryDataSourceList(Object.assign({ page: currentPage, size: pageSize }, cleanedBody));
        tasksTotal.value = total;
        tasksPagination.value.total = Math.ceil(total / tasksPagination.value.size);
        tasksData.value = Array.isArray(data) ? data : [];
        tasksData.value.forEach((item) => {
            item.status = item.expire ? $t('dataSourceManagement.expire') : $t('dataSourceManagement.canUse');
            item.dataSourceType = getDataSourceType(item.dataSourceTypeId);
        });
    } catch (error) {
        console.log('error: ', error);
    }
    isLoading.value = false;
};

tasksData.value = [];

const tasksPageChange = () => {
    getTasksData(tasksPagination.value.current - 1, tasksPagination.value.size);
};
const tasksPageSizeChange = () => {
    tasksPagination.value.current = 1;
    getTasksData(tasksPagination.value.current - 1, tasksPagination.value.size);
};

// 表格设置弹框
const originProjectHeaders = [
    { prop: 'dataSourceType', label: $t('dataSourceManagement.dataSourceType') },
    { prop: 'status', label: $t('dataSourceManagement.status') },
    { prop: 'createUser', label: $t('dataSourceManagement.createUser') },
    { prop: 'labels', label: $t('dataSourceManagement.label') },
    { prop: 'dataSourceDesc', label: $t('dataSourceManagement.dataSourceDesc') },
    { prop: 'subSystem', label: $t('dataSourceManagement.subSystem'), hidden: true },
    { prop: 'modifyUser', label: $t('dataSourceManagement.modifyUser') },
    { prop: 'createTime', label: $t('dataSourceManagement.createTime'), hidden: true },
    { prop: 'modifyTime', label: $t('dataSourceManagement.modifyTime'), hidden: true },
    { prop: 'dev_department_name', label: $t('dataSourceManagement.developDepartment') },
    { prop: 'ops_department_name', label: $t('dataSourceManagement.maintainDepartment') },
    { prop: 'visibility_department_list', label: $t('dataSourceManagement.visibleRange'), hidden: true },
];

// 普通项目表头设置
const projectTableHeaders = ref([]);
const showProjectTableColConfig = ref(false);
const checkTColShow = col => projectTableHeaders.value.map(item => item.prop).includes(col);
const toggleTColConfig = () => {
    showProjectTableColConfig.value = true;
};
const resultByInit = ref(true); // 表格的结果是否是初始状态，搜索后该值变为false
// 调用查询接口
const search = async () => {
    assignedQuery.value = Object.assign({}, queryData.value, advanceQuery.value);
    console.log(assignedQuery.value);
    getTasksData(0, tasksPagination.value.size);
    resultByInit.value = false;
};
const handleCommonChange = () => {
    const keys = Object.keys(advanceQuery.value);
    for (let i = 0; i < keys.length; i++) {
        if (Array.isArray(advanceQuery.value[keys[i]])) {
            advanceQuery.value[keys[i]] = [];
        } else {
            advanceQuery.value[keys[i]] = '';
        }
    }
    advancedQueryRef.value.selectDevDate = '';
    advancedQueryRef.value.selectOpsDate = '';
    advancedQueryRef.value.selectVisDate = [];

    // advanceQuery.value = {
    //     action_range: [],
    // };
};
// “更多”下拉弹框
const moreOptions = ref([
    {
        value: 'setTable',
        label: $t('common.setTableHeaderConfig'),
    },
    {
        value: 'batchOffline',
        label: $t('dataSourceManagement.batchOffline'),
    },
]);

// 点击更多里的操作
const clickMore = (value) => {
    if (value === 'setTable') {
        toggleTColConfig();
    }
    if (value === 'batchOffline') {
        // 预留的批量下线功能
        FMessage.warn('敬请期待');
    }
};

// 表格里的“更多”下拉弹框
const tableMoreOptions = ref([
    {
        value: 'detail',
        label: $t('dataSourceManagement.detail'),
    }, {
        value: 'expire',
        label: $t('dataSourceManagement.expire'),
    }, {
        value: 'testConnection',
        label: $t('dataSourceManagement.testConnection'),
    },
]);

const showVersionListDrawer = ref(false);

// 当前点击的行的任务编号
const taskId = ref('');
const trData = ref({});

// 点击表格的某一行
const clickTableCell = (data) => {
    trData.value = data.row;
    taskId.value = data.row.application_id;
    console.log('clickTableCell-trData.value: ', trData.value);
};


const init = async () => {
    isLoading.value = true;

    try {
        getDataSourceTypeAllData();
        getDataSourceNameList();
        getTasksData(0, tasksPagination.value.size);
    } catch (error) {
        console.log('error: ', error);
    }

    // try {
    //     getProxyUserList();
    // } catch (error) {
    //     console.log('error: ', error);
    // }
};

onMounted(() => {
    init();
});

// 版本列表部分

// 获取集群列表
const versionListTableData = ref([]);

// 获取版本列表表格数据
const getVersionList = async (id, currentPage, pageSize) => {
    const url = `/api/v1/projector/meta_data/data_source/versions?&proxyUser=${queryData.value.proxyUser}&dataSourceId=${id}`;

    try {
        const { res } = await fetchVersionList(url);
        versionListTableData.value = res && res.versions && Array.isArray(res.versions) ? res.versions : [];
        const publishedId = trData.value.publishedVersionId;

        if (publishedId) {
            versionListTableData.value.forEach((item) => {
                if (item.versionId === publishedId) {
                    item.versionStatus = '已发布';
                } else if (item.versionId < publishedId) {
                    item.versionStatus = '不可发布';
                } else if (item.versionId > publishedId) {
                    item.versionStatus = '未发布';
                }
            });
        } else {
            versionListTableData.value.forEach((item) => {
                item.versionStatus = '未发布';
            });
        }
    } catch (error) {
        console.log('error: ', error);
    }
};

// 获取版本列表表格数据
const getVersionListTableData = async (datasourceId) => {
    const params = {
        current: 0,
        size: 100,
    };

    await getVersionList(datasourceId, params.current, params.size);
};

// 当前点击的行的任务编号
const versionListTaskId = ref('');
const versionListTrData = ref({});

// 点击表格的某一行
const clickVersionListTableCell = (data) => {
    versionListTrData.value = data.row;
    versionListTaskId.value = data.row.application_id;
};

// 子系统列表
const subSystemList = ref([]);
// 获取子系统中文名
const getSystemNameNew = (data, tr) => tr.full_cn_name || tr.subSystemFullCnName || data;

// 获取子系统列表
const getSubSystemInfo = async () => {
    const res = await fetchSubSystemInfo();
    const list = res || [];
    subSystemList.value = list.map((item) => {
        const cnName = getSystemNameNew(item.subSystemId, item);
        return Object.assign({}, item, {
            subSystemName: cnName,
            enName: item.subSystemName,
            cnName,
            id: item.subSystemId,
            value: String(item.subSystemName),
            label: item.subSystemName,
        });
    });
};

// 版本列表组件的数据初始化
const logDetailDrawerInit = (dataSourceId) => {
    showVersionListDrawer.value = true;

    try {
        getVersionListTableData(dataSourceId);
        getSubSystemInfo();
    } catch (error) {
        console.log('error: ', error);
    }
};

// 新增数据源 开始
const addDataSourceFormRef = ref();
const showAddDataSourceModal = ref(false);

const resetAddDataSourceForm = () => {
    addDataSourceForm.value = { // modal表单数据结构
        // 代理用户
        proxyUser: '',
        // 数据源类型
        dataSourceType: '',
        // 数据源名称
        dataSourceName: '',
        // Host
        host: '',
        // 端口
        port: '',
        // 用户名
        username: '',
        // 密码
        password: '',
        // 数据源描述
        dataSourceDescription: '',
        // 子系统
        subSystem: '',
        // 标签
        label: '',
        // 连接参数
        connectParams: '',
        // 认证方式
        authType: '',
        // appid
        appid: '',
        // objectid
        objectid: '',
        // 账户私钥
        mkPrivate: '',
        buss_custom: '',
        // 可选范围
        action_range: [],
        visibility_department_list: [],
        // 开发部门
        department_name: '',
        // 开发科室
        dev_department_id: '',
        dev_department_name: [],
        // 运维科室
        ops_department_id: '',
        ops_department_name: [],
        // 是否可编辑
        is_editable: false,
    };
};


// 选择数据源类型
// const selectDataSourceType = async (value) => {
//     resetAddDataSourceForm();
//     addDataSourceForm.value.dataSourceType = value;
// };
const curDataSourceDetail = ref({});

// 打开 新增数据源  弹框
const openAddDataSourceModal = () => {
    showAddDataSourceModal.value = true;
    curMode.value = 'add';
    if (subSystemList.value.length === 0) {
        getSubSystemInfo();
    }
};

const addRuleValidate = ref({ // 表单验证规则
    dataSourceType: [
        { required: true, message: $t('common.notEmpty') },
    ],
    dataSourceName: [
        { required: true, message: $t('common.notEmpty') },
    ],
    host: [
        { required: true, message: $t('common.notEmpty') },
    ],
    port: [
        { required: true, message: $t('common.notEmpty') },
    ],
    username: [
        { required: true, message: $t('common.notEmpty') },
        // { pattern: /^[a-zA-Z]+$/, message: $t('indexManagement.pleaseEnterEnChar') },
    ],
    password: [{ required: true, message: $t('common.notEmpty') }],
    authType: [{ required: true, message: $t('common.notEmpty') }],
    appid: [{ required: true, message: $t('common.notEmpty') }],
    objectid: [{ required: true, message: $t('common.notEmpty') }],
    mkPrivate: [{ required: true, message: $t('common.notEmpty') }],
});

// 新增数据源后的初始化
const afterAddDataSource = async (dataSourceId, type, subSystemName) => {
    if (!dataSourceId) return;
    let params = {};
    let url = `/api/v1/projector/meta_data/data_source/param/modify?&dataSourceId=${dataSourceId}&`;
    if (type === 'add') {
        url += `proxyUser=${queryData.value.proxyUser}`;
        params = {
            comment: 'qualitis',
            connectParams: {
                host: addDataSourceForm.value.host,
                port: addDataSourceForm.value.port,
                username: addDataSourceForm.value.username,
                password: addDataSourceForm.value.password,
                subSystem: subSystemName || '',
                dataSourceTypeId: addDataSourceForm.value.dataSourceType,
                params: addDataSourceForm.value.connectParams,
                authType: addDataSourceForm.value.authType,
                appid: addDataSourceForm.value.appid,
                objectid: addDataSourceForm.value.objectid,
                mkPrivate: addDataSourceForm.value.mkPrivate,
            },
            dataSourceTypeId: addDataSourceForm.value.dataSourceType,
        };
    } else {
        url += `&proxyUser=${queryData.value.proxyUser}`;
        params = {
            comment: 'qualitis',
            connectParams: {
                host: addDataSourceForm.value.host,
                port: addDataSourceForm.value.port,
                username: addDataSourceForm.value.username,
                password: addDataSourceForm.value.password,
                subSystem: subSystemName || '',
                dataSourceTypeId: addDataSourceForm.value.dataSourceType,
                params: addDataSourceForm.value.connectParams,
                authType: addDataSourceForm.value.authType,
                appid: addDataSourceForm.value.appid,
                objectid: addDataSourceForm.value.objectid,
                mkPrivate: addDataSourceForm.value.mkPrivate,
            },
            dataSourceTypeId: addDataSourceForm.value.dataSourceType,
        };
    }
    try {
        await fetchAddDataSource(url, params);
    } catch (error) {
        console.log('error: ', error);
        FMessage.error(error);
    }
};
// true-指标详情，false-指标编辑
const isDetail = ref(true);
// 新增弹框 保存按钮
const confirmAddDataSource = async () => {
    // eslint-disable-next-line no-use-before-define
    if (!isDetail.value) return saveEditing();
    addDataSourceFormRef.value.validate().then(async () => {
        const url = `/api/v1/projector/meta_data/data_source/create?proxyUser=${queryData.value.proxyUser}`;
        const subSystemId = addDataSourceForm.value.subSystem;
        const subSystemName = subSystemList.value.find(v => v.value === subSystemId)?.enName;
        const params = {
            dataSourceName: addDataSourceForm.value.dataSourceName,
            dataSourceDesc: addDataSourceForm.value.dataSourceDescription,
            labels: addDataSourceForm.value.label,
            connectParams: {
                host: addDataSourceForm.value.host,
                port: addDataSourceForm.value.port,
                username: addDataSourceForm.value.username,
                password: addDataSourceForm.value.password,
                subSystem: subSystemName || '',
                dataSourceTypeId: addDataSourceForm.value.dataSourceType,
                params: addDataSourceForm.value.connectParams,
                authType: addDataSourceForm.value.authType,
                appid: addDataSourceForm.value.appid,
                objectid: addDataSourceForm.value.objectid,
                mkPrivate: addDataSourceForm.value.mkPrivate,
            },
            createSystem: 'Linkis',
            dataSourceTypeId: addDataSourceForm.value.dataSourceType,
        };
        try {
            const { res } = await fetchAddDataSource(url, params);
            await afterAddDataSource(res?.insertId, 'add', subSystemName);
            FMessage.success($t('toastSuccess.addSuccess'));
            showAddDataSourceModal.value = false;
            // 刷新表格，将新增数据源的集群和代理用户赋值到查询条件中，代理用户为空时会查询当前登录用户的数据,
            // [hadoop]代理用户下可查询指定集群下全量数据源数据
            getTasksData(0, tasksPagination.value.size);
            resetAddDataSourceForm();
        } catch (error) {
            console.log('error: ', error);
            FMessage.error(error);
        }
    });
};
// 新增数据源 结束


// 数据源详情/编辑相关 开始
const dataSourceFormRef = ref();
const showDataSourceModal = ref(false);

const openEditDataSourceModal = async () => {
    showDataSourceModal.value = false;
    isDetail.value = false;
    if (subSystemList.value.length === 0) await getSubSystemInfo();
    showAddDataSourceModal.value = true;
};

const editRuleValidate = ref({ // 表单验证规则
    dataSourceType: [
        { required: true, message: $t('common.notEmpty') },
    ],
    dataSourceName: [
        { required: true, message: $t('common.notEmpty') },
    ],
    host: [
        { required: true, message: $t('common.notEmpty') },
    ],
    port: [
        { required: true, message: $t('common.notEmpty') },
    ],
    username: [
        { required: true, message: $t('common.notEmpty') },
        // { pattern: /^[a-zA-Z]+$/, message: $t('indexManagement.pleaseEnterEnChar') },
    ],
    password: [{ required: true, message: $t('common.notEmpty') }],
    authType: [{ required: true, message: $t('common.notEmpty') }],
    appid: [{ required: true, message: $t('common.notEmpty') }],
    objectid: [{ required: true, message: $t('common.notEmpty') }],
    mkPrivate: [{ required: true, message: $t('common.notEmpty') }],
});

// 指标详情 删除
const confirmDeleteMetric = async () => {
    const id = addDataSourceForm.value.id;
    // TODOS：现在还没有删除接口
    FMessage.warn('暂无删除功能');
};

const showFModal = (type) => {
    FModal[type]({
        title: $t('dataSourceManagement.confirmDelete'),
        okText: $t('common.delete'),
        onOk() {
            confirmDeleteMetric();
        },
        onCancel() {
        },
    });
};

const getAuthType = (value) => {
    if (value === 'accountPwd') return '账户密码';
    if (value === 'dpm') return '密码管家';
    return '';
};

// 编辑弹框 取消按钮
const cancelEditing = () => {
    showDataSourceModal.value = false;
    isDetail.value = true;
};

// 编辑弹框 保存按钮
const saveEditing = () => {
    addDataSourceFormRef.value.validate().then(async () => {
        const url = `/api/v1/projector/meta_data/data_source/modify?proxyUser=${queryData.value.proxyUser}&dataSourceId=${trData.value.id}`;
        const subSystemId = addDataSourceForm.value.subSystem;
        const subSystemName = subSystemList.value.find(v => v.value === subSystemId)?.enName;

        const params = {
            dataSourceName: addDataSourceForm.value.dataSourceName,
            dataSourceDesc: addDataSourceForm.value.dataSourceDescription,
            labels: addDataSourceForm.value.label,
            connectParams: {
                host: addDataSourceForm.value.host,
                port: addDataSourceForm.value.port,
                username: addDataSourceForm.value.username,
                password: addDataSourceForm.value.password,
                subSystem: subSystemName || '',
                dataSourceTypeId: addDataSourceForm.value.dataSourceType,
                connectParams: addDataSourceForm.value.connectParams,
                authType: addDataSourceForm.value.authType,
                appid: addDataSourceForm.value.appid,
                objectid: addDataSourceForm.value.objectid,
                mkPrivate: addDataSourceForm.value.mkPrivate,
            },
            createSystem: 'Linkis',
            dataSourceTypeId: addDataSourceForm.value.dataSourceTypeId,
        };

        try {
            const { res } = await fetchEditDataSource(url, params);
            await afterAddDataSource(res?.updateId, 'edit', subSystemName);
            showAddDataSourceModal.value = false;
            FMessage.success($t('toastSuccess.editSuccess'));
            getTasksData(0, tasksPagination.value.size);
            isDetail.value = true;
            resetAddDataSourceForm();
        } catch (error) {
            console.log('error: ', error);
            FMessage.error(error);
        }
    });
};

// 数据源详情/编辑相关 结束

// 查询数据源详情数据 版本-查看、操作-详情
const getDataSourceDetailByVersionId = async (versionId = '') => {
    try {
        const urlParams = {
            proxyUser: queryData.value.proxyUser || '',
            dataSourceId: trData.value.id || '',
        };
        if (!urlParams.dataSourceId) return;
        const url = `/api/v1/projector/meta_data/data_source/info/detail?proxyUser=${urlParams.proxyUser}&dataSourceId=${urlParams.dataSourceId}&versionId=${versionId}`;
        const { res } = await fetchDataSourceDetail(url);
        if (res && res.info) {
            curDataSourceDetail.value = {
                // 代理用户 用的查询框数据，有些怪
                proxyUser: queryData.value.proxyUser || '',
                // 数据源id
                dataSourceId: trData.value.id,
                // 数据源类型
                dataSourceType: getDataSourceType(res.info.dataSourceTypeId),
                dataSourceTypeId: res.info.dataSourceTypeId,
                // 数据源名称
                dataSourceName: res.info.dataSourceName || '',
                // 数据源描述
                dataSourceDescription: res.info.dataSourceDesc || '',
                // 关联子系统
                subSystem: res.info.subSystem || '',
                // 标签
                labels: res.info.labels || [],
                // 录入方式
                inputType: res.info.inputType || 1,
                // 认证方式
                verifyType: res.info.verifyType || 1,
                // 共享登陆时共用的连接参数-登录认证
                connectParams: res.info.connectParams,
                // 数据源环境列表
                dataSourceEnvs: res.info.dataSourceEnvs,
                // DCN 多环境是Tree选择器对象数据
                dcnSequence: res.info.dcnSequence,
                // 操作权限
                is_editable: res.info.is_editable,
                // 开发科室
                dev_department_name: res.info.dev_department_name?.split(' ') || [],
                dev_department_id: res.info.dev_department_id || '',
                // 运维科室
                ops_department_name: res.info.ops_department_name?.split(' ') || [],
                ops_department_id: res.info.ops_department_id || '',
                // 可见范围
                visibility_department_list: res.info.visibility_department_list || [],
                action_range: res.info.visibility_department_list?.map(item => item.name) || [],
            };
        }
        console.log('curDataSourceDetail传', curDataSourceDetail.value);
    } catch (error) {
        console.log('error: ', error);
    }
};

// 操作-过期时，进行二次确认
const showExpireFmodalShow = ref(false);
const handleExpire = async () => {
    const url = `/api/v1/projector/meta_data/data_source/expire?proxyUser=${queryData.value.proxyUser}&dataSourceId=${trData.value.id}`;
    const { res } = await fetchExpireDataSource(url);
    FMessage.success($t('toastSuccess.editSuccess'));
    showExpireFmodalShow.value = false;
    getTasksData(0, tasksPagination.value.size);
};

// 点击表格里的更多里的操作
const spinShow = ref(false);
const clickTableMore = async (value, row) => {
    if (value === 'detail') {
        if (subSystemList.value.length === 0) await getSubSystemInfo();
        // 数据源详情查看时不带版本id
        spinShow.value = true; // 数据请求太慢给个加载中提示
        await getDataSourceDetailByVersionId();
        spinShow.value = false;
        showAddDataSourceModal.value = true;
        curMode.value = 'preview';
        curSelectedDataSourceId.value = trData.value.id;
    }
    if (value === 'expire') {
        // 过期
        try {
            showExpireFmodalShow.value = true;
        } catch (error) {
            console.log('error: ', error);
        }
    }
    if (value === 'testConnection') {
        // 测试连接
        try {
            const url = `/api/v1/projector/meta_data/data_source/info/detail?proxyUser=${queryData.value.proxyUser}&dataSourceId=${trData.value.id}`;
            const { res } = await fetchDataSourceDetail(url);
            if (res && res.info) {
                const url2 = `/api/v1/projector/meta_data/data_source/connect?proxyUser=${queryData.value.proxyUser}`;
                await fetchTestConnection(url2, res.info);
                FMessage.success($t('dataSourceManagement.connectSuccessfully'));
            }
        } catch (error) {
            console.log('error: ', error);
        }
    }
};

// 版本列表 drawer 开始

const publishDataSource = async (id) => {
    try {
        const url = `/api/v1/projector/meta_data/data_source/publish?proxyUser=${queryData.value.proxyUser}&dataSourceId=${trData.value.id}&versionId=${id}`;
        await fetchPublishDataSource(url);
        await getTasksData(tasksPagination.value.current - 1, tasksPagination.value.size);
        trData.value = tasksData.value.find(v => v.id === trData.value.id);
        await getVersionListTableData(trData.value.id);
        FMessage.success($t('toastSuccess.publishSuccess'));
    } catch (error) {
        console.log('error: ', error);
    }
};

const viewDataSource = async (id) => {
    showAddDataSourceModal.value = true;
    curMode.value = 'versionPreview';
    await getDataSourceDetailByVersionId(id);
};

const testConnection = async (id) => {
    try {
        const url = `/api/v1/projector/meta_data/data_source/info/detail?proxyUser=${queryData.value.proxyUser}&dataSourceId=${trData.value.id}&versionId=${id}`;
        const { res } = await fetchDataSourceDetail(url);
        if (res && res.info) {
            const url2 = `/api/v1/projector/meta_data/data_source/connect?proxyUser=${queryData.value.proxyUser}`;
            await fetchTestConnection(url2, res.info);
            FMessage.success($t('dataSourceManagement.connectSuccessfully'));
        }
    } catch (error) {
        console.log('error: ', error);
    }
};
// 版本列表 drawer 结束

</script>
<style lang="less" scoped>
.dashboard {
    .table-container {
        .table-operation-item {
            padding: 0;
            width: 100%;
            min-width: 100%;
            border: 0;
            font-family: PingFangSC-Regular;
            font-size: 14px;
            color: #646670;
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
        width: 200px;
    }
}

.detail-modal-item {
    margin-bottom: 16px;
    &:last-child {
        margin-bottom: 0;
    }
    .label {
        display: inline-block;
        width: 100px;
        text-align: right;
        margin-right: 16px;
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

    .table-operation-item {
        padding: 0;
        width: 100%;
        min-width: 100%;
        border: 0;
        font-family: PingFangSC-Regular;
        font-size: 14px;
        color: #646670;
        letter-spacing: 0;
        line-height: 22px;
        font-weight: 400;
        cursor: pointer;
        color: #5384FF;
        margin-right: 16px;
        &:hover {
            opacity: 0.9;
        }
        &:last-of-type{
            margin-right: 0;
        }
    }
</style>

<config>
{
    "name": "dataSourceManagement",
    "title": "$dataSourceManagement.title"
}
</config>
