public final class SoftProvider extends MidiDeviceProvider {

    static final Info softinfo = SoftSynthesizer.info;
    private static final Info[] softinfos = {softinfo};

    public MidiDevice.Info[] getDeviceInfo() {
        return Arrays.copyOf(softinfos, softinfos.length);
    }

    public MidiDevice getDevice(MidiDevice.Info info) {
        if (info == softinfo) {
            return new SoftSynthesizer();
        }
        return null;
    }
}
