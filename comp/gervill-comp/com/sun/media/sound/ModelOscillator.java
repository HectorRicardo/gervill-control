public interface ModelOscillator {

    public int getChannels();

        public float getAttenuation();

    public ModelOscillatorStream open(float samplerate);
}
