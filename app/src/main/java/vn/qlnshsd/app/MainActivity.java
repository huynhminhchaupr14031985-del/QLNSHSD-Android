package vn.qlnshsd.app;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.JsResult;
import android.webkit.JsPromptResult;
import android.webkit.ConsoleMessage;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class MainActivity extends Activity {

    private WebView webView;

    private static final String APP_URL =
        "https://script.google.com/macros/s/AKfycbxOveWjcHYT32TUwKOaOg1U4med7YyBhXuY3z64vYUKun5oXwng_6PGmpsdlvMa4wN3/exec";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(false);

        webView.setWebViewClient(new WebViewClient());

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(
                    WebView view,
                    String url,
                    String message,
                    JsResult result) {

                new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Thông báo")
                    .setMessage(message)
                    .setPositiveButton("OK",
                        (dialog, which) -> result.confirm())
                    .setOnCancelListener(dialog -> result.cancel())
                    .show();

                return true;
            }

            @Override
            public boolean onJsConfirm(
                    WebView view,
                    String url,
                    String message,
                    JsResult result) {

                new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Xác nhận")
                    .setMessage(message)
                    .setPositiveButton("Đồng ý",
                        (dialog, which) -> result.confirm())
                    .setNegativeButton("Hủy",
                        (dialog, which) -> result.cancel())
                    .setOnCancelListener(dialog -> result.cancel())
                    .show();

                return true;
            }

            @Override
            public boolean onJsPrompt(
                    WebView view,
                    String url,
                    String message,
                    String defaultValue,
                    JsPromptResult result) {

                result.confirm(defaultValue);
                return true;
            }

@Override
public boolean onConsoleMessage(ConsoleMessage message) {
    String log = message.message()
        + " -- Dòng " + message.lineNumber()
        + " -- URL: " + message.sourceId();

    android.util.Log.d("QLNSHSD", log);

    if (message.messageLevel() == ConsoleMessage.MessageLevel.ERROR) {
        runOnUiThread(() ->
            new AlertDialog.Builder(MainActivity.this)
                .setTitle("Lỗi JavaScript")
                .setMessage(log)
                .setPositiveButton("Đóng", null)
                .show()
        );
    }

    return true;
}

 });

        webView.loadUrl(APP_URL);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
