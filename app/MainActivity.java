package vn.qlnshsd.app;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String TAG = "QLNSHSD";

    private WebView webView;

    private static final String WEB_APP_URL =
        "https://script.google.com/macros/s/AKfycbzV6f9WfzTTlEzhuf5L02p1yljNMebTc6w_Xp0d1ta2ZYWtUoRRYhAAJGhhlL60DDWv/exec";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );

        webView = new WebView(this);
        setContentView(webView);

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setNeedInitialFocus(true);

        // Hỗ trợ cookie và phiên làm việc của Google Apps Script.
        CookieManager cookies = CookieManager.getInstance();
        cookies.setAcceptCookie(true);
        cookies.setAcceptThirdPartyCookies(webView, true);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "Trang đã tải: " + url);
            }

            @Override
            public void onReceivedError(
                    WebView view,
                    WebResourceRequest request,
                    WebResourceError error) {

                super.onReceivedError(view, request, error);

                if (request.isForMainFrame()) {
                    Log.e(TAG, "Lỗi tải trang: " + error.getDescription());

                    Toast.makeText(
                        MainActivity.this,
                        "Không tải được trang. Kiểm tra Internet.",
                        Toast.LENGTH_LONG
                    ).show();
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onConsoleMessage(ConsoleMessage message) {
                Log.d(
                    TAG,
                    "JS " + message.message()
                    + " | dòng " + message.lineNumber()
                    + " | " + message.sourceId()
                );

                return true;
            }

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
                    .setOnCancelListener(
                        dialog -> result.cancel())
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
                    .setCancelable(true)
                    .setPositiveButton("Đồng ý",
                        (dialog, which) -> result.confirm())
                    .setNegativeButton("Hủy",
                        (dialog, which) -> result.cancel())
                    .setOnCancelListener(
                        dialog -> result.cancel())
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

                result.cancel();
                return true;
            }
        });

        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);

        webView.loadUrl(WEB_APP_URL);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
            webView = null;
        }

        super.onDestroy();
    }
}
