package com.auryan898.socketcomm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class defines an Advanced Communications Object. Instances of this class
 * are capable of utilizing sockets to open communications, that can connect to
 * other devices that are also running sockets. It uses ports 8888 to 8880,
 * represented by the CommChannel enumeration's range of A to G. Each
 * AdvancedComm instance can connect to each other and work seamlessly, sending
 * and receiving information. Use this class along with AdvancedCommReceiver for
 * the most low-level usage of this class (ie data streams and bytes).
 * 
 * <p>
 * Each Advanced Communications Object (shortened to comm from here on) will
 * follow a consistent protocol every time it sends and receives information.
 * When the comm sends data, it first sends *two signed bytes* that are for
 * identification purposes only. Each signed byte is called an event here, and
 * it allows one to differentiate between different messages. Next up is a
 * series of bytes, basically a byte array. That's it.
 * 
 * <p>
 * See BasicComm/BasicCommReceiver and ServerComm/ServerCommReceiver for
 * alternative extensions and the test code for implementations of this
 * communication method.
 * 
 * @author Ryan Au
 *
 */
public class AdvancedComm {

  protected static final int CONNECTION_ATTEMPTS = 5;

  protected AdvancedCommReceiver commReceiver; // the user defined object that handles/parsee data
  protected CommChannel channel; // the current port represented by A-G letters
  protected int port;
  protected boolean connected;
  protected boolean accepting;

  protected Thread threadAccepter; // a thread to accept connections
  protected Thread threadReceiver; // a thread to accept data input
  protected ServerSocket serverSocket;
  protected ReentrantLock lock;

  // Shared resources that need to be locked
  protected Socket conn;
  protected DataInputStream dis;
  protected DataOutputStream dos;

  /**
   * Constructor for an instance that can send and receive data between another
   * computer. As long as the other device can use raw sockets, then this program
   * will work.
   * 
   * @param channel  channel to connect to other comms CommChannel A through G
   *                 (inclusive)
   * @param receiver A user-defined AdvancedCommReceiver object
   */
  public AdvancedComm(CommChannel channel, AdvancedCommReceiver receiver) {
    this.commReceiver = receiver;
    this.channel = channel;
    this.port = 8888 - channel.ordinal();
    accepting = false;
    connected = false;
    lock = new ReentrantLock();
  }

  /**
   * Overloaded constructor that runs on Channel A (port 8888) by default.
   * 
   * @param receiver A user-defined AdvancedCommReceiver object
   */
  public AdvancedComm(AdvancedCommReceiver receiver) {
    this(CommChannel.A, receiver);
  }

  /**
   * Overloaded constructor that does not run a thread that continuously reads
   * data from the connection, running on specified channel. Specifically useful
   * for only sending data from one device to a different receiving device.
   * 
   * @param channel the channel to connect with
   */
  public AdvancedComm(CommChannel channel) {
    this(channel, null);
  }

  /**
   * Overloaded constructor that does not run a thread that continuously reads
   * data from the connection, running on Channel A (port 8888). Specifically
   * useful for only sending data from one device to a different receiving device.
   */
  public AdvancedComm() {
    this(CommChannel.A, null);
  }

  public boolean send(byte event1, byte event2, byte[] data) {
    if (!isConnected()) {
      return false;
    }

    try {
      lock.lock();
      dos.writeByte(event1);
      dos.writeByte(event2);
      dos.write(data);
      if (dos.size() > 0) {
        dos.flush();
      }
    } catch (IOException e) {
      return false;
    }

    return true;
  }

  /**
   * Sets this device into accepting mode, where it will wait until a device
   * connects to it via another AdvancedComm or subclass instance. Set keepWaiting
   * to true, to immediately return this function, and keep waiting for a
   * connection. Stops waiting once connection is made, or told to stopWaiting().
   * 
   * @param  keepWaiting if true it keeps accepting a connection (one at a time)
   * @return             true if successful, or if comm is successfully waiting
   */
  public boolean waitForConnection(boolean keepWaiting) {
    if (accepting || connected) {
      // Return false if already accepting or connected
      return false;
    }

    // try to create the server socket and establish the connection
    // or start accepter thread
    try {
      serverSocket = new ServerSocket(port);
      if (!keepWaiting) {
        establishConnection(serverSocket.accept());
      } else {
        threadAccepter = new Thread(new Accepter());
        threadAccepter.setDaemon(true);
        threadAccepter.start();
        accepting = true;
      }
    } catch (IOException e) { // Error in connecting
      // if fail, return false
      return false;
    }

    return true;
  }

  /**
   * Actively attempts to connect to an existing Server Socket (ie a waiting
   * AdvancedComm or subclass). It will try 5 times (CONNECTION_ATTEMPTS), then
   * throw an error because it can't connect to the specified ip address.
   * 
   * @param  ipAddress network address of the server/waiting comm
   * @return           true if connection success
   */
  public boolean connect(String ipAddress) {
    if (connected || accepting) {
      // Return false if already accepting or connected
      return false;
    }

    // try multiple times to create the socket and establish the connection
    for (int i = 0; i < CONNECTION_ATTEMPTS; i++) {
      try {
        establishConnection(new Socket(ipAddress, port));
        return true;
      } catch (IOException e) {
      }
    }

    // reaches here, it didn't connect
    return false;
  }

  /**
   * Given a socket instance, it will try to open an input and output stream for
   * further reading and writing of data. It will run the receiver object if it
   * was passed to the constructor.
   * 
   * @param  socket      a valid socket connection
   * @return             true if successful, false otherwise
   * @throws IOException some sort of read write error occurred while opening
   *                     streams
   */
  protected boolean establishConnection(Socket socket) throws IOException {
    if (socket == null || connected || conn != null || dis != null || dos != null) {
      return false;
    }
    lock.lock();
    dis = new DataInputStream(socket.getInputStream());
    dos = new DataOutputStream(socket.getOutputStream());
    conn = socket;
    if (commReceiver != null) {
      try {
        commReceiver.setProps(this);
        threadReceiver = new Thread(new Receiver());
        threadReceiver.setDaemon(true);
        threadReceiver.start();
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }
    }
    connected = true;
    lock.unlock();
    return true;
  }

  /**
   * Determines if the connection is still open.
   * 
   * @return true if connection is open
   */
  public boolean isConnected() {
    return connected && dis != null && dos != null && conn != null && conn.isConnected();
  }

  /**
   * Attempts to close all connections and free up memory.
   */
  public void close() {
    lock.lock();
    try {
      if (dis != null) {
        dis.close();
      }
    } catch (IOException e) {
    }
    try {
      if (dos != null) {
        dos.close();
      }
    } catch (IOException e) {
    }
    try {
      if (conn != null) {
        conn.close();
      }
    } catch (IOException e) {
    }

    dis = null;
    dos = null;
    conn = null;
    connected = false;
  }

  /**
   * Stops the accepter thread from running and checking for new connections.
   */
  public void stopWaiting() {

    try {
      if (serverSocket != null) {
        serverSocket.close();
      }
    } catch (IOException e) {
    }

    serverSocket = null;
    accepting = false;
  }

  /**
   * Inner class to define the thread that manages continuously accepting
   * connections from clients.
   * 
   * @author Ryan Au
   *
   */
  class Accepter implements Runnable {
    public void run() {
      accepting = true;
      try {
        establishConnection(serverSocket.accept());
      } catch (IOException e) {
      } finally {
        accepting = false;
        stopWaiting();
      }
    }
  }

  /**
   * Inner class to define the thread that manages continuously reading
   * data from clients.
   * 
   * @author Ryan Au
   *
   */
  class Receiver implements Runnable {
    public void run() {
      while (isConnected()) {
        try {
          byte event1 = dis.readByte();
          byte event2 = dis.readByte();
          lock.lock();
          commReceiver.receive(event1, event2, dis, dos);
          lock.unlock();
          connected = true;
        } catch (IOException e) {
          // Probably disconnected (other end closed)
          close();
        }
      }
    }
  }

  /**
   * The state of the current communication object.
   * 
   * @author Ryan Au
   *
   */
  enum CommStatus {
    ACCEPTOR, CONNECTOR, NONE
  }

  public boolean isWaiting() {
    return serverSocket != null && !serverSocket.isClosed() && accepting;
  }
}
