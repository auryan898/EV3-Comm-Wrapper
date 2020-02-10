package com.auryan898.dpm.lejoscomm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;

import lejos.remote.nxt.SocketConnection;
import lejos.robotics.Transmittable;

public class ServerComm {  
  private ServerCommReceiver commReceiver;
  private CommChannel channel;
  private String[] events1;
  private String[] events2;
  private Thread accepter;
  public boolean accepting;
  
  private ArrayList<AdvancedComm> listComms;
  private ServerSocket ss;
  
  /**
   * Create an instance of a ServerComm, which allows a device to constantly listen for 
   * multiple connections, via the AdvancedComm class.  This ServerComm instance will 
   * store the new connections as instances of AdvancedComm, where a single send() sends 
   * the data to all client devices.  
   * 
   * @param channel the typical CommChannel to connect with
   * @param commReceiver the base receiver that will be cloned to each new AdvancedComm instance
   * @param events1 the first set of events received
   * @param events2 the second set of events received
   */
  public ServerComm(CommChannel channel, ServerCommReceiver commReceiver, String[] events1, String[] events2) {
    this.channel = channel;
    this.commReceiver = commReceiver;
    this.events1 = events1;
    this.events2 = events2;
    this.accepting = true;
    listComms = new ArrayList<AdvancedComm>();
  }
  
  /**
   * Starts the connection listener thread that will constantly accept incoming connections 
   * from other devices. 
   */
  public void acceptConnections() {
    int portNum = 8888 - channel.ordinal();
    try {
      ss = new ServerSocket(portNum);
      
      this.accepter = new Thread(new Accepter(ss));
      this.accepter.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * The send method sends the same data and events to every device that is connected to it.
   * 
   * @param event1 the first event
   * @param event2 the second event
   * @param data the information to be transmitted
   * @return whether every send() was successful
   */
  public boolean send(String event1, String event2, lejos.robotics.Transmittable data) {
    boolean allSent = true;
    for (AdvancedComm comm : listComms) {
      allSent &= comm.send(event1, event2, data);
    }
    
    return allSent;
  }
  
  /**
   * Stops the accepter thread from running.
   */
  public void stopAcceptingConnections() {
    accepting = false;
    try {
      ss.close();
    } catch (IOException e) {
      return;
    }
  }
  
  public class Accepter implements Runnable {
    private ServerSocket ss;
    

    public Accepter(ServerSocket ss) {
      this.ss = ss;
    }
    
    public void run() {
      while (accepting) {
        try {
          SocketConnection conn = new SocketConnection(ss.accept());
          AdvancedComm comm = new AdvancedComm(channel, commReceiver.clone(), events1, events2);
          comm.establishConn((lejos.remote.nxt.NXTConnection)conn);
          // add comm to the list of comms
          listComms.add(comm);
        } catch (IOException e) {
          break;
        }
      }
    }
  }
}
