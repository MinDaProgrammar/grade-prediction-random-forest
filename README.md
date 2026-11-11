# Student Grade Prediction — Decision Trees & Random Forest from Scratch (Java)

Predicts a student's missing course grade from (a) grades of past graduates and (b) categorical student attributes, using regression trees and a random forest written from scratch in Java (no ML libraries). Includes a JavaFX dashboard for exploring the dataset.

> Group project (Group 21) for **Project 1-1**, BSc Data Science & AI, Maastricht University (2025–26). The dataset is a synthetic one provided by the course (fictional courses such as *Dark Energy* and *Warp Field Theory*).

## What it does

**Phase 1 — statistics and decision stumps**
- Data loading and cleaning (handles `NG`/missing grades), per-course statistics, cum-laude calculation
- Best single split per course using variance reduction, then a "forest of stumps" from the top-k splits

**Phase 3 — regression trees and random forest**
- Recursive regression trees on two feature sets: graduate grades (course-to-course correlation) and student attributes
- Post-pruning on a validation set using MSE
- Random forest: bootstrap 70% samples, build both tree types per iteration, average the predictions
- Console mode writes predictions to a CSV

**Visualisation (JavaFX)**
- Bar charts of failing grades and averages per course, cum-laude pie chart, histograms, scatter plot, swarm plot, side-by-side grade distributions

## Project structure

```
src/main/java/com/example/
├── Predictor.java        # stumps, regression trees, pruning, random forest
├── SplitResult.java      # split description + prediction values
├── TreeNode.java
├── StatisticsUtil.java   # Phase 1 statistics
├── DataLoader.java       # CSV loading, NG handling
├── HonorsCalculator.java # cum-laude ranking
├── FileDisplayer.java    # console prediction workflow
├── App.java              # JavaFX dashboard
├── Launcher.java         # entry point (GUI or console)
└── Test/                 # individual chart prototypes
src/main/resources/       # course-provided CSV data
```

## Running

Requires Java 21 and Maven.

```bash
# GUI dashboard
mvn clean javafx:run

# Console prediction mode
mvn clean package
java -jar target/Project1-1-Phase3.jar cli
```

In console mode you enter a student ID and a course name, then choose decision tree (graduate grades), decision tree (student attributes) or random forest.

## Tech

Java 21 · JavaFX 21 · Maven
