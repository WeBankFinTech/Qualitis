<template>
    <FDrawer
        v-model:show="editShow"
        displayDirective="if"
        :title="drawerTitle"
        :footer="true"
        width="50%"
        @cancel="handleClose">
        <template #footer>
            <div v-if="mode !== 'view'">
                <FButton
                    type="primary"
                    style="margin-right: 16px;"
                    @click="finishPublish"
                >
                    {{$t('_.确定')}}
                </FButton>
                <FButton @click="handleClose">{{$t('_.取消')}}</FButton>
            </div>
            <div v-else>
                <FButton
                    type="info"
                    style="margin-right: 16px;"
                    @click="aboutTimingTask('edit')"
                >
                    {{$t('_.编辑')}}
                </FButton>
                <FButton
                    type="danger"
                    @click="deleteTimingTask"
                >
                    {{$t('_.删除')}}
                </FButton>
            </div>
        </template>
        <FForm
            ref="MainFormRef"
            labelWidth="69px"
            labelPosition="right"
            :class="mode === 'view' ? 'view' : ''"
            :model="templateForm"
            :rules="mode !== 'view' ? taskRules : []">
            <div class="header" :class="mode === 'view' ? 'ml-12' : 'ml-4'">{{$t('_.采集配置')}}</div>
            <FFormItem prop="proxy_user" :label="$t('_.代理用户')">
                <FSelect
                    v-if="mode !== 'view'"
                    v-model="templateForm.proxy_user"
                    style="width:830px"
                    disabled
                    filterable
                    clearable
                    :options="proxyUserList"
                    @change="userchange"
                ></FSelect>
                <span v-else class="view-font">
                    {{templateForm?.proxy_user || '--'}}
                </span>
            </FFormItem>
            <FFormItem :label="$t('_.采集对象')" prop="collect_obj">
                <FSelectCascader
                    v-if="mode !== 'view'"
                    v-model="templateForm.collect_obj"
                    :data="clusterVivisions"
                    :loadData="loadData"
                    disabled
                    clearable
                    remote
                    emitPath
                    showPath
                />
                <span v-else class="view-font">
                    {{templateForm?.collect_obj?.join('/') || '--'}}
                </span>
            </FFormItem>
            <FFormItem :label="$t('_.采集分区')" prop="partition">
                <FSelect
                    v-if="mode !== 'view'"
                    v-model="templateForm.partition"
                    style="width:100%"
                    remote
                    filterable
                    disabled
                    clearable
                    :options="[]"
                ></FSelect>
                <span v-else class="view-font">{{templateForm.partition}}</span>
                <!-- <FFormItem
                    label="采集频率"
                    prop="executeInterval"
                >
                    <FSelect
                        v-if="mode !== 'view'"
                        v-model="templateForm.executeInterval"
                        class="workflows-select"
                        :options="executeIntervalList"
                        @change="generateExecutionTime(templateForm)"
                    ></FSelect>
                    <span
                        v-else
                        class="view-font"
                    >{{executeIntervalDict[templateForm.executeInterval]}}</span>
                </FFormItem>
                <FFormItem
                    v-if="templateForm?.executeInterval"
                    label="采集时间"
                    prop="executionTime"
                >
                    <div
                        v-if="mode !== 'view'"
                        class="execution-time-wrapper"
                    >
                        <FInput
                            v-if="templateForm.executeInterval === 'hour'"
                            v-model="templateForm.executionTimeMM"
                            class="mr-8px"
                            @change="generateExecutionTime(templateForm)"
                        >
                            <template #suffix>分</template>
                        </FInput>
                        <FInput
                            v-if="templateForm.executeInterval === 'hour'"
                            v-model="templateForm.executionTimeSS"
                            @change="generateExecutionTime(templateForm)"
                        >
                            <template #suffix>秒</template>
                        </FInput>
                        <FTimePicker
                            v-if="templateForm.executeInterval === 'day'"
                            v-model="templateForm.executionTimePicker"
                            @change="generateExecutionTime(templateForm)"
                        ></FTimePicker>
                        <FSelect
                            v-if="templateForm.executeInterval === 'week'"
                            v-model="templateForm.executionDateInInterval"
                            class="mr-8px"
                            :options="executionTimeWeekOptions"
                            @change="generateExecutionTime(templateForm)"
                        ></FSelect>
                        <FSelect
                            v-if="templateForm.executeInterval === 'month'"
                            v-model="templateForm.executionDateInInterval"
                            class="mr-8px"
                            :options="executionTimeMonthOptions"
                            @change="generateExecutionTime(templateForm)"
                        ></FSelect>
                        <FTimePicker
                            v-if="templateForm.executeInterval === 'week' || templateForm.executeInterval === 'month'"
                            v-model="templateForm.executionTimePicker"
                            @change="generateExecutionTime(templateForm)"
                        ></FTimePicker>
                    </div>
                    <span
                        v-else
                        class="view-font"
                    >{{fomatPreviewExecutionTime(templateForm.execute_interval,templateForm.execute_date_in_interval,templateForm.execute_time_in_date).time}}</span>
                </FFormItem> -->
            </FFormItem>
            <div v-if="mode !== 'view'">
                <FFormItem :label="$t('_.采集信息')" prop="template_id">
                    <FFormItem prop="template_id" style="margin-right:8px" :rules="[{ trigger: ['change'], required: true, type: 'number', message: '不能为空' }]">
                        <FSelect
                            v-model="templateForm.template_id"
                            style="width: 410px"
                            filterable
                            :disabled="templateForm.en_name === 'Table Row Num'"
                            :placeholder="'请选择采集算子'"
                            :options="editTemplateList"
                            valueField="template_id"
                            labelField="template_name"
                            @change="templateIdchange"
                        >
                        </FSelect>
                    </FFormItem>
                    <FFormItem prop="column">
                        <FSelect
                            v-model="templateForm.column"
                            style="width: 410px"
                            filterable
                            :placeholder="$t('_.请选择')"
                            :options="columnList"
                            valueField="column_name"
                            labelField="column_name"
                        >
                        </FSelect>
                    </FFormItem>
                </FFormItem>
                <FFormItem :label="$t('_.采集脚本')" style="margin-top:-22px" prop="collect_type">
                    <FInput v-model="templateForm.collect_script" type="textarea" disabled :placeholder="$t('_.根据所选采集对象和算子反显')" />
                </FFormItem>
                <FFormItem :label="$t('_.执行参数')" prop="execution_parameters_name">
                    <FInput
                        v-model="templateForm.execution_parameters_name"
                        class="form-edit-input"
                        readonly
                        :placeholder="$t('_.请选择')"
                        @focus="openExecuteParamsDrawer" />
                </FFormItem>
            </div>
            <div v-else class="border">
                <div class="lheader">{{$t('_.配置组01')}}</div>
                <FFormItem :label="$t('_.采集信息')" prop="template_id">{{templateList?.find(item => item.template_id === templateForm.template_id)?.template_name || '--'}} {{templateForm.column}}</FFormItem>
                <FFormItem :label="$t('_.采集脚本')" prop="collect_script">{{templateForm.collect_script || '--'}}</FFormItem>
                <FFormItem :label="$t('_.执行参数')" prop="execution_parameters_name">{{templateForm.execution_parameters_name || '--'}}</FFormItem>
            </div>
            <div></div>
            <div v-if="mode === 'view'">
                <div class="header ml-12">{{$t('_.采集调度')}}</div>
                <FFormItem :label="$t('_.采集频率')" prop="executeInterval">
                    <span class="view-font">{{executeIntervalDict[templateForm.executeInterval] || '--'}}</span>
                </FFormItem>
                <FFormItem :label="$t('_.采集时间')" prop="executionTime">
                    <span class="view-font">{{fomatPreviewExecutionTime(templateForm.execute_interval,templateForm?.execute_date_in_interval,templateForm.execute_time_in_date).time || '--'}}</span>
                </FFormItem>
            </div>
        </FForm>
        <!-- 选择参数模版 -->
        <FDrawer
            v-model:show="showExecuteParamsDrawer"
            :title="$t('_.执行模板')"
            displayDirective="if"
            :footer="true"
            :oktext="$t('_.确认')"
            width="50%"
            @ok="confirmSelect"
        >
            <ProjectTemplate
                ref="templateRef"
                :showHeader="false"
                :isEmbed="true"
                :isFold="true"
                :curExeName="templateForm.execution_parameters_name"
                class="project-template-style"
            />
        </FDrawer>
    </FDrawer>
</template>
<script setup>

import {
    defineProps, defineEmits, computed, ref, unref, inject, onMounted, watch,
} from 'vue';
import { request, useI18n } from '@fesjs/fes';
import { FMessage, FModal, FTimePicker } from '@fesjs/fes-design';
import {
    MinusCircleOutlined, QuestionCircleOutlined, PlusCircleOutlined,
} from '@fesjs/fes-design/es/icon';
import { cloneDeep, method } from 'lodash-es';
import ProjectTemplate from '@/pages/projects/template';
import useBasicOptions from '../hooks/useBasicOptions';
import {
    fomatPreviewExecutionTime, executeIntervalList, executeIntervalDict, executionTimeWeekOptions, executionTimeMonthOptions,
} from '../utils';


const { t: $t } = useI18n();

const {
    clusterVivisions,
    loadData,
} = useBasicOptions();
const props = defineProps({
    isEditShow: {
        type: Boolean,
        required: true,
    },
    templateForm: {
        required: true,
        type: Object,
    },
    mode: {
        type: String,
        default: 'view',
    },
    drawerTitle: {
        type: String,
        required: true,
    },
    templateList: {
        type: Array,
        default: () => [],
    },
});
const templateForm = ref(cloneDeep(props.templateForm));
const emit = defineEmits(['editSubmit', 'update:isEditShow', 'deleteTask', 'update:mode', 'update:drawerTitle']);

const editShow = computed({
    get() {
        return props.isEditShow;
    },
    set(value) {
        emit('update:isEditShow', value);
    },
});
// 选择模板
const templateRef = ref(null);
const showExecuteParamsDrawer = ref(false);
const openExecuteParamsDrawer = () => {
    showExecuteParamsDrawer.value = true;
};
const confirmSelect = () => {
    const templateLists = templateRef.value.templateList;
    let selectedIndex = -1;
    for (let i = 0; i < templateLists.length; i++) {
        if (templateLists[i].isSelected) {
            selectedIndex = i;
            break;
        }
    }
    if (selectedIndex === -1) return FMessage.warning($t('_.请选择模板'));
    showExecuteParamsDrawer.value = false;
    templateForm.value.execution_parameters_name = templateLists[selectedIndex].name;
};
// 表单校验
const MainFormRef = ref(null);
const taskRules = computed(() => ({
    collect_obj: {
        type: 'array',
        required: true,
        trigger: ['blur', 'change'],
    },
    partition: {
        type: 'string',
        required: true,
        trigger: ['blur', 'change'],
    },
    executionTime: {
        required: false,
    },
    execute_interval: {
        required: false,
    },
    template_id: {
        required: true,
        type: 'number',
    },
    column: {
        required: true,
    },
    execution_parameters_name: {
        required: true,
    },
}));

const generateExecutionTime = (item) => {
    console.log(item);
    if (item.executeInterval === 'hour') {
        const mm = parseInt(item.executionTimeMM) || 0;
        const ss = parseInt(item.executionTimeSS) || 0;
        if (mm > 59) {
            item.executionTimeMM = '00';
        } else if (mm < 10) {
            item.executionTimeMM = `0${mm}`;
        } else {
            item.executionTimeMM = `${mm}`;
        }
        if (ss > 59) {
            item.executionTimeSS = '00';
        } else if (ss < 10) {
            item.executionTimeSS = `0${ss}`;
        } else {
            item.executionTimeSS = `${ss}`;
        }
        item.executionTimeInDate = `00:${item.executionTimeMM}:${item.executionTimeSS}`;
    } else {
        item.executionTimeInDate = item.executionTimePicker;
    }
    item.executionTime = fomatPreviewExecutionTime(item.executeInterval, item.executionDateInInterval, item.executionTimeInDate).time;
    console.log('item.executionTime', item.executionTime);
};
const handleClose = () => {
    editShow.value = false;
    // resetTask();
};
const finishPublish = async () => {
    await MainFormRef.value.validate();
    console.log('点击发布', templateForm.value);
    const params = {
        id: templateForm.value.id,
        template_id: templateForm.value.template_id,
        column: templateForm.value.column,
        execution_parameters_name: templateForm.value.execution_parameters_name,
    };
    const res = await request('api/v1/projector/imsmetric/collect/modify', params);
    FMessage.success($t('_.修改成功'));
    handleClose();
};
const aboutTimingTask = async () => {
    emit('update:mode', 'edit');
    emit('update:drawerTitle', `编辑采集配置（${templateForm.value.en_name}）`);
};
const deleteTimingTask = async () => {
    FModal.confirm({
        title: $t('_.删除'),
        content: $t('_.确认删除吗？'),
        okText: $t('_.确认'),
        async onOk() {
            try {
                await request('api/v1/projector/imsmetric/collect/delete', [templateForm.value.id]);
                FMessage.success($t('_.删除成功'));
                emit('deleteTask');
            } catch (err) {
                console.warn(err);
            }
        },
    });
};

const templateIdchange = (val) => {
    if (val) {
        templateForm.value.collect_script = props.templateList?.find(item => item.template_id === val)?.sql_action ?? '';
    } else {
        templateForm.value.collect_script = '';
    }
};
const columnList = ref([]);
const editTemplateList = ref(cloneDeep(props.templateList));
onMounted(async () => {
    if (props.mode !== 'view') {
        const params = {
            cluster_name: templateForm.value?.collect_obj[0] ?? '',
            db_name: templateForm.value?.collect_obj[1] ?? '',
            table_name: templateForm.value?.collect_obj[2] ?? '',
            proxy_user: templateForm.value.proxy_user,
            start_index: 0,
            page_size: 2147483647,
        };
        console.log('请求', params);

        const res = await request('/api/v1/projector/meta_data/column', params);
        columnList.value = res?.data || [];
        if (templateForm.value.en_name === 'Table Row Num') {
            editTemplateList.value.forEach((item) => {
                if (item.template_name === $t('_.采集表行数统计')) {
                    item.disabled = true;
                }
            });
        }
    }
    templateForm.value.collect_script = props.templateList?.find(item => item.template_id === templateForm.value.template_id)?.sql_action ?? '';
    templateForm.value.executeInterval = templateForm.value.execute_interval;
    templateForm.value.executionDateInInterval = templateForm.value.execute_date_in_interval;
    templateForm.value.executionTimeInDate = templateForm.value.execute_time_in_date;
    templateForm.value.executionTime = fomatPreviewExecutionTime(templateForm.value.execute_interval, templateForm.value.execute_date_in_interval, templateForm.value.execute_time_in_date).time;
    templateForm.value.executionTimePicker = templateForm.value.execute_interval === 'hour' ? '' : templateForm.value.execute_time_in_date;
    templateForm.value.executionTimeMM = templateForm.value.execute_interval === 'hour' ? templateForm.value.execute_time_in_date.split(':')[1] : '';
    templateForm.value.executionTimeSS = templateForm.value.execute_interval === 'hour' ? templateForm.value.execute_time_in_date.split(':')[2] : '';
});

</script>
<style lang="less" scoped>
.header{
    font-family: PingFangSC-Medium;
    font-size: 14px;
    color: #0F1222;
    letter-spacing: 0;
    line-height: 22px;
    font-weight: 600;
    margin-bottom: 10px;
}
.ml-12 {
    margin-left: 12px;
}
.ml-4 {
    margin-left: 4px;
}
.workflows-input {
    width: 100%;
}
.workflows-select {
    width: 100%;
}
.execution-time-wrapper {
    width: 100%;
    display: flex;
    align-items: center;

    .mr-8px {
        margin-right: 8px;
    }
}
.add-box {
    width: 100%;
}
.add {
    display: flex;
}
.add-icon {
    color: #5384FF;
}
.border{
    padding: 16px 16px 10px;
    border: 1px solid #cfd0d3;
    border-radius: 4px;
    margin-bottom: 16px;
    margin-right: 8px;
}
.lheader {
    display: flex;
    margin-left: 12px;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    font-family: PingFangSC-Regular;
    font-size: 14px;
    color: #0F1222;
    line-height: 22px;
    font-weight: 400;
}
.view-font {
    font-family: PingFangSC-Regular;
    font-size: 14px;
    color: #0f1222;
    line-height: 22px;
    font-weight: 400;
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
.view {
    :deep(.fes-form-item) {
        margin-bottom: 6px;
    }
}
</style>
