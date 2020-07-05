package mzrw.k2aplugin.bluetoothkeyboard.core;

import mzrw.k2aplugin.bluetoothkeyboard.layout.KeyCode;
import mzrw.k2aplugin.bluetoothkeyboard.layout.Layout;

/**
 * This class contains the USB_KEYBOARD_REPORT which is used to define the keystrokes format sent to the connected device.
 *
 * Additionally it can be used to transform a string into keystrokes according the USB_KEYBOARD_REPORT format.
 */
class UsbReports {
     static final byte[] USB_KEYBOARD_REPORT = intArrayToByteArray(new int[] {
            0x05, 0x01,    /*  USAGE_PAGE (Generic Desktop)		*/
            0x09, 0x06,    /*  USAGE (Keyboard)				*/
            0xa1, 0x01,    /*  COLLECTION (Application)			*/
            0x85, 0x01,    /*  REPORT_ID (Keyboard)         */
            0x05, 0x07,    /*  USAGE_PAGE (Keyboard)			*/
            0x19, 0xe0,    /*  USAGE_MINIMUM (Keyboard LeftControl)	*/
            0x29, 0xe7,    /*  USAGE_MAXIMUM (Keyboard Right GUI)		*/
            0x15, 0x00,    /*  LOGICAL_MINIMUM (0)				*/
            0x25, 0x01,    /*  LOGICAL_MAXIMUM (1)				*/
            0x75, 0x01,    /*  REPORT_SIZE (1)				*/
            0x95, 0x08,    /*  REPORT_COUNT (8)				*/
            0x81, 0x02,    /*  INPUT (Data,Var,Abs)			*/
            0x95, 0x01,    /*  REPORT_COUNT (1)				*/
            0x75, 0x08,    /*  REPORT_SIZE (8)				*/
            0x81, 0x03,    /*  INPUT (Cnst,Var,Abs)			*/
            0x95, 0x05,    /*  REPORT_COUNT (6)				*/
            0x75, 0x08,    /*  REPORT_SIZE (8)				*/
            0x15, 0x00,    /*  LOGICAL_MINIMUM (0)				*/
            0x25, 0x65,    /*  LOGICAL_MAXIMUM (101)			*/
            0x05, 0x07,    /*  USAGE_PAGE (Keyboard)			*/
            0x19, 0x00,    /*  USAGE_MINIMUM (Reserved)			*/
            0x29, 0x65,    /*  USAGE_MAXIMUM (Keyboard Application)	*/
            0x81, 0x00,    /*  INPUT (Data,Ary,Abs)			*/
            0xc0        /*  END_COLLECTION				*/
});
    private static final int BYTES_PER_KEYCODE = 7;

    /**
     * Transform the given integer array into a byte array. Integers are casted to bytes. (Only the lowest byte of the integer is used.)
     * @param intArray the given integer array
     * @return the byte array
     */
    private static byte[] intArrayToByteArray(int[] intArray) {
        byte[] byteArray = new byte[intArray.length];
        for(int i = 0; i < intArray.length; i++)
            byteArray[i] = (byte)intArray[i];

        return byteArray;
    }

    /**
     * Transform a given string into keystrokes using the given keyboard layout.
     * @param layout the given keyboard layout
     * @param string the given string
     * @return the keystrokes
     */
    static byte[][] stringToKeystrokeReports(Layout layout, String string) {
        final byte[][] keystrokes = new byte[string.length() * 2][BYTES_PER_KEYCODE]; // keycode + key up
        for(int i = 0; i < string.length(); i++) {
            final char key = string.charAt(i);
            final KeyCode keycode = layout.getKeycode(key);

            if(keycode == null)
                continue;

            final byte[] keystroke = new byte[] {(byte)keycode.modifier,
                    0,
                    (byte)keycode.code,
                    0,0,0,0
            };

            System.arraycopy(keystroke, 0, keystrokes[i*2], 0, BYTES_PER_KEYCODE);
        }

        return keystrokes;
    }
}
