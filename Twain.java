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
 * Twain Class: A "struct" class for the Envelope Class
 * 
 * Each Twain consists of a duration (in milliseconds) and a level (0.0d ... 1.0d).
 * An ADSR Envelope therefore consists of 4 Twains: one each for Attack, Decay, Sustain and Release.
 */
public class Twain {
    public int duration;
    public double level;

    // Constructor for Twain Class - requires duration (in milliseconds)
    // and level (0.0d ... 1.0d)
    public Twain(int duration, double level) {
	this.duration = duration;
	this.level = level;
    }
}