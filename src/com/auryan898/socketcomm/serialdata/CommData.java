package com.auryan898.socketcomm.serialdata;

public interface CommData {

  /**
   * The data contained in this class, converted to an array of bytes.
   * 
   * @return a byte array representing the data of this class
   */
  byte[] toBytesArray();
  
  /**
   * Turn an array of bytes into the data defined for this class.
   * 
   * @return a byte array representing the data of this class
   */
  CommData fromBytes(byte[] raw);
}
