package virbinarus.qubits.internal;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import virbinarus.qubits.block.QubitBlockTileEntity;

import java.util.*;

public class QubitSystem {
    private Random rand = new Random();
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
        System.out.println(this);
        System.out.println(Arrays.toString(worldRealParts));
        for (IQubitReference qubitReference : qubitReferences) {
            qubitReference.updateLocalState(getLocalState(qubitReference.getQubit(world).getQubitID()), world);
        }
    }

    public double getLocalState(int qubitID) {
        int posBit = 1 << qubitID;
        double localState = 0;
        for (int worldID = 0; worldID < worldRealParts.length; worldID++) {
            if ((worldID | posBit) == worldID) {
                localState += worldRealParts[worldID] * worldRealParts[worldID];
                localState += worldImaginaryParts[worldID] * worldImaginaryParts[worldID];
            }
        }
        return localState;
    }

    public void applyNot(int qubitID, int[] controlIDs, World world) {
        int posBit = 1 << qubitID;
        int[] controlPosBits = Arrays.stream(controlIDs).map(x -> 1 << x).toArray();
        for (int worldID = 0; worldID < worldRealParts.length; worldID++) {
            if ((worldID & posBit) == 0
                    && controlsMatch(worldID, controlPosBits)) {
                double tempR = worldRealParts[worldID];
                double tempI = worldImaginaryParts[worldID];
                worldRealParts[worldID] = worldRealParts[worldID | posBit];
                worldImaginaryParts[worldID] = worldImaginaryParts[worldID | posBit];
                worldRealParts[worldID | posBit] = tempR;
                worldImaginaryParts[worldID | posBit] = tempI;
            }
        }
        updateAll(world);
    }

    public void applyH(int qubitID, int[] controlIDs, World world) {
        int posBit = 1 << qubitID;
        int[] controlPosBits = Arrays.stream(controlIDs).map(x -> 1 << x).toArray();
        for (int worldID = 0; worldID < worldRealParts.length; worldID++) {
            if ((worldID & posBit) == 0
                    && controlsMatch(worldID, controlPosBits)) {
                double tempR1 = worldRealParts[worldID];
                double tempI1 = worldImaginaryParts[worldID];
                double tempR2 = worldRealParts[worldID | posBit];
                double tempI2 = worldImaginaryParts[worldID | posBit];
                worldRealParts[worldID] = Math.sqrt(0.5) * (tempR1 + tempR2);
                worldImaginaryParts[worldID] = Math.sqrt(0.5) * (tempI1 + tempI2);
                worldRealParts[worldID | posBit] = Math.sqrt(0.5) * (tempR1 - tempR2);;
                worldImaginaryParts[worldID | posBit] = Math.sqrt(0.5) * (tempI1 - tempI2);;
            }
        }
        updateAll(world);
    }

    public void applyMeasure(int qubitID, World world) {
        double probability = getLocalState(qubitID);
        double random = rand.nextDouble();
        boolean result = random > probability;
        double probabilityOfResult;
        if (!result) {
            probabilityOfResult = 1 - probability;
        } else {
            probabilityOfResult = probability;
        }

        int posBit = 1 << qubitID;
        for (int worldID = 0; worldID < worldRealParts.length; worldID++) {
            if (((worldID & posBit) == 0) != result) {
                worldRealParts[worldID] = worldRealParts[worldID] / Math.sqrt(probabilityOfResult);
                worldImaginaryParts[worldID] = worldImaginaryParts[worldID] / Math.sqrt(probabilityOfResult);
            } else {
                worldRealParts[worldID] = 0;
                worldImaginaryParts[worldID] = 0;
            }
        }
        updateAll(world);
    }

    private boolean controlsMatch(int worldID, int[] controlPosBits) {
        for (int controlPB: controlPosBits) {
            if ((controlPB & worldID) == 0) {
                return false;
            }
        }
        return true;
    }

    public void merge(QubitSystem other, World world) {
        int newArrayLength = worldRealParts.length * other.worldRealParts.length;
        double[] newRealParts = new double[newArrayLength];
        double[] newImaginaryParts = new double[newArrayLength];
        for (int i = 0; i < worldRealParts.length; i++) {
            for (int j = 0; j < other.worldRealParts.length; j++) {
                int k = i + j * worldRealParts.length;
                newRealParts[k] = worldRealParts[i] * other.worldRealParts[j] - worldImaginaryParts[i] * other.worldImaginaryParts[j];
                newImaginaryParts[k] = worldRealParts[i] * other.worldImaginaryParts[j] + worldImaginaryParts[i] * other.worldImaginaryParts[j];
            }
        }
        worldRealParts = newRealParts;
        worldImaginaryParts = newImaginaryParts;
        Qubit holderQubitRef = qubitReferences.get(0).getQubit(world);
        IQubitReference holderQubitReference = qubitReferences.get(0).getQubit(world).systemHolderReference;
        int originalQubitCount = qubitReferences.size();
        for (IQubitReference qubitReference : other.qubitReferences) {
            qubitReference.getQubit(world).isSystemHolder = false;
            qubitReference.getQubit(world).systemHolderReference = holderQubitReference;
            qubitReference.getQubit(world).qubitSystem = this;
            qubitReference.getQubit(world).qubitID += originalQubitCount;
            qubitReferences.add(qubitReference);
        }
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
