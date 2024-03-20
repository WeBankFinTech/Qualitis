import { ref, onMounted } from 'vue';
import { useI18n } from '@fesjs/fes';
import { getLabelFromList } from '@/assets/js/utils';
import { BOOLEANS as _booleans } from '@/assets/js/const';
import { fetchOptions } from '../api';

export default function useMetricInit() {
    const { t: $t, getLocaleMessage } = useI18n();

    // 指标分类列表
    const metricCategories = ref([]);
    // 指标英文名列表
    const enCodes = ref([]);
    // 子系统列表
    const subSystemNames = ref([]);
    // 指标频率列表
    const enIndexMap = getLocaleMessage('en-US').indexManagement;
    const cnIndexMap = getLocaleMessage('zh-CN').indexManagement;
    const metricFrequencies = [
        {
            label: $t('indexManagement.daily'), value: 1, cnName: cnIndexMap.daily, enName: enIndexMap.daily,
        },
        {
            label: $t('indexManagement.monthly'), value: 2, cnName: cnIndexMap.monthly, enName: enIndexMap.monthly,
        },
        {
            label: $t('indexManagement.quarterly'), value: 3, cnName: cnIndexMap.quarterly, enName: enIndexMap.quarterly,
        },
        {
            label: $t('indexManagement.halfYear'), value: 4, cnName: cnIndexMap.halfYear, enName: enIndexMap.halfYear,
        },
        {
            label: $t('indexManagement.year'), value: 5, cnName: cnIndexMap.year, enName: enIndexMap.year,
        },
        {
            label: $t('indexManagement.single'), value: 6, cnName: cnIndexMap.single, enName: enIndexMap.single,
        },
    ];

    // 拼接子系统名称
    const getSystemName = (data, row) => {
        let str = '';
        if (row.sub_system_name) {
            str = row.sub_system_name;
        }
        if (row.subSystemName) {
            str = row.subSystemName;
        }
        if (str === '') {
            str = data;
        }
        return str;
    };

    // 子系统formatter
    const subSystemFormatter = ({ row, cellValue }) => {
        const arr = [
            getSystemName(cellValue, row),
            row.product_name || '',
            row.buss_custom || '',
        ];
        return arr.filter(v => !!v).join('/') || '--';
    };

    // _booleans先进行国际化转化
    const booleans = _booleans.map(v => ({ label: $t(v.label), value: v.value }));

    // 指标分类formatter
    const typeFormatter = ({ cellValue }) => getLabelFromList(metricCategories.value, cellValue);

    // 指标是否可用formatter
    const availableFormatter = ({ cellValue }) => (typeof cellValue === 'boolean' ? getLabelFromList(booleans, cellValue) : '--');

    // 是否为多DCN指标formatter
    const multiEnvFormatter = ({ cellValue }) => (typeof cellValue === 'boolean' ? getLabelFromList(booleans, cellValue) : '--');

    // 指标频率formatter
    const frequencyFormatter = ({ cellValue }) => getLabelFromList(metricFrequencies, cellValue);

    onMounted(async () => {
        const res = await fetchOptions();
        enCodes.value = res.en_code || [];
        subSystemNames.value = res.sub_system_name_condition || [];
        if (Array.isArray(res.rule_metric_type)) {
            const lang = localStorage.getItem('currentLanguage') || 'zh-CN';
            const labelFieldNameMap = {
                'zh-CN': 'cnName',
                'en-US': 'enName',
            };
            metricCategories.value = res.rule_metric_type.map(item => ({
                ...item,
                label: item[labelFieldNameMap[lang]],
                value: item.id,
            }));
        }
    });

    return {
        // boolean值列表
        booleans,
        // 指标分类列表
        metricCategories,
        // 指标英文名列表
        enCodes,
        // 子系统列表
        subSystemNames,
        // 指标频率列表
        metricFrequencies,

        // 子系统formatter
        subSystemFormatter,
        // 指标分类formatter
        typeFormatter,
        // 指标是否可用formatter
        availableFormatter,
        // 指标频率formatter
        frequencyFormatter,
        // 是否为多DCN指标formatter
        multiEnvFormatter,
    };
}
