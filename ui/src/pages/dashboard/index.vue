<template>
    <article class="wd-content dashboard">
        <main>
            <section class="section">
                <h3 class="wd-content-title">{{$t('dashboard.todaySummary')}}</h3>
                <div class="wd-content-body">
                    <div class="summary-container">
                        <DashboardSummary v-bind="alarmsSummary" />
                    </div>
                    <div class="table-container">
                        <f-table :data="alarms">
                            <f-table-column prop="alarm_level" :label="$t('dashboard.level')" :formatter="alarmLevelFormatter" :width="88" ellipsis></f-table-column>
                            <f-table-column ellipsis prop="project_name" :label="$t('common.projectName')" :width="200"></f-table-column>
                            <f-table-column ellipsis prop="application_id" :label="$t('dashboard.number')" :width="164"></f-table-column>
                            <f-table-column ellipsis prop="application_begin_time" :label="$t('dashboard.startTime')" :width="184"></f-table-column>
                            <f-table-column ellipsis prop="application_end_time" :label="$t('dashboard.endTime')" :width="184"></f-table-column>
                            <template #empty>
                                <BPageLoading actionType="emptyInitResult" :loadingText="loadingText" />
                            </template>
                        </f-table>
                    </div>
                    <div v-if="alarms && alarms.length > 0" class="table-pagination-container">
                        <FPagination
                            v-model:pageSize="alarmsPagination.size"
                            v-model:currentPage="alarmsPagination.page"
                            show-size-changer
                            show-total
                            :total-count="alarmsPagination.total"
                            @change="alarmsPageChange(false)"
                            @pageSizeChange="alarmsPageChange(true)"
                        ></FPagination>
                    </div>
                </div>
                <div class="wd-content-body">
                    <div class="summary-container">
                        <DashboardSummary v-bind="applicationsSummary" />
                    </div>
                    <div class="table-container">
                        <f-table :data="applications">
                            <f-table-column prop="application_id" :label="$t('dashboard.number')" :width="160" ellipsis></f-table-column>
                            <f-table-column ellipsis prop="project_name" :label="$t('common.projectName')" :width="200"></f-table-column>
                            <f-table-column prop="submit_time" :label="$t('dashboard.startTime')" :width="184" ellipsis></f-table-column>
                            <f-table-column :formatter="applicationStatusFormatter" prop="status" :label="$t('common.projectState')" :width="102" ellipsis>
                                <template #default="{ row }">
                                    <span :class="getClassNamesOfApplicatoinStatus(row.status)">{{applicationStatusFormatter({ cellValue: row.status })}}</span>
                                </template>
                            </f-table-column>
                            <template #empty>
                                <BPageLoading actionType="emptyInitResult" :loadingText="loadingText" />
                            </template>
                        </f-table>
                    </div>
                    <div v-if="applications && applications.length > 0" class="table-pagination-container">
                        <FPagination
                            v-model:pageSize="applicationsPagination.size"
                            v-model:currentPage="applicationsPagination.page"
                            show-size-changer
                            show-total
                            :total-count="applicationsPagination.total"
                            @change="applicationsPageChange(false)"
                            @pageSizeChange="applicationsPageChange(true)"
                        ></FPagination>
                    </div>
                </div>
            </section>
            <section class="section">
                <h3 class="wd-content-title">{{$t('dashboard.summaryAnalysis')}}</h3>
                <div class="wd-content-body">
                    <h4 class="chart-title">{{$t('dashboard.alarmLevelSummaryAnalysis')}}</h4>
                    <div class="date-range-container">
                        <DateRange
                            v-model:start-date="alarmsStartDate"
                            v-model:end-date="alarmsEndDate"
                            v-model:days="alarmsDays"
                        />
                    </div>
                    <div id="alarms-chart" class="my-chart-container"></div>
                </div>
                <div class="wd-content-body">
                    <h4 class="chart-title">{{$t('dashboard.projectStateSummaryAnalysis')}}</h4>
                    <div class="date-range-container">
                        <DateRange
                            v-model:start-date="applicationsStartDate"
                            v-model:end-date="applicationsEndDate"
                            v-model:days="applicationsDays"
                        />
                    </div>
                    <div id="applications-chart" class="my-chart-container"></div>
                </div>
            </section>
        </main>
    </article>
</template>
<script setup>
import { useI18n } from '@fesjs/fes';
import { subDays } from 'date-fns';
import { BPageLoading } from '@fesjs/traction-widget';
import DashboardSummary from './components/DashboardSummary';
import DateRange from './components/DateRange';
import useDashboardTable from './hooks/useDashboardTable';
import useDateRange from './hooks/useDateRange';
import useAlarmsChart from './hooks/useAlarmsChart';
import useApplicationsChart from './hooks/useApplicationsChart';
import { fetchAlarmData, fetchApplicationData } from './api';
import { getLabelFromList } from '../../assets/js/utils';
import {
    ALARM_LEVELS,
    APPLICATION_STATUSES,
} from './const';

// 今日告警数据处理器(处理后端返回数据，防止异常数据导致页面报错)
const alarmsResHandler = (res) => {
    if (!res) {
        return {
            total: 0,
            tableData: [],
            calculateData: {},
        };
    }
    const {
        // eslint-disable-next-line camelcase
        alarm_critical_num = 0,
        // eslint-disable-next-line camelcase
        alarm_major_num = 0,
        // eslint-disable-next-line camelcase
        alarm_minor_num = 0,
        // eslint-disable-next-line camelcase
        alarm_warning_num = 0,
        // eslint-disable-next-line camelcase
        alarm_info_num = 0,
    } = res;
    return {
        total: res.total_num || 0,
        tableData: res.alarms || [],
        calculateData: {
            alarm_critical_num,
            alarm_major_num,
            alarm_minor_num,
            alarm_warning_num,
            alarm_info_num,
        },
        lastUpdateTime: res.lastUpdateTime,
    };
};
// 今日应用数据处理器(处理后端返回数据，防止异常数据导致页面报错)
const applicationsResHandler = (res) => {
    if (!res) {
        return {
            total: 0,
            tableData: [],
            calculateData: {},
        };
    }
    const calculateData = {
        application_succ_num: res.application_succ_num || 0,
        application_fail_check_num: res.application_fail_check_num || 0,
        application_fail_num: res.application_fail_num || 0,
    };
    return {
        total: res.total_num || 0,
        tableData: res.applications || [],
        calculateData,
        lastUpdateTime: res.lastUpdateTime,
    };
};

const { t: $t } = useI18n();

const loadingText = {
    emptyInitResult: $t('common.emptyInitResult'),
};

const {
    pagination: alarmsPagination,
    tableData: alarms,
    calculateData: alarmsCalculateData,
    lastUpdateTime: alarmsLastUpdateTime,
    getData: getAlarmsData,
    alarmsSummary,
} = useDashboardTable(fetchAlarmData, alarmsResHandler);
const {
    pagination: applicationsPagination,
    tableData: applications,
    calculateData: applicationsCalculateData,
    lastUpdateTime: applicationsLastUpdateTime,
    getData: getApplicationsData,
    applicationsSummary,
} = useDashboardTable(fetchApplicationData, applicationsResHandler);

const days = 7;
const endDate = new Date();
const startDate = subDays(endDate, days - 1);
const {
    startDate: alarmsStartDate,
    endDate: alarmsEndDate,
    days: alarmsDays,
} = useDateRange(startDate.getTime(), endDate.getTime(), days);
const {
    startDate: applicationsStartDate,
    endDate: applicationsEndDate,
    days: applicationsDays,
} = useDateRange(startDate.getTime(), endDate.getTime(), days);
useAlarmsChart(alarmsStartDate, alarmsEndDate, alarmsDays, applicationsDays);
useApplicationsChart(applicationsStartDate, applicationsEndDate, applicationsDays);
const alarmLevelFormatter = ({ cellValue }) => getLabelFromList(ALARM_LEVELS, cellValue);
const applicationStatusFormatter = ({ cellValue }) => getLabelFromList(APPLICATION_STATUSES, cellValue);
const alarmsPageChange = (reset = false) => {
    if (reset) alarmsPagination.page = 1;
    getAlarmsData();
};
const applicationsPageChange = (reset = false) => {
    if (reset) applicationsPagination.page = 1;
    getApplicationsData();
};
const getClassNamesOfApplicatoinStatus = (status) => {
    const target = APPLICATION_STATUSES.find(item => item.value === status);
    return target ? target.classNames : [];
};
</script>
<style lang="less" scoped>
@import "@/style/varible";
.dashboard {
    max-height: 100%;
    overflow-y: auto;
    .section {
        margin-bottom: 24px;
        &:last-of-type {
            margin-bottom: 0;
        }
        & > .wd-content-body {
            &:last-of-type {
                margin-bottom: 0;;
            }
        }
    }
    .table-container {
        margin: 16px 0 17px;
        .red {
            color: @red-color;
        }
        .orange {
            color: @orange-color;
        }
        .green {
            color: @green-color;
        }
    }
    .chart-title {
        line-height: 24px;
        font-size: 16px;
        color: @black-color;
        font-weight: 500;
    }
    .date-range-container {
        margin: 24px 0;
    }
    .my-chart-container {
        height: 520px;
    }
}
</style>
<config>
{
    "name": "dashboard",
    "title": "$dashboard.dashboard"
}
</config>
