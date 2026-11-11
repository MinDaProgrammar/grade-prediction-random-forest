package com.example;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.Node; 

public class App extends Application {

    // Center container where the chart will be displayed and dynamically swapped
    private StackPane chartDisplayPane; 

    private double[][] grades = DataLoader.readGradesCSV("currentGrades.csv"); 
    

    private final List<String> SUBJECTS = Arrays.asList(
        "Cryogenic Physics", "Evolutionary Dynamics", "Dark Energy",
        "Stellar Chemistry", "Sublight Propulsion Systems",
        "Transdimensional Navigation", "Neural Computation", 
        "Plasma Physics", "Crystalline Geology", "Pulsar Spectroscopy",
        "Propulsion Systems", "Radiation Shielding", "Atmospheric Chemistry",
        "Warp Field Theory", "Plasmawave Analysis", "Exoplanet Climatology",
        "Astrology", "Magnetosphere Modeling", "Graviton Dynamics", 
        "Xyloprax Computation", "Mineral Extraction", "Synthetic Atmospheres",
        "Quantum Fluids", "Nanostructures Fabrication", "Photonic Computing",
        "Orbital Mechanics", "Neutrino Engineering", "Astrogeophysics",
        "Genomic Engineering", "Particle Isolation", "Nuclear Synthesis",
        "Xenorobotics Systems", "Cartographic Topologies", "Exotic Matter",
        "Plasma Containment", "Bioluminal Signals"
    );

    @Override
    public void start(Stage stage) {
        //This is just to check if the charts are working properly with small array
        //loadTestData();
        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        // Setup Main Layout Container
        BorderPane root = new BorderPane();

        // Setup Top Control Panel such as subject and plot selection
        HBox controlPanel = createControlPanel();
        root.setTop(controlPanel);

        // Setup Chart Display Area
        chartDisplayPane = new StackPane();
        chartDisplayPane.setPadding(new Insets(10));
        root.setCenter(chartDisplayPane);

        // Initial Plot Display (Show something when the app starts)
        Runnable initialUpdateAction = (Runnable) controlPanel.getProperties().get("updateAction");
        if (initialUpdateAction != null) {
            initialUpdateAction.run();
        }

        // Finalize Stage
        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Visualizing Datas");
        stage.show();
    }
    
    //Creating horizontal boxes for the selection part
    private HBox createControlPanel() {
        
        // Subject Selection 
        Label subjectLabel = new Label("Select Subject1:");
        ChoiceBox<String> subjectChooser = new ChoiceBox<>();
        
        // Choose from 36 subjects
        subjectChooser.getItems().addAll(SUBJECTS);
        subjectChooser.setValue("Cryogenic Physics"); // Default value

        // Subject Selection 
        Label subjectLabel2 = new Label("Select Subject2:");
        ChoiceBox<String> subjectChooser2 = new ChoiceBox<>();
        
        // Choose from 36 subjects
        subjectChooser2.getItems().addAll(SUBJECTS);
        subjectChooser2.setValue("Bioluminal Signals"); // Default value

        // Plot Type Selection 
        Label plotLabel = new Label("Select Plot Type:");
        ChoiceBox<String> plotChooser = new ChoiceBox<>();
        
        // Choose from given plots
        plotChooser.getItems().addAll(
            "Bar Chart1",
            "Bar Chart2",
            "Pie Chart",
             "Scatter Plot", 
             "Histograms",
             "Swarm Plot", 
             "Joint Plot"
             );
        plotChooser.setValue("Bar Chart1"); // Default value
        
        //  Putting each label and select box in a sepecific location
        HBox controls = new HBox(15); // 15 is spacing
        controls.setPadding(new Insets(15));
        controls.setAlignment(Pos.CENTER);
        controls.getChildren().addAll(
            subjectLabel, subjectChooser, subjectLabel2, subjectChooser2, plotLabel, plotChooser 
        );
        
        // Event Handling based on the chosen plot and subject
        // This lambda function runs whenever the selection changes in either ChoiceBox


        Runnable updateAction = () -> {
            String selectedPlot = plotChooser.getValue();
            //String selectedSubject = subjectChooser.getValue(); 
            //get index value instead of string value of subject chosen
            int courseIndex = subjectChooser.getSelectionModel().getSelectedIndex(); 
            int courseIndex2 = subjectChooser2.getSelectionModel().getSelectedIndex();

            //  Test logic on console to check if the selectors work properly
            System.out.println("--- Selection Change Detected ---");
            System.out.println("Selected Plot: " + selectedPlot);
            System.out.println("Selected Subject1: " + SUBJECTS.get(courseIndex));
            System.out.println("Selected Subject2: " + SUBJECTS.get(courseIndex2));
            System.out.println("Subject Index (for array access): " + courseIndex);
            System.out.println("Subject Index (for array access): " + courseIndex2);
            
            //updateChartDisplay(selectedPlot, courseIndex)
            // Also checking if the values are actually selected again
            if (selectedPlot != null && courseIndex != -1) {
                
                // Call the central update method which contains the switch statement
                updateChartDisplay(selectedPlot, courseIndex, courseIndex2);
            }

        };
        
        // Link the action to both ChoiceBoxes so that whenever the choiceBox is chosen it runs the action
        plotChooser.valueProperty().addListener((obs, oldVal, newVal) -> updateAction.run());
        subjectChooser.valueProperty().addListener((obs, oldVal, newVal) -> updateAction.run());
        subjectChooser2.valueProperty().addListener((obs, oldVal, newVal) -> updateAction.run());

        // This allows the start() method to trigger the initial plot display
        controls.getProperties().put("updateAction", updateAction);
        
        return controls;
    }

    //Create a chart that plots 1D array 
    public void updateChartDisplay(String selectedPlot, int courseIndex, int courseIndex2) {
        // Clear the previous chart
        chartDisplayPane.getChildren().clear();
        Node chartNode = null;
        
        String subjectName = SUBJECTS.get(courseIndex); 

        // Show different chart depending on the selectBox choice
        switch (selectedPlot) {
            
            case "Bar Chart1":
                // chartNode = plotBarGraph(courseIndex, grades);
                chartNode = plotBarChart();
                break;
            case "Bar Chart2":
                chartNode = plotBarChart2(grades);
                break;
            case "Scatter Plot":
                // chartNode = plotScatterPlot(courseIndex, otherCourseIndex, grades); 
                chartNode = plotScatterplot(courseIndex, courseIndex2, subjectName, grades, SUBJECTS);
                break;
                
            case "Histograms":
                // chartNode = plotHistogram(courseIndex, grades);
                chartNode = plotHistogram(courseIndex, true);
                break;
                
            case "Swarm Plot":
                // *** THIS IS THE INTEGRATION POINT ***
                // It calls the implemented method directly:
                chartNode = plotSwarmPlot(courseIndex, subjectName, grades, SUBJECTS); 
                break;
                
            case "Joint Plot":
                // chartNode = plotJointPlot(courseIndex, grades);
                chartNode = plotJointplot(courseIndex, courseIndex2, subjectName, grades, SUBJECTS);
                break;
            case "Pie Chart":
                chartNode = plotPieChart(courseIndex,courseIndex2,grades);
                break;

            
            default:
                chartNode = new Label("Select a valid plot type to begin visualization.");
                break;
        }

        // Add the new chart/node to the display pane
        if (chartNode != null) {
            chartDisplayPane.getChildren().add(chartNode);
        }
    }


    //Methods for each chart

    //Bar Chart - calculating the number of NGs in each course
    private BarChart<String, Number> plotBarChart() {

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
        double [] ngs = new double[36];

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

    //BarChart2
    private BarChart<String, Number> plotBarChart2(double[][] grades) {

        // setup axes
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Courses");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Average grade");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Average grades per courses");
        chart.setLegendVisible(false);
        chart.setAnimated(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        String[] courses = FileDisplayer.courses;
        double [] averages = new double[courses.length];

        //create an array containing grade averages (index of array corresponds to course index)
        for (int course = 0; course < FileDisplayer.courses.length; course++) {
            double count = StatisticsUtil.computeCourseMean(grades, course);
            if (!Double.isNaN(count)) {
            averages[course] = count;
            }
        }

        //array of indexes corresponding to course indexes
        int[] indexes = new int[courses.length];
        for (int i = 0; i < indexes.length; i++) {
            if (!Double.isNaN(averages[i])) {
            indexes[i] = i;
            }
        }

        //loop to sort the indexes
        for (int i = 0; i < indexes.length - 1; i++) {
            for (int j = 0; j < indexes.length - 1 - i; j++) {
                int index1 = indexes[j];
                int index2 = indexes[j + 1];
                if (averages[index2] > averages[index1]) {
                    int helper = indexes[j];
                    indexes[j] = indexes[j + 1];
                    indexes[j + 1] = helper;
                }
            }
        }

        //create list of sorted courses
        List<String> sortedCourses = new ArrayList<>();
        for (int index : indexes) {
            if (!Double.isNaN(averages[index])) {
                sortedCourses.add(courses[index]);
            }
        }

        //add data in sorted order
        for (int index : indexes) {
            if (!Double.isNaN(averages[index])) {
                series.getData().add(new XYChart.Data<>(courses[index], averages[index]));
            }
        }

        //apply sorted array to the x axis
        xAxis.setCategories(FXCollections.observableArrayList(sortedCourses));

        //add the series to the chart
        chart.getData().add(series);

        //rotate labels otherwise they don't fit
        xAxis.setTickLabelRotation(90);

        return chart;
    }


     //Pie Chart
     private PieChart plotPieChart(int courseIndex, int courseIndex2, double[][] grades) {
        int students = grades.length;
        int courses = grades[0].length;
        int counterCumLaude = 0;

        //applying rules based on selected subjs
        for (int i = 0; i < students; i++) {
            double grade1 = grades[i][courseIndex];
            double grade2 = grades[i][courseIndex2];

            //valid grade
            if (Double.isNaN(grade1) || Double.isNaN(grade2)) {
                continue;
            }

            //grades must be at lease 8.0
            if(grade1 < 8.0 || grade2 < 8.0) {
                continue;
            }

            double sum = 0.0;
            int counter = 0;
            for (int j = 0; j < courses; j++) {
                double grade = grades[i][j];
                if (!Double.isNaN(grade)) {
                    sum += grade;
                    counter++;
                }
            }
            if (counter == 0) continue;
            double average = sum / counter;

                if (average >= 8.0) {
                    counterCumLaude++;
                
            }
        }

        int nonCumLaude = students - counterCumLaude;

        PieChart pieChart = new PieChart();
        // pieChart.setLegendVisible(true);
        pieChart.setTitle("How many students graduated Cum-Laude");
        // pieChart.setClockwise(true); // set the data clockwise

        // Add data with the counts in the label strings
        PieChart.Data slice1 = new PieChart.Data("Cum Laude(" + counterCumLaude + ")", counterCumLaude);
        PieChart.Data slice2 = new PieChart.Data ("Others(" + nonCumLaude + ")", nonCumLaude);

        pieChart.getData().addAll(slice1, slice2);

        int total = counterCumLaude + nonCumLaude;
        for (PieChart.Data data : pieChart.getData()) {
            double percent = (data.getPieValue() / total) * 100;
            data.setName(data.getName() + String.format("(%.1f%%)", percent));
        }

        return pieChart;
    }

    //Histogram
    private BarChart<String, Number> plotHistogram(int courseIndex, boolean showNGs) {
        //Check in console what are the grade values for one coursee
        System.out.println("Histogram open!");
        for(int i=0;i<grades.length;i++){
           // System.out.println("student "+i+" : "+grades[i][courseIndex]);
        }
        System.out.println("Length: "+ grades.length);

        // Use fixed bins because data are integers from 0 to 10
        //int min = 0;
        //int max = 10;
        int bins = 11;
        //double binWidth = 1.0; // each bin covers one integer
        int NGs = 0;

        // count frequencies
        int[] counts = new int[bins];

        for(int i=0;i<grades.length;i++){
            if (Double.isNaN(grades[i][courseIndex])) {
                NGs++;
                continue;
            }
            int value = (int) grades[i][courseIndex];
            counts[value]++;
        }

        // setup axes
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Grade");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Number of Students");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Histogram of " + SUBJECTS.get(courseIndex));
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

    //Scatter plot
    public static Node plotScatterplot(int courseIndex,int courseIndex2, String courseName, double[][]grades, List<String> subjects){
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Grade Distribution for "+ subjects.get(courseIndex));  // X-axis subject

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Grade Distribution for "+subjects.get(courseIndex2));     // Y-axis subject

        ScatterChart<Number, Number> chart = new ScatterChart<>(xAxis, yAxis);
        
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setTitle("Scatterplot");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        int students = grades.length;
        double[] course1 = new double[students];
        double[] course2 = new double[students];

        for (int i = 0; i < students; i++) {
            course1[i] = grades[i][courseIndex]; // first selected course
            course2[i] = grades[i][courseIndex2]; // second selected course
            if (!Double.isNaN(course1[i]) && !Double.isNaN(course2[i])) { // skip NGs
                XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(course1[i], course2[i]);
                series.getData().add(dataPoint);
                // customize the datapoints
                Circle circle = new Circle(2, Color.RED);
                circle.setStroke(Color.WHITE);
                circle.setStrokeWidth(0.1);
                
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
        return chart;
    }

    //Swarm plot
    public static Node plotSwarmPlot(int courseIndex, String courseName, double[][] grades, List<String> subjects) {
        // Categories on X-axis
        System.out.println("Swarm plot Called!");
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Courses");

        // Continuous values on Y-axis
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Grades");

        Random rand = new Random();

        ScatterChart<String, Number> chart = new ScatterChart<String, Number> (xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setTitle("Swarmplot");

        System.out.println("Before the Loop");
        for (int course=0; course < grades[0].length; course++) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            Color randomColor = Color.color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
            for (int student=0; student < grades.length; student++) {
                double grade = grades[student][course];
                // skip NGs
                if (!Double.isNaN(grade) && grade >= 0) {
                    System.out.println("Course: "+ course+" student: "+student);
                    XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(subjects.get(course), grade);
                    // Add one data point (x=course name, y=grade)
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
        System.out.println("After the loop");
        return chart;
    }

    //Joint Plot
    public static Node plotJointplot(int courseIndex,int courseIndex2, String courseName, double[][]grades, List<String> subjects){
        NumberAxis xAxis = new NumberAxis();
        //xAxis.setLabel("Course: " + courseIndex);  // X-axis subject
        xAxis.setLabel(subjects.get(courseIndex));

        NumberAxis yAxis = new NumberAxis();
        //yAxis.setLabel("Course: " + courseIndex2);     // Y-axis subject
        yAxis.setLabel(subjects.get(courseIndex2));

        ScatterChart<Number, Number> chart = new ScatterChart<>(xAxis, yAxis);
        
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        

        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        int students = grades.length;
        double[] course1 = new double[students];
        double[] course2 = new double[students];

        Map<Integer, Integer> countsX = new java.util.TreeMap<>();
        Map<Integer, Integer> countsY = new java.util.TreeMap<>();

        for (int i = 0; i < students; i++) {
            course1[i] = grades[i][courseIndex]; // first selected course
            course2[i] = grades[i][courseIndex2]; // second selected course
            if (!Double.isNaN(course1[i]) && !Double.isNaN(course2[i])) { // skip NGs
                XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(course1[i], course2[i]);
                series.getData().add(dataPoint);
                // customize the datapoints
                Circle circle = new Circle(2, Color.BLUE);
                circle.setStroke(Color.WHITE);
                circle.setStrokeWidth(0.1);
                
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
                int xGroup = (int) Math.round(course1[i]);
                int yGroup = (int) Math.round(course2[i]);
                countsX.put(xGroup, countsX.getOrDefault(xGroup, 0) + 1);
                countsY.put(yGroup, countsY.getOrDefault(yGroup, 0) + 1);
            }
        }

        chart.getData().add(series);       
        
        CategoryAxis topCatAxis = new CategoryAxis();
        NumberAxis topNumAxis = new NumberAxis();
        BarChart<String, Number> topChart = new BarChart<>(topCatAxis, topNumAxis);
        topChart.setLegendVisible(false);
        topChart.setAnimated(false);
        topChart.setTitle(subjects.get(courseIndex)); //grade distribution for subject1 
        topChart.setPrefHeight(140);
        XYChart.Series<String, Number> topSeries = new XYChart.Series<>();
        for (Map.Entry<Integer, Integer> e : countsX.entrySet()) {
            topSeries.getData().add(new XYChart.Data<>(String.valueOf(e.getKey()), e.getValue()));

        }
        topChart.getData().add(topSeries);
        // make the top histogram match the scatter plot's width and center it
        topChart.prefWidthProperty().bind(chart.widthProperty());
        topChart.maxWidthProperty().bind(chart.widthProperty());
        for(Node n:topChart.lookupAll(".default-color0.chart-bar")) {
            n.setStyle("-fx-bar-fill: blue;");
        }

        NumberAxis rightNumAxis = new NumberAxis();
        CategoryAxis rightCatAxis = new CategoryAxis();
        BarChart<Number, String> rightChart = new BarChart<>(rightNumAxis, rightCatAxis);
        rightChart.setLegendVisible(false);
        rightChart.setAnimated(false);
        rightChart.setTitle(subjects.get(courseIndex2));    //grade distribution for subject (but the string doesn't fit in the chart)
        rightChart.setPrefWidth(140);
        XYChart.Series<Number, String> rightSeries = new XYChart.Series<>();
        for (Map.Entry<Integer, Integer> e : countsY.entrySet()) {
            rightSeries.getData().add(new XYChart.Data<>(e.getValue(), String.valueOf(e.getKey())));
        }
        rightChart.getData().add(rightSeries);
        for(Node n:rightChart.lookupAll(".default-color0.chart-bar")) {
            n.setStyle("-fx-bar-fill: blue;");
        }
        BorderPane root = new BorderPane();
        root.setCenter(chart);
        root.setTop(topChart);
        BorderPane.setAlignment(topChart, Pos.TOP_LEFT);
        root.setRight(rightChart);

        
        return root;
    }

    public void loadTestData(){
        this.grades = new double[][]{
            {7.0, 8.5, 6.0},
            {5.5, 6.0, 7.5},
            {9.0, 7.0, 8.0},
            {6.5, 5.0, 6.5},
            {8.0, 9.0, 9.5}
        };
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}