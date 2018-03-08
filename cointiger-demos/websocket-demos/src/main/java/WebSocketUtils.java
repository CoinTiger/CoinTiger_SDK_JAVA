import com.alibaba.fastjson.JSONObject;
import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

public class WebSocketUtils extends WebSocketClient {

    private static String wss_url = "wss://ws.cointiger.com/exchange-market/ws";

    private static WebSocketUtils client = null;

    public WebSocketUtils(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public WebSocketUtils(URI serverURI) {
        super(serverURI);
    }

    public WebSocketUtils(URI serverUri, Map<String, String> headers, int connecttimeout) {
        super(serverUri, new Draft_17(), headers, connecttimeout);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("开流--opened connection");
    }

    @Override
    public void onMessage(ByteBuffer socketBuffer) {
        try {
            String marketStr = CommonUtils.byteBufferToString(socketBuffer);
            String market = CommonUtils.uncompress(marketStr);
            if (market.contains("ping")) {
                System.out.println("发送心跳：" + market.replace("ping", "pong"));
                // Client 心跳
                client.send(market.replace("ping", "pong"));
            } else {
                System.out.println("返回的数据：" + market);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(String message) {
        System.out.println("接收的消息：" + message);
    }

    public void onFragment(Framedata fragment) {
        System.out.println("消息矁：" + new String(fragment.getPayloadData().array()));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("连接关闭：" + (remote ? "remote peer" : "us"));
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("WebSocket 连接异常： " + ex);
    }

    public static Map<String, String> getWebSocketHeaders() throws IOException {
        Map<String, String> headers = new HashMap<String, String>();
        return headers;
    }

    private static void trustAllHosts(WebSocketUtils appClient) {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            appClient.setWebSocketFactory(new DefaultSSLWebSocketClientFactory(sc));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reqKline(String symbol, String type, String event) throws Exception {

        client = new WebSocketUtils(new URI(wss_url), getWebSocketHeaders(), 1000);
        trustAllHosts(client);

        client.connectBlocking();
        // 订阅-K线行情
        SubscribeModel subscribeModel = new SubscribeModel();
        subscribeModel.setEvent(event);

        SubscribeModel.ChannelObject params = new SubscribeModel.ChannelObject();
        params.setChannel("market_" + symbol + "_kline_" + type);
        params.setCb_id("etcbtc");
        subscribeModel.setParams(params);
        System.out.println("请求数据：" + JSONObject.toJSONString(subscribeModel));
        client.send(JSONObject.toJSONString(subscribeModel));
    }

    public static void reqHq(String symbol) throws Exception {
        client = new WebSocketUtils(new URI(wss_url), getWebSocketHeaders(), 1000);
        trustAllHosts(client);
        client.connectBlocking();
        // 订阅-24小时行情
        SubscribeModel subscribeModel = new SubscribeModel();
        subscribeModel.setEvent("sub");

        SubscribeModel.ChannelObject params = new SubscribeModel.ChannelObject();
        params.setChannel("market_" + symbol + "_ticker");
        params.setCb_id("etcbtc");
        subscribeModel.setParams(params);
        System.out.println("请求数据：" + JSONObject.toJSONString(subscribeModel));
        client.send(JSONObject.toJSONString(subscribeModel));
    }

    public static void reqTrade(String symbol) throws Exception {
        client = new WebSocketUtils(new URI(wss_url), getWebSocketHeaders(), 1000);
        trustAllHosts(client);
        client.connectBlocking();
        // 订阅-24小时行情
        SubscribeModel subscribeModel = new SubscribeModel();
        subscribeModel.setEvent("sub");

        SubscribeModel.ChannelObject params = new SubscribeModel.ChannelObject();
        params.setChannel("market_" + symbol + "_trade_ticker");
        params.setCb_id("etcbtc");
        subscribeModel.setParams(params);
        System.out.println("请求数据：" + JSONObject.toJSONString(subscribeModel));
        client.send(JSONObject.toJSONString(subscribeModel));
    }

    // 其它接口类似
    // 1. 实时成交信息
    // 2. 深度盘口
    // 3. K线历史数据
    // 4. 成交历史数据
}
