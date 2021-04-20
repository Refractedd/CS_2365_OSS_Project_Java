import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

class fileParser{
    String accounts_path;
    String items_path;
    String orders_path;

    //Holds all the available items. Parsed from items.json
    ArrayList<item> catalog = new ArrayList<item>();
    ArrayList<order> order_history = new ArrayList<order>();
    ArrayList<account> accounts = new ArrayList<account>();

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

    /**
     * This method parses through the accounts.json file and returns the next available customer_id
     * @return : the next available customer_id
     * @throws IOException : incorrect file path
     * @throws ParseException : incorrect JSON object
     */
    public int getNextCustomerId() throws IOException, ParseException {
        int nextAvailable = -2;
        //Parse the file
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader("../CS_2365_Project_Java/config_files/accounts.json"));

        //Iterating the contents of the array 'items'
        Iterator iterator = jsonArray.iterator();
        while(iterator.hasNext()) {
            //Grab the next object and save appropriate types
            JSONObject account = (JSONObject) iterator.next();

            nextAvailable = ((Long)account.get("id")).intValue();
        }

        return nextAvailable + 1;
    }

    /**
     * This method parses through the orders.json file and returns the next available orderId
     * @return : the next available orderId
     * @throws IOException : incorrect file path
     * @throws ParseException : incorrect JSON object
     */
    public String getNextOrderId() throws IOException, ParseException {
        String nextAvailable = "0";
        //Parse the file
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader("../CS_2365_Project_Java/config_files/orders.json"));

        //Iterating the contents of the array 'items'
        Iterator iterator = jsonArray.iterator();
        while(iterator.hasNext()) {
            //Grab the next object and save appropriate types
            JSONObject order = (JSONObject) iterator.next();

            nextAvailable = (String) order.get("orderId");
        }

        return "" + (Integer.parseInt(nextAvailable) + 1);
    }

    /**
     * Given an orderId and a desiredStatus this method will update the orders.json file to reflect the appropriate orderStatus.
     *  Please not that this method does no error checking. Valid values for desiredStatus are "Ordered", "Ready", or "Shipped"
     * @param orderId : unique id for the order
     * @param desiredStatus : value to set the order status to. "Ordered", "Ready", or "Shipped" are valid values
     * @throws IOException : invalid file path
     * @throws ParseException : invalid JSON object
     */
    public void updateOrderStatus(String orderId, String desiredStatus) throws IOException, ParseException {
        //Parse the file
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader("../CS_2365_Project_Java/config_files/orders.json"));

        //Iterating the contents of the array 'items'
        Iterator iterator = jsonArray.iterator();
        while(iterator.hasNext()) {
            //Grab the next object and save appropriate types
            JSONObject order = (JSONObject) iterator.next();

            if(orderId.equals((String) order.get("orderId"))){
                order.put("orderStatus", desiredStatus);
            }
        }

        PrintWriter pw = new PrintWriter("../CS_2365_Project_Java/config_files/orders.json");
        pw.write(jsonArray.toJSONString()
                .replace("},{", "},\n\n\t\t{")
                .replace("}]", "\n\t\t}]")
                .replace("\"orderId", "\n\t\t\"orderId")
                .replace("\"customerId", "\n\t\t\"customerId")
                .replace("\"orderTotal", "\n\t\t\"orderTotal")
                .replace("\"orderStatus", "\n\t\t\"orderStatus")
                .replace("\"purchaseAuthorizationNumber", "\n\t\t\"purchaseAuthorizationNumber")
                .replace("\"orderDate", "\n\t\t\"orderDate")
                .replace("\"items", "\n\t\t\"items")
                .replace("\"imgName", "\n\t\t\t\"imgName")
                .replace("\"price", "\n\t\t\t\"price")
                .replace("\"name", "\n\t\t\t\"name")
                .replace("\"quantityPurchased", "\n\t\t\t\"quantityPurchased")
                .replace("\"id", "\n\t\t\t\"id")


        );

        pw.flush();
        pw.close();
    }

    /**
     * Given the information for the oder, this method adds a new order to the orders.json file. Please note that this
     *  method does no error checking.
     * @param customerId : customer id associating this order with a customer
     * @param orderTotal : amount of the order * 100
     * @param orderStatus : Status of the order... "Ordered", "Ready", "Shipped"
     * @param purchaseAuthorizationNumber : 8 digit confirmation code from the banking system
     * @param orderDate : Date of the order in MM/DD/YYYY format
     * @param items : ArrayList<item>... The envoker should call getItems() change the 'quantityPurchased' variable for
     *              items purchased from the catalog and pass the entire catalog into this method
     * @throws IOException
     * @throws ParseException
     */
    public void addNewOrder(String customerId, int orderTotal, String orderStatus, String purchaseAuthorizationNumber, String orderDate, String shippingMethod, ArrayList<item> items) throws IOException, ParseException {
        String orderId = this.getNextOrderId();

        //Parse the file
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader("../CS_2365_Project_Java/config_files/orders.json"));

        Map<String, Serializable> m = new LinkedHashMap<String, Serializable>(7);
        m.put("orderId", orderId);
        m.put("customerId", customerId);
        m.put("orderTotal", orderTotal);
        m.put("orderStatus", orderStatus);
        m.put("purchaseAuthorizationNumber", purchaseAuthorizationNumber);
        m.put("orderDate", orderDate);
        m.put("shippingMethod", shippingMethod);

        JSONArray purchased = new JSONArray();
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i).getQuantityPurchased() != 0){
                item item = items.get(i);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", item.getId());
                jsonObject.put("name", item.getName());
                jsonObject.put("price", item.getPrice());
                jsonObject.put("imgName", item.getImgName());
                jsonObject.put("quantityPurchased", item.getQuantityPurchased());

                purchased.add(jsonObject);
            }
        }

        m.put("items", purchased);

        jsonArray.add(m);

        PrintWriter pw = new PrintWriter("../CS_2365_Project_Java/config_files/orders.json");
        pw.write(jsonArray.toJSONString()
                .replace("},{", "},\n\n\t\t{")
                .replace("}]", "\n\t\t}]")
                .replace("\"orderId", "\n\t\t\"orderId")
                .replace("\"customerId", "\n\t\t\"customerId")
                .replace("\"orderTotal", "\n\t\t\"orderTotal")
                .replace("\"orderStatus", "\n\t\t\"orderStatus")
                .replace("\"purchaseAuthorizationNumber", "\n\t\t\"purchaseAuthorizationNumber")
                .replace("\"orderDate", "\n\t\t\"orderDate")
                .replace("\"shippingMethod", "\n\t\t\"shippingMethod")
                .replace("\"items", "\n\t\t\"items")
                .replace("\"imgName", "\n\t\t\t\"imgName")
                .replace("\"price", "\n\t\t\t\"price")
                .replace("\"name", "\n\t\t\t\"name")
                .replace("\"quantityPurchased", "\n\t\t\t\"quantityPurchased")
                .replace("\"id", "\n\t\t\t\"id")


        );

        pw.flush();
        pw.close();
    }

    /**
     * Given an item name and a quantity by which the change the stock by, this method will edit the file to reflect that.
     *  (i.e. "Let it Be", -1 will subtract one from the reserved amount. Please note that this method does not do error
     *  checking. Confirming that enough stock is available is the responsibility of the envoker.
     * @param itemName : name of the item to change the stock of
     * @param quantity : amount to change the stock by. Positive and negitive integers are allowed
     * @throws IOException : incorrect file path
     * @throws ParseException : incorrect json object
     */
    public boolean changeItemReservedAmount(String itemName, int quantity, boolean updateFile) throws IOException, ParseException {
        //Parse the file
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("../CS_2365_Project_Java/config_files/items.json"));

        //Iter through the 'items' attribute
        JSONArray jsonArray = (JSONArray) jsonObject.get("items");

        //Iterating the contents of the array 'items'
        Iterator iterator = jsonArray.iterator();
        while(iterator.hasNext()) {
            //Grab the next object and save appropriate types
            JSONObject item = (JSONObject) iterator.next();

            String name = (String) item.get("name");
            int stock = ((Long)item.get("reserved")).intValue();

            if(itemName.equals(name))
                if(stock + quantity >= 0)
                    item.put("reserved", stock + quantity);
                else
                    return false;

        }

        if(updateFile) {
            PrintWriter pw = new PrintWriter("../CS_2365_Project_Java/config_files/items.json");
            pw.write(jsonObject.toJSONString()
                    .replace("{\"items\":[{", "{\"items\":[{")
                    .replace("\"imgName\"", "\n\t\t\"imgName\"")
                    .replace("\"reserved\"", "\n\t\t\"reserved\"")
                    .replace("\"price\"", "\n\t\t\"price\"")
                    .replace("\"name\"", "\n\t\t\"name\"")
                    .replace("\"available\"", "\n\t\t\"available\"")
                    .replace("\"id\"", "\n\t\t\"id\"")
                    .replace("},{", "},\n\t{")
            );
            pw.flush();
            pw.close();
        }
        return true;
    }

    /**
     * Given an item name and a quantity by which the change the stock by, this method will edit the file to reflect that.
     *  (i.e. "Let it Be", -1 will subtract one from the available amount. Please note that this method does not do error
     *  checking. Confirming that enough stock is available is the responsibility of the envoker.
     * @param itemName : name of the item to change the stock of
     * @param quantity : amount to change the stock by. Positive and negitive integers are allowed
     * @throws IOException : incorrect file path
     * @throws ParseException : incorrect json object
     */
    public boolean changeItemAvailableAmount(String itemName, int quantity, boolean updateFile) throws IOException, ParseException {
        //Parse the file
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("../CS_2365_Project_Java/config_files/items.json"));

        //Iter through the 'items' attribute
        JSONArray jsonArray = (JSONArray) jsonObject.get("items");

        //Iterating the contents of the array 'items'
        Iterator iterator = jsonArray.iterator();
        while(iterator.hasNext()) {
            //Grab the next object and save appropriate types
            JSONObject item = (JSONObject) iterator.next();

            String name = (String) item.get("name");
            int stock = ((Long)item.get("available")).intValue();

            if(itemName.equals(name))
                if(stock + quantity >= 0)
                    item.put("available", stock + quantity);
                else
                    return false;

        }

        if(updateFile) {
            PrintWriter pw = new PrintWriter("../CS_2365_Project_Java/config_files/items.json");
            pw.write(jsonObject.toJSONString()
                    .replace("{\"items\":[{", "{\"items\":[{")
                    .replace("\"imgName\"", "\n\t\t\"imgName\"")
                    .replace("\"reserved\"", "\n\t\t\"reserved\"")
                    .replace("\"price\"", "\n\t\t\"price\"")
                    .replace("\"name\"", "\n\t\t\"name\"")
                    .replace("\"available\"", "\n\t\t\"available\"")
                    .replace("\"id\"", "\n\t\t\"id\"")
                    .replace("},{", "},\n\t{")
            );
            pw.flush();
            pw.close();
        }
        return true;
    }

    /**
     * Given new cardInfo and a customerID, this method will update the card information saved. Used if the Customer's
     *  card gets declined and a new one is entered, that works.
     * @param customerID : unique ID to the customer
     * @param cardNumber : 16 digit card number
     * @param cardExpiration : MM-YY format date
     * @param cardCVV : 3 digit security code
     * @throws IOException : wrong file path
     * @throws ParseException : wrong json file
     */
    public void changeCardInfo(String customerID, String cardNumber, String cardExpiration, String cardCVV) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader("../CS_2365_Project_Java/config_files/accounts.json"));
        Iterator iterator = jsonArray.iterator();
        while(iterator.hasNext()) {
            //Grab the next object and save appropriate types
            JSONObject account = (JSONObject) iterator.next();

            if(customerID.equals("" + account.get("id"))){
                account.put("cardNumber", cardNumber);
                account.put("cardExpiration", cardExpiration);
                account.put("cardCVV", cardCVV);
            }
        }

        PrintWriter pw = new PrintWriter("../CS_2365_Project_Java/config_files/accounts.json");
        pw.write(jsonArray.toJSONString()
                .replace("\"cardCVV", "\n\t\"cardCVV")
                .replace("\"password", "\n\t\"password")
                .replace("\"id", "\n\t\"id")
                .replace("\"cardExpiration", "\n\t\"cardExpiration")
                .replace("\"type", "\n\t\"type")
                .replace("\"email", "\n\t\"email")
                .replace("\"cardNumber", "\n\t\"cardNumber")
                .replace("},{", "},\n\n\t{")
        );

        pw.flush();
        pw.close();
    }

        /**
         * Given the params passed, adds an account to the accounts.json file. Please note that error checking is not done
         *  in this method. All params must be passed in correctly
         * @param email : email of the customer
         * @param password : password of the customer
         * @param cardNumber : 16 digit card number of the customer
         * @param cardExpiration : MM/YY format of the card expiration date
         * @param cardCVV : three digit card CVV code
         * @param type : type of the account... customer or supplier
         * @throws IOException : incorrect file path
         * @throws ParseException : incorrect JSON object
         */
    public int createAccount(String email, String password, String cardNumber, String cardExpiration, String cardCVV, String type) throws IOException, ParseException {
        int id = this.getNextCustomerId();

        //Parse the file
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader("../CS_2365_Project_Java/config_files/accounts.json"));

        Map<String, Serializable> m = new LinkedHashMap<String, Serializable>(7);
        m.put("cardCVV",cardCVV);
        m.put("password", password);
        m.put("id", id);
        m.put("cardExpiration", cardExpiration);
        m.put("type", type);
        m.put("email", email);
        m.put("cardNumber", cardNumber);

        jsonArray.add(m);

        PrintWriter pw = new PrintWriter("../CS_2365_Project_Java/config_files/accounts.json");
        pw.write(jsonArray.toJSONString()
            .replace("\"cardCVV", "\n\t\"cardCVV")
            .replace("\"password", "\n\t\"password")
            .replace("\"id", "\n\t\"id")
            .replace("\"cardExpiration", "\n\t\"cardExpiration")
            .replace("\"type", "\n\t\"type")
            .replace("\"email", "\n\t\"email")
            .replace("\"cardNumber", "\n\t\"cardNumber")
            .replace("},{", "},\n\n\t{")
        );

        pw.flush();
        pw.close();

        return id;
    }

    /**
     * This class, when called, parses the accounts.json file and puts that information into an ArrayList of account classes
     * @return : an arrayList of account classes
     * @throws IOException : incorrect file path
     * @throws ParseException : incorrect accounts.json file
     */
    public ArrayList<account> getAccounts() throws IOException, ParseException {
        accounts = new ArrayList<>();

        //Parse the file
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader("../CS_2365_Project_Java/config_files/accounts.json"));

        //Iterating the contents of the array 'items'
        Iterator iterator = jsonArray.iterator();
        while(iterator.hasNext()) {
            //Grab the next object and save appropriate types
            JSONObject account = (JSONObject) iterator.next();

            String email = (String) account.get("email");;
            String password = (String) account.get("password");
            String cardNumber = (String) account.get("cardNumber");
            String cardExpiration = (String) account.get("cardExpiration");
            String cardCVV = (String) account.get("cardCVV");
            String type = (String) account.get("type");
            int id = ((Long)account.get("id")).intValue();

            accounts.add(new account(email, password, cardNumber, cardExpiration, cardCVV, type, id));
        }

        return accounts;
    }

    /**
     * This class, when called, parses the orders.json file and puts that information into an ArrayList of order classes
     * @return : an arrayList of order classes
     * @throws IOException : incorrect file path
     * @throws ParseException : incorrect orders.json file
     */
    public ArrayList<order> getOrders() throws IOException, ParseException {
        order_history = new ArrayList<>();
        //Parse the file
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader("../CS_2365_Project_Java/config_files/orders.json"));

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
            String shippingMethod = (String) order.get("shippingMethod");
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

            order_history.add(new order(orderId, customerId, orderTotal, orderStatus, purchaseAuthorizationNumber, orderDate, shippingMethod, items));
        }

        return order_history;
    }

    /**
     * This class, when called, parses the items.json file and puts that information into an ArrayList of item classes
     * @return : an arrayList of item classes
     * @throws IOException : incorrect file path
     * @throws ParseException : incorrect items.json file
     */
    public ArrayList<item> getItems() throws IOException, ParseException {
        catalog = new ArrayList<>();

        //Parse the file
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("../CS_2365_Project_Java/config_files/items.json"));

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
        return catalog;
    }
}

