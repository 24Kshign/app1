package com.maihaoche.commonbiz.service.http;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.maihaoche.commonbiz.module.dto.SessionRequest;
import com.maihaoche.commonbiz.module.dto.SessionRequestBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 类简介：
 * 作者：  yang
 * 时间：  17/6/6
 * 邮箱：  yangyang@maihaoche.com
 */

public class GsonConverterFactory extends Converter.Factory {

    private final Gson gson;

    private GsonConverterFactory(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
    }

    /**
     * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static GsonConverterFactory create() {
        return new GsonConverterFactory(new Gson());
    }


    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonResponseBodyConverter<>(gson, adapter);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonRequestBodyConverter<>(gson, adapter);
    }

    /**
     * 请求的数据body的转换器
     */

    public static class GsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
        private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
        private static final Charset UTF_8 = Charset.forName("UTF-8");

        private final Gson gson;
        private final TypeAdapter<T> adapter;

        GsonRequestBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.adapter = adapter;
        }

        @Override
        public RequestBody convert(T value) throws IOException {
            Buffer buffer = new Buffer();
            Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
            JsonWriter jsonWriter = gson.newJsonWriter(writer);
            adapter.write(jsonWriter, value);
            jsonWriter.close();
            if (value instanceof SessionRequest) {
                return SessionRequestBody.create(MEDIA_TYPE, buffer.readByteString());
            }
            return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
        }
    }

    /**
     * 网络返回的数据body的转换器
     */

    public static class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private final Gson gson;
        private final TypeAdapter<T> adapter;

        GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.adapter = adapter;
        }

        @Override
        public T convert(ResponseBody value) throws IOException {
            String s = readerToString(value.charStream());
            if (s.endsWith("\r\n")) {
                s = s.replace("\r\n", "");
            }
            JsonReader jsonReader = gson.newJsonReader(new StringReader(s));
            try {
                return adapter.read(jsonReader);
            } finally {
                value.close();
            }
        }

        public String readerToString(Reader reader) throws IOException {
            BufferedReader r = new BufferedReader(reader);
            StringBuilder b = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                b.append(line);
                b.append("\r\n");
            }
            r.close();
            return b.toString();

        }
    }
}
