package com.example.Test;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class ScatterPlot extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Plots");
        //FileDisplayer.loadGrades(); // load grades

        double [][] grades = 
        {
        {7.0, 8.5, 6.0, 6.0},
        {5.5, 1.0, 7.5 , 9.0},
        {9.0, 7.0, Double.NaN , 9.0},
        {6.5, 5.0, 6.5 , 9.0},
        {8.0, 9.0, 9.5 , 9.0} 
        };
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Grades course1");  // X-axis subject

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Grades course2");     // Y-axis subject

        ScatterChart<Number, Number> chart = new ScatterChart<>(xAxis, yAxis);
        
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setTitle("Scatterplot");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        int students = grades.length;
        double[] course1 = new double[students];
        double[] course2 = new double[students];

        for (int i = 0; i < students; i++) {
            course1[i] = grades[i][1]; // course 15
            course2[i] = grades[i][2]; // course 30
            if (!Double.isNaN(course1[i]) && !Double.isNaN(course2[i])) { // skip NGs
                XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(course1[i], course2[i]);
                series.getData().add(dataPoint);
                // customize the datapoints
                Circle circle = new Circle(4, Color.RED);
                circle.setStroke(Color.WHITE);
                circle.setStrokeWidth(1);
                
                dataPoint.setNode(circle);
            }
        }


        chart.getData().add(series);       

        Scene scene = new Scene(chart, 800, 500);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}