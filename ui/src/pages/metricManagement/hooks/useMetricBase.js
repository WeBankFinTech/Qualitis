import {
    ref, reactive, onMounted, computed,
} from 'vue';
import { useRoute, useRouter } from '@fesjs/fes';
import { getURLQueryParams } from '@/common/utils';
import { cloneDeep } from 'lodash-es';
import { fetchMetrics } from '../api';

export default function useMetricBase(showLoading, metricManagementOperation) {
    const router = useRouter();
    const route = useRoute();
    const urlQuery = computed(() => route.query);

    const initAdvanceQueryFormModel = () => ({
        rule_metric_name: '',
        type: '',
        available: null, // boolean
        en_code: '',
        sub_system_name: '',
        multi_envs: null, // boolean
        dev_department_id: '',
        ops_department_id: '',
        action_range: [],
        create_user: '',
        modify_user: '',
    });
    const initQueryFormModel = () => ({
        rule_metric_name: '',
        type: '',
        en_code: '',
        sub_system_name: '',
    });
    // 高级筛选表单
    const advanceQueryFormModel = reactive(initAdvanceQueryFormModel());
    // 查询表单对象
    const queryFormModel = reactive(initQueryFormModel());
    // 已勾选指标列表
    const metricTableSelection = ref([]);
    // 指标列表
    const metrics = ref([]);
    // 指标列表分页对象
    const pagination = reactive({
        page: urlQuery.value.page ? +urlQuery.value.page : 1,
        size: urlQuery.value.size ? +urlQuery.value.size : 10,
        total: 0,
    });
    // 当前查询状态 search/advance
    const curSearchStatus = ref('search');

    const resultByInit = ref(true); // 表格的结果是否是初始状态，搜索后该值变为false
    // 查询指标列表
    const search = async (page = 1) => {
        // if (curSearchStatus.value === 'advance') {
        //     pagination.page = 1; // 上一次是高级筛选,页面重置为1
        // }
        curSearchStatus.value = 'search';
        pagination.page = page;
        Object.assign(advanceQueryFormModel, initAdvanceQueryFormModel());
        const params = {
            rule_metric_name: queryFormModel.rule_metric_name || '',
            type: queryFormModel.type || null,
            en_code: queryFormModel.en_code || '',
            sub_system_name: queryFormModel.sub_system_name || '',
            page: pagination.page - 1,
            size: pagination.size,
        };
        showLoading.value = true;
        try {
            const { total, data } = await fetchMetrics(params);
            metrics.value = data;
            pagination.total = total;
        } catch (error) {
            console.error(error);
            metrics.value = [];
            pagination.total = 0;
        }
        // 这个地方只是重写url，并不会重新加载
        router.replace(
            `${route.path}?${getURLQueryParams({
                query: route.query,
                params: { page: pagination.page, size: pagination.size },
            })}`,
        );
        showLoading.value = false;
        resultByInit.value = false;
    };

    // 高级筛选指标列表
    const handleAdvanceSearch = async () => {
        if (curSearchStatus.value === 'search') {
            pagination.page = 1; // 上一次是顶部栏查询,页面重置为1
        }
        curSearchStatus.value = 'advance';
        Object.assign(queryFormModel, initQueryFormModel());
        const params = {
            rule_metric_name: advanceQueryFormModel.rule_metric_name || '',
            type: advanceQueryFormModel.type || null,
            available: advanceQueryFormModel.available === '' ? null : advanceQueryFormModel.available,
            en_code: advanceQueryFormModel.en_code || '',
            sub_system_name: advanceQueryFormModel.sub_system_name || '',
            multi_envs: advanceQueryFormModel.multi_envs === '' ? null : advanceQueryFormModel.multi_envs,
            dev_department_id: advanceQueryFormModel.dev_department_id || '',
            ops_department_id: advanceQueryFormModel.ops_department_id || '',
            action_range: advanceQueryFormModel.action_range || '',
            create_user: advanceQueryFormModel.create_user || '',
            modify_user: advanceQueryFormModel.modify_user || '',
            page: pagination.page - 1,
            size: pagination.size,
        };
        console.log('advanceQueryFormModel-params', params);
        showLoading.value = true;
        try {
            const { total, data } = await fetchMetrics(params);
            metrics.value = data;
            pagination.total = total;
            metricManagementOperation.value.closeAdvanceModal();
        } catch (error) {
            console.error(error);
            metrics.value = [];
            pagination.total = 0;
        }
        // 这个地方只是重写url，并不会重新加载
        router.replace(
            `${route.path}?${getURLQueryParams({
                query: route.query,
                params: { page: pagination.page, size: pagination.size },
            })}`,
        );
        showLoading.value = false;
    };

    // 指标列表翻页
    const changePage = () => {
        if (curSearchStatus.value === 'search') {
            search(pagination.page);
        } else if (curSearchStatus.value === 'advance') {
            handleAdvanceSearch();
        }
        // 回到列表顶部
        document.getElementsByClassName('wd-content')[0].scrollTop = 0;
    };
    // 重置查询条件
    const reset = async () => {
        Object.assign(queryFormModel, initQueryFormModel());
        Object.assign(advanceQueryFormModel, initAdvanceQueryFormModel());
        await search();
        resultByInit.value = true;
    };
    // 已选指标勾选更新
    const changeMetricTableSelection = (selection) => {
        metricTableSelection.value = Object.values(selection);
    };

    const updateCurrentAfterAdd = () => {
        pagination.page = 1;
        changePage();
    };
    const updateCurrentAfterDelete = (num) => {
        const pageTotal = Math.ceil(pagination.total / pagination.size);
        const pageMod = pagination.total % pagination.size;
        if (pagination.page === pageTotal && pageMod === num) {
            pagination.page = pagination.page - 1;
        }
        changePage();
    };
    const advanceQueryFormModelBak = ref({});
    const handleCancelAdvanceSearch = () => {
        console.log('handleCancelAdvanceSearch-取消高级筛选');
        Object.assign(advanceQueryFormModel, advanceQueryFormModelBak.value);
    };
    const handleShowAdvanceSearch = () => {
        console.log('handleShowAdvanceSearch-展示高级筛选弹窗');
        Object.keys(queryFormModel).forEach((key) => {
            if (queryFormModel[key]) {
                advanceQueryFormModel[key] = queryFormModel[key];
            }
        });
        advanceQueryFormModelBak.value = cloneDeep(advanceQueryFormModel);
    };

    onMounted(async () => {
        await search(pagination.page);
        resultByInit.value = true;
    });

    return {
        advanceQueryFormModel,
        queryFormModel,
        metricTableSelection,
        metrics,
        pagination,
        resultByInit,

        handleAdvanceSearch,
        handleCancelAdvanceSearch,
        handleShowAdvanceSearch,
        search,
        changePage,
        reset,
        changeMetricTableSelection,
        updateCurrentAfterAdd,
        updateCurrentAfterDelete,
    };
}
