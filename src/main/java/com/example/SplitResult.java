package com.example;
public class SplitResult {

    private final int featureIndex;   //36 course + 5 attribute, so starting from 0 index to 40 in order
    //so 0 to 35 are subjects in the original order, and from 36 to 40 are exact order given from studentInfo.csv
    //36 : QCT, 37: Symbiotic, 38: Drift, 39: Psionic, 40: Bio
    private final String thresholdValue; // Stored as a String for uniformity (e.g., "8.0" or "Stable")
    //This somehow need to be manipulated when comparing continuous value vs fixed value
    //e.g. 8.0 need to be compared as double while "Stable" needs to be compared as equal or not 

    private final double varianceReduction; // The final variance reduction 

    // Split prediction (If X then Y or Z)
    private final double leftMean;        // Mean grade for the group that satisfies the split condition
    private final double rightMean;       // Mean grade for the group that does NOT satisfy the split condition

   
    public SplitResult(int featureIndex, String thresholdValue, double varianceReduction, double leftMean, double rightMean) {
        this.featureIndex = featureIndex;
        this.thresholdValue = thresholdValue;
        this.varianceReduction = varianceReduction;
        this.leftMean = leftMean;
        this.rightMean = rightMean;
    }

    public int getFeatureIndex() {
        return featureIndex;
    }

    public String getThresholdValue() {
        return thresholdValue;
    }

    public double getVarianceReduction() {
        return varianceReduction;
    }

    public double getLeftMean() {
        return leftMean;
    }

    public double getRightMean() {
        return rightMean;
    }

    //Just overriding toString method for convinience
    @Override
    public String toString() {
        return "Best Split: Feature Index=" + featureIndex + 
               ", Threshold='" + thresholdValue + "'" + 
               ", Gain=" + String.format("%.4f", varianceReduction) + 
               ", Means (L/R): " + String.format("%.2f", leftMean) + 
               "/" + String.format("%.2f", rightMean);
    }
}