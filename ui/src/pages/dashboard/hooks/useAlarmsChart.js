import { watch } from 'vue';
import { useI18n } from '@fesjs/fes';
import { genTooltipStr } from '@/common/utils';
import echarts from './useEcharts';
import { fetchAlarmChartData } from '../api';

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
                alarmCriticalNums: [],
                alarmMajorNums: [],
                alarmMinorNums: [],
                alarmWarningNums: [],
                alarmInfoNums: [],
            };
        }
        const dates = [];
        const alarmCriticalNums = [];
        const alarmMajorNums = [];
        const alarmMinorNums = [];
        const alarmWarningNums = [];
        const alarmInfoNums = [];
        // 最小16，每个阶梯是8
        let maxSum = 0;
        let leftGridSize = 16;
        for (let i = 0; i < data.length; i++) {
            const {
                date,
                alarm_critical_num: alarmCriticalNum = 0,
                alarm_major_num: alarmMajorNum = 0,
                alarm_minor_num: alarmMinorNum = 0,
                alarm_warning_num: alarmWarningNum = 0,
                alarm_info_num: alarmInfoNum = 0,
            } = data[i];
            dates.push(date);
            alarmCriticalNums.push(alarmCriticalNum);
            alarmMajorNums.push(alarmMajorNum);
            alarmMinorNums.push(alarmMinorNum);
            alarmWarningNums.push(alarmWarningNum);
            alarmInfoNums.push(alarmInfoNum);

            const currentTotalSum = alarmCriticalNum + alarmMajorNum + alarmMinorNum + alarmWarningNum + alarmInfoNum;
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
            alarmCriticalNums,
            alarmMajorNums,
            alarmMinorNums,
            alarmWarningNums,
            alarmInfoNums,
            leftGridSize,
        };
    };
    const initChart = (data) => {
        if (!data) return;
        const {
            dates,
            alarmCriticalNums,
            alarmMajorNums,
            alarmMinorNums,
            alarmWarningNums,
            alarmInfoNums,
            leftGridSize,
        } = data;
        const el = document.getElementById('alarms-chart');
        const instance = echarts.getInstanceByDom(el);
        if (instance) {
            instance.dispose();
        }
        const myChart = echarts.init(el);
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
                        name: $t('dashboard.criticalNum'),
                        itemStyle: {
                            color: '#FEEEEE',
                            borderColor: '#FF4D4F',
                        },
                    },
                    {
                        name: $t('dashboard.majorNum'),
                        itemStyle: {
                            color: '#EDF2FF',
                            borderColor: '#5384FF',
                        },
                    },
                    {
                        name: $t('dashboard.minorNum'),
                        itemStyle: {
                            color: '#FFF4EB',
                            borderColor: '#FF9900',
                        },
                    },
                    {
                        name: $t('dashboard.warningNum'),
                        itemStyle: {
                            color: '#FFF3DC',
                            borderColor: '#FAC017',
                        },
                    },
                    {
                        name: $t('dashboard.infolNum'),
                        itemStyle: {
                            color: '#D1F4E9',
                            borderColor: '#00CB91',
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
                    name: $t('dashboard.criticalNum'),
                    type: 'bar',
                    emphasis: {
                        focus: 'series',
                    },
                    itemStyle: {
                        color: '#FEEEEE',
                        borderColor: '#FF4D4F',
                    },
                    data: alarmCriticalNums,
                },
                {
                    name: $t('dashboard.majorNum'),
                    type: 'bar',
                    emphasis: {
                        focus: 'series',
                    },
                    itemStyle: {
                        color: ' #EDF2FF',
                        borderColor: '#5384FF',
                    },
                    data: alarmMajorNums,
                },
                {
                    name: $t('dashboard.minorNum'),
                    type: 'bar',
                    emphasis: {
                        focus: 'series',
                    },
                    itemStyle: {
                        color: '#FFF4EB',
                        borderColor: ' #FF9540',
                    },
                    data: alarmMinorNums,
                },
                {
                    name: $t('dashboard.warningNum'),
                    type: 'bar',
                    emphasis: {
                        focus: 'series',
                    },
                    itemStyle: {
                        color: '#FFF9ED',
                        borderColor: '#FAC017',
                    },
                    data: alarmWarningNums,
                },
                {
                    name: $t('dashboard.infolNum'),
                    type: 'bar',
                    emphasis: {
                        focus: 'series',
                    },
                    itemStyle: {
                        color: '#E5F9F4',
                        borderColor: '#00CB91',
                    },
                    data: alarmInfoNums,
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
        let alarms = await fetchAlarmChartData(buildParams(newStartDate, newEndDate, newDays)) || [];
        alarms = alarms.sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());
        initChart(getChartData(alarms));
    }, {
        immediate: true,
    });
    return {
    };
}
