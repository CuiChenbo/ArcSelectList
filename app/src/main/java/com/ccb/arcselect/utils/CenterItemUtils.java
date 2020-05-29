package com.ccb.arcselect.utils;

import java.util.List;

public class CenterItemUtils {

    /**
     * 计算距离中间最近的一个ItemView
     * @param itemHeights
     * @return
     */
    public static CenterViewItem getMinDifferItem(List<CenterViewItem> itemHeights){
        CenterViewItem minItem = itemHeights.get(0); //默认第一个是最小差值
        for (int i = 0; i < itemHeights.size(); i++) {
            //遍历获取最小差值
            if (itemHeights.get(i).differ <= minItem.differ){
                minItem = itemHeights.get(i);
            }
        }
        return minItem;
    }

//    public static void main(String[] a){
//
//        CenterViewItem i = getMinDifferItem(Arrays.asList(
//                new CenterViewItem(2 , 39)
//                ,new CenterViewItem(3 , 3)
//                ,new CenterViewItem(1 , 9)
//                ,new CenterViewItem(4 , 449)));
//       System.out.println("position:"+i.position+"   height:"+i.differ);
//    }

   public static class CenterViewItem{
        public CenterViewItem(int position, int differ) {
            this.position = position; //当前Item索引
            this.differ = differ; //当前item和居中位置的差值
        }

        public int position;
        public int differ;
    }
}
