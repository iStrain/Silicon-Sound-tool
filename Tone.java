
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
 * 
 * Class Tone: Simple sound generator for Silicon Project.  It is simple ... Java does all the work.
 * 
 * The methods are threaded so you can go: "new Thread(new Tone(int hz, int msecs)).start()" to play a tone.
 * You can also set the volume: "new Thread(new Tone(int hz, int msecs, double vol)).start()", where "vol"
 * is between 0.0d and 1.0d. To find frequencies that correspond to musical notes, look here:
 * https://pages.mtu.edu/~suits/notefreqs.html
 * 
 * This Class was adapted from some horrible (mainly non-functional) code at Stack Overflow.  Nonetheless, 
 * coding credits(!) are due to: "https://stackoverflow.com/a/6700039".
 * 
 * The original code worked at the specific combination of sample rate and frequency it used 
 * (for a unique definition of "worked" -- ie. it played a completely different frequency).
 * However, it produced silence at the frequencies normal ears can handle.  It also blocked 
 * the calling method until the sound was finished playing.  Finally, it flicked method-specific 
 * errors up-the-chain (don't you hate that?).
 * 
 * Our code seems to work for frequencies up to about 11KHz, doesn't block, and handles its own errors.  Cough.
 * 
 * The sound generator includes support for ADSR envelopes, provided through the Envelope class.  The 
 * envelope can be set separately from playing tones, and should be, because at 22KHz sampling rate, 
 * the envelope for a tone lasting one-tenth of a second will require the calculation of 2,200 individual levels
 * (actually done within the constructor for the Envelope object).
 * 
 * So, the preferred way to use Envelope is:
 * 
 * 	Envelope env = new Envelope(10, 5, 0.75d, 60, 25);  <-- Do this somewhere in setup
 *	Tone.setEnvelope(env.envArray);  <-- static function, accessible outside Tone and without a Tone object
 *	new Thread(new Tone(262, 100, 0.75d)).start();  <-- Play fundamental note (with Envelope)
 *	new Thread(new Tone(786, 100, 0.25d)).start();	<-- Play 3rd harmonic (with Envelope) 
 *
 * This example will set an envelope with 10ms attack, 5ms decay, sustain at 75% volume for 60ms, and 25ms release.
 * It will then play a fundamental note at middle C, with its 3rd harmonic, both modulated with the envelope.
 * 
 * Note: This is "kind-of" backwards-compatible with earlier versions of Tone: if your app does not set an Envelope,
 * the default will apply (null) and you will get the raw tone.  That is probably more listenable than the base-grade  
 * ADSR I had in there before.
 */

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Tone implements Runnable {
    static private float SAMPLE_RATE = 22000f; // Don't mess with this number. You have been warned!
    static private float SCALE = 0.0002857f; // 2 * PI / SAMPLE_RATE
    static private int MULTIPLIER = 22; // The number of sample ticks per millisecond
    private int BLOCK_SIZE = 2200; // The number of samples written to the buffer each loop
    private int hz; // Frequency of sound to play
    private int msecs; // Milliseconds to play the sound
    private double vol; // Volume (0.0 ... 1.0) for the sound
    static private double[] envArray = null; // ADSR Envelope to use to modulate the sound (null = none)

    /*
     * This is the constructor for code that just wants to specify frequency and
     * duration.
     */
    public Tone(int hz, int msecs) {
	this.hz = hz;
	this.msecs = msecs;
	this.vol = 1.0d;
    }

    /*
     * This is the constructor for code that wants to specify frequency, duration
     * *and* volume (0.0d ... 1.0d). For example, to play a third harmonic you would
     * play the original tone at 0.75 volume, and the harmonic tone at 3 times the
     * frequency but 0.25 volume.
     */
    public Tone(int hz, int msecs, double vol) {
	this.hz = hz;
	this.msecs = msecs;
	this.vol = vol;
    }

    /*
     * Accessor method for value of MULTIPLIER. Other classes can use it to convert
     * from milliseconds to sampling 'ticks.
     */
    static public int getMultiplier() {
	return MULTIPLIER;
    }

    // Accessor method for value of envArray (null by default)
    static public double[] getEnvelope() {
	return envArray;
    }

    // Mutator method for value of envArray (set to null for no Envelope)
    static public boolean setEnvelope(double[] envArray) {
	Tone.envArray = envArray;
	return true;
    }

    /*
     * This is the code for a new thread that plays a tone. Multiple tones,
     * including for harmonics, tunes or fade-in/fade-out effects, require multiple
     * threads.
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
	// Sample rate, number of bits, number of channels, signed?, bigEndian?
	AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);

	/*
	 * Sound output lines can be a bit scarce at times, especially when you start
	 * using them in multiples (e.g. to play harmonics) - well worth a try/catch
	 * block.
	 */
	SourceDataLine sdl;
	try {
	    sdl = AudioSystem.getSourceDataLine(af);
	} catch (LineUnavailableException lue) {
	    lue.printStackTrace();
	    return;
	}

	/*
	 * We probably could have gotten away with flicking this one, 'cos there's not
	 * much chance it'll fire if we got the source-data-line OK, but meh, I've put
	 * it in a try/catch block anyway!
	 */
	try {
	    sdl.open(af);
	} catch (LineUnavailableException lue) {
	    lue.printStackTrace();
	    return;
	}

	/*
	 * We generate sampled quanta for a sine wave at the requested frequency and
	 * scale them to -127.0 ... +127.0 (adjusted by volume if required). We store
	 * the result in our buffer. To minimise latency, our buffer is somewhat smaller
	 * than the data line's internal buffer.
	 * 
	 * The variable "written" keeps track of how many samples we have written to the
	 * data line, and we also use it to maintain continuity between the last quanta
	 * in one block, and the first quanta in the next block (otherwise the phase
	 * change between blocks, from (angle = BLOCK_SIZE - 1) to (angle = 0) can cause
	 * a perceptible "warbling" in the tone).
	 * 
	 * There are two versions, depending on whether we need an ADSR envelope (no
	 * envelope is signified by a "null" in envArray[]).
	 */
	double scalar = hz * SCALE;
	int sample = msecs * MULTIPLIER;
	int written = 0;
	byte[] buf = new byte[BLOCK_SIZE];
	int block = BLOCK_SIZE;
	sdl.start();
	if (envArray != null) {
	    while (written < sample) {
		if ((written + block) > sample) {
		    block = sample - written;
		}
		for (int i = 0; i < block; i++) {
		    double angle = (written + i) * scalar;
		    buf[i] = (byte) (Math.sin(angle) * 127.0 * vol * envArray[written + i]);
		}
		sdl.write(buf, 0, block);
		written += block;
	    }
	} else {
	    while (written < sample) {
		if ((written + block) > sample) {
		    block = sample - written;
		}
		for (int i = 0; i < block; i++) {
		    double angle = (written + i) * scalar;
		    buf[i] = (byte) (Math.sin(angle) * 127.0 * vol);
		}
		sdl.write(buf, 0, block);
		written += block;
	    }
	}
	sdl.drain();
	sdl.stop();
	sdl.close();
    }
}
