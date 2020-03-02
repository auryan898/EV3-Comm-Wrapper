package com.auryan898.socketcomm;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public abstract class AdvancedCommReceiver {

  protected AdvancedComm comm;

  /**
   * A method to set all of the necessary properties that this receiver needs.
   * 
   * @param advancedComm the comm object that calls this method
   */
  public void setProps(AdvancedComm comm) {
    this.comm = comm;
  }

  /**
   * 
   * @param code1 the first byte that is automatically extracted from dis
   * @param code2 the second byte that is automatically extracted from dis
   * @param dis   the input stream, that reads data from the connection
   * @param dos   the output stream, that writes data to the connection
   */
  public abstract void receive(byte code1, byte code2, DataInputStream dis,
      DataOutputStream dos);

}
