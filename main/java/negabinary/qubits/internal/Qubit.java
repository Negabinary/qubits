package negabinary.qubits.internal;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

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

    public QubitSystem getQubitSystem(IWorld world) {
        if (qubitSystem == null) {
            qubitSystem = systemHolderReference.getQubit(world).getQubitSystem(world);
        }
        return  qubitSystem;
    }

    public CompoundNBT write() {
        CompoundNBT compound = new CompoundNBT();
        compound.putByte("is_system_holder", (byte)(isSystemHolder? 1 : 0));
        if (isSystemHolder) {
            compound.put("qubit_system", qubitSystem.write());
        }
        compound.put("qubit_system_ref", systemHolderReference.write());
        compound.putInt("qubit_id", qubitID);
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
        } else {
            qubitSystem = null;
        }
        qubitID = compound.getInt("qubit_id");
    }

    public void applyNot(Qubit[] controlQubits, World world) {
        getQubitSystem(world).applyNot(qubitID, getQubitIDs(controlQubits, world), world);
        systemHolderReference.markDirty(world);
    }

    public void applyH(Qubit[] controlQubits, World world) {
        getQubitSystem(world).applyH(qubitID, getQubitIDs(controlQubits, world), world);
        systemHolderReference.markDirty(world);
    }

    public boolean applyMeasure(IWorld world) {
        boolean result = getQubitSystem(world).applyMeasure(qubitID, world);
        systemHolderReference.markDirty(world);
        return result;
    }

    public boolean applyMeasureAndRemove(IWorld world) {
        boolean result = getQubitSystem(world).applyMeasureAndRemove(qubitID, world);
        return result;
    }

    public int getQubitID() {
        return qubitID;
    }

    public int[] getQubitIDs(Qubit[] qubits, World world) {
        QubitSystem qubitSystem = getQubitSystem(world);
        int[] qubitIDs = new int[qubits.length];
        for (int i = 0; i < qubits.length; i++) {
            Qubit qubit = qubits[i];
            if (qubit.getQubitSystem(world) != getQubitSystem(world)) {
                qubitSystem.merge(qubit.getQubitSystem(world), world);
                System.out.println("MERG");
            }
            qubitIDs[i] = qubit.getQubitID();
        }
        return qubitIDs;
    }
}
