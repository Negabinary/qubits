package virbinarus.qubits.internal;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import virbinarus.qubits.block.QubitBlock;
import virbinarus.qubits.block.QubitBlockTileEntity;

public class BlockQubitReference implements IQubitReference {
    QubitBlockTileEntity tileEntity;
    BlockPos pos;
    World world;

    public BlockQubitReference(QubitBlockTileEntity newTileEntity) {
        tileEntity = newTileEntity;
    }

    public BlockQubitReference(World newWorld, CompoundNBT compound) {
        pos = new BlockPos(compound.getInt("x"), compound.getInt("y"), compound.getInt("z"));
        world = newWorld;
    }

    public QubitBlockTileEntity getTileEntity() {
        System.out.println(tileEntity);
        System.out.println(world);
        System.out.println(pos);
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
        int litLevel = (int) Math.round(newState * newState * 10);
        System.out.println(litLevel);

        BlockState blockState = getTileEntity().getBlockState();

        getTileEntity().getWorld().setBlockState(
                getTileEntity().getPos(), blockState.with(QubitBlock.LIT_LEVEL, litLevel)
        );
    }

    @Override
    public void markDirty(World world) {
        getTileEntity().markDirty();
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
    public Qubit getQubit() {
        System.out.println("ALPHA");
        System.out.println(getTileEntity());
        System.out.println("BETA");
        return getTileEntity().qubit;
    }
}
