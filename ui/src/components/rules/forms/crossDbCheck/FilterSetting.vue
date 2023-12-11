<template>
    <div class="filter-list">
        <FForm
            v-for="(filterSettingItem,index) in currentFilterSettingList"
            :key="filterSettingItem.id"
            class="filter-item"
            :class="{ edit: ruleData.currentProject.editMode !== 'display' }"
        >
            <!-- 来源表 -->
            <FForm
                :ref="el => { if (el) filterForms[`${index}_0`] = el }"
                :model="filterSettingItem"
                :rules="crossDBFilterFormRules"
                layout="inline"
                :labelWidth="80"
                labelPosition="right"
                class="filter-item-row"
            >
                <FFormItem :label="$t('ruleTemplatelist.sourceTable')" prop="source_table">
                    <FSelect
                        v-model="filterSettingItem.source_table"
                        class="form-edit-input"
                        filterable
                        valueField="table_name"
                        labelField="table_name"
                        :options="originTableList"
                        @change="changeTable('source', filterSettingItem)"
                    ></FSelect>
                    <div class="form-preview-label">{{filterSettingItem.source_table}}</div>
                </FFormItem>
                <FFormItem
                    :label="$t('crossDatabaseFullVerification.rowFilter')"
                    prop="source_filter"
                >
                    <FInput
                        v-model="filterSettingItem.source_filter"
                        class="form-edit-input"
                        placeholder="请输入"
                    />
                    <div class="form-preview-label">{{filterSettingItem.source_filter}}</div>
                </FFormItem>
                <FFormItem
                    v-if="filterSettingItem.source_table"
                    :label="$t('crossDatabaseFullVerification.columnFilter')"
                    prop="source_filter_column_list"
                >
                    <FSelect
                        v-model="filterSettingItem.source_filter_column_list"
                        class="form-edit-input"
                        multiple
                        filterable
                        :collapseTags="true"
                        :collapseTagsLimit="2"
                        valueField="column_name"
                        labelField="label"
                        :options="filterSettingItem.sourceColumnList"
                    ></FSelect>
                    <div
                        class="form-preview-label"
                    >
                        {{filterSettingItem.source_filter_column_list}}
                    </div>
                </FFormItem>
            </FForm>
            <!-- 链接符号 -->
            <LinkOutlined class="filter-link-icon" />
            <!-- 目标表 -->
            <FForm
                :ref="el => { if (el) filterForms[`${index}_1`] = el }"
                :model="filterSettingItem"
                :rules="crossDBFilterFormRules"
                layout="inline"
                :labelWidth="80"
                labelPosition="right"
                class="filter-item-row filter-item-row-end"
            >
                <FFormItem :label="$t('ruleTemplatelist.targetTable')" prop="target_table">
                    <FSelect
                        v-model="filterSettingItem.target_table"
                        class="form-edit-input"
                        filterable
                        valueField="table_name"
                        labelField="table_name"
                        :options="targetTableList"
                        @change="changeTable('target', filterSettingItem)"
                    ></FSelect>
                    <div class="form-preview-label">{{filterSettingItem.target_table}}</div>
                </FFormItem>
                <FFormItem
                    :label="$t('crossDatabaseFullVerification.rowFilter')"
                    prop="target_filter"
                >
                    <FInput
                        v-model="filterSettingItem.target_filter"
                        class="form-edit-input"
                        placeholder="请输入"
                    />
                    <div class="form-preview-label">{{filterSettingItem.target_filter}}</div>
                </FFormItem>
                <FFormItem
                    v-if="filterSettingItem.target_table"
                    :label="$t('crossDatabaseFullVerification.columnFilter')"
                    prop="target_filter_column_list"
                >
                    <FSelect
                        v-model="filterSettingItem.target_filter_column_list"
                        class="form-edit-input"
                        multiple
                        filterable
                        :collapseTags="true"
                        :collapseTagsLimit="2"
                        valueField="column_name"
                        labelField="label"
                        :options="filterSettingItem.targetColumnList"
                    ></FSelect>
                    <div
                        class="form-preview-label"
                    >
                        {{filterSettingItem.target_filter_column_list}}
                    </div>
                </FFormItem>
            </FForm>
            <!-- 删除按钮 -->
            <FButton class="filter-delete-btn" type="text" @click="deleteRow(index)">
                <template #icon>
                    <MinusCircleOutlined />
                </template>删除
            </FButton>
        </FForm>
        <!-- 新增 -->
        <FButton
            v-if="ruleData.currentProject.editMode !== 'display'"
            class="filter-add-btn"
            type="link"
            @click="add"
        >
            <PlusCircleOutlined />
            {{$t('common.add')}}
        </FButton>
    </div>
</template>

<script setup>
/* eslint-disable no-undefined */
import { useI18n } from '@fesjs/fes';
import {
    ref, computed, inject, defineProps, defineEmits, defineExpose, watch,
} from 'vue';
import { useStore } from 'vuex';
import {
    MinusCircleOutlined, LinkOutlined, PlusCircleOutlined,
} from '@fesjs/fes-design/es/icon';
import { getColumnList, getColumnListBySourceID } from '@/components/rules/api';
import { cloneDeep } from 'lodash-es';
import { useListener } from '@/components/rules/utils';
import { getColumns } from '@/components/rules/utils/datasource';
import { FMessage } from '@fesjs/fes-design';

const store = useStore();
const ruleData = computed(() => store.state.rule);
const sourceDatasource = computed(() => ruleData.value.currentRuleDetail.source || {});
const targetDatasource = computed(() => ruleData.value.currentRuleDetail.target || {});
const originTableList = computed(() => ruleData.value.currentRuleDetail.originTableList || []);
const targetTableList = computed(() => ruleData.value.currentRuleDetail.targetTableList || []);

const props = defineProps({
    filterSettingList: {
        type: Array,
        default: () => [],
    },
});

const emit = defineEmits(['update:filterSettingList']);

const filterForms = ref({});

const { t: $t } = useI18n();

const currentFilterSettingList = computed({
    get: () => props.filterSettingList || [],
    set: (value) => {
        emit('update:filterSettingList', value);
    },
});

watch(() => sourceDatasource.value?.db_name, () => {
    // 来源源相关数据变了之后重置
    currentFilterSettingList.value = [];
});

watch(() => targetDatasource.value?.db_name, () => {
    // 目标源相关数据变了之后重置
    currentFilterSettingList.value = [];
});

const crossDBFilterFormRules = ref({
    source_table: [{
        required: true,
        trigger: 'change',
        message: $t('common.notEmpty'),
    }],
    source_filter: [{
        required: true,
        trigger: ['blur', 'input'],
        message: $t('common.notEmpty'),
    }],
    source_filter_column_list: [{
        required: true,
        trigger: 'change',
        type: 'array',
        message: $t('common.notEmpty'),
    }],
    target_table: [{
        required: true,
        trigger: ['blur', 'input'],
        message: $t('common.notEmpty'),
    }],
    target_filter: [{
        required: true,
        trigger: ['blur', 'input'],
        message: $t('common.notEmpty'),
    }],
    target_filter_column_list: [{
        required: true,
        trigger: 'change',
        type: 'array',
        message: $t('common.notEmpty'),
    }],
});

function deleteRow(index) {
    // 跨库可以全部删除
    console.log('deleteRow', index);
    currentFilterSettingList.value.splice(index, 1);
}

function add() {
    if (!(sourceDatasource.value.db_name && targetDatasource.value.db_name)) {
        FMessage.warn('请先选择来源或目标数据库');
        return;
    }
    currentFilterSettingList.value.push({
        source_table: '',
        source_filter: '',
        source_filter_column_list: '',
        target_table: '',
        target_filter: '',
        target_filter_column_list: '',
        sourceColumnList: [],
        targetColumnList: [],
    });
}

// 选择数据表后，根据对应source的数据获取表字段list
const changeTable = async (type, data) => {
    try {
        if (!data) return;
        let res = {};
        console.log(sourceDatasource);
        const proxyUser = sourceDatasource.value.proxy_user;
        const clusterName = sourceDatasource.value.cluster_name;
        const dataSourceType = type === 'source' ? sourceDatasource.value.type : targetDatasource.value.type;
        const dataSourceId = type === 'source' ? sourceDatasource.value.linkis_datasource_id : targetDatasource.value.linkis_datasource_id;
        const dbName = type === 'source' ? sourceDatasource.value.db_name : targetDatasource.value.db_name;
        const tableName = type === 'source' ? data.source_table : data.target_table;
        res = await getColumns({
            clusterName,
            proxyUser,
            dataSourceType,
            dataSourceId,
            dbName,
            tableName,
        });
        const columnList = (res || []).map(item => ({ ...item, label: `${item.column_name} (${item.data_type})` }));
        // console.log(type, res, columnList)
        type === 'source' ? data.sourceColumnList = columnList : data.targetColumnList = columnList;
    } catch (err) {
        console.warn(err);
    }
};

const crossDBFilterFormRef = ref(null);

// filtersetting组件传给后台的数据格式 字段名filter_list
// [
//   {
//     "filter_column_list": [
//       "openid",
//       "type1"
//     ],
//     "source_table": "channel_marketing_credit_add_open_acct_pay_off_20201231",
//     "source_filter": "1111",
//     "target_table": "date01",
//     "target_filter": "2222"
//   }
// ]
const valid = async () => {
    try {
        const result = await Promise.all(Object.keys(filterForms.value).map((form) => {
            console.log(form);
            return filterForms.value[form].validate();
        }));
        console.log('crossDBValidationForm表单验证成功: ', result);
        // eslint-disable-next-line camelcase
        // const filter_list = currentFilterSettingList.value.map((item) => {
        //     const {
        //         source_filter_column_list,
        //         target_filter_column_list,
        //         source_table,
        //         source_filter,
        //         target_table,
        //         target_filter,
        //     } = item;
        //     return {
        //         source_filter_column_list,
        //         target_filter_column_list,
        //         source_table,
        //         source_filter,
        //         target_table,
        //         target_filter,
        //     };
        // });
        return true;
    } catch (error) {
        console.log('crossDBValidationForm表单验证失败: ', error);
        return false;
    }
};

const init = async () => {
    // 初始化filterSettingList每一项对应的列过滤下拉list
    currentFilterSettingList.value.forEach(async (element) => {
        await changeTable('source', element);
        await changeTable('target', element);
    });
};

defineExpose({ valid });
</script>
<style lang="less" scoped>

.filter-list {
    padding-left: 8px;
    .filter-item {
        position: relative;
        background: rgba(15, 18, 34, 0.03);
        .filter-item-row {
            padding: 8px 0;
            position: relative;
            :deep(.fes-form-item) {
                margin-bottom: 0px;
            }
            &::before {
                content: "";
                display: block;
                position: absolute;
                top: 0;
                left: -8px;
                width: 1px;
                height: 100%;
                background: #cfd0d3;
            }
        }
        .filter-item-row-end{
            margin-bottom: 8px;
        }
        .filter-link-icon {
            position: absolute;
            top: 50%;
            height: 16px;
            left: -16px;
            padding: 1px;
            margin-top: -8px;
            color: #5384ff;
            background: rgba(83, 132, 255, 0.06);
            border-radius: 1px;
        }
        .filter-delete-btn {
            position: absolute;
            top: 50%;
            right: 8px;
            margin-top: -16px;
            color: #93949b;
        }
    }
    .filter-add-btn {
        margin-left: -32px;
        height: 22px;
    }
}
</style>
