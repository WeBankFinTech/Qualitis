import {
    ref, reactive, onMounted, watch,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import {
    ALARM_LEVELS, ALARM_LEVELS_FIELD_NAMES, APPLICATION_STATUSES, APPLICATION_STATUS_FIELD_NAMES,
} from '../const';

/**
 * 今日告警、今日应用模块 表格数据，分页数据 相关hook
 * @param {Function} fetchDataAction 获取数据逻辑，指调用接口方法
 * @param {Function} handler 处理接口返回数据逻辑，处理成 今日告警，今日应用模块 通用的数据格式
 */

/**
 * 今日告警 汇总各个等级告警统计的hook
 * @param {Number} alarmsTotal 今日告警总数
 * @param {String} alarmsLastUpdateTime 最近更新时间
 * @param {Object} alarmsCalculateData 今日告警汇总统计数据
 */
export default function useDashboardTable(fetchDataAction = () => {}, handler = () => {}) {
    // 表格数据分页
    const pagination = reactive({
        page: 1,
        size: 10,
        total: 0,
    });
    // 表格数据
    const tableData = ref([]);
    // 表格数据的汇总统计相关数据
    const calculateData = ref({});
    // 最近更新时间
    const lastUpdateTime = ref('');

    const { t: $t } = useI18n();
    // 今日告警统计模块的数据
    const alarmsSummary = reactive({
        title: $t('dashboard.warningToday'),
        total: 0,
        lastUpdateTime: '',
        // 各个等级告警的统计数据
        data: [],
    });
    // 更新该hook状态
    const updateAlarmsSummary = (alarmsTotal, alarmsLastUpdateTime, alarmsCalculateData) => {
        alarmsSummary.total = alarmsTotal || 0;
        alarmsSummary.lastUpdateTime = alarmsLastUpdateTime.value;
        alarmsSummary.data = ALARM_LEVELS_FIELD_NAMES.map(({ fieldName, status }) => {
            const value = alarmsCalculateData.value[fieldName] || 0;
            const target = ALARM_LEVELS.find(item => +item.value === status) || {};
            const label = `${target.label}${$t('dashboard.level')}` || '';
            const classNames = target.classNames || [];
            return {
                label,
                value,
                classNames,
            };
        });
    };

    const applicationsSummary = reactive({
        title: $t('dashboard.todayProjectState'),
        lastUpdateTime: '',
        data: [],
    });
    const updateApplicationsSummary = (applicationsLastUpdateTime, applicationsCalculateData) => {
        applicationsSummary.lastUpdateTime = applicationsLastUpdateTime.value;
        applicationsSummary.data = APPLICATION_STATUS_FIELD_NAMES.map(({ fieldName, status }) => {
            const value = applicationsCalculateData.value[fieldName] || 0;
            const target = APPLICATION_STATUSES.find(item => item.value === status) || {};
            const label = target.label || '';
            const classNames = target.classNames || [];
            return {
                label,
                value,
                classNames,
            };
        });
    };

    const getData = async () => {
        if (!(fetchDataAction instanceof Function)) return { tableData, calculateData };
        const params = {
            page: pagination.page - 1,
            size: pagination.size,
        };
        const res = await fetchDataAction(params);
        const data = handler(res);
        tableData.value = data.tableData;
        calculateData.value = data.calculateData;
        pagination.total = data.total;
        lastUpdateTime.value = data.most_recent_time || '';
        updateAlarmsSummary(pagination.total, lastUpdateTime, calculateData);
        updateApplicationsSummary(lastUpdateTime, calculateData);
    };

    onMounted(getData);
    return {
        pagination,
        tableData,
        calculateData,
        lastUpdateTime,
        alarmsSummary,
        applicationsSummary,
        getData,
    };
}
