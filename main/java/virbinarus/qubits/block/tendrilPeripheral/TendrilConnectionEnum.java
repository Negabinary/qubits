package virbinarus.qubits.block.tendrilPeripheral;

import net.minecraft.util.IStringSerializable;

public enum TendrilConnectionEnum implements IStringSerializable {
    FROM("from"),
    TO("to"),
    NONE("none");

    private final String name;

    private TendrilConnectionEnum(String name) {
        this.name = name;
    }

    public String toString() {
        return this.getName();
    }

    public String getName() {
        return this.name;
    }
}
