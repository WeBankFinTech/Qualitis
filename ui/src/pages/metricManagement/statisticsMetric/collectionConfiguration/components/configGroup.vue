<template>
    <div class="wrap rule-detail-form edit">
        <div class="header">
            {{$t('_.配置组')}}{{configIndex.toString().padStart(2, '0')}}
            <span v-if="removable" class="delete-btn" @click="deleteInputMeta">
                <MinusCircleOutlined style="margin-right: 4.67px;" />{{$t('_.删除')}}</span>
        </div>
        <FForm
            ref="inputMetaForm"
            :labelWidth="68"
            labelPosition="right"
            :model="inputConfigData"
            :rules="inputMetaRules"
        >
            <!-- 字段是否多选 (占位符类型：字段)-->
            <FFormItem
                :label="$t('_.采集方式')"
                prop="collect_type"
            >
                <FRadioGroup
                    v-model="inputConfigData.collect_type"
                    class="form-edit-input"
                    :cancelable="false"
                >
                    <FRadio :value="true">{{$t('_.手动录入')}}</FRadio>
                    <FRadio :value="false">{{$t('_.自动识别')}}</FRadio>
                    <div class="tip-icon">
                        <FTooltip placement="right">
                            <QuestionCircleOutlined />
                            <template #content>
                                <div class="tip-content">{{$t('_.自动识别情况下，所有字段都会被录入空值量，空置率的采集配置，所有数值字段都会被录入总和统计、平均统计、最大值统计，最小值统计，标准差统计的采集配置，注意，首个字段会被录入表行数统计。')}}</div>
                            </template>
                        </FTooltip>
                    </div>
                </FRadioGroup>
                <span class="form-preview-label">{{inputConfigData.field_multiple_choice ? '是' : '否'}}</span>
            </FFormItem>
            <div v-if="inputConfigData.collect_type">
                <FFormItem :label="$t('_.采集信息')" prop="calcu_unit_configs">
                    <div class="add-box" style="width:100%">
                        <div v-for="(data, Iindex) in inputConfigData.calcu_unit_configs" :key="Iindex" class="add" style="width:100%">
                            <FFormItem :prop="`calcu_unit_configs.${Iindex}.template_id`" style="margin-right:8px" :rules="[{ trigger: ['change'], required: true, type: 'number', message: '不能为空' }]">
                                <FSelect
                                    v-model="inputConfigData.calcu_unit_configs[Iindex].template_id"
                                    style="width: 367px"
                                    filterable
                                    :placeholder="'请选择采集算子'"
                                    :options="templateList"
                                    valueField="template_id"
                                    labelField="template_name"
                                    @change="templateChange(Iindex)"
                                >
                                </FSelect>
                            </FFormItem>
                            <FFormItem :prop="`calcu_unit_configs.${Iindex}.columns`" style="margin-right:8px" :rules="[{ trigger: ['change', 'blur'], required: true,type: 'array', message: '不能为空' }]">
                                <FSelect
                                    v-model="inputConfigData.calcu_unit_configs[Iindex].columns"
                                    style="width: 367px"
                                    filterable
                                    :placeholder="'请先选择采集对象和代理用户'"
                                    multiple
                                    :options="columnList"
                                    valueField="column_name"
                                    labelField="label"
                                >
                                </FSelect>
                            </FFormItem>
                            <FFormItem props="icon" style="flex-direction: row">
                                <div class="act">
                                    <MinusCircleOutlined style="margin-right:8px" :class="inputConfigData.calcu_unit_configs.length === 1 ? 'disgray' : 'gray'" @click="delcapa(Iindex)" />
                                    <PlusCircleOutlined v-if="Iindex === inputConfigData.calcu_unit_configs.length - 1" class="add-icon" @click="addcapa(Iindex)" />
                                </div>
                            </FFormItem>
                        </div>
                    </div>
                </FFormItem>
                <FFormItem :label="$t('_.采集脚本')" prop="collect_script" style="margin-top:-22px">
                    <FInput v-model="inputConfigData.collect_script" type="textarea" disabled :placeholder="$t('_.根据所选采集对象和算子反显')" />
                </FFormItem>
            </div>
            <div class="form-bottom">
                <FFormItem :label="$t('_.执行参数')" prop="execution_parameters_name">
                    <FInput
                        v-model="inputConfigData.execution_parameters_name"
                        class="form-edit-input"
                        readonly
                        :placeholder="$t('common.pleaseSelect')"
                        @focus="openExecuteParamsDrawer" />
                    <div class="form-preview-label">{{inputConfigData.execution_parameters_name || '--'}}</div>
                </FFormItem>
            </div>
        </FForm>
    </div>
    <!-- 选择参数模版 -->
    <FDrawer
        v-model:show="showExecuteParamsDrawer"
        :title="$t('myProject.template')"
        displayDirective="if"
        :footer="true"
        width="50%"
    >
        <template #footer>
            <FButton type="primary" @click="confirmSelect">{{$t('myProject.confirmSelect')}}</FButton>
        </template>
        <ProjectTemplate ref="templateRef" :showHeader="false" :isEmbed="true" :curExeName="inputConfigData.execution_parameters_name" class="project-template-style"></ProjectTemplate>
    </FDrawer>
</template>
<script setup>

import {
    useI18n, request,
} from '@fesjs/fes';
import {
    computed, inject, ref, watch,
} from 'vue';
import { FMessage, FModal } from '@fesjs/fes-design';
import usePermissionDivisions from '@/hooks/usePermissionDivisions';
import { cloneDeep } from 'lodash-es';
import {
    MinusCircleOutlined, QuestionCircleOutlined, PlusCircleOutlined,
} from '@fesjs/fes-design/es/icon';
import ProjectTemplate from '@/pages/projects/template';
import { es } from 'date-fns/locale';

const { t: $t } = useI18n();

// eslint-disable-next-line no-undef
const props = defineProps({
    inputConfigData: {
        type: Object,
        default: () => ({}),
        required: true,
    },
    configIndex: {
        type: Number,
        requierd: true,
    },
    proxy_user: {
        type: String,
        default: '',
    },
    removable: {
        type: Boolean,
        requierd: true,
        default: false,
    },
    templateList: {
        required: true,
        type: Array,
    },
    clusterVivisions: {
        type: Array,
        default: () => [],
    },
    columnList: {
        type: Array,
        default: () => [],
    },
});
// eslint-disable-next-line no-undef
const emit = defineEmits(['update:inputConfigData', 'delete']);
const inputConfigData = ref(props.inputConfigData);
const currentInputMetaData = computed({
    get: () => props.inputConfigData,
    set: (value) => {
        emit('update:inputConfigData', value);
    },
});
const deleteInputMeta = () => {
    emit('delete');
};
const inputMetaRules = {
    collect_type: [
        {
            required: inputConfigData.value.collect_type,
            type: 'boolean',
            message: $t('_.不能为空'),
        },
    ],
    collect_script: [
        {
            required: inputConfigData.value.collect_type,
            message: $t('_.不能为空'),
        },
    ],
    execution_parameters_name: [
        {
            required: true,
            type: 'string',
            trigger: 'change',
            message: $t('_.不能为空'),
        },
    ],
    calcu_unit_configs: [{
        required: inputConfigData.value.collect_type,
        type: 'array',
        message: $t('_.不能为空'),
    }],
};
// 选择模板
const templateRef = ref(null);
const showExecuteParamsDrawer = ref(false);
const openExecuteParamsDrawer = () => {
    showExecuteParamsDrawer.value = true;
};
const confirmSelect = () => {
    const templateList = templateRef.value.templateList;
    let selectedIndex = -1;
    for (let i = 0; i < templateList.length; i++) {
        if (templateList[i].isSelected) {
            selectedIndex = i;
        }
    }
    if (selectedIndex === -1) return FMessage.warning($t('common.pleaseSelect'));
    showExecuteParamsDrawer.value = false;
    inputConfigData.value.execution_parameters_name = templateList[selectedIndex].name;
};
const inputMetaForm = ref(null);
const valid = async () => {
    try {
        await inputMetaForm.value?.validate();
        return true;
    } catch (e) {
        return false;
    }
};
const delcapa = (Iindex) => {
    if (inputConfigData.value.calcu_unit_configs.length > 1) {
        inputConfigData.value.calcu_unit_configs.splice(Iindex, 1);
    }
};
const addcapa = (Iindex) => {
    inputConfigData.value.calcu_unit_configs.push({
        template_id: null,
        columns: null,
    });
};
// eslint-disable-next-line no-undef
defineExpose({ valid });
// 查询调度详情
const templateChange = async (Iindex) => {
    console.log('查询调度详情', inputConfigData.value.calcu_unit_configs[Iindex].template_id, Iindex);
};
const updateCollectScript = (configs) => {
    inputConfigData.value.collect_script = props.templateList
        .filter(item => configs.some(obj => obj.template_id === item.template_id))
        .map(ii => ii.sql_action)
        .join('\n');
};

watch(() => inputConfigData.value.calcu_unit_configs, (newConfigs) => {
    updateCollectScript(newConfigs);
}, { deep: true });
watch(() => props.columnList, (newConfigs) => {
    inputConfigData.value.calcu_unit_configs.forEach((item, index) => { item.columns = []; });
}, { deep: true });

</script>
<style lang='less' scoped>
.wrap {
    padding: 16px;
    border: 1px solid #cfd0d3;
    border-radius: 4px;
    margin-bottom: 16px;
    margin-right: 8px;
    width: 913px;
}
.wrap.view{
    background-color: #f7f7f8;
    border: none;

    :deep(.fes-form-item){
        margin-bottom: 8px;
    }
}
.tip-icon {
    display: flex;
    margin-left: -13px;
}
.tip-content {
    width: 300px;
    font-family: PingFangSC-Regular;
    font-size: 12px;
    color: #FFFFFF;
    letter-spacing: 0;
    line-height: 20px;
    font-weight: 400;
}
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    font-family: PingFangSC-Regular;
    font-size: 14px;
    color: #0F1222;
    line-height: 22px;
    font-weight: 600;
    .delete-btn {
        cursor: pointer;
        display: flex;
        align-items: center;
        color:#93949B;
        font-weight: 400;
    }
}
.rule-detail-form {
    :deep(.fes-input) {
        max-width: 100%;
    }
    :deep(.fes-select) {
        max-width: 100%;
    }
    :deep(.fes-textarea) {
        max-width: 100%;
    }
}
.add-box {
    width: 100%;
}
.add {
    display: flex;
}
.disgray {
    color: #B7B7B7;
}
.gray {
    color: #63656F;
}
.add-icon {
    color: #5384FF;
}
.form-bottom {
    :deep(.fes-form-item){
        margin-bottom: 0px !important;
        width: 100%;
    }
}
</style>
