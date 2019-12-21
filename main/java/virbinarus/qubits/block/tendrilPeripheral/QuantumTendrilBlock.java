package virbinarus.qubits.block.tendrilPeripheral;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import virbinarus.qubits.block.QubitBlock;
import virbinarus.qubits.block.tendrilPeripheral.TendrilConnectionEnum;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class QuantumTendrilBlock extends TendrilPeripheralBlock {
    public static final VoxelShape AABB = Block.makeCuboidShape(0.0D, 1.0D, 0.0D, 16.0D, 2.5D, 16.0D);

    public QuantumTendrilBlock(QubitBlock qubitBlockArg, Block.Properties properties) {
        super(qubitBlockArg, properties);
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return AABB;
    }


    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.down();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        return blockstate.func_224755_d(worldIn, blockpos, Direction.UP);
    }

    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(CONNECTED, NORTH, EAST, WEST, SOUTH);
    }
}
