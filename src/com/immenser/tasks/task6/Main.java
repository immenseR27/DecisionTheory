package com.immenser.tasks.task6;

import java.util.*;

class Detail{
    int a;
    int b;
    int c;

    Detail(int a, int b, int c){
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public int getC() {
        return c;
    }
}

class Production{
    ArrayList<Integer> detailSet;
    int A;
    int B;
    int C;

    public void setDetailSet(ArrayList<Integer> detailSet) {
        this.detailSet = detailSet;
    }

    public ArrayList<Integer> getDetailSet() {
        return detailSet;
    }

    public void setA(int a) {
        A = a;
    }

    public int getA() {
        return A;
    }

    public void setB(int b) {
        B = b;
    }

    public int getB() {
        return B;
    }
    public void setC(int c) {
        C = c;
    }

    public int getC() {
        return C;
    }

    public void print(){
        System.out.println(detailSet);
        System.out.print(A+" ");
        System.out.print(B+" ");
        System.out.println(C);
    }
}

public class Main {

    static int n = 5; //кол-во деталей
    static int[][] times = {{8,7,9}, {9,4,5}, {5,9,6}, {4,8,6}, {6,3,5}}; //матрица трудоемкостей

    static int stage = 1; //номер этапа
    static ArrayList<ArrayList<Production>> timesTable = new ArrayList<>(); //список для хранения характеристик фрагментов
    static ArrayList<Detail> details = new ArrayList<>();   //список для объектов деталей

    public static void main(String[] args) {

        for (int i=0; i<n; i++){    //заполнение списка объектами деталей
            int[] abc = times[i];
            Detail detail = new Detail(abc[0], abc[1], abc[2]);
            details.add(detail);
        }

        //Шаг 1.
        System.out.printf("Шаг %d:\n", stage);
        timesTable.add(new ArrayList<>()); //добавление списка с фрагментами длины 1
        for (int i=0; i<details.size(); i++){   //заполнение характеристик фрагментов длины 1
            Detail detail = details.get(i);
            Production production = new Production();
            production.setDetailSet(new ArrayList<>(i));
            production.setA(detail.getA());
            production.setB(production.getA()+detail.getB());
            production.setC(production.getB()+detail.getC());
            System.out.printf("[%d]\n%d %d %d\n", i, production.getA(), production.getB(), production.getC());
            timesTable.get(0).add(production);
        }

        //Шаг 2.
        System.out.printf("\nШаг %d:\n", stage+1);
        System.out.println("Полный список");
        timesTable.add(new ArrayList<>()); //добавление списка с фрагментами длины 2
        for (int i=0; i<n; i++){    ////заполнение характеристик фрагментов длины 2
            for (int j=0; j<n; j++){
                if (i!=j){
                    Production production = new Production();
                    ArrayList<Integer> detailSet = new ArrayList<>();
                    detailSet.add(i);
                    detailSet.add(j);
                    production.setDetailSet(detailSet);
                    production.setA(details.get(i).getA()+details.get(j).getA());
                    production.setB(Math.max(production.getA(), timesTable.get(0).get(i).getB())+details.get(j).getB());
                    production.setC(Math.max(production.getB(), timesTable.get(0).get(i).getC())+details.get(j).getC());
                    production.print();
                    timesTable.get(1).add(production);
                }
            }
        }
        optimalSeq();

        //Шаги 3-n.
        while (stage<n){
            System.out.printf("\nШаг %d:", stage+1);
            getSeqs();
            optimalSeq();
        }

        int tMin = timesTable.get(n-1).get(0).getC();
        for (Production production : timesTable.get(n-1)){
            if (production.getC()<tMin){
                tMin = production.getC();
            }
        }
        System.out.println("\nОптимальная последовательность запуска деталей");
        for (Production production : timesTable.get(n-1)){
            if (production.getC()==tMin){
                for (int detail : production.getDetailSet()){
                    System.out.printf("%d ", detail+1);
                }
                System.out.println();
            }
        }
        System.out.print("\nДлительность производственного цикла: " + tMin);
    }

    static void optimalSeq(){  //функция для поиска оптимальных последовательностей
        ArrayList<Production> stageSets = new ArrayList<>(timesTable.get(stage));
        timesTable.get(stage).clear();
        HashMap<ArrayList<Integer>, ArrayList<Production>> groups = new HashMap<>();    //карта ключ-группа
        for (Production production : stageSets){   //разделение на группы
            ArrayList<Integer> key = new ArrayList<>(production.getDetailSet());
            Collections.sort(key);
            if (groups.containsKey(key)){
                groups.get(key).add(production);
            }
            else{
                ArrayList<Production> group = new ArrayList<>();
                group.add(production);
                groups.put(key, group);
            }
        }

        for (Map.Entry<ArrayList<Integer>, ArrayList<Production>> group : groups.entrySet()){   //поиск оптимальных последовательностей
            ArrayList<Production> optimalSeqs = new ArrayList<>();
            ArrayList<Production> minsB = new ArrayList<>();    //список для последовательностей с минимальным значением B
            ArrayList<Production> minsC = new ArrayList<>();    //список для последовательностей с минимальным значением С
            int minB = group.getValue().get(0).getB();
            int minC = group.getValue().get(0).getC();
            for (Production production : group.getValue()){ //поиск минимальных значений
                if(production.getB()<minB){
                    minB = production.getB();
                }
                if(production.getC()<minC){
                    minC = production.getC();
                }
            }
            for (Production production : group.getValue()){ //выбор последовательностей с минимальными значениями B
                if(production.getB()==minB){
                    minsB.add(production);
                }
                if(production.getC()==minC){   //выбор последовательностей с минимальными значениями C
                    minsC.add(production);
                }
            }
            for(Production production : minsB){    //отбор оптимальных последовательностей
                if (minsC.contains(production)){
                    optimalSeqs.add(production);
                }
            }
            if (optimalSeqs.size()==0){    //если последовательности, оптимальные по B и C, не совпадают
                optimalSeqs.addAll(minsB);
                optimalSeqs.addAll(minsC);

            }
            timesTable.get(stage).addAll(optimalSeqs);
        }
        System.out.println("Оптимальный список");
        for (Production production : timesTable.get(stage)){
            production.print();
        }
        stage += 1;
    }

    static void getSeqs(){
        System.out.println("Полный список");
        for (Production preproduction : timesTable.get(stage-1)){
            for (int i=0; i<n; i++){
                if(!preproduction.getDetailSet().contains(i)){
                    ArrayList<Integer> detailSet = new ArrayList<>(preproduction.getDetailSet());
                    detailSet.add(i);
                    Production production = new Production();
                    production.setDetailSet(detailSet);
                    production.setA(preproduction.getA()+details.get(i).getA());
                    production.setB(Math.max(production.getA(),preproduction.getB())+details.get(i).getB());
                    production.setC(Math.max(production.getB(),preproduction.getC())+details.get(i).getC());
                    timesTable.add(new ArrayList<>());
                    timesTable.get(stage).add(production);
                    production.print();
                }
            }
        }
    }
}

