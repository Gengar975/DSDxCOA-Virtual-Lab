package lab.ui;

import javafx.application.Application;

/**
 * Start-up class for IDEs and plain "java -cp": a main class that does not extend Application avoids the
 * "JavaFX runtime components are missing" error when JavaFX is on the classpath.
 */
public final class Launcher {

    private Launcher() { }

    public static void main(String[] args) {
        Application.launch(LabApp.class, args);
    }
}
