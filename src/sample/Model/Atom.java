package sample.Model;

import java.util.ArrayList;

public abstract class Atom {

    public ArrayList<Bond> bondsList;
    private int maxNoOfBonds; // max number of bonds atom can form
    private int noOfBonds; // current no. of bonds

    private double amu; // Atomic Mass Unit, in g/mol
    private String element; // element of atom

    public Atom(String element, double amu, int maxNoOfBonds){
        this.bondsList = new ArrayList<>();
        this.maxNoOfBonds = maxNoOfBonds;
        this.noOfBonds = 0;
        this.amu = amu;
        this.element = element;
    }

    public Atom(String element, double amu, int maxNoOfBonds, ArrayList<Bond> bondsList) {
        this(element, amu, maxNoOfBonds);
        this.bondsList = bondsList;
    }

    // Methods
    public abstract boolean isStable();

    public boolean addBond(Bond bond) {
        if (noOfBonds == maxNoOfBonds) {
            return false; // false if alr max no of bonds reached
        }

        noOfBonds += bond.getType();
        bondsList.add(bond);

        return true;
    }

    public boolean deleteBond(Bond bond) {
        if (noOfBonds < 1) {
            return false;
        }
        noOfBonds -= bond.getType();
        bondsList.remove(bond);
        return true;
    }

    public ArrayList<Bond> bondedToCarbon() {
        ArrayList<Bond> bonds = new ArrayList<>();
        for (Bond b: bondsList) {
            if (b.getOtherAtom(this) instanceof Carbon) {
                bonds.add(b);
            }
        } return bonds;
    }
    public ArrayList<Bond> bondedToHydrogen() {
        ArrayList<Bond> bonds = new ArrayList<>();
        for (Bond b: bondsList) {
            if (b.getOtherAtom(this) instanceof Hydrogen) {
                bonds.add(b);
            }
        } return bonds;
    }
    public ArrayList<Bond> bondedToOxygen() {
        ArrayList<Bond> bonds = new ArrayList<>();
        for (Bond b: bondsList) {
            if (b.getOtherAtom(this) instanceof Oxygen) {
                bonds.add(b);
            }
        } return bonds;
    }
    public ArrayList<Bond> bondedToNitrogen() {
        ArrayList<Bond> bonds = new ArrayList<>();
        for (Bond b: bondsList) {
            if (b.getOtherAtom(this) instanceof Nitrogen) {
                bonds.add(b);
            }
        } return bonds;
    }

    // Getters

    public int getMaxNoOfBonds() {
        return maxNoOfBonds;
    }

    public int getNoOfBonds() {
        return noOfBonds;
    }

    public double getAmu() {  // for calculations
        return amu;
    }

    public String getAmuString() {  // for display
        return String.format("%.2f",amu);
    }

    public String getElement() {
        return element;
    }
}
