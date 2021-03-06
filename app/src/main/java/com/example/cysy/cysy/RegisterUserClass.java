package com.example.cysy.cysy;

/**
 * Created by youssouf on 28/04/17.
 */

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

class RegisterUserClass {

    private static final String TAG = "RegisterUserClass";

    private static final int READ_TIME_OUT = 15000;
    private static final int CONNECT_TIME_OUT = 15000;


    JSONObject sendPostRequest(String requestURL,
                               HashMap<String, String> postDataParams) {

        Log.d(TAG, "Sending the request to the server ...");

        URL url;
        JSONObject responseObject = new JSONObject();
        String response = "";

        int connect = 0, success = 0;
        String error = SignupActivity.SignupErrorMessages.ERROR_REGISTERING.getMessage();
        JSONObject user = null;

        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(READ_TIME_OUT);
            conn.setConnectTimeout(CONNECT_TIME_OUT);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            Log.d(TAG, "Request has been sent");

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                connect = 1;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                response = br.readLine();

                JSONObject responseParser = new JSONObject(response);
                success = responseParser.getInt(SignupActivity.SignupMessageKeys.SUCCESS.getKey());
                error = responseParser.getString(SignupActivity.SignupMessageKeys.ERROR.getKey());
                if (success == 1) {
                    user = responseParser.getJSONObject(SignupActivity.SignupMessageKeys.USER.getKey());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        try {
            responseObject.put(SignupActivity.SignupMessageKeys.CONNECT.getKey(), connect);
            responseObject.put(SignupActivity.SignupMessageKeys.SUCCESS.getKey(), success);
            responseObject.put(SignupActivity.SignupMessageKeys.ERROR.getKey(), error);
            responseObject.put(SignupActivity.SignupMessageKeys.USER.getKey(), user);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return responseObject;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}