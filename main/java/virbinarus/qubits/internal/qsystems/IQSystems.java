package virbinarus.qubits.internal.qsystems;

import java.util.UUID;

public interface IQSystems {
    public int getSystemCount();
    public void add_q_system();
    public void add_q_system(UUID uuid);
}
