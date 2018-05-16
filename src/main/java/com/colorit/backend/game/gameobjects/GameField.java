package com.colorit.backend.game.gameobjects;

import com.colorit.backend.game.gameobjects.math.Point;

import java.util.ArrayList;
import java.util.List;

public class GameField extends GameObject {
    private Integer matrixRank;
    private static final Integer INITIAL_FIELD_VALUE = 0;
    private List<List<Integer>> matrix;
    
    public GameField(int rank) {
        matrixRank = rank;
        matrix = new ArrayList<>(rank);
        for (int i = 0; i < rank; i++) {
            final List<Integer> row = new ArrayList<>(rank);
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
    }

    public int searchLoop(long id) {
        return 0;
    }


    public int getRank() {
        return matrixRank;
    }


    public List<List<Integer>> getMatrix() {
        return matrix;
    }

    @Override
    public GameFieldSnap getSnap() {
        return new GameFieldSnap(this);
    }

    public static class GameFieldSnap implements Snap<GameField> {
        private List<List<Integer>> field;
        public GameFieldSnap(GameField gameField) {
            this.field = gameField.getMatrix();
        }

        public List<List<Integer>> getField() {
            return field;
        }
    }


}
