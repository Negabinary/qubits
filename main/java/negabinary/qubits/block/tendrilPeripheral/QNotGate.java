package negabinary.qubits.block.tendrilPeripheral;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import negabinary.qubits.block.QubitBlock;

public class QNotGate extends QFlatGate {
    public QNotGate(QubitBlock qubitBlockArg, Block.Properties properties) {
        super(qubitBlockArg, properties);
    }

    public BlockState applyGate(BlockState blockState, BlockPos pos, World worldIn) {
        applyNot(blockState, pos, getControlQubits(pos, worldIn), worldIn);
        return blockState;
    }
}
