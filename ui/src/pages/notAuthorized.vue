<template>
    <BPageLoading
        actionType="noPermissions"
        loadingText="暂无权限"
        @logout="logout"
    ></BPageLoading>
</template>
<script setup>
import { useI18n, request as FRequest, useRouter } from '@fesjs/fes';
import { BPageLoading } from '@fesjs/traction-widget';

const { t: $t } = useI18n();
const router = useRouter();
if (sessionStorage.getItem('firstUserName') && sessionStorage.getItem('firstUserName') !== 'errorauth') {
    router.push({ path: '/dashboard' });
}
// 普通退出
const logout = async () => {
    sessionStorage.clear();
    try {
        await FRequest('auth/common/logout', {}, 'get');
    } catch (error) {
        console.warn(error);
    }
};
</script>
