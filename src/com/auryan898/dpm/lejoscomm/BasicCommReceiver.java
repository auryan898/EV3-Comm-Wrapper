package com.auryan898.dpm.lejoscomm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

import lejos.robotics.Transmittable;

public abstract class BasicCommReceiver {

  private CommEvent commEvents;
  private DataInputStream dis;
  private DataOutputStream dos;
  private BasicComm commSender;

  public void setProps(BasicComm commSender, DataInputStream dis, DataOutputStream dos, CommEvent commEvents) {
    this.dis = dis;
    this.dos = dos;
    this.commEvents = commEvents;
    this.commSender = commSender;
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
   * Tells if the program has connected to a device yet.
   * 
   * @return if connected to something
   */
  public boolean isConnected() {
    return commSender.isConnected();
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
    return commSender.send(event, data);
  }

  /**
   * Can be used to properly stop the loop that checks for incoming data.
   */
  public void shutdown() {
    commSender.shutdown();
  }

  /**
   * Basic method to read the data off of the input stream and return true if the
   * read was successful.
   * 
   * @param data the object which data will be written to
   * @return true if there was no problem reading the data
   */
  public boolean read(Transmittable data) {
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
   */
  protected abstract void receive(String event, DataInputStream dis, DataOutputStream dos);

}
