package com.auryan898.dpm.lejoscomm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;
import lejos.robotics.Transmittable;

public class PCTestComm {
  public static void main(String[] args) {
    BasicComm comm = new BasicComm(new CommReceiver(), new String[] { "Print", "Light", "Sound" });

    comm.connect("10.0.1.1");
    
    Scanner scan = new Scanner(System.in);
    String line = "";
    while (comm.isConnected()) {
      System.out.println("Do you wish to say something? ");
      line = scan.nextLine();
      comm.send("Print", new StringData(line));
    }
    comm.shutdown();
  }
}

/**
 * This can be in its own file, but this is just a demo to show it works.
 * 
 * @author Ryan Au
 *
 */
class PcCommReceiver extends BasicCommReceiver {
  StringData dat;
  
  public PcCommReceiver() {
    dat = new StringData();
  }
  
  @Override
  protected void receive(String event, DataInputStream dis, DataOutputStream dos) {
    switch (event) {
      case "Print":
        try {
          dat.loadObject(dis);
          System.out.println(dat.getString());
        } catch (IOException e) {
          e.printStackTrace();
          System.out.println("Error: failed Print event read");
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