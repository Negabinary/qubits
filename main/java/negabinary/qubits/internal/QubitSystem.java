package negabinary.qubits.internal;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.swing.plaf.nimbus.State;
import java.util.*;

public class QubitSystem {
    private static final double R2O2 = Math.sqrt(0.5);
    private StateVector stateVector;
    private List<IQubitReference> qubitReferences;

    public QubitSystem(List<IQubitReference> qubitReferences, StateVector stateVector) {
        this.stateVector = stateVector;
        this.qubitReferences = qubitReferences;
    }

    public QubitSystem(IQubitReference newQubitReference) {
        this.stateVector = new StateVector(2);
        this.stateVector.setValue(0, 1, 0);
        this.qubitReferences = new ArrayList<>(Collections.singletonList(newQubitReference));
    }

    public QubitSystem(World world, CompoundNBT compound) {
        read(world, compound);
    }

    private void updateAll(IWorld world) {
        for (IQubitReference qubitReference : qubitReferences) {
            qubitReference.updateLocalState(getLocalState(qubitReference.getQubit(world).getQubitID()), world);
        }
    }

    public double getLocalState(int qubitID) {
        return stateVector.getProb(qubitID);
    }

    public void applyNot(int qubitID, int[] controlIDs, World world) {
        boolean[] controlValues = new boolean[controlIDs.length];
        Arrays.fill(controlValues, true);
        stateVector.applyGate(0, 0, 1, 0, 1, 0, 0, 0,
                qubitID, controlIDs, controlValues, world);
        updateAll(world);
    }

    public void applyH(int qubitID, int[] controlIDs, World world) {
        boolean[] controlValues = new boolean[controlIDs.length];
        Arrays.fill(controlValues, true);
        stateVector.applyGate(R2O2, 0, R2O2, 0, R2O2, 0, -R2O2, 0,
                qubitID, controlIDs, controlValues, world);
        updateAll(world);
    }

    public void applyGate(double ar, double ai, double br, double bi, double cr, double ci, double dr, double di,
                          int qubitID, int[] controlIDs, World world) {
        boolean[] controlValues = new boolean[controlIDs.length];
        Arrays.fill(controlValues, true);
        stateVector.applyGate(ar, ai, br, bi, cr, ci, dr, di,
                qubitID, controlIDs, controlValues, world);
        updateAll(world);
    }

    public boolean applyMeasure(int qubitID, IWorld world) {
        boolean result = stateVector.sample(qubitID);
        StateVector measuredQubitStateVector = new StateVector(result);

        if (stateVector.getLength() == 2) {
            stateVector = measuredQubitStateVector;
            updateAll(world);
        } else {
            stateVector = stateVector.filter(qubitID, result);
            stateVector.normalize();
            IQubitReference kickedQubit = qubitReferences.get(qubitID);
            kickQubit(qubitID, world);
            createKickedSystem(kickedQubit, measuredQubitStateVector, world);
        }

        return result;
    }

    public boolean applyMeasureAndRemove(int qubitID, IWorld world) {
        boolean result = stateVector.sample(qubitID);
        if (stateVector.getLength() != 2) {
            stateVector = stateVector.filter(qubitID, result);
            stateVector.normalize();
            kickQubit(qubitID, world);
        }
        return result;
    }

    private void kickQubit(int qubitID, IWorld world) {
        //kicking system
        qubitReferences.remove(qubitID);
        IQubitReference newSystemHolderReference = qubitReferences.get(0);
        newSystemHolderReference.getQubit(world).systemHolderReference.getQubit(world).isSystemHolder = false;
        newSystemHolderReference.getQubit(world).isSystemHolder = true;
        for (IQubitReference qubitReference: qubitReferences) {
            qubitReference.getQubit(world).systemHolderReference = newSystemHolderReference;
        }

        for (int qID = qubitID; qID < qubitReferences.size(); qID++) {
            qubitReferences.get(qID).getQubit(world).qubitID --;
        }
        updateAll(world);
    }

    private void createKickedSystem(IQubitReference kickedQubitReference, StateVector newStateVector, IWorld world) {
        //kicked system
        Qubit qubit = kickedQubitReference.getQubit(world);
        List<IQubitReference> newQubitReferences = new LinkedList<IQubitReference>();
        newQubitReferences.add(kickedQubitReference);
        qubit.qubitSystem = new QubitSystem(newQubitReferences, newStateVector);
        qubit.isSystemHolder = true;
        qubit.qubitID = 0;
        qubit.systemHolderReference = kickedQubitReference;
        qubit.qubitSystem.updateAll(world);
    }


    public void merge(QubitSystem other, World world) {
        stateVector = stateVector.merge(other.stateVector);

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
        CompoundNBT compound = stateVector.write();
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
        stateVector = StateVector.read(world, compound, qubitCount);
    }
}
