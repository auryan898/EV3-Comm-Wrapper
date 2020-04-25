package com.auryan898.socketcomm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import com.auryan898.socketcomm.serialdata.ByteData;
import com.auryan898.socketcomm.serialdata.SerialData;

public class MonitoredSocketStream {
  protected Socket socket;
  protected ReceiverCommand receiver;
  protected DataInputStream dis;

  protected DataOutputStream dos;

  protected ReentrantLock lock;
  protected Monitor monitor;
  protected Thread thread;

  protected boolean receiving = false;

  private MonitoredSocketStream(Socket socket, ReceiverCommand receiver) {
    this.socket = socket;
    this.receiver = receiver;
    this.lock = new ReentrantLock();
    this.monitor = new Monitor();
    this.thread = new Thread(monitor);
    try {
      dis = new DataInputStream(socket.getInputStream());
    } catch (IOException e) {
      dis = null;
    }
    try {
      dos = new DataOutputStream(socket.getOutputStream());
    } catch (IOException e) {
      dos = null;
    }
  }

  /**
   * Creates an instance of MonitoredSocketStream, but requires that the socket is
   * already connected, but the receiver can be null.
   * 
   * @param  socket   a socket which is already connected
   * @param  receiver a receiver object
   * @return
   */
  public static MonitoredSocketStream createInstance(Socket socket, ReceiverCommand receiver) {
    if (socket.isClosed() || !socket.isConnected()) {
      return null;
    }
    MonitoredSocketStream m = new MonitoredSocketStream(socket, receiver);
    m.thread.setDaemon(true);
    m.thread.start();
    return m;
  }

  protected class Monitor implements Runnable {
    @Override
    public void run() {
      while (true) {
        if (receiving) {
          updateReceiver();
        }
      }
    }

  }

  public void stopReceiver() {
    receiving = false;
    thread.interrupt();
  }

  public void startReceiver() {
    receiving = true;
  }

  /**
   * Returns an estimate of the number of bytes that can be read from the
   * DataInputStream of the socket. Returns -1 if it cannot be accessed.
   * 
   * @return number of bytes to read, or -1 if it cannot read
   */
  public int availableToRead() {
    try {
      return dis.available();
    } catch (IOException e) {
      return -1;
    }
  }

  private void updateReceiver() {
    if (receiver == null) {
      receiving = false;
      return;
    }
    try {
      if (dis.available() < 2) {
        return;
      }
      byte code1 = dis.readByte();
      byte code2 = dis.readByte();
      receiver.receive(code1, code2, dis, dos);
      dis.skip(dis.available()); // Skips the remaining bytes that were not read
    } catch (IOException e) {
      receiving = false;
    }
  }
  
  /**
   * Sends DataStream object to connected socket/device. data can be set to null
   * to send no data, to send just the two bytes event1 and event2.
   * 
   * <p>
   * Gives false when the socket is disconnected, or dos is null.
   * 
   * @param  event1 a byte value to help identify this send message
   * @param  event2 a byte value to help identify this send message
   * @param  data   a byte array that is sent to other device
   * @return        true for successfully sending message
   */
  public boolean send(byte event1, byte event2, byte[] data) {
    return send(event1, event2, new ByteData(data));
  }

  /**
   * Sends DataStream object to connected socket/device. dataStream can be null to
   * send no data,
   * to send just the two bytes event1 and event2.
   * 
   * <p>
   * Gives false when the socket is disconnected, or dos is null.
   * 
   * @param  event1     a byte value to help identify this send message
   * @param  event2     a byte value to help identify this send message
   * @param  serialData a DataStream containing data that is sent to other device
   * @return            true for successfully sending message
   */
  public boolean send(byte event1, byte event2, SerialData serialData) {
    lock.lock();
    try {
      dos.writeByte(event1);
      dos.writeByte(event2);
      if (serialData != null) {
        serialData.dumpObject(dos);
      }
      if (dos.size() > 0) {
        dos.flush();
      }
    } catch (IOException e) {
      return false;
    } finally {
      lock.unlock();
    }

    return true;
  }

  public DataInputStream getDis() {
    return dis;
  }

  public DataOutputStream getDos() {
    return dos;
  }

  public ReentrantLock getLock() {
    return lock;
  }

  public ReceiverCommand getReceiver() {
    return receiver;
  }

  /**
   * Changes the receiver object that is called upon receiving any data.
   * 
   * @param receiver ReceiverCommand object
   */
  public void setReceiver(ReceiverCommand receiver) {
    lock.lock();
    this.receiver = receiver;
    lock.unlock();
  }

  public Socket getSocket() {
    return socket;
  }
}
