package negabinary.qubits.block.tendrilPeripheral;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
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
import negabinary.qubits.block.QubitBlock;
import negabinary.qubits.block.QubitBlockTileEntity;

public class QGateControl extends TendrilPeripheralBlock {
    public static final DirectionProperty CONTROLLING = DirectionProperty.create("controlling", Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);

    public static final VoxelShape AABB = makeCuboidShape(0.0D, 1.0D, 0.0D, 16.0D, 2.5D, 16.0D);

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return AABB;
    }

    public QGateControl(QubitBlock qubitBlockArg, Properties properties) {
        super(qubitBlockArg, properties);
        this.setDefaultState(
                this.getDefaultState()
                        .with(CONTROLLING, Direction.EAST)
        );
    }

    @Override
    protected BlockState updateTendrilConnections(BlockState stateIn, Direction facing, Boolean isUp, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
        BlockPos gatePos = currentPos.offset(stateIn.get(CONTROLLING));
        BlockState gateState = world.getBlockState(gatePos);
        if (!isGate(gateState.getBlock())) {
            return Blocks.AIR.getDefaultState();
        } else {
            return super.updateTendrilConnections(stateIn, facing, isUp, facingState, world, currentPos, facingPos);
        }
    }

    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.down();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        return blockstate.func_224755_d(worldIn, blockpos, Direction.UP)
                && (
                        isGate(worldIn.getBlockState(pos.north()).getBlock())
                        || isGate(worldIn.getBlockState(pos.east()).getBlock())
                        || isGate(worldIn.getBlockState(pos.south()).getBlock())
                        || isGate(worldIn.getBlockState(pos.west()).getBlock())
        );

    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction controlDirection;
        IBlockReader world = context.getWorld();
        BlockPos blockPos = context.getPos();

        if (isGate(world.getBlockState(blockPos.north()).getBlock())) {
            controlDirection = Direction.NORTH;
        } else if (isGate(world.getBlockState(blockPos.east()).getBlock())) {
            controlDirection = Direction.EAST;
        } else if (isGate(world.getBlockState(blockPos.south()).getBlock())) {
            controlDirection = Direction.SOUTH;
        } else {
            controlDirection = Direction.WEST;
        }
        return super.getStateForPlacement(context).with(CONTROLLING, controlDirection) ;
    }


    public boolean couldConnectTo(BlockState blockState, Direction direction, BlockPos ownPos, World world) {
        BlockState ownState = world.getBlockState(ownPos);
        BlockPos gatePos = ownPos.offset(ownState.get(CONTROLLING));
        BlockState gateState = world.getBlockState(gatePos);
        if (hasTendril(blockState.getBlock())) {
            if (!blockState.get(CONNECTED)) {
                return false;
            } else if (!gateState.get(CONNECTED)) {
                return direction != ownState.get(CONTROLLING);
            } else {
                QubitBlockTileEntity gateQubit = getGateQubit(gateState, gatePos, world);
                QubitBlockTileEntity otherQubit = getQubitBlockTileEntity(blockState, ownPos.offset(direction), world);
                return gateQubit != otherQubit;
            }
        } else if (blockState.getBlock() == qubitBlock) {
            return world.getTileEntity(ownPos.offset(direction)) != getGateQubit(gateState, gatePos, world);
        } else {
            return false;
        }
    }

    private QubitBlockTileEntity getGateQubit(BlockState gateState, BlockPos gatePos, World world) {
        return getQubitBlockTileEntity(gateState, gatePos, world);
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(CONTROLLING);
    }

    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }
}
