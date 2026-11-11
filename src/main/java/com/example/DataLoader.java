package com.example;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DataLoader {
    //Trying to save all the CSV files under resources folder for maven export
    private static final String RESOURCE_PATH = "/";

    // Reads a CSV file of grades into a double[][] matrix
	// Skips the first column (Student ID) and the header row.
	public static double[][] readGradesCSV(String fileName) {
        List<double[]> dataList = new ArrayList<>();
        int colCount = 0;

        // Use BufferedReader which is much faster than Scanner
        try (InputStream is = DataLoader.class.getResourceAsStream(RESOURCE_PATH + fileName);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            
            if (is == null) {
                System.out.println("FILE NOT FOUND at " + RESOURCE_PATH + fileName);
                return new double[0][0];
            }

            String line;

            // Read Header
            if ((line = br.readLine()) != null) {
                colCount = line.split(",").length - 1; // subtract 1 for StudentID
            }

            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                
                // Create row array (skipping index 0 which is StudentID)
                double[] rowData = new double[colCount];

                for (int i = 1; i < tokens.length; i++) {
                    try {
                        // Safety check: ensure token exists
                        if (i >= tokens.length) {
                             rowData[i-1] = Double.NaN;
                             continue;
                        }

                        String val = tokens[i].trim();
                        if (val.equals("NG") || val.isEmpty()) {
                            //I'm doing it just for the error just in case
                            //but graduategrades.csv has no NG anyway in reality, only currentgrades.csv
                            rowData[i-1] = Double.NaN;
                        } else {
                            rowData[i-1] = Double.parseDouble(val);
                        }
                    } catch (NumberFormatException e) {
                        rowData[i-1] = Double.NaN;
                    }
                }
                dataList.add(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new double[0][0];
        }

        // Convert List to Array
        return dataList.toArray(new double[dataList.size()][]);
    }

    //Phase1 Step 3 Reading studentInfo CSV 
    public static StudentInfo[] readStudentInfoCSV(String fileName , double[][]grades) {
        StudentInfo[] studentArray = null;

        try (InputStream is = DataLoader.class.getResourceAsStream(RESOURCE_PATH + fileName);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            
            if (is == null) {
                System.out.println("FILE NOT FOUND at " + RESOURCE_PATH + fileName);
                return new StudentInfo[0];
            }

            //creating an arraylist for convinience
            List<StudentInfo> tempList = new ArrayList<>();
            String line;

            // Skip header
            if ((line = br.readLine()) != null) {
                // Header skipped
            }

            int index = 0;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                if (values.length < 6) continue;

                String studentID = values[0].trim();
                String qct = values[1].trim();
                String snc = values[2].trim();
                String atdr = values[3].trim();
                String pit = values[4].trim();
                String blt = values[5].trim();

                // Check boundary for grades array to avoid crashes if files don't match length
                double[] studentGrades = (index < grades.length) ? grades[index] : new double[0];

                tempList.add(new StudentInfo(studentID, qct, snc, atdr, pit, blt, studentGrades));
                index++;
            }
            
            studentArray = tempList.toArray(new StudentInfo[0]);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return studentArray;
    }

    // Merge Graduate + Current Grades (Filtered for Target Course)
    public static List<double[]> combineAndFilterData(double[][] graduateGrades, double[][] currentGrades, int targetCourseIndex) {
        List<double[]> combined = new ArrayList<>();

        // Add Graduates if they have a valid grade
        for (double[] row : graduateGrades) {
            if (!Double.isNaN(row[targetCourseIndex])) {
                combined.add(row);
            }
        }
        // Add Current Students if they have a valid grade
        for (double[] row : currentGrades) {
            if (!Double.isNaN(row[targetCourseIndex])) {
                combined.add(row);
            }
        }
        return combined;
    }

    // Split for Graduate Grades (List<double[]>)
    public static List<List<double[]>> splitGraduateData(List<double[]> data) {
        // Create copy and shuffle
        List<double[]> shuffled = new ArrayList<>(data);
        Collections.shuffle(shuffled, new Random(12345)); 

        int totalSize = shuffled.size();
        
        // Split 1: 67% TrainingTotal, 33% Test
        int split1 = (int) (totalSize * (2.0 / 3.0));
        List<double[]> trainingTotal = shuffled.subList(0, split1);
        List<double[]> testSet = shuffled.subList(split1, totalSize);

        // Split 2: From TrainingTotal, take 66% InnerTrain, 33% Validation
        int split2 = (int) (trainingTotal.size() * (2.0/3.0));
        List<double[]> innerTrain = trainingTotal.subList(0, split2);
        List<double[]> validation = trainingTotal.subList(split2, trainingTotal.size());

        List<List<double[]>> result = new ArrayList<>();
        result.add(innerTrain); // Index 0
        result.add(validation); // Index 1
        result.add(testSet);    // Index 2
        return result;
    }

    // Split for StudentInfo (List<StudentInfo>)
    public static List<List<StudentInfo>> splitStudentData(List<StudentInfo> data) {
        // Create copy and shuffle
        List<StudentInfo> shuffled = new ArrayList<>(data);
        Collections.shuffle(shuffled, new Random(12345)); 

        int totalSize = shuffled.size();

        // Split 1: 67% TrainingTotal, 33% Test
        int split1 = (int) (totalSize * (2.0 / 3.0));
        List<StudentInfo> trainingTotal = shuffled.subList(0, split1);
        List<StudentInfo> testSet = shuffled.subList(split1, totalSize);

        // Split 2: From TrainingTotal, take 70% InnerTrain, 30% Validation
        int split2 = (int) (trainingTotal.size() * (2.0 / 3.0));
        List<StudentInfo> innerTrain = trainingTotal.subList(0, split2);
        List<StudentInfo> validation = trainingTotal.subList(split2, trainingTotal.size());

        List<List<StudentInfo>> result = new ArrayList<>();
        result.add(innerTrain); // Index 0
        result.add(validation); // Index 1
        result.add(testSet);    // Index 2
        return result;
    }

    //This technically is not loading, but giving studentID from given row
    //Not using it now, but could be useful later for the final prediction 
    public static HashMap<Integer, String> mapIndexToID(StudentInfo[] studentArray) { // a method that maps index of array with studentID
		HashMap<Integer, String> indexToID = new HashMap<>();

		for (int i = 0; i < studentArray.length; i++) {
			indexToID.put(i, studentArray[i].studentID);
		}

        return indexToID;	
	}
}