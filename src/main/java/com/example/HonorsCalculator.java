package com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;




public class HonorsCalculator {


   private static final double CUM_LAUDE_THRESHOLD = 8.0;




   private static class StudentHonors implements Comparable<StudentHonors> {
       final int studentId;
       final double averageGrade;
       final int coursesCounted;


       public StudentHonors(int studentId, double averageGrade, int coursesCounted) {
           this.studentId = studentId;
           this.averageGrade = averageGrade;
           this.coursesCounted = coursesCounted;
       }


       @Override
       public String toString() {
          
           return String.format("ID: %d, Average Grade: %.2f (from %d courses)",   //formats grade to 2dp
                                studentId, averageGrade, coursesCounted);
       }


      
       @Override
       public int compareTo(StudentHonors other) {
           return -Double.compare(this.averageGrade, other.averageGrade);
       }
   }


   public static void main(String[] args) {
       String fileName = "CurrentGrades.csv";
       List<StudentHonors> honorsList = new ArrayList<>();


       try (Scanner fileScanner = new Scanner(new File(fileName))) {
          
           if (fileScanner.hasNextLine()) {
               fileScanner.nextLine();
           }


           while (fileScanner.hasNextLine()) {
               String line = fileScanner.nextLine();
               String[] values = line.split(",");


               if (values.length < 2) continue;


               try {
                   int studentId = Integer.parseInt(values[0].trim());
                   double totalGradeSum = 0.0;
                   int coursesCounted = 0;


                   for (int i = 1; i < values.length; i++) {
                       String gradeStr = values[i].trim();


                       if (!gradeStr.equalsIgnoreCase("NG") && !gradeStr.isEmpty()) {
                           try {
                               double grade = Double.parseDouble(gradeStr);
                               totalGradeSum += grade;
                               coursesCounted++;
                           } catch (NumberFormatException e) {
                               System.err.println("Warning: Skipping non-numeric grade for Student " + studentId + " in column " + i + ": " + gradeStr);
                           }
                       }
                   }


                   if (coursesCounted > 0) {
                       double average = totalGradeSum / coursesCounted;


                       if (average >= CUM_LAUDE_THRESHOLD) {
                           honorsList.add(new StudentHonors(studentId, average, coursesCounted));
                       }
                   }


               } catch (NumberFormatException e) {
                   System.err.println("Error parsing StudentID in line: " + line);
               }
           }


           Collections.sort(honorsList);


           System.out.println("--- Students Graduating Cum Laude (Average Grade >= " + CUM_LAUDE_THRESHOLD + ") ---");
           if (honorsList.isEmpty()) {
               System.out.println("No students meet the Cum Laude requirements based on the current data.");
           } else {
               System.out.println("Total Qualifying Students: " + honorsList.size());
               System.out.println("-----------------------------------------------------------------");
               for (StudentHonors student : honorsList) {
                   System.out.println(student);
               }
           }




       } catch (FileNotFoundException ex) {
           System.err.println("ERROR: The file '" + fileName + "' was not found. Please ensure it is in the correct directory.");
       } catch (Exception ex) {
           System.err.println("An unexpected error occurred: " + ex.getMessage());
           ex.printStackTrace();
       }
   }
}
