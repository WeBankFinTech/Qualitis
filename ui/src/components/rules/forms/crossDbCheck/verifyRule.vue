<template>
    <div class="rule-detail-form" :class="{ edit: ruleData.currentProject.editMode !== 'display' }">
        <h6 class="wd-body-title">校验规则</h6>
        <!-- {{ verifyRuleData }} -->
        <BasicInfo ref="basicInfoRef" v-model:basicInfo="verifyRuleData" />
        <FForm
            ref="verifyRuleFormRef"
            :layout="layout"
            :class="layout === 'inline' ? 'inline-form-item' : ''"
            :model="verifyRuleData"
            :rules="verifyRuleRules"
            :labelWidth="96"
            :labelPosition="ruleData.currentProject.editMode !== 'display' ? 'right' : 'left'"
        >
            <FFormItem label="比对方向" prop="contrast_type">
                <FSelect
                    v-model="verifyRuleData.contrast_type"
                    class="form-edit-input"
                    filterable
                    placeholder="请选择比对方向"
                    :options="compareDirectionList"
                ></FSelect>
                <div
                    class="form-preview-label"
                >
                    {{getLabelByValue(compareDirectionList, verifyRuleData.contrast_type)}}
                </div>
            </FFormItem>
            <FFormItem label="过滤设置">
                <FilterSetting
                    ref="filterSettingRef"
                    v-model:filterSettingList="verifyRuleData.filterSettingList"
                ></FilterSetting>
            </FFormItem>
            <FFormItem label="执行参数" prop="execution_parameters_name">
                <FInput
                    v-model="verifyRuleData.execution_parameters_name"
                    class="form-edit-input excution-parameters-name-input"
                    readonly
                    :placeholder="$t('common.pleaseSelect')"
                    @focus="openExecuteParamsDrawer"
                />
                <div class="form-preview-label">{{verifyRuleData.execution_parameters_name || '--'}}</div>
            </FFormItem>
        </FForm>
        <!-- 选择执行参数模版 -->
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
            <ProjectTemplate
                ref="templateRef"
                :showHeader="false"
                :isEmbed="true"
                class="project-template-style"
                :curExeName="verifyRuleData.execution_parameters_name"
            ></ProjectTemplate>
        </FDrawer>
    </div>
</template>
<script setup>
import {
    ref, computed, onMounted, provide, watch, unref,
} from 'vue';
import { useStore } from 'vuex';
import { useI18n, request as FRequest } from '@fesjs/fes';
import BasicInfo from '@/components/rules/BasicInfo';
import ProjectTemplate from '@/pages/projects/template';
import { getLabelByValue } from '@/common/utils';
import useTemplateSelector from '@/components/rules/hook/useTemplateSelector';
import { cloneDeep } from 'lodash-es';
import { useListener } from '@/components/rules/utils';
import FilterSetting from './FilterSetting.vue';

const { t: $t } = useI18n();
const store = useStore();
const ruleData = computed(() => store.state.rule);

// eslint-disable-next-line no-undef
const props = defineProps({
    layout: {
        type: String,
        default: 'horizontal',
    },
});
const verifyRuleData = ref({
    datasource: [{}],
    filterSettingList: [], // 不设置初始值会导致初始情况下数据绑定不到子组件里，所以这里设置初始值为空数组
});

const verifyRuleFormRef = ref(null);
const basicInfoRef = ref(null);
const filterSettingRef = ref(null);

// 表单规则
const verifyRuleRules = ref({
    execution_parameters_name: [{
        required: true,
        trigger: 'change',
        message: $t('common.notEmpty'),
    }],
    contrast_type: [{
        required: true,
        trigger: 'change',
        type: 'number',
        message: $t('common.notEmpty'),
    }],
});

// 校验模板下拉列表
const checkTemplateList = ref([]);
const getCheckTemplateList = async () => {
    try {
        const res = await FRequest('api/v1/projector/rule_template/multi/all', { page: 0, size: 512 }, 'post');
        checkTemplateList.value = res?.data || [];
    } catch (e) {
        console.log('getCheckTemplateList Error:', e);
    }
};
// 比对方向下拉列表
const compareDirectionList = ref([]);
const getCompareDirectionList = async () => {
    try {
        const res = await FRequest('api/v1/projector/mul_source_rule/constrast/all', {}, 'post');
        compareDirectionList.value = res.map(v => ({ value: v.code, label: v.message }));
    } catch (e) {
        console.log('getCompareDirectionList Error:', e);
    }
};
// 选择执行参数模板
const templateRef = ref(null);
const datasourceComFormRef = ref(null);
const {
    showExecuteParamsDrawer,
    openExecuteParamsDrawer,
    confirmSelect,
} = useTemplateSelector(templateRef, (value) => {
    console.log(value);
    verifyRuleData.value.execution_parameters_name = value.execution_parameters_name;
});

const valid = async () => {
    try {
        await verifyRuleFormRef.value.validate();
        const result = await Promise.all([
            basicInfoRef.value.valid(),
            filterSettingRef.value.valid(),
        ]);
        console.log(result);
        if (result.includes(false)) {
            console.log('datasrouce表单验证失败: ', result);
            return false;
        }
        // 更新store，给save调用
        store.commit('rule/updateCurrentRuleDetail', cloneDeep(verifyRuleData.value));
        return true;
    } catch (error) {
        console.log('表单验证失败: ', error);
        return false;
    }
};

const init = async () => {
    const target = cloneDeep(ruleData.value.currentRuleDetail);
    verifyRuleData.value = target;
    verifyRuleData.value.filterSettingList = target.filter_list;
};

onMounted(async () => {
    await getCompareDirectionList();
    await getCheckTemplateList();
});

// 初始化数据
useListener('IS_RULE_DETAIL_DATA_LOADED', init);
// eslint-disable-next-line no-undef
defineExpose({ valid });
</script>
