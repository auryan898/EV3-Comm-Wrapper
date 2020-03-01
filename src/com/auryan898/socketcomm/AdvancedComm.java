package com.auryan898.socketcomm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The primary class for creating/instantiating an object that allows one to
 * send communications between java programs.
 * 
 * @author Ryan Au
 *
 */
public class AdvancedComm {

  protected static final int CONNECTION_ATTEMPTS = 5;

  protected AdvancedCommReceiver receiver; // the user defined object that handles/parsee data
  protected CommChannel channel; // the current port represented by A-G letters
  protected CommStatus status; // current status of communications
  protected boolean connected = false;
  protected boolean waiting = false;

  protected Thread commAccepter; // a thread to accept connections
  protected Thread commReceiver; // a thread to accept data input

  protected ServerSocket serverSocket;
  protected Socket currSocket;
  protected DataInputStream dis;
  protected DataOutputStream dos;

  /**
   * 
   * 
   * 
   * @param channel  The channel is of A through G (inclusive)
   * @param receiver A user-defined receiver object
   */
  public AdvancedComm(CommChannel channel, AdvancedCommReceiver receiver) {
    this.receiver = receiver;
    this.channel = channel;
  }

  public boolean waitForConnection(boolean keepWaiting) {
    // Only wait if not already waiting
    if (waiting) {
      return false;
    }

    // establish the port (channel) to use
    int portNum = 8888 - channel.ordinal();
    try {
      serverSocket = new ServerSocket(portNum);
      commAccepter = new Thread();
    } catch (IOException | IllegalArgumentException | SecurityException e) {
      return false;
    }
    
    return true;
  }

  public boolean connect(String ipAddress) {
    // Only accepting connection if there is no existing connections
    switch (this.status) {
      case DISCONNECTED:
        break;
      default:
        return false; // when waiting or already connected
    }

    // establish the port (channel) to use
    int portNum = 8888 - channel.ordinal();
    // attempt several connections
    for (int i = 0; i < CONNECTION_ATTEMPTS; i++) {
      try {
        Socket s = new Socket(ipAddress, portNum);
        if (establishConnection(s)) {
          return true;
        }
      } catch (IOException e) {
      }
    }
    return false;
  }

  /**
   * 
   * 
   * @param socket
   * @return
   * @throws IOException
   */
  protected boolean establishConnection(Socket socket) throws IOException {
    // this failed because socket shouldn't be null
    if (socket == null) {
      return false;
    }
    // Get the input streams
    final DataInputStream dis = new DataInputStream(socket.getInputStream());
    final DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

    // Update the status of this comm
    status = CommStatus.CONNECTED;
    try {
      if (receiver != null) {
        // Set the properties of the receiver
        receiver.setProps(this, dis, dos);
        // Start the receiver thread
        commReceiver = new Thread(new Runnable() {
          public void run() {
            while (status == CommStatus.CONNECTED) {
              try {
               synchronized(this) {
                 byte code1 = dis.readByte();
                 byte code2 = dis.readByte();
                 receiver.receive(code1,code2,dis,dos);
               }
              } catch (IOException e) {
                shutdown();
              }              
            }
          }
        });
        commReceiver.setDaemon(true);
        commReceiver.start();
      }
    } catch (Exception e) {
    }

    // Set the instance variables for the data streams
    this.currSocket = socket;
    this.dis = dis;
    this.dos = dos;
    return true;
  }

  protected boolean isConnected() {
    // TODO Auto-generated method stub
    return false;
  }

  public void disconnect() {

  }

  public void shutdown() {

  }

  enum CommStatus {
    WAITER, CONNECTOR
  }
}
