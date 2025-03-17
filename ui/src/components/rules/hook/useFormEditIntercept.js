import {
    onBeforeRouteLeave,
    locale,
} from '@fesjs/fes';
import { FModal } from '@fesjs/fes-design';
import { isEqual } from '@/common/utils';


const $t = locale.t;
// import { unLockRule } from '@/components/rules/api/index';
// import { nextTick } from 'vue';

export function useFormEditIntercept(store) {
    onBeforeRouteLeave((to, from, next) => {
        const projectConfig = store.currentProject;
        if (isEqual(projectConfig.groupRuleEditMode, 'display')
            && isEqual(projectConfig.groupObjectEditMode, 'display')
            && isEqual(projectConfig.editMode, 'display')) {
            // 预览模式可以退出
            next();
        } else {
            FModal.confirm({
                title: $t('_.提示'),
                content: $t('_.直接退出会导致未保存的数据丢失，是否确认退出？'),
                okText: $t('_.确认退出'),
                cancelText: $t('_.取消'),
                closable: true,
                async onOk() {
                    // try {
                    //     // 普通规则组
                    //     if (store.currentRule.rule_id) {
                    //         await unLockRule({ rule_lock_id: store.currentRule.rule_id, rule_lock_range: 'rule' });
                    //     } else { // 表规则组
                    //         await unLockRule({ rule_lock_id: store.currentProject.groupId, rule_lock_range: 'table_group_datasource' });
                    //         await unLockRule({ rule_lock_id: store.currentProject.groupId, rule_lock_range: 'table_group_rules' });
                    //     }
                    // } catch (error) {
                    //     console.log(error);
                    // }
                    // nextTick();
                    next();
                },
                onCancel() {
                    next(false);
                },
            });
        }
    });
}
