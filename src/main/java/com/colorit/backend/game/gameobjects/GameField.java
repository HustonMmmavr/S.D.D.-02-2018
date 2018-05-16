package com.colorit.backend.game.gameobjects;

import com.colorit.backend.game.gameobjects.math.Point;

import java.util.ArrayList;
import java.util.List;

public class GameField extends GameObject {
    private Integer matrixRank;
    private Integer INITIAL_FIELD_VALUE = 0;
    private List<List<Integer>> matrix;
    
    public GameField(int rank) {
        matrixRank = rank;
        matrix = new ArrayList<>(rank);
        for (int i = 0; i < rank; i++) {
            List<Integer> row = new ArrayList<>(rank);
            for (int j = 0; j < rank; j++) {
                row.add(INITIAL_FIELD_VALUE);
            }
            matrix.add(row);
        }
    }

    public void markCell(int i, int j, int id) {
        this.matrix.get(i).set(j, id);
    }

    public void markCell(Point position, long id) {
        markCell(position.getY(), position.getX(), (int) id);
//        this.matrix.get((position.getX())).set(position.getY(), (int) id);
    }

    public void print() {
        for(List<Integer> row : matrix) {
//            for (Integer data: row) {
//                System.out.print(data);
//            }
//            System.out.println();
        }
    }

    public int searchLoop(long id) {
        return 0;
    }


    public int getRank() {
        return matrixRank;
    }





    // implement
    public void findCycle() {

    }


}
