import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ViewStock extends Application {
	
	private static final String STOCK_FILE_PATH = "/Users/brandonmaciel/Documents/School/Semesters/Spring 2021 TTU/Object-Oriented Programming (CS 2365-001)/Project/OnlineShoppingSystem/items.json";
	private TableView table = new TableView();
 	private final ObservableList<Item> data = FXCollections.observableArrayList();

 	@Override
	public void start(Stage stage) throws FileNotFoundException, IOException, ParseException {
		
		/* create JSONParser object and parses ORDER_FILE_PATH */
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new FileReader(STOCK_FILE_PATH));
		
		JSONObject itemsObj = (JSONObject) obj;
		JSONArray items = (JSONArray) itemsObj.get("items");
		
		table.setEditable(true);
		
		TableColumn columnName = new TableColumn("Item Name");
		columnName.setMinWidth(200);
		columnName.setCellValueFactory(
                new PropertyValueFactory<Item, String>("name"));
 
        TableColumn columnReserved = new TableColumn("Reserved");
        columnReserved.setMinWidth(100);
        columnReserved.setCellValueFactory(
                new PropertyValueFactory<Item, Integer>("reserved"));
 
        TableColumn columnPrice = new TableColumn("Price");
        columnPrice.setMinWidth(100);
        columnPrice.setCellValueFactory(
                new PropertyValueFactory<Item, Double>("price"));
        
        TableColumn columnStock = new TableColumn("In Stock");
        columnStock.setMinWidth(100);
        columnStock.setCellValueFactory(
        		new PropertyValueFactory<Item, Integer>("available"));
        
        TableColumn columnID = new TableColumn("ID");
        columnID.setMinWidth(100);
        columnID.setCellValueFactory(
        		new PropertyValueFactory<Item, String>("ID"));

        
        table.getColumns().addAll(columnName, columnReserved, columnPrice, columnStock, columnID);
        
        /* for each order in orders */
		for(Object order: items) {
			JSONObject orderObj = (JSONObject) order;
			
			/* add orderID to linkedList */
			data.add(new Item((String) 		orderObj.get("name"),
							(int) (long)	orderObj.get("reserved"),
							(Double) 		orderObj.get("price"),
							(String) 		orderObj.get("id"),
							(int) (long)	orderObj.get("available")));
		}
		
		table.setItems(data);
		
		
		
		final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        final Label title = new Label("Stock View");
		title.setFont(new Font("Arial", 20));
        vbox.getChildren().addAll(title, table);
		
		Scene scene = new Scene(new Group());
        ((Group) scene.getRoot()).getChildren().addAll(vbox);
        stage.setTitle("Stock");
        stage.setWidth(625);
        stage.setHeight(475);
        stage.setScene(scene);
        stage.show();
	}
 	
 	public static class Item {
 		private final SimpleStringProperty name;
        private final SimpleIntegerProperty reserved;
        private final SimpleDoubleProperty price;	        
        private final SimpleIntegerProperty available;
        private final SimpleStringProperty ID;
        
        private Item(String name, int reserved, double price, String ID, int available) {
            this.name = new SimpleStringProperty(name);
            this.reserved = new SimpleIntegerProperty(reserved);
            this.price = new SimpleDoubleProperty(price);
            this.ID = new SimpleStringProperty(ID);
            this.available = new SimpleIntegerProperty(available);
        }

        public String getName() {
            return name.get();
        }
        public void setName(String string) {
        	name.set(string);
        }
        
        public int getReserved() {
            return reserved.get();
        }
        public void setReserved(int quantity) {
        	reserved.set(quantity);
        }
        
        public double getPrice() {
            return price.get();
        }
        public void setPrice(double price) {
        	this.price.set(price);
        }
        
        public int getAvailable() {
            return available.get();
        }        
        public void setAvailable(int stock) {
        	available.set(stock);
        }
        
        public String getID() {
            return ID.get();
        }
        public void setID(String string) {
        	ID.set(string);
        }
 	}
}
