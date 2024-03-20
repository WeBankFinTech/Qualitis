<template>
    <div style="padding: 16px 16px 32px">
        <BTablePage :isLoading="showLoading" actionType="loading" :loadingText="{ loading: '' }" :isDivider="false">
            <template v-slot:search>
                <Operation
                    ref="searchModal"
                    :form-model="queryFormModel"
                    :clusters="clusters"
                    :dataSourceTypes="dataSourceTypes"
                    :databases="databases"
                    :tables="tables"
                    :dcns="dcns"
                    :advanceQueryData="advanceQueryData"
                    :subSystems="subSystems"
                    :dataLabels="dataLabels"
                    :devDivisions="devDivisions"
                    :loadDevDivisions="loadDevDivisions"
                    :busDivisions="busDivisions"
                    :loadBusDivisions="loadBusDivisions"
                    @on-search="handleSearch"
                    @on-advance-search="handleAdvanceSearch"
                    @on-cancel-advance-search="handleCancelAdvanceSearch"
                    @on-show-advance-search="handleShowAdvanceSearch"
                    @on-reset="reset"
                    @on-metadataUpdate="metadataUpdate" />
            </template>
            <template v-slot:operate>
                <FSpace size="middle">
                    <FDropdown trigger="hover" :options="moreMenus" @click="ruleOperationActionHandler">
                        <FButton><MoreCircleOutlined />{{$t('myProject.more')}}</FButton>
                    </FDropdown>
                </FSpace>
            </template>
            <template v-slot:table>
                <f-table row-key="table_name" :data="tableData ? tableData : []">
                    <template #empty>
                        <BPageLoading :actionType="resultByInit ? 'emptyInitResult' : 'emptyQueryResult'" />
                    </template>
                    <f-table-column :visible="checkTColShow('cluster_name')" :formatter="formatterEmptyValue" ellipsis prop="cluster_name" :label="$t('common.cluster')" :width="140" />
                    <f-table-column :visible="checkTColShow('datasource_name')" :formatter="formatterEmptyValue" ellipsis prop="datasource_name" :label="$t('ruleTemplatelist.dataSourceType')" :width="140" />
                    <f-table-column :visible="checkTColShow('db_name')" :formatter="formatterEmptyValue" ellipsis prop="db_name" :label="$t('common.database')" :width="180" />
                    <f-table-column #default="{ row = {}}" :visible="checkTColShow('table_name')" ellipsis prop="table_name" :label="$t('common.table')" :width="200">
                        <span v-if="row.table_name" class="a-link" @click="handleRowClick(row)">{{row.table_name}}</span>
                        <span v-else>--</span>
                    </f-table-column>
                    <f-table-column :visible="checkTColShow('sub_system_name')" :formatter="formatterEmptyValue" ellipsis prop="sub_system_name" :label="$t('common.subSystem')" :width="120" />
                    <f-table-column :visible="checkTColShow('tag_name')" :formatter="formatterEmptyValue" ellipsis prop="tag_name" :label="$t('common.dataLabel')" :width="120" />
                    <f-table-column :visible="checkTColShow('dev_department_name')" :formatter="formatterEmptyValue" ellipsis prop="dev_department_name" :label="$t('common.developDepartment')" :width="120">
                        <template #default="{ row }">{{row.dev_department_name ? row.dev_department_name : '--'}}</template>
                    </f-table-column>
                    <f-table-column :visible="checkTColShow('department_name')" :formatter="formatterEmptyValue" ellipsis prop="department_name" :label="$t('common.bussinessDepartment')" :width="120" />
                    <f-table-column :visible="checkTColShow('table_commit')" :formatter="formatterEmptyValue" ellipsis prop="table_commit" :label="$t('common.description')" :width="120" />
                    <f-table-column :visible="checkTColShow('envName')" :formatter="formatterEmptyValue" ellipsis prop="envName" :label="$t('ruleQuery.dcn')" :width="120" />
                    <f-table-column #default="{ row = {}}" :label="$t('common.operate')" align="center" fixed="right" :width="66">
                        <FDropdown trigger="focus" :options="tableOperations" @click="handleTableOperation($event, row)">
                            <FButton class="table-operation-item">
                                <MoreCircleOutlined />
                            </FButton>
                        </FDropdown>
                    </f-table-column>
                </f-table>
            </template>
            <template v-slot:pagination>
                <FPagination
                    v-show="tableData && tableData.length > 0"
                    v-model:currentPage="pagination.current"
                    v-model:pageSize="pagination.size"
                    show-size-changer
                    show-total
                    :total-count="pagination.total"
                    @change="changePage"
                    @pageSizeChange="changePage"></FPagination>
            </template>
        </BTablePage>
        <RelatedRuleDrawer
            v-model:show="showTableRuleDrawer"
            :title="$t('ruleQuery.verifyRule')"
            :cluster="tableRuleDrawerParams.cluster"
            :db="tableRuleDrawerParams.db"
            :table="tableRuleDrawerParams.table"
            :datasource_id="tableRuleDrawerParams.datasource_id"
            mode="table"
        />
        <!--设置表格-->
        <BTableHeaderConfig
            v-model:headers="tableHeaders"
            v-model:show="showTableHeaderConfig"
            :originHeaders="originTableHeaders"
            type="query_rule"
        />
    </div>
</template>
<script setup>
import {
    ref, reactive, onMounted, watch,
} from 'vue';
import { useRouter, useRoute, useI18n } from '@fesjs/fes';
import { getURLQueryParams, formatterEmptyValue } from '@/common/utils';
import { CONDITIONBUTTONSPACE } from '@/assets/js/const';

import { FMessage } from '@fesjs/fes-design';
import { MoreCircleOutlined } from '@fesjs/fes-design/es/icon';
import useDivisions from '@/hooks/useDivisions';
import {
    BTableHeaderConfig, BPageLoading, BTablePage, BSearch,
} from '@fesjs/traction-widget';
import useTableHeaderConfig from '@/hooks/useTableHeaderConfig';
import Operation from './components/operation';
import useRuleQueryInit from './hooks/useRuleQueryInit';
import {
    fetchRuleQueryData, fetchAllRuleQueryData, fetctmetadataSync, fetctBloodParams,
} from './api';
import RelatedRuleDrawer from './components/relatedRuleDrawer';

const { t: $t } = useI18n();
const router = useRouter();
const route = useRoute();
const showLoading = ref(false);
const {
    divisions: devDivisions,
    loadDivisions: loadDevDivisions,
} = useDivisions();
const busDivisions = devDivisions;
const loadBusDivisions = loadDevDivisions;
const {
    clusters,
    dataSourceTypes,
    databases,
    tables,
    dcns,
    subSystems,
    dataLabels,
} = useRuleQueryInit();

const initAdvanceQueryData = () => ({
    datasourceType: '',
    cluster: '',
    envName: '',
    db: '',
    table: '',
    sub_system_id: '',
    dev_department_name: '',
    department_name: '',
    tag_code: '',
    filterFlag: false,
});
const advanceQueryData = reactive(initAdvanceQueryData());
const advanceQueryDataCopy = ref(initAdvanceQueryData());
const resetAdvanceQueryData = () => { Object.assign(advanceQueryData, initAdvanceQueryData()); };
const initQueryData = () => ({
    cluster: '',
    envName: '',
    dataSourceType: '',
    db: '',
    table: '',
});
const queryFormModel = reactive(initQueryData());

let searchType = '1';
let cachedTable = [];
let cachedTotalCount = 0;
const tableData = ref([]);
const pagination = reactive({
    current: 1,
    size: 10,
    total: 0,
});
const validadvanceQueryData = () => {
    Object.keys(initAdvanceQueryData()).forEach((key) => {
        if (!advanceQueryData[key]) {
            advanceQueryData[key] = '';
        }
    });
};

const showTableRuleDrawer = ref(false);
const tableRuleDrawerParams = reactive({
    cluster: '',
    db: '',
    table: '',
    datasource_id: '',
});
const handleTableRule = (row) => {
    console.log('handleTableRule', row);
    tableRuleDrawerParams.cluster = row.cluster_name;
    tableRuleDrawerParams.db = row.db_name;
    tableRuleDrawerParams.table = row.table_name;
    tableRuleDrawerParams.datasource_id = row.datasource_id;
    showTableRuleDrawer.value = true;
};

const handleRowClick = async (row) => {
    console.log('handleRowClick');
    const {
        cluster_name: cluster,
        datasource_id: dataSourceId,
        db_name: db,
        table_name: table,
    } = row;
    const params = {
        cluster,
        dataSourceId,
        db,
        table,
    };
    router.push({ path: '/rules/query/detail', query: params });
};

const updateTableData = (queryRes) => {
    let totalCount = 0;
    let content = null;
    totalCount = queryRes.totalCount;
    content = queryRes.content;
    tableData.value = content;
    tableData.value.forEach((item) => {
        if (item.datasource_type) {
            item.datasource_name = dataSourceTypes.find(e => e.value === item.datasource_type).label;
        }
    });
    cachedTable = content;
    pagination.total = totalCount;
    cachedTotalCount = totalCount;
    if (!tableData.value) {
        tableData.value = [];
    }
};
const resultByInit = ref(true); // 表格的结果是否是初始状态，搜索后该值变为false
const search = async () => {
    try {
        searchType = '1';
        const params = {
            ...queryFormModel,
            datasourceType: queryFormModel.dataSourceType,
            page: pagination.current - 1,
            size: pagination.size,
        };
        delete params.dataSourceType;
        tableData.value = [];
        pagination.total = 0;
        showLoading.value = true;
        // 首次进来查询所有数据

        // if (all) {
        //     const AllResult = await fetchAllRuleQueryData(params);
        //     totalCount = AllResult.totalCount;
        //     content = AllResult.content;
        // } else {
        // }
        // 重置高级筛选框数据，避免混淆
        Object.assign(advanceQueryData, initAdvanceQueryData());
        advanceQueryDataCopy.value = { ...advanceQueryData };
        const queryResult = await fetchRuleQueryData(params);
        updateTableData(queryResult);
        showLoading.value = false;
        resultByInit.value = false;
    } catch (e) {
        console.error(e);
        showLoading.value = false;
    }
};
const handleSearch = () => {
    pagination.current = 1;
    search();
};
// 外部重置，清理外部查询栏和高级筛选中数据
const reset = async () => {
    Object.assign(queryFormModel, initQueryData());
    Object.assign(advanceQueryData, initAdvanceQueryData());
    advanceQueryDataCopy.value = { ...advanceQueryData };
    pagination.current = 1;
    await search();
    resultByInit.value = true;
};

// 高级筛选
const searchModal = ref(null);
const handleShowAdvanceSearch = () => {
    Object.keys(queryFormModel).forEach((key) => {
        if (queryFormModel[key]) {
            if (key !== 'dataSourceType') {
                advanceQueryData[key] = queryFormModel[key];
            } else {
                advanceQueryData.datasourceType = dataSourceTypes.find(item => item.value === queryFormModel[key]).value;
            }
        }
    });
    console.log(advanceQueryData);
};
const advanceSearch = async () => {
    searchType = '2';
    validadvanceQueryData();
    advanceQueryDataCopy.value = { ...advanceQueryData };
    const params = {
        ...advanceQueryData,
        page: pagination.current - 1,
        size: pagination.size,
    };
    // replace过滤掉2级节点追加的code
    if (Array.isArray(advanceQueryData.department_name)) {
        params.department_name = advanceQueryData.department_name.join('-').replace(/[0-9]/g, '');
    }
    if (Array.isArray(advanceQueryData.dev_department_name)) {
        params.dev_department_name = advanceQueryData.dev_department_name.join('-').replace(/[0-9]/g, '');
    }
    try {
        showLoading.value = true;
        // 重置下顶部查询栏数据，避免混淆
        Object.assign(queryFormModel, initQueryData());
        const advanceQueryResult = await fetchRuleQueryData(params);
        updateTableData(advanceQueryResult);
        if (advanceQueryResult.totalCount === 0) {
            FMessage.success($t('toastSuccess.noResult'));
        } else {
            FMessage.success($t('toastSuccess.search'));
        }
        searchModal.value.closeAdvanceModal();
        resultByInit.value = false;
        showLoading.value = false;
    } catch (e) {
        searchModal.value.closeAdvanceModal();
        showLoading.value = false;
    }
};
const handleAdvanceSearch = async () => {
    pagination.current = 1;
    await advanceSearch();
    if (advanceQueryData.filterFlag && tableData.value) {
        tableData.value = tableData.value.filter(item => Number.parseInt(item.rule_count) > 0);
        pagination.total = tableData.value.length;
    }
    // } else {
    //     tableData.value = cachedTable;
    //     pagination.total = cachedTotalCount;
    // }
};
const changePage = () => {
    if (searchType === '1') {
        search();
    }
    if (searchType === '2') {
        advanceSearch();
    }
    // 这个地方只是重写url，并不会重新加载
//     router.replace(`${route.path}?${getURLQueryParams({
//         query: route.query,
//         params: { page: pagination.current, pageSize: pagination.size },
//     })}`);
//     // 回到列表顶部
//     document.getElementsByClassName('wd-content')[0].scrollTop = 0;
};
const metadataUpdate = async () => {
    try {
        const res = await fetctmetadataSync();
        if (res && res.msg) {
            if (res.data && res.data.type === 1) {
                FMessage.success(res.msg);
            }
            if (res.data && res.data.type === 2) {
                FMessage.info(res.msg);
            }
        }
    } catch (e) {}
};
const handleCancelAdvanceSearch = () => {
    Object.assign(advanceQueryData, advanceQueryDataCopy.value);
    searchModal.value.closeAdvanceModal();
};

const moreMenus = ref([
    { label: $t('metadataManagement.metadataUpdate'), value: '1' },
    { label: $t('common.setTableHeaderConfig'), value: '2' },
]);

const originTableHeaders = [
    { prop: 'cluster_name', label: $t('common.cluster') },
    { prop: 'datasource_name', label: $t('ruleTemplatelist.dataSourceType') },
    { prop: 'db_name', label: $t('common.database') },
    { prop: 'table_name', label: $t('common.table') },
    { prop: 'sub_system_name', label: $t('common.subSystem') },
    { prop: 'tag_name', label: $t('common.dataLabel') },
    { prop: 'dev_department_name', label: $t('common.developDepartment') },
    { prop: 'department_name', label: $t('common.bussinessDepartment') },
    { prop: 'table_commit', label: $t('common.description') },
    { prop: 'envName', label: $t('ruleQuery.dcn') },
];

// 表头设置
const {
    checkTColShow,
    toggleTColConfig,
    tableHeaders,
    showTableHeaderConfig,
} = useTableHeaderConfig();
const ruleOperationActionHandler = (value) => {
    switch (value) {
        case '1':
            metadataUpdate();
            break;
        case '2':
            toggleTColConfig();
            break;
        default:
            break;
    }
};
const bloodUrl = ref('');
const bloodUrlParams = reactive({});

// 判断是否嵌套在dms里，是的话新窗口打开，避免套娃
const isEmbedDMS = () => {
    // eslint-disable-next-line no-restricted-globals
    if (window.__MICRO_APP_ENVIRONMENT__) {
        const baseUrlData = window.microApp.getData();
        if (baseUrlData) {
            return baseUrlData?.parents === 'dms';
        }
    }
    // eslint-disable-next-line no-restricted-globals
    console.log('self.location', self.location);
    // if (window.__MICRO_APP_ENVIRONMENT__ && self.location.href.includes('microApp/dqm')) {
    // eslint-disable-next-line no-restricted-globals
    if (self.location.href.includes('microApp/dqm')) {
        return true;
    }
    return false;
};

const dataLineageJumpBlood = async (row) => {
    const bloodParams = {
        cluster_name: row.cluster_name,
        db_name: row.db_name,
        table_name: row.table_name,
    };
    try {
        const res = await fetctBloodParams(bloodParams);
        const { urn } = res;
        const jumpUrn = urn?.split('&').slice(0, 2).join('&');
        // eslint-disable-next-line no-restricted-globals
        if (isEmbedDMS() && self.location.ancestorOrigins.length > 0) {
            // eslint-disable-next-line no-restricted-globals
            bloodUrl.value = `${self.location.ancestorOrigins[0]}/#/dataLineage?`;
            bloodUrl.value += getURLQueryParams({
                params: {
                    urn: jumpUrn,
                },
            });
            console.log('bloodUrl', bloodUrl.value);
            window.open(bloodUrl.value);
        } else {
            router.push({ path: '/rules/query/dataLineage', query: { urn: jumpUrn } });
        }
    } catch (e) {}
};
const dataAppLineageJumpBlood = async (row) => {
    const bloodParams = {
        cluster_name: row.cluster_name,
        db_name: row.db_name,
        table_name: row.table_name,
    };
    try {
        const res = await fetctBloodParams(bloodParams);
        const { urn } = res;
        const jumpUrn = urn?.split('/').slice(0, 4).join('/');
        const params = {
            clusterType: row.cluster_name,
            databaseName: row.db_name,
            datasetName: row.table_name,
            searchType: 'DATASET_FIND_FLOW',
            urn: jumpUrn,
        };
        // eslint-disable-next-line no-restricted-globals
        if (isEmbedDMS() && self.location.ancestorOrigins.length > 0) {
            // eslint-disable-next-line no-restricted-globals
            bloodUrl.value = `${self.location.ancestorOrigins[0]}/#/dataAppAnalysis?`;
            bloodUrl.value += getURLQueryParams({
                params,
            });
            console.log('bloodUrl', bloodUrl.value);
            window.open(bloodUrl.value);
        } else {
            router.push({ path: '/rules/query/appDataLineage', query: params });
        }
    } catch (e) {}
};

const tableOperations = ref([
    { label: $t('ruleQuery.verifyRule'), value: '3' },
]);
const handleTableOperation = (value, row) => {
    switch (value) {
        case '3':
            handleTableRule(row);
            break;
        default:
            break;
    }
};
onMounted(async () => {
    await search();
    resultByInit.value = true;
});
</script>
<style lang="less" scoped>
.space-16{
    display: inline-block;
    width: 16px;
}
</style>
<config>
{
    "name": "ruleQuery",
    "title": "$ruleQuery.ruleQuery"
}
</config>
<style lang="less" scoped>
@import "@/style/mixins";
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
    }
</style>
