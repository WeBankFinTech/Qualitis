<template>
    <FDrawer :show="show" :maskClosable="false" title="执行规则" :footer="true" width="50%" @cancel="cancel">
        <template #footer>
            <FSpace justify="start">
                <FButton type="primary" :loading="isLoading" @click="ok">确认执行</FButton>
                <FButton @click="cancel">{{$t('common.cancel')}}</FButton>
            </FSpace>
        </template>
        <div class="exec-config-panel">
            <FForm
                ref="form"
                labelPosition="right"
                :rules="formRules"
                style="padding: 0"
                :model="model"
                :labelWidth="88"
            >
                <FFormItem v-if="props.list.show" prop="rules" :label="props.list.label">
                    <FSelectTree
                        v-if="props.list.flag === '规则'"
                        ref="selectTreeRef"
                        v-model="model.rules"
                        multiple
                        filterable
                        :disabled="props.list.disabled"
                        :data="props.list.list"
                        :clearable="false"
                        cascade
                        checkStrictly="parent"
                        collapseTags
                        :collapseTagsLimit="2"
                    >
                    </FSelectTree>
                    <FSelect v-else v-model="model.rules" multiple filterable :clearable="false"
                             :options="props.list.list">
                    </FSelect>
                </FFormItem>
                <FFormItem prop="actor">
                    <template #label>
                        <span style="margin-right: 4px">
                            {{$t('common.runUser')}}
                        </span>
                        <FTooltip :content="$t('executationConfig.executationUserConfigDesc')">
                            <QuestionCircleOutlined />
                        </FTooltip>
                    </template>
                    <FSelect v-model="model.actor" filterable>
                        <FOption
                            v-for="v in userList"
                            :key="v"
                            :value="v"
                            :label="v"
                        ></FOption>
                    </FSelect>
                </FFormItem>
            </FForm>
            <div class="more-config-body">
                <!-- 动态引擎配置 -->
                <ExpansionPanel v-model="expansions[0]" class="panel">
                    <template v-slot:header>
                        <div class="panel-title">
                            <div>{{$t('executationConfig.dynamicEngineConfig')}}</div>
                            <FTooltip :content="$t('executationConfig.dynamicEngineConfigDesc')">
                                <QuestionCircleOutlined />
                            </FTooltip>
                        </div>
                    </template>
                    <template v-slot:body>
                        <!-- <Engine ref="engine" v-model:name="model.engineConfig.cluster_name" v-model:param="model.engineConfig.param" v-model:engineReuse="model.engineConfig.engine_reuse" />
                        <Spark ref="spark" v-model="model.sparkConfig" /> -->
                        <div>
                            <FForm ref="engineConfigRef"
                                   labelPosition="right"
                                   :model="model.engineConfig">
                                <FFormItem prop="cluster_name" :label="`${$t('common.switchCluster')}`">
                                    <FSelect v-model="model.engineConfig.cluster_name"
                                             :options="clusterList"
                                             filterable
                                             :clearable="false"></FSelect>
                                </FFormItem>
                            </FForm>
                        </div>

                        <!-- 引擎参数 -->
                        <div style="padding:0 16px">
                            <EngineParam
                                v-for="(param, index) in model.engineConfig.params"
                                :key="param"
                                :ref="el => { if (el) engineParamRefs[index] = el; }"
                                v-model:param="model.engineConfig.params[index]"
                                :index="index"
                                :paramTypeList="paramTypeList"
                                :params="model.engineConfig.params.map(item => item.param_name)"
                                @removeEngineParam="removeParam('engine', index)"
                            />
                        </div>

                        <div class="add-param" @click="addParam('engine')"><PlusCircleOutlined class="add-icon" /><a class="add-text" href="javascript:;">添加引擎参数</a></div>
                    </template>
                </ExpansionPanel>
                <!-- 执行变量配置 -->
                <ExpansionPanel v-model="expansions[1]" class="panel">
                    <template v-slot:header>
                        <div class="panel-title">
                            <div>{{$t('executationConfig.executationVaribleConfig')}}</div>
                            <FTooltip :content="$t('executationConfig.executationVaribleConfigDesc')">
                                <QuestionCircleOutlined />
                            </FTooltip>
                        </div>
                    </template>
                    <template v-slot:body>
                        <div style="padding:0 16px">
                            <Variable
                                v-for="(variable, index) in model.variables"
                                :key="variable"
                                :ref="el => { if (el) variableRefs[index] = el; }"
                                v-model:variable="model.variables[index]"
                                :index="index"
                                :variableTypeList="variableTypeList"
                                :variables="model.variables.map(item => item.var_name)"
                                @removeVariable="removeParam('var', index)"
                            />
                        </div>

                        <div class="add-param" @click="addParam('var')"><PlusCircleOutlined class="add-icon" /><a class="add-text" href="javascript:;">添加变量</a></div>
                    </template>
                </ExpansionPanel>
                <!-- 高级执行配置 -->
                <ExpansionPanel v-model="expansions[2]" class="panel">
                    <template v-slot:header>
                        <div class="panel-title">
                            <div>{{$t('executationConfig.advanceExecutationConfig')}}</div>
                            <FTooltip :content="$t('executationConfig.advancedConfigDesc')">
                                <QuestionCircleOutlined />
                            </FTooltip>
                        </div>
                    </template>
                    <template v-slot:body>
                        <div>
                            <FForm ref="advancedConfigRef"
                                   labelWidth="66px"
                                   labelPosition="right"
                                   :model="model.advancedConfig">
                                <FFormItem prop="reuse" label="引擎复用">
                                    <FRadioGroup v-model="model.advancedConfig.engineReuse" :cancelable="false" @change="() => advancedConfChanged = true">
                                        <FRadio :value="true">是</FRadio>
                                        <FRadio :value="false">否</FRadio>
                                    </FRadioGroup>
                                </FFormItem>
                                <FFormItem prop="granularity" label="并发粒度">
                                    <FRadioGroup v-model="model.advancedConfig.granularity" :cancelable="false" @change="() => advancedConfChanged = true">
                                        <FRadio :value="0">库粒度</FRadio>
                                        <FRadio :value="1">表粒度</FRadio>
                                        <FRadio :value="2">任务融合</FRadio>
                                    </FRadioGroup>
                                </FFormItem>
                                <FFormItem prop="dynamicPartitionBool" label="动态分区">
                                    <FRadioGroup v-model="model.advancedConfig.dynamicPartitionBool" :cancelable="false" @change="() => advancedConfChanged = true">
                                        <FRadio :value="true">是</FRadio>
                                        <FRadio :value="false">否</FRadio>
                                    </FRadioGroup>
                                </FFormItem>
                                <FFormItem v-if="model.advancedConfig.dynamicPartitionBool" prop="dynamicPartitionPrefix" label="顶层分区">
                                    <FInput
                                        v-model="model.advancedConfig.dynamicPartitionPrefix"
                                        class="form-edit-input"
                                        clearable
                                        placeholder="请输入顶层区分，示例 ds=${run_date}"
                                    />
                                </FFormItem>
                            </FForm>
                        </div>
                    </template>
                </ExpansionPanel>
                <!-- FPS文件配置 -->
                <!-- <ExpansionPanel class="panel">
                    <template v-slot:header>
                        <div class="panel-title">
                            <div>{{$t('executationConfig.fpsFileConfig')}}</div>
                            <FTooltip :content="$t('executationConfig.fpsFileConfigDesc')">
                                <QuestionCircleOutlined />
                            </FTooltip>
                        </div>
                    </template>
                    <template v-slot:body><Fps v-model="model.fpsConfig" /></template>
                </ExpansionPanel> -->
                <!-- spark -->
                <!-- <ExpansionPanel class="panel">
                    <template v-slot:header>
                        <div class="panel-title">
                            <div>{{$t('executationConfig.sparkConfig')}}</div>
                            <FTooltip :content="$t('executationConfig.sparkConfigDesc')">
                                <QuestionCircleOutlined />
                            </FTooltip>
                        </div>
                    </template>
                    <template v-slot:body><Spark ref="spark" v-model="model.sparkConfig" /></template>
                </ExpansionPanel> -->
            </div>
            <!-- <div class="more">
                <div @click="() => (expanded = !expanded)">
                    <span>{{expanded ? '收起' : '更多配置'}}</span><UpOutlined v-if="expanded" /><DownOutlined v-else />
                </div>
            </div> -->
        </div>
    </FDrawer>
</template>
<script setup>
import {
    defineProps, defineEmits, ref, onMounted, watchEffect, computed, watch,
} from 'vue';
import {
    DownOutlined,
    UpOutlined,
    QuestionCircleOutlined,
    PlusCircleOutlined,
} from '@fesjs/fes-design/icon';
import { FMessage, FTooltip } from '@fesjs/fes-design';
import eventbus from '@/common/useEvents';
import { getFlatDataFormTreeSelect } from '@/common/utils';
import { useTransition } from '@/hooks/useTransition';
import {
    access, request, useI18n, useModel,
} from '@fesjs/fes';
import ExpansionPanel from '@/components/ExpansionPanel';
import EngineParam from './EngineParam';
import Variable from './Variable';
// import Spark from './Spark';
// import Fps from './Fps';
// import Args from './Args';

const { t: $t } = useI18n();
const form = ref();
// const { body, height, expanded } = useTransition();
const isLoading = ref(false);
const props = defineProps({
    show: Boolean,
    formRule: Object,
    selectDisabled: { type: Boolean, default: false },
    modelValue: {
        rules: [],
        actor: '',
        engineConfig: {
            cluster_name: '',
            params: [],
        },
        variables: [],
        // fpsConfig: { id: '', hash: '' },
        // argsConfig: { dynamic: false, args: [], partition: '' },
        advancedConfig: {
            engineReuse: true,
            granularity: 2,
            dynamicPartitionBool: false,
        },
    },
    list: {
        flag: String, // 规则/任务 不传就用rulelist的长度猜
        label: String, // 控件label文案
        disabled: Boolean, // 控件是否可用
        list: { type: Array, default: [] },
        // 规则: Array<{label:string,value:string,children:Array<{label:string,value:string}>}>
        // 任务: Aarray<{label:string,value:string}>
    },
    gid: { type: Number, default: 0 },
});
const userList = ref([]);
const initialState = useModel('@@initialState');
const paramTypeList = ref([]);
const variableTypeList = ref([]);
const clusterList = ref([]);
const engineParamRefs = ref([]);
const variableRefs = ref([]);
const advancedConfigRef = ref();
onMounted(async () => {
    try {
        const group = await request('/api/v1/projector/proxy_user', {}, {
            method: 'GET',
            mergeRequest: true,
        });
        const user = initialState.userName;
        console.log(user);
        // || this.FesApp.get('FesUserName');
        let list = [];
        if (Array.isArray(user)) {
            list = [].concat(user);
        } else {
            list.push(user);
        }
        if (Array.isArray(group)) {
            list = list.concat(group);
        } else {
            list.push(group);
        }
        userList.value = Array.from(new Set(list)).filter(v => v);
        console.log(userList.value);
        const paramTypeRes = await request('/api/v1/projector/execution_parameters/dynamic/engine/all', {});
        paramTypeList.value = paramTypeRes.map(item => ({
            label: item.message,
            value: item.code,
        }));
        const varTypeRes = await request('/api/v1/projector/execution_parameters/execution/type/all', {});
        variableTypeList.value = varTypeRes.map(item => ({
            label: item.message,
            value: item.code,
        }));

        const { optional_clusters } = await request(
            'api/v1/projector/meta_data/cluster',
            {},
            { method: 'POST' },
        );
        if (Array.isArray(optional_clusters)) {
            clusterList.value = optional_clusters.map(item => ({
                label: item,
                value: item,
            }));
        }
    } catch (err) {
        console.warn(err);
    }
});

const emit = defineEmits(['update:modelValue', 'update:show', 'ok']);
const formRules = computed(() => {
    let result = {
        rules: [{
            required: true,
            trigger: ['change', 'blur'],
            asyncValidator: (_, value) => new Promise((res, rej) => {
                if (value.length !== 0) {
                    res();
                } else {
                    rej($t('common.notEmpty'));
                }
            }),
        }],
        actor: [{ required: true, trigger: ['change', 'blur'], message: $t('common.notEmpty') }],
    };
    if (props.formRule) {
        result = Object.assign({}, result, this.formRule);
    }
    return result;
});
const disableSelected = computed(() => props.selectDisabled);
const model = ref({
    rules: [],
    actor: '',
    engineConfig: {
        cluster_name: '',
        params: [],
    },
    variables: [],
    // fpsConfig: { id: '', hash: '' },
    // argsConfig: { dynamic: false, args: [], partition: '' },
    advancedConfig: {
        engineReuse: true,
        granularity: 2,
        dynamicPartitionBool: false,
    },
});
const selectTreeRef = ref(null);
const addParam = (type) => {
    if (type === 'var') {
        model.value.variables.push({ var_type: '', var_name: '', var_value: '' });
    } else {
        model.value.engineConfig.params.push({ param_type: '', param_name: '', param_value: '' });
    }
};
const removeParam = (type, index) => {
    if (type === 'var') {
        model.value.variables.splice(index, 1);
        variableRefs.value.splice(index, 1);
    } else {
        model.value.engineConfig.params.splice(index, 1);
        engineParamRefs.value.splice(index, 1);
    }
};
watchEffect(() => {
    if (props.show) {
        model.value = props.modelValue;
    } else {
        selectTreeRef.value = null;
    }
    if (selectTreeRef.value && props.gid) {
        model.value.rules = [props.gid];
    }
});
watch(model, () => {
    emit('update:modelValue', model.value);
}, { deep: true });
const expansions = ref([false, false, false]);
const advancedConfChanged = ref(false);
watch(() => props.show, (newVal, oldVal) => {
    if (!oldVal && newVal) {
        for (let i = 0; i < expansions.value.length; i++) {
            expansions.value[i] = false;
        }
        if (model.value.engineConfig.params.length > 0) {
            expansions.value[0] = true;
        }
        if (model.value.variables.length > 0) {
            expansions.value[1] = true;
        }
        if (advancedConfChanged.value) {
            expansions.value[2] = true;
        }
    }
});
watch(() => expansions.value, (newVal, oldVal) => {
    if (newVal[0] && model.value.engineConfig.params.length === 0) {
        addParam('engine');
    } else if (newVal[1] && model.value.variables.length === 0) {
        addParam('var');
    }
}, { deep: true });
function ok() {
    console.log('ok');
    if (isLoading.value) {
        return;
    }
    const validList = [];
    for (let i = 0; i < engineParamRefs.value.length; i++) {
        validList.push(engineParamRefs.value[i].valid());
    }
    for (let i = 0; i < variableRefs.value.length; i++) {
        validList.push(variableRefs.value[i].valid());
    }


    validList.push(advancedConfigRef.value.validate());
    form.value?.validate().then(() => Promise.all(validList)).then(() => {
        let rules;
        if (props.list.flag === '规则') {
            console.log(model.value.rules);
            rules = getFlatDataFormTreeSelect(model.value.rules, props.list.list);
        } else {
            rules = model.value.rules;
        }
        isLoading.value = false;
        emit('ok', {
            rules,
            actor: model.value.actor,
            engineConfig: { cluster_name: model.value.engineConfig.cluster_name, param: model.value.engineConfig.params },
            variables: model.value.variables,
            advancedConfig: model.value.advancedConfig,
        });
        advancedConfChanged.value = false;
    })
        .catch((err) => {
            console.warn('validate: ', err);
            FMessage.error({
                content: '配置表单中存在必填项内容为空，请检查后提交',
            });
            isLoading.value = false;
        });
}

function cancel() {
    // 清空数据
    // model.value = {
    //     rules: [],
    //     actor: '',
    //     engineConfig: {
    //         cluster_name: '',
    //         params: [{ param_type: '', param_value: '', param_name: '' }],
    //     },
    //     variables: [{ var_type: '', var_name: '', var_value: '' }],
    //     // fpsConfig: { id: '', hash: '' },
    //     // argsConfig: { dynamic: false, args: [], partition: '' },
    //     advancedConfig: {
    //         engineReuse: false,
    //         granularity: true,
    //         dynamicPartitionBool: false,
    //     },
    // };
    emit('update:show', false);
}
// 关闭loading
eventbus.on('closeTaskLoading', () => {
    isLoading.value = false;
});

</script>
<style lang="less" scoped>
.exec-config-panel{
    overflow: auto;
    // scrollbar-width: none;
    // &::-webkit-scrollbar{
    //     width:0
    // }
}
.more {
    cursor: pointer;
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    div {
        padding: 2px;
        display: flex;
        justify-content: center;
        align-items: center;
        & span {
            margin-left: 4px;
            font-size: 12px;
            line-height: 20px;
        }
    }
}
.panel {
    margin-bottom: 22px;
    .add-param {
        margin-left: 16px;
        margin-bottom: 32px;
        display: flex;
        align-items: center;
        cursor: pointer;
        text-align: center;
        height: 22px;
        .add-icon {
            color: #5384FF;
        }
        .add-text {
            display: inline-block;
            margin-left: 5px;
        }
    }
}
.panel-title {
    display: flex;
    align-items: center;
    > div {
        color: #0f1222;
        font-weight: 400;
        margin-right: 4px;
    }
}
.more-config-body {
    overflow: auto;
    transition: height 300ms cubic-bezier(0.4, 0, 0.2, 1) 0ms;
}
.label{
    margin-left: 16px;
}
</style>
<style lang="less">
@import './style.less';
</style>
