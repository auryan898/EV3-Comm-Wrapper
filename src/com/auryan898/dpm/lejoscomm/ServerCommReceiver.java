package com.auryan898.dpm.lejoscomm;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public abstract class ServerCommReceiver extends AdvancedCommReceiver {

  @Override
  protected abstract void receive(String event1, String event2, DataInputStream dis, DataOutputStream dos);

  /**
   * Create a copy of this object, to be replicated across AdvancedComm instances.
   */
  protected abstract ServerCommReceiver clone();
}
