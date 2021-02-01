import threads.BuyRunnable;
import threads.SellRunnable;
import threads.UpdateRunnable;
import threads.WorkerRunnable;
import util.DbConnectUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

    protected int          serverPort   = 6868;
    protected ServerSocket serverSocket = null;
    protected Thread       runningThread = null;
    protected Thread       buyThread = null;
    protected Thread       sellThread = null;
    protected Thread       updateThread = null;
    protected ExecutorService threadPool = Executors.newFixedThreadPool(10);
    protected DbConnectUtil dbConnect;

    public Server(int port){
        this.serverPort = port;
    }

    @Override
    public void run(){

        synchronized(this){
            this.runningThread = Thread.currentThread();
            buyThread = new Thread(new BuyRunnable());
            sellThread = new Thread(new SellRunnable());
            updateThread = new Thread(new UpdateRunnable());
//            System.out.println("Purchase Thread Started");
//            buyThread.start();
//            System.out.println("Sell Thread Started");
//            sellThread.start();
            System.out.println("Update Thread Started");
            updateThread.start();
        }

        openServerSocket();
        connectToDb();

        while(!serverSocket.isClosed()){
            Socket clientSocket = null;

            // Error handling
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(serverSocket.isClosed()) {
                    System.out.println("Server Stopped.") ;
                    return;
                }
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }

            // ThreadPool server execute
            this.threadPool.execute(new WorkerRunnable(clientSocket, dbConnect));

        }
        System.out.println("Server Stopped.") ;
    }

    public synchronized void stop(){
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 6868", e);
        }
    }

    private void connectToDb() {
        dbConnect = new DbConnectUtil();
        dbConnect.connect();
    }

    public static void main(String[] args) {
        Server server = new Server(6868);
        new Thread(server).start();
    }

}
