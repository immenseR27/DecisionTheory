package com.immenser.tasks.task2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Distribution {

    int q;
    int profit;

    public Distribution() {}

    public int getQ() {
        return q;
    }

    public void setQ(int q) {
        this.q = q;
    }

    public int getProfit() {
        return profit;
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }
}

public class Main {

    static int m = 5;
    static int[] investitions = {0, 100, 200, 300, 400, 500};
    static int[][] fabricsProfit = {{0, 40, 50, 65, 75, 85}, {0, 50, 73, 85, 95, 110}, {0, 30, 55, 75, 95, 120}, {0, 60, 75, 95, 110, 125}, {0, 50, 60, 80, 101, 130}};

    static ArrayList<HashMap<Integer, Integer>> profitTable = new ArrayList<>();
    static ArrayList<HashMap<Integer, Distribution>> W = new ArrayList<>();
    static int globalProfit = 0;

    public static void main(String[] args) {

        for (int i=0; i<m; i++){                                //заполнение таблицы profitTable
            HashMap<Integer, Integer> fabric = new HashMap<>();
            profitTable.add(fabric);
            for (int j=0; j<investitions.length; j++){
                fabric.put(investitions[j], fabricsProfit[i][j]);
            }
        }

        System.out.println(profitTable + "\n");
        fillW();   //заполнение списка W

        for (int i=0; i<W.size(); i++){
            HashMap<Integer, Distribution> fabric = W.get(i);
            for (Map.Entry<Integer, Distribution> entry : fabric.entrySet()) {
                int fabricNum = i+1;
                System.out.println("Распределение между заводами 1-" + fabricNum);
                int investition = entry.getKey();
                Distribution distribution = fabric.get(investition);
                System.out.println("Размер инвестиции в " + fabricNum + " завод: " + distribution.getQ() + ". Максимальная прибыль: " + distribution.getProfit());
            }
        }

        System.out.println("\n");
        int fabric = m-1;
        getDistribution(fabric, W.get(fabric).get(investitions[fabric]), investitions[investitions.length-1]);      //обратный проход по W

        System.out.println("\nМаксимально возможная прибыль составляет " + globalProfit);
    }

    private static void fillW() {                          //добавление в W HashMap Wi для каждого завода
        for (int fabric=0; fabric<m; fabric++){
            HashMap<Integer, Distribution> Wi = new HashMap<>();
            W.add(fabric,Wi);
            for (int investition : investitions){
                Wi.put(investition, getProfit(fabric, investition));    //расчет прибыли для всех вариантов инвестиций в заводы 1-Wi
            }
        }
    }

    private static Distribution getProfit(int fabric, int investition) {
        Distribution distributionOb = new Distribution();      //первый этап
        if (fabric == 0){
            distributionOb.setQ(investition);
            distributionOb.setProfit(profitTable.get(0).get(investition));
        }
        else {
            if (investition == 0){      //нулевые инвестиции -> нулевая прибыль
                distributionOb.setQ(0);
                distributionOb.setProfit(0);
            }
            else {
                HashMap<Integer, Integer> variants = new HashMap<>();   //вычисление инвестиций и соответствующей прибыли
                int i = 0;
                int currInvestition = investitions[i];
                while (currInvestition <= investition){
                    int q = investition - investitions[i];
                    int profit = profitTable.get(fabric).get(q) + W.get(fabric-1).get(investitions[i]).getProfit();
                    variants.put(q, profit);
                    i++;
                    if (i != investitions.length){
                        currInvestition = investitions[i];
                    }
                    else {
                        currInvestition = investition+1;
                    }
                }

                int keyOfMaxProfit = -1;
                int maxProfit = 0;
                for (Map.Entry<Integer, Integer> entry : variants.entrySet()) {     //поиск условной максимальной прибыли
                    int key = entry.getKey();
                    int profit = variants.get(key);
                    if (profit > maxProfit) {
                        keyOfMaxProfit = key;
                        maxProfit = profit;
                    }
                }
                distributionOb.setQ(keyOfMaxProfit);
                distributionOb.setProfit(maxProfit);
            }
        }
        return distributionOb;
    }

    private static void getDistribution(int fabric, Distribution distributionOb, int balance) {    //получение оптимального распределения инвестиций
        int fabricNum = fabric + 1;
        int q = distributionOb.getQ();
        balance = balance - q;
        int profit = profitTable.get(fabric).get(q);
        globalProfit = globalProfit + profit;
        System.out.println("Завод " + fabricNum + " - " + q + " (прибыль=" + profit + ")");
        fabric -= 1;
        if (fabric > -1) {
            getDistribution(fabric, W.get(fabric).get(balance), balance);
        }
    }
}