package pl.linean.btleuart;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.UnsupportedEncodingException;


/**
 * BTLEUart - Bluetooth low energy uart library for nRF8001 and similar
 *
 * Created by Maciej Sady on 15.11.2016.
 */
public class BTLEUart extends ContextWrapper{

    final static String TAG = "BTLEUart";

    private UartService uartService;
    private Activity activity;
    private BTLEInit btleInit;
    private BTLEConnection btleConnection;
    private BTLEData btleData;
    private BTLESupport btleSupport;

    public interface BTLEInit {
        void onBTLEInitSuccess();
        void onBTLEInitFailed();
    }

    public interface BTLEConnection{
        void onConnected();
        void onDisconnected();
    }

    public interface BTLEData{
        void onDataAvailable(String received);
    }

    public interface BTLESupport{
        void onDeviceNotSupported();
    }

    public void connectDevice(String address){
        uartService.connect(address);
    }

    public void disconnectDevice(){
        uartService.disconnect();
    }

    public void writeMessage(String message){
        try {
            byte[] value = message.getBytes("UTF-8");
            uartService.writeRXCharacteristic(value);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public BTLEUart(Activity base) {
        super(base);
        this.activity = base;
        this.btleInit = (BTLEInit) base;
    }

    public BTLEUart withBTLEInitListener(BTLEInit listener){
        this.btleInit = listener;
        return this;
    }

    public BTLEUart withBTLEConnectionListener(BTLEConnection listener){
        this.btleConnection = listener;
        return this;
    }

    public BTLEUart withBTLESupportListener(BTLESupport listener){
        this.btleSupport = listener;
        return this;
    }

    public BTLEUart withBTLEDataListener(BTLEData listener){
        this.btleData = listener;
        return this;
    }

    public BTLEUart init(){
        initBTLE();
        return this;
    }

    private void initBTLE() {
        Intent bindIntent = new Intent(this, UartService.class);
        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(uartReceiver, makeGattUpdateIntentFilter());
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            uartService = ((UartService.LocalBinder) rawBinder).getService();

            if (!uartService.initialize() && btleInit != null) {
                Log.i(TAG, "Init Failed");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btleInit.onBTLEInitFailed();
                    }
                });

            } else if(btleInit != null) {
                Log.i(TAG, "Init Success");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btleInit.onBTLEInitSuccess();
                    }
                });
            }
        }

        public void onServiceDisconnected(ComponentName classname) {
            uartService = null;
        }
    };

    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    private final BroadcastReceiver uartReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();

            Log.i(TAG, "Action: " + action);
            switch(action){
                case UartService.ACTION_GATT_CONNECTED:
                    if(btleSupport != null)
                        uartService.enableTXNotification();

                    if(btleConnection != null)
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btleConnection.onConnected();
                            }
                        });
                    break;

                case UartService.ACTION_GATT_DISCONNECTED:
                    if(btleConnection != null)
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btleConnection.onDisconnected();
                            }
                        });
                    break;

                case UartService.DEVICE_DOES_NOT_SUPPORT_UART:
                    if(btleSupport != null)
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btleSupport.onDeviceNotSupported();
                            }
                        });
                    break;

                case UartService.ACTION_DATA_AVAILABLE:
                    if(btleData != null) {
                        try {
                            final String received = new String(intent.getByteArrayExtra(UartService.EXTRA_DATA), "UTF-8");
                            Log.i(TAG, "Received data: " + received);

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    btleData.onDataAvailable(received);
                                }
                            });

                        } catch (UnsupportedEncodingException | NullPointerException e) {
                            e.printStackTrace();
                            Log.i(TAG, "Unsupported data type exception");
                        }
                    }
                    break;
            }
        }
    };
}
