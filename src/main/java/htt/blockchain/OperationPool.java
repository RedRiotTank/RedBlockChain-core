package htt.blockchain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class OperationPool implements Serializable {
    private final CopyOnWriteArrayList<Blockable> operations = new CopyOnWriteArrayList<>();

    public void addOperation(Blockable operation) {
        operations.add(operation);
    }

    public List<Blockable> getOperations() {
        return new ArrayList<>(operations);
    }
}
