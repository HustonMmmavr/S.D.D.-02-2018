package com.colorit.backend.game.gameobjects;

import java.util.ArrayList;
import java.util.List;

public class GameField extends GameObject {
    private Integer matrixRank;
    private Integer INITIAL_FIELD_VALUE = 0;
    private List<List<Integer>> matrix;
    
    public GameField(Integer rank) {
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

    public void markCell(Integer i, Integer j, Integer id) {
        this.matrix.get(i).set(j, id);
    }

    public void print() {
        for(List<Integer> row : matrix) {
            for (Integer data: row) {
                System.out.print(data);
            }
            System.out.println();
        }
    }


    // implement
    public void findCycle() {

    }
}
