// package ch.qc.starter;

// import ch.cern.quantumconnect.core.Algorithm;
// import ch.cern.quantumconnect.core.QuantumField;
// import java.util.*;

// public class SmartBot implements Algorithm {
//     private final int[] COLUMN_PRIORITY = {5,6,4,7,3,8,2,9,1,10,0,11}; // Center-first strategy
//     private int specialMoveCooldown = 0;

//     @Override
//     public int accelerateQuark(QuantumField field) {
//         // Check for winning moves
//         for (int col : COLUMN_PRIORITY) {
//             if (!field.isColumnFull(col) && isWinningMove(field, col)) {
//                 return col;
//             }
//         }

//         // Use priority-based selection
//         for (int col : COLUMN_PRIORITY) {
//             if (!field.isColumnFull(col)) {
//                 return col;
//             }
//         }

//         throw new IllegalStateException("No valid moves available");
//     }

//     @Override
//     public boolean useSpecialMove(QuantumField field) {
//         if (specialMoveCooldown > 0) {
//             specialMoveCooldown--;
//             return false;
//         }

//         // Check if opponent has potential 5-in-a-row
//         for (int col = 0; col < field.getNumberOfColumns(); col++) {
//             if (!field.isColumnFull(col) && isOpponentThreatening(field, col)) {
//                 specialMoveCooldown = 5;
//                 return true;
//             }
//         }
//         return false;
//     }

//     private boolean isWinningMove(QuantumField field, int col) {
//         // Simplified win check using column fill simulation
//         int simulatedHeight = getColumnHeight(field, col) + 1;
//         return simulatedHeight >= 6; // 6-connected required
//     }

//     private boolean isOpponentThreatening(QuantumField field, int col) {
//         // Simple threat detection based on column fill
//         int opponentHeight = getColumnHeight(field, col);
//         return opponentHeight >= 5; // Opponent needs 6, trigger at 5
//     }

//     private int getColumnHeight(QuantumField field, int col) {
//         // Estimate height based on column fill status
//         for (int row = 0; row < field.getNumberOfRows(); row++) {
//             if (field.isColumnFull(col)) {
//                 return field.getNumberOfRows();
//             }
//         }
//         return 0;
//     }
// }