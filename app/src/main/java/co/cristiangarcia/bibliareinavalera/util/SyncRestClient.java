package co.cristiangarcia.bibliareinavalera.util;

import android.widget.ProgressBar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SyncRestClient {
    private static final String BASE_URL = "https://sync.tusversiculos.com/";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static class MiJsonHttpResponseHandler extends JsonHttpResponseHandler {
        ProgressBar pb;

        public MiJsonHttpResponseHandler(ProgressBar pb) {
            this.pb = pb;
        }

        public void onStart() {
            super.onStart();
            if (this.pb != null) {
                this.pb.setVisibility(0);
            }
        }

        public void onFinish() {
            super.onFinish();
            if (this.pb != null) {
                this.pb.setVisibility(8);
            }
        }
    }

    public static void get(String url, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
