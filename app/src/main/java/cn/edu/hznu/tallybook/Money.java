package cn.edu.hznu.tallybook;

public class Money {
    private String source;
    private String price;
    private String date;
    private String account;
    private String type;
    private int id;

    public Money(){}

    public Money(String s,String p,String d,String a,int i,String t){
        source = s;
        price = p;
        date = d;
        account = a;
        id = i;
        type = t;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}