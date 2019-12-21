package negabinary.qubits;


import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import negabinary.qubits.block.QubitBlockTileEntity;
import negabinary.qubits.command.QubitsCommand;

/**
 * The main class of the mod, this is the class that looks like a mod to forge.
 */

@Mod(QubitsMod.MOD_ID)
@Mod.EventBusSubscriber(modid = QubitsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class QubitsMod {

    /**
     * The modid of this mod, this has to match the modid in the mods.toml and has to be in the format defined in {@link net.minecraftforge.fml.loading.moddiscovery.ModInfo}
     */
    public static final String MOD_ID = "qubits";

    public QubitsMod() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void serverStarting(FMLServerStartingEvent event) {
        new QubitsCommand(event.getCommandDispatcher());
    }

    @SubscribeEvent
    public void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(TileEntityType.Builder.create(QubitBlockTileEntity::new).build(null).setRegistryName(QubitsMod.MOD_ID, "qubit_block_tile_entity"));
    }
}