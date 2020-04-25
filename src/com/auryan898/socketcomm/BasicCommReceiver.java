package com.auryan898.socketcomm;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import com.auryan898.socketcomm.serialdata.CommData;

public abstract class BasicCommReceiver extends AdvancedCommReceiver {

  @Override
  public void receive(byte code1, byte code2, DataInputStream dis, DataOutputStream dos) {
    
    
    receive(code1, code2, null);
  }
  
  public abstract void receive(byte code1, byte code2, CommData[] data);

}
