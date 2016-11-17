# BTLEUart
Bluetooth low energy UART for Android

Library works with nRF8001 module by Adafruit


Usage:


    public class Test extends Activity implements
            BTLEUart.BTLEConnection,
            BTLEUart.BTLEData,
            BTLEUart.BTLEInit,
            BTLEUart.BTLESupport{

      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);

          // All listeners are optional :) 
          BTLEUart uart = new BTLEUart(this)
                  .withBTLEConnectionListener(this)
                  .withBTLEDataListener(this)
                  .withBTLEInitListener(this)
                  .withBTLESupportListener(this)
                  .init();

          uart.connectDevice("your bluetooth MAC address");


          if(You want to send something)
              uart.writeMessage("Your string message");


          if(You want to disconnect)
              uart.disconnectDevice();

      }

      @Override
      public void onBTLEInitSuccess() {
          // Bluetooth low energy init success - then you can try to connect to device
      }

      @Override
      public void onBTLEInitFailed() {
        // Fail, bluetooth wont work
      }

      @Override
      public void onConnected() {
        // Connected to device, now you can receive and send data
      }

      @Override
      public void onDisconnected() {
        // Disconnected from device, you can still try to connect to another
      }

      @Override
      public void onDataAvailable(String received) {
        // Connected device received data
      }


      @Override
      public void onDeviceNotSupported() {
        // Connected device dont support this kind of UART implementation
      }
