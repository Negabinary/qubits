package virbinarus.qubits.internal.qsystems;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class QSystemProvider implements ICapabilityProvider {

    @CapabilityInject(IQSystems.class)
    public static Capability<IQSystems> Q_SYSTEMS = null;

    private IQSystems q_systems;

    public QSystemProvider() {
        q_systems  = new QSystems();
    }

    private final LazyOptional<IQSystems> holder = LazyOptional.of(new NonNullSupplier<IQSystems>() {
        @Nonnull
        @Override
        public IQSystems get() {
            return q_systems;
        }
    });

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return holder.cast();
    }
}
