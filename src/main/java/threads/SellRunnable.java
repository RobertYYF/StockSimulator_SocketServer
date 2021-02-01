package threads;

import model.PendingItem;
import model.Stock;
import util.DbConnectUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static util.ScrapUtil.getSingleData;

public class SellRunnable implements Runnable{
    DbConnectUtil dbConnect = new DbConnectUtil();

    @Override
    public void run() {

        dbConnect.connect();

        while (true) {

            List<PendingItem> sellPendingList = dbConnect.getSellPendingList();
            List<PendingItem> toRemove = new ArrayList<>();


            if (!sellPendingList.isEmpty()) {

                System.out.println("Ahh! New sell pending coming ...");

                for (PendingItem each : sellPendingList) {

                    Stock stock = null;

                    try {
                        stock = getSingleData(each.getStockId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (each.getPrice() <= stock.getPrice()) {
                        dbConnect.changeTradeType(each.getTradeId().toString(), "SOLD");

                        // Update corresponding amount of stocks from InStock DB
                        dbConnect.sellInStock(each.getUsername(), each.getStockId(), each.getAmount(), each.getPrice());

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
