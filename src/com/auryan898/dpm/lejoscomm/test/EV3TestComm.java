package com.auryan898.dpm.lejoscomm.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.auryan898.dpm.lejoscomm.BasicComm;
import com.auryan898.dpm.lejoscomm.BasicCommReceiver;
import com.auryan898.dpm.lejoscomm.StringData;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;

public class EV3TestComm extends BasicCommReceiver {
  public static TextLCD lcd = LocalEV3.get().getTextLCD();
  private StringData dat;

  public static void main(String[] args) {
    BasicComm comm = new BasicComm(new EV3TestComm(), new String[] { "Print", "Light", "Sound" });

    lcd.drawString("Wait connection", 0, 1);
    comm.waitForConnection();
    lcd.drawString("Waiting for End", 0, 1);
    Button.waitForAnyPress();
    comm.shutdown();
  }

  public EV3TestComm() {
    dat = new StringData();
  }

  @Override
  protected void receive(String event, DataInputStream dis, DataOutputStream dos) {
    EV3TestComm.lcd.drawString("Start Receiving", 0, 7);
    switch (event) {
      case "Print":
        try {
          dat.loadObject(dis);
          lcd.clear();
          String temp = dat.getString();
          int len = temp.length();
          for (int i = 0; i < (len / 18) + 1; i++) {
            EV3TestComm.lcd.drawString(temp, 0, i);
            if (temp.length() >= 18) {
              temp = temp.substring(18);
            } else {
              break;
            }
          }
          dat.setString("Thanks.");
          send("Print",dat);
        } catch (IOException e) {
          e.printStackTrace();
          lcd.drawString("Error Read", 0, 0);
        }
        break;
      case "Light":
        break;
      case "Sound":
        break;
      default:
    }
  }
}