<template>
    <template v-if="!isExporting">
        <FSpace :size="CONDITIONBUTTONSPACE">
            <template v-for="(action, index) in actions">
                <template v-if="['btn', 'download'].includes(action.actionType)">
                    <FButton
                        :key="index"
                        :type="action.type"
                        @click="clickBtn(action)">
                        <template #icon>
                            <component :is="action.icon" />
                        </template>
                        {{action.label}}
                    </FButton>
                </template>
                <template v-else-if="action.actionType === 'upload'">
                    <FButton
                        :key="index"
                        :loading="action.actionType === 'upload' && isImportingFiles"
                        :disabled="action.actionType === 'upload' && isImportingFiles"
                        :type="action.type"
                        @click="clickProjectBtn(action)"
                    >
                        <template #icon>
                            <component :is="action.icon" />
                        </template>
                        {{action.label}}
                        <input v-if="parentType !== 'project'"
                               :ref="el => { if (el) fileInputRefs[index] = el }"
                               type="file"
                               class="btn-file-input"
                               accept=".xlsx"
                               @change="changeFile(action, index)" />
                    </FButton>
                </template>
                <template v-else-if="action.actionType === 'dropdown'">
                    <FDropdown
                        :key="index"
                        :trigger="action.trigger"
                        :options="action.options"
                        @click="clickDropdown($event, action)">
                        <FButton :type="action.type">
                            <template #icon>
                                <component :is="action.icon" />
                            </template>
                            {{action.label}}
                        </FButton>
                    </FDropdown>
                </template>
            </template>
        </FSpace>
    </template>
    <template v-else>
        <FSpace :size="CONDITIONBUTTONSPACE">
            <FButton v-if="isVisual || isDelete" type="primary" :disabled="isDownloading" :loading="isDownloading" @click="confirmDownload">确认</FButton>
            <FButton v-else :disabled="isDownloading" :loading="isDownloading" type="primary" @click="confirmDownload">{{$t('myProject.confirmExport')}}</FButton>
            <FButton :disabled="isDownloading" @click="cancelDownload">{{$t('common.cancel')}}</FButton>
        </FSpace>
    </template>
    <template v-if="parentType === 'project'">
        <!-- 导入项目 -->
        <ImportProjectByDetail v-if="showProjectImport" v-model:show="showProjectImport" type="table" @uploadSucess="updateProjectTable"></ImportProjectByDetail>
        <!-- 导出项目 -->
        <EXportProjectByDetail v-if="showProjectExport" v-model:show="showProjectExport" type="table"></EXportProjectByDetail>
    </template>
</template>
<script setup>
import {
    defineProps, defineEmits, ref, onBeforeUpdate,
} from 'vue';
import {
    PlusOutlined, UploadOutlined, DownloadOutlined, MoreCircleOutlined,
} from '@fesjs/fes-design/es/icon';
import { useI18n } from '@fesjs/fes';
import { FMessage, FSpace } from '@fesjs/fes-design';
import { isIE } from '@/assets/js/utils';
import { CONDITIONBUTTONSPACE } from '@/assets/js/const';
import ImportProjectByDetail from '@/pages/projects/components/ImExport/importProjectByDetail';
import EXportProjectByDetail from '@/pages/projects/components/ImExport/exportProjectByDetail';

const { t: $t } = useI18n();
const props = defineProps({
    // 与项目的导入导出做到差异化 normal-非项目模块，project-项目模块
    parentType: {
        type: String,
        required: false,
        default: 'normal',
    },
    actions: {
        type: Array,
        required: true,
    },
    isExporting: {
        type: Boolean,
        required: false,
        default: false,
    },
    isImportingFiles: {
        type: Boolean,
        required: false,
        default: false,
    },
    isDownloading: {
        type: Boolean,
        required: false,
        default: false,
    },
    isVisual: {
        type: Boolean,
        required: false,
        default: false,
    },
    isDelete: {
        type: Boolean,
        required: false,
        default: false,
    },
});
const emit = defineEmits([
    'update:isExporting',
    'update:isVisual',
    'update:isDelete',
    'confirmDownload',
    'cancelDownload',
]);
const fileInputRefs = ref([]);
// 项目模块文件导入导出
const showProjectImport = ref(false);
const showProjectExport = ref(false);
const clickProjectBtn = (action) => {
    if (props.parentType === 'project') {
        if (action.actionType === 'btn') {
            action.handler();
        } else if (action.actionType === 'download') {
            showProjectExport.value = true;
        } else if (action.actionType === 'upload') {
            showProjectImport.value = true;
        }
    }
};
const clickBtn = (action) => {
    if (props.parentType === 'project') {
        clickProjectBtn(action);
        return;
    }
    if (!(action.handler instanceof Function)) return;
    if (action.actionType === 'btn') {
        action.handler();
    } else if (action.actionType === 'download') {
        emit('update:isExporting', true);
    }
};
const updateProjectTable = () => {
    const actions = props.actions;
    actions.forEach((e) => {
        if (e.actionType === 'upload') {
            console.log(e);
            e.handler();
        }
    });
};
const changeFile = (action, index) => {
    if (!(action.handler instanceof Function)) return;
    if (isIE()) return;
    const fileInputRef = fileInputRefs.value[index];
    if (!fileInputRef) return;
    const file = fileInputRef.files[0];
    const SIZE = 5;
    if (file.size > SIZE * 1024 * 1024) {
        FMessage.warn($t('toastWarn.importFile'));
        return;
    }
    action.handler(file);
    fileInputRef.value = null;
};
const clickDropdown = (value, action) => {
    if (!Array.isArray(action.options)) return;
    const target = action.options.find(item => item.value === value);
    if (!target && !(target.handler instanceof Function)) return;
    target.handler();
};
const confirmDownload = () => {
    emit('confirmDownload');
};
const cancelDownload = () => {
    emit('update:isExporting', false);
    emit('update:isVisual', false);
    emit('update:isDelete', false);
    emit('cancelDownload');
};
onBeforeUpdate(() => {
    fileInputRefs.value = [];
});
</script>
