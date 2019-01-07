package com.maihaoche.base.http;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * 类简介：OKHttp3 的sample中给出的，下载文件监听进度的例子
 * 作者：  yang
 * 时间：  17/6/23
 * 邮箱：  yangyang@maihaoche.com
 */

public abstract class ProgressCall {


    private static ExecutorService mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());//4核手机就是长度为4的线程池。

    public abstract OkHttpClient getOkhttpClient();

    public abstract void onResponse(ResponseBody responseBody);

    public abstract void onError(Exception exception);

    public abstract void update(long bytesRead, long contentLength, boolean done);


    private OkHttpClient mOkHttpClient;

    public void call(String url) {
        mExecutorService.submit(() -> {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            if (mOkHttpClient == null) {
                mOkHttpClient = getOkhttpClient()
                        .newBuilder()
                        .addNetworkInterceptor(chain -> {
                            Response originalResponse = chain.proceed(chain.request());
                            return originalResponse.newBuilder()
                                    .body(new ProgressResponseBody(originalResponse.body(), (bytesRead, contentLength, done) -> ProgressCall.this.update(bytesRead, contentLength, done)))
                                    .build();
                        })
                        .build();
            }
            Response response = null;
            try {
                response = mOkHttpClient.newCall(request).execute();
                if (response != null && response.isSuccessful()) {
                    onResponse(response.body());
                } else {
                    onError(new IOException("请求出错 code " + response.code()));
                }
            } catch (Exception e) {
                onError(e);
            } finally {
                if (response != null) {
                    response.close();
                }
            }
        });
    }

    /**
     * okhttp中，通用的progressbody
     */
    private static class ProgressResponseBody extends ResponseBody {

        private final ResponseBody responseBody;
        private final ProgressListener progressListener;
        private BufferedSource bufferedSource;

        ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
            this.responseBody = responseBody;
            this.progressListener = progressListener;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                    return bytesRead;
                }
            };
        }
    }

    /**
     * 进度监听
     */
    private interface ProgressListener {
        void update(long bytesRead, long contentLength, boolean done);
    }
}