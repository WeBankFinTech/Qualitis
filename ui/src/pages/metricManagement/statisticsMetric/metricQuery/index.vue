<template>
    <div style="padding: 16px 16px 16px">
        <BTablePage
            :isLoading="isLoading"
            :actionType="actionType"
            :loadingText="loadingText"
        >
            <template v-slot:search>
                <BSearch
                    v-model:form="searchForm"
                    v-model:advanceForm="advanceSearchForm"
                    :isAdvance="true"
                    @search="handleSearch"
                    @reset="handleReset"
                    @advance="toggleAdvanceQuery"
                >
                    <template v-slot:form>
                        <div>
                            <span class="condition-label">{{$t('_.数据源类型')}}</span>
                            <FSelect v-model="searchForm.datasource_type" :options="dataSourceTypes" filterable clearable :placeholder="$t('_.请选择')">
                            </FSelect>
                        </div>
                        <div>
                            <span class="condition-label">{{$t('_.集群')}}</span>
                            <FSelect v-model="searchForm.cluster_name" :options="clusters" filterable clearable :placeholder="$t('_.请选择')">
                            </FSelect>
                        </div>
                        <div>
                            <span class="condition-label">{{$t('_.数据库')}}</span>
                            <FSelect v-model="searchForm.database" :options="databases" filterable clearable :placeholder="$t('_.请选择')">
                            </FSelect>
                        </div>
                        <div>
                            <span class="condition-label">{{$t('_.数据表')}}</span>
                            <FSelect v-model="searchForm.table" :options="tables" filterable clearable :placeholder="$t('_.请选择')">
                            </FSelect>
                        </div>
                        <div>
                            <span class="condition-label">{{$t('_.字段')}}</span>
                            <FSelect v-model="searchForm.column" :options="columns" filterable clearable :placeholder="$t('_.请选择')">
                            </FSelect>
                        </div>
                        <div>
                            <span class="condition-label">{{$t('_.数据日期')}}</span>
                            <FDatePicker v-model="searchForm.gather_time" type="daterange" :maxDate="new Date()" class="w220" />
                        </div>
                    </template>
                </BSearch>
                <FModal :maskClosable="false" :width="600" :title="$t('_.高级筛选')" :show="showAdvanceQuery" @cancel="advanceCancel" @ok="advanceQuery">
                    <FForm :labelWidth="70" labelPosition="right">
                        <!-- <FFormItem label="指标ID">
                            <FInput v-model="advanceSearchForm.metric_id" placeholder="请输入"></FInput>
                        </FFormItem>
                        <FFormItem label="指标Value">
                            <FInput v-model="advanceSearchForm.metric_value" placeholder="请输入"></FInput>
                        </FFormItem> -->
                        <FFormItem :label="$t('_.数据源类型')">
                            <FSelect v-model="advanceSearchForm.datasource_type" :options="dataSourceTypes" filterable clearable :placeholder="$t('_.请选择')">
                            </FSelect>
                        </FFormItem>
                        <FFormItem :label="$t('_.集群')">
                            <FSelect v-model="advanceSearchForm.cluster_name" :options="clusters" filterable clearable :placeholder="$t('_.请选择')">
                            </FSelect>
                        </FFormItem>
                        <FFormItem :label="$t('_.数据库')">
                            <FSelect v-model="advanceSearchForm.database" :options="databases" filterable clearable :placeholder="$t('_.请选择')">
                            </FSelect>
                        </FFormItem>
                        <FFormItem :label="$t('_.数据表')">
                            <FSelect v-model="advanceSearchForm.table" :options="tables" filterable clearable :placeholder="$t('_.请选择')">
                            </FSelect>
                        </FFormItem>
                        <FFormItem :label="$t('_.字段')">
                            <FSelect v-model="advanceSearchForm.column" :options="columns" filterable clearable :placeholder="$t('_.请选择')">
                            </FSelect>
                        </FFormItem>
                        <FFormItem :label="$t('_.指标模板')">
                            <FSelect v-model="advanceSearchForm.template_id" :options="templates" filterable clearable :placeholder="$t('_.请选择')">
                            </FSelect>
                        </FFormItem>
                        <FFormItem :label="$t('_.采集人')">
                            <FSelect v-model="advanceSearchForm.data_user" :options="users" filterable clearable :placeholder="$t('_.请选择')">
                            </FSelect>
                        </FFormItem>
                        <FFormItem :label="$t('_.数据日期')">
                            <FDatePicker v-model="advanceSearchForm.gather_time" clearable type="daterange" :maxDate="new Date()" />
                        </FFormItem>
                    </FForm>
                </FModal>
            </template>
            <template v-slot:operate>
                <FButton @click="syncMetric">{{$t('_.同步指标')}}</FButton>
                <FButton @click="toggleTColConfig">{{$t('_.设置表格')}}</FButton>
            </template>
            <template v-slot:table>
                <f-table :data="tableData">
                    <f-table-column
                        #default="{ row } = {}"
                        :label="$t('_.指标ID')"
                        :width="116"
                        ellipsis
                        prop="metric_id"
                        :visible="checkTColShow('metric_id')"
                    >
                        <a style="cursor: pointer" @click="checkMetric(row)">{{row.metric_id || '- -'}}</a>
                    </f-table-column>
                    <f-table-column
                        prop="metric_value"
                        :label="$t('_.指标Value')"
                        :width="160"
                        ellipsis
                        :visible="checkTColShow('metric_value')"
                        :formatter="colFormatter"
                    />
                    <f-table-column
                        prop="metric_name"
                        :label="$t('_.指标名称')"
                        :width="160"
                        ellipsis
                        :visible="checkTColShow('metric_name')"
                        :formatter="colFormatter"
                    />
                    <f-table-column
                        prop="identify_value"
                        :label="$t('_.指标枚举值')"
                        :width="180"
                        ellipsis
                        :visible="checkTColShow('identify_value')"
                        :formatter="colFormatter"
                    />
                    <f-table-column
                        prop="datasource_type"
                        :label="$t('_.数据源类型')"
                        :width="116"
                        ellipsis
                        :visible="checkTColShow('datasource_type')"
                        :formatter="datasourceTypeFormatter"
                    />
                    <f-table-column
                        prop="cluster_name"
                        :label="$t('_.集群')"
                        :width="138"
                        ellipsis
                        :visible="checkTColShow('cluster_name')"
                        :formatter="colFormatter"
                    />
                    <f-table-column
                        prop="database"
                        :label="$t('_.数据库')"
                        :width="180"
                        ellipsis
                        :visible="checkTColShow('database')"
                        :formatter="colFormatter"
                    />
                    <f-table-column
                        prop="table"
                        :label="$t('_.数据表')"
                        :width="180"
                        ellipsis
                        :visible="checkTColShow('table')"
                        :formatter="colFormatter"
                    />
                    <f-table-column
                        prop="column"
                        :label="$t('_.字段')"
                        :width="180"
                        ellipsis
                        :visible="checkTColShow('column')"
                        :formatter="colFormatter"
                    />
                    <f-table-column
                        prop="template_name"
                        :label="$t('_.采集算子')"
                        :width="160"
                        ellipsis
                        :visible="checkTColShow('template_name')"
                        :formatter="colFormatter"
                    />
                    <f-table-column
                        prop="data_user"
                        :label="$t('_.采集人')"
                        :width="150"
                        ellipsis
                        :visible="checkTColShow('data_user')"
                        :formatter="colFormatter"
                    />
                    <f-table-column
                        prop="update_time"
                        :label="$t('_.采集时间')"
                        :width="160"
                        ellipsis
                        :visible="checkTColShow('update_time')"
                        :formatter="colFormatter"
                    />
                    <f-table-column
                        prop="data_date"
                        :label="$t('_.数据日期')"
                        :width="120"
                        ellipsis
                        :visible="checkTColShow('data_date')"
                        :formatter="colFormatter"
                    />
                    <f-table-column ellipsis prop="calculation_mode" :visible="checkTColShow('calculation_mode')" :label="$t('indexManagement.calculationMode')" :width="88">
                        <template #default="{ row }"> {{calculations.find(item => item.value === row.calculation_mode)?.label || '--'}} </template>
                    </f-table-column>
                    <f-table-column ellipsis prop="monitoring_capabilities" :visible="checkTColShow('monitoring_capabilities')" :label="$t('indexManagement.monitoringCapabilities')" :width="125">
                        <template #default="{ row }">
                            <div v-if="!row.monitoring_capabilities?.length">--</div>
                            <div v-else>
                                <FTag type="info" style="margin-right: 4px">{{row.monitoring_capabilities[0]}}</FTag>
                                <FPopper v-if="row.monitoring_capabilities.length - 1 > 0" trigger="hover">
                                    <div style="padding: 5px 10px; boder-raius: 4px;">
                                        <FTag v-for="(item, index) in row.monitoring_capabilities.slice(1)" :key="index" style="margin-right: 4px" type="info">{{item}}</FTag>
                                    </div>


                                    <template #trigger>
                                        <FTag type="info">+{{row.monitoring_capabilities.length - 1}}</FTag>
                                    </template>
                                </FPopper>
                            </div>
                        </template>
                    </f-table-column>
                    <f-table-column ellipsis prop="metric_definition" :visible="checkTColShow('metric_definition')" :label="$t('indexManagement.metricDefinition')" :width="160">
                        <template #default="{ row }">{{row.metric_definition || '--'}}</template>
                    </f-table-column>
                    <f-table-column ellipsis prop="business_domain" :visible="checkTColShow('business_domain')" :label="$t('indexManagement.businessDomain')" :width="116">
                        <template #default="{ row }">{{row.business_domain || '--'}}</template>
                    </f-table-column>
                    <f-table-column ellipsis prop="business_strategy" :visible="checkTColShow('business_strategy')" :label="$t('indexManagement.businessStrategy')" :width="160">
                        <template #default="{ row }">{{row.business_strategy || '--'}}</template>
                    </f-table-column>
                    <f-table-column ellipsis prop="business_system" :visible="checkTColShow('business_system')" :label="$t('indexManagement.businessSystem')" :width="160">
                        <template #default="{ row }">{{row.business_system || '--'}}</template>
                    </f-table-column>
                    <f-table-column ellipsis prop="business_model" :visible="checkTColShow('business_model')" :label="$t('indexManagement.businessModel')" :width="160">
                        <template #default="{ row }">{{row.business_model || '--'}}</template>
                    </f-table-column>
                    <f-table-column ellipsis prop="imsmetric_desc" :visible="checkTColShow('imsmetric_desc')" :label="$t('indexManagement.imsmetricDesc')" :width="160">
                        <template #default="{ row }">{{row.imsmetric_desc || '--'}}</template>
                    </f-table-column>
                </f-table>
            </template>
            <template v-slot:pagination>
                <FPagination
                    v-model:currentPage="pagination.page"
                    v-model:pageSize="pagination.size"
                    show-size-changer
                    show-total
                    :total-count="pagination.total"
                    @change="fetchTableData"
                />
            </template>
        </BTablePage>
        <BTableHeaderConfig
            v-model:originHeaders="originHeaders"
            v-model:headers="headers"
            v-model:show="showTableHeaderConfig"
            type="allocationProportionTable"
        />
    </div>
</template>
<script setup>

import { cloneDeep } from 'lodash-es';
import dayjs from 'dayjs';
import { BTablePage, BSearch, BTableHeaderConfig } from '@fesjs/traction-widget';
import {
    onMounted, ref, reactive,
} from 'vue';
import { useRouter, useI18n } from '@fesjs/fes';
import { FModal, FMessage, FPopper } from '@fesjs/fes-design';
import { colFormatter, datasourceTypeFormatter } from './utils';
import useMetricList from './useMetricList.js';
import { calculations } from '../../utils';
import { fetctmetricSync } from './api';

const { t: $t } = useI18n();


const router = useRouter();
const {
    clusters,
    dataSourceTypes,
    databases,
    tables,
    columns,
    tableData,
    users,
    templates,
    fetchTableData,
    updateTempParams,
    pagination,
    InitOptions,
} = useMetricList();
const tempParams = ref({});
// 原始表头
const originHeaders = [
    { label: $t('_.指标ID'), prop: 'metric_id' },
    { label: $t('_.指标Value'), prop: 'metric_value' },
    { label: $t('_.指标名称'), prop: 'metric_name' },
    { label: $t('_.指标枚举值'), prop: 'identify_value' },
    { label: $t('_.数据源类型'), prop: 'datasource_type' },
    { label: $t('_.集群'), prop: 'cluster_name' },
    { label: $t('_.数据库'), prop: 'database' },
    { label: $t('_.数据表'), prop: 'table' },
    { label: $t('_.字段'), prop: 'column' },
    { label: $t('_.采集算子'), prop: 'template_name' },
    { label: $t('_.采集人'), prop: 'data_user' },
    { label: $t('_.采集时间'), prop: 'update_time' },
    { label: $t('_.数据日期'), prop: 'data_date' },
    { prop: 'calculation_mode', label: $t('indexManagement.calculationMode') },
    { prop: 'monitoring_capabilities', label: $t('indexManagement.monitoringCapabilities') },
    { prop: 'metric_definition', label: $t('indexManagement.metricDefinition') },
    { prop: 'business_domain', label: $t('indexManagement.businessDomain') },
    { prop: 'business_strategy', label: $t('indexManagement.businessStrategy') },
    { prop: 'business_system', label: $t('indexManagement.businessSystem') },
    { prop: 'business_model', label: $t('indexManagement.businessModel') },
    { prop: 'imsmetric_desc', label: $t('indexManagement.imsmetricDesc') },
];
// 用于接收当前需要展示的表头的列表
const headers = ref([]);
const showTableHeaderConfig = ref(false);
// 判断表头是否展示
const checkTColShow = col => headers.value.map(item => item.prop).includes(col);
// 开始设置表格
const toggleTColConfig = () => {
    showTableHeaderConfig.value = true;
};

const isLoading = ref(false);
const actionType = ref('loading');
const loadingText = {
    loading: 'Loading. . .',
    emptyInitResult: $t('_.这里还没有数据'),
    emptyQueryResult: $t('_.没有符合条件的结果'),
};
const currentTimestamp = Date.now();
const defaultQueryCondition = () => ({ datasource_type: 1, cluster_name: clusters.value[0].value, gather_time: [dayjs(currentTimestamp).subtract(1, 'day').valueOf(), dayjs(currentTimestamp).valueOf()] });
const searchForm = ref({});
const advanceSearchForm = ref({});
const showAdvanceQuery = ref(false);
const toggleAdvanceQuery = () => {
    showAdvanceQuery.value = true;
    searchForm.value = defaultQueryCondition();

    // advanceSearchForm.value = Object.assign(advanceSearchForm.value, searchForm.value);
};
const advanceCancel = () => {
    showAdvanceQuery.value = false;
};
const advanceQuery = async () => {
    pagination.page = 1;
    updateTempParams(advanceSearchForm.value);
    await fetchTableData();
    showAdvanceQuery.value = false;
};
// 查询操作
// const handleCommonChange = async ($event, type) => {
//     columns.value = [];
//     const keys = Object.keys(advanceSearchForm.value);
//     for (let i = 0; i < keys.length; i++) {
//         delete advanceSearchForm.value[keys[i]];
//     }
//     if (['cluster_name', 'table', 'database'].includes(type) && searchForm.value.cluster_name && searchForm.value.table && searchForm.value.database) {
//         await getColumns(searchForm.value.cluster_name, searchForm.value.database, searchForm.value.table);
//     }
// };
// const handleColumnDepsChange = async () => {
//     columns.value = [];
//     if (advanceSearchForm.value.cluster_name && advanceSearchForm.value.table && advanceSearchForm.value.database) {
//         await getColumns(advanceSearchForm.value.cluster_name, advanceSearchForm.value.database, advanceSearchForm.value.table);
//     }
// };
const handleSearch = async () => {
    pagination.page = 1;
    advanceSearchForm.value = defaultQueryCondition();
    updateTempParams(searchForm.value);
    await fetchTableData();
};

// 重置操作
const handleReset = async () => {
    setTimeout(async () => {
        advanceSearchForm.value = defaultQueryCondition();
        searchForm.value = defaultQueryCondition();
        updateTempParams(defaultQueryCondition());
        pagination.page = 1;
        await fetchTableData();
    }, 0);
};
// 指标详情
const showMetricDetailDrawer = ref(false);
const checkMetric = (row) => {
    // router.push({
    //     path: '/metricManagement/statisticsMetric/metricQuery/metricDetail',
    //     query: {
    //         metricId: id,
    //     },
    // });
    const baseUrl = window.location.href.split('#')[0];
    window.open(`${baseUrl}#/metricManagement/statisticsMetric/metricQuery/metricDetail?metricId=${row.metric_id}&dataDate=${row.data_date}`, '_blank');
};
// 同步指标
const syncMetric = async () => {
    try {
        await fetctmetricSync();
        FMessage.success($t('metadataManagement.syncSuccess'));
    } catch (err) {
        console.error(err);
    }
};
onMounted(async () => {
    // await fetchTableData();
    await InitOptions();
    searchForm.value = defaultQueryCondition();
    advanceSearchForm.value = defaultQueryCondition();
    await handleSearch();
});

</script>
<style lang="less" scoped>
:deep(.w220) {
    width: 220px;
}
</style>
