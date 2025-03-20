package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Run{
	private String ref="";
	private double sysCapacityInTons=0;
	private double sysCapacityInKW=0;
	private double mmsuction=0;
	private double mmliquid=0;
	public Run(String ref) {
		this.ref=ref;
	}
	public double capacityIntons(double l, double w) {
		   sysCapacityInTons = (l*w*500)/12000;
		   return sysCapacityInTons;
	   }
	public double capacityInKW(double val) {
		   sysCapacityInKW = val*(211)*1/60;
		   return sysCapacityInKW;
	   }
	public String setSuctionLine(double evapTemp, double condenseTemp, double designTemp) throws FileNotFoundException {
		String msg="";
		File table=new File("C:\\Users\\petil\\eclipse-workspace\\SoftwareAutomationPiping\\src\\resources\\table.txt");
		Scanner scanner= new Scanner(table);
		while(scanner.hasNextLine()) {
			String ref=scanner.nextLine();
			if(ref.equals(this.ref+"{")) {
				String evptmp="";
				while(!evptmp.equals(evapTemp+"{") && !evptmp.equals("};"+this.ref+" closing")) {
					evptmp=scanner.nextLine();
				}
				if(evptmp.equals("};"+this.ref+" closing")) {
					msg="invalid evaporator temperature value";
				}else {
					int index=0;
					boolean inloop=true;
					while(inloop==true) {
						String x=scanner.nextLine();
						if (x.equals("liquid{")) {
							 msg="no record found";
							  inloop=false;
						  }else {
							  if(sysCapacityInKW<Double.parseDouble(x) || sysCapacityInKW==Double.parseDouble(x)) {
								  inloop=false;
								  String y="";
								  while(!y.equals("mm{")) {
									  y=scanner.nextLine();
								  }
								  y=scanner.nextLine();
								  for(int i=0; i<index; i++) {
									  y=scanner.nextLine();
								  }
								  mmsuction=Double.parseDouble(y);
							  }
						  }
						index++;
					}
					
				}
			}
		}
	return msg;
	}
	String setLiquidLine(double evapTemp, double condenseTemp, double designTemp) throws FileNotFoundException {
		String msg="";
		File table=new File("C:\\Users\\petil\\eclipse-workspace\\SoftwareAutomationPiping\\src\\resources\\table.txt");
		Scanner scanner= new Scanner(table);
		while(scanner.hasNextLine()) {
			String ref=scanner.nextLine();
			if(ref.equals(this.ref+"{")) {
				String evptmp="";
				while(!evptmp.equals(evapTemp+"{") && !evptmp.equals("};"+this.ref+" closing")) {
					evptmp=scanner.nextLine();
				}
				if(evptmp.equals("};"+this.ref+" closing")) {
					msg="invalid evaporator temperature value";
				}else {
					String x=scanner.nextLine();
					while(!x.equals("liquid{")) {
						x=scanner.nextLine();
					}
					int index=0;
					boolean inloop=true;
					while(inloop==true) {
						x=scanner.nextLine();
						if(sysCapacityInKW<Double.parseDouble(x)  || sysCapacityInKW<Double.parseDouble(x)) {
							inloop=false;
							}
						if(x.equals("mm{")) {
							inloop=false;
						}
						index++;
					}
					while(!x.equals("mm{")) {
						x=scanner.nextLine();
					}
					for(int i=0; i<index; i++) {
						mmliquid=Double.parseDouble(scanner.nextLine());
					}
				}
			}
		}
		
	return msg;	
	}
	double getSuctionLineSize() {
		return this.mmsuction;
	}
	double getLiquidLineSize() {
		return this.mmliquid;
	}
}
