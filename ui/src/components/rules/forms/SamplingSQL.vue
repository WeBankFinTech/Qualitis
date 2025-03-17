<template>
    <div class="rule-detail-form" :class="{ edit: editMode !== 'display' }">
        <div class="title">{{title}}</div>
        <FForm
            ref="samplingSqlFormRef"
            :model="samplingSql"
            :rules="samplingSqlRules"
            :labelWidth="96"
            :labelPosition="editMode !== 'display' ? 'right' : 'left'"
        >
            <FFormItem :label="$t('_.UDF设置')">
                <FSelect
                    v-model="linkis_udf_names"
                    multiple
                    class="form-edit-input"
                    style="width:315px"
                    filterable
                    collapseTags
                    :collapseTagsLimit="2"
                    :placeholder="$t('_.请选择')">
                    <FOption
                        v-for="(item, index) in udfList"
                        :key="index"
                        :value="item.value"
                        :label="item.label"
                        :disabled="!item.status"
                    ></FOption>
                </FSelect>
                <div class="form-preview-label project-tags">
                    <div v-for="(item, index) in linkis_udf_names" :key="index" class="tags-item">{{item}}</div>
                </div>
            </FFormItem>
            <FFormItem class="h44" :label="$t('_.指标计算采集Sql')" prop="sql">
                <FInput
                    v-model="samplingSql.sql"
                    class="form-edit-input"
                    type="textarea"
                />
                <div class="form-preview-label">{{samplingSql.sql}}</div>
            </FFormItem>
            <FFormItem :label="$t('_.关联字段')" prop="connect_col">
                <FInput
                    v-model="samplingSql.connect_col"
                    class="form-edit-input"
                    placeholder="eg: column_a,column_b,column_c"
                />
                <div class="form-preview-label">{{samplingSql.connect_col}}</div>
                <FTooltip v-if="editMode !== 'display'" placement="right-start">
                    <ExclamationCircleOutlined class="tip" />
                    <template #content>
                        <div style="width: 348px;">{{$t('_.输入字段由逗号分隔，eg: column_a,column_b,column_c')}}</div>
                    </template>
                </FTooltip>
            </FFormItem>
            <FFormItem :label="$t('_.比对字段')" prop="compare_col">
                <FInput
                    v-model="samplingSql.compare_col"
                    class="form-edit-input"
                    placeholder="eg: column_a,column_b,column_c"
                />
                <div class="form-preview-label">{{samplingSql.compare_col}}</div>
                <FTooltip v-if="editMode !== 'display'" placement="right-start">
                    <ExclamationCircleOutlined class="tip" />
                    <template #content>
                        <div style="width: 348px;">{{$t('_.输入字段由逗号分隔，eg: column_a,column_b,column_c')}}</div>
                    </template>
                </FTooltip>
            </FFormItem>
        </FForm>
    </div>
</template>
<script setup>

import {
    computed, defineProps, defineEmits, ref, onMounted, defineExpose, inject,
} from 'vue';
import { useStore } from 'vuex';
import { useI18n, request } from '@fesjs/fes';
import { cloneDeep } from 'lodash-es';
import { ExclamationCircleOutlined } from '@fesjs/fes-design/es/icon';
import { useListener } from '../utils';

const { t: $t } = useI18n();
const props = defineProps({
    samplingSql: {
        required: true,
        default: () => {},
    },
    type: {
        required: true,
        default: 'source',
        type: String,
    },
    linkis_udf_names: {
        required: true,
        default: () => [],
        type: Array,
    },
});
const samplingSqlFormRef = ref(null);
const title = computed(() => {
    if (props.type === 'source') {
        return $t('_.源采样SQL');
    }
    return $t('_.目标采样SQL');
});
const emits = defineEmits(['update:samplingSql', 'update:linkis_udf_names']);
const udfList = ref([]);
const samplingSql = computed({
    get() {
        return props.samplingSql;
    },
    set(v) {
        emits('update:samplingSql', v);
    },
});
// eslint-disable-next-line camelcase
const linkis_udf_names = computed({
    get() {
        return props.linkis_udf_names || [];
    },
    set(v) {
        emits('update:linkis_udf_names', v);
    },
});
const store = useStore();
const ruleData = computed(() => store.state.rule);
const editMode = computed(() => ruleData.value.currentProject.editMode);
const samplingSqlRules = {
    sql: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    connect_col: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    compare_col: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
};
const currentRuleType = inject('currentRuleType');
const getUdfOptions = async () => {
    try {
        const target = cloneDeep(ruleData.value.currentRuleDetail);
        let cluster;
        if (currentRuleType.value === '3-3') {
            cluster = target[props.type]?.cluster_name;
        } else {
            cluster = target.source.cluster_name;
        }
        if (!cluster) return;
        const res = await request('/api/v1/projector/meta_data/udf/all', Object.assign({
            page: 0,
            size: 999,
        }, { enable_cluster: [cluster] }), { cache: true });
        udfList.value = res.content?.map(item => ({ value: item.name, label: item.name, status: item.status }));
    } catch (err) {
        console.warn(err);
    }
};
// 当数据库、数据表、过滤条件、字段等发生变化的时候，需要调用updateSqlDataSource进行预览SQL更新
useListener('SHOULD_UPDATE_NECESSARY_DATA', () => getUdfOptions());

// 初始化数据
useListener('IS_RULE_DETAIL_DATA_LOADED', () => getUdfOptions());

onMounted(async () => {
    await getUdfOptions();
});
const valid = () => samplingSqlFormRef.value.validate();
defineExpose({ valid });

</script>
<style lang="less" scoped>
.h44 {
    :deep(.fes-form-item-label) {
        height: 44px !important;
    }
}
.title {
    width: 100%;
    margin-bottom: 16px;
    font-family: PingFangSC-Medium;
    font-size: 14px;
    color: #0F1222;
    line-height: 22px;
    font-weight: 500;
}
.fes-form-item-content {
    align-items: center;
    .tip {
        display: inline-block;
        margin-left: 10px;
    }
}
</style>
