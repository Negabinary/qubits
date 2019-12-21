package virbinarus.qubits.block.tendrilPeripheral;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import virbinarus.qubits.block.QubitBlock;

public class QHGate extends QGate {
    public static final VoxelShape AABB = Block.makeCuboidShape(0.0D, 1.0D, 0.0D, 16.0D, 2.5D, 16.0D);

    public QHGate(QubitBlock qubitBlockArg, Block.Properties properties) {
        super(qubitBlockArg, properties);
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return AABB;
    }

    public void applyGate(BlockState blockState, BlockPos pos, World worldIn) {
        applyH(blockState, pos, getControlQubits(pos, worldIn), worldIn);
    }

    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }
}
