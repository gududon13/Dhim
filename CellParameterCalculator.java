package com.leophysics.dhim3drenderer;

public class CellParameterCalculator
{
	double[][] phase;
	private final double path= 1;
	private final double alpha= 1;
	private double opdavg,phasesurfacearea;
	public static final double wavelengthnm=650;
	public CellParameterCalculator(double[][] basecorrectedphase){
		this.phase=basecorrectedphase;
		getaveragedOPD();
	}
	
	public  double getaveragedOPD(){
	 double opd=ImageProcessor.calculateAverage(phase);
		this.opdavg=opd*path;
		return opdavg;
	}
	public  double[][] getOPDprofile(){
		

		return phase;
	}
	public double getDryMass(){
		
		return (opdavg*Math.PI*(10000))/alpha;
	}
	public double getaverageddrymassDensity(){
		
		return opdavg*alpha;
	}
	public double[][] getDrymassSurfaceDensity(){
		return phase;
	}
	
	public double getPhaseVolume(){
		return opdavg*Math.PI*(10000);
	}
	
	public double getPhaseSurfaceArea(){
		double sum=0;
		int width = phase.length;
		int height = phase[0].length;
		
		for (int x = 0; x < height; x++) {
			for (int y = 0; y < width; y++) {
				if(phase[x][y]!=0){
				if((x)<height-2){
					if((y)<width-2){
				sum+=Math.sqrt(1+ Math.pow(phase[x+1][y]-phase[x][y],2) + Math.pow(phase[x][y+1]-phase[x][y],2));
				}}}
			}
		}
		phasesurfacearea=sum;
		
		return sum;
	}
	public double phaseSurfaceAreatoVolumeRatio(){
		return phasesurfacearea/(getPhaseVolume());
	}
	public double phaseSurfaceAreatodryMassRatio(){
		return phasesurfacearea/getDryMass();
	}
	public double projectedAreaToVolumeRatio(){
		return Math.PI*1000/getPhaseVolume();
	}
	public double phaseSphericityIndex(){
		return ((Math.pow(Math.PI,1/3))*Math.pow(6*opdavg, 2/3))/phasesurfacearea;
	}
	public double PhaseVariance(){
		double phasevariance=0;
		double[] array=ArrayConv2dto1d();
		for(int i=0; i< array.length; i++){
			if(array[i]!=0){
			phasevariance+=Math.pow(array[i]-opdavg,2);
			}
		}
		
		return phasevariance/(Math.PI*10000-1);
	}
	public double phasekurtosis(){
		double phasevariance=0;
		double[] array=ArrayConv2dto1d();
		for(int i=0; i< array.length; i++){
			if(array[i]!=0){
				phasevariance+=Math.pow(array[i]-opdavg,4);
			}
		}
		double phasekurosis=phasevariance/Math.pow(PhaseVariance(),4);
		
		return phasekurosis;
	}
	public double phaseskewness(){
		
		double phasevariance=0;
		double[] array=ArrayConv2dto1d();
		for(int i=0; i< array.length; i++){
			if(array[i]!=0){
				phasevariance+=Math.pow(array[i]-opdavg,4);
			}
		}
		double phasekurosis=phasevariance/Math.pow(PhaseVariance(),3);

		
		
		return phasekurosis;
		}
	public double phaseCellEcentricity(){
		return 0;
	}
	public double[] ArrayConv2dto1d(){
		
	
		int width = phase.length;
		int height = phase[0].length;
		double[] array=new double[width*height];
        int index=0;
		for (int x = 0; x < height; x++) {
			for (int y = 0; y < width; y++) {
				
					
					array[index]=phase[x][y];
					index++;
					}}
		
		return array;
	}
	
}
