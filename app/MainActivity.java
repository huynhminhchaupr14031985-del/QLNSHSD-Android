package vn.qlnshsd.app;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.ConsoleMessage;
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
        "https://script.google.com/macros/s/AKfycbwKsK5Fjqz1uxcB57z_2otp4AOU75svB7agXGPM7ZH5YGSvcPXx1TA57ShfpHzJ6bBj/exec";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );

        // Cho phép WebView nhận cookie khi Google chuyển hướng.
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(
            null,
            true
        );

        webView = new WebView(this);
        setContentView(webView);

        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.requestFocus();

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadsImagesAutomatically(true);

        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setNeedInitialFocus(true);

        // Theo dõi quá trình tải trang.
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(
                WebView view,
                WebResourceRequest request
            ) {
                return false;
            }

            @Override
            public void onPageStarted(
                WebView view,
                String url,
                Bitmap favicon
            ) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "Bat dau tai: " + url);
            }

            @Override
            public void onPageFinished(
                WebView view,
                String url
            ) {
                super.onPageFinished(view, url);

                Log.d(TAG, "Tai xong: " + url);

                view.setFocusable(true);
                view.setFocusableInTouchMode(true);

                // Kiểm tra JavaScript và địa chỉ trang đang chạy.
                view.evaluateJavascript(
                    "(function() {" +
                    " var old = document.getElementById('android-debug-check');" +
                    " if(old) old.remove();" +
                    " var box = document.createElement('div');" +
                    " box.id = 'android-debug-check';" +
                    " box.style.cssText = " +
                    " 'position:fixed;top:0;left:0;right:0;" +
                    "z-index:2147483647;background:#fff3cd;color:#000;" +
                    "padding:8px;font-size:12px;" +
                    "overflow-wrap:anywhere;pointer-events:none;';" +
                    " box.textContent = 'ANDROID WEBVIEW - JS HOAT DONG' +" +
                    " '\\nTrang: ' + location.href;" +
                    " document.body.appendChild(box);" +
                    " return 'OK';" +
                    "})()",
                    value -> Log.d(TAG, "JavaScript check: " + value)
                );
            }

            @Override
            public void onReceivedError(
                WebView view,
                WebResourceRequest request,
                WebResourceError error
            ) {
                super.onReceivedError(view, request, error);

                Log.e(TAG, "Loi tai trang: " + error.getDescription());

                if (request.isForMainFrame()) {
                    Toast.makeText(
                        MainActivity.this,
                        "Không tải được trang. Hãy kiểm tra Internet.",
                        Toast.LENGTH_LONG
                    ).show();
                }
            }
        });

        // Xử lý JavaScript alert, confirm và prompt.
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onConsoleMessage(ConsoleMessage message) {
                Log.d(
                    TAG,
                    "JS Console: " + message.message() +
                    " - Dong: " + message.lineNumber()
                );
                return true;
            }

            @Override
            public boolean onJsAlert(
                WebView view,
                String url,
                String message,
                JsResult result
            ) {
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Thông báo")
                    .setMessage(message)
                    .setPositiveButton(
                        "OK",
                        (dialog, which) -> result.confirm()
                    )
                    .setOnCancelListener(
                        dialog -> result.cancel()
                    )
                    .show();

                return true;
            }

            @Override
            public boolean onJsConfirm(
                WebView view,
                String url,
                String message,
                JsResult result
            ) {
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Xác nhận xóa số lô")
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(
                        "Đồng ý",
                        (dialog, which) -> result.confirm()
                    )
                    .setNegativeButton(
                        "Hủy",
                        (dialog, which) -> result.cancel()
                    )
                    .show();

                return true;
            }

            @Override
            public boolean onJsPrompt(
                WebView view,
                String url,
                String message,
                String defaultValue,
                JsPromptResult result
            ) {
                result.cancel();
                return true;
            }
        });

        // Mở ứng dụng Google Apps Script.
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
