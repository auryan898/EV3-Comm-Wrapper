package com.auryan898.socketcomm.serialdata;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ByteData implements SerialData {

  private byte[] data;

  public ByteData() {
    this.data = null;
  }

  public ByteData(byte[] data) {
    this.data = data;
  }

  public byte[] getData() {
    return data;
  }

  public Byte[] getData(Byte[] buffer) {
    if (buffer == null) {
      return buffer;
    }

    for (int i = 0; i < buffer.length; i++) {
      if (i < data.length) {
        buffer[i] = data[i];
      } else {
        buffer[i] = null;
      }
    }
    return buffer;
  }

  @Override
  public void dumpObject(DataOutputStream dos) throws IOException {
    dos.writeInt(data.length);
    dos.write(data);
  }

  @Override
  public void loadObject(DataInputStream dis) throws IOException {
    data = new byte[dis.readInt()];
    dis.read(data);
  }

}
