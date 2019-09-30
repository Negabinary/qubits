package virbinarus.qubits;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import virbinarus.qubits.commands.QubitsCommand;
import virbinarus.qubits.globalboolean.CapabilityGlobalBoolean;
import virbinarus.qubits.internal.qsystems.CapabilityQSystems;

/**
 * The main class of the mod, this is the class that looks like a mod to forge.
 */

@Mod(QubitsMod.MOD_ID)
public class QubitsMod {

    /**
     * The modid of this mod, this has to match the modid in the mods.toml and has to be in the format defined in {@link net.minecraftforge.fml.loading.moddiscovery.ModInfo}
     */
    public static final String MOD_ID = "qubits";

    public QubitsMod() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(new CapabilitiesEventHandler());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void commonSetup(FMLCommonSetupEvent evt) {
        CapabilityQSystems.register();
        CapabilityGlobalBoolean.register();
    }

    @SubscribeEvent
    public void serverStarting(FMLServerStartingEvent event) {
        new QubitsCommand(event.getCommandDispatcher());
    }

}