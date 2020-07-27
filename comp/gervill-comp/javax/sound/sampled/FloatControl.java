public abstract class FloatControl extends Control {


    


    

        private float minimum;

        private float maximum;

        private float precision;

        private int updatePeriod;


        private final String units;

        private final String minLabel;

        private final String maxLabel;

        private final String midLabel;


    

        private float value;



    


        protected FloatControl(Type type, float minimum, float maximum,
            float precision, int updatePeriod, float initialValue,
            String units, String minLabel, String midLabel, String maxLabel) {

        super(type);

        if (minimum > maximum) {
            throw new IllegalArgumentException("Minimum value " + minimum
                    + " exceeds maximum value " + maximum + ".");
        }
        if (initialValue < minimum) {
            throw new IllegalArgumentException("Initial value " + initialValue
                    + " smaller than allowable minimum value " + minimum + ".");
        }
        if (initialValue > maximum) {
            throw new IllegalArgumentException("Initial value " + initialValue
                    + " exceeds allowable maximum value " + maximum + ".");
        }


        this.minimum = minimum;
        this.maximum = maximum;

        this.precision = precision;
        this.updatePeriod = updatePeriod;
        this.value = initialValue;

        this.units = units;
        this.minLabel = ( (minLabel == null) ? "" : minLabel);
        this.midLabel = ( (midLabel == null) ? "" : midLabel);
        this.maxLabel = ( (maxLabel == null) ? "" : maxLabel);
    }


    /**
     * Constructs a new float control object with the given parameters.
     * The labels for the minimum, maximum, and mid-point values are set
     * to zero-length strings.
     *
     * @param type the kind of control represented by this float control object
     * @param minimum the smallest value permitted for the control
     * @param maximum the largest value permitted for the control
     * @param precision the resolution or granularity of the control.
     * This is the size of the increment between discrete valid values.
     * @param updatePeriod the smallest time interval, in microseconds, over which the control
     * can change from one discrete value to the next during a {@link #shift(float,float,int) shift}
     * @param initialValue the value that the control starts with when constructed
     * @param units the label for the units in which the control's values are expressed,
     * such as "dB" or "frames per second"
     *
     * @throws IllegalArgumentException if {@code minimum} is greater
     *     than {@code maximum} or {@code initialValue} does not fall
     *     within the allowable range
     */
    protected FloatControl(Type type, float minimum, float maximum,
            float precision, int updatePeriod, float initialValue, String units) {
        this(type, minimum, maximum, precision, updatePeriod,
                initialValue, units, "", "", "");
    }



    // METHODS


    /**
     * Sets the current value for the control.  The default implementation
     * simply sets the value as indicated.  If the value indicated is greater
     * than the maximum value, or smaller than the minimum value, an
     * IllegalArgumentException is thrown.
     * Some controls require that their line be open before they can be affected
     * by setting a value.
     * @param newValue desired new value
     * @throws IllegalArgumentException if the value indicated does not fall
     * within the allowable range
     */
    public void setValue(float newValue) {

        if (newValue > maximum) {
            throw new IllegalArgumentException("Requested value " + newValue + " exceeds allowable maximum value " + maximum + ".");
        }

        if (newValue < minimum) {
            throw new IllegalArgumentException("Requested value " + newValue + " smaller than allowable minimum value " + minimum + ".");
        }

        value = newValue;
    }


    /**
     * Obtains this control's current value.
     * @return the current value
     */
    public float getValue() {
        return value;
    }


    /**
     * Obtains the maximum value permitted.
     * @return the maximum allowable value
     */
    public float getMaximum() {
        return maximum;
    }


    /**
     * Obtains the minimum value permitted.
     * @return the minimum allowable value
     */
    public float getMinimum() {
        return minimum;
    }


    /**
     * Obtains the label for the units in which the control's values are expressed,
     * such as "dB" or "frames per second."
     * @return the units label, or a zero-length string if no label
     */
    public String getUnits() {
        return units;
    }


        public String getMinLabel() {
        return minLabel;
    }


        public String getMidLabel() {
        return midLabel;
    }


        public String getMaxLabel() {
        return maxLabel;
    }


        public float getPrecision() {
        return precision;
    }


        public int getUpdatePeriod() {
        return updatePeriod;
    }


        public void shift(float from, float to, int microseconds) {
        
        if (from < minimum) {
            throw new IllegalArgumentException("Requested value " + from
                    + " smaller than allowable minimum value " + minimum + ".");
        }
        if (from > maximum) {
            throw new IllegalArgumentException("Requested value " + from
                    + " exceeds allowable maximum value " + maximum + ".");
        }
        setValue(to);
    }


    


        public String toString() {
        return new String(getType() + " with current value: " + getValue() + " " + units +
                          " (range: " + minimum + " - " + maximum + ")");
    }


    


        public static class Type extends Control.Type {


        


        

                public static final Type MASTER_GAIN            = new Type("Master Gain");

                public static final Type AUX_SEND                       = new Type("AUX Send");

                public static final Type AUX_RETURN                     = new Type("AUX Return");

                public static final Type REVERB_SEND            = new Type("Reverb Send");

                public static final Type REVERB_RETURN          = new Type("Reverb Return");


        

                        public static final Type VOLUME                         = new Type("Volume");


        

                public static final Type PAN                            = new Type("Pan");


        

                public static final Type BALANCE                        = new Type("Balance");


        

                public static final Type SAMPLE_RATE            = new Type("Sample Rate");


        

                protected Type(String name) {
            super(name);
        }

    } 

} 
