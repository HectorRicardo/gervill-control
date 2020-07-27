public interface ReferenceCountingDevice {
        public Receiver getReceiverReferenceCounting() throws MidiUnavailableException;

        public Transmitter getTransmitterReferenceCounting() throws MidiUnavailableException;
}
