final class JSSecurityManager {

        private JSSecurityManager() {
    }

        private static boolean hasSecurityManager() {
        return (System.getSecurityManager() != null);
    }


    static void checkRecordPermission() throws SecurityException {
        if(Printer.trace) Printer.trace("JSSecurityManager.checkRecordPermission()");
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AudioPermission("record"));
        }
    }

        static void loadProperties(final Properties properties,
                               final String filename) {
        if(hasSecurityManager()) {
            try {
                
                PrivilegedAction<Void> action = new PrivilegedAction<Void>() {
                        public Void run() {
                            loadPropertiesImpl(properties, filename);
                            return null;
                        }
                    };
                AccessController.doPrivileged(action);
                if(Printer.debug)Printer.debug("Loaded properties with JDK 1.2 security");
            } catch (Exception e) {
                if(Printer.debug)Printer.debug("Exception loading properties with JDK 1.2 security");
                
                loadPropertiesImpl(properties, filename);
            }
        } else {
            
            loadPropertiesImpl(properties, filename);
        }
    }


    private static void loadPropertiesImpl(Properties properties,
                                           String filename) {
        if(Printer.trace)Printer.trace(">> JSSecurityManager: loadPropertiesImpl()");
        String fname = System.getProperty("java.home");
        try {
            if (fname == null) {
                throw new Error("Can't find java.home ??");
            }
            File f = new File(fname, "lib");
            f = new File(f, filename);
            fname = f.getCanonicalPath();
            InputStream in = new FileInputStream(fname);
            BufferedInputStream bin = new BufferedInputStream(in);
            try {
                properties.load(bin);
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } catch (Throwable t) {
            if (Printer.trace) {
                System.err.println("Could not load properties file \"" + fname + "\"");
                t.printStackTrace();
            }
        }
        if(Printer.trace)Printer.trace("<< JSSecurityManager: loadPropertiesImpl() completed");
    }

        static Thread createThread(final Runnable runnable,
                               final String threadName,
                               final boolean isDaemon, final int priority,
                               final boolean doStart) {
        Thread thread = new Thread(runnable);
        if (threadName != null) {
            thread.setName(threadName);
        }
        thread.setDaemon(isDaemon);
        if (priority >= 0) {
            thread.setPriority(priority);
        }
        if (doStart) {
            thread.start();
        }
        return thread;
    }

    static synchronized <T> List<T> getProviders(final Class<T> providerClass) {
        List<T> p = new ArrayList<>(7);
        
        
        
        
        final PrivilegedAction<Iterator<T>> psAction =
                new PrivilegedAction<Iterator<T>>() {
                    @Override
                    public Iterator<T> run() {
                        return ServiceLoader.load(providerClass).iterator();
                    }
                };
        final Iterator<T> ps = AccessController.doPrivileged(psAction);

        
        
        PrivilegedAction<Boolean> hasNextAction = new PrivilegedAction<Boolean>() {
            public Boolean run() {
                return ps.hasNext();
            }
        };

        while (AccessController.doPrivileged(hasNextAction)) {
            try {
                
                
                
                T provider = ps.next();
                if (providerClass.isInstance(provider)) {
                    
                    
                    
                    
                    p.add(0, provider);
                }
            } catch (Throwable t) {
                
                if (Printer.err) t.printStackTrace();
            }
        }
        return p;
    }
}
