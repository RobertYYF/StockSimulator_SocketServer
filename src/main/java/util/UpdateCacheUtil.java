package util;

import model.MarketInfo;
import model.Stock;

import java.io.IOException;
import java.util.Map;

import static util.ScrapUtil.getIndexInfo;
import static util.ScrapUtil.getTotalData;

public class UpdateCacheUtil {

    public static void updateStockData() {
        try {
            Map<String, Stock> map = getTotalData();
            map.keySet().stream().forEach(e -> {
                MarketInfo.setStocksCache(e, map.get(e));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateCompositeIndex() {
        try {
            Stock stock = getIndexInfo();
            MarketInfo.setCompositeIndexCache(stock);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
