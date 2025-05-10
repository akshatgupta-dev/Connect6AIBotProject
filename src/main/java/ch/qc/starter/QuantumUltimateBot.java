package ch.qc.starter;

import ch.cern.quantumconnect.core.Algorithm;
import ch.cern.quantumconnect.core.Experiment;
import ch.cern.quantumconnect.core.QuantumField;
import ch.cern.quantumconnect.core.SpaceOwnership;

import java.util.*;

/**
 * Single-file submission: QuantumUltimateBot.java
 * Combines bot logic, evaluation masks, and support classes in one file.
 */
public class QuantumUltimateBot implements Algorithm {

    // Constants
    private static final long TIME_BUDGET_NS = 90_000_000;

    // State
    private volatile boolean timeUp;
    private boolean useSpecial;
    private long startTime;

    /**
     * Entry point for local testing.
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws InterruptedException {
        Experiment experiment = new Experiment(new QuantumUltimateBot(), new RandomBot());
        experiment.runExperiment();
    }

    /**
     * Main decision function: returns column to drop quark in.
     */
    @Override
    public int accelerateQuark(QuantumField field) {
        startTime = System.nanoTime();
        timeUp = false;
        useSpecial = false;

        List<int[]> validMoves = getValidMoves(field);
        if (validMoves.isEmpty()) return -1;

        int bestMove = validMoves.get(0)[1];

        // Iterative deepening for better time control
        for (int depth = 1; depth <= 6 && !timeUp; depth++) {
            try {
                bestMove = searchRoot(field, depth);
            } catch (TimeUp ignored) {
                break;
            }
        }

        return bestMove;
    }

    /**
     * Determines whether to use special move.
     */
    @Override
    public boolean useSpecialMove(QuantumField field) {
        return useSpecial;
    }

    /**
     * Search top level moves with alpha-beta pruning.
     */
    private int searchRoot(QuantumField field, int depth) throws TimeUp {
        int bestCol = -1;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for (int[] move : getValidMoves(field)) {
            checkTime();

            QuantumField copy = field.clone();
            copy.dropQuark(move[1]);

            int score = -alphaBeta(copy, depth - 1, -beta, -alpha);

            if (score > alpha) {
                alpha = score;
                bestCol = move[1];
            }

            if (alpha >= beta) break;
        }

        // Trigger special move if situation is dire
        if (alpha < MaskState.ENEMY_5.weight) {
            useSpecial = true;
        }

        return bestCol;
    }

    /**
     * Recursive alpha-beta pruning.
     */
    private int alphaBeta(QuantumField field, int depth, int alpha, int beta) throws TimeUp {
        checkTime();

        if (depth == 0 || field.isFull()) return evaluate(field);

        for (int[] move : getValidMoves(field)) {
            field.dropQuark(move[1]);
            int val = -alphaBeta(field, depth - 1, -beta, -alpha);
            field.undoLastMove();

            if (val >= beta) return beta;
            alpha = Math.max(alpha, val);
        }

        return alpha;
    }

    /**
     * Evaluation function based on predefined mask weights.
     */
    private int evaluate(QuantumField field) {
        SpaceOwnership[][] board = field.getField();
        int score = 0;
        int total = field.getNumberOfRows() * field.getNumberOfColumns();

        for (int key = 0; key < total; key++) {
            int[] masks = MaskLibrary.POINT_TO_MASKS.getOrDefault(key, new int[0]);
            for (int mid : masks) {
                MaskState state = MaskLibrary.getMaskState(mid, board, field);
                score += state.weight;
            }
        }

        return score;
    }

    /**
     * Checks if time budget has been exceeded.
     */
    private void checkTime() throws TimeUp {
        if (System.nanoTime() - startTime > TIME_BUDGET_NS) {
            timeUp = true;
            throw new TimeUp();
        }
    }

    /**
     * Gets all valid moves (bottommost empty slot in each column).
     */
    private List<int[]> getValidMoves(QuantumField field) {
        List<int[]> moves = new ArrayList<>();
        int rows = field.getNumberOfRows();
        int cols = field.getNumberOfColumns();

        for (int col = 0; col < cols; col++) {
            if (field.isColumnFull(col)) continue;
            for (int row = rows - 1; row >= 0; row--) {
                if (field.isSpaceEmpty(row, col)) {
                    moves.add(new int[]{row, col});
                    break;
                }
            }
        }

        return moves;
    }

    // ---------------- Supporting Classes ----------------

    /** Exception class to signal timeout */
    private static class TimeUp extends Exception {}

    /** Fallback RandomBot */
    public static class RandomBot implements Algorithm {
        private final Random random = new Random();

        @Override
        public int accelerateQuark(QuantumField field) {
            List<Integer> valid = new ArrayList<>();
            for (int c = 0; c < field.getNumberOfColumns(); c++) {
                if (!field.isColumnFull(c)) valid.add(c);
            }
            return valid.isEmpty() ? -1 : valid.get(random.nextInt(valid.size()));
        }

        @Override
        public boolean useSpecialMove(QuantumField field) {
            return false;
        }
    }

    /** Enum to represent mask evaluation state */
    private enum MaskState {
        EMPTY(0),
        OWN_1(3), OWN_2(14), OWN_3(60), OWN_4(300), OWN_5(20000),
        ENEMY_1(4), ENEMY_2(18), ENEMY_3(80), ENEMY_4(400), ENEMY_5(10000),
        BLOCKED(0);

        final int weight;

        MaskState(int weight) {
            this.weight = weight;
        }
    }

    /** Static class for managing masks */
    private static class MaskLibrary {
        static final Map<Integer, int[]> POINT_TO_MASKS = new HashMap<>();

        // Replace with actual mask data
        static final int[][][] WINNING_MASKS = new int[0][][];

        static MaskState getMaskState(int maskId, SpaceOwnership[][] board, QuantumField field) {
            int own = 0, opp = 0;

            for (int[] pos : WINNING_MASKS[maskId]) {
                SpaceOwnership cell = board[pos[0]][pos[1]];
                if (cell == SpaceOwnership.EMPTY) continue;

                if (field.belongsToMe(pos[0], pos[1])) own++;
                else opp++;

                if (own > 0 && opp > 0) return MaskState.BLOCKED;
            }

            if (own == 0 && opp == 0) return MaskState.EMPTY;
            return (opp == 0) ? MaskState.values()[own] : MaskState.values()[6 + opp];
        }
    }
}
