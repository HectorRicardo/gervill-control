package samplesynth;

import gervill.com.sun.media.sound.DLSSoundbank;
import gervill.com.sun.media.sound.SF2Soundbank;
import gervill.com.sun.media.sound.SoftSynthesizer;
import gervill.javax.sound.midi.Instrument;
import gervill.javax.sound.midi.MidiChannel;
import gervill.javax.sound.midi.Patch;
import gervill.javax.sound.midi.Soundbank;
import gervill.javax.sound.midi.Synthesizer;
import gervill.javax.sound.midi.VoiceStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

public class SynthesizerPlayer {

    private static final boolean FALSE = Boolean.parseBoolean("false".toLowerCase()); // helper variable to add unused code without warnings

    private static final int PORTAMENTO_LEVEL = 80;

    private Synthesizer synthesizer;
    private Soundbank soundbank;
    private MidiChannel channel;
    private Instrument instrument;

    private boolean ready = false;
    private boolean playing = false;

    private StringBuilder instrumentInfo;

    public SynthesizerPlayer() {
        toAvoidUnused();
    }

    private static String toString(Instrument instrument) {
        Patch patch = instrument.getPatch();
        return MessageFormat.format("{0} - ({1}, {2})", instrument.getName(), patch.getBank(), patch.getProgram());
    }

    private static void addInstrumentsToStringBuilder(Instrument[] instruments, StringBuilder stringBuilder) {
        for (Instrument instrument : instruments) {
            stringBuilder.append(toString(instrument)).append('\n');
        }
    }

    private static int random(int topExclusive) {
        return (int) (Math.random() * topExclusive);
    }

    public void init(Soundbank soundbank) {
        try {
            synthesizer = new SoftSynthesizer();
            if (!synthesizer.isOpen()) {
                synthesizer.open();
            }

            Soundbank defaultSoundbank = synthesizer.getDefaultSoundbank();
            synthesizer.unloadAllInstruments(defaultSoundbank);

            this.soundbank = soundbank == null ? defaultSoundbank : soundbank;

            instrumentInfo = new StringBuilder();

            instrumentInfo.append("LOADED INSTRUMENTS\n---------------------------------------\n");
            addInstrumentsToStringBuilder(synthesizer.getLoadedInstruments(), instrumentInfo);
            instrumentInfo.append("\nAVAILABLE INSTRUMENTS\n-------------------------------------------\n");
            addInstrumentsToStringBuilder(synthesizer.getAvailableInstruments(), instrumentInfo);
            instrumentInfo.append("\nSOUNDBANK INSTRUMENTS\n-------------------------------------------\n");
            addInstrumentsToStringBuilder(this.soundbank.getInstruments(), instrumentInfo);

            channel = synthesizer.getChannels()[0];
            ready = true;
        } catch (gervill.javax.sound.midi.MidiUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    public StringBuilder getInstrumentInfo() {
        return instrumentInfo;
    }

    public void close() {
        if (ready && synthesizer.isOpen()) {
            synthesizer.close();
        }
    }

    public boolean readyToStartPlaying() {
        return ready && !playing;
    }

    public String[] sample1() {
        playing = true;

        Instrument[] instruments = soundbank.getInstruments();
        Instrument instrument1 = instruments[random(10)];
        Instrument instrument2 = instruments[random(11)];

        changeInstrument(instrument1);
        playMelody();

        changeInstrument(instrument2);
        playMelody();

        playing = false;

        return new String[]{toString(instrument1), toString(instrument2)};
    }

    public void sample2() {
        if (FALSE) {
            playing = true;

            for (Instrument instrument : soundbank.getInstruments()) {
                changeInstrument(instrument);
                playMelody();
                synthesizer.unloadInstrument(instrument);
            }

            playing = false;
        }
    }

    private void setPortamentoLevel(int level) {
        channel.controlChange(5, level);
    }

    private void switchPortamento(boolean on) {
        channel.controlChange(65, on ? 127 : 0);
    }

    private void changeInstrument(Instrument instrument) {
        if (this.instrument != null) {
            synthesizer.unloadInstrument(this.instrument);
        }
        this.instrument = instrument;
        synthesizer.loadInstrument(instrument);
        Patch patch = instrument.getPatch();
        channel.programChange(patch.getBank(), patch.getProgram());
    }

    private void playMelody() {
        int pause = 500;
        switchPortamento(true);
        setPortamentoLevel(FALSE ? 0 : PORTAMENTO_LEVEL);

        try {
            channel.noteOn(60, 64);
            Thread.sleep(pause);
            channel.noteOff(60);

            channel.noteOn(62, 64);
            Thread.sleep(pause);
            channel.noteOff(62);

            channel.noteOn(64, 64);
            Thread.sleep(pause);
            channel.noteOff(64);

            channel.noteOn(65, 64);
            Thread.sleep(pause);
            channel.noteOff(65);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        switchPortamento(false);
    }

    private void toAvoidUnused() {
        if (FALSE) {
            // Patch
            Patch p = new Patch(10, 20);
            System.out.println(p.getBank());
            System.out.println(p.getProgram());

            // VoiceStatus
            VoiceStatus voiceStatus = new VoiceStatus();
            System.out.println(voiceStatus.active);
            System.out.println(voiceStatus.bank);
            System.out.println(voiceStatus.channel);
            System.out.println(voiceStatus.note);
            System.out.println(voiceStatus.program);
            System.out.println(voiceStatus.volume);

            // MIDI CHANNEL
            channel.allNotesOff();
            channel.allSoundOff();
            channel.controlChange(89, 2);

            System.out.println(channel.getChannelPressure());
            System.out.println(channel.getController(89));
            System.out.println(channel.getMono());
            System.out.println(channel.getMute());
            System.out.println(channel.getOmni());
            System.out.println(channel.getPitchBend());
            System.out.println(channel.getPolyPressure(15));
            System.out.println(channel.getProgram());
            System.out.println(channel.getSolo());
            System.out.println(channel.localControl(true));

            channel.noteOff(15);
            channel.noteOff(15, 30);
            channel.noteOn(15, 30);
            channel.programChange(80);
            channel.programChange(2, 80);
            channel.resetAllControllers();
            channel.setChannelPressure(40);
            channel.setMono(true);
            channel.setMute(false);
            channel.setOmni(true);
            channel.setPitchBend(30);
            channel.setPolyPressure(15, 40);
            channel.setSolo(true);

            // Instrument
            System.out.println(instrument.getName());
            soundbank = instrument.getSoundbank();
            System.out.println(instrument.getPatch().getBank());

            // Soundbank
            System.out.println(soundbank.getDescription());
            instrument = soundbank.getInstrument(instrument.getPatch());
            for (Instrument instrument : soundbank.getInstruments()) {
                System.out.println(instrument.getName());
            }
            System.out.println(soundbank.getName());
            System.out.println(soundbank.getVendor());
            System.out.println(soundbank.getVersion());

            // Synthesizer
            for (Instrument instrument : synthesizer.getAvailableInstruments()) {
                System.out.println(instrument.getName());
            }
            channel = synthesizer.getChannels()[0];
            soundbank = synthesizer.getDefaultSoundbank();
            System.out.println(synthesizer.getLatency());
            for (Instrument instrument : synthesizer.getLoadedInstruments()) {
                System.out.println(instrument.getName());
            }
            System.out.println(synthesizer.getMaxPolyphony());
            for (VoiceStatus vs : synthesizer.getVoiceStatus()) {
                System.out.println(vs.active);
            }
            System.out.println(synthesizer.isSoundbankSupported(soundbank));
            System.out.println(synthesizer.loadAllInstruments(soundbank));
            System.out.println(synthesizer.loadInstrument(instrument));
            System.out.println(synthesizer.loadInstruments(soundbank, new Patch[]{new Patch(1, 2)}));
            System.out.println(synthesizer.remapInstrument(instrument, instrument));
            synthesizer.unloadAllInstruments(soundbank);
            synthesizer.unloadInstrument(instrument);
            synthesizer.unloadInstruments(soundbank, new Patch[]{new Patch(1, 2)});

            if (!synthesizer.isOpen()) {
                try {
                    synthesizer.open();

                    File file = new File("whatever.DlsOrSf2");
                    FileInputStream fis = new FileInputStream(file);
                    URL url = new URL("https://google.com");

                    soundbank = new SF2Soundbank(fis);
                    soundbank = new SF2Soundbank(file);
                    soundbank = new SF2Soundbank(url);

                    soundbank = new DLSSoundbank(fis);
                    soundbank = new DLSSoundbank(file);
                    soundbank = new DLSSoundbank(url);
                } catch (gervill.javax.sound.midi.MidiUnavailableException | IOException e) {
                    e.printStackTrace();
                }
            }

            synthesizer.close();
        }
    }
}
