import { ref } from 'vue';


export default function useRuleQueryInit() {
    const editMode = ref('add');
    const showEditPanel = ref(false);
    const openEditPanel = (mode = 'add') => {
        editMode.value = mode;
        showEditPanel.value = true;
    };

    return {
        editMode,
        showEditPanel,
        openEditPanel,
    };
}
