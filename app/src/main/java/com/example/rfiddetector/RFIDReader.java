package com.example.rfiddetector;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.text.TextUtils;
import android.util.Log;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.*;

public class RFIDReader {

    private UsbManager manager;
    private HashMap<String, UsbDevice> deviceList;
    private UsbSerialDriver driver;
    private UsbSerialPort port;
    private UsbDeviceConnection connection;

    private int WRITE_WAIT_MILLIS = 1500;
    private int READ_WAIT_MILLIS = 1500;
    public int MAX_SCANNING_TIME = 10;
    private int WRITE_SLEEP_TIME = 500;

    private byte[] READ_TAG_COMMAND = { (byte) 10, (byte) 82, (byte) 50, (byte) 44, (byte) 48, (byte) 44, (byte) 52, (byte) 13 };
    private byte[] READ_MULTITAG_COMMAND = { (byte) 10, (byte) 85, (byte) 13 };
    private byte[] readBuffer = new byte[1024];

    private Context mContext;

    private HashSet<String> scannedTags = new HashSet<String>();

    public RFIDReader (Context context) throws Exception {
        this.mContext = context;

        this.manager = (UsbManager) this.mContext.getSystemService(Context.USB_SERVICE);
        this.deviceList = this.manager.getDeviceList();

        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(this.manager);
        if (availableDrivers.isEmpty()) {
            //throw new Exception("NO USB DEVICES!");
            Log.d("ERROR", "NO USB DEVICE");
        }

        // Open a connection to the first available driver.
        this.driver = availableDrivers.get(0);
        this.connection = manager.openDevice(this.driver.getDevice());
        if (this.connection == null) {
            // add UsbManager.requestPermission(driver.getDevice(), ..) handling here

        }
        this.port = this.driver.getPorts().get(0); // Most devices have just one port (port 0)
    }

    public void connect () throws IOException {
        try {
            this.port.open(this.connection);
            this.port.setParameters(38400, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
        }
        catch (IOException e) {
            Log.d("IOException", e.getMessage());
            throw e;
        }
    }

    public void disconnect () throws IOException {
        try {
            this.port.close();
        }
        catch (IOException e) {
            Log.d("IOException", e.getMessage());
            throw e;
        }
    }

    public int write (byte[] message) throws IOException {
        try {
            return this.port.write(message, this.WRITE_WAIT_MILLIS);
        }
        catch (IOException e) {
            Log.d("IOException", e.getMessage());
            throw e;
        }
    }

    public int read (byte[] buffer) throws IOException {
        try {
            return this.port.read(buffer, this.READ_WAIT_MILLIS);
        }
        catch (IOException e) {
            Log.d("IOException", e.getMessage());
            throw e;
        }
    }

    public String readStringFromBuffer (byte[] buffer, int length) throws Exception {
        try {
            return new String(Arrays.copyOf(buffer, length), "UTF-8");
        }
        catch (Exception e) {
            Log.d("Exception", e.getMessage());
            throw e;
        }
    }

    public String scanStep () throws Exception {
        try {
            this.port.write(this.READ_TAG_COMMAND, this.WRITE_WAIT_MILLIS);
            Thread.sleep(this.WRITE_SLEEP_TIME);
            int length = this.port.read(this.readBuffer, this.READ_WAIT_MILLIS);
            String answerStr = this.readStringFromBuffer(this.readBuffer, length);
            return answerStr;
        }
        catch (Exception e) {
            Log.d("Exception", e.getMessage());
            throw e;
        }
    }


    public String multiScanStep () throws Exception {
        try {
            this.port.write(this.READ_MULTITAG_COMMAND, this.WRITE_WAIT_MILLIS);
            Thread.sleep(this.WRITE_SLEEP_TIME);
            int length = this.port.read(this.readBuffer, this.READ_WAIT_MILLIS);
            String answerStr = this.readStringFromBuffer(this.readBuffer, length);
            return answerStr;
        }
        catch (Exception e) {
            Log.d("Exception", e.getMessage());
            throw e;
        }
    }

    public String filterScanningValues (String value) throws Exception {
        try {
            if(!TextUtils.isEmpty(value)) {
                if (!value.equals("\nR\r\n") && !value.equals("\nX\r\n") && !value.equals("\nE\r\n")) {
                    value = value.replaceAll("[\\r\\n]", "");
                    if(!this.scannedTags.contains(value)) {
                        this.scannedTags.add(value);
                        return value;
                    }
                }
            }
        }
        catch (Exception e) {
            throw e;
        }
        return null;
    }

    public ArrayList<String> explodeMultiReadResponse (String value) {
        ArrayList<String> responseArr = new ArrayList<>();
        try {
            if(!TextUtils.isEmpty(value)) {
                value = value.replaceAll("[\\r]", "");
                String[] tempArr = value.split("\\n");
                for (int i=0; i < tempArr.length; i++) {
                    if (!TextUtils.isEmpty(tempArr[i]) && !tempArr[i].equals("R") && !tempArr[i].equals("X") && !tempArr[i].equals("E") && !tempArr[i].equals("U")) {
                        responseArr.add(tempArr[i]);
                    }
                }
            }
        }
        catch (Exception e) {
            Notification.showPopUpWindow(this.mContext, e.getMessage());
        }
        return responseArr;
    }

    public HashSet<String> getScannedTags() {
        return this.scannedTags;
    }
}
