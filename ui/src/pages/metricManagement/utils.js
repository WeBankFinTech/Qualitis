import { locale } from '@fesjs/fes';

const { t: $t } = locale;
// 计算模式下拉列表
export const calculations = [{ label: $t('indexManagement.offline'), value: 'offline' }, { label: $t('indexManagement.realtime'), value: 'realtime' }];
