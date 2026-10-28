package vn.qlnshsd.app;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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

    private WebView webView;

    private static final String WEB_APP_URL =
        "https://script.google.com/macros/s/AKfycbzV6f9WfzTTlEzhuf5L02p1yljNMebTc6w_Xp0d1ta2ZYWtUoRRYhAAJGhhlL60DDWv/exec";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cho phép màn hình co lại khi bàn phím xuất hiện.
        getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );

        // Tạo WebView.
        webView = new WebView(this);

        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.requestFocus();

        setContentView(webView);

        // Cấu hình WebView.
        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setNeedInitialFocus(true);

        // Hỗ trợ nhập liệu trên điện thoại.
        settings.setLoadsImagesAutomatically(true);

        // Xử lý tải trang.
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    WebResourceRequest request) {

                // Cho phép WebView xử lý URL và các chuyển hướng.
                return false;
            }

            @Override
            public void onPageStarted(
                    WebView view,
                    String url,
                    Bitmap favicon) {

                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(
                    WebView view,
                    String url) {

                super.onPageFinished(view, url);

                view.setFocusable(true);
                view.setFocusableInTouchMode(true);

                // Không tự mở bàn phím khi vừa tải xong trang.
            }

            @Override
            public void onReceivedError(
                    WebView view,
                    WebResourceRequest request,
                    WebResourceError error) {

                super.onReceivedError(view, request, error);

                if (request.isForMainFrame()) {
                    Toast.makeText(
                        MainActivity.this,
                        "Không tải được trang. Hãy kiểm tra Internet.",
                        Toast.LENGTH_LONG
                    ).show();
                }
            }
        });

        // Xử lý alert(), confirm() và prompt() của JavaScript.
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
                    JsResult result) {

                new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Xác nhận")
                    .setMessage(message)
                    .setCancelable(true)
                    .setPositiveButton(
                        "Đồng ý",
                        (dialog, which) -> result.confirm()
                    )
                    .setNegativeButton(
                        "Hủy",
                        (dialog, which) -> result.cancel()
                    )
                    .setOnCancelListener(
                        dialog -> result.cancel()
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
                    JsPromptResult result) {

                // Không sử dụng prompt trong ứng dụng.
                result.cancel();

                return true;
            }
        });

        // Đảm bảo WebView nhận được sự kiện chạm.
        webView.setOnTouchListener((view, event) -> {

            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                view.requestFocus();
            }

            // Trả về false để không chặn sự kiện của HTML.
            return false;
        });

        // Mở Google Apps Script.
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
