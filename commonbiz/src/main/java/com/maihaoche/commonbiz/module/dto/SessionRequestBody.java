package com.maihaoche.commonbiz.module.dto;

import android.support.annotation.Nullable;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.ByteString;

/**
 * 类简介：需要添加session数据的请求的body类型
 * 作者：  yang
 * 时间：  17/6/16
 * 邮箱：  yangyang@maihaoche.com
 */

public abstract class SessionRequestBody extends RequestBody {

    public static SessionRequestBody create(final @Nullable MediaType contentType, final ByteString content) {
        RequestBody requestBody = RequestBody.create(contentType, content);
        return new SessionRequestBody() {
            @Override
            public MediaType contentType() {
                return requestBody.contentType();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                requestBody.writeTo(sink);
            }
        };
    }

}
