package virbinarus.qubits;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import virbinarus.qubits.block.QuantumTendrilBlock;
import virbinarus.qubits.block.QubitBlock;

@Mod.EventBusSubscriber(modid = QubitsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(QubitsMod.MOD_ID)
public class ModBlocks {

    public static Block QUBIT_BLOCK;
    public static Block QUANTUM_TENDRIL;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        QUBIT_BLOCK = new QubitBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(5).harvestLevel(2).harvestTool(ToolType.PICKAXE)).setRegistryName(QubitsMod.MOD_ID, "qubit_block") ;
        QUANTUM_TENDRIL = new QuantumTendrilBlock((QubitBlock) QUBIT_BLOCK, Block.Properties.create(Material.IRON).hardnessAndResistance(5).harvestLevel(2).harvestTool(ToolType.PICKAXE)).setRegistryName(QubitsMod.MOD_ID, "quantum_tendril");
        event.getRegistry().registerAll(
                QUBIT_BLOCK,
                QUANTUM_TENDRIL
        );
    }
}
