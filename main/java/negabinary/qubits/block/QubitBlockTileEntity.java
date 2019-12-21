package negabinary.qubits.block;

import negabinary.qubits.ModTileEntities;
import negabinary.qubits.internal.BlockQubitReference;
import negabinary.qubits.internal.Qubit;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;


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
        System.out.println("RE");
        if (compound.contains("qubit")) {
            qubit.read(world, compound.getCompound("qubit"));
        }
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        System.out.println("WR");
        compound.put("qubit",qubit.write());
        return super.write(compound);
    }

    public Qubit getQubit() {
        return qubit;
    }
}
