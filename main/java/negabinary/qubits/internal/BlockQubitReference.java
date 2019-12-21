package negabinary.qubits.internal;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import negabinary.qubits.block.QubitBlock;
import negabinary.qubits.block.QubitBlockTileEntity;

public class BlockQubitReference implements IQubitReference {
    QubitBlockTileEntity tileEntity;
    BlockPos pos;

    public BlockQubitReference(QubitBlockTileEntity newTileEntity) {
        tileEntity = newTileEntity;
    }

    public BlockQubitReference(CompoundNBT compound) {
        pos = new BlockPos(compound.getInt("x"), compound.getInt("y"), compound.getInt("z"));
    }

    public QubitBlockTileEntity getTileEntity(World world) {
        if (tileEntity == null) {
            tileEntity = (QubitBlockTileEntity) world.getTileEntity(pos);
        }
        return tileEntity;
    }

    public BlockPos getPos() {
        if (pos == null) {
            pos = tileEntity.getPos();
        }
        return pos;
    }

    public void updateLocalState(double newState, World world) {
        int litLevel = (int) Math.round(newState * 10);

        BlockState blockState = getTileEntity(world).getBlockState();

        world.setBlockState(
                getTileEntity(world).getPos(), blockState.with(QubitBlock.LIT_LEVEL, litLevel)
        );
    }

    @Override
    public void markDirty(World world) {
        getTileEntity(world).markDirty();
    }

    @Override
    public CompoundNBT write() {
        CompoundNBT compound = new CompoundNBT();
        compound.putString("ref_type", "block");
        getPos();
        compound.putInt("x", pos.getX());
        compound.putInt("y", pos.getY());
        compound.putInt("z", pos.getZ());
        return compound;
    }

    @Override
    public Qubit getQubit(World world) {
        return getTileEntity(world).qubit;
    }
}
