package trans.jingjing.com.websocketdemo;

import android.util.Log;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http.RealInterceptorChain;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by xmq on 2017/4/17.
 */

public class HttpLoggingInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private final HttpLoggingInterceptor.Logger logger;
    private volatile HttpLoggingInterceptor.Level level;
    private OkHttpClient client;

    public HttpLoggingInterceptor(Logger logger , OkHttpClient client) {
        this.level = Level.NONE;
        this.logger = logger;
        this.client = client;
    }

    public HttpLoggingInterceptor setLevel(HttpLoggingInterceptor.Level level) {
        if (level == null) {
            throw new NullPointerException("level == null. Use Level.NONE instead.");
        } else {
            this.level = level;
            return this;
        }
    }

    public HttpLoggingInterceptor.Level getLevel() {
        return this.level;
    }

    public Response intercept(Chain chain) throws IOException {
        HttpLoggingInterceptor.Level level = this.level;
        Request request = chain.request();

        logger.log("HttpLoggingInterceptor:  " +client.dns().lookup("spear.xk12.cn").toString());
        if (level == HttpLoggingInterceptor.Level.NONE) {
            return chain.proceed(request);
        } else {
            boolean logBody = level == HttpLoggingInterceptor.Level.BODY;
            boolean logHeaders = logBody || level == HttpLoggingInterceptor.Level.HEADERS;
            boolean logEnable = level != HttpLoggingInterceptor.Level.NONE;
            RequestBody requestBody = request.body();
            boolean hasRequestBody = requestBody != null;
            Connection connection = chain.connection();
            Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
            if (logEnable) {
                String startNs = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
                if (!logHeaders && hasRequestBody) {
                    startNs = startNs + " (" + requestBody.contentLength() + "-byte body)";
                }

                this.logger.log(startNs);
            }

            if (logHeaders) {
                if (hasRequestBody) {
                    if (requestBody.contentType() != null) {
                        this.logger.log("Content-Type: " + requestBody.contentType());
                    }

                    if (requestBody.contentLength() != -1L) {
                        this.logger.log("Content-Length: " + requestBody.contentLength());
                    }
                }

                Headers var30 = request.headers();
                int buffer = 0;

                for (int response = var30.size(); buffer < response; ++buffer) {
                    String tookMs = var30.name(buffer);
                    if (!"Content-Type".equalsIgnoreCase(tookMs) && !"Content-Length".equalsIgnoreCase(tookMs)) {
                        this.logger.log(tookMs + ": " + var30.value(buffer));
                    }
                }

                if (logBody && hasRequestBody) {
                    if (this.bodyEncoded(request.headers())) {
                        this.logger.log("--> END " + request.method() + " (encoded body omitted)");
                    } else {
                        Buffer var32 = new Buffer();
                        requestBody.writeTo(var32);
                        Charset var33 = UTF8;
                        MediaType var35 = requestBody.contentType();
                        if (var35 != null) {
                            var33 = var35.charset(UTF8);
                        }

                        this.logger.log("");
                        if (isPlaintext(var32)) {
                            this.logger.log(var32.readString(var33));
                            this.logger.log("--> END " + request.method() + " (" + requestBody.contentLength() + "-byte body)");
                        } else {
                            this.logger.log("--> END " + request.method() + " (binary " + requestBody.contentLength() + "-byte body omitted)");
                        }
                    }
                } else {
                    this.logger.log("--> END " + request.method());
                }
            }

            long var31 = System.nanoTime();

            Response var34;
            try {
                var34 = chain.proceed(request);
            } catch (Exception var29) {
                if (logEnable) {
                    this.logger.log("<-- HTTP FAILED: " + var29);
                }
                throw var29;
            }

            long var36 = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - var31);
            ResponseBody responseBody = var34.body();
            long contentLength = responseBody.contentLength();
            String bodySize = contentLength != -1L ? contentLength + "-byte" : "unknown-length";
            if (logEnable) {
                this.logger.log("<-- " + var34.protocol().name() + ' ' + var34.code() + ' ' + var34.message() + ' ' + var34.request().url() + " (" + var36 + "ms" + (!logHeaders ? ", " + bodySize + " body" : "") + ')');
            }

            if (logHeaders) {
                Headers headers = var34.headers();
                int source = 0;

                for (int buffer1 = headers.size(); source < buffer1; ++source) {
                    this.logger.log(headers.name(source) + ": " + headers.value(source));
                }

                if (logBody && HttpHeaders.hasBody(var34)) {
                    if (this.bodyEncoded(var34.headers())) {
                        this.logger.log("<-- END HTTP (encoded body omitted)");
                    } else {
                        BufferedSource var37 = responseBody.source();
                        var37.request(9223372036854775807L);
                        Buffer var38 = var37.buffer();
                        Charset charset = UTF8;
                        MediaType contentType = responseBody.contentType();
                        if (contentType != null) {
                            try {
                                charset = contentType.charset(UTF8);
                            } catch (UnsupportedCharsetException var28) {
                                this.logger.log("");
                                this.logger.log("Couldn\'t decode the response body; charset is likely malformed.");
                                this.logger.log("<-- END HTTP");
                                return var34;
                            }
                        }

                        if (!isPlaintext(var38)) {
                            this.logger.log("");
                            this.logger.log("<-- END HTTP (binary " + var38.size() + "-byte body omitted)");
                            return var34;
                        }

                        if (contentLength != 0L) {
                            this.logger.log("");
                            this.logger.log(var38.clone().readString(charset));
                        }

                        this.logger.log("<-- END HTTP (" + var38.size() + "-byte body)");
                    }
                } else {
                    this.logger.log("<-- END HTTP");
                }
            }

            return var34;
        }
    }

    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer e = new Buffer();
            long byteCount = buffer.size() < 64L ? buffer.size() : 64L;
            buffer.copyTo(e, 0L, byteCount);

            for (int i = 0; i < 16 && !e.exhausted(); ++i) {
                int codePoint = e.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }

            return true;
        } catch (EOFException var6) {
            return false;
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

    public interface Logger {
        HttpLoggingInterceptor.Logger DEFAULT = new HttpLoggingInterceptor.Logger() {
            public void log(String message) {
//                Platform.get().log(4, message, (Throwable) null);
                Log.e("websocket", message);
            }
        };

        void log(String var1);
    }

    public static enum Level {
        NONE,
        BASIC,
        HEADERS,
        BODY;

        private Level() {
        }
    }
}

