package negabinary.qubits.block.tendrilPeripheral;

import com.google.common.collect.Maps;
import negabinary.qubits.internal.Qubit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import negabinary.qubits.QubitsMod;
import negabinary.qubits.block.QubitBlock;
import negabinary.qubits.block.QubitBlockTileEntity;

import java.util.*;

public class TendrilPeripheralBlock extends Block {

    public static final Direction[] DIRECTIONS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    public static final Tag<Block> TENDRIL_PERIPHERAL = BlockTags.getCollection().getOrCreate(new ResourceLocation(QubitsMod.MOD_ID, "blocks/tendril"));

    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    public static final IProperty<TendrilConnectionEnum> NORTH
            = EnumProperty.create("north", TendrilConnectionEnum.class);
    public static final IProperty<TendrilConnectionEnum> EAST
            = EnumProperty.create("east", TendrilConnectionEnum.class);
    public static final IProperty<TendrilConnectionEnum> SOUTH
            = EnumProperty.create("south", TendrilConnectionEnum.class);
    public static final IProperty<TendrilConnectionEnum> WEST
            = EnumProperty.create("west", TendrilConnectionEnum.class);

    public static final Map<Direction, IProperty<TendrilConnectionEnum>> FACING_PROPERTY_MAP
            = Util.make(Maps.newEnumMap(Direction.class), (newMap) -> {
                newMap.put(Direction.NORTH, NORTH);
                newMap.put(Direction.EAST, EAST);
                newMap.put(Direction.SOUTH, SOUTH);
                newMap.put(Direction.WEST, WEST);
    });

    public QubitBlock qubitBlock;
    public Random rand = new Random();

    public TendrilPeripheralBlock(QubitBlock qubitBlockArg, Block.Properties properties) {
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

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return updateTendrilConnections(stateIn, facing, false, facingState, worldIn, currentPos, facingPos);
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getPos();
        world.getPendingBlockTicks().scheduleTick(blockPos, this, 4);
        return this.getDefaultState();
    }

    protected IProperty<TendrilConnectionEnum> getNewConnection(World world, BlockPos blockPos) {
        boolean couldConnectNorth = couldConnectTo(world.getBlockState(blockPos.north()), Direction.NORTH, blockPos, world);
        boolean couldConnectEast = couldConnectTo(world.getBlockState(blockPos.east()), Direction.EAST, blockPos, world);
        boolean couldConnectSouth = couldConnectTo(world.getBlockState(blockPos.south()), Direction.SOUTH, blockPos, world);
        boolean couldConnectWest = couldConnectTo(world.getBlockState(blockPos.west()), Direction.WEST, blockPos, world);
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
            rand.setSeed(blockPos.toLong());
            return possibleConnections.get(rand.nextInt(possibleConnectionCount));
        }
    }

    public boolean couldConnectTo(BlockState blockState, Direction direction, BlockPos ownPos, World world) {
        if (hasTendril(blockState.getBlock())) {
            return blockState.get(CONNECTED);
        } else {
            return blockState.getBlock() == qubitBlock;
        }
    }

    protected BlockState updateTendrilConnections(BlockState stateIn, Direction facing, Boolean isUp, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
        if (facing == Direction.UP || facing == facing.DOWN) {
            return stateIn;
        }
        if (stateIn.get(CONNECTED)) {
            TendrilConnectionEnum currentConnection = stateIn.get(FACING_PROPERTY_MAP.get(facing));
            if (currentConnection == TendrilConnectionEnum.FROM) {
                return disconnectAllIfTendrilMissing(stateIn, facing, facingState, world, currentPos);
            } else if (currentConnection == TendrilConnectionEnum.TO) {
                return disconnectIfTendrilMissing(stateIn, facing, facingState, world, currentPos);
            } else {
                return connectToIfFacingFrom(stateIn, facing, facingState, world, currentPos);
            }
        } else {
            if (hasTendril(facingState.getBlock())
                    && facingState.get(CONNECTED)) {
                if (facingState.get(FACING_PROPERTY_MAP.get(facing.getOpposite())) == TendrilConnectionEnum.TO) {
                    return stateIn.with(FACING_PROPERTY_MAP.get(facing), TendrilConnectionEnum.FROM).with(CONNECTED, true);
                } else if (facingState.get(FACING_PROPERTY_MAP.get(facing.getOpposite())) == TendrilConnectionEnum.NONE) {
                    world.getPendingBlockTicks().scheduleTick(currentPos, this, 4);
                }
            } else if ((facingState.getBlock() == qubitBlock)) {
                world.getPendingBlockTicks().scheduleTick(currentPos, this, 4);
            }
        }
        return stateIn;
    }

    public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
        IProperty<TendrilConnectionEnum> newConnection = getNewConnection(worldIn, pos);
        if ((newConnection != null) && (!state.get(CONNECTED))) {
            worldIn.setBlockState(pos, state.with(newConnection, TendrilConnectionEnum.FROM).with(CONNECTED, true));
        }
        super.tick(state, worldIn, pos, random);
    }


    private BlockState disconnectAllIfTendrilMissing(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos) {
        if ((!hasTendril(facingState.getBlock())
                || (!facingState.get(CONNECTED))
                || (facingState.get(FACING_PROPERTY_MAP.get(facing.getOpposite())) == TendrilConnectionEnum.FROM))
                && (facingState.getBlock() != qubitBlock)) {
            return disconnectAll(stateIn, worldIn, currentPos);
        } else {
            return stateIn;
        }
    }

    protected BlockState disconnectAll(BlockState stateIn, IWorld worldIn, BlockPos currentPos) {
        worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 4);
        return stateIn.with(CONNECTED, false)
                .with(NORTH, TendrilConnectionEnum.NONE)
                .with(EAST, TendrilConnectionEnum.NONE)
                .with(SOUTH, TendrilConnectionEnum.NONE)
                .with(WEST, TendrilConnectionEnum.NONE);
    }

    private BlockState disconnectIfTendrilMissing(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos) {
        if (!(hasTendril(facingState.getBlock()))
                || (!facingState.get(CONNECTED))
                || (facingState.get(FACING_PROPERTY_MAP.get(facing.getOpposite())) != TendrilConnectionEnum.FROM)) {
            return stateIn.with(FACING_PROPERTY_MAP.get(facing), TendrilConnectionEnum.NONE);
        } else {
            return stateIn;
        }
    }

    private BlockState connectToIfFacingFrom(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos) {
        if (hasTendril(facingState.getBlock())
                && (facingState.get(CONNECTED))
                && (facingState.get(FACING_PROPERTY_MAP.get(facing.getOpposite())) == TendrilConnectionEnum.FROM)) {
            return stateIn.with(FACING_PROPERTY_MAP.get(facing), TendrilConnectionEnum.TO);
        } else {
            return stateIn;
        }
    }

    protected boolean hasTendril(Block block) {
        return block.getTags().contains(new ResourceLocation(QubitsMod.MOD_ID, "tendril/tendril"));
    }

    public void applyNot(BlockState currentState, BlockPos currentPos, Qubit[] controlQubits, World world) {
        getQubitBlockTileEntity(currentState, currentPos, world).qubit.applyNot(controlQubits, world);
    }

    public void applyH(BlockState currentState, BlockPos currentPos, Qubit[] controlQubits, World world) {
        getQubitBlockTileEntity(currentState, currentPos, world).qubit.applyH(controlQubits, world);
    }

    public boolean applyMeasure(BlockState currentState, BlockPos currentPos, World world) {
        return getQubitBlockTileEntity(currentState, currentPos, world).qubit.applyMeasure(world);
    }

    public QubitBlockTileEntity getQubitBlockTileEntity(BlockState currentState, BlockPos currentPos, World world) {
        while (currentState.getBlock() != qubitBlock) {
            if (currentState.get(NORTH) == TendrilConnectionEnum.FROM) { currentPos = currentPos.north(); }
            if (currentState.get(EAST) == TendrilConnectionEnum.FROM) { currentPos = currentPos.east(); }
            if (currentState.get(SOUTH) == TendrilConnectionEnum.FROM) { currentPos = currentPos.south(); }
            if (currentState.get(WEST) == TendrilConnectionEnum.FROM) { currentPos = currentPos.west(); }
            currentState = world.getBlockState(currentPos);
        }
        return (QubitBlockTileEntity) world.getTileEntity(currentPos);
    }

    public Qubit getQubit(BlockState currentState, BlockPos currentPos, World world) {
        return getQubitBlockTileEntity(currentState, currentPos, world).getQubit();
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(CONNECTED, NORTH, EAST, WEST, SOUTH);
    }

    protected boolean isGate(Block block) {
        return block.getTags().contains(new ResourceLocation(QubitsMod.MOD_ID, "tendril/qgate"));
    }

    protected boolean isControl(Block block) {
        return block.getTags().contains(new ResourceLocation(QubitsMod.MOD_ID, "tendril/qcontrol"));
    }
}
