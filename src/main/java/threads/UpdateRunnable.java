package threads;

import util.UpdateCacheUtil;

import java.io.IOException;

public class UpdateRunnable implements Runnable{
    @Override
    public void run() {
        while (true) {
            // Update all cache data
            UpdateCacheUtil.updateStockData();
            UpdateCacheUtil.updateCompositeIndex();

            // Pause for 10s
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
