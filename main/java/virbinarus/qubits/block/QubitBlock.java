package virbinarus.qubits.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;

public class QubitBlock extends Block {
    public static final IntegerProperty LIT_LEVEL = IntegerProperty.create("lit_level",0,15);

    public QubitBlock(Block.Properties properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(LIT_LEVEL, 0));
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(LIT_LEVEL);
    }
}
