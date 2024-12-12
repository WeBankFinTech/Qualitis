import {
    createStore,
    createLogger,
} from 'vuex';
import rule from './modules/rule';

const debug = process.env.NODE_ENV !== 'production';

export default createStore({
    modules: {
        rule,
    },
    strict: debug,
    plugins: debug ? [createLogger()] : [],
});
