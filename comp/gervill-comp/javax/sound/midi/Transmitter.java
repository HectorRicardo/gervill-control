public interface Transmitter extends AutoCloseable {


        public void setReceiver(Receiver receiver);


        public Receiver getReceiver();


        public void close();
}
