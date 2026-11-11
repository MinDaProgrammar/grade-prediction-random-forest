package com.example;

public class TreeNode {
    //Either the Node hold these two or
    boolean isLeaf;
    double predictedValue;  //if isLeaf == true, it has predicted value

    //These three variables
    SplitResult splitRule;  // For Splits
    TreeNode left;
    TreeNode right;

    //Leaf Constructor
    public TreeNode(double val) {
        this.isLeaf = true;
        this.predictedValue = val;
    }
    
    //Splitting node constructor
    public TreeNode(SplitResult rule, TreeNode leftNode, TreeNode rightNode) {
        this.isLeaf = false;
        this.splitRule = rule;
        this.left = leftNode;
        this.right = rightNode;
    }
}
