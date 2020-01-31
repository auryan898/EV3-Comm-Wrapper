package ca.mcgill.ecse211.project;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;

public class EV3TestComm {
  public static TextLCD lcd = LocalEV3.get().getTextLCD();
  
  public static void main(String[] args) {
    BasicComm comm = new BasicComm(CommReceiver.class,new String[] {
        "Print","Light","Sound"
    });
    comm.waitForConnection();
  }
}

/**
 * This can be in its own file, but this is just a demo to show it works.
 * @author Ryan Au
 *
 */
class CommReceiver extends BasicCommReceiver {

  /**
   * Using only the super constructor.
   * @param running - will be set to true when used by BasicComm
   * @param dis - supplied by BasicComm during connection
   * @param dos - supplied by BasicComm during connection
   * @param commEvents - supplied by BasicComm during connection
   */
  public CommReceiver(BasicComm commSender, boolean running, DataInputStream dis, 
      DataOutputStream dos, CommEvent commEvents) {
    super(commSender, running, dis, dos, commEvents);
  }

  @Override
  protected void receive(String event) {
    switch (event) {
      case "Print":
        byte[] arr = new byte[18];
        try {
          this.dis.read(arr);
          EV3TestComm.lcd.clear();
          EV3TestComm.lcd.drawString(new String(arr, "UTF-8"),0,0);
        } catch (IOException e) {
          e.printStackTrace();
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