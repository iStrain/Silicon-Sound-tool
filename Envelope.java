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
 * Class Envelope: Provides ADSR envelopes for Tone Class
 * Each Envelope consists of 4 Twains: one each for Attack, Decay, Sustain and Release
 * Constructors are provided for 4 Twains or 4 duration/level pairs.
 * The caller can also specify ADSHR (attack.duration, decay.duration, sustain.level, 
 * sustain.duration (Hold) and release.duration.
 * Durations are denominated in milliseconds, levels in normalised volume (0.0d ... 1.0d)
 * There is also an accessor method to get the total duration of a Tone
 */

public class Envelope {
    private Twain attack = new Twain(0, 0); // Duration and level for attack phase
    private Twain decay = new Twain(0, 0); // Duration and level for decay phase
    private Twain sustain = new Twain(0, 0); // Duration and level for sustain phase
    private Twain release = new Twain(0, 0); // Duration and level for release phase
    public double[] envArray; // Array of samples (0.0 ... 1.0) for the Envelope

    // Constructor for Envelope Class - requires 4 Twains: one each for
    // Attack, Decay, Sustain and Release
    public Envelope(Twain attack, Twain decay, Twain sustain, Twain release) {
	this.attack = attack;
	this.decay = decay;
	this.sustain = sustain;
	this.release = release;
	createEnvArray();
    }

    // Alternative constructor for Envelope Class - requires 4 duration/level pairs:
    // one each for Attack, Decay, Sustain and Release
    public Envelope(int attackDuration, double attackLevel, int decayDuration, double decayLevel, int sustainDuration,
	    double sustainLevel, int releaseDuration, double releaseLevel) {
	attack.duration = attackDuration;
	attack.level = attackLevel;
	decay.duration = decayDuration;
	decay.level = decayLevel;
	sustain.duration = sustainDuration;
	sustain.level = sustainLevel;
	release.duration = releaseDuration;
	release.level = releaseLevel;
	createEnvArray();
    }

    // Alternative constructor for Envelope Class - requires 2 arrays of 5 elements each:
    // array[0] is ignored, then Attack[1], Decay[2], Sustain[3] and Release[4]
    public Envelope(int[] durations, double[] levels) {
	attack.duration = durations[1];
	attack.level = levels[1];
	decay.duration = durations[2];
	decay.level = levels[2];
	sustain.duration = durations[3];
	sustain.level = levels[3];
	release.duration = durations[4];
	release.level = levels[4];
	createEnvArray();
    }

    // Alternative constructor for Envelope Class - requires Attack, Decay, Sustain,
    // Hold and Release. Attack, Decay, Hold and Release are all durations
    // (in milliseconds). Sustain is a level (0.0d ... 1.0d).
    public Envelope(int attackDuration, int decayDuration, double sustainLevel, int sustainDuration,
	    int releaseDuration) {
	this.attack.duration = attackDuration;
	this.attack.level = 1.0d;
	this.decay.duration = decayDuration;
	this.decay.level = sustainLevel;
	this.sustain.duration = sustainDuration;
	this.sustain.level = sustainLevel;
	this.release.duration = releaseDuration;
	this.release.level = 0.0d;
	createEnvArray();
    }

    // getToneDuration()
    public int getToneDuration(Envelope e) {
	return e.attack.duration + e.decay.duration + e.sustain.duration + e.release.duration;
    }

    /*
     * createEnvArray(): Creates an array of ADSR modulators (0.0 ... 1.0), one for
     * each sample quanta. The constructors call this method. However, as the array
     * of modulators is public, its contents can be accessed directly as (e.g.)
     * env.envArray[index]. This should be more efficient for the calling method.
     */
    private void createEnvArray() {
	int multiplier = Tone.getMultiplier();
	this.envArray = new double[getToneDuration(this) * multiplier];

	// Attack phase
	int i;
	this.envArray[0] = 0.0d;
	double step = this.attack.level / (this.attack.duration * multiplier);
	for (i = 1; i < this.attack.duration * multiplier; i++) {
	    this.envArray[i] = this.envArray[i - 1] + step;
	}
	int sample = i;

	// Decay phase
	step = (this.decay.level - this.attack.level) / (this.decay.duration * multiplier);
	for (i = sample; i < sample + this.decay.duration * multiplier; i++) {
	    this.envArray[i] = this.envArray[i - 1] + step;
	}
	sample = i;

	// Sustain phase
	step = (this.sustain.level - this.decay.level) / (this.sustain.duration * multiplier);
	for (i = sample; i < sample + this.sustain.duration * multiplier; i++) {
	    this.envArray[i] = this.envArray[i - 1] + step;
	}
	sample = i;

	// Release phase
	step = (this.release.level - this.sustain.level) / (this.release.duration * multiplier);
	for (i = sample; i < sample + this.release.duration * multiplier; i++) {
	    this.envArray[i] = this.envArray[i - 1] + step;
	}
    }
}
