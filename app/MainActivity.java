package vn.qlnshsd.app;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceError;
import android.graphics.Bitmap;
import android.widget.Toast;

public class MainActivity extends Activity {

    private WebView webView;

    private static final String WEB_APP_URL =
        "https://script.google.com/macros/s/AKfycbxd6iEfT-PZIfjZoU1tUp-auIOl1YX3fwfCcn1gFhpqonRs1Cg4VQ_G27mzWsTAMeVN/exec";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

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
            webView.destroy();
        }
        super.onDestroy();
    }
}
