import { ref } from 'vue';

export const executeIntervalList = ref([
    {
        label: '每小时',
        value: 'hour',
    }, {
        label: '每天',
        value: 'day',
    },
    {
        label: '每周',
        value: 'week',
    }, {
        label: '每月',
        value: 'month',
    },
]);
export const executeIntervalDict = {
    hour: '每小时',
    day: '每天',
    week: '每周',
    month: '每月',
};
export const executionTimeWeekOptions = ref([
    {
        label: '周一',
        value: 1,
    }, {
        label: '周二',
        value: 2,
    }, {
        label: '周三',
        value: 3,
    }, {
        label: '周四',
        value: 4,
    }, {
        label: '周五',
        value: 5,
    }, {
        label: '周六',
        value: 6,
    }, {
        label: '周日',
        value: 7,
    },
]);
export const executionTimeMonthOptions = ref([]);
for (let i = 1; i <= 31; i++) {
    executionTimeMonthOptions.value.push({
        label: `${i}日`,
        value: i,
    });
}

export const fomatPreviewExecutionTime = (executeInterval, executionDateInInterval, executionTimeInDate) => {
    const result = {
        complete: '', // 包含执行频率
        time: '', // 不包含执行频率
    };
    const weekDict = ['', '周一', '周二', '周三', '周四', '周五', '周六', '周日'];
    if (executeInterval === 'month') {
        result.complete = `${executeIntervalDict.month} ${executionDateInInterval}日 ${executionTimeInDate}`;
        result.time = `${executionDateInInterval}日 ${executionTimeInDate}`;
    }
    if (executeInterval === 'week') {
        result.complete = `${executeIntervalDict.week} ${weekDict[executionDateInInterval]} ${executionTimeInDate}`;
        result.time = `${weekDict[executionDateInInterval]} ${executionTimeInDate}`;
    }
    if (executeInterval === 'day') {
        result.complete = `${executeIntervalDict.day} ${executionTimeInDate}`;
        result.time = `${executionTimeInDate}`;
    }
    if (executeInterval === 'hour') {
        const time = executionTimeInDate.substring(3);
        result.complete = `${executeIntervalDict.hour} ${time}`;
        result.time = time;
    }
    // console.log(result)
    return result;
};
