<template>
    <FForm ref="templateFormRef" class="index-detail-form" labelWidth="90px" labelPosition="left" :model="standdard">
        <FFormItem :label="`标准值ID`" prop="standard_value_id">{{standdard.standard_value_id}}</FFormItem>
        <FFormItem :label="`中文名称`" prop="cn_name">{{standdard.cn_name}}</FFormItem>
        <FFormItem :label="`英文名称`" prop="en_name">{{standdard.en_name}}</FFormItem>
        <FFormItem :label="`版本号`" prop="version">{{standdard.version}}</FFormItem>
        <FFormItem :label="`标签`" prop="label_name">
            <div class="project-tags">
                <div v-for="(item, index) in standdard.label_name" :key="index" class="tags-item">{{item}}</div>
                <div v-if="standdard.label_name && standdard.label_name.length === 0">--</div>
            </div>
        </FFormItem>
        <FFormItem :label="`创建用户`" prop="create_user">{{standdard.create_user}}</FFormItem>
        <FFormItem :label="`更新用户`" prop="modify_user">{{standdard.modify_user || '--'}}</FFormItem>
        <FFormItem :label="`开发科室`" prop="dev_department_name">
            {{departmentDetail(standdard.dev_department_name)}}
        </FFormItem>
        <FFormItem :label="`运维科室`" prop="ops_department_name">
            {{departmentDetail(standdard.ops_department_name)}}
        </FFormItem>
        <FFormItem :label="`可见范围`" prop="action_range">
            <FEllipsis v-if="standdard.action_range?.length" :line="2">
                {{actionRangeDetail(standdard.action_range)}}
                <template #tooltip>
                    <div style="text-align:center">可见范围</div>
                    <div style="width:300px;word-wrap:break-word">
                        {{actionRangeDetail(standdard.action_range)}}
                    </div>
                </template>
            </FEllipsis>
            <div v-else>--</div>
        </FFormItem>
        <FFormItem :label="`内容`" prop="content">
            <div v-if="standdard.content">
                <FEllipsis :line="2">
                    {{standdard.content}}
                    <template #tooltip>
                        <div style="width:300px;word-wrap:break-word">
                            {{standdard.content}}
                        </div>
                    </template>
                </FEllipsis>
            </div>
            <span v-else>--</span>
        </FFormItem>
        <FFormItem :label="`来源`" prop="source">
            <div v-if="standdard.source">
                <FEllipsis :line="2">
                    {{standdard.source}}
                    <template #tooltip>
                        <div style="width:300px;word-wrap:break-word">
                            {{standdard.source}}
                        </div>
                    </template>
                </FEllipsis>
            </div>
            <span v-else>--</span>
        </FFormItem>
        <FFormItem :label="`审批系统`" prop="approve_system">{{getSystemLabel(standdard.approve_system)}}</FFormItem>
        <FFormItem :label="`审批编号`" prop="approve_number">{{standdard.approve_number || '--'}}</FFormItem>
    </FForm>
</template>
<script setup>
import {
    defineProps, inject,
} from 'vue';
import {
    useI18n,
} from '@fesjs/fes';
import { actionRangeDetail, departmentDetail } from '@/common/utils';

const { t: $t } = useI18n();

const systemList = inject('systemList');

// 参数type代表不同的模版类型
// 参数id代表需要编辑的模版id，尽在mode===edit的时候有效
const props = defineProps({
    standdard: {
        type: Object,
        default: {},
    },
});

const getSystemLabel = (data) => {
    const target = systemList.value.find(item => item.code === data);
    return target ? target.message : '--';
};
</script>
