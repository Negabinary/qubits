package negabinary.qubits.internal;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public class QubitReferenceBuilder {
    public static IQubitReference readRef(World world, CompoundNBT compound) {
        switch (compound.getString("ref_type")) {
            case "block" : return new BlockQubitReference(compound);
            default: throw new RuntimeException();
        }
    }
}

