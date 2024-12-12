import {
    ref, reactive, onMounted, computed, toRaw,
} from 'vue';
import { useRoute, useRouter } from '@fesjs/fes';
import { FMessage } from '@fesjs/fes-design';
import { getURLQueryParams } from '@/common/utils';
import { format } from 'date-fns';
import { fetchProjectDetail, fetchProjectAllRules } from '../api';

export default function useProjectDetail(projectId) {
    const router = useRouter();
    const route = useRoute();
    const urlQuery = computed(() => route.query);

    const pagination = reactive({
        current: urlQuery.value.page ? +urlQuery.value.page : 1,
        size: urlQuery.value.pageSize ? +urlQuery.value.pageSize : 10,
        total: 0,
    });
    const projectDetail = ref({});
    const projectRules = ref({});
    const projectAllRules = ref([]);
    // 筛选条件
    const filrateCondition = ref({});
    const isLoading = ref(false);

    const handleRuleDetails = (data) => {
        if (!Array.isArray(data)) return {};
        const temp = {};
        data.forEach((item) => {
            if (Object.keys(temp).includes(item.rule_group_name)) {
                temp[item.rule_group_name].push(item);
            } else {
                temp[item.rule_group_name] = [item];
            }
        });
        return temp;
    };
    // 获得指定项目id下的所有规则
    const getProjectAllRules = async () => {
        try {
            const res = await fetchProjectAllRules(projectId);
            const originAllRules = handleRuleDetails(res.rule_details || []);
            // projectDetail中的规则为当前页的规则数据，替换为全部的规则数据
            let index = 0;
            Object.keys(originAllRules).forEach((key) => {
                originAllRules[key].forEach((subkey) => {
                    projectAllRules.value[index++] = toRaw(subkey);
                });
            });
            console.log('rules', projectAllRules);
        } catch (err) {
            console.warn(err);
        }
    };
    const getProjectDetail = async () => {
        if (isLoading.value) {
            return;
        }
        isLoading.value = true;
        try {
            const params = {
                page: pagination.current - 1,
                size: pagination.size,
                ...filrateCondition.value,
            };
            if (params?.create_time?.length > 0) {
                params.start_create_time = format(new Date(params.create_time[0]), 'yyyy-MM-dd HH:mm:ss');
                params.end_create_time = format(new Date(params.create_time[1]), 'yyyy-MM-dd HH:mm:ss');
                params.create_time = null;
            }
            if (params?.modify_time?.length > 0) {
                params.start_modify_time = format(new Date(params.modify_time[0]), 'yyyy-MM-dd HH:mm:ss');
                params.end_modify_time = format(new Date(params.modify_time[1]), 'yyyy-MM-dd HH:mm:ss');
                params.modify_time = null;
            }
            const res = await fetchProjectDetail(projectId, params);
            const projectDetailRes = res.project_detail || {};
            projectDetail.value = {
                project_id: projectId,
                git_branch: projectDetailRes.git_branch,
                git_repo: projectDetailRes.git_repo,
                git_root_dir: projectDetailRes.git_root_dir,
                git_type: projectDetailRes.git_type,
                project_name: projectDetailRes.project_name,
                cn_name: projectDetailRes.cn_name,
                description: projectDetailRes.description,
                stakeholders: projectDetailRes.stakeholders,
                create_user: projectDetailRes.create_user,
                executor_user: projectDetailRes.executor_user,
                level: projectDetailRes.level,
                type: projectDetailRes.type,
                reportAll: projectDetailRes.report_all ? '1' : '2',
                project_label: projectDetailRes?.project_label || [],
                sub_system_id: String(projectDetailRes.sub_system_id || ''),
                sub_system_name: projectDetailRes.sub_system_name,
                rule_details: res.rule_details,
            };
            projectRules.value = handleRuleDetails(res.rule_details);
            pagination.total = res.total;
            isLoading.value = false;
        } catch (error) {
            console.error('ERROR in getProjectDetail: ', error);
            // FMessage.warn(error.data?.message || 'Get project detail error');
            isLoading.value = false;
        }
    };

    const pageChange = () => {
        getProjectDetail();
        // 这个地方只是重写url，并不会重新加载
        router.replace(`${route.path}?${getURLQueryParams({
            query: route.query,
            params: { page: pagination.current, pageSize: pagination.size },
        })}`);
        // 回到列表顶部
        document.getElementsByClassName('wd-content')[0].scrollTop = 0;
    };
    onMounted(() => {
        getProjectDetail();
        getProjectAllRules();
    });
    return {
        projectDetail,
        projectRules,
        projectAllRules,
        pagination,
        pageChange,
        filrateCondition,
        getProjectDetail,
        getProjectAllRules,
    };
}
