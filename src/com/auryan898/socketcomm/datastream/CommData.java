package com.auryan898.socketcomm.datastream;

public abstract class CommData {

  /**
   * The data contained in this class, converted to an array of bytes.
   * 
   * @return a byte array representing the data of this class
   */
  public abstract byte[] toBytesArray();
  
  /**
   * Turn an array of bytes into the data defined for this class.
   * 
   * @return a byte array representing the data of this class
   */
  protected abstract CommData fromBytes(byte[] raw);
}
