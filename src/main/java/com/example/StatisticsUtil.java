package com.example;

import java.util.ArrayList;

//Reorganized as oop as the code gets longer

//This class has all the methods created from step1 and step2
public class StatisticsUtil {
    //Step 1
	public static double computeCourseMean(double[][] grades, int courseIndex){
        double sum = 0.0;
		int count = 0;

       for(int row = 0; row < grades.length; row++) {
		if(Double.isNaN(grades[row][courseIndex]))continue;	//skip if NaN value included

           sum = sum + grades[row][courseIndex];
		   count++;
       }
       double mean = sum / count;
       return mean;
    }


    public static double computeStudentMean(double[][] grades, int studentIndex){
        double sum = 0.0;
		int count = 0;
        for(int column = 0; column < grades[0].length; column++) {
			if(Double.isNaN(grades[studentIndex][column])) continue; //skip NaN values
                sum = sum + grades[studentIndex][column];
				count++;
        }
        double mean = sum / count;
        return mean;
    }
	
	public static double computeCourseStdDev(double[][] grades, int courseIndex) {
		int rows = grades.length;
		int cols = grades[0].length;
 
		double[] rowStd = new double[rows];
		double[] colStd = new double[cols];
 
		for (int i = 0; i < rows; i++) {
			double sum = 0;
			int validCount = 0;
		   
			for (int j = 0; j < cols; j++) {
				if (Double.isNaN(grades[i][j])) {
					System.out.println("Compute Course STDDEV, NaN found in row " + (i + 1));
				} else {
					sum += grades[i][j];
					validCount++;
				}
			}
 
			if (validCount > 0) {
				double mean = sum / validCount;
				double sumSq = 0;
 
				for (int j = 0; j < cols; j++) {
					if (!Double.isNaN(grades[i][j])) {
						sumSq += Math.pow(grades[i][j] - mean, 2);  //Math.pow = method to raise to the square
					}
				}
				rowStd[i] = Math.sqrt(sumSq / validCount);
			} else {
				rowStd[i] = Double.NaN; // if the whole line is invalid
			}
		}
 
		for (int j = 0; j < cols; j++) {
			double sum = 0;
			int validCount = 0;
 
			for (int i = 0; i < rows; i++) {
				if (Double.isNaN(grades[i][j])) {
					System.out.println("Computing course STDDEV, NaN found in column " + (j + 1));
				} else {
					sum += grades[i][j];
					validCount++;
				}
			}

			if (validCount > 0) {
				double mean = sum / validCount;
				double sumSq = 0;
 
				for (int i = 0; i < rows; i++) {
					if (!Double.isNaN(grades[i][j])) {
						sumSq += Math.pow(grades[i][j] - mean, 2);
					}
				}
				colStd[j] = Math.sqrt(sumSq / validCount); // Math.sqrt = method to calculate the square root
			} else {
				colStd[j] = Double.NaN;
			}
		}

		System.out.println("Standard Deviation for course (column) " + courseIndex + ": " + colStd[courseIndex]+ " and standard Deviation for rows : " + rowStd[courseIndex]);
		return colStd[courseIndex];
		
	}
 
	public static void StandardDeviation(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
 
 
        // Tableaux pour stocker les écarts-types
        double[] rowStd = new double[rows];
        double[] colStd = new double[cols];
 
 
        // standart deviation line
        for (int i = 0; i < rows; i++) {
            double sum = 0;
            for (int j = 0; j < cols; j++) {
                sum += matrix[i][j];
            }
            double mean = sum / cols;
 
 
            double sumSq = 0;
            for (int j = 0; j < cols; j++) {
                sumSq += Math.pow(matrix[i][j] - mean, 2); // Math.pow = method to elevate square
            }
            rowStd[i] = Math.sqrt(sumSq / cols);  // Math.sqrt = method to get the square root
        }
 
 
        // standart deviation colums
        for (int j = 0; j < cols; j++) {
            double sum = 0;
            for (int i = 0; i < rows; i++) {
                sum += matrix[i][j];
            }
            double mean = sum / rows;
 
 
            double sumSq = 0;
            for (int i = 0; i < rows; i++) {
                sumSq += Math.pow(matrix[i][j] - mean, 2);
            }
            colStd[j] = Math.sqrt(sumSq / rows);
        }
 
 
        // --- AFFICHAGE ---
        System.out.println("Standart deviation per line :");
        for (int i = 0; i < rows; i++) {
            System.out.println("Line" + (i + 1) + " : " + rowStd[i]);
        }
 
 
        System.out.println("\nStandart deviation per colum :");
        for (int j = 0; j < cols; j++) {
            System.out.println("Colum " + (j + 1) + " : " + colStd[j]);
        }
    }
	public static double computeCourseMin(double[][] grades, int courseIndex) {
        double num = 10;
        for (int i = 0; i < grades.length; i++) {
            double value = grades[i][courseIndex];
            if (!Double.isNaN(value) && value < num) {
                num = value;
            }
        }
        return num;
    }
 
    public static double computeCourseMax(double[][] grades, int courseIndex) {
        double num = 0;
        for (int i = 0; i < grades.length; i++) {
            double value = grades[i][courseIndex];
            if (!Double.isNaN(value) && value > num) {
                num = value;
            }
        }
        return num;
    }
 
 
    public static double computeStudentMin(double[][] grades, int studentIndex) {
        double num = 10;
        for (int j = 0; j < grades[studentIndex].length; j++) {
            double value = grades[studentIndex][j];
            if (!Double.isNaN(value) && value < num) {
                num = value;
            }
        }
        return num;
    }
 
 
    public static double computeStudentMax(double[][] grades, int studentIndex) {
        double num = 0;
        for (int j = 0; j < grades[studentIndex].length; j++) {
            double value = grades[studentIndex][j];
            if (!Double.isNaN(value) && value > num) {
                num = value;
            }
        }
        return num;
    }

	//finding related courses
	public static void relatedCourses(double[][] array) {
		int sum = 0;
		
		// create groups according to the average of grades
		ArrayList<String> group1 = new ArrayList<>();
		ArrayList<String> group2 = new ArrayList<>();
		ArrayList<String> group3 = new ArrayList<>();
		ArrayList<String> group4 = new ArrayList<>();
		ArrayList<String> group5 = new ArrayList<>();
 
 
		//go through columns
		for (int column = 0; column < array[0].length; column++) {
			//go through rows
			for (int row = 0; row < array.length; row++) {
				sum = sum + (int)array[row][column];
			}
			int average = sum / array.length;
			//add courses to groups according to averages
			if (average >= 9) {
				group1.add(FileDisplayer.courses[column]);
			} else if (average >= 8) {
				group2.add(FileDisplayer.courses[column]);
			} else if (average >= 7) {
				group3.add(FileDisplayer.courses[column]);
			} else if (average >= 6) {
				group4.add(FileDisplayer.courses[column]);
			} else {
				group5.add(FileDisplayer.courses[column]);
			}
			sum = 0;
		}
		System.out.println("Group 1 (Avg >= 9): " + group1);
		System.out.println("Group 2 (Avg >= 8): " + group2);
		System.out.println("Group 3 (Avg >= 7): " + group3);
		System.out.println("Group 4 (Avg >= 6): " + group4);
		System.out.println("Group 5 (Avg < 6): " + group5);
	}
 
	public static double computeCourseVariance(StudentInfo[] student, int courseIndex){
		double sum = 0.0;
		int count = 0;
		double sumVariance = 0.0;
		for (int row = 0; row < student[0].studentGrades.length; row++) {
			if(Double.isNaN(student[row].studentGrades[courseIndex])) {
				continue;   //skip if NaN value included
			}
			sum = sum + student[row].studentGrades[courseIndex];
			count++;
		}
		double mean = sum / count;
 
 
		for (int row = 0; row < student[0].studentGrades.length; row++) {
			if(Double.isNaN(student[row].studentGrades[courseIndex])) {
				continue;   //skip if NaN value included
			}
			double x = student[row].studentGrades[courseIndex] - mean;
			x = x * x;
			sumVariance = sumVariance + x;
		   
		}
		double variance = sumVariance / count;
		return variance;
	}
 
 
	public static double computeStudentVariance(double[][] grades, int studentIndex){
		double sum = 0.0;
		int count = 0;
		double sumVariance = 0.0;
		for(int column = 0; column < grades[0].length; column++) {
			if(Double.isNaN(grades[studentIndex][column])) {
			continue; //skip NaN values
			}
			sum = sum + grades[studentIndex][column];
			count++;
		}
		double mean = sum / count;
 
 
		for (int column = 0; column < grades[0].length; column++) {
			if(Double.isNaN(grades[studentIndex][column])) {
			continue; //skip NaN values
			}
			double x = grades[studentIndex][column] - mean;
			x = x * x;
			sumVariance = sumVariance + x;
		}
		double variance = sumVariance / count;
		return variance;
	}

	public static void findRangeOfCourses(double[][] grades) {
		int numCourses = grades[0].length;
 
 
		for (int i = 0; i < numCourses; i++) {
			   System.out.print(FileDisplayer.courses[i] + ": " + (computeCourseMax(grades,i)-computeCourseMin(grades,i)));
			 System.out.println();
		}
	}
 
	//Step 2 Figure out which people will be graduating soon?
	public static int countMissingForStudent (double[][] grades, int studentIndex) {
        int counter = 0;
        for (int column = 0; column < grades[0].length; column++) {
            if (Double.isNaN(grades[studentIndex][column])) {
                counter++;
            }
        }
        return counter;
    }

	public static void areCoursesOrdered(double[][] grades) {
       int numCourses = grades[0].length;


       for (int i = 0; i < numCourses; i++) {
      		System.out.print(FileDisplayer.courses[i] + ": " + countMissingForCourse(grades, i));
			System.out.println();
	   }

   	}
 
 
    public static int countMissingForCourse (double[][] grades, int courseIndex) {
        int counter = 0;
        for (int row = 0; row < grades.length; row++) {
            if (Double.isNaN(grades[row][courseIndex])) {
                counter++;
            }
        }
        return counter;
    }
	

   public static void gradeFinderForStudents(double[][] grades, int studentId){
		System.out.println("Which students are going to graduate soon?");
		int rows = grades.length;
		int cols = grades[0].length;
		for (int i = 0; i < rows; i++) {    //search the NaN in the array
			studentId = (int) grades[i][0];
			int NaNCount = 0;
			for (int j = 1; j < cols; j++) {
				if (Double.isNaN(grades[i][j])) {
					NaNCount++; //if found a NaN, increase the count
				}
			}
			if (NaNCount <= 1) { //if there are equal or less than one NaN, the student can graduate
				System.out.println("Student " + studentId + " is likely to graduate soon.");
			}
		}
	}



	//create a method to see how many students are eligible to graduate this year
	public static int numberOfStudentsEligibleToGraduate (double[][] grades) {
		//find the min value of NGs per student
		int min = grades[0].length;
		for (int row = 0; row < grades.length; row++) {
			int counter = 0;
			for (int column = 0; column < grades[0].length; column++) {
				if (Double.isNaN(grades[row][column])) {
					counter++;
				}
			}
			if (counter < min) {
				min = counter;
			}
		}
		//count students who have min amount of NGs and their grades are higher or equal to 6.0
		int eligible = 0;
		for (int studentIndex = 0; studentIndex < grades.length; studentIndex++) {
			if ((countMissingForStudent(grades, studentIndex) == min && allPassingGrades(grades, studentIndex))) {
				eligible++;
			}
		}
		return eligible;	
	}

	//boolean method to check if all of the grades for a student are higher or equal to 6.0
	public static boolean allPassingGrades (double[][] grades, int studentIndex) {
		for (int column = 0; column < grades[0].length; column++) {
			if (Double.isNaN(grades[studentIndex][column])) {
				continue;
			}
			if (grades[studentIndex][column] < 6) {
				return false;
			}
		}
		return true;
	}
	// find which semester the student is in
	public static int semesterFinderForStudents(double[][] grades, int row){
			int semester = 0;
			int counter = 0;
	
			for (int col = 1; col < grades[0].length; col++) {
				if (Double.isNaN(grades[row][col])) {
					counter++;
				}
			}
	
			int coursesTaken = 36 - counter;
			semester = ((coursesTaken % 12) / 3) + 1; // need the remainder, 3 courses in 1 semester
	
			return semester;
	}

	//see the passing percentages of the course
    //count how many students have grades >= 6 
    public static void passingPercentageOfCourses(double[][]grades){
        double passingGrade = 6.0;
        int total = 0;  //total number of grades (except NG) for each subject
        int passingNum = 0; // total number of students with passing grades

        for(int i=0;i<grades[0].length;i++){ // i is the loop for column
           total = 0;
           passingNum = 0;
            //for each column(subject), count how many students have grade and count how many student have passing grade
            for(int j=0;j<grades.length;j++){ // j is the loop for row
                if(Double.isNaN(grades[j][i])) continue;
                total++;
                if(grades[j][i]>=passingGrade){
                    passingNum++;
                }
            }
            double percent =(double)passingNum / total;
            System.out.println("===================================");
            System.out.println("For "+ FileDisplayer.courses[i]);
            System.out.println("The total number of students: "+ total);
            System.out.println("The passing number: " + passingNum);
            System.out.println("Passing percentage of this course is : "+ percent*100);
            System.out.println("===================================");
        }
    }
    
    	//Connecting studentInfo with studentGrades average for a specific property for a specific subject
	//Create 5methods, for each property

	// 1️ Quantum Coherence Threshold → QCT
	// QCT has 5 properties - Fractured, Chaotic, Stable, Coherent, Resonant
	public static void computeAvgByQCT(double[][] grades, StudentInfo[] students, int courseIndex, String property){
		double sum =0;
		int count = 0;

		boolean propertyMatch = false;
		String [] properties ={
			"Fractured",
			"Chaotic",
			"Stable",
			"Coherent",
			"Resonant"
		};

		for(String p : properties){  //checking if the input parameters match with the existing properties
			if(p.equalsIgnoreCase(property)){
				propertyMatch = true;
				break;
			}
		}

		if(!propertyMatch){
			System.out.println("Wrong input for property");
			return ;
		}
		
		for(int i=0;i<grades.length;i++){
			if(Double.isNaN(grades[i][courseIndex])) continue; //skip NaN values
			if(students[i].quantumCoherenceThreshold.equalsIgnoreCase(property)){
				count++;
				sum+=grades[i][courseIndex];
			}
		}
		double average = sum/count;
		System.out.println("The number of students with "+property +" in QCT and has grades are "+ count);
		System.out.println("The average grade of "+ FileDisplayer.courses[courseIndex]+" of "+ property+" is: "+ average);
	}

	// 2️ Symbiotic Network Compatibility → SNC
	//SNC has 2 - None or Harmonized
	public static void computeAvgBySNC(double[][] grades, StudentInfo[] students, int courseIndex, String property){
		double sum = 0;
		int count = 0;
		//if students[i].symbioticNetworkCapability == None or Harmonized

		boolean propertyMatch = false;
		String [] properties ={
			"None",
			"Harmonized"
		};

		for(String p : properties){  //checking if the input parameters match with the existing properties
			if(p.equalsIgnoreCase(property)){
				propertyMatch = true;
				break;
			}
		}

		if(!propertyMatch){
			System.out.println("Wrong input for property");
			return ;
		}

		for(int i=0;i<grades.length;i++){
			if(Double.isNaN(grades[i][courseIndex])) continue; //skip NaN values
			if(students[i].symbioticNetworkCompatibility.equalsIgnoreCase(property)){
				count++;
				sum+=grades[i][courseIndex];
			}
		}

		double average = sum / count;
		
		System.out.println("The number of students with "+property +" in SNC and has grades are "+ count);
		System.out.println("The average grade of "+ FileDisplayer.courses[courseIndex]+"is: "+ average);
	}

	// 3️ Astro-Temporal Drift Resistance → ATDR
	// has 3 - 1 ns/mc, 2ns/mc, 3ns/mc  (but we stored as int value so 1,2,3)
	public static void computeAvgByATDR(double[][] grades, StudentInfo[] students, int courseIndex, String property){
		double sum =0;
		int count = 0;

		String[] properties = {"1 ns/mc","2 ns/mc","3 ns/mc"};
		boolean propertyMatch = false;

		for(String i : properties){	//if the parameter has the right values
			if(i.equalsIgnoreCase(property)){
				propertyMatch = true;
				break;
			}
		}

		if(!propertyMatch){
			System.out.println("Wrong Parameter! Property does not match!");
			return;
		}

		for(int i=0;i<grades.length;i++){
			if(Double.isNaN(grades[i][courseIndex])) continue; //skip values if NaN
			if(students[i].astroTemporalDriftResistance.equalsIgnoreCase(property)){
				sum+=grades[i][courseIndex];
				count++;
			}
		}
		
		double average = sum/count;
		System.out.println("The number of students who have grades and who has ATDR is: "+count);
		System.out.println("The average grade of "+ FileDisplayer.courses[courseIndex]+"is: "+ average);
	}

	// 4️ Psionic Interference Tolerance → PIT
	// maybe we can divide it by positive or negative so 2choices?
	public static void computeAvgByPIT(double[][] grades, StudentInfo[] students, int courseIndex){
		// since we are dividing into two branches, it could be more than or equal to 0 or less than 0
		// so we are not taking any property parameter for this one
		double sumPositive = 0;
		double sumNegative = 0;
		int countPositive = 0;
		int countNegative = 0;

		for(int i=0;i<grades.length;i++){
			if(Double.isNaN(grades[i][courseIndex])) continue; //skip NaN value again
			if(Double.parseDouble(students[i].psionicInterferenceTolerance)>0){ //students who has positive value of PIT
				sumPositive+=grades[i][courseIndex];
				countPositive++;
			}else{	//students who have negative value of PIT
				sumNegative+=grades[i][courseIndex];
				countNegative++;
			}
		}
		double average1 = sumPositive/countPositive;
		double average2 = sumNegative/countNegative;
		System.out.println("The number of Positive PIT student that have grades : "+ countPositive);
		System.out.println("The average grade of "+ FileDisplayer.courses[courseIndex]+" for positive PIT is: "+ average1);
		System.out.println("The number of Negative PIT student that have grades: "+ countNegative);
		System.out.println("The average grade of "+ FileDisplayer.courses[courseIndex]+" for negative PIT is: "+ average2);
	}

	// 5️ Bio-Luminal Transmission → BLT
	//BLT has 5 properties - Turquiose, Violet, Silver, Crimson, White-Blue
	public static void computeAvgByBLT(double[][] grades, StudentInfo[] students, int courseIndex, String property){
		double sum =0;
		int count = 0;
		boolean propertyMatch = false;
		String [] properties ={
			"Silver",
			"Crimson",
			"White-Blue",
			"Violet",
			"Turquiose"
		};

		for(String p : properties){  //checking if the input parameters match with the existing properties
			if(p.equalsIgnoreCase(property)){
				propertyMatch = true;
				break;
			}
		}

		if(!propertyMatch){
			System.out.println("Wrong input for property");
			return ;
		}

		for(int i=0;i<grades.length;i++){
			if(Double.isNaN(grades[i][courseIndex])) continue; //skip NaN values
			if(students[i].bioLuminalTransmission.equalsIgnoreCase(property)){
				count++;
				sum+=grades[i][courseIndex];
			}
		}

		double average = sum/count;
		System.out.println("The number of students with "+property +" in BLT and has grades are "+ count);
		System.out.println("The average grade of "+ FileDisplayer.courses[courseIndex]+"is: "+ average);
	}



	//Create a method to average score of another score(maybe we can use the related courses as inputs)
	public static void  computeAvgByAnotherSubject(double[][]grades, StudentInfo[] students, int courseIndex, int courseIndex2){
		if(courseIndex==courseIndex2){ //make sure to compare another subject
			System.out.println("No meaning to compare same subject!");
			return;
		}

		int countHighGrade = 0;
		int countLowgrade = 0;
		double sumHigh = 0;
		double sumLow =0;

		double compareScore = 8.0; //this value can be modified, but we just consider the high performers as greater or equal to 7
		for(int i=0;i<grades.length;i++){
			if(Double.isNaN(grades[i][courseIndex])||Double.isNaN(grades[i][courseIndex2]))continue; //skip if one of the value has no grades

			if(grades[i][courseIndex2]>=compareScore){ 
				countHighGrade++;
				sumHigh+=grades[i][courseIndex];
			}else{
				countLowgrade++;
				sumLow+=grades[i][courseIndex];
			}
		}
		System.out.println("Average score of "+FileDisplayer.courses[courseIndex]+" students who perform "+compareScore+" or above in course "+FileDisplayer.courses[courseIndex2]+" is : "+sumHigh/countHighGrade);
		System.out.println("The number of students was: "+countHighGrade);
		System.out.println("Average score of "+FileDisplayer.courses[courseIndex]+" students who perform less than "+compareScore+" in course "+FileDisplayer.courses[courseIndex2]+" is : "+sumLow/countLowgrade);
		System.out.println("The number of students was: "+countLowgrade);
		
	}
}