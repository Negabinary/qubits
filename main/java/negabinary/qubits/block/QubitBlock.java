package negabinary.qubits.block;

import negabinary.qubits.internal.Qubit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class QubitBlock extends Block {
    public static final IntegerProperty LIT_LEVEL = IntegerProperty.create("lit_level",0,10);

    public QubitBlock(Block.Properties properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(LIT_LEVEL, 0));
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(LIT_LEVEL, 0);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new QubitBlockTileEntity();
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(LIT_LEVEL);
    }

    public QubitBlockTileEntity getTileEntity(BlockPos pos, IWorld world) {
        return (QubitBlockTileEntity) world.getTileEntity(pos);
    }

    /*
    public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
        super.onPlayerDestroy(worldIn, pos, state);
        QubitBlockTileEntity tileEntity = getTileEntity(pos, worldIn);

        System.out.println("here");
    }
     */
}
