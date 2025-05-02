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
	private double evapTemp=0; 
	private double condenseTemp=0;
	private double designTemp=0;
	private double h4=0;
	File table = new File("src/resources/table.txt");
	//File table=new File("C:\\Users\\petil\\eclipse-workspace\\SoftwareAutomationPiping\\src\\resources\\table.txt");
	File saturationtable = new File("src/resources/saturationtable.csv");
	public Run(String ref, double evapTemp, double condenseTemp, double designTemp) {
		this.refrigerant=ref;
		this.condenseTemp=condenseTemp;
		this.evapTemp=evapTemp;
		this.designTemp=designTemp;
	}
	public double capacityIntons(double l, double w) {
		   sysCapacityInTons = (l*w*500)/12000;
		   return sysCapacityInTons;
	   }
	public double capacityInKW(double val) {
		   sysCapacityInKW = val*(211)*1/60;
		   return sysCapacityInKW;
	   }
public String setSuctionLine() throws FileNotFoundException {
		
		
		Scanner scanner= new Scanner(table);
		String ref=scanner.nextLine();
		while(!ref.equals(this.refrigerant+"{")) {
			ref=scanner.nextLine();
		}
		while(!ref.equals(evapTemp+"{") && !ref.equals("};"+this.refrigerant+" closing")) {
			
			ref=scanner.nextLine();
		};
		System.out.println(evapTemp+"");
		
	    if(ref.equals("};"+this.refrigerant+" closing")) {
			return "invalid evaporator temperature value";
		}else {
			while(!ref.equals(designTemp+"{") && !ref.equals("Pa{")) {
				ref=scanner.nextLine();
			}
			if(ref.equals("Pa{")) {
				return "invalid design temperature value";
			}else {
				ref=scanner.nextLine();
				int index=0;
				boolean inLoop=true;
				while(!ref.equals("liquid{") && inLoop==true) {
					ref=scanner.nextLine();
					if(sysCapacityInKW<Double.parseDouble(ref) || sysCapacityInKW==Double.parseDouble(ref)) {
						tableCap=Double.parseDouble(ref);
						inLoop=false;
					}
					index++;
				}
				if(ref.equals("liquid{")) {
					return "no record found";
				}else {
					while(!ref.equals("mm{")) {
						  ref=scanner.nextLine();
					  }
					ref=scanner.nextLine();
					for(int i=0; i<index; i++) {
						ref=scanner.nextLine();
					}
					mmsuction=Double.parseDouble(ref);
					return "";
				}
			}
			
			
		}
		
		
	}
	
	public String setLiquidLine() throws FileNotFoundException {
		
		
		Scanner scanner= new Scanner(table);
		String ref=scanner.nextLine();
		while(!ref.equals(this.refrigerant+"{")) {
			ref=scanner.nextLine();
		}
		while(!ref.equals(evapTemp+"{") && !ref.equals("};"+this.refrigerant+" closing")) {
			ref=scanner.nextLine();
		}
	    if(ref.equals("};"+this.refrigerant+" closing")) {
			return "invalid evaporator temperature value";
		}else {
			while(!ref.equals("liquid{")) {
				ref=scanner.nextLine();
			}
			int index=0;
			boolean inloop=true;
			while(!ref.equals("mm{") && inloop==true) {
				ref=scanner.nextLine();
				if(sysCapacityInKW<Double.parseDouble(ref)  || sysCapacityInKW<Double.parseDouble(ref)) {
					inloop=false;
				}
				index ++;
			}
			if(ref.equals("mm{")) {
				return "no value found";
			}else {
				while(!ref.equals("mm{") ) {
					ref=scanner.nextLine();
				}
				for(int i=0; i<index; i++) {
					ref=scanner.nextLine();
				}
				mmliquid=Double.parseDouble(ref);
				return "";
			}
		}
		
		
	}
	
	
	double getSuctionLineSize() {
		return this.mmsuction;
	}
	double getLiquidLineSize() {
		return this.mmliquid;
	}

	double getEquivalentLength(double line, double filterDrier) throws FileNotFoundException {
		
		double rad=0;
		double length=0;
		
		Scanner scanner= new Scanner(table);
		String ref="";
		while(!ref.equals("eqv-line-for-fitting{")) {
			ref=scanner.nextLine();
		}
		while(!ref.equals(String.valueOf(line)+"mm{")) {
			ref=scanner.nextLine();
		}
		ref=scanner.nextLine();
		rad=Double.parseDouble(ref);
		length=4+2+rad*2+filterDrier;
		return length;
		
	}
	double getTempDropLiquidLine(double mm, double actualLength, double actualCapacity) throws FileNotFoundException {
		double temp=0;
		
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
		temp=(designTemp)*(actualLength/30.4878)*Math.pow((actualCapacity/this.tableCap), 1.8);
		return temp;
	}
	double getPressureDropLiquid() throws FileNotFoundException {
		
		Scanner scanner=  new Scanner(table);
		String ref="";
		while(!ref.equals(this.refrigerant+"{")) {
			ref=scanner.nextLine();
		}
		while(!ref.equals("liquid{")) {
			ref=scanner.nextLine();
		}
		while(!ref.equals("Pa{")) {
			ref=scanner.nextLine();
		}
		ref=scanner.nextLine();
		double kPa=Double.parseDouble(ref)/1000;
		
		return kPa;
	}
	
	public double getPressureDropFromRiser(double kpa) throws FileNotFoundException {
		
		Scanner scanner=  new Scanner(table);
		String ref="";
		while(!ref.equals(this.refrigerant+"{")) {
			ref=scanner.nextLine();
		}
		while(!ref.equals("pressure-drop{")) {
			ref=scanner.nextLine();
		}
		ref=scanner.nextLine();
		
		return 2*Double.parseDouble(ref);
		
	}
	
	public double getPressureDropSuction() throws FileNotFoundException {
		Scanner scanner= new Scanner(table);
		String ref=scanner.nextLine();
		while(!ref.equals(this.refrigerant+"{")) {
			ref=scanner.nextLine();
		}
		while(!ref.equals(evapTemp+"{") && !ref.equals("};"+this.refrigerant+" closing")) {
			ref=scanner.nextLine();
		};
		while(!ref.equals(designTemp+"{") && !ref.equals("Pa{")) {
			ref=scanner.nextLine();
		}
		while(!ref.equals("Pa{")) {
			ref=scanner.nextLine();
		}
		ref=scanner.nextLine();
		
		return Double.parseDouble(ref)/1000;
		}
	
	public double getEnthalpyGas() throws FileNotFoundException {
			Scanner scanner= new Scanner(saturationtable);
			String ref=scanner.nextLine();
			while(!ref.equals(this.refrigerant)) {
				ref=scanner.nextLine();
			}
			while(!ref.equals(evapTemp+"")) {
				ref=scanner.nextLine();
			}
			ref=scanner.nextLine();
			String[] values = ref.split(",");
		    return Double.parseDouble(values[3]);
	   }
	public String setEnthalpyLiquid() throws FileNotFoundException {
		   Scanner scanner= new Scanner(saturationtable);
			String ref=scanner.nextLine();
			while(!ref.equals(this.refrigerant)) {
				ref=scanner.nextLine();
			}
			while(!ref.equals(condenseTemp+"") && !ref.equals("/"+this.refrigerant)) {
				ref=scanner.nextLine();
			}
			if(ref.equals("/"+this.refrigerant)) {
				return "invalid condenser temperature value";
			}else {
				ref=scanner.nextLine();
				String[] values = ref.split(",");
				h4= Double.parseDouble(values[2]);
				return "";
			}
		   
	   }
	
  public double getEnthalpyLiquid() {
	  return this.h4;
  }
  public double getCapacityInKW() {
	  return this.sysCapacityInKW;
  }
  public double getSpecVolumeGas() throws FileNotFoundException {
	  Scanner scanner= new Scanner(saturationtable);
		String ref=scanner.nextLine();
		while(!ref.equals(this.refrigerant)) {
			ref=scanner.nextLine();
		}
		while(!ref.equals(evapTemp+"")) {
			ref=scanner.nextLine();
		}
		ref=scanner.nextLine();
		String[] values = ref.split(",");
		double val= Double.parseDouble(values[1]);
		double area = (Math.PI/4)*Math.pow((mmsuction/1000), 2);
		System.out.println(area);
		return (val/area);
}
  public double getSpecVolumeLiquid() throws FileNotFoundException {
	   Scanner scanner= new Scanner(saturationtable);
		String ref=scanner.nextLine();
		while(!ref.equals(this.refrigerant)) {
			ref=scanner.nextLine();
		}
		while(!ref.equals(condenseTemp+"")) {
			ref=scanner.nextLine();
		}
		ref=scanner.nextLine();
		String[] values = ref.split(",");
		double val= Double.parseDouble(values[0]);
		double area = (Math.PI/4)*Math.pow((mmliquid/1000), 2);
		return (val/area);
}
   

}
