# DigiCore Lab - how to run

The learning application is written in JavaFX (`src/main/java/lab/ui`).

## With Maven (recommended)
```
mvn clean javafx:run
```
Maven downloads JavaFX automatically (needs internet the first time). Java 21 is required.

## From an IDE (Eclipse / IntelliJ / VS Code)
1. Import the project as a Maven project so the JavaFX dependency is downloaded.
2. Run the class `lab.ui.Launcher` (it avoids the "JavaFX runtime components are missing" error).

## Tests
```
mvn test
```

## Project layout
| Package | What it contains |
|---|---|
| `dsd.*`, `coa.*`, `playground` | the engine: gates, circuits, algorithms (unchanged logic, tested) |
| `lab.content` | the lessons: theory, key points, worked examples for every topic |
| `lab.model` | gate-level diagrams of the lessons, truth-table generator, K-map grouping |
| `lab.ui` | the JavaFX screens, animations and the drag-and-drop playground |
| `src/main/resources/lab/ui/lab.css` | the look of the application |

`MainApp.java` is the previous Swing prototype; it can be deleted once you no longer need it.
