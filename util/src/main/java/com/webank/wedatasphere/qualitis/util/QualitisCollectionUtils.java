package com.webank.wedatasphere.qualitis.util;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2023/2/7 18:55
 */
public class QualitisCollectionUtils {

    public static <T> List<List<T>> getDescartes(List<List<T>> list) {
        List<List<T>> returnList = new ArrayList<>();
        descartesRecursive(list, 0, returnList, new ArrayList<T>());
        return returnList;
    }

    /**
     * 递归实现
     *
     * @param originalList 原始列表
     * @param position     递归层级
     * @param returnList   返回结果
     * @param cacheList    临时列表
     */
    private static <T> void descartesRecursive(List<List<T>> originalList, int position, List<List<T>> returnList, List<T> cacheList) {
        List<T> originalItemList = originalList.get(position);
        for (int i = 0; i < originalItemList.size(); i++) {
            List<T> childCacheList = (i == originalItemList.size() - 1) ? cacheList : new ArrayList<>(cacheList);
            childCacheList.add(originalItemList.get(i));
            if (position == originalList.size() - 1) {
                returnList.add(childCacheList);
                continue;
            }
            descartesRecursive(originalList, position + 1, returnList, childCacheList);
        }
    }

    public static void main(String[] args) {
        List<String> list1 = new ArrayList<>();
        list1.add("str1");
        list1.add("str2");
        List<String> list2 = new ArrayList<>();
        list2.add("str3");
        list2.add("str4");
        List<String> list3 = new ArrayList<>();
        list3.add("str5");

        List<List<String>> totalList = new ArrayList<>();
        totalList.add(list1);
        totalList.add(list2);
        totalList.add(list3);

        List<List<String>> resultList1 = Lists.cartesianProduct(totalList);
        List<List<String>> resultList2 = getDescartes(totalList);

        System.out.println(resultList1);
        System.out.println(resultList2);
    }
}
