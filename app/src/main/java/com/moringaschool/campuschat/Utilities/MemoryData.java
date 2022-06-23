package com.moringaschool.campuschat.Utilities;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public final class MemoryData {

    public static void saveName(String data, Context context){
        try {
            FileOutputStream fileOutputStream = context.openFileOutput("cc_name.txt", Context.MODE_PRIVATE);
            fileOutputStream.write(data.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getName(Context context){
        String data = "";
        try {
            FileInputStream fis = context.openFileInput("cc_name.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null){
                sb.append(line);
            }
            data = sb.toString();
        } catch (IOException e){
            e.printStackTrace();
        }
        return data;
    }

    public static void savePhone(String data, Context context){
        try {
            FileOutputStream fileOutputStream = context.openFileOutput("cc_phone.txt", Context.MODE_PRIVATE);
            fileOutputStream.write(data.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getPhone(Context context){
        String data = "";
        try {
            FileInputStream fis = context.openFileInput("cc_phone.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null){
                sb.append(line);
            }
            data = sb.toString();
        } catch (IOException e){
            e.printStackTrace();
        }
        return data;
    }

    public static void saveEmail(String data, Context context){
        try {
            FileOutputStream fileOutputStream = context.openFileOutput("cc_email.txt", Context.MODE_PRIVATE);
            fileOutputStream.write(data.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getEmail(Context context){
        String data = "";
        try {
            FileInputStream fis = context.openFileInput("cc_email.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null){
                sb.append(line);
            }
            data = sb.toString();
        } catch (IOException e){
            e.printStackTrace();
        }
        return data;
    }

    public static void saveLastMessage(String data, String chat_id, Context context){
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(chat_id+".txt", Context.MODE_PRIVATE);
            fileOutputStream.write(data.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getLastMessage(Context context, String chat_id){
        String data = "0";
        try {
            FileInputStream fis = context.openFileInput(chat_id+".txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null){
                sb.append(line);
            }
            data = sb.toString();
        } catch (IOException e){
            e.printStackTrace();
        }
        return data;
    }
}
