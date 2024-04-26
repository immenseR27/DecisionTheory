package com.immenser.tasks.task5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

class Result{
    int sum;
    int j;
    int i;

    public void setSum(int sum) {
        this.sum = sum;
    }

    public int getSum() {
        return sum;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public int getJ() {
        return j;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getI() {
        return i;
    }
}
public class Main {

    static int Do = 95; //максимальный вес ранца
    static int n = 12; //количество предметов
    static int[] c = {5,3,12,14,5,6,5,10,1,8,9,4};  //массив ценностей предметов
    static int[] w = {20,15,24,28,15,8,9,10,12,16,21,17}; //массив весов предметов

    static ArrayList<Integer> f = new ArrayList<>();    //список для значений f(D)
    static ArrayList<ArrayList<Integer>> E = new ArrayList<>(); //список множеств Ei
    static ArrayList<ArrayList<Integer>> X = new ArrayList<>(); //список множеств Xi

    public static void main(String[] args) {
        int d = Arrays.stream(w).max().orElse(0);
        f.add(0);   //f(0)=0
        X.add(new ArrayList<>());   //Xo=[]

        for (int i=0; i<d; i++){    //формирование множеств Ei

            ArrayList<Integer> Ei = new ArrayList<>();
            E.add(Ei);
            for (int j=0; j<n; j++){
                if(w[j]==i+1){
                    Ei.add(j);
                }
            }
            System.out.printf("E%d:%s ", i+1, Ei);
        }
        System.out.println();
        for (int D=1; D<=Do; D++) { //вызов функции, возвращающей f(D), для каждого D
            int m = Math.min(D, d);
            f.add(f(D, m));
        }

        System.out.print("\nОптимальный набор предметов в рюкзаке: ");
        int sum = 0;
        Collections.sort(X.get(Do));
        for (int j : X.get(Do)){
            sum = sum + w[j];
            System.out.printf("x%d ", j+1);
        }
        System.out.println("\nСуммарный вес предметов: " + sum);
        System.out.println("Максимальная ценность предметов: " + f.get(Do));
    }

    public static int f(int D, int m) {
        ArrayList<Result> variants = new ArrayList<>();
        for (int i=0; i<m; i++){
            ArrayList<Integer> EiCopy = new ArrayList<>(E.get(i));
            EiCopy.removeAll(X.get(D-i-1));
            if(EiCopy.size()!=0){  //если Ei\Xd-i != {}
                int cMax = 0;
                int j = 0;
                for (int xj : EiCopy){ //поиск предмета с наибольшей ценностью среди предметов веса i
                    if (c[xj]>cMax){
                        cMax = c[xj];
                        j = xj;
                    }
                }
                Result result = new Result();
                result.setSum(f.get(D-i-1)+cMax);
                result.setJ(j);
                result.setI(D-i-1);
                variants.add(result);   //добавление найденного результата к сравнению
            }
        }
        int maxSum = 0;
        Result maxResult = new Result();
        for (Result result : variants){     //поиск варианта с наибольшей суммой f(D)+cjxj
            if (result.getSum()>maxSum){
                maxSum = result.getSum();
                maxResult = result;
            }
        }
        X.add(new ArrayList<>());
        if (maxSum!=0){
            X.get(D).addAll(X.get(maxResult.getI()));  //добавление в множество Xi множества Xa, где f(a)+cjxj - max
            X.get(D).add(maxResult.getJ()); //добавление xj в множество Xi
        }
        System.out.printf("D=%d, f(%d)=%d, X: %s\n", D, D, maxSum, X.get(D));
        return maxSum;
    }
}