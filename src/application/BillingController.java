// Replace with your actual package
package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;


import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class BillingController {
    
	
	@FXML
    private GridPane gridPane;
	
	@FXML
    private TextField txttotal,txtlaborcost,txtunitprice;
    @FXML Button btnadd,btnrem,btnsave,btnprint;
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
    public void saveAs() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Text File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.csv"));
        Stage stage = (Stage) txttotal.getScene().getWindow();

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            	writer.write("Price,"+txtunitprice.getText()+"\n");
            	for(int i=0; i<currentRowIndex; i++) {
            		for(Node n:gridPane.getChildren()) {
            			int index=(GridPane.getRowIndex(n)==null)?0:GridPane.getRowIndex(n);
            			if(index==i) {
            				if(n instanceof Label label) {
            					writer.write(label.getText() + ",");
            				}else if(n instanceof TextField textfield) {
            					writer.write(textfield.getText() + ",");
            				}
            			}
            		}
            		//
            		writer.write("\n");
            	}
            	writer.write("Labor cost,"+txtlaborcost.getText()+"\n");
            	writer.write("Overall Total Cost,"+txttotal.getText()+"\n");
                System.out.println("Data saved to: " + file.getAbsolutePath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    
    }
    
    
    private GridPane copyGridPane(GridPane original) {
        GridPane copy = new GridPane();
        for (Node node : original.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);
            boolean a=(colIndex==1)?true:false;
            Node clone = cloneNode(node,a);
            if (clone != null) {
                copy.add(clone, colIndex == null ? 0 : colIndex, rowIndex == null ? 0 : rowIndex);
            }
        }
        return copy;
    }

    private Node cloneNode(Node node, boolean a) {
        if (node instanceof TextField tf) {
            TextField newTF = new TextField(tf.getText());
            newTF.setEditable(false);
            newTF.setFocusTraversable(false);
            newTF.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
            if(a==true) {
            	newTF.setPrefWidth(100);
            }
            return newTF;
        } else if (node instanceof Label label) {
        	Label newlbl=new Label(label.getText());
        	newlbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
            return newlbl;
        }
        
        return null; // Ignore unsupported types
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
    	btnsave.setOnAction(e->{
    		saveAs();
    	});
    	btnprint.setOnAction(e->{
    		try {
            	FXMLLoader loader=new FXMLLoader(getClass().getResource("Receipt.fxml"));
                Parent root = loader.load();
                ReceiptController controller=loader.getController();
                GridPane copy = copyGridPane(gridPane);
                controller.prepReceipt(copy,totalcost+laborcost+unitPrice+"",unitPrice+"",laborcost+"");
                Stage newStage = new Stage();
                newStage.setTitle("Billing of Materials");
                newStage.setResizable(false);
                newStage.setScene(new Scene(root));
                newStage.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
    		
    	});
    	
    }
   
}

 