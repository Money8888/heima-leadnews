package com.heima.crawler.utils;

import com.alibaba.fastjson.JSON;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtils {

    private HttpClientUtils() {
    }

    /**
     * 连接超时时间
     */
    public static final int CONNECTION_TIMEOUT_MS = 5000;

    /**
     * 读取数据超时时间
     */
    public static final int SO_TIMEOUT_MS = 5000;


    public static final String utf8 = "UTF-8";

    public static final String application_json = "application/json";

    public static final String gbk = "GBK";

    /**
     * 简单get调用
     *
     * @param url
     * @param params
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    public static String get(String url, Map<String, String> params)
            throws IOException, URISyntaxException {
        return get(url, params, utf8);
    }

    /**
     * 简单get调用
     *
     * @param url
     * @param params
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    public static String get(String url, Map<String, String> params, String charset)
            throws IOException, URISyntaxException {
        HttpClient client = buildHttpClient(true);
        HttpGet get = buildHttpGet(url, params, charset);
        HttpResponse response = client.execute(get);
        assertStatus(response);

        HttpEntity entity = response.getEntity();
        if (entity != null) {
            return EntityUtils.toString(entity, charset);
        }
        return null;
    }


    public static String get(String url, Map<String, String> params, Map<String, String> headerMap, CookieStore cookieStore, HttpHost proxy, String charset)
            throws IOException, URISyntaxException {
        HttpClient client = buildHttpClient(true, cookieStore, proxy);
        HttpGet get = buildHttpGet(url, params, charset);
        seHttpHeader(get, headerMap);
        HttpResponse response = client.execute(get);
        assertStatus(response);

        HttpEntity entity = response.getEntity();
        if (entity != null) {
            return EntityUtils.toString(entity, charset);
        }
        return null;
    }

    /**
     * 简单post调用
     *
     * @param url
     * @param params
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String post(String url, Map<String, String> params)
            throws URISyntaxException, IOException {
        return post(url, params, utf8);
    }

    public static String postJSON(String url, Map<String, String> params) throws IOException, URISyntaxException {
        return postJSON(url, params, utf8);
    }

    /**
     * 简单post调用
     *
     * @param url
     * @param params
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String post(String url, Map<String, String> params, String charset)
            throws URISyntaxException, IOException {

        HttpClient client = buildHttpClient(true);
        HttpPost postMethod = buildHttpPost(url, params, charset);
        HttpResponse response = client.execute(postMethod);
        assertStatus(response);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            return EntityUtils.toString(entity, charset);
        }

        return null;
    }

    public static String postJSON(String url, Map params, String charset)
            throws URISyntaxException, IOException {

        HttpClient client = buildHttpClient(true);
        HttpPost postMethod = buildHttpJSONPost(url, params, charset);
        HttpResponse response = client.execute(postMethod);
        assertStatus(response);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            return EntityUtils.toString(entity, charset);
        }

        return null;
    }

    /**
     * 创建HttpClient
     *
     * @param isMultiThread
     * @return
     */
    public static HttpClient buildHttpClient(boolean isMultiThread) {
        CloseableHttpClient client;
        if (isMultiThread)
            client = HttpClientBuilder
                    .create().setDefaultRequestConfig(buildRequestConfig(null))
                    .setRetryHandler(new DefaultHttpRequestRetryHandler())
                    .setConnectionManager(
                            new PoolingHttpClientConnectionManager()).build();
        else {
            client = HttpClientBuilder.create().setDefaultRequestConfig(buildRequestConfig(null)).build();
        }
        return client;
    }


    /**
     * 创建HttpClient
     *
     * @param isMultiThread
     * @return
     */
    public static HttpClient buildHttpClient(boolean isMultiThread, CookieStore cookieStore, HttpHost proxy) {
        CloseableHttpClient client;
        if (isMultiThread)
            client = HttpClientBuilder
                    .create().setDefaultCookieStore(cookieStore).setDefaultRequestConfig(buildRequestConfig(proxy))
                    .setRetryHandler(new DefaultHttpRequestRetryHandler())
                    .setConnectionManager(
                            new PoolingHttpClientConnectionManager()).build();
        else {
            client = HttpClientBuilder.create().setDefaultRequestConfig(buildRequestConfig(proxy)).setDefaultCookieStore(cookieStore).build();
        }
        return client;
    }

    /**
     * 构建httpPost对象
     *
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     * @throws URISyntaxException
     */
    public static HttpPost buildHttpPost(String url, Map<String, String> params, String charset)
            throws UnsupportedEncodingException, URISyntaxException {

        HttpPost post = new HttpPost(url);
        setCommonHttpMethod(post);
        if (params != null) {
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            HttpEntity he = new UrlEncodedFormEntity(formparams, charset);
            post.setEntity(he);
        }
        return post;
    }

    public static HttpPost buildHttpJSONPost(String url, Map<String, String> params, String charset)
            throws UnsupportedEncodingException, URISyntaxException {

        HttpPost post = new HttpPost(url);
        setJSONHttpMethod(post);
        if (params != null) {
            String json = JSON.toJSONString(params);
            System.out.println(json);
            StringEntity stringEntity = new StringEntity(json, utf8);
            stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, application_json));
            post.setEntity(stringEntity);
        }
        return post;
    }


    /**
     * 构建httpGet对象
     *
     * @param url
     * @return
     * @throws URISyntaxException
     */
    public static HttpGet buildHttpGet(String url, Map<String, String> params, String chatset) {

        return new HttpGet(buildGetUrl(url, params, chatset));
    }

    /**
     * build getUrl str
     *
     * @param url
     * @param params
     * @return
     */
    public static String buildGetUrl(String url, Map<String, String> params, String charset) {
        StringBuilder uriStr = new StringBuilder(url);
        if (params != null && !params.isEmpty()) {
            List<NameValuePair> ps = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                ps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            uriStr.append("?");
            uriStr.append(URLEncodedUtils.format(ps, charset));
        }
        return uriStr.toString();
    }

    /**
     * 设置HttpMethod通用配置
     *
     * @param httpMethod
     */
    public static void setCommonHttpMethod(HttpRequestBase httpMethod) {
        httpMethod.setHeader(HTTP.CONTENT_ENCODING, utf8);// setting
    }

    /* 设置HttpMethod通用配置
     *
     * @param httpMethod
     */
    public static void setJSONHttpMethod(HttpRequestBase httpMethod) {
        httpMethod.setHeader(HTTP.CONTENT_ENCODING, utf8);// setting
        httpMethod.setHeader(HTTP.CONTENT_TYPE, application_json);// setting
    }

    /**
     * 设置成消息体的长度 setting MessageBody length
     *
     * @param httpMethod
     * @param he
     */
    public static void setContentLength(HttpRequestBase httpMethod, HttpEntity he) {
        if (he == null) {
            return;
        }
        httpMethod.setHeader(HTTP.CONTENT_LEN, String.valueOf(he.getContentLength()));
    }

    /**
     * 构建公用RequestConfig
     *
     * @return
     */
    public static RequestConfig buildRequestConfig(HttpHost proxy) {
        // 设置请求和传输超时时间
        return RequestConfig.custom().setProxy(proxy)
                .setSocketTimeout(SO_TIMEOUT_MS)
                .setConnectTimeout(CONNECTION_TIMEOUT_MS).build();
    }


    /**
     * 强验证必须是200状态否则报异常
     *
     * @param res
     * @throws HttpException
     */
    static void assertStatus(HttpResponse res) throws IOException {

        switch (res.getStatusLine().getStatusCode()) {
            case HttpStatus.SC_OK:
                break;
            default:
                throw new IOException("服务器响应状态异常,失败.");
        }
    }


    public static CookieStore getCookieStore(Map<String, String> cookieMap) {
        BasicCookieStore cookieStore = null;
        //Cookie 处理
        if (null != cookieMap && !cookieMap.isEmpty()) {
            cookieStore = new BasicCookieStore();
            for (Map.Entry<String, String> entry : cookieMap.entrySet()) {
                cookieStore.addCookie(new BasicClientCookie(entry.getKey(), entry.getValue()));
            }
        }
        return cookieStore;
    }


    public static void seHttpHeader(HttpMessage httpMessage, Map<String, String> headerMap) {
        //Cookie 处理
        if (null != headerMap && !headerMap.isEmpty()) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                httpMessage.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

}
