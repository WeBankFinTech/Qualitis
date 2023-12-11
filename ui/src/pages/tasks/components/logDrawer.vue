<template>
    <!-- 日志详情 -->
    <FDrawer
        v-model:show="showLogDetailDrawer"
        title="日志详情"
        displayDirective="if"
        :width="940"
        contentClass="task-drawer"
        @cancel="cancel"
    >
        <div class="log-detail">
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
            <ResizeBox
                v-for="log in logs"
                :key="log.task_id"
                class="log-item"
                @onMouseMove="onMouseMove($event, log.task_id)"
                @onMouseUp="onMouseUp($event, log.task_id)"
            >
                <div class="header">
                    <div class="first-row">
                        <div
                            v-if="log.cluster_id"
                            class="hive"
                            :title="log.cluster_id"
                        >
                            HIVE：{{log.cluster_id}}
                        </div>
                        <div
                            v-if="log.task_rules"
                            class="rule-name"
                            :title="getRuleNames(log.task_rules)"
                        >
                            {{$t('taskDetail.taskRule')}}: {{getRuleNames(log.task_rules)}}
                        </div>
                        <div class="status">
                            {{$t('common.status')}}: <span
                                class="status-content"
                                :style="{ color: getStatusColor(log.status) }"
                            >{{formatStatus(log.status)}}</span>
                        </div>
                        <DownOutlined
                            v-if="!log.showSecond"
                            class="icon"
                            @click.stop="log.showSecond = true"
                        />
                        <UpOutlined
                            v-if="log.showSecond"
                            class="icon"
                            @click.stop="log.showSecond = false"
                        />
                    </div>
                    <div
                        v-if="log.showSecond"
                        class="division"
                    >
                        <div class="line"></div>
                    </div>
                    <div
                        v-if="log.showSecond"
                        class="second-row"
                    >
                        <div
                            v-if="log.start_time"
                            class="time"
                        >
                            {{$t('taskDetail.startTime')}} {{log.start_time}}
                        </div>
                        <div
                            v-if="log.end_time"
                            class="time"
                        >
                            {{$t('taskDetail.endTime')}} {{log.end_time}}
                        </div>
                    </div>
                </div>
                <div
                    class="log-txt"
                >
                    <FTabs>
                        <FTabPane value="all">
                            <template #tab>
                                <div class="log-tab-pane log-all">All</div>
                            </template>
                            <MonacoEditor
                                v-if="log.cluster_id && log.task_id"
                                v-model="log.log"
                                :height="`${defaultHeight[log.task_id] || 360}px`"
                                language="log"
                                theme="logview"
                                readOnly
                            />
                            <MonacoEditor
                                v-else
                                v-model="log.logtxt"
                                :height="`${defaultHeight[log.task_id] || 360}px`"
                                language="log"
                                theme="logview"
                                readOnly
                            />
                        </FTabPane>
                        <FTabPane value="errro">
                            <template #tab>
                                <div class="log-tab-pane log-error">
                                    Error
                                    <CountCircle
                                        :count="log.errorCount"
                                        type="error"
                                    />
                                </div>
                            </template>
                            <MonacoEditor
                                v-model="log.errorString"
                                :height="`${defaultHeight[log.task_id] || 360}px`"
                                language="log"
                                theme="logview"
                                readOnly
                            />
                        </FTabPane>
                        <FTabPane value="warn">
                            <template #tab>
                                <div class="log-tab-pane log-warn">
                                    Warn
                                    <CountCircle
                                        :count="log.warnCount"
                                        type="warn"
                                    />
                                </div>
                            </template>
                            <MonacoEditor
                                v-model="log.warnString"
                                :height="`${defaultHeight[log.task_id] || 360}px`"
                                language="log"
                                theme="logview"
                                readOnly
                            />
                        </FTabPane>
                        <FTabPane value="Info">
                            <template #tab>
                                <div class="log-tab-pane log-info">
                                    Info
                                    <CountCircle
                                        :count="log.infoCount"
                                        type="info"
                                    />
                                </div>
                            </template>
                            <MonacoEditor
                                v-model="log.infoString"
                                :height="`${defaultHeight[log.task_id] || 360}px`"
                                language="log"
                                theme="logview"
                                readOnly
                            />
                        </FTabPane>
                    </FTabs>
                </div>
            </ResizeBox>
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
    useI18n, MonacoEditor,
} from '@fesjs/fes';
import {
    DownOutlined,
    UpOutlined,
} from '@fesjs/fes-design/es/icon';
// import MonacoEditor from './MonacoEditor.vue';
import { cloneDeep } from 'lodash-es';
import {
    fetchLog, fetchTaskIdStatus,
} from '../api';
import CountCircle from './CountCircle.vue';
import ResizeBox from './resizeBox.vue';

const { t: $t } = useI18n();
const language = localStorage.getItem('currentLanguage');
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

const showLogDetailDrawer = ref(false);
const pageSizeOption = reactive([1, 5, 10, 20]);
const pageInfo = reactive({
    currentPage: 1,
    pageSize: 5,
    total: 0,
});
// 日志详情部分

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

const logs = ref([]);

const getLog = async (clusterId, tempId) => {
    try {
        const res = await fetchLog(`api/v1/projector/job/log/${clusterId}/${tempId}`, { tempId });
        console.log('getLog res: ', res);
        return res;
    } catch (error) {
        console.log('error: ', error);
    }
    return [];
};
const cancel = () => {
    pageInfo.currentPage = 1;
    pageInfo.pageSize = 5;
    pageInfo.total = 0;
};

// 控制log框大小
const defaultHeight = reactive({});
const originDefaultHeight = reactive({});
const onMouseMove = (delta, taskId) => {
    // console.log(delta);
    defaultHeight[taskId] = originDefaultHeight[taskId] + delta.deltaY;
};
const onMouseUp = (delta, taskId) => {
    console.log(delta, taskId);
    originDefaultHeight[taskId] = defaultHeight[taskId];
};

// 日志详情组件的数据初始化
const tempPageData = ref([]);
const logDetailDrawerInit = async () => {
    showLogDetailDrawer.value = true;

    logs.value = [];
    if (props.trData.exception_message) {
        logs.value.push(props.trData);
    } else {
        logs.value = tempPageData.value;
    }
    if (Array.isArray(logs.value)) {
        logs.value.map((task) => {
            task.logtxt = '';
            return null;
        });
    }
    logs.value = logs.value || [];

    const getLogPromise = [];
    logs.value.forEach((task) => {
        if (task.cluster_id && task.task_id) {
            getLogPromise.push(getLog(task.cluster_id, task.task_id));
        } else {
            task.logtxt = task.exception_message;
            if (task.submit_time) {
                task.start_time = task.submit_time;
            }
        }
    });
    await Promise.all(getLogPromise).then((tempLogs) => {
        // tempLogs.forEach((tempLogs2) => {
        //     logs.value.forEach((task) => {
        //         if (task.task_id === tempLogs2.task_id) {
        //             task = cloneDeep(tempLogs2.res);
        //         }
        //     });
        // });
        logs.value = logs.value?.map(task => Object.assign({}, task, tempLogs?.find(tempLogs2 => tempLogs2.task_id === task.task_id)?.res || {}));
        logs.value = [].concat(logs.value);
    });
    logs.value = logs.value.map((item) => {
        item.errorCount = item.errorCount || item.error_count;
        if (item.task_id) {
            defaultHeight[item.task_id] = 360;
            originDefaultHeight[item.task_id] = 360;
        }
        const message = language === 'zh-CN' ? item.zhMessage : item.enMessage;
        if (!item.errorString && item.exception_message && item.status > 5) {
            item.errorString = item.exception_message;
        }
        if (message && item.status > 5) {
            item.errorCount = item.errorCount + 1 || 0;
        }
        return {
            ...item,
            errorString: (message && item.status > 5) ? ` ERROR ${message}\n${item.errorString || ''}` : item.errorString || '',
        };
    });
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
    await logDetailDrawerInit();
};


// 暴露valid属性
defineExpose({ handleChange, logDetailDrawerInit });

const getRuleNames = rules => rules.map(it => it.rule_name).join(',');

const tempLogList = {
    1: {
        label: '已提交',
        color: '#00CB91',
    },
    2: {
        label: '初始化',
        color: '#00CB91',
    },
    3: {
        label: '运行中',
        color: '#00CB91',
    },
    4: {
        label: '',
        color: '#00CB91',
    },
    5: {
        label: '通过校验',
        color: '#00CB91',
    },
    6: {
        label: '未通过校验',
        color: '#FF9540',
    },
    7: {
        label: '失败',
        color: '#FF4D4F',
    },
    8: {
        label: '任务不存在',
        color: '#FF4D4F',
    },
    9: {
        label: '取消',
        color: '#FF4D4F',
    },
    10: {
        label: '超时',
        color: '#FF4D4F',
    },
    11: {
        label: '',
        color: '#FF4D4F',
    },
};

const formatStatus = data => (tempLogList[data] ? tempLogList[data].label : '');
const getStatusColor = data => (tempLogList[data] ? tempLogList[data].color : '');

</script>
<style lang="less" scoped></style>

<style lang="less">
.log-detail {
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

    .log-item {
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
                    flex: 4;
                    overflow: hidden;
                    white-space: nowrap;
                    text-overflow: ellipsis;
                    padding-right: 16px;
                }

                .rule-name {
                    flex: 4;
                    overflow: hidden;
                    white-space: nowrap;
                    text-overflow: ellipsis;
                    padding-right: 16px;
                }

                .status {
                    flex: 2;
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

        .log-txt {
            .fes-textarea {
                height: 100%;

                .fes-textarea-inner {
                    padding: 16px;
                    border: 0;
                    height: 100% !important;
                    overflow: auto;
                }
            }
        }

        .show-more {
            margin: 16px 0;
            text-align: center;
            font-size: 14px;
            color: #5384FF;
            font-weight: 400;

            &:hover {
                cursor: pointer;
            }

            user-select: none;
        }
    }
}

.pagination {
    display: flex;
    justify-content: flex-end;
}
</style>
