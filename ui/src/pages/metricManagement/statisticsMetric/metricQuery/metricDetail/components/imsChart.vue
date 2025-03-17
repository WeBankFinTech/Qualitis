<template>
    <div id="ims-chart-container" ref="normalChartRef" class="my-chart-container"></div>
</template>

<script setup>
import {
    ref,
    computed,
    onMounted,
    defineProps,
    onBeforeUnmount,
    watch,
} from 'vue';
import dayjs from 'dayjs';
import echarts from './useEcharts';

const props = defineProps({
    warnDatas: {
        type: Array,
    },
    chartData: {
        type: Object,
        required: true,
    },
    showMiss: { // 缺数展示
        type: Boolean,
        default: false,
    },
});
const normalChartRef = ref(null);
const datazoombottom = ref(30);

const options = computed(() => ({
    grid: {
        left: 16, // 调整左边距
        right: 33,
        height: '431px',
        containLabel: true,
    },
    title: {
        text: props.chartData?.title || '',
        left: 'center', // 设置标题水平居中
        textStyle: {
            color: '#0F1222',
            fontSize: 16, // 字体大小
            fontWeight: 400, // 字体粗细
        },
    },
    tooltip: {
        trigger: 'axis',
        transitionDuration: 0,
        axisPointer: {
            type: 'shadow',
        },
        textStyle: {
            fontSize: 12, // 设置文字大小，单位为像素
        },
        formatter(params) {
            let content = `${params[0].name}<br>`;
            params.forEach((item) => {
                if (typeof item.value !== 'undefined') {
                    content += `<div class="ims-tooltip-item"><p style="max-width: 550px; flex-basis: 90%;word-wrap: break-word;white-space: pre-wrap;">${item.marker}${item.seriesName}:</p> <p style="font-weight:bold;">${item.value}</p></div>`;
                }
            });
            return `<div class="custom-tooltip">${content}</div>`; // 使用自定义的 tooltip 容器并设置最大宽度
        },
    },
    dataZoom: {
        type: 'slider',
        start: 0,
        end: 100, // 设置滑动条的初始范围
        bottom: datazoombottom.value,
    },
    legend: {
        data: props.chartData?.metrics?.map(item => item.metricName) || [],
        bottom: 0, // 设置图例距离底部的距离
        orient: 'horizontal',
        icon: 'rect',
        left: 0,
        itemWidth: 10,
        itemHeight: 10,
        itemGap: 25,
        itemStyle: {
            borderWidth: 1,
            lineHeight: 20, // 设置图例项的行高
        },
        formatter(name) {
            // 自定义图例文本的显示方式，实现自动换行
            if (name.length > 50) {
                return `${name.substring(0, 50)}...`; // \n${name.substring(10)}
            }
            return name;
        },
    },
    xAxis: {
        boundaryGap: false, // 禁用轴线两侧的间隙
        type: 'category',
        data: props.chartData?.metrics[0]?.lineDataList?.timestampList?.map(item => dayjs(item).format('YYYY-MM-DD')) || [],
    },
    yAxis: {
        type: 'value',
    },
    series: props.chartData?.metrics?.map((item, index) => ({
        name: item.metricName,
        data: item.lineDataList.valueList.map(value => (value === null && props.showMiss ? -1 : value)),
        type: 'line',
        showSymbol: false, // 设置不显示圆圈
        color: index === 0 ? '#1E90FF' : '#FF7F50', // 设置第一项颜色为蓝色，第二项颜色为红色
        markPoint: {
            itemStyle: {
                color: '#ee1112',
            },
            data: props.warnDatas || [],
        },
    })) || [],
}));
let instanceChart = null;
let resizeObserver = null;
const onelegendtext = (text) => {
    // 图例方块10px+6px小间隔 gap为25px
    // 创建一个隐藏的 Canvas 元素
    // 元素超过50字符省略显示有320px
    if (text.length > 50) return 336;
    const canvas = document.createElement('canvas');
    const ctx = canvas.getContext('2d');
    // 设置字体样式
    const fontSize = 12;
    ctx.font = `${fontSize}px Microsoft YaHei`;
    // 使用 measureText() 方法计算字符串宽度
    return ctx.measureText(text).width + 17;
};
const computedlegendHeight = (data) => {
    if (data && data.length > 1) {
        let line = 0; // 需要增加几行高度
        // 获取容器元素
        const chartContainer = document.getElementById('ims-chart-container');
        const containerWidth = chartContainer.offsetWidth; // 容器宽度
        let curlinewidth = 0;
        data.forEach((item) => {
            const textwidth = onelegendtext(item);

            if (curlinewidth + 25 + textwidth + 5 <= containerWidth) {
                curlinewidth += textwidth + 25;
            } else {
                line += 1;
                curlinewidth = textwidth;
            }
        });
        // console.log('有几行', data.length, line, 558 + line * 38);
        return line * 38;
    }
    return 0;
};
const initChart = () => {
    const metricName = props.chartData?.metrics?.map(item => item.metricName) || [];
    const setmetricName = Array.from(new Set(metricName));
    const realheight = computedlegendHeight(setmetricName);
    if (realheight > 0) {
        // 获取容器元素
        datazoombottom.value = datazoombottom.value + realheight;
        const chartContainer = document.getElementById('ims-chart-container');

        // 设置容器元素的高度
        const height = 558 + realheight;
        chartContainer.style.height = `${height}px`; // 设置为新的高度
        // console.log('chartContainer.style.height', chartContainer.style.height);
        // 调用 ECharts 的重新渲染方法
        instanceChart?.resize();
    }
    const el = normalChartRef.value;
    // devicePixelRatio renderer: 'svg'
    instanceChart = echarts.init(el, null, { renderer: 'svg' });
    instanceChart.setOption(options.value);
    resizeObserver = new ResizeObserver(() => instanceChart?.resize());
    resizeObserver.observe(el);
};
const resizeChart = () => {
    instanceChart?.resize();
};
const disposeBarChart = () => {
    if (instanceChart) {
        instanceChart.dispose();
        instanceChart = null;
    }
};
watch(
    () => props.chartData,
    (newVal) => {
        instanceChart?.setOption(options.value);
    },
    {
        deep: true,
    },
);
onMounted(() => {
    initChart();
    window.addEventListener('resize', resizeChart);
});
onBeforeUnmount(() => {
    disposeBarChart();
    window.removeEventListener('resize', resizeChart);
    if (resizeObserver) {
        resizeObserver.disconnect();
    }
});
</script>

<style>
#ims-chart-container {
    height: 558px;
}
.custom-tooltip {
  width: 520px; /* 设置固定宽度 */
}
.ims-tooltip-item {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    padding-bottom: 4px;
}
</style>
