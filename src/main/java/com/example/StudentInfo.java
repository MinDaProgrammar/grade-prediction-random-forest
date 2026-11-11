package com.example;
public class StudentInfo {
    String studentID;
    String quantumCoherenceThreshold;
    String symbioticNetworkCompatibility;
    String astroTemporalDriftResistance;    //note that this value is int
    String psionicInterferenceTolerance;    //and this value is double, but for the sake of storing value easily I left it as string
    String bioLuminalTransmission;
    double [] studentGrades;

    public StudentInfo(String studentID, String quantumCoherenceThreshold, 
                       String symbioticNetworkCompatibility, String astroTemporalDriftResistance,
                       String psionicInterferenceTolerance, String bioLuminalTransmission, double[] studentGrades) {
        this.studentID = studentID;
        this.quantumCoherenceThreshold = quantumCoherenceThreshold;
        this.symbioticNetworkCompatibility = symbioticNetworkCompatibility;
        this.astroTemporalDriftResistance = astroTemporalDriftResistance;
        this.psionicInterferenceTolerance = psionicInterferenceTolerance;
        this.bioLuminalTransmission = bioLuminalTransmission;
        this.studentGrades = studentGrades;
    }

    public void displayInfo() {
        System.out.println("Student ID: " + studentID);
        System.out.println("Quantum Coherence Threshold: " + quantumCoherenceThreshold);
        System.out.println("Symbiotic Network Compatibility: " + symbioticNetworkCompatibility);
        System.out.println("Astro-Temporal Drift Resistance: " + astroTemporalDriftResistance);
        System.out.println("Psionic Interference Tolerance: " + psionicInterferenceTolerance);
        System.out.println("Bio-Luminal Transmission: " + bioLuminalTransmission);
        System.out.println("-----------------------------------------");
    }

    public void displayStudentGrade(){
        for(int i=0;i<studentGrades.length;i++){
            System.out.print(studentGrades[i]+" ");
        }
    }
}