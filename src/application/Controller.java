package application;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.net.UnknownHostException;

import com.mongodb.MongoClient;
import com.mongodb.util.JSON;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class Controller
{

	@FXML
	private ChoiceBox<String> year_choice_2;
	@FXML
	private ChoiceBox<String> month_choice_2; 
	@FXML
	private ChoiceBox<String> month_choice_1; 
	@FXML
	private ChoiceBox<String> year_choice_1; 
	@FXML
	private TextField descrip; 
	@FXML 
	private TextField price; 
	@FXML
	private TextArea result_bin;
	@FXML
	private Button adder_button;
	@FXML 
	private Button delete_button; 
	@FXML 
	private Button launch_button; 
	
	private MongoClient mongoClient;
	private DB database;
	private DBCollection collection; 

	
	public Controller(){}


	//initialize choice box choices and connect to local db 
	@FXML
	private void initialize()
	{
		ArrayList<String> years = new ArrayList<String>();
		years.add("2018"); 
		years.add("2017"); 
		ObservableList<String> list = FXCollections.observableArrayList(years); 
		year_choice_1.setItems(list);
		year_choice_2.setItems(list);
		ArrayList<String> months = new ArrayList<String>(); 
		months.add("January"); 		
		months.add("February"); 
		months.add("March"); 
		months.add("April"); 
		months.add("May"); 
		months.add("June"); 
		months.add("July"); 
		months.add("August"); 
		months.add("September"); 
		months.add("October"); 
		months.add("November"); 
		months.add("December");
		ObservableList<String> list2 = FXCollections.observableArrayList(months); 
		month_choice_1.setItems(list2);
		month_choice_2.setItems(list2);
		try {
			mongoClient = new MongoClient("localhost");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		database = mongoClient.getDB("testerbase");
		collection = database.getCollection("budget_storage");
	}
	
	//find all entries for month/year 
	@FXML
	private void runQuery(){
		if(year_choice_1.getValue() != null && month_choice_1.getValue() != null) {
		        DBObject dbObject = (DBObject) JSON.parse("{'year':'" + year_choice_1.getValue() + "', 'month':'" + month_choice_1.getValue().toLowerCase() + "'}"); 
		        if(collection.findOne(dbObject) != null) {
		        	List descriptions = collection.distinct("description", dbObject);
		        	List prices = collection.distinct("price", dbObject);
		        	String result = ""; double price = 0.0; 
		        	for (int i = 0; i < descriptions.size() && i < prices.size(); i++) {
		        		result += descriptions.get(i) + " " + prices.get(i) + "\n";
		        		price += Double.parseDouble((String) prices.get(i)); 
		        	}
		        	result_bin.setText(result + "\n Total: " + price);
		        }
		}
		else {
			result_bin.setText(null);
		}
	}
	
	//insert a new entry 
	@FXML
	private void addElement() {
		if(month_choice_2.getValue() != null && year_choice_2.getValue() != null && descrip.getText() != null && price.getText() != null) {
		        DBObject dbObject = (DBObject) JSON.parse("{'year':'" + year_choice_2.getValue() + "', 'month':'" + month_choice_2.getValue().toLowerCase() + "', 'description':'" + descrip.getText()+ "', 'price':'" + price.getText() + "'}"); 
		        collection.insert(dbObject); 
		        clean(); 
		}
	}
	
	//delete an exact query, requires all bounds 
	@FXML
	private void deleteElement() {
		if(month_choice_2.getValue() != null && year_choice_2.getValue() != null && descrip.getText() != null && price.getText() != null) {
	        DBObject dbObject = (DBObject) JSON.parse("{'year':'" + year_choice_2.getValue() + "', 'month':'" + month_choice_2.getValue().toLowerCase() + "', 'description':'" + descrip.getText()+ "', 'price':'" + price.getText() + "'}"); 
	        collection.findAndRemove(dbObject); 
			clean();
		}
	}
	
	//reset all selections to null 
	@FXML
	private void clean() {
        descrip.setText(null);
        price.setText(null);
        month_choice_2.setValue(null);
        year_choice_2.setValue(null);
	}
	

}
