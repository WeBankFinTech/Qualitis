<template>
    <FDrawer
        v-model:show="show"
        :title="`新值管理`"
        :width="900"
        displayDirective="if"
        @cancel="togglePanel"
    >
        <div v-if="actionType === 'mjz' && propDatas.data.length && (!workflowProject || (workflowProject && isEmbedInFrame))" class="wd-drawer-header">
            <FForm ref="mjzFormRef" :model="propDatas" layout="inline" class="data-range" style="margin-bottom: 22px;">
                <FFormItem :prop="`data[0].argument_value`" :rules="[{ required: true, message: $t('common.notEmpty') }]">
                    <FInput v-model="propDatas.data[curIndexs[0]].argument_value" :placeholder="$t('common.pleaseEnter')">
                        <template #prepend>枚举值</template>
                    </FInput>
                </FFormItem>
            </FForm>
            <FButton type="primary" style="margin-left: 8px;" @click="confirmMjzModify">{{$t('common.ok')}}</FButton>
        </div>
        <div v-else-if="actionType === 'szfw' && propDatas.data.length && (!workflowProject || (workflowProject && isEmbedInFrame))" class="wd-drawer-header">
            <FForm ref="szfwFormRef" :model="propDatas" class="data-range">
                <FFormItem style="flex: 0 0 20%; max-width: 20%;" :prop="`data[0].argument_value`" :rules="[{ required: true, message: $t('common.notEmpty') }]">
                    <FInput v-model="propDatas.data[curIndexs[0]].argument_value" placeholder="最小值" />
                    <span class="sperator">&le;</span>
                </FFormItem>
                <FFormItem style="flex: 0 0 58%; max-width: 58%;" :prop="`data[1].argument_value`" :rules="[{ required: true, message: $t('common.notEmpty') }]">
                    <FInput v-model="propDatas.data[curIndexs[1]].argument_value" placeholder="表达式" />
                    <span class="sperator">&le;</span>
                </FFormItem>
                <FFormItem style="flex: 0 0 20%; max-width: 20%;" :prop="`data[2].argument_value`" :rules="[{ required: true, message: $t('common.notEmpty') }]">
                    <FInput v-model="propDatas.data[curIndexs[2]].argument_value" placeholder="最大值" />
                </FFormItem>
            </FForm>
            <FButton type="primary" @click="confirmSzfwModify">{{$t('common.ok')}}</FButton>
        </div>
        <FTable :data="standardLists">
            <FTableColumn prop="task_new_value_id" :label="`标准值ID`" :width="78" ellipsis />
            <FTableColumn prop="status" :visible="actionType !== ''" :label="`状态`" :width="78" ellipsis>
                <template #default="{ row }">
                    {{dataStatusMap[row.status]}}
                </template>
            </FTableColumn>
            <FTableColumn prop="result_value" :label="`结果值`" :width="120" ellipsis />
            <FTableColumn prop="create_user" :label="`规则执行人`" :width="160" ellipsis />
            <FTableColumn prop="create_time" :label="`新值生成时间`" :width="160" ellipsis />
            <FTableColumn :visible="(!workflowProject || (workflowProject && isEmbedInFrame)) && actionType !== ''" :label="$t('common.operate')" :width="104" ellipsis>
                <template #default="{ row }">
                    <ul class="wd-table-operate-btns">
                        <li class="btn-item" :class="{ disabled: row.status !== 1 || DQMWorkflow }" @click="handleImport(row)">导入</li>
                        <li class="btn-item red" :class="{ disabled: row.status !== 1 || DQMWorkflow }" @click="handleDelete(row)">{{$t('common.delete')}}</li>
                    </ul>
                </template>
            </FTableColumn>
        </FTable>
        <div class="table-pagination-container">
            <FPagination
                v-model:currentPage="pagination.page"
                v-model:pageSize="pagination.size"
                show-size-changer
                show-total
                :total-count="pagination.total"
                @change="loadListData"
                @pageSizeChange="loadListData"
            ></FPagination>
        </div>
    </FDrawer>
</template>
<script setup>
import {
    onUpdated, ref, inject, defineEmits, watch, computed,
} from 'vue';
import { useStore } from 'vuex';
import { request as FRequest, useI18n } from '@fesjs/fes';
import { FMessage } from '@fesjs/fes-design';
import { cloneDeep, isEqual } from 'lodash-es';
import { TEMPLATE_ARGUMENT_INPUT_TYPE } from '@/common/constant';
// import eventbus from '@/common/useEvents';

const store = useStore();
const ruleData = computed(() => store.state.rule);
const { t: $t } = useI18n();
const editOrDisplay = inject('action');
const workflowProject = inject('workflowProject');
const isEmbedInFrame = inject('isEmbedInFrame');

// eslint-disable-next-line no-undef
const props = defineProps({
    show: {
        type: Boolean,
        default: false,
    },
    /**
     * 只有数值范围、枚举值可以导入和编辑
     * 标准值不能直接导入
     */
    actionType: {
        type: String,
        default: '',
    },
    data: {
        type: Array,
        default: [],
    },
    rule: {
        type: Object,
        default: null,
    },
    DQMWorkflow: {
        type: Boolean,
        default: false,
    },
});

const dataStatusMap = ref({
    1: '未处理',
    2: '已录入',
    3: '已丢弃',
});

const emit = defineEmits(['update:show', 'update:data', 'updateSqlPreview']);

const propDatas = ref({
    data: cloneDeep(props.data),
});

watch(() => props.data, () => {
    if (!isEqual(props.data, propDatas.data)) {
        propDatas.value.data = cloneDeep(props.data);
    }
}, { deep: true });

// 根据actionType类型，通过比对arument_type获取枚举值和数值范围对应的index下标，保存在数组中
const curIndexs = computed(() => {
    let indexArr = [];
    if (props.actionType === 'mjz') {
        indexArr.push(propDatas.value.data.findIndex(i => i.argument_type === TEMPLATE_ARGUMENT_INPUT_TYPE.LIST));
    }

    if (props.actionType === 'szfw') {
        // 需要按顺序
        indexArr = [
            propDatas.value.data.findIndex(i => i.argument_type === TEMPLATE_ARGUMENT_INPUT_TYPE.MIN),
            propDatas.value.data.findIndex(i => i.argument_type === TEMPLATE_ARGUMENT_INPUT_TYPE.EXPRESSION),
            propDatas.value.data.findIndex(i => i.argument_type === TEMPLATE_ARGUMENT_INPUT_TYPE.MAX),
        ];
    }

    return indexArr;
});

const pagination = ref({
    page: 1,
    size: 10,
    total: 0,
});

const standardLists = ref([]);
const isLoading = ref(false);
const loadListData = async () => {
    try {
        if (isLoading.value) {
            return;
        }
        isLoading.value = true;
        const { data = [], total = 0 } = await FRequest('/api/v1/projector/taskNewValue/query', {
            page: pagination.value.page - 1,
            size: pagination.value.size,
            rule_id: props.rule.rule_id,
        });
        pagination.value.total = total;
        standardLists.value = data;
        isLoading.value = false;
    } catch (err) {
        console.warn(err);
        pagination.value.total = 0;
        standardLists.value = [];
        isLoading.value = false;
    }
};

const handleImport = async ({ task_new_value_id, result_value, status }) => {
    try {
        if (status !== 1) {
            return;
        }
        await FRequest('/api/v1/projector/taskNewValue/modify', {
            task_new_value_id,
            result_value,
            type: 2,
        });
        const targetData = propDatas.value.data;
        switch (props.actionType) {
            case 'szfw':
                // 数值范围
                // eslint-disable-next-line camelcase
                if (+targetData[curIndexs.value[0]].argument_value > result_value) {
                    // eslint-disable-next-line camelcase
                    targetData[curIndexs.value[0]].argument_value = result_value;
                // eslint-disable-next-line camelcase
                } else if (+targetData[curIndexs.value[2]].argument_value < result_value) {
                    // eslint-disable-next-line camelcase
                    targetData[curIndexs.value[2]].argument_value = result_value;
                }
                break;
            case 'mjz':
                // 枚举值
                // 20221220 和周玉壮讨论结果：如果是多个值导入如'1,2',不需要拆开且全部变为字符串
                // eslint-disable-next-line no-case-declarations,camelcase
                const tempResult = `${targetData[curIndexs.value[0]].argument_value}`;
                // 对双引号处理为#方便判断
                // eslint-disable-next-line no-case-declarations
                const parsedTempRes = tempResult.replace(/"/g, '#');

                /**
                 * 如果含有双引号则说明枚举值都是字符串，直接拼接即可
                 * */
                if (parsedTempRes.indexOf('#') !== -1) {
                    if (/^\d*$/.test(result_value) || !/^"(.*)"$/.test(result_value)) {
                        // eslint-disable-next-line no-case-declarations,camelcase
                        result_value = `"${result_value}"`;
                    }
                    // eslint-disable-next-line no-case-declarations,camelcase
                    targetData[curIndexs.value[0]].argument_value = `${tempResult},${result_value}`;
                    break;
                }

                /**
                 * 不含双引号则需要判断
                 * */
                // eslint-disable-next-line no-case-declarations
                const tempResultArr = tempResult.split(',');
                // eslint-disable-next-line no-case-declarations,camelcase
                tempResultArr.push(result_value); // 直接push,多个值后面会判断为字符串
                // eslint-disable-next-line no-case-declarations
                let isContainString = false;
                for (let i = 0; i < tempResultArr.length; i++) {
                    const item = tempResultArr[i];
                    if (!/^\d*$/.test(item)) {
                        isContainString = true;
                        break;
                    }
                }
                // eslint-disable-next-line no-case-declarations
                let targetResult = [];
                if (isContainString) {
                    targetResult = tempResultArr.map((item) => {
                        // 数字或者没有包括的字符串需要加上引号
                        console.log(item, /^\d*$/.test(item), /^"(.*)"$/.test(item));
                        if (/^\d*$/.test(item) || !/^"(.*)"$/.test(item)) {
                            item = `"${item}"`;
                        }
                        return item;
                    });
                    console.log(targetResult);
                }
                targetData[curIndexs.value[0]].argument_value = isContainString ? targetResult.join(',') : tempResultArr.join(',');
                break;
            default:
                break;
        }
        FMessage.success('导入成功');
        loadListData();
    } catch (err) {
        console.warn(err);
    }
};

const handleDelete = async ({ task_new_value_id, status }) => {
    try {
        if (status !== 1) {
            return;
        }
        // eslint-disable-next-line camelcase
        await FRequest(`/api/v1/projector/taskNewValue/delete/${task_new_value_id}`);
        FMessage.success($t('toastSuccess.deleteSuccess'));
        loadListData();
    } catch (err) {
        console.warn(err);
    }
};

const szfwFormRef = ref(null);
const confirmSzfwModify = async () => {
    try {
        await szfwFormRef.value.validate();
        emit('update:data', propDatas.value.data);
        // 通知父组件更新sql预览
        emit('updateSqlPreview', 'szfw');
        emit('update:show', false);
    } catch (err) {
        console.warn(err);
    }
};

const mjzFormRef = ref(null);
const confirmMjzModify = async () => {
    try {
        await mjzFormRef.value.validate();
        // 触发全局的项目规则信息修改结果保存
        // eventbus.emit('saveRuleQu');
        emit('update:data', propDatas.value.data);
        // 通知父组件更新sql预览
        emit('updateSqlPreview', 'mjz');
        emit('update:show', false);
    } catch (err) {
        console.warn(err);
    }
};

const togglePanel = () => {
    emit('update:show', false);
};

onUpdated(async () => {
    if (!props.show) {
        return;
    }
    pagination.value = {
        page: 1,
        size: 10,
        total: 0,
    };
    loadListData();
});
</script>
<style lang="less" scoped>
.wd-drawer-header{
    display: flex;
    .fes-form-inline{
        margin: 0;
        .fes-form-item {
            padding: 0;
            flex: 0 0 100%;
            max-width: 100%;
        }
    }
}
</style>
