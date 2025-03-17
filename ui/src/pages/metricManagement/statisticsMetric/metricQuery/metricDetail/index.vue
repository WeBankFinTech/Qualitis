<template>
    <div class="wd-content dashboard-content">
        <div class="wd-project-header">
            <LeftOutlined class="back" @click="back" />
            <div class="name">
                <div class="breadcrumb">
                    <span class="breadcrumb-item" @click="goQueryPage">{{$t('_.IMS管理页面')}}</span>
                    /
                    <span class="breadcrumb-item">{{$t('_.指标值详情')}}</span>
                </div>
            </div>
        </div>
    </div>
    <div class="wd-content-body">
        <div style="padding: 16px 16px 32px" class="dashboard">
            <BTablePage :isLoading="isLoading" :actionType="actionType">
                <!-- <template v-slot:search>
                    <BSearch v-model:form="searchForm" :isReset="false" @search="handleSearch">
                        <template ref="commonRef" v-slot:form>
                            <div>
                                <span class="condition-label">指标ID</span>
                                <FInput v-model="searchForm.metricId" style="width:320px" placeholder="请输入指标ID" clearable @input="inputChange">
                                </FInput>
                            </div>
                        </template>
                    </BSearch>
                </template> -->
                <template v-slot:table>
                    <div v-if="!isPageShow" style="height: 766px">
                        <FEmpty :image-style="{ width: '200px' }" :description="$t('_.暂无查询内容，请输入指标ID查询')" />
                    </div>
                    <div v-else>
                        <div class="ims-body">
                            <div class="time-line">
                                <div style="display: flex; align-items:center">
                                    <FDatePicker
                                        v-model="searchForm.time"
                                        style="width:246px"
                                        format="yyyy-MM-dd"
                                        class="date-picker"
                                        :shortcuts="rangeShortcuts"
                                        type="daterange"
                                        maxRange="1095D"
                                        @change="handleSearch"
                                    />
                                    <div style="margin-left: 20px">{{$t('_.相关指标：')}}</div>
                                    <FCheckbox v-model="colNum" :disabled="colDisable" @change="handleSearch">{{$t('_.表行数')}}</FCheckbox>
                                </div>
                                <div class="time-right">
                                    {{$t('_.缺数展示')}}<FSwitch v-model="missNum" @change="handleSearch">
                                        <template #active>{{nullNum}}</template>
                                    </FSwitch>
                                    <FButton @click="handleSearch">
                                        <template #icon>
                                            <RotateRightOutlined />
                                        </template>{{$t('_.刷新')}}
                                    </FButton>
                                    <!-- <FButton>相关曲线</FButton> -->
                                    <FButton @click="viewDSS">{{$t('_.查看源表')}}</FButton>
                                </div>
                            </div>
                            <div v-if="chartData.metrics && chartData.metrics?.length">
                                <ImsChart :chartData="chartData" :warnDatas="warnDatas" :showMiss="missNum" />
                            </div>

                            <FRadioGroup v-model="searchForm.type" style="margin-top:22px;margin-bottom:24px" :cancelable="false">
                                <FRadioButton value="metric">{{$t('_.指标数据')}}</FRadioButton>
                                <!-- <FRadioButton :value="2">检测策略</FRadioButton> -->
                                <!-- <FRadioButton value="alarm">告警数据</FRadioButton> -->
                            </FRadioGroup>
                            <div v-if="searchForm.type === 'metric'" style="margin-bottom:18px">
                                <FCheckbox v-model="isWarn">{{$t('_.只看异常告警数据')}}</FCheckbox>
                            </div>
                            <div v-if="searchForm.type === 'metric'">
                                <f-table ref="metricTable" row-key="datatime" :data="filteredImsDataslist">
                                    <f-table-column #default="{ row } = {}" sortable prop="datatime" :label="$t('_.数据时间')" align="left" :width="170">{{dayjs(row?.datatime).format('YYYY-MM-DD HH:mm') || '--'}}</f-table-column>
                                    <div v-for="(item, index) in tableLabels" :key="index">
                                        <f-table-column #default="{ row } = {}" :prop="item" :label="item" sortable align="left" :width="288">
                                            <div :class="[alarmDataLists.find((obj) => +obj.dataTime === +row.datatime && obj?.metricName === item) !== undefined ? 'red' : '']">{{row[item]}}</div>
                                        </f-table-column>
                                    </div>
                                </f-table>
                                <div v-if="imsDataslist && imsDataslist?.length > 20">
                                    <FButton v-if="!isShowAll" style="margin-top:18px" size="small" @click="showAll(isShowAll)">
                                        <template #icon> <DownOutlined /> </template>{{$t('_.展开全部')}}
                                    </FButton>
                                    <FButton v-else size="small" style="margin-top:18px" @click="showAll(isShowAll)">
                                        <template #icon> <UpOutlined /> </template>{{$t('_.收起全部')}}
                                    </FButton>
                                </div>
                            </div>
                            <!-- <div v-else>
                                <f-table :data="alarmDatatable">
                                    <f-table-column prop="alarmId" label="告警ID" align="left" :width="170" ellipsis />
                                    <f-table-column prop="alarmTitle" label="告警标题" align="left" :width="333" ellipsis />
                                    <f-table-column prop="alarmTime" label="告警时间" align="left" :width="232" ellipsis />
                                    <f-table-column #default="{ row } = {}" prop="dataTime" label="数据时间" align="left" :width="232" ellipsis>{{dayjs(row?.dataTime).format('YYYY-MM-DD') || '--'}}</f-table-column>
                                    <f-table-column prop="domainName" label="业务域" align="left" :width="230" ellipsis />
                                    <f-table-column prop="alarmLevel" label="告警级别" align="left" :width="138" ellipsis />
                                    <f-table-column prop="alarmStatus" label="告警状态" align="left" :width="138" ellipsis>
                                    </f-table-column>
                                </f-table>
                                <div class="table-pagination-container">
                                    <FPagination
                                        v-model:currentPage="pagination.page"
                                        v-model:pageSize="pagination.size"
                                        show-size-changer
                                        show-total
                                        :total-count="pagination.total"
                                        @change="changePage"></FPagination>
                                </div>
                            </div> -->
                        </div>
                    </div>
                </template>
            </BTablePage>
        </div>
    </div>
</template>
<script setup>

import {
    onMounted, ref, computed,
} from 'vue';
import {
    BTableHeaderConfig, BPageLoading, BTablePage, BSearch,
} from '@fesjs/traction-widget';
import {
    RotateRightOutlined, DownOutlined, UpOutlined, LeftOutlined,
} from '@fesjs/fes-design/icon';
import dayjs from 'dayjs';
import { cloneDeep } from 'lodash-es';
import { FMessage } from '@fesjs/fes-design';
import { useRoute, useRouter, useI18n } from '@fesjs/fes';
import ImsChart from './components/imsChart.vue';
import { getMetricData, getAlarmData, getMetricRelation } from './api';


const { t: $t } = useI18n();

const dssUrl = ref('');
const viewDSS = () => {
    const url = '';
    window.open(dssUrl.value);
};
const route = useRoute();
const router = useRouter();
const rangeShortcuts = {
    最近一周: [new Date().setDate(new Date().getDate() - 7), new Date().getTime()],
    最近两周: [new Date().setDate(new Date().getDate() - 14), new Date().getTime()],
    最近一个月: [new Date().setDate(new Date().getDate() - 31), new Date().getTime()],
    最近三个月: [new Date().setDate(new Date().getDate() - 90), new Date().getTime()],
    最近六个月: [new Date().setDate(new Date().getDate() - 183), new Date().getTime()],
    最近一年: [new Date().setDate(new Date().getDate() - 365), new Date().getTime()],
};
const isPageShow = ref(false);
const isLoading = ref(false);
const actionType = ref('loading');
const searchForm = ref({
    metricId: '',
    time: [], // new Date().setDate(new Date().getDate() - 90), new Date().getTime()
    type: 'metric',
});
const pagination = ref({
    page: 1,
    size: 20,
    total: 0,
});
const isWarn = ref(false);
const commonRef = ref(null);
const chartData = ref({
    title: '',
    metrics: [{
        metricName: '',
        lineDataList: {
            timestampList: [

            ],
            valueList: [

            ],
        },
    }],
});
const tableLabels = ref([]);
const echartsTitle = ref('');
const imsDataslist = ref([]);
const initFunc = () => {
    chartData.value = {};
    imsDataslist.value = [];
    // eslint-disable-next-line no-use-before-define
    warnDatas.value = [];
    echartsTitle.value = '';
};
const colNum = ref(false);
const missNum = ref(false);
const nullNum = ref(0);
const colDisable = ref(false);
const getMetricDatas = async () => {
    chartData.value = {};
    // eslint-disable-next-line no-use-before-define
    warnDatas.value = [];
    nullNum.value = 0;
    try {
        const params = {
            metricId: searchForm.value.metricId,
            startDate: +dayjs(searchForm.value.time[0]).format('YYYYMMDD'),
            endDate: +dayjs(searchForm.value.time[1]).format('YYYYMMDD'),
            requestSource: 'dqm',
        };
        if (colNum.value) {
            const { relationMetricId } = await getMetricRelation({ metricId: route.query.metricId });
            params.metricId = `${params.metricId},${relationMetricId}`;
        }
        const res = await getMetricData(params);
        chartData.value = res;
        // echartsTitle.value = res?.title || '';
        dssUrl.value = res?.dssUrl?.split(',')[1];
        tableLabels.value = res?.metrics?.map(item => item.metricName) || [];
        if (res?.metrics && res?.metrics.length) {
            res?.metrics?.forEach((item, index) => {
                const timestampList = item.lineDataList?.timestampList || [];
                const valueList = item.lineDataList?.valueList || [];
                if (index === 0) {
                    imsDataslist.value = timestampList.map(time => ({
                        datatime: time,
                    }));
                }
                // 如果当前指标是表行数指标，禁用选择相关指标“表行数”
                if (String(item.metricId) === route.query.metricId) colDisable.value = item.metricType === 5;
                imsDataslist.value = imsDataslist.value.map((obj, indexIn) => {
                    if (valueList[indexIn] === null) {
                        if (item.metricType !== 5 || res?.metrics.length === 1) nullNum.value += 1; // 表行数指标， 缺数不统计
                        return Object.assign({}, obj, { [item.metricName]: -1 });
                    }
                    return Object.assign({}, obj, { [item.metricName]: valueList[indexIn] });
                });
            });
            if (missNum.value) FMessage.info(`当前曲线缺少${nullNum.value}个数据点，缺少数据点值已展示为-1 `);
        } else {
            imsDataslist.value = [];
        }
    } catch (err) {
        initFunc();
    }
    // eslint-disable-next-line no-use-before-define
    getwarnDatas();
};
const handleSearch = async () => {
    if (!searchForm.value.metricId) {
        return FMessage.error($t('_.请输入指标查询'));
    }
    try {
        pagination.value.page = 1;
        // eslint-disable-next-line no-use-before-define
        // await handleChange();
        await getMetricDatas();
        isPageShow.value = true;
    } catch (err) {
        console.log(err);
    }
};
const alarmDataLists = ref([]);
const alarmDatatable = ref([]);
const changePage = () => {
    alarmDatatable.value = alarmDataLists.value.slice((pagination.value.page - 1) * pagination.value.size, pagination.value.page * pagination.value.size);
};
const warns = computed(() => alarmDataLists?.value?.map(item => ({
    dataTime: +item.dataTime,
    metricName: item.metricName,
})));

const isShowAll = ref(false);
const filteredImsDataslist = computed(() => (isWarn.value ? (isShowAll.value ? imsDataslist.value.filter(it => warns.value.some(obj => +obj.dataTime === +it.datatime && Object.keys(it).includes(obj.metricName))) : imsDataslist.value.filter(it => warns.value.some(obj => +obj.dataTime === +it.datatime && Object.keys(it).includes(obj.metricName))).slice(0, 20)) : isShowAll.value ? imsDataslist.value : imsDataslist.value.slice(0, 20)));
const showAll = (show) => {
    isShowAll.value = !show;
};
const warnDatas = ref([]);
const getwarnDatas = () => {
    const temp = imsDataslist.value.filter(it => warns.value.some(obj => +obj.dataTime === +it.datatime && Object.keys(it).includes(obj.metricName))) || [];
    temp?.forEach((item) => {
        const tempobj = warns.value.find(obj => +obj.dataTime === +item.datatime && Object.keys(item).includes(obj.metricName)) || null;
        if (tempobj) {
            const warnobj = {
                value: $t('_.告警'),
                xAxis: dayjs(item.datatime).format('YYYY-MM-DD'),
                yAxis: item[tempobj.metricName],
            };
            warnDatas.value.push(warnobj);
        }
    });
};

const handleChange = async (value) => {
    try {
        const params = {
            metricId: searchForm.value.metricId,
            startDate: +dayjs(searchForm.value.time[0]).format('YYYYMMDD'),
            endDate: +dayjs(searchForm.value.time[1]).format('YYYYMMDD'),
        };
        const res = await getAlarmData(params);
        console.log('alarm datas', res);
        alarmDataLists.value = res?.data?.alarmData || [];
        pagination.value.total = res?.data?.alarmData?.length || 0;
        alarmDatatable.value = alarmDataLists.value?.slice(0, pagination.value.size) || [];
    } catch (err) {
        alarmDataLists.value = [];
        initFunc();
    }
};
const inputChange = (e) => {
    if (!e) {
        isPageShow.value = false;
        tableLabels.value = [];
        alarmDataLists.value = [];
        initFunc();
    }
};
onMounted(() => {
    initFunc();
    if (route.query.metricId) {
        searchForm.value.metricId = route.query.metricId;
        if (route.query.dataDate) {
            const dataTime = dayjs(route.query.dataDate, 'YYYY-MM-DD').valueOf();
            searchForm.value.time = [dataTime - 183 * 24 * 60 * 60 * 1000, dataTime]; // 默认数据日期的前6个月
        }
        handleSearch();
    }
});
const back = () => {
    router.go(-1);
};
const goQueryPage = () => {
    router.push('/metricManagement/statisticsMetric/metricQuery');
};

</script>
<config>
{
    "name": "imetricManagement",
    "title": "IMS指标管理"
}
</config>
<style lang="less" scoped>
.dashboard {
        height: 100%;
    .wd-table-page{
        // height: 100%;
        .table-container {
            .mr10 {
                margin-right: 10px;
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
                &:hover {
                    color: #5384FF;
                }
            }
        }
    }
}
.ims-body {
    display: flex;
    flex-direction: column;
    .time-line{
        display: flex;
        justify-content: space-between;
        margin-bottom: 24px;
        .time-right {
            display: flex;
            column-gap: 16px;
            align-items: center;
        }
    }
}
.red {
    color: red;
}
.dashboard-content {
    padding-bottom: 0px;
    overflow: hidden;
}
.wd-content .wd-project-header {
    padding-bottom: 0px;
}
.breadcrumb {
    color: #63656f;
    .breadcrumb-item {
        cursor: pointer;
        &:hover {
            color: #0f1222;
        }
    }
}
.fes-radio-group>.fes-radio-button:last-child {
    border-radius: 4px 4px 4px 4px;
}
</style>
