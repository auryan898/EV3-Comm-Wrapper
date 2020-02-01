package com.auryan898.dpm.lejoscomm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import lejos.robotics.Transmittable;

/**
 * Transmittable String for usage with BasicComm and BasicCommReceiver send() and read().
 */
class StringData implements Transmittable {
  private String val;

  /**
   * Initializes with a blank string which is useful for reading String data.
   */
  public StringData() {
    this.val = "";
  }
  
  /**
   * Initializes with a user defined string, which is useful for sending data.
   * @param val the initial string, that can be sent
   */
  public StringData(String val) {
    this.val = val;
  }
  
  public void setString(String val) {
    if (val != null) {
      this.val = val;
    }
  }
  
  public String getString() {
    return val;
  }

  @Override
  public void dumpObject(DataOutputStream dos) throws IOException {
    dos.writeInt(val.length());
    dos.write(val.getBytes("UTF-8"));
  }

  @Override
  public void loadObject(DataInputStream dis) throws IOException {
    int len = dis.readInt();
    if (len > 0) {
      byte[] arr = new byte[len];
      dis.read(arr);
      val = new String(arr,"UTF-8");
    }
  }
}

