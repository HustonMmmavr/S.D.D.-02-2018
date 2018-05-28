package com.colorit.backend.game.gameobjects;

import com.colorit.backend.game.gameobjects.math.Point;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.colorit.backend.game.GameConfig.BAD_AREA;
import static com.colorit.backend.game.GameConfig.DEFAULT_FIELD_VALUE;
import static com.colorit.backend.game.GameConfig.SCORED;

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

    private void markCell(int i, int j, int id) {
        this.matrix.get(i).set(j, id);
    }

    public void markCell(Point position, long id) {
        markCell(position.getY(), position.getX(), (int) id);
    }

    public int getRank() {
        return matrixRank;
    }

    @SuppressWarnings("OverlyComplexBooleanExpression")
    private boolean isPointValid(List<List<Integer>> fieldCopy, Point point, int playerId) {
        return (
                (point.getX() > 0 && point.getX() < this.matrixRank - 1) && // not on first or last col
                        (point.getY() > 0 && point.getY() < this.matrixRank - 1) && // not on first or last row
                        (fieldCopy.get(point.getY()).get(point.getX()) != playerId) && // current player is not owner of cell
                        (fieldCopy.get(point.getY()).get(point.getX()) != SCORED) && // not marked as scored
                        (fieldCopy.get(point.getY()).get(point.getX()) != BAD_AREA) // not marked as bad area
        );
    }

    @SuppressWarnings("OverlyComplexBooleanExpression")
    private boolean isDotValidNoEdgeCheck(List<List<Integer>> fieldCopy, Point point, int playerId) {
        return (
                (point.getX() > 0 && point.getX() <= this.matrixRank - 1) && // not on first or last col
                        (point.getY() > 0 && point.getY() <= this.matrixRank - 1) && // not on first or last row
                        (fieldCopy.get(point.getY()).get(point.getX()) != playerId) && // current player is not owner of cell
                        (fieldCopy.get(point.getY()).get(point.getX()) != SCORED) && // not marked as scored
                        (fieldCopy.get(point.getY()).get(point.getX()) != BAD_AREA) // not marked as bad area
        );
    }

    private List<Point> getArrayOfPointsToCheck(List<List<Integer>> fieldCopy, Point position, int playerId) {
        final List<Point> pointsToCheck = new ArrayList<>();

        for (int x = position.getX() - 1; x <= position.getX() + 1; x++) {
            for (int y = position.getY() - 1; y <= position.getY() + 1; y++) {
                final Point pointToCheck = new Point(x, y);
                if (isPointValid(fieldCopy, pointToCheck, playerId)) {
                    pointsToCheck.add(pointToCheck);
                }
            }
        }
        return pointsToCheck;
    }

    /**
     * Check if we got rounded area.
     *
     * @param position Last player step.
     * @param playerId Player.
     */
    public boolean checkArea(Point position, int playerId) {
        final List<List<Integer>> matrixCopy = new ArrayList<>(matrixRank);
        for (List<Integer> list: matrix) {
            matrixCopy.add(new ArrayList<>(list));
        }
        final List<Point> checkArray = this.getArrayOfPointsToCheck(matrixCopy, position, playerId);
        final LinkedList<Point> stack = new LinkedList<>();
        boolean flagScore = false;
        /*
        1. Поместить затравочный пиксел в стек;
        2. Извлечь пиксел из стека;
        3. Присвоить пикселу требуемое значение (цвет внутренней области);
        4. Каждый окрестный пиксел добавить в стек, если он
        4.1. Не является граничным;
        4.2. Не обработан ранее (т.е. его цвет отличается от цвета границы или цвета внутренней области);
        5. Если стек не пуст, перейти к шагу 2
        */

        for (Point startPoint : checkArray) {
            if (!((matrixCopy.get(startPoint.getY()).get(startPoint.getX()) == SCORED) ||
                    (matrixCopy.get(startPoint.getY()).get(startPoint.getX()) == BAD_AREA))) {
                final LinkedList<Point> curAreaPoints = new LinkedList<>();
                stack.addLast(startPoint);
                boolean isBadArea = false;
                while (!stack.isEmpty()) {
                    final Point curPoint = stack.pollLast();
                    matrixCopy.get(curPoint.getY()).set(curPoint.getX(), SCORED);
                    curAreaPoints.addLast(curPoint);

                    // if on edge => badArea
                    //noinspection OverlyComplexBooleanExpression
                    if (!isBadArea && (curPoint.getY() == 0 || curPoint.getY() == matrixRank - 1) ||
                            curPoint.getX() == 0 || curPoint.getX() == matrixRank - 1) {
                        isBadArea = true;
                    }

                    // adding around dots
                    for (int y = curPoint.getY() - 1; y <= curPoint.getY() + 1; y++) {
                        for (int x = curPoint.getX() - 1; x <= curPoint.getX() + 1; x++) {
                            final Point pointToAdd = new Point(x, y);
                            if (isDotValidNoEdgeCheck(matrixCopy, pointToAdd, playerId)) {
                                stack.addLast(pointToAdd);
                            }
                        }
                    }
                }

                // mark that player can get score on next step
                if (!isBadArea) {
                    flagScore = true;
                }

                // after finishing area by current startingDot
                // filling it with badArea
                // or filling with player id
                while (!curAreaPoints.isEmpty()) {
                    final Point pointToColor = curAreaPoints.pollLast();
                    if (isBadArea) {
                        matrixCopy.get(pointToColor.getY()).set(pointToColor.getX(), BAD_AREA);
                    } else {
                        matrixCopy.get(pointToColor.getY()).set(pointToColor.getX(), playerId);
                    }
                    if (!isBadArea) {
                        this.matrix.get(pointToColor.getY()).set(pointToColor.getX(), playerId);
                    }
                }
            }
        }
        return flagScore;
    }

    public int countScoresForPlayer(int playerId) {
        int score = 0;

        for (int i = 0; i < matrixRank; i++) {
            for (int j = 0; j < matrixRank; j++) {
                if (matrix.get(i).get(j) == playerId) {
                    score++;
                    matrix.get(i).set(j, DEFAULT_FIELD_VALUE);
                }
            }
        }
        return score;
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
