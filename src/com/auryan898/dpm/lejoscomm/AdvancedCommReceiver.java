package com.auryan898.dpm.lejoscomm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import lejos.robotics.Transmittable;

public abstract class AdvancedCommReceiver {

  private AdvancedComm sender;
  private CommEvent events1;
  private CommEvent events2;

  public void setProps(AdvancedComm commSender) {
    CommEvent[] e = commSender.getEvents();
    this.events1 = e[0];
    this.events2 = e[1];
    this.sender = commSender;
  }

  /**
   * Gets the local object that describes communication events.
   * 
   * @return commEvents object
   */
  public CommEvent[] getEvents() {
    return sender.getEvents();
  }

  /**
   * Tells if the program has connected to a device yet.
   * 
   * @return if connected to something
   */
  public boolean isConnected() {
    return sender.isConnected();
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
  public boolean send(String event1, String event2, Transmittable data) {
    return sender.send(event1, event2, data);
  }

  /**
   * Can be used to properly stop the loop that checks for incoming data.
   */
  public void shutdown() {
    sender.shutdown();
  }

  /**
   * Basic method to read the data off of the input stream and return true if the
   * read was successful.
   * 
   * @param data the object which data will be written to
   * @return true if there was no problem reading the data
   */
  public boolean read(DataInputStream dis, Transmittable data) {
    if (data == null) {
      return false;
    }
    try {
      data.loadObject(dis);
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * receive Ideally implement this method using a switch-case for the different
   * String events that should be commonly shared between client and host.
   * 
   * @param event a String determining what type of message is sent through the
   *              connection
   * @param event2 a String determining what type of message is sent through the
   *              connection
   */
  protected abstract void receive(String event1, String event2, DataInputStream dis, DataOutputStream dos);

}
