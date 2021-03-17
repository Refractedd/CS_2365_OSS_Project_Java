import java.util.ArrayList;

class order{
    String orderId;
    String customerId;
    int orderTotal;
    String orderStatus;
    String purchaseAuthorizationNumber;
    String orderDate;
    ArrayList<item> items;

    //constructors
    order(){
    }

    order(String orderId, String customerId, int orderTotal, String orderStatus, String purchaseAuthorizationNumber, String orderDate, ArrayList<item> items){
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderTotal = orderTotal;
        this.orderStatus = orderStatus;
        this.purchaseAuthorizationNumber = purchaseAuthorizationNumber;
        this.orderDate = orderDate;
        this.items = items;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public int getOrderTotal() {
        return orderTotal;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getPurchaseAuthorizationNumber() {
        return purchaseAuthorizationNumber;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public ArrayList<item> getItems() {
        return items;
    }
}
