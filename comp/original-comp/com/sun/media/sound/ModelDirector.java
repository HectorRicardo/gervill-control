public interface ModelDirector {

    public void noteOn(int noteNumber, int velocity);

    public void noteOff(int noteNumber, int velocity);

    public void close();
}
