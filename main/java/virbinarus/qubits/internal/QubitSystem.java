package virbinarus.qubits.internal;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import virbinarus.qubits.block.QubitBlockTileEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QubitSystem {
    private double[] worldRealParts;
    private double[] worldImaginaryParts;
    private List<IQubitReference> qubitReferences;

    public QubitSystem(List<IQubitReference> newQubitReferences, double[] newRealParts, double[] newImaginaryParts) {
        assert (newRealParts.length == 1 << newQubitReferences.size());
        assert (newImaginaryParts.length == newRealParts.length);
        worldRealParts = newRealParts;
        worldImaginaryParts = newImaginaryParts;
        qubitReferences = newQubitReferences;
    }

    public QubitSystem(IQubitReference newQubitReference) {
        this(
                new ArrayList<>(Collections.singletonList(newQubitReference)),
                new double[]{1d, 0d},
                new double[]{0d, 0d}
        );
    }

    public QubitSystem(World world, CompoundNBT compound) {
        read(world, compound);
    }

    private void updateAll(World world) {
        for (IQubitReference qubitReference : qubitReferences) {
            qubitReference.updateLocalState(worldRealParts[1], world);
        }
    }

    public void applyNot(int qubitID, World world) {
        double temp = worldRealParts[0];
        worldRealParts[0] = worldRealParts[1];
        worldRealParts[1] = temp;
        updateAll(world);
    }

    public void applyH(int qubitID, World world) {
        double temp1 = worldRealParts[0];
        double temp2 = worldRealParts[1];
        worldRealParts[0] = Math.sqrt(0.5d) * (temp1 + temp2);
        worldRealParts[1] = Math.sqrt(0.5d) * (temp1 - temp2);
        updateAll(world);
    }

    public void merge(QubitSystem other) {
        System.out.println("ONE");
        int newArrayLength = worldRealParts.length * other.worldRealParts.length;
        double[] newRealParts = new double[newArrayLength];
        double[] newImaginaryParts = new double[newArrayLength];
        System.out.println("TWO");
        for (int i = 0; i < worldRealParts.length; i++) {
            for (int j = 0; j < other.worldRealParts.length; j++) {
                int k = i * other.worldRealParts.length + j;
                newRealParts[k] = worldRealParts[i] * other.worldRealParts[j] - worldImaginaryParts[i] * other.worldImaginaryParts[j];
                newImaginaryParts[k] = worldRealParts[i] * other.worldImaginaryParts[j] + worldImaginaryParts[i] * other.worldImaginaryParts[i];
            }
        }
        worldRealParts = newRealParts;
        worldImaginaryParts = newImaginaryParts;
        System.out.println("THREE");
        Qubit holderQubitRef = qubitReferences.get(0).getQubit();
        System.out.println("THREE AND A HALF");
        IQubitReference holderQubitReference = qubitReferences.get(0).getQubit().systemHolderReference;
        System.out.println("FOUR");
        for (IQubitReference qubitReference : other.qubitReferences) {
            qubitReference.getQubit().isSystemHolder = false;
            qubitReference.getQubit().systemHolderReference = holderQubitReference;
            qubitReferences.add(qubitReference);
        }
        System.out.println("FIVE");
    }

    public CompoundNBT write() {
        CompoundNBT compound = new CompoundNBT();
        ListNBT realPartsList = new ListNBT();
        for (double i:worldRealParts) {realPartsList.add(new DoubleNBT(i));}
        compound.put("real_parts",realPartsList);
        ListNBT imaginaryPartsList = new ListNBT();
        for (double i:worldImaginaryParts) {imaginaryPartsList.add(new DoubleNBT(i));}
        compound.put("imaginary_parts", imaginaryPartsList);
        ListNBT qubitReferencesList = new ListNBT();
        for (IQubitReference i:qubitReferences) {qubitReferencesList.add(i.write());}
        compound.put("qubit_references", qubitReferencesList);
        return compound;
    }


    public void read(World world, CompoundNBT compound) {
        qubitReferences = new ArrayList<>();
        ListNBT qubitReferencesList = compound.getList("qubit_references", Constants.NBT.TAG_COMPOUND);
        for (INBT inbt : qubitReferencesList) {
            CompoundNBT ref_compound = (CompoundNBT) inbt;
            qubitReferences.add(QubitReferenceBuilder.readRef(world, ref_compound));
        }
        int qubitCount = qubitReferences.size();
        int worldCount = 1 << qubitCount;
        worldRealParts = new double[worldCount];
        worldImaginaryParts = new double[worldCount];
        ListNBT worldRealPartsList = compound.getList("real_parts", Constants.NBT.TAG_DOUBLE);
        ListNBT worldImaginaryPartsList = compound.getList("imaginary_parts", Constants.NBT.TAG_DOUBLE);
        for (int i = 0; i < worldCount; i++) {
            worldRealParts[i] = worldRealPartsList.getDouble(i);
            worldImaginaryParts[i] = worldImaginaryPartsList.getDouble(i);
        }

    }
}
