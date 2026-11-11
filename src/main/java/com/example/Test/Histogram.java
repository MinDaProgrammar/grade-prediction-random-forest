package com.example.Test;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;

import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class Histogram extends Application {

    double [][] grades = 
    {
    {7.0, 8.5, 7.0},
    {5.5, 6.0, 7.5},
    {9.0, 7.0, 8.0},
    {6.5, 5.0, 6.5},
    {8.0, 8.0, Double.NaN} 
    };

    @Override
    public void start(Stage stage) {
        stage.setTitle("Histogram using BarChart (JavaFX)");
        //FileDisplayer.loadGrades(); //load grades in FileDisplayer so the array studentGrades won't be empty

        //data of student (right now #0)
        BarChart<String, Number> chart = generateChart(grades[4], true);

        BorderPane root = new BorderPane(chart);
        root.setPadding(new Insets(10));
        Scene scene = new Scene(root, 800, 500);
        stage.setScene(scene);
        stage.show();
    }
    // Funtion which creates a histogram from data
    /**
     * Funtion which creates a histogram from data
     * @param data grades list
     * @return histogram of grades
     */

    private BarChart<String, Number> generateChart(double[] data, boolean showNGs) {
        // Example data

        // Use fixed bins because data are integers from 0 to 10
        //int min = 0;
        //int max = 10;
        int bins = 11;
        //double binWidth = 1.0; // each bin covers one integer
        int NGs = 0;

        // count frequencies
        int[] counts = new int[bins];
        for (double element : data) {
            if (Double.isNaN(element)) {
                NGs++;
                continue;
            }
            int value = (int) element;
            counts[value]++;
        }

        // setup axes
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Grade");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount of grades");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Histogram");
        chart.setLegendVisible(false);
        chart.setAnimated(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Add data to the series
        // Integer.toString(i) = x-axis label and counts[i] = Y-axis value
        for (int i = 0; i <= 10; i++) { // bins 0–10
            series.getData().add(new XYChart.Data<>(Integer.toString(i), counts[i]));
        }

        if (showNGs) {
            series.getData().add(new XYChart.Data<>("NG", NGs));
        }

        // Add the series to the chart
        chart.getData().add(series);
        return chart;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
