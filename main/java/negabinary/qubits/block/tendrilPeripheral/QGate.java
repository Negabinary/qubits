package negabinary.qubits.block.tendrilPeripheral;

import negabinary.qubits.internal.Qubit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import negabinary.qubits.block.QubitBlock;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class QGate extends TendrilPeripheralBlock {
    public static final BooleanProperty TRIGGERED = BooleanProperty.create("triggered");

    public boolean canProvidePower(BlockState state) {
        return true;
    }

    public QGate(QubitBlock qubitBlockArg, Properties properties) {
        super(qubitBlockArg, properties);
        this.setDefaultState(
                this.getDefaultState()
                        .with(TRIGGERED, false)
        );
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        boolean hasPower = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.down());
        boolean isTriggered = state.get(TRIGGERED);
        if (hasPower && !isTriggered) {
            if (state.get(CONNECTED)) {
                state = applyGate(state, pos, worldIn);
            }
            worldIn.setBlockState(pos, state.with(TRIGGERED, true), 4);
        } else if (!hasPower && isTriggered) {
            worldIn.setBlockState(pos, state.with(TRIGGERED, false),4);
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
    }

    public Qubit[] getControlQubits(BlockPos pos, World world) {
        List<Qubit> controlQubits = new LinkedList<>();
        for (Direction direction: DIRECTIONS) {
            BlockPos controlPos = pos.offset(direction);
            BlockState controlState = world.getBlockState(controlPos);
            if (isControl(controlState.getBlock())
                    && controlState.get(CONNECTED)) {
                controlQubits.add(getQubit(controlState, controlPos, world));
            }
        }
        return controlQubits.toArray(new Qubit[]{});
    }

    public abstract BlockState applyGate(BlockState blockState, BlockPos pos, World worldIn);

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(TRIGGERED);
    }

    public boolean couldConnectTo(BlockState connectingState, Direction direction, BlockPos gatePos, World world) {
        return super.couldConnectTo(connectingState, direction, gatePos, world)
                && !Arrays.asList(getControlQubits(gatePos, world)).contains(getQubit(connectingState, gatePos.offset(direction), world)) ;
    }
}