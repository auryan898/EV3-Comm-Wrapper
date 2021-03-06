package com.auryan898.dpm.lejoscomm;

import java.util.HashMap;

public class CommEvent {
  private String[] keys;
  private HashMap<String, Integer> lookup;
  
  /**
   * Create an enum-like data structure that can handle similar use cases.
   * @param keys an initial set of keys that will not change
   */
  public CommEvent(String[] keys) {
    this.keys = keys;
    lookup = new HashMap<String, Integer>();
    
    if (keys == null) {
      return;
    }
    
    for (int i = 0; i < keys.length; i++) {
      lookup.put(keys[i], i);
    }
    
  }
  
  public String getKey(int i) {
    return keys == null ? null : keys[i];
  }
  
  /**
   * Get an integer value from a String.
   * @param key original key when instantiated, like an enum
   * @return
   */
  public int valueOf(String key) {
    return lookup.containsKey(key) ? lookup.get(key) : -1;
  }

  public boolean hasKey(int code) {
    // TODO Auto-generated method stub
    return 0 <= code && code < keys.length;
  }
}