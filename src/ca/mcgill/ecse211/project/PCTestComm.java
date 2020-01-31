package ca.mcgill.ecse211.project;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

import lejos.robotics.Transmittable;

public class PCTestComm {
  public static void main(String[] args) {
    BasicComm comm = new BasicComm(CommReceiver.class,new String[] {
        "Print","Light","Sound"
    });
    comm.connect("10.0.1.1");
    
    Scanner scan = new Scanner(System.in);
    System.out.println("Do you wish to say something? ");
    final String line = scan.nextLine();
    comm.send("Print", new Transmittable() {
      
      @Override
      public void dumpObject(DataOutputStream dos) throws IOException {
        dos.writeChars(new String(line));
      }
      @Override
      public void loadObject(DataInputStream dis) throws IOException {
      }
      
    });
  }
}

/**
 * This can be in its own file, but this is just a demo to show it works.
 * 
 * @author Ryan Au
 *
 */
class PcCommReceiver extends BasicCommReceiver {

  /**
   * Using only the super constructor.
   * 
   * @param running    - will be set to true when used by BasicComm
   * @param dis        - supplied by BasicComm during connection
   * @param dos        - supplied by BasicComm during connection
   * @param commEvents - supplied by BasicComm during connection
   */
  public PcCommReceiver(BasicComm commSender, boolean running, DataInputStream dis, DataOutputStream dos,
      CommEvent commEvents) {
    super(commSender, running, dis, dos, commEvents);
  }

  @Override
  protected void receive(String event) {
    switch (event) {
      case "Print":
        byte[] arr = new byte[18];
        try {
          this.dis.read(arr);
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