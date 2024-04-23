package com.immenser.tasks.task4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Cell{
    int f;
    int i;
    int iPrev;
    int x;

    public Cell(int f, int i, int iPrev, int x) {
        this.f = f;
        this.i = i;
        this.iPrev = iPrev;
        this.x = x;
    }

    public int getF() {
        return f;
    }

    public int getI() {
        return i;
    }

    public int getPrevI() {
        return iPrev;
    }

    public int getX() {
        return x;
    }
}

public class Main {

    static int N = 4;   //количество интервалов
    static int[] demand = {6,6,6,6};  //величина спроса для каждого интервала

    //затраты на производство и хранение продукции Cn(xn,in-1)=a+b*xn+h*in-1
    static int a = 12;
    static int b = 5;
    static int h = 1;

    //затраты на формирование начального запаса Co(io)=k*io
    static int k = 4;

    static int xMax = 6;   //ограничение на производственные мощности
    static int iMax = 4;   //ограничение на предельный уровень запасов

    static ArrayList<ArrayList<ArrayList<Cell>>> intermediateTables = new ArrayList<>();  //список для хранения значений промежуточных таблиц
    static ArrayList<HashMap<Integer,Cell>> finalTables = new ArrayList<>(); //список для хранения значений окончательных таблиц

    public static void main(String[] args) {

        for (int interval=0; interval<N; interval++){
            System.out.printf("Интервал %d\n", interval+1);
            fullIntermediateTable(interval);    //заполнение промежуточных таблиц для каждого интервала
            fullFinalTable(interval);   //заполнение окончательных таблиц для каждого интервала
        }
        Cell minCell = finalTables.get(N-1).get(0);
        for (Map.Entry<Integer, Cell> iter : finalTables.get(N-1).entrySet()){
            if (iter.getValue().getF()<minCell.getF()){
                minCell = iter.getValue();
            }
        }
        System.out.println("Минимальное значение функции затрат: " + minCell.getF());
        findOptimal(minCell);  //обратный проход по окончательным таблицам для выбора оптимальных значений
    }

    private static void fullIntermediateTable(int interval) {
        System.out.println("Промежуточная таблица");
        ArrayList<ArrayList<Cell>> intermediateTable = new ArrayList<>();
        for (int i = 0; i<= iMax; i++){
            intermediateTable.add(new ArrayList<>());
        }
        for (int x = 0; x<= xMax; x++){
            for (int iPrev = 0; iPrev<= iMax; iPrev++){
                int i = x+iPrev-demand[interval];
                if (i>=0 && i<= iMax){
                    int f;
                    if (interval==0){
                        if (x==0){
                            f = (k + h) * iPrev;
                        }
                        else {
                            f = a + b * x + (k + h) * iPrev;
                        }
                    }
                    else {
                        if (x==0){
                            f = finalTables.get(interval - 1).get(iPrev).getF() + h * iPrev;
                        }
                        else {
                            f = finalTables.get(interval - 1).get(iPrev).getF() + a + b * x + h * iPrev;
                        }
                    }

                    System.out.printf("i=%d f*(%d)=%d x=%d iPrev=%d\n", i, i, f, x, iPrev);
                    Cell cell = new Cell(f,i,iPrev,x);
                    intermediateTable.get(i).add(cell);
                }
            }
        }
        System.out.println();
        intermediateTables.add(intermediateTable);
    }

    private static void fullFinalTable(int interval) {
        System.out.println("Окончательная таблица");
        finalTables.add(new HashMap<>());
        for (ArrayList<Cell> cells : intermediateTables.get(interval)){
            Cell minCell = cells.get(0);
            for (Cell cell : cells){
                if(cell.getF()<minCell.getF()){
                    minCell=cell;
                }
            }
            System.out.printf("i=%d f(%d)=%d x=%d iPrev=%d\n", minCell.getI(), minCell.getI(), minCell.getF(), minCell.getX(), minCell.getPrevI());
            finalTables.get(interval).put(minCell.getI(), minCell);
        }
        System.out.println();
    }

    private static void findOptimal(Cell cell) {
        System.out.printf("i%d=%d ", N, cell.getI());
        System.out.printf("x%d=%d ", N, cell.getX());
        N--;
        if (N>0) {
            findOptimal(finalTables.get(N - 1).get(cell.getPrevI()));
        }
        else{
            int i = cell.getPrevI();
            ArrayList<Cell> iCells = intermediateTables.get(0).get(i);
            Cell iCell = iCells.get(0);
            int j = 0;
            while (iCell.getI()!=i){
                j++;
                iCell = iCells.get(j);
            }
            System.out.printf("i%d=%d ", N, iCell.getI());
        }
    }
}