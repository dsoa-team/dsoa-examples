package br.com.homebroker.bb.client;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.osgi.framework.BundleContext;

import br.com.bb.homebroker.Homebroker;
import br.com.bb.stock.Stock;

public class HomebrokerClient implements Runnable {

	private Homebroker homebroker;
	
	private long startTime = 0;
	private int requestCounter = 0;
	private int errorsCounter = 0;
	
	private Logger invocationLogger;
	
	private Logger adaptationLogger;
	
	private Thread thread;
	private volatile boolean started = false;

	public HomebrokerClient(BundleContext ctx) throws SecurityException,
			IOException {
		System.out.println("#### Constructor");

		java.util.logging.Formatter f = new java.util.logging.Formatter() {

			public String format(LogRecord record) {
				StringBuilder builder = new StringBuilder(1000);
				builder.append(formatMessage(record));
				builder.append("\n");
				return builder.toString();
			}
		};

		invocationLogger = Logger.getLogger("InvocationLogger");
		adaptationLogger = Logger.getLogger("AdaptationLogger");
		try {
			FileHandler invocationLogFile = new FileHandler("logs/app" + this.getClass().getCanonicalName() + "-invocations" + ".log");
			invocationLogFile.setFormatter(f);
			invocationLogger.addHandler(invocationLogFile);
			
			FileHandler adaptationLogFile = new FileHandler("logs/app" + this.getClass().getCanonicalName() + "-adaptations" +".log");
			adaptationLogFile.setFormatter(f);
			adaptationLogger.addHandler(adaptationLogFile);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() throws InterruptedException {
		System.out.println("==>> STARTING...");
		adaptationLogger.info("Start time: " + getSimulatedTime());
		thread = new Thread(this);
		if (!started) {
			thread.start();
			started = true;
		}
	}

	public void stop() {
		System.out.println("==>> STOPPING...");
		adaptationLogger.info("Stop time: " + getSimulatedTime());
		this.started = false;
		if (thread != null) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		if (startTime == 0) {
			startTime = System.currentTimeMillis();
		}
		int count = 0;
		int avaiExcept = 0;
		
		/*for (int i =1; i<=10;i++) {
			try {
				count++;
				long requestTime = System.currentTimeMillis();
				System.out.println("PETR3: " + homebroker.getCurrentPrice(Stock.PETR3));
				long responseTime = System.currentTimeMillis();
				invocationLogger.log(Level.INFO, System.currentTimeMillis() + "," + (responseTime - requestTime));
			}catch (Exception e) {
				avaiExcept++;
				invocationLogger.log(Level.INFO, System.currentTimeMillis() + ", 0");
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/
		
		while (started) {
			try {
				count++;
				long requestTime = System.currentTimeMillis();
				System.out.println("PETR3: " + homebroker.getCurrentPrice(Stock.PETR3));
				long responseTime = System.currentTimeMillis();
				invocationLogger.log(Level.INFO, System.currentTimeMillis() + "," + (responseTime - requestTime));
				//System.out.println(System.currentTimeMillis() + "," + (responseTime - requestTime));
			} catch (Exception e) {
				avaiExcept++;
				invocationLogger.log(Level.INFO, System.currentTimeMillis() + ", 0");
				//System.out.println(System.currentTimeMillis() + ",0");
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (getSimulatedTime() >= 120000) {
				started = false;
			}
		}
		requestCounter += count;
		errorsCounter += avaiExcept;
		
		System.err.println("Total requests: " + count);
		System.err.println("Total availabilityException: " + avaiExcept);
	}

	private long getSimulatedTime() {
		return (System.currentTimeMillis() - startTime);
	}

}
