
/*
 * GRP-COSC2635 2D
 * (a.k.a. ERROR 404: TEAM NAME NOT FOUND)
 * MiNiSYNTH - A JavaFX tool by:
 * - Clark Lavery (mentor)
 * - Evert Visser (s3727884)
 * - Duncan Baxter (s3737140)
 * - Kira Macarthur (s3742864)
 * - Dao Kun Nie (s3691571)
 * - John Zealand-Doyle (s3319550)
 * - ex-team member Michael Power (s3162668)
 * 
 * Duncan can answer queries in relation to this Class.
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Timer;

import javafx.application.Application;
import javafx.application.Platform;
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
    Envelope env; // Global storage for the active Envelope
    Button btPlay; // Global storage for the "Play a tone" Button
    Timer tTime; // Global storage for the MP3player slider Timer
    ActionListener alTime; // Global storage for the ActionListener for the MP3player slider Timer
    int duration; // Total duration of the current settings
    String[] names = { "Origin", "Attack", "Decay", "Sustain", "Release" }; // Names for the phases
    int[] durations = { 0, 50, 50, 50, 50 }; // Initial durations of the phases
    int[] positions = { 0, 50, 100, 150, 200 }; // Cumulative durations of the phases
    double[] levels = { 0.0, 1.0, 0.75, 0.5, 0.0 }; // Volume levels for the phases
    Tune loaded = new Tune("Silicon_Theme_Funk.mp3");

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
	// Create a Scene with a GridPane as root and a dark grey background
	Scene scene = new Scene(createGridPane(), Color.DIMGRAY);
	scene.getStylesheets().add("SoundStyles.css");
	root.requestLayout();
	// Add the Scene to the primary Stage and resize
	stage.setScene(scene);
	stage.show();
    }

    /*
     * JavaFX Application thread automatically calls stop() method. It gives us a
     * chance to clean up the MP3player slider Timer and the MP3 before the JavaFX
     * application exits.
     * 
     * @see javafx.application.Application#stop()
     */
    @Override
    public void stop() {
	tTime.removeActionListener(alTime);
	tTime.stop();
	loaded.mp.dispose();
    }

    /*
     * createGridPane() method: creates GridPane and its contents.
     */
    private GridPane createGridPane() {
	// Create a GridPane to hold the MiNiSYNTH
	root = new GridPane();
	root.getStyleClass().add("grid-pane");

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

	/*
	 * Create the MP3player with sliders for playing time and volume, and various
	 * button controls. You can select and play an mp3 file (or another file-type
	 * JavaFX supports - e.g. wav).
	 */
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

	Button btExit = new Button("EXIT");
	btExit.setTooltip(new Tooltip("Press this button when you've had enough"));
	btExit.setOnAction(ae -> Platform.exit());

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

	// Set GridLinesVisible(true) during debug
	// root.setGridLinesVisible(true);
	return root;
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

    /*
     * createMP3player(): Create a simple mp3 player in the top-right corner of the
     * MiNiSYNTH player. It has the "funk" version of Kira's game theme
     * pre-installed, but can also load other files. The mp3 player includes the
     * usual controls, including sliders for play-back and volume control.
     */
    private void createMP3player() {
	// Queue the "createMP3player()" code to execute when the MediaPlayer is
	// 'READY'. Note this method does not block, so we return immediately and can
	// continue to create the rest of the MiNiSYNTH.
	loaded.mp.setOnReady(() -> {
	    // Create a GridPane to hold the mp3player
	    GridPane gp = new GridPane();
	    gp.setId("grid-pane-small");

	    // Set GridLinesVisible(true) during debug
	    // gp.setGridLinesVisible(true);

	    // Create slider for MediaPlayer time control
	    Slider sTime = new Slider(loaded.mp.getStartTime().toMinutes(), loaded.mp.getStopTime().toMinutes(),
		    loaded.mp.getCurrentTime().toMinutes());
	    sTime.setId("TimeSlider");
	    sTime.setTooltip(new Tooltip("Use this Slider to control playback duration"));

	    /*
	     * Create a 0.1 second Timer to sync the MediaPlayer and the Slider. If the user
	     * is moving the Slider then copy the Slider's position to the MediaPlayer.
	     * Otherwise, copy the MediaPlayer's position to the Slider.
	     */
	    alTime = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    if (sTime.isValueChanging()) {
			loaded.mp.seek(Duration.minutes(sTime.getValue()));
		    } else {
			sTime.setValue(loaded.mp.getCurrentTime().toMinutes());
		    }
		}
	    };
	    tTime = new Timer(100, alTime);
	    tTime.start();

	    // Create slider for MediaPlayer volume control
	    Slider sVol = new Slider(0.0, 1.0, loaded.mp.getVolume());
	    sVol.setId("VolSlider");
	    sVol.setTooltip(new Tooltip("Use this Slider to control playback volume"));

	    // Bind the MediaPlayer's volume to the Slider's position
	    loaded.mp.volumeProperty().bind(Bindings.createDoubleBinding(() -> sVol.getValue(), sVol.valueProperty()));

	    // Define all 7 x MediaPlayer Buttons (this is needed up-front because they
	    // replace themselves with each other)
	    Button btEject = new Button("", new ImageView(new Image(getClass().getResourceAsStream("Eject.png"))));
	    btEject.setId("button-round");
	    btEject.setTooltip(new Tooltip("Press this button to select a new mp3"));

	    Button btBack = new Button("", new ImageView(new Image(getClass().getResourceAsStream("Backward.png"))));
	    btBack.setId("button-round");
	    btBack.setTooltip(new Tooltip("Press this button to skip backwards 10 seconds"));

	    Button bt2Start = new Button("",
		    new ImageView(new Image(getClass().getResourceAsStream("Back2Start.png"))));
	    bt2Start.setId("button-round");
	    bt2Start.setTooltip(new Tooltip("Press this button to restart the mp3"));

	    Button btPlay = new Button("", new ImageView(new Image(getClass().getResourceAsStream("Play.png"))));
	    btPlay.setId("button-round");
	    btPlay.setTooltip(new Tooltip("Press this button to start playback"));

	    Button btPause = new Button("", new ImageView(new Image(getClass().getResourceAsStream("Pause.png"))));
	    btPause.setId("button-round");
	    btPause.setTooltip(new Tooltip("Press this button to pause playback"));

	    Button btStop = new Button("", new ImageView(new Image(getClass().getResourceAsStream("Stop.png"))));
	    btStop.setId("button-round");
	    btStop.setTooltip(new Tooltip("Press this button to stop playback"));

	    Button btFwd = new Button("", new ImageView(new Image(getClass().getResourceAsStream("Forward.png"))));
	    btFwd.setId("button-round");
	    btFwd.setTooltip(new Tooltip("Press this button to skip forwards 10 seconds"));

	    Button bt2End = new Button("", new ImageView(new Image(getClass().getResourceAsStream("Forward2End.png"))));
	    bt2End.setId("button-round");
	    bt2End.setTooltip(new Tooltip("Press this button to skip to the end of the mp3"));

	    // Define actions for all 7 x MediaPlayer Buttons
	    // Actions for "Eject" Button (at 0, 1)
	    btEject.setOnAction(ae -> {
		FileChooser fc = new FileChooser();
		fc.setTitle("Select a new mp3");
		fc.getExtensionFilters().addAll(new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
			new ExtensionFilter("All Files", "*.*"));
		File f = fc.showOpenDialog(stage);
		if (f != null) {
		    tTime.removeActionListener(alTime);
		    tTime.stop();
		    loaded.mp.dispose();
		    root.getChildren().remove(gp);
		    loaded = new Tune(f);
		    createMP3player();
		}
	    });

	    // Actions for "Seek Backwards" Button (at 1, 1)
	    btBack.setOnAction(ae -> {
		loaded.mp.seek(loaded.mp.getCurrentTime().subtract(Duration.seconds(10.0d)));
		gp.getChildren().remove(btBack);
		gp.add(bt2Start, 1, 1);
	    });

	    // Actions for "Seek to Start" Button (at 1, 1)
	    bt2Start.setOnAction(ae -> {
		loaded.mp.seek(loaded.mp.getStartTime());
//		gp.getChildren().remove(bt2Start);
//		gp.add(btBack, 1, 1);
	    });

	    // Actions for "Play" Button (at 2, 1)
	    btPlay.setOnAction(ae -> {
		loaded.mp.play();
		gp.getChildren().remove(btPlay);
		gp.add(btPause, 2, 1);
	    });

	    // Actions for "Pause" Button (at 2, 1)
	    btPause.setOnAction(ae -> {
		loaded.mp.pause();
		gp.getChildren().remove(btPause);
		gp.add(btPlay, 2, 1);
	    });

	    // Actions for "Stop" Button (at 2, 1)
	    btStop.setOnAction(ae -> {
		loaded.mp.stop();
		gp.getChildren().remove(btStop);
		gp.add(btPlay, 2, 1);
	    });

	    // Actions for "Seek Forwards" Button (at 3, 1)
	    btFwd.setOnAction(ae -> {
		loaded.mp.seek(loaded.mp.getCurrentTime().add(Duration.seconds(10.0d)));
//		gp.getChildren().remove(btFwd);
//		gp.add(bt2End, 3, 1);
	    });

	    // Actions for "Seek to End" Button (at 3, 1)
	    bt2End.setOnAction(ae -> {
		loaded.mp.seek(loaded.mp.getStartTime());
		gp.getChildren().remove(bt2End);
		gp.add(btFwd, 3, 1);
	    });

	    gp.add(sTime, 0, 0, 4, 1);
	    gp.addRow(1, btEject, bt2Start, btPlay, btFwd);
	    gp.add(sVol, 0, 2, 4, 1);
	    root.add(gp, 6, 0, 2, 1);
	    root.requestLayout();
	});
    }
}
