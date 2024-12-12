<template>
    <div>
        <div v-if=" mappings?.length > 0 || ruleData.currentProject.editMode !== 'display'" class="rule-form-table">
            <table class="form-table cross-table">
                <thead class="table-header">
                    <th>左侧表达式</th>
                    <th>关系</th>
                    <th>右侧表达式</th>
                    <th v-if="ruleData.currentProject.editMode === 'edit'">操作</th>
                </thead>
                <tbody>
                    <tr v-for="(m, index) in mappings" :key="index" class="table-row">
                        <td>{{m.left_statement}}</td>
                        <td>{{operationList[m.operation - 1]}}</td>
                        <td>{{m.right_statement}}</td>
                        <td v-if="ruleData.currentProject.editMode !== 'display'">
                            <a class="btn" href="javascript:;" @click="editMap(index, m)">编辑</a>
                            <a class="btn del" href="javascript:;" @click="delMap(index)">删除</a>
                        </td>
                    </tr>
                </tbody>
            </table>
            <div v-if="ruleData.currentProject.editMode !== 'display'" class="table-operation cross-table">
                <FButton type="info" @click="addMap"><PlusOutlined />添加映射关系</FButton>
                <AddMapModal v-model:show="mapShowModal" v-model:form="mapForm" :type="mapEditType" :modelTitle="curModelTitle" @saved="mapSaved" @cancel="cancel" />
            </div>
        </div>
        <div v-else>--</div>
    </div>
</template>
<script setup>
import { FMessage } from '@fesjs/fes-design';
import { PlusOutlined } from '@fesjs/fes-design/icon';
import {
    defineProps, computed, ref, defineEmits, watch,
} from 'vue';
import { useI18n } from '@fesjs/fes';
import { useStore } from 'vuex';
import AddMapModal from '@/components/rules/forms/AddMapModal.vue';
import { getColumns } from '@/components/rules/utils/datasource';
import { useListener, buildColumnData } from '@/components/rules/utils';
import { useFormModel } from '@/components/rules/hook/useModel';

const { t: $t } = useI18n();
const props = defineProps({
    mappings: {
        type: Array,
        required: true,
        default: [],
    },
    title: {
        type: String,
        required: true,
        default: '映射关系',
    },
    datasourceGroupData: {
        type: Object,
    },
});
const emit = defineEmits(['update:mappings', 'mapSaved', 'valid']);
const mappings = computed({
    get() {
        return props.mappings;
    },
    set(value) {
        emit('update:mappings', value);
    },
});
const datasourceGroupData = computed(() => props.datasourceGroupData);
const curModelTitle = ref('');
const operationList = ['=', '!=', '>', '>=', '<', '<='];
const store = useStore();
const ruleData = computed(() => store.state.rule);
const curEditMap = ref(-1);
const mapEditType = ref('add');
const mapForm = ref({});
const mapFormFieldMap = {
    left: {
        columns: 'leftGroup',
        table: 'leftTableName',
    },
    right: {
        columns: 'rightGroup',
        table: 'rightTableName',
    },
};
const mapShowModal = ref(false);

const validData = (config) => {
    if (!config.source.type || !config.target.type) {
        FMessage.error('请完善数据源类型信息');
        return false;
    }
    if ((!config.source.type || !config.target.type) && !config.isUpStream) {
        FMessage.error('请完善数据库信息');
        return false;
    }
    if (!config.source.table_name || !config.target.table_name) {
        FMessage.error('请完善数据表信息');
        return false;
    }
    if (['hive', 'fps'].includes(config.source.type.toLowerCase()) || ['hive', 'fps'].includes(config.target.type.toLowerCase())) {
        return true;
    }
    if (!config.source.linkis_datasource_id || !config.target.linkis_datasource_id) {
        FMessage.error('请完善数据源名称');
        return false;
    }
    return true;
};

async function initMapForm(config, flag) {
    const fieldMap = mapFormFieldMap[flag];
    if (config.type.toLowerCase() === 'fps') {
        if (typeof (config.file_table_desc) === 'string') {
            const fileTableDesc = buildColumnData(config.file_table_desc);
            mapForm.value[fieldMap.columns] = fileTableDesc || [];
        } else {
            mapForm.value[fieldMap.columns] = config.file_table_desc;
        }
        mapForm.value[fieldMap.table] = config?.table_name;
    } else {
        // store中clustname数据不固定，初始化时source和target中无cluster,只能从currentRuleDetail取
        // cluster数据更新时，又只有source和target中出现最新cluster数据，currentRuleDetail中的cluster数据未更新，优先取source
        // mapForm.value[fieldMap.columns] = await getColumns({
        //     clusterName: config.cluster_name || ruleData.value.currentRuleDetail.cluster_name,
        //     proxyUser: config.proxy_user,
        //     dataSourceType: config.type,
        //     dataSourceId: config.linkis_datasource_id,
        //     dbName: config.db_name,
        //     tableName: config.table_name,
        //     upstreamParams: props.upstream,
        // });
        // console.log(mapForm.value[fieldMap.columns]);
        mapForm.value[fieldMap.table] = config.table_name;
    }
}
async function initMap() {
    // 添加和新增时依赖的上游表数据从stor中的source和targe取,连接字段和对比字段依赖的上游表相同
    await Promise.all([
        initMapForm(ruleData.value.currentRuleDetail.source, 'left'),
        initMapForm(ruleData.value.currentRuleDetail.target, 'right'),
    ]);
}

async function editMap(index, map) {
    curEditMap.value = index;
    mapEditType.value = 'edit';
    await initMap();
    mapForm.value = {
        ...mapForm.value,
        left_statement: map.left_statement,
        relationshipBetween: map.operation,
        right_statement: map.right_statement,
    };
    curModelTitle.value = `编辑${props.title}`;
    mapShowModal.value = true;
}

function delMap(index) {
    mappings.value.splice(index, 1);
    console.log('delete-curMapping', mappings);
    emit('valid');
}

async function addMap() {
    try {
        mapEditType.value = 'add';
        // 直接校验stor中的规则详情数据
        if (!ruleData.value.currentRuleDetail.cluster_name && !ruleData.value.currentRuleDetail.source.cluster_name) {
            FMessage.error('请完善任务执行集群信息');
            return;
        }
        if (!validData(ruleData.value.currentRuleDetail)) {
            return;
        }
        await initMap();
        mapForm.value = {
            ...mapForm.value,
            left_statement: '',
            relationshipBetween: '',
            right_statement: '',
        };
        curModelTitle.value = `添加${props.title}`;
        mapShowModal.value = true;
    } catch (err) {
        console.warn(err);
    }
}

function mapSaved(map) {
    console.log(props.title, 'title');
    const m = {
        left_statement: map.left_statement,
        operation: map.relationshipBetween,
        right_statement: map.right_statement,
        left: map.left,
        right: map.right,
    };
    if (mapEditType.value === 'edit') {
        mappings.value[curEditMap.value] = m;
    } else {
        mappings.value.push(m);
    }
    emit('mapSaved');
    emit('valid');
    console.log('save-curMapping', mappings.value);
}
function cancel() {
    emit('valid');
}
useListener('crossTableCheck:resetMappings', () => {
    mappings.value.splice(0, mappings.value.length);
});
// useListener('UPDATE_COLUMN_LIST', () => {
//     mappings.splice(0, mappings.length);
// });
watch(datasourceGroupData, () => {
    mapForm.value.leftGroup = datasourceGroupData.value.leftGroup;
    mapForm.value.rightGroup = datasourceGroupData.value.rightGroup;
}, { immediate: true, deep: true });
</script>
