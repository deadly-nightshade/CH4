package sample.Model;

public class Hydrogen extends Atom{

    public Hydrogen() {
        super("Hydrogen", 1.008, 1);
    }

    @Override
    public boolean isStable() {
        return getNoOfBonds() == 1;
    }
}
