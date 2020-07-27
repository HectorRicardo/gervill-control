abstract class SunCodec extends FormatConversionProvider {

    private final AudioFormat.Encoding[] inputEncodings;
    private final AudioFormat.Encoding[] outputEncodings;

        SunCodec(final AudioFormat.Encoding[] inputEncodings,
             final AudioFormat.Encoding[] outputEncodings) {
        this.inputEncodings = inputEncodings;
        this.outputEncodings = outputEncodings;
    }


        public final AudioFormat.Encoding[] getSourceEncodings() {
        AudioFormat.Encoding[] encodings = new AudioFormat.Encoding[inputEncodings.length];
        System.arraycopy(inputEncodings, 0, encodings, 0, inputEncodings.length);
        return encodings;
    }
        public final AudioFormat.Encoding[] getTargetEncodings() {
        AudioFormat.Encoding[] encodings = new AudioFormat.Encoding[outputEncodings.length];
        System.arraycopy(outputEncodings, 0, encodings, 0, outputEncodings.length);
        return encodings;
    }

        public abstract AudioFormat.Encoding[] getTargetEncodings(AudioFormat sourceFormat);


        public abstract AudioFormat[] getTargetFormats(AudioFormat.Encoding targetEncoding, AudioFormat sourceFormat);


        public abstract AudioInputStream getAudioInputStream(AudioFormat.Encoding targetEncoding, AudioInputStream sourceStream);
        public abstract AudioInputStream getAudioInputStream(AudioFormat targetFormat, AudioInputStream sourceStream);


}
