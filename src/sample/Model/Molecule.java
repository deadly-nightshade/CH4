package sample.Model;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Molecule {
    private ArrayList<AtomNode> atomNodeList;
    private ArrayList<Bond> edgesList;

    final private String[] funcGrpOrder = new String[] {"alkane", "alkene", "alkyne",  "alcohol", "ether", "aldehyde", "ketone", "carbox", "ester","amine", "amide", "nitrile"};
    final private String[] funcGrpPriority = new String[] {"carbox", "ester", "amide", "nitrile", "aldehyde", "ketone", "alcohol", "amine", "alkene", "alkyne", "alkane", "ether"};
    final private String[] endPrefix = new String[] {"oic", "oate", "amide", "ile", "nitrile", "dehyde", "one", "ol", "amine", "ene", "yne", "ane", "ether"};
    final private String[] substituent = new String[] {};
    //    private ArrayList<String> imfList; // list of intermolecular forces it may form, todo sorted by dominance so choose the top one
// todo imf just... its just hb and ldf tbh,, and ldf is alw ticked :') UNLESS figure out ddf ddf just polar


    // properties
    private String boilingPoint; // Low <0, Medium 0<x<100, High x < 100. celsius
    private String solubilityWater; // Yes / No solubility in water (HB dominant)
    // no IDF, DIDF, DDF (need vsper so is difficult) todo put in about pg
    //supports HB, LDF, DDF but only for polar molecules - no vsepr
    private String solubilityOrganic; // Yes/ No solubility in organic (LDF dominant)
    // todo display chemical structure ??


    // list of functional groups! feature (and highlight where it is) todo
    //func grp and pos

    // reactionList - if 2 molecules share an item in reactionList (with same reaction conditions)
    // todo FIND WAY TO STORE CHEM MOLECULES STRUCTURE

    // todo when add/ delete molecule, IUPAC name changes

    public Molecule() {
        // initialising
        this.atomNodeList = new ArrayList<>();
        this.edgesList = new ArrayList<>();
    }


    // methods
    public String isMoleculeComplete() {
        if (atomNodeList.size() == 0) {
            return "Molecule has no atoms!";
        }

        if (atomNodeList.size() == 1) {
            return "Molecule has only one atom! Invalid organic molecule.";
        }

        boolean hasCarbon = false;

        for (AtomNode a: atomNodeList) {
            if (a.getAtom().getNoOfBonds() == 0) {
                // if no bond & > 1 atom
                return "Free-floating atom! Please bond the atom to the molecule.";
            }

            // if unbonded bond
            for (Bond b: a.getAtom().bondsList) {
                if (b.getAtom1() == null || b.getAtom2() == null) {
                    return "Unbonded bond! Please drag the bond to an atom.";
                }
            }

            // if not all of molecule's electron domains are bonded
            if (a.getAtom().getNoOfBonds() != a.getAtom().getMaxNoOfBonds()) {
                return "Not all electrons in an atom is used for bonding!";
            }

            if (a instanceof CarbonNode) {
                hasCarbon = true;
            }
        }
        if (!hasCarbon) {
            return "No carbon atoms in molecule! Invalid organic compound.";
        }
        return "Complete";
    }

    public int[] calculateFormula() {
        int[] noOfAtoms = new int[4];
        // Carbon, Hydrogen, Oxygen, Nitrogen

        for (AtomNode a: atomNodeList) {
            if (a instanceof CarbonNode) {
                noOfAtoms[0]++;
            }
            else if (a instanceof HydrogenNode) {
                noOfAtoms[1]++;
            } else if (a instanceof OxygenNode) {
                noOfAtoms[2]++;
            } else if (a instanceof NitrogenNode) {
                noOfAtoms[3]++;
            }
        }
        return noOfAtoms;
    }

    public double calculateMolecularMass() {
        // g/mol
        double mass = 0;

        for (AtomNode a: atomNodeList) {
            mass += a.getAtom().getAmu();
        }

        return mass;
    }

    /* public String calculateIUPACname(int[] funcGroups) {
        // priority: "carbox", "ester", "amide", "nitrile", "aldehyde", "ketone", "alcohol", "amine", "alkene", "alkyne", "alkane", "ether"
        // funcgroup Order: 0alkane, 1alkene, 2alkyne, 3alcohol, 4ether, 5aldehyde, 6ketone, 7carbox, 8ester,9amine, 10amide, 11nitrile

        int[] priorityFuncGroups = new int[] {funcGroups[7], funcGroups[8], funcGroups[10], funcGroups[11], funcGroups[5], funcGroups[6], funcGroups[3], funcGroups[9], funcGroups[1], funcGroups[2],funcGroups[0], funcGroups[4]};
        String endPrefix;


        return null;
        // todo
    } */

    public int[] calculateFunctionalGroups() {
        int[] funcGroups = new int[12];
        // Order: alkane, alkene, alkyne,  alcohol, ether, aldehyde, ketone, carbox, ester,amine, amide, nitrile

        int[] alkaeyne = hasAlkaeyne();
        int[] oxygenGroups = hasOxygenFuncGroup();
        int[] nitrogenGroups = hasNitrogenFuncGroup();

        funcGroups[0] = alkaeyne[0];
        funcGroups[1] = alkaeyne[1];
        funcGroups[2] = alkaeyne[2];
        funcGroups[3] = oxygenGroups[4];
        funcGroups[4] = oxygenGroups[5];
        funcGroups[5] = oxygenGroups[0];
        funcGroups[6] = oxygenGroups[1];
        funcGroups[7] = oxygenGroups[2];
        funcGroups[8] = oxygenGroups[3];
        funcGroups[9] = nitrogenGroups[0];
        funcGroups[10] = nitrogenGroups[1];
        funcGroups[11] = nitrogenGroups[2];

        return funcGroups;
    }

    public boolean[] calculateReactions(int[] funcGroups) {
        // assume its size 12 already!
        // funcgroup Order: 0alkane, 1alkene, 2alkyne, 3alcohol, 4ether, 5aldehyde, 6ketone, 7carbox, 8ester,9amine, 10amide, 11nitrile
        boolean[] reactions = new boolean[7];
        // 0neutralisation, 1oxidation, 2addition, 3substitution, 4condensation, 5dehydration, 6esterification

        if (funcGroups[7] > 0 || funcGroups[9] > 0) {
            reactions[0] = true;
        } if (funcGroups[3] > 0) {
            reactions[1] = true;
            reactions[5] = true;
            // todo implement tertiary alcohols!
        } if (funcGroups[1] > 0 || funcGroups[2] > 0) {
            reactions[2] = true;
        } if (funcGroups[0] > 0) {
            reactions[3] = true;
        } if (funcGroups[8] > 0 || funcGroups[9] > 0) {
            reactions[4] = true;
        } if (funcGroups[3] > 0 || funcGroups[7] > 0) {
            reactions[6] = true;
        }

        return reactions;
    }

    public boolean[] calculateImf(int[] formula) {
        boolean[] imf = new boolean[] {true, false, false};

        // single bonded o-h and n-h
        for (AtomNode a: atomNodeList) {
            if (!(a instanceof NitrogenNode) && !(a instanceof OxygenNode)) {continue;}
                ArrayList<Bond> bonds = a.getAtom().bondedToHydrogen();
                if (!bonds.isEmpty()) {
                    for (Bond b: bonds) {
                        if (b.getType() == 1) {
                            imf[1] = true;
                        }
                    }
                }
            }


        // if have o, n : ddf (if have HB definitely yes)
        // Carbon, Hydrogen, Oxygen, Nitrogen
        if (imf[1] || formula[2] > 0 || formula[3] > 0) {
            imf[2] = true;
        }

        return imf;
    }

    public boolean calculateSolubilityInWater(int[] formula) {
        if (formula[0] > 6 || (formula[2] == 0 && formula[3] == 0)) {
            // more than 6 carbons or only hydrocarbons
            return false;
        } return true;
    }

    // functional group helper functions
    public int[] hasAlkaeyne() {
        int[] alkaeyne = new int[3];

        for (AtomNode a: atomNodeList) {
            if (!(a instanceof CarbonNode)) {continue;}
            ArrayList<Bond> bonds = a.getAtom().bondedToCarbon();
            if (!bonds.isEmpty()) {
                for (Bond b: bonds) {
                    if (b.getType()==1) {
                       // alkane
                       alkaeyne[0]++;
                    } else if (b.getType()==2) {
                        alkaeyne[1]++;
                    } else if (b.getType()==3) {
                        alkaeyne[2]++;
                    }
                }
            }
        }

        // divide by 2 since double-counted
        for (int i = 0; i < 3; i++) {
            alkaeyne[i]/=2;
        }
        return alkaeyne;
    }

    // alcohol,ether doesn't work
    public int[] hasOxygenFuncGroup() {
        int[] oxygenFuncGroup = new int[6];
        // aldehyde, ketone, carbox, ester, alcohol, ether

        for (AtomNode a: atomNodeList) {
            if (!(a instanceof CarbonNode)) {continue;}
            ArrayList<Bond> bonds = a.getAtom().bondedToOxygen();
            if (!bonds.isEmpty()) {
                boolean carboxEsterfuncGroupFound = false;
                for (Bond b: bonds) {
                    if (b.getType() == 2 && b.getOtherAtom(a.getAtom()) instanceof Oxygen) {
                        // can be aldehyde, ketone, carbox, ester

                        // aldehyde ketone test
                        if (!a.getAtom().bondedToHydrogen().isEmpty()) {
                            // aldehyde
                            oxygenFuncGroup[0]++;
                        }

                        int noOfCarbon = 0;
                        for (Bond x : a.getAtom().bondsList) {
                            if (x.getOtherAtom(a.getAtom()) instanceof Carbon) {
                                noOfCarbon++;
                            }
                        }

                        if (noOfCarbon == 2) {
                            // ketone
                            oxygenFuncGroup[1]++;
                        }

                        // carbox ester test
                        for (Bond x : bonds) {
                            if (x.getType() == 1 && x.getOtherAtom(a.getAtom()) instanceof Oxygen) {
                                if (!x.getOtherAtom(a.getAtom()).bondedToHydrogen().isEmpty()) {
                                    // if oxygen bonded to H
                                    // carbox
                                    oxygenFuncGroup[2]++;
                                    carboxEsterfuncGroupFound = true;
                                    break;
                                } else if (x.getOtherAtom(a.getAtom()).bondedToCarbon().size() == 2) {
                                    // if oxygen bonded to 2 C
                                    // ester
                                    oxygenFuncGroup[3]++;
                                    carboxEsterfuncGroupFound = true;
                                    break;
                                }
                            }
                        }

                        if (carboxEsterfuncGroupFound) {
                            break;
                        }

                    }
                }

                if (carboxEsterfuncGroupFound) {
                    continue;
                }

                for (Bond b: bonds) {
                    if (b.getType() == 1 && b.getOtherAtom(a.getAtom()) instanceof Oxygen) {
                        if (!b.getOtherAtom(a.getAtom()).bondedToHydrogen().isEmpty()) {
                            // alcohol
                            oxygenFuncGroup[4]++;
                            continue;
                        }

                        int noOfC = 0;
                        for (Bond x : b.getOtherAtom(a.getAtom()).bondsList) {
                            if (x.getOtherAtom(b.getOtherAtom(a.getAtom())) instanceof Carbon) {
                                noOfC++;
                            }
                        }

                        if (noOfC == 2) {
                            // ether
                            oxygenFuncGroup[5]++;
                            continue;
                        }
                    }
                }

            }
        }
        oxygenFuncGroup[5] /= 2;
        // ether, as a non-terminal group and joined to 2 carbon, is double counted.
        return oxygenFuncGroup;
    }

    public int[] hasNitrogenFuncGroup() {
        int[] nitrogenFuncGroup = new int[3];
        // amine, amide, nitrile

        for (AtomNode a: atomNodeList) {
            if (!(a instanceof CarbonNode)) {continue;}
            ArrayList<Bond> bonds = a.getAtom().bondedToNitrogen();
            if (!bonds.isEmpty()) {
                for (Bond b: bonds) {

                    // nitrile test
                    if (b.getType() == 3) {
                        nitrogenFuncGroup[2]++;
                    } else if (b.getType() == 1) {

                        boolean amineFound = false;
                        // amide test
                        for (Bond x: a.getAtom().bondsList) {
                            if (x.getType() == 2 && x.getOtherAtom(a.getAtom()) instanceof Oxygen) {
                                nitrogenFuncGroup[1]++;
                                amineFound = true;
                                break;
                            }
                        }

                        // amine test
                        if (!amineFound) {
                            nitrogenFuncGroup[0]++;
                        }
                    }
                }
            }
        }

        return nitrogenFuncGroup;
    }

    // other methods
    public void addAtomNode(AtomNode atomNode) {
        this.atomNodeList.add(atomNode);
    }

    public void deleteAtomNode(AtomNode atomNode) {
        this.atomNodeList.remove(atomNode);
    }

    public void addBond(Bond bond) {
        this.edgesList.add(bond);
    }

    public void deleteBond(Bond bond) {
        this.edgesList.remove(bond);
    }

    public ArrayList<AtomNode> getAtomNodeList() {
        return atomNodeList;
    }

    public ArrayList<Bond> getEdgesList() {return edgesList;}

    // file io
    public boolean writeToFile(File file) {
        try {
            if (atomNodeList.size() == 0) {return true;}

            PrintWriter output = new PrintWriter(file);
            for (int i = 1; i < atomNodeList.size() + 1; i++) {
                output.println(i + " " + atomNodeList.get(i-1).getAtom().getClass().getName().substring(13) + " " + atomNodeList.get(i-1).getTranslateX()+","+atomNodeList.get(i-1).getTranslateY());
            }

            output.println("bonds");
            for (int i = 0; i < edgesList.size(); i++) {
                int[] ans = getPositiveIndexOfAtomFromBond(edgesList.get(i));
                output.println(ans[0]+"-"+ans[1]+" "+edgesList.get(i).getType());
            }

            output.close();
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    public int[] getPositiveIndexOfAtomFromBond(Bond b) {
        int[] ans = new int[] {0,0};

        for (int i = 0; i < atomNodeList.size(); i++) {
            if (atomNodeList.get(i).getAtom().equals(b.getAtom1())) {
                ans[0] = i+1;
            } else if (atomNodeList.get(i).getAtom().equals(b.getAtom2())) {
                ans[1] = i+1; // index+1
            }
        } return ans;
    }

}
