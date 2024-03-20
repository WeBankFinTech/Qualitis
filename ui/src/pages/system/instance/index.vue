<template>
    <div class="wd-content engine-config" style="padding: 16px 16px 32px">
        <BTablePage :isLoading="showLoading" actionType="loading" :loadingText="{ loading: '' }">
            <template v-slot:search>
                <BSearch :form="{}" :isReset="false" @search="search">
                    <template v-slot:form>
                        <div>
                            <span class="condition-label">{{$t('instanceConfig.instance')}}</span>
                            <FInput v-model="instance" :placeholder="$t('instanceConfig.placeholderInput')" />
                        </div>
                    </template>
                </BSearch>
            </template>
            <template v-slot:operate>
                <FButton type="primary" class="operation-item" @click="newInstance">
                    <PlusOutlined />
                    {{$t('instanceConfig.newInstance')}}
                </FButton>
            </template>
            <template v-slot:table>
                <f-table ref="instanceTable" :data="instanceData" :no-data-text="$t('common.noData')">
                    <template #empty>
                        <BPageLoading :actionType="resultByInit ? 'emptyInitResult' : 'emptyQueryResult'" />
                    </template>
                    <f-table-column :formatter="formatterEmptyValue" prop="ip" :label="$t('instanceConfig.instance')" :minWidth="130" ellipsis></f-table-column>
                    <f-table-column prop="status" :label="$t('instanceConfig.status')" :formatter="formatterStatus" :minWidth="88" ellipsis></f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" prop="updatingApplicationNum" :label="$t('instanceConfig.updatingApplicationNum')" :minWidth="116" ellipsis></f-table-column>
                    <f-table-column :formatter="formatterEmptyValue" prop="tenant_user_name" :label="$t('tenantManagePage.tenantUserName')" :minWidth="130" ellipsis></f-table-column>
                    <f-table-column prop="create_user" :label="$t('instanceConfig.create_user')" :minWidth="120" ellipsis />
                    <f-table-column prop="create_time" :label="$t('instanceConfig.create_time')" :minWidth="180" ellipsis />
                    <f-table-column prop="modify_user" :label="$t('instanceConfig.modify_user')" :minWidth="120" ellipsis />
                    <f-table-column prop="modify_time" :label="$t('instanceConfig.modify_time')" :minWidth="180" ellipsis />
                    <f-table-column #default="{ row } = {}" :label="$t('instanceConfig.operate')" :minWidth="148" ellipsis fixed="right">
                        <ul class="wd-label-btns">
                            <li :class="row.status === 1 ? 'disabled' : '' " @click="handler(row, 'online')">{{$t('instanceConfig.online')}}</li>
                            <li :class="row.status === 4 ? 'disabled' : '' " @click="handler(row, 'offline')">{{$t('instanceConfig.offline')}}</li>
                            <li class="delete" @click="handler(row, 'delete')">{{$t('instanceConfig.delete')}}</li>
                        </ul>
                    </f-table-column>
                </f-table>
            </template>
            <template v-slot:pagination>
                <FPagination
                    v-model:pageSize="instancePagination.size"
                    v-model:currentPage="instancePagination.current"
                    show-size-changer
                    show-quick-jumper
                    show-total
                    :total-count="instanceTotal"
                    class="pagination"
                    @change="instancePageChange"
                    @pageSizeChange="instancePageSizeChange"
                ></FPagination>
            </template>
        </BTablePage>
        <!-- 新增实例 -->
        <FModal
            :show="showNewInstanceModal"
            :title="$t('instanceConfig.newInstance')"
            :width="484"
            :okText="$t('common.ok')"
            :cancelText="$t('common.cancel')"
            :maskClosable="false"
            @ok="instanceSave"
            @cancel="showNewInstanceModal = false"
        >
            <FForm
                ref="instanceFormRef"
                labelWidth="120px"
                labelPosition="right"
                :model="editInstanceForm"
                :rules="rules"
            >
                <FFormItem prop="instance" :rules="instanceRules">
                    <template #label><span>{{$t('instanceConfig.instanceAddress')}}</span></template>
                    <FInput
                        v-model="editInstanceForm.instance"
                        :placeholder="$t('instanceConfig.placeholderInstance')"
                    ></FInput>
                </FFormItem>
            </FForm>
        </FModal>
    </div>
</template>
<script setup>
import { ref, reactive } from 'vue';
import { FMessage, FModal } from '@fesjs/fes-design';

import { useI18n } from '@fesjs/fes';
import { PlusOutlined } from '@fesjs/fes-design/es/icon';
import { getLabelFromList } from '@/assets/js/utils';
import { CONDITIONBUTTONSPACE } from '@/assets/js/const';
import { formatterEmptyValue } from '@/common/utils';
import { BPageLoading, BTablePage, BSearch } from '@fesjs/traction-widget';
import {
    instanceConfigData, instanceConfigDelete, instanceConfigModify, instanceConfigAdd,
} from './api';

const { t: $t } = useI18n();

const showLoading = ref(false);
// 表格数据
const instanceData = ref([]);
const instanceTable = ref(null);
const instanceTotal = ref(0);
const instancePagination = ref({
    total: 0,
    current: 1,
    size: 10,
});

// 新增实例
const instanceFormRef = ref(null);
const showNewInstanceModal = ref(false);
const editInstanceForm = reactive({
    instance: '',
});
const instanceRules = [
    { required: true, message: $t('instanceConfig.placeholderInstance'), trigger: 'blur' },
    { pattern: /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/, message: $t('instanceConfig.instanceAddressRegTips') },
];
const newInstance = () => {
    showNewInstanceModal.value = true;
    instanceFormRef.value.resetFields(); // 表单重置
};

// 查询获取实例配置列表数据
const instance = ref('');

async function getInstanceData() {
    showLoading.value = true;
    const page = instancePagination.value.current - 1;
    const params = {
        ip: instance.value,
        page,
        size: instancePagination.value.size,
    };
    try {
        const res = await instanceConfigData(params);
        instanceTotal.value = res.total;
        instancePagination.value.total = Math.ceil(res.total / instancePagination.value.size);
        instanceData.value = res.data;
        showLoading.value = false;
    } catch (error) {
        console.error(error);
        instanceData.value = [];
        FMessage.error(error.data.message);
        showLoading.value = false;
    }
}
getInstanceData();
const resultByInit = ref(true); // 表格的结果是否是初始状态，搜索后该值变为false
// 查询
function search() {
    getInstanceData();
    resultByInit.value = false;
}

// 分页查询
const instancePageChange = () => {
    getInstanceData();
};
const instancePageSizeChange = () => {
    instancePagination.value.current = 1;
    getInstanceData();
};


// 新增保存
function instanceSave() {
    showLoading.value = true;
    const params = {
        ip: editInstanceForm.instance,
    };
    instanceConfigAdd(params).then(() => {
        FMessage.success($t('instanceConfig.newSuccess'));
        showNewInstanceModal.value = false;
        getInstanceData();
    }).catch((err) => {
        console.log('error', err);
        FMessage.error(err.response.data.message);
    }).finally(() => {
        showLoading.value = false;
    });
}
// 状态筛选
const statuslist = [{ label: $t('instanceConfig.running'), value: 1 }, { label: $t('instanceConfig.hang'), value: 4 }];
const formatterStatus = ({ cellValue }) => getLabelFromList(statuslist, cellValue);

// 操作 上下线
const handlerOnOffline = (row, status) => {
    showLoading.value = true;
    const params = {
        id: row.id,
        status,
    };
    const message = status === 1 ? $t('instanceConfig.onlineSuccess') : $t('instanceConfig.offlineSuccess');
    instanceConfigModify(params).then(() => {
        FMessage.success(message);
        getInstanceData();
    }).catch((err) => {
        console.log('error', err);
        FMessage.error(err.response.data.message);
    }).finally(() => {
        showLoading.value = false;
    });
};


// 操作 删除
const handlerDelete = (row) => {
    showLoading.value = true;
    const params = {
        id: row.id,
    };
    instanceConfigDelete(params).then(() => {
        FMessage.success($t('instanceConfig.deleteSuccess'));
        getInstanceData();
    }).catch((err) => {
        console.log('error', err);
        FMessage.error(err.response.data.message);
    }).finally(() => {
        showLoading.value = false;
    });
};
// 操作
const handler = (row, operate) => {
    switch (operate) {
        case 'online':
            if (row.status === 4) {
                console.log('上线');
                FModal.confirm({
                    title: $t('instanceConfig.onlineConfirm'),
                    okText: $t('common.ok'),
                    cancelText: $t('common.cancel'),
                    onOk() {
                        handlerOnOffline(row, 1);
                    },
                });
            }
            break;
        case 'offline':
            console.log('下线');
            if (row.status === 1) {
                console.log('下线');
                FModal.confirm({
                    title: $t('instanceConfig.offlineConfirm'),
                    okText: $t('common.ok'),
                    cancelText: $t('common.cancel'),
                    onOk() {
                        handlerOnOffline(row, 4);
                    },
                });
            }
            break;
        case 'delete':
            FModal.confirm({
                title: $t('instanceConfig.deleteConfirm'),
                okText: $t('common.ok'),
                cancelText: $t('common.cancel'),
                onOk() {
                    handlerDelete(row);
                },
            });
            break;
        default:
            break;
    }
};


</script>
<style lang="less" scoped>
@import "@/style/mixins";
.engine-config{
    .division {
        width: 100%;
        height: 1px;
        background: rgba(15,18,34,0.06);
        margin-bottom: 24px;
    }
    .new-btn{
        margin: 24px 0;
    }
    .fes-pagination{
        display: flex;
        justify-content: flex-end;
    }
    .wd-label-btns{
        display: flex;
        align-items: center;
        height: 100%;
        font-family: PingFangSC-Regular;
        font-size: 14px;
        color: #5384FF;
        line-height: 22px;
        font-weight: 400;
        li{
            margin-right: 16px;
            cursor: pointer;
            &.disabled{
                color: #B7B7BC;
                cursor: not-allowed;
            }
            &.delete{
                color: #F75F56;
            }
        }
    }
}
</style>
