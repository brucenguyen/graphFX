import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/*
'Interface' handles layout of GUI
 */

public class Interface extends Grapher {

    /*
    Defines main screen objects
     */

    AnchorPane bottomAnchor(HBox hb){
        AnchorPane anchorpane = new AnchorPane();
        anchorpane.setStyle("-fx-background-color: #303030;");

        hb.setPadding(new Insets(10, 10, 10 , 10));
        hb.setSpacing(10);

        anchorpane.getChildren().add(hb);
        AnchorPane.setBottomAnchor(hb, 0.0);
        AnchorPane.setRightAnchor(hb, 5.0);

        return anchorpane;
    }

    HBox topHBox(){
        HBox hb = new HBox();
        hb.setStyle("-fx-background-color: #303030;");
        hb.setPadding(new Insets(10, 10, 10, 10));
        hb.setSpacing(5);

        Label titleLabel = new Label("GraphFX");
        titleLabel.setTextFill(Color.WHITE);
        Label versionLabel = new Label(version);
        versionLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI Light", 24));
        versionLabel.setFont(new Font("Segoe UI Light", 10));

        hb.getChildren().addAll(titleLabel, versionLabel);
        hb.setAlignment(Pos.BOTTOM_CENTER);

        return hb;
    }

    VBox centerVBox() {
        VBox vb = new VBox();
        vb.setAlignment(Pos.CENTER);

        StackPane sp = new StackPane();

        Rectangle bg = new Rectangle(width, height);
        bg.setFill(colorLvl1);
        Group plot = new Group();
        Group axes = new Group();

        AnchorPane errorIcon = new AnchorPane();
        ImageView errorLabel = new ImageView(new Image(getClass().getResourceAsStream("error.png"), 30, 30, true, true));
        errorIcon.getChildren().add(errorLabel);
        errorIcon.setTopAnchor(errorLabel, 15.0);
        errorIcon.setRightAnchor(errorLabel, 25.0);
        errorLabel.setVisible(false);
        errorLabel.setPickOnBounds(true);

        sp.getChildren().addAll(axes, plot, errorIcon);
        axes.getChildren().add(bg);

        // Drawing reference lines
        drawAxes(axes, bg);

        // User input + color picker
        final TextField funcInput = new TextField();
        funcInput.setPromptText("Set function ...");
        funcInput.setPrefColumnCount(30);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                funcInput.requestFocus();
            }
        });

        final ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue(colorLvl5);
        colorPicker.setStyle("-fx-color-label-visible: false ;");

        colorPicker.setOnAction(e -> {
            plot.getChildren().clear();
            calculate(funcInput.getText(), plot, bg, errorLabel, colorPicker.getValue());
        });

        GridPane inputList = new GridPane();
        inputList.setAlignment(Pos.CENTER);
        inputList.setHgap(10);
        inputList.setVgap(5);
        inputList.setPadding(new Insets(10, 10, 10, 10));
        inputList.add(funcInput, 1, 0);
        inputList.add(colorPicker, 0,0);

        vb.getChildren().addAll(sp, inputList);

        if (liveInput) {
            funcInput.setOnKeyReleased(e -> {
                plot.getChildren().clear();
                calculate(funcInput.getText(), plot, bg, errorLabel, colorPicker.getValue());
            });
        } else {
            funcInput.setOnAction(e -> {
                plot.getChildren().clear();
                calculate(funcInput.getText(), plot, bg, errorLabel, colorPicker.getValue());
            });
        }
        return vb;
    }

    // Draws the axes on initial startup
    private void drawAxes(Group axes, Rectangle bg){
        Label tempNum = new Label("0");
        Font labelFont = new Font("Calibri", 12);
        tempNum.setFont(labelFont);
        tempNum.setTranslateX(getX(0, bg) - 9);
        tempNum.setTranslateY(getY(0, bg));
        axes.getChildren().add(tempNum);

        for (double x = xPosition + (zoom/10); x < xPosition + zoom; x = x + (zoom/10)){
            Line tempLine = new Line(getX(x, bg), 0, getX(x, bg), height);
            tempLine.setStroke(colorLvl2);
            axes.getChildren().add(tempLine);
            if (x % (zoom/5) == 0) {
                tempLine.setStroke(colorLvl3);
                tempNum = new Label(String.format("%.0f", x));
                tempNum.setFont(labelFont);
                tempNum.setTranslateX(getX(x, bg) - 3);
                tempNum.setTranslateY(getY(0, bg));
                axes.getChildren().add(tempNum);
            }
        }
        for (double x = xPosition - (zoom/10); x > xPosition - zoom; x = x - (zoom/10)){
            Line tempLine = new Line(getX(x, bg), 0, getX(x, bg), height);
            tempLine.setStroke(colorLvl2);
            axes.getChildren().add(tempLine);
            if (x % (zoom/5) == 0) {
                tempLine.setStroke(colorLvl3);
                tempNum = new Label(String.format("%.0f", x));
                tempNum.setFont(labelFont);
                tempNum.setTranslateX(getX(x, bg) - 7);
                tempNum.setTranslateY(getY(0, bg));
                axes.getChildren().add(tempNum);
            }
        }
        for (double y = yPosition + (zoom/10); y < (bg.getHeight()/2)/(bg.getWidth()/(2*zoom)); y = y + (zoom/10)){
            Line tempLine = new Line(0, getY(y, bg), width, getY(y, bg));
            tempLine.setStroke(colorLvl2);
            axes.getChildren().add(tempLine);
            if (y % (zoom/5) == 0) {
                tempLine.setStroke(colorLvl3);
                tempNum = new Label(String.format("%.0f", y));
                tempNum.setFont(labelFont);
                tempNum.setTranslateX(getX(0, bg) - 9);
                tempNum.setTranslateY(getY(y, bg) - 7);
                axes.getChildren().add(tempNum);
            }
        }
        for (double y = yPosition - (zoom/10); y > -(bg.getHeight()/2)/(bg.getWidth()/(2*zoom)); y = y - (zoom/10)){
            Line tempLine = new Line(width, getY(y, bg), 0, getY(y, bg));
            tempLine.setStroke(colorLvl2);
            axes.getChildren().add(tempLine);
            if (y % (zoom/5) == 0) {
                tempLine.setStroke(colorLvl3);
                tempNum = new Label(String.format("%.0f", y));
                tempNum.setFont(labelFont);
                tempNum.setTranslateX(getX(0, bg) - 13);
                tempNum.setTranslateY(getY(y, bg) - 7);
                axes.getChildren().add(tempNum);
            }
        }

        // Drawing axes
        Line xAxis = new Line(0, getY(0, bg), width, getY(0, bg));
        Line yAxis = new Line(getX(0, bg), 0, getX(0, bg), height);
        xAxis.setStroke(colorLvl5);
        xAxis.setStrokeWidth(1.2f);
        yAxis.setStroke(colorLvl5);
        yAxis.setStrokeWidth(1.2f);

        Rectangle border = new Rectangle(width, height);
        border.setStroke(Color.LIGHTSKYBLUE);
        border.setFill(Color.TRANSPARENT);

        axes.getChildren().addAll(xAxis, yAxis, border);
    }

    /*
    Defines settings screen layout (debug only)
     */

    GridPane centerSettings(){
        GridPane gp = new GridPane();
        gp.setAlignment(Pos.CENTER);
        gp.setHgap(50);
        gp.setVgap(30);
        gp.setPadding(new Insets(10, 10, 10, 10));

        Font settingsFont = new Font("Segoe UI Light", 18);

        /* Currently not implemented since it uses external libraries

        Label liveLabel = new Label("Live input");
        liveLabel.setFont(settingsFont);
        gp.add(liveLabel, 0, 0);
        Tooltip.install(liveLabel, new Tooltip("Enables live updating of function input. Disable if function input is slow. Default: On."));
        ToggleSwitch liveSwitch = new ToggleSwitch();
        liveSwitch.setSelected(liveInput);
        liveSwitch.selectedProperty().addListener((o, old, newValue) -> {
            liveInput = liveSwitch.selectedProperty().getValue();
        });
        gp.add(liveSwitch, 1, 0);

        Label darkLabel = new Label("Dark mode");
        darkLabel.setFont(settingsFont);
        gp.add(darkLabel, 0, 1);
        ToggleSwitch darkSwitch = new ToggleSwitch();
        darkSwitch.setSelected(darkMode);
        darkSwitch.selectedProperty().addListener((o, old, newValue) -> {
            darkMode = darkSwitch.selectedProperty().getValue();
        });
        gp.add(darkSwitch, 1, 1);
        */

        Label sensLabel = new Label("Line accuracy");
        Tooltip.install(sensLabel, new Tooltip("Sets accuracy of plotted line. Higher values will slow down the program! Default value: 3."));
        sensLabel.setFont(settingsFont);
        gp.add(sensLabel, 0, 2);
        Slider sensSlider = new Slider(1,5, sensitivity);
        sensSlider.setShowTickLabels(true);
        sensSlider.setShowTickMarks(true);
        sensSlider.setMajorTickUnit(4);
        sensSlider.setSnapToTicks(true);
        gp.add(sensSlider, 1, 2);

        return gp;
    }

    AnchorPane bottomSettings(HBox hb) {
        AnchorPane anchorpane = new AnchorPane();
        anchorpane.setStyle("-fx-background-color: #303030;");

        hb.setPadding(new Insets(10, 10, 10 , 10));
        hb.setSpacing(10);

        anchorpane.getChildren().add(hb);
        AnchorPane.setBottomAnchor(hb, 0.0);
        AnchorPane.setLeftAnchor(hb, 5.0);

        return anchorpane;
    }

    /*
    Defines help screen objects
     */

    VBox centerHelp() {
        VBox vb = new VBox(30);
        vb.setAlignment(Pos.CENTER);
        vb.setPadding(new Insets(10, 20, 10 , 20));

        Label intro = new Label();
        intro.setText(
                "GraphFX is a simple graphing calculator, using it is just as simple. In the text-box, you can input any single variable function you desire. You are also free to assign any variable you'd like (except for e). " +
                "You can also use special functions such as sinusoidal and logarithmic functions. For example, you can try 'sin(x)' or 'log(x)'."
        );
        intro.setWrapText(true);
        intro.setFont(new Font("Segoe UI Light", 18));

        Label notes =  new Label();
        notes.setText(
                "This calculator is a work in progress project, and it will have some issues. If you find that your function is not being recognized by the program, it may be a limitation of exp4j, an external library " +
                "which allows the program to read your functions. Future features will include a interactive graph, where you can zoom and move around as you desire."
        );
        notes.setWrapText(true);
        notes.setFont(new Font("Segoe UI Light", 18));

        Label credits = new Label();
        credits.setText(
                "GraphFX was created in JavaFX with special thanks to exp4j. \n© Bruce Nguyen"
        );
        credits.setWrapText(true);
        credits.setTextAlignment(TextAlignment.CENTER);
        credits.setFont(new Font("Segoe UI Light", 12));

        vb.getChildren().addAll(intro, notes, credits);
        return vb;
    }
}
