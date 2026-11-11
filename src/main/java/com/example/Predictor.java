package com.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//All the code for training the datas and predicting are here
//From Phase1 step3 and step4 - finding best decision stump, finding multiple stumps
//Phase3 - Building a Regression tree using recursion & building a random forest(multiple trees)

//Task to complete
// Finding the stopping criteria for a tree, and pruning 

public class Predictor {

    // The forest , lists of decision stumps at once
    private List<SplitResult> forest = new ArrayList<>();
    

	//----------------------Prediction Logic with decision stumps------------------------------------
    // Train the forest (Phase1 Step 4 add all together)
    public void train(double[][] graduateGrades, StudentInfo[] currentGrades, int courseIndex, int kGrad, int kInfo) {
        //probably need to clear the forest to create multiple forests
        this.forest.clear();
        
        // graduate grades prediction (courses correlation)
        List<SplitResult> gradStumps = findTopSplitsGraduates(graduateGrades, courseIndex, kGrad);
        this.forest.addAll(gradStumps);

        // current grades prediction(studnetInfo attributes)
        List<SplitResult> infoStumps = findTopSplits(currentGrades, courseIndex, kInfo);
        this.forest.addAll(infoStumps);
    }

    //Testing result for the bestsplit
    public void testSingleSplit(StudentInfo[] students, int courseIndex) {
        System.out.println("--- Testing Single Best Split for Course " + courseIndex + " ---");
        SplitResult result = findBestAttributeSplit(students, courseIndex);
		
        System.out.println(result);
    }

    //  Testing results for multiple stumps
    public void testTopSplits(StudentInfo[] students, int courseIndex, int k) {
        System.out.println("--- Testing Top " + k + " Splits for Course " + courseIndex + " ---");
        List<SplitResult> bestSplits = findTopSplits(students, courseIndex, k);
        for (SplitResult bs : bestSplits) {
            System.out.println(bs.toString());
        }
    }

    // Predict using the internal forest
    public double predict(StudentInfo student) {
        return predictWithForestOfStumps(this.forest, student);
    }

	//---------------- Prediction logic with Decision Trees and Random Forest---------------------------

	public double predictRecursive(TreeNode node, StudentInfo student) {
		// Base case: reaching a leaf
		if (node.isLeaf) {
			return node.predictedValue;
		}

		// Check the split condition
		boolean goLeft = checkCondition(student, node.splitRule.getFeatureIndex(), node.splitRule.getThresholdValue());

		// Keep going down until this specific student reach a leaf
		if (goLeft) {
			return predictRecursive(node.left, student);
		} else {
			return predictRecursive(node.right, student);
		}
	}

	//Printing to visualize the tree
	public void printTree(TreeNode node) {
        if (node == null) {
            System.out.println("Tree is empty.");
            return;
        }
        System.out.println("--- Decision Tree Structure ---");
        printTreeRecursive(node, "", true, "Root",0);
    }

    private void printTreeRecursive(TreeNode node, String indent, boolean isLast, String branchLabel, int depth) {
        if (node == null) return;

        // Print indentation and connectors
        System.out.print(indent);
        if (isLast) {
            System.out.print("└── [" + branchLabel + "] ");
            indent += "    ";
        } else {
            System.out.print("├── [" + branchLabel + "] ");
            indent += "│   ";
        }

        // Add Depth Label
        String depthTag = "[Depth " + depth + "] ";

        if (node.isLeaf) {
            System.out.println(depthTag + "Leaf: Predict " + String.format("%.2f", node.predictedValue));
        } else {
            // It's a split node
            String featureName = getFeatureName(node.splitRule.getFeatureIndex());
            String threshold = node.splitRule.getThresholdValue();
            
            System.out.println(depthTag + "Is " + featureName + " >= " + threshold + "?");

            // Recurse with depth incremented
            printTreeRecursive(node.left, indent, false, "Yes", depth + 1);
            printTreeRecursive(node.right, indent, true, "No", depth + 1);
        }
    }

	private String getFeatureName(int index) {
		// 0-35 are Courses
		if (index >= 0 && index < 36) {
			// Access the course names array from your FileDisplayer class
			if (FileDisplayer.courses != null && index < FileDisplayer.courses.length) {
				return FileDisplayer.courses[index];
			}
			return "Course_" + index;
		}
		
		// 36-40 are Student Attributes
		switch (index) {
			case 36: return "QCT";  // Quantum Coherence Threshold
			case 37: return "SNC";  // Symbiotic Network Compatibility
			case 38: return "ATDR"; // Astro-Temporal Drift Resistance
			case 39: return "PIT";  // Psionic Interference Tolerance
			case 40: return "BLT";  // Bio-Luminal Transmission
			default: return "Unknown_Feature_" + index;
		}
	}

	
	/*********************************** Phase 3 Build Decision Trees***************************************/
	//Building a full tree using current grades + studentInfo
	public TreeNode buildStudentInfoTree(List<StudentInfo> students, int targetCourseIndex, int depth) {
		double currentMean = calculateMean(students, targetCourseIndex);	//the prediction at  this point of split

		// Stopping Criteria
		// to use post pruning, trying to have an overfitting tree.
		if (students.size() < 2 || calculateVariance(students, targetCourseIndex)==0) {
			double leafValue = calculateMean(students, targetCourseIndex);
			return new TreeNode(leafValue);
		}

		// Find Best Split using attributes (Index 36-40)
		SplitResult bestSplit = findBestAttributeSplit(students.toArray(new StudentInfo[0]), targetCourseIndex);

		if (bestSplit == null) {
			return new TreeNode(calculateMean(students, targetCourseIndex));
		}

		// Split the Data
		List<StudentInfo> leftGroup = new ArrayList<>();
		List<StudentInfo> rightGroup = new ArrayList<>();
		
		// Reuse your existing logic to split students
		splitData(students, bestSplit.getFeatureIndex(), bestSplit.getThresholdValue(), leftGroup, rightGroup);

		// Recursive Step
		TreeNode leftChild = buildStudentInfoTree(leftGroup, targetCourseIndex, depth + 1);
		TreeNode rightChild = buildStudentInfoTree(rightGroup, targetCourseIndex, depth + 1);

		TreeNode node = new TreeNode(bestSplit, leftChild, rightChild);
		node.predictedValue = currentMean; 
		return node;
	}

	//Building a full tree using graduate grades
	public TreeNode buildGraduateTree(List<double[]> data, int targetCourseIndex, int depth) {
		double currentMean = calculateMeanDoubles(data, targetCourseIndex);	//the prediction at  this point of split

		// Stopping Criteria (Right now just for pre-pruning method)
		if (data.size() < 2 || calculateVarianceDoubles(data, targetCourseIndex)==0) { // Example criteria
			double leafValue = calculateMeanDoubles(data, targetCourseIndex);
			return new TreeNode(leafValue);
		}

		// Find Best Split
		SplitResult bestSplit = findBestSubjectSplit(data, targetCourseIndex); 

		if (bestSplit == null) {
			return new TreeNode(calculateMeanDoubles(data, targetCourseIndex));
		}

		// Split the Data
		List<double[]> leftData = new ArrayList<>();
		List<double[]> rightData = new ArrayList<>();

		// Split data based on this threshold
		for (double[] row : data) {
			if (row[bestSplit.getFeatureIndex()] >= Double.parseDouble(bestSplit.getThresholdValue())) {
				leftData.add(row);
			} else {
				rightData.add(row);
			}
		}
		// Recursive Step
		TreeNode leftChild = buildGraduateTree(leftData, targetCourseIndex, depth + 1);
		TreeNode rightChild = buildGraduateTree(rightData, targetCourseIndex, depth + 1);

		TreeNode node = new TreeNode(bestSplit, leftChild, rightChild);
		node.predictedValue = currentMean; 
		return node;
	}
	//2. Post pruning using validation set - finding the stopping criteria for the tree

    public void postprune(TreeNode node, List<StudentInfo> validationData, int targetCourseIndex, int depth) {
		if (node == null || node.isLeaf || validationData.isEmpty()) return;
	
		List<StudentInfo> leftSubset = new ArrayList<>();
		List<StudentInfo> rightSubset = new ArrayList<>();
		splitData(validationData, node.splitRule.getFeatureIndex(), node.splitRule.getThresholdValue(), leftSubset, rightSubset);
	
		postprune(node.left, leftSubset, targetCourseIndex,depth+1);
		postprune(node.right, rightSubset, targetCourseIndex, depth+1);
	
		//What if we treate this as a leaf
		//mse before pruning and after pruning
		double mseAfter = computeMSE(node, validationData, targetCourseIndex, true);
		
		//What if we treate this as a subtree
		double mseBefore = computeMSE(node, validationData, targetCourseIndex, false);
	
		//System.out.println("mseBefore: "+mseBefore+"  mseAfter: "+mseAfter);
		// Prune if simple leaf is better or equal
		if (mseAfter <= mseBefore) {
			//System.out.println("Postpruend "+"Feature index: "+node.splitRule.getFeatureIndex()+" equals to "+node.splitRule.getThresholdValue());
			//System.out.println("Depth: "+ depth);
			node.isLeaf = true;
			node.left = null;
			node.right = null;
			node.splitRule = null;
		}
	}

    public double computeMSE (TreeNode node, List<StudentInfo> validation, int targetCourseIndex, boolean wasLeaf) {
        double sum = 0.0;
		int count = 0;
        for (StudentInfo s : validation) {
			Double actual = getGrade(s, targetCourseIndex);
			
			if (actual == null || Double.isNaN(actual)) {
				continue;
			}
	
			double predicted;
			if (wasLeaf) {
				// Predict using the specific node's stored mean if it was treated as a leaf
				predicted = node.predictedValue;
			} else {
				// Predict using the normal recursive method as it is a tree
				predicted = predictRecursive(node, s);
			}
			
			count++;
			double error = actual - predicted;
			sum += Math.pow(error, 2);
		}
        return sum/count;
    }

	// --------Same pruning method but for different data types(graduate grades) ---------------------
    // Recursive prediction for a tree (using double[] data type)
    public double predictRecursive(TreeNode node, double[] grades) {
        if (node.isLeaf) {
            return node.predictedValue;
        }

        // Grades tree only uses features 0-35 (Courses)
        int featureIndex = node.splitRule.getFeatureIndex();
        double threshold = Double.parseDouble(node.splitRule.getThresholdValue());

        // if grade >= threshold go left, else right
        if (grades[featureIndex] >= threshold) {
            return predictRecursive(node.left, grades);
        } else {
            return predictRecursive(node.right, grades);
        }
    }

    // Post-pruning for graduate datas + currentgrades (merged data)
    public void postpruneSubject(TreeNode node, List<double[]> validationData, int targetCourseIndex,int depth) {
        if (node == null || node.isLeaf || validationData.isEmpty()) return;

        List<double[]> leftSubset = new ArrayList<>();
        List<double[]> rightSubset = new ArrayList<>();

        int featureIndex = node.splitRule.getFeatureIndex();
        double threshold = Double.parseDouble(node.splitRule.getThresholdValue());

        // Split validation data based on the node's rule
        for (double[] row : validationData) {
            if (row[featureIndex] >= threshold) {
                leftSubset.add(row);
            } else {
                rightSubset.add(row);
            }
        }

        // Recursive pruning
        postpruneSubject(node.left, leftSubset, targetCourseIndex,depth+1);
        postpruneSubject(node.right, rightSubset, targetCourseIndex,depth+1);

        // Compare MSE: Treat as Leaf vs Treat as Subtree
        double mseAfter = computeMSESubject(node, validationData, targetCourseIndex, true);
        double mseBefore = computeMSESubject(node, validationData, targetCourseIndex, false);

        if (mseAfter <= mseBefore) {
            System.out.println("Pruned Node at Feature: " + featureIndex);
			System.out.println("Depth: "+depth);
            node.isLeaf = true;
            node.left = null;
            node.right = null;
            node.splitRule = null;
        }
    }

    //  MSE Calculatio for double[] data
    public double computeMSESubject(TreeNode node, List<double[]> validation, int targetCourseIndex, boolean wasLeaf) {
        double sum = 0.0;
        int count = 0;

        for (double[] row : validation) {
            double actual = row[targetCourseIndex];

            // Skip invalid target grades
            if (Double.isNaN(actual)) continue;

            double predicted;
            if (wasLeaf) {
                predicted = node.predictedValue;
            } else {
                predicted = predictRecursive(node, row); // Calls the new double[] overload above
            }

            double error = actual - predicted;
            sum += Math.pow(error, 2);
            count++;
        }
        return count == 0 ? 0.0 : sum / count;
    }

	//3. Create multiple trees to fix each other (Random Forest)
	public double predictRandomForest(List<double[]> gradData, List<StudentInfo> studentData, StudentInfo targetStudent, int courseIndex, int numIterations) {

		double totalPredictionSum = 0.0;
		int totalTreesBuilt = 0;
		java.util.Random rand = new java.util.Random();

		//loop number of trees
		for(int i = 0; i < numIterations; i++) {

			//create random subsets
			//(70% sample)
			List<double[]> randomGradSubset = createRandomSubsetGrads(gradData, 0.7, rand);
			List<StudentInfo> randomStudentSubset = createRandomSubsetInfo(studentData, 0.7, rand);

			//build trees on rand subjs
			TreeNode gradTree = buildGraduateTree(randomGradSubset, courseIndex, 0);
			TreeNode infoTree = buildStudentInfoTree(randomStudentSubset, courseIndex, 0);

			//predict
			if (gradTree != null) { //check if its valid
				double prediction = predictRecursive(gradTree, targetStudent);
				totalPredictionSum += prediction;
				if(prediction> 0 ){
					totalTreesBuilt++;
				}
			}

			if(infoTree != null) {
				double prediction = predictRecursive(infoTree, targetStudent);
				totalPredictionSum += prediction;
				if(prediction> 0 ){
					totalTreesBuilt++;
				}
			}

		}
		if (totalTreesBuilt == 0) return -1; // return minus so we know in our final prediction that there's no training

		return totalPredictionSum / totalTreesBuilt; //return avg of predictions
	}

	//helper to pick random graduate data
	private List<double[]> createRandomSubsetGrads(List<double[]> original, double ratio, java.util.Random rand) {
		List<double[]> subset = new ArrayList<>();
		int targetSize = (int) (original.size() * ratio);

		for (int k = 0; k < targetSize; k++) {
			subset.add(original.get(rand.nextInt(original.size())));
		}
		return subset;

	}

	//helper to pick random student info data
	private List<StudentInfo> createRandomSubsetInfo(List<StudentInfo> original, double ratio, java.util.Random rand) {
		List<StudentInfo> subset = new ArrayList<>();
		int targetSize = (int) (original.size() * ratio);

		for (int k = 0; k < targetSize; k++) {
			subset.add(original.get(rand.nextInt(original.size())));
		}
	return subset;
	}
    //************************* Phase1 STEP 3 ACTUAL DECISION STUMPS ***************************************/
	
	//Finding best split property for one subject using variance reduction
	public static SplitResult findBestAttributeSplit(StudentInfo[] studentInfoArray, int courseIndex){
		//Only using students who have a valid grade for the target course
		//System.out.println("findBestSplit method called!"); //debugging

        List<StudentInfo> validStudents = new ArrayList<>();
        for (StudentInfo s : studentInfoArray) {
            Double grade = getGrade(s, courseIndex);
            if (grade != null) {
                validStudents.add(s);
            }
        }

		if (validStudents.size() < 2) {
			double prediction;
			if (validStudents.isEmpty()) {
				prediction = 0.0; // if there is no student just give prediction of 0
				//This part suggests that the given course is an open course. 
			} else {
				// Size is 1
				prediction = getGrade(validStudents.get(0), courseIndex);
			}
			
			System.out.println("Split is not possible! Returning from BestAttributeSplit Method!");
			//originally I returned null there is no split, and I have null point exception and cannot predict grades for some subject.
			//so instead of returning null I return dummy prediction
			return new SplitResult(55, "No Split", 0.0, prediction, prediction);
		}

        // Calculate initial variance (Impurity)
        double parentVariance = calculateVariance(validStudents, courseIndex);
		//System.out.println("Parent Variance: "+ parentVariance);
        
        SplitResult bestSplit = null;
        double maxGain = -1.0;	//later going to compare the max gain and switch the bestSplitResult

        //Iterate through all possible features (0 to 40)
		//Since there are limited number of possible split, 
		//I just set each index from 0 to 35 as course comparison, and 36 to 40 as attribute from studentInfo
        for (int featureIndex = 36; featureIndex <= 40; featureIndex++) {
            
            // Skip if the feature is the target course itself (target course value is unknown yet)
            if (featureIndex == courseIndex) continue;

            // Get all unique values existing in the data for this feature
            Set<String> candidateThresholds = getUniqueValues(validStudents, featureIndex);
			// System.out.println("Candidate Thresholds are: "); //Debugging
			// System.out.println(candidateThresholds);

			//Iterate for every property of every attribute possible
            for (String threshold : candidateThresholds) {
                
                // Split data based on this threshold if(X then Y or if not Z)
				//left group satisfy the condition (X)
				//right group does not (Z)
                List<StudentInfo> leftGroup = new ArrayList<>();
                List<StudentInfo> rightGroup = new ArrayList<>();

                splitData(validStudents, featureIndex, threshold, leftGroup, rightGroup);

                // Skip invalid splits (empty sides) when there is no split
                if (leftGroup.isEmpty() || rightGroup.isEmpty()) continue;

                // Calculate Weighted Variance of the children
				//Weighted Variance = Sum of probability of each split * variance of each split 
                double varLeft = calculateVariance(leftGroup, courseIndex);
                double varRight = calculateVariance(rightGroup, courseIndex);
                
                double weightLeft = (double) leftGroup.size() / validStudents.size();
                double weightRight = (double) rightGroup.size() / validStudents.size();
                
                double weightedVariance = (weightLeft * varLeft) + (weightRight * varRight);
                double gain = parentVariance - weightedVariance;

                // Check if this is the best split so far
                if (gain > maxGain) {
                    maxGain = gain;
                    double meanLeft = calculateMean(leftGroup, courseIndex);
                    double meanRight = calculateMean(rightGroup, courseIndex);
                    
                    // We store the "String" version of the threshold, even for numbers
                    bestSplit = new SplitResult(featureIndex, threshold, maxGain, meanLeft, meanRight);
                }
            }
        }

        return bestSplit;
	}

	// Finding best split from courses(index 0~35) (Graduate Data)
	public static SplitResult findBestSubjectSplit(List<double[]> graduateGrades, int courseIndex) {

		// Check sufficient data
		if (graduateGrades.size() < 2) {
			double prediction;
			if (graduateGrades.isEmpty()) {
				prediction = 0.0; // No data, predict 0
			} else {
				// Size is 1, return that grade
				prediction = graduateGrades.get(0)[courseIndex];
			}
			
			System.out.println("Split is not possible! Returning from BestSubjectSplit Method!");
			// Return dummy prediction
			return new SplitResult(55, "No Split", 0.0, prediction, prediction);
		}

		// Calculate initial variance
		// Variance reduction = ParentVariance - probability of 1stsplit* splitted variance - probabiility of 2nd split*splittedd
		double parentVariance = calculateVarianceDoubles(graduateGrades, courseIndex);
		
		SplitResult bestSplit = null;
		double maxGain = -1.0; 

		// Iterate through all possible features (Columns 0 to end, but this should ideally be 35)
		//System.out.println("graduateGrades[0] = "+graduateGrades.get(0).length);
		for (int featureIndex = 0; featureIndex < graduateGrades.get(0).length; featureIndex++) {
			
			// Skip if the feature is the target course itself
			if (featureIndex == courseIndex) continue;

			// Get all unique values existing in the data for this feature (Helper from Predictor.java)
			Set<String> candidateThresholds = getUniqueValuesDoubles(graduateGrades, featureIndex);

			// Iterate for every threshold
			for (String threshold : candidateThresholds) {
				
				// Parse threshold to double for comparison
				double numericThreshold = Double.parseDouble(threshold);

				List<double[]> leftGroup = new ArrayList<>();
				List<double[]> rightGroup = new ArrayList<>();

				// Split data based on this threshold
				for (double[] row : graduateGrades) {
					if (row[featureIndex] >= numericThreshold) {
						leftGroup.add(row);
					} else {
						rightGroup.add(row);
					}
				}

				// Skip invalid splits (empty sides)
				if (leftGroup.isEmpty() || rightGroup.isEmpty()) continue;

				// Calculate Weighted Variance of the children
				double varLeft = calculateVarianceDoubles(leftGroup, courseIndex);
				double varRight = calculateVarianceDoubles(rightGroup, courseIndex);
				
				double weightLeft = (double) leftGroup.size() / graduateGrades.size();
				double weightRight = (double) rightGroup.size() / graduateGrades.size();
				
				double weightedVariance = (weightLeft * varLeft) + (weightRight * varRight);
				double gain = parentVariance - weightedVariance;

				// Check if this is the best split so far
				if (gain > maxGain) {
					maxGain = gain;
					double meanLeft = calculateMeanDoubles(leftGroup, courseIndex);
					double meanRight = calculateMeanDoubles(rightGroup, courseIndex);
					
					bestSplit = new SplitResult(featureIndex, threshold, maxGain, meanLeft, meanRight);
				}
			}
		}

		return bestSplit;
	}


	public static void splitData(List<StudentInfo> students, int featureIndex, String threshold, List<StudentInfo> left, List<StudentInfo> right) {
		//System.out.println("splidData method called!");
        boolean isNumeric = isFeatureNumeric(featureIndex);
        double numericThreshold = 0.0;
        
        // parse the threshold to double type if we are doing a numeric comparison
        if (isNumeric) {
             try {
                 numericThreshold = Double.parseDouble(threshold); //because threshold is stored as string value
             } catch (NumberFormatException e) {
                 return; 
             }
        }

        for (StudentInfo s : students) {
            Object rawValue = getFeatureValue(s, featureIndex); //this will either be double or string value
            
            if (rawValue == null) continue; // Skip missing data

            if (isNumeric) {
                // Numeric Comparison
                // "rawValue" is already a Double because getFeatureValue parsed it
                if (((Double) rawValue) >= numericThreshold) {
                    left.add(s);
                } else {
                    right.add(s);
                }
            } else {
                // String Comparison (Nominal)
                if (rawValue.toString().equals(threshold)) {
                    left.add(s);
                } else {
                    right.add(s);
                }
            }
        }
    }

    //Extracting values from studentInfo attribute. Also parsing some numeric values from string
    private static Object getFeatureValue(StudentInfo s, int featureIndex) {
        //System.out.println("getFeaturedValue method called!");
		// 0-35: Course Grades
        if (featureIndex < 36) {
            return getGrade(s, featureIndex);
        }

        // 36-40: Student Attributes
        switch (featureIndex) {
            case 36: return s.quantumCoherenceThreshold;       // QCT = "Stable" smt like this and it will return "Stable"
            case 37: return s.symbioticNetworkCompatibility;   // SNC = "None/Harmonized" so it will return "Harmonized" smt
            
            case 38: // "ATR" is stored as "2 ns/mc" (String value)
                try {
                    String val = s.astroTemporalDriftResistance;
                    if (val == null || val.equals("None")) return null;
                    // Split "2 ns/mc" by space and take the first part
                    return Double.parseDouble(val.split(" ")[0]);
                } catch (Exception e) { return null; }

            case 39: // "PIT" is also stored as String so parse it
                try {
                    String val = s.psionicInterferenceTolerance;
                    if (val == null || val.equals("None")) return null;
                    return Double.parseDouble(val);
                } catch (Exception e) { return null; }

            case 40: return s.bioLuminalTransmission;   // String
            default: return null;
        }
    }

   //Check if the threshold has numeric or static value 
    private static boolean isFeatureNumeric(int featureIndex) {
        // 0-35 are course grades (Numeric)
        if (featureIndex < 36) return true;

        // 38 (Drift) and 39 (Psionic) are Numeric
        if (featureIndex == 38 || featureIndex == 39) return true;

        // 36 (Quantum), 37 (Symbiotic), 40 (Bio) are Strings (Nominal)
        return false;
    }

    //get all the possible property of for 1 attribute using HashSet, so no overlap
	// ex) for course [3.0,4.0,5.0] ... and for QCT ["Stalbe, Fracutred, blahblah"]
    private static Set<String> getUniqueValues(List<StudentInfo> students, int featureIndex) {
		//System.out.println("get UniqueValue method called!");
        Set<String> values = new HashSet<>();
        for (StudentInfo s : students) {
            Object val = getFeatureValue(s, featureIndex);
            if (val != null) values.add(val.toString());
        }
        return values;
    }

    //Already created from step1, but just changing the parameters so I can use it for step3
    private static double calculateMean(List<StudentInfo> students, int courseIndex) {
		//System.out.println("calcul.ateMean method called!");
        if (students.isEmpty()) return 0.0;
        double sum = 0.0;
        int count = 0;
        for (StudentInfo s : students) {
            Double g = getGrade(s, courseIndex);
            if (g != null) {
                sum += g;
                count++;
            }
        }
        return count == 0 ? 0.0 : sum / count;
    }

	//rewrote for step3
    private static double calculateVariance(List<StudentInfo> students, int courseIndex) {
		//System.out.println("calculatedVariance method called!");
        if (students.isEmpty()) return 0.0;
        double mean = calculateMean(students, courseIndex);
        double sumSq = 0.0;
        int count = 0;
        for (StudentInfo s : students) {
            Double g = getGrade(s, courseIndex);
            if (g != null) {
                sumSq += Math.pow(g - mean, 2);
                count++;
            }
        }
        return count == 0 ? 0.0 : sumSq / count;
    }


	//Needed to implment this after getting null values for variance values which give null for all split
	private static Double getGrade(StudentInfo s, int courseIndex) {
		// Ensure the array exists and we aren't asking for a course outside bounds
		if (s.studentGrades == null || courseIndex >= s.studentGrades.length) {
			return null;
		}
		
		double val = s.studentGrades[courseIndex];
		
		if (Double.isNaN(val)) { 
			return null; 
		}
		
		return val;
	}

    /************************* STEP4, Making Forest Of STUMPS  *********************************/
	
	//This is the Top K method, choosing k number of bestsplits and taking average 
	public static List<SplitResult> findTopSplits(StudentInfo[] studentInfoArray, int courseIndex, int k) {
		List<SplitResult> allFeatureBestSplits = new ArrayList<>();

		// Iterate through every feature 
		// I modified the featureIndex to start from 36 so it only looks at the attribute
		// The comparison for courses will be done from graduate grades now with more datasets.
		for (int featureIndex = 36; featureIndex <= 40; featureIndex++) {
			if (featureIndex == courseIndex) continue;

			//instead of using findBestSplit method, I am using findBestSplitForOneFeature
			//because findBestSplit method might use the same features for the data evaluation, which doesn't really train the data with new information
			//ex) the best variance might be course Physics > 8.0 and the second variance could be Physics > 7.0 then we are not really comparing with different attributes.
			SplitResult bestSplitForFeature = findBestSplitForOneFeature(studentInfoArray, courseIndex, featureIndex);
			
			if (bestSplitForFeature != null) {
				allFeatureBestSplits.add(bestSplitForFeature);
			}
		}

		// Sort the list by variance reduction
		//So using .reversed method to sort highest to lowest as we want high variance reduction first
		allFeatureBestSplits.sort(Comparator.comparingDouble(SplitResult::getVarianceReduction).reversed());
		
		// Return the top K
		if (allFeatureBestSplits.size() > k) {  	//if top feature is more than k, just cut off until k number
			return allFeatureBestSplits.subList(0, k);
		} else {
			return allFeatureBestSplits; // if it is less or equal to k then just return 
		}
	} 

	//Basically the same method as findBestSplit, but without iterating for multiple featureIndex, only one
	public static SplitResult findBestSplitForOneFeature(StudentInfo[] studentInfoArray, int courseIndex, int oneFeatureIndex){
		// Filtering. Only use students who have a valid grade for the target course
		//System.out.println("findBestSplitForOneFeature method called!"); //debugging

        List<StudentInfo> validStudents = new ArrayList<>();
        for (StudentInfo s : studentInfoArray) {
            Double grade = getGrade(s, courseIndex);
            if (grade != null) {
                validStudents.add(s);
            }
        }

        // Need at least 2 students to make a split
        if (validStudents.size() < 2) {
			double prediction;
			if (validStudents.isEmpty()) {
				prediction = -1; // Or some global default idk I just put -1 to say we don't have a solid prediction.
			} else {
				// Size is 1
				prediction = getGrade(validStudents.get(0), courseIndex);
			}
			
			//originally I returned null there is no split, and I have null point exception and cannot predict grades for some subject.
			//so instead of returning null I return dummy prediction
			return new SplitResult(55, "No Split", 0.0, prediction, prediction);
		}
        // Calculate initial variance (Impurity)
        double parentVariance = calculateVariance(validStudents, courseIndex);
		//System.out.println("Parent Variance: "+ parentVariance);
        
        SplitResult bestSplit = null;
        double maxGain = -1.0;	//later going to compare the max gain and switch the bestSplitResult

        int featureIndex = oneFeatureIndex;
		// Skip if the feature is the target course itself
		if (featureIndex == courseIndex) return null;

		//  Get all unique values existing in the data for this feature
		Set<String> candidateThresholds = getUniqueValues(validStudents, featureIndex);
		// System.out.println("Candidate Thresholds are: "); //Debugging
		// System.out.println(candidateThresholds);

		//Iterate for every property of every attribute possible
		for (String threshold : candidateThresholds) {
			
			// Split data based on this threshold if(X then Y or if not Z)
			//left group satisfy the condition (X)
			//right group does not (Z)
			List<StudentInfo> leftGroup = new ArrayList<>();
			List<StudentInfo> rightGroup = new ArrayList<>();

			splitData(validStudents, featureIndex, threshold, leftGroup, rightGroup);

			// Skip invalid splits (empty sides) when there is no split
			if (leftGroup.isEmpty() || rightGroup.isEmpty()) continue;

			// Calculate Weighted Variance of the children
			//Weighted Variance = Sum of probability of each split * variance of each split 
			double varLeft = calculateVariance(leftGroup, courseIndex);
			double varRight = calculateVariance(rightGroup, courseIndex);
			
			double weightLeft = (double) leftGroup.size() / validStudents.size();
			double weightRight = (double) rightGroup.size() / validStudents.size();
			
			double weightedVariance = (weightLeft * varLeft) + (weightRight * varRight);
			double gain = parentVariance - weightedVariance;

			//  Check if this is the best split so far
			if (gain > maxGain) {
				maxGain = gain;
				double meanLeft = calculateMean(leftGroup, courseIndex);
				double meanRight = calculateMean(rightGroup, courseIndex);
				
				// We store the "String" version of the threshold, even for numbers
				bestSplit = new SplitResult(featureIndex, threshold, maxGain, meanLeft, meanRight);
			}
		}
        
        return bestSplit;
	}


	//I also added the calculation for graduate grades later.
	//Because current grades use StudentInfo[] type and graduate grades only use double[][] type, had to make the entire helper method separately as well :(

	// Find Top K Splits for graduate grades
	public static List<SplitResult> findTopSplitsGraduates(double[][] graduateGrades, int targetCourseIndex, int k) {
		List<SplitResult> allSplits = new ArrayList<>();

		// Iterate through all columns (0 to 35 are courses)
		// We treat every OTHER course as a potential feature to split on
		for (int featureIndex = 0; featureIndex < graduateGrades[0].length; featureIndex++) {
			
			// Don't use the course to predict itself
			if (featureIndex == targetCourseIndex) continue;

			// Find the best split using this specific course as the feature
			SplitResult result = findBestSplitGraduatesForOneFeature(graduateGrades, targetCourseIndex, featureIndex);
			
			if (result != null) {
				allSplits.add(result);
			}
		}

		// Sort by Variance Reduction (Highest gain first)
		allSplits.sort(Comparator.comparingDouble(SplitResult::getVarianceReduction).reversed());

		// Return the top K splits
		if (allSplits.size() > k) {
			return allSplits.subList(0, k);
		} else {
			return allSplits;
		}
	}

	// Find Best Split for one feature 
	public static SplitResult findBestSplitGraduatesForOneFeature(double[][] data, int targetCourseIndex, int featureIndex) {
		List<double[]> validRows = new ArrayList<>();
		
		// Filter: Use only rows that have valid grades for BOTH target and feature
		for (double[] row : data) {
			if (!Double.isNaN(row[targetCourseIndex]) && !Double.isNaN(row[featureIndex])) {
				validRows.add(row);
			}
		}

		// Need at least 2 students to split
		if (validRows.size() < 2) return null;

		// Calculate Parent Variance
		double parentVariance = calculateVarianceDoubles(validRows, targetCourseIndex);
		
		SplitResult bestSplit = null;
		double maxGain = -1.0;

		// Get unique values to test as thresholds like (6.0, 7.0, 8.0)
		Set<String> thresholds = getUniqueValuesDoubles(validRows, featureIndex);

		for (String threshold : thresholds) {
			double numThreshold = Double.parseDouble(threshold);
			
			List<double[]> left = new ArrayList<>();
			List<double[]> right = new ArrayList<>();

			// Split the data
			for (double[] row : validRows) {
				if (row[featureIndex] >= numThreshold) {
					left.add(row);
				} else {
					right.add(row);
				}
			}

			if (left.isEmpty() || right.isEmpty()) continue;

			// Calculate Weighted Variance
			double varLeft = calculateVarianceDoubles(left, targetCourseIndex);
			double varRight = calculateVarianceDoubles(right, targetCourseIndex);
			
			double wLeft = (double) left.size() / validRows.size();
			double wRight = (double) right.size() / validRows.size();
			
			double gain = parentVariance - ((wLeft * varLeft) + (wRight * varRight));

			// Check if this is the best one
			if (gain > maxGain) {
				maxGain = gain;
				double meanLeft = calculateMeanDoubles(left, targetCourseIndex);
				double meanRight = calculateMeanDoubles(right, targetCourseIndex);
				
				// featureIndex here is only course (from 0-35), it doesn't include studentInfo attribute
				bestSplit = new SplitResult(featureIndex, threshold, maxGain, meanLeft, meanRight);
			}
		}
		return bestSplit;
	}

	private static Set<String> getUniqueValuesDoubles(List<double[]> rows, int columnIndex) {
		Set<String> values = new HashSet<>();
		for (double[] row : rows) {
			values.add(String.valueOf(row[columnIndex]));
		}
		return values;
	}

	private static double calculateMeanDoubles(List<double[]> rows, int columnIndex) {
		if (rows.isEmpty()) return 0.0;
		double sum = 0.0;
		int count = 0;
		for (double[] row : rows) {
			sum += row[columnIndex];
			count++;
		}
		return count == 0 ? 0.0 : sum / count;
	}

	private static double calculateVarianceDoubles(List<double[]> rows, int columnIndex) {
		if (rows.isEmpty()) return 0.0;
		double mean = calculateMeanDoubles(rows, columnIndex);
		double sumSq = 0.0;
		int count = 0;
		for (double[] row : rows) {
			sumSq += Math.pow(row[columnIndex] - mean, 2);
			count++;
		}
		return count == 0 ? 0.0 : sumSq / count;
	}

    /****************************************** Predict using the splits! (and maybe comapre) *************/
	
	//predict grades using single decision stump for one student for one subject
	//the decisionStump parameter has to come from findBestSplit method.
	public static double predictWithOneDeicisionStump(SplitResult decisionStump,StudentInfo student){
		
		if(decisionStump == null){
			System.out.println("The Decision stump is empty!");
		}

		//check if the student match the 
		boolean isMatch = checkCondition(student, decisionStump.getFeatureIndex(), decisionStump.getThresholdValue());

		return isMatch? decisionStump.getLeftMean() : decisionStump.getRightMean();
	}


	//predict grades using the forest of stumps for one student for one subject
	//this method can be called after finding the forest using findTopSplit method with a specific course Index
	public static double predictWithForestOfStumps(List<SplitResult> forest, StudentInfo student) {
		
		if (forest == null || forest.isEmpty()){ //just in case for null value or empty forest
			System.out.println("Forest is Empty!");
			return 0.0; 
		}
	
		double totalPrediction = 0.0;
		
		// Iterate every stump in the forest for the split and find the average of average grade per split
		for (SplitResult stump : forest) {
			
			// check if this student match the condition (if match then left not then right)
			boolean matchCondition = checkCondition(student, stump.getFeatureIndex(), stump.getThresholdValue());
			
			if (matchCondition) {
				totalPrediction += stump.getLeftMean();
			} else {
				totalPrediction += stump.getRightMean();
			}
		}
		
		// Return the average of all predictions
		return totalPrediction / forest.size();
	}
	
	//Checking if a student matches the provided attribute property or not
	private static boolean checkCondition(StudentInfo s, int featureIndex, String threshold) {
		
		// Get feature value that splits (e.g. "QCT = Stable", course>=7.0 smt like this)
		Object value = getFeatureValue(s, featureIndex); 
		
		// If data is missing for this feature check, set default to go right(return false)
		// ex) some students have no grades for comparing feature courses (NG values)
		if (value == null) return false; 
	
		// Find if feature is numeric or nominal
		boolean isNumeric = isFeatureNumeric(featureIndex); 
	
		if (isNumeric) {
			try {
				double numThreshold = Double.parseDouble(threshold);
				double numValue = ((Number) value).doubleValue(); // works for Double or Integer
				
				return numValue >= numThreshold; // if true then it goes to left
				
			} catch (NumberFormatException e) {
				return false; 
			}
		} else {
			String strValue = value.toString();
			
			return strValue.equals(threshold); 
		}
	}

}