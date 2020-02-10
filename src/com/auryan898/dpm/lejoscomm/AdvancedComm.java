package com.auryan898.dpm.lejoscomm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import lejos.remote.nxt.NXTConnection;
import lejos.remote.nxt.SocketConnection;
import lejos.robotics.Transmittable;

public class AdvancedComm {

  private static final int CONNECTION_ATTEMPTS = 5;
  private boolean connected = false;
  private CommChannel channel = CommChannel.A;
  private DataInputStream dis;
  private DataOutputStream dos;
  private Thread receiver;
  private AdvancedCommReceiver commReceiver = null;
  private CommEvent commEvents;
  private CommEvent commEvents1;

  /**
   * Gives an instance of AdvancedComm that can send and receive information. Extend
   * AdvancedCommReceiver and define receive() which is called by AdvancedComm every
   * time it receives information from the other device.
   * 
   * @param commReceiver a new instance of any subclass of AdvancedCommReceiver
   * @param events1         user-chosen strings that identify each type of message
   *                     sent/received
   * @param events2 
   */
  public AdvancedComm(AdvancedCommReceiver commReceiver, String[] events1, String[] events2) {
    this.commReceiver = commReceiver;
    this.commEvents = new CommEvent(events1);
    this.commEvents = new CommEvent(events2);
  }

  /**
   * Gives an instance of AdvancedComm that can send and receive information. Extend
   * AdvancedCommReceiver and define receive() which is called by AdvancedComm every
   * time it receives information from the other device. The communications
   * "channel" can be changed, ie. the sockets use a tcp port other than the
   * default port 8888 (A), ranging to port 8880 (H).
   * 
   * @param channel      user-chosen "channel" for the communications (different
   *                     tcp port than default A)
   * @param commReceiver a new instance of any subclass of AdvancedCommReceiver
   * @param events1         user-chosen strings that identify each type of message
   *                     sent/received
   * @param events2 
   */
  public AdvancedComm(CommChannel channel, AdvancedCommReceiver commReceiver, String[] events1, String[] events2) {
    this.channel = channel;
    this.commReceiver = commReceiver;
    this.commEvents = new CommEvent(events1);
    this.commEvents = new CommEvent(events2);
  }

  /**
   * Gives an instance of AdvancedComm that can only connect and send information to
   * the other device.
   * 
   * @param events1 user-chosen strings that identify each type of message
   *             sent/received
   * @param events2 
   */
  public AdvancedComm(String[] events1, String[] events2) {
    this.commReceiver = new SimpleCommReceiver();
    this.commEvents = new CommEvent(events1);
    this.commEvents = new CommEvent(events2);
  }

  /**
   * Gives an instance of AdvancedComm that can only connect and send information to
   * the other device. The communications "channel" can be changed, ie. the
   * sockets use a tcp port other than the default port 8888 (A), ranging to port
   * 8880 (H).
   * 
   * @param channel user-chosen "channel" for the communications (different tcp
   *                port than default A)
   * @param events1    user-chosen strings that identify each type of message
   *                sent/received
   * @param events2 
   */
  public AdvancedComm(CommChannel channel, String[] events1, String[] events2) {
    this.channel = channel;
    this.commReceiver = new SimpleCommReceiver();
    this.commEvents = new CommEvent(events1);
    this.commEvents = new CommEvent(events2);
  }

  /**
   * Gets the local object that describes communication events.
   * 
   * @return commEvents object
   */
  public CommEvent getEvents() {
    return commEvents;
  }

  /**
   * Gets back the internal instance of the AdvancedCommReceiver, to be able to read
   * back values if convenient.
   * 
   * @return the original AdvancedCommReceiver, but connected and initialized
   */
  public AdvancedCommReceiver getReceiver() {
    if (isConnected()) {
      return commReceiver;
    } else {
      return null;
    }
  }

  /**
   * Tells if the program has connected to a device yet.
   * 
   * @return if connected to something
   */
  public boolean isConnected() {
    return this.connected;
  }

  /**
   * Basic method to send an event and an object that can be serialized into basic
   * data types and back again.
   * 
   * @param event predefined event that is shared knowledge between both PcComm
   *              and EV3Comm
   * @param data  an object which can be turned into basic data types, sent
   *              through dos and turned back into an object. Can be null to send
   *              nothing.
   * @return true for successful data sending
   */
  public boolean send(String event1, String event2, Transmittable data) {
    if (!connected) {
      return false;
    }
    try {
      synchronized (receiver) {
        dos.writeByte(commEvents.valueOf(event1));
        dos.writeByte(commEvents.valueOf(event2));
        if (data != null) {
          data.dumpObject(dos);
        }
        if (dos.size() > 0) {
          dos.flush();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * Waiting for another device to connect to this one and send it commands.
   */
  public void waitForConnection() {
    if (connected) {
      return;
    }
    int portNum = 8888 - channel.ordinal();
    try {
      ServerSocket ss = new ServerSocket(portNum);
      NXTConnection conn = new SocketConnection(ss.accept());
      establishConn(conn);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Connect to an ev3 brick with an ip address.
   * 
   * @param ipAddress the ip address of the brick
   */
  public boolean connect(String ipAddress) {
    if (connected) {
      return false;
    }
    int portNum = 8888 - channel.ordinal();

    for (int i = 0; i < CONNECTION_ATTEMPTS; i++) {
      try {
        NXTConnection conn = new SocketConnection(new Socket(ipAddress,portNum));
        establishConn(conn);
        return connected;
      } catch (IOException e) {
        
      }
    }
    return false;
  }

  /**
   * Initializes the data streams and starts the receiver daemon.
   * 
   * @param conn a connection to a brick
   */
  private void establishConn(NXTConnection conn) {
    if (conn == null) {
      return;
    }
    dis = new DataInputStream(conn.openInputStream());
    dos = new DataOutputStream(conn.openOutputStream());
    // Establish Receiver Daemon
    try {
      commReceiver.setProps(this, dis, commEvents);
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    connected = true;
    receiver = new Thread(new Receiver());
    receiver.setDaemon(true);
    receiver.start();
  }

  class Receiver implements Runnable {

    /**
     * Doesn't need to be overridden, defines the logic for checking received
     * information.
     */
    public void run() {
      while (isConnected()) {
        // update received messages
        try {
          byte code = dis.readByte();
          String event1 = commEvents.hasKey(code) ? commEvents.getKey(code) : "";
          code = dis.readByte();
          String event2 = commEvents.hasKey(code) ? commEvents.getKey(code) : "";
          synchronized (this) {
            commReceiver.receive(event1, event2, dis, dos);
          }
        } catch (IOException e) {
          shutdown();
        }
      }
    }
  }

  public void shutdown() {
    this.connected = false;
  }
}

class SimpleCommReceiver extends AdvancedCommReceiver {

  @Override
  protected void receive(String event1, String event2, DataInputStream dis, DataOutputStream dos) {
  }

}