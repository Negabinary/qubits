package virbinarus.qubits.internal;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import java.util.UUID;

public class Qubit {
    public int qubitID;
    public boolean isSystemHolder;
    public IQubitReference systemHolderReference;
    public QubitSystem qubitSystem;

    public Qubit(IQubitReference qubitReference) {
        qubitSystem = new QubitSystem(qubitReference);
        systemHolderReference = qubitReference;
        isSystemHolder = true;
        qubitID = 0;
    }

    public QubitSystem getQubitSystem() {
        if (qubitSystem == null) {
            qubitSystem = systemHolderReference.getQubit().getQubitSystem();
        }
        return  qubitSystem;
    }

    public CompoundNBT write() {
        CompoundNBT compound = new CompoundNBT();
        compound.putByte("is_system_holder", (byte)(isSystemHolder? 1 : 0));
        if (isSystemHolder) {
            compound.put("qubit_system", getQubitSystem().write());
        }
        compound.put("qubit_system_ref", systemHolderReference.write());
        return compound;
    }

    public void read(World world, CompoundNBT compound) {

        isSystemHolder = compound.getByte("is_system_holder") == 1;
        systemHolderReference = QubitReferenceBuilder.readRef(world, compound.getCompound("qubit_system_ref"));
        if (isSystemHolder) {
            if (qubitSystem != null) {
                qubitSystem.read(world, compound.getCompound("qubit_system"));
            } else {
                qubitSystem = new QubitSystem(world, compound.getCompound("qubit_system"));
            }
        }
    }

    public void applyNot(World world) {
        getQubitSystem().applyNot(qubitID, world);
        systemHolderReference.markDirty(world);
    }

    public void applyH(World world) {
        getQubitSystem().applyH(qubitID, world);
        systemHolderReference.markDirty(world);
    }
}
