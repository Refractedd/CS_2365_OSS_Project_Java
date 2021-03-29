import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;


class account {
	
    String email;
    String pwd;
    String cardNumber;
    String cardExpiration;
    String cardCVV;
    String type;
    int id;

    //constructors
    account(){
    }

    account(String email, String pwd, String cardNumber, String cardExpiration, String cardCVV, String type, int id) {
       
        this.email = email;
        this.pwd = pwd;
        this.cardNumber = cardNumber;
        this.cardExpiration = cardExpiration;
        this.cardCVV = cardCVV;
        this.type = type;
        this.id = id;
    }

}

class order {

    int orderId;
    int customerId;
    int orderTotal;
    String orderStatus;
    String purchaseAuthorizationNumber;
    String orderDate;
    ArrayList<item> item;

    order(){
    }

    order(int orderId, int customerId, int orderTotal, String orderStatus, String purchaseAuthorizationNumber, String orderDate, ArrayList<item> item) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderTotal = orderTotal;
        this.orderStatus= orderStatus;
        this.purchaseAuthorizationNumber = purchaseAuthorizationNumber;
        this.orderDate = orderDate;
        this.item = item;
    }
    
}


class arrays {
	
    ArrayList<order> order_history = new ArrayList<order>();
    ArrayList<account> accounts = new ArrayList<account>();
	
    public ArrayList<account> getAccounts() {
    	
    	try {
    		
    		   //Parse the file
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader("../config_files/accounts.json"));

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
    		
    	}
    	catch(IOException e) {
    		
    		e.printStackTrace();
    		
    	}
    	catch(ParseException e) {
    		
    		e.printStackTrace();
    		
    	}

        return accounts;
    }


 public ArrayList<order> getOrders() {
	 	
	 try {
		 
	        //Parse the file
	        JSONParser jsonParser = new JSONParser();
	        JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader("../config_files/orders.json"));

	        //Iterating the contents of the array 'items'
	        Iterator iterator = jsonArray.iterator();
	        while(iterator.hasNext()) {
	            //Grab the next object and save appropriate types
	            JSONObject order = (JSONObject) iterator.next();

	            int orderId = ((Long)order.get("orderId")).intValue();
	            int customerId = ((Long)order.get("customerId")).intValue();
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

	                int id = ((Long)item.get("id")).intValue();
	                String name = (String) item.get("name");
	                int price = ((Long)item.get("price")).intValue();
	                String imgName = (String) item.get("imgName");
	                int quantityPurchased = ((Long)item.get("quantityPurchased")).intValue();


	                //Add a new item class to the catalog using the information parsed
	                items.add(new item(id, name, price, imgName, quantityPurchased));
	                
	            }

	            order_history.add(new order(orderId, customerId, orderTotal, orderStatus, purchaseAuthorizationNumber, orderDate, items));
	            
	        }
		 
	 } 
	 catch(IOException e) {
		 
		 e.printStackTrace();
		 
	 }
	 catch(ParseException e) {
		 
		 e.printStackTrace();
		 
	 }

        return order_history;
    }

	
}

class showInvoice extends arrays {
	
	ArrayList<account> accArr = super.getAccounts();
	ArrayList<order> orderArr = super.getOrders();
	
	
	
}

class requestInvoice {
	
	Scanner scan = new Scanner(System.in);
	
	requestInvoice() {
		
		System.out.println("1. View Invoice");
		int input = scan.nextInt();
		
		if(input == 1) {
			
			
			
		}
		
	}
}

public class viewInvoice {

	public static void main(String[] args) {
		

	}

}
