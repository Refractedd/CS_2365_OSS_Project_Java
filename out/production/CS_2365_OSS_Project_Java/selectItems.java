import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

class item {
    int id; //id of the product
    String name; //name of the product
    int price; //price of the product * 100 (no decimal place. $1.99 would be 199)
    int reserved; //units reserved
    int available; //units available
    String imgName; //file path of the img (id applicable)
    int quantityPurchased; //holds the number of items purchased for order history

    //constructors
    item(){
    }

    item(int id, String name, int price, int reserved, int available){
        this.id = id;
        this.name = name;
        this.price = price;
        this.reserved = reserved;
        this.available = available;
    }

    item(int id, String name, int price, int reserved, int available, String imgName){
        this.id = id;
        this.name = name;
        this.price = price;
        this.reserved = reserved;
        this.available = available;
        this.imgName = imgName;
    }

    item(int id, String name, int price, String imgName, int quantityPurchased){
        this.id = id;
        this.name = name;
        this.price = price;
        this.imgName = imgName;
        this.quantityPurchased = quantityPurchased;
    }

    //Get methods
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getReserved() {
        return reserved;
    }

    public int getAvailable() {
        return available;
    }

    public int getQuantityPurchased() { 
    	
    	return quantityPurchased; 
    	
   }

    public String getImgName() {
        return imgName;
    }

    public void setQuantityPurchased(int quantityPurchased) {
        this.quantityPurchased = quantityPurchased;
    }
}

class itemParser {
		
		ArrayList<item> catalog = new ArrayList<item>();
		
		ArrayList<item> itemsArr() {
			
			try {
				//Parse the file
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("/Users/cantrellpicoujr/Documents/Object-Oriented-Programming/CS_2365_OSS_Project_Java/config_files/items.json"));

				//Iter through the 'items' attribute
				JSONArray jsonArray = (JSONArray) jsonObject.get("items");

				//Iterating the contents of the array 'items'
				Iterator iterator = jsonArray.iterator();
				while(iterator.hasNext()) {
		        //Grab the next object and save appropriate types
		        JSONObject item = (JSONObject) iterator.next();

		        String id = (String) item.get("id");
		        int id_num = Integer.parseInt(id);
		        String name = (String) item.get("name");
		        int price = ((Long)item.get("price")).intValue();
		        int reserved = ((Long)item.get("reserved")).intValue();
		        int available = ((Long)item.get("available")).intValue();
		        String imgName = (String) item.get("imgName");

		        //Add a new item class to the catalog using the information parsed
		        catalog.add(new item(id_num, name, price, reserved, available, imgName));
		    }
		}
		catch(IOException e) {
			
			e.printStackTrace();
			
		}
		catch(ParseException f) {
			
			f.printStackTrace();
			
		}
	    
		return catalog;
			
	}
		
}

class display extends itemParser {
	
	item shoppingCart = new item();
	
	void displayItems() {
		
		System.out.println("CATALOG ITEMS");
		
		ArrayList<item> item = super.itemsArr();
			
		for(int i = 0; i < item.size(); i++) {
			
			String itemName = item.get(i).name;
			int price = item.get(i).price;
			int id = item.get(i).id;
			int reserved = item.get(i).reserved;
			int available = item.get(i).available;
			
			System.out.println(id);
			System.out.println(itemName);
			System.out.println(price);
			System.out.println(reserved);
			System.out.println(available);
			
		}
	 		
	}
	
}

class customerRequest extends display {
	
	Scanner scan = new Scanner(System.in);
	
	customerRequest() {
		
		String optionRequest;
		
		do {
			
			System.out.println("Select a catalog to browse");
			System.out.println("1: Items");
			System.out.println("q: to quit.");
			System.out.print("> ");
			optionRequest = scan.nextLine();
			
			switch(optionRequest) {
			
				case "1":
					cart cart = new cart();
					ArrayList<item> cartArr = cart.addToCart();
					cart.print();
					break;
				case "q":
					break;
					
			}
			
		} while(!optionRequest.equals("q"));
		
		scan.close();
		
	}
	
}

class cart extends itemParser {
	
	ArrayList<item> cartArr = new ArrayList<item>();
	ArrayList<item> itemArr = super.itemsArr();
	
	double total = 0.00;
	
	Scanner scan = new Scanner(System.in);
	
	char input = 'n';
	
	void print() {
		
		for(item x : cartArr) {
			
			System.out.println(x.id);
			
		}
		
	}
	
	
	ArrayList<item> addToCart() {
		
		System.out.print("Do you want to add any items to the cart?(y/n): ");
		input = scan.next().charAt(0);
		
		if(input == 'n') {
			
			System.out.println("Exited.");
			
		} else {
			
			while(input != 'n') {
				
				boolean found = false;
				int i = 0;
				
				System.out.print("Select id to add to cart: ");
				int id = scan.nextInt();
				
				System.out.print("How many of the items do you want?: ");
				int quantity = scan.nextInt();
				
				while(!found && i < itemArr.size()) {
					
					if(itemArr.get(i).id == id) {
						
						found = true;
						cartArr.add(new item(itemArr.get(i).id, itemArr.get(i).name, itemArr.get(i).price, itemArr.get(i).imgName, quantity));
						
						
					} else {
						
						i++;
						
					}
					
					
				}
				
				if(found == false) {
					
					System.out.println("Item not found.");
					
				}
				
				System.out.print("Do you want to add another item to the cart?(y/n): ");
				input = scan.next().charAt(0);
				
				
			}
			
		}
		
		return cartArr;
		
	}
	
	
}

public class selectItems {

	public static void main(String[] args) {
	
		customerRequest myObj_2 = new customerRequest();
				
	}

}
