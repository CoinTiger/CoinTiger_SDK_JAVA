package exchange;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


public class HttpClientUtils {
    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HttpClientUtils.class.getName());
    private final int DEFAULT_TIMEOUT = 30000;

    private static HttpClientUtils ins;

    private HttpClient client;
    private int timeout = DEFAULT_TIMEOUT;

    private static int maxConnTotal = 200;   //最大不要超过1000
    private static int maxConnPerRoute = 100;//实际的单个连接池大小，

    private HttpClientUtils() {
        if (client == null) {
            client = HttpClients.createDefault();
        }
    }

    public static HttpClientUtils getInstance() {
        if (ins == null) {
            synchronized (HttpClientUtils.class) {
                if (ins == null) {
                    ins = new HttpClientUtils();
                }
            }
        }
        return ins;
    }

    public String doGet(String uri) {
        String json = null;
        log.debug("========= Call [{}] Start ==========", uri);
        HttpResponse response = null;
        try {
            HttpGet request = new HttpGet(uri);
            request.setHeader("Language", "zh_CN");
            RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(timeout)
                    .setConnectTimeout(timeout).setSocketTimeout(timeout).build();

            request.setConfig(config);
            client = HttpClients.custom().setDefaultRequestConfig(config)
                    .setMaxConnTotal(maxConnTotal)
                    .setMaxConnPerRoute(maxConnPerRoute).build();
            response = client.execute(request);
            log.debug("Response status code: {}", response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                json = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                log.debug("Payload : {}", json);
            }
        } catch (Exception e) {
            log.error("HttpClient has exception! message: {}", e.getMessage(), e);
            return null;
        } finally {
            try {
                if (response != null) {
                    EntityUtils.consume(response.getEntity());
                }
            } catch (IOException e) {
                log.error("HttpClient has exception! message: {}", e.getMessage(), e);
            }
        }
        log.debug("========= Call [{}] End ==========", uri);
        return json;
    }
    
    public String doDelete(String uri) {
        String json = null;
        log.debug("========= Call [{}] Start ==========", uri);
        HttpResponse response = null;
        try {
            HttpDelete request = new HttpDelete(uri);
            request.setHeader("Language", "zh_CN");
            RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(timeout)
                    .setConnectTimeout(timeout).setSocketTimeout(timeout).build();

            request.setConfig(config);
            client = HttpClients.custom().setDefaultRequestConfig(config)
                    .setMaxConnTotal(maxConnTotal)
                    .setMaxConnPerRoute(maxConnPerRoute).build();
            response = client.execute(request);
            log.debug("Response status code: {}", response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                json = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                log.debug("Payload : {}", json);
            }
        } catch (Exception e) {
            log.error("HttpClient has exception! message: {}", e.getMessage(), e);
            return null;
        } finally {
            try {
                if (response != null) {
                    EntityUtils.consume(response.getEntity());
                }
            } catch (IOException e) {
                log.error("HttpClient has exception! message: {}", e.getMessage(), e);
            }
        }
        log.debug("========= Call [{}] End ==========", uri);
        return json;
    }

    public String doPost(String url, String jsonStr) {
        log.debug("========= Call [{}] Start ==========", url);
        URL u = null;
        HttpURLConnection con = null;

        try {
            u = new URL(url);
            con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            //application/x-www-form-urlencoded
            //con.setRequestProperty("content-type", "*/*");
            con.setConnectTimeout(timeout);
            con.setReadTimeout(timeout);
            //con.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            if (jsonStr != null && !"".equals(jsonStr)) {
                OutputStreamWriter osw = new OutputStreamWriter(
                        con.getOutputStream(), "UTF-8");
                log.debug("即将发送参数:{}", jsonStr);
                osw.write(jsonStr);
                osw.flush();
                osw.close();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), new IllegalStateException(e));
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        // 读取返回内容
        StringBuffer buffer = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(
                    con.getInputStream(), "UTF-8"));
            String temp = "";
            while ((temp = br.readLine()) != null) {
                buffer.append(temp);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), new IllegalStateException(e));
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String result = buffer.toString();
        log.debug("Payload: {}", result);
        log.debug("========= Call [{}] End ==========", url);
        return result;
    }

    /**
     * @return the timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String doPost(String url, String jsonStr, int timeout) {
        setTimeout(timeout);
        return doPost(url, jsonStr);
    }

    public String doGetCustom(String url, int timeout) {
        setTimeout(timeout);
        return doGet(url);
    }

    public String doAPIPost(String uri, Map<String, String> paramMap) {
        String json = null;
        log.debug("========= Call [{}] Start ==========", uri);
        HttpResponse response = null;
        try {
            HttpPost request = new HttpPost(uri);
            request.setHeader("Language", "zh_CN");
            RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(timeout)
                    .setConnectTimeout(timeout).setSocketTimeout(timeout).build();
            request.setConfig(config);
            List<NameValuePair> params = new ArrayList<NameValuePair>(0);
            if (paramMap != null && !paramMap.isEmpty()) {
                for (String key : paramMap.keySet()) {
                    params.add(new BasicNameValuePair(key, paramMap.get(key)));
                }
                request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            }
            response = client.execute(request);
            log.debug("Response status code: {}", response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                json = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                log.debug("Payload : {}", json);
            }
            request.releaseConnection();
        } catch (Exception e) {
            log.error("HttpClient has exception! message: {}", e.getMessage(), e);
            return null;
        } finally {
            try {
                if (response != null) {
                    EntityUtils.consume(response.getEntity());
                }
            } catch (IOException e) {
                log.error("HttpClient has exception! message: {}", e.getMessage(), e);
            }
        }
        log.debug("========= Call [{}] End ==========", uri);
        return json;
    }
}
