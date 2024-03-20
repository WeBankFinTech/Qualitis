import { ref } from 'vue';
import { FMessage } from '@fesjs/fes-design';
import { useI18n } from '@fesjs/fes';

export default function useTemplateSelector(templateRef, updateFn = null) {
    const { t: $t } = useI18n();

    const showExecuteParamsDrawer = ref(false);

    const openExecuteParamsDrawer = () => {
        showExecuteParamsDrawer.value = true;
    };

    const templateModel = ref({
        name: '',
        static: false,
        staticParams: '',
        disconnect: false,
        warning: false,
        level: '',
        receiver: '',
    });

    const confirmSelect = () => {
        const templateList = templateRef.value.templateList;
        let selectedIndex = -1;
        for (let i = 0; i < templateList.length; i++) {
            if (templateList[i].isSelected) {
                selectedIndex = i;
                break;
            }
        }
        if (selectedIndex === -1) return FMessage.warning($t('common.pleaseSelect'));
        showExecuteParamsDrawer.value = false;
        templateModel.value = templateList[selectedIndex];
        templateModel.value.execution_parameters_name = templateModel.value.name;
        // delete templateModel.value.id;
        delete templateModel.value.name;
        if (updateFn) {
            updateFn(templateModel.value);
        }
    };

    const comfirmCancel = () => {
        showExecuteParamsDrawer.value = false;
    };

    return {
        showExecuteParamsDrawer,
        openExecuteParamsDrawer,
        confirmSelect,
        comfirmCancel,
    };
}
