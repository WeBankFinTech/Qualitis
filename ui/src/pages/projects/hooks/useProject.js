import {
    ref, reactive, onMounted, computed,
} from 'vue';
import { useI18n, useRoute, useRouter } from '@fesjs/fes';
import { FModal, FMessage } from '@fesjs/fes-design';
import { FORM_MODE } from '@/assets/js/const';
import { getURLQueryParams } from '@/common/utils';
import { deleteProject } from '../api';

export default function useProject(fetchAction = () => {}, showLoading, workSpace = '') {
    const { t: $t } = useI18n();
    const router = useRouter();
    const route = useRoute();
    const urlQuery = computed(() => route.query);

    const pagination = reactive({
        current: urlQuery.value.page ? +urlQuery.value.page : 1,
        size: urlQuery.value.pageSize ? +urlQuery.value.pageSize : 10,
        total: 0,
    });
    const projects = ref([]);
    const projectSelection = ref([]);
    // 定义查询条件
    const queryCondition = ref({});
    const getProjects = async () => {
        const params = {
            page: Math.max(pagination.current - 1, 0),
            size: pagination.size,
            ...queryCondition.value,
        };
        try {
            const res = await fetchAction(params);
            projects.value = res.data;
            pagination.total = res.total;
        } catch (error) {
            console.error('ERROR IN getProjects: ', error);
        }
        // 这个地方只是重写url，并不会重新加载
        router.replace(`${route.path}?${getURLQueryParams({
            query: route.query,
            params: { page: pagination.current, pageSize: pagination.size },
        })}`);
        return Promise.resolve();
    };
    const pageChange = () => {
        getProjects();
        // 回到列表顶部
        document.getElementsByClassName('wd-content')[0].scrollTop = 0;
    };
    const projectSelectionChange = (selection) => {
        projectSelection.value = Object.values(selection);
    };
    // 删除项目
    const handleDeleteProject = (data) => {
        FModal.confirm({
            title: $t('common.prompt'),
            content: $t('myProject.deleteProject', { name: data.project_name }),
            onOk: async () => {
                try {
                    if (showLoading.value) {
                        return;
                    }
                    showLoading.value = true;
                    await deleteProject({ project_id: data.project_id });
                    FMessage.success($t('toastSuccess.deleteSuccess'));
                    const pageTotal = Math.ceil(pagination.total / pagination.size);
                    const pageMod = pagination.total % pagination.size;
                    if (pagination.current === pageTotal && pageMod === 1) {
                        pagination.current = Math.max(pagination.current - 1, 0);
                    }
                    await getProjects();
                    showLoading.value = false;
                } catch (err) {
                    console.warn(err);
                    showLoading.value = false;
                    return Promise.reject();
                }
            },
            onCancel: () => {
                showLoading.value = false;
            },
        });
    };
    onMounted(async () => {
        const storage = sessionStorage.getItem(workSpace);
        if (workSpace && storage) {
            const { _queryCondition } = JSON.parse(storage);
            queryCondition.value = { ..._queryCondition };
        }
        showLoading.value = true;
        await getProjects();
        showLoading.value = false;
    });
    return {
        pagination,
        projects,
        queryCondition,
        projectSelection,
        getProjects,
        pageChange,
        projectSelectionChange,
        handleDeleteProject,
        // 新增项目相关
        // eslint-disable-next-line no-use-before-define
        ...useAddProject(pagination, getProjects, showLoading),
    };
}

function useAddProject(pagination, getProjects, showLoading) {
    const showProjectFormModal = ref(false);
    const projectFormMode = ref('');
    const handleAddProject = () => {
        showProjectFormModal.value = true;
        projectFormMode.value = FORM_MODE.ADD;
    };
    const handleProjectFormModalSuccess = async (mode) => {
        console.log('mode: ', mode);
        showLoading.value = true;
        projectFormMode.value = '';
        showProjectFormModal.value = false;
        pagination.current = 1;
        await getProjects();
        showLoading.value = false;
    };
    return {
        showProjectFormModal,
        projectFormMode,
        handleAddProject,
        handleProjectFormModalSuccess,
    };
}
