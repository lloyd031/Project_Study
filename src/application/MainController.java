package application;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Stack;

import javax.swing.JOptionPane;

import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.AmbientLight;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;



public class MainController implements Initializable{
	private boolean compdragged=false;
	private double anchorX, anchorY;
	private double anchorAngleX=0;
	private double anchorAngleY=0;
	private double prevPointY=0;
	private double prevPointXZ=0;
	private double tempX=0;
	private double tempY=0;
	private boolean err=false;
	private final DoubleProperty angleX=new SimpleDoubleProperty(0);
	private final DoubleProperty angleY=new SimpleDoubleProperty(45);
	private DoubleProperty acuX=null;
	private DoubleProperty acuY=null;
	private DoubleProperty compX=null;
	private DoubleProperty compY=null;
    private Group acu, root3D, cmp,mainRoot;
    private PathNode start;
    private PathNode end;
    private PathNode mid;
    private Group flowlinea;
    private double width=50;
    private double height=24;
    private double length=70;
    private int selectedwall2=3;
    private int selectedwall=2;
    private int selectedwall3=0;
    private Box[] n;
    private Box[] n2;
    private PointLight acuL;
    private double acuw=10.5;
    private double acuh=3.2;
    private double acud=2.5;
    private double compw=8.7;
    private double comph=6.5 ;
    private double compd=3.3;
    private double wall=2.032/2;
    private double selectedcomp=0;
    private boolean lockperspective=false;
    private String ref="";
    private PerspectiveCamera camera;
    private double capacity=0;
    private double capacityinkw=0;
    private double refeffect=0;
    private double massflow=0;
    private double pipesizeS=0;
    private double tempdropS=0;
    private double pressdropS=0;
    private double velocityS=0;
    private double equivlengthS=0;
    private double pipesizeL=0;
    private double tempdropL=0;
    private double pressdropL=0;
    private double equivlengthL=0;
    private double velocityL=0;
    private boolean running=false;
	@FXML
    private SubScene threeDModel;
	
	@FXML 
    private BorderPane borderPane;
	@FXML
	private MenuItem btnsave,btnopen,btnbill;
	@FXML 
    private TextField txtsx,txtsz,txtlx,txtlz,txtlength,txtwidth,txtheight,txtacul,txtacut,txtacur,txtacub,txtcompl,txtcompw,txtcompd,txtcompt,txtcompr,txtcomph,txtcompb;
	@FXML 
    private TextField txtevapw,txtevaph,txtevapd,txtdesigntemp,txtevaptemp,txtcondensetemp,lblcap,lblcap2,lblm,lblref;
	@FXML
    private Button btndim,btnevap,btncomp;
	@FXML
	private TitledPane acrdncomploc,acrdnaculoc;
	@FXML
	private Label btnref,runbtn,output1,output2,output3,output4,btnsuctionline,btnsysout,btnliquidline;
	@FXML
	private ComboBox<String> cbboxref;
	
	@FXML
	private ComboBox<String> cbboxwall;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    	cbboxref.setItems(FXCollections.observableArrayList("R-22","R-134a","R-407","R-410"));
    	cbboxwall.setItems(FXCollections.observableArrayList("Wall-1","Wall-2","Wall-3","Wall-4"));
        cbboxwall.getSelectionModel().select(2);
        root3D = new Group();
         n=new Box[4];
         n2=new Box[4];
         camera = new PerspectiveCamera(true);
 		camera.setFarClip(1500);
 		camera.setNearClip(1);
 		camera.translateXProperty().set(-5);
 		camera.translateZProperty().set(-200);
 		camera.translateYProperty().set(-200);
 		camera.setRotationAxis(Rotate.X_AXIS);
        camera.setRotate(-45);
         mainRoot=new Group();
         mainRoot.getChildren().add(root3D);
        
         loadModel(root3D);
         mainRoot.getChildren().addAll(prepareLightSource());
        threeDModel.setRoot( mainRoot);
        // Set camera
       
        
        //camera.translateZProperty().set(-1000);
		threeDModel.widthProperty().bind(borderPane.widthProperty());
		threeDModel.heightProperty().bind(borderPane.heightProperty());
        threeDModel.setFill(Color.valueOf("#212830"));
        
        threeDModel.setCamera(camera);
        initMouseControl(root3D, threeDModel,camera);
        initializeLocationAcu();
        initAcuDragged();
        setAcuLoc();
        setCompLoc();
        drawPipe();
    	btndim.setOnMouseClicked(event -> {
    		renderRoom();
    	});
    	
    	cbboxwall.setOnAction(event -> {
            root3D.getChildren().removeAll(acu,acuL,cmp);
            selectedwall=cbboxwall.getSelectionModel().getSelectedIndex();
            //selectedwall2=selectedwall;
            acu=setAcu();
            cmp=setCompressor();
            drawPipe();
            addAcul(root3D);
            initializeLocationAcu();
            initAcuDragged();
            root3D.getChildren().addAll(acu,cmp);
        });
    	acrdncomploc.setOnMouseClicked(e->{
    		selectedcomp=1;
    	});
    	acrdnaculoc.setOnMouseClicked(e->{
    		selectedcomp=0;
    	});
        
    	initKeyPressed(root3D);
        cbboxref.setOnAction(event -> {
            // Get the selected value
            this.ref = cbboxref.getValue();
        });
        runbtn.setOnMouseClicked(e->{
        	this.running=false;
        	String r=run();
        	if(r.equals("")) {
        		btnsysout.setStyle("-fx-background-color: #3c4454; ");
        		btnsuctionline.setStyle("-fx-background-color:#2d3441 ;");
        		btnliquidline.setStyle("-fx-background-color: #2d3441; ");
        		btnref.setStyle("-fx-background-color:#2d3441 ;");
        		output1.setText("Capacity");
            	output2.setText("");
            	output3.setText("Mass flow rate");
            	output4.setText("Refrigerating effect");
            	lblcap.setText(String.format("%.5f",this.capacity)+" tons");
            	lblcap2.setText(String.format("%.5f",this.capacityinkw)+ " Kw");
            	lblref.setText(String.format("%2f", this.refeffect )+ " KJ/kg");
				lblm.setText(String.format("%2f", this.massflow )+ " kg/s");
        	}else {
        		System.out.println(r);
        	}
        });
        
        btnsysout.setOnMouseClicked(e->{
        	
        	if(this.running==true) {
        		btnsysout.setStyle("-fx-background-color: #3c4454; ");
        		btnsuctionline.setStyle("-fx-background-color:#2d3441 ;");
        		btnliquidline.setStyle("-fx-background-color: #2d3441; ");
        		btnref.setStyle("-fx-background-color:#2d3441 ;");
        		output1.setText("Capacity");
            	output2.setText("");
            	lblcap2.setVisible(true);
            	output3.setText("Mass flow rate");
            	output4.setText("Refrigerating effect");
            	lblcap.setText(String.format("%.5f",this.capacity)+" tons");
            	lblcap2.setText(String.format("%.5f",this.capacityinkw)+ " Kw");
            	lblref.setText(String.format("%2f", this.refeffect )+ " KJ/kg");
				lblm.setText(String.format("%2f", this.massflow )+ " kg/s");
        	}
        });
        btnliquidline.setOnMouseClicked(e->{
        	
        	if(this.running==true) {
        		btnsuctionline.setStyle("-fx-background-color: #2d3441;");
        		btnsysout.setStyle("-fx-background-color:#2d3441;");
        		btnliquidline.setStyle("-fx-background-color:#3c4454 ;");
        		btnref.setStyle("-fx-background-color:#2d3441 ;");
        		output1.setText("Pipe size");
            	output2.setText("Temp. drop");
            	output3.setText("Pressure drop");
            	lblcap2.setVisible(true);
            	output4.setText("Equivalent length");
            	lblcap.setText(String.format("%.5f",this.pipesizeL)+" mm");
            	lblcap2.setText(String.format("%.5f",this.tempdropL)+ " C");
            	lblref.setText(String.format("%2f", this.equivlengthL )+ " m");
				lblm.setText(String.format("%2f",  this.pressdropL)+ " kPa");
        	}
        });
        btnsave.setOnAction(e -> {
        	saveAs();
        });
        btnbill.setOnAction(e->{
        	openNewWindow();
        });
        
        btnopen.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Text File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

            Stage stage = (Stage) txtacul.getScene().getWindow();  // or any other node
            File file = fileChooser.showOpenDialog(stage);

            if (file != null) {
                try {
                    List<String> lines = java.nio.file.Files.readAllLines(file.toPath());

                    // You can now assign the lines to variables or text fields
                    if (lines.size() >= 16) {
                    	this.length=Double.parseDouble(lines.get(0))*10;
                    	this.width=Double.parseDouble(lines.get(1))*10;
                    	this.height=Double.parseDouble(lines.get(2))*10;
                    	txtlength.setText(lines.get(0));
                    	txtwidth.setText(lines.get(1));
                    	txtheight.setText(lines.get(2));
                    	renderRoom();
                    	cbboxref.setValue(lines.get(3));
                    	this.ref=lines.get(3);
                    	this.selectedwall=Integer.parseInt(lines.get(4));
                    	int wall=+Integer.parseInt(lines.get(4))+1;
                    	cbboxwall.setValue("Wall-"+wall);
                    	txtacul.setText(lines.get(5));
                    	txtacur.setText(lines.get(6));
                    	txtacut.setText(lines.get(7));
                    	txtacub.setText(lines.get(8));
                    	txtcompl.setText(lines.get(9));
                    	txtcompr.setText(lines.get(10));
                    	txtcompt.setText(lines.get(11));
                    	txtacub.setText(lines.get(12));
                    	acuX.set(Double.parseDouble(lines.get(13)));
                    	acuY.set(Double.parseDouble(lines.get(14)));
                    	compX.set(Double.parseDouble(lines.get(15)));
                    	compY.set(Double.parseDouble(lines.get(16)));
                    	if(this.selectedwall==2 || this.selectedwall ==3) {
                    		
                			acu.translateXProperty().bind(acuX);
                			cmp.translateXProperty().bind(compX);
                			
                		}else {
	                		acu.translateZProperty().bind(acuX);
	                		cmp.translateZProperty().bind(compX);
	                	}
                    	acu.translateYProperty().bind(acuY);
                    	cmp.translateYProperty().bind(compY);
                    	drawPipe();
                    	txtevaptemp.setText(lines.get(17));
                    	txtcondensetemp.setText(lines.get(18));
                    	txtdesigntemp.setText(lines.get(19));
                    	
                    } else {
                        System.out.println("File does not contain enough lines.");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        btnsuctionline.setOnMouseClicked(e->{
        	
        	if(this.running==true) {
        		btnsuctionline.setStyle("-fx-background-color:#3c4454 ;");
        		btnsysout.setStyle("-fx-background-color: #2d3441;");
        		btnliquidline.setStyle("-fx-background-color:#2d3441;");
        		btnref.setStyle("-fx-background-color:#2d3441 ;");
        		output1.setText("Pipe size");
            	output2.setText("Temp. drop");
            	output3.setText("Pressure drop");
            	output4.setText("Equivalent length");
            	lblcap2.setVisible(true);
            	lblcap.setText(String.format("%.5f",this.pipesizeS)+" mm");
            	lblcap2.setText(String.format("%.5f",this.tempdropS)+ " C");
            	lblref.setText(String.format("%2f", this.equivlengthS )+ " m");
				lblm.setText(String.format("%2f",  this.pressdropS)+ " kPa");
        	}
        });
        
        btnref.setOnMouseClicked(e->{
        	
        	if(this.running==true) {
        		btnsuctionline.setStyle("-fx-background-color: #2d3441;");
        		btnsysout.setStyle("-fx-background-color: #2d3441;");
        		btnliquidline.setStyle("-fx-background-color:#2d3441 ;");
        		btnref.setStyle("-fx-background-color:#3c4454 ;");
        		output1.setText("Type ");
            	output2.setText("");
            	output3.setText("Velocity in suction line");
            	output4.setText("Velocity in liquid line");
            	lblcap.setText((this.ref.equals(""))?"R-22":this.ref);
            	lblcap2.setVisible(false);
            	lblcap2.setText("");
            	lblref.setText(String.format("%2f", this.velocityL )+ " m/s");
				lblm.setText(String.format("%2f",  this.velocityS)+ " m/s");
        	}
        });
        
    }
    private void openNewWindow() {
        try {
        	FXMLLoader loader=new FXMLLoader(getClass().getResource("Billing.fxml"));
            Parent root = loader.load();
            BillingController controller=loader.getController();
            controller.loadCol(equivlengthL,equivlengthS,pipesizeL,pipesizeS);
            Stage newStage = new Stage();
            newStage.setTitle("Billing of Materials");
            newStage.setResizable(false);
            newStage.setScene(new Scene(root));
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void saveAs() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Text File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        Stage stage = (Stage) txtlength.getScene().getWindow();

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            	writer.write(this.length/10 + "\n");
            	writer.write(this.width/10 + "\n");
            	writer.write(this.height/10 + "\n");
            	writer.write(((this.ref.equals(""))?"R-22":this.ref) + "\n");
            	writer.write(this.selectedwall + "\n");
            	writer.write(txtacul.getText() + "\n");
            	writer.write( txtacur.getText() + "\n");;
            	writer.write( txtacut.getText() + "\n");
            	writer.write(txtacub.getText() + "\n");
            	writer.write(txtcompl.getText() + "\n");
            	writer.write(txtcompr.getText() + "\n");
            	writer.write(txtcompt.getText() + "\n");
            	writer.write(txtcompb.getText() + "\n");
            	writer.write(acuX.doubleValue() + "\n");
            	writer.write(acuY.doubleValue() + "\n");
            	writer.write(compX.doubleValue() + "\n");
            	writer.write(compY.doubleValue() + "\n");
            	writer.write(txtevaptemp.getText() + "\n");
            	writer.write(txtcondensetemp.getText() + "\n");
            	writer.write(txtdesigntemp.getText() + "\n");
                System.out.println("Data saved to: " + file.getAbsolutePath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    
    }
    public void renderRoom() {
		width=Double.parseDouble(txtwidth.getText())*10;
		length=Double.parseDouble(txtlength.getText())*10;
		height=Double.parseDouble(txtheight.getText())*10;
		root3D.getChildren().clear();
		loadModel(root3D);
		initializeLocationAcu();
		initAcuDragged();
	}
    private String run() {
    	double evapTemp=Double.parseDouble(txtevaptemp.getText());
    	double condenseTemp=Double.parseDouble(txtcondensetemp.getText());
    	double designTemp=Double.parseDouble(txtdesigntemp.getText());
        System.out.println(evapTemp +" "+ condenseTemp+ " "+ designTemp );
    	Run r=new Run((cbboxref.getValue()==null)?"R-22":this.ref,evapTemp, condenseTemp, designTemp);
    	this.capacity=r.capacityIntons(this.length/10, this.width/10);
    	this.capacityinkw=r.capacityInKW(this.capacity);
    	
    	//double h4=r.getEnthalpyLiquid();
        try {
			String run=r.setSuctionLine();
			if(run.equals("")) {
				this.pipesizeS=r.getSuctionLineSize();
				String enthalpy=r.setEnthalpyLiquid();
				if(enthalpy.equals("")) {
					String liquidline= r.setLiquidLine();
					if(liquidline.equals("")) {
						System.out.println("Liquid line ");
						this.pipesizeL=r.getLiquidLineSize();
						System.out.println("equivalent size for liquid line "+ this.pipesizeL);
						
						try {
							this.equivlengthL= r.getEquivalentLength(this.pipesizeL,0.0254,Double.parseDouble(txtlx.getText()), Double.parseDouble(txtlz.getText()));
							this.tempdropL=r.getTempDropLiquidLine(this.pipesizeL,this.equivlengthL,this.capacityinkw);
							System.out.println("liquidLineLength "+this.equivlengthL);
							String scientificNotation = String.format("%.2e", this.tempdropL);
							String[] parts = scientificNotation.split("e");
							String base = parts[0];
							String exponent = parts[1].replace("+", "");
							System.out.println("tempdrop liquid line  "+base + "x10^" + exponent);
							double pressdropLa=r.getPressureDropLiquid()*(this.tempdropL/0.02);
							
							System.out.println("pressure drop liquid line "+pressdropLa);
							double pressureFromRiser=r.getPressureDropFromRiser(pressdropLa);
							System.out.println("pressure drop from the riser "+pressureFromRiser);
							System.out.println("total pressure drop "+ (pressdropLa + pressureFromRiser));
							this.pressdropL=(pressdropLa + pressureFromRiser);
							System.out.println();
							System.out.println("Suction line ");
							
							System.out.println("equivalent size for suction line "+ this.pipesizeS);
							
								try {
									this.equivlengthS= r.getEquivalentLength(this.pipesizeS,0,Double.parseDouble(txtsx.getText()), Double.parseDouble(txtsz.getText()));
								}catch(Exception e) {
									JOptionPane.showMessageDialog(null, "No Equivalent length for fitting","Error "+this.pipesizeS+" mm",JOptionPane.ERROR_MESSAGE);
									return "";
								}
									System.out.println("suctionLineLength "+this.equivlengthS);
									this.tempdropS=r.getTempDropSuctionLine(this.pipesizeS,this.equivlengthS,this.capacityinkw);
									String scientificNotation1 = String.format("%.2e", this.tempdropS);
									String[] parts1 = scientificNotation1.split("e");
									String base1 = parts1[0];
									String exponent1 = parts1[1].replace("+", "");
									System.out.println("tempdrop suction line  "+ base1 + "x10^" + exponent1);
									
									this.pressdropS=r.getPressureDropSuction()*this.tempdropS/designTemp;
									System.out.println("pressure drop suction line "+this.pressdropS);
									System.out.println(r.getPressureDropSuction()+" x "+this.tempdropS+" / "+designTemp);
									double h1=r.getEnthalpyGas();
									double h4=r.getEnthalpyLiquid();
									this.refeffect=h1-h4;
									System.out.println("Refrigeration effect "+ h1 + " - "+ h4 + " = "+this.refeffect);
									this.massflow=r.getCapacityInKW()/this.refeffect;
									double volGas =r.getSpecVolumeGas();
									this.velocityS=volGas*this.massflow;
									System.out.println("Refrigerant velocity in suction line "+ this.velocityS);
									double volLiquid =r.getSpecVolumeLiquid();
									this.velocityL=volLiquid*this.massflow;
									System.out.println("Refrigerant velocity in liquid line "+ this.velocityL);
									this.running=true;
						}catch(Exception ex) {
							JOptionPane.showMessageDialog(null, "No Equivalent length for fitting","Error "+this.equivlengthL+" mm",JOptionPane.ERROR_MESSAGE);
						} 
						
							
						
						
						
						/**
						 * 
						
						//convert double into scientific notation
						
						
						String scientificNotation1 = String.format("%.2e", suctionTempDrp);
						String[] parts1 = scientificNotation1.split("e");
						String base1 = parts1[0];
						String exponent1 = parts1[1].replace("+", "");
						System.out.println(base1 + "x10^" + exponent1);
						 */
						//System.out.println("temp drop for liquid line "+ liquidTempDrp);
					}else {
						return liquidline;
					}
				}else {
					setDefault();
					this.running=false;
					JOptionPane.showMessageDialog(null, enthalpy,"Error",JOptionPane.ERROR_MESSAGE);
					return enthalpy;
				}
				
			}else {
				setDefault();
				JOptionPane.showMessageDialog(null, run,"Error",JOptionPane.ERROR_MESSAGE);
				return run;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	//fanRotate.start();
       return "";
    }
    public void setDefault() {
    	btnsysout.setStyle("-fx-background-color: #3c4454; ");
		btnsuctionline.setStyle("-fx-background-color:#2d3441 ;");
		btnliquidline.setStyle("-fx-background-color: #2d3441; ");
		btnref.setStyle("-fx-background-color:#2d3441 ;");
		output1.setText("Capacity");
    	output2.setText("");
    	lblcap2.setVisible(true);
    	output3.setText("Mass flow rate");
    	output4.setText("Refrigerating effect");
    	lblcap.setText("");
    	lblcap2.setText("");
    	lblref.setText("");
		lblm.setText("");
    }
    private void initPerspective(Camera cam,Group g) {
    	cam.setRotationAxis(Rotate.X_AXIS);
    	if(lockperspective==false) {
    		double gap=(cam.getTranslateY()-(-200))/10;
    		double ggap=g.getTranslateZ()/10;
    		//cam.translateYProperty().set(-200);
    		AnimationTimer timer= new AnimationTimer() {

				@Override
				public void handle(long arg0) {
					// TODO Auto-generated method stub
					g.translateZProperty().set(g.getTranslateZ()-ggap);
					cam.translateYProperty().set(cam.getTranslateY()-gap);
					cam.setRotate(cam.getRotate()-4.5);
					if(cam.getRotate()<=-45) {
				 		camera.translateYProperty().set(-200);
		    			this.stop();
		    		}
				}
    			
    		};
    		timer.start();
    		
    		
    		
    	}else {
    		double gap= (-height/2 -(-200))/10;
    		double ggap=g.getTranslateY()/10;
    		AnimationTimer timer= new AnimationTimer() {

				@Override
				public void handle(long arg0) {
					// TODO Auto-generated method stub
					g.translateYProperty().set(g.getTranslateY()-ggap);
					cam.translateYProperty().set(cam.getTranslateY()+gap);
					cam.setRotate(cam.getRotate()+4.5);
					if(cam.getTranslateY()>=-height/2 && cam.getRotate()>=0 && g.getTranslateY()>=0) {
		    			this.stop();
		    		}
				}
    			
    		};
    		timer.start();
    		
    		/**
    		 * g.translateYProperty().set(0);
    		cam.translateYProperty().set(-height/2);
    		cam.setRotationAxis(Rotate.X_AXIS);
    		cam.setRotate(0);
    		 */
    	}
    }
    private void prepdragproperty(double x, double y, int sc, double tempx, double tempy) {
    	compdragged=true;
		selectedcomp=sc;
		anchorX=x;
		anchorY=y;
		this.prevPointY=y;
		this.prevPointXZ=x;
		if(sc==0) {
			tempX=acuX.get();
			tempY=acuY.get();
		}else {
			tempX=compX.get();
			tempY=compY.get();
		}
    }
    private void initAcuDragged() {
    	int i=(selectedwall==2 || selectedwall==1)?1:-1;
    	int j=(selectedwall2==2 || selectedwall2==1)?1:-1;
    	acu.setOnMousePressed(e->{
    		prepdragproperty(e.getX(),e.getY(),0,acuX.get(),acuY.get());
    	});
    	
    	cmp.setOnMousePressed(e->{
    		prepdragproperty(e.getX(),e.getY(),1,compX.get(),compY.get());
    		if(c==0) {
        		c++;
        	}else {
        		c=0;
        		this.lockperspective=!this.lockperspective;
        		initPerspective(this.camera,this.root3D);
        	}
    	});
    	if(selectedwall==2 || selectedwall ==3) {
    		
    			acu.translateXProperty().bind(acuX);
    		
    		
    	}else {
    		acu.translateZProperty().bind(acuX);
    	}
    	
    	if(selectedwall2==2 || selectedwall2 ==3) {
    		
			cmp.translateXProperty().bind(compX);
		
		
		}else {
			cmp.translateZProperty().bind(compX);
		}
    	acu.setOnMouseDragged(e->{
    		
        	if(selectedcomp==0) {
        		
            	double newX=tempX-(anchorX-e.getSceneX())/10*i;
            	double newY=tempY-(anchorY-e.getSceneY())/10;
            	double leftBound=(selectedwall>1)?n[1].getTranslateX()+wall/2 + acuw/2:n[2].getTranslateZ()-wall/2 - acuw/2;
            	double rightBound=(selectedwall>1)?n[0].getTranslateX()-wall/2 - acuw/2:n[3].getTranslateZ()+wall/2 + acuw/2;
            	boolean conA=(selectedwall==2)?newX<rightBound:(selectedwall==3)?newX>leftBound:(selectedwall==0)?newX>rightBound:newX<leftBound;
            	boolean conB=(selectedwall==2)?acu.getTranslateX()>leftBound:(selectedwall==3)?acu.getTranslateX()<rightBound:(selectedwall==0)?acu.getTranslateZ()<leftBound:acu.getTranslateZ()>rightBound;
            	boolean conC=(selectedwall==2)?newX>leftBound:(selectedwall==3)?newX<rightBound:(selectedwall==0)?newX<leftBound:newX>rightBound;
            	boolean conD=(selectedwall==2)?acu.getTranslateX()<rightBound:(selectedwall==3)?acu.getTranslateX()>leftBound:(selectedwall==0)?acu.getTranslateZ()>rightBound:acu.getTranslateZ()<leftBound;
            		if(this.prevPointXZ>e.getSceneX() &&conA) {
            			if(conB) {
               			acuX.set(newX);
               		}else {
               			acuX.set((selectedwall==2||selectedwall==0)?leftBound:rightBound);
               		}
            		}else if(this.prevPointXZ<e.getSceneX() && conC) {
            			if(conD) {
               			acuX.set(newX);
               		}else {
               			acuX.set((selectedwall==2||selectedwall==0)?rightBound:leftBound);
               		}
            		}
            		
            	
            	
            	acu.translateYProperty().bind(acuY);
            	//removing the second condition sa main ifs will result a wierd snapping effect sa acu
            			if(this.prevPointY<e.getSceneY() && newY>n[selectedwall].getTranslateY()-height/2+acuh) {
            				if(acu.getTranslateY()<0) {
                        		acuY.set(newY);
            					}
                			else {
                				acuY.set(0);
                			}
            			}else if(this.prevPointY>e.getSceneY() && newY<0) {
            				if(acu.getTranslateY()>n[selectedwall].getTranslateY()-height/2+acuh) {
                        		acuY.set(newY);
            					}else {
            						acuY.set(n[selectedwall].getTranslateY()-height/2+acuh);
            					}
                			
            			}
            			
                		drawPipe();
            			
            	}
        	this.prevPointY=e.getSceneY();	
        	this.prevPointXZ=e.getSceneX();	
        	setAcuLoc();
        	});
    	cmp.setOnMouseDragged(e->{
    		c=0;
    		//double leftBound=(selectedwall>1)?width/2+wall*2:length/-2-wall*2;
    		//double rightBound=(selectedwall>1)?width/-2 - wall*2:length/-2 - wall*2;
        	if(selectedcomp==1) {
        		
            	double newX=tempX+(anchorX-e.getSceneX())/10*j;
            	double newY=tempY-(anchorY-e.getSceneY())/10;
            	cmp.translateYProperty().bind(compY);
            	compX.set(newX);
            	compY.set(newY);
            	setCompLoc();
            	
        		drawPipe();
        	}
        	
        	});
    	
    	
    	acu.setOnMouseReleased(e->{
    		compdragged=false;
    		this.prevPointXZ=0;
    	});
    	cmp.setOnMouseReleased(e->{
    		compdragged=false;
    		this.prevPointXZ=0;
    	});
    	
    	
    }
   
    private void setAcuLoc() {
    	
		
			if(selectedwall > 1) {
    			txtacul.setText(String.format("%.3f",(selectedwall==2)?(width-acuw)/20 + acu.getTranslateX()/10:(width-acuw)/20 - acu.getTranslateX()/10));
    			txtacur.setText(String.format("%.3f",(selectedwall==3)?(width-acuw)/20 + acu.getTranslateX()/10:(width-acuw)/20 - acu.getTranslateX()/10 ));
    		}else {
    			txtacul.setText(String.format("%.3f",(selectedwall==1)?(length-acuw)/20 + acu.getTranslateZ()/10:(length-acuw)/20 - acu.getTranslateZ()/10));
    			txtacur.setText(String.format("%.3f",(selectedwall==0)?(length-acuw)/20 + acu.getTranslateZ()/10:(length-acuw)/20 - acu.getTranslateZ()/10 ));
    		}
    		txtacut.setText(String.format("%.2f",height/10 - Math.abs((acu.getTranslateY()-acuh)/10)));
    		txtacub.setText(String.format("%.2f",Math.abs(acu.getTranslateY()/10)));
    		drawPipe();
		
		/*
		 * double cux=(selectedwall==2 || selectedwall==3)?(acu.getTranslateX()-width/-2-acuw/2)/10:(acu.getTranslateZ()-length/-2-acuw/2)/10;
 	    txtevapx.setText(String.format("%.2f",cux));
 	    double cuy=(((Math.round((acu.getTranslateY()-3.2/2)*10.0)/10.0)-(Math.round(((height+0.75)/-2)*10.0)/10.0)))/10;
 	    
 	    txtevapy.setText(String.format("%.2f", cuy));*/
	
		
	
}
    private void setCompLoc() {
    		if(selectedwall > 1) {
    			txtcompr.setText(String.format("%.3f",(selectedwall==2)?(width+wall*4-compw)/20+cmp.getTranslateX()/10:(width+wall*4-compw)/20-cmp.getTranslateX()/10));
    			txtcompl.setText(String.format("%.3f",(selectedwall==3)?(width+wall*4-compw)/20+cmp.getTranslateX()/10:(width+wall*4-compw)/20-cmp.getTranslateX()/10));
    		}else {
    			txtcompr.setText(String.format("%.3f",(selectedwall==1)?(length+wall*4-compw)/20+cmp.getTranslateZ()/10:(length+wall*4-compw)/20-cmp.getTranslateZ()/10));
    			txtcompl.setText(String.format("%.3f",(selectedwall==0)?(length+wall*4-compw)/20+cmp.getTranslateZ()/10:(length+wall*4-compw)/20-cmp.getTranslateZ()/10));
    		}
			txtcompt.setText(String.format("%.2f",height/10 - Math.abs((cmp.getTranslateY()-comph)/10)));
    		txtcompb.setText(String.format("%.2f",Math.abs(cmp.getTranslateY()/10)));
}
    private void initializeLocationAcu()
    {
    	this.acuX=new SimpleDoubleProperty(0);
    	this.acuY=new SimpleDoubleProperty((height)/-2+acuh/2);
    	this.compX=new SimpleDoubleProperty(0);
    	this.compY=new SimpleDoubleProperty((height)/-2+comph/2);
    	setAcuLoc();
    	setCompLoc();
    }
    private void initlocation(DoubleProperty a,double bound, String val, int mul) {
    	try{a.set( bound + Double.parseDouble(val)*mul);
    	updateLoc();
    	drawPipe();
    	}catch(Exception x) {
    		updateLoc();
		   }
    	}
    private void updateLoc() {
		if(selectedcomp==0) {
		    		setAcuLoc();
		   }else {
		    		setCompLoc();
		   }
    }
    private void loadModel(Group root3D) {
    	root3D.getChildren().addAll(drawRoom(width,height,length));
       
        acu=setAcu();
        cmp=setCompressor();
       
        flowlinea=new Group();
        addAcul(root3D);
        PointLight pLight=new PointLight();
		pLight.setColor(Color.WHITE);
		pLight.getTransforms().add(new Translate(0,height*-1-7,0));
        root3D.getChildren().addAll(acu,cmp,flowlinea,pLight);
    }
    private void addAcul(Group root) {
    	int i=(selectedwall==2 || selectedwall==1)?1:-1;
    	root.getChildren().remove(acuL);
    	acuL=new PointLight();
        acuL.setColor(Color.valueOf("#001217"));
 		Node a=this.n[selectedwall];
 		if(selectedwall==2 || selectedwall ==3) {
 			acuL.getTransforms().add(new Translate(a.getTranslateX()+3.5*i,a.getTranslateY(),a.getTranslateZ()));
 		}else {
 			acuL.getTransforms().add(new Translate(a.getTranslateX(),a.getTranslateY(),a.getTranslateZ()+3.5*i));
 		}
 		root.getChildren().add(acuL);
    }
   private Group setAcu() {
    	int i=selectedwall;
    	Node wall=this.n[i];
    	int z=(i==2 || i==0)?-1:1;
    	int deg=(i==2)?0:(i==3)?180:(i==0)?90:270;
 		PhongMaterial material = new PhongMaterial();
 		//this.floor.getTranslateY()-0.75-(acuh+1)/2
        material.setDiffuseColor(Color.valueOf("#b0b2b4"));
        Box box=drawBox(0,acuw,acuh-1,1,0,-(acuh/2),-(acud)/2 + 0.5);
	    Cylinder c=drawCylinder(0,acuw,box.getTranslateY()-box.getHeight()/2,box.getTranslateZ(),90);
		Cylinder c1=drawCylinder(0,acuw,box.getTranslateY()+box.getHeight()/2,box.getTranslateZ(),90);
		Box box1=drawBox(0,acuw,0.3,acud-0.5,0,(box.getTranslateY()-box.getHeight()/2-0.5)+0.3/2,box.getTranslateZ()+(acud-0.5)/2);
		Box box2=drawBox(0,acuw,0.3,acud-0.5,0,(box.getTranslateY()+box.getHeight()/2+0.5)-0.3/2,box.getTranslateZ()+(acud-0.5)/2);
		Box box3=drawBox(0,0.3,acuh,acud-0.5,(box.getTranslateX()-box.getWidth()/2)+0.3/2,box.getTranslateY(),box.getTranslateZ()+(acud-0.5)/2);
		Box box4=drawBox(0,0.3,acuh,acud-0.5,(box.getTranslateX()+box.getWidth()/2)-0.3/2,box.getTranslateY(),box.getTranslateZ()+(acud-0.5)/2);
		Image icon=new Image("/resources/power-on .png");
	    ImageView img=new ImageView(icon);
	    img.setFitWidth(0.5);
	    img.setFitHeight(0.5);
	    img.translateYProperty().set(box.getTranslateY()-box.getHeight()/3);
	    img.translateXProperty().set(box.getTranslateX()+box.getWidth()/3);
	    img.translateZProperty().set(box.getTranslateZ()-0.54);
	    PhongMaterial m = new PhongMaterial();
        m.setDiffuseColor(Color.valueOf("#2e4357"));
        m.setSpecularColor(Color.WHITE);
	    Box fl=new Box(acuw-1.5,0.3,1.1);
	    fl.translateZProperty().set(box.getTranslateZ());
	    fl.translateYProperty().set(box.getTranslateY()+(box.getHeight()/2));
	    fl.setMaterial(m);
	    Group acu=new Group();
	    acu.getChildren().addAll(box,c,c1,box1,box2,box3,box4,img,fl);
	    acu.setRotationAxis(Rotate.Y_AXIS);
	    acu.setRotate(deg);
        if(i==2 || i==3) {
	    	acu.translateZProperty().set(wall.getTranslateZ()+((acud+this.wall)/2)*z);
	    }else if(i==0 || i ==1) {
	    	acu.translateXProperty().set(wall.getTranslateX()+((acud+this.wall)/2)*z);
	    }
	    acu.translateYProperty().set((height)/-2 + (acuh)/2);
	    box3.setOnMouseClicked(e->{
	    	if(this.start==null) {
	    		
	    		
	    	}
	    });
	    return  acu;
    }
   	private Group setCompressor() {
    	int i=selectedwall2;
    	Node wall=this.n2[i];
    	int z=(i==2 || i==0)?-1:1;
    	int deg=(i==2)?180:(i==3)?0:(i==0)?270:90;
        Box box=drawBox(1,compw,comph-1,1,0,-(comph/2),-compd/2 + 0.5);
        Cylinder c=drawCylinder(1,compw,box.getTranslateY()-box.getHeight()/2,box.getTranslateZ(),90);
		Cylinder c1=drawCylinder(1,compw,box.getTranslateY()+box.getHeight()/2,box.getTranslateZ(),90);
		Box box1=drawBox(1,compw,0.3,compd-0.5,0,(box.getTranslateY()-box.getHeight()/2-0.5)+0.3/2,box.getTranslateZ()+(compd-0.5)/2);
		Box box2=drawBox(1,compw,0.3,compd-0.5,0,(box.getTranslateY()+box.getHeight()/2+0.5)-0.3/2,box.getTranslateZ()+(compd-0.5)/2);
		Box box3=drawBox(0,0.3,comph-0.5,compd-0.5,(box.getTranslateX()-box.getWidth()/2)+0.3,box.getTranslateY(),box.getTranslateZ()+(compd-0.5)/2);
		Box box4=drawBox(1,0.3,comph,compd-0.5,(box.getTranslateX()+box.getWidth()/2)-0.3/2,box.getTranslateY(),box.getTranslateZ()+(compd-0.5)/2);
		Box box5=drawBox(1,0.3,comph-0.5,0.5,(box.getTranslateX()-box.getWidth()/2)+0.3/2,box.getTranslateY(),box.getTranslateZ()+(compd-0.5)-0.25);
		Box box6=drawBox(1,0.3,comph-0.5,0.5,(box.getTranslateX()-box.getWidth()/2)+0.3/2,box.getTranslateY(),box.getTranslateZ()+(compd-0.5)/2);
		Cylinder f=new Cylinder(2.5,1);
		f.translateYProperty().set(box.getTranslateY());
		f.translateXProperty().set(-1.5/2);
		f.setRotationAxis(Rotate.X_AXIS);
		f.setRotate(90);
		f.translateZProperty().set(box.getTranslateZ()-0.05);
		PhongMaterial m = new PhongMaterial();
		m.setDiffuseMap(new Image(getClass().getResourceAsStream("/resources/fan.png")));
		m.setDiffuseColor(Color.WHITE);
	    f.setMaterial(m);
		Group compressor=new Group();
	    compressor.getChildren().addAll(box,c,c1,box1,box2,box3,box4,box5,box6,f);
	    compressor.setRotationAxis(Rotate.Y_AXIS);
	    compressor.setRotate(deg);
	    
	    if(i==2 || i==3) {
	    	compressor.translateZProperty().set(wall.getTranslateZ()-(compd+this.wall)/2*z - 0.984252*z) ;
	    	
	    }else if(i==0 || i ==1) {
	    	
	    	compressor.translateXProperty().set(wall.getTranslateX()-(compd+this.wall)/2*z - 0.984252*z);
	    	
	    }
	    compressor.translateYProperty().set((height)/-2 + acuh);
	    //System.out.println(compressor.getTranslateX());
	    return  compressor;
    }
   	private void drawPipe() {
   		flowlinea.getChildren().clear();
   		 //compd/2*-mul2+0.984252/2*-mul2
   		
   		for(int c=0; c<2; c++) {
   			for(int i=0; i<2; i++) {
   				int mul=(selectedwall==2|| selectedwall==0)?1:-1;
   				int mul2=(selectedwall2==2|| selectedwall2==0)?1:-1;
   				int mul3=(selectedwall3==2|| selectedwall3==0)?1:-1;
   	   			double x1=(selectedwall2>1)?cmp.getTranslateX()+compw/2*-mul2+0.25*-mul2:cmp.getTranslateX()+compd/2*-mul2;
   	   			double z1=(selectedwall2>1)?cmp.getTranslateZ()+compd/2*-mul2:cmp.getTranslateZ()+compw/2*mul2+0.25*mul2;
   	   			double x2=(selectedwall>1)?acu.getTranslateX()-acuw/2*-mul+0.5*-mul:n[selectedwall].getTranslateX()+this.wall*2*mul-0.25*mul;
   	   			double z2=(selectedwall>1)?n[selectedwall].getTranslateZ()+this.wall*2*mul-0.25*mul:acu.getTranslateZ()-acuw/2*mul;
   	   			double x3=n[selectedwall3].getTranslateX()+this.wall*2*mul3;
	   			double z3=n[selectedwall3].getTranslateZ()+this.wall*2*mul3;
   	   			
	   			double y1=cmp.getTranslateY();
   	   			double y2=acu.getTranslateY();
   	   			this.start=new PathNode(x1,y1-((i==0)?(y1>=y2)?1:2:(y1<=y2)?1:2),z1);
	   	   		this.end=new PathNode((selectedwall>1)?((i==0)?(x1>=x2)?x2-1:x2:(x1<=x2)?x2-1:x2):x2,y2-1,(selectedwall>1)?z2:((i==0)?(z1>=z2)?z2-1:z2:(z1<=z2)?z2-1:z2));
	   	   		this.mid=new PathNode((selectedwall3>1)?((i==0)?(x1>=x3)?x3-1:x3:(x1<=x3)?x3-1:x3):x3,y1-((i==0)?(y1>=y2)?1:2:(y1<=y2)?1:2),(selectedwall3>1)?z3:((i==0)?(z1>=z3)?z3-1:z3:(z1<=z3)?z3-1:z3));
	   	   			
   	   			
   	   			Pipe p=new Pipe(this.start, this.end,width,length);
   	   			if(selectedwall!= selectedwall2 &&((selectedwall==0 && selectedwall2==1) || (selectedwall==1 && selectedwall2==0) || (selectedwall==2 && selectedwall2==3) || (selectedwall==3 && selectedwall2==2))) {
   	   				selectedwall3=((selectedwall==0 && selectedwall2==1) || (selectedwall==1 && selectedwall2==0))?2:0;
   	   				if(c==0) {
   	   				p.setselectedwall(selectedwall, selectedwall3);
   	   	   			p.BFS(start, mid);
   	   				}else {
   	   				p.setselectedwall(selectedwall, selectedwall2);
   	   	   			p.BFS(mid,end);
   	   				}
   	   			}else {
   	   				p.setselectedwall(selectedwall, selectedwall2);
	   	   			p.BFS(start,end);
	   	   			c=2;
   	   			}
   	   			Stack<PathNode> k=p.getPath();
   	   			PathNode path[]=new PathNode[p.getPath().size()];
   	   			for(int j=0; j<path.length; j++) {
   	   					path[j]=k.pop();
   	   				}
   	   			
   	   			for(int j=path.length-1; j>0; j--) {
   	   				if(j!=0) {
   	   					if(path[j].getZ()<path[j-1].getZ() || path[j].getZ()>path[j-1].getZ()){
   	   						double w=path[j-1].getZ()-path[j].getZ();
   	   						Cylinder pathpoint=createpn(Math.abs(w),path[j].getX(),path[j].getY(), path[j].getZ()+w/2,90);
   	   		    			pathpoint.setRotationAxis(Rotate.X_AXIS);
   	   		    			this.flowlinea.getChildren().add(pathpoint);// System.out.println(path[j].getX()+"------ "+path[j].getY()+"------ "+path[j].getZ());
   	   					}
   	   					if(path[j].getY()>path[j-1].getY() || path[j].getY()<path[j-1].getY()){
   	   						double w=path[j].getY()-path[j-1].getY();
   	   						Cylinder pathpoint=createpn(Math.abs(w),path[j].getX(),path[j].getY()-w/2, path[j].getZ(),0);
   	   		    			this.flowlinea.getChildren().add(pathpoint);// System.out.println(path[j].getX()+"------ "+path[j].getY()+"------ "+path[j].getZ());
   	   					}
   	   					if(path[j].getX()>path[j-1].getX() || path[j].getX()<path[j-1].getX()){
   	   						double w=path[j].getX()-path[j-1].getX();
   	   						Cylinder pathpoint=createpn(Math.abs(w),path[j].getX()-w/2,path[j].getY(), path[j].getZ(),90);
   	   						pathpoint.setRotationAxis(Rotate.Z_AXIS);
   	   		    			this.flowlinea.getChildren().add(pathpoint);// System.out.println(path[j].getX()+"------ "+path[j].getY()+"------ "+path[j].getZ());
   	   					}
   	   				}
   	   				
   	   				/**
   	   				 * 
   	   					Cylinder pathpoint=createpn(0.984252,start.getX(),start.getY(), start.getZ()-wall,90);
   	   		   			pathpoint.setRotationAxis(Rotate.X_AXIS);
   	   					this.flowlinea.getChildren().add(pathpoint);
   	   				 */
   	   				
   	   			}
   	   			
   	   		 
   	   		}
   		}
		
		
	
   	}
   	public Cylinder createpn(double w, double x, double y, double z, int deg) {
   		PhongMaterial material = new PhongMaterial();
		//material.setSelfIlluminationMap(new Image(getClass().getResourceAsStream("/resources/pipe.png")));
   		material.setDiffuseColor(Color.color(1, 1, 1,0.6));
   		material.setSpecularColor(Color.WHITE);
		Cylinder pathpoint=new Cylinder(0.25,w);
		pathpoint.translateXProperty().set(x);
		pathpoint.translateYProperty().set(y);
		pathpoint.translateZProperty().set(z);
		pathpoint.setRotate(deg);
		pathpoint.setMaterial(material);
   		return pathpoint;
   	}
	private Box drawBox(int comp, double w, double h, double d, double x, double y, double z) {
		PhongMaterial material = new PhongMaterial();
		Box b=new Box(w,h,d);
		b.translateZProperty().set(z);
	    b.translateYProperty().set(y);
	    b.translateXProperty().set(x);
	    if(comp==1) {
			material.setSelfIlluminationMap(new Image(getClass().getResourceAsStream("/resources/black1.jpg")));
		}else if(comp==2) {
			material.setDiffuseColor(Color.valueOf("#888989"));
		}else{
			material.setDiffuseColor(Color.valueOf("#b0b2b4"));
		}
	    b.setMaterial(material);
		return b;
	}
    private Cylinder drawCylinder(int comp, double w, double y, double z, double deg) {
    	PhongMaterial material = new PhongMaterial();
    	Cylinder c=new Cylinder(0.5,w);
    	c.translateYProperty().set(y);
    	c.translateZProperty().set(z);
    	c.setRotationAxis(Rotate.X_AXIS);
	    c.setRotationAxis(Rotate.Z_AXIS);
		c.setRotate(deg);
		if(comp==1) {
			material.setSelfIlluminationMap(new Image(getClass().getResourceAsStream("/resources/black1.jpg")));
		}else {
			material.setDiffuseColor(Color.valueOf("#b0b2b4"));
		}
		c.setMaterial(material);
    	return c;
    }
    
  
    int c=0;
    private Node[] drawRoom(double roomWidth, double roomHieght, double roomLength) {
    	//whlxyz
    	
    	
    	Box platform= prepareBox(roomWidth+10,roomLength+10,2,0,1.1,0,0);
		Box floor= prepareBox(roomWidth,roomLength,1.5,0,0.75,0,1);
		Box wall1= prepareBox(wall,roomLength+wall*2,roomHieght,(roomWidth/2)+wall/2,roomHieght/2*-1,0,0);
		Box wall2= prepareBox(wall,roomLength+wall*2,roomHieght,(roomWidth/2*-1)-wall/2,roomHieght/2*-1,0,0);
		Box wall3= prepareBox(roomWidth,wall,roomHieght,0,roomHieght/2*-1,roomLength/2+wall/2,0);
		Box wall4= prepareBox(roomWidth,wall,roomHieght,0,roomHieght/2*-1,roomLength/-2-wall/2,0);
		Box wall5= prepareBox(wall,roomLength+wall*4,roomHieght,wall1.getTranslateX()+wall,roomHieght/2*-1,0,2);
		Box wall6= prepareBox(wall,roomLength+wall*4,roomHieght,wall2.getTranslateX()-wall,roomHieght/2*-1,0,2);
		Box wall7= prepareBox(roomWidth+wall*4,wall,roomHieght,0,roomHieght/2*-1,wall3.getTranslateZ()+wall,2);
		Box wall8= prepareBox(roomWidth+wall*4,wall,roomHieght,0,roomHieght/2*-1,wall4.getTranslateZ()-wall,2);
		Box wall9= prepareBox(wall,roomLength+wall*2,0.1,(roomWidth/2)+wall/2,roomHieght*-1,0,2);
		Box wall10= prepareBox(wall,roomLength+wall*2,0.1,(roomWidth/2*-1)-wall/2,roomHieght*-1,0,2);
		Box wall11=prepareBox(roomWidth,wall,0.1,0,roomHieght*-1,roomLength/2+wall/2,2);
		Box wall12= prepareBox(roomWidth,wall,0.1,0,roomHieght*-1,roomLength/-2-wall/2,2);
		
		Box[] wall= { wall1,wall2,wall3,wall4,wall5,wall6,wall7,wall8,wall9,wall10,wall11,wall12,platform,floor};
		for(int i=0; i<4; i++) {
			n[i]=wall[i];
			n2[i]=wall[i+4];
		}
		
        
		return wall;
	}
    private Box prepareBox(double width, double height, double length,double x, double y, double z, int floor) {
		PhongMaterial material = new PhongMaterial();
	if(floor==1) {
			material.setDiffuseMap(new Image(getClass().getResourceAsStream("/resources/wood.jpg")));
			material.setSpecularColor(Color.WHITE);
		}else if(floor==2)
		{
		 material.setSelfIlluminationMap(new Image(getClass().getResourceAsStream("/resources/gray.jpg")));
		 
		}else {
			material.setDiffuseColor(Color.GREY);
			//material.setSelfIlluminationMap(new Image(getClass().getResourceAsStream("/resources/white.jpg")));
		}
		
		
		
		Box box= new Box(width,length, height);
		box.translateYProperty().set(y);
		box.translateXProperty().set(x);
		box.translateZProperty().set(z);
		box.setMaterial(material);
		
		return box;
	}
	private Node[] prepareLightSource() {
		AmbientLight amLight= new AmbientLight();
		amLight.setColor(Color.valueOf("#373737"));
		PointLight pLight1=new PointLight();
		pLight1.setColor(Color.valueOf("#252325"));
		pLight1.getTransforms().add(new Translate(camera.getTranslateX(),cmp.getTranslateY(),camera.getTranslateZ()));
		return new Node[] {amLight,pLight1};
	}
	private void initMouseControl(Group group, SubScene scene,Camera cam) {
		Rotate rotateX;
		Rotate rotateY;
		rotateY=new Rotate(0,Rotate.Y_AXIS);
		 rotateX=new Rotate(0, Rotate.X_AXIS);
		scene.setOnMousePressed(event ->{
			anchorX=event.getSceneX();
			anchorY=event.getSceneY();
			anchorAngleX=angleX.get();
			anchorAngleY=angleY.get();
			
		});
		
		scene.setOnMouseDragged(event -> {
			c=0;
			if(compdragged==false) {
				angleX.set(anchorAngleX-(anchorY-event.getSceneY()));
				angleY.set(anchorAngleY+(anchorX-event.getSceneX()));
			}
		});
		rotateY.angleProperty().bind(angleY);
		//rotateX.angleProperty().bind(angleX);
		group.getTransforms().addAll(rotateY,rotateX);
		
		scene.addEventHandler(ScrollEvent.SCROLL, event ->{
			double movement=event.getDeltaY();
			group.translateZProperty().set(group.getTranslateZ()+movement/2*-1);
			if(lockperspective==false) {
				group.translateYProperty().set(group.getTranslateY()+movement/2*-1);
			}
		});
		
		
		
	}
	private void initKeyPressed(Group g) {
		txtacul.setOnKeyPressed(e->{
	    	   double boundL=(selectedwall==2)?(width-acuw)/-2:(selectedwall==3)?(width-acuw)/2:(selectedwall==0)?(length-acuw)/2:(length-acuw)/-2;
	    	   int mul=(selectedwall==2 || selectedwall==1)?10:-10;
	    	   switch(e.getCode()) {
	     	   case ENTER:
	     		   if(txtacul.getText().equals("center")) {
	     			  acuX.set(0);
	     			  setAcuLoc();
	     			  
	     		   }else {
	     			initlocation(acuX, boundL,txtacul.getText(),mul);
	     			 }
	     		   break;
	     	    default:
	     	    	break;
	     	   }
	        });
	     txtacur.setOnKeyPressed(e->{
	    	 	double boundR=(selectedwall==2)?(width-acuw)/2:(selectedwall==3)?(width-acuw)/-2:(selectedwall==0)?(length-acuw)/-2:(length-acuw)/2;
	    	 	int mul=(selectedwall==2 || selectedwall==1)?-10:10;
	    	 	switch(e.getCode()) {
	     	   case ENTER:
	     		  if(txtacur.getText().equals("center"))  {
	     			  acuX.set(0);
	     			  setAcuLoc();
	     		  }else {
	     				initlocation(acuX, boundR,txtacur.getText(),mul);
	     			}
	     		   
	     		   break;
	     	    default:
	     	    	break;
	     	   }
	        });
	     txtacub.setOnKeyPressed(e->{
	    		switch(e.getCode()) {
	        	   case ENTER:
	        		   
	        		   if(txtacub.getText().equals("center"))  {
	        			   acu.translateYProperty().bind(acuY);
	        			   acuY.set((height)/-2+acuh/2);
	        			   setAcuLoc();
	        		   }else {
	        			   acu.translateYProperty().bind(acuY);
	        			   initlocation(acuY, 0,txtacub.getText(),-10);
	        				}
	        		 break;
	        	    default:
	        	    	break;
	        	   }
	      	   
	         });
	    	
	    	txtacut.setOnKeyPressed(e->{
		      	 switch(e.getCode()) {
	      	   case ENTER:
	      		 if(txtacut.getText().equals("center")) {
	      			acu.translateYProperty().bind(acuY);
	         		 acuY.set((height)/-2+acuh/2);
	         		 setAcuLoc();
	      		 }else {
	      			 	 acu.translateYProperty().bind(acuY);
	        		     initlocation(acuY, -height + acuh,txtacut.getText(),10);
	      				 }
	      		 break;
	      	    default:
	      	    	break;
	      	   
	      	   }
	         });
	    	
	    	txtevapw.setOnKeyPressed(e->{
			      switch(e.getCode()) {
		      	   case ENTER:
		      		 
		      			 try {
		      				 
		      				 this.acuw=Double.parseDouble(txtevapw.getText())*10;
		      				 g.getChildren().remove(this.acu);
		      				 this.acu=setAcu();
		      				 g.getChildren().add(this.acu);
		      				initAcuDragged();
		      			 }
		      			     
		      			  catch(Exception x) {
		        		    	setAcuLoc();
		        		    };
		      			break;
		      	    default:
		      	    	break;
		      	   }
		         });
		    	txtevaph.setOnKeyPressed(e->{
			      	 switch(e.getCode()) {
		      	   case ENTER:
		      		 try {
		      				 this.acuh=Double.parseDouble(txtevaph.getText())*10;
		      				 g.getChildren().remove(this.acu);
		      				 this.acu=setAcu();
		      				 g.getChildren().add(this.acu);
		      				initAcuDragged();
		      			 }
		      			catch(Exception x) {
		        		    	setAcuLoc();
		        		    };
		      		break;
		      	    default:
		      	    	break;
		      	   
		      	   }
		         });
		    	
		    	txtevapd.setOnKeyPressed(e->{
		      		 switch(e.getCode()) {
		      	   case ENTER:
		      		 try {
		      				 this.acud=Double.parseDouble(txtevapd.getText())*10;
		      				 g.getChildren().remove(this.acu);
		      				 this.acu=setAcu();
		      				 g.getChildren().add(this.acu);
		      				initAcuDragged();
		      			 }
		      			catch(Exception x) {
		        		    	setAcuLoc();
		        		    };
		      		   break;
		      	    default:
		      	    	break;
		      	   }
		         });
		    	
		    	
		    	txtcompw.setOnKeyPressed(e->{
				      switch(e.getCode()) {
			      	   case ENTER:
			      		 
			      			 try {
			      				 this.compw=Double.parseDouble(txtcompw.getText())*10;
			      				 g.getChildren().remove(this.cmp);
			      				 this.cmp=setCompressor();
			      				 g.getChildren().add(this.cmp);
			      				initAcuDragged();
			      			 }
			      			  catch(Exception x) {
			      				setCompLoc();
			        		    };
			      			break;
			      	    default:
			      	    	break;
			      	   }
			         });
			    	txtcomph.setOnKeyPressed(e->{
				      	 switch(e.getCode()) {
			      	   case ENTER:
			      		 try {
			      				 this.comph=Double.parseDouble(txtcomph.getText())*10;
			      				 g.getChildren().remove(this.cmp);
			      				 this.cmp=setCompressor();
			      				 g.getChildren().add(this.cmp);
			      				initAcuDragged();
			      			 }
			      			catch(Exception x) {
			      				setCompLoc();
			        		    };
			      		break;
			      	    default:
			      	    	break;
			      	   }
			         });
			    	
			    	txtcompd.setOnKeyPressed(e->{
			      		 switch(e.getCode()) {
			      	   case ENTER:
			      		 try {
			      				 this.compd=Double.parseDouble(txtcompd.getText())*10;
			      				 g.getChildren().remove(this.cmp);
			      				 this.cmp=setCompressor();
			      				 g.getChildren().add(this.cmp);
			      				initAcuDragged();
			      			 }
			      			catch(Exception x) {
			      				setCompLoc();
			        		    };
			      		   break;
			      	    default:
			      	    	break;
			      	   }
			         });
			    	txtcompb.setOnKeyPressed(e->{
			    		switch(e.getCode()) {
			        	   case ENTER:
			        		   
			        		   if(txtcompb.getText().equals("center"))  {
			        			   cmp.translateYProperty().bind(compY);
			        			   compY.set((height)/-2+comph/2);
			        			   setCompLoc();
			        		   }else {
			        			   cmp.translateYProperty().bind(compY);
			        			   initlocation(compY, 0,txtcompb.getText(),-10);
			        			}
			        		 break;
			        	    default:
			        	    	break;
			        	   }
			      	   
			         });
			    	
			    	txtcompt.setOnKeyPressed(e->{
				      	 switch(e.getCode()) {
			      	   case ENTER:
			      		 if(txtcompt.getText().equals("center")) {
			      			cmp.translateYProperty().bind(compY);
			      			compY.set((height)/-2+comph/2);
			         		 setCompLoc();
			      		 }else {
			      			 cmp.translateYProperty().bind(compY);
			        		 initlocation(compY, -height + comph,txtcompt.getText(),10);
			      			 }
			      		 break;
			      	    default:
			      	    	break;
			      	   }
			         });
			    	txtcompl.setOnKeyPressed(e->{
				    	   double boundL=(selectedwall==2)?(width+wall*4-compw)/2:(selectedwall==3)?(width+wall*4-compw)/-2:(selectedwall==0)?(length+wall*4-compw)/-2:(length+wall*4-compw)/2;
				     	   int mul=(selectedwall==2 || selectedwall==1)?-10:10;
				    	   switch(e.getCode()) {
				     	   case ENTER:
				     		   if(txtcompl.getText().equals("center")) {
				     			  compX.set(0);
				     			  setCompLoc();
				     		   }else {
				     			  initlocation(compX, boundL,txtcompl.getText(),mul);
				     		   }
				     		   break;
				     	    default:
				     	    	break;
				     	   }
				        });
			    	
			    	txtcompr.setOnKeyPressed(e->{
				    	   double boundR=(selectedwall==2)?(width+wall*4-compw)/-2:(selectedwall==3)?(width+wall*4-compw)/2:(selectedwall==0)?(length+wall*4-compw)/2:(length+wall*4-compw)/-2;
				    	   int mul=(selectedwall==2 || selectedwall==1)?10:-10;
				    	   switch(e.getCode()) {
				     	   case ENTER:
				     		   if(txtcompr.getText().equals("center")) {
				     			  compX.set(0);
				     			  setCompLoc();
				     		   }else {
				     			   initlocation(compX, boundR,txtcompr.getText(),mul);
				     			}
				     		   break;
				     	    default:
				     	    	break;
				     	   }
				        });
			    	
			    	
	}
}
