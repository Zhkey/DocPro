package com.lieyunwang.liemine.net;

import android.util.Log;

import com.google.gson.JsonParseException;
import com.lieyunwang.liemine.R;
import com.lieyunwang.liemine.mvp.presenter.base.BaseView;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

/**
 * 错误异常处理类
 * 1.Retrifit网络错误回调，有时候需要做提示
 */
public class ExceptionHandle {
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;

    public static ResponeThrowable handleException(Throwable e) {
        return handleException(e, null);
    }
    public static ResponeThrowable handleException(Throwable e, BaseView baseView) {
        ResponeThrowable ex;
        Log.e("tag", "e.toString = " + e.toString());
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ResponeThrowable(e, ERROR.HTTP_ERROR);
            switch (httpException.code()) {
                case UNAUTHORIZED:
                case FORBIDDEN:
                case NOT_FOUND:
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                default:
                    //ex.code = httpException.code();
                    if (null != baseView){
                        ex.message = baseView.resourceId2string(R.string.accout_error);
                    }else{
                        ex.message = "连接错误";
                    }
                    break;
            }
            return ex;
        } else if (e instanceof ServerException) {
            ServerException resultException = (ServerException) e;
            ex = new ResponeThrowable(resultException, resultException.code);
            ex.message = resultException.message;
            return ex;
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
            /*|| e instanceof ParseException*/) {
            ex = new ResponeThrowable(e, ERROR.PARSE_ERROR);
            if (null != baseView){
                ex.message = baseView.resourceId2string(R.string.parse_error);
            }else{
                ex.message = "解析错误";
            }
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ResponeThrowable(e, ERROR.NETWORK_ERROR);
            if (null != baseView){
                ex.message = baseView.resourceId2string(R.string.link_fail);
            }else{
                ex.message = "连接失败";
            }
            return ex;
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ResponeThrowable(e, ERROR.SSL_ERROR);
            if (null != baseView){
                ex.message = baseView.resourceId2string(R.string.verify_error);
            }else{
                ex.message = "证书验证失败";
            }
            return ex;
        }else if (e instanceof ApiException){
            if (e.getMessage().contains("Token")){
                ex = new ResponeThrowable(e, ERROR.TOKEN);
            }else if (e.getMessage().contains("网络未连接")){
                ex = new ResponeThrowable(e, ERROR.NO_NETWORK);
            }else{
                ex = new ResponeThrowable(e, ERROR.UNKNOWN);
            }
            ex.message = e.getMessage();
            return ex;
        }else if (e instanceof SocketTimeoutException){
            ex = new ResponeThrowable(e, ERROR.SOCKET_TIMEOUT_ERROR);
            if (null != baseView){
                ex.message = baseView.resourceId2string(R.string.time_out);
            }else{
                ex.message = "请求超时";
            }
            return ex;
        }
        else if (e instanceof UnknownHostException){
            ex = new ResponeThrowable(e, ERROR.SOCKET_TIMEOUT_ERROR);
            if (null != baseView){
                ex.message = baseView.resourceId2string(R.string.cannot_link);
            }else{
                ex.message = "无法连接服务器";
            }
            return ex;
        }
        else if (e instanceof NullPointerException){
            ex = new ResponeThrowable(e, ERROR.PARMA_ERROR);
            if (null != baseView){
                ex.message = baseView.resourceId2string(R.string.params_error);
            }else{
                ex.message = "参数错误";
            }
            return ex;
        }
        else {
            ex = new ResponeThrowable(e, ERROR.UNKNOWN);
            ex.message = e.getMessage();
            return ex;
        }
    }


    /**
     * 约定异常
     */
    public class ERROR {
        /**
         * 网络错误
         */
        public static final int NO_NETWORK = 999;
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 1001;
        /**
         * 网络错误
         */
        public static final int NETWORK_ERROR = 1002;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = 1003;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = 1005;

        /**
         * Socket超时
         */
        public static final int SOCKET_TIMEOUT_ERROR = 10060;

        /**
         * TOKEN过期
         */
        public static final int TOKEN = 110110;

        /**
         * NULL_PARAM错误
         */
        public static final int PARMA_ERROR = 110120;
    }

    public static class ResponeThrowable extends Exception {
        public int code;
        public String message;

        public ResponeThrowable(Throwable throwable, int code) {
            super(throwable);
            this.code = code;
        }
    }

    /**
     * ServerException发生后，将自动转换为ResponeThrowable返回
     */
    class ServerException extends RuntimeException {
        int code;
        String message;
    }
}
