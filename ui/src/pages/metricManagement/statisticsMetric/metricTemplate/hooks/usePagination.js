import {
    ref, reactive, computed, onMounted,
} from 'vue';
import dayjs from 'dayjs';

export const usePagination = (param, cb) => {
    // 分页信息初始化
    const init = () => ({
        page: 0,
        size: 10,
        totalCount: 0,
        totalPage: 0,
    });
    const pagination = ref(init());
    const isLoading = ref(false); // 加载中

    // 查询参数
    const parameter = computed(() => ({
        ...(param?.value || {}),
        page: pagination.value.page,
        size: pagination.value.size,
    }));

    // 设置分页
    const setPagination = (params = {}) => {
        Object.keys(params).forEach((key) => {
            pagination.value[key] = params[key];
        });
    };

    // 回调函数
    const callback = async () => {
        try {
            if (cb) {
                isLoading.value = true;
                const pagedata = await cb(parameter.value);
                isLoading.value = false;
                setPagination(pagedata);
            }
        } catch (error) {
            isLoading.value = false;
            console.log(error);
        }
    };

    // 查询表单
    const handleInit = () => {
        pagination.value.page = 0;
        callback();
    };

    // 使用当前查询条件
    const handleCurrent = () => {
        callback();
    };

    // 分页事件
    const handleCurrentChange = (currentPage, pageSize) => {
        pagination.value.size = pageSize;
        pagination.value.page = currentPage - 1;
        callback();
    };

    // 重置
    const handlePaginationReset = () => {
        pagination.value = init();
        pagination.value.size = param?.value?.size || 10;
        pagination.value.page = param?.value?.page || 0;
    };

    onMounted(() => {
        pagination.value.size = param?.value?.size || 10;
        pagination.value.page = param?.value?.page || 0;
    });

    // 表格内容filter
    const fillText = row => (['null', 'undefined', ''].includes(String(row.cellValue))
        ? '- -'
        : row.cellValue);

    // 表格内容filter
    const fillTimeText = row => (['null', 'undefined', ''].includes(String(row.cellValue))
        ? '- -'
        : dayjs(row.cellValue).format('YYYY-MM-DD HH:mm:ss'));

    return {
        isLoading,
        pagination,
        handleInit,
        handleCurrent,
        handleCurrentChange,
        handlePaginationReset,
        fillText,
        fillTimeText,
    };
};
