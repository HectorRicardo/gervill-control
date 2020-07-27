public interface ModelOscillatorStream {

    public void setPitch(float pitch); 

    public void noteOn(MidiChannel channel, VoiceStatus voice, int noteNumber,
            int velocity);

    public void noteOff(int velocity);

    public int read(float[][] buffer, int offset, int len) throws IOException;

    public void close() throws IOException;
}
