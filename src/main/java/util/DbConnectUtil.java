package util;

import model.PendingItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbConnectUtil {

    Connection con;

    public synchronized void connect() {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/backend?useUnicode=true&characterEncoding=utf-8" +
                            "&serverTimezone=GMT%2B8&useSSL=false&allowPublicKeyRetrieval=true","test","Aa123456789!");
        } catch (Exception e){ System.out.println(e);}

    }

    public synchronized String[] getUserData(String username) {

        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM userdata WHERE username = ?;");
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String[] result = {rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)};
                return result;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;

    }

    public synchronized String[] authentication(String username, String password) {

        try {
            PreparedStatement statement = con.prepareStatement("SELECT pwd FROM users WHERE username = ?;");
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            String result = null;

            while (rs.next())
                result = rs.getString(1);
            if (result != null) {
                if (result.equals(password)) {
                    return getUserData(username);
                }
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public synchronized boolean createUser(String username, String password, String phone, String email, Float balance) {

        try {
            PreparedStatement statement1 = con.prepareStatement("INSERT INTO users VALUES (?, ?);");
            statement1.setString(1, username);
            statement1.setString(2, password);
            PreparedStatement statement2 = con.prepareStatement("INSERT INTO userdata VALUES (?, ?, ?, ?);");
            statement2.setString(1, username);
            statement2.setString(2, email);
            statement2.setString(3, phone);
            statement2.setFloat(4, balance);
            int rs1 = statement1.executeUpdate();
            int rs2 = statement2.executeUpdate();
            if (rs1 == 1 && rs2 == 1)
                return true;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;
    }

    public synchronized List<String[]> getWatchlist(String username) {

        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM userdata_watchlist WHERE username = ?;");
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            List<String[]> watchlist = new ArrayList<>();
            while (rs.next()) {
                String[] result = {rs.getString(1), rs.getString(2), rs.getString(3)};
                watchlist.add(result);
            }

            return watchlist;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;

    }

    public synchronized boolean insertIntoWatchlist(String username, String stock_id, String stock_name) {

        try {
            PreparedStatement statement = con.prepareStatement("INSERT INTO userdata_watchlist VALUES (?, ?, ?);");
            statement.setString(1, username);
            statement.setString(2, stock_id);
            statement.setString(3, stock_name);

            int rs = statement.executeUpdate();
            if (rs == 1) {
                return true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;

    }

    public synchronized List<String[]> getInStock(String username) {

        try {
            PreparedStatement statement = con.prepareStatement("SELECT stock_id, stock_name, price, amount FROM userdata_instock WHERE username = ?;");
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            List<String[]> inStock = new ArrayList<>();
            while (rs.next()) {
                String[] result = {rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)};
                inStock.add(result);
            }

            return inStock;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;

    }

    public synchronized boolean buyNewStock(String username, String stock_id, String stock_name, Integer amount, Float price) {

        try {
            PreparedStatement statement = con.prepareStatement("INSERT INTO userdata_instock VALUES (?, ?, ?, ?, ?);");
            statement.setString(1, username);
            statement.setString(2, stock_id);
            statement.setString(3, stock_name);
            statement.setInt(4, amount);
            statement.setFloat(5, price);

            int rs = statement.executeUpdate();
            if (rs == 1) {
                return true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;
    }

    public synchronized boolean buyMoreStock(String username, String stock_id, Integer amount, Float price) {

        try {
            PreparedStatement statement = con.prepareStatement("UPDATE userdata_instock SET amount = amount + ?, price = (price * amount + (? * ?)) / (amount + ?) " +
                    "WHERE username = ? AND stock_id = ?;");
            statement.setInt(1, amount);
            statement.setFloat(2, price);
            statement.setInt(3, amount);
            statement.setInt(4, amount);
            statement.setString(5, username);
            statement.setString(6, stock_id);

            int rs = statement.executeUpdate();
            if (rs == 1) {
                return true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public synchronized boolean sellInStock(String username, String stock_id, Integer amount, Float price) {

        try {
            PreparedStatement statement = con.prepareStatement("UPDATE userdata_instock SET amount = amount - ?, price = (price * amount - (? * ?)) / (amount - ?) " +
                    "WHERE username = ? AND stock_id = ?;");
            statement.setInt(1, amount);
            statement.setFloat(2, price);
            statement.setInt(3, amount);
            statement.setInt(4, amount);
            statement.setString(5, username);
            statement.setString(6, stock_id);

            int rs = statement.executeUpdate();
            if (rs == 1) {
                return true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }



    public synchronized List<String[]> getTradeHistory(String username) {

        try {
            PreparedStatement statement = con.prepareStatement("SELECT tradeId, stock_id, stock_name, amount, price, date, type FROM userdata_tradehistory WHERE username = ?;");
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            List<String[]> inStock = new ArrayList<>();
            while (rs.next()) {
                String[] result = {rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getString(7)};
                inStock.add(result);
            }

            return inStock;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;

    }

    public synchronized List<PendingItem> getBuyPendingList() {

        try {
            PreparedStatement statement = con.prepareStatement("SELECT tradeId, username, stock_id, stock_name, amount, price FROM userdata_tradehistory WHERE type = 'BUY_PENDING';");
            ResultSet rs = statement.executeQuery();
            List<PendingItem> buy_pending = new ArrayList<>();
            while (rs.next()) {
                PendingItem pendingItem = new PendingItem(Integer.parseInt(rs.getString(1)), rs.getString(2), rs.getString(3),
                        rs.getString(4), Integer.parseInt(rs.getString(5)), Float.parseFloat(rs.getString(6)),"BUY_PENDING");
                buy_pending.add(pendingItem);
            }

            return buy_pending;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;

    }

    public synchronized List<PendingItem> getSellPendingList() {

        try {
            PreparedStatement statement = con.prepareStatement("SELECT tradeId, username, stock_id, stock_name, amount, price FROM userdata_tradehistory WHERE type = 'SELL_PENDING';");
            ResultSet rs = statement.executeQuery();
            List<PendingItem> sell_pending = new ArrayList<>();
            while (rs.next()) {
                PendingItem pendingItem = new PendingItem(Integer.parseInt(rs.getString(1)), rs.getString(2), rs.getString(3),
                        rs.getString(4), Integer.parseInt(rs.getString(5)), Float.parseFloat(rs.getString(6)), "SELL_PENDING");
                sell_pending.add(pendingItem);
            }

            return sell_pending;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;

    }

    public synchronized boolean insertIntoTradeHistory(String username, String stock_id, String stock_name, Integer amount, Float price, String date, String type) {

        try {
            PreparedStatement statement = con.prepareStatement("INSERT INTO userdata_tradehistory (username, stock_id, " +
                    "stock_name, amount, price, date, type) VALUES (?, ?, ?, ?, ?, ?, ?);");
            statement.setString(1, username);
            statement.setString(2, stock_id);
            statement.setString(3, stock_name);
            statement.setInt(4, amount);
            statement.setFloat(5, price);
            statement.setString(6, date);
            statement.setString(7, type);

            int rs = statement.executeUpdate();
            if (rs == 1) {
                return true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;
    }

    public synchronized boolean changeTradeType(String tradeId, String type) {

        try {
            PreparedStatement statement = con.prepareStatement("UPDATE userdata_tradehistory SET type = ? WHERE tradeId = ?;");
            statement.setString(1, type);
            statement.setString(2, tradeId);

            int rs = statement.executeUpdate();
            if (rs == 1) {
                return true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

}
