import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

class fileParser{
    String accounts_path;
    String items_path;
    String orders_path;

    //Holds all the available items. Parsed from items.json
    ArrayList<item> catalog = new ArrayList<item>();
    ArrayList<order> order_history = new ArrayList<order>();


    /**
     * Constructor. Holds all file paths
     * @param accounts_path : file path of 'accounts.json'
     * @param items_path : file path of 'items.json'
     * @param orders_path : file path of 'orders.json'
     */
    fileParser(String accounts_path, String items_path, String orders_path){
        this.accounts_path = accounts_path;
        this.items_path = items_path;
        this.orders_path = orders_path;
    }

    public fileParser() { }

    public ArrayList<order> getOrders() throws IOException, ParseException {
        //Parse the file
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader("../config_files/orders.json"));

        //Iterating the contents of the array 'items'
        Iterator iterator = jsonArray.iterator();
        while(iterator.hasNext()) {
            //Grab the next object and save appropriate types
            JSONObject order = (JSONObject) iterator.next();

            String orderId = (String) order.get("orderId");;
            String customerId = (String) order.get("customerId");
            int orderTotal = ((Long)order.get("orderTotal")).intValue();
            String orderStatus = (String) order.get("orderStatus");
            String purchaseAuthorizationNumber = (String) order.get("purchaseAuthorizationNumber");
            String orderDate = (String) order.get("orderDate");
            ArrayList<item> items = new ArrayList<item>();

            JSONArray innerJsonArray = (JSONArray) order.get("items");

            //Iterating the contents of the array 'items'
            Iterator innerIterator = innerJsonArray.iterator();
            while(innerIterator.hasNext()) {
                //Grab the next object and save appropriate types
                JSONObject item = (JSONObject) innerIterator.next();

                String id = (String) item.get("id");
                String name = (String) item.get("name");
                int price = ((Long)item.get("price")).intValue();
                String imgName = (String) item.get("imgName");
                int quantityPurchased = ((Long)item.get("quantityPurchased")).intValue();


                //Add a new item class to the catalog using the information parsed
                items.add(new item(id, name, price, imgName, quantityPurchased));

            }

            order_history.add(new order(orderId, customerId, orderTotal, orderStatus, purchaseAuthorizationNumber, orderDate, items));
        }

        return order_history;
    }
    /**
     * Parses available items in the items.json file and puts them into 'catalog' arrayList.
     * @throws IOException : File not found. Check paths
     * @throws ParseException : thrown by JSONParse. Means incorrect JSON file
     */
    public void getItems() throws IOException, ParseException {
        //Parse the file
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("../config_files/items.json"));

        //Iter through the 'items' attribute
        JSONArray jsonArray = (JSONArray) jsonObject.get("items");

        //Iterating the contents of the array 'items'
        Iterator iterator = jsonArray.iterator();
        while(iterator.hasNext()) {
            //Grab the next object and save appropriate types
            JSONObject item = (JSONObject) iterator.next();

            String id = (String) item.get("id");
            String name = (String) item.get("name");
            int price = ((Long)item.get("price")).intValue();
            int reserved = ((Long)item.get("reserved")).intValue();
            int available = ((Long)item.get("available")).intValue();
            String imgName = (String) item.get("imgName");

            //Add a new item class to the catalog using the information parsed
            catalog.add(new item(id, name, price, reserved, available, imgName));
        }
    }
}

