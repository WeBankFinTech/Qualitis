import { watch } from 'vue';
import { useI18n } from '@fesjs/fes';
import { genTooltipStr } from '@/common/utils';
import echarts from './useEcharts';
import { fetchApplicationChartData } from '../api';

const getDateStr = (timestamp) => {
    if (!timestamp) return '';
    const dateObj = new Date(timestamp);
    const year = dateObj.getFullYear();
    const month = (`${dateObj.getMonth() + 1}`).padStart(2, '0');
    const date = (`${dateObj.getDate()}`).padStart(2, '0');
    return `${year}-${month}-${date}`;
};

export default function useAlarmsChart(startDate, endDate, days) {
    const { t: $t } = useI18n();
    const buildParams = (_startDate, _endDate, _days) => (_days ? {
        step_size: _days * -1,
    } : {
        start_date: getDateStr(_startDate),
        end_date: getDateStr(_endDate),
    });
    const getChartData = (data) => {
        if (!Array.isArray(data)) {
            return {
                dates: [],
                applicationSuccNums: [],
                applicationFailNums: [],
                applicationFailCheckNums: [],
            };
        }
        const dates = [];
        const applicationSuccNums = [];
        const applicationFailNums = [];
        const applicationFailCheckNums = [];
        // 最小16，每个阶梯是8
        let maxSum = 0;
        let leftGridSize = 16;
        for (let i = 0; i < data.length; i++) {
            const {
                date,
                application_succ_num: applicationSuccNum = 0,
                application_fail_num: applicationFailNum = 0,
                application_fail_check_num: applicationFailCheckNum = 0,
            } = data[i];
            dates.push(date);
            applicationSuccNums.push(applicationSuccNum);
            applicationFailNums.push(applicationFailNum);
            applicationFailCheckNums.push(applicationFailCheckNum);

            const currentTotalSum = applicationSuccNum + applicationFailNum + applicationFailCheckNum;
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
            applicationSuccNums,
            applicationFailNums,
            applicationFailCheckNums,
            leftGridSize,
        };
    };
    const initChart = (data) => {
        if (!data) return;
        const {
            dates,
            applicationSuccNums,
            applicationFailNums,
            applicationFailCheckNums,
            leftGridSize,
        } = data;
        const chartDom = document.getElementById('applications-chart');
        const instance = echarts.getInstanceByDom(chartDom);
        if (instance) {
            instance.dispose();
        }
        const myChart = echarts.init(chartDom);
        const option = {
            backgroundColor: '#fff',
            grid: {
                top: 17,
                right: 0,
                left: leftGridSize,
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow',
                },
                formatter: tempData => genTooltipStr(tempData)
                ,
            },
            dataZoom: {
                type: 'slider',
                startValue: 0,
                endValue: 29,
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
                        name: $t('common.byCheck'),
                        itemStyle: {
                            color: '#00CB91',
                            borderColor: '#00CB91',
                        },
                    },
                    {
                        name: $t('common.failCheck'),
                        itemStyle: {
                            color: '#FF9540',
                            borderColor: '#FF9540',
                        },
                    },
                    {
                        name: $t('common.errorCheck'),
                        itemStyle: {
                            color: '#FF4D4F',
                            borderColor: '#FF4D4F',
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
                    name: $t('common.byCheck'),
                    type: 'bar',
                    barWidth: 24,
                    stack: 'one',
                    emphasis: {
                        focus: 'series',
                    },
                    itemStyle: {
                        color: '#00CB91',
                        borderColor: '#00CB91',
                    },
                    data: applicationSuccNums,
                },
                {
                    name: $t('common.failCheck'),
                    type: 'bar',
                    barWidth: 24,
                    stack: 'one',
                    emphasis: {
                        focus: 'series',
                    },
                    itemStyle: {
                        color: '#FF9540',
                        borderColor: '#FF9540',
                    },
                    data: applicationFailCheckNums,
                },
                {
                    name: $t('common.errorCheck'),
                    type: 'bar',
                    barWidth: 24,
                    stack: 'one',
                    emphasis: {
                        focus: 'series',
                    },
                    itemStyle: {
                        color: '#FF4D4F',
                        borderColor: '#FF4D4F',
                    },
                    data: applicationFailNums,
                },
            ],
        };
        myChart.setOption(option);
        window.addEventListener('resize', () => {
            myChart.resize();
        });
    };
    watch([startDate, endDate, days], async ([newStartDate, newEndDate, newDays]) => {
        if (!newStartDate && !newEndDate && !newDays) return;
        let applications = await fetchApplicationChartData(buildParams(newStartDate, newEndDate, newDays)) || [];
        applications = applications.sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());
        initChart(getChartData(applications));
    }, {
        immediate: true,
    });
    return {
    };
}
