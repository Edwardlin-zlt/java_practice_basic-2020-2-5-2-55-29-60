package com.thoughtworks;

import java.util.Arrays;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("请点餐（菜品Id x 数量，用逗号隔开）：");
        String selectedItems = scan.nextLine();
        String summary = bestCharge(selectedItems);
        System.out.println(summary);
    }

    /**
     * 接收用户选择的菜品和数量，返回计算后的汇总信息
     *
     * @param selectedItems 选择的菜品信息
     */
    public static String bestCharge(String selectedItems) {
        // 此处补全代码
        String[][] orderedItemsList = getOrderedItemsList(selectedItems);
        float totalPriceNoDiscount = calTotalPriceNoDiscount(orderedItemsList);
        float priceWithHalfPriceItems = calPriceWithHalpPriceItems(orderedItemsList);
        float priceWithReduction = calPriceWithReduction(orderedItemsList);
        String discountType;
        float finalPrice;

        if (priceWithHalfPriceItems == totalPriceNoDiscount &&
            priceWithReduction == totalPriceNoDiscount) {
            discountType = "";
            finalPrice = totalPriceNoDiscount;
        } else {
            if (priceWithHalfPriceItems < priceWithReduction) {
                discountType = "指定菜品半价";
                finalPrice = priceWithHalfPriceItems;
            } else {
                discountType = "满30减6元";
                finalPrice = priceWithReduction;
            }
        }
        String summary = generateSummary(orderedItemsList, discountType, finalPrice);

        return summary;
    }

    public static String[][] getOrderedItemsList(String orderString) {
        String[] itemIdsAndCounts = orderString.split(",");
        String[][] orderedItemsList = new String[4][itemIdsAndCounts.length];
        int i = 0;
        for (String itemStr : itemIdsAndCounts) {
            orderedItemsList[0][i] = itemStr.split(" x ")[0];
            orderedItemsList[1][i] = itemStr.split(" x ")[1];
            i++;
        }
        byte[] itemsIndex = getItemsIndex(orderedItemsList[0]);
        orderedItemsList[2] = getOrderedItemsPrice(itemsIndex);
        orderedItemsList[3] = getOrderedItemsName(itemsIndex);
        return orderedItemsList;
    }

    public static byte[] getItemsIndex(String[] orderedItemIds) {
        final String[] ITEM_IDS = getItemIds();
        byte[] orderedItemIndexs = new byte[orderedItemIds.length];
        byte j = 0;
        for (String itemId : orderedItemIds) {
            for (byte i = 0; i < ITEM_IDS.length; i++) {
                if (itemId.equals(ITEM_IDS[i])) {
                    orderedItemIndexs[j] = i;
                    j++;
                    break;
                }
            }
        }
        return orderedItemIndexs;
    }

    public static String[] getOrderedItemsPrice(byte[] orderedItemIndexs) {
        final double[] ITEM_PRICES = getItemPrices();
        String[] orderedItemPrices = new String[orderedItemIndexs.length];
        byte i = 0;
        for (byte value : orderedItemIndexs) {
            orderedItemPrices[i] = String.valueOf(ITEM_PRICES[value]);
            i++;
        }
        return orderedItemPrices;
    }

    public static String[] getOrderedItemsName(byte[] orderItemIndexs) {
        final String[] ITEM_NAMES = getItemNames();
        String[] orderedItemNames = new String[orderItemIndexs.length];
        byte i = 0;
        for (byte value : orderItemIndexs) {
            orderedItemNames[i] = ITEM_NAMES[value];
            i++;
        }
        return orderedItemNames;
    }

    public static float calTotalPriceNoDiscount(String[][] orderedItemsList) {
        float totalPriceNoDiscount = .0f;
        String[] prices = orderedItemsList[2];
        String[] counts = orderedItemsList[1];
        for (int i = 0; i < prices.length; i++) {
            float itemTotalPrice = Float.parseFloat(prices[i]) * Integer.parseInt(counts[i]);
            totalPriceNoDiscount += itemTotalPrice;
        }
        return totalPriceNoDiscount;
    }

    public static float calPriceWithHalpPriceItems(String[][] orderedItemsList) {
        final String[] HALF_PRICE_IDS = getHalfPriceIds();
        float priceWithHalfPriceItems = .0f;
        String[] orderedItemIds = orderedItemsList[0];
        String[] orderedItemPrices = orderedItemsList[2];
        String[] orderedItemCounts = orderedItemsList[1];
        for (int i = 0; i < orderedItemIds.length; i++) {
            boolean flag = false;
            for (String halfPriceId : HALF_PRICE_IDS) {
                if (orderedItemIds[i].equals(halfPriceId)) {
                    float itemTotalPrice = Float.parseFloat(orderedItemPrices[i]) / 2 * Integer.parseInt(orderedItemCounts[i]);
                    priceWithHalfPriceItems += itemTotalPrice;
                    flag = true;
                }
            }
            if (flag) {
                continue;
            }
            priceWithHalfPriceItems += Float.parseFloat(orderedItemPrices[i]) * Integer.parseInt(orderedItemCounts[i]);
        }
        return priceWithHalfPriceItems;
    }

    public static float calPriceWithReduction(String[][] orderedItemsList) {
        final float REDUCTION = 6F;
        float totalPriceNoDiscount = calTotalPriceNoDiscount(orderedItemsList);
        float priceWithReduction = totalPriceNoDiscount;
        if (totalPriceNoDiscount >= 30) {
            priceWithReduction = totalPriceNoDiscount - REDUCTION;
        }
        return priceWithReduction;
    }

    public static String generateSummary(String[][] orderedItemsList, String discountType, float finalPrice) {
        final String HEADER = "============= 订餐明细 =============\n";
        String itemDetail = generateItemDetailInfo(orderedItemsList);
        String discountInfo = generateDiscountInfo(discountType);
        String totalInfo = "总计：" + String.valueOf((int) finalPrice) + "元\n" +
            "===================================";
        return HEADER + itemDetail + discountInfo + totalInfo;
    }

    public static String generateItemDetailInfo(String[][] orderedItemsList) {
        final String SPLIT_LINE = "-----------------------------------\n";
        String itemDetails = "";
        String[] names = orderedItemsList[3];
        String[] counts = orderedItemsList[1];
        String[] prices = orderedItemsList[2];
        for (int i = 0; i < names.length; i++) {
            int tmpPrice = (int) (Float.parseFloat(prices[i]) * Integer.parseInt(counts[i]));
            String tmpLine = names[i] + " x " + counts[i] + " = " + String.valueOf(tmpPrice) + "元\n";
            itemDetails += tmpLine;
        }
        itemDetails += SPLIT_LINE;
        return itemDetails;
    }

    public static String generateDiscountInfo(String discountType) {
        final String SPLIT_LINE = "-----------------------------------\n";
        String discountInfo = "";
        if (discountType == "满30减6元") {
            discountInfo += "使用优惠:\n满30减6元，省6元\n";
            discountInfo += SPLIT_LINE;
        } else if (discountType == "指定菜品半价") {
            discountInfo += "使用优惠:\n指定菜品半价(黄焖鸡，凉皮)，省13元\n";
            discountInfo += SPLIT_LINE;
        }
        return discountInfo;
    }

    /**
     * 获取每个菜品依次的编号
     */
    public static String[] getItemIds() {
        return new String[]{"ITEM0001", "ITEM0013", "ITEM0022", "ITEM0030"};
    }

    /**
     * 获取每个菜品依次的名称
     */
    public static String[] getItemNames() {
        return new String[]{"黄焖鸡", "肉夹馍", "凉皮", "冰粉"};
    }

    /**
     * 获取每个菜品依次的价格
     */
    public static double[] getItemPrices() {
        return new double[]{18.00, 6.00, 8.00, 2.00};
    }

    /**
     * 获取半价菜品的编号
     */
    public static String[] getHalfPriceIds() {
        return new String[]{"ITEM0001", "ITEM0022"};
    }
}
