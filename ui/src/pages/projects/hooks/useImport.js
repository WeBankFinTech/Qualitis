import { ref } from 'vue';
import { useI18n } from '@fesjs/fes';
import { FMessage } from '@fesjs/fes-design';
import { isIE } from '@/assets/js/utils';
import {
    uploadProject,
    uploadRules,
} from '../api';
import { uploadMetric } from '../../metricManagement/api';

export default function useImport(type = 'project') {
    const { t: $t } = useI18n();

    const nameMap = {
        project: $t('common.project'),
        rule: $t('common.technicalRegulation'),
        metric: $t('common.metric'),
    };
    const uploadActionMap = {
        project: uploadProject,
        rule: uploadRules,
        metric: uploadMetric,
    };

    const isImportingFiles = ref(false);

    const importDatas = async (file, projectId) => {
        try {
            if (isImportingFiles.value) {
                return;
            }
            if (isIE()) return;

            const SIZE = 5;
            if (file.size > SIZE * 1024 * 1024) return FMessage.warn($t('toastWarn.importFile'));
            isImportingFiles.value = true;

            if (!file) return Promise.reject();
            const formData = new FormData();
            formData.append('file', file);
            await uploadActionMap[type](formData, projectId);
            FMessage.success($t('toastSuccess.successImport', { name: nameMap[type] }));
            isImportingFiles.value = false;
            return Promise.resolve();
        } catch (error) {
            console.warn(error);
            isImportingFiles.value = false;
            return Promise.reject();
        }
    };

    return {
        importDatas,
        isImportingFiles,
    };
}
