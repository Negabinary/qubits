package negabinary.qubits.internal;

import negabinary.qubits.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
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

    public QubitBlockTileEntity getTileEntity(IWorld world) {
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

    public void updateLocalState(double newState, IWorld world) {
        int litLevel = (int) Math.round(newState * 10);

        BlockState blockState = getTileEntity(world).getBlockState();

        if (blockState.getBlock() == ModBlocks.QUBIT_BLOCK) {
            world.setBlockState(
                    getTileEntity(world).getPos(), blockState.with(QubitBlock.LIT_LEVEL, litLevel), 6
            );
        }
    }

    @Override
    public void markDirty(IWorld world) {
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
    public Qubit getQubit(IWorld world) {
        return getTileEntity(world).qubit;
    }
}
