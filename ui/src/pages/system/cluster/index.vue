<template>
    <div style="padding: 16px 16px 32px">
        <BTablePage :isLoading="showLoading" actionType="loading" :loadingText="{ loading: '' }">
            <template v-slot:search>
                <BSearch :form="{}" :isReset="false" @search="search">
                    <template v-slot:form>
                        <div>
                            <span class="condition-label">{{$t('system.clusterName')}}</span>
                            <FInput
                                v-model="clusterName"
                                clearable
                                :maxlength="50"
                                :placeholder="$t('system.searchCluster')"
                            />
                        </div>
                    </template>
                </BSearch>
            </template>
            <template v-slot:operate>
                <ActionBar :actions="actions" />
            </template>
            <template v-slot:table>
                <f-table class="table-container" row-key="cluster_info_id" :data="tableData">
                    <template #empty>
                        <BPageLoading :actionType="resultByInit ? 'emptyInitResult' : 'emptyQueryResult'" />
                    </template>
                    <f-table-column :formatter="formatterEmptyValue" ellipsis prop="cluster_name" :label="$t('configureParameterPage.clusterName')" :minWidth="192" />
                    <f-table-column :formatter="formatterEmptyValue" ellipsis prop="cluster_type" :label="$t('configureParameterPage.clusterType')" :minWidth="102" />
                    <f-table-column :formatter="formatterEmptyValue" ellipsis prop="linkis_address" :label="$t('common.linkis')" :minWidth="200" />
                    <f-table-column :formatter="formatterEmptyValue" ellipsis prop="linkis_token" label="Linkis Token" :minWidth="142" />
                    <f-table-column prop="create_user" :label="$t('optionManagePage.create_user')" :minWidth="120" ellipsis />
                    <f-table-column prop="create_time" :label="$t('optionManagePage.create_time')" :minWidth="180" ellipsis />
                    <f-table-column prop="modify_user" :label="$t('optionManagePage.modify_user')" :minWidth="120" ellipsis />
                    <f-table-column prop="modify_time" :label="$t('optionManagePage.modify_time')" :minWidth="180" ellipsis />
                    <f-table-column #default="{ row = {}}" :label="$t('common.operate')" :minWidth="104" fixed="right">
                        <ul class="wd-table-operate-btns">
                            <li class="btn-item" @click="edit(row, $event)">{{$t('common.edit')}}</li>
                            <li class="btn-item btn-delete" @click="del(row, $event)">{{$t('common.delete')}}</li>
                        </ul>
                    </f-table-column>
                </f-table>
            </template>
            <template v-slot:pagination>
                <FPagination
                    v-show="tableData.length > 0"
                    v-model:currentPage="pagination.current"
                    v-model:pageSize="pagination.size"
                    show-size-changer
                    show-total
                    :total-count="pagination.total"
                    @change="search(true)"
                    @pageSizeChange="search" />
            </template>
        </BTablePage>
    </div>
    <FModal
        v-model:show="showModal"
        :title="modelTitle"
        displayDirective="if"
        :maskClosable="false"
        @ok="save"
        @cancel="cancel"
    >
        <FForm ref="formRef" labelWidth="80px" labelPosition="right" :model="form" :rules="rules" style="padding: 20px 0 0;" labelClass="form-label">
            <FFormItem :label="`${$t('configureParameterPage.clusterName')}`" prop="cluster_name">
                <FInput v-model="form.cluster_name" :placeholder="$t('common.pleaseEnter')" />
            </FFormItem>
            <FFormItem :label="`${$t('configureParameterPage.clusterType')}`" prop="cluster_type">
                <FInput v-model="form.cluster_type" :placeholder="$t('common.pleaseEnter')" />
            </FFormItem>
            <FFormItem :label="`${$t('common.linkis')}`" prop="linkis_address">
                <FInput v-model="form.linkis_address" :placeholder="$t('common.pleaseEnter')" />
            </FFormItem>
            <FFormItem label="LinkisToken" prop="linkis_token">
                <FInput v-model="form.linkis_token" :placeholder="$t('common.pleaseEnter')" />
            </FFormItem>
            <FFormItem :label="`${$t('configureParameterPage.wtssConf')}`" prop="wtss_conf">
                <FInput v-model="form.wtss_conf" type="textarea" :rows="4" :placeholder="$t('common.pleaseEnter')" />
            </FFormItem>
            <FFormItem :label="`${$t('configureParameterPage.jobserverConf')}`" prop="jobserver_conf">
                <FInput v-model="form.jobserver_conf" type="textarea" :rows="4" :placeholder="$t('common.pleaseEnter')" />
            </FFormItem>
        </FForm>
    </FModal>
</template>
<script setup>
import {
    ref, reactive, onMounted, defineEmits,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import { FMessage, FModal } from '@fesjs/fes-design';
import { PlusOutlined, MoreCircleOutlined } from '@fesjs/fes-design/es/icon';
import { getClusterInfo, changeClusterInfo, deleteClusterInfo } from '@/pages/system/api';
import ActionBar from '@/pages/projects/components/projectActionBar';
import { CONDITIONBUTTONSPACE } from '@/assets/js/const';
import { BPageLoading, BTablePage, BSearch } from '@fesjs/traction-widget';
import { formatterEmptyValue } from '@/common/utils';

const { t: $t } = useI18n();
const showLoading = ref(false);
const showModal = ref(false);
const clusterName = ref('');
const tableData = ref([]);
const pagination = reactive({
    current: 1,
    size: 10,
    total: 0,
});
const method = ref('post');
const modelTitle = ref($t('configureParameterPage.addExecuteCluster'));
const resultByInit = ref(true); // 表格的结果是否是初始状态，搜索后该值变为false
async function search(flag) {
    try {
        showLoading.value = true;
        const result = await getClusterInfo({
            cluster_name: clusterName.value,
            page: flag === true ? pagination.current - 1 : 0,
            size: pagination.size,
        });
        tableData.value = result.data;
        pagination.total = result.total;
        showLoading.value = false;
        resultByInit.value = false;
    } catch (error) {
        console.error(error);
        showLoading.value = false;
    }
}

const formRef = ref(null);
const form = ref({
});

function edit(row, e) {
    console.log('edit', row, e);
    method.value = 'post';
    form.value.clusterId = row.cluster_info_id;
    form.value.cluster_name = row.cluster_name;
    form.value.cluster_type = row.cluster_type;
    form.value.linkis_address = row.linkis_address;
    form.value.linkis_token = row.linkis_token;
    form.value.wtss_conf = row.wtssJson;
    form.value.jobserver_conf = row.jobserverJson;
    modelTitle.value = $t('configureParameterPage.modifyExecuteCluster');
    showModal.value = true;
}

function del(row, e) {
    console.log('del', row, e);
    FModal.confirm({
        title: $t('common.prompt'),
        content: $t('configureParameterPage.deleteCurrentCluster', { name: row.cluster_name }),
        async onOk() {
            await deleteClusterInfo({ cluster_info_id: row.cluster_info_id });
            FMessage.success($t('toastSuccess.deleteSuccess'));
            search();
        },
    });
}

// 按钮配置
const actions = [
    {
        actionType: 'btn',
        type: 'primary',
        icon: PlusOutlined,
        label: $t('system.addCluster'),
        handler: () => {
            modelTitle.value = $t('configureParameterPage.addExecuteCluster');
            showModal.value = true;
            method.value = 'put';
        },
    },
    // {
    //     actionType: 'dropdown',
    //     type: 'default',
    //     icon: MoreCircleOutlined,
    //     label: $t('myProject.more'),
    //     trigger: 'click',
    //     options: [],
    // },
];

const rules = {
    cluster_name: [
        { required: true, message: $t('common.notEmpty'), trigger: ['blur', 'input'] },
    ],
    cluster_type: [
        { required: true, message: $t('common.notEmpty'), trigger: ['blur', 'input'] },
    ],
    linkis_address: [
        { required: true, message: $t('common.notEmpty'), trigger: ['blur', 'input'] },
    ],
    linkis_token: [
        { required: true, message: $t('common.notEmpty'), trigger: ['blur', 'input'] },
    ],
    wtss_conf: [
        { required: true, message: $t('common.notEmpty'), trigger: ['blur', 'input'] },
    ],
    jobserver_conf: [
        { required: true, message: $t('common.notEmpty'), trigger: ['blur', 'input'] },
    ],
};
async function valid() {
    try {
        await formRef.value.validate();
        return true;
    } catch (error) {
        return false;
    }
}
function reset() {
    form.value = {};
}
async function save() {
    if (!await valid()) return;
    console.log('save', form.value);
    try {
        showLoading.value = true;
        const params = {
            linkis_address: form.value.linkis_address,
            linkis_token: form.value.linkis_token,
            cluster_name: form.value.cluster_name,
            cluster_type: form.value.cluster_type,
            wtss_json: form.value.wtss_conf,
            jobserver_json: form.value.jobserver_conf,
        };
        let msg = $t('toastSuccess.addSuccess');
        if (method.value === 'post') {
            msg = $t('toastSuccess.editSuccess');
            params.cluster_info_id = form.value.clusterId;
        }
        const result = await changeClusterInfo(params, method.value);
        console.log('changeClusterInfo', result);
        FMessage.success(msg);
        showLoading.value = false;
        reset();
        showModal.value = false;
        search();
    } catch (error) {
        console.error(error);
        showLoading.value = false;
    }
}

function cancel() {
    showModal.value = false;
    reset();
}

onMounted(async () => {
    await search();
    resultByInit.value = true;
});

</script>
<style scoped lang="less">
:deep(.form-label) {
    text-align: right;
}
</style>
