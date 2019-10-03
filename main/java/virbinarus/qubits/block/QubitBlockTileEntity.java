package virbinarus.qubits.block;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import virbinarus.qubits.ModTileEntities;
import virbinarus.qubits.internal.BlockQubitReference;
import virbinarus.qubits.internal.Qubit;


public class QubitBlockTileEntity extends TileEntity {

    public Qubit qubit;

    public QubitBlockTileEntity(final TileEntityType<?> type) {
        super(type);


        qubit = new Qubit(
                new BlockQubitReference(this)
        );
    }

    public QubitBlockTileEntity() {
        this(ModTileEntities.QUBIT_BLOCK_TILE_ENTITY);
    }

    @Override
    public void read(CompoundNBT compound) {

        qubit.read(world, compound.getCompound("qubit"));
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {

        compound.put("qubit",qubit.write());
        return super.write(compound);
    }
}
