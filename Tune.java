
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
    public MediaPlayer mp;
    public MediaView mv;

    public Tune(String filename) {
	this.filename = filename;
	m = new Media(new File(filename).toURI().toASCIIString());
	mp = new MediaPlayer(m);
	mv = new MediaView(mp);
    }

    public Tune(File file) {
	m = new Media(file.toURI().toASCIIString());
	mp = new MediaPlayer(m);
	mv = new MediaView(mp);
    }

    /*
     * This Class will probably end up being re-absorbed as a couple of lines in
     * Sound. Salutary lesson: do not assume you need a new class to implement "x"
     * feature!
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
    }
}
