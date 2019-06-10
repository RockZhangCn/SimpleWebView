package com.rockzhang.simplewebview;

import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.ClientCertRequest;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.HttpAuthHandler;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.rockzhang.simplewebview.R.id.url;

public class MainActivity extends AppCompatActivity
{

    private Button   mBtnGo;
    private EditText mEditUrl;
    private WebView  mWebView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        certVerify();
                    }
                }).start();
            }
        });
    }

    private void certVerify()
    {
        String content = "abc=abc";
        //String cp_url = "https://szsdren.com";
        String cp_url = "https://ec2-52-80-37-4.cn-north-1.compute.amazonaws.com.cn/vivo/billings";

        int               respCode       = -1;
        HttpURLConnection httpConnection = null;
        OutputStream      os             = null;
        StringBuilder     responseMsg    = new StringBuilder();
        BufferedReader    reader         = null;

        try {
            URL url = new URL(cp_url);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod("POST");
            httpConnection.setConnectTimeout(15000);
            httpConnection.setReadTimeout(15000);
            os = httpConnection.getOutputStream(); // url地址无效时，此处会抛异常
            os.write(content.getBytes());
            os.flush();
            respCode = httpConnection.getResponseCode();

            if (respCode == HttpURLConnection.HTTP_OK) {// 返回码是200时才读取响应
                reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                String lines = "";

                while ((lines = reader.readLine()) != null) {
                    lines = new String(lines.getBytes(), "utf-8");
                    responseMsg.append(lines);
                }
            }else{
                System.out.println("not 200");
            }

            System.out.println("finish...");
        } catch (Exception e) {// 因为可能需要失败再通知，所以这里的异常不能抛出
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initView()
    {
        mBtnGo = (Button) findViewById(R.id.go);
        mEditUrl = (EditText) findViewById(url);
        mWebView = (WebView) findViewById(R.id.webview);


        mEditUrl.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                {
                    mWebView.loadUrl(mEditUrl.getText().toString());
                    return true;
                }
                return false;
            }
        });

        final WebChromeClient mWebChromeClient = new WebChromeClient()
        {
            @Override
            public void onProgressChanged(WebView view, int newProgress)
            {
                Log.e("ROCKEE", "onProgressChanged " + newProgress + " url is " + view.getUrl());
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title)
            {
                Log.e("ROCKEE", "onReceivedTitle " + title + " url is " + view.getUrl());
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon)
            {
                Log.e("ROCKEE", "onReceivedIcon " + " url is " + view.getUrl());
                super.onReceivedIcon(view, icon);
            }

            @Override
            public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed)
            {
                super.onReceivedTouchIconUrl(view, url, precomposed);
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback)
            {
                super.onShowCustomView(view, callback);
            }

            /**
             * @param view
             * @param requestedOrientation
             * @param callback
             * @deprecated
             */
            @Override
            public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback)
            {
                super.onShowCustomView(view, requestedOrientation, callback);
            }

            @Override
            public void onHideCustomView()
            {
                super.onHideCustomView();
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg)
            {
                return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
            }

            @Override
            public void onRequestFocus(WebView view)
            {
                super.onRequestFocus(view);
            }

            @Override
            public void onCloseWindow(WebView window)
            {
                super.onCloseWindow(window);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result)
            {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result)
            {
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result)
            {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            @Override
            public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result)
            {
                return super.onJsBeforeUnload(view, url, message, result);
            }

            /**
             * @param url
             * @param databaseIdentifier
             * @param quota
             * @param estimatedDatabaseSize
             * @param totalQuota
             * @param quotaUpdater
             * @deprecated
             */
            @Override
            public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater)
            {
                super.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize,
                        totalQuota, quotaUpdater);
            }

            /**
             * @param requiredStorage
             * @param quota
             * @param quotaUpdater
             * @deprecated
             */
            @Override
            public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater)
            {
                super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback)
            {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            @Override
            public void onGeolocationPermissionsHidePrompt()
            {
                super.onGeolocationPermissionsHidePrompt();
            }

            @Override
            public void onPermissionRequest(PermissionRequest request)
            {
                super.onPermissionRequest(request);
            }

            @Override
            public void onPermissionRequestCanceled(PermissionRequest request)
            {
                super.onPermissionRequestCanceled(request);
            }

            /**
             * @deprecated
             */
            @Override
            public boolean onJsTimeout()
            {
                return super.onJsTimeout();
            }

            /**
             * @param message
             * @param lineNumber
             * @param sourceID
             * @deprecated
             */
            @Override
            public void onConsoleMessage(String message, int lineNumber, String sourceID)
            {
                super.onConsoleMessage(message, lineNumber, sourceID);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage)
            {
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public Bitmap getDefaultVideoPoster()
            {
                return super.getDefaultVideoPoster();
            }

            @Override
            public View getVideoLoadingProgressView()
            {
                return super.getVideoLoadingProgressView();
            }

            @Override
            public void getVisitedHistory(ValueCallback<String[]> callback)
            {
                super.getVisitedHistory(callback);
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams)
            {
                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }
        };

        final WebViewClient mWebViewClient = new WebViewClient()
        {
            /**
             * @param view
             * @param url
             * @deprecated
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                Log.e("ROCK", "shouldOverrideUrlLoading url " + url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request)
            {
                Log.e("ROCK", "shouldOverrideUrlLoading request " + request);
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                Log.e("ROCK", "OnPageStarted url " + url);
                mEditUrl.setText(url);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                Log.e("ROCK", "onPageFinished url " + url);
                super.onPageFinished(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url)
            {
                Log.e("ROCK", "onLoadResource url " + url);
                super.onLoadResource(view, url);
            }

            @Override
            public void onPageCommitVisible(WebView view, String url)
            {
                super.onPageCommitVisible(view, url);
            }

            /**
             * @param view
             * @param url
             * @deprecated
             */
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url)
            {
                Log.e("ROCK", "shouldInterceptRequest url " + url);
                WebResourceResponse resp = NetworkHandler.getInstance().msgRequestTestMethod(url);
                if (resp == null) {
                    return super.shouldInterceptRequest(view, url);
                } else {
                    return resp;
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request)
            {
                Log.e("ROCK", "shouldInterceptRequest request " + request);
                return super.shouldInterceptRequest(view, request);
            }

            /**
             * @param view
             * @param cancelMsg
             * @param continueMsg
             * @deprecated
             */
            @Override
            public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg)
            {
                super.onTooManyRedirects(view, cancelMsg, continueMsg);
            }

            /**
             * @param view
             * @param errorCode
             * @param description
             * @param failingUrl
             * @deprecated
             */
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
            {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse)
            {
                super.onReceivedHttpError(view, request, errorResponse);
            }

            @Override
            public void onFormResubmission(WebView view, Message dontResend, Message resend)
            {
                super.onFormResubmission(view, dontResend, resend);
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload)
            {
                super.doUpdateVisitedHistory(view, url, isReload);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
            {
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void onReceivedClientCertRequest(WebView view, ClientCertRequest request)
            {
                super.onReceivedClientCertRequest(view, request);
            }

            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm)
            {
                super.onReceivedHttpAuthRequest(view, handler, host, realm);
            }

            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event)
            {
                return super.shouldOverrideKeyEvent(view, event);
            }

            @Override
            public void onUnhandledKeyEvent(WebView view, KeyEvent event)
            {
                super.onUnhandledKeyEvent(view, event);
            }

            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale)
            {
                super.onScaleChanged(view, oldScale, newScale);
            }

            @Override
            public void onReceivedLoginRequest(WebView view, String realm, String account, String args)
            {
                super.onReceivedLoginRequest(view, realm, account, args);
            }
        };

        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDefaultTextEncodingName("UTF-8");

        mBtnGo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mWebView.loadUrl(mEditUrl.getText().toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
