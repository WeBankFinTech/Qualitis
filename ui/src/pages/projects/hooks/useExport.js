import { ref } from 'vue';
import { isIE, forceDownload } from '@/assets/js/utils';
import { FMessage } from '@fesjs/fes-design';
import { useI18n } from '@fesjs/fes';
import {
    downloadProject,
    downloadProjectRules,
} from '../api';
import { downloadMetric } from '../../metricManagement/api';
import { downloadRelatedRulesOfTableDetail } from '../../rules/query/api';

/**
 * type 下载类型
 * project-项目（在列表页下载）
 * rule-规则（在详情页下载）
 */
export default function useExport(type = 'project') {
    const { t: $t } = useI18n();
    const nameMap = {
        project: $t('common.project'),
        rule: $t('common.technicalRegulation'),
        metric: $t('common.metric'),
        ruleQuery: $t('common.technicalRegulation'),
    };
    const urlMap = {
        project: downloadProject,
        rule: downloadProjectRules,
        metric: downloadMetric,
        ruleQuery: downloadRelatedRulesOfTableDetail,
    };
    const isExporting = ref(false);
    // 防重复
    const isDownloading = ref();

    const exportFileByParams = async (datas) => {
        if (isDownloading.value) {
            return;
        }
        isDownloading.value = true;
        if (isIE()) return;
        if (datas.length === 0) {
            FMessage.warn($t('toastError.selectFile'));
            return;
        }
        let blobUrl;
        try {
            const res = await urlMap[type](datas);
            const fileNameUnicode = res?.headers['content-disposition'].split('filename*=')[1];
            const fileName = fileNameUnicode ? decodeURIComponent(fileNameUnicode.split("''")[1]) : `${nameMap[type]}.xlsx`;
            blobUrl = window.URL.createObjectURL(res.data);
            forceDownload(blobUrl, fileName, () => {
                window.URL.revokeObjectURL(blobUrl);
                isExporting.value = false;
            });
            isDownloading.value = false;
        } catch (error) {
            isDownloading.value = false;
            console.error(error);
            FMessage.error(`${$t('toastError.exportFail', { name: nameMap[type] })}`);
            window.URL.revokeObjectURL(blobUrl);
        }
    };

    return {
        exportFileByParams,
        isExporting,
        isDownloading,
    };
}
