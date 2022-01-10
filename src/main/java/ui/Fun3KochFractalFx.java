/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import calculate.Edge;
import calculate.KochManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * @author Nico Kuijpers
 * Modified for FUN3 by Gertjan Schouten
 */
public class Fun3KochFractalFx extends Application {
    private static final int THRESHOLD = 800_000;
    private final int kpWidth = 500;
    private final int kpHeight = 500;
    private final WritableImage image = new WritableImage(kpWidth, kpHeight);
    // Zoom and drag
    private double zoomTranslateX = 0.0;
    private double zoomTranslateY = 0.0;
    private double zoom = 1.0;
    private double startPressedX = 0.0;
    private double startPressedY = 0.0;
    private double lastDragX = 0.0;
    private double lastDragY = 0.0;
    // Koch manager
    // TO DO: Create class KochManager in package calculate
    private KochManager kochManager;
    // Current level of Koch fractal
    private int currentLevel = 1;
    // Labels for level, nr edges, calculation time, and drawing time
    private Label lblLevel;
    private Label lblDrawText;
    private Label lblNumberOfEdgesText;
    private Label lblCalcText;
    private Label lblProgressLeftText;
    private Label lblProgressRightText;
    private Label lblProgressBottomText;
    //ProgressBars for number of edges
    private ProgressBar progLeft;
    private ProgressBar progRight;
    private ProgressBar progBottom;
    // Koch panel and its size
    private Canvas kochPanel;
    // counter for snapshot and its threshold (fixes rendering issue)
    private int counter = 0;

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        System.out.println("Application Stopped");
        kochManager.stopPool();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setHeight(800);
        // Define grid pane
        GridPane grid;
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        // For debug purposes
        // Make de grid lines visible
        // grid.setGridLinesVisible(true);

        // Drawing panel for Koch fractal
        kochPanel = new Canvas(kpWidth, kpHeight);
        grid.add(kochPanel, 0, 3, 25, 1);

        // Labels to present number of edges for Koch fractal
        grid.add(new Label("Nr edges:"), 0, 0, 4, 1);
        lblNumberOfEdgesText = new Label();
        grid.add(lblNumberOfEdgesText, 3, 0, 22, 1);

        // Labels to present time of calculation for Koch fractal
        lblCalcText = new Label();
        grid.add(new Label("Calculating:"), 0, 1, 4, 1);
        grid.add(lblCalcText, 3, 1, 22, 1);

        // Labels to present time of drawing for Koch fractal
        lblDrawText = new Label();
        grid.add(new Label("Drawing:"), 0, 2, 4, 1);
        grid.add(lblDrawText, 3, 2, 22, 1);

        // Label to present current level of Koch fractal
        lblLevel = new Label("Level: " + currentLevel);
        grid.add(lblLevel, 0, 6);

        //Labels to present progress of drawing
        grid.add(new Label("Left Edges:"), 1, 7);
        lblProgressLeftText = new Label();
        grid.add(lblProgressLeftText, 3, 7);
        progLeft = new ProgressBar();
        grid.add(progLeft, 0, 7);

        grid.add(new Label("Right Edges:"), 1, 8);
        lblProgressRightText = new Label();
        grid.add(lblProgressRightText, 3, 8);
        progRight = new ProgressBar();
        grid.add(progRight, 0, 8);

        lblProgressBottomText = new Label();
        progBottom = new ProgressBar();
        grid.add(new Label("Bottom Edges:"), 1, 9);
        grid.add(lblProgressBottomText, 3, 9);
        grid.add(progBottom, 0, 9);

        // Button to increase level of Koch fractal
        Button buttonIncreaseLevel = new Button();
        buttonIncreaseLevel.setText("Increase Level");
        buttonIncreaseLevel.setOnAction(this::increaseLevelButtonActionPerformed);
        grid.add(buttonIncreaseLevel, 3, 6);

        // Button to decrease level of Koch fractal
        Button buttonDecreaseLevel = new Button();
        buttonDecreaseLevel.setText("Decrease Level");
        buttonDecreaseLevel.setOnAction(this::decreaseLevelButtonActionPerformed);
        grid.add(buttonDecreaseLevel, 5, 6);

        // Button to fit Koch fractal in Koch panel
        Button buttonFitFractal = new Button();
        buttonFitFractal.setText("Fit Fractal");
        buttonFitFractal.setOnAction(this::fitFractalButtonActionPerformed);
        grid.add(buttonFitFractal, 7, 6);

        // Add mouse clicked event to Koch panel
        kochPanel.addEventHandler(MouseEvent.MOUSE_CLICKED,
                this::kochPanelMouseClicked);

        // Add mouse pressed event to Koch panel
        kochPanel.addEventHandler(MouseEvent.MOUSE_PRESSED,
                this::kochPanelMousePressed);

        // Add mouse dragged event to Koch panel
        kochPanel.setOnMouseDragged(this::kochPanelMouseDragged);

        // Create Koch manager and set initial level
        resetZoom();
        kochManager = new KochManager(this);
        kochManager.changeLevel(currentLevel);

        // Create the scene and add the grid pane
        Group root = new Group();
        Scene scene = new Scene(root, kpWidth + 50, kpHeight + 170);
        root.getChildren().add(grid);

        // Define title and assign the scene for main window
        primaryStage.setTitle("Koch Fractal");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void clearKochPanel() {
        GraphicsContext gc = kochPanel.getGraphicsContext2D();
        gc.clearRect(0.0, 0.0, kpWidth, kpHeight);
        gc.setFill(Color.BLACK);
        gc.fillRect(0.0, 0.0, kpWidth, kpHeight);
        counter = 0;
    }

    public void drawEdge(Edge e) {
        // Graphics
        GraphicsContext gc = kochPanel.getGraphicsContext2D();

        // Adjust edge for zoom and drag
        Edge e1 = edgeAfterZoomAndDrag(e);

        // Set line color
        gc.setStroke(e1.getColor());

        // Set line width depending on level
        if (currentLevel <= 3) {
            gc.setLineWidth(2.0);
        } else if (currentLevel <= 5) {
            gc.setLineWidth(1.5);
        } else {
            gc.setLineWidth(1.0);
        }

        // Draw line
        gc.strokeLine(e1.getX1(), e1.getY1(), e1.getX2(), e1.getY2());
        counter++;

        if (counter >= THRESHOLD) {
            kochPanel.snapshot(null, image);
            counter = 0;
        }

    }

    public void setLblNumberOfEdgesText(String text) {
        lblNumberOfEdgesText.setText(text);
    }

    public void setLblCalcText(String text) {
        lblCalcText.setText(text);
    }

    public void setLblDrawText(String text) {
        lblDrawText.setText(text);
    }

    public Label getLblProgressLeftText() {
        return lblProgressLeftText;
    }

    public Label getLblProgressRightText() {
        return lblProgressRightText;
    }

    public Label getLblProgressBottomText() {
        return lblProgressBottomText;
    }

    public ProgressBar getProgLeft() {
        return progLeft;
    }

    public ProgressBar getProgRight() {
        return progRight;
    }

    public ProgressBar getProgBottom() {
        return progBottom;
    }

    public void requestDrawEdges() {
        Platform.runLater(() -> kochManager.drawEdges());
    }

    private void increaseLevelButtonActionPerformed(ActionEvent event) {
        if (currentLevel < 12) {
            // resetZoom();
            currentLevel++;
            lblLevel.setText("Level: " + currentLevel);
            kochManager.changeLevel(currentLevel);
        }
    }

    private void decreaseLevelButtonActionPerformed(ActionEvent event) {
        if (currentLevel > 1) {
            // resetZoom();
            currentLevel--;
            lblLevel.setText("Level: " + currentLevel);
            kochManager.changeLevel(currentLevel);
        }
    }

    private void fitFractalButtonActionPerformed(ActionEvent event) {
        resetZoom();
        kochManager.drawEdges();
    }

    private void kochPanelMouseClicked(MouseEvent event) {
        if (Math.abs(event.getX() - startPressedX) < 1.0 &&
                Math.abs(event.getY() - startPressedY) < 1.0) {
            double originalPointClickedX = (event.getX() - zoomTranslateX) / zoom;
            double originalPointClickedY = (event.getY() - zoomTranslateY) / zoom;

            if (event.getButton() == MouseButton.PRIMARY) {
                zoom *= 2.0;
            } else if (event.getButton() == MouseButton.SECONDARY) {
                zoom /= 2.0;
            }

            zoomTranslateX = (int) (event.getX() - originalPointClickedX * zoom);
            zoomTranslateY = (int) (event.getY() - originalPointClickedY * zoom);

            kochManager.drawEdges();
        }
    }

    private void kochPanelMouseDragged(MouseEvent event) {
        zoomTranslateX = zoomTranslateX + event.getX() - lastDragX;
        zoomTranslateY = zoomTranslateY + event.getY() - lastDragY;

        lastDragX = event.getX();
        lastDragY = event.getY();

        kochManager.drawEdges();
    }

    private void kochPanelMousePressed(MouseEvent event) {
        startPressedX = event.getX();
        startPressedY = event.getY();

        lastDragX = event.getX();
        lastDragY = event.getY();
    }

    private void resetZoom() {
        int kpSize = Math.min(kpWidth, kpHeight);
        zoom = kpSize;

        zoomTranslateX = (kpWidth - kpSize) / 2.0;
        zoomTranslateY = (kpHeight - kpSize) / 2.0;
    }

    private Edge edgeAfterZoomAndDrag(Edge e) {
        return new Edge(
                e.getX1() * zoom + zoomTranslateX,
                e.getY1() * zoom + zoomTranslateY,
                e.getX2() * zoom + zoomTranslateX,
                e.getY2() * zoom + zoomTranslateY,
                e.getColor());
    }
}
