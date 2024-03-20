<template>
    <div v-if="mounted" class="drawer-wrap">
        <div class="form">
            <div class="switch">
                <div class="left" :class="{ active: type === 'table' }" @click="changeDisplay('table')"><UnorderedListOutlined /></div>
                <div class="right" :class="{ active: type === 'chart' }" @click="changeDisplay('chart')"><UnorderedListOutlined /></div>
            </div>
            <div class="date">
                <DateRange v-model:start-date="startDate" v-model:end-date="endDate" v-model:days="days" />
            </div>
        </div>
        <div v-if="type === 'table' && columns.length" class="table">
            <div v-for="(table) in columns" :key="table.rule_metric_id" class="wrap">
                <div class="name">
                    <div>{{$t('indexManagement.indexName')}}：{{table.name}}</div>
                    <div>
                        <span>环境名：</span>
                        <FDropdown trigger="click" :options="table.envList" @click="($event) => { filterEnvChange($event, table) }">
                            <FButton type="text">
                                <template #icon>
                                    {{table.curSelectedEnvLabel}}
                                    <CaretDownOutlined />
                                </template>
                            </FButton>
                        </FDropdown>
                    </div>
                </div>
                <f-table :data="table.rule_metric_values">
                    <template #empty>
                        <BPageLoading actionType="emptyInitResult" />
                    </template>
                    <f-table-column prop="rule_metric_value" :label="$t('common.historicalValue')" :width="74" ellipsis></f-table-column>
                    <f-table-column prop="generate_time" label="生成时间" :width="184" ellipsis></f-table-column>
                    <f-table-column prop="datasource_names" label="数据源名" :width="184" ellipsis>
                        <template #default="{ row }">{{row.datasource_names?.join('、') || '--'}}</template>
                    </f-table-column>
                    <f-table-column prop="env_name" label="环境名" :width="184" ellipsis>
                        <template #default="{ row }">{{row?.env_name || '--'}}</template>
                    </f-table-column>

                    <f-table-column #default="{ row = {}}" prop="related_rule_name" label="关联规则" :width="160" ellipsis>
                        <span class="a-link" @click="handleRowClick(row)">{{row.related_rule_name}}</span>
                    </f-table-column>
                </f-table>
            </div>
            <div v-if="!multi" class="table-pagination-container">
                <FPagination
                    v-model:currentPage="pagination.current"
                    v-model:pageSize="pagination.size"
                    show-size-changer
                    show-total
                    :total-count="pagination.total"
                    @change="single(pagination.current - 1)"
                    @pageSizeChange="single(0)"
                ></FPagination>
            </div>
        </div>
        <div v-if="type === 'chart'" class="chart">
            <div v-for="row in columns" :id="`chart-${row.rule_metric_id}`" :key="row.rule_metric_id" class="my-chart-container" :class="{ no: !row.rule_metric_values.length }">
                <div v-if="!row.rule_metric_values.length" style="color: #333333">
                    <span>{{row.name}}</span>
                    <span style="font-weight: bold">{{$t('common.noData')}}</span>
                </div>
            </div>
        </div>
    </div>
</template>
<script setup>
import {
    defineProps, computed, reactive, ref, watch, nextTick,
} from 'vue';
import { subDays } from 'date-fns';
import { useI18n, useRouter } from '@fesjs/fes';
import useDateRange from '@/pages/dashboard/hooks/useDateRange';
import DateRange from '@/pages/dashboard/components/DateRange';
import { UnorderedListOutlined, CaretDownOutlined } from '@fesjs/fes-design/icon';
import { getHistory, batchHistory } from '@/pages/metricManagement/api';
import echarts from '@/pages/dashboard/hooks/useEcharts';
import { FMessage } from '@fesjs/fes-design';
import { INTMAXVALUE } from '@/assets/js/const';
import { BPageLoading } from '@fesjs/traction-widget';

const { t: $t } = useI18n();
const router = useRouter();

const getDateStr = (timestamp) => {
    if (!timestamp) return '';
    const dateObj = new Date(timestamp);
    const year = dateObj.getFullYear();
    const month = (`${dateObj.getMonth() + 1}`).padStart(2, '0');
    const date = (`${dateObj.getDate()}`).padStart(2, '0');
    return `${year}-${month}-${date}`;
};

const props = defineProps({
    selected: {
        type: Array,
        required: true,
    },
    ready: {
        type: Boolean,
        required: true,
    },
    isMulti: {
        type: Boolean,
        required: true,
    },
});
const type = ref('table');
const historyKeys = computed(() => props.selected);
const mounted = computed(() => props.ready);
const multi = computed(() => props.isMulti);
const end = new Date();
const start = subDays(end, 29);
const {
    startDate,
    endDate,
    days,
} = useDateRange(start.getTime(), end.getTime(), 30);

const columns = ref([]);
const pagination = reactive({
    current: 1,
    size: 15,
    total: 0,
});
const initAllEnv = {
    label: 'ALL',
    value: 'ALL',
};

function handleRowClick(row) {
    console.log('点击关联规则，进行跳转', row);
    if (row.rule) {
        let ruleType = 'rules';
        if (row.rule.table_group) ruleType = 'tableGroupRules';
        router.push(`/projects/${ruleType}?ruleGroupId=${row.rule.rule_group_id}&id=${row.rule.rule_id}&workflowProject=${row.rule.project_type === 2}&projectId=${row.rule.project_id}`);
    } else {
        FMessage.info('暂无规则数据，跳转失败。');
    }
}

function initChart(met) {
    console.log('initChart', met);
    if (!met.rule_metric_values.length) return;
    const el = document.getElementById(`chart-${met.rule_metric_id}`);
    console.log('el', el);
    const instance = echarts.getInstanceByDom(el);
    if (instance) {
        instance.dispose();
    }
    const dates = met.rule_metric_values.map(ele => ele.generate_time);
    const data = met.rule_metric_values.map(ele => ele.rule_metric_value || 0);
    const myChart = echarts.init(el);
    const option = {
        backgroundColor: '#fff',
        grid: {
            top: 17,
            right: 0,
            left: '8%',
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow',
            },
        },
        dataZoom: {
            type: 'slider',
            startValue: 0,
            endValue: 10,
            xAxisIndex: [0],
            handleSize: 0,
            height: 5,
            bottom: 70,
            borderColor: 'transparent',
            fillerColor: 'transparent',
            backgroundColor: 'transparent',
            showDataShadow: false,
            showDetail: false,
            realtime: true,
            filterMode: 'filter',
        },
        legend: {
            bottom: 0,
            icon: 'rect',
            height: 20,
            itemWidth: 10,
            itemHeight: 10,
            itemGap: 32,
            itemStyle: {
                borderWidth: 1,
            },
            data: [
                {
                    name: met.name,
                    itemStyle: {
                        color: '#EDF2FF',
                        borderColor: '#5384FF',
                    },
                },
            ],
        },
        xAxis: [
            {
                type: 'category',
                axisTick: {
                    show: false,
                },
                axisLine: {
                    show: false,
                },
                data: dates,
            },
        ],
        yAxis: [
            {
                type: 'value',
                splitLine: {
                    lineStyle: {
                        width: 1,
                        color: '#F1F1F2',
                    },
                },
            },
        ],
        series: [
            {
                name: met.name,
                type: 'line',
                symbol: 'rect',
                symbolSize: 8,
                itemStyle: {
                    normal: {
                        color: '#EDF2FF',
                        borderColor: '#5384FF',
                        lineStyle: {
                            color: '#5384FF',
                        },
                    },
                },
                data,
            },
        ],
    };
    myChart.setOption(option);
    console.log('myChart', myChart);
    window.addEventListener('resize', () => {
        myChart && myChart.resize();
    });
}

// 区分当前是单一历史值-single还是多历史值-batch的标志位
let historyFlag = 'single';
// 单一历史值
async function single(page, slectedEnvName = 'ALL') {
    try {
        console.log('single row', historyKeys.value);
        console.log('page', page);
        console.log('pagination', pagination);
        console.log('startDate, endDate', startDate.value, endDate.value);
        historyFlag = 'single';
        const id = historyKeys.value[0]?.id;
        const name = historyKeys.value[0]?.name;
        const params = {
            page,
            rule_metric_id: id,
            size: pagination.size,
            start_time: `${getDateStr(startDate.value)} 00:00:00`,
            end_time: `${getDateStr(endDate.value)} 23:59:59`,
        };
        if (slectedEnvName !== 'ALL') {
            params.env_name = slectedEnvName;
        }
        const result = await getHistory(params);
        console.log('getHistory', result);
        let envListInfo = [];
        if (result.env_names) {
            envListInfo = result.env_names.map(envNameItem => ({ label: envNameItem, value: envNameItem }));
            // 排个序，后端传过来的env_names元素顺序会变动
            envListInfo.sort((a, b) => (a.label > b.label ? 1 : a.label < b.label ? -1 : 0));
        }
        envListInfo.unshift(initAllEnv);
        columns.value = [{
            rule_metric_values: result.content,
            rule_metric_id: id,
            name,
            envList: envListInfo,
            curSelectedEnvLabel: slectedEnvName,
        }];
        pagination.total = result.totalCount;
        if (type.value === 'table') return;
        await nextTick();
        columns.value.forEach(initChart);
    } catch (error) {
        console.error(error);
    }
}

async function batch() {
    try {
        console.log('multi rows', historyKeys.value);
        historyFlag = 'batch';
        const ids = historyKeys.value.map(e => +e.id);
        const result = await batchHistory({
            rule_metric_id_list: ids,
            start_time: `${getDateStr(startDate.value)} 00:00:00`,
            end_time: `${getDateStr(endDate.value)} 23:59:59`,
        });
        console.log('batchHistory', result);
        const envListInfo = [];
        columns.value = result.map((met, index) => {
            const id = met.rule_metric_id;
            const currentMet = historyKeys.value.find(key => +key.id === +id) || {};
            envListInfo[index] = [];
            if (met.env_names) {
                envListInfo[index] = met.env_names.map(envNameItem => ({ label: envNameItem, value: envNameItem }));
                envListInfo[index].sort((a, b) => (a.label > b.label ? 1 : a.label < b.label ? -1 : 0));
            }
            envListInfo[index].unshift(initAllEnv);
            return {
                rule_metric_values: met.rule_metric_values,
                rule_metric_id: id,
                name: currentMet.name,
                envList: envListInfo[index],
                curSelectedEnvLabel: 'ALL',
            };
        });
        // 批量可视化不用分页
        if (type.value === 'table') return;
        await nextTick();
        columns.value.filter(c => c.rule_metric_values && c.rule_metric_values.length).forEach(initChart);
    } catch (error) {
        console.error(error);
    }
}

async function updateOneOfBatchByEnv(columnData, slectedEnvName) {
    try {
        const params = {
            page: 0,
            rule_metric_id: columnData.rule_metric_id,
            size: INTMAXVALUE, // 批量没翻页，直接放最大值
            start_time: `${getDateStr(startDate.value)} 00:00:00`,
            end_time: `${getDateStr(endDate.value)} 23:59:59`,
        };
        if (slectedEnvName !== 'ALL') {
            params.env_name = slectedEnvName;
        }
        const result = await getHistory(params);
        columnData.rule_metric_values = result.content;
        if (type.value === 'table') return;
        await nextTick();
        initChart(columnData);
    } catch (error) {
        console.error(error);
    }
}

function filterEnvChange(value, table) {
    const selectedIndex = table.envList.findIndex(item => item.value === value);
    table.curSelectedEnvLabel = table.envList[selectedIndex].label;
    if (historyFlag === 'single') single(0, value);
    if (historyFlag === 'batch') {
        // 批量时也只是更新其中一个历史值的数据
        updateOneOfBatchByEnv(table, value);
    }
}

function changeDisplay(val) {
    if (val === type.value) return;
    type.value = val;
}

watch([mounted, startDate, endDate, days, type], ([newVal]) => {
    console.log('mounted', newVal);
    if (!newVal) {
        columns.value = [];
        return;
    }
    if (multi.value) return batch();
    single(0);
});

</script>
<style lang="less" scoped>
.drawer-wrap {
    height: 100%;
    overflow: hidden;
    overflow-y: auto;
}
.form {
    display: flex;
    align-items: center;
    padding-bottom: 14px;
    .switch {
        display: flex;
        align-items: center;
        border: 1px solid #CFD0D3;
        border-radius: 4px;
        margin-right: 16px;
        overflow: hidden;
        .left, .right {
            padding: 5px 16px 1px;
            cursor: pointer;
            &.active {
                background-color: #5384FF;
                color: #ffffff;
            }
        }
    }
}
.table {
    .wrap {
        padding-bottom: 14px;
        .name {
            font-size: 14px;
            color: #646670;
            line-height: 22px;
            font-weight: 400;
            margin-bottom: 16px;
            display: flex;
            justify-content: space-between;
        }
    }
}

.my-chart-container {
    margin-bottom: 25px;
    height: 530px;
    border: 1px solid #CFD0D3;
    border-radius: 4px;
    &.no {
        height: auto;
        border: none;
    }
}
</style>
