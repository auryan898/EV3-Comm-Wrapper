package com.auryan898.socketcomm;

import java.math.BigInteger;

import com.auryan898.socketcomm.datastream.CommData;

public class BasicComm extends AdvancedComm {

  public BasicComm(CommChannel channel, AdvancedCommReceiver receiver) {
    super(channel, receiver);
  }

  public BasicComm(AdvancedCommReceiver receiver) {
    super(receiver);
  }

  public BasicComm(CommChannel channel) {
    super(channel);
  }
  
  public boolean send(byte event1, byte event2, CommData data) {
    return super.send(event1, event2, data.toBytesArray());
  }
}
