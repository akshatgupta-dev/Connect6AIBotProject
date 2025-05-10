package ch.qc.starter;

import ch.cern.quantumconnect.core.Algorithm;
import ch.cern.quantumconnect.core.QuantumField;
import ch.cern.quantumconnect.core.SpaceOwnership;
import ch.qc.starter.MaskLibrary;
import ch.qc.starter.MaskState;
import java.util.*;

/**
 * QuantumStrategistBot: a high-performance, timed alpha-beta bot with
 * iterative deepening, heuristic evaluation of 6-in-a-row patterns,
 * and dynamic use of the special move.
 * Turn limit: 100ms (90ms search + buffer).
 */
public class QuantumStrategistBot implements Algorithm {
    private static final long TIME_BUDGET_NS = 90_000_000; // 90 ms
    private volatile boolean timeUp;
    private boolean useSpecial;
    private long startTime;

    @Override
    public int accelerateQuark(QuantumField field) {
        startTime = System.nanoTime();
        timeUp = false;
        useSpecial = false;

        List<int[]> moves = getValidPositions(field);
        int bestMove = moves.isEmpty() ? -1 : moves.get(0)[1];

        // Iterative deepening from depth=1 up to maxDepth
        for (int depth = 1; depth <= 6 && !timeUp; depth++) {
            try {
                bestMove = searchRoot(field, depth);
            } catch (TimeUpException e) {
                break;
            }
        }
        return bestMove;
    }

    @Override
    public boolean useSpecialMove(QuantumField field) {
        return useSpecial;
    }

    private int searchRoot(QuantumField field, int depth) throws TimeUpException {
        int bestCol = -1;
        int alpha = Integer.MIN_VALUE, beta = Integer.MAX_VALUE;
        for (int[] mv : getValidPositions(field)) {
            checkTime();
            QuantumField copy = field.clone();
            copy.dropQuark(mv[1]);
            int score = -alphaBeta(copy, depth - 1, -beta, -alpha);
            if (score > alpha) {
                alpha = score;
                bestCol = mv[1];
            }
            if (alpha >= beta) break;
        }
        // trigger special if under threat
        if (alpha < MaskState.ENEMY_5.getWeight()) useSpecial = true;
        return bestCol;
    }

    private int alphaBeta(QuantumField field, int depth, int alpha, int beta) throws TimeUpException {
        checkTime();
        if (depth == 0 || field.isFull()) {
            return evaluate(field);
        }
        for (int[] mv : getValidPositions(field)) {
            field.dropQuark(mv[1]);
            int val = -alphaBeta(field, depth - 1, -beta, -alpha);
            field.undoLastMove();
            if (val >= beta) return beta;
            alpha = Math.max(alpha, val);
        }
        return alpha;
    }

    private int evaluate(QuantumField field) {
        SpaceOwnership[][] board = field.getField();
        int score = 0;
        int totalCells = field.getNumberOfRows() * field.getNumberOfColumns();
        for (int key = 0; key < totalCells; key++) {
            for (int maskId : MaskLibrary.POINT_TO_MASKS.getOrDefault(key, new int[0])) {
                MaskState st = evaluateMask(MaskLibrary.WINNING_MASKS[maskId], board, field);
                score += st.getWeight();
            }
        }
        return score;
    }

    private void checkTime() throws TimeUpException {
        if (System.nanoTime() - startTime > TIME_BUDGET_NS) {
            timeUp = true;
            throw new TimeUpException();
        }
    }

    private List<int[]> getValidPositions(QuantumField field) {
        List<int[]> list = new ArrayList<>();
        int rows = field.getNumberOfRows();
        int cols = field.getNumberOfColumns();
        for (int c = 0; c < cols; c++) {
            if (field.isColumnFull(c)) continue;
            for (int r = rows - 1; r >= 0; r--) {
                if (field.isSpaceEmpty(r, c)) {
                    list.add(new int[]{r, c});
                    break;
                }
            }
        }
        return list;
    }

    private MaskState evaluateMask(int[][] mask, SpaceOwnership[][] board, QuantumField field) {
        int own = 0, opp = 0;
        for (int[] p : mask) {
            SpaceOwnership cell = board[p[0]][p[1]];
            if (cell == SpaceOwnership.EMPTY) continue;
            if (field.belongsToMe(p[0], p[1])) own++;
            else opp++;
            if (own > 0 && opp > 0) return MaskState.BLOCKED;
        }
        if (own == 0 && opp == 0) return MaskState.EMPTY;
        return opp == 0 ? MaskState.forOwn(own) : MaskState.forEnemy(opp);
    }

    private static class TimeUpException extends Exception {}
}
