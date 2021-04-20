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
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ViewStock {
	
	private static final String STOCK_FILE_PATH = "../CS_2365_Project_Java/config_files/items.json";
	@SuppressWarnings("rawtypes")
	private TableView table = new TableView();
 	private final ObservableList<Item> data = FXCollections.observableArrayList();

	@SuppressWarnings("unchecked")
	public void GUI(Stage stage) throws FileNotFoundException, IOException, ParseException {
		
		/* create JSONParser object and parses ORDER_FILE_PATH */
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new FileReader(STOCK_FILE_PATH));
		
		JSONObject itemsObj = (JSONObject) obj;
		JSONArray items = (JSONArray) itemsObj.get("items");
		
		table.setEditable(true);        
        table.getColumns().addAll(	newTableColumn("Item Name", 200, "name"), 
					        		newTableColumn("Reserved", 100, "reserved"), 
					        		newTableColumn("Price", 100, "price"), 
					        		newTableColumn("In Stock", 100, "available"), 
					        		newTableColumn("ID", 100, "ID"));
        
        /* for each order in orders */
		for(Object order: items) {
			JSONObject orderObj = (JSONObject) order;
			
			/* add orderID to linkedList */
			data.add(new Item((String) 		orderObj.get("name"),
							(int) (long)	orderObj.get("reserved"),
							(int) (long)	orderObj.get("price"),
							(String) 		orderObj.get("id"),
							(int) (long)	orderObj.get("available")));
		}
		
		table.setItems(data);
		
		Button backButton = new Button("Back");
		backButton.setOnAction(new backButtonHandler());
		backButton.setMinWidth(100);
		
		final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        final Label title = new Label("Stock View");
		title.setFont(new Font("Arial", 20));
        vbox.getChildren().addAll(title, backButton, table);
		
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
        
        private Item(String name, int reserved, int price, String ID, int available) {
            this.name = new SimpleStringProperty(name);
            this.reserved = new SimpleIntegerProperty(reserved);
            this.price = new SimpleDoubleProperty(price/100.00);
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
 	
 	/* creates new table column */
	 private TableColumn<Item, String> newTableColumn(String columnTitle, int columnWidth, String classVar) {
		 
		 /* Creates column */
		 TableColumn<Item, String> newColumn = new TableColumn<Item, String>(columnTitle);
		 newColumn.setMinWidth(columnWidth);
		 
		 /* sets value to cell from object Order using attribute classVar */
		 newColumn.setCellValueFactory(new PropertyValueFactory<Item, String>(classVar));
		 
		 return newColumn;
	 }
 	
	 class backButtonHandler implements EventHandler<ActionEvent> {
		 
		 @Override
		 public void handle(ActionEvent event) {
			 SupplierView.supplierGUI.supplierView(SupplierView.primaryStage);
		 }
	 }
}
