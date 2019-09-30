package virbinarus.qubits.globalboolean;

public class GlobalBoolean implements IGlobalBoolean{

    private boolean globalBoolean;

    public GlobalBoolean(boolean newBoolean) {
        globalBoolean = newBoolean;
    }

    @Override
    public boolean getBoolean() {
        return globalBoolean;
    }

    @Override
    public void setBoolean(boolean newBoolean) {
        globalBoolean = newBoolean;
    }
}
