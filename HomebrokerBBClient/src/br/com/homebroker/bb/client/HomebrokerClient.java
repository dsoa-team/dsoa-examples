package br.com.homebroker.bb.client;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.osgi.framework.BundleContext;

import br.com.bb.exception.OutOfScheduleException;
import br.com.bb.homebroker.Homebroker;
import br.com.bb.stock.Stock;

public class HomebrokerClient implements Runnable {

	private Homebroker homebroker;
	Thread invocations;
	boolean started = false;
	
	private long time = 0;
	private Logger invocation;
	private FileHandler invocation_f;

	public HomebrokerClient(BundleContext ctx) throws SecurityException, IOException {
		System.out.println("#### Constructor");
		
java.util.logging.Formatter f = new java.util.logging.Formatter() {
			
			private final DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");

			public String format(LogRecord record) {
				StringBuilder builder = new StringBuilder(1000);
				builder.append(formatMessage(record));
				builder.append("\n");
				return builder.toString();
			}
		};
		
		invocation = Logger.getLogger("InvocationReal");
		
		try {
			invocation_f = new FileHandler("log_invocation_real.txt");
			invocation_f.setFormatter(f);
			invocation.addHandler(invocation_f);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		invocations = new Thread(this);
	}

	public void start() throws InterruptedException {
		System.out.println("==>> STARTING...");
		if (!started) {
			invocations.start();
			started = true;
		}
	}

	public void stop() {
		System.out.println("==>> STOPPING...");
	}

	@Override
	public void run() {

		long start = System.currentTimeMillis();
		boolean request = true;
		int count = 0;
		int businessExceptions = 0;
		int avaiExcept = 0;
		while (request) {
			try {
				count++;
				long req = System.currentTimeMillis();
				homebroker.priceAlert("ENDERECO", Stock.PETR3, 0, 1000);
				long resp = System.currentTimeMillis();
				invocation.log(Level.INFO, now() +"," +  (resp-req));
			} catch (OutOfScheduleException e) {
				businessExceptions ++;
				invocation.log(Level.INFO, now() +", 0");
			} catch (Exception e) {
				avaiExcept ++;
				invocation.log(Level.INFO, now() +", 0");
			}
			long time = System.currentTimeMillis();
			if (time - start >= 120000) {
				request = false;
			}
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.err.println("Total requests: " + count);
		System.err.println("Total bussinesException: " + businessExceptions);
		System.err.println("Total availabilityException: " + avaiExcept);
	}
	
private String now(){
		
		if(time == 0){
			time = System.currentTimeMillis();
			return "0";
		}
		
		long current = System.currentTimeMillis();
		String now = (current - time)+"";

		return now;
	}

}
