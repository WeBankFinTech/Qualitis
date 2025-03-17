<template>
    <div>
        <micro-app v-if="url" class="microAppClass" name="sqlProject" :url="url" :data="baseUrlData" default-page="#/app-manage/application/sql"></micro-app>
    </div>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import { request, useRouter } from '@fesjs/fes';

const router = useRouter();
const baseUrlData = ref({
    baseUrl: '',
    parents: 'qualities',
});
const url = ref('');
function generateUrlTail() {
    const tail = [];
    tail.push('app_id=facade-framework');
    tail.push('timestamp=1659924920342');
    tail.push('nonce=12345');
    tail.push('signature=eca1a93c2c2bb8fc55972d76d0c1267c7782f51552a5d4562a2d871a63f64168');
    return tail.join('&');
}
const loginUserName = sessionStorage.getItem('firstUserName');
// eslint-disable-next-line no-undef
const microAppUrl = BASEMICROURL;
async function initMicroApp() {
    try {
        // 查询绑定信息
        const data = await request(`/mfgov/fesdk/bindQuery/v2?${generateUrlTail()}`, {
            main_mf_env: 'prd',
            sub_mf_env: 'prd',
            main_mf_name: 'dqm_realtime_proj_flink_sql_manage',
            sub_mf_name: 'rcs_realtime_flink_sql_config',
        }, {
            method: 'get',
            baseURL: microAppUrl,
            headers: {
                proxyUser: loginUserName,
            },
        });
        console.log(data);
        url.value = `${data.accessLocation.split('#')[0].replace(/\/$/, '')}` || '';
        baseUrlData.value.baseUrl = `${data.accessLocation.split('#')[0].replace(/\/$/, '')}` || '';
    } catch (err) {
        console.warn('-------', err);
    }
}

onMounted(async () => {
    initMicroApp();
});

</script>
<style lang="less" scoped>
.microAppClass {
    height: calc(100vh - 110px);
    overflow-y: auto;
}
</style>
