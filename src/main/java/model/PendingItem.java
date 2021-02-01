package model;

public class PendingItem {

    private Integer tradeId;
    private String username;
    private String stockId;
    private String stockName;
    private Integer amount;
    private Float price;
    private String status;

    public PendingItem(Integer tradeId, String username, String stockId, String stockName, Integer amount, Float price, String status) {
        this.tradeId = tradeId;
        this.username = username;
        this.stockId = stockId;
        this.stockName = stockName;
        this.amount = amount;
        this.price = price;
        this.status = status;
    }

    public String getStockName() {
        return stockName;
    }

    public Integer getTradeId() {
        return tradeId;
    }

    public String getUsername() {
        return username;
    }

    public Integer getAmount() {
        return amount;
    }

    public String getStockId() {
        return stockId;
    }

    public Float getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

}
