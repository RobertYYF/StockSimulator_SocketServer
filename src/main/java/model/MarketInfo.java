package model;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MarketInfo {

    private static Map<String, Stock> stocksCache = new ConcurrentHashMap<>();
    private static Stock compositeIndexCache = null;
    private static List<Classification> classificationsCache = new CopyOnWriteArrayList<>();

    public static void setStocksCache(String key, Stock stock) {
        stocksCache.put(key, stock);
    }

    public static Map<String, Stock> getStocksCache() {
        return stocksCache;
    }

    public static Stock getStockCache(String key) {
        return stocksCache.get(key);
    }

    public static void removeStockCache(String key) {
        stocksCache.remove(key);
    }

    public static void setCompositeIndexCache(Stock stock) {
        compositeIndexCache = stock;
    }

    public static Stock getCompositeIndexCache() {
        return compositeIndexCache;
    }

    public static void setClassificationsCache(Classification classification) {
        classificationsCache.add(classification);
    }

    public static List<Classification> getClassificationsCache() {
        return classificationsCache;
    }
}
