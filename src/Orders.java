import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.event.ActionEvent;

public class Orders extends Application {
	
 	private static final String ORDER_FILE_PATH = "/Users/brandonmaciel/Documents/School/Semesters/Spring 2021 TTU/Object-Oriented Programming (CS 2365-001)/Project/OnlineShoppingSystem/orders.json";
 	private static final String STOCK_FILE_PATH = "/Users/brandonmaciel/Documents/School/Semesters/Spring 2021 TTU/Object-Oriented Programming (CS 2365-001)/Project/OnlineShoppingSystem/items.json"; 	
 	private TableView table = new TableView();
 	private final ObservableList<Order> data = FXCollections.observableArrayList();
 	JSONArray orders;
 	JSONObject orderObj;
		
	// display orders GUI
	@Override
    public void start(Stage stage) throws FileNotFoundException, IOException, ParseException {
		
		/* create JSONParser object and parses ORDER_FILE_PATH */
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new FileReader(ORDER_FILE_PATH));
		
		/* get JSON array of orders from Object obj */
		orders = (JSONArray) obj;
		
		final Label label = new Label("Orders");
		label.setFont(new Font("Arial", 20));
		
		table.setEditable(true);
		
		TableColumn status = new TableColumn("Status");
		status.setMinWidth(100);
		status.setCellValueFactory(
                new PropertyValueFactory<Order, String>("status"));
 
        TableColumn ID = new TableColumn("Order ID");
        ID.setMinWidth(100);
        ID.setCellValueFactory(
                new PropertyValueFactory<Order, String>("ID"));
 
        TableColumn custID = new TableColumn("Customer ID");
        custID.setMinWidth(200);
        custID.setCellValueFactory(
                new PropertyValueFactory<Order, String>("customerID"));
        
        TableColumn total = new TableColumn("Total");
        total.setMinWidth(100);
        total.setCellValueFactory(
        		new PropertyValueFactory<Order, Double>("total"));
        
        TableColumn date = new TableColumn("Date of purchase");
        date.setMinWidth(100);
        date.setCellValueFactory(
        		new PropertyValueFactory<Order, String>("date"));

        
        table.getColumns().addAll(status, ID, custID, total, date);
        processOrderButton(stage);
		
		/* for each order in orders */
		for(Object order: orders) {
			orderObj = (JSONObject) order;
			
			/* add orderID to linkedList */
			data.add(new Order((String) orderObj.get("orderStatus"),
							(String) orderObj.get("orderId"),
							(String) orderObj.get("customerId"),
							(Double) orderObj.get("orderTotal"),
							(String) orderObj.get("orderDate"),
							(JSONArray) orderObj.get("items")));
		}
		
		table.setItems(data);
		table.setMinWidth(706);
		
		final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table);
        
        
        Scene scene = new Scene(new Group());
        ((Group) scene.getRoot()).getChildren().addAll(vbox);
        stage.setTitle("Orders");
        stage.setWidth(725);
        stage.setHeight(475);
        stage.setScene(scene);
        stage.show();
    }
	
	 public static class Order {
		 	 
	        private final SimpleStringProperty status;
	        private final SimpleStringProperty ID;
	        private final SimpleStringProperty customerID;	        
	        private final SimpleDoubleProperty total;
	        private final SimpleStringProperty date;
	        private final JSONArray items;
	        
	        private Order(String status, String ID, String customerID, double total, String date, JSONArray items) {
	            this.status = new SimpleStringProperty(status);
	            this.ID = new SimpleStringProperty(ID);
	            this.customerID = new SimpleStringProperty(customerID);
	            this.total = new SimpleDoubleProperty(total);
	            this.date = new SimpleStringProperty(date);
	            this.items = items;
	        }
	 
	        public String getStatus() {
	            return status.get();
	        }
	        public void setStatus(String string) {
	        	status.set(string);
	        }
	        
	        public String getID() {
	            return ID.get();
	        }
	        public void setID(String string) {
	        	ID.set(string);
	        }
	 
	        public String getCustomerID() {
	            return customerID.get();
	        }
	        public void setCustomerID(String string) {
	        	customerID.set(string);
	        }
	        
	        public double getTotal() {
	        	return total.get();
	        }
	        public void setTotal(double totalCost) {
	        	total.set(totalCost);
	        }
	        
	        public String getDate() {
	        	return date.get();
	        }
	        public void setDate(String string) {
	        	date.set(string);
	        }

			public JSONArray getItems() {
				return items;
			}
	        
	        
	    }

	 /* process order button */
	 private void processOrderButton(Stage mainStage) {
	        TableColumn<Order, Void> colBtn = new TableColumn("Process Orders");

	        Callback<TableColumn<Order, Void>, TableCell<Order, Void>> cellFactory = new Callback<TableColumn<Order, Void>, TableCell<Order, Void>>() {
	            @Override
	            public TableCell<Order, Void> call(final TableColumn<Order, Void> param) {
	                final TableCell<Order, Void> cell = new TableCell<Order, Void>() {

	                    private final Button btn = new Button("Process");

	                    {
	                        btn.setOnAction((ActionEvent event) -> {
	                        	Order selectedOrder = getTableView().getItems().get(getIndex());
	                        	
	                        	/* order has already been processed */
	                        	if(selectedOrder.getStatus().equals("Ready")) {
	                        		
	                        		/* creates popup message */
	                        		final Alert alert = new Alert(AlertType.INFORMATION, "Order ID: " + selectedOrder.getID(), ButtonType.OK);
	                        		alert.setTitle("Order Status");
	                        		alert.setHeaderText("Order has already been processed!");
	                        		alert.initOwner(mainStage);
	                        		alert.showAndWait();
	                        		return ;
	                        	}
	                        	
	                        	/* confirmation to process order */
	                        	Alert alert = new Alert(AlertType.CONFIRMATION, "This will reserve the items associated with this order.", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
	                        	alert.setHeaderText("Process order?");
	                        	alert.showAndWait();

	                        	if (alert.getResult() != ButtonType.YES) {
	                        	    return;
	                        	}
	                        	
	                        	/* reserve items and update stock */
	                        	
	                        	/* create JSONParser object and parses ORDER_FILE_PATH */
	                    		JSONParser parser = new JSONParser();
	                    		try {
									Object stockJSON = parser.parse(new FileReader(STOCK_FILE_PATH));
									
									JSONObject stock = (JSONObject) stockJSON;
									JSONArray stockArray = (JSONArray) stock.get("items");
									
									/* for each item in the stock */
									for(Object item: stockArray) {
										JSONObject itemObj = (JSONObject) item;
										
										/* for each item in the selected order */
										for(Object itemOrdered: selectedOrder.getItems()) {
											JSONObject tmpItem = (JSONObject) itemOrdered;
											
											/* item found */
											if(itemObj.get("name").equals(tmpItem.get("name"))) {
												
												/* items in stock */
												if((int) (long) itemObj.get("available") >= (int) (long) tmpItem.get("quantityPurchased")) {
													
													/* udpate json file */
						                            try {
						                            	/* open json file */
														PrintWriter out = new PrintWriter(STOCK_FILE_PATH);
														
														/* update stock */
														itemObj.put("available", (int) (long) itemObj.get("available") - (int) (long)tmpItem.get("quantityPurchased"));
														itemObj.put("reserved", (int) (long) tmpItem.get("quantityPurchased") + (int) (long) itemObj.get("reserved"));
														
														/* format orders json */
							                            Gson gson = new GsonBuilder().setPrettyPrinting().create();
							                            JsonParser jp = new JsonParser();
														JsonElement je = jp.parse(stock.toJSONString());  
							                            String prettyJsonString = gson.toJson(je);
							                            
							                            /* write to orders.json */
														out.write(prettyJsonString);
														out.close();
														
													} catch (FileNotFoundException e) {
														e.printStackTrace();
													}
												}
												else {
													/* items not in stock */
					                        		final Alert outOfStock = new Alert(AlertType.INFORMATION, "Item: " + tmpItem.get("name"), ButtonType.OK);
					                        		outOfStock.setTitle("Stock");
					                        		outOfStock.setHeaderText("Item out of stock!");
					                        		outOfStock.initOwner(mainStage);
					                        		outOfStock.showAndWait();
					                        		
					                        		return ;
												}
												
												break;
											}
										}
									}
									
									
								} catch (FileNotFoundException e1) {
									e1.printStackTrace();
								} catch (IOException e1) {
									e1.printStackTrace();
								} catch (ParseException e1) {
									e1.printStackTrace();
								}
	                        	
	                        	/* update status on GUI */
	                            selectedOrder.setStatus("Ready");
	                            table.refresh();
	                            
	                            /* udpate json file */
	                            try {
	                            	/* open json file */
									PrintWriter out = new PrintWriter(ORDER_FILE_PATH);
									
									/* find order and change status to Ready */
		                            for(Object tmp: orders) {
		                            	JSONObject tmpOrder = (JSONObject) tmp;
		                            	if(selectedOrder.getID() == tmpOrder.get("orderId")) {
		                            		
		                            		tmpOrder.put("orderStatus", "Ready");
		                            	}
		                            }
									
									/* format orders json */
		                            Gson gson = new GsonBuilder().setPrettyPrinting().create();
		                            JsonParser jp = new JsonParser();
									JsonElement je = jp.parse(orders.toJSONString());  
		                            String prettyJsonString = gson.toJson(je);
		                            
		                            /* write to orders.json */
									out.write(prettyJsonString);
									out.close();
									
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								}
	                            
	                            /* alerts user order was processed successfully */
	                            Alert successAlert = new Alert(AlertType.INFORMATION, " ", ButtonType.OK);
	                            successAlert.setHeaderText("Successfully reserved items!");
	                            successAlert.showAndWait();
	                            
	                        });
	                    }

	                    @Override
	                    public void updateItem(Void item, boolean empty) {
	                        super.updateItem(item, empty);
	                        if (empty) {
	                            setGraphic(null);
	                        } else {
	                            setGraphic(btn);
	                        }
	                    }
	                };
	                
	                /* set button in center of cell */
	                cell.setAlignment(Pos.CENTER);
	                return cell;
	            }
	        };

	        colBtn.setCellFactory(cellFactory);

	        table.getColumns().add(colBtn);

	    }
}


