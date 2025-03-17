<template>
    <div class="wd-content">
        <div class="wd-project-header">
            <LeftOutlined class="back" @click="back" />
            <div class="name">{{$t('ruleQuery.tableDetail')}} {{table}}</div>
        </div>
        <div class="wd-content-body">
            <div class="page-header-condition">
                <div class="condition-item">
                    <span class="condition-label">{{$t('metadataManagementPage.type')}}</span>
                    <FSelect v-model="searchForm.data_type" :options="columnTypeList" clearable :placeholder="$t('common.pleaseSelect')"></FSelect>
                </div>
                <div class="condition-item">
                    <span class="condition-label">{{$t('ruleQueryPage.partition')}}</span>
                    <FSelect v-model="searchForm.is_partition" :options="isPartitionList" clearable :placeholder="$t('common.pleaseSelect')"></FSelect>
                </div>
                <div class="condition-item">
                    <span class="condition-label">{{$t('ruleQueryPage.primaryKey')}}</span>
                    <FSelect v-model="searchForm.is_primary" :options="isKeysList" clearable :placeholder="$t('common.pleaseSelect')"></FSelect>
                </div>
                <div class="condition-item">
                    <span class="condition-label">{{$t('ruleQuery.fieldName')}}</span>
                    <FInput v-model="searchForm.column_name" :placeholder="$t('ruleQuery.fieldNameKey')"></FInput>
                </div>
                <div class="condition-item">
                    <FSpace :size="CONDITIONBUTTONSPACE">
                        <FButton type="primary" @click="filterSearch">{{$t('_.查询')}}</FButton>
                        <FButton @click="filterReset">{{$t('_.重置')}}</FButton>
                    </FSpace>
                </div>
                <div class="condition-item" style="margin-left:auto; display:flex">
                    <p class="filter-switch">
                        <span class="filter-switch-text">{{$t('ruleQuery.switchColumnTips')}}</span>
                        <FSwitch v-model="filterFlag" @change="changeFilterFlag" />
                    </p>
                </div>
            </div>
            <div v-if="showLoading">
                <BPageLoading :loadingText="{ loading: '' }" />
            </div>
            <f-table v-else row-key="column_name" :data="columns">
                <template #empty>
                    <BPageLoading :actionType="resultByInit ? 'emptyInitResult' : 'emptyQueryResult'" />
                </template>
                <f-table-column ellipsis prop="column_name" :label="$t('ruleQuery.fieldName')" :width="140" />
                <f-table-column ellipsis prop="data_type" :label="$t('metadataManagementPage.type')" :width="102" />
                <f-table-column ellipsis prop="is_partition" :formatter="booleanFormatter" :label="$t('ruleQueryPage.partition')" :width="88" />
                <f-table-column ellipsis prop="is_primary" :formatter="booleanFormatter" :label="$t('ruleQueryPage.primaryKey')" :width="88" />
                <f-table-column ellipsis prop="column_length" :label="$t('ruleQueryPage.negativeLength')" :width="88">
                    <template #default="{ row }">
                        {{row.column_length || '--'}}
                    </template>
                </f-table-column>
                <f-table-column ellipsis prop="column_comment" :label="$t('common.description')" :width="120">
                    <template #default="{ row }">
                        {{row.column_comment || '--'}}
                    </template>
                </f-table-column>
                <f-table-column #default="{ row = {}}" :label="$t('common.operate')" :width="128">
                    <FSpace :size="CONDITIONBUTTONSPACE">
                        <a class="a-link" @click="jumpBlood(row)"> {{$t('common.bloodRelationshipAnalysis')}} </a>
                        <a class="a-link" @click="handleRelatedRule(row)">{{`${$t('common.columnRule')}(${row?.rule_count || 0})`}}</a>
                    </FSpace>
                </f-table-column>
            </f-table>
        </div>
        <div class="table-pagination-container">
            <FPagination
                v-show="columns.length > 0"
                v-model:currentPage="pagination.current"
                v-model:pageSize="pagination.size"
                show-size-changer
                show-total
                :total-count="pagination.total"
                @change="changePage"
                @pageSizeChange="changePage"></FPagination>
        </div>
        <RelatedRuleDrawer
            v-if="showRelatedRuleDrawer"
            v-model:show="showRelatedRuleDrawer"
            :title="$t('common.columnRule')"
            :cluster="cluster"
            :db="db"
            :table="table"
            :column="currentColumn"
            mode="column" />
    </div>
</template>
<script setup>

import { ref, reactive, onMounted } from 'vue';
import {
    useI18n, useRouter, useRoute, request,
} from '@fesjs/fes';
import { LeftOutlined } from '@fesjs/fes-design/es/icon';
import { CONDITIONBUTTONSPACE } from '@/assets/js/const';
import { FDivider, FMessage } from '@fesjs/fes-design';
import { getURLQueryParams } from '@/common/utils';
import { BPageLoading } from '@fesjs/traction-widget';
import RelatedRuleDrawer from './components/relatedRuleDrawer';
import { fetchColumnsOfTableDetail, fetctBloodParams } from './api';

const { t: $t } = useI18n();
const router = useRouter();
const route = useRoute();

const showLoading = ref(false);
const {
    cluster = '',
    dataSourceId = '',
    db = '',
    table = '',
} = route.query;
const filterFlag = ref(false);
const showRelatedRuleDrawer = ref(false);
const columns = ref([]);
const currentColumn = ref(null);
let cachedColumns = [];
let cachedTotalCount = 0;
const pagination = reactive({
    current: 1,
    size: 10,
    total: 0,
});
// 等完整字段类型文档
const columnTypeList = [
    {
        value: 'bigint',
        label: 'bigint',
    },
    {
        value: 'int',
        label: 'int',
    },
    {
        value: 'string',
        label: 'string',
    },
    {
        value: 'long',
        label: 'long',
    },
    {
        value: 'double',
        label: 'double',
    },
];
const isPartitionList = [
    {
        value: false,
        label: $t('_.否'),
    },
    {
        value: true,
        label: $t('_.是'),
    },
];
const isKeysList = isPartitionList;
const searchForm = reactive({});

const booleanFormatter = ({ cellValue }) => (cellValue ? $t('common.yes') : $t('common.no'));

const updateColumnsDetail = async (params) => {
    console.log('updateColumnsDetail', params);
    try {
        showLoading.value = true;
        columns.value = [];
        cachedColumns = [];
        cachedTotalCount = 0;
        pagination.total = 0;
        const { content, totalCount } = await fetchColumnsOfTableDetail(params);
        if (Array.isArray(content)) {
            columns.value = content;
            cachedColumns = content;
            cachedTotalCount = totalCount;
            pagination.total = totalCount;
        }
        showLoading.value = false;
    } catch (e) {
        console.error(e);
        showLoading.value = false;
    }
};

const search = async () => {
    const params = {
        cluster,
        datasource_id: dataSourceId,
        db,
        table,
        page: pagination.current - 1,
        size: pagination.size,
    };
    updateColumnsDetail(params);
};
const changePage = () => {
    search();
};
const back = () => {
    if (window.history?.state?.back) {
        // 从项目里正常跳转进来
        router.back();
    } else {
        // 复制url直接进来
        router.replace('/projects');
    }
};
const changeFilterFlag = (flag) => {
    if (flag) {
        columns.value = columns.value.filter(item => Number.parseInt(item.rule_count) > 0);
        pagination.total = columns.value.length;
    } else {
        columns.value = cachedColumns;
        pagination.total = cachedTotalCount;
    }
};
const handleRelatedRule = (row) => {
    currentColumn.value = {
        columnName: row.column_name,
        dataType: row.data_type,
    };
    showRelatedRuleDrawer.value = true;
};
const resultByInit = ref(true); // 表格的结果是否是初始状态，搜索后该值变为false
const filterReset = () => {
    console.log('filterReset');
    searchForm.data_type = '';
    searchForm.is_partition = '';
    searchForm.is_primary = '';
    searchForm.column_name = '';
    resultByInit.value = true;
};
const filterSearch = async () => {
    console.log('filterSearch', searchForm);
    const params = {
        ...searchForm,
        cluster,
        datasource_id: dataSourceId,
        db,
        table,
        page: pagination.current - 1,
        size: pagination.size,
    };
    console.log('filterSearch', params);
    updateColumnsDetail(params);
    resultByInit.value = false;
};
const baseUrl = ref('');
const jumpBlood = async (row) => {
    const bloodParams = {
        cluster_name: route.query.cluster,
        db_name: route.query.db,
        table_name: route.query.table,
        column_name: row.column_name,

    };
    try {
        const res = await fetctBloodParams(bloodParams);
        let { urn } = res;
        urn = urn.split('&baseUrl')[0];
        // eslint-disable-next-line no-restricted-globals
        const bloodUrl = `${baseUrl.value}&${getURLQueryParams({
            params: {
                urn,
            },
        })}`;
        window.open(bloodUrl);
    } catch (e) {}
};

async function getBaseUrl() {
    const loginUserName = sessionStorage.getItem('firstUserName');
    // eslint-disable-next-line no-undef
    const microAppUrl = BASEMICROURL;
    function generateUrlTail() {
        const tail = [];
        tail.push('app_id=facade-framework');
        tail.push('timestamp=1659924920342');
        tail.push('nonce=12345');
        tail.push('user=dqm');
        tail.push('signature=eca1a93c2c2bb8fc55972d76d0c1267c7782f51552a5d4562a2d871a63f64168');
        return tail.join('&');
    }
    try {
        // 查询绑定信息
        const data = await request(`/mfgov/fesdk/bindQuery/v1?${generateUrlTail()}`, {
            main_mf_name: 'dqm_rule_management',
            sub_mf_name: 'data_lineage',
        }, {
            method: 'get',
            baseURL: microAppUrl,
            headers: {
                proxyUser: loginUserName,
            },
        });
        console.log(data);
        baseUrl.value = `${data.accessLocation}?baseUrl=${data.accessLocation.split('#')[0].replace(/\/$/, '')}` || '';
    } catch (err) {
        console.warn('-------', err);
    }
}

onMounted(() => {
    search();
    getBaseUrl();
});

</script>
<style lang="less" scoped>
@import "@/style/varible";
.space-16{
    display: inline-block;
    width: 16px;
}
.filter-switch {
    .filter-switch-text {
        margin-right: 16px;
    }
    line-height: 22px;
    text-align: right;
    font-size: 14px;
    font-weight: 400;
    color: @label-color;
}
</style>
