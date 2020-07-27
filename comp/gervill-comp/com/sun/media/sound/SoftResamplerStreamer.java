public interface SoftResamplerStreamer extends ModelOscillatorStream {

    public void open(ModelWavetable osc, float outputsamplerate)
            throws IOException;
}
