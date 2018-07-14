package proyecto.prototicket;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

    private LayoutInflater mLayoutInflater;
    public ArrayList<BluetoothDevice> mDevices;


    private int  mViewResourceId;
    BluetoothAdapter mBluetoothAdapter;

    public DeviceListAdapter(Context context, int tvResourceId, ArrayList<BluetoothDevice> devices){
        super(context, tvResourceId,devices);
        this.mDevices = devices;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = tvResourceId;

    }


    public void setmViewResourceId(int mViewResourceId) {
        this.mViewResourceId = mViewResourceId;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(mViewResourceId, null);

        BluetoothDevice device = mDevices.get(position);

        if (device != null) {
            TextView deviceName = (TextView) convertView.findViewById(R.id.txt_nombreDispositivo);
            TextView addressdevice = (TextView) convertView.findViewById(R.id.txtaddrres);

            if (deviceName != null&&device.getName()!=mDevices.get(position).getName()) {

                    deviceName.setText(device.getName());
                    addressdevice.setText(device.getAddress());



            }else if (device.getName().equals(null)||mDevices.get(position).getName().equals(null)){
                deviceName.setText("No se encontraron dispositivos cercanos!!!");
                mBluetoothAdapter.cancelDiscovery();
            }


        }

        return convertView;
    }

}
