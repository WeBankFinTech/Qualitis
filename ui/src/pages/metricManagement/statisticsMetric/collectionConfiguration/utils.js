import { ref } from 'vue';
import { locale } from '@fesjs/fes';

const $t = locale.t;


export const executeIntervalList = ref([
    {
        label: $t('_.每小时'),
        value: 'hour',
    }, {
        label: $t('_.每天'),
        value: 'day',
    },
    {
        label: $t('_.每周'),
        value: 'week',
    }, {
        label: $t('_.每月'),
        value: 'month',
    },
]);
export const executeIntervalDict = {
    hour: $t('_.每小时'),
    day: $t('_.每天'),
    week: $t('_.每周'),
    month: $t('_.每月'),
};
export const executionTimeWeekOptions = ref([
    {
        label: $t('_.周一'),
        value: 1,
    }, {
        label: $t('_.周二'),
        value: 2,
    }, {
        label: $t('_.周三'),
        value: 3,
    }, {
        label: $t('_.周四'),
        value: 4,
    }, {
        label: $t('_.周五'),
        value: 5,
    }, {
        label: $t('_.周六'),
        value: 6,
    }, {
        label: $t('_.周日'),
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
    const weekDict = ['', $t('_.周一'), $t('_.周二'), $t('_.周三'), $t('_.周四'), $t('_.周五'), $t('_.周六'), $t('_.周日')];
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
