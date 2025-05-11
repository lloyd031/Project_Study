package application;

import java.util.LinkedList;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ReceiptController {
@FXML 
private Button btnprint;
@FXML 
private VBox receiptVBox,vBoxGridPane,vBoxCon;

public void prepReceipt(GridPane gridPane,String totalcost, String unitPrice, String laborcost) {
	
	vBoxGridPane.getChildren().add(gridPane);
	HBox hBox1=new HBox();
	hBox1.setAlignment(Pos.CENTER_LEFT);
	Label price=new Label("₱ "+unitPrice+'0');
	price.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
	hBox1.getChildren().addAll(new Label("Unit price "),price);
	
	HBox hBox2=new HBox();
	hBox2.setAlignment(Pos.CENTER_LEFT);
	Label labor=new Label("₱ "+laborcost+'0');
	labor.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
	hBox2.getChildren().addAll(new Label("Labor cost "),labor);
	
	HBox hBox3=new HBox();
	hBox3.setAlignment(Pos.CENTER_LEFT);
	Label total=new Label("₱ "+totalcost+'0');
	total.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
	
	hBox3.getChildren().addAll(new Label("Total cost "),total);
	vBoxGridPane.setSpacing(10);
	vBoxGridPane.getChildren().addAll(hBox1,hBox2,hBox3);
	Platform.runLater(this::printReceipt);
}

public void printReceipt() {
    if (vBoxCon == null || vBoxCon.getScene() == null) {
        System.out.println("vBoxCon or Scene is null!");
        return;
    }

    PrinterJob job = PrinterJob.createPrinterJob();
    if (job != null && job.showPrintDialog(vBoxCon.getScene().getWindow())) {
        boolean success = job.printPage(vBoxCon);
        if (success) {
            job.endJob();
            Stage stage = (Stage) vBoxCon.getScene().getWindow();
            stage.close();
        } else {
            System.out.println("Print failed.");
        }
    } else {
        System.out.println("Printer job was null or dialog was cancelled.");
    }

}
}
