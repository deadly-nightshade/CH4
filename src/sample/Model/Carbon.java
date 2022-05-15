package sample.Model;

public class Carbon extends Atom{

    public Carbon() {
        super("Carbon", 12.01, 4);
    }

    @Override
    public boolean isStable() {
        return getNoOfBonds() == 4;
    }
}
