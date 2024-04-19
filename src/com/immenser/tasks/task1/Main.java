package com.immenser.tasks.task1;

import java.util.*;

class Values{

    int cost;
    ArrayList<Integer> cities;

    public Values(int cost, ArrayList<Integer> cities){
        this.cost = cost;
        this.cities = cities;
    }

    public int getCost() {
        return cost;
    }

    public ArrayList<Integer> getCities() {
        return cities;
    }
}

public class Main {

    static int N = 6;
    static int[][] C = {{0, 5, 9, 6, 3, 5}, {3, 0, 8, 8, 5, 9}, {6, 9, 0, 1, 6, 7}, {7, 5, 4, 0, 4, 2}, {4, 6, 3, 2, 0, 2}, {5, 2, 2, 1, 1, 0}};

    static ArrayList<HashMap<ArrayList<Integer>, Values>> W = new ArrayList<>();

    public static void main(String[] args) {

        for(int i=0; i<N; i++){
            W.add(new HashMap<>());
        }

        int s = N-2; //номер этапа
        int u = 2; //число промежуточных городов

        for (int i = 0; i < N-1; i++) {                //предпоследний этап
            for (int j = 0; j < N-1; j++) {
                if (i != j) {
                    ArrayList<Integer> key = new ArrayList<>();
                    key.add(i+1);
                    key.add(j+1);
                    ArrayList<Integer> city = new ArrayList<>();
                    city.add(j+1);
                    W.get(s).put(key, new Values(C[i][j] + C[j][N-1], city));
                }
            }
        }
        s--;

        while (s != 0){                     //промежуточные этапы
            W.set(s, getValues(u, N, s));
            s--;
            u++;
        }

        ArrayList<Integer> lastKey = new ArrayList<>();       //последний этап
        lastKey.add(N);
        for (int i=1; i<N; i++){
            lastKey.add(i);
        }
        W.get(0).put(lastKey, countValue(lastKey, s));

        for (HashMap<ArrayList<Integer>, Values> Wi : W){
            for (Map.Entry<ArrayList<Integer>, Values> arrayListValuesEntry : Wi.entrySet()) {
                ArrayList<Integer> key = arrayListValuesEntry.getKey();
                System.out.println(key + " = " + Wi.get(key).getCost() + " через А" + Wi.get(key).getCities().get(0));
            }
        }

        Iterator<Map.Entry<ArrayList<Integer>, Values>> iterator = W.get(0).entrySet().iterator();  //оптимальный маршрут
        ArrayList<Integer> key = iterator.next().getKey();
        ArrayList<Integer> cities = W.get(0).get(key).getCities();
        ArrayList<Integer> route = new ArrayList<>();
        route.add(N);
        findRoute(0, cities, route);
        System.out.print("\nОптимальный маршрут: ");
        for (int A : route){
            System.out.print("A" + A + "->");
        }
        System.out.println("A" + N);
        System.out.println("Длина оптимального маршрута: " + W.get(0).get(lastKey).getCost());
    }

    public static HashMap<ArrayList<Integer>, Values> getValues(int u, int N, int s){   //заполнение Wi этапа значениями маршрут:параметры
        ArrayList<ArrayList<Integer>> arrays = new ArrayList<>();
        for (int i=0; i<u; i++){
            ArrayList<Integer> array = new ArrayList<>();
            for (int j=0; j<N-u+i; j++){
                array.add(j+1);
            }
            arrays.add(array);
        }

        HashMap<ArrayList<Integer>, Values> Wi = new HashMap<>(); //карта города/стоимости для i-го этапа
        ArrayList<ArrayList<Integer>> keysI = new ArrayList<>();

        for (int i=0; i<N-1; i++){      //создание копии для списка маршрутов
            ArrayList<ArrayList<Integer>> copyArrays = new ArrayList<>();
            for (ArrayList<Integer> ai:arrays){
                copyArrays.add(new ArrayList<>(ai));
            }
            System.out.print("i: ");
            System.out.println(i+1);
            for (ArrayList<Integer> arr:copyArrays){   //удаление i-го города из всех списков в copyArrays
                if (arr.contains(i+1)){
                    arr.remove(Integer.valueOf(i+1));
                }
                else {
                    arr.remove(arr.size()-1);
                }
            }
            ArrayList<Integer> resultArray = new ArrayList<>(Collections.nCopies(u+1, null));
            resultArray.set(0, i+1);
            int d = 0; //текущая глубина выбора
            getKeys(d, copyArrays, resultArray, keysI);  //переход к формированию маршрутов
        }
        //получить все ключи для текущего этапа в виде ArrayList<LinkedHashSet<Integer>>
        //для каждого Set-а рассчитать стоимость, формируя Wi
        System.out.println("Keys: " + keysI + "\n");
        for (ArrayList<Integer> key : keysI){
            Wi.put(key, countValue(key, s));    //сопоставление маршрутов и их параметров
        }
        return Wi;
    }

    public static void getKeys(int d, ArrayList<ArrayList<Integer>> copyArrays, ArrayList<Integer> resultArray, ArrayList<ArrayList<Integer>> keysI) {   //составление наборов из городов
        if (copyArrays.size() > 1) {
            System.out.println("Массив: " + copyArrays);
            ArrayList<Integer> headArray = copyArrays.get(0);
            System.out.println("Отделяю заголовок: " + headArray);
            copyArrays.remove(0);
            d += 1;
            System.out.println("Осталось: " + copyArrays);
            ArrayList<Integer> forRenew = new ArrayList<>(copyArrays.get(0));

            Iterator<Integer> iterator = headArray.iterator();
            for (int key : headArray) {       //формирование цепочек городов
                if (resultArray.get(d) != null) {
                    resultArray = new ArrayList<>(resultArray);
                    for (int i=d+1; i<resultArray.size()-1; i++){
                        resultArray.set(i, null);
                    }
                }
                resultArray.set(d, key);

                System.out.println(resultArray);
                System.out.println("Беру из заголовка и удаляю из массива: " + key);
                for (ArrayList<Integer> arr : copyArrays) {
                    arr.remove(Integer.valueOf(key));
                }
                if (copyArrays.size()==1){     //сопоставление полученных цепочек со всеми вариантами из tail для получения маршрутов
                    for (int tail : copyArrays.get(0)){
                        ArrayList<Integer> chain = new ArrayList<>(resultArray);
                        chain.set(resultArray.size()-1, tail);
                        keysI.add(chain);  //добавление маршрутов в список i-го этапа
                    }
                }
                System.out.println("Осталось: " + copyArrays);
                getKeys(d, copyArrays, resultArray, keysI);
                iterator.next();
                if (!iterator.hasNext()) {      //обратное присоединение tail и headArray к copyArrays по достижении конца headArray
                    copyArrays.set(0, forRenew);
                    copyArrays.add(0, headArray);
                    d -= 1;
                }
            }
        }
    }

    public static Values countValue(ArrayList<Integer> key, int s){     //вычисление минимальной стоимости маршрута
//        System.out.println(key);
        Integer [] sums = new Integer[key.size()-1];
        ArrayList<ArrayList<Integer>> keys = new ArrayList<>();
        for (int i=0; i<key.size()-1; i++){
            ArrayList<Integer> variantI = new ArrayList<>(key);
            variantI.remove(0);
            int t = variantI.get(i);
            variantI.remove(i);
            variantI.add(0, t);
            keys.add(variantI);
            Iterator<Map.Entry<ArrayList<Integer>, Values>> iterator = W.get(s+1).entrySet().iterator();
            ArrayList<Integer> existing = iterator.next().getKey();
            while (!existing.equals(variantI)){
                existing = iterator.next().getKey();
            }
            sums[i] = C[key.get(0)-1][variantI.get(0)-1] + W.get(s+1).get(existing).getCost(); //массив для выбора минимальной стоимости маршрута
        }
        int minCost = Collections.min(Arrays.asList(sums));    //минимальная стоимость
        return new Values(minCost, keys.get(Arrays.asList(sums).indexOf(minCost)));
    }

    public static void findRoute(int i, ArrayList<Integer> cities, ArrayList<Integer> route) {     //составление оптимального маршрута
        Iterator<Map.Entry<ArrayList<Integer>, Values>> iterator = W.get(i+1).entrySet().iterator();
        ArrayList<Integer> existing = iterator.next().getKey();
        while (!cities.equals(existing)){
            existing = iterator.next().getKey();
        }
        i++;
        route.add(existing.get(0));
        if (i<N-2) {
            findRoute(i, W.get(i).get(existing).getCities(), route);
        }
        else {
            route.add(existing.get(1));
        }
    }
}
