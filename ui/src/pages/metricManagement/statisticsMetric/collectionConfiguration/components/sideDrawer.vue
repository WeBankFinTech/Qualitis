<template>
    <FDrawer v-model:show="drawerShow" displayDirective="if" :title="drawerTitle" :footer="true" width="50%" @cancel="handleClose">
        <FForm ref="funcFormRef" :labelWidth="66" :labelPosition="drawerType === 'display' ? 'left' : 'right'" :model="data" :rules="curRules" :class="classMode" class="rule-detail-form">
            <FFormItem prop="proxy_user" :label="$t('_.代理用户')">
                <FSelect
                    v-model="data.proxy_user"
                    style="width:830px"
                    filterable
                    clearable
                    :options="userList"
                ></FSelect>
            </FFormItem>
            <FFormItem prop="collect_obj" :label="$t('_.采集对象')">
                <FSelectCascader
                    v-model="data.collect_obj"
                    :data="clusterVivisions"
                    :loadData="loadData"
                    clearable
                    remote
                    emitPath
                    showPath
                    @change="handleChange"
                />
            </FFormItem>
            <FFormItem prop="partition" :label="$t('_.采集分区')">
                <FSelect
                    v-model="data.partition"
                    style="width:830px"
                    tag
                    filterable
                    clearable
                    :options="partitionList"
                ></FSelect>
            </FFormItem>
            <div class="selection">
                <ConfigGroup
                    v-for="(item, index) in data.collect_configs"
                    :ref="el => { if (el) inputMetaRefs[index] = el }"
                    :key="item"
                    v-model:inputConfigData="data.collect_configs[index]"
                    :removable="data.collect_configs.length > 1"
                    :index="index + 1"
                    :templateList="templateList"
                    :collect_obj="data.collect_obj"
                    :columnList="columnList"
                    :userList="userList"
                    :clusterVivisions="clusterVivisions"
                    @delete="deleteInputMeta(index)"
                />
                <div class="add-sign">
                    <span class="add-sign-btn" @click="addInputMeta">
                        <PlusCircleOutlined style="padding-right: 4.58px;" />{{$t('_.添加配置组')}}</span>
                </div>
            </div>
        </FForm>
        <template #footer>
            <div>
                <FButton type="primary" style="margin-right: 16px;" :loading="isSubmitting || uploading" @click="submitForm">{{$t('_.保存')}}</FButton>
                <FButton @click="handleClose">{{$t('_.取消')}}</FButton>
            </div>
        </template>
        <input
            v-show="false"
            :ref="el => { if (el) fileInputRefs = el }"
            type="file"
            class="btn-file-input"
            :accept="acceptType"
            @change="importFunc(file)"
        />
    </FDrawer>
</template>
<script setup>

import {
    defineProps, defineEmits, computed, ref, unref, inject, onMounted, watch,
} from 'vue';
import {
    useI18n, request,
} from '@fesjs/fes';
import {
    PlusOutlined, PlusCircleOutlined, MinusCircleOutlined,
} from '@fesjs/fes-design/es/icon';
import { ACCEPT_TYPE_MAP } from '@/assets/js/const.js';
import { cloneDeep } from 'lodash-es';
import { FMessage, FModal } from '@fesjs/fes-design';
import useBasicOptions from '../hooks/useBasicOptions';
import ConfigGroup from './configGroup.vue';

const {
    handleClusterChange,
    // 开始
    clusterVivisions,
    loadData,
    getPartitionList,
    userList,
    templateList,
} = useBasicOptions();
const partitionList = ref([]);

const { t: $t } = useI18n();

const props = defineProps({
    show: {
        type: Boolean,
        required: true,
    },
    type: {
        type: String,
        required: true,
    },
});
const initConfigInfo = {
    template_id: null,
    columns: null,
};
const inputMetaRefs = ref([]);
const drawerType = ref(props.type);
const classMode = computed(() => {
    if (drawerType.value !== 'display') {
        return 'edit';
    }
    return 'index-detail-form';
});
const data = ref({
    collect_obj: [],
});
const handleChange = async (value) => {
    console.log('采集对象', value);
    data.value.partition = '';
    if (value) {
        partitionList.value = await getPartitionList({ cluster_name: value[0], database: value[1], table: value[2] });
    } else {
        partitionList.value = [];
    }
};
const addInputMeta = () => {
    data.value.collect_configs.push({
        collect_type: true,
        calcu_unit_configs: [cloneDeep(initConfigInfo)],
    });
    console.log('data.value.collect_configs', data.value.collect_configs);
};
const deleteInputMeta = (index) => {
    data.value.collect_configs.splice(index, 1);
};
const emit = defineEmits(['submit', 'update:show']);

const drawerShow = computed({
    get() {
        return props.show;
    },
    set(value) {
        emit('update:show', value);
    },
});
const titleMap = {
    display: $t('_.采集配置详情'),
    edit: $t('_.编辑采集配置'),
    add: $t('_.新增采集配置'),
};
const drawerTitle = computed(() => titleMap[drawerType.value]);

const handleClose = () => {
    drawerShow.value = false;
};

// 函数文件
const uploading = ref(false);
const fileInputRefs = ref(null);
const uploadFile = () => {
    if (!data.value.impl_type) {
        FMessage.warning($t('_.请先选择实现方式'));
        return;
    }
    fileInputRefs.value?.click();
};
const importFunc = async () => {
    try {
        const fileInputRef = fileInputRefs.value;
        if (!fileInputRef) return;
        const file = fileInputRef.files[0];
        const formData = new FormData();
        formData.append('file', file);
        uploading.value = true;
        const res = await request('/api/v1/projector/meta_data/udf/upload', formData, 'post');
        uploading.value = false;
        data.value.file = res;
    } catch (err) {
        console.warn(err);
        uploading.value = false;
    }
};

const delFile = () => {
    data.value.file = '';
    fileInputRefs.value.value = '';
};
// 提交表单
const isSubmitting = ref(false);
const funcFormRef = ref(null);
const inputMetaForm = ref(null);
const submitForm = async () => {
    try {
        isSubmitting.value = true;
        const result = await Promise.all([funcFormRef.value.validate(), ...inputMetaRefs.value.map(item => item.valid())]);
        if (result.includes(false)) {
            isSubmitting.value = false;
            return;
        }
        // 不能直接在data上处理，绑定了级联选择器组件操作对应数据会报错
        const body = cloneDeep(unref(data));
        const params = {
            cluster_name: body.collect_obj[0],
            database: body.collect_obj[1],
            table: body.collect_obj[2],
            proxy_user: body.proxy_user,
            partition: body.partition,
            collect_configs: body.collect_configs,
        };
        delete body.original_list;
        const res = await request('api/v1/projector/imsmetric/collect/create_group', [params], 'post');
        isSubmitting.value = false;
        FMessage.success($t('_.录入成功'));
        emit('submit');
        handleClose();
    } catch (err) {
        console.warn(err);
        isSubmitting.value = false;
    }
};
const rules = ref({

    cn_name: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    collect_obj: [{
        required: true,
        type: 'array',
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    partition: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],
    proxy_user: [{
        required: true,
        trigger: ['change', 'blur'],
        message: $t('common.notEmpty'),
    }],

});
const curRules = computed(() => {
    if (drawerType.value === 'display') {
        return {};
    }
    return rules.value;
});

// 可接受类型
const acceptType = computed(() => {
    switch (data.value.impl_type) {
        // jar
        case ACCEPT_TYPE_MAP.JAR:
            return '.jar';
        // scala
        case ACCEPT_TYPE_MAP.SCALA:
            return '.scala';
        // python
        case ACCEPT_TYPE_MAP.PYTHON:
            return '.py';
        default:
            return '.jar,.scala,.py';
    }
});
const columnList = ref([]);


const columnParams = computed(() => {
    if (data.value?.collect_obj?.length > 0 && data.value.proxy_user) {
        return true;
    }
    return false;
});
const getcolumnList = async () => {
    if (columnParams.value) {
        try {
            const params = {
                cluster_name: data.value?.collect_obj[0] ?? '',
                db_name: data.value?.collect_obj[1] ?? '',
                table_name: data.value?.collect_obj[2] ?? '',
                proxy_user: data.value.proxy_user,
                start_index: 0,
                page_size: 2147483647,

            };
            const res = await request('/api/v1/projector/meta_data/column', params);
            columnList.value = res.data;
        } catch (err) {
            columnList.value = [];
        }
    }
};
watch(
    columnParams,
    getcolumnList,
    { deep: true },
);
onMounted(async () => {
    console.log('初始数据', data.value);
    if (!data.value?.collect_configs?.length) {
        data.value.collect_configs = [{
            collect_type: true,
            calcu_unit_configs: [cloneDeep(initConfigInfo)],
        }];
    }
    await handleClusterChange();
});

</script>
<style lang="less" scoped>
.wrap {
    padding: 16px;
    border: 1px solid #cfd0d3;
    border-radius: 4px;
    margin-bottom: 16px;
    margin-right: 8px;
}
.add-sign {
    color: #5384FF;
    .add-sign-btn {
        display: flex;
        align-items: center;
        justify-content: flex-start;
        cursor: pointer;
    }
}
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    color: #0f1222;
    font-weight: bold;
    .delete-btn {
        cursor: pointer;
        display: flex;
        align-items: center;
        color:#93949B;
        font-weight: 400;
    }
}
.view-font {
    font-family: PingFangSC-Regular;
    font-size: 14px;
    color: #0f1222;
    line-height: 22px;
    font-weight: 400;
}
.rule-detail-form .fes-select, .rule-detail-form .fes-input, .rule-detail-form .fes-textarea {
    max-width: 800px;
}

/deep/ .preview {
    .fes-form-item-content {
        background-color: #f8f8f8;
        padding: 0 16px;
        border-radius: 4px;
    }
}

</style>
