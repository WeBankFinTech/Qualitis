<template>
    <div class="create-table-structure">
        <FModal
            v-model:show="showModal"
            title="创建表结构"
            displayDirective="if"
            width="550px"
            :maskClosable="false"
            @ok="saveTableStructure"
            @cancel="cancel"
        >
            <FForm ref="structRef" :model="tableStructure" :rules="rules" :labelWidth="80" labelPosition="right">
                <FFormItem label="文件ID" prop="file_id">
                    <FInput
                        v-model="tableStructure.file_id"
                        class="form-edit-input"
                        placeholder="请输入文件ID"
                    />
                </FFormItem>
                <FFormItem label="哈希值" prop="file_hash_values">
                    <FInput
                        v-model="tableStructure.file_hash_values"
                        class="form-edit-input"
                        placeholder="请输入哈希值"
                    />
                </FFormItem>
                <FFormItem label="分隔符" prop="file_delimiter">
                    <FSelect
                        v-model="tableStructure.file_delimiter"
                        class="form-edit-input"
                        placeholder="请选择分隔符"
                    >
                        <FOption
                            v-for="item in separatorList"
                            :key="item.id"
                            :value="item.value"
                            :label="item.label"
                        ></FOption>
                    </FSelect>
                </FFormItem>
                <FFormItem label="首行类型" prop="file_header">
                    <FCheckbox v-model="tableStructure.file_header" class="form-edit-input">首行为表头</FCheckbox>
                </FFormItem>
                <div class="data-save">
                    <FFormItem label="数据储存" prop="db_name" class="database">
                        <FSelect
                            v-model="tableStructure.db_name"
                            filterable
                            clearable
                            class="form-edit-input"
                            placeholder="请选择存储数据库"
                            labelField="db_name"
                            valueField="db_name"
                            :options="databaseList" />
                    </FFormItem>
                    <FFormItem label="table" prop="table_name" class="table">
                        <FInput
                            v-model="tableStructure.table_name"
                            class="form-edit-input"
                            placeholder="请为存储数据表命名"
                        />
                    </FFormItem>
                </div>
                <FFormItem label="文件类型" prop="file_type">
                    <FSelect
                        v-model="tableStructure.file_type"
                        class="form-edit-input"
                        placeholder="请选择文件存储类型"
                    >
                        <FOption
                            v-for="(item, index) in fileTypeList"
                            :key="index"
                            :value="item"
                            :label="item"
                        ></FOption>
                    </FSelect>
                </FFormItem>
                <FFormItem label="表结构" prop="file_table_desc">
                    <div class="form-edit-input table-structure">
                        <div v-for="(char, index) in tableStructure.file_table_desc" :key="index" class="table-char-list">
                            <FInput v-model="char.column_name" placeholder="请输入字段名称">
                                <template #prepend>
                                    <FSelectCascader
                                        v-model="char.charType"
                                        :data="charTypeList"
                                        placeholder="字段类型"
                                        style="width: 120px;"
                                        class="prepend-cas"
                                        @change="charTypeChange(index, char.charType)"
                                    >
                                    </FSelectCascader>
                                </template>
                            </FInput>
                            <PlusCircleFilled v-if="index === tableStructure.file_table_desc.length - 1" class="add" @click="add(index)" />
                            <MinusCircleOutlined v-else-if="tableStructure.file_table_desc.length > 1" class="del" @click="del(index)" />
                        </div>
                    </div>
                </FFormItem>
            </FForm>
        </FModal>
    </div>
</template>

<script>
/* eslint-disable camelcase */
import { useI18n } from '@fesjs/fes';
import {
    ref, computed, onMounted, inject,
} from 'vue';
import { FMessage } from '@fesjs/fes-design';
import { PlusCircleFilled, MinusCircleOutlined } from '@fesjs/fes-design/es/icon';
import eventbus from '@/common/useEvents';
import { cloneDeep } from 'lodash-es';
import { timeFormatList } from '../utils/index';

// props: 数据源数据, 打开类型（新增、编辑、详情）
export default {
    components: {
        PlusCircleFilled, MinusCircleOutlined,
    },
    props: {
        show: {
            type: Boolean,
            default: false,
        },
        /**
         * file_id
         * file_hash_values
         * file_delimiter
         * file_header
         * database
         * table
         * file_type
         * file_table_desc Array<{column_name, charType}>
         */
        form: {
            type: Object,
            default: () => ({}),
        },
        /**
         * Array<string>
         */
        target: {
            type: String,
            default: 'single',
        },
        index: {
            type: Number,
            default: 0,
        },
    },
    setup(props, context) {
        const { t: $t } = useI18n();
        const showModal = computed({
            get() {
                return props.show;
            },
            set(value) {
                context.emit('update:show', value);
            },
        });
        const tableStructure = ref({});
        const databaseList = inject('dbs');
        const separatorList = ref([
            { id: 1, label: '逗号(,)', value: ',' },
            { id: 2, label: '竖杠(|)', value: '|' },
            { id: 3, label: '空格( )', value: '*' },
        ]);
        const fileTypeList = ref(['.csv', '.txt', '.xls', '.xlsx', '.gz']);
        const originCharTypeList = ['string', 'int', 'bigint', 'smallint', 'tinyint', 'double', 'float', 'boolean', 'decimal', 'timestamp', 'date', 'char', 'varchar'].map((type) => {
            if (type !== 'date') return { value: type, label: type };
            return {
                value: 'date',
                label: 'date',
                children: [{
                    value: 'yyyyMMdd',
                    label: 'yyyyMMdd',
                }, {
                    value: 'yyyy-MM-dd',
                    label: 'yyyy-MM-dd',
                }, {
                    value: 'yyyy.MM.dd',
                    label: 'yyyy.MM.dd',
                }, {
                    value: 'yyyy/MM/dd',
                    label: 'yyyy/MM/dd',
                }],
            };
        });
        const charTypeList = ref(originCharTypeList);

        const structRef = ref(null);
        const rules = ref({
            file_id: [{
                required: true,
                trigger: 'blur',
                message: $t('common.notEmpty'),
            }],
            file_hash_values: [{
                required: true,
                trigger: 'blur',
                message: $t('common.notEmpty'),
            }],
            file_delimiter: [{
                required: true,
                trigger: 'change',
                message: $t('common.notEmpty'),
            }],
            db_name: [{
                required: true,
                trigger: 'change',
                message: $t('common.notEmpty'),
            }],
            table_name: [{
                required: true,
                trigger: 'change',
                message: $t('common.notEmpty'),
            }],
            file_type: [{
                required: true,
                trigger: 'change',
                message: $t('common.notEmpty'),
            }],
        });
        function charTypeChange(index, val) {
            console.log('charTypeChange', index, val);
            if (timeFormatList.includes(val)) {
                tableStructure.value.file_table_desc[index].type = 'date';
                tableStructure.value.file_table_desc[index].data_type = `date_${timeFormatList.indexOf(val)}`;
            } else {
                tableStructure.value.file_table_desc[index].type = val;
                tableStructure.value.file_table_desc[index].data_type = val;
            }
        }

        function add(index, returnflag) {
            // 需要校验当前列
            // 先判断是否满足条件，是否为空或者重复
            const {
                column_name,
                type,
                data_type,
            } = tableStructure.value.file_table_desc[index];
            // 判断是否为空
            if (!column_name || !type) {
                FMessage.warning('列名称和列类型不能为空！');
                return false;
            }
            // 判断是包含特殊字符和中文等
            if (!/^\w*$/.test(column_name) || !/^\w*$/.test(type)) {
                FMessage.warning('不能包含空格，空，特殊符号，中文...');
                return false;
            }
            // 判断是否重复
            const flag = tableStructure.value.file_table_desc.some((item, i) => (index !== i) && (item.column_name === column_name) && (item.data_type === data_type));
            if (flag) {
                FMessage.warning('列名称和列类型不能重复！');
                return false;
            }
            if (returnflag) return true;
            tableStructure.value.file_table_desc.push({});
        }

        function del(index) {
            tableStructure.value.file_table_desc.splice(index, 1);
        }

        const valid = async () => {
            try {
                const result = await structRef.value.validate();
                console.log('structRef表单验证成功: ', result);
                return true;
            } catch (error) {
                console.log('structRef表单验证失败: ', error);
                return false;
            }
        };

        async function saveTableStructure() {
            console.log('saveTableStructure');
            if (!await valid()) return;
            for (let index = 0; index < tableStructure.value.file_table_desc.length; index++) {
                if (!add(index, true)) return;
            }
            tableStructure.value.file_table_desc = tableStructure.value.file_table_desc.map(item => ({ ...item, column_name_with_type: `${item.column_name} (${item.data_type})` }));
            eventbus.emit('UPDATE_COLUMN_LIST', { data: tableStructure.value.file_table_desc, target: props.target });
            // 通知VerifyObject更新FPS表单相关数据
            eventbus.emit('TABLE_STRUCTURE_CHANGE', [tableStructure.value, props.target, props.index]);
            showModal.value = false;
        }
        function cancel() {
            console.log('cancel', showModal.value);
            showModal.value = false;
        }
        onMounted(() => {
            tableStructure.value = cloneDeep(props.form);
            // 通过规则模版创建-高级设置时会传一个空数组，需要初始化一下
            if (!Array.isArray(tableStructure.value.file_table_desc) || tableStructure.value.file_table_desc.length === 0) tableStructure.value.file_table_desc = [{ column_name: '', charType: '' }];
        });
        return {
            tableStructure,
            showModal,
            separatorList,
            databaseList,
            fileTypeList,
            charTypeList,

            charTypeChange,
            add,
            del,
            rules,
            structRef,

            saveTableStructure,
            cancel,
        };
    },
};
</script>

<style lang="less">
.data-save {
    display: flex;
    .database {
        flex: 1;
        padding-right: 10px;
    }
    .table {
        label {
            display: none;
        }
    }
}
.table-structure {
    width: 100%;
}
.table-char-list{
    display: flex;
    align-items: center;
    margin-bottom: 10px;
    .fes-design-icon{
        margin-left: 8px;
    }
}
.item {
    &.char {
        width: 98%;
        display: block;
        .line {
            display: flex;
            align-items: center;
        }
        .left {
            width: 96px;
            opacity: 0;
        }
        &.first {
            .left {
                opacity: 1;
            }
        }
        &.last {
            padding-bottom: 11px;
        }
    }
    .right {
        width: unset;
        flex: 1;
        display: flex;
        align-items: center;
        .img {
            margin-left: 9px;
            display: flex;
            .add, .del {
                width: 15px;
                height: 15px;
                transform: scale(1.2);
                cursor: pointer;
            }
            .add {
                color: #5384FF;
            }
        }
    }
}
</style>
