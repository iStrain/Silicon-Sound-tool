
/*
 * SILICON - A JavaFX GAME BY:
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

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class Tune implements Runnable {
    String filename;
    Media m;
    public static MediaPlayer mp;
    MediaView mv;

    public Tune(String filename) {
	this.filename = filename;
	m = new Media(new File(filename).toURI().toASCIIString());
	mp = new MediaPlayer(m);
	mp.setAutoPlay(true);
	mp.setVolume(0.9);
	mv = new MediaView(mp);
    }

    public Tune(File file) {
	m = new Media(file.toURI().toASCIIString());
	mp = new MediaPlayer(m);
	mp.setAutoPlay(true);
	mp.setVolume(0.9);
	mv = new MediaView(mp);
    }

    public MediaPlayer getMP() {
	return mp;
    }
    /*
     * This is the code for a new thread that plays an mp3 file.
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
//	createControls(mp);
    }
    /*
     * // Create slider for MediaPlayer time control private Slider
     * createTimeSlider() { Slider slider = new Slider(0, 100, 0); Double start =
     * mp.getStartTime().toSeconds(); Double stop = mp.getStopTime().toSeconds();
     * int duration = ((int) (stop - start)); int durationMins = duration / 60; int
     * durationSecs = duration % 60;
     * 
     * slider.setMaximum(duration); slider.setMajorTickSpacing(60);
     * slider.setMinorTickSpacing(15); slider.setPaintTicks(true);
     * Dictionary<Integer, JComponent> table = slider.createStandardLabels(60); int
     * i = 0; while (i <= durationMins) { JLabel label = new
     * JLabel(String.format("%d.00", i)); adjustFont(label, -2); table.put(i * 60,
     * label); i++; } if (durationSecs > 45) { JLabel label = new
     * JLabel(String.format("%d.%d", durationMins, durationSecs)); adjustFont(label,
     * -2); table.put(duration, label); } slider.setLabelTable(table);
     * slider.setPaintLabels(true);
     * 
     * Timer t = new Timer(15, new ActionListener() {
     * 
     * @Override public void actionPerformed(ActionEvent e) { if
     * (!slider.getValueIsAdjusting()) { slider.setValue((int)
     * (mp.getCurrentTime().toSeconds())); } } }); t.start();
     * 
     * slider.addChangeListener(new ChangeListener() { public void
     * stateChanged(ChangeEvent e) { if (slider.getValueIsAdjusting()) {
     * mp.seek(Duration.seconds((double) (slider.getValue()))); } } }); return
     * slider; }
     * 
     * /* Button btEnv = new Button("SET ENVELOPE"); btEnv.setTooltip(new
     * Tooltip("Press this button to update the ADSR Envelope with the current settings"
     * )); btEnv.setOnAction(ae -> { ta.setText(updateTable()); env = new
     * Envelope(durations, levels); });
     */
    /*
     * // Create Play/Pause button for MediaPlayer private Button
     * createPlayButton(ImageView playIcon, ImageView pauseIcon) { Button b = new
     * Button("", pauseIcon); b.setActionCommand("Pause"); b.setOnAction(ae -> { if
     * (getOnAction(ae).equals("Pause")) { mp.pause(); b.setActionCommand("Play");
     * b.setIcon(playIcon); } else { mp.play(); b.setActionCommand("Pause");
     * b.setIcon(pauseIcon); } }); return b; }
     * 
     * // Create Stop button for MediaPlayer private Button
     * createStopButton(ImageView stopIcon, Button play, ImageView playIcon) {
     * Button b = new Button("", stopIcon); b.setActionCommand("Stop");
     * b.addActionListener(new ActionListener() { public void
     * actionPerformed(ActionEvent e) { mp.stop(); play.setActionCommand("Play");
     * play.setIcon(playIcon); } }); return b; }
     * 
     * // Create Rewind button for MediaPlayer private Button
     * createRewButton(ImageView rewIcon) { Button b = new Button("", rewIcon);
     * b.setActionCommand("Rewind"); b.addActionListener(new ActionListener() {
     * public void actionPerformed(ActionEvent e) {
     * mp.seek(mp.getCurrentTime().subtract(new Duration(10000.0d))); } }); return
     * b; }
     * 
     * // Create Fast Forward button for MediaPlayer private Button
     * createFwdButton(ImageView fwdIcon) { Button b = new Button("", fwdIcon);
     * b.setActionCommand("Fwd"); b.addActionListener(new ActionListener() { public
     * void actionPerformed(ActionEvent e) { mp.seek(mp.getCurrentTime().add(new
     * Duration(10000.0d))); } }); return b; }
     * 
     * // Create Start button for MediaPlayer private Button
     * createStartButton(ImageView startIcon) { Button b = new Button("",
     * startIcon); b.setActionCommand("Start"); b.addActionListener(new
     * ActionListener() { public void actionPerformed(ActionEvent e) {
     * mp.seek(mp.getStartTime()); } }); return b; }
     * 
     * // Create End button for MediaPlayer private Button createEndButton(ImageView
     * endIcon, Button play, ImageView playIcon) { Button b = new Button("",
     * endIcon); b.setActionCommand("End"); b.addActionListener(new ActionListener()
     * { public void actionPerformed(ActionEvent e) { mp.stop();
     * mp.seek(mp.getStartTime()); play.setActionCommand("Play");
     * play.setIcon(playIcon); } }); return b; } /* // Create slider for MediaPlayer
     * volume control private Slider createVolSlider() { Slider slider = new
     * Slider(0, 100); slider.setMajorTickSpacing(25);
     * slider.setMinorTickSpacing(5); slider.setPaintTicks(true);
     * Dictionary<Integer, JComponent> table = slider.createStandardLabels(25); int
     * i = 0; while (i <= 4) { JLabel label = new JLabel(String.format("%d%%", i *
     * 25)); adjustFont(label, -2); table.put(i * 25, label); i++; }
     * slider.setLabelTable(table); slider.setPaintLabels(true);
     * 
     * slider.addChangeListener(new ChangeListener() { public void
     * stateChanged(ChangeEvent e) { if (slider.getValueIsAdjusting()) {
     * mp.setVolume(slider.getValue() / 100.0); } } }); return slider; } /* Image
     * image = new Image(getClass().getResourceAsStream("image.png")); ImageView
     * imageView = new ImageView(); imageView.setImage(image); Label label = new
     * Label("text", imageView); label.setContentDisplay(ContentDisplay.TOP);
     */
    /*
     * // Create controls for Media Player object private void
     * createControls(MediaPlayer mp) { ImageView startIcon = new ImageView(new
     * Image(getClass().getResourceAsStream("sm_media_skip_backward.png")));
     * ImageView rewIcon = new ImageView(new
     * Image(getClass().getResourceAsStream("sm_media_seek_backward.png")));
     * ImageView playIcon = new ImageView(new
     * Image(getClass().getResourceAsStream("sm_media_playback_start.png")));
     * ImageView pauseIcon = new ImageView(new
     * Image(getClass().getResourceAsStream("sm_media_playback_pause.png")));
     * ImageView stopIcon = new ImageView(new
     * Image(getClass().getResourceAsStream("sm_media_playback_stop.png")));
     * ImageView fwdIcon = new ImageView(new
     * Image(getClass().getResourceAsStream("sm_media_seek_forward.png")));
     * ImageView endIcon = new ImageView(new
     * Image(getClass().getResourceAsStream("sm_media_skip_forward.png")));
     * 
     * mp.setOnReady(new Runnable() {
     * 
     * @Override public void run() { HBox topPanel = new HBox();
     * topPanel.getChildren().add(createTimeSlider());
     * 
     * GridPane midPanel = new GridPane();
     * midPanel.add(createStartButton(startIcon), 0, 0);
     * midPanel.add(createRewButton(rewIcon), 1, 0); Button play =
     * createPlayButton(playIcon, pauseIcon); midPanel.add(play, 2, 0);
     * midPanel.add(createStopButton(stopIcon, play, playIcon), 3, 0);
     * midPanel.add(createFwdButton(fwdIcon), 4, 0);
     * midPanel.add(createEndButton(endIcon, play, playIcon), 5, 0);
     * 
     * HBox endPanel = new HBox(); endPanel.getChildren().add(createVolSlider());
     * 
     * VBox mpContents = new VBox(); mpContents.getChildren().addAll(topPanel, new
     * Label("TIME"), midPanel, new Label("VOLUME"), endPanel);
     * 
     * mp.setOnEndOfMedia(new Runnable() { public void run() { mp.dispose(); } }); }
     * }); }
     * 
     * // Adjust Font size for a JLabel Object // + values -> bigger, - values ->
     * smaller private void adjustFont(JLabel label, int change) { Font f =
     * label.getFont(); label.setFont(new Font(f.getFontName(), f.getStyle(),
     * f.getSize() + change)); }
     * 
     * }
     */
}