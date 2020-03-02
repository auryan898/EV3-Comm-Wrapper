package com.auryan898.socketcomm.test;

import static org.junit.Assert.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.auryan898.socketcomm.AdvancedComm;
import com.auryan898.socketcomm.AdvancedCommReceiver;
import com.auryan898.socketcomm.CommChannel;

public class ConnectionTester {
  private AdvancedComm comm1;
  private AdvancedComm comm2;
  private byte event1;
  private byte event2;
  private byte[] dataSent;
  private byte[] dataReceived;
  private boolean receiveDone;

  @Before
  public void init() throws Exception {
    dataSent = new byte[100];
    dataReceived = new byte[100];
    comm1 = new AdvancedComm(CommChannel.A);
    comm2 = new AdvancedComm(CommChannel.A, new AdvancedCommReceiver() {
      public void receive(byte code1, byte code2, DataInputStream dis, DataOutputStream dos) {
        receiveDone = false;
        System.out.println("Status: Received info");
        assertEquals("Event Code 1 should be equivalent", event1, code1);
        assertEquals("Event Code 2 should be equivalent", event2, code2);
        System.out.println("Status: Received " + (int)code1 + " & " + (int)code2);
        try {
          dis.readFully(dataReceived);
        } catch (IOException e) {
          assertTrue("Connection Successfully closed while reading bytes. IOEXCeption", true);
        }
        assertArrayEquals("Data sent equals data received", dataSent, dataReceived);
        receiveDone = true;
      }
    });
  }

  @After
  public void destroy() throws Exception {
    if (comm1 != null)
      comm1.close();
    if (comm2 != null)
      comm2.close();
    comm1 = null;
    comm2 = null;
  }

  @Test
  public void testConnection() {
    assertTrue("Sever Waiting", comm2.waitForConnection(true));
    assertTrue("Server is waiting", comm2.isWaiting());
    assertTrue("Client Connected", comm1.connect("127.0.0.1"));

    long startTime = System.currentTimeMillis(); // fetch starting time
    while (comm2.isWaiting() && (System.currentTimeMillis() - startTime) < 2000) {
      // Waiting for a little while first to make connection, and end waiting
    }

    assertFalse("Server no longer waiting", comm2.isWaiting());
    assertTrue("Server Connected", comm1.isConnected());

    comm1.close();
    comm2.close();

    assertFalse("Server Disconnected", comm2.isConnected());
    assertFalse("Client Disconnected", comm1.isConnected());
    comm1 = null;
    comm2 = null;

  }

  @Test
  public void testDataSend() {
    assertTrue("Sever Waiting", comm2.waitForConnection(true));
    assertTrue("Client Connected", comm1.connect("127.0.0.1"));
    dataSent[0] = 12;
    dataSent[1] = 11;
    dataSent[2] = 127;
    dataSent[3] = -128;
    dataSent[4] = 0;
    event1 = 23;
    event2 = 0b11001;
    System.out.println("Status: Sending info");
    receiveDone = false;
    assertTrue("Data Sent to comm 2", comm1.send(event1, event2, dataSent));
    System.out.println("Status: Sent info");
    long startTime = System.currentTimeMillis(); // fetch starting time
    while (!receiveDone && (System.currentTimeMillis() - startTime) < 2000) {
      // Waiting for a little while first, to allow time to receive data and test it
    }
  }

  @Test
  public void testReconnection() {
    assertTrue("Sever Waiting", comm2.waitForConnection(true));
    assertTrue("Client Connected", comm1.connect("127.0.0.1"));

  }
}
