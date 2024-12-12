import { reactive, ref } from 'vue';
import { debounce } from 'lodash-es';
import {
    fetchStdSource, fetchStdType, fetchBigStd, fetchSmallStd, fetchDataStd, fetchStdCode,
} from '../api';

export const useDataList = () => {
    const stdSourceList = ref([]);
    const stdTypeList = ref([]);
    const stdBigList = ref([]);
    const stdSmallList = ref([]);
    const stdList = ref([]);
    const debounceTime = 100;

    const loadStdCode = async (stdUrn) => {
        try {
            const params = {
                std_urn: stdUrn,
                page: 1,
                size: 50,
            };
            const result = await fetchStdCode(params);
            return result.content;
        } catch (error) {
            console.warn('loadDataStd', error);
        }
    };
    const loadDataStd = debounce(async (key, stdSmallCategoryUrn) => {
        try {
            const params = {
                std_cn_name: key,
                std_small_category_urn: stdSmallCategoryUrn,
                page: 1,
                size: 50,
            };
            const result = await fetchDataStd(params);
            stdList.value = result.content;
        } catch (error) {
            console.warn('loadDataStd', error);
        }
    }, debounceTime);

    const loadStdSmall = debounce(async (key = '', stdSubName, stdBigCategoryName) => {
        try {
            const params = {
                std_sub_name: stdSubName,
                std_big_category_name: stdBigCategoryName,
                small_category_name: key,
                page: 1,
                size: 50,
            };
            const result = await fetchSmallStd(params);
            stdSmallList.value = result.content;
        } catch (error) {
            console.warn('loadStdSmall', error);
        }
    }, debounceTime);

    const loadStdBig = debounce(async (key = '', stdSubName) => {
        try {
            const params = {
                std_sub_name: stdSubName,
                std_big_category_name: key,
                page: 1,
                size: 50,
            };
            const result = await fetchBigStd(params);
            stdBigList.value = result.content;
        } catch (error) {
            console.log('loadStdBig', error);
        }
    }, debounceTime);
    const loadStdType = debounce(async (key = '') => {
        try {
            const result = await fetchStdType({
                std_sub_name: key,
                page: 1,
                size: 50,
            });
            stdTypeList.value = result.content;
        } catch (error) {
            console.warn('loadStdType', error);
        }
    }, debounceTime);
    const loadStdSource = async () => {
        try {
            const res = await fetchStdSource();
            stdSourceList.value = res;
        } catch (error) {
            console.log(error);
        }
    };
    return {
        stdSourceList,
        stdTypeList,
        stdBigList,
        stdSmallList,
        stdList,
        loadStdSource,
        loadStdType,
        loadStdBig,
        loadStdSmall,
        loadDataStd,
        loadStdCode,
    };
};
