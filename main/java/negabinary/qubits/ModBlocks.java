package negabinary.qubits;

import negabinary.qubits.block.tendrilPeripheral.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import virbinarus.qubits.block.tendrilPeripheral.*;
import negabinary.qubits.block.QubitBlock;

@Mod.EventBusSubscriber(modid = QubitsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(QubitsMod.MOD_ID)
public class ModBlocks {

    public static Block QUBIT_BLOCK;
    public static Block QUANTUM_TENDRIL;
    public static Block Q_NOT_GATE;
    public static Block Q_H_GATE;
    public static Block Q_MEASURE_GATE;
    public static Block Q_CONTROL;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        QUBIT_BLOCK = new QubitBlock(
                Block.Properties.create(Material.IRON)
                        .hardnessAndResistance(5)
                        .harvestLevel(2)
                        .harvestTool(ToolType.PICKAXE))
                .setRegistryName(QubitsMod.MOD_ID, "qubit_block");
        QUANTUM_TENDRIL = new QuantumTendrilBlock(
                (QubitBlock) QUBIT_BLOCK,
                Block.Properties.create(
                        Material.IRON)
                        .hardnessAndResistance(5)
                        .harvestLevel(2)
                        .harvestTool(ToolType.PICKAXE))
                .setRegistryName(QubitsMod.MOD_ID, "quantum_tendril");
        Q_NOT_GATE = new QNotGate(
                (QubitBlock) QUBIT_BLOCK,
                Block.Properties.create(Material.IRON)
                        .hardnessAndResistance(5)
                        .harvestLevel(2)
                        .harvestTool(ToolType.PICKAXE))
                .setRegistryName(QubitsMod.MOD_ID, "q_not_gate");
        Q_H_GATE = new QHGate(
                (QubitBlock) QUBIT_BLOCK,
                Block.Properties.create(Material.IRON)
                        .hardnessAndResistance(5)
                        .harvestLevel(2)
                        .harvestTool(ToolType.PICKAXE))
                .setRegistryName(QubitsMod.MOD_ID, "q_h_gate");
        Q_CONTROL = new QGateControl(
                (QubitBlock) QUBIT_BLOCK,
                Block.Properties.create(Material.IRON)
                        .hardnessAndResistance(5)
                        .harvestLevel(2)
                        .harvestTool(ToolType.PICKAXE))
                .setRegistryName(QubitsMod.MOD_ID, "q_control");
        Q_MEASURE_GATE = new QMeasureGate(
                (QubitBlock) QUBIT_BLOCK,
                Block.Properties.create(Material.IRON)
                        .hardnessAndResistance(5)
                        .harvestLevel(2)
                        .harvestTool(ToolType.PICKAXE))
                .setRegistryName(QubitsMod.MOD_ID, "q_measure_gate");
        event.getRegistry().registerAll(
                QUBIT_BLOCK,
                QUANTUM_TENDRIL,
                Q_NOT_GATE,
                Q_H_GATE,
                Q_CONTROL,
                Q_MEASURE_GATE
        );
    }
}
