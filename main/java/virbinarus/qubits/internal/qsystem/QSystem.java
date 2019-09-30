package virbinarus.qubits.internal.qsystem;

import virbinarus.qubits.internal.qubitreference.IQubitReference;

import java.util.ArrayList;
import java.util.UUID;

public class QSystem implements IQSystem {
    private ArrayList<IQubitReference> qubit_array;
    public UUID uuid;

    public QSystem() {
        this(UUID.randomUUID());
    }

    public QSystem(UUID newUUID) {
        qubit_array = new ArrayList<IQubitReference>(); //INITIAL CAPACITY OF TEN
        uuid = newUUID;
    }

    public int getQubitCount() {
        return qubit_array.size();
    }
}