import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yuboliu
 * @Description: 交易行情接口调用测试
 * @date 2018/3/2616:12
 */
public class TestTradeRestApi {
    private static final Logger log = LoggerFactory.getLogger(TestTradeRestApi.class);

    private static final String ROOT_API= "https://www.cointiger.com/api/market";

    //修改为你的api_key
    private static final String API_KEY_VALUE = "100310001";

    //修改为你要做市的币对 格式如 tchbtc,ltcbtc,ethbitcny
    private static final String SYMBOL = "btcbitcny";

    //以下几项不需要修改
    private static final String API_KEY = "api_key";

    /**
     * K线历史数据
     *
     * 返回数据格式
     *{"code":"0","msg":"suc","data":{"ch":"market.btcbitcny_kline.1day","kline_data":
     * [{"amount":156529.8957323,"vol":3.1582,"high":49903.51,"low":49262.91639061,"id":1521734400,"close":49375.30927674,"open":49701.38}]}}
     */
    @Test
    public void testGetHistoryKline(){

        StringBuilder stringBuilder = new StringBuilder(ROOT_API);
        stringBuilder.append("/history/kline?")
                .append(API_KEY).append("=").append(API_KEY_VALUE).append("&")
                .append("symbol=").append(SYMBOL).append("&")
                .append("period=").append("1day").append("&")
                .append("size=").append(10);

        HttpClientUtils httpClientUtils = HttpClientUtils.getInstance();
        String result = httpClientUtils.doGet(stringBuilder.toString());
        System.out.println(result);
        JSONObject jsonObject = JSON.parseObject(result);
        if (Integer.parseInt(jsonObject.getString("code")) > 0) {
            System.out.println("error:" + result);
        }
        jsonObject = jsonObject.getJSONObject("data");
        System.out.println(jsonObject.toJSONString());
    }

    /**
     * 成交历史数据
     *
     * 返回数据格式
     * {"code":"0","msg":"suc","data":{"trade_data":[{"id":460760,"side":"SELL","price":49375.30927674,"vol":0.0009,
     * "amount":44.43777834,"ts":1521747240000,"ds":"2018-03-23 03:34:00"}],"size":1,"ch":"market.btcbitcny_trade.detail"}}
     */
    @Test
    public void testGetHistoryTrade(){
        StringBuilder stringBuilder = new StringBuilder(ROOT_API);
        stringBuilder.append("/history/trade?")
                .append(API_KEY).append("=").append(API_KEY_VALUE).append("&")
                .append("symbol=").append(SYMBOL).append("&")
                .append("size=").append(2);

        HttpClientUtils httpClientUtils = HttpClientUtils.getInstance();
        String result = httpClientUtils.doGet(stringBuilder.toString());
        System.out.println(result);
        JSONObject jsonObject = JSON.parseObject(result);
        if (Integer.parseInt(jsonObject.getString("code")) > 0) {
            System.out.println("error:" + result);
        }
        jsonObject = jsonObject.getJSONObject("data");
        System.out.println(jsonObject.toJSONString());
    }

    /**
     * 深度盘口
     *
     * 返回数据格式
     *{"code":"0","msg":"suc","data":{"ch":"market.btcbitcny_depth.step0","depth_data":
     * {"tick":{"buys":[["49896.81",0.0441],["49936.23",0.0021]],"asks":[["49896.81",0.0441],["49936.23",0.0021]]},"ts":1521860775784}}}
     */
    @Test
    public void testGetDepthData(){
        StringBuilder stringBuilder = new StringBuilder(ROOT_API);
        stringBuilder.append("/depth?")
                .append(API_KEY).append("=").append(API_KEY_VALUE).append("&")
                .append("symbol=").append(SYMBOL).append("&")
                .append("type=").append("step0");

        HttpClientUtils httpClientUtils = HttpClientUtils.getInstance();
        String result = httpClientUtils.doGet(stringBuilder.toString());
        System.out.println(result);
        JSONObject jsonObject = JSON.parseObject(result);
        if (Integer.parseInt(jsonObject.getString("code")) > 0) {
            System.out.println("error:" + result);
        }
        jsonObject = jsonObject.getJSONObject("data");
        System.out.println(jsonObject.toJSONString());
    }

    /**
     * 前24小时行情
     *
     * 返回数据格式
     * {"code":"0","msg":"suc","data":{"ch":"market.btcbitcny.ticker","trade_ticker_data":
     * {"tick":{"amount":7418370.49115073,"vol":148.5636,"high":52494.09797291,"low":48645.1,"rose":-0.00656059,"close":49375.30927674,"open":51196.00406763},"ts":1521747255985}}}
     */
    @Test
    public void testGet24FrontTradeData(){
        StringBuilder stringBuilder = new StringBuilder(ROOT_API);
        stringBuilder.append("/detail?")
                .append(API_KEY).append("=").append(API_KEY_VALUE).append("&")
                .append("symbol=").append(SYMBOL);

        HttpClientUtils httpClientUtils = HttpClientUtils.getInstance();
        String result = httpClientUtils.doGet(stringBuilder.toString());
        System.out.println(result);
        JSONObject jsonObject = JSON.parseObject(result);
        if (Integer.parseInt(jsonObject.getString("code")) > 0) {
            System.out.println("error:" + result);
        }
        jsonObject = jsonObject.getJSONObject("data");
        System.out.println(jsonObject.toJSONString());
    }
}