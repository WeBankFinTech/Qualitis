import { ref } from 'vue';

export default function useTableHeaderConfig() {
    // 实际展示的列
    const tableHeaders = ref([]);

    // 决定当前表单列是否显示
    const checkTColShow = col => tableHeaders.value.map(item => item.prop).includes(col);

    // 显示表头列配置modal
    const showTableHeaderConfig = ref(false);

    // 显示运维报表订阅管理
    const showSubsManagement = ref(false);

    const toggleTColConfig = () => {
        showTableHeaderConfig.value = true;
    };
    const openSubsManagement = () => {
        showSubsManagement.value = true;
    };

    return {
        checkTColShow,
        toggleTColConfig,
        tableHeaders,
        showTableHeaderConfig,
        openSubsManagement,
        showSubsManagement,
    };
}
