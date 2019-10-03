package virbinarus.qubits.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

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
}
