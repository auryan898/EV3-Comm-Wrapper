package com.auryan898.dpm.lejoscomm;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public abstract class BasicCommReceiver extends AdvancedCommReceiver {

  @Override
  protected void receive(String event1, String event2, DataInputStream dis, DataOutputStream dos) {
    receive(event1, dis, dos);
  }
  
  protected abstract void receive(String event, DataInputStream dis, DataOutputStream dos);

}
