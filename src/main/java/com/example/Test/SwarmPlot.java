package com.example.Test;
import java.util.Random;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class SwarmPlot extends Application {

    double [][] grades = 
    {
    {7.0, 8.5, 6.0},
    {5.5, 6.0, 7.5},
    {9.0, 7.0, 8.0},
    {6.5, 5.0, 6.5},
    {8.0, 9.0, 9.5} 
    };
    @Override
    public void start(Stage stage) {
        stage.setTitle("Plots");
       // FileDisplayer.loadGrades(); //load grades in FileDisplayer so the array studentGrades won't be empty

        // Categories on X-axis
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Courses");

        // Continuous values on Y-axis
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Grades");

        Random rand = new Random();

        ScatterChart<String, Number> chart = new ScatterChart<String, Number> (xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setTitle("Swarmplot");

        for (int course=0; course < grades[0].length; course++) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            Color randomColor = Color.color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
            for (int student=0; student < grades.length; student++) {
                double grade = grades[student][course];
                // skip NGs
                if (!Double.isNaN(grade) && grade >= 0) {
                    XYChart.Data<String, Number> dataPoint = new XYChart.Data<>("Physics", grade);
                    // add one data point (x=course name, y=grade)
                    series.getData().add(dataPoint);
                    Circle circle = new Circle(2, randomColor);
                    circle.setStroke(Color.WHITE);
                    circle.setStrokeWidth(1);
                    // Jitter value points in x and y
                    dataPoint.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                            double jitter = (Math.random() - 0.5) * 20;
                            double jitter2 = (Math.random() - 0.5) * 20;
                            newNode.setTranslateX(jitter);
                            newNode.setTranslateY(jitter2);
                        }  
                    });
                    dataPoint.setNode(circle);
                }
            }
            chart.getData().add(series);
        }
        Scene scene = new Scene(chart, 1000, 800);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}