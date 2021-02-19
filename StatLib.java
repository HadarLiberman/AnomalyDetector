package test;


public class StatLib {

	

	// simple average
	public static float avg(float[] x){
		float avarage=0;
		for(int i=0; i< x.length; i++){
			avarage+=x[i];

		}
		avarage=avarage/x.length;
		return avarage;
	}

	// returns the variance of X and Y
	public static float var(float[] x) {

		float avX=0;
			float avY=0;
			for(int i=0; i<x.length; i++){
				avX+=x[i]*x[i];
			}
			avX=avX/x.length;
			for(int i=0; i<x.length; i++){
				avY+=x[i];
			}
			avY=avY/x.length;
			avY=avY*avY;
			return avX-avY;
	}
	// returns the covariance of X and Y
	public static float cov(float[] x, float[] y){

		float avX=avg(x);
		float avy=avg(y);
		float s=0;
		for (int i=0;i<x.length||i<y.length;i++){
			s+=(x[i]-avX)*(y[i]-avy);
		}
		return (s/x.length);
	}


	// returns the Pearson correlation coefficient of X and Y
	public static float pearson(float[] x, float[] y){
		float covXY=cov(x,y);
		float stiyaX=var(x);
		float stiyaY=var(y);
		return covXY/((float) Math.sqrt(stiyaX)*(float) Math.sqrt(stiyaY));


	}

	// performs a linear regression and returns the line equation
	public static Line linear_reg(Point[] points){
		float []arrX=new float[points.length];
		float []arrY=new float[points.length];
		for(int i=0; i<points.length; i++){
			arrX[i]=points[i].x;
			arrY[i]=points[i].y;
		}
		float covBoth=cov(arrX,arrY);
		float varX=var(arrX);
		float a=covBoth/varX;
		float avX=avg(arrX);
		float avY=avg(arrY);
		float b=avY-a*avX;
		Line l=new Line(a,b);
		return l;

	}

	// returns the deviation between point p and the line equation of the points
	public static float dev(Point p,Point[] points){
		return dev(p,linear_reg(points));
	}

	// returns the deviation between point p and the line
	public static float dev(Point p,Line l){
		float lineY=Math.abs(p.y-(l.a*p.x)-l.b);
		return lineY;
	}
	
}
