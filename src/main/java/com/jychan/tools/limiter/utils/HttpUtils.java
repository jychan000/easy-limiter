package com.jychan.tools.limiter.utils;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

import static org.apache.http.client.config.RequestConfig.custom;

/**
 * Created by chenjinying on 2017/8/3.
 * mail: 415683089@qq.com
 */
public class HttpUtils {

    private static Logger logger = Logger.getLogger(HttpUtils.class);

    private static final int CONNECTION_TIMEOUT = 3000;
    private static final int SOCKET_TIMEOUT = 3000;

    public static String doGet(String url) throws IOException {
        String json = null;
        InputStream is = null;
        CloseableHttpResponse httpResponse = null;

        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(custom().setConnectTimeout(CONNECTION_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build());
        CloseableHttpClient httpClient = HttpClientBuilder.create().setMaxConnTotal(200).setMaxConnPerRoute(20).build();

        try {
            httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();

            is = httpEntity.getContent();
            json = IOUtils.toString(is, "UTF-8");

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
            if (is != null) {
                is.close();
            }
        }
        return json;
    }

    /**
     * 获取ip地址
     */
    public static String getIpAddress(HttpServletRequest request) throws Exception {
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("x-forwarded-for");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
