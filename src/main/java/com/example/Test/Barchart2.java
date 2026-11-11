package com.example.Test;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import com.example.DataLoader;
import com.example.FileDisplayer;
import com.example.StatisticsUtil;

public class Barchart2 extends Application {
    double [][] grades;
    @Override
    public void start(Stage stage) {
        stage.setTitle("Histogram using BarChart (JavaFX)");
        //FileDisplayer.loadGrades(); //load grades in FileDisplayer so the array studentGrades won't be empty
        grades = DataLoader.readGradesCSV("CurrentGrades.csv");
        BarChart<String, Number> chart = generateChart();

        BorderPane root = new BorderPane(chart);
        root.setPadding(new Insets(10));
        Scene scene = new Scene(root, 800, 500);
        stage.setScene(scene);
        stage.show();
    }

    private BarChart<String, Number> generateChart() {

        // setup axes
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Courses");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount of NGs");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Amount of NGs per course");
        chart.setLegendVisible(false);
        chart.setAnimated(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        String[] courses = FileDisplayer.courses;
        double [] ngs = new double[grades[0].length];

        //add data to x axis
        for (int course = 0; course < FileDisplayer.courses.length; course++) {
            int count = StatisticsUtil.countMissingForCourse(grades, course);
            ngs[course] = count;
            XYChart.Data<String, Number> item = new XYChart.Data<>(FileDisplayer.courses[course], count);
            series.getData().add(item);
        }

        //array of indexes corresponding to course indexes
        int[] indexes = new int[courses.length];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i;
        }

        //loop to sort the indexes
        for (int i = 0; i < indexes.length - 1; i++) {
            for (int j = 0; j < indexes.length - 1 - i; j++) {
                int index1 = indexes[j];
                int index2 = indexes[j + 1];
                if (ngs[index2] > ngs[index1]) {
                    int helper = indexes[j];
                    indexes[j] = indexes[j + 1];
                    indexes[j + 1] = helper;
                }
            }
        }

        List<String> sortedCourses = new ArrayList<>();
        for (int index : indexes) {
            sortedCourses.add(courses[index]);
        }

        //apply sorted array to the x axis
        xAxis.setCategories(FXCollections.observableArrayList(sortedCourses));

        //rotate labels otherwise they don't fit
        xAxis.setTickLabelRotation(90);

        //add the series to the chart
        chart.getData().add(series);
        return chart;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
