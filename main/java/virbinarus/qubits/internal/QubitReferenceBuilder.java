package virbinarus.qubits.internal;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public class QubitReferenceBuilder {
    public static IQubitReference readRef(World world, CompoundNBT compound) {
        System.out.println(compound.getString("ref_type"));
        switch (compound.getString("ref_type")) {
            case "block" : return new BlockQubitReference(compound);
            default: throw new RuntimeException();
        }
    }
}
