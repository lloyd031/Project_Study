package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Run{
	private String refrigerant="";
	private double sysCapacityInTons=0;
	private double sysCapacityInKW=0;
	private double mmsuction=0;
	private double mmliquid=0;
	private double tableCap=0;
	public Run(String ref) {
		this.refrigerant=ref;
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
			if(ref.equals(this.refrigerant+"{")) {
				String evptmp="";
				while(!evptmp.equals(evapTemp+"{") && !evptmp.equals("};"+this.refrigerant+" closing")) {
					evptmp=scanner.nextLine();
				}
				if(evptmp.equals("};"+this.refrigerant+" closing")) {
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
								  this.tableCap=Double.parseDouble(x);
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
			String s=scanner.nextLine();
			if(s.equals(this.refrigerant+"{")) {
				String evptmp="";
				while(!evptmp.equals(evapTemp+"{") && !evptmp.equals("};"+this.refrigerant+" closing")) {
					evptmp=scanner.nextLine();
				}
				if(evptmp.equals("};"+this.refrigerant+" closing")) {
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
double getEquivalentLengthSuctionLine(double line) throws FileNotFoundException {
		
		double rad=0;
		double length=0;
		File table=new File("C:\\Users\\petil\\eclipse-workspace\\SoftwareAutomationPiping\\src\\resources\\table.txt");
		Scanner scanner= new Scanner(table);
		String s="";
		while(!s.equals("eqv-line-for-fitting{")) {
			s=scanner.nextLine();
		}
		while(!s.equals(String.valueOf(line)+"mm{")) {
			s=scanner.nextLine();
		}
		s=scanner.nextLine();
		rad=Double.parseDouble(s);
		length=4+2+rad*2;
		System.out.println(length);
		return length;
		
	}
	double getEquivalentLengthLiquidLine(double line) throws FileNotFoundException {
		double filterDrier=3.45;
		double rad=0;
		double length=0;
		File table=new File("C:\\Users\\petil\\eclipse-workspace\\SoftwareAutomationPiping\\src\\resources\\table.txt");
		Scanner scanner= new Scanner(table);
		String s="";
		while(!s.equals("eqv-line-for-fitting{")) {
			s=scanner.nextLine();
		}
		while(!s.equals(String.valueOf(line)+"mm{")) {
			s=scanner.nextLine();
		}
		s=scanner.nextLine();
		rad=Double.parseDouble(s);
		length=4+2+rad*2+filterDrier;
		System.out.println(length);
		return length;
		
	}
	double getTempDropLiquidLine(double mm, double actualLength, double actualCapacity) throws FileNotFoundException {
		double temp=0;
		File table=new File("C:\\Users\\petil\\eclipse-workspace\\SoftwareAutomationPiping\\src\\resources\\table.txt");
		Scanner scanner= new Scanner(table);
		String s="";
		while(!s.equals(this.refrigerant+"{")) {
			s=scanner.nextLine();
		}
		while(!s.equals("mm{")) {
			s=scanner.nextLine();
		}
		int i=-1;
		while(!s.equals(mm+"")) {
			i++;
			s=scanner.nextLine();
		}
		
		while(!s.equals("temp-drp-liquidln{")) {
			s=scanner.nextLine();
		}
		s=scanner.nextLine();
		for(int j=0; j<i;j++) {
			s=scanner.nextLine();
		}
		temp=(0.02)*(actualLength/30.4878)*Math.pow((actualCapacity/Double.parseDouble(s)), 1.8);
		return temp;
	}
	
	double getTempDropSuctionLine(double mm, double actualLength, double actualCapacity) throws FileNotFoundException {
		double temp=0;
		temp=(0.04)*(actualLength/30.4878)*Math.pow((actualCapacity/this.tableCap), 1.8);
		return temp;
	}
	
	
}
