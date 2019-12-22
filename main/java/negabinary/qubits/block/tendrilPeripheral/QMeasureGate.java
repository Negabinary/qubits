package negabinary.qubits.block.tendrilPeripheral;

import negabinary.qubits.block.QubitBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class QMeasureGate extends QGate {

    public static final BooleanProperty MEASUREMENT = BooleanProperty.create("measurement");

    public static final VoxelShape AABB = Block.makeCuboidShape(0.0D, 1.0D, 0.0D, 16.0D, 2.5D, 16.0D);

    public QMeasureGate(QubitBlock qubitBlockArg, Properties properties) {
        super(qubitBlockArg, properties);
        this.setDefaultState(
                this.getDefaultState()
                        .with(MEASUREMENT, false)
        );
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(MEASUREMENT);
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return AABB;
    }

    public BlockState applyGate(BlockState blockState, BlockPos pos, World worldIn) {
        boolean result = applyMeasure(blockState, pos, worldIn);
        worldIn.setBlockState(pos, blockState.with(MEASUREMENT, result), 6);
        worldIn.updateComparatorOutputLevel(pos, this);
        return blockState.with(MEASUREMENT, result);

    }

    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        return blockState.get(MEASUREMENT) ? 15 : 0;
    }
}
