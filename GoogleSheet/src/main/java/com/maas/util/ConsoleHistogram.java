package com.maas.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConsoleHistogram {

    static List<Integer> owners;  //represents number of dogs per owner
    static final int MAX_DOGS_NUM = 10;

    public static void main(String[] args) {
        //test data
        owners = Arrays.asList(new Integer[] {0,1,2,3,4,5,6});
        drawHistogram(2);
    }

    static void drawHistogram(int bin) {

        //todo add check to make sure bin < MAX_DOGS_NUM
        List<Column> columns = makeColumns(bin);
        calcColumnsData(columns);
        System.out.println(new Histogram(columns));
    }

    //construct all columns, set interval ends 
    private static List<Column> makeColumns(int bin) {

        List<Column> columns = new ArrayList<>();

        for (int i = 0 ; i <= MAX_DOGS_NUM ; i += bin ) {
            int intervalEnd = (i+ bin) > MAX_DOGS_NUM ? MAX_DOGS_NUM : (i+ bin);
            columns.add(new Column(i, intervalEnd));
            i++;
        }
        return columns;
    }

    //calculate quantity of each column 
    private static void calcColumnsData(List<Column> columns) {

        for(Column col : columns) {
            //count the number of owners who has dogs within interval 
            int ownersCounter = 0;   
            for(int numberOfDogs : owners) {
                if((numberOfDogs >= col.getInteravalStart())
                        && (numberOfDogs <= col.getIntervalEnd())) { ownersCounter++ ;}
            }
            col.setQty(ownersCounter); //update column quantity 
        }
    }
}

//represents a single histogram column
class Column {
    //interval ends, quantity of owners 
    int interavalStart, intervalEnd, qty =0;

    Column(int interavalStart, int intervalEnd) {
        this.interavalStart = interavalStart;
        this.intervalEnd = intervalEnd;
    }

    int getQty() {  return qty; }

    void setQty(int qty) {  this.qty = qty; }

    int getInteravalStart() {return interavalStart; }

    int getIntervalEnd() {return intervalEnd; }

    @Override
    public String toString() {
        return interavalStart +"-" +intervalEnd+": "+ qty;
    }
}

//represents histogram graph
class Histogram{

    private List<Column> columns; //histogram columns 
    //representation of graph mark and space 
    private static final String MARK = "-", SPACE =" ";
    private static final int COLUMN_WIDTH = 8; 
    private int maxHeight =0; //size of highest histogram 
    //histogram data. each row contains makrs or space. last 
    //row contains footer 
    private String graphRepresentation[][]; 

    Histogram(List<Column> columns) {
        this.columns = columns;
        calculateMaxHeight();
        prepareGraphRepresentation();
    }

    //find tallest column 
    private void calculateMaxHeight() {
        for(Column col : columns) {
            if(col.getQty() > maxHeight) { maxHeight = col.getQty();}
        }
        maxHeight +=1; //add 1 for column footer
    }

    //fill graphRepresentation with spaces, marks or footer 
    private void prepareGraphRepresentation() {

        graphRepresentation = new String[maxHeight][columns.size()];

        for(int colIndex = 0 ; colIndex < columns.size() ; colIndex ++ ) {

            Column col = columns.get(colIndex);
            int rowCounter = 0;

            for(int rowIndex = maxHeight -1 ; rowIndex >=0; rowIndex -- ) {

                String s = SPACE;
                if (rowCounter == 0 ) { //histogram footer
                     s = col.getInteravalStart()+"-"+col.getIntervalEnd();
                }else if(rowCounter <= col.getQty()) {
                    s = MARK;
                }
                graphRepresentation[rowIndex][colIndex] = format(s);
                rowCounter++;
            }
        }
    }

    //add spaces to s to make it as wide as column width
    private String format(String s) {

        int leftSpaces = (COLUMN_WIDTH - s.length())/2;
        int rightSpaces = COLUMN_WIDTH - s.length() - leftSpaces;

        StringBuilder sb = new StringBuilder();
        //add left spaces
        for(int spaces =0 ; spaces <leftSpaces; spaces++) {
            sb.append(SPACE);
        }
        sb.append(s);
        for(int spaces =0 ; spaces <rightSpaces; spaces++) {
            sb.append(SPACE);
        }

        return sb.toString();
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        for(String[] row : graphRepresentation ) {
            for(String s : row ) {
                sb.append(s);
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}