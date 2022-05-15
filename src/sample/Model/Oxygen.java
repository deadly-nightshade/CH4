package sample.Model;

public class Oxygen extends Atom{
    public Oxygen() {
        super("Oxygen", 16.00, 2);
    }

    @Override
    public boolean isStable() {
        return getNoOfBonds() == 2;
    }
}
