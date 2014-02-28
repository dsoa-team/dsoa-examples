package br.com.homebroker.bb.client;

import java.io.IOException;

import org.osgi.framework.BundleContext;

import br.com.bb.exception.OutOfScheduleException;
import br.com.bb.homebroker.Homebroker;
import br.com.bb.stock.Stock;

public class HomebrokerClient implements Runnable {

	private Homebroker homebroker;
	Thread invocations;
	boolean started = false;

	public HomebrokerClient(BundleContext ctx) throws SecurityException, IOException {
		System.out.println("#### Constructor");
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
				homebroker.priceAlert("ENDERECO", Stock.PETR3, 0, 1000);
			} catch (OutOfScheduleException e) {
				businessExceptions ++;
			} catch (Exception e) {
				avaiExcept ++;
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
}
