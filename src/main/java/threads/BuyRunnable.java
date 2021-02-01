package threads;

import model.PendingItem;
import model.Stock;
import util.DbConnectUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static util.ScrapUtil.getSingleData;

public class BuyRunnable implements Runnable{

    DbConnectUtil dbConnect = new DbConnectUtil();

    @Override
    public void run() {

        dbConnect.connect();

        while (true) {

            List<PendingItem> buyPendingList = dbConnect.getBuyPendingList();
            List<PendingItem> toRemove = new ArrayList<>();

            if (!buyPendingList.isEmpty()) {

                System.out.println("Ahh! New buy pending coming ...");

                for (PendingItem each : buyPendingList) {

                    Stock stock = null;

                    try {
                        stock = getSingleData(each.getStockId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (each.getPrice() >= stock.getPrice()) {
                        dbConnect.changeTradeType(each.getTradeId().toString(), "BOUGHT");
                        List<String[]> inStockList = dbConnect.getInStock(each.getUsername());
                        boolean flag = true;

                        // 加仓操作
                        for (String[] i : inStockList) {
                            String stock_id = i[0];
                            String stock_name = i[1];
                            Integer amount = Integer.parseInt(i[2]);
                            Float price = Float.parseFloat(i[3]);
                            if (stock_id.equals(each.getStockId())) {
                                dbConnect.buyMoreStock(each.getUsername(), each.getStockId(), each.getAmount(), each.getPrice());
                                flag = false;
                                break;
                            }
                        }

                        // 建仓操作
                        if (flag) {
                            System.out.println("Add new stock to in stock list");
                            dbConnect.buyNewStock(each.getUsername(), each.getStockId(), each.getStockName(), each.getAmount(), each.getPrice());
                        }

                        toRemove.add(each);
                    }
                }
            }

            // wait for 5s to loop again
            try {
                Thread.sleep(9000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
