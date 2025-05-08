// Replace with your actual package
package application;

import java.util.LinkedList;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class BillingController {

	@FXML
    private GridPane gridPane;
	@FXML
    private TextField txttotal;

    private int currentRowIndex =1; // Start at 7 if you already have 7 rows in FXML
    double qty=0;
	double price=0;
	LinkedList<Double> totallist=new LinkedList<Double>();
	TextField total;
	
    @FXML
    private void handleAddRow() {
    	
        for (int col = 0; col < 7; col++) {
            TextField textField = new TextField();
            if(col!=6) {
            	totallist.add(0.0);
            	if(col!=2 && col!=4 ) {
            		if(col==0) {
                		textField.textProperty().addListener((obs, oldValue, newValue) -> {
                            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                                textField.setText(oldValue);
                             }else {
                            	 if(!newValue.isEmpty()) {
                            		 qty=Double.parseDouble(newValue);
                            		 total.setText(qty*price+"");
                            	 }
                             }
                        });
                	}else if(col==5) {
                		textField.textProperty().addListener((obs, oldValue, newValue) -> {
                            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                                textField.setText(oldValue);
                             }else {
                            	 if(!newValue.isEmpty()) {
                            		 price=Double.parseDouble(newValue);
                            		 total.setText(qty*price+"");
                            		 txttotal.setText("SDfsdf");
                            	 }
                             }
                        });
                	}else {
                		textField.textProperty().addListener((obs, oldValue, newValue) -> {
                            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                                textField.setText(oldValue);
                             }
                        });
                	}
                }
            	
            }else {
            	textField.setEditable(false);
            	total=textField;
            }
            gridPane.add(textField, col, currentRowIndex);
        }
        currentRowIndex++;
    }
}