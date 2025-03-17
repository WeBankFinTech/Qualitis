<template>
    <div class="wd-content project-detail">
        <ImportProjectByDetail v-if="showImport" v-model:show="showImport" :project="projectDetail" uploadRule type="detail" @uploadSucess="updateGit" />
        <EXportProjectByDetail v-if="showExport" v-model:show="showExport" :project="projectDetail" downloadRule type="detail"></EXportProjectByDetail>
        <div class="wd-project-header">
            <LeftOutlined class="back" @click="backToProjectList" />
            <div class="name">{{$t('addGroupTechniqueRule.projectDetails')}}</div>
        </div>
        <basicInfo v-if="projectDetail.project_id" v-model:project="projectDetail" @upateProject="getProjectDetail"></basicInfo>
        <div class="wd-content-body">
            <ul class="wd-body-menus">
                <li class="wd-body-menu-item" @click="showTaskRecordPanel">{{$t('myProject.record')}}</li>
                <!-- <li class="wd-body-menu-item" @click="showExecuteParamsTemplate(projectId)">{{$t('myProject.executeParamsTemplate')}}</li> -->
                <li class="wd-body-menu-item" @click="showTemplateDrawer">{{$t('myProject.executeParamsTemplate')}}</li>
                <li v-if="overseasVersion === 'false'" class="wd-body-menu-item" @click="showApplicationInfoTemplateDrawer = true">{{$t('myProject.applicationInfoTemplate')}}</li>
            </ul>
            <h6 class="wd-body-title">{{$t('myProject.rules')}}</h6>
            <!-- 规则筛选 -->
            <ruleFilter :isWorkflowProject="workflowProject" @filtrateRules="filtrateRules" />
            <div class="rules-operate-menus">
                <!-- 导入规则 -->
                <!-- <input
                    v-show="false"
                    :ref="el => { if (el) fileInputRefs = el }"
                    type="file"
                    class="btn-file-input"
                    accept=".xlsx"
                    @change="importRules(file)"
                /> -->
                <FSpace :size="[16, 16]">
                    <template v-if="!isExporting && !isEnable">
                        <template v-if="!workflowProject">
                            <FDropdown trigger="hover" :options="createOptions" @click="selectCreate">
                                <FButton type="primary"><PlusOutlined />{{$t('myProject.create')}}</FButton>
                            </FDropdown>
                            <FDropdown trigger="hover" :options="createBatchOptions" @click="selectCreateBatch">
                                <FButton type="primary"><PlusOutlined />{{$t('myProject.createBatch')}}</FButton>
                            </FDropdown>
                            <FButton type="primary" @click="excuteProjectRules"><fes-icon type="execute" style="margin-right: 4px;" />{{$t('myProject.run')}}</FButton>
                            <FTooltip placement="top" popperClass="range-text-wrap">
                                <div>
                                    <FButton type="default" class="button" style="margin-right: 16px" @click="toTask(1)">
                                        <img class="button-icon" src="@/assets/images/icons/scheduleIcon.svg" style="margin-right: 4px; height: 14px; width:14px;" />
                                        {{$t('myProject.associateScheduledTask')}}
                                    </FButton>
                                    <FButton v-if="overseasVersion === 'false'" type="default" class="button" @click="toTask(2)">
                                        <img class="button-icon" src="@/assets/images/icons/timingIcon.svg" style="margin-right: 4px; height: 14px; width:14px;" />{{$t('myProject.publishScheduledTask')}}
                                    </FButton>
                                </div>
                                <template #content>
                                    <div>{{$t('_.当前租户下可支持的最大峰值提交总量：')}}{{peakNum}}</div>
                                </template>
                            </FTooltip>
                        </template>
                        <!-- 数据告警规则按钮 -->
                        <FButton v-if="workflowProject" @click="showDataAlarmRuleDrawer">
                            <fes-icon style="margin-right: 4px;" type="alarm" /> {{$t('myProject.dataAlarmRule')}}
                        </FButton>
                        <FButton type="default" class="button" @click="toggleExecuteParams">
                            <img class="button-icon" src="@/assets/images/icons/config.svg" style="margin-right: 4px; height: 14px; width:14px;" />{{$t('common.parameterExeBatchSet')}}
                        </FButton>

                        <FDropdown trigger="hover" :options="moreMenus" @click="projectOperationActionHandler">
                            <FButton><MoreCircleOutlined />{{$t('myProject.more')}}</FButton>
                        </FDropdown>
                    </template>
                    <!-- 批量启用操作 -->
                    <template v-else-if="isEnable">
                        <FButton :disabled="isEnabling" type="primary" class="btn-item" @click="changeEnable">{{enableTurnon ? $t('myProject.confirmEnable') : $t('myProject.confirmDisenable')}}</FButton>
                        <FButton key="enableCancel" :disabled="isEnabling" type="default" @click="enableCancel">{{$t('_.取消')}}</FButton>
                    </template>
                    <template v-else>
                        <FButton :disabled="isDownloading" type="primary" class="btn-item" @click="exportProject">{{$t('myProject.confirmExport')}}</FButton>
                        <FButton key="toggleExport" :disabled="isDownloading" type="default" @click="toggleExport">{{$t('common.cancel')}}</FButton>
                    </template>
                </FSpace>
            </div>
            <!-- <div v-for="(val, key, index) in projectRules" :key="index"> -->
            <div v-if="mergeCells">
                <!-- <p class="rule-table-name">{{$t('myProject.ruleGroupName')}}: <span class="rule-table-name-value" @click="toProjectRuleDetail({ row: val[0], column: { props: {}}})">{{val[0].rule_group_name}}</span></p> -->

                <f-table ref="ruleTableRef" :data="projectRuleList" rowKey="rule_id" :verticalLine="true" bordered :span-method="objectSpanMethod" @selectionChange="mergeProjectSelectionChange">
                    <f-table-column :visible="isExporting || isEnable" type="selection" :width="32" />
                    <f-table-column prop="rule_group_name" :label="$t('_.规则组名称')" :width="160">
                        <template #default="{ row }">
                            <clipboard isLink :val="row.rule_group_name" @clickLink="toProjectRuleDetail({ row, column: { props: {}}})" />
                        </template>
                    </f-table-column>
                    <f-table-column :visible="checkTColShow('rule_name')" prop="rule_name" :label="$t('common.ruleEnName')" :width="160">
                        <template #default="{ row = {}}">
                            <!-- <span class="a-link" href="javascript:void(0);" @click="toProjectRuleDetail({ row, column: { props: {}}})">{{row.rule_name || '--'}}</span> -->
                            <clipboard isLink :val="row.rule_name" @clickLink="toProjectRuleDetail({ row, column: { props: {}}})" />
                        </template>
                    </f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" :visible="checkTColShow('rule_cn_name')" prop="rule_cn_name" :label="$t('common.ruleCnName')" :width="160" ellipsis></f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" :visible="checkTColShow('rule_id')" prop="rule_id" :label="$t('tableThead.ruleId')" :width="92" ellipsis></f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" :visible="checkTColShow('template_name')" prop="template_name" :label="$t('tableThead.templateName')" :width="140" ellipsis></f-table-column>
                    <f-table-column :visible="checkTColShow('rule_type')" prop="rule_type" :label="$t('common.ruleType')" :formatter="formatter.rule" :width="102" ellipsis></f-table-column>
                    <f-table-column v-slot="{ row }" :visible="checkTColShow('rule_enable')" prop="rule_enable" :label="$t('common.ruleEnable')" :width="102" ellipsis><span :class="row.rule_enable ? 'enable' : 'disabled'">{{row.rule_enable ? $t('_.启用') : $t('_.禁用')}}</span></f-table-column>
                    <f-table-column :visible="checkTColShow('cluster')" prop="cluster_name" :label="$t('common.cluster')" :formatter="formatterEmptyValue" :width="140" ellipsis></f-table-column>
                    <f-table-column #default="{ row = {}}" :visible="checkTColShow('datasource')" :label="$t('tableThead.databaseAndTable')" :width="200" ellipsis>
                        <template v-if="row.datasource.length === 0">
                            - -
                        </template>
                        <template v-for="(ds,index) in row.datasource" v-else :key="index">
                            {{$t('common.databaseList')}}: {{ds.db || '- -'}} {{$t('common.tableLibst')}}: {{ds.table || '- -'}} <br>
                        </template>
                    </f-table-column>
                    <f-table-column :visible="checkTColShow('filter')" prop="filter" :label="$t('common.condition')" :formatter="formatter.condition" :width="140" ellipsis></f-table-column>
                    <f-table-column v-if="workflowProject" :visible="checkTColShow('work_flow_space')" prop="work_flow_space" :label="$t('myProject.workflowSpace')" :formatter="formatterEmptyValue" :width="140" ellipsis></f-table-column>
                    <f-table-column v-if="workflowProject" :visible="checkTColShow('work_flow_project')" prop="work_flow_project" :label="$t('myProject.workflowProject')" :formatter="formatterEmptyValue" :width="160" ellipsis></f-table-column>
                    <f-table-column v-if="workflowProject" :visible="checkTColShow('work_flow_name')" prop="work_flow_name" :label="$t('myProject.workflowName')" :formatter="formatterEmptyValue" :width="160" ellipsis></f-table-column>
                    <f-table-column v-if="workflowProject" :visible="checkTColShow('node_name')" prop="node_name" :label="$t('myProject.workflowTaks')" :formatter="formatterEmptyValue" :width="160" ellipsis></f-table-column>
                    <f-table-column :visible="checkTColShow('create_user')" prop="create_user" :label="$t('myProject.creator')" :formatter="formatterEmptyValue" :width="120" ellipsis></f-table-column>
                    <f-table-column :visible="checkTColShow('create_time')" prop="create_time" :label="$t('myProject.createTime')" :formatter="formatterEmptyValue" :width="180" ellipsis></f-table-column>
                    <f-table-column :visible="checkTColShow('modify_user')" prop="modify_user" :label="$t('myProject.modifier')" :formatter="formatterEmptyValue" :width="120" ellipsis></f-table-column>
                    <f-table-column :visible="checkTColShow('modify_time')" prop="modify_time" :label="$t('myProject.modifyTime')" :formatter="formatterEmptyValue" :width="180" ellipsis></f-table-column>
                    <f-table-column
                        v-if="!workflowProject"
                        v-slot="{ row }"
                        prop="operate"
                        :label="$t('common.operate')"
                        :formatter="formatter.enable"
                        :width="70"
                        fixed="right"
                    >
                        <FDropdown :options="getOpertaions(row)" @click="handleOperationCick($event, row)">
                            <FButton type="text" style="margin-left: -5px"><MoreCircleOutlined /></FButton>
                        </FDropdown>
                    </f-table-column>
                </f-table>
            </div>
            <div v-else>
                <div v-for="(val, key, index) in projectRules" :key="index">
                    <p class="rule-table-name">{{$t('myProject.ruleGroupName')}}: <span class="rule-table-name-value" @click="toProjectRuleDetail({ row: val[0], column: { props: {}}})">{{val[0].rule_group_name}}</span></p>

                    <f-table :ref="ruleRefs[index]" :data="val" rowKey="rule_id" @selectionChange="(selectData) => projectSelectionChange(index, key, selectData)">
                        <f-table-column :visible="isExporting || isEnable" type="selection" :width="32" />
                        <!-- <f-table-column prop="rule_group_name" label="规则组名称" :width="160" ellipsis>
                        <template #default="{ row = {}}">
                            <span class="a-link" href="javascript:void(0);" @click="toProjectRuleDetail({ row, column: { props: {}}})">{{row.rule_group_name || '--'}}</span>
                        </template>
                    </f-table-column> -->
                        <f-table-column :visible="checkTColShow('rule_name')" prop="rule_name" :label="$t('common.ruleEnName')" :width="160" ellipsis>
                            <template #default="{ row = {}}">
                                <span class="a-link" href="javascript:void(0);" @click="toProjectRuleDetail({ row, column: { props: {}}})">{{row.rule_name || '--'}}</span>
                            </template>
                        </f-table-column>
                        <f-table-column :formatter="formatterEmptyValue" :visible="checkTColShow('rule_cn_name')" prop="rule_cn_name" :label="$t('common.ruleCnName')" :width="160" ellipsis></f-table-column>
                        <f-table-column :formatter="formatterEmptyValue" :visible="checkTColShow('rule_id')" prop="rule_id" :label="$t('tableThead.ruleId')" :width="92" ellipsis></f-table-column>
                        <f-table-column :formatter="formatterEmptyValue" :visible="checkTColShow('template_name')" prop="template_name" :label="$t('tableThead.templateName')" :width="140" ellipsis></f-table-column>
                        <f-table-column :visible="checkTColShow('rule_type')" prop="rule_type" :label="$t('common.ruleType')" :formatter="formatter.rule" :width="102" ellipsis></f-table-column>
                        <f-table-column v-slot="{ row }" :visible="checkTColShow('rule_enable')" prop="rule_enable" :label="$t('common.ruleEnable')" :width="102" ellipsis><span :class="row.rule_enable ? 'enable' : 'disabled'">{{row.rule_enable ? $t('_.启用') : $t('_.禁用')}}</span></f-table-column>
                        <f-table-column :visible="checkTColShow('cluster')" prop="cluster_name" :label="$t('common.cluster')" :formatter="formatterEmptyValue" :width="140" ellipsis></f-table-column>
                        <f-table-column :visible="checkTColShow('datasource')" :label="$t('tableThead.databaseAndTable')" :width="200" ellipsis>
                            <!-- <template v-for="(ds,index) in row.datasource" :key="index">
                            {{$t('common.databaseList')}}: {{ds.db || '- -'}} {{$t('common.tableLibst')}}: {{ds.table || '- -'}} <br>
                        </template> -->
                            <template #default="{ row = {}}">
                                <span v-if="row.datasource.length === 0">- -</span>
                                <div v-else>
                                    <div v-for="(ds,index) in row.datasource" :key="index">
                                        {{$t('common.databaseList')}}: {{ds.db || '- -'}} {{$t('common.tableLibst')}}: {{ds.table || '- -'}} <br>
                                    </div>
                                </div>
                            </template>
                        </f-table-column>
                        <f-table-column :visible="checkTColShow('filter')" prop="filter" :label="$t('common.condition')" :formatter="formatter.condition" :width="140" ellipsis></f-table-column>
                        <f-table-column v-if="workflowProject" :visible="checkTColShow('work_flow_space')" prop="work_flow_space" :label="$t('myProject.workflowSpace')" :formatter="formatterEmptyValue" :width="140" ellipsis></f-table-column>
                        <f-table-column v-if="workflowProject" :visible="checkTColShow('work_flow_project')" prop="work_flow_project" :label="$t('myProject.workflowProject')" :formatter="formatterEmptyValue" :width="160" ellipsis></f-table-column>
                        <f-table-column v-if="workflowProject" :visible="checkTColShow('work_flow_name')" prop="work_flow_name" :label="$t('myProject.workflowName')" :formatter="formatterEmptyValue" :width="160" ellipsis></f-table-column>
                        <f-table-column v-if="workflowProject" :visible="checkTColShow('node_name')" prop="node_name" :label="$t('myProject.workflowTaks')" :formatter="formatterEmptyValue" :width="160" ellipsis></f-table-column>
                        <f-table-column :visible="checkTColShow('create_user')" prop="create_user" :label="$t('myProject.creator')" :formatter="formatterEmptyValue" :width="120" ellipsis></f-table-column>
                        <f-table-column :visible="checkTColShow('create_time')" prop="create_time" :label="$t('myProject.createTime')" :formatter="formatterEmptyValue" :width="180" ellipsis></f-table-column>
                        <f-table-column :visible="checkTColShow('modify_user')" prop="modify_user" :label="$t('myProject.modifier')" :formatter="formatterEmptyValue" :width="120" ellipsis></f-table-column>
                        <f-table-column :visible="checkTColShow('modify_time')" prop="modify_time" :label="$t('myProject.modifyTime')" :formatter="formatterEmptyValue" :width="180" ellipsis></f-table-column>
                        <f-table-column
                            v-if="!workflowProject"
                            v-slot="{ row }"
                            prop="operate"
                            :label="$t('common.operate')"
                            :formatter="formatter.enable"
                            :width="70"
                            fixed="right"
                        >
                            <!-- <ul class="wd-table-operate-btns">
                                <li class="btn-item" @click="projectOperationBtnEnable(row)">{{!row.rule_enable ? $t('_.启用') : $t('_.禁用')}} </li>
                                <li
                                    v-for="item in ruleOperationBtns"
                                    :key="item.value"
                                    class="btn-item"
                                    :class="{ red: item.isDelete }"
                                    @click="projectOperationBtnHandler(item, row)"
                                >
                                    {{item.label}}
                                </li>
                            </ul> -->
                            <FDropdown :options="getOpertaions(row)" @click="handleOperationCick($event, row)">
                                <FButton type="text" style="margin-left: -5px"><MoreCircleOutlined /></FButton>
                            </FDropdown>
                        </f-table-column>
                    </f-table>
                </div>
            </div>
            <div class="table-pagination-container">
                <FPagination
                    v-model:currentPage="pagination.current"
                    v-model:pageSize="pagination.size"
                    show-size-changer
                    show-total
                    :total-count="pagination.total"
                    @change="pageChange"
                    @pageSizeChange="pageChange"
                ></FPagination>
            </div>
        </div>

        <!-- 显示执行记录 -->
        <FDrawer v-model:show="showTaskRecord" :title="$t('myProject.record')" width="800px" @cancel="resetTaskRecords">
            <div class="page-header-condition">
                <div class="condition-item">
                    <span class="condition-label">{{$t('myProject.taskEndTime')}}</span>
                    <FDatePicker
                        v-model="taskRecordsQuery.taskEndTime"
                        type="datetimerange"
                        clearable
                        format="yyyy-MM-dd HH:mm:ss"
                        style="width: 380px;"
                        :placeholder="$t('_.请选择')"
                    >
                    </FDatePicker>
                </div>
                <div class="condition-item">
                    <FSpace size="middle">
                        <FButton type="primary" @click="searchGetProjectTaskRecord">{{$t('common.query')}}</FButton>
                        <FButton @click="resetTaskRecordsQuery">{{$t('common.reset')}}</FButton>
                    </FSpace>
                </div>
            </div>
            <f-table :data="taskRecords || []">
                <f-table-column v-slot="{ row }" prop="application_id" :label="$t('myProject.taskNumber')" :width="160" ellipsis>
                    <span class="a-link" @click="toTaskQuery(row)">{{row.application_id}}</span>
                </f-table-column>
                <f-table-column prop="execute_user" :label="$t('myProject.excuteUser')" :width="120" ellipsis></f-table-column>
                <f-table-column v-slot="{ row }" prop="status" :label="$t('myProject.excuteStatus')" :width="130" ellipsis>
                    <div :style="{ color: tempStatusList[row.status] ? tempStatusList[row.status].color : '' }">
                        {{tempStatusList[row.status] ? tempStatusList[row.status].label : row.status}}
                    </div>
                </f-table-column>
                <f-table-column prop="start_time" :label="$t('myProject.taskStartTime')" :width="180" ellipsis></f-table-column>
                <f-table-column prop="end_time" :label="$t('myProject.taskEndTime')" :width="180" ellipsis></f-table-column>
            </f-table>
            <div class="table-pagination-container">
                <FPagination
                    v-model:currentPage="taskRecordsPagination.current"
                    v-model:pageSize="taskRecordsPagination.size"
                    show-size-changer
                    show-total
                    :total-count="taskRecordsPagination.total"
                    @change="getProjectTaskRecord(taskRecordsQuery)"
                    @pageSizeChange="getProjectTaskRecord(taskRecordsQuery)"
                ></FPagination>
            </div>
        </FDrawer>

        <!--设置表格-->
        <BTableHeaderConfig
            v-model:headers="projectTableHeaders"
            v-model:show="showProjectTableColConfig"
            :originHeaders="workflowProject ? originWorkflowProjectHeaders : originProjectHeaders"
            :type="workflowProject ? 'workflow_project_rule' : 'project_rule'"
        />
        <!-- 任务执行 -->
        <ExecRulePanel
            v-model:show="showExecutation"
            v-model="executationConfig"
            :list="executationRules"
            @ok="excuteRuleInProjectDetail" />
        <!-- (批量)执行参数选择 -->
        <FDrawer
            v-model:show="showExecuteParamsDrawer"
            :title="editParamsConfig.batch ? $t('common.parameterExeBatchSet') : $t('common.ruleExecutionParameters')"
            displayDirective="if"
            :footer="true"
            width="50%"
            contentClass="template-drawer"
            @cancel="clearSelectedRow"
        >
            <template #footer>
                <FButton type="primary" style="margin-right: 16px" @click="updateRowParams">{{$t('common.save')}}</FButton>
                <FButton @click="clearSelectedRow">{{$t('common.cancel')}}</FButton>
            </template>
            <FForm ref="executionParamsTemplateRef" labelPosition="right" :labelWidth="80" :model="editParamsConfig.params" :rules="excutionParamsRules">
                <FFormItem v-if="editParamsConfig.batch" :label="$t('common.technicalRegulation')" prop="rule_id_list">
                    <FSelectTree
                        v-model="editParamsConfig.params.rule_id_list"
                        multiple
                        filterable
                        :data="adaptDataToTreeSelect(projectAllRules)"
                        :clearable="false"
                        cascade
                        virtualList
                        checkStrictly="parent"
                        collapseTags
                        :collapseTagsLimit="2"
                        :placeholder="$t('common.pleaseSelect')"
                    >
                    </FSelectTree>
                </FFormItem>
                <FFormItem :label="$t('_.执行参数')" class="rule-valid-style">
                    <ProjectTemplate ref="templateRef" :showHeader="false" :isEmbed="true" :curExeName="editParamsConfig.params.execution_parameters_name" class="project-template-style"></ProjectTemplate>
                </FFormItem>
            </FForm>
        </FDrawer>
        <!-- 执行参数模版操作 -->
        <FDrawer
            v-model:show="showExecuteParamsTemplateDrawer"
            :title="$t('myProject.template')"
            displayDirective="if"
            width="50%"
        >
            <ProjectTemplate ref="templateRef" :showHeader="false" :isEmbed="true" :isOperate="true" class="project-template-style"></ProjectTemplate>
        </FDrawer>
        <!-- 应用信息模版 -->
        <FDrawer
            v-model:show="showApplicationInfoTemplateDrawer"
            :title="$t('myProject.applicationInfoTemplate')"
            displayDirective="if"
            width="50%"
        >
            <AppInfoTemplate :subSystemList="subSystemList"></AppInfoTemplate>
        </FDrawer>

        <!-- 数据告警规则抽屉 -->
        <DataAlarmRuleDrawer
            v-if="workflowProject"
            v-model:show="dataAlarmRuleDrawerShow"
        />
    </div>
</template>
<script setup>

import {
    ref, computed, provide, onMounted, toRaw, watch, reactive, nextTick,
} from 'vue';
import {
    useI18n, useRouter, useRoute, request as FRequest,
} from '@fesjs/fes';
import {
    LeftOutlined, DownloadOutlined, UploadOutlined, MoreCircleOutlined, PlusOutlined, CheckOutlined, FilterOutlined,
} from '@fesjs/fes-design/es/icon';
import { BTableHeaderConfig } from '@fesjs/traction-widget';
import {
    FMessage, FModal, FAlert, FSpace, FButton, FTooltip,
} from '@fesjs/fes-design';
import {
    SINGLE_TABLE_RULE_FLAG,
    CROSS_TABLE_VERIFICATION_FULLY_RULE_FLAG,
    CUSTOMIZATION_RULE_FLAG,
    CROSS_DB_VERIFICATION_FULLY_RULE_FLAG,
    FILE_VERIFICATION_RULE_FLAG,
    SQL_VERIFICATION_RULE_FLAG,
    isIE,
} from '@/assets/js/utils';
import ExecRulePanel from '@/components/ExecutionConfig/ExecRulePanel';
import ExecutionParams from '@/components/rules/ExecutionParams';
import ProjectTemplate from '@/pages/projects/template';
import AppInfoTemplate from '@/pages/projects/appInfoTemplate';
import { RULE_TYPE_MAP, MAX_PAGE_SIZE, CONDITIONBUTTONSPACE } from '@/assets/js/const';
import { adaptDataToTreeSelect, getFlatDataFormTreeSelect, formatterEmptyValue } from '@/common/utils';
import { cloneDeep } from 'lodash-es';
import useTemplateSelector from '@/components/rules/hook/useTemplateSelector';
import useTableHeaderConfig from '@/hooks/useTableHeaderConfig';
import EXportProjectByDetail from '@/pages/projects/components/ImExport/exportProjectByDetail';
import ImportProjectByDetail from '@/pages/projects/components/ImExport/importProjectByDetail';
import useDataSource from '@/pages/projects/hooks/useDataSource';
import clipboard from '@/components/clipboard';
import useProjectDetail from './hooks/useProjectDetail';
import useExecutation from './hooks/useExecutation';
import useProjectTaskRecord from './hooks/useProjectTaskRecord';
import useExport from './hooks/useExport';
import useImport from './hooks/useImport';
import basicInfo from './components/basicInfo';
import ruleFilter from './components/ruleFilter';
import ParamsTemplate from './template';
import {
    fetchModifyRule, fetchTemplateList, fetchRuleTemplate,
} from './api';
import DataAlarmRuleDrawer from './components/dataAlarmRuleDrawer.vue';

const { t: $t } = useI18n();
const overseasVersion = sessionStorage.getItem('overseas_external_version');
const subSystemList = ref([]);
// 获取子系统中文名
const getSystemNameNew = (data, tr) => tr.full_cn_name || tr.subSystemFullCnName || data;
// 获取子系统列表
const getSubSystemInfo = async () => {
    try {
        const timestamp = new Date().getTime();
        const res = await FRequest(`/api/v1/projector/meta_data/subSystemInfo?timestamp=${timestamp}`, {}, 'post');
        const list = res || [];
        subSystemList.value = list.map((item) => {
            const cnName = getSystemNameNew(item.subSystemId, item);
            return Object.assign({}, item, {
                subSystemName: cnName,
                enName: item.subSystemName,
                cnName,
                value: String(item.subSystemId),
                label: item.subSystemName,
            });
        });
    } catch (error) {
        console.log('error: ', error);
    }
};

const tempStatusList = ref({
    0: {
        label: $t('taskQuery.all'),
        color: '#444',
    },
    1: {
        label: $t('taskQuery.submitted'),
        color: '#00CB91',
    },
    3: {
        label: $t('taskQuery.inTheOperation'),
        color: '#00CB91',
    },
    4: {
        label: $t('common.byCheck'),
        color: '#00CB91',
    },
    7: {
        label: $t('taskQuery.failure'),
        color: '#FF4D4F',
    },
    8: {
        label: $t('common.failCheck'),
        color: '#FF9540',
    },
    9: {
        label: $t('taskQuery.tasksFailed'),
        color: '#FF4D4F',
    },
    10: {
        label: $t('taskQuery.tasksSuccessfully'),
        color: '#00CB91',
    },
    11: {
        label: $t('taskQuery.parameterError'),
        color: '#FF4D4F',
    },
    12: {
        label: $t('taskQuery.submitPending'),
        color: '#FF4D4F',
    },
});

const router = useRouter();
const route = useRoute();
console.log(route.query);
provide('action', 'edit');

// 项目详情页返回永远指定到项目列表页
const backToProjectList = () => {
    const backUrl = window.history?.state?.back;
    if (backUrl && backUrl.includes('/projects?page')) {
        router.back();
    } else {
        const tab = route.query?.workflowProject ? 'tab=2' : 'tab=1';
        router.replace(`/projects?${tab}`);
    }
    // if (window.history?.state?.back) {
    //     // 从项目里正常跳转进来
    //     router.back();
    // } else {
    //     // 复制url直接进来
    //     router.replace('/projects');
    // }
};
const mergeCells = ref(true);
const projectId = route.query.projectId;
const workflowProject = !!route.query.workflowProject;
const {
    projectDetail,
    projectRules,
    filrateCondition,
    projectAllRules,
    pagination,
    pageChange,
    getProjectDetail,
    getProjectAllRules,
} = useProjectDetail(projectId);

const taskRecordsQuery = ref({});
const {
    taskRecords,
    taskRecordsPagination,
    getProjectTaskRecord,
    resetTaskRecords,
} = useProjectTaskRecord(projectId);

const searchGetProjectTaskRecord = () => {
    taskRecordsPagination.current = 1;
    getProjectTaskRecord(taskRecordsQuery.value);
};

const resetTaskRecordsQuery = () => {
    resetTaskRecords();
    taskRecordsQuery.value = {};
    getProjectTaskRecord();
};

const showTaskRecord = ref(false);
const showTaskRecordPanel = () => {
    showTaskRecord.value = true;
    getProjectTaskRecord();
};
const ruleTableRef = ref(null);
const ruleRefs = ref([]);
const projectRuleList = ref([]);
let colIndex = 0; // 记录合并单元格rowindex
// 导出规则|修改规则
let projectSelection = [];
let enableSelection = [];
// 是否点击复选框渲染
let isSelectCheckbox = false;

function clear() {
    console.log(ruleRefs.value, '🚀🚀');
    projectSelection = [];
    enableSelection = [];
    if (mergeCells.value) {
        ruleTableRef.value.clearSelection();
    } else {
        for (let i = 0; i < Object.keys(projectRules.value).length; i++) {
            ruleRefs.value[i].value[0].clearSelection();
        }
    }
}
const showExecuteParamsTemplateDrawer = ref(false);
const showApplicationInfoTemplateDrawer = ref(false);
const showTemplateDrawer = () => {
    showExecuteParamsTemplateDrawer.value = true;
};
// const showExecuteParamsTemplate = (id) => {
//     if (!id) return;
//     router.push({
//         path: '/projects/template',
//         query: {
//             projectId: id,
//             isBatch: true,
//             isOperate: true,
//         },
//     });
// };

const dataAlarmRuleDrawerShow = ref(false);

const showDataAlarmRuleDrawer = () => {
    dataAlarmRuleDrawerShow.value = true;
};

const {
    showExecutation,
    executationConfig,
    executationRules,
    executeTaskPre,
    excuteRuleInProjectDetail,
} = useExecutation(projectDetail.value);

// 执行规则
const allRuleList = [];
const excuteProjectRules = async () => {
    await getProjectAllRules();
    projectDetail.value.rule_details = projectAllRules;
    executeTaskPre(projectDetail.value);
};

// 新建按钮 规则类型
const createOptions = computed(() => [
    {
        label: $t('_.创建普通规则组'),
        value: SINGLE_TABLE_RULE_FLAG,
    },
    {
        label: $t('_.创建单表规则组'),
        value: 'TABLE_GROUP',
    },
]);

const createBatchOptions = computed(() => [
    {
        label: $t('_.基于规则模版创建'),
        value: 'CREATE_BY_RULE_TEMPLATE',
    },
    {
        label: $t('_.库一致性对比配置'),
        value: 'CROSS_DB_VERIFICATION_FULLY_RULE_FLAG',
    },
]);

const getOpertaions = (row) => {
    const operations = [
        {
            value: 'enable',
            label: row.rule_enable ? $t('_.禁用') : $t('_.启用'),
        },
        {
            label: $t('common.executeParams'),
            value: 'executeTask',
        },
        {
            label: $t('_.复制'),
            value: 'copy',
            disabled: row.table_group,
        },
        {
            label: $t('common.delete'),
            value: 'deleteProject',
            isDelete: true,
        },
    ];
    return operations;
};
const ruleOperationBtns = [
    {
        label: $t('common.executeParams'),
        value: 'executeTask',
    },
    {
        label: $t('_.复制'),
        value: 'copy',
    },
    {
        label: $t('common.delete'),
        value: 'deleteProject',
        isDelete: true,
    },
];

const formatter = ref({
    rule: ({ row }) => {
        const templateType = ['', $t('ruleTemplatelist.singleTableType'), $t('ruleTemplatelist.singleOrMultipleIndexType'), $t('common.crossTableType'), $t('common.fileType'), $t('verifyFailData.acrossCluster')];
        const ruleType = row.cluster_num === 2 && row.rule_type === 3 ? 5 : row.rule_type;
        return templateType[ruleType] || '--';
    },
    condition: ({ row: { filter } }) => {
        if (filter) {
            const str = filter.join(' ');
            return str || '--';
        }
        return '--';
    },
});

const toProjectRuleDetail = ({ row: { rule_group_id: ruleGroupId, rule_id: ruleId, table_group: tableGroup }, column }) => {
    if (column.props.prop !== 'operate' && column.props.type !== 'selection') {
        router.push(`/projects/${tableGroup ? 'tableGroupRules' : 'rules'}?ruleGroupId=${ruleGroupId}&id=${ruleId}&workflowProject=${workflowProject}&projectId=${projectId}`);
    }
};

const toTaskQuery = (row) => {
    router.push(`/tasks?application_id=${row.application_id}`);
};

const originProjectHeaders = [
    { prop: 'rule_name', label: $t('common.ruleEnName') },
    { prop: 'rule_cn_name', label: $t('common.ruleCnName') },
    { prop: 'rule_id', label: $t('tableThead.ruleId') },
    { prop: 'template_name', label: $t('tableThead.templateName') },
    { prop: 'rule_type', label: $t('common.ruleType') },
    { prop: 'rule_enable', label: $t('common.ruleEnable') },
    { prop: 'cluster', label: $t('common.cluster') },
    { prop: 'datasource', label: $t('tableThead.databaseAndTable') },
    { prop: 'filter', label: $t('common.condition') },
    { prop: 'create_user', label: $t('myProject.creator'), hidden: true },
    { prop: 'create_time', label: $t('myProject.createTime'), hidden: true },
    { prop: 'modify_user', label: $t('myProject.modifier'), hidden: true },
    { prop: 'modify_time', label: $t('myProject.modifyTime'), hidden: true },
];

const originWorkflowProjectHeaders = [
    { prop: 'rule_name', label: $t('common.ruleEnName') },
    { prop: 'rule_cn_name', label: $t('common.ruleCnName') },
    { prop: 'rule_id', label: $t('tableThead.ruleId') },
    { prop: 'template_name', label: $t('tableThead.templateName') },
    { prop: 'rule_type', label: $t('common.ruleType') },
    { prop: 'rule_enable', label: $t('common.ruleEnable') },
    { prop: 'cluster', label: $t('common.cluster') },
    { prop: 'datasource', label: $t('tableThead.databaseAndTable') },
    { prop: 'filter', label: $t('common.condition') },
    { prop: 'work_flow_space', label: $t('myProject.workflowSpace'), hidden: true },
    { prop: 'work_flow_project', label: $t('myProject.workflowProject'), hidden: true },
    { prop: 'work_flow_name', label: $t('myProject.workflowName'), hidden: true },
    { prop: 'node_name', label: $t('myProject.workflowTaks'), hidden: true },
    { prop: 'create_user', label: $t('myProject.creator'), hidden: true },
    { prop: 'create_time', label: $t('myProject.createTime'), hidden: true },
    { prop: 'modify_user', label: $t('myProject.modifier'), hidden: true },
    { prop: 'modify_time', label: $t('myProject.modifyTime'), hidden: true },
];
// 表头设置
const {
    checkTColShow,
    toggleTColConfig,
    tableHeaders: projectTableHeaders,
    showTableHeaderConfig: showProjectTableColConfig,
} = useTableHeaderConfig();

const moreMenus = computed(() => {
    const defaultMenus = [
        { label: $t('common.setTableHeaderConfig'), value: '1' },
        { label: $t('_.切换表格'), value: '8' },
    ];
    const otherMenus = [
        //  { label: '库一致性比对配置', value: '7' },
        { label: $t('myProject.importRules'), value: '3' },
        { label: $t('myProject.DownloadRules'), value: '4' },
        { label: $t('myProject.changeEnable'), value: '5' },
        { label: $t('myProject.changeDisEnable'), value: '6' },
        // { label: $t('myProject.associateScheduledTask'), value: '5' },
        // { label: $t('myProject.publishScheduledTask'), value: '6' },
    ];
    return workflowProject ? defaultMenus : [...otherMenus, ...defaultMenus];
});

const selectCreate = (opt) => {
    console.log('createFlag: ', opt);
    if (opt === 'TABLE_GROUP') {
        router.push(`/projects/tableGroupRules?projectId=${projectId}`);
        return;
    }
    const typeMap = {
        [SINGLE_TABLE_RULE_FLAG]: 'newSingleTableRule',
        [CUSTOMIZATION_RULE_FLAG]: 'newCustomRule',
        [CROSS_TABLE_VERIFICATION_FULLY_RULE_FLAG]: 'newMultiTableRule',
        [FILE_VERIFICATION_RULE_FLAG]: 'documentVerification',
        [CROSS_DB_VERIFICATION_FULLY_RULE_FLAG]: 'crossDatabaseFullVerification',
        [SQL_VERIFICATION_RULE_FLAG]: 'sqlVerification',
    };
    const type = typeMap[opt] || 'newSingleTableRule';
    router.push(`/projects/rules?tpl=${type}&projectId=${projectId}`);
};

const selectCreateBatch = (opt) => {
    if (opt === 'CROSS_DB_VERIFICATION_FULLY_RULE_FLAG') {
        selectCreate(CROSS_DB_VERIFICATION_FULLY_RULE_FLAG);
        return;
    }
    router.push(`/projects/createRulesByTemplate?projectId=${projectId}`);
};

// 选择需要修改参数的规则
const editParamsConfig = ref({
    params: {
        specify_filter: false,
        delete_fail_check_result: false,
        upload_abnormal_value: false,
        upload_rule_metric_value: false,
        abnormal_data_storage: false,
        specify_static_startup_param: false,
        alert: false,
        abort_on_failure: false,
        rule_enable: true,
        union_way: 0,
        execution_parameters_name: '',
        rule_id_list: [],
    },
    show: false,
    loading: false,
    batch: false,
});

const executionParamsRef = ref(null);
const executionParamsTemplateRef = ref(null);
// 执行参数模板相关
const templateRef = ref(null);
const {
    showExecuteParamsDrawer,
    openExecuteParamsDrawer,
    confirmSelect,
    comfirmCancel,
} = useTemplateSelector(templateRef, (value) => {
    editParamsConfig.value.params.execution_parameters_name = value.execution_parameters_name;
    executionParamsTemplateRef.value.validate('execution_parameters_name');
});

const excutionParamsRules = ref({
    rule_id_list: [{
        required: true,
        message: $t('common.notEmpty'),
        trigger: ['change', 'blur'],
        type: 'array',
    }],
    execution_parameters_name: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
        type: 'string',
    }],
});
const currentRule = ref({});

// 根据规则分类获取接口地址前缀
const apiPrefix = '/api/v1/projector';
const getAPIPrefix = (rule) => {
    const ruleType = String(rule.rule_type);
    let url;
    switch (ruleType) {
        case RULE_TYPE_MAP.SINGLE_TABLE_RULE:
            url = '/api/v1/projector/rule';
            break;
        case RULE_TYPE_MAP.CROSS_TABLE_VERIFICATION_FULLY_RULE:
            url = '/api/v1/projector/mul_source_rule';
            break;
        case RULE_TYPE_MAP.CUSTOMIZATION_RULE:
            url = '/api/v1/projector/rule/custom';
            break;
        case RULE_TYPE_MAP.FILE_VERIFICATION_RULE:
            url = '/api/v1/projector/rule/file';
            break;
        default:
            url = '';
    }
    return url;
};

// 获取当前租户可支持的最大峰值提交总量
const peakNum = ref('');
const getPeakNum = async () => {
    try {
        const url = `${apiPrefix}/execution/schedule/max_num`;
        const res = await FRequest(url, {}, 'get');
        peakNum.value = res;
    } catch (err) {
        console.warn(err);
        return {};
    }
};

// 获取规则详情数据
const getRuleDetail = async (rule) => {
    try {
        const ruleId = rule.rule_id;
        const url = getAPIPrefix(rule);
        if (url && ruleId) {
            const result = await FRequest(`${url}/${ruleId}`, {}, 'get');
            return result;
        }
    } catch (err) {
        console.warn(err);
        return {};
    }
};

// 表单行操作
const projectOperationBtnHandler = async ({ value }, row) => {
    try {
        if (value === 'executeTask') {
            console.log('row', row);
            const ruleDetail = await getRuleDetail(row);
            ruleDetail.table_group = !!row.table_group; // null undefined 为false
            let curAbnormalDataStorage = false;
            if (ruleDetail.abnormal_database || ruleDetail.cluster || ruleDetail.abnormal_proxy_user) { curAbnormalDataStorage = true; }
            ruleDetail.abnormal_data_storage = curAbnormalDataStorage;
            editParamsConfig.value.batch = false;
            editParamsConfig.value.params = ruleDetail;
            editParamsConfig.value.rule_type = row.rule_type;
            showExecuteParamsDrawer.value = true;
        } else if (value === 'deleteProject') {
            // 删除规则
            FModal.confirm({
                title: $t('common.prompt'),
                content: `请确认是否删除当前${row.rule_name}规则名?`,
                onOk: async () => {
                    try {
                        const url = `${getAPIPrefix(row)}/delete`;
                        if (url === '') {
                            return;
                        }
                        await FRequest(url, {
                            rule_id: row.rule_id,
                        });
                        FMessage.success($t('toastSuccess.deleteSuccess'));
                        // 重新加载数据
                        pageChange();
                    } catch (derr) {
                        console.warn(derr);
                    }
                },
            });
        }
    } catch (err) {
        console.warn(err);
    }
};
// 规则类型操作
const projectOperationBtnEnable = async (row) => {
    const enablename = !row.rule_enable ? $t('_.启用') : $t('_.禁用');
    console.log('enablename', enablename);
    FModal.confirm({
        title: $t('common.prompt'),
        content: `确认${enablename}【${row.rule_name}】规则`,
        onOk: async () => {
            try {
                const url = `${apiPrefix}/rule/enable`;
                await FRequest(url, {
                    project_id: projectDetail.value.project_id,
                    rule_enable: !row.rule_enable,
                    rule_ids: [row.rule_id],
                });
                FMessage.success(`${enablename}成功`);
                // 重新加载数据
                pageChange();
            } catch (derr) {
                console.warn(derr);
            }
        },
    });
};

// 关闭规则参数面板
const clearSelectedRow = () => {
    editParamsConfig.value.params.rule_id_list = [];
    editParamsConfig.value.params.execution_parameters_name = '';
    comfirmCancel();
};
const copyRule = async ({ rule_group_id: ruleGroupId, rule_id: ruleId, table_group: tableGroup }) => {
    // 进入详情页，让用户去编辑
    router.push(`/projects/${tableGroup ? 'tableGroupRules' : 'rules'}?ruleGroupId=${ruleGroupId}&id=${ruleId}&workflowProject=${workflowProject}&projectId=${projectId}&copy=true`);
};
const handleOperationCick = (value, row) => {
    switch (value) {
        case 'executeTask':
            // 执行任务
            projectOperationBtnHandler({ value }, row);
            break;
        case 'copy':
            // 复制
            copyRule(row);
            break;
        case 'deleteProject':
            // 删除
            projectOperationBtnHandler({ value }, row);
            break;
        default:
            // 禁用启用
            projectOperationBtnEnable(row);
            break;
    }
};
const abnormalDataValid = (validData) => {
    if (!validData.abnormal_data_storage) {
        validData.abnormal_data_storage = false;
        validData.abnormal_database = '';
        validData.abnormal_proxy_user = '';
        validData.cluster = '';
    }
    if (!validData.specify_static_startup_param) {
        validData.specify_static_startup_param = false;
        validData.static_startup_param = '';
    }
};

// 批量更新规则参数
const updateRowParamsBatch = async () => {
    try {
        const params = cloneDeep(editParamsConfig.value.params);
        if (!params.execution_parameters_name) {
            return;
        }
        abnormalDataValid(params);
        params.rule_id_list = getFlatDataFormTreeSelect(editParamsConfig.value.params.rule_id_list, adaptDataToTreeSelect(projectAllRules.value));
        await FRequest(`${apiPrefix}/rule/batchUpdate/ruleParameters`, {
            rule_id_list: params.rule_id_list,
            execution_parameters_name: params.execution_parameters_name,
        });
        clearSelectedRow();
        FMessage.success($t('toastSuccess.editSuccess'));
        // 重新加载数据
        pageChange();
        editParamsConfig.value.loading = false;
    } catch (err) {
        console.warn(err);
        editParamsConfig.value.loading = false;
    }
};
// 更新规则参数
const updateRowParams = async () => {
    try {
        if (editParamsConfig.value.loading) {
            return;
        }
        if (editParamsConfig.value.batch) {
            await executionParamsTemplateRef.value.validate();
        }
        confirmSelect();
        if (editParamsConfig.value.batch) {
            // 批量设置
            updateRowParamsBatch();
            return;
        }
        let selectedIndex = -1;
        const templateList = templateRef.value.templateList;
        for (let i = 0; i < templateList.length; i++) {
            if (templateList[i].isSelected) {
                selectedIndex = i;
                break;
            }
        }
        if (selectedIndex === -1) {
            return;
        }
        editParamsConfig.value.loading = true;
        abnormalDataValid(editParamsConfig.value.params);
        await FRequest(`${getAPIPrefix(editParamsConfig.value)}/modify`, Object.assign({}, editParamsConfig.value.params, {
            project_id: projectDetail.value.project_id,
        }));
        // 重新加载数据
        pageChange();
        clearSelectedRow();
        FMessage.success($t('toastSuccess.editSuccess'));
        editParamsConfig.value.loading = false;
    } catch (err) {
        console.warn(err);
        editParamsConfig.value.loading = false;
    }
};

const fileInputRefs = ref(null);
const { importDatas, isImportingFiles } = useImport('rule');
// const importRules = async () => {
//     try {
//         // todo
//         const fileInputRef = fileInputRefs.value;
//         if (!fileInputRef) return;
//         const file = fileInputRef.files[0];
//         await importDatas(file, projectId);
//         pageChange();
//     } catch (err) {
//         console.warn(err);
//     }
// };
const uploadSucess = async () => {
    await pageChange();
};
const projectSelectionChange = (index, key, selectData) => {
    console.log(index, key, selectData);
    projectSelection[index] = selectData;
    enableSelection[index] = key;
    console.log('projectSelection', projectSelection);
    console.log('enableSelection', enableSelection);
};

const {
    isExporting,
    isDownloading,
    exportFileByParams,
} = useExport('rule');

// 批量更改启用|禁用规则
const isEnable = ref(false);
const isEnabling = ref();
const enableTurnon = ref(true);
const toggleEnable = (value) => {
    isEnable.value = !isEnable.value;
    if (value === '5') {
        // 启用
        enableTurnon.value = true;
    } else if (value === '6') {
        // 禁用
        enableTurnon.value = false;
    }
    // eslint-disable-next-line no-use-before-define
    filtrateRules({ rule_enable: !enableTurnon.value });
};
const enableCancel = () => {
    clear();
    isEnable.value = false;
    isEnabling.value = false;
    // 重新加载数据
    // eslint-disable-next-line no-use-before-define
    filtrateRules();
};

const objectSpanMethod = ({ rowIndex, columnIndex }) => {
    if (columnIndex === 0) {
        // 调用clearSelection后会再次回调mergeProjectSelectionChange将isSelectCheckbox设置为true
        // 点击表格复选框时会调用objectSpanMethod 重新渲染 rowIndex返回单元格合并后的rowIndex，此时colIndex一直为0
        if (rowIndex === 0 && !isExporting.value && !isEnable.value) {
            isSelectCheckbox = false;
            colIndex = 0;
        }
        const ruleList = projectRuleList.value.filter(v => projectRuleList.value[rowIndex].rule_group_name === v.rule_group_name) || [];
        if (!colIndex && ruleList.length) {
            if (ruleList.length > 1) {
                colIndex = isSelectCheckbox ? 0 : rowIndex + ruleList.length - 1;
            }
            return {
                rowspan: ruleList.length,
                colspan: 1,
            };
        }
        if (rowIndex === colIndex) {
            colIndex = 0;
        }
        return {
            rowspan: 0,
            colspan: 0,
        };
    }
};

// 过滤规则
const filtrateRules = (conditions) => {
    filrateCondition.value = conditions;
    getProjectDetail();
    clear();
};
let enableSelectData = [];
const mergeProjectSelectionChange = (selectData) => {
    isSelectCheckbox = true;
    enableSelectData = selectData;
    projectSelection = [];
    enableSelection = [];
    if (selectData.length) {
        selectData.forEach((id, index) => {
            const group = projectRuleList.value.find(rule => rule.rule_id === id);
            const ruleList = projectRuleList.value.filter(rule => rule.rule_group_name === group.rule_group_name);
            projectSelection[index] = ruleList.map(v => v.rule_id);
            enableSelection[index] = group.rule_group_name;
        });
    }
};
const changeEnable = async () => {
    const selectedArray = [];
    if (enableSelectData.length === 0) return FMessage.warn($t('common.selectOne'));
    FModal.confirm({
        title: $t('common.prompt'),
        content: $t('_.确认更改这些规则'),
        onOk: async () => {
            try {
                const url = `${apiPrefix}/rule/enable`;
                await FRequest(url, {
                    project_id: projectDetail.value.project_id,
                    rule_enable: enableTurnon.value,
                    rule_ids: enableSelectData,
                });
                isEnable.value = false;
                isEnabling.value = false;
                FMessage.success($t('_.更改成功'));
                await clear();
                // await nextTick();
                // 重新加载数据
                // eslint-disable-next-line no-use-before-define
                filrateCondition.value = null;
                await getProjectDetail();
            } catch (derr) {
                isEnabling.value = false;
                console.warn(derr);
            }
        },
    });
};

const exportProject = async () => {
    // 由于表格循环的，所以需要处理之后再给到后台
    console.log(projectSelection);
    if (projectSelection.length === 0) return FMessage.warn($t('common.selectOne'));
    const ids = [];
    for (let i = 0; i < projectSelection.length; i++) {
        if (projectSelection[i]) {
            for (let j = 0; j < projectSelection[i].length; j++) {
                ids.push(projectSelection[i][j]);
            }
        }
    }
    exportFileByParams({
        rule_ids: ids,
    });
    clear();
};
const toggleExport = () => {
    clear();
    isExporting.value = !isExporting.value;
};


const toggleExecuteParams = () => {
    editParamsConfig.value.batch = true;
    showExecuteParamsDrawer.value = true;
};

const handleProjectRules = () => {
    if (mergeCells.value) {
        projectRuleList.value = [];
        const ruleList = [];
        Object.keys(projectRules.value).forEach((key) => {
            projectRules.value[key].forEach((item) => {
                item.datasource = item.datasource.filter(v => v.db && v.table);
                ruleList.push(item);
            });
        });
        // 修复objectSpanMethod 函数rowIndex 过滤时有时候值不是从0开始导致单元格合并显示异常
        setTimeout(() => {
            colIndex = 0;
            isSelectCheckbox = false;
            projectRuleList.value = ruleList;
        }, 50);
    } else {
        for (let i = 0; i < Object.keys(projectRules.value).length; i++) {
            ruleRefs.value[i] = ref(null);
        }
        console.log(ruleRefs.value, '🚀');
        Object.keys(projectRules.value).forEach((key) => {
            projectRules.value[key].forEach((item) => {
                // 过滤空数据
                item.datasource = item.datasource.filter(v => v.db || v.table);
            });
        });
    }
};
const showImport = ref(false);
const showExport = ref(false);
const projectOperationActionHandler = (value) => {
    switch (value) {
        case '1':
            toggleTColConfig();
            break;
        case '2':
            editParamsConfig.value.batch = true;
            showExecuteParamsDrawer.value = true;
            break;
        case '3':
            try {
                // console.log(1, fileInputRefs);
                showImport.value = true;
            } catch (error) {
                console.warn(error);
            }
            break;
        case '4':
            showExport.value = true;
            break;
        case '5':
            toggleEnable('5');
            break;
        case '6':
            toggleEnable('6');
            break;
        // case '7':
        //     selectCreate(CROSS_DB_VERIFICATION_FULLY_RULE_FLAG);
        //     break;
        case '8':
            mergeCells.value = !mergeCells.value;
            localStorage.setItem('projectMergeCells', mergeCells.value);
            handleProjectRules();
            break;
        default:
            break;
    }
};
function toTask(type) {
    // type为1时是进入关联调度任务页面，2是进入定时调度任务页面
    if (type === 1) {
        router.push({
            path: '/projects/task/schedule',
            query: {
                projectId,
            },
        });
    } else {
        router.push({
            path: '/projects/task/timing',
            query: {
                projectId,
            },
        });
    }
}

onMounted(() => {
    const projectMergeCells = localStorage.getItem('projectMergeCells');
    if (projectMergeCells) {
        mergeCells.value = projectMergeCells !== 'false';
    }
    getPeakNum();
    getSubSystemInfo();
});


watch(projectRules, () => {
    handleProjectRules();
});

</script>
<config>
{
    "name": "projectDetail",
    "title": "项目详情"
}
</config>
<style lang="less" scoped>
@import '@/style/varible.less';
.template-drawer {
    .rule-valid-style {
        :deep(.fes-form-item-label) {
            margin-right: 24px;
        }
        // :deep(.fes-form-item-label::before) {
        //     display: inline-block;
        //     margin-top: 2px;
        //     margin-right: 4px;
        //     color: var(--f-danger-color);
        //     content: '*';
        // }
    }
};
.name {
    font-size: 24px;
    color: #0F1222;
    margin: 0 12px;
    font-weight: 500;
}
.rules-operate-menus{
    // .fes-btn{
    //     margin-right: 16px;
    // }
    margin-bottom: 24px;
    :deep(.button) {
        &:hover {
            .button-icon {
                filter: invert(42%) sepia(84%) saturate(1624%) hue-rotate(207deg) brightness(102%) contrast(101%);
            }
        }
    }
}
.rule-table-name{
    padding-top: 24px;
    padding-bottom: 8px;
    color: @label-color;
    & > .rule-table-name-value {
        color: @blue-color;
        cursor: pointer;
    }
}
.project-detail{
    .rule-detail-form{
        margin-bottom: 0;
    }
}
.select-hint {
    background: #F7F7F8;
    border-radius: 2px;
    padding:4px 8px;
    margin-bottom: 16px;
    span {
        color: #5384FF;
        text-align: justify;
        line-height: 22px;
        font-weight: 400;
        cursor: pointer;
    }
}
.card {
    position: relative;
    margin: 16px 0;
    border: 1px solid #CFD0D3;
    border-radius: 4px;
    padding: 16px;
    cursor: pointer;
    .check-area {
        position: absolute;
        top: 0;
        left: 0;
        width: 0;
        height: 0;
        border-radius: 4px 0 0;
        border-top: 12px solid #5384FF;
        border-right: 12px solid transparent;
        border-bottom: 12px solid transparent;
        border-left: 12px solid #5384FF;
    }
    .check {
        position: absolute;
        top: 0;
        left: 0;
        color: #FFFFFF;
    }
    .title {
        font-size: 16px;
        color: #0F1222;
        letter-spacing: 0;
        font-weight: 500;
    }
    &.selected {
        border: 1px solid #5384FF;
        border-radius: 4px;
    }
}
.enable {
    color: #00CB91;
}

.disabled {
    color: #93949B;
}


</style>
<style lang="less">
.project-detail {
    .fes-table.is-bordered {
        border: 1px solid #E7E7E8;
    }
    .fes-table.is-horizontal-line .fes-table-header .fes-table-td, .fes-table.is-horizontal-line .fes-table-body .fes-table-td, .fes-table.is-horizontal-line .fes-table-header .fes-table-th, .fes-table.is-horizontal-line .fes-table-body .fes-table-th{
        border-bottom: 1px solid #E7E7E8;
    }

    .fes-table.is-vertical-line .fes-table-header .fes-table-th, .fes-table.is-vertical-line .fes-table-body .fes-table-th, .fes-table.is-vertical-line .fes-table-header .fes-table-td, .fes-table.is-vertical-line .fes-table-body .fes-table-td {
        border-right: 1px solid #E7E7E8;
    }

    .fes-table .fes-table-header .fes-table-th, .fes-table .fes-table-body .fes-table-th {
        font-weight: 600;
    }
}

</style>
