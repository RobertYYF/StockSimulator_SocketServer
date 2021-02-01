package threads;

import model.Classification;
import model.MarketInfo;
import model.Stock;
import util.DataConvertUtil;
import util.DbConnectUtil;

import java.io.*;
import java.net.Socket;
import java.util.List;

import static util.DataConvertUtil.getData;

public class WorkerRunnable implements Runnable {

    protected Socket clientSocket = null;
    protected DbConnectUtil dbConnect = null;
    private PrintWriter out;
    private BufferedReader in;
    private OutputStream outputStream;
    private boolean flag;

    public WorkerRunnable(Socket clientSocket, DbConnectUtil dbConnect) {
        this.clientSocket = clientSocket;
        this.dbConnect = dbConnect;
    }

    @Override
    public void run() {

        try {
            outputStream = clientSocket.getOutputStream();
            out = new PrintWriter(outputStream, true);
            InputStream is = clientSocket.getInputStream(); // net+i/o
            in = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String msg;
            flag = true;

            while ((msg = in.readLine()) != null) {
                if (msg.length() == 0) {
                    break;
                }

                String[] split = msg.split(" ");
                String request = split[0];

                // handle user requests
                switch (request) {
                    case "received":
                        flag = true;
                        break;

                    case "login":
                        System.out.println("Receive login request from : " + split[1]);
                        String[] result = dbConnect.authentication(split[1], split[2]);
                        if (result == null)
                            out.println("failed");
                        out.println(String.join(" ", result));
                        break;

                    case "register":
                        System.out.println("Receive register request");
                        boolean r = dbConnect.createUser(split[1], split[2], split[3], split[4], Float.parseFloat(split[5]));
                        out.println(r);
                        break;

                    case "allStocks":
                        if (!flag)
                            this.wait();

                        System.out.println("Sending all stocks");
                        byte[] temp = getData();
                        DataOutputStream dOut = new DataOutputStream(outputStream);
                        dOut.writeInt(temp.length); // write length of the message
                        dOut.write(temp);           // write the message
                        dOut.flush();
                        flag = false;

                        break;

                    case "singleStock":
                        if (!flag)
                            this.wait();

                        String stock_id = split[1];
                        System.out.println("Sending stock " + stock_id + " data");
                        Stock target = MarketInfo.getStockCache(stock_id);
                        out.println(target.getCode() + " " + target.getName() + " " + target.getPrice() + " " + target.getPercent());
                        flag = false;

                        break;

                    case "watchlist":
                        if (!flag)
                            this.wait();

                        System.out.println("Sending user's watchlist");
                        List<String[]> watchlist = dbConnect.getWatchlist(split[1]);
                        for (String[] i : watchlist) {
                            System.out.println(String.join(" ", i));
                            out.println(String.join(" ", i));
                        }
                        flag = false;

                        break;

                    case "addToWatchlist":
                        String username = split[1];
                        String stockId = split[2];
                        String stockName = split[3];
                        System.out.println("Adding stock " + stockId + " to " + username + " watchlist");
                        dbConnect.insertIntoWatchlist(username, stockId, stockName);
                        break;

                    case "inStock":
                        if (!flag)
                            this.wait();

                        System.out.println("Sending user's inStock list");
                        List<String[]> inStock = dbConnect.getInStock(split[1]);
                        for (String[] i : inStock) {
                            out.println(String.join(" ", i));
                        }
                        flag = false;

                        break;

                    case "buyIn":
                        System.out.println("Adding to buy pending list");
                        dbConnect.insertIntoTradeHistory(split[1], split[2], split[3], Integer.parseInt(split[4]), Float.parseFloat(split[5]), split[6], split[7]);
                        break;

                    case "sell":
                        System.out.println("Adding to sell pending list");
                        dbConnect.insertIntoTradeHistory(split[1], split[2], split[3], Integer.parseInt(split[4]), Float.parseFloat(split[5]), split[6], split[7]);
                        break;

                    case "tradeHistory":
                        if (!flag)
                            this.wait();

                        System.out.println("Sending trade history");
                        List<String[]> tradeHistory =dbConnect.getTradeHistory(split[1]);
                        for (String[] i : tradeHistory) {
                            out.println(String.join(" ", i));
                        }
                        flag = false;

                        break;

                    case "index":
                        if (!flag)
                            this.wait();

                        System.out.println("Sending Composite Index");
                        Stock index = MarketInfo.getCompositeIndexCache();
                        System.out.println(index.getCode() + " " + index.getName() + " " + index.getPrice() + " " + index.getPercent());
                        out.println(index.getCode() + " " + index.getName() + " " + index.getPrice() + " " + index.getPercent());
                        flag = false;

                        break;

                    case "classification":
                        while (true) {
                            if (flag) {
                                System.out.println("Sending classifications");
                                List<Classification> classifications = MarketInfo.getClassificationsCache();
                                flag = false;
                                break;
                            }
                        }

                        break;

                    case "exit":
                        System.out.println("Ending connection");
                        in.close();
                        out.close();

                    default:
                        System.out.println("Unrecognizable request");
                }
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
