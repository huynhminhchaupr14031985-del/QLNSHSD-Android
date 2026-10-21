package vn.qlnshsd.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceError;
import android.webkit.JsResult;
import android.webkit.JsPromptResult;
import android.widget.Toast;

public class MainActivity extends Activity {

    private WebView webView;

    private static final String WEB_APP_URL =
        "https://script.google.com/macros/s/AKfycbwh8hbyWMG3XBNSHsdiULO-ipLMSYMbeOtjRmZ-42j77mhmG0i3ClD4lbMAwZOzKdZ7/exec";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cho phép màn hình co lại khi bàn phím xuất hiện.
        getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );

        webView = new WebView(this);

        // Cho phép WebView nhận thao tác chạm và lấy tiêu điểm.
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.requestFocus(View.FOCUS_DOWN);

        setContentView(webView);

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        // Hỗ trợ nhập liệu và lưu trạng thái trang.
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        // Xử lý tải trang web.
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    WebResourceRequest request) {
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

                // Để WebView sẵn sàng nhận thao tác chạm.
                view.setFocusable(true);
                view.setFocusableInTouchMode(true);
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
                        "Không tải được trang. Hãy kiểm tra Internet và quyền truy cập.",
                        Toast.LENGTH_LONG
                    ).show();
                }
            }
        });

        // Xử lý hộp thoại JavaScript trong WebView.
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
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                result.confirm();
                            }
                        })
                    .setOnCancelListener(
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                result.cancel();
                            }
                        })
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
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                result.confirm();
                            }
                        })
                    .setNegativeButton("Hủy",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                result.cancel();
                            }
                        })
                    .setOnCancelListener(
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                result.cancel();
                            }
                        })
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

        // Mở ứng dụng quản lý NSX - HSD.
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
