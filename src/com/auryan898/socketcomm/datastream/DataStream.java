package com.auryan898.socketcomm.datastream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class DataStream {
  
  public abstract void dumpObject(DataOutputStream dos) throws IOException;

  public abstract void loadObject(DataInputStream dis) throws IOException;
}
