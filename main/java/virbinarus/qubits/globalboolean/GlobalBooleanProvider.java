package virbinarus.qubits.globalboolean;

import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GlobalBooleanProvider implements ICapabilitySerializable<INBT> {
    @CapabilityInject(IGlobalBoolean.class)
    public static Capability<GlobalBoolean> GLOBAL_BOOLEAN = null;

    private GlobalBoolean globalBoolean;

    private final LazyOptional<IGlobalBoolean> holder = LazyOptional.of(new NonNullSupplier<IGlobalBoolean>() {
        @Nonnull
        @Override
        public IGlobalBoolean get() {
            return globalBoolean;
        }
    });

    public GlobalBooleanProvider() {
        globalBoolean  = new GlobalBoolean(false);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return holder.cast();
    }

    @Override
    public INBT serializeNBT() {
        System.out.println(GLOBAL_BOOLEAN);
        return GLOBAL_BOOLEAN.getStorage().writeNBT(GLOBAL_BOOLEAN, globalBoolean, null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        GLOBAL_BOOLEAN.getStorage().readNBT(GLOBAL_BOOLEAN, globalBoolean, null, nbt);
    }
}
