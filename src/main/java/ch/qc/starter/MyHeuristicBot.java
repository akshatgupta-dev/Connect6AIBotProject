package ch.qc.starter;

import ch.cern.quantumconnect.core.Algorithm;
import ch.cern.quantumconnect.core.QuantumField;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Heuristic bot:
 *  - center‑first spiral
 *  - after 30 moves: focus on central pairs
 *  - special move exactly on turn 60
 */
public class MyHeuristicBot implements Algorithm {
    private final Random rand = new Random();
    private boolean specialUsed = false;
    private int turnCount = 0;

    @Override
    public int accelerateQuark(QuantumField field) {
        turnCount++;
        int cols = field.getNumberOfColumns();
        int center = cols / 2;

        // After 30 drops, focus on central columns
        if (turnCount > 30) {
            int[] priority = { center, center-1, center+1, center-2, center+2, center-3, center+3 };
            for (int c : priority) {
                if (c >= 0 && c < cols && !field.isColumnFull(c)) {
                    return c;
                }
            }
        }

        // Default: spiral from center
        if (!field.isColumnFull(center)) {
            return center;
        }
        for (int d = 1; d < cols; d++) {
            int left = center - d, right = center + d;
            if (left >= 0 && !field.isColumnFull(left)) {
                return left;
            }
            if (right < cols && !field.isColumnFull(right)) {
                return right;
            }
        }

        // Fallback: pick any non‑full at random
        List<Integer> avail = new ArrayList<>();
        for (int c = 0; c < cols; c++) {
            if (!field.isColumnFull(c)) {
                avail.add(c);
            }
        }
        return avail.get(rand.nextInt(avail.size()));
    }

    @Override
    public boolean useSpecialMove(QuantumField field) {
        // Exactly once on turn 60
        if (!specialUsed && turnCount >= 60) {
            specialUsed = true;
            return true;
        }
        return false;
    }
}
