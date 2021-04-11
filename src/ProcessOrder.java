
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

public class ProcessOrder {	
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		Application.launch(Orders.class, args);
//		Orders test = new Orders();
		
//		test.getOrders();
		
	}
}

class Stock {
	// view stock
	// check in stock
	// reserve items
	// update stock
}