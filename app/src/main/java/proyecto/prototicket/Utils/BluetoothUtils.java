package proyecto.prototicket.Utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import proyecto.prototicket.CrearTicket;


public class BluetoothUtils {

    Context context;

    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    public BluetoothUtils(Context context) {

        this.context = context;

        try {
            this.findBT();
            this.openBT();

        }catch(Exception e){
            e.printStackTrace();
        }
    }



    private void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                //Toast.makeText(this.context,"No bluetooth adapter available",Toast.LENGTH_LONG);
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                ((Activity)context).startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    if (device.getName().equals("printQln")) {
                        mmDevice = device;
                        CrearTicket ct =new CrearTicket();
                        Toast.makeText(ct.getApplicationContext(),"Conectado con impresora zebra",Toast.LENGTH_LONG);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void openBT() throws IOException {
        try {

            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                //myLabel.setText(data);
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                            ex.printStackTrace();
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void sendData(ArrayList<String> printData) throws IOException {
        try {

            String txtSend = this.completeLine("","-");
            mmOutputStream.write(txtSend.getBytes());
            for(String _printData: printData){

                txtSend = this.completeLine(_printData, " ");
                mmOutputStream.write(txtSend.getBytes());

            }

            txtSend = this.completeLine("","-");
            mmOutputStream.write(txtSend.getBytes());

            String endData = "\n\n\n\n\n";
            mmOutputStream.write(endData.getBytes());
            //Toast.makeText(this.context,"Data Send",Toast.LENGTH_LONG);

        } catch (NullPointerException e) {
            //Toast.makeText(this.context,"No printer detected",Toast.LENGTH_LONG);
            e.printStackTrace();
        } catch (Exception e) {
            //Toast.makeText(this.context,"No printer detected",Toast.LENGTH_LONG);
            e.printStackTrace();
        }
    }

    private String completeLine(String data, String completeChar){
        Integer limit = 32;
        String adjust = "";
        Integer count = limit - data.length() % limit;
        for(int i = 0; i < count; i++){
            adjust += completeChar;
        }
        return data + adjust;
    }

    public void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write(String prueba) throws IOException {
        mmOutputStream.write(prueba.getBytes());

    }
}
