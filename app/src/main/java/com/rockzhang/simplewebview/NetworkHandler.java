package com.rockzhang.simplewebview;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.webkit.WebResourceResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class NetworkHandler extends Handler {

    public static final int MESSAGE_REQUEST_TEST = 0;

    private static NetworkHandler s_instance = null;
    public static NetworkHandler getInstance() {
        if (s_instance == null) {
            synchronized (NetworkHandler.class) {
                if (s_instance == null) {
                    s_instance = new NetworkHandler(NetworkThread.getInstance().getLooper());
                }
            }
        }

        return s_instance;
    }

    private NetworkHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {

        switch (msg.what) {
            case MESSAGE_REQUEST_TEST:
                msgRequestTestMethod((String)msg.obj);
                break;

            default:
                break;
        }

        super.handleMessage(msg);
    }

    public WebResourceResponse msgRequestTestMethod(String checkURL) {
        Network network1 =  null;

        ConnectivityManager cm = (ConnectivityManager) BrowserApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = cm.getAllNetworks();

        for (Network network : networks) {
            if (network.toString().equals("195")) {
                network1 = network;
            }
            Log.i("NetworkHandler", "network is:" + network.toString());
        }

        network1 = cm.getActiveNetwork();
        if (network1 == null) {
            Log.i("NetworkHandler", "didn't find the specified network id");
            return null;
        }

        URLConnection urlConnection = null;
        InputStream inputStream = null;
        int respCode = 0;
        try {
            urlConnection = getResponseCode1(checkURL, network1);

            if (urlConnection == null) {
                Log.i("NetworkHandler", "get null urlConnection, return.");
                return null;
            }
            if (urlConnection instanceof HttpsURLConnection) {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) urlConnection;
                httpsURLConnection.setRequestMethod("GET");
                respCode = httpsURLConnection.getResponseCode();

                if (respCode / 100 == 2) {
                    inputStream = httpsURLConnection.getInputStream();
                }

            } else if (urlConnection instanceof HttpURLConnection) {

                HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
                httpURLConnection.setRequestMethod("GET");
                respCode = httpURLConnection.getResponseCode();
                if (respCode / 100 == 2) {
                    inputStream = httpURLConnection.getInputStream();
                }
            }
        } catch (IOException e) {
           e.printStackTrace();
        }

        Log.i("NetworkHandler", "resp code is " + respCode);

        if (inputStream != null) {
            Log.i("NetworkHandler", "ContentType : " + urlConnection.getContentType() + " Encoding: " + urlConnection.getContentEncoding());

            String encoding = null;

            String headerType = urlConnection.getContentType();

            String[] types = headerType.split(";");
            String contentType = types[0];

            if (types.length == 2) {
                encoding = types[1].split("=")[1];
            } else {
                encoding = "utf-8";
            }

            Log.i("NetworkHandler", "ContentType : " + contentType + " Encoding: " + encoding);
            WebResourceResponse myResp =  new WebResourceResponse(contentType, encoding, inputStream);
            return myResp;
        }

        return null;

    }

    private URLConnection getResponseCode1(String checkUrl, Network network) {
        URLConnection urlConnection = null;
        try {
            URL url = new URL(checkUrl);
            urlConnection = network.openConnection(url);
        } catch (Exception e) {
            return null;
        }


        urlConnection.setAllowUserInteraction(true);
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(false);
        urlConnection.setRequestProperty("Connection", "Keep-Alive");
        urlConnection.setRequestProperty("User-Agent", "SimpleBrowser-Test");
        Log.i("NetworkHandler", "after set header: " + urlConnection.getHeaderFields());
        if (urlConnection instanceof HttpsURLConnection) {
            HttpsURLConnection httpsUrlConnection = (HttpsURLConnection) urlConnection;
            httpsUrlConnection.setInstanceFollowRedirects(true);
            httpsUrlConnection.setConnectTimeout(5000);
            httpsUrlConnection.setReadTimeout(5000);
            httpsUrlConnection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return httpsUrlConnection;
        } else {
            HttpURLConnection httpUrlConnection = (HttpURLConnection) urlConnection;
            httpUrlConnection.setInstanceFollowRedirects(true);
            httpUrlConnection.setConnectTimeout(5000);
            httpUrlConnection.setReadTimeout(5000);
            return httpUrlConnection;
        }
    }

}
