package negabinary.qubits;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = QubitsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(QubitsMod.MOD_ID)
public class ModItems {

    public static final Item quantum_dust = null;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new Item(new Item.Properties()).setRegistryName(QubitsMod.MOD_ID, "quantum_dust"),
                new BlockItem(ModBlocks.QUBIT_BLOCK, new Item.Properties().group(ItemGroup.REDSTONE)).setRegistryName("qubit_block") ,
                new BlockItem(ModBlocks.QUANTUM_TENDRIL, new Item.Properties().group(ItemGroup.REDSTONE)).setRegistryName("quantum_tendril"),
                new BlockItem(ModBlocks.Q_NOT_GATE, new Item.Properties().group(ItemGroup.REDSTONE)).setRegistryName("q_not_gate"),
                new BlockItem(ModBlocks.Q_H_GATE, new Item.Properties().group(ItemGroup.REDSTONE)).setRegistryName("q_h_gate"),
                new BlockItem(ModBlocks.Q_CONTROL, new Item.Properties().group(ItemGroup.REDSTONE)).setRegistryName("q_control"),
                new BlockItem(ModBlocks.Q_MEASURE_GATE, new Item.Properties().group(ItemGroup.REDSTONE)).setRegistryName("q_measure_gate")
        );
    }
}
