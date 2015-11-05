package com.temp.simplevoipdialer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.temp.simplevoipdialer.services.MainService;

/**
 * Created by klim-mobile on 10.09.2015.
 */
public class CheckBalance extends Activity {

    private WebView mBrowser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBrowser = new WebView(this);

        mBrowser.addJavascriptInterface(new MyJavaScriptInterface(this), "HTMLOUT");

        mBrowser.getSettings().setJavaScriptEnabled(true);
        mBrowser.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                mBrowser.loadUrl("javascript: {" +
                        "document.getElementById('loginType').value = '" + "1" + "';" +
                        "document.getElementById('name').value = '" + MainService.userName.trim() + "';" +
                        "document.getElementById('pass').value = '" + MainService.userPassword.trim() + "';" +
                        "document.getElementById('bt').click();" +
                        "};");
//                mBrowser.loadUrl("javascript:window.HTMLOUT.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }

            public void onPageFinished2(WebView view, String url) {
                mBrowser.loadUrl(url);
            }
        });
        mBrowser.setVisibility(View.VISIBLE);
        mBrowser.clearCache(true);
        mBrowser.clearHistory();
        setContentView(mBrowser);
        mBrowser.loadUrl("http://198.50.158.153/eng/index.html");

    }

    class MyJavaScriptInterface {

        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }

        @SuppressWarnings("unused")
        @JavascriptInterface
        public void showHTML(String html) {
            Toast.makeText(ctx, "jopa", Toast.LENGTH_LONG).show();
            new AlertDialog.Builder(ctx)
                    .setTitle("HTML")
                    .setMessage(html)
                    .setPositiveButton(android.R.string.ok, null)
                    .setCancelable(false)
                    .create()
                    .show();
        }

    }

}
