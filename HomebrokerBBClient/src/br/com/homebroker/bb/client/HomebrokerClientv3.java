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

public class HomebrokerClientv3 implements Homebroker {

	private Homebroker homebroker;
	Thread invocations;
	boolean started = false;

	private long time = 0;
	int count = 0;
	private Logger invocation;
	private FileHandler invocation_f;
	private int businessExceptions;
	private int avaiExcept;

	public HomebrokerClientv3(BundleContext ctx) throws SecurityException,
			IOException {
		System.out.println("#### Constructor");

		java.util.logging.Formatter f = new java.util.logging.Formatter() {

			private final DateFormat df = new SimpleDateFormat(
					"dd/MM/yyyy hh:mm:ss.SSS");

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
	}

	public void start() throws InterruptedException {
		System.out.println("==>> STARTING...");
	}

	public void stop() {
		System.out.println("==>> STOPPING...");
	}

	private String now() {

		if (time == 0) {
			time = System.currentTimeMillis();
			return "0";
		}

		long current = System.currentTimeMillis();
		String now = (current - time) + "";

		return now;
	}

	@Override
	public double priceAlert(String address, Stock stock,
			double lowerThreshold, double higherThreshold)
			throws OutOfScheduleException {
		double price = 0;
		try {
			count++;
			long req = System.currentTimeMillis();
			price = homebroker.priceAlert("ENDERECO", Stock.PETR3, 0, 1000);
			long resp = System.currentTimeMillis();
			invocation.log(Level.INFO, now() +"," +  (resp-req));
		} catch (OutOfScheduleException e) {
			businessExceptions ++;
			invocation.log(Level.INFO, now() +", 0");
		} catch (Exception e) {
			avaiExcept ++;
			invocation.log(Level.INFO, now() +", 0");
		}
		return price;
	}

	@Override
	public void schedulingOrder(String investorCode, Stock stock, int quantity,
			double orderPrice, double lowerThreshold, double higherThreshold) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getCurrentPrice(Stock stock) {
		// TODO Auto-generated method stub
		return 0;
	}

}
