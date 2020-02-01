package com.auryan898.dpm.lejoscomm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;

public class EV3TestComm {
  public static TextLCD lcd = LocalEV3.get().getTextLCD();
  
  public static void main(String[] args) {
    BasicComm comm = new BasicComm(new CommReceiver(),new String[] {
        "Print","Light","Sound"
    });
    
    lcd.drawString("Wait connection", 0, 1);
    comm.waitForConnection();
    lcd.drawString("Waiting for End",0,1);
    Button.waitForAnyPress();
    comm.shutdown();
  }
}

/**
 * This can be in its own file, but this is just a demo to show it works.
 * @author Ryan Au
 *
 */
class CommReceiver extends BasicCommReceiver {

  @Override
  protected void receive(String event, DataInputStream dis, DataOutputStream dos) {
    EV3TestComm.lcd.drawString("Start Receiving",0,7);
    switch (event) {
      case "Print":
        byte[] arr = new byte[18];
        try {
          dis.read(arr);
          EV3TestComm.lcd.clear();
          EV3TestComm.lcd.drawString(new String(arr, "UTF-8"),0,0);
          
        } catch (IOException e) {
          e.printStackTrace();
          EV3TestComm.lcd.drawString("Error Read",0,0);
        }
        break;
      case "Light":
        break;
      case "Sound":
        break;
      default:
    }
  }
  
}