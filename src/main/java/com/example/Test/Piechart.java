package com.example.Test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.stage.Stage;

public class Piechart extends Application{
    @Override
    public void start(Stage stage) {
        stage.setTitle("Plots");
        //FileDisplayer.loadGrades(); //load grades
        double [][] grades = 
        {
        {7.0, 8.0, 6.0, 6.0},
        {5.5, 1.0, 7.5 , 5.0},
        //{9.0, 7.0, Double.NaN , 9.0},
        {6.5, 5.0, 6.5 , 5.0},
        {8.0, 9.0, 9.5 , 9.0} 
        }; //testing grades

        int students = grades.length;
        int courses = grades[0].length;

        PieChart pieChart = new PieChart();
        // pieChart.setLegendVisible(true);
        pieChart.setTitle("How many students graduated Cum-Laude");

        // pieChart.setClockwise(true); // set the data clockwise

        int counterCumLaude = 0;
        for (int i = 0; i < students; i++) {
            double sum = 0.0;
            int counter = 0;
            for (int j = 0; j < courses; j++) {
                double grade = grades[i][j];
                if (!Double.isNaN(grade)) { // skip NGs
                    sum += grade;
                    counter++;
                }
            }
            if (counter > 0) {
                double average = sum / counter;
                if (average >= 8.0) {
                    counterCumLaude++;
                }  
            }
        }
        

        int nonCumLaude = students - counterCumLaude;

        pieChart.getData().add(new PieChart.Data("Cum Laude("+counterCumLaude+")", counterCumLaude));
        pieChart.getData().add(new PieChart.Data("Others("+nonCumLaude+")", nonCumLaude));

        Scene scene = new Scene(pieChart, 600, 400);
        stage.setScene(scene);
        stage.show();        
    }

    public static void main(String[] args) {
        launch(args);
    }
}