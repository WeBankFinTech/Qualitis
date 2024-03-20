<template>
    <div>
        <div v-if="exportStatus === 'no'" class="operation-container">
            <FButton type="primary" class="operation-item white" @click="dataQualityAnalysis">
                <template #icon> <ClockCircleOutlined /> </template>{{$t('taskQuery.dataQualitisAnalysis')}}
            </FButton>
            <FButton class="operation-item" @click="clickbatchReExecution">
                <template #icon> <FileOutlined /> </template>{{$t('taskQuery.batchReExecution')}}
            </FButton>
            <FDropdown :options="moreOptions" @click="clickMore">
                <FButton class="operation-item">
                    <template #icon> <MoreCircleOutlined /> </template>{{$t('common.more')}}
                </FButton>
            </FDropdown>
        </div>
        <div v-if="exportStatus !== 'no'" class="operation-container">
            <FButton class="operation-item" style="color: #ffffff" type="primary" @click="confirmSelectItems">
                {{$t('common.ok')}}
            </FButton>
            <FButton class="operation-item" @click="resetOperation">{{$t('common.cancel')}}</FButton>
        </div>

        <!-- 数据质量分析表单弹窗 -->
        <FModal
            :show="showDataQualitiesAnalysisModal"
            :title="$t('taskQuery.dataQualitisAnalysis')"
            :width="602"
            :okText="$t('common.generate')"
            :cancelText="$t('common.cancel')"
            :maskClosable="false"
            @ok="generate"
            @cancel="showDataQualitiesAnalysisModal = false"
        >
            <FForm
                ref="dataQualitiesAnalysisForm"
                :label-width="100"
                labelPosition="right"
                :model="dataQualityAnalysisData"
            >
                <FFormItem
                    :label="$t('common.dataSource')"
                    prop="dataSource"
                    :rules="[{ required: true, trigger: ['change', 'blur'], message: $t('common.notEmpty') }]"
                >
                    <FSelect
                        v-model="dataQualityAnalysisData.dataSource"
                        clearable
                        @change="changeDataQualityDataSource"
                    >
                        <FOption
                            v-for="item in dataSourceList"
                            :key="item.cluster_name"
                            :label="item.cluster_name"
                            :value="item.cluster_name"
                            @click="props.chooseDataSource(item.cluster_name, 'dataQuality')"
                        />
                    </FSelect>
                </FFormItem>
                <FFormItem
                    :label="$t('common.databaseList')"
                    prop="dataBase"
                    :rules="[{ required: true, trigger: ['change', 'blur'], message: $t('common.notEmpty') }]"
                >
                    <FSelect
                        v-model="dataQualityAnalysisData.dataBase"
                        clearable
                        filterable
                        valueField="database_name"
                        labelField="database_name"
                        :options="dataQualityDataBaseList"
                        @change="changeDataQualityDataBase"
                    >
                    </FSelect>
                </FFormItem>
                <FFormItem
                    :label="$t('common.tableLibst')"
                    prop="dataTable"
                    :rules="[{ required: false, message: $t('common.notEmpty') }]"
                >
                    <FSelect
                        v-model="dataQualityAnalysisData.dataTable"
                        clearable
                        filterable
                        valueField="table_name"
                        labelField="table_name"
                        :options="dataQualityDataTableList"
                    >
                    </FSelect>
                </FFormItem>
                <FFormItem :label="$t('common.timeRange')" prop="timeRangeOfDataQuality" :rules="[{ required: true, message: $t('common.notEmpty'), trigger: ['blur'] }]">
                    <FDatePicker
                        v-model="dataQualityAnalysisData.timeRangeOfDataQuality"
                        clearable
                        type="datetimerange"
                        format="yyyy-MM-dd HH:mm:ss"
                        :placeholder="$t('common.pleaseSelect')"
                    />
                </FFormItem>
                <FFormItem :label="$t('common.status')" prop="status" :rules="[{ required: true, message: $t('common.notEmpty') }]">
                    <FCheckboxGroup v-model="dataQualityAnalysisData.status">
                        <FCheckbox
                            v-for="item of stateList"
                            :key="item.value"
                            :value="item.value"
                            :label="item.label"
                        />
                    </FCheckboxGroup>
                </FFormItem>
                <FFormItem
                    :label="$t('taskQuery.fileUpdator')"
                    prop="fileUpdator"
                >
                    <FSelect
                        v-model="dataQualityAnalysisData.fileUpdator"
                        clearable
                        filterable
                        valueField="value"
                        labelField="label"
                        :options="fileUpdatorList"
                    >
                    </FSelect>
                </FFormItem>
                <FFormItem :label="$t('taskQuery.filePath')" prop="path" :rules="[{ required: true, message: $t('common.notEmpty') }]">
                    <FInput v-model="dataQualityAnalysisData.path" />
                </FFormItem>
            </FForm>
        </FModal>
    </div>
</template>
<script setup>
import {
    defineProps, ref,
} from 'vue';
import { FMessage } from '@fesjs/fes-design';
import { format } from 'date-fns';


import {
    useI18n, useModel,
} from '@fesjs/fes';

import {
    ClockCircleOutlined,
    FileOutlined,
    MoreCircleOutlined,
} from '@fesjs/fes-design/es/icon';

import {
    generateData, fetchFileUpdatorList,
} from '../api';

const { t: $t } = useI18n();

const props = defineProps({
    exportStatus: {
        type: String,
        required: true,
    },
    clickbatchReExecution: {
        type: Function,
        required: true,
    },
    clickMore: {
        type: Function,
        required: true,
    },
    confirmSelectItems: {
        type: Function,
        required: true,
    },
    dataSourceList: {
        type: Array,
        required: true,
    },
    chooseDataSource: {
        type: Function,
        required: true,
    },
    chooseDataBase: {
        type: Function,
        required: true,
    },
    dataQualityDataBaseList: {
        type: Array,
        required: true,
    },
    dataQualityDataTableList: {
        type: Array,
        required: true,
    },
    resetOperation: {
        type: Function,
        required: true,
    },
});

// “更多”下拉弹框
const moreOptions = ref([
    {
        value: 'setTable',
        label: '设置表格',
    }, {
        value: 'batchStop',
        label: '批量停止',
    },
]);


const showDataQualitiesAnalysisModal = ref(false);
const dataQualitiesAnalysisForm = ref();


// 数据质量分析的弹框数据
const dataQualityAnalysisData = ref({
    dataSource: '',
    dataBase: '',
    dataTable: '',
    status: [],
    fileUpdator: '',
    path: '',
    timeRangeOfDataQuality: [],
});

const stateList = ref([
    {
        label: $t('common.byCheck'),
        value: '1',
    },
    {
        label: $t('common.failCheck'),
        value: '2',
    },
    {
        label: $t('taskQuery.failure'),
        value: '3',
    },
]);

const fileUpdatorList = ref([]);

// 全局的初始化信息
const initialState = useModel('@@initialState');

const getFileUpdatorList = async () => {
    const group = await fetchFileUpdatorList('/api/v1/projector/proxy_user');
    const user = initialState.userName;
    // || this.FesApp.get('FesUserName');
    let list = [];
    if (Array.isArray(user)) {
        list = [].concat(user);
    } else {
        list.push(user);
    }
    if (Array.isArray(group.res)) {
        list = list.concat(group.res);
    } else {
        list.push(group.res);
    }
    fileUpdatorList.value = [];
    list.forEach((item) => {
        fileUpdatorList.value.push({
            value: item,
            label: item,
        });
    });
};

const changeDataQualityDataSource = (value) => {
    props.chooseDataSource(value, 'dataQuality');
};

const changeDataQualityDataBase = (value) => {
    props.chooseDataBase(value, 'dataQuality');
};


// 数据质量分析
const dataQualityAnalysis = () => {
    getFileUpdatorList();
    showDataQualitiesAnalysisModal.value = true;
};

// 数据质量分析 生成数据
const generate = () => {
    // const clusterName = dataQualityAnalysisData.value.dataSource;
    // const dbName = dataQualityAnalysisData.value.dataBase;
    // if (!clusterName) {
    //     return FMessage.error($t('common.pleaseSelectPlus', { fieldName: $t('common.dataSource') }));
    // }
    // if (!dbName) {
    //     return FMessage.error($t('common.pleaseSelectPlus', { fieldName: $t('common.databaseList') }));
    // }

    dataQualitiesAnalysisForm.value.validate().then(() => {
        const startTime = dataQualityAnalysisData.value.timeRangeOfDataQuality[0];
        const endTime = dataQualityAnalysisData.value.timeRangeOfDataQuality[1];
        const params = {
            cluster_name: dataQualityAnalysisData.value.dataSource,
            database_name: dataQualityAnalysisData.value.dataBase,
            table_name: dataQualityAnalysisData.value.dataTable,
            start_time: startTime ? format(new Date(startTime), 'yyyy-MM-dd HH:mm:ss') : '',
            end_time: endTime ? format(new Date(endTime), 'yyyy-MM-dd HH:mm:ss') : '',
            status_conditions: dataQualityAnalysisData.value.status,
            hdfs_path: dataQualityAnalysisData.value.path,
            proxy_user: dataQualityAnalysisData.value.fileUpdator,
        };
        generateData(params).then(() => {
            FMessage.success($t('taskQuery.exportSuccessTips', { path: dataQualityAnalysisData.value.path }));
            showDataQualitiesAnalysisModal.value = false;
        }).catch((error) => {
            console.error(error);
        }).finally(() => {
        });
    }).catch((error) => {
        console.log('表单验证失败: ', error);
    });
};

</script>
<style lang="less" scoped>
.operation-container {
    display: flex;
    // margin-top: 24px;
    .operation-item {
        margin-right: 16px;
        font-family: PingFangSC-Regular;
        font-size: 14px;
        color: #0f1222;
        letter-spacing: 0;
        line-height: 22px;
        font-weight: 400;
        &.white {
            color: #FFFFFF;
        }
    }
}

</style>
