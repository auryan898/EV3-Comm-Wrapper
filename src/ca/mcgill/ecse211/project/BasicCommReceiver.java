package ca.mcgill.ecse211.project;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public abstract class BasicCommReceiver implements Runnable {

  private static final long T_INTERVAL = 10;
  private boolean running;
  private CommEvent commEvents;
  protected DataInputStream dis;
  protected DataOutputStream dos;
  protected BasicComm commSender;
  
  public BasicCommReceiver(BasicComm commSender, boolean running, DataInputStream dis, DataOutputStream dos, CommEvent commEvents) {
    this.running = running;
    this.dis = dis;
    this.dos = dos;
    this.commEvents = commEvents;
    this.commSender = commSender;
  }
  
  /**
   * Doesn't need to be overridden, defines the logic 
   * for checking received information.
   */
  public void run() {
    while (running) {
      // update received messages
      try {
        String event = commEvents.getKey(dis.readByte());
        synchronized (this) {
          receive(event);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      try {
        Thread.sleep(T_INTERVAL);
      } catch (InterruptedException e) {
        e.printStackTrace();
        break;
      }
    }
  }
  
  /**
   * Can be used to properly stop the loop that checks for incoming data.
   */
  public void shutdown() {
    running = false;
  }
  
  /**
   * receive
   * Ideally implement this method using a switch-case for the different 
   * String events that should be commonly shared between client and host.
   * 
   * @param event a String determining what type of message is sent through the connection
   */
  protected abstract void receive(String event);
  
}
