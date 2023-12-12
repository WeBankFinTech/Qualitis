<template>
    <FModal :show="show" :maskClosable="false" :title="$t('taskQuery.advanceSearch')" @cancel="cancel" @ok="confirmSelect">
        <template #footer>
            <FSpace justify="end">
                <FButton @click="cancel">{{$t('common.cancel')}}</FButton>
                <FButton type="primary" @click="confirmSelect">{{$t('common.ok')}}</FButton>
            </FSpace>
        </template>
        <FForm ref="standardQueryFormRef" :model="condition" :labelWidth="56" :rules="standardFormRules" labelPosition="right">
            <FFormItem label="标准值ID" prop="edition_id">
                <FInput
                    v-model="condition.edition_id"
                    class="form-edit-input"
                    placeholder="请输入标准值ID"
                />
            </FFormItem>
            <FFormItem label="中文名称" prop="cn_name">
                <FInput
                    v-model="condition.cn_name"
                    :maxlength="128"
                    showWordLimit
                    class="form-edit-input"
                    placeholder="请输入中文名称"
                />
            </FFormItem>
            <FFormItem label="英文名称" prop="en_name">
                <FInput
                    v-model="condition.en_name"
                    :maxlength="128"
                    showWordLimit
                    class="form-edit-input"
                    placeholder="请输入英文名称"
                />
            </FFormItem>
            <!-- <FFormItem v-if="condition.edition_id && showVersionList" label="版本号" prop="version">
                <FSelect
                    v-model="condition.version"
                    class="form-edit-input"
                    filterable
                    placeholder="请选择版本号"
                >
                    <FOption
                        v-for="(item, index) in versionList"
                        :key="index"
                        :value="item"
                        :label="item"
                    ></FOption>
                </FSelect>
            </FFormItem> -->
            <FFormItem :label="`来源类型`" prop="source_type">
                <FSelect v-model="condition.source_type" :options="stdSourceList" filterable clearable
                         valueField="code" labelField="message" placeholder="请选择" />
            </FFormItem>
            <FFormItem :label="$t('ruleTemplatelist.developDepartment')">
                <FSelectCascader
                    v-model="condition.dev_department_id"
                    :data="devDivisions"
                    :loadData="loadDevDivisions"
                    valueField="code"
                    clearable
                    remote
                    showPath
                    checkStrictly="child"
                ></FSelectCascader>
            </FFormItem>
            <FFormItem :label="$t('ruleTemplatelist.maintainDepartment')">
                <FSelectCascader
                    v-model="condition.ops_department_id"
                    :data="opsDivisions"
                    :loadData="loadOpsDivisions"
                    valueField="code"
                    clearable
                    remote
                    showPath
                    checkStrictly="child"
                ></FSelectCascader>
            </FFormItem>
            <FFormItem label="可见范围" prop="action_range">
                <FSelectCascader
                    v-model="condition.action_range"
                    :data="visDivisions"
                    :loadData="loadVisDivisions"
                    clearable
                    remote
                    emitPath
                    showPath
                    multiple
                    checkStrictly="all"
                    collapseTags
                    :collapseTagsLimit="3"
                    @change="visSelectChange" />
            </FFormItem>
            <FFormItem label="创建人" prop="create_user">
                <FSelect v-model="condition.create_user" :options="formatProjectUserList" clearable filterable>
                </FSelect>
            </FFormItem>
            <FFormItem label="修改人" prop="modify_user">
                <FSelect v-model="condition.modify_user" :options="formatProjectUserList" clearable filterable>
                </FSelect>
            </FFormItem>
        </FForm>
    </FModal>
</template>
<script setup>
import {
    ref, watch, onMounted,
} from 'vue';
import { request as FRequest, useI18n } from '@fesjs/fes';
import useDivisions from '@/hooks/useDivisions';
import useDepartment from '@/hooks/useDepartment';
import usePermissionDivisions from '@/hooks/usePermissionDivisions';
import useProjectAuth from '@/pages/projects/hooks/useProjectAuth';
import { useDataList } from '../hooks/useDataList';

// 获取用户列表
const {
    projectUserList,
    getProjectUserList,
} = useProjectAuth();
const { t: $t } = useI18n();
const {
    stdSourceList,
    loadStdSource,
} = useDataList();
const {
    divisions: devDivisions,
    loadDivisions: loadDevDivisions,
    curSubDepartData: devCurSubDepartData,
} = usePermissionDivisions();
const {
    divisions: opsDivisions,
    loadDivisions: loadOpsDivisions,
    curSubDepartData: opsCurSubDepartData,
} = usePermissionDivisions();
const {
    divisions: visDivisions,
    loadDivisions: loadVisDivisions,
    curSubDepartData: visCurSubDepartData,
    initDepartment,
} = useDivisions();
const {
    selectDevDate,
    selectOpsDate,
    selectVisDate,
    handleDevId,
    handleOpsId,
    visSelectChange,
} = useDepartment(devCurSubDepartData, opsCurSubDepartData, visCurSubDepartData, visDivisions);
// eslint-disable-next-line no-undef
const props = defineProps({
    show: {
        type: Boolean,
        default: false,
    },
    parentCondition: {
        type: Object,
        default: {},
    },
});

// eslint-disable-next-line no-undef
const emit = defineEmits(['update:show', 'update:parentCondition', 'loadData']);

const condition = ref({
    edition_id: '',
    action_range: [],
});

const standardFormRules = ref({});

// const versionList = ref([]);
const standardQueryFormRef = ref(null);

const {
    divisions,
} = useDivisions();

const cancel = () => {
    emit('update:show', false);
};

const confirmSelect = () => {
    // 选择了多少高级选择
    let selectParamsLen = 0;
    const conditionKeys = Object.keys(condition.value);
    console.log(condition.value);
    for (let i = 0; i < conditionKeys.length; i++) {
        const key = conditionKeys[i];
        // 不存在数字0的情况，所以下面的判断可以用
        const value = condition.value[key];
        if (!!value && typeof value === 'object') {
            if (value.length > 0 || Object.keys(value).length > 0) {
                // 值不为空
                selectParamsLen++;
            }
        } else if (value) {
            selectParamsLen++;
        }
    }
    emit('update:show', false);
    emit('update:parentCondition', condition.value);
    emit('loadData', selectParamsLen);
};

watch(() => props.parentCondition, () => {
    if (props.parentCondition) {
        condition.value = Object.assign({}, condition.value, props.parentCondition);
    }
}, { deep: true });

// const showVersionList = ref(false);
// const getVersionsById = async () => {
//     try {
//         if (!condition.value.edition_id) {
//             return;
//         }
//         showVersionList.value = true;
//         const data = await FRequest(`/api/v1/projector/standardValue/version/${condition.value.edition_id}`, {}, 'get');
//         versionList.value = data || [];
//     } catch (err) {
//         console.warn(err);
//     }
// };

const reset = () => {
    condition.value = {
        edition_id: '',
        action_range: [],
    };
    selectVisDate.value = [];
};
const formatProjectUserList = ref([]);
onMounted(async () => {
    loadStdSource();
    await getProjectUserList();
    formatProjectUserList.value = projectUserList.value.map(item => ({
        label: item,
        value: item,
    }));
});
// eslint-disable-next-line no-undef
defineExpose({ reset, selectVisDate });
</script>
