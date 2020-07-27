public final class JDK13Services {

        private static final String PROPERTIES_FILENAME = "sound.properties";

        private static Properties properties;

        private JDK13Services() {
    }

        public static List<?> getProviders(final Class<?> serviceClass) {
        final List<?> providers;
        if (!MixerProvider.class.equals(serviceClass)
                && !FormatConversionProvider.class.equals(serviceClass)
                && !AudioFileReader.class.equals(serviceClass)
                && !AudioFileWriter.class.equals(serviceClass)
                && !MidiDeviceProvider.class.equals(serviceClass)
                && !SoundbankReader.class.equals(serviceClass)
                && !MidiFileWriter.class.equals(serviceClass)
                && !MidiFileReader.class.equals(serviceClass)) {
            providers = new ArrayList<>(0);
        } else {
            providers = JSSecurityManager.getProviders(serviceClass);
        }
        return Collections.unmodifiableList(providers);
    }

        public static synchronized String getDefaultProviderClassName(Class typeClass) {
        String value = null;
        String defaultProviderSpec = getDefaultProvider(typeClass);
        if (defaultProviderSpec != null) {
            int hashpos = defaultProviderSpec.indexOf('#');
            if (hashpos == 0) {
                
            } else if (hashpos > 0) {
                value = defaultProviderSpec.substring(0, hashpos);
            } else {
                value = defaultProviderSpec;
            }
        }
        return value;
    }


        public static synchronized String getDefaultInstanceName(Class typeClass) {
        String value = null;
        String defaultProviderSpec = getDefaultProvider(typeClass);
        if (defaultProviderSpec != null) {
            int hashpos = defaultProviderSpec.indexOf('#');
            if (hashpos >= 0 && hashpos < defaultProviderSpec.length() - 1) {
                value = defaultProviderSpec.substring(hashpos + 1);
            }
        }
        return value;
    }


        private static synchronized String getDefaultProvider(Class typeClass) {
        if (!SourceDataLine.class.equals(typeClass)
                && !TargetDataLine.class.equals(typeClass)
                && !Clip.class.equals(typeClass)
                && !Port.class.equals(typeClass)
                && !Receiver.class.equals(typeClass)
                && !Transmitter.class.equals(typeClass)
                && !Synthesizer.class.equals(typeClass)
                && !Sequencer.class.equals(typeClass)) {
            return null;
        }
        String name = typeClass.getName();
        String value = AccessController.doPrivileged(
                (PrivilegedAction<String>) () -> System.getProperty(name));
        if (value == null) {
            value = getProperties().getProperty(name);
        }
        if ("".equals(value)) {
            value = null;
        }
        return value;
    }


        private static synchronized Properties getProperties() {
        if (properties == null) {
            properties = new Properties();
            JSSecurityManager.loadProperties(properties, PROPERTIES_FILENAME);
        }
        return properties;
    }
}
