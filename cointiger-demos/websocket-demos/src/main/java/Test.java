
public class Test {

    public static void main(String[] args) {
        /**
         *
         * 参数说明
         *   1、币对code  格式如 ethbtc、ltcbtc、ethbitcny
         *   2、1min 一分钟K线
         *      5min 五分钟K线
         *      5min
                15min
                30min
                60min
                1day 日K
                1week 周K
                1month月K
             3、req 历史 sub 最新
         */

        try {
            //历史k线，不包括最新一根k线
            WebSocketUtils.reqKline("ethbtc", "1min", "req");
            //最新一根K线
            WebSocketUtils.reqKline("ethbtc", "1min", "sub");
            //最新价
            WebSocketUtils.reqHq("ethbtc");
            //最新成交记录
            WebSocketUtils.reqTrade("ethbtc");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}