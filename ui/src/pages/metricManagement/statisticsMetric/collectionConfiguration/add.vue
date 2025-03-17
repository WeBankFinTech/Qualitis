<template>
    <div id="collectAdd" style="padding: 23px 16px 32px;">
        <div class="header">
            <div class="item-center"><LeftOutlined style="margin-right: 5px;" @click="goBack" /></div>
            <div>{{$t('_.采集配置管理')}}</div>
            <div style="margin: 0 4px;font-size: 20px;padding-bottom: 2px;">/</div>
            <div style="font-weight: bold;">{{$t('_.新增指标采集配置')}}</div>
        </div>
        <div style="background-color: #fff;">
            <FScrollbar ref="scrollbarRefEl" height="760px" style="width: 100%">
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
            </FScrollbar>
            <div class="btn-bottom">
                <FButton type="primary" style="margin-right: 16px" @click="submitsave">{{$t('_.保存')}}</FButton>
                <FButton @click="goBack">{{$t('_.取消')}}</FButton>
            </div>
        </div>
    </div>
</template>
<script setup>

import { LeftOutlined, PlusCircleOutlined } from '@fesjs/fes-design/es/icon';
import { FTimePicker, FMessage } from '@fesjs/fes-design';
import {
    useI18n, request, useRouter, useRoute,
} from '@fesjs/fes';
import {
    ref, defineProps, defineEmits, computed, watch, onBeforeUnmount, onMounted,
} from 'vue';
import CollectGroup from './components/collectGroup';
import useBasicOptions from './hooks/useBasicOptions';


const { t: $t } = useI18n();

const {
    clusterVivisions,
    templateList,
} = useBasicOptions();
const router = useRouter();
const route = useRoute();
const goBack = () => {
    router.back();
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
        goBack();
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
onBeforeUnmount(() => {
    window.onresize = null;
    containerDom.setAttribute('class', 'right-container');
});

</script>
<style lang="less">
.right-container {
    flex: 1 1;
    overflow-y: hidden;
    > div {
        height: 100%;
    }
}
</style>
<style lang='less' scoped>
.wd-title {
    margin: 16px;
    display: flex;
    font-family: PingFangSC-Medium;
    font-size: 18px;
    color: #0F1222;
    text-align: justify;
    line-height: 26px;
    font-weight: 500;
    display: flex;
    justify-content: space-between;
}
.header {
    display: flex;
    align-items: center;
    font-size: 16px;
    margin-bottom: 21px;
}
.item-center{
    display: flex;
    align-items: center;
    justify-content: center;
}
.add-body {
    background-color: #fff;
    padding: 24px;
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

</style>
