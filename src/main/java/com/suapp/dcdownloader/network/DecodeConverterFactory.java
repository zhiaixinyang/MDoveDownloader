package com.suapp.dcdownloader.network;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.suapp.dcdownloader.App;
import com.suapp.dcdownloader.utils.CipherUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by yuki on 16/9/27.
 */
public class DecodeConverterFactory extends Converter.Factory {
    private static final String TAG = "DecodeConvert";

    public static DecodeConverterFactory create() {
        return create(new Gson(), true);
    }


    public static DecodeConverterFactory create(boolean encrypt) {
        return create(new Gson(), encrypt);
    }

    public static DecodeConverterFactory create(Gson gson, boolean encrypt) {
        return new DecodeConverterFactory(gson, encrypt);
    }

    private final Gson gson;
    private boolean encrypt = true;

    private DecodeConverterFactory(Gson gson, boolean encrypt) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
        this.encrypt = encrypt;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        if (!encrypt) {
            return new GsonResponseBodyConverter<>(gson, adapter);
        }
        for (Annotation annotation : annotations) {
            if (NoEncrypt.class == annotation.annotationType()) {
                return new GsonResponseBodyConverter<>(gson, adapter);
            }
        }
        return new EncryptGsonResponseBodyConverter<>(adapter);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        if (!encrypt) {
            return new GsonRequestBodyConverter<>(gson, adapter);
        }
        for (Annotation annotation : methodAnnotations) {
            if (NoEncrypt.class == annotation.annotationType()) {
                return new GsonRequestBodyConverter<>(gson, adapter);
            }
        }
        return new EncryptGsonRequestBodyConverter<>(gson, adapter);
    }

    private class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private final Gson gson;
        private final TypeAdapter<T> adapter;

        GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.adapter = adapter;
        }

        @Override
        public T convert(ResponseBody value) throws IOException {
            JsonReader jsonReader = gson.newJsonReader(value.charStream());
            try {
                return adapter.read(jsonReader);
            } finally {
                value.close();
            }
        }
    }

    private class EncryptGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private final TypeAdapter<T> adapter;

        EncryptGsonResponseBodyConverter(TypeAdapter<T> adapter) {
            this.adapter = adapter;
        }

        @Override
        public T convert(ResponseBody value) throws IOException {
            try {
                byte[] json = CipherUtils.decrypt(value.bytes(),
                        CipherUtils.getAESKey(App.getAppContext()));
                return adapter.fromJson(new String(json));
            } catch (GeneralSecurityException e) {
                return null;
            }
        }
    }

    private class GsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
        private final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
        private final Charset UTF_8 = Charset.forName("UTF-8");

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
            return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
        }
    }

    private class EncryptGsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
        private final MediaType MEDIA_TYPE = MediaType.parse("application/octet-stream; charset=UTF-8");
        private final Charset UTF_8 = Charset.forName("UTF-8");

        private final Gson gson;
        private final TypeAdapter<T> adapter;

        EncryptGsonRequestBodyConverter(Gson gson, TypeAdapter<T> adapter) {
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
            byte[] body = buffer.readByteString().toByteArray();
            try {
                body = CipherUtils.encrypt(body,
                        CipherUtils.getAESKey(App.getAppContext()));
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
            return RequestBody.create(MEDIA_TYPE, body);
        }
    }
}
