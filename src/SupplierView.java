import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.simple.parser.ParseException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class SupplierView extends Application {
	
	static Stage primaryStage;

	public static void GUI(String[] args) {
		
		launch(args);
	}
	
	@Override
    public void start(Stage stage) throws FileNotFoundException, IOException, ParseException {
		
		/* make stage global variable */
		primaryStage = stage;
		
		supplierGUI.supplierView(stage);
		
	}
	
	public static class supplierGUI {
		
		static void supplierView(Stage stage) {
			
			SupplierView view = new SupplierView();
			
			final VBox vbox = new VBox();
			vbox.setSpacing(5);
			vbox.setPadding(new Insets(10, 0, 0, 10));
			final Label title = new Label("Supplier View");
			title.setFont(new Font("Arial", 20));
			
			Button processOrderButton = new Button("Process Ordered Orders");
			processOrderButton.setOnAction(view.new processOrderButtonHandler());
			processOrderButton.setAlignment(Pos.CENTER);
			processOrderButton.setMinWidth(200);
			
			Button shipOrderButton = new Button("Ship Ready Orders");
			shipOrderButton.setOnAction(view.new shipOrderButtonHandler());
			shipOrderButton.setAlignment(Pos.CENTER);
			shipOrderButton.setMinWidth(200);
			
			Button viewStockButton = new Button("View Stock");
			viewStockButton.setOnAction(view.new viewStockButtonHandler());
			viewStockButton.setAlignment(Pos.CENTER);
			viewStockButton.setMinWidth(200);
			
			
			vbox.getChildren().addAll(title, processOrderButton, shipOrderButton, viewStockButton);
			Scene scene = new Scene(new Group());
			((Group) scene.getRoot()).getChildren().addAll(vbox);
			stage.setTitle("Orders");
			stage.setWidth(725);
			stage.setHeight(475);
			stage.setScene(scene);
			stage.show();
		}
	}
	
	class processOrderButtonHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			
			/* Process order GUI */
			ProcessOrder processOrder = new ProcessOrder();
			try {
				processOrder.GUI(primaryStage);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}	
	}
	
	class shipOrderButtonHandler implements EventHandler<ActionEvent> {
		
		@Override
		public void handle(ActionEvent event) {
			
			/* ship order GUI */
			ShipOrder shipOrder = new ShipOrder();
			try {
				shipOrder.GUI(primaryStage);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}	
	}
	
	class viewStockButtonHandler implements EventHandler<ActionEvent> {
		
		@Override
		public void handle(ActionEvent event) {
			
			/* view stock GUI */
			ViewStock viewStock = new ViewStock();
			try {
				viewStock.GUI(primaryStage);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}	
	}
}
