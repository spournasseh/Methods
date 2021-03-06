package com.gearback.methods;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

public class HttpGetRequest extends AsyncTask<String, Void, String> {
    private static final String REQUEST_METHOD = "GET";
    private int READ_TIMEOUT = 10000;
    private int CONNECTION_TIMEOUT = 10000;
    private Context activity;
    private Methods methods = new Methods();
    private final HttpGetRequest.TaskListener taskListener;

    public HttpGetRequest(Context activity, HttpGetRequest.TaskListener listener) {
        this.activity = activity;
        taskListener = listener;
    }

    public HttpGetRequest(Context activity, int timeout, HttpGetRequest.TaskListener listener) {
        this.activity = activity;
        taskListener = listener;
        READ_TIMEOUT = timeout;
        CONNECTION_TIMEOUT = timeout;
    }

    @Override
    protected void onPreExecute() {
        if (activity != null) {
            if (!methods.isInternetAvailable(activity)) {
                cancel(true);
            }
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        String stringUrl = strings[0];
        String result;
        String inputLine;

        try {
            URL myUrl = new URL(stringUrl);
            HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();

            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.connect();

            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            while((inputLine = reader.readLine()) != null){
                stringBuilder.append(inputLine);
            }
            reader.close();
            streamReader.close();
            result = stringBuilder.toString();
        }
        catch(IOException e){
            e.printStackTrace();
            if (e instanceof SocketTimeoutException) {
                result = "*SOCKET*";
            }
            else {
                result = "";
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        if(this.taskListener != null) {
            this.taskListener.onFinished(s);
        }
    }

    public interface TaskListener {
        public void onFinished(String result);
    }
}
