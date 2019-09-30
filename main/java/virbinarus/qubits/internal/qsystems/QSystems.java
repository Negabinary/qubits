package virbinarus.qubits.internal.qsystems;

import virbinarus.qubits.internal.qsystem.IQSystem;
import virbinarus.qubits.internal.qsystem.QSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QSystems implements IQSystems {

    private Map<UUID, IQSystem> qSystemMap;

    public QSystems() {
        qSystemMap = new HashMap<>();
    }

    public int getSystemCount() {
        return qSystemMap.size();
    }

    public void add_q_system() {
        QSystem newQSystem = new QSystem();
        qSystemMap.put(newQSystem.uuid, newQSystem);
    }

    public void add_q_system(UUID uuid) {
        QSystem newQSystem = new QSystem(uuid);
        qSystemMap.put(newQSystem.uuid, newQSystem);
    }
}
