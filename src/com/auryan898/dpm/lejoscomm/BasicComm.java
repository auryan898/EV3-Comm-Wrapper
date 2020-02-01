package com.auryan898.dpm.lejoscomm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

import lejos.remote.nxt.NXTCommConnector;
import lejos.remote.nxt.NXTConnection;
import lejos.remote.nxt.SocketConnector;
import lejos.robotics.Transmittable;

public class BasicComm {

  private static final int CONNECTION_ATTEMPTS = 5;
  private boolean connected = false;
  private boolean disableReceive = false; 
  private DataInputStream dis;
  private DataOutputStream dos;
  private Thread receiver;
  private BasicCommReceiver commReceiver = null;
  private CommEvent commEvents;

  /**
   * Gives an instance of BasicComm that can send and receive information. 
   * Extend BasicCommReceiver and define receive() which is called by 
   * BasicComm every time it receives information from the other device. 
   * 
   * @param commReceiver a new instance of any subclass of BasicCommReceiver
   * @param keys user-chosen strings that identify each type of message sent/received
   */
  public BasicComm(BasicCommReceiver commReceiver, String[] keys) {
    this.commReceiver = commReceiver;
    this.commEvents = new CommEvent(keys);
  }

  /**
   * Gives an instance of BasicComm that can only connect and send 
   * information to the other device.
   * 
   * @param keys user-chosen strings that identify each type of message sent/received
   */
  public BasicComm(String[] keys) {
    this.commReceiver = new SimpleCommReceiver();
    this.commEvents = new CommEvent(keys);
    this.disableReceive = true;
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
   * Gets back the internal instance of the BasicCommReceiver,
   * to be able to read back values if convenient. 
   * 
   * @return the original BasicCommReceiver, but connected and initialized
   */
  public BasicCommReceiver getReceiver() {
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
   *              through dos and turned back into an object.
   * @return
   */
  public boolean send(String event, Transmittable data) {
    if (!connected) {
      return false;
    }
    try {
      synchronized (receiver) {
        dos.writeByte(commEvents.valueOf(event));
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
    NXTCommConnector connector = new SocketConnector();
    NXTConnection conn = connector.waitForConnection(0, NXTConnection.PACKET);
    establishConn(conn);
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
    for (int i = 0; i < CONNECTION_ATTEMPTS; i++) {
      try {
        NXTCommConnector connector = new SocketConnector();
        NXTConnection conn = connector.connect(ipAddress, NXTConnection.PACKET);
        establishConn(conn);
        return connected;
      } catch (Exception e) {
        e.printStackTrace();
        
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
      commReceiver.setProps(this, dis, dos, commEvents);
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
      while (connected) {
        // update received messages
        try {
          byte code = dis.readByte();
          String event = commEvents.getKey(code);
          synchronized (this) {
            commReceiver.receive(event, dis, dos);
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

class SimpleCommReceiver extends BasicCommReceiver {

  @Override
  protected void receive(String event, DataInputStream dis, DataOutputStream dos) {
    
  }

}