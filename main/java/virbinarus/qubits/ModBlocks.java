package virbinarus.qubits;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import virbinarus.qubits.block.GlobalBooleanBlock;
import virbinarus.qubits.block.QubitBlock;

@Mod.EventBusSubscriber(modid = QubitsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(QubitsMod.MOD_ID)
public class ModBlocks {

    public static final Block QUBIT_BLOCK = null;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                new QubitBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(5).harvestLevel(2).harvestTool(ToolType.PICKAXE)).setRegistryName(QubitsMod.MOD_ID, "qubit_block"),
                new GlobalBooleanBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(5).harvestLevel(2).harvestTool(ToolType.PICKAXE)).setRegistryName(QubitsMod.MOD_ID, "global_boolean_block"),
                new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(5).harvestLevel(2).harvestTool(ToolType.PICKAXE)).setRegistryName(QubitsMod.MOD_ID, "quantum_tendril")
        );

    }
}
