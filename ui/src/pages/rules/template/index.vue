<template>
    <article class="wd-content my-project">
        <header class="my-project-header">
            <RuleTemplateNavBar v-model="currentRuleTemplateBar" :data="ruleTemplateNavBars" />
        </header>
        <main>
            <RuleTemplate
                :origin-rule-template-headers="originRuleTemplateHeaders"
                :templateType="currentRuleTemplateBar"
            />
        </main>
    </article>
</template>
<script setup>
import {
    ref, onMounted, provide, computed,
} from 'vue';
import { useI18n, useRoute } from '@fesjs/fes';
import RuleTemplateNavBar from '@/pages/projects/components/NavBar';
import { DATASOURCE_TYPE_LIST } from '@/assets/js/const';
import RuleTemplate from './components/ruleTemplate';
import MultipleTemplate from './components/multipleTemplate';
import {
    fetchVerificationType, fetchVerificationLevel, fetchInputType, fetchActionContent, fetchCountFunction,
} from './api';

const { t: $t } = useI18n();
const route = useRoute();

// tab相关
const currentRuleTemplateBar = ref(-1);
const ruleTemplateNavBars = [
    { label: $t('ruleTemplatelist.singleTemplate'), value: 1 },
    { label: $t('ruleTemplatelist.fileTemplate'), value: 4 },
    { label: $t('ruleTemplatelist.crossTableTemplate'), value: 3 },
];

// 单表模板、表文件模板、跨表模板 表头设置
const originRuleTemplateHeaders = [
    { prop: 'template_id', label: $t('ruleTemplatelist.templateId') },
    { prop: 'template_name', label: $t('ruleTemplatelist.templateCNName') },
    { prop: 'en_name', label: $t('ruleTemplatelist.templateENName') },
    { prop: 'description', label: $t('ruleTemplatelist.templateDesc') },
    { prop: 'datasource_type', label: $t('ruleTemplatelist.dataSourceType') },
    { prop: 'verification_level', label: $t('ruleTemplatelist.verificationLevel') },
    { prop: 'verification_type', label: $t('ruleTemplatelist.verificationType') },
    { prop: 'action_type', label: $t('ruleTemplatelist.actionType') },
    { prop: 'save_mid_table', label: $t('ruleTemplatelist.exceptionDatabase') },
    { prop: 'creator', label: $t('ruleTemplatelist.creator') },
    { prop: 'create_time', label: $t('ruleTemplatelist.createTime'), hidden: true },
    { prop: 'modify_name', label: $t('ruleTemplatelist.modifyName') },
    { prop: 'modify_time', label: $t('ruleTemplatelist.modifyTime'), hidden: true },
    { prop: 'dev_department_name', label: $t('ruleTemplatelist.developDepartment') },
    { prop: 'ops_department_name', label: $t('ruleTemplatelist.maintainDepartment') },
    { prop: 'visibility_department_list', label: $t('ruleTemplatelist.visibleRange'), hidden: true },
];

const verificationTypeList = ref([]);
const verificationLevelList = ref([]);
const actionContentList = ref([]); // 采样内容枚举
const countFunctionList = ref([]); // 统计函数枚举
const datasourceTypeList = ref(DATASOURCE_TYPE_LIST);
const actionTypeList = ref([{ // 采样类型枚举
    label: 'SQL',
    value: 1,
}, {
    label: 'JAVA',
    value: 2,
    disabled: true,
}, {
    label: 'SCALA',
    value: 3,
    disabled: true,
}]);
provide('verificationTypeList', computed(() => verificationTypeList.value));
provide('verificationLevelList', computed(() => verificationLevelList.value));
provide('actionContentList', computed(() => actionContentList.value));
provide('datasourceTypeList', computed(() => datasourceTypeList.value));
provide('actionTypeList', computed(() => actionTypeList.value));
provide('countFunctionList', computed(() => countFunctionList.value));
const getVerificationTypeList = async () => {
    try {
        const result = await fetchVerificationType();
        verificationTypeList.value = result.map(item => ({ value: item.code, label: item.message }));
    } catch (error) {
        console.log('getVerificationTypeList error:', error);
    }
};
const getVerificationLevelList = async () => {
    try {
        const result = await fetchVerificationLevel();
        verificationLevelList.value = result.map(item => ({ value: item.code, label: item.message }));
    } catch (error) {
        console.log('getVerificationLevelList error:', error);
    }
};
const getActionContentList = async () => {
    try {
        const result = await fetchActionContent();
        actionContentList.value = result.map(item => ({ value: `${item.code}`, label: item.message }));
    } catch (error) {
        console.log('getActionContentList error:', error);
    }
};
const getCountFunctionList = async () => {
    try {
        const result = await fetchCountFunction();
        countFunctionList.value = result.map(item => ({ value: item.code, label: item.code, ...item }));
    } catch (error) {
        console.log('getCountFunctionList error:', error);
    }
};

onMounted(() => {
    currentRuleTemplateBar.value = parseInt(route.query.tab) || ruleTemplateNavBars[0].value;
    getVerificationLevelList();
    getVerificationTypeList();
    getActionContentList();
    getCountFunctionList();
});
</script>
<config>
{
    "name": "ruleTemplate",
    "title": "$ruleTemplate.ruleTemplate"
}
</config>
