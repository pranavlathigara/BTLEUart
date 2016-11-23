# BTLEUart
Bluetooth low energy UART for Android
Library works with nRF8001 module by Adafruit


It's my first library, so I'm sorry for my all mistakes. 
Fell free to report issues or pullrequest's :)


Usage:


    public class YourActivity extends Activity implements
            BTLEUart.BTLEConnection,
            BTLEUart.BTLEData,
            BTLEUart.BTLEInit{
            
      private BTLEUart uart;

      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);

          // All listeners are optional :) 
          uart = new BTLEUart(this)
                  .withBTLEConnectionListener(this)
                  .withBTLEDataListener(this)
                  .withBTLEInitListener(this)
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
        // Receive data from connected device
      }
      
      
      // Very IMPORTANT !
      @Override
      public void onDestroy(){
        if(uart != null)
            uart.onDestroy();
      }
      
      

In this library i use great Nordic Semiconductor UartService so below is Their copyright :)
Copyright (c) 2015, Nordic Semiconductor
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
