<template>
    <FForm
        ref="basicInfoFormRef"
        :layout="layout"
        :class="layout === 'inline' ? 'inline-form-item' : ''"
        :model="currentBasicInfo"
        :rules="basicInfoRules"
        :labelWidth="labelWidth"
        :labelPosition="editMode !== 'display' ? 'right' : 'left'"
    >
        <FFormItem v-if="listType === 'groupList' && editMode === 'edit'" :label="prefixTitle" class="title">
        </FFormItem>
        <FFormItem :label="(listType === 'groupList' && editMode === 'display') ? '' : `规则名称`" prop="rule_name">
            <FForm
                v-if="editMode !== 'display'"
                ref="nameFormRef"
                layout="inline"
                class="pure-form"
                :model="currentBasicInfo"
                :rules="basicInfoRules"
                :inlineItemWidth="315"
            >
                <FFormItem v-if="listType !== 'listByTemplate'" label="" prop="rule_name">
                    <FInput
                        v-model="currentBasicInfo.rule_name"
                        class="form-edit-input"
                        :maxlength="128"
                        showWordLimit
                        :placeholder="`${$t('common.ruleEnName')}(必填)`"
                    />
                </FFormItem>
                <FFormItem label="" prop="cn_name">
                    <FInput
                        v-model="currentBasicInfo.cn_name"
                        class="form-edit-input"
                        :maxlength="128"
                        showWordLimit
                        :disabled="listType === 'listByTemplate'"
                        :placeholder="listType === 'listByTemplate' ? '模版中文名' : `${$t('common.ruleCnName')}(非必填)`"
                    />
                </FFormItem>
                <FFormItem v-if="listType === 'listByTemplate'" label="" prop="rule_name">
                    <FInput
                        v-model="currentBasicInfo.rule_name"
                        class="form-edit-input"
                        :maxlength="128"
                        showWordLimit
                        disabled
                        :placeholder="listType === 'listByTemplate' ? '模版英文名' : `${$t('common.ruleEnName')}(必填)`"
                    />
                </FFormItem>
            </FForm>
            <div class="form-preview-label">
                <span v-if="listType === 'groupList' && editMode === 'display'">
                    {{prefixTitle}} |
                </span>
                {{currentBasicInfo.rule_name}}
                <span v-if="currentBasicInfo.cn_name">（{{currentBasicInfo.cn_name}}）</span>
            </div>
        </FFormItem>
        <FFormItem :label="$t('common.ruleDescription')" prop="rule_detail">
            <FInput
                v-model="currentBasicInfo.rule_detail"
                class="form-edit-input"
                type="textarea"
                placeholder="请输入规则描述"
                :maxlength="1000"
                showWordLimit
            />
            <div class="form-preview-label">{{currentBasicInfo.rule_detail || '--'}}</div>
        </FFormItem>
    </FForm>
</template>
<script setup>
import {
    ref, computed,
} from 'vue';
import { useStore } from 'vuex';
import { useI18n } from '@fesjs/fes';
import { COMMON_REG } from '@/assets/js/const';

const store = useStore();
const { t: $t } = useI18n();
const ruleData = computed(() => store.state.rule);
// eslint-disable-next-line no-undef
const props = defineProps({
    basicInfo: {
        type: Object,
        default: () => { },
    },
    layout: {
        type: String,
        default: 'horizontal',
    },
    // 页面的类型 普通规则组：commonList，表规则组：groupList
    lisType: {
        type: String,
        default: 'commonList',
    },
    prefixTitle: {
        type: String,
        default: '',
    },
});
const listType = computed(() => props.lisType);
// label宽度
const labelWidth = computed(() => {
    if (listType.value === 'groupList') {
        return 70;
    }
    return 96;
});
const prefixTitle = computed(() => props.prefixTitle);
// 判断页面为普通规则组/表规则组，切换不同 editMode 变量
const editMode = computed(() => {
    if (listType.value === 'groupList') {
        return ruleData.value.currentProject.groupRuleEditMode;
    }
    if (listType.value === 'listByTemplate') {
        return 'edit';
    }
    return ruleData.value.currentProject.editMode;
});

// eslint-disable-next-line no-undef
const emit = defineEmits(['update:basicInfo']);
const currentBasicInfo = computed({
    get: () => props.basicInfo || {},
    set: (value) => {
        emit('update:basicInfo', value);
    },
});

// 表单规则
const basicInfoRules = ref({
    rule_name: [{
        required: true,
        trigger: ['blur', 'input'],
        message: $t('common.notEmpty'),
    }, {
        pattern: listType.value === 'listByTemplate' ? false : COMMON_REG.EN_NAME,
        message: $t('myProject.projectEnNameRegTips'),
    }],
    cn_name: [{
        trigger: ['blur', 'input'],
        message: $t('common.notEmpty'),
    }, {
        pattern: COMMON_REG.CN_NAME,
        message: $t('myProject.projectCnNameRegTips'),
    }],
    rule_detail: [{
        trigger: ['blur', 'input'],
        message: $t('common.notEmpty'),
    }],
});

const basicInfoFormRef = ref(null);
const nameFormRef = ref(null);
const valid = async () => {
    try {
        await nameFormRef.value.validate();
        await basicInfoFormRef.value.validate();
        return true;
    } catch (err) {
        console.warn(err);
        return false;
    }
};

// eslint-disable-next-line no-undef
defineExpose({ valid });
</script>
<style lang="less" scoped>
.pure-form {
    margin-bottom: 0 !important;
    .fes-form-item {
        margin-bottom: 0 !important;
    }
}
.group-list-form-preview-label {
    font-family: PingFangSC-Regular;
    font-size: 16px;
    color: #0F1222;
    letter-spacing: 0;
    line-height: 45px;
    font-weight: 400;
}
.title /deep/ .fes-form-item-label{
    color: #0F1222;
}
</style>
