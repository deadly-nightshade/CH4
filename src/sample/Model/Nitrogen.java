package sample.Model;

public class Nitrogen extends Atom{
    public Nitrogen() {
        super("Nitrogen", 14.01, 3);  // no dative bonds!!
    }

    @Override
    public boolean isStable() {
        return getNoOfBonds() == 3;
    }
}
