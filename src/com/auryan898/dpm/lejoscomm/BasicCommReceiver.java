package com.auryan898.dpm.lejoscomm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;

public abstract class BasicCommReceiver {

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
   * Can be used to properly stop the loop that checks for incoming data.
   */
  public void shutdown() {
    commSender.shutdown();
  }
  
  /**
   * receive
   * Ideally implement this method using a switch-case for the different 
   * String events that should be commonly shared between client and host.
   * 
   * @param event a String determining what type of message is sent through the connection
   */
  abstract protected void receive(String event);
  
}
