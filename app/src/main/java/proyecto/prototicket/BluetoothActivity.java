package proyecto.prototicket;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BluetoothActivity extends AppCompatActivity {
    BluetoothAdapter mBluetoothAdapter;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public ArrayList<BluetoothDevice> mBTDevices2 = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;
    public Thread t;
    private ImageButton Btn_buscar_disp;
    ListView lvNewDevices;
    public ProgressDialog pd;

    @Override
    protected void onDestroy() {
        //Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        /*unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver2);*/
        //mBluetoothAdapter.cancelDiscovery();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        lvNewDevices = (ListView) findViewById(R.id.Lv_Dispbt_disponibles);
        Btn_buscar_disp=(ImageButton) findViewById(R.id.ImgBtn_BuscarDispositivos);
        mBTDevices = new ArrayList<>();
        mBTDevices2 =new ArrayList<>();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        BuscarDispositivosDisponibles();

        Btn_buscar_disp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BuscarDispositivosDisponibles();
            }
        });
    }
    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by btnDiscover() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            //Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);

                        mDeviceListAdapter = new DeviceListAdapter(context,
                                R.layout.item_dispositivos_disponibles, mBTDevices);
                        lvNewDevices.setAdapter(mDeviceListAdapter);
            }
        }
    };

    public void BuscarDispositivosDisponibles(){
        lvNewDevices.setAdapter(null);
        mBTDevices.clear();
        pd=new ProgressDialog(BluetoothActivity.this);
        pd.setTitle("");
        pd.setMessage("Buscando dispositivos disponibles");
        pd.setProgress(0);
        pd.setMax(100);
        t =new Thread(new Runnable() {
            @Override
            public void run() {
                int progress=0;
                while (progress<=100){
                    try {
                        pd.setProgress(progress);
                        progress++;
                        Thread.sleep(150);

                    }catch (Exception ex){

                    }
                }
                pd.dismiss();
                //dispencontrados();

                BluetoothActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mBTDevices.size()==0){
                            Toast.makeText(BluetoothActivity.this,"No se encontraron dispositivos disponibles",Toast.LENGTH_SHORT).show();
                            mBluetoothAdapter.cancelDiscovery();
                            Toast.makeText(BluetoothActivity.this,"Búsqueda cancelada",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(BluetoothActivity.this, "Se encontraron "+mBTDevices.size()+" dispositivos disponibles", Toast.LENGTH_SHORT).show();
                            mBluetoothAdapter.cancelDiscovery();
                        }
                    }
                });
            }
        });
        btnDiscover();
        t.start();
        pd.show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean btnDiscover() {
        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            //Log.d(TAG, "btnDiscover: Canceling discovery.");

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);

        }
        if(!mBluetoothAdapter.isDiscovering()){

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);

        }




        return true;
    }

    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            //Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }
}
