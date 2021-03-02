package com.yunlankeji.yishangou.utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ListUtil {

    public static <T> boolean isListEmpty(List<T> list) {
        if (list == null || list.size() == 0) return true;
        return false;
    }

    public static List<String> removeDuplicate(List<String> list) {
        Set set = new LinkedHashSet<String>();
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return list;
    }

    /**
     * 集合位置互换
     * @param list 被调换集合
     * @param oldPosition 需要调换的元素
     * @param newPosition 被调换的元素
     * @param <T>
     * @return
     */
    public static <T> List<T> swapList(List<T> list, int oldPosition, int newPosition){
        List<T> list1 = new ArrayList<>();
        T oldT = list.get(oldPosition);
        T newT = list.get(newPosition);
        list.set(oldPosition,newT);
        list.set(newPosition,oldT);
        list1.addAll(list);
        return list1;
    }

    /**
     * 插入数据到第一位
     * @param list 被调换集合
     * @param nfcPos 前叉的元素
     * @param <T>
     * @return
     */
    public static <T> List<T> insertListFirst(List<T> list, int nfcPos){
        List<T> list1 = new ArrayList<>();
        List<T> list2= new ArrayList<>();
        for (int i=0;i<list.size();i++){
            if (GsonUtil.GsonString(list.get(i)).equals("{}")){
                list1.add(list.get(i));
            }else if (i==nfcPos){
                list1.add(list.get(nfcPos));
            }else {
                //有值的集合
                list2.add(list.get(i));
            }
        }
        list1.addAll(list2);
        return list1;
    }

}
