```java
package vn.qlnshsd.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String TAG = "QLNSHSD";
    private static final int FILE_CHOOSER_REQUEST = 1001;

    private WebView webView;

    // Nhận đường dẫn tệp được chọn từ Android.
    private ValueCallback<Uri[]> filePathCallback;

    // Địa chỉ Google Apps Script.
    private static final String WEB_APP_URL =
        "https://script.google.com/macros/s/AKfycbydeyc3jiEzhx5DU8AK1jISCAKFa3TDx1URMGBr0gpd29JNxB4iQ1dUOCLCmkLmkuRH/exec";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );

        // Khởi tạo WebView.
        webView = new WebView(this);
        setContentView(webView);

        // Cookie.
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

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
        settings.setSupportMultipleWindows(true);

        // Xử lý tải trang.
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(
                WebView view,
                WebResourceRequest request
            ) {
                Log.d(TAG, "Chuyen huong: " + request.getUrl());
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

                // Kiểm tra JavaScript.
                String debugScript =
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
                    "})()";

                view.evaluateJavascript(
                    debugScript,
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

            @Override
            public void onReceivedHttpError(
                WebView view,
                WebResourceRequest request,
                android.webkit.WebResourceResponse response
            ) {
                super.onReceivedHttpError(view, request, response);

                Log.e(
                    TAG,
                    "HTTP Error: " + response.getStatusCode()
                    + " - " + request.getUrl()
                );
            }
        });

        // Xử lý JavaScript và chọn tệp.
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onConsoleMessage(ConsoleMessage message) {
                Log.d(
                    TAG,
                    "JS Console: " + message.message()
                    + " - Dong: " + message.lineNumber()
                );
                return true;
            }

            // MỞ BỘ CHỌN TỆP CỦA ANDROID.
            @Override
            public boolean onShowFileChooser(
                WebView webView,
                ValueCallback<Uri[]> filePathCallback,
                FileChooserParams fileChooserParams
            ) {

                // Hủy yêu cầu chọn tệp trước nếu còn tồn tại.
                if (MainActivity.this.filePathCallback != null) {
                    MainActivity.this.filePathCallback.onReceiveValue(null);
                }

                MainActivity.this.filePathCallback = filePathCallback;

                try {
                    Intent intent = fileChooserParams.createIntent();

                    startActivityForResult(
                        intent,
                        FILE_CHOOSER_REQUEST
                    );

                    return true;

                } catch (Exception e) {

                    Log.e(TAG, "Khong mo duoc bo chon tep", e);

                    MainActivity.this.filePathCallback = null;

                    Toast.makeText(
                        MainActivity.this,
                        "Không thể mở trình chọn tệp.",
                        Toast.LENGTH_LONG
                    ).show();

                    return false;
                }
            }

            // Xử lý alert().
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

            // Xử lý confirm().
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

            // Không sử dụng prompt().
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

            // Xử lý cửa sổ mới.
            @Override
            public boolean onCreateWindow(
                WebView view,
                boolean isDialog,
                boolean isUserGesture,
                android.os.Message resultMsg
            ) {
                return false;
            }
        });

        // Tải Google Apps Script.
        webView.loadUrl(WEB_APP_URL);
    }

    // NHẬN KẾT QUẢ SAU KHI CHỌN TỆP.
    @Override
    protected void onActivityResult(
        int requestCode,
        int resultCode,
        Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_CHOOSER_REQUEST) {

            if (filePathCallback == null) {
                return;
            }

            Uri[] results = null;

            if (resultCode == Activity.RESULT_OK) {
                results = WebChromeClient.FileChooserParams.parseResult(
                    resultCode,
                    data
                );

                // Dự phòng nếu thiết bị trả về một URI đơn.
                if (results == null && data != null && data.getData() != null) {
                    results = new Uri[]{data.getData()};
                }
            }

            filePathCallback.onReceiveValue(results);
            filePathCallback = null;
        }
    }

    // Nút quay lại điện thoại.
    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    // Giải phóng tài nguyên.
    @Override
    protected void onDestroy() {

        if (filePathCallback != null) {
            filePathCallback.onReceiveValue(null);
            filePathCallback = null;
        }

        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
            webView = null;
        }

        super.onDestroy();
    }
}
```
