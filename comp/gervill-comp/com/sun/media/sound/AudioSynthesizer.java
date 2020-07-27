public interface AudioSynthesizer extends Synthesizer {

        public AudioFormat getFormat();

        public AudioSynthesizerPropertyInfo[] getPropertyInfo(
            Map<String, Object> info);

        public void open(SourceDataLine line, Map<String, Object> info)
            throws MidiUnavailableException;

        public AudioInputStream openStream(AudioFormat targetFormat,
            Map<String, Object> info) throws MidiUnavailableException;
}
