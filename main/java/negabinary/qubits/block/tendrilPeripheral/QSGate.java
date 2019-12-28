package negabinary.qubits.block.tendrilPeripheral;

import negabinary.qubits.block.QubitBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class QSGate extends QFlatGate {
    public QSGate(QubitBlock qubitBlockArg, Properties properties) {
        super(qubitBlockArg, properties);
    }

    public BlockState applyGate(BlockState blockState, BlockPos pos, World worldIn) {
        applyGate(1, 0, 0, 0, 0, 0, 0, 1,
                blockState, pos, getControlQubits(pos, worldIn), worldIn);
        return blockState;
    }
}
