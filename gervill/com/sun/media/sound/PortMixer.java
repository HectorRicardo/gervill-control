/*
 * Copyright (c) 2002, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package gervill.com.sun.media.sound;

import java.util.Vector;

import gervill.javax.sound.sampled.Control;
import gervill.javax.sound.sampled.Line;
import gervill.javax.sound.sampled.LineUnavailableException;
import gervill.javax.sound.sampled.Port;
import gervill.javax.sound.sampled.BooleanControl;
import gervill.javax.sound.sampled.CompoundControl;
import gervill.javax.sound.sampled.FloatControl;


/**
 * A Mixer which only provides Ports.
 *
 * @author Florian Bomers
 */
final class PortMixer extends AbstractMixer {

    // CONSTANTS
    private static final int SRC_UNKNOWN      = 0x01;
    private static final int SRC_MICROPHONE   = 0x02;
    private static final int SRC_LINE_IN      = 0x03;
    private static final int SRC_COMPACT_DISC = 0x04;
    private static final int SRC_MASK         = 0xFF;

    private static final int DST_UNKNOWN      = 0x0100;
    private static final int DST_SPEAKER      = 0x0200;
    private static final int DST_HEADPHONE    = 0x0300;
    private static final int DST_LINE_OUT     = 0x0400;
    private static final int DST_MASK         = 0xFF00;

    // INSTANCE VARIABLES
    private Port.Info[] portInfos;
    // cache of instantiated ports
    private PortMixerPort[] ports;

    // instance ID of the native implementation
    private long id = 0;

    // CONSTRUCTOR
    PortMixer(PortMixerProvider.PortMixerInfo portMixerInfo) {
        // pass in Line.Info, mixer, controls
        super(portMixerInfo,              // Mixer.Info
              null,                       // Control[]
              null,                       // Line.Info[] sourceLineInfo
              null);                      // Line.Info[] targetLineInfo

        if (Printer.trace) Printer.trace(">> PortMixer: constructor");

        int count = 0;
        int srcLineCount = 0;
        int dstLineCount = 0;

        try {
            try {
                id = nOpen(getMixerIndex());
                if (id != 0) {
                    count = nGetPortCount(id);
                    if (count < 0) {
                        if (Printer.trace) Printer.trace("nGetPortCount() returned error code: " + count);
                        count = 0;
                    }
                }
            } catch (Exception e) {}

            portInfos = new Port.Info[count];

            for (int i = 0; i < count; i++) {
                int type = nGetPortType(id, i);
                srcLineCount += ((type & SRC_MASK) != 0)?1:0;
                dstLineCount += ((type & DST_MASK) != 0)?1:0;
                portInfos[i] = getPortInfo(i, type);
            }
        } finally {
            if (id != 0) {
                nClose(id);
            }
            id = 0;
        }

        // fill sourceLineInfo and targetLineInfos with copies of the ones in portInfos
        sourceLineInfo = new Port.Info[srcLineCount];
        targetLineInfo = new Port.Info[dstLineCount];

        srcLineCount = 0; dstLineCount = 0;
        for (int i = 0; i < count; i++) {
            if (portInfos[i].isSource()) {
                sourceLineInfo[srcLineCount++] = portInfos[i];
            } else {
                targetLineInfo[dstLineCount++] = portInfos[i];
            }
        }

        if (Printer.trace) Printer.trace("<< PortMixer: constructor completed");
    }


    // ABSTRACT MIXER: ABSTRACT METHOD IMPLEMENTATIONS

    public Line getLine(Line.Info info) throws LineUnavailableException {
        Line.Info fullInfo = getLineInfo(info);

        if ((fullInfo != null) && (fullInfo instanceof Port.Info)) {
            for (int i = 0; i < portInfos.length; i++) {
                if (fullInfo.equals(portInfos[i])) {
                    return getPort(i);
                }
            }
        }
        throw new IllegalArgumentException("Line unsupported: " + info);
    }


    public int getMaxLines(Line.Info info) {
        Line.Info fullInfo = getLineInfo(info);

        // if it's not supported at all, return 0.
        if (fullInfo == null) {
            return 0;
        }

        if (fullInfo instanceof Port.Info) {
            //return AudioSystem.NOT_SPECIFIED; // if several instances of PortMixerPort
            return 1;
        }
        return 0;
    }


    protected void implOpen() throws LineUnavailableException {
        if (Printer.trace) Printer.trace(">> PortMixer: implOpen (id="+id+")");

        // open the mixer device
        id = nOpen(getMixerIndex());

        if (Printer.trace) Printer.trace("<< PortMixer: implOpen succeeded.");
    }

    protected void implClose() {
        if (Printer.trace) Printer.trace(">> PortMixer: implClose");

        // close the mixer device
        long thisID = id;
        id = 0;
        nClose(thisID);
        if (ports != null) {
            for (int i = 0; i < ports.length; i++) {
                if (ports[i] != null) {
                    ports[i].disposeControls();
                }
            }
        }

        if (Printer.trace) Printer.trace("<< PortMixer: implClose succeeded");
    }

    protected void implStart() {}
    protected void implStop() {}

    // IMPLEMENTATION HELPERS

    private Port.Info getPortInfo(int portIndex, int type) {
        switch (type) {
        case SRC_UNKNOWN:      return new PortInfo(nGetPortName(getID(), portIndex), true);
        case SRC_MICROPHONE:   return Port.Info.MICROPHONE;
        case SRC_LINE_IN:      return Port.Info.LINE_IN;
        case SRC_COMPACT_DISC: return Port.Info.COMPACT_DISC;

        case DST_UNKNOWN:      return new PortInfo(nGetPortName(getID(), portIndex), false);
        case DST_SPEAKER:      return Port.Info.SPEAKER;
        case DST_HEADPHONE:    return Port.Info.HEADPHONE;
        case DST_LINE_OUT:     return Port.Info.LINE_OUT;
        }
        // should never happen...
        if (Printer.debug) Printer.debug("unknown port type: "+type);
        return null;
    }

    int getMixerIndex() {
        return ((PortMixerProvider.PortMixerInfo) getMixerInfo()).getIndex();
    }

    Port getPort(int index) {
        if (ports == null) {
            ports = new PortMixerPort[portInfos.length];
        }
        if (ports[index] == null) {
            ports[index] = new PortMixerPort((Port.Info)portInfos[index], this, index);
            return ports[index];
        }
        // $$fb TODO: return (Port) (ports[index].clone());
        return ports[index];
    }

    long getID() {
        return id;
    }

    // INNER CLASSES

    /**
     * Private inner class representing a Port for the PortMixer.
     */
    private static final class PortMixerPort extends AbstractLine
            implements Port {

        private final int portIndex;
        private long id;

        // CONSTRUCTOR
        private PortMixerPort(Port.Info info,
                              PortMixer mixer,
                              int portIndex) {
            super(info, mixer, null);
            if (Printer.trace) Printer.trace("PortMixerPort CONSTRUCTOR: info: " + info);
            this.portIndex = portIndex;
        }


        // ABSTRACT METHOD IMPLEMENTATIONS

        // ABSTRACT LINE

        void implOpen() throws LineUnavailableException {
            if (Printer.trace) Printer.trace(">> PortMixerPort: implOpen().");
            long newID = ((PortMixer) mixer).getID();
            if ((id == 0) || (newID != id) || (controls.length == 0)) {
                id = newID;
                Vector vector = new Vector();
                synchronized (vector) {
                    nGetControls(id, portIndex, vector);
                    controls = new Control[vector.size()];
                    for (int i = 0; i < controls.length; i++) {
                        controls[i] = (Control) vector.elementAt(i);
                    }
                }
            } else {
                enableControls(controls, true);
            }
            if (Printer.trace) Printer.trace("<< PortMixerPort: implOpen() succeeded");
        }

        private void enableControls(Control[] controls, boolean enable) {
            for (int i = 0; i < controls.length; i++) {
                if (controls[i] instanceof BoolCtrl) {
                    ((BoolCtrl) controls[i]).closed = !enable;
                }
                else if (controls[i] instanceof FloatCtrl) {
                    ((FloatCtrl) controls[i]).closed = !enable;
                }
                else if (controls[i] instanceof CompoundControl) {
                    enableControls(((CompoundControl) controls[i]).getMemberControls(), enable);
                }
            }
        }

        private void disposeControls() {
            enableControls(controls, false);
            controls = new Control[0];
        }


        void implClose() {
            if (Printer.trace) Printer.trace(">> PortMixerPort: implClose()");
            // get rid of controls
            enableControls(controls, false);
            if (Printer.trace) Printer.trace("<< PortMixerPort: implClose() succeeded");
        }

        // METHOD OVERRIDES

        // this is very similar to open(AudioFormat, int) in AbstractDataLine...
        public void open() throws LineUnavailableException {
            synchronized (mixer) {
                // if the line is not currently open, try to open it with this format and buffer size
                if (!isOpen()) {
                    if (Printer.trace) Printer.trace("> PortMixerPort: open");
                    // reserve mixer resources for this line
                    mixer.open(this);
                    try {
                        // open the line.  may throw LineUnavailableException.
                        implOpen();

                        // if we succeeded, set the open state to true and send events
                        setOpen(true);
                    } catch (LineUnavailableException e) {
                        // release mixer resources for this line and then throw the exception
                        mixer.close(this);
                        throw e;
                    }
                    if (Printer.trace) Printer.trace("< PortMixerPort: open succeeded");
                }
            }
        }

        // this is very similar to close() in AbstractDataLine...
        public void close() {
            synchronized (mixer) {
                if (isOpen()) {
                    if (Printer.trace) Printer.trace("> PortMixerPort.close()");

                    // set the open state to false and send events
                    setOpen(false);

                    // close resources for this line
                    implClose();

                    // release mixer resources for this line
                    mixer.close(this);
                    if (Printer.trace) Printer.trace("< PortMixerPort.close() succeeded");
                }
            }
        }

    } // class PortMixerPort

    /**
     * Private inner class representing a BooleanControl for PortMixerPort
     */
    private static final class BoolCtrl extends BooleanControl {
        // the handle to the native control function
        private final long controlID;
        private boolean closed = false;

        private static BooleanControl.Type createType(String name) {
            if (name.equals("Mute")) {
                return BooleanControl.Type.MUTE;
            }
            else if (name.equals("Select")) {
                // $$fb add as new static type?
                //return BooleanControl.Type.SELECT;
            }
            return new BCT(name);
        }


        private BoolCtrl(long controlID, String name) {
            this(controlID, createType(name));
        }

        private BoolCtrl(long controlID, BooleanControl.Type typ) {
            super(typ, false);
            this.controlID = controlID;
        }

        public void setValue(boolean value) {
            if (!closed) {
                nControlSetIntValue(controlID, value?1:0);
            }
        }

        public boolean getValue() {
            if (!closed) {
                // never use any cached values
                return (nControlGetIntValue(controlID)!=0)?true:false;
            }
            // ??
            return false;
        }

        /**
         * inner class for custom types
         */
        private static final class BCT extends BooleanControl.Type {
            private BCT(String name) {
                super(name);
            }
        }
    }

    /**
     * Private inner class representing a CompoundControl for PortMixerPort
     */
    private static final class CompCtrl extends CompoundControl {
        private CompCtrl(String name, Control[] controls) {
            super(new CCT(name), controls);
        }

        /**
         * inner class for custom compound control types
         */
        private static final class CCT extends CompoundControl.Type {
            private CCT(String name) {
                super(name);
            }
        }
    }

    /**
     * Private inner class representing a BooleanControl for PortMixerPort
     */
    private static final class FloatCtrl extends FloatControl {
        // the handle to the native control function
        private final long controlID;
        private boolean closed = false;

        // predefined float control types. See also Ports.h
        private final static FloatControl.Type[] FLOAT_CONTROL_TYPES = {
            null,
            FloatControl.Type.BALANCE,
            FloatControl.Type.MASTER_GAIN,
            FloatControl.Type.PAN,
            FloatControl.Type.VOLUME
        };

        private FloatCtrl(long controlID, String name,
                          float min, float max, float precision, String units) {
            this(controlID, new FCT(name), min, max, precision, units);
        }

        private FloatCtrl(long controlID, int type,
                          float min, float max, float precision, String units) {
            this(controlID, FLOAT_CONTROL_TYPES[type], min, max, precision, units);
        }

        private FloatCtrl(long controlID, FloatControl.Type typ,
                         float min, float max, float precision, String units) {
            super(typ, min, max, precision, 1000, min, units);
            this.controlID = controlID;
        }

        public void setValue(float value) {
            if (!closed) {
                nControlSetFloatValue(controlID, value);
            }
        }

        public float getValue() {
            if (!closed) {
                // never use any cached values
                return nControlGetFloatValue(controlID);
            }
            // ??
            return getMinimum();
        }

        /**
         * inner class for custom types
         */
        private static final class FCT extends FloatControl.Type {
            private FCT(String name) {
                super(name);
            }
        }
    }

    /**
     * Private inner class representing a port info
     */
    private static final class PortInfo extends Port.Info {
        private PortInfo(String name, boolean isSource) {
            super(Port.class, name, isSource);
        }
    }

    // open the mixer with the given index. Returns a handle ID
    private static native long nOpen(int mixerIndex) throws LineUnavailableException;
    private static native void nClose(long id);

    // gets the number of ports for this mixer
    private static native int nGetPortCount(long id);

    // gets the type of the port with this index
    private static native int nGetPortType(long id, int portIndex);

    // gets the name of the port with this index
    private static native String nGetPortName(long id, int portIndex);

    // fills the vector with the controls for this port
    private static native void nGetControls(long id, int portIndex, Vector vector);

    // getters/setters for controls
    private static native void nControlSetIntValue(long controlID, int value);
    private static native int nControlGetIntValue(long controlID);
    private static native void nControlSetFloatValue(long controlID, float value);
    private static native float nControlGetFloatValue(long controlID);

}
