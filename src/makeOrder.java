import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

class parseOrder {
	
	
    public String getNextOrderId() {

    	try {
    		
            String nextAvailable = "";
            //Parse the file
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader("/Users/cantrellpicoujr/Documents/Object-Oriented-Programming/CS_2365_OSS_Project_Java/config_files/orders.json"));

            //Iterating the contents of the array 'items'
            Iterator iterator = jsonArray.iterator();
            while(iterator.hasNext()) {
                //Grab the next object and save appropriate types
                JSONObject order = (JSONObject) iterator.next();

                nextAvailable = (String) order.get("orderId");
                System.out.println(nextAvailable);
            }
            
    		
    	}
    	catch(IOException e) {
    		
    		e.printStackTrace();
    		
    	}
    	catch(ParseException e) {
    		
    		e.printStackTrace();
    		
    	}
    	
    	return "x";
        
    }

	
    public void updateOrderStatus(String orderId, String desiredStatus) {
    	
    	try {
            //Parse the file
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader("/Users/cantrellpicoujr/Documents/Object-Oriented-Programming/CS_2365_OSS_Project_Java/config_files/orders.json"));

            //Iterating the contents of the array 'items'
            Iterator iterator = jsonArray.iterator();
            while(iterator.hasNext()) {
                //Grab the next object and save appropriate types
                JSONObject order = (JSONObject) iterator.next();

                if(orderId.equals((String) order.get("orderId"))){
                    order.put("orderStatus", desiredStatus);
                }
            }

            PrintWriter pw = new PrintWriter("../config_files/orders.json");
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
    	catch(FileNotFoundException e) {
    		
    		e.printStackTrace();
    		
    	}
    	catch(IOException e) {
    		
    		e.printStackTrace();
    		
    	}
    	catch(ParseException e) {
    		
    		e.printStackTrace();
    		
    	}
    	
    }
    
    public void addNewOrder(String customerId, int orderTotal, String orderStatus, String purchaseAuthorizationNumber, String orderDate, ArrayList<item> items) throws IOException, ParseException {
        String orderId = this.getNextOrderId();

        //Parse the file
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader("/Users/cantrellpicoujr/Documents/Object-Oriented-Programming/CS_2365_OSS_Project_Java/config_files/orders.json"));

        Map<String, Serializable> m = new LinkedHashMap<String, Serializable>(7);
        m.put("orderId", orderId);
        m.put("customerId", customerId);
        m.put("orderTotal", orderTotal);
        m.put("orderStatus", orderStatus);
        m.put("purchaseAuthorizationNumber", purchaseAuthorizationNumber);
        m.put("orderDate", orderDate);

        JSONArray purchased = new JSONArray();
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i).getQuantityPurchased() != 0){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", items.get(i).getId());
                jsonObject.put("name", items.get(i).getName());
                jsonObject.put("price", items.get(i).getPrice());
                jsonObject.put("imgName", items.get(i).getImgName());
                jsonObject.put("quantityPurchased", items.get(i).getQuantityPurchased());

                purchased.add(jsonObject);
            }
        }

        m.put("items", purchased);

        jsonArray.add(m);

        PrintWriter pw = new PrintWriter("../config_files/orders.json");
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
	
}

class addOrder extends order {
	
	Scanner scan = new Scanner(System.in);
	
	double grandTotal = 0.00;
	
	addOrder() {
		
		deliveryMethod();
		checkIfPremium();
		requestBank();
		
	}
	
	void checkIfPremium() {
		
		Boolean isPremium = true;
		Boolean firstBuy = true;
		
		double fee = 0.00;
		
		if(isPremium && firstBuy)  {
			
			grandTotal += 40.00;
			
		}
		
	}
	
	void deliveryMethod() {
		
		double rate = 3.00;
		
		System.out.println("Choose a delivery method");
		System.out.println("1. mail - $3.00");
		System.out.println("2. pick up - free");
		int input = scan.nextInt();
		
		if(input == 1) {
			
			grandTotal += rate;
			
		} 
		
		
	}
	
	void requestBank() {
		
		Boolean bankTransaction = true;
		
		if(bankTransaction) {
			
			int transactionNum = 00000001;
			
		} else {
			
			System.out.println("TRANSACTION WAS DENIED");
			System.out.println("Do you want to enter another card number?(y/n)");
			char input = scan.next().charAt(0);
			
			if(input == 'y') {
				
				System.out.print("New Card Number: ");
				int newCardNum = scan.nextInt();
				
				if(bankTransaction) {
					
					
					int transactionNum = 00000002;
					
				}
				
			}
			
		}
		
	}
	
	void storeOrder() {
		
		
		
	}
	
	
}


class askOrder extends addOrder{
	
	Scanner scan = new Scanner(System.in);

	
	askOrder() {
		
		System.out.println("Order?(y/n)");
		char input = scan.next().charAt(0);
		
		if(input == 'y') {
			
			addOrder orderObj = new addOrder();
			
		}
		
	}
	
}


public class makeOrder {

	public static void main(String[] args) {
		
		//askOrder startObj = new askOrder();	

		addOrder orderObj = new addOrder();
		
		String obj = orderObj.getNextOrderId();
	}

}
