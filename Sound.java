
/*
 * MiNiSYNTH - A JavaFX tool by:
 * - Clark Lavery (mentor)
 * - Evert Visser (s3727884)
 * - Duncan Baxter (s3737140)
 * - Kira Macarthur (s3742864)
 * - Dao Kun Nie (s3691571)
 * - Michael Power (s3162668)
 * - John Zealand-Doyle (s3319550)
 * 
 * Duncan can answer queries in relation to this Class.
 */

import java.io.File;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/*
 * Top-level Class for the tool
 */
public class Sound extends Application {
    Stage stage; // Stage for JavaFX application
    public static GridPane root; // Root node for the application - a GridPane
    public static GridPane gp; // Node for the mp3player - a GridPane
    Envelope env; // Global storage for the active Envelope
    int duration; // Total duration of the current settings
    Button btPlay; // Global storage for the "Play a tone" button
    String[] names = { "Origin", "Attack", "Decay", "Sustain", "Release" }; // Names for the 4 phases
    int[] durations = { 0, 50, 50, 50, 50 }; // Initial durations of the 4 phases
    int[] positions = { 0, 50, 100, 150, 200 }; // Cumulative durations of the 4 phases
    double[] levels = { 0.0, 1.0, 0.75, 0.5, 0 }; // Volume levels for the 4 phases
    Tune loaded = new Tune("resources/Silicon_Theme_Funk.mp3");

    /*
     * The usual "main" method - this code is only executed on platforms that lack
     * full JavaFX support.
     */
    public static void main(String[] args) {
	Application.launch(args);
    }

    /*
     * JavaFX Application thread automatically calls start() method. The parameter
     * Stage stage is our top-level window, then Scene scene, GridPane root, and all
     * the other Nodes. This method is quite short: it just creates the GUI.
     * 
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage primaryStage) {
	stage = primaryStage;
	// Set the Style for the primary Stage
	stage.initStyle(StageStyle.DECORATED);
	// Set the title of the primary Stage
	stage.setTitle("MiNiSYNTH");
	// Create the GridPane and its contents
	createGridPane();
	// Create a Scene based on the GridPane with a dark grey background
	Scene scene = new Scene(root, Color.DIMGRAY);
	// Add the Scene to the primary Stage and resize
	stage.setScene(scene);
	stage.show();
	btPlay.requestFocus();
    }

    /*
     * createGridPane() method: creates GridPane and its contents.
     */
    private boolean createGridPane() {
	// Create a GridPane to hold the MiNiSYNTH
	root = new GridPane();
	root.getStyleClass().add("grid-pane");

	// Load the JavaFX CSS StyleSheet
	root.getStylesheets().add(getClass().getResource("SoundStyles.css").toString());

	// Load the "MiNiSYNTH" logo as a new Image resource into an ImageView and add
	// it to the GridPane
	ImageView iv = new ImageView(new Image(getClass().getResourceAsStream("MiNiSYNTH.png")));
	iv.setPreserveRatio(true);
	root.add(iv, 0, 0, 4, 1);

	// Create a VBox to hold the Wave radio buttons
	VBox vbWave = new VBox();
	vbWave.setId("VBox-buttons");
	Label lWave = new Label("WAVES\n[To Do]");
	vbWave.getChildren().add(lWave);
	root.add(vbWave, 4, 0, 2, 1);

	//
	createMP3player();

	// Define the x and y NumberAxis
	NumberAxis xAxis = new NumberAxis();
	xAxis.setLabel("MILLISECONDS");
	NumberAxis yAxis = new NumberAxis(0.0, 1.00, 0.25);
	yAxis.setLabel("VOLUME");

	// Prepare XYChart.Series objects by setting data elements
	XYChart.Series<Integer, Double> series = new XYChart.Series<>();
	series.setName("ADSR PARAMETERS");
	for (int i = 0; i < 5; i++) {
	    series.getData().add(new Data<Integer, Double>(positions[i], levels[i]));
	}

	// Create the Line Chart and add it to the GridPane
	@SuppressWarnings({ "rawtypes", "unchecked" })
	LineChart<Integer, Double> linechart = new LineChart(xAxis, yAxis);
	linechart.getData().add(series);
	linechart.setTitle("ADSR ENVELOPE");
	root.add(linechart, 0, 1, 4, 1);

	// Create a TextArea to display the active Envelope settings
	TextArea ta = new TextArea(updateTable());
	ta.setEditable(false);
	root.add(ta, 4, 1, 2, 1);

	// Create the 3 x Buttons
	Button btEnv = new Button("SET ENVELOPE");
	btEnv.setTooltip(new Tooltip("Press this button to update the ADSR Envelope with the current settings"));
	btEnv.setOnAction(ae -> {
	    ta.setText(updateTable());
	    env = new Envelope(durations, levels);
	});

	btPlay = new Button("PLAY TONE");
	btPlay.setTooltip(new Tooltip("Press this button to play a Tone with the current settings"));
	btPlay.setOnMousePressed(me -> new Thread(new Tone(262, duration)).start());
	btPlay.setOnAction(ae -> System.out.println("Play"));

	Button btExit = new Button("EXIT");
	btExit.setTooltip(new Tooltip("Press this button when you've had enough"));
	btExit.setOnAction(ae -> System.exit(0));

	// Create a VBox to hold the 3 x Buttons
	VBox vb = new VBox();
	vb.setId("VBox-buttons");
	vb.getChildren().addAll(btEnv, btPlay, btExit);
	root.add(vb, 6, 1, 2, 1);

	// Create the 8 x TextFields, Sliders and Labels
	for (int i = 0; i < 8; i++) {
	    final int index = 1 + i / 2;
	    final boolean isDuration = (i % 2 == 0);
	    ColumnConstraints cc = new ColumnConstraints();
	    cc.setPercentWidth(12.5d);
	    cc.setHalignment(HPos.CENTER);
	    root.getColumnConstraints().add(cc);
	    Slider s;
	    TextField t;
	    Label l;

	    // Set unique parameters for the 4 x "duration" variables
	    if (isDuration) {
		s = new Slider(0, 100, durations[index]);
		s.setMajorTickUnit(25.0f);
		s.setBlockIncrement(10.0f);
		t = new TextField(String.format("%.0f", s.getValue()));
		l = new Label(names[index] + "\n(ms)");
		s.valueProperty().addListener((ov, oldValue, newValue) -> {
		    durations[index] = newValue.intValue();
		    t.setText(String.format("%d", durations[index]));
		    for (int j = index; j < 5; j++) {
			positions[j] = positions[j - 1] + durations[j];
			series.getData().set(j, new XYChart.Data<Integer, Double>(positions[j], levels[j]));
		    }
		});

		// Set unique parameters for the 4 x "level" (i.e. volume) variables
	    } else {
		s = new Slider(0.0, 1.0, levels[index]);
		s.setMajorTickUnit(0.25f);
		s.setBlockIncrement(0.1f);
		t = new TextField();
		t.textProperty().bind(Bindings.format("%.2f", s.valueProperty()));
		l = new Label(names[index] + "\n(volume)");
		s.valueProperty().addListener((ov, oldValue, newValue) -> {
		    levels[index] = newValue.doubleValue();
		    series.getData().set(index, new XYChart.Data<Integer, Double>(positions[index], levels[index]));
		});
	    }
	    // Set shared parameters for the 8 x variables (most are set in the CSS file)
	    t.setEditable(false);

	    // Create a VBox to hold the TextField, Slider and Label for a variable
	    VBox v = new VBox(t, s, l);
	    v.setId("VBox-variable");
	    root.add(v, i, 2);
	}

	// Set Grid-lines-visible during debug
	// root.setGridLinesVisible(true);

	// Signal that we need to layout the GridPane (ie. the Nodes are done)
	root.needsLayoutProperty();
	return true;
    }

    /*
     * updateTable(): Format and display the active envelope settings. This method
     * is used to initialise the TextArea (ta), and whenever Button (btnEnv) is
     * pressed. Returns the formatted settings as a String.
     */
    private String updateTable() {
	duration = 0;
	String str = "========================";
	str += "\nACTIVE ENVELOPE SETTINGS";
	str += "\n------------------------";
	for (int i = 1; i < 5; i++) {
	    str += String.format("\n%-16s%8d", names[i] + " (ms)", durations[i]);
	    duration += durations[i];
	}
	str += "\n------------------------";
	str += String.format("\n%-16s%8d", "TOTAL TIME (ms)", duration);
	str += "\n------------------------";
	for (int i = 1; i < 5; i++) {
	    str += String.format("\n%-16s%8.2f", names[i] + " (volume)", levels[i]);
	}
	str += "\n========================";
	return str;
    }

    private void createMP3player() {
	// Create a GridPane to hold the mp3player
	gp = new GridPane();
	gp.setId("grid-pane-small");

	// Set Grid-lines-visible during debug
	// gp.setGridLinesVisible(true);

	// Create the duration control Slider
	loaded.mp.setOnReady(() -> {
	    Slider sTime = new Slider(loaded.mp.getStartTime().toMinutes(), loaded.mp.getStopTime().toMinutes(),
		    loaded.mp.getCurrentTime().toMinutes());
	    sTime.setId("TimeSlider");
	    sTime.setTooltip(new Tooltip("Use this Slider to control playback duration"));
	    InvalidationListener sliderChangeListener = o -> loaded.mp.seek(Duration.minutes(sTime.getValue()));
	    sTime.valueProperty().addListener(sliderChangeListener);
	    loaded.mp.startTimeProperty().addListener((ov, oldValue, newValue) -> sTime.setMin(newValue.toMinutes()));
	    loaded.mp.stopTimeProperty().addListener((ov, oldValue, newValue) -> sTime.setMax(newValue.toMinutes()));
	    loaded.mp.currentTimeProperty().addListener(l -> {
		sTime.valueProperty().removeListener(sliderChangeListener);
		sTime.setValue(loaded.mp.getCurrentTime().toMinutes());
		sTime.valueProperty().addListener(sliderChangeListener);
	    });
	    gp.add(sTime, 0, 0, 4, 1);
	    root.needsLayoutProperty();
	});

	// Create the volume control Slider
	Slider sVol = new Slider(0.0, 1.0, loaded.mp.getVolume());
	sVol.setId("VolSlider");
	sVol.setTooltip(new Tooltip("Use this Slider to control playback volume"));
	sVol.valueProperty().addListener((ov, oldValue, newValue) -> {
	    loaded.mp.setVolume(newValue.doubleValue());
	});
	gp.add(sVol, 0, 2, 4, 1);

	// Define all 7 x Buttons - this is needed up-front because they replace
	// themselves with each other
	Button btEject = new Button("", new ImageView(new Image(getClass().getResourceAsStream("Eject.png"))));
	btEject.setId("button-round");
	btEject.setTooltip(new Tooltip("Press this button to select a new mp3"));

	Button btBack = new Button("", new ImageView(new Image(getClass().getResourceAsStream("Backward.png"))));
	btBack.setId("button-round");
	btBack.setTooltip(new Tooltip("Press this button to skip backwards 30 seconds"));

	Button bt2Start = new Button("", new ImageView(new Image(getClass().getResourceAsStream("Back2Start.png"))));
	bt2Start.setId("button-round");
	bt2Start.setTooltip(new Tooltip("Press this button to restart the mp3"));

	Button btPlay = new Button("", new ImageView(new Image(getClass().getResourceAsStream("Play.png"))));
	btPlay.setId("button-round");
	btPlay.setTooltip(new Tooltip("Press this button to start playback"));

	Button btStop = new Button("", new ImageView(new Image(getClass().getResourceAsStream("Stop.png"))));
	btStop.setId("button-round");
	btStop.setTooltip(new Tooltip("Press this button to stop playback"));

	Button btFwd = new Button("", new ImageView(new Image(getClass().getResourceAsStream("Forward.png"))));
	btFwd.setId("button-round");
	btFwd.setTooltip(new Tooltip("Press this button to skip forwards 30 seconds"));

	Button bt2End = new Button("", new ImageView(new Image(getClass().getResourceAsStream("Forward2End.png"))));
	bt2End.setId("button-round");
	bt2End.setTooltip(new Tooltip("Press this button to skip to the end of the mp3"));

	// Define actions for all 7 x Buttons
	// Actions for "Eject" Button (at 0, 1)
	btEject.setOnAction(ae -> {
	    FileChooser fc = new FileChooser();
	    fc.setTitle("Select a new mp3");
	    fc.getExtensionFilters().addAll(new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
		    new ExtensionFilter("All Files", "*.*"));
	    File f = fc.showOpenDialog(stage);
	    if (f != null) {
		loaded.mp.stop();
		loaded = new Tune(f);
	    }
	});

	// Actions for "Seek Backwards" Button (at 1, 1)
	btBack.setOnAction(ae -> {
	    loaded.mp.seek(loaded.mp.getCurrentTime().subtract(Duration.seconds(30.0d)));
	    gp.getChildren().remove(btBack);
	    gp.add(bt2Start, 1, 1);
	});

	// Actions for "Seek to Start" Button (at 1, 1)
	bt2Start.setOnAction(ae -> {
	    loaded.mp.seek(loaded.mp.getStartTime());
	    gp.getChildren().remove(bt2Start);
	    gp.add(btBack, 1, 1);
	});

	// Actions for "Play" Button (at 2, 1)
	btPlay.setOnAction(ae -> {
	    loaded.mp.play();
	    gp.getChildren().remove(btPlay);
	    gp.add(btStop, 2, 1);
	});

	// Actions for "Stop" Button (at 2, 1)
	btStop.setOnAction(ae -> {
	    loaded.mp.stop();
	    gp.getChildren().remove(btStop);
	    gp.add(btPlay, 2, 1);
	});

	// Actions for "Seek Forwards" Button (at 3, 1)
	btFwd.setOnAction(ae -> {
	    loaded.mp.seek(loaded.mp.getCurrentTime().add(Duration.seconds(30.0d)));
	    gp.getChildren().remove(btFwd);
	    gp.add(bt2End, 3, 1);
	});

	// Actions for "Seek to End" Button (at 3, 1)
	bt2End.setOnAction(ae -> {
	    loaded.mp.seek(loaded.mp.getStopTime());
	    gp.getChildren().remove(bt2End);
	    gp.add(btFwd, 3, 1);
	});
	gp.addRow(1, btEject, btBack, btPlay, btFwd);
	root.add(gp, 6, 0, 2, 1);
    }
}