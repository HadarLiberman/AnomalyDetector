package test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import test.Commands.Command;
import test.Commands.DefaultIO;

public class CLI {

	ArrayList<Command> commands;
	DefaultIO dio;
	Commands c;
	int port;

	public CLI(DefaultIO dio) {
		this.dio=dio;
		c=new Commands(dio);
		commands=new ArrayList<>();

		// example: commands.add(c.new ExampleCommand());
		// implement


		commands.add(c.new UploadCommand());
		commands.add(c.new SettingCommand());
		commands.add(c.new DetectCommand());
		commands.add(c.new ResCommand());
		commands.add(c.new AnalyzeCommand());
		commands.add(c.new ExitCommand());


	}

	public void start() {

		// implement
		try {


			/*ServerSocket server=new ServerSocket(port);
			Socket aClient=server.accept();*/

			/*Scanner myScan = new Scanner(System.in);*/
			dio.write("Welcome to the Anomaly Detection Server.\n" +
					"Please choose an option:\n"+
					"1. upload a time series csv file\n"+
					"2. algorithm settings\n"+
					"3. detect anomalies\n"+
					"4. display results\n"+
					"5. upload anomalies and analyze results\n"+
					"6. exit\n");

		/*	int in = myScan.nextInt();
			int enter = myScan.nextInt();*/
			//String des = String.valueOf(in);
			float in=dio.readVal();
				if (in == 1) {
					commands.get(0).execute();
					start();

				} else if (in == 2) {
					commands.get(1).execute();
					start();

				} else if (in == 3) {
					commands.get(2).execute();
					start();

				} else if (in == 4) {
					commands.get(3).execute();
					start();

				} else if (in == 5) {
					commands.get(4).execute();
					start();



				} else if (in == 6) {
					commands.get(5).execute();



				} else if (in != 1 && in != 2 && in != 3 && in != 4 && in != 5 && in != 6) {
					System.out.println("Error in input");
					start();

				}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
