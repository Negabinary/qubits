package negabinary.qubits;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import negabinary.qubits.block.QubitBlockTileEntity;


@Mod.EventBusSubscriber(modid = QubitsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegisterAll {
    @SubscribeEvent
    public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(
                TileEntityType.Builder.create(QubitBlockTileEntity::new).build(null)
                        .setRegistryName(QubitsMod.MOD_ID, "qubit_block_tile_entity")
        );
    }
}
