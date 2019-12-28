package negabinary.qubits.internal;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.Arrays;
import java.util.Random;

public class StateVector {

    private Random rand = new Random();
    private final double[] worldRealParts;
    private final double[] worldImaginaryParts;

    public StateVector(int n) {
        worldRealParts = new double[n];
        worldImaginaryParts = new double[n];
    }

    public StateVector(double[] worldRealParts, double[] worldImaginaryParts) {
        this.worldRealParts = worldRealParts;
        this.worldImaginaryParts = worldImaginaryParts;
    }

    public StateVector(boolean value) {
        worldRealParts = new double[2];
        worldImaginaryParts = new double[2];
        setValue(0, value ? 0 : 1, 0);
        setValue(1, value ? 1 : 0, 0);
    }

    public int getLength() {
        return worldRealParts.length;
    }

    public void setValue(int index, int re, int im) {
        worldRealParts[index] = re;
        worldImaginaryParts[index] = im;
    }

    public double getProb(int qubitID) {
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

    public boolean sample(int qubitID) {
        double probability = getProb(qubitID);
        double random = rand.nextDouble();
        return random < probability;
    }

    public void applyGate(double ar, double ai, double br, double bi, double cr, double ci, double dr, double di,
                          int qubitID, int[] controlIDs, boolean[] controlValues, World world) {
        int posBit = 1 << qubitID;
        int[] controlPosBits = Arrays.stream(controlIDs).map(x -> 1 << x).toArray();
        for (int worldID = 0; worldID < worldRealParts.length; worldID++) {
            if ((worldID & posBit) == 0
                    && controlsMatch(worldID, controlPosBits, controlValues)) {
                double pr = worldRealParts[worldID];
                double pi = worldImaginaryParts[worldID];
                double qr = worldRealParts[worldID | posBit];
                double qi = worldImaginaryParts[worldID | posBit];
                worldRealParts[worldID] = ar * pr - ai * pi + br * qr - bi *  qi;
                worldImaginaryParts[worldID] = ai*pr + ar*pi + qr*bi + qi*br;
                worldRealParts[worldID | posBit] = cr * pr - ci * pi + dr * qr - di *  qi;
                worldImaginaryParts[worldID | posBit] = ci*pr + cr*pi + qr*di + qi*dr;
            }
        }
    }

    private boolean controlsMatch(int worldID, int[] controlPosBits, boolean[] controlValues) {
        for (int i = 0; i < controlPosBits.length; i++) {
            int controlPB = controlPosBits[i];
            boolean controlValue = controlValues[i];
            if (((controlPB & worldID) == 0) == controlValue) {
                return false;
            }
        }
        return true;
    }

    public StateVector filter(int qubitID, boolean value) {
        int posbit = 1 << qubitID;
        StateVector newVector = new StateVector(getLength() / 2);
        int newVectorIndex = 0;
        for (int worldID = 0; worldID < getLength(); worldID++) {
            if (((posbit & worldID) == 0) != value) {
                newVector.worldRealParts[newVectorIndex] = worldRealParts[worldID];
                newVector.worldImaginaryParts[newVectorIndex++] = worldImaginaryParts[worldID];
            }
        }
        return newVector;
    }

    public void normalize() {
        double sqrtTotal = Math.sqrt(getTotal());
        System.out.println(worldRealParts);
        for (int worldID = 0; worldID < getLength(); worldID++) {
            worldRealParts[worldID] = worldRealParts[worldID] / sqrtTotal;
            worldImaginaryParts[worldID] = worldImaginaryParts[worldID] / sqrtTotal;
        }
    }

    private double getTotal() {
        double total = 0;
        for (int worldID = 0; worldID < getLength(); worldID++) {
            total += worldRealParts[worldID] * worldRealParts[worldID];
            total += worldImaginaryParts[worldID] * worldImaginaryParts[worldID];
        }
        System.out.println(total);
        return total;
    }

    public StateVector merge(StateVector other) {
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
        return new StateVector(newRealParts, newImaginaryParts);
    }

    public CompoundNBT write() {
        CompoundNBT compound = new CompoundNBT();
        ListNBT realPartsList = new ListNBT();
        for (double i:worldRealParts) {realPartsList.add(new DoubleNBT(i));}
        compound.put("real_parts",realPartsList);
        ListNBT imaginaryPartsList = new ListNBT();
        for (double i:worldImaginaryParts) {imaginaryPartsList.add(new DoubleNBT(i));}
        compound.put("imaginary_parts", imaginaryPartsList);
        return compound;
    }

    public static StateVector read(World world, CompoundNBT compound, int qubitCount) {
        int worldCount = 1 << qubitCount;
        double[] worldRealParts = new double[worldCount];
        double[] worldImaginaryParts = new double[worldCount];
        ListNBT worldRealPartsList = compound.getList("real_parts", Constants.NBT.TAG_DOUBLE);
        ListNBT worldImaginaryPartsList = compound.getList("imaginary_parts", Constants.NBT.TAG_DOUBLE);
        for (int i = 0; i < worldCount; i++) {
            worldRealParts[i] = worldRealPartsList.getDouble(i);
            worldImaginaryParts[i] = worldImaginaryPartsList.getDouble(i);
        }
        return new StateVector(worldRealParts, worldImaginaryParts);
    }
}
