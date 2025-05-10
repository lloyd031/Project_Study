// Replace with your actual package
package application;

import java.util.LinkedList;


import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class BillingController {
    
	
	@FXML
    private GridPane gridPane;
	@FXML
    private TextField txttotal,txtlaborcost,txtunitprice;
    @FXML Button btnadd,btnrem;
    private int currentRowIndex =1; // Start at 7 if you already have 7 rows in FXML
    LinkedList<Double> qty=new LinkedList<Double>();
    LinkedList<Double> price=new LinkedList<Double>();
    LinkedList<Integer> toRemove=new LinkedList<Integer>();
    LinkedList<Integer> toRemoveTotal=new LinkedList<Integer>();
    LinkedList<Integer> qtyToRemove=new LinkedList<Integer>();
    LinkedList<Integer> priceToRemove=new LinkedList<Integer>();
	double totalcost=0;
	double laborcost=0;
	double unitPrice=0;
	LinkedList<TextField> total=new LinkedList<TextField>();
	
    @FXML
    private void callHandleAddRow() {
    	toRemove.clear();
		toRemoveTotal.clear();
		qtyToRemove.clear();
		priceToRemove.clear();
    	for(Node n:gridPane.getChildren()) {
			if (n instanceof CheckBox) {
		        ((CheckBox) n).setSelected(false);
		    }
		}
    	btnadd.setDisable(true);
    	handleAddRow(0,0,"","");
    	
    }
    
    private void handleAddRow( double l, double d, String mat, String app) {
    	int i=currentRowIndex-1;
    	
    	
        for (int col = 0; col < 8; col++) {
            if(col>0) {
            	TextField textField = new TextField();
                if(col!=7) {
                	if(col!=3 && col!=5 ) {
                		if(col==1) {
                			qty.add(0.0);
                    		textField.textProperty().addListener((obs, oldValue, newValue) -> {
                                if (!newValue.matches("\\d*(\\.\\d*)?")) {
                                    textField.setText(oldValue);
                                 }else {
                                	     qty.set(i, (!newValue.isEmpty())?Double.parseDouble(newValue):0);
                                		 total.get(i).setText(qty.get(i)*price.get(i)+"");
                                		 getTotal();
                                	 
                                 }
                            });
                    	}else if(col==6) {
                    		price.add(0.0);
                    		textField.textProperty().addListener((obs, oldValue, newValue) -> {
                                if (!newValue.matches("\\d*(\\.\\d*)?")) {
                                    textField.setText(oldValue);
                                 }else {
                                	
                                		 price.set(i, (!newValue.isEmpty())?Double.parseDouble(newValue):0);
                                		 total.get(i).setText(qty.get(i)*price.get(i)+"");
                                		 getTotal();
                                		
                                	 
                                 }
                            });
                    	}else {
                    		if(col==2) {
                    			textField.setText(l+"");
                    		}else if(col==4) {
                    			textField.setText(d+"");
                    		}
                    		textField.textProperty().addListener((obs, oldValue, newValue) -> {
                                if (!newValue.matches("\\d*(\\.\\d*)?")) {
                                    textField.setText(oldValue);
                                 }
                            
                            });
                    	}
                    }else {
                    	if(col==3) {
                    		textField.setText(mat);
                    	}else {
                    		textField.setText(app);
                    	}
                    }
                	
                }else {
                	textField.setEditable(false);
                	total.add(textField);
                }
                gridPane.add(textField, col, currentRowIndex);
            
            }else {
            	CheckBox cb=new CheckBox();
            	cb.setOnAction(e->{
            	 if(!toRemove.contains(i+1)) {
            		 toRemove.add(i+1);
            		 qtyToRemove.add(i);
            		 priceToRemove.add(i);
            		 toRemoveTotal.add(i);
            	 }else {
            		 toRemove.remove(toRemove.indexOf(i+1));
            		 qtyToRemove.remove(i);
            		 priceToRemove.remove(i);
            		 toRemoveTotal.remove(i);
            	 }
            		
            	});
            	gridPane.add(cb, col, currentRowIndex);
            }
        }
        currentRowIndex++;
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(event -> btnadd.setDisable(false));
        pause.play();
    }
    @FXML
    private void remove() {
    	
    	if(!toRemove.isEmpty()) {
    		btnrem.setDisable(true);
    		LinkedList<Integer> indexListRow=new LinkedList<Integer>();
    		gridPane.getChildren().removeIf(node-> toRemove.contains(GridPane.getRowIndex(node)));
    		toRemove.clear();
    		for(int i=qty.size()-1; i>=0; i--) {
    			if(qtyToRemove.contains(i)) {
    				qty.remove(i);
    			}
    		}
    		for(int i=price.size()-1; i>=0; i--) {
    			if(priceToRemove.contains(i)) {
    				price.remove(i);
    			}
    		}
    		for(int i=total.size()-1; i>=0; i--) {
    			if(toRemoveTotal.contains(i)) {
    				total.remove(i);
    			}
    		}
    		toRemoveTotal.clear();
    		qtyToRemove.clear();
    		priceToRemove.clear();
    		currentRowIndex=qty.size()+1;
    		for(Node node:gridPane.getChildren()) {
    			if(!indexListRow.contains(GridPane.getRowIndex(node))) {
    				indexListRow.add(GridPane.getRowIndex(node));
    			}
    		}
    		for(int i=0; i<indexListRow.size(); i++) {
    			for(Node node:gridPane.getChildren()) {
        			if(indexListRow.get(i)==GridPane.getRowIndex(node)) {
        				GridPane.setRowIndex(node, i); 
        			}
        		}
    		}
    		
    		System.out.println("qty "+qty.size());
    		System.out.println("price "+price.size());
    		System.out.println("qty2 "+qtyToRemove.size());
    		System.out.println("price2 "+priceToRemove.size());
    		getTotal(); 
    		PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
            pause.setOnFinished(event -> btnrem.setDisable(false));
            pause.play();
    	}
    	
    }
    public void getTotal() {
     totalcost=0;
   	 for(int i=0; i<qty.size(); i++) {
   		 totalcost+=qty.get(i)*price.get(i);
   	 }
   	 txttotal.setText(totalcost+laborcost+unitPrice+"");
   	
    }
    
    public void loadCol(double ll, double ls, double dl, double ds) {
    	handleAddRow(ls,ds,"Copper Tube","Suction Line");
    	handleAddRow(ll,dl,"Copper Tube","Liquid Line");
    	handleAddRow(0,ds,"Copper (elbow)","Suction Line");
    	handleAddRow(0,dl,"Copper (elbow)","Liquid Line");
    	handleAddRow(0,0,"Circuit Breaker"," ");
    	handleAddRow(0,0,"Rubber Insulation","Liquid Line");
    	
    	txtlaborcost.textProperty().addListener((obs, oldValue, newValue) -> {
    		if(!newValue.matches("\\d*(\\.\\d*)?")) {
            	txtlaborcost.setText(oldValue);
             }else {
            	  
            	 laborcost=(!newValue.isEmpty())?Double.parseDouble(newValue):0;
            	 getTotal();
             }
        });
    	txtunitprice.textProperty().addListener((obs, oldValue, newValue) -> {
    		if(!newValue.matches("\\d*(\\.\\d*)?")) {
    			txtunitprice.setText(oldValue);
             }else {
            	  
            	 unitPrice=(!newValue.isEmpty())?Double.parseDouble(newValue):0;
            	 getTotal();
             }
        });
    }
   
}

 