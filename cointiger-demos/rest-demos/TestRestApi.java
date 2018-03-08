package exchange;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * cointiger api 示例
 *
 * 只要把参数配置完成即可test
 */
public class TestRestApi {

    private static final Logger log = LoggerFactory.getLogger(TestRestApi.class);

    //修改为你的api_key
    private static final String API_KEY_VALUE = "100140001";
    //修改为你的api_secret
    private static final String API_SECRET = "OWE2ZjJiNzI3YzllNjE3ZTlhZjBiNTc1MzhiYWMwOWEwMGZjMTRiMzA3ZjI5Y2QxYjk1MDExODQwMDdkZDdhZQ==";
    //修改为你在交易所中的设置的资金密码
    private static final String CAPITAL_PASSWORD_VALUE = "Zq7148632";
    //修改为你要做市的币对 格式如 tchbtc,ltcbtc,ethbitcny
    private static final String SYMBOL = "ocnbtc";

    //以下几项不需要修改
    private static final String ROOT_API = "https://www.cointiger.com/exchange/trading/api";
    private static final String CAPITAL_PASSWORD = "capital_password";
    private static final String API_KEY = "api_key";

    /**
     * 下单接口
     * 返回数据格式
     * {"code":"0","msg":"suc","data":{"order_id":918}} order_id为订单号
     */
    @Test
    public void testOrderCreate() {
        String time = System.currentTimeMillis() + "";
        Map<String, String> paramMap = new HashMap<>();
        //币对code ，格式如 tchbtc,ltcbtc,ethbitcny
        paramMap.put("symbol", SYMBOL);
        //下单价格
        paramMap.put("price", "0.00000130");
        //下单数量
        paramMap.put("volume", "10");
        //买卖方向 买BUY  卖SELL
        paramMap.put("side", "BUY");
        paramMap.put(CAPITAL_PASSWORD, CAPITAL_PASSWORD_VALUE);
        //限价交易为1
        paramMap.put("type", "1");
        paramMap.put("time", time);
        String sign = getSign(paramMap);
        StringBuilder stringBuilder = new StringBuilder(ROOT_API);
        stringBuilder.append("/order?")
                .append(API_KEY).append("=").append(API_KEY_VALUE).append("&")
                .append("time=").append(time).append("&")
                .append("sign=").append(sign);

        HttpClientUtils httpClientUtils = HttpClientUtils.getInstance();
        System.out.println(stringBuilder.toString());
        String result = httpClientUtils.doAPIPost(stringBuilder.toString(), paramMap);
        System.out.println(result);
        JSONObject jsonObject = JSON.parseObject(result);
        if (Integer.parseInt(jsonObject.getString("code")) > 0) {
            System.out.println("error:" + result);
        }
        jsonObject = jsonObject.getJSONObject("data");
        System.out.println("order_id=" + jsonObject.getLong("order_id"));

    }

    /**
     * 撤单接口
     * 返回数据：{"code":"0","msg":"suc","data":null}
     */
    @Test
    public void testCancel() {
        String time = System.currentTimeMillis() + "";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("symbol", SYMBOL);
        //order_id订单号，下单接口成功时会返回，也可以通过查询订单的接口获得
        String order_id = "918";
        paramMap.put("order_id", order_id);
        paramMap.put("time", time);
        String sign = getSign(paramMap);
        StringBuilder stringBuilder = new StringBuilder(ROOT_API);
        stringBuilder.append("/order?")
                .append(API_KEY).append("=").append(API_KEY_VALUE).append("&")
                .append("order_id=").append(order_id).append("&")
                .append("symbol=").append(SYMBOL).append("&")
                .append("time=").append(time).append("&")
                .append("sign=").append(sign);

        HttpClientUtils httpClientUtils = HttpClientUtils.getInstance();
        String result = httpClientUtils.doDelete(stringBuilder.toString());
        System.out.println(result);
        JSONObject jsonObject = JSON.parseObject(result);
        if (Integer.parseInt(jsonObject.getString("code")) > 0) {
            System.out.println("error:" + result);
        }
        jsonObject = jsonObject.getJSONObject("data");
    }

    /**
     * 查询当前委托(未成交、部分成交的订单)
     * 返回数据
     * {"code":"0","msg":"suc","data":{"offset":0,"limit":10,"count":1,"list":[{"volume":{"amount":"10.000","icon":"","title":"委托数量"},"side":"BUY","price":{"amount":"0.00000130","icon":"","title":"委托价格"},"created_at":1520497361511,"id":918,"label":{"title":"撤单","click":1},"remain_volume":{"amount":"10.00000000","icon":"","title":"尚未成交"},"side_msg":"买入"}]}}
     */
    @Test
    public void testOrderNew() {
        String time = System.currentTimeMillis() + "";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("symbol", SYMBOL);
        //分页逻辑 offset为第几页从1开始 limit为每页显示几条数据
        paramMap.put("offset", "1");
        paramMap.put("limit", "10");
        paramMap.put("time", time);
        String sign = getSign(paramMap);
        StringBuilder stringBuilder = new StringBuilder(ROOT_API);
        stringBuilder.append("/order/new?")
                .append(API_KEY).append("=").append(API_KEY_VALUE).append("&")
                .append("sign=").append(sign).append("&")
                .append("offset=").append("1").append("&")
                .append("limit=").append("10").append("&")
                .append("symbol=").append(SYMBOL).append("&")
                .append("time=").append(time);
        HttpClientUtils httpClientUtils = HttpClientUtils.getInstance();
        System.out.println(stringBuilder.toString());
        String result = httpClientUtils.doGet(stringBuilder.toString());
        System.out.println(result);
        JSONObject jsonObject = JSON.parseObject(result);
        if (Integer.parseInt(jsonObject.getString("code")) > 0) {
            System.out.println("error:" + result);
        }
        jsonObject = jsonObject.getJSONObject("data");
    }

    /**
     * 获取成交记录（包括全部成交和部分成交的几率记录）
     * 返回数据
     * {"code":"0","msg":"suc","data":{"offset":0,"limit":10,"count":1,"list":[{"volume":{"amount":"1.000","icon":"","title":"成交量"},"price":{"amount":"0.00000150","icon":"","title":"委托价格"},"created_at":1520499171000,"deal_price":{"amount":0.00000150000000000000000000000000,"icon":"","title":"成交价格"},"id":1035}]}}
     */
    @Test
    public void testOrderTrade() {

        String time = System.currentTimeMillis() + "";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("symbol", SYMBOL);
        //分页逻辑 offset为第几页从1开始 limit为每页显示几条数据
        paramMap.put("offset", "1");
        paramMap.put("limit", "10");
        paramMap.put("time", time);
        String sign = getSign(paramMap);
        StringBuilder stringBuilder = new StringBuilder(ROOT_API);
        stringBuilder.append("/order/trade?")
                .append(API_KEY).append("=").append(API_KEY_VALUE).append("&")
                .append("offset=").append("1").append("&")
                .append("limit=").append("10").append("&")
                .append("symbol=").append(SYMBOL).append("&")
                .append("time=").append(time).append("&")
                .append("sign=").append(sign);

        HttpClientUtils httpClientUtils = HttpClientUtils.getInstance();
        String result = httpClientUtils.doGet(stringBuilder.toString());
        System.out.println(result);
        JSONObject jsonObject = JSON.parseObject(result);
        if (Integer.parseInt(jsonObject.getString("code")) > 0) {
            System.out.println("error:" + result);
        }
        jsonObject = jsonObject.getJSONObject("data");
    }

    /**
     * 获取下单记录（包括所有状态的订单）
     */
    @Test
    public void testOrderHistory() {
        String time = System.currentTimeMillis() + "";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("symbol", SYMBOL);
        //分页逻辑 offset为第几页从1开始 limit为每页显示几条数据
        paramMap.put("offset", "1");
        paramMap.put("limit", "16");
        paramMap.put("time", time);
        String sign = getSign(paramMap);
        StringBuilder stringBuilder = new StringBuilder(ROOT_API);
        stringBuilder.append("/order/history?")
                .append(API_KEY).append("=").append(API_KEY_VALUE).append("&")
                .append("sign=").append(sign).append("&")
                .append("offset=").append("1").append("&")
                .append("limit=").append("16").append("&")
                .append("symbol=").append(SYMBOL).append("&")
                .append("time=").append(time);
        HttpClientUtils httpClientUtils = HttpClientUtils.getInstance();
        System.out.println(stringBuilder.toString());
        String result = httpClientUtils.doGet(stringBuilder.toString());
        System.out.println(result);
        JSONObject jsonObject = JSON.parseObject(result);
        if (Integer.parseInt(jsonObject.getString("code")) > 0) {
            System.out.println("error:" + result);
        }
        jsonObject = jsonObject.getJSONObject("data");
    }

    /**
     * 获取账户资金详情
     * 返回数据
     * {"code":"0","msg":"suc","data":[{"normal":"0.00004349","lock":"0.00000000","coin":"btc"},{"normal":"0.00200000","lock":"0.00000000","coin":"eth"},{"normal":"0.00000000","lock":"0.00000000","coin":"ltc"},{"normal":"0.00000000","lock":"0.00000000","coin":"bch"},{"normal":"0.00000000","lock":"0.00000000","coin":"etc"},{"normal":"5.79079536","lock":"0.00000000","coin":"bitcny"},{"normal":"0.00000000","lock":"0.00000000","coin":"doge"},{"normal":"0.89000000","lock":"0.00000000","coin":"gto"},{"normal":"0.00000000","lock":"0.00000000","coin":"nem"},{"normal":"0.99500000","lock":"0.00000000","coin":"avh"},{"normal":"1.12783400","lock":"700000.00000000","coin":"tch"},{"normal":"0.00000000","lock":"0.00000000","coin":"eos"},{"normal":"0.00000000","lock":"0.00000000","coin":"soc"},{"normal":"0.00000000","lock":"0.00000000","coin":"aac"},{"normal":"0.00000000","lock":"0.00000000","coin":"bptn"},{"normal":"0.99900000","lock":"0.00000000","coin":"ocn"},{"normal":"0.00000000","lock":"0.00000000","coin":"mex"},{"normal":"0.00000000","lock":"0.00000000","coin":"afc"},{"normal":"0.00000000","lock":"0.00000000","coin":"rep"},{"normal":"0.00000000","lock":"0.00000000","coin":"elf"},{"normal":"0.00000000","lock":"0.00000000","coin":"say"}]}
     */
    @Test
    public void testBalance() {
        String time = System.currentTimeMillis() + "";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("time", time);
        String sign = getSign(paramMap);
        StringBuilder stringBuilder = new StringBuilder(ROOT_API);
        stringBuilder.append("/user/balance?")
                .append(API_KEY).append("=").append(API_KEY_VALUE).append("&")
                .append("time=").append(time).append("&").append("sign=").append(sign);
        HttpClientUtils httpClientUtils = HttpClientUtils.getInstance();
        System.out.println(stringBuilder.toString());
        String result = httpClientUtils.doGet(stringBuilder.toString());
        System.out.println(result);
        JSONObject jsonObject = JSON.parseObject(result);
        if (Integer.parseInt(jsonObject.getString("code")) > 0) {
            System.out.println("error:" + result);
            // return null;
        }
    }


    /**
     * 签名方法
     * @param params
     * @return
     */
    public static String getSign(Map<String, String> params) {
        Map<String, String> resultMap = new TreeMap<>(params);
        StringBuilder basestring = new StringBuilder();
        for (String key : resultMap.keySet()) {
            if (key.equals("sign")) {//去掉签名字段
                continue;
            }
            System.out.println(key + ":" + resultMap.get(key));
            basestring.append(key).append(resultMap.get(key));
        }
        basestring.append(API_SECRET);
        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(API_SECRET.getBytes(), "HmacSHA512"));
            String signature = new String(Hex.encodeHex(mac.doFinal(basestring.toString().getBytes())));
            System.out.println("========================================signature: " + signature);
            return signature;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error(e.getMessage(), e);
        }

        return "";
    }


}
