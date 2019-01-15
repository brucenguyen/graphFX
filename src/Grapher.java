import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import net.objecthunter.exp4j.*;

/*
GraphFX

Bruce Nguyen
Created: November 10, 2018
Last modified: January 14, 2019

'Grapher' handles core functionality of application.
 */

public class Grapher extends Application {

    String version = "Demo";

    private Stage window;
    private Scene mainScene, settingsScene, helpScene;

    boolean liveInput = true;
    boolean darkMode = false; // currently debug
    double zoom = 10; // does not work for less than 10
    double yPosition = 0;
    double xPosition = 0;
    double sensitivity = 3;
    int width = 706;
    int height = 370;

    // Theme colors (debug only, should be implemented with CSS)
    Color colorLvl1 = Color.WHITE;
    Color colorLvl2 = Color.web("#efefef");
    Color colorLvl3 = Color.LIGHTGRAY;
    Color colorLvl4 = Color.web("#303030");
    Color colorLvl5 = Color.BLACK;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        window.setResizable(false); // until implemented

        Interface mainInterface = new Interface();

        /*
        Defining all layouts
         */

        // Main layout
        BorderPane root = new BorderPane();

        // Create buttons for main screen and add them to the scene (causes a bug when added outside of start function)
        Button settingsButton = new Button("", new ImageView(new Image(getClass().getResourceAsStream("settings.png"), 25, 25, true, true)));
        settingsButton.setOnAction(e -> window.setScene(settingsScene));
        Button helpButton = new Button("", new ImageView(new Image(getClass().getResourceAsStream("help.png"), 25, 25, true, true)));
        helpButton.setOnAction(e -> window.setScene(helpScene));
        HBox bottomHBox = new HBox();
        // bottomHBox.getChildren().add(settingsButton); // work in progress
        bottomHBox.getChildren().add(helpButton);

        // Add the modules to the main scene
        root.setBottom(mainInterface.bottomAnchor(bottomHBox));
        root.setTop(mainInterface.topHBox());
        root.setCenter(mainInterface.centerVBox());


        // Settings layout (currently debug only)
        BorderPane rootSettings = new BorderPane();

        Button backButton = new Button("", new ImageView(new Image(getClass().getResourceAsStream("back.png"), 25, 25, true, true)));
        backButton.setOnAction(e -> window.setScene(mainScene));
        Button backButton2 = new Button("", new ImageView(new Image(getClass().getResourceAsStream("back.png"), 25, 25, true, true)));
        backButton2.setOnAction(e -> window.setScene(mainScene));
        HBox settingsHBox = new HBox();
        settingsHBox.getChildren().add(backButton);
        HBox helpHBox = new HBox();
        helpHBox.getChildren().add(backButton2);

        rootSettings.setTop(mainInterface.topHBox());
        rootSettings.setCenter(mainInterface.centerSettings());
        rootSettings.setBottom(mainInterface.bottomSettings(settingsHBox));


        // Help layout
        BorderPane rootHelp = new BorderPane();

        rootHelp.setTop(mainInterface.topHBox());
        rootHelp.setCenter(mainInterface.centerHelp());
        rootHelp.setBottom(mainInterface.bottomSettings(helpHBox));

        mainScene = new Scene(root, 720, 560);
        settingsScene = new Scene(rootSettings, 720,560);
        helpScene = new Scene(rootHelp, 720, 560);


        /*
        Define window properties
         */

        window.setScene(mainScene);
        window.setTitle("GraphFX");
        window.setWidth(720);
        window.setHeight(560);
        window.getIcons().add(new Image("icon.png"));
        window.show();
    }

    void calculate(String func, Group root, Rectangle rect, ImageView error, Color color) {
        error.setVisible(false); // Error is not visible by default
        String input = func; // Working variable of the user input

        String var = "x"; // 'x' by default
        String var2 = "y"; // WIP

        String tempInput = input; // tempinput is for error checking

        // Replace all known functions so they can be skipped by error check
        tempInput = tempInput.replace("sinh", "()");
        tempInput = tempInput.replace("sin", "()");
        tempInput = tempInput.replace("sqrt", "()");
        tempInput = tempInput.replace("sec", "()");
        tempInput = tempInput.replace("cosh", "()");
        tempInput = tempInput.replace("cos", "()");
        tempInput = tempInput.replace("csc", "()");
        tempInput = tempInput.replace("cot", "()");
        tempInput = tempInput.replace("tan", "()");
        tempInput = tempInput.replace("tanh", "()");
        tempInput = tempInput.replace("log", "()");
        tempInput = tempInput.replace("ln", "()");

        if (input.contains("pi")) { // Check if universal constant
            tempInput = tempInput.replace("pi", "(3.14159265359)");
            input = func.replace("pi", "(3.14159265359)");
        }  else if (input.contains("PI")) {
            tempInput = tempInput.replace("PI","(3.14159265359)");
            input = func.replace("PI","(3.14159265359)");
        } else if (input.contains("e")){
            tempInput = tempInput.replace("e", "(2.71828182845)");
            input = func.replace("e", "(2.71828182845)");
        }

        boolean varFound = false, var2Found = false, badFunction = false; // varFound checks if another variable exists, badFunction is if function is in error state
        for (int i = 0; i < tempInput.length() && !badFunction; i++){
            if (((tempInput.charAt(i) >= 97 && tempInput.charAt(i) <= 122) || (tempInput.charAt(i) >= 65 && tempInput.charAt(i) <= 90)) && !varFound) {
                var = Character.toString(tempInput.charAt(i));
                varFound = true;
            } else if (((tempInput.charAt(i) >= 97 && tempInput.charAt(i) <= 122) || (tempInput.charAt(i) >= 65 && tempInput.charAt(i) <= 90)) && varFound) {
                if (tempInput.charAt(i) != var.charAt(0)) {
                    /*var2 = Character.toString(tempInput.charAt(i)); // WIP
                    var2Found = true;*/
                    badFunction = true;
                    error.setVisible(true);
                    Tooltip.install(error, new Tooltip("Current variable is " + var + ". Detected " + tempInput.charAt(i) + ". GraphFX only supports single variable functions!"));
                }
                /*if (!tempInput.contains("=")) {
                    badFunction = true;
                    System.err.println("Error: 2 variables detected, try putting an equals sign.");
                }*/
            } /*else if (((tempInput.charAt(i) >= 97 && tempInput.charAt(i) <= 122) || (tempInput.charAt(i) >= 65 && tempInput.charAt(i) <= 90)) && varFound && var2Found) {
                if (tempInput.charAt(i) != var.charAt(0) && tempInput.charAt(i) != var2.charAt(0)) {
                    badFunction = true;
                    System.err.println("Error: Maximum 2 variables. Received " + tempInput.charAt(i));
                }
            }*/
        }

        if (varFound && !var2Found && !badFunction) { // If a valid function is passed
            double lastValue = 0, lastIndex = xPosition - zoom;

            try {
                Expression outputTest = new ExpressionBuilder(input).variables(var).build().setVariable(var, xPosition - zoom); // Generate function with exp4j
                lastValue = outputTest.evaluate();
            } catch (Exception e) { // If function cannot be generated
                error.setVisible(true);
                Tooltip.install(error, new Tooltip("Cannot recognize input. Please check your syntax."));
                badFunction = true;
            }
            for (double j = xPosition - zoom; j < (xPosition + zoom) && !badFunction; j = j + (zoom/10)*Math.pow(10, -sensitivity)) { // Draw function
                try {
                    Expression output = new ExpressionBuilder(input).variables(var).build().setVariable(var, j);
                    if (output.evaluate() > (-(rect.getHeight()/2)/(rect.getWidth()/(1.99*zoom)) + yPosition) && output.evaluate() < ((rect.getHeight()/2)/(rect.getWidth()/(1.99*zoom)) + yPosition) &&
                            lastValue > -((rect.getHeight()/2)/(rect.getWidth()/(1.99*zoom)) + yPosition) && lastValue < ((rect.getHeight()/2)/(rect.getWidth()/(1.99*zoom))) + yPosition) {
                        Line tempLine = new Line(getX(lastIndex, rect), getY(lastValue, rect),
                                getX(j, rect), getY(output.evaluate(), rect));
                        tempLine.strokeLineJoinProperty();
                        tempLine.setStrokeWidth(2.0f);
                        tempLine.setStroke(color);
                        root.getChildren().add(tempLine);

                        lastValue = output.evaluate();
                        lastIndex = j;
                    } else {
                        lastValue = output.evaluate();
                        lastIndex = j;
                    }
                } catch (Exception e) {
                    error.setVisible(true);
                    Tooltip.install(error, new Tooltip("Cannot evaluate function at " + var + " = " + j));
                    badFunction = true;
                }
            }
        }
        Line support = new Line(0, 0, width, height); // Support is needed so the graph displays correctly
        support.setStroke(Color.TRANSPARENT);
        root.getChildren().add(support);
    }


    /*
    getX and getY takes in cartesian values and converts it into units that can be correctly interpreted by JavaFX
     */

    double getX(double valueX, Rectangle root){
        // Default zoom is -10 to 10
        double centerX = root.getWidth() / 2;
        double unit = root.getWidth()/(2*zoom);
        return ((valueX + xPosition) * unit) + centerX;
    }

    double getY(double valueY, Rectangle root){
        double centerY = root.getHeight() / 2;
        double unit = root.getWidth()/(2*zoom);
        return (root.getHeight()-(((valueY + yPosition) * unit) + centerY));
    }
}