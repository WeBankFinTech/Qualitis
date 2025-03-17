<template>
    <!-- <micro-app name="dataLineage" :url="url" baseroute="/dataLineage"></micro-app> -->
    <iframe :src="url" style="width:100%; height: 100%" frameborder="0" sandbox="allow-forms allow-modals allow-popups allow-same-origin allow-scripts allow-top-navigation"></iframe>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import { request, useRoute } from '@fesjs/fes';
import { getURLQueryParams } from '@/common/utils';

const route = useRoute();
const url = ref('');
function generateUrlTail() {
    const tail = [];
    tail.push('app_id=facade-framework');
    tail.push('timestamp=1659924920342');
    tail.push('nonce=12345');
    tail.push('user=dqm');
    tail.push('signature=eca1a93c2c2bb8fc55972d76d0c1267c7782f51552a5d4562a2d871a63f64168');
    return tail.join('&');
}
const loginUserName = sessionStorage.getItem('firstUserName');
// eslint-disable-next-line no-undef
const microAppUrl = BASEMICROURL;
async function initMicroApp() {
    try {
        // 查询绑定信息
        const data = await request(`/mfgov/fesdk/bindQuery/v1?${generateUrlTail()}`, {
            main_mf_name: 'dqm_rule_management',
            sub_mf_name: 'data_app_analysis',
        }, {
            method: 'get',
            baseURL: microAppUrl,
            headers: {
                proxyUser: loginUserName,
            },
        });
        console.log(data);
        url.value = `${data.accessLocation}?baseUrl=${data.accessLocation.split('#')[0].replace(/\/$/, '')}` || '';
        const params = route.query;
        url.value += `&${getURLQueryParams({ params })}`;
        console.log('url.value', url.value);
    } catch (err) {
        console.warn('-------', err);
    }
}
onMounted(async () => {
    await initMicroApp();
});

</script>
