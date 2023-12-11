<template>
    <div class="rule-detail-form execution-params-style" :class="{ edit: mode === 'edit' || (editOrDisplay === 'edit' && !mode), isEmbed: isEmbed } ">
        <FForm ref="excutionParamsFormRef" :model="executionParams" :labelWidth="computeLabelWidth()" :labelPosition="computeLabelPosition()" :rules="excutionParamsRules">
            <div class="form-title">{{$t('myProject.executeParams')}}</div>
            <!-- 是否设置过滤条件 -->
            <FFormItem v-if="isShowFilterParams()" prop="specify_filter">
                <FCheckbox v-model="executionParams.specify_filter" class="form-edit-input" @change="clearSubData($event, 'filter')">{{$t('myProject.setFilterCondition')}}</FCheckbox>
                <div class="form-preview-label"><span class="input-preview-label">{{$t('myProject.setFilterCondition')}}</span> {{executionParams.specify_filter ? $t('common.yes') : $t('common.no')}}</div>
            </FFormItem>
            <!-- 过滤条件 -->
            <div v-if="executionParams.specify_filter && isShowFilterParams()" :class="{ edit: mode === 'edit' } " class="single-sub-form rule-detail-sub-form">
                <FFormItem prop="filter" :label="$t('myProject.filterCondition')" labelClass="label-center">
                    <FInput v-model="executionParams.filter" class="form-edit-input"
                            :placeholder="$t('myProject.pleaseEnterFilter')" type="textarea" :rows="2" />
                    <div class="form-preview-label">{{executionParams.filter}}</div>
                </FFormItem>
                <FFormItem prop="source_table_filter" :label="$t('myProject.sourceTableFilter')" labelClass="label-center">
                    <FInput v-model="executionParams.source_table_filter" class="form-edit-input"
                            :placeholder="$t('myProject.pleaseEnterSourceTableFilter')" type="textarea" :rows="2" />
                    <div class="form-preview-label">{{executionParams.source_table_filter}}</div>
                </FFormItem>
                <FFormItem prop="target_table_filter" :label="$t('myProject.targetTableFilter')" labelClass="label-center">
                    <FInput v-model="executionParams.target_table_filter" class="form-edit-input"
                            :placeholder="$t('myProject.pleaseEnterTargetTableFilter')" type="textarea" :rows="2" />
                    <div class="form-preview-label">{{executionParams.target_table_filter}}</div>
                </FFormItem>
            </div>
            <!-- 是否开启任务阻断 -->
            <FFormItem prop="abort_on_failure">
                <FCheckbox v-model="executionParams.abort_on_failure" class="form-edit-input">{{$t('myProject.disconnect')}}</FCheckbox>
                <div class="form-preview-label"><span class="input-preview-label">{{$t('myProject.isDisconnect')}}</span> {{executionParams.abort_on_failure ? $t('common.yes') : $t('common.no')}}</div>
            </FFormItem>
            <!-- 是否聚合多环境计算 -->
            <FFormItem prop="union_all">
                <FCheckbox v-model="executionParams.union_all" class="form-edit-input">{{$t('myProject.unionSwitch')}}</FCheckbox>
                <div class="form-preview-label"><span class="input-preview-label">{{$t('myProject.unionSwitch')}}</span> {{executionParams.union_all ? $t('common.yes') : $t('common.no')}}</div>
            </FFormItem>
            <!-- 是否开启异常数据定向存储 -->
            <FFormItem prop="abnormal_data_storage">
                <FCheckbox v-model="executionParams.abnormal_data_storage" class="form-edit-input" @change="clearSubData($event, 'dataStorage')">{{$t('myProject.abnormalDataStorage')}}</FCheckbox>
                <div class="form-preview-label"><span class="input-preview-label">{{$t('myProject.abnormalDataStorage')}}</span> {{executionParams.abnormal_data_storage ? $t('common.yes') : $t('common.no')}}</div>
            </FFormItem>
            <div v-if="executionParams.abnormal_data_storage" :class="{ edit: mode === 'edit' } " class="single-sub-form rule-detail-sub-form">
                <!-- 存储集群 -->
                <FFormItem :label="$t('common.storageCluster')" prop="cluster" labelClass="label-center">
                    <FSelect
                        v-model="executionParams.cluster"
                        class="form-edit-input"
                        filterable
                        :placeholder="$t('common.pleaseSelect')"
                        @change="getDB"
                    >
                        <FOption
                            v-for="item in (injectClusterList || clusterList)"
                            :key="item.cluster_name"
                            :value="item.cluster_name"
                            :label="item.cluster_name"
                        ></FOption>
                    </FSelect>
                    <div class="form-preview-label">{{executionParams.cluster || '--'}}</div>
                </FFormItem>
                <!-- 代理用户 -->
                <FFormItem :label="$t('common.proxyUser')" prop="abnormal_proxy_user" labelClass="label-center">
                    <FSelect
                        v-model="executionParams.abnormal_proxy_user"
                        class="form-edit-input"
                        filterable
                        clearable
                        :placeholder="$t('common.pleaseSelect')"
                        @change="getDB">
                        <FOption
                            v-for="(item, index) in (injectProxyUserList || proxyUserList)"
                            :key="index"
                            :value="item"
                            :label="item"
                        ></FOption>
                    </FSelect>
                    <div class="form-preview-label">{{executionParams.abnormal_proxy_user || '--'}}</div>
                </FFormItem>
                <!-- 异常数据存储库 -->
                <FFormItem :label="$t('myProject.abnormalDatabaseStorage')" prop="abnormal_database" labelClass="label-center">
                    <FSelect
                        v-model="executionParams.abnormal_database"
                        class="form-edit-input"
                        filterable
                        :placeholder="$t('common.pleaseSelect')"
                    >
                        <FOption
                            v-for="item in abnormalDatabaseList"
                            :key="item.db_name"
                            :value="item.db_name"
                            :label="item.db_name"
                        ></FOption>
                    </FSelect>
                    <div class="form-preview-label">{{executionParams.abnormal_database || '--'}}</div>
                </FFormItem>
            </div>
            <!-- 文件校验规则 没有静态执行参数配置 -->
            <template v-if="!['4-1'].includes(ruleType)">
                <!-- 动态引擎配置，原静态执行参数 -->
                <FFormItem prop="specify_static_startup_param">
                    <FCheckbox v-model="executionParams.specify_static_startup_param" class="form-edit-input" @change="clearSubData($event, 'staticParams')">{{$t('executationConfig.dynamicEngineConfig')}}</FCheckbox>
                    <div class="form-preview-label inline"><span class="input-preview-label">{{$t('executationConfig.dynamicEngineConfig')}}</span> {{executionParams.specify_static_startup_param ? $t('common.yes') : $t('common.no')}}</div>
                </FFormItem>
                <div v-if="executionParams.specify_static_startup_param" :class="{ isEmbed: isEmbed }" class="rule-detail-sub-form inline">
                    <div v-for="(staticParamsData, index) in executionParams.static_execution_parameters" :key="index" class="sub-form-item">
                        <FForm
                            :ref="el => { if (el) staticParamsListRef[index] = el; }"
                            label-width="120px"
                            :model="executionParams.static_execution_parameters[index]"
                            class="inline-forms"
                            :class="{ isEmbed: isEmbed , editInlineForm: computeInlineFormStyle(mode), previewInlineForm: !computeInlineFormStyle(mode) }"
                            :labelPosition="mode === 'edit' ? 'right' : 'left'"
                            :labelWidth="84"
                            :rules="staticParamsRuleValidate"
                        >
                            <div class="sub-title">
                                <div class="left">{{$t('common.engine')}}{{index + 1}}</div>
                                <div v-if="executionParams.static_execution_parameters.length > 1 && mode === 'edit'" class="right">
                                    <FButton type="text" class="del-button" @click="deleteStaticParams(executionParams.static_execution_parameters[index])">
                                        <template #icon><MinusCircleOutlined /></template>{{$t('common.delete')}}
                                    </FButton>
                                </div>
                            </div>
                            <!-- 参数类型-->
                            <FFormItem :label="$t('myProject.paramsType')" prop="parameter_type">
                                <FSelect
                                    v-model="executionParams.static_execution_parameters[index].parameter_type"
                                    class="form-edit-input"
                                    filterable
                                    :placeholder="$t('common.pleaseSelect')"
                                    :options="engineParamsTypeList"
                                    valueField="code"
                                    labelField="message"
                                    @change="staticParameterTypeChange(executionParams.static_execution_parameters[index], $event)"
                                >
                                </FSelect>
                                <div class="form-preview-label"> {{engineParamsTypeList?.find(v => v.code === executionParams.static_execution_parameters[index].parameter_type)?.message || ''}}</div>
                            </FFormItem>
                            <!-- 参数名称 -->
                            <FFormItem :label="$t('myProject.paramsName')" prop="parameter_name">
                                <FInput v-if="executionParams.static_execution_parameters[index].parameter_type === 4" v-model="executionParams.static_execution_parameters[index].parameter_name" class="form-edit-input" :placeholder="$t('common.pleaseEnter')" />
                                <FSelect
                                    v-else
                                    v-model="executionParams.static_execution_parameters[index].parameter_name"
                                    class="form-edit-input"
                                    filterable
                                    :placeholder="$t('common.pleaseSelect')"
                                    :options="engineParamsTypeListArray[String(executionParams.static_execution_parameters[index].parameter_type)]"
                                    @change="setDefalutParamsValue(executionParams.static_execution_parameters[index])"
                                >
                                </FSelect>
                                <div class="form-preview-label">
                                    <template v-if="executionParams.static_execution_parameters[index].parameter_type === 4">
                                        {{executionParams.static_execution_parameters[index].parameter_name}}
                                    </template>
                                    <template v-else>
                                        {{engineParamsTypeListArray[String(executionParams.static_execution_parameters[index].parameter_type)]
                                            ?.find(v => v.value === executionParams.static_execution_parameters[index].parameter_name)?.label || ''}}
                                    </template>
                                </div>
                            </FFormItem>
                            <!-- 参数值 -->
                            <FFormItem :label="$t('myProject.paramsValue')" prop="parameter_value">
                                <FInput v-model="executionParams.static_execution_parameters[index].parameter_value" class="form-edit-input" :placeholder="$t('myProject.pleaseEnterParamsValue')" />
                                <div class="form-preview-label">{{executionParams.static_execution_parameters[index].parameter_value}}</div>
                                <FTooltip :content="$t('common.paramsExample') + ': wds.linkis.rm.yarnqueue=dws;...'" placement="right-start">
                                    <ExclamationCircleOutlined v-show="mode === 'edit'" class="tip hint" />
                                </FTooltip>
                            </FFormItem>
                        </FForm>
                    </div>
                    <div v-show="mode === 'edit'" class="add-item">
                        <FButton type="link" class="link-button" @click="addStaticParams">
                            <template #icon><PlusCircleOutlined /></template>{{$t('common.addEngine')}}
                        </FButton>
                    </div>
                </div>
            </template>
            <!-- 执行变量配置 -->
            <FFormItem prop="execution_variable">
                <FCheckbox v-model="executionParams.execution_variable" class="form-edit-input" @change="clearSubData($event, 'variableConfig')">{{$t('executationConfig.executationVaribleConfig')}}</FCheckbox>
                <div class="form-preview-label inline"><span class="input-preview-label">{{$t('executationConfig.executationVaribleConfig')}}</span> {{executionParams.execution_variable ? $t('common.yes') : $t('common.no')}}</div>
            </FFormItem>
            <div v-if="executionParams.execution_variable" :class="{ isEmbed: isEmbed }" class="rule-detail-sub-form inline">
                <div v-for="(variableConfigData, index) in executionParams.execution_management" :key="index" class="sub-form-item">
                    <FForm
                        :ref="el => { if (el) variableConfigListRef[index] = el; }"
                        label-width="120px"
                        :model="executionParams.execution_management[index]"
                        class="inline-forms"
                        :class="{ isEmbed: isEmbed , editInlineForm: computeInlineFormStyle(mode), previewInlineForm: !computeInlineFormStyle(mode) }"
                        :labelPosition="mode === 'edit' ? 'right' : 'left'"
                        :labelWidth="84"
                        :rules="variableConfigRuleValidate"
                    >
                        <div class="sub-title">
                            <div class="left">{{$t('common.variable')}}{{index + 1}}</div>
                            <div v-if="executionParams.execution_management.length > 1 && mode === 'edit'" class="right">
                                <FButton type="text" class="del-button" @click="deleteVariableConfig(executionParams.execution_management[index])">
                                    <template #icon><MinusCircleOutlined /></template>{{$t('common.delete')}}
                                </FButton>
                            </div>
                        </div>
                        <!-- 变量类型-->
                        <FFormItem :label="$t('myProject.variableType')" prop="variable_type">
                            <FSelect
                                v-model="executionParams.execution_management[index].variable_type"
                                class="form-edit-input"
                                filterable
                                :placeholder="$t('common.pleaseSelect')"
                                :options="executionVariableTypeList"
                                valueField="code"
                                labelField="message"
                                @change="clearVariableType(executionParams.execution_management[index])"
                            >
                            </FSelect>
                            <div class="form-preview-label"> {{executionVariableTypeList?.find(v => v.code === executionParams.execution_management[index].variable_type)?.message || ''}}</div>
                        </FFormItem>
                        <!-- 变量名称 -->
                        <FFormItem :label="$t('myProject.variableName')" prop="variable_name">
                            <FInput v-if="executionParams.execution_management[index].variable_type === 2" v-model="executionParams.execution_management[index].variable_name" class="form-edit-input" :placeholder="$t('common.pleaseEnter')" />
                            <FSelect
                                v-else
                                v-model="executionParams.execution_management[index].variable_name"
                                class="form-edit-input"
                                filterable
                                :placeholder="$t('common.pleaseSelect')"
                                :options="executionVariableNameList"
                                @change="clearVariableName(executionParams.execution_management[index])"
                            >
                            </FSelect>
                            <div class="form-preview-label">
                                <template v-if="executionParams.execution_management[index].variable_type === 2">
                                    {{executionParams.execution_management[index].variable_name}}
                                </template>
                                <template v-else>
                                    {{executionVariableNameList?.find(v => v.value === executionParams.execution_management[index].variable_name)?.label || ''}}
                                </template>
                            </div>
                        </FFormItem>
                        <!-- 变量值 -->
                        <FFormItem :label="$t('myProject.variableValue')" prop="variable_value">
                            <FInput v-model="executionParams.execution_management[index].variable_value" class="form-edit-input" :placeholder="$t('myProject.pleaseEnterVariableValue')" />
                            <div class="form-preview-label">{{executionParams.execution_management[index].variable_value}}</div>
                        </FFormItem>
                    </FForm>
                </div>
                <div v-show="mode === 'edit'" class="add-item">
                    <FButton type="link" class="link-button" @click="addVariableConfig">
                        <template #icon><PlusCircleOutlined /></template>{{$t('common.addVariable')}}
                    </FButton>
                </div>
            </div>
            <!-- 高级执行配置 -->
            <FFormItem prop="advanced_execution">
                <FCheckbox v-model="executionParams.advanced_execution" class="form-edit-input" @change="clearSubData($event, 'advanceConfig')">{{$t('executationConfig.advanceExecutationConfig')}}</FCheckbox>
                <div class="form-preview-label inline"><span class="input-preview-label">{{$t('executationConfig.advanceExecutationConfig')}}</span> {{executionParams.advanced_execution ? $t('common.yes') : $t('common.no')}}</div>
            </FFormItem>
            <div v-if="executionParams.advanced_execution" :class="{ edit: mode === 'edit' } " class="single-sub-form rule-detail-sub-form">
                <FFormItem prop="engine_reuse" :label="$t('common.engineReuse')" labelClass="label-center">
                    <FRadioGroup v-model="executionParams.engine_reuse" class="form-edit-input" :cancelable="false">
                        <FRadio :value="true">{{$t('common.yes')}}</FRadio>
                        <FRadio :value="false">{{$t('common.no')}}</FRadio>
                    </FRadioGroup>
                    <div class="form-preview-label">{{executionParams.engine_reuse ? $t('common.yes') : $t('common.no')}}</div>
                </FFormItem>
                <FFormItem prop="concurrency_granularity" :label="$t('executationConfig.concurrencyGranularity')" labelClass="label-center">
                    <FRadioGroup v-model="executionParams.concurrency_granularity" class="form-edit-input" :cancelable="false">
                        <FRadio value="split_by:db">{{$t('executationConfig.dbGranularity')}}</FRadio>
                        <FRadio value="split_by:table">{{$t('executationConfig.tableGranularity')}}</FRadio>
                        <FRadio value="split_by:merge">{{$t('executationConfig.taskMerge')}}</FRadio>
                    </FRadioGroup>
                    <div class="form-preview-label">{{granularityMap[executionParams.concurrency_granularity]}}</div>
                </FFormItem>
                <FFormItem prop="dynamic_partitioning" :label="$t('common.isDynamicPartition')" labelClass="label-center">
                    <FRadioGroup v-model="executionParams.dynamic_partitioning" class="form-edit-input" :cancelable="false">
                        <FRadio :value="true">{{$t('common.yes')}}</FRadio>
                        <FRadio :value="false">{{$t('common.no')}}</FRadio>
                    </FRadioGroup>
                    <div class="form-preview-label">{{executionParams.dynamic_partitioning ? $t('common.yes') : $t('common.no')}}</div>
                </FFormItem>
                <FFormItem v-if="executionParams.dynamic_partitioning" prop="top_partition" :label="$t('common.topPartition')" labelClass="label-center">
                    <FInput v-model="executionParams.top_partition" class="form-edit-input"
                            :placeholder="$t('common.pleaseEnterTopPartition')" />
                    <div class="form-preview-label">{{executionParams.top_partition}}</div>
                </FFormItem>
            </div>
            <div class="form-title">{{$t('myProject.indicatorParameters')}}</div>
            <!-- 是否上传校验通过标值至IMS -->
            <FFormItem prop="upload_rule_metric_value">
                <FCheckbox v-model="executionParams.upload_rule_metric_value" class="form-edit-input">{{$t('myProject.uploadRuleMetricValue')}}</FCheckbox>
                <div class="form-preview-label"><span class="input-preview-label">{{$t('myProject.uploadRuleMetricValue')}}</span> {{executionParams.upload_rule_metric_value ? $t('common.yes') : $t('common.no')}}</div>
            </FFormItem>
            <!-- 是否上传校验失败指标值至IMS -->
            <FFormItem prop="upload_abnormal_value">
                <FCheckbox v-model="executionParams.upload_abnormal_value" class="form-edit-input">{{$t('myProject.uploadAbnormalValue')}}</FCheckbox>
                <div class="form-preview-label"><span class="input-preview-label">{{$t('myProject.uploadAbnormalValue')}}</span> {{executionParams.upload_abnormal_value ? $t('common.yes') : $t('common.no')}}</div>
            </FFormItem>
            <!-- 是否剔除校验失败指标值 -->
            <FFormItem prop="delete_fail_check_result">
                <FCheckbox v-model="executionParams.delete_fail_check_result" class="form-edit-input">{{$t('myProject.deleteFailCheckResult')}}</FCheckbox>
                <div class="form-preview-label"><span class="input-preview-label">{{$t('myProject.deleteFailCheckResult')}}</span> {{executionParams.delete_fail_check_result ? $t('common.yes') : $t('common.no')}}</div>
            </FFormItem>
            <div class="form-title">{{$t('myProject.warningParams')}}</div>
            <!-- 是否告警 -->
            <FFormItem prop="alert">
                <FCheckbox v-model="executionParams.alert" class="form-edit-input" @change="clearSubData($event, 'alert')">{{$t('myProject.isWarning')}}</FCheckbox>
                <div class="form-preview-label"><span class="input-preview-label">{{$t('myProject.isWarning')}}</span> {{executionParams.alert ? $t('common.yes') : $t('common.no')}}</div>
            </FFormItem>
            <div v-if="executionParams.alert" :class="{ isEmbed: isEmbed }" class="rule-detail-sub-form inline">
                <div v-for="(alarmData, index) in executionParams.alarm_arguments_execution_parameters" :key="index" class="sub-form-item">
                    <FForm
                        :ref="el => { if (el) alarmListRef[index] = el; }"
                        label-width="120px"
                        :model="executionParams.alarm_arguments_execution_parameters[index]"
                        class="inline-forms"
                        :class="{ isEmbed: isEmbed , editInlineForm: computeInlineFormStyle(mode), previewInlineForm: !computeInlineFormStyle(mode) }"
                        :labelPosition="mode === 'edit' ? 'right' : 'left'"
                        :labelWidth="84"
                        :rules="alertRuleValidate"
                    >
                        <div class="sub-title">
                            <div class="left">{{$t('myProject.warning')}}{{index + 1}}</div>
                            <div v-if="executionParams.alarm_arguments_execution_parameters.length > 1 && mode === 'edit'" class="right">
                                <FButton type="text" class="del-button" @click="deleteAlarm(executionParams.alarm_arguments_execution_parameters[index])">
                                    <template #icon><MinusCircleOutlined /></template>{{$t('common.delete')}}
                                </FButton>
                            </div>
                        </div>
                        <!-- 告警事件-->
                        <FFormItem :label="$t('myProject.alarmEvent')" prop="alarm_event">
                            <FSelect
                                v-model="executionParams.alarm_arguments_execution_parameters[index].alarm_event"
                                class="form-edit-input"
                                filterable
                                :placeholder="$t('common.pleaseSelect')"
                                :options="alarmEventList"
                                valueField="code"
                                labelField="message"
                            >
                            </FSelect>
                            <div class="form-preview-label"> {{alarmEventList?.find(v => v.code === executionParams.alarm_arguments_execution_parameters[index].alarm_event)?.message || ''}}</div>
                        </FFormItem>
                        <!-- 告警级别 -->
                        <FFormItem :label="$t('myProject.level')" prop="alarm_level">
                            <FSelect
                                v-model="executionParams.alarm_arguments_execution_parameters[index].alarm_level"
                                class="form-edit-input"
                                filterable
                                :placeholder="$t('common.pleaseSelect')"
                                :options="alarmLevelList"
                                valueField="level"
                            >
                            </FSelect>
                            <div class="form-preview-label"> {{alarmLevelList?.find(v => v.level === executionParams.alarm_arguments_execution_parameters[index].alarm_level)?.label || ''}}</div>
                        </FFormItem>
                        <!-- 告警接收人 -->
                        <FFormItem :label="$t('myProject.receiver')" prop="alarm_receiver">
                            <FInput v-model="executionParams.alarm_arguments_execution_parameters[index].alarm_receiver" class="form-edit-input" :placeholder="$t('myProject.pleaseEnterWarningReceiver')" />
                            <div class="form-preview-label">{{executionParams.alarm_arguments_execution_parameters[index].alarm_receiver}}</div>
                            <FTooltip :content="$t('crossTableCheck.alert_receiver')" placement="right-start">
                                <ExclamationCircleOutlined v-show="mode === 'edit'" class="tip hint" />
                            </FTooltip>
                        </FFormItem>
                    </FForm>
                </div>
                <div v-show="mode === 'edit'" class="add-item">
                    <FButton type="link" class="link-button" @click="addAlarm">
                        <template #icon><PlusCircleOutlined /></template>{{$t('myProject.addAlarm')}}
                    </FButton>
                </div>
            </div>
        </FForm>
    </div>
</template>

<script setup>
import {
    computed, ref, inject, defineProps, defineExpose, defineEmits, onMounted, watch, provide,
} from 'vue';
import { useI18n, request } from '@fesjs/fes';
import {
    ExclamationCircleOutlined, CheckOutlined, PlusCircleOutlined, MinusCircleOutlined,
} from '@fesjs/fes-design/es/icon';

import {
    fetchTemplateList,
} from '@/pages/projects/api';
import ProjectTemplate from '@/pages/projects/template';
import useRuleBasicData from '@/hooks/useRuleBasicData';
import useExecutionParamsData from '@/components/rules/hook/useExecutionParamsData';
import { adaptDataToTreeSelect } from '@/common/utils';
import RuleConditionList from '@/components/rules/forms/RuleConditionList';
import dayjs from 'dayjs';
import { fetchTemplateDetail } from '@/pages/rules/template/api';
import {
    fetchDB,
} from '../../pages/projects/api';


// props: 数据源数据, 打开类型（新增、编辑、详情）
const props = defineProps({
    // 是否被嵌套
    isEmbed: {
        type: Boolean,
        default: false,
    },
    // 是否是批量
    isBatch: {
        type: Boolean,
        default: false,
    },
    // 是否展示批量规则组下拉框
    // showBatchRule: {
    //     type: Boolean,
    //     default: false,
    // },
    // 批量的时候传入可选数据
    // avaibleRules: {
    //     type: Array,
    //     default: [],
    // },
    mode: {
        type: String,
        default: '',
    },
    manual: {
        type: Boolean,
        default: false,
    },
    info: {
        type: Object,
        default: {},
    },
});
const emit = defineEmits(['update:info']);
const { t: $t } = useI18n();

const granularityMap = {
    'split_by:table': $t('executationConfig.tableGranularity'),
    'split_by:db': $t('executationConfig.dbGranularity'),
    'split_by:merge': $t('executationConfig.taskMerge'),
};
const isNormalProject = inject('isNormalProject');
const isTemplateCall = inject('isTemplateCall');
const editOrDisplay = inject('action');
// 创建执行参数模板时，可能无ruleType注入
const ruleType = inject('ruleType');
// 向校验条件组件注入，做适配
provide('ruleType', '0-0'); // 执行参数
provide('configuredRuleMetric', {
    value: -1,
    label: '',
    en_code: '',
});

const executionParams = computed({
    get() {
        console.log('get-executionParams', props.info);
        return props.info;
    },
    set(value) {
        emit('update:info', value);
    },
});

// const avaibleOptionsRules = computed(() => adaptDataToTreeSelect(props.avaibleRules));

/*
过滤条件显示隐藏条件说明
显示：批量执行参数、表规则组-执行参数、执行参数模板 隐藏：普通规则组详情页、普通规则组-执行参数
props.info.table_group true-表规则组 null/undefine/false-普通规则组
isNormalProject true-普通规则组
isTemplateCall true-执行参数模板
isBatch true-批量执行参数
*/

const {
    alarmEventList,
    dateSelectionMethodList,
    eliminateStrategyList,
    verifyTemplateList,
    engineParamsTypeList,
    executionVariableTypeList,
    executionVariableNameList,
    loadAlarmEvent,
    loadDateSelectionMethod,
    loadEliminateStrategy,
    loadVerifyTemplateList,
    computeInlineFormStyle,
    loadEngineParamsType,
    loadEngineParamsNameAndInit,
    loadExecutionVariableType,
    loadExecutionVariableName,
} = useExecutionParamsData();

const engineParamsTypeListArray = ref({});

const staticParameterTypeChange = async (data, id) => {
    data.parameter_name = '';
    data.parameter_value = '';
    if (Object.keys(engineParamsTypeListArray.value).findIndex(e => e === String(id)) === -1) {
        const engineParamsNameData = await loadEngineParamsNameAndInit(id);
        engineParamsTypeListArray.value[id] = engineParamsNameData;
    }
};

const setDefalutParamsValue = (data) => {
    data.parameter_value = engineParamsTypeListArray.value[String(data.parameter_type)].find(v => v.value === data.parameter_name)?.initial || '';
};

const isShowFilterParams = () => props.isBatch || isTemplateCall || (!isNormalProject && props.info.table_group);


// 计算不同模式下标签的位置
const computeLabelPosition = () => {
    const flag = props.mode === 'edit' || (editOrDisplay === 'edit' && !props.mode);
    if (flag) {
        return 'right';
    }
    return 'left';
};

// 计算不同模式下的标签宽度
const computeLabelWidth = () => {
    const position = computeLabelPosition();
    console.log('computeLabelWidth', position);
    if (position === 'right') {
        return 68;
    }
    return 108;
};

executionParams.value.rule_id_list = [];

const alarmLevelList = ref([
    { level: 1, label: 'CRITICAL' },
    { level: 2, label: 'MAJOR' },
    { level: 3, label: 'MINOR' },
    { level: 4, label: 'WARNING' },
    { level: 5, label: 'INFO' },
]);

const excutionParamsFormRef = ref(null);

const ruleconditionlistRef = ref([]);
// 校验模板相关
const getTemplateLabel = (v) => {
    const result = verifyTemplateList.value.filter(item => item.template_id === v);
    if (result) {
        return result[0]?.template_name;
    }
    return '';
};

// 告警参数动态表单
const getInitAlarmItem = () => {
    const item = {
        timestamp: Date.now(),
        alarm_event: '',
        alarm_level: '',
        alarm_receiver: '',
    };
    return item;
};

const alarmListRef = ref([]);

const deleteAlarm = (data) => {
    const index = executionParams.value.alarm_arguments_execution_parameters.findIndex(item => item.timestamp === data.timestamp);
    executionParams.value.alarm_arguments_execution_parameters.splice(index, 1);
};
const addAlarm = () => {
    executionParams.value.alarm_arguments_execution_parameters.push(getInitAlarmItem());
};

const alertRuleValidate = ref({
    alarm_event: [
        { type: 'number', required: true, message: $t('common.notEmpty') },
    ],
    alarm_level: [
        { type: 'number', required: true, message: $t('common.notEmpty') },
    ],
    alarm_receiver: [
        { required: true, message: $t('common.notEmpty') },
    ],
});

// 静态执行参数动态表单
const getInitStaticParamsItem = () => {
    const item = {
        timestamp: Date.now(),
        parameter_type: '',
        parameter_name: '',
        parameter_value: '',
    };
    return item;
};

const staticParamsListRef = ref([]);

const deleteStaticParams = (data) => {
    const index = executionParams.value.static_execution_parameters.findIndex(item => item.timestamp === data.timestamp);
    executionParams.value.static_execution_parameters.splice(index, 1);
};
const addStaticParams = () => {
    executionParams.value.static_execution_parameters.push(getInitStaticParamsItem());
};

const validateParameterNameRepeat = (rule, value) => {
    let count = 0;
    executionParams.value.static_execution_parameters.forEach((item) => {
        if (item.parameter_name === value) {
            count++;
        }
    });
    if (count > 1) {
        return false;
    }
    return true;
};

const staticParamsRuleValidate = ref({
    parameter_type: [
        {
            type: 'number', trigger: ['change', 'blur'], required: true, message: $t('common.notEmpty'),
        },
    ],
    parameter_name: [
        { required: true, message: $t('common.notEmpty'), trigger: ['change', 'blur'] },
        {
            validator: validateParameterNameRepeat,
            message: '参数名称不可重复设置',
        },
    ],
    parameter_value: [
        { required: true, message: $t('common.notEmpty') },
    ],
});
// 执行变量配置动态表单
const getInitVariableConfig = () => {
    const item = {
        timestamp: Date.now(),
        variable_type: '',
        variable_name: '',
        variable_value: '',
    };
    return item;
};

const variableConfigListRef = ref([]);

const deleteVariableConfig = (data) => {
    const index = executionParams.value.execution_management.findIndex(item => item.timestamp === data.timestamp);
    executionParams.value.execution_management.splice(index, 1);
};
const addVariableConfig = () => {
    executionParams.value.execution_management.push(getInitVariableConfig());
};
const clearVariableType = (data) => {
    data.variable_name = '';
    data.variable_value = '';
};
const clearVariableName = (data) => {
    data.variable_value = '';
};
const validateVariableNameRepeat = (rule, value) => {
    let count = 0;
    executionParams.value.execution_management.forEach((item) => {
        if (item.variable_name === value) {
            count++;
        }
    });
    if (count > 1) {
        return false;
    }
    return true;
};
const variableConfigRuleValidate = ref({
    variable_type: [
        {
            type: 'number', trigger: ['change', 'blur'], required: true, message: $t('common.notEmpty'),
        },
    ],
    variable_name: [
        { required: true, trigger: ['change', 'blur'], message: $t('common.notEmpty') },
        {
            validator: validateVariableNameRepeat,
            message: '变量名称不可重复设置',
        },
    ],
    variable_value: [
        { required: true, message: $t('common.notEmpty') },
    ],
});
// 执行参数表单
const excutionParamsRules = computed(() => ({
    cluster: [{
        required: executionParams.value.abnormal_data_storage,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    abnormal_proxy_user: [{
        required: executionParams.value.abnormal_data_storage,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    abnormal_database: [{
        required: executionParams.value.abnormal_data_storage,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    rule_id_list: [{
        required: true,
        message: $t('common.notEmpty'),
        type: 'array',
    }],
    engine_reuse: [{
        required: executionParams.value.advanced_execution,
        message: $t('common.notEmpty'),
        type: 'boolean',
    }],
    concurrency_granularity: [{
        required: executionParams.value.advanced_execution,
        message: $t('common.notEmpty'),
    }],
    dynamic_partitioning: [{
        required: executionParams.value.advanced_execution,
        message: $t('common.notEmpty'),
        type: 'boolean',
    }],
}));

const valid = async () => {
    try {
        const formValidArray = [...ruleconditionlistRef.value.map(item => item.valid()), ...variableConfigListRef.value.map(item => item.validate()), excutionParamsFormRef.value.validate()];
        const result = await Promise.all(formValidArray);
        console.log('excutionParams表单验证成功: ', result);
        return true;
    } catch (error) {
        console.log('excutionParams表单验证失败: ', error);
        return false;
    }
};
defineExpose({ valid });

const abnormalDatabaseList = ref([]);

const getDB = async () => {
    if (!executionParams.value.cluster) {
        return;
    }
    const params = {
        cluster_name: executionParams.value.cluster,
        proxy_user: executionParams.value.abnormal_proxy_user,
        page_size: 99999,
        source_type: '',
        start_index: 0,
    };
    const data = await fetchDB(params);
    console.log('数据库');
    console.log(data);
    // 判断是否需要清空原来选择的数据
    let isExistInList = false;
    for (let i = 0; i < data.data.length; i++) {
        const item = data.data[i];
        if (item.db_name === executionParams.value.abnormal_database) {
            isExistInList = true;
            break;
        }
    }
    if (!isExistInList) {
        executionParams.value.abnormal_database = '';
    }
    abnormalDatabaseList.value = data.data;
};

const injectClusterList = inject('clusterList');
const injectProxyUserList = inject('proxyUserList');

const {
    clusterList,
    loadClusterList,
    proxyUserList,
    updateProxyUserList,
} = useRuleBasicData();

// 动态表单初始化
watch(
    () => executionParams.value.specify_static_startup_param, (newValue) => {
        if (newValue && executionParams.value.static_execution_parameters.length === 0) {
            executionParams.value.static_execution_parameters.push(getInitStaticParamsItem());
        }
    },
);
watch(
    () => executionParams.value.alert, (newValue) => {
        if (newValue && executionParams.value.alarm_arguments_execution_parameters.length === 0) {
            executionParams.value.alarm_arguments_execution_parameters.push(getInitAlarmItem());
        }
    },
);
watch(
    () => executionParams.value.execution_variable, (newValue) => {
        if (newValue && executionParams.value.execution_management.length === 0) {
            executionParams.value.execution_management.push(getInitVariableConfig());
        }
    },
);
// 高级执行配置表单初始化
const initAdvanceForm = () => {
    executionParams.value.engine_reuse = true;
    executionParams.value.concurrency_granularity = 'split_by:merge';
    executionParams.value.dynamic_partitioning = false;
    executionParams.value.top_partition = '';
};
// 监控清空子级数据
let initAdvanceConfig = true;
const clearSubData = (v, type) => {
    if (!v || initAdvanceConfig) {
        if (type === 'filter') {
            executionParams.value.filter = '';
            executionParams.value.source_table_filter = '';
            executionParams.value.target_table_filter = '';
        } else if (type === 'dataStorage') {
            executionParams.value.cluster = '';
            executionParams.value.abnormal_proxy_user = '';
            executionParams.value.abnormal_database = '';
        } else if (type === 'staticParams') {
            executionParams.value.static_execution_parameters = [];
        } else if (type === 'alert') {
            executionParams.value.alarm_arguments_execution_parameters = [];
        } else if (type === 'variableConfig') {
            executionParams.value.execution_management = [];
        } else if (type === 'advanceConfig') {
            initAdvanceForm();
            initAdvanceConfig = false;
        }
    }
};
const handleConvertTimeArray = (array) => {
    const timeStrArray = [];
    array.forEach((e) => {
        timeStrArray.push(dayjs(e).format('YYYYMMDD'));
    });
    return timeStrArray.join(';');
};
const initEngineParamsNameArray = async () => {
    // eslint-disable-next-line no-restricted-syntax
    for (const parametersItem of executionParams.value.static_execution_parameters) {
        if (Object.keys(engineParamsTypeListArray.value).findIndex(e => e === String(parametersItem.parameter_type)) === -1) {
            engineParamsTypeListArray.value[parametersItem.parameter_type] = await loadEngineParamsNameAndInit(parametersItem.parameter_type);
        }
    }
};
onMounted(() => {
    loadAlarmEvent();
    loadDateSelectionMethod();
    loadEliminateStrategy();
    loadVerifyTemplateList();
    loadEngineParamsType();
    loadExecutionVariableType();
    loadExecutionVariableName();
    initEngineParamsNameArray();
    if (props.manual) {
        loadClusterList();
        updateProxyUserList();
    }
});

</script>
<style lang="less" scoped>
.rule-detail-form {
    .single-sub-form{
        padding: 16px;
        &.edit {
            padding: 16px 16px 0 16px;
        }
    }
    &.edit {
        .fes-form-item {
            margin-bottom: 16px;
            :deep(.fes-form-item-content){
                min-height: 22px;
            }
        }
    }
    &.isEmbed{
        background: #F7F7F8;
        .rule-detail-sub-form {
            background:#FFFFFF ;
            padding: 8px;
            margin-bottom: 8px;
            :deep(.fes-form-item-label) {
                justify-content: flex-start;
            }
        }
        &.edit {
            .rule-detail-sub-form {
                background-color: #F7F7F8;
                padding: 0 8px;
                :deep(.fes-form-item-label) {
                    justify-content: flex-end;
                }
                .single-sub-form {
                    .fes-form-item:last-child{
                        margin-bottom: 0px;
                    }
                }
            }
        }
    }
    .rule-detail-sub-form {
        &.inline {
            padding: 0px;
            background: #FFFFFF;
            .add-item {
                margin-left: -16px;
            }
            &.isEmbed {
                background: #F7F7F8;
            }
        }
        .inline-forms {
            border-radius: 4px;
            padding: 24px 16px 4px 16px;
            margin-bottom: 16px;
            .sub-title {
                padding-bottom: 12px;
                .left {
                    display: inline;
                }
                .right {
                    font-weight: 400;
                    display: inline;
                    float: right;
                    cursor: pointer;
                    .del-button {
                        color: #93949B;
                        margin-right: -16px;
                    }
                }
            }
            .rule-condition-execution {
                margin-bottom: 8px;
                &.edit{
                background: #FFFFFF;
                margin-bottom: 0px
                }
            }
        }
        .previewInlineForm {
            background: #F7F7F8;
            padding: 16px;
            &.isEmbed{
                background: #FFFFFF;
            }
        }
        .editInlineForm {
            border: 1px solid #F1F1F2;
            border-radius: 4px;
            &.isEmbed{
                background: #FFFFFF;
            }
        }
    }
}
.form-info.execution-params {
    .form {
        .item {
            &.checkbox {
                .edit.input {
                    position: relative;
                    width: 100%;
                    .error {
                        position: absolute;
                        font-size: 12px;
                        color: #F75F56;
                        display: none;
                        &.active {
                            display: block;
                        }
                    }
                }
                .left {
                    width: unset;
                }
                .wrap {
                    display: flex;
                    align-items: center;
                    .label {
                        margin-left: 22px;
                        margin-right: 16px;
                        white-space: nowrap;
                        .require {
                            vertical-align: text-top;
                            color: #F75F56;
                            margin-right: 4px;
                        }
                    }
                }
            }
        }
    }
}

.hint {
    display: inline-block;
    margin-left: 8px;
    color: #646670;
}

.select-hint {
    background: #F7F7F8;
    border-radius: 2px;
    padding:4px 8px;
    margin-bottom: 16px;
    &::after{
            content: '';
            display: block;
            height: 0;
            clear: both;
            zoom: 1;
    }
    span {
        color: #5384FF;
        text-align: justify;
        line-height: 22px;
        font-weight: 400;
        cursor: pointer;
    }
}

.rule-detail-form{
    .fes-form-item {
        margin-bottom: 8px;
    }
     .fes-input,.fes-select,.fes-textarea{
        // max-width: 315px;
        max-width: 100%;
    }
}
.execution-params-style{
    color:#0F1222;
    padding: 0 16px 8px 30px;
    &.isEmbed{
        padding: 8px 16px 8px 16px;
    }
}
:deep(.label-center) {
    height: auto;
    text-align: end;
}
:deep(.fes-checkbox-content){
    color:#63656f
}
.form-title{
    margin-bottom: 12px;
    font-weight: 600;
}
</style>
