package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.Classification;
import model.Stock;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScrapUtil {

    public static Map<String, Stock> getTotalData() throws IOException {

        System.out.println("Start scraping total stock data");

        int pageNo = 1, pageCount = 3;

        Map<String, Stock> map = new LinkedHashMap<>();

        while (pageNo < pageCount) {
            Document doc = Jsoup.connect(getNextReqUrl(pageNo)).ignoreContentType(true)
                    .data("query", "Java")
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(30000)
                    .get();

            String json = doc.text();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);

            JsonNode listNode = node.get("data").path("list");
            Iterator<JsonNode> iterator = listNode.elements();

            while (iterator.hasNext()) {
                JsonNode stock = iterator.next();
                String code = stock.get("symbol").asText();
                String name = stock.get("name").asText();
                Double price = stock.get("current").asDouble();
                Double percent = stock.get("percent").asDouble();
                String key = name;

                if (!map.containsKey(key)) {
                    Stock s = new Stock(code, name, price.floatValue(), percent.floatValue());
                    map.put(key, s);
                }
            }

            pageNo++;
        }

        return map;

    }

    private static String getNextReqUrl(int pageNo) {
        return getNextReqUrl(String.valueOf(pageNo));
    }

    private static String getNextReqUrl(String pageNo) {
        return "https://xueqiu.com/service/v5/stock/screener/quote/list?page=" + pageNo + "&size=25&order=desc&orderby=percent&order_by=percent&market=CN&type=sh_sz&_=1588639128007";
    }


    public static Stock getSingleData(String stockCode) throws IOException {

        Stock currentStock = null;

        String url = String.format("https://xueqiu.com/S/%s", stockCode);

        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build())
                .build();

        HttpGet httpGet = new HttpGet(url);
        String header = UserAgentUtil.randomUserAgent();
        httpGet.setHeader("User-Agent", header);
        CloseableHttpResponse response = null;

        try {
            response = httpclient.execute(httpGet);
            System.out.println(response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                Document doc = Jsoup.parse(content);
                Elements s1 = doc.getElementsByClass("stock-name");
                Elements s2 = doc.getElementsByClass("stock-current");
                Elements s3 = doc.getElementsByClass("stock-change");
                String stockName = null;
                String stockId = null;
                String stockPrice = s2.text().substring(3);
                String pattern = "(\\D*)(\\()(\\D*)(:)(\\d+)(\\))";
                String percent = s3.text();
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(s1.text());
                if (m.find()) {
                    stockName = m.group(1);
                    stockId = m.group(3) + m.group(5);
                } else {
                    System.out.println("No match");
                }

                currentStock = new Stock(stockId, stockName, Float.parseFloat(stockPrice), Float.parseFloat(percent));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
            httpclient.close();
        }

        return currentStock;

    }

    public static Stock getIndexInfo() throws IOException {

        Stock currentStock = null;

        String stockCode = "SH000001";
        String url = String.format("https://xueqiu.com/S/%s", stockCode);

        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build())
                .build();

        HttpGet httpGet = new HttpGet(url);
        String header = UserAgentUtil.randomUserAgent();
        httpGet.setHeader("User-Agent", header);
        CloseableHttpResponse response = null;

        try {
            response = httpclient.execute(httpGet);
            System.out.println(response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                Document doc = Jsoup.parse(content);
                Elements s1 = doc.getElementsByClass("stock-name");
                Elements s2 = doc.getElementsByClass("stock-current");
                Elements s3 = doc.getElementsByClass("stock-change");
                String stockName = null;
                String stockId = null;
                String stockPrice = s2.text();
                String pattern = "(\\D*)(\\()(\\D*)(:)(\\d+)(\\))";
                String percent = s3.text();
                String[] temp = percent.split(" ");
                Float change = Float.parseFloat(temp[1].substring(0, temp[1].length() - 1));
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(s1.text());
                if (m.find()) {
                    stockName = m.group(1);
                    stockId = m.group(3) + m.group(5);
                } else {
                    System.out.println("No match");
                }

                currentStock = new Stock(stockId, stockName, Float.parseFloat(stockPrice), change);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
            httpclient.close();
        }

        return currentStock;

    }

    public static List<Classification> getClassification() throws IOException {

        List<Classification> classifications = new ArrayList<>();

        String url = "https://xueqiu.com/hq";

        CloseableHttpClient httpclient= HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build())
                .build();


        HttpGet httpGet = new HttpGet(url);
        String header = UserAgentUtil.randomUserAgent();
        httpGet.setHeader("User-Agent", header);
        CloseableHttpResponse response = null;

        try {
            response = httpclient.execute(httpGet);
            System.out.println(response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                Document doc = Jsoup.parse(content);
                Elements s1 = doc.getElementsByClass("third-nav");
                Element s2  = s1.first();
                Elements uls = s2.select("ul");
                for (Element ul : uls) {
                    Elements lis = ul.select("li");
                    lis.stream().forEach(e -> {
                        Element link = e.select("a").first();
                        String each_url = "https://xueqiu.com/hq" + link.attr("href");
                        String class_id = each_url.substring(each_url.length() - 4);
                        String class_name = link.attr("title");
                        System.out.println(class_name);
                        try {
                            classifications.add(getClassificationDetail(class_name, class_id));
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }

                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
            httpclient.close();
        }

        return classifications;

    }

    private static Classification getClassificationDetail(String class_name, String class_id) throws IOException {

        String url = String.format("https://xueqiu.com/service/v5/stock/screener/quote/list?page=1&size=90&" +
                "order=desc&order_by=percent&exchange=CN&market=CN&ind_code=S%s&_=1611408653450", class_id);

        CloseableHttpClient httpclient= HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build())
                .build();

        HttpGet httpGet = new HttpGet(url);
        String header = UserAgentUtil.randomUserAgent();
        httpGet.setHeader("User-Agent", header);
        CloseableHttpResponse response = null;

        try {
            response = httpclient.execute(httpGet);
            System.out.println(response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                Document doc = Jsoup.parse(content);
                String json = doc.text();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(json);

                JsonNode listNode = node.get("data").path("list");
                Iterator<JsonNode> iterator = listNode.elements();

                List<String[]> subclasses = new ArrayList<>();

                while (iterator.hasNext()) {
                    JsonNode stock = iterator.next();
                    String code = stock.get("symbol").asText();
                    String name = stock.get("name").asText();
                    String[] temp = {code, name};
                    subclasses.add(temp);
                }

                return new Classification(class_name, subclasses);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
            httpclient.close();
        }

        return null;

    }

}
