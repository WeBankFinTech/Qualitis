import { watch, ref } from 'vue';
import { useI18n } from '@fesjs/fes';
import { genTooltipStr } from '@/common/utils';
import echarts from './useEcharts';
import { fetchCollectChartData } from '../api';

const getMonthStr = (timestamp, type) => {
    function getLastDayOfMonth(year, month) {
        const date = new Date(year, month, 1);
        date.setMonth(date.getMonth() + 1);
        date.setDate(0);
        return date.getDate();
    }
    if (!timestamp) return '';
    const dateObj = new Date(timestamp);
    const year = dateObj.getFullYear();
    const month = (`${dateObj.getMonth() + 1}`).padStart(2, '0');
    const day = type === 'end' ? getLastDayOfMonth(year, dateObj.getMonth()) : '01';
    return `${year}-${month}-${day}`;
};

export default function useCollectChart(startMonth, endMonth, months) {
    const { t: $t } = useI18n();
    const buildParams = (_startMonth, _endMonth, _months) => (_months ? {
        step_size: _months * -1,
    } : {
        start_date: getMonthStr(_startMonth, 'start'),
        end_date: getMonthStr(_endMonth, 'end'),
    });
    const getChartData = (data) => {
        if (!Array.isArray(data)) {
            return {
                dates: [],
                noPermissionNums: [],
                analysisedNums: [],
                collectedNums: [],
                runDateSchedulerNums: [],
                historySchedulerNums: [],
            };
        }
        const dates = [];
        const noPermissionNums = [];
        const analysisedNums = [];
        const collectedNums = [];
        const runDateSchedulerNums = [];
        const historySchedulerNums = [];
        // 最小16，每个阶梯是8
        let maxSum = 0;
        let leftGridSize = 16;
        for (let i = 0; i < data.length; i++) {
            const {
                date,
                no_permission_num: noPermissionNum = 0,
                analysised_num: analysisedNum = 0,
                collected_num: collectedNum = 0,
                run_date_scheduler_num: runDateSchedulerNum = 0,
                history_scheduler_num: historySchedulerNum = 0,
            } = data[i];
            dates.push(date);
            noPermissionNums.push(noPermissionNum);
            analysisedNums.push(analysisedNum);
            collectedNums.push(collectedNum);
            runDateSchedulerNums.push(runDateSchedulerNum);
            historySchedulerNums.push(historySchedulerNum);

            const currentTotalSum = noPermissionNum + analysisedNum + collectedNum + runDateSchedulerNum + historySchedulerNum;
            if (currentTotalSum > maxSum) {
                maxSum = currentTotalSum;
            }
        }
        const maxSumLen = String(maxSum).length;
        if (maxSumLen > 0) {
            leftGridSize += (maxSumLen - 1) * 8;
        }
        return {
            dates,
            noPermissionNums,
            analysisedNums,
            collectedNums,
            runDateSchedulerNums,
            historySchedulerNums,
            leftGridSize,
        };
    };
    const initChart = (data) => {
        console.log('data:', data);
        if (!data) return;
        const {
            dates,
            noPermissionNums,
            analysisedNums,
            collectedNums,
            runDateSchedulerNums,
            historySchedulerNums,
            leftGridSize,
        } = data;
        const el = document.getElementById('collect-chart');
        const instance = echarts.getInstanceByDom(el);
        if (instance) {
            instance.dispose();
        }
        const myChart = echarts.init(el);
        const option = {
            backgroundColor: '#fff',
            grid: {
                top: 60,
                right: 0,
                left: leftGridSize,
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow',
                },
                formatter: tempData => genTooltipStr(tempData),
            },
            dataZoom: {
                type: 'slider',
                startValue: 0,
                endValue: 6,
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
                left: 0,
                top: 0,
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
                        name: $t('_.无权限'),
                        itemStyle: {
                            color: '#FEEEEE',
                            borderColor: '#FF4D4F',
                        },
                    },
                    {
                        name: $t('_.已分析'),
                        itemStyle: {
                            color: '#EDF2FF',
                            borderColor: '#5384FF',
                        },
                    },
                    {
                        name: $t('_.已录入采集'),
                        itemStyle: {
                            color: '#E5F9F4',
                            borderColor: '#00CB91',
                        },
                    },
                    {
                        name: $t('_.已增量采集'),
                        itemStyle: {
                            color: '#FFF4EB',
                            borderColor: '#FF9540',
                        },
                    },
                    {
                        name: $t('_.已历史采集'),
                        itemStyle: {
                            color: 'rgba(15, 18, 34, 0.06)',
                            borderColor: '#CFD0D3',
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
                    name: $t('_.无权限'),
                    type: 'bar',
                    emphasis: {
                        focus: 'series',
                    },
                    itemStyle: {
                        color: '#FEEEEE',
                        borderColor: '#FF4D4F',
                    },
                    data: noPermissionNums,
                },
                {
                    name: $t('_.已分析'),
                    type: 'bar',
                    emphasis: {
                        focus: 'series',
                    },
                    itemStyle: {
                        color: '#EDF2FF',
                        borderColor: '#5384FF',
                    },
                    data: analysisedNums,
                },
                {
                    name: $t('_.已录入采集'),
                    type: 'bar',
                    emphasis: {
                        focus: 'series',
                    },
                    itemStyle: {
                        color: '#E5F9F4',
                        borderColor: '#00CB91',
                    },
                    data: collectedNums,
                },
                {
                    name: $t('_.已增量采集'),
                    type: 'bar',
                    emphasis: {
                        focus: 'series',
                    },
                    itemStyle: {
                        color: '#FFF4EB',
                        borderColor: '#FF9540',
                    },
                    data: runDateSchedulerNums,
                },
                {
                    name: $t('_.已历史采集'),
                    type: 'bar',
                    emphasis: {
                        focus: 'series',
                    },
                    itemStyle: {
                        color: 'rgba(15, 18, 34, 0.06)',
                        borderColor: '#CFD0D3',
                    },
                    data: historySchedulerNums,
                },
            ],
        };
        myChart.setOption(option);
        window.addEventListener('resize', () => {
            myChart.resize();
        });
    };
    watch([startMonth, endMonth, months], async ([newStartMonth, newEndMonth, newMonths]) => {
        if (!newStartMonth && !newEndMonth && !newMonths) return;
        let collections = await fetchCollectChartData(buildParams(newStartMonth, newEndMonth, newMonths)) || [];
        collections = collections.sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());
        initChart(getChartData(collections));
    }, {
        immediate: true,
    });
}
