package lab.ui;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lab.content.Topic;

/** DigiCore Lab - a learning application for digital system design and computer organization. */
public class LabApp extends Application {

    private final BorderPane root = new BorderPane();
    private Sidebar sidebar;
    private LessonPage lesson;
    private PlaygroundView playground;

    @Override
    public void start(Stage stage) {
        sidebar = new Sidebar(this::openHome, this::open);
        root.setLeft(sidebar);
        openHome();
        Scene scene = new Scene(root, 1440, 900);
        scene.getStylesheets().add(LabApp.class.getResource("lab.css").toExternalForm());
        stage.setTitle("DigiCore Lab - learn digital logic and computer organization");
        stage.setMinWidth(980);
        stage.setMinHeight(620);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private void releaseCurrent() {
        if (lesson != null) lesson.dispose();
        if (playground != null) playground.dispose();
        lesson = null;
        playground = null;
    }

    private void openHome() {
        releaseCurrent();
        sidebar.select(null);
        setPage(new HomePage(this::open));
    }

    private void open(Topic topic) {
        releaseCurrent();
        sidebar.select(topic);
        if (topic.getGroup() == Topic.Group.PLAYGROUND) {
            playground = new PlaygroundView();
            root.setCenter(playground);
            return;
        }
        lesson = new LessonPage(topic);
        setPage(lesson);
    }

    private void setPage(Node page) {
        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("page-scroll");
        scroll.setVvalue(0);
        root.setCenter(scroll);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
