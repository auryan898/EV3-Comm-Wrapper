package com.auryan898.socketcomm.serialdata;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface SerialData {
  
  void dumpObject(DataOutputStream dos) throws IOException;

  void loadObject(DataInputStream dis) throws IOException;
}
