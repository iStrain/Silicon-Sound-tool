
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
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class Tune implements Runnable {
    String filename;
    Media m;
    public MediaPlayer mp;
    public MediaView mv;

    public Tune(String filename) {
	this.filename = filename;
	try {
//	    m = new Media(new File("resources/" + filename).toURI().toASCIIString());
m = new Media(this.getClass().getResource(filename).toExternalForm());
	    mp = new MediaPlayer(m);
	    mv = new MediaView(mp);
	} catch (MediaException me) {
	    System.out.println("Tune Class (line 31): Cannot load '" + filename + "' - please check folders.");
	}
    }

    public Tune(File file) {
	try {
	    filename = file.getName();
	    m = new Media(file.toURI().toASCIIString());
	    mp = new MediaPlayer(m);
	    mv = new MediaView(mp);
	} catch (MediaException me) {
	    System.out.println("Tune Class (line 41): Cannot load '" + file.getName() + "' - please check folders.");
	}
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
