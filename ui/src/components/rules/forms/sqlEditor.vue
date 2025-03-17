<template>
    <div class="editor-wrapper">
        <MonacoEditor
            v-model="formattedSql"
            height="360px"
            language="sql"
            theme="logview"
            :readOnly="props.readOnly"
        />
        <span v-if="formatted" style="display: block">{{$t('_.注：指标名请用 `` 包含，即 `指标名称`，注释请用 /*注释部分*/ 格式')}}</span>
    </div>
</template>

<script setup>

import { format } from 'sql-formatter';
import {
    defineProps, computed, defineEmits, ref, defineExpose,
} from 'vue';
import { MonacoEditor, useI18n } from '@fesjs/fes';
import {
    FMessage,
} from '@fesjs/fes-design';


const { t: $t } = useI18n();

const formatted = ref(false);
const props = defineProps({
    sql: String,
    readOnly: Boolean,
});
const emit = defineEmits(['update:sql', 'valid']);

const formattedSql = computed({
    get() {
        return props.sql;
    },
    set(value) {
        emit('update:sql', value);
        emit('valid');
    },
});

function formatSql() {
    if (props.sql) {
        try {
            formattedSql.value = format(props.sql, {
                language: 'sql',
                tabWidth: 4,
                paramTypes: {
                    custom: [{ regex: '\\$\\{[a-zA-Z0-9_]+\\}' }],
                },
            });
            formatted.value = true;
        } catch (err) {
            console.warn(err);
            FMessage.error($t('_.请检查SQL语句'));
        }
    }
}
defineExpose({ formatSql });

</script>

<style lang="less" scoped>
.editor-wrapper {
  position: relative;
  width: 100%;
}
</style>
