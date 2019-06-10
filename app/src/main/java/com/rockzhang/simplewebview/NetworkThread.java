package com.rockzhang.simplewebview;

import android.os.HandlerThread;
import android.os.Process;

public class NetworkThread extends HandlerThread {
    private static final String VIVO_LOG_THREAD_NAME = "NetworkThread";
    private volatile static NetworkThread g_instance = null;

    public NetworkThread() {
        super(VIVO_LOG_THREAD_NAME, Process.THREAD_PRIORITY_BACKGROUND);
    }

    public static NetworkThread getInstance() {
        if (g_instance == null) {
            synchronized (NetworkThread.class) {
                if (g_instance == null) {
                    g_instance = new NetworkThread();
                    g_instance.start();
                }
            }
        }

        return g_instance;
    }
}