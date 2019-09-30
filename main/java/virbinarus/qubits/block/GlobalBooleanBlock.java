package virbinarus.qubits.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import virbinarus.qubits.globalboolean.GlobalBoolean;
import virbinarus.qubits.globalboolean.GlobalBooleanProvider;

import javax.annotation.Nullable;
import java.util.Random;

public class GlobalBooleanBlock extends Block {
    public static final IntegerProperty LIT_LEVEL = IntegerProperty.create("lit_level",0,15);

    public GlobalBooleanBlock(Block.Properties properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(LIT_LEVEL, 0));
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(LIT_LEVEL, context.getWorld().isBlockPowered(context.getPos()) ? 0 : 15);
    }

    public void onRightClickBlock(BlockState state, World worldIn, BlockPos pos) {
        System.out.println("BOO");
        GlobalBoolean globalBoolean = worldIn.getCapability(GlobalBooleanProvider.GLOBAL_BOOLEAN).orElse(null);
        boolean flag = state.get(LIT_LEVEL) == 15;
        if (flag != globalBoolean.getBoolean()) {
            if (flag) {
                worldIn.getPendingBlockTicks().scheduleTick(pos, this, 4);
            } else {
                worldIn.setBlockState(pos, state.with(LIT_LEVEL, state.get(LIT_LEVEL) == 15 ? 0 : 15), 2);
            }
        }
    }

    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
    }

    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (!worldIn.isRemote) {
            System.out.println("HEYYY");
            boolean flag = state.get(LIT_LEVEL) == 15;
            GlobalBoolean globalBoolean = worldIn.<GlobalBoolean>getCapability(GlobalBooleanProvider.GLOBAL_BOOLEAN, null).orElse(null);
            if (globalBoolean.getBoolean() != worldIn.isBlockPowered(pos)) {
                globalBoolean.setBoolean(!globalBoolean.getBoolean());
            } /* else if (flag != globalBoolean.getBoolean()) {
                if (flag) {
                    worldIn.getPendingBlockTicks().scheduleTick(pos, this, 4);
                } else {
                    worldIn.setBlockState(pos, state.with(LIT_LEVEL, state.get(LIT_LEVEL) == 15 ? 0 : 15), 2);
                }
            } */

        }
    }

    public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
        if (!worldIn.isRemote) {
            if (state.get(LIT_LEVEL) == 15 && !worldIn.isBlockPowered(pos)) {
                worldIn.setBlockState(pos, state.with(LIT_LEVEL, state.get(LIT_LEVEL) == 15 ? 0 : 15), 2);
            }

        }
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(LIT_LEVEL);
    }
}