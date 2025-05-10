// package ch.qc.starter;

// import ch.cern.quantumconnect.core.Algorithm;
// import ch.cern.quantumconnect.core.QuantumField;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.Random;

// public class MyIntelligentBot implements Algorithm {
//     private final Random rand = new Random();
//     private boolean specialUsed = false;

//     @Override
//     public int accelerateQuark(QuantumField field) {
//         int rows = field.getNumberOfRows();
//         int cols = field.getNumberOfColumns();

//         // Determine IDs: current player vs opponent
//         int myId   = field.getCurrentPlayer();                 // e.g. 1 or 2
//         int oppId  = myId == 1 ? 2 : 1;

//         // Build a local copy of the board: 0=empty, 1/2=player discs
//         int[][] board = new int[rows][cols];
//         for (int r = 0; r < rows; r++) {
//             for (int c = 0; c < cols; c++) {
//                 board[r][c] = field.getDisc(r, c);
//             }
//         }

//         // 1) Win if possible
//         for (int c : validCols(field)) {
//             int r = dropRow(board, c);
//             if (r < 0) continue;
//             board[r][c] = myId;
//             if (isWinningMove(board, r, c, myId)) {
//                 return c;
//             }
//             board[r][c] = 0;
//         }

//         // 2) Block opponent’s win
//         for (int c : validCols(field)) {
//             int r = dropRow(board, c);
//             if (r < 0) continue;
//             board[r][c] = oppId;
//             if (isWinningMove(board, r, c, oppId)) {
//                 return c;
//             }
//             board[r][c] = 0;
//         }

//         // 3) Center‑first fallback
//         int center = cols / 2;
//         if (!field.isColumnFull(center)) return center;
//         for (int d = 1; d < cols; d++) {
//             if (center - d >= 0 && !field.isColumnFull(center - d)) {
//                 return center - d;
//             }
//             if (center + d < cols && !field.isColumnFull(center + d)) {
//                 return center + d;
//             }
//         }

//         // 4) As a last resort, pick randomly
//         List<Integer> choices = validCols(field);
//         return choices.get(rand.nextInt(choices.size()));
//     }

//     @Override
//     public boolean useSpecialMove(QuantumField field) {
//         if (specialUsed) return false;
//         int cols = field.getNumberOfColumns();
//         int full = 0;
//         for (int c = 0; c < cols; c++) {
//             if (field.isColumnFull(c)) full++;
//         }
//         // Clear once when >50% of columns are full
//         if (full > cols * 0.5) {
//             specialUsed = true;
//             return true;
//         }
//         return false;
//     }

//     // Helper: list of non‑full columns
//     private List<Integer> validCols(QuantumField field) {
//         List<Integer> cols = new ArrayList<>();
//         for (int c = 0; c < field.getNumberOfColumns(); c++) {
//             if (!field.isColumnFull(c)) {
//                 cols.add(c);
//             }
//         }
//         return cols;
//     }

//     // Helper: given a local board, find where a disc would land in col (or –1 if full)
//     private int dropRow(int[][] board, int col) {
//         for (int r = 0; r < board.length; r++) {
//             if (board[r][col] == 0) {
//                 return r;
//             }
//         }
//         return -1;
//     }

//     // Check if placing `id` at (row,col) makes a 6‑in‑a‑row
//     private boolean isWinningMove(int[][] b, int row, int col, int id) {
//         int rows = b.length, cols = b[0].length;
//         int[][] dirs = {{0,1}, {1,0}, {1,1}, {1,-1}};
//         for (var d : dirs) {
//             int count = 1;
//             // Explore both directions
//             for (int sign : new int[]{-1,1}) {
//                 int r = row + d[0]*sign, c = col + d[1]*sign;
//                 while (r >= 0 && r < rows && c >= 0 && c < cols && b[r][c] == id) {
//                     count++;
//                     r += d[0]*sign;
//                     c += d[1]*sign;
//                 }
//             }
//             if (count >= 6) return true;
//         }
//         return false;
//     }
// }
