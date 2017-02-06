package com.koenhabets.survur.server;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class timer extends TimerTask {
    static String tempTime;
    static String tempData;
    static String outsideTemp;
    static String tempDataPrecise;
    static String tempDataLivingRoom;
    private int d = 0;
    private int counter = 500;
    private int counter2 = 999;
    private int tempArrayLength = 160;

    static void main() {
        TimerTask timerTask = new timer();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 10 * 1000);
    }

    @Override
    public void run() {
        double temp = TemperatureHandler.temp;
        double tempOutside = TemperatureHandler.tempOutside;
        counter++;
        counter2++;
        if (ActionHandler.sleeping && counter >= 499) {
            counter = 0;
        }
        if (!ActionHandler.inside && counter >= 499) {
            counter = 0;
        } else if (ActionHandler.inside && !ActionHandler.sleeping) {
            counter = 500;
        }
        if (!ActionHandler.sleeping && ActionHandler.inside) {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            if (d == 0) {
                LcdHandler.printLcd(hour + ":" + minute, "Binnen:" + temp);
                d = 1;
            } else {
                LcdHandler.printLcd(hour + ":" + minute, "Buiten:" + tempOutside);
                d = 0;
            }
        }

        if (counter == 6) {
            LcdHandler.disableBacklight();
        }

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if (counter2 > 120) {
            //TEMP moving avarage//////////////////////
            JSONParser parser = new JSONParser();
            JSONArray ja = new JSONArray();
            try {

                Object obj = parser.parse(new FileReader("temp.json"));

                ja = (JSONArray) obj;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //System.out.println("Stored: " + ja.toString());
            if (ja.size() > tempArrayLength) {
                ja.remove(0);
            }
            ja.add(TemperatureHandler.tempAvarage);
            //System.out.println("Saving: " + ja.toString());
            try {

                FileWriter file = new FileWriter("temp.json");
                file.write(ja.toJSONString());
                file.flush();
                file.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            tempTime = ja.toJSONString();

            //TEMP time//////////////////////
            parser = new JSONParser();
            ja = new JSONArray();
            try {

                Object obj = parser.parse(new FileReader("time.json"));

                ja = (JSONArray) obj;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //System.out.println("Stored: " + ja.toString());
            if (ja.size() > tempArrayLength) {
                ja.remove(0);
            }
            ja.add(hour + ":" + minute);
            //System.out.println("Saving: " + ja.toString());
            try {

                FileWriter file = new FileWriter("time.json");
                file.write(ja.toJSONString());
                file.flush();
                file.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            tempData = ja.toJSONString();


            //outside temp//////////////////////
            parser = new JSONParser();
            ja = new JSONArray();
            try {

                Object obj = parser.parse(new FileReader("tempOutside.json"));

                ja = (JSONArray) obj;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //System.out.println("Stored: " + ja.toString());
            if (ja.size() > tempArrayLength) {
                ja.remove(0);
            }
            ja.add(TemperatureHandler.tempOutside);
            //System.out.println("Saving: " + ja.toString());
            try {

                FileWriter file = new FileWriter("tempOutside.json");
                file.write(ja.toJSONString());
                file.flush();
                file.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            outsideTemp = ja.toJSONString();


            //living room temp//////////////////////
            parser = new JSONParser();
            ja = new JSONArray();
            try {

                Object obj = parser.parse(new FileReader("livingRoomTemp.json"));

                ja = (JSONArray) obj;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //System.out.println("Stored: " + ja.toString());
            if (ja.size() > tempArrayLength) {
                ja.remove(0);
            }
            try {
                ja.add(TemperatureHandler.getLivingRoomTemp());
            } catch (IOException e) {
                e.printStackTrace();
                ja.add(20);
            }
            //System.out.println("Saving: " + ja.toString());
            try {

                FileWriter file = new FileWriter("livingRoomTemp.json");
                file.write(ja.toJSONString());
                file.flush();
                file.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            tempDataLivingRoom = ja.toJSONString();





            counter2 = 0;
        }
        if (ActionHandler.hour == hour && ActionHandler.minute - 2 == minute && ActionHandler.inside){
            lightsHandler.Light("Bon");
        }
        if (ActionHandler.hour == hour && ActionHandler.minute + 5 == minute && ActionHandler.inside){
            lightsHandler.Light("Boff");
        }
    }
}