package com.ilavista.minsksale;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JSONDataLoader {

    public JSONDataLoader() {

    }


    public List<MyEvent> getEventsFromJson(InputStream in) throws UnsupportedEncodingException {
        String jSonValue = getStringFromInputStream(in);
        Gson jSon = new Gson();

        Type listType = new TypeToken<List<MyEvent>>() {
        }.getType();

        ArrayList<MyEvent> events;
        events = jSon.fromJson(jSonValue, listType);

        return events;
    }

    // convert InputStream to String
    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }
}
