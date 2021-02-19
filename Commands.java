package test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Commands {

	// Default IO interface
	public interface DefaultIO{
		public String readText();
		public void write(String text);
		public float readVal();
		public void write(float val);


	}
	public class StandardIO implements DefaultIO {

		@Override
		public String readText() {
			Scanner scan=new Scanner(System.in);
			String line = scan.nextLine();
			scan.close();
			return line;
		}

		@Override
		public void write(String text) {
			System.out.println(text);

		}

		@Override
		public float readVal() {
			Scanner myScan = new Scanner(System.in);
			float in = myScan.nextInt();
			myScan.close();
			return in;
		}

		@Override
		public void write(float val) {
			System.out.println(val);

		}
		public void getCsv(PrintWriter out) throws IOException {
			String line = dio.readText();
			if (line==""){
				line=dio.readText();
			}
			while(!line.contains("done")){
				out.write(line+"\n");
				line = dio.readText();
			}
		}
	}


	DefaultIO dio;
	public Commands(DefaultIO dio) {
		this.dio=dio;
	}

	private class SharedState{
		float threshold = (float) 0.9;
		TimeSeries traincsv = null;
		TimeSeries testcsv;
		SimpleAnomalyDetector anomalyDecor = new SimpleAnomalyDetector();
		List<AnomalyReport> anomalyReport;
		StandardIO s = new StandardIO();

		public float getThreshold() {
			return threshold;
		}

		public void setThreshold(float threshold) {
			this.threshold = threshold;
		}


	}

	private  SharedState sharedState=new SharedState();

	public abstract class Command{
		protected String description;

		public Command(String description) {
			this.description=description;
		}

		public abstract void execute() throws IOException;
	}

	public class UploadCommand extends Command{

		public UploadCommand() {
			super("Please upload your local train CSV file.\n");
		}

		@Override
		public void execute() throws IOException {
            dio.write(description);
			PrintWriter train =new PrintWriter(new FileWriter("anomalyTrain.csv"));
			sharedState.s.getCsv(train);
			train.close();
			TimeSeries temp=new TimeSeries("anomalyTrain.csv");
			sharedState.traincsv = temp;

			//System.out.println(sharedState.traincsv.getAttributes());
			String a="A";
			//System.out.println(sharedState.traincsv.getAttributeData(a));


			dio.write("Upload complete.\n");

			dio.write("Please upload your local test CSV file.\n");
			PrintWriter test =new PrintWriter( new FileWriter("anomalyTest.csv"));
			sharedState.s.getCsv(test);
			test.close();

			TimeSeries temp2=new TimeSeries("anomalyTest.csv");
			sharedState.testcsv = temp2;
			dio.write("Upload complete.\n");

		}
	}

	public class SettingCommand extends Command{

		public SettingCommand() {
			super("The current correlation threshold is ");
		}

		@Override
		public void execute() {

			float currentT=sharedState.getThreshold();
		    dio.write(description+ currentT+"\n");
		    dio.write("Type a new threshold\n");
			boolean flag=false;
			while(flag!=true) {
				float newT = dio.readVal();
			if (newT >= 0 && newT <= 1) {
					sharedState.setThreshold(newT);
					flag=true;
				} else dio.write("please choose a value between 0 and 1");
			}

		}
	}

	public class DetectCommand extends Command{

		public DetectCommand() {
			super("\nanomaly detection complete");
		}

		@Override
		public void execute() {
			sharedState.anomalyDecor.learnNormal(sharedState.traincsv);
			sharedState.anomalyReport=sharedState.anomalyDecor.detect(sharedState.testcsv);
			dio.write("anomaly detection complete.\n");

		}
	}

	public class ResCommand extends Command{

		public ResCommand() {
			super("\n4.display results");
		}

		@Override
		public void execute() {
			for(AnomalyReport a:sharedState.anomalyReport){
				dio.write(a.timeStep+ "\t"+a.description+"\n");
			}
			dio.write("Done.\n");
		}
	}

	public class AnalyzeCommand extends Command{

		public AnalyzeCommand() {
			super("\n5.upload anomalies and analyze results");
		}

		@Override
		public void execute() throws IOException {

			dio.write("Please upload your local anomalies file\n");
			List<Integer> temp = new ArrayList<Integer>();
			for (int i=0;i<sharedState.anomalyReport.size()-1;i++) {
				temp.add((int) sharedState.anomalyReport.get(i).timeStep);
				for (int j = i + 1; j < sharedState.anomalyReport.size(); j++){
					if (sharedState.anomalyReport.get(i).timeStep + 1 == sharedState.anomalyReport.get(j).timeStep
							&& sharedState.anomalyReport.get(i).description.compareTo(sharedState.anomalyReport.get(j).description)==0)
					{
						i=j;
					}
					else {
						break;
					}
				}
				temp.add((int) sharedState.anomalyReport.get(i).timeStep);
			}
			String name= "Anomalydetector.txt";
			PrintWriter ADtxt =new PrintWriter( new FileWriter(name));
			sharedState.s.getCsv(ADtxt);
			ADtxt.close();
			dio.write("upload complete\n");

			int start, end;
			int numCounter=0,positive=0,TPCounter=0, FTCounter=0;
			boolean flag=true;
			List<Integer> check = new ArrayList<Integer>();
			BufferedReader reader = new BufferedReader(new FileReader(name));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.compareTo("")==0){
					continue;
				}
				String[] lines = line.split(",");
				start = Integer.parseInt(lines[0]);
				end = Integer.parseInt(lines[1]);
				check.add(start);
				check.add(end);
				positive++;
				numCounter +=end-start+1;
			}
			reader.close();
			int rows= sharedState.traincsv.dataRowSize;
			int negative= rows-numCounter;
			for (int i=0;i<temp.size();i+=2){
				for (int j=0;j<check.size();j+=2){
					if (temp.get(i)>=check.get(j)){
						if (temp.get(i)<=check.get(j+1)){
							TPCounter++;
							flag=false;
							break;
						}
					}
					if (temp.get(i)<check.get(j)){
						if (temp.get(i+1)<=check.get(j+1)-1){
							TPCounter++;
							flag=false;
							break;
						}
					}

				}
				if (flag){
					FTCounter++;
				}
				flag=true;
			}
			double rateTP=(double)TPCounter/(double)positive;
			double rateFA=(double)FTCounter/(double)negative;
			rateTP=(double)((int)(rateTP*1000))/1000;
			rateFA=(double)((int)(rateFA*1000))/1000;
			dio.write("True Positive Rate: "+rateTP+"\n");
			dio.write("False Positive Rate: "+rateFA+"\n");
		}
	}

	public class ExitCommand extends Command{

		public ExitCommand() {
			super("\n6.exit");
		}

		@Override
		public void execute() {

		}
	}



}
