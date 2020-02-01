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
 * Transmittable String
 */
class StringData implements Transmittable {
  private String val;

  public StringData(String val) {
    this.val = val;
  }

  @Override
  public void dumpObject(DataOutputStream dos) throws IOException {
    dos.writeChars(val);
  }

  @Override
  public void loadObject(DataInputStream dis) throws IOException {
    // TODO Auto-generated method stub

  }
}

/**
 * This can be in its own file, but this is just a demo to show it works.
 * 
 * @author Ryan Au
 *
 */
class PcCommReceiver extends BasicCommReceiver {
  @Override
  protected void receive(String event, DataInputStream dis, DataOutputStream dos) {
    switch (event) {
      case "Print":
        byte[] arr = new byte[18];
        try {
          dis.read(arr);
          System.out.println(new String(arr, "UTF-8"));
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