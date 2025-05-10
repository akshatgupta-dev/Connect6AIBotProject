package ch.qc.starter;

import ch.cern.quantumconnect.core.Algorithm;
import ch.cern.quantumconnect.core.QuantumField;
import java.util.*;

private static class MaskLibrary {
    static final Map<Integer, int[]> POINT_TO_MASKS = new HashMap<>();
    static final int[][][] WINNING_MASKS;

    static {
        int rows = 6, cols = 7;
        List<int[][]> masks = new ArrayList<>();
        Map<String, Integer> maskIndexMap = new HashMap<>();

        int maskId = 0;

        // Directions: horizontal, vertical, diagonal /
        int[][] directions = {
            {0, 1},   // horizontal
            {1, 0},   // vertical
            {1, 1},   // diagonal \
            {1, -1}   // diagonal /
        };

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                for (int[] dir : directions) {
                    List<int[]> mask = new ArrayList<>();
                    for (int i = 0; i < 4; i++) {
                        int nr = r + dir[0] * i;
                        int nc = c + dir[1] * i;
                        if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) {
                            mask = null;
                            break;
                        }
                        mask.add(new int[]{nr, nc});
                    }
                    if (mask != null) {
                        // Sort and hash the mask to prevent duplicates
                        mask.sort((a, b) -> a[0] != b[0] ? a[0] - b[0] : a[1] - b[1]);
                        String hash = mask.toString();
                        if (!maskIndexMap.containsKey(hash)) {
                            masks.add(mask.toArray(new int[0][]));
                            maskIndexMap.put(hash, maskId);
                            for (int[] p : mask) {
                                int key = p[0] * cols + p[1];
                                POINT_TO_MASKS.computeIfAbsent(key, k -> new int[0]);
                                POINT_TO_MASKS.put(key, append(POINT_TO_MASKS.get(key), maskId));
                            }
                            maskId++;
                        }
                    }
                }
            }
        }

        WINNING_MASKS = masks.toArray(new int[0][][]);
    }

    private static int[] append(int[] arr, int val) {
        int[] res = Arrays.copyOf(arr, arr.length + 1);
        res[arr.length] = val;
        return res;
    }

    static MaskState getMaskState(int mid, SpaceOwnership[][] b, QuantumField f) {
        int own = 0, opp = 0;
        for (int[] p : WINNING_MASKS[mid]) {
            SpaceOwnership cell = b[p[0]][p[1]];
            if (cell == SpaceOwnership.EMPTY) continue;
            if (f.belongsToMe(p[0], p[1])) own++;
            else opp++;
            if (own > 0 && opp > 0) return MaskState.BLOCKED;
        }
        if (own == 0 && opp == 0) return MaskState.EMPTY;
        return opp == 0 ? MaskState.values()[own] : MaskState.values()[6 + opp];
    }
}
