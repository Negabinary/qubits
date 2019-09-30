package virbinarus.qubits.internal.qsystems;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import virbinarus.qubits.internal.qsystem.IQSystem;

import java.util.concurrent.Callable;

public class CapabilityQSystems {
  @CapabilityInject(IQSystems.class)
  public static Capability<IQSystems> Q_SYSTEMS = null;

  public static void register() {
    CapabilityManager.INSTANCE.register(IQSystems.class, new Capability.IStorage<IQSystems>() {
              @Override
              public INBT writeNBT(Capability<IQSystems> capability, IQSystems instance, Direction side) {
                return null; //TODO
              }

              @Override
              public void readNBT(Capability<IQSystems> capability, IQSystems instance, Direction side, INBT nbt) {
                //TODO
              }
            },
            new Callable<IQSystems>() {
              @Override
              public IQSystems call() throws Exception {
                return new QSystems();
              }
            });
  }
}
