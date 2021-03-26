import java.io.*;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class F {
	
	JSONParser parser = new JSONParser();
	
	void readFile() {
		
		try(FileReader reader = new FileReader("/Users/cantrellpicoujr/Documents/Object-Oriented-Programming/CS_2365_OSS_Project_Java/config_files/items.json")) {
			
			Object obj = parser.parse(reader);
			JSONArray empList = (JSONArray) obj;
			empList.add(obj);
			
			
			
		} catch(FileNotFoundException e) {
			
			e.printStackTrace();
			
		}  catch(IOException e) {
			
			e.printStackTrace();
			
		} catch(ParseException e) {
			
			e.printStackTrace();
			
		} catch(Exception e) {
			
			e.printStackTrace();
			
		}
		
	}
	
}

public class mainClass {

	public static void main(String[] args) {
		
		//F obj = new F();
		
		//obj.readFile();

		LogOn login = new LogOn();

		login.display();

	}

}
