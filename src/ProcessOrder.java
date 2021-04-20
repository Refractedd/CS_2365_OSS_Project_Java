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

//import SupplierView.supplierGUI;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.stage.Stage;
import javafx.util.Callback;
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
import javafx.event.EventHandler;

public class ProcessOrder {
	
	private static final String ORDER_FILE_PATH = "../CS_2365_Project_Java/config_files/orders.json";
	private static final String STOCK_FILE_PATH = "../CS_2365_Project_Java/config_files/items.json"; 	
 	@SuppressWarnings("rawtypes")
	private TableView table = new TableView();
 	private final ObservableList<Order> data = FXCollections.observableArrayList();
 	JSONArray orders;
		
	// display orders GUI
	@SuppressWarnings("unchecked")
    public void GUI(Stage stage) throws FileNotFoundException, IOException, ParseException {
		
		/* create JSONParser object and parses ORDER_FILE_PATH */
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new FileReader(ORDER_FILE_PATH));
		orders = (JSONArray) obj;
		
		/* set table columns */
		table.setEditable(true);
		table.setMinWidth(706);
        table.getColumns().addAll(	newTableColumn("Status", 100, "status"),
					        		newTableColumn("Order ID", 100, "ID"),
					        		newTableColumn("Customer ID", 200, "customerID"),
					        		newTableColumn("Total", 100, "total"),
					        		newTableColumn("Date of purchase", 100, "date"));        
        processOrderButton(stage);
		
		/* for each order in orders */
		for(Object order: orders) {
			JSONObject orderObj = (JSONObject) order;
			
			/* add orderID to linkedList */
			data.add(new Order(	(String) orderObj.get("orderStatus"),
								(String) orderObj.get("orderId"),
								(String) orderObj.get("customerId"),
								(int) (long) orderObj.get("orderTotal"),
								(String) orderObj.get("orderDate"),
								(JSONArray) orderObj.get("items")));
		}
		/* populate table */
		table.setItems(data);
		
		Button backButton = new Button("Back");
		backButton.setOnAction(new backButtonHandler());
		backButton.setMinWidth(100);
		
		final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 10, 10, 10));
        final Label title = new Label("Orders");
		title.setFont(new Font("Arial", 20));
        vbox.getChildren().addAll(title, backButton, table);
        
        
        Scene scene = new Scene(new Group());
        ((Group) scene.getRoot()).getChildren().addAll(vbox);
        stage.setTitle("Orders");
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
	        
	        private Order(String status, String ID, String customerID, int total, String date, JSONArray items) {
	            this.status = new SimpleStringProperty(status);
	            this.ID = new SimpleStringProperty(ID);
	            this.customerID = new SimpleStringProperty(customerID);
	            this.total = new SimpleDoubleProperty(total/100.00);
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
	 
	 /* creates new table column */
	 private TableColumn<Order, String> newTableColumn(String columnTitle, int columnWidth, String classVar) {
		 
		 /* Creates column */
		 TableColumn<Order, String> newColumn = new TableColumn<Order, String>(columnTitle);
		 newColumn.setMinWidth(columnWidth);
		 
		 /* sets value to cell from object Order using attribute classVar */
		 newColumn.setCellValueFactory(new PropertyValueFactory<Order, String>(classVar));
		 
		 return newColumn;
	 }
	 
	 /* process order button */
	 @SuppressWarnings("unchecked")
	 private void processOrderButton(Stage mainStage) {
	        TableColumn<Order, Void> colBtn = new TableColumn<Order, Void>("Process Orders");

	        Callback<TableColumn<Order, Void>, TableCell<Order, Void>> cellFactory = new Callback<TableColumn<Order, Void>, TableCell<Order, Void>>() {
	            @Override
	            public TableCell<Order, Void> call(final TableColumn<Order, Void> param) {
					final TableCell<Order, Void> cell = new TableCell<Order, Void>() {

	                    private final Button btn = new Button("Process");

	                    {
	                    	/* button was pressed */
	                        btn.setOnAction((ActionEvent event) -> {
	                        	
	                        	/* gets order object associated wtih button */
	                        	Order selectedOrder = getTableView().getItems().get(getIndex());
	                        	
	                        	/* order has already been processed */
	                        	if(selectedOrder.getStatus().equals("Ready")) {
	                        		
	                        		/* alerts user order has been processed */
	                        		final Alert alert = new Alert(AlertType.INFORMATION, "Order ID: " + selectedOrder.getID(), ButtonType.OK);
	                        		alert.setTitle("Order Status");
	                        		alert.setHeaderText("Order has already been processed!");
	                        		alert.initOwner(mainStage);
	                        		alert.showAndWait();
	                        		return ;
	                        	}
	                        	
	                        	/* alert to confirm order */
	                        	Alert alert = new Alert(AlertType.CONFIRMATION, "This will reserve the items associated with this order.", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
	                        	alert.setHeaderText("Process order?");
	                        	alert.showAndWait();

	                        	if (alert.getResult() != ButtonType.YES) {
	                        	    return;
	                        	}
	                        	
	                        	/* reserve items and update stock */
	                        	
	                        	/* create JSONParser object and parses STOCK_FILE_PATH */
	                    		try {
	                    			JSONParser parser = new JSONParser();
									Object stockJSON = parser.parse(new FileReader(STOCK_FILE_PATH));
									JSONObject stock = (JSONObject) stockJSON;
									JSONArray stockArray = (JSONArray) stock.get("items");
									
									/* for each ordered item in the selected order */
									for(Object itemOrdered: selectedOrder.getItems()) {
										JSONObject orderedItem = (JSONObject) itemOrdered;
										
										/* for each item in stock */
										for(Object item: stockArray) {
											JSONObject itemObj = (JSONObject) item;
											
											/* item found */
											if(itemObj.get("name").equals(orderedItem.get("name"))) {
												
												/* items in stock */
												if((int) (long) itemObj.get("available") >= (int) (long) orderedItem.get("quantityPurchased")) {
													
													/* udpate json file */
						                            try {
						                            	/* open json file */
														PrintWriter out = new PrintWriter(STOCK_FILE_PATH);
														
														/* update stock */
														itemObj.put("available", (int) (long) itemObj.get("available") - (int) (long) orderedItem.get("quantityPurchased"));
														itemObj.put("reserved", (int) (long) orderedItem.get("quantityPurchased") + (int) (long) itemObj.get("reserved"));
														
														/* format stock to beautiful json and write to STOCK_FILE_PATH */
														out.write(new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(stock.toJSONString())));
														out.close();
														
													} catch (FileNotFoundException e) {
														e.printStackTrace();
													}
												}
												else {
													/* items not in stock */
					                        		final Alert outOfStock = new Alert(AlertType.INFORMATION, "Item: " + orderedItem.get("name"), ButtonType.OK);
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

	 class backButtonHandler implements EventHandler<ActionEvent> {
		 
		 @Override
		 public void handle(ActionEvent event) {
			 SupplierView.supplierGUI.supplierView(SupplierView.primaryStage);
		 }
	 }
}


