package com.auryan898.socketcomm;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface ReceiverCommand {
  /**
   * Provides data from a stream on a socket that is currently receiving
   * information.
   * 
   * @param code1 the first byte that is automatically extracted from dis
   * @param code2 the second byte that is automatically extracted from dis
   * @param dis   the input stream, that reads data from the connection
   * @param dos   the output stream, that writes data to the connection
   */
  public void receive(byte code1, byte code2, DataInputStream dis,
      DataOutputStream dos);
}
