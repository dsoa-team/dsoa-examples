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
		if(!started){
			invocations.start();
			started = true;
		}
	}

	public void stop() {
		System.out.println("==>> STOPPING...");
	}

	@Override
	public void run() {

		System.out.println("===================== PRICE ALERT ==================================");

		for (int i = 1; i < 100; i++) {
			System.out.print("priceAlert " + i + " = ");
			try {
				System.out.println(homebroker.priceAlert("ENDERECO", Stock.PETR3, 0, 1000));
			}catch (OutOfScheduleException e){
				System.err.println("Exception: Out of Schedule");
			} catch (Exception e) {
				System.err.println("INVALID INVOCATION " + e.getClass());
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("================================================================");

	}
}
