package com.auryan898.dpm.lejoscomm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import lejos.robotics.Transmittable;

public class HashMapData implements Transmittable {
  StringData dat = null;
  HashMap<String,Float> map = null;

  public HashMapData() {
    dat = new StringData();
    map = new HashMap<String,Float>();
  }
  
  public HashMap<String,Float> getHashMap(){
    return map;
  }
  
  public void setHashMap(HashMap<String,Float> obj) {
    map = obj;
  }
  
  @Override
  public void dumpObject(DataOutputStream dos) throws IOException {
    for (Entry<String,Float> entry : map.entrySet()) {
      dat.setString(entry.getKey());
      dat.dumpObject(dos);
      dos.writeFloat(entry.getValue());
    }
  }

  @Override
  public void loadObject(DataInputStream dis) throws IOException {
    while (dis.available() > 0) {
      dat.loadObject(dis);
      String str = dat.getString();
      Float num = dis.readFloat();
      map.put(str, num);
    }
  }

}
