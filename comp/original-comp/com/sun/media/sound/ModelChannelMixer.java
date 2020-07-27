public interface ModelChannelMixer extends MidiChannel {

    
    public boolean process(float[][] buffer, int offset, int len);

    
    
    public void stop();
}
