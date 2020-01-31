package ca.mcgill.ecse211.project;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;

import lejos.remote.nxt.NXTCommConnector;
import lejos.remote.nxt.NXTConnection;
import lejos.remote.nxt.SocketConnector;
import lejos.robotics.Transmittable;

public class BasicComm {

  private boolean running = true;
  public static final long T_INTERVAL = 10;
  private boolean connected = false;
  private DataInputStream dis;
  private DataOutputStream dos;
  private Thread receiver;
  private BasicCommReceiver commReceiver;
  private Class<?> classReceiver;
  private CommEvent commEvents;

  public BasicComm(Class<?> classReceiver, String[] keys) {
    this.classReceiver = classReceiver;
    this.commEvents = new CommEvent(keys);
  }

  /**
   * For instantly connecting to the EV3.
   * 
   * @param ipAddress the ip address of the EV3 to connect to
   */
  public BasicComm(String ipAddress) {
    this.connect(ipAddress);
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
        dos.writeByte((byte)commEvents.valueOf(event));
        data.dumpObject(dos);
      }
    } catch (Exception e) {
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
  public void connect(String ipAddress) {
    if (connected) {
      return;
    }
    NXTCommConnector connector = new SocketConnector();
    NXTConnection conn = connector.connect(ipAddress, NXTConnection.PACKET);
    establishConn(conn);

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
      commReceiver = (BasicCommReceiver)classReceiver
          .getConstructor(BasicComm.class, boolean.class, 
              DataInputStream.class, DataOutputStream.class)
          .newInstance(this,running, dis, dos);
      
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    connected = true;
    receiver = new Thread(commReceiver);
    receiver.setDaemon(true);
    receiver.start();
  }
}
