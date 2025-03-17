<template>
    <div class="rule-detail-form group-rule-form">
        <div class="wd-content-body tab-content rule-group-content">
            <ul v-if="(!currentProject.isWorkflowProject || (currentProject.isEmbedInFrame && !isEmbedInDMS))" class="wd-body-menus">
                <li class="wd-body-menu-item">
                    <FDropdown trigger="click" :options="options" placement="bottom-end" @click="select">
                        <MoreOutlined style="transform: rotate(90deg);" />
                    </FDropdown>
                </li>
            </ul>
            <slot></slot>
        </div>
    </div>
</template>

<script setup>
import { useI18n } from '@fesjs/fes';

import { ref, defineProps, computed } from 'vue';
import { MoreOutlined } from '@fesjs/fes-design/es/icon';
import { useStore } from 'vuex';


const { t: $t } = useI18n();

// eslint-disable-next-line no-restricted-globals
const isEmbedInDMS = computed(() => self.location.href.includes('microApp/dqm'));
const store = useStore();
const ruleData = computed(() => store.state.rule);
const currentProject = computed(() => ruleData.value.currentProject);
const props = defineProps({
    rule: {
        type: Object,
        default: null,
    },
    index: {
        type: Number,
        default: 0,
    },
});
// eslint-disable-next-line no-undef
const emit = defineEmits(['update:rule', 'copyRule', 'deleteRule', 'executeRule']); // 暂时移除 'addRuleAbove', 'addRuleBelow',
const options = ref([
    {
        value: 'copyRule',
        label: $t('_.复制该规则'),
    },
    {
        value: 'deleteRule',
        label: $t('_.删除该规则'),
    },
    {
        value: 'executeRule',
        label: $t('_.执行该规则'),
    },
]);
const select = (type) => {
    const isFileRule = props.rule?.rule_type === 4;
    emit(type, props.index, props.rule, isFileRule);
};


</script>

<style lang="less" scoped>
//在表规则组和普通规则组样式不同
.excution-parameters-name-input :deep(input){
    cursor: pointer;
}
.table-group-datasource :deep(.tags-item) {
    margin-left: 0px;
}
.project-template-style :deep(.card) {
    &.isEmbed{
        margin: 16px 8px;
    }
}
.project-template-style{
    padding-top: 0;
    margin-right: -24px;
    margin-left: -24px;
}
.rule-group-content{
    border: 1px solid #F1F1F2;
    border-radius: 4px;
    padding-bottom: 0px;
    .table-rule-name-form{
        grid-template-columns: 246px 160px;
    }
    .fes-form-inline{
        margin-bottom: 0;
    }
    .wd-body-title {
        display: flex;
        align-items: center;
        .edit{
            color: #93949B;
            margin-left: 14px;
        }
    }
}
.file-verification-form{
    grid-template-columns:repeat(auto-fit,268px);
}
</style>
