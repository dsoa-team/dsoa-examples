package br.com.homebroker.bb.client;

import java.io.IOException;

import org.osgi.framework.BundleContext;

import br.com.bb.homebroker.Homebroker;
import br.com.bb.stock.Stock;

public class HomebrokerClient {

	private Homebroker homebroker;

	public HomebrokerClient(BundleContext ctx) throws SecurityException, IOException {
		System.out.println("#### Constructor");
	}

	public void start() throws InterruptedException {
		System.out.println("============================ PRICE ALERT ========================================");
		
		for (int i = 1; i < 10000; i++) {
			System.out.print("priceAlert " + i + " = ");
			System.out.println(homebroker.priceAlert("ENDERECO", Stock.PETR3, 0, 1000));
			Thread.sleep(1000);
		}
		System.out.println("==================================================================================");
	}

	public void stop() {
		System.out.println("==>> STOPPING...");
	}
}
