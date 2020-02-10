package com.auryan898.dpm.lejoscomm;

import lejos.robotics.Transmittable;

public class BasicComm extends AdvancedComm {

  public BasicComm(CommChannel channel, AdvancedCommReceiver commReceiver, String[] events) {
    super(channel, commReceiver, events, null);
  }
  
  public BasicComm(AdvancedCommReceiver commReceiver, String[] events) {
    super(commReceiver, events, null);
  }
  
  public BasicComm(CommChannel channel, String[] events) {
    super(channel, events, null);
  }
  
  public BasicComm(String[] events) {
    super(events, null);
  }

  public boolean send(String event, Transmittable data) {
    return super.send(event, null, data);
  }
}
