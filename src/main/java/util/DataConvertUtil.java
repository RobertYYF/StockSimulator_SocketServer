package util;

import model.MarketInfo;
import model.Stock;
import protoData.StockDataProto;

import java.util.Map;

public class DataConvertUtil {

    public static byte[] getData() {

        byte[] result = null;

        StockDataProto.AllStock.Builder bigBuilder = StockDataProto.AllStock.newBuilder();
        Map<String, Stock> stocks = MarketInfo.getStocksCache();
        stocks.values().forEach(e -> {
            bigBuilder.addStock(
                StockDataProto.SingleStock.newBuilder()
                        .setStockId(e.getCode())
                        .setStockName(e.getName())
                        .setStockPrice(e.getPrice())
                        .setStockChange(e.getPercent())
                        .build()
            );
        });

        StockDataProto.AllStock allStock = bigBuilder.build();

        result = allStock.toByteArray();

        return result;
    }

}
