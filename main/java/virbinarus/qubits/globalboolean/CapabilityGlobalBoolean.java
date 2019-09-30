package virbinarus.qubits.globalboolean;

import jdk.nashorn.internal.objects.Global;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;

public class CapabilityGlobalBoolean {
    @CapabilityInject(IGlobalBoolean.class)
    public static Capability<IGlobalBoolean> GLOBAL_BOOLEAN = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IGlobalBoolean.class, new Capability.IStorage<IGlobalBoolean>() {
                    @Override
                    public INBT writeNBT(Capability<IGlobalBoolean> capability, IGlobalBoolean instance, Direction side) {
                        return new ByteNBT(instance.getBoolean() ? (byte) 1 : (byte) 0);
                    }

                    @Override
                    public void readNBT(Capability<IGlobalBoolean> capability, IGlobalBoolean instance, Direction side, INBT nbt) {
                        instance.setBoolean(((ByteNBT) nbt).getByte() == (byte) 1);
                    }
                },
                new Callable<IGlobalBoolean>() {
                    @Override
                    public IGlobalBoolean call() throws Exception {
                        return new GlobalBoolean(false);
                    }
                });
    }
}
