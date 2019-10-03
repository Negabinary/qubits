package virbinarus.qubits.block;

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
import virbinarus.qubits.block.tendrilPeripheral.TendrilConnectionEnum;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class QuantumTendrilBlock extends Block {
    public static final VoxelShape AABB = Block.makeCuboidShape(0.0D, 1.0D, 0.0D, 16.0D, 2.5D, 16.0D);
    public Random rand = new Random();
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");
    public static final IProperty<TendrilConnectionEnum> NORTH = EnumProperty.create("north", TendrilConnectionEnum.class);
    public static final IProperty<TendrilConnectionEnum> EAST = EnumProperty.create("east", TendrilConnectionEnum.class);
    public static final IProperty<TendrilConnectionEnum> SOUTH = EnumProperty.create("south", TendrilConnectionEnum.class);
    public static final IProperty<TendrilConnectionEnum> WEST = EnumProperty.create("west", TendrilConnectionEnum.class);
    public  QubitBlock qubitBlock;

    public QuantumTendrilBlock(QubitBlock qubitBlockArg, Block.Properties properties) {
        super(properties);
        qubitBlock = qubitBlockArg;
        this.setDefaultState(
                this.getDefaultState()
                        .with(CONNECTED, false)
                        .with(NORTH, TendrilConnectionEnum.NONE)
                        .with(EAST, TendrilConnectionEnum.NONE)
                        .with(SOUTH, TendrilConnectionEnum.NONE)
                        .with(WEST, TendrilConnectionEnum.NONE)
        );
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return AABB;
    }

    public IProperty<TendrilConnectionEnum> getPropertyFacing(Direction direction) {
        switch (direction) {
            case SOUTH : return SOUTH;
            case EAST : return EAST;
            case NORTH : return NORTH;
            case WEST : return WEST;
        }
        return null;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.down();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        return blockstate.func_224755_d(worldIn, blockpos, Direction.UP);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        IBlockReader world = context.getWorld();
        BlockPos blockPos = context.getPos();
        IProperty<TendrilConnectionEnum> newConnection = getNewConnection(world, blockPos);
        if (newConnection == null) {
            return this.getDefaultState();
        } else {
            return this.getDefaultState().with(newConnection, TendrilConnectionEnum.FROM).with(CONNECTED, true);
        }
    }

    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.get(CONNECTED)) {
            if (stateIn.get(getPropertyFacing(facing)) == TendrilConnectionEnum.FROM) {
                if (facingState.getBlock() != this
                        || (!facingState.get(CONNECTED))
                        || (facingState.get(getPropertyFacing(facing.getOpposite())) == TendrilConnectionEnum.FROM)) {
                    return disconnectAll(stateIn, worldIn, currentPos);
                }
            } else if (stateIn.get(getPropertyFacing(facing)) == TendrilConnectionEnum.TO) {
                if (facingState.getBlock() != this
                        || (!facingState.get(CONNECTED))
                        || (facingState.get(getPropertyFacing(facing.getOpposite())) != TendrilConnectionEnum.FROM)) {
                    return stateIn.with(getPropertyFacing(facing), TendrilConnectionEnum.NONE);
                }
            } else {
                if (facingState.getBlock() == this
                        && (facingState.get(CONNECTED))
                        && (facingState.get(getPropertyFacing(facing.getOpposite())) == TendrilConnectionEnum.FROM)) {
                    return stateIn.with(getPropertyFacing(facing), TendrilConnectionEnum.TO);
                }
            }
        } else {
            if (facingState.getBlock() == this
                    && facingState.get(CONNECTED)) {
                if (facingState.get(getPropertyFacing(facing.getOpposite())) == TendrilConnectionEnum.TO) {
                    return stateIn.with(getPropertyFacing(facing), TendrilConnectionEnum.FROM).with(CONNECTED, true);
                } else if (facingState.get(getPropertyFacing(facing.getOpposite())) == TendrilConnectionEnum.NONE) {
                    worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 4);
                }
            }
        }
        /*
        if (facingState.getBlock() == this) {
            if (stateIn.get(CONNECTED)) {
                if (facingState.get(CONNECTED)) {
                    if (facingState.get(getPropertyFacing(facing.getOpposite())) == TendrilConnectionEnum.FROM) {
                        return stateIn.with(getPropertyFacing(facing), TendrilConnectionEnum.TO);
                    }
                } else {
                    if (stateIn.get(getPropertyFacing(facing)) == TendrilConnectionEnum.FROM) {
                        return disconnectAll(stateIn, worldIn, currentPos);
                    }
                }
            } else {
                if (facingState.get(CONNECTED)) {
                    return stateIn.with(getPropertyFacing(facing), TendrilConnectionEnum.FROM).with(CONNECTED, true);
                } else {
                    return disconnectAll(stateIn, worldIn, currentPos);
                }
            }
        } else {
            if (stateIn.get(getPropertyFacing(facing)) == TendrilConnectionEnum.FROM) {
                return disconnectAll(stateIn, worldIn, currentPos);
            } else {
                return stateIn.with(getPropertyFacing(facing), TendrilConnectionEnum.NONE);
            }
        }
         */
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    public void enqueueConnection(World worldIn, BlockPos pos) {
        worldIn.getPendingBlockTicks().scheduleTick(pos, this, 4);
    }

    @Override
    public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
        IProperty<TendrilConnectionEnum> newConnection = getNewConnection(worldIn, pos);
        if ((newConnection != null) && (!state.get(CONNECTED))) {
            worldIn.setBlockState(pos, this.getDefaultState().with(newConnection, TendrilConnectionEnum.FROM).with(CONNECTED, true));
        }
        super.tick(state, worldIn, pos, random);
    }

    public BlockState disconnectAll(BlockState stateIn, IWorld worldIn, BlockPos currentPos) {
        worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 4);
        return stateIn.with(CONNECTED, false)
                .with(NORTH, TendrilConnectionEnum.NONE)
                .with(EAST, TendrilConnectionEnum.NONE)
                .with(SOUTH, TendrilConnectionEnum.NONE)
                .with(WEST, TendrilConnectionEnum.NONE);
    }

    public IProperty<TendrilConnectionEnum> getNewConnection(IBlockReader world, BlockPos blockPos) {
        boolean couldConnectNorth = couldConnectTo(world.getBlockState(blockPos.north()), Direction.NORTH);
        boolean couldConnectEast = couldConnectTo(world.getBlockState(blockPos.east()), Direction.EAST);
        boolean couldConnectSouth = couldConnectTo(world.getBlockState(blockPos.south()), Direction.SOUTH);
        boolean couldConnectWest = couldConnectTo(world.getBlockState(blockPos.west()), Direction.WEST);
        int possibleConnectionCount = (couldConnectNorth ? 1 : 0) + (couldConnectEast ? 1 : 0) + (couldConnectSouth ? 1 : 0) + (couldConnectWest ? 1 : 0);
        if (possibleConnectionCount == 0) {
            return null;
        } else if (possibleConnectionCount == 1) {
            if (couldConnectNorth) {
                return NORTH;
            } else if (couldConnectEast) {
                return EAST;
            } else if (couldConnectSouth) {
                return SOUTH;
            } else {
                return WEST;
            }
        } else {
            List<IProperty<TendrilConnectionEnum>> possibleConnections = new ArrayList<>(4);
            if (couldConnectNorth) {
                possibleConnections.add(NORTH);
            }
            if (couldConnectEast) {
                possibleConnections.add(EAST);
            }
            if (couldConnectSouth) {
                possibleConnections.add(SOUTH);
            }
            if (couldConnectWest) {
                possibleConnections.add(WEST);
            }
            return possibleConnections.get(rand.nextInt(possibleConnectionCount));
        }
    }

    public boolean couldConnectTo(BlockState blockState, Direction direction) {
        if (blockState.getBlock() == this) {
            return blockState.get(CONNECTED);
        } else if (blockState.getBlock() == qubitBlock){
            return true;
        } else {
            return false;
        }
    }

    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(CONNECTED, NORTH, EAST, WEST, SOUTH);
    }
}
