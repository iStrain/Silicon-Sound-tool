Silicon Game - Sound tools

MiNiSYNTH

Small sound player with the ability to set the 8 parameters (duration and volume for Attack, Decay, Sustain and Release) in the ADSR Envelope.  Displays the ADSR Envelope in a Line Chart and the active parameters in a Text Area table.

Also includes a mini-mp3 player, which allows you to (obviously) select and play an mp3 or other supported file format (eg. wav).

Files are:
+ Sound.java (main file for the tool);
+ SoundStyles.css (JavaFX CSS stylesheet for the tool);
+ MiNiSYNTH.png (logo for the tool);
+ .png icons for various buttons; and
+ Silicon_Theme_Funk.mp3 (test file containing Kira's music).

The tool also uses the Tone.java, Envelope.java and Twain.java files from the Silicon repository (copies of the latest versions are provided here). The logo, icons and test file are included in the "resources.zip" file.  

To compile with Eclipse: Put the .java and .css files in the /src folder, the .css file in a /bin folder directly underneath the Project folder, unzip the "resources.zip" file into the /bin folder, and then create a /resources folder, also directly underneath the Project folder, and then unzip the "resources.zip" file again, into the new /resources folder.  Yes, I suspect that is #doingitwrong, but it seems to work for me.  Compile Sound.java.
