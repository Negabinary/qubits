package virbinarus.qubits.internal;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public interface IQubitReference {
    public void updateLocalState(double newState, World world);

    public Qubit getQubit();

    public void markDirty(World world);

    public CompoundNBT write();
}
