package com.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;

import java.util.Scanner;
import java.io.File;

public class FileDisplayer {
    
    // Lists of courses from the csv files
	// just manually added instead of reading csv file because I also want to see the lists of courses visually
	// whoever sees this can change the code to read courses from the csv file(I agree that's the better way)
    public static String[] courses = {
        "Cryogenic Physics", "Evolutionary Dynamics", "Dark Energy", "Stellar Chemistry",
        "Sublight Propulsion Systems", "Transdimensional Navigation", "Neural Computation",
        "Plasma Physics", "Crystalline Geology", "Pulsar Spectroscopy", "Propulsion Systems",
        "Radiation Shielding", "Atmospheric Chemistry", "Warp Field Theory", "Plasmawave Analysis",
        "Exoplanet Climatology", "Astrology", "Magnetosphere Modeling", "Graviton Dynamics",
        "Xyloprax Computation", "Mineral Extraction", "Synthetic Atmospheres", "Quantum Fluids",
        "Nanostructures Fabrication", "Photonic Computing", "Orbital Mechanics", "Neutrino Engineering",
        "Astrogeophysics", "Genomic Engineering", "Particle Isolation", "Nuclear Synthesis",
        "Xenorobotics Systems", "Cartographic Topologies", "Exotic Matter", "Plasma Containment",
        "Bioluminal Signals"
    };

    public static void main(String[] args) {
        try {

            //Load Datas from all the CSVs.
            double[][] graduateGrades = DataLoader.readGradesCSV("GraduateGrades.csv");
            double[][] currentDoubleGrades = DataLoader.readGradesCSV("CurrentGrades.csv"); 
            StudentInfo[] currentGrades = DataLoader.readStudentInfoCSV("StudentInfo.csv", currentDoubleGrades);
            System.out.println("Datas are Loaded!");

            //Changing datatypes for buildTree parameters
            List<double[]> gradList = new ArrayList<>();
            for (double[] row : graduateGrades) {
                 gradList.add(row);
            }

            List<StudentInfo> studentList = new ArrayList<>(Arrays.asList(currentGrades));

            Predictor predictor = new Predictor();
    
            //--- Testing if trees are working fine---
            //TreeNode subjectTree = predictor.buildGraduateTree(gradList, 0, 0);
            TreeNode infoTree = predictor.buildStudentInfoTree(studentList, 0, 0);
            //predictor.printTree(infoTree);
            predictor.postprune(infoTree, studentList.subList(0, 200), 0,0);
            predictor.printTree(infoTree);
            System.out.println("--------------Line break------------------------");
            //predictor.printTree(subjectTree);
            //TreeNode infoTree = predictor.buildStudentInfoTree(studentList, 12, 0);
            //predictor.printTree(infoTree);
            //TreeNode subjectTree = predictor.buildGraduateTree(gradList, 12, 0);
            //predictor.printTree(subjectTree);
            Scanner scanner = new Scanner(System.in);

            //this method uses the top 5 decisionStumps
            //predictWithUserInput(scanner, predictor, gradList, studentList, currentGrades);

            //findOptimalRandomForestSize(scanner, predictor, gradList, studentList, currentDoubleGrades);

            //this method uses randomForest
            finalPredictWithUserInput(scanner, predictor, gradList, studentList, currentGrades, currentDoubleGrades);

            scanner.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Ask users how many predictions with which studentID and which subject
	// and if any prediction exists, write it out to CSV
    public static void predictWithUserInput(Scanner scanner, Predictor predictor, List<double[]> gradList, List<StudentInfo> studentList, StudentInfo[] currentGrades)
     {
        
        System.out.println("Prediction with Decision Trees!");
        System.out.print("How many predictions do you want to make? ");
        
        int numPredictions = 0;
        try {
            numPredictions = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number!! Only enter Integer please^^");
            return;
        }

        List<String[]> resultsToSave = new ArrayList<>();

        for (int k = 0; k < numPredictions; k++) {
            System.out.println("\n--- Prediction " + (k + 1) + " of " + numPredictions + " ---");

            //Get input for studentID
            System.out.print("Enter Student Number (e.g. 310433): ");
            String targetID = scanner.nextLine().trim();

			//Get input for course
            System.out.print("Enter Target Course Name (e.g. Dark Energy): ");
            String targetSubject = scanner.nextLine().trim();

            //Find StudentID exists or not
            StudentInfo targetStudent = findStudentByID(currentGrades, targetID);
            if (targetStudent == null) {
                System.out.println("Student ID " + targetID + " not found.");
                continue;
            }

            //Find course 
            int courseIndex = findCourseIndex(targetSubject);
            if (courseIndex == -1) {
                System.out.println(" Course: " + targetSubject + " not found. Skipping.");
                continue;
            }

            System.out.println("Building Decision Trees for " + targetSubject + "!");
            TreeNode gradTree = predictor.buildGraduateTree(gradList, courseIndex, 0);
            TreeNode infoTree = predictor.buildStudentInfoTree(studentList, courseIndex, 0);

            System.out.println("Choose prediction source: ");
            System.out.println("1. Decision Tree (Use Graduate Grades)");
            System.out.println("2. Decision Tree (Use Student Info)");
            System.out.println("3. Random Forest");
            System.out.println("(Choose 1-3)");

            String choice = scanner.nextLine().trim();
            double prediction = 0.0;

            //Using predictRecursive method in predictor.java
            String methodName = "";
            if (choice.equals("1")) {
                prediction = predictor.predictRecursive(gradTree, targetStudent);

            }
            else if (choice.equals("2")) {
                prediction = predictor.predictRecursive(infoTree, targetStudent);
            }
            else if (choice.equals("3")) {
                prediction = predictor.predictRandomForest(gradList,
                    studentList, 
                    targetStudent,
                    courseIndex, 
                    5);
            }
            else {
                System.out.println("Invalid choice, using default graduate grades.");
                prediction = predictor.predictRecursive(gradTree, targetStudent);
                methodName = "Decision Tree (Subject)";
            }

			//This format %.4f can change depending on the requirement. Probably up to 1 decimal or 0 or round up?
            System.out.printf("Done. Prediction: %.1f%n", prediction);

            resultsToSave.add(new String[]{targetID, targetSubject, String.format("%.1f", prediction), methodName});
        }

        //Write out to csv if anything exists in the array
        if (!resultsToSave.isEmpty()) {
            writeResultsToCSV(resultsToSave, "PredictionsOutput.csv");
        } else {
            System.out.println("\nNo valid predictions were made to save.");
        }
    }


    private static StudentInfo findStudentByID(StudentInfo[] students, String id) {
        for (StudentInfo s : students) {
            if (s.studentID.equals(id)) return s;
        }
        return null;
    }

    private static int findCourseIndex(String subjectName) {
        for (int i = 0; i < courses.length; i++) {
            if (courses[i].equalsIgnoreCase(subjectName)) return i;
        }
        return -1;
    }

    private static void writeResultsToCSV(List<String[]> data, String filename) {
        //making a different file for each prediction
        String baseName = filename.replace(".csv", "");
        String uniqueFileName = filename;
        int counter = 1;
        File file = new File(uniqueFileName);

        while (file.exists()){
            uniqueFileName = baseName + "_" + counter + ".csv";
            file = new File(uniqueFileName);
            counter++;
        }
        System.out.println("\nWriting results to " + uniqueFileName + "...");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(uniqueFileName))) {
            writer.write("Student Number,Subject,Prediction Value,Method");
            writer.newLine();

            for (String[] row : data) {
                writer.write(row[0] + "," + row[1] + "," + row[2] + "," + row[3]);
                writer.newLine();
            }
            System.out.println("Success! File created. " + "(" + uniqueFileName + ")");
        } catch (IOException e) {
            System.out.println("Couldn't write out the CSV files for prediction!: " + e.getMessage());
        }
    }

    // The final prediction method using userinput
    public static void finalPredictWithUserInput(Scanner scanner, 
        Predictor predictor,  
        List<double[]> graduateList,
        List<StudentInfo> studentInfoList, 
        StudentInfo[] currentGradesArray, 
        double[][] currentGradesDouble) { 

        System.out.println("Prediction with Data Merging & Pruning!");      
        
        //loop for number of predictions
        int numPredictions = 0;
        while (true) {
            System.out.print("How many predictions do you want to make? ");
        try {
            String input = scanner.nextLine();
            numPredictions = Integer.parseInt(input);
            if (numPredictions > 0) break;
            else System.out.println("Please enter a number larger than 0");
        } catch (NumberFormatException e) {
            System.out.println("Invalid number!");
} 
       }
    

        List<String[]> resultsToSave = new ArrayList<>();

        for (int k = 0; k < numPredictions; k++) {
            System.out.println("\n--- Prediction " + (k + 1) + " of " + numPredictions + " ---");

            //loop for student ID 
            StudentInfo targetStudent = null;
            String targetID = "";
            while (targetStudent == null) {
                System.out.print("Enter Student Number (e.g. 310433):");
                targetID = scanner.nextLine().trim();

                targetStudent = findStudentByID(currentGradesArray, targetID);

                if (targetStudent == null) {
                    System.out.println("Student ID " + targetID + " not found. Please try again." );
                }
            }

            //loop for course name
            int courseIndex = -1;
            String targetSubject = "";
            while (courseIndex == -1) {
                System.out.println("Enter Target Course Name (e.g. Dark Energy): ");
                targetSubject = scanner.nextLine().trim();

                courseIndex = findCourseIndex(targetSubject);

                if (courseIndex == -1) {
                    System.out.println("Course " + targetSubject + " not found. Please try again.");
                }
            }

            System.out.println("Found student: " + targetID);
            System.out.println("Found course: " + targetSubject);
            System.out.println("Preparing data...");

        

            // Merging the data from graduate grades + 
            List<double[]> mergedGrades = DataLoader.combineAndFilterData(
                graduateList.toArray(new double[0][]), 
                currentGradesDouble, 
                courseIndex
            );

            // From the merged data of currentgrades+studentInfo, only keeping students with no NG for this course
            List<StudentInfo> filteredStudentInfo = new ArrayList<>();
            for (StudentInfo s : studentInfoList) {
                if (!Double.isNaN(s.studentGrades[courseIndex])) {
                    filteredStudentInfo.add(s);
                }
            }

            // Splitting datasets into 2/3 of training and 1/3 of test data
            //then 2/3 of training data from training data and 1/3 of validation set from training data(outer)
            List<List<double[]>> gradeSplits = DataLoader.splitGraduateData(mergedGrades);
            List<double[]> gradTrain = gradeSplits.get(0); // Inner Train 
            List<double[]> gradVal   = gradeSplits.get(1); // Validation 
            // List<double[]> gradTest  = gradeSplits.get(2); // Test Set (33%) - unused for prediction

            // Same split for studentInfo data 
            List<List<StudentInfo>> infoSplits = DataLoader.splitStudentData(filteredStudentInfo);
            List<StudentInfo> infoTrain = infoSplits.get(0);
            List<StudentInfo> infoVal   = infoSplits.get(1);

            System.out.println("Building and Pruning Trees!");

            // building a graduate tree and post-pruning
            TreeNode gradTree = predictor.buildGraduateTree(gradTrain, courseIndex, 0);
            //predictor.postpruneSubject(gradTree, gradVal, courseIndex,0);

            // building a studentInfo tree and post-pruning
            TreeNode infoTree = predictor.buildStudentInfoTree(infoTrain, courseIndex, 0);
            //predictor.postprune(infoTree, infoVal, courseIndex,0);

            String choice = "";
            while(true) {
            System.out.println("Choose prediction source: ");
            System.out.println("1. Decision Tree (subject Tree)");
            System.out.println("2. Decision Tree (info Tree)");
            System.out.println("3. Random Forest (Merged Data)");

            choice = scanner.nextLine().trim();

            if (choice.equals("1") || choice.equals("2") || choice.equals("3")) {
                break;
            }
            else {
                System.out.println("Invalid option, please choose 1, 2, or 3");
            }
        }
            double prediction = 0.0;
            String methodName = "";

            if (choice.equals("1")) {
                prediction = predictor.predictRecursive(gradTree, targetStudent.studentGrades);
                methodName = "Decision Tree (Subject)";
            } else if (choice.equals("2")) {
                prediction = predictor.predictRecursive(infoTree, targetStudent);
                methodName = "Decision Tree (Student Info)";
            } else if (choice.equals("3")) {
                // Passes merged data to Random Forest
                prediction = predictor.predictRandomForest(mergedGrades,
                    filteredStudentInfo, 
                    targetStudent, 
                    courseIndex, 
                    25);
                    methodName = "Random Forest";
            } else {
                prediction = predictor.predictRecursive(gradTree, targetStudent.studentGrades);
            }

            System.out.printf("Done. Prediction: %f%n", prediction);
            resultsToSave.add(new String[]{targetID, targetSubject, String.format("%f", prediction), methodName});
        }

        if (!resultsToSave.isEmpty()) {
            writeResultsToCSV(resultsToSave, "PredictionsOutput.csv");
        }
        
    }

    public static void findOptimalRandomForestSize(Scanner scanner, Predictor predictor, 
                                               List<double[]> graduateList, 
                                               List<StudentInfo> studentInfoList, 
                                               double[][] currentGradesDouble) {
    
		System.out.println("\n--- Random Forest Optimization (Hyperparameter Tuning) ---");
		System.out.print("Enter the target course(e.g. Dark Energy) ");
		String targetSubject = scanner.nextLine().trim();

		int courseIndex = findCourseIndex(targetSubject);
		if (courseIndex == -1) {
			System.out.println("Course not found!! Type Properly!");
			return;
		}

		//Same order
        //1. preparing data, 2.filtering students with no NG, splitting into training, validating, testing
		List<double[]> mergedGrades = DataLoader.combineAndFilterData(
			graduateList.toArray(new double[0][]), 
			currentGradesDouble, 
			courseIndex
		);

		List<StudentInfo> filteredStudentInfo = new ArrayList<>();
		for (StudentInfo s : studentInfoList) {
			if (!Double.isNaN(s.studentGrades[courseIndex])) {
				filteredStudentInfo.add(s);
			}
		}

		// Split into Train and Validation
		List<List<double[]>> gradeSplits = DataLoader.splitGraduateData(mergedGrades);
		List<double[]> gradTrain = gradeSplits.get(0); //inner train
		//List<double[]> gradVal   = gradeSplits.get(1); //validation

		List<List<StudentInfo>> infoSplits = DataLoader.splitStudentData(filteredStudentInfo);
		List<StudentInfo> infoTrain = infoSplits.get(0);
		List<StudentInfo> infoVal   = infoSplits.get(1);

        
        // 3. PARALLEL EXECUTION
        // This runs the heavy calculation for k=1, k=5, k=10 etc. at the same time
        List<Integer> treeCounts = Arrays.asList(1, 5, 10, 20,35, 50,70,80,90, 100); 
        
        System.out.println("Testing tree counts in PARALLEL: " + treeCounts + "...");
        System.out.printf("%-15s %-15s%n", "Trees (k)", "MSE (Error)");
        System.out.println("-------------------------------");

        // 3. PARALLEL EXECUTION
        // This runs the heavy calculation for k=1, k=5, k=10 etc. at the same time
        treeCounts.parallelStream().forEach(k -> {
            
            double totalSquaredError = 0.0;
            int predictionCount = 0;

            // Use the Sampling Trick (Test only 50 random students)
            List<StudentInfo> validationSample = new ArrayList<>(infoVal);
            // Note: Shuffle needs to be synchronized or local if running inside parallel stream, 
            // but since we read-only, we can just take the first 50 if they were shuffled before.
            // For simplicity, let's just take a sublist (shuffling once before the stream is safer).
            int sampleSize = Math.min(validationSample.size(), 50); 

            for (int i = 0; i < sampleSize; i++) {
                StudentInfo valStudent = validationSample.get(i);
                double actual = valStudent.studentGrades[courseIndex];
                
                double predicted = predictor.predictRandomForest(
                    gradTrain,   
                    infoTrain,   
                    valStudent, 
                    courseIndex, 
                    k
                );

                double error = actual - predicted;
                totalSquaredError += Math.pow(error, 2);
                predictionCount++;
            }

            double mse = (predictionCount == 0) ? 0.0 : totalSquaredError / predictionCount;
            
            // Print immediately (Order might be mixed up, e.g. k=20 finishes before k=5)
            // Using synchronized block to prevent text scrambling
            synchronized(System.out) {
                System.out.printf("%-15d %-15.4f%n", k, mse);
            }
        });
		System.out.println("-------------------------------");
		
	}
}