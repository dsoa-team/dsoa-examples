package br.com.homebroker.bb.client;

import java.io.IOException;

import br.com.bb.homebroker.Homebroker;
import br.com.bb.stock.Stock;

public class HomebrokerClientv2 {

	private Homebroker homebroker;

	public HomebrokerClientv2() throws SecurityException, IOException {
		System.out.println("#### Constructor");
	}

	public void start() {
		System.out.println("============================ PRICE ALERT ========================================");
		for (int i = 1; i < 10; i++) {
			homebroker.priceAlert("ENDERECO", Stock.PETR3, 0, 1000);
			homebroker.schedulingOrder("Fabio", Stock.PETR3, 1000, 2, 1, 5);
		}
		System.out.println("==================================================================================");
	}

	public void stop() {
		System.out.println("==>> STOPPING...");
	}
}
