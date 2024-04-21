package com.immenser.tasks.task3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Cell{
    int U;  //управление
    int prevD; //состояние предыдущего шага
    int T;  //время выполнения
    int D;  //состояние текущего шага

    public Cell(int d, int u, int t) {
        D = d;
        U = u;
        T = t;
    }

    public Cell(int u, int prevD, int t, int d) {
        U = u;
        this.prevD = prevD;
        T = t;
        D = d;
    }

    public int getU() {
        return U;
    }

    public int getPrevD() {
        return prevD;
    }

    public int getT() {
        return T;
    }

    public int getD() {
        return D;
    }
}

public class Main {

    static int m = 6; //количество программных модулей
    static int Do = 16; //количество процессоров
    static int [][] T = {{12,7,2,0,0,0,0,0},{14,10,7,4,2,0,0,0},{0,9,5,2,0,0,0,0},{0,0,9,6,4,3,0,0},{0,0,0,8,6,4,5,0},{0,0,0,0,7,4,2,1}};

    static ArrayList<ArrayList<Cell>> intermediateTables = new ArrayList<>();
    static ArrayList<HashMap<Integer, Cell>> finalTables = new ArrayList<>();

    public static void main(String[] args) {

        //Шаг 1.
        finalTables.add(new HashMap<>());
        for (int i=0; i<T[0].length; i++){
            if (T[0][i]!=0){
                Cell cell = new Cell(i+1, i+1, T[0][i]);
                finalTables.get(0).put(cell.getD(), cell);
            }
        }

        //Шаги 2-m.
        for (int stage=1; stage<m; stage++) {
            System.out.printf("\nШаг %d:\n", stage+1);
            fullIntermediateTable(stage);   //заполнение промежуточной таблицы
            fullFinalTable(stage);  //заполнение окончательной таблицы
        }

        Cell minCell = finalTables.get(m-1).entrySet().iterator().next().getValue();
        for (Map.Entry<Integer,Cell> iter : finalTables.get(m-1).entrySet()){
            Cell cell = iter.getValue();
            if (cell.getT()<minCell.getT()){
                minCell = cell;
            }
        }
        System.out.print("\nПолученное распределение: ");
        getChain(minCell, m-1);
    }

    private static void getChain(Cell cell, int stage) {
        System.out.printf("d%d=%d ", stage+1, cell.getU());
        stage--;
        if (stage>=0) {
            getChain(finalTables.get(stage).get(cell.getPrevD()), stage);
        }
    }

    private static void fullIntermediateTable(int stage) {
        System.out.println("Промежуточная таблица");
        intermediateTables.add(new ArrayList<>());
        for (int i=0; i<T[stage].length; i++){
            if (T[stage][i]!=0){
                for (Map.Entry<Integer, Cell> iter : finalTables.get(stage-1).entrySet()){
                    Cell cell = iter.getValue();
                    if (i+cell.getD()+1<=Do){
                        int t = Math.max(T[stage][i], cell.getT());
                        int d = i+cell.getD()+1;
                        Cell newCell = new Cell(i+1, cell.getD(), t, d);
                        intermediateTables.get(stage-1).add(newCell);
                        System.out.printf("U=%d, prevD=%d, T=%d, D=%d ", newCell.getU(), newCell.getPrevD(), t, d);
                    }
                }
            }
        }
    }

    private static void fullFinalTable(int stage) {
        System.out.println("\nОкончательная таблица");
        finalTables.add(new HashMap<>());
        HashMap<Integer, ArrayList<Cell>> groups = divideGroups(stage);
        for (Map.Entry<Integer,ArrayList<Cell>> group : groups.entrySet()){
            Cell minCell = group.getValue().get(0);
            for (Cell cell : group.getValue()){
                if (cell.getT()<minCell.getT()){
                    minCell = cell;
                }
            }
            Cell newCell = new Cell(minCell.getU(), minCell.getPrevD(), minCell.getT(), minCell.getD());
            System.out.printf("D=%d, U=%d, T=%d\n", newCell.getD(), newCell.getU(), newCell.getT());
            finalTables.get(stage).put(newCell.getD(), newCell);
        }
    }

    private static HashMap<Integer, ArrayList<Cell>> divideGroups(int stage) {
        HashMap<Integer, ArrayList<Cell>> groups = new HashMap<>();
        for (Cell cell : intermediateTables.get(stage-1)){
            int key = cell.getD();
            if(groups.containsKey(key)){
                groups.get(key).add(cell);
            }
            else {
                ArrayList<Cell> group = new ArrayList<>();
                groups.put(key, group);
                group.add(cell);
            }
        }
        return groups;
    }
}