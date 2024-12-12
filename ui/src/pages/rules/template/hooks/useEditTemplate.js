import { ref } from 'vue';
import { useI18n } from '@fesjs/fes';

export default function useRuleQueryInit() {
    const { t: $t } = useI18n();

    // 模版编辑模式
    const editMode = ref('add');
    // 新增模板
    const showEditPanel = ref(false);
    const openEditPanel = (mode = 'add') => {
        editMode.value = mode;
        showEditPanel.value = true;
    };

    // 跨表校验模板表格操作按钮配置
    const selectedDataId = ref(-1);
    const ruleTemplateTableActions = [
        {
            label: $t('common.detail'),
            func: (id) => {
                console.log('查看详情： ', id);
                selectedDataId.value = id;
                openEditPanel('view');
            },
        },
    ];

    return {
        editMode,
        showEditPanel,
        openEditPanel,
        selectedDataId,
        ruleTemplateTableActions,
    };
}
