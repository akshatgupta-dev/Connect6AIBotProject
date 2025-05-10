package ch.qc.starter;

import ch.cern.quantumconnect.core.Algorithm;
import ch.cern.quantumconnect.core.QuantumField;
import java.util.*;

public class RandomBot implements Algorithm {
    private final Random random = new Random();

    @Override
    public int accelerateQuark(QuantumField quantumField) {
        int cols = quantumField.getNumberOfColumns();
        List<Integer> valid = new ArrayList<>();
        for (int c = 0; c < cols; c++) {
            if (!quantumField.isColumnFull(c)) valid.add(c);
        }
        return valid.isEmpty() ? -1 : valid.get(random.nextInt(valid.size()));
    }

    @Override
    public boolean useSpecialMove(QuantumField field) {
        return false;
    }
}