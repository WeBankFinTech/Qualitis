<template>
    <!-- 状态详情 -->
    <FDrawer
        v-model:show="showStatusDetailDrawer"
        title="状态详情"
        displayDirective="if"
        :width="940"
        contentClass="task-drawer"
        @cancel="cancel"
    >
        <div class="status-detail">
            <div class="task-id">
                <div>{{$t('common.number')}}: {{taskId}}</div>
                <div>
                    <span>校验状态：</span>
                    <FSelect
                        v-model="filter_status"
                        filterable
                        valueField="key"
                        labelField="name"
                        style="width: 200px"
                        :options="optionList"
                        @change="handleChange"
                    >
                    </FSelect>
                </div>
            </div>
            <div v-for="(statusItem, index) in statusList" :key="index">
                <div v-if="statusList.length > 0 && statusItem.single && statusItem.single.length > 0">
                    <div v-for="(item, i) in statusItem.single" :key="i" class="status-item">
                        <div class="header">
                            <div class="first-row">
                                <div v-if="item.cluster_name" class="hive">HIVE：{{item.cluster_name}} / {{item.database}}</div>
                                <div class="status">{{$t('common.status')}}: <span class="status-content" :style="{ color: getItemStatus(item) === '通过校验' ? '#00CB91' : '#FF9540' }">{{getItemStatus(item)}}</span></div>
                                <DownOutlined v-if="!item.showSecond" class="icon" @click.stop="item.showSecond = true" />
                                <UpOutlined v-if="item.showSecond" class="icon" @click.stop="item.showSecond = false" />
                            </div>
                            <div v-if="item.showSecond" class="division">
                                <div class="line"></div>
                            </div>
                            <div v-if="item.showSecond" class="second-row">
                                <div v-if="item.start_time" class="time">{{$t('taskDetail.startTime')}} {{item.start_time}}</div>
                                <div v-if="item.end_time" class="time">{{$t('taskDetail.endTime')}} {{item.end_time}}</div>
                            </div>
                        </div>
                        <div class="status-rules">
                            <div v-for="(item2, i2) in item.check_table" :key="i2" class="rule-item">
                                <div v-if="item2.rule_name" class="item">{{$t('taskDetail.taskRule')}}: <span class="rule-content rule_name" @click="toDetail(item)">{{item2.rule_name}}</span></div>
                                <div class="item">{{$t('verifyFailData.ruleType')}}: <span class="rule-content">{{$t('verifyFailData.singleTableCheck')}}</span></div>
                                <div v-if="item2.columns && item2.columns[0]" class="item">{{$t('common.parityField')}}: <span class="rule-content">{{item2.columns.join(',')}}</span></div>
                                <div v-if="item.table" class="item">{{$t('verifyFailData.databaseAndDataTable')}}: <span class="rule-content">{{`${item.database} ${item.table}`}}</span></div>
                                <div v-if="item2.save_table" class="item">{{$t('verifyFailData.notPassBase')}}: <span class="rule-content">{{item2.save_table}}</span></div>
                                <f-table :data="item2.alarm_variable" border :no-data-text="$t('common.noData')">
                                    <f-table-column v-slot="{ row }" ellipsis prop="rule_condition" :label="$t('common.ruleCondition')">{{getRuleCondition(row)}}</f-table-column>
                                    <f-table-column ellipsis prop="result" :formatter="formatterEmptyValue" :label="$t('common.ruleResult')"> </f-table-column>
                                    <f-table-column v-slot="{ row }" ellipsis prop="validate_status" :label="$t('verifyFailData.validateStatus')"><span :style="{ color: getValidateStatus(row).color }">{{getValidateStatus(row).content}}</span></f-table-column>
                                </f-table>
                            </div>
                        </div>
                    </div>
                </div>
                <div v-if="statusList.length > 0 && statusItem.custom && statusItem.custom.length > 0">
                    <div v-for="(item, i) in statusItem.custom" :key="i" class="status-item">
                        <div class="header">
                            <div class="first-row">
                                <div v-if="item.cluster_name" class="hive">HIVE：{{item.cluster_name}} / {{item.from_content}}</div>
                                <div class="status">{{$t('common.status')}}: <span class="status-content" :style="{ color: getItemStatus(item) === '通过校验' ? '#00CB91' : '#FF9540' }">{{getItemStatus(item)}}</span></div>
                                <DownOutlined v-if="!item.showSecond" class="icon" @click.stop="item.showSecond = true" />
                                <UpOutlined v-if="item.showSecond" class="icon" @click.stop="item.showSecond = false" />
                            </div>
                            <div v-if="item.showSecond" class="division">
                                <div class="line"></div>
                            </div>
                            <div v-if="item.showSecond" class="second-row">
                                <div v-if="item.start_time" class="time">{{$t('taskDetail.startTime')}} {{item.start_time}}</div>
                                <div v-if="item.end_time" class="time">{{$t('taskDetail.endTime')}} {{item.end_time}}</div>
                            </div>
                        </div>
                        <div class="status-rules">
                            <div v-for="(item2, i2) in item.check_table" :key="i2" class="rule-item">
                                <div v-if="item2.rule_name" class="item">{{$t('taskDetail.taskRule')}}: <span class="rule-content rule_name" @click="toDetail(item)">{{item2.rule_name}}</span></div>
                                <div class="item">{{$t('verifyFailData.ruleType')}}: <span class="rule-content">{{$t('verifyFailData.customTableCheck')}}</span></div>
                                <div v-if="item2.columns && item2.columns[0]" class="item">{{$t('common.parityField')}}: <span class="rule-content">{{item2.columns.join(',')}}</span></div>
                                <div v-if="item.from_content" class="item">{{$t('verifyFailData.databaseAndDataTable')}}: <span class="rule-content">{{item.from_content}}</span></div>
                                <div v-if="item2.save_table" class="item">{{$t('verifyFailData.notPassBase')}}: <span class="rule-content">{{item2.save_table}}</span></div>
                                <f-table :data="item2.alarm_variable" border :no-data-text="$t('common.noData')">
                                    <f-table-column v-slot="{ row }" ellipsis prop="rule_condition" :label="$t('common.ruleCondition')">{{getRuleCondition(row)}}</f-table-column>
                                    <f-table-column ellipsis prop="result" :formatter="formatterEmptyValue" :label="$t('common.ruleResult')"> </f-table-column>
                                    <f-table-column v-slot="{ row }" ellipsis prop="validate_status" :label="$t('verifyFailData.validateStatus')"><span :style="{ color: getValidateStatus(row).color }">{{getValidateStatus(row).content}}</span></f-table-column>
                                </f-table>
                            </div>
                        </div>
                    </div>
                </div>
                <div v-if="statusList.length > 0 && statusItem.multiple && statusItem.multiple.length > 0">
                    <div v-for="(item, i) in statusItem.multiple" :key="i" class="status-item">
                        <div class="header">
                            <div class="first-row">
                                <div v-if="item.cluster_name" class="hive">HIVE：{{item.cluster_name}} / {{item.database}}</div>
                                <div class="status">{{$t('common.status')}}: <span class="status-content" :style="{ color: getItemStatus(item) === '通过校验' ? '#00CB91' : '#FF9540' }">{{getItemStatus(item)}}</span></div>
                                <DownOutlined v-if="!item.showSecond" class="icon" @click.stop="item.showSecond = true" />
                                <UpOutlined v-if="item.showSecond" class="icon" @click.stop="item.showSecond = false" />
                            </div>
                            <div v-if="item.showSecond" class="division">
                                <div class="line"></div>
                            </div>
                            <div v-if="item.showSecond" class="second-row">
                                <div v-if="item.start_time" class="time">{{$t('taskDetail.startTime')}} {{item.start_time}}</div>
                                <div v-if="item.end_time" class="time">{{$t('taskDetail.endTime')}} {{item.end_time}}</div>
                            </div>
                        </div>
                        <div class="status-rules">
                            <div v-for="(item2, i2) in item.check_table" :key="i2" class="rule-item">
                                <div v-if="item2.rule_name" class="item">{{$t('taskDetail.taskRule')}}: <span class="rule-content rule_name" @click="toDetail(item)">{{item2.rule_name}}</span></div>
                                <div class="item">{{$t('verifyFailData.ruleType')}}: <span class="rule-content">{{$t('verifyFailData.acrossCheck')}}</span></div>
                                <div v-if="item2.columns && item2.columns[0]" class="item">{{$t('common.parityField')}}: <span class="rule-content">{{item2.columns.join(',')}}</span></div>
                                <div v-if="item.table" class="item">{{$t('verifyFailData.databaseAndDataTable')}}: <span class="rule-content">{{`${item.database} ${item.table}`}}</span></div>
                                <div v-if="item2.save_table" class="item">{{$t('verifyFailData.notPassBase')}}: <span class="rule-content">{{item2.save_table}}</span></div>
                                <f-table :data="item2.alarm_variable" border :no-data-text="$t('common.noData')">
                                    <f-table-column v-slot="{ row }" ellipsis prop="rule_condition" :label="$t('common.ruleCondition')">{{getRuleCondition(row)}}</f-table-column>
                                    <f-table-column ellipsis prop="result" :formatter="formatterEmptyValue" :label="$t('common.ruleResult')"> </f-table-column>
                                    <f-table-column v-slot="{ row }" ellipsis prop="validate_status" :label="$t('verifyFailData.validateStatus')"><span :style="{ color: getValidateStatus(row).color }">{{getValidateStatus(row).content}}</span></f-table-column>
                                </f-table>
                            </div>
                        </div>
                    </div>
                </div>
                <div v-if="statusList.length > 0 && statusItem.file && statusItem.file.length > 0">
                    <div v-for="(item, i) in statusItem.file" :key="i" class="status-item">
                        <div class="header">
                            <div class="first-row">
                                <div v-if="item.cluster_name" class="hive">HIVE：{{item.cluster_name}} / {{item.database}}</div>
                                <div class="status">{{$t('common.status')}}: <span class="status-content" :style="{ color: getItemStatus(item) === '通过校验' ? '#00CB91' : '#FF9540' }">{{getItemStatus(item)}}</span></div>
                                <DownOutlined v-if="!item.showSecond" class="icon" @click.stop="item.showSecond = true" />
                                <UpOutlined v-if="item.showSecond" class="icon" @click.stop="item.showSecond = false" />
                            </div>
                            <div v-if="item.showSecond" class="division">
                                <div class="line"></div>
                            </div>
                            <div v-if="item.showSecond" class="second-row">
                                <div v-if="item.start_time" class="time">{{$t('taskDetail.startTime')}} {{item.start_time}}</div>
                                <div v-if="item.end_time" class="time">{{$t('taskDetail.endTime')}} {{item.end_time}}</div>
                            </div>
                        </div>
                        <div class="status-rules">
                            <div v-for="(item2, i2) in item.check_table" :key="i2" class="rule-item">
                                <div v-if="item2.rule_name" class="item">{{$t('taskDetail.taskRule')}}: <span class="rule-content rule_name" @click="toDetail(item)">{{item2.rule_name}}</span></div>
                                <div class="item">{{$t('verifyFailData.ruleType')}}: <span class="rule-content">{{$t('addGroupTechniqueRule.documentVerification')}}</span></div>
                                <div v-if="item2.columns && item2.columns[0]" class="item">{{$t('common.parityField')}}: <span class="rule-content">{{item2.columns.join(',')}}</span></div>
                                <div v-if="item.table" class="item">{{$t('verifyFailData.databaseAndDataTable')}}: <span class="rule-content">{{`${item.database} ${item.table}`}}</span></div>
                                <div v-if="item2.save_table" class="item">{{$t('verifyFailData.notPassBase')}}: <span class="rule-content">{{item2.save_table}}</span></div>
                                <f-table :data="item2.alarm_variable" border :no-data-text="$t('common.noData')">
                                    <f-table-column v-slot="{ row }" ellipsis prop="rule_condition" :label="$t('common.ruleCondition')">{{getRuleCondition(row)}}</f-table-column>
                                    <f-table-column ellipsis prop="result" :formatter="formatterEmptyValue" :label="$t('common.ruleResult')"> </f-table-column>
                                    <f-table-column v-slot="{ row }" ellipsis prop="validate_status" :label="$t('verifyFailData.validateStatus')"><span :style="{ color: getValidateStatus(row).color }">{{getValidateStatus(row).content}}</span></f-table-column>
                                </f-table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <FPagination
            v-model:currentPage="pageInfo.currentPage"
            v-model:pageSize="pageInfo.pageSize"
            :pageSizeOption="pageSizeOption"
            show-size-changer
            :total-count="pageInfo.total"
            showTotal
            class="pagination"
            @change="handleChange"
        ></FPagination>
    </FDrawer>
</template>
<script setup>
import {
    defineProps, ref, defineExpose, reactive,
} from 'vue';
import {
    useI18n, useRouter,
} from '@fesjs/fes';

import {
    DownOutlined,
    UpOutlined,
} from '@fesjs/fes-design/es/icon';

import { formatterEmptyValue } from '@/common/utils';
import {
    fetchStatus, fetchTaskIdStatus,
} from '../api';


const { t: $t } = useI18n();

const props = defineProps({
    trData: {
        type: Object,
        required: true,
    },
    taskId: {
        type: String,
        required: true,
    },
    tasksPagination: {
        type: Object,
        required: true,
    },
});
const pageSizeOption = reactive([1, 5, 10, 20]);
const pageInfo = reactive({
    currentPage: 1,
    pageSize: 5,
    total: 0,
});
const showStatusDetailDrawer = ref(false);

const router = useRouter();
const optionList = [
    {
        name: 'ALL',
        key: 0,
    },
    // {
    //     name: '通过校验',
    //     key: 0,
    // },
    // {
    //     name: '未通过校验',
    //     key: 0,
    // },
    // {
    //     name: '校验失败',
    //     key: 0,
    // },
    {
        name: '筛除通过校验状态',
        key: 1,
    },
];
// 状态详情部分
/**
 * Type of project
 * 1, "Normal Project"
 * 2, "Workflow Project"
 */
function toDetail(row) {
    console.log(row);
    const {
        check_table,
    } = row;
    if (check_table.length > 0) {
        const {
            rule_group_id, rule_id, project_id, project_type, table_group: tableGroup,
        } = check_table[0];
        const targetPath = tableGroup ? '/projects/tableGroupRules' : '/projects/rules';
        router.push({
            path: targetPath,
            query: {
                ruleGroupId: rule_group_id,
                id: rule_id,
                // eslint-disable-next-line camelcase
                workflowProject: +project_type === 2,
                projectId: project_id,
            },
        });
    }
}
const cancel = () => {
    pageInfo.currentPage = 1;
    pageInfo.pageSize = 5;
    pageInfo.total = 0;
};
const getStatus = async (tempId) => {
    try {
        const res = await fetchStatus(`api/v1/projector/application/task/detail/${tempId}`, { tempId });
        console.log('getStatus res: ', res);
        return res;
    } catch (error) {
        console.log('error: ', error);
    }
    return [];
};

const sortArrayByIndex = (arr) => {
    if (!Array.isArray(arr)) return arr;
    return arr.sort((a, b) => a.index - b.index);
};

const statusList = ref([]);

// 获取状态详情里每个大任务的状态
const getItemStatus = (item) => {
    let status = '通过校验';
    item.check_table.forEach((item2) => {
        item2.alarm_variable.forEach((item3) => {
            // 只要有一个不是pass就是fail
            if (item3.status !== 1) status = '未通过校验';
            // 只要有一个是未校验，那就整体返回未校验
            if (item3.status === 3) status = '未校验';
        });
    });
    return status;
};

// 状态详情组件的数据初始化
const tempPageData = ref([]);
const statusDetailDrawerInit = async () => {
    showStatusDetailDrawer.value = true;
    statusList.value = tempPageData.value;
    console.log('statusList', statusList.value);
    if (tempPageData.value.length > 0) {
        tempPageData.value.forEach((itemOut, index) => {
            getStatus(itemOut.task_id).then((tempLogs) => {
                const tempRes = tempLogs.res;
                const tempData = tempRes.check_datasource;
                statusList.value.forEach(() => {
                    statusList.value[index].single = tempData.single || [];
                    statusList.value[index].single.forEach((item) => {
                        item.status = getItemStatus(item);
                        item.task_rules = itemOut.task_rules;
                        item.start_time = itemOut.start_time;
                        item.end_time = itemOut.end_time;
                        item.cluster_name = tempRes.cluster_name;
                    });
                    statusList.value[index].multiple = tempData.multiple || [];
                    statusList.value[index].multiple.forEach((item) => {
                        item.status = getItemStatus(item);
                        item.task_rules = itemOut.task_rules;
                        item.start_time = itemOut.start_time;
                        item.end_time = itemOut.end_time;
                        item.cluster_name = tempRes.cluster_name;
                        // eslint-disable-next-line camelcase
                        if (Array.isArray(item.check_table)) {
                            // eslint-disable-next-line camelcase
                            item.check_table = item.check_table.map(subItem => ({
                                rule_name: subItem.rule_name,
                                alarm_variable: subItem.alarm_variable.map(alarm => ({
                                    ...alarm,
                                    result: typeof (alarm.result) === 'string' ? alarm.result : (!Array.isArray(subItem.result)
                                        ? subItem.result : subItem.result.map(temp => `${temp.envName}(${temp.value})`).join('; ')),
                                })),
                                save_table: !Array.isArray(subItem.save_table)
                                    ? subItem.save_table : subItem.save_table.map(temp => temp.mid_table_name).join(', '),
                                rule_group_id: subItem.rule_group_id,
                                rule_id: subItem.rule_id,
                                project_id: subItem.project_id,
                                project_type: subItem.project_type,
                            }));
                        }
                        item.datasource = sortArrayByIndex(item.datasource);
                    });
                    statusList.value[index].custom = tempData.custom || [];
                    statusList.value[index].custom.forEach((item) => {
                        item.status = getItemStatus(item);
                        item.task_rules = itemOut.task_rules;
                        item.start_time = itemOut.start_time;
                        item.end_time = itemOut.end_time;
                        item.cluster_name = tempRes.cluster_name;
                    });
                    statusList.value[index].file = tempData.file || [];
                    statusList.value[index].file.forEach((item) => {
                        item.status = getItemStatus(item);
                        item.task_rules = itemOut.task_rules;
                        item.start_time = itemOut.start_time;
                        item.end_time = itemOut.end_time;
                        item.cluster_name = tempRes.cluster_name;
                    });
                });
            });
        });
    }
    statusList.value = [].concat(statusList.value);
};
// eslint-disable-next-line camelcase
const filter_status = ref(0);
const handleChange = async (currentPage, pageSize) => {
    if (currentPage) pageInfo.currentPage = currentPage;
    if (pageSize) pageInfo.pageSize = pageSize;
    const res = await fetchTaskIdStatus({
        application_id: props.taskId, filter_status: filter_status.value, page: props.tasksPagination.current - 1, size: props.tasksPagination.size, task_page: pageInfo.currentPage - 1, task_size: pageInfo.pageSize,
    });
    tempPageData.value = res.data && res.data.length ? res.data[0]?.task : [];
    pageInfo.total = res.data && res.data.length ? res.data[0]?.task_total : 0;
    statusDetailDrawerInit();
};
defineExpose({ handleChange, statusDetailDrawerInit });

const formatCompareType = (compareType) => {
    let flag = '';
    switch (compareType) {
        case 1:
            flag = '=';
            break;
        case 2:
            flag = '>';
            break;
        case 3:
            flag = '<';
            break;
        case 4:
            flag = '≥';
            break;
        case 5:
            flag = '≤';
            break;
        case 6:
            flag = '≠';
            break;
        default:
            break;
    }
    return flag;
};

const getTem = (checkTemplate, compareType, threshold) => {
    let tem = '';
    switch (checkTemplate) {
        case 1:
            tem = `${$t('common.monthlyFluctuation')}${threshold}%`;
            break;
        case 2:
            tem = `${$t('common.weeklyFluctuation')}${threshold}%`;
            break;
        case 3:
            tem = `${$t('common.daillyFluctuation')}${threshold}%`;
            break;
        case 4:
            tem = `${$t('common.thresholdValue')}${formatCompareType(compareType)}${threshold}`;
            break;
        case 5:
            tem = `${$t('common.yearCircleCompare')}${formatCompareType(compareType)}${threshold}%`;
            break;
        case 6:
            tem = `${$t('common.halfYearCircleCompare')}${formatCompareType(compareType)}${threshold}%`;
            break;
        case 7:
            tem = `${$t('common.seasonCircleCompare')}${formatCompareType(compareType)}${threshold}%`;
            break;
        case 8:
            tem = `${$t('common.monthCircleCompare')}${formatCompareType(compareType)}${threshold}%`;
            break;
        case 9:
            tem = `${$t('common.weekCircleCompare')}${formatCompareType(compareType)}${threshold}%`;
            break;
        case 10:
            tem = `${$t('common.dayCircleCompare')}${formatCompareType(compareType)}${threshold}%`;
            break;
        case 11:
            tem = `${$t('common.hourCircleCompare')}${formatCompareType(compareType)}${threshold}%`;
            break;
        case 12:
            tem = `${$t('common.monthSameCompare')}${formatCompareType(compareType)}${threshold}%`;
            break;
        default:
            break;
    }
    return tem;
};

const getRuleCondition = (row) => {
    const { check_template, compare_type, threshold } = row;
    return getTem(check_template, compare_type, threshold);
};

const getValidateStatus = (row) => {
    const { status } = row;
    const tem = {
        content: '',
        color: '',
    };

    switch (status) {
        case 1:
            tem.content = '通过校验';
            tem.color = '#00CB91';
            break;
        case 2:
            tem.content = '未通过校验';
            tem.color = '#FF9540';
            break;
        case 3:
            tem.content = '未校验';
            tem.color = '#FF9540';
            break;
        default:
            break;
    }
    return tem;
};


</script>
<style lang="less" scoped>


</style>

<style lang="less">
.status-detail {
    .task-id {
        font-family: PingFangSC-Regular;
        font-size: 14px;
        color: #646670;
        line-height: 22px;
        font-weight: 400;
        margin-bottom: 16px;
        display: flex;
        justify-content: space-between;
    }
    .status-item {
        overflow: hidden;
        border: 1px solid #CFD0D3;
        border-radius: 4px;
        margin-bottom: 16px;
        .header {
            .first-row,
            .second-row {
                padding: 16px;
                display: flex;
                min-height: 54px;
                background: #F7F7F8;
                color: #0F1222;
                line-height: 22px;
                font-weight: 600;
            }
            .first-row {
                position: relative;
                .hive {
                    flex: 1;
                }
                .rule-name {
                    flex: 1;
                }
                .status {
                    flex: 1;
                }
                .icon {
                    position: absolute;
                    // width: 14px;
                    // height: 14px;
                    right: 16px;
                    top: 20px;
                }
            }
            .division {
                background-color: #F7F7F8;
                height: 1px;
                display: flex;
                justify-content: space-around;
                .line {
                    border-top: 1px solid #CFD0D3;
                    width: calc(100% - 32px);
                    height: 1px;
                }
            }
            .second-row {
                .time {
                    flex: 1;
                }
            }
        }
        .status-rules {
            padding: 16px;
            .rule-item {
                margin-bottom: 16px;
                .item {
                    margin-bottom: 16px;
                    .title {
                        color: #646670;
                        line-height: 22px;
                        font-weight: 400;
                    }
                    .rule-content {
                        color: #0F1222;
                        line-height: 22px;
                        font-weight: 400;
                        &.rule_name {
                            cursor: pointer;
                            color: #5384FF;
                        }
                    }
                }
            }
        }
    }
}
.pagination {
    display: flex;
    justify-content: flex-end;
}
</style>
