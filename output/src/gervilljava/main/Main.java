package own.main;

import gervill.com.sun.media.sound.DLSSoundbank;
import gervill.com.sun.media.sound.SF2Soundbank;
import gervill.javax.sound.midi.Soundbank;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    public static void main(String[] args) throws IOException {
        // Step 1: Select random soundbank
        Soundbank soundbank;

        switch (ThreadLocalRandom.current().nextInt(3)) {
            case 0:
                soundbank = new SF2Soundbank(new File("assets/gm.sf2"));
                break;
            case 1:
                soundbank = new DLSSoundbank(new File("assets/gm.dls"));
                break;
            case 2:
                soundbank = null;
                break;
            default:
                throw new RuntimeException("WTF just happened");
        }

        // Step 2. Set up synthesizer
        final SynthesizerPlayer player = new SynthesizerPlayer();
        player.init(soundbank);

        System.out.println(player.getInstrumentInfo());

        // Step 3: Play something
        if (player.readyToStartPlaying()) { // will always be true
            final String[] instrumentInfo = player.sample1();

            // Print details
            System.out.println(soundbank == null ? "Default soundbank" : soundbank.getClass().getSimpleName());
            System.out.println("--------------------------");
            System.out.println(instrumentInfo[0]);
            System.out.println(instrumentInfo[1]);

            player.sample2();
        }

        // Step 4. Close synthesizer
        player.close();
    }
}
