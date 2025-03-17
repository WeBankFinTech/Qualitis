<template>
    <FDrawer
        v-model:show="addShow"
        displayDirective="if"
        :title="$t('_.新增采集配置')"
        width="65%"
        :footer="true"
        :oktext="$t('_.保存')"
        @cancel="handleClose"
        @ok="submitsave">
        <!-- <Add></Add> -->
        <div class="add-body">
            <div class="selection">
                <CollectGroup
                    v-for="(item, index) in addList"
                    :ref="el => { if (el) inputMetaRefs[index] = el }"
                    :key="item"
                    v-model:inputCollectData="addList[index]"
                    :removable="addList.length > 1"
                    :index="index + 1"
                    :templateList="templateList"
                    :proxyUserList="proxyUserList"
                    :clusterVivisions="clusterVivisions"
                    @delete="deleteInputMeta(index)"
                />
                <div class="add-sign">
                    <span class="add-sign-btn" @click="addInputMeta">
                        <PlusCircleOutlined style="padding-right: 4.58px;" />{{$t('_.添加采集配置组')}}</span>
                </div>
            </div>
        </div>
    </FDrawer>
</template>
<script setup>

import {
    ref, defineProps, computed, defineEmits, onMounted,
} from 'vue';
import { PlusCircleOutlined } from '@fesjs/fes-design/es/icon';
import { FMessage } from '@fesjs/fes-design';
import {
    useI18n, request, useRouter, useRoute,
} from '@fesjs/fes';
import Add from './add.vue';
import CollectGroup from './components/collectGroup';
import useBasicOptions from './hooks/useBasicOptions';


const { t: $t } = useI18n();

const props = defineProps({
    isAddShow: {
        type: Boolean,
        required: true,
    },
});
const emit = defineEmits(['addSubmit', 'update:isAddShow']);
const addShow = computed({
    get() {
        return props.isAddShow;
    },
    set(value) {
        emit('update:isAddShow', value);
    },
});
const {
    clusterVivisions,
    templateList,
} = useBasicOptions();
const router = useRouter();
const route = useRoute();

const handleClose = () => {
    addShow.value = false;
};
const scrollbarRefEl = ref(null);
const inputMetaRefs = ref([]);
const addList = ref([{
    collect_type: true,
    collect_configs: [{
        collect_type: true,
        calcu_unit_configs: [{
            template_id: null,
            columns: null,
        }],
    }],
}]);
const deleteInputMeta = (index) => {
    addList.value.splice(index, 1);
};
const addInputMeta = () => {
    addList.value.push({
        collect_type: true,
        collect_configs: [{
            collect_type: true,
            calcu_unit_configs: [{
                template_id: null,
                columns: null,
            }],
        }],
    });
    console.log('data.value.collect_configs', addList.value);
};
const isSubmitting = ref(false);
const submitsave = async () => {
    try {
        isSubmitting.value = true;
        const result = await Promise.all([...inputMetaRefs.value.map(item => item.valid())]);
        if (result.includes(false)) {
            isSubmitting.value = false;
            return;
        }
        console.log('params', addList.value);
        // 不能直接在data上处理，绑定了级联选择器组件操作对应数据会报错
        const params = addList.value.map(item => ({
            cluster_name: item.collect_obj[0],
            database: item.collect_obj[1],
            table: item.collect_obj[2],
            proxy_user: item.proxy_user,
            partition: item.partition,
            collect_configs: item.collect_configs,
        }));
        console.log('保存参数', addList.value, params);

        const res = await request('api/v1/projector/imsmetric/collect/create_group', params, 'post');
        isSubmitting.value = false;
        FMessage.success($t('_.录入成功'));
        handleClose();
    } catch (err) {
        console.warn(err);
        isSubmitting.value = false;
    }
};
const proxyUserList = ref([]);
const getProxyUserList = async () => {
    const res = await request('/api/v1/projector/proxy_user', {}, { method: 'get' });
    proxyUserList.value = res.filter(i => i).map(item => ({
        label: item,
        value: item,
    }));
};
let containerDom = {};
onMounted(async () => {
    getProxyUserList();
    containerDom = document.getElementsByClassName('right-container')[0];
    containerDom.setAttribute('class', 'right-container');
});

</script>
<style lang="less" scoped>
.add-body {
    background-color: #fff;
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
.btn-bottom {
    padding: 24px;
}
.item-center {
    display: flex;
    align-items: center;
    justify-content: center;
}
</style>
