package negabinary.qubits.internal;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public interface IQubitReference {
    public void updateLocalState(double newState, IWorld world);

    public Qubit getQubit(IWorld world);

    public void markDirty(IWorld world);

    public CompoundNBT write();
}
