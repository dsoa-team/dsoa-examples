package br.com.homebroker.bb;

import br.com.bb.homebroker.Homebroker;
import br.com.bb.provider.InformationProvider;
import br.com.bb.stock.Stock;

public class HomebrokerBB implements Homebroker {

	private InformationProvider provider;
	
	public void start() {
		System.out.println("==>> HomebrokerBB activated!");
	}

	public void stop() {
		System.out.println("==>> HomebrokerBB deactivated!");
	}

	
	public double priceAlert(String address, Stock stock, double lowerThreshold,
			double higherThreshold) {
		System.out.println("HOMEBROKERBB: priceAlert");
		return 1.0;
	}


	public void schedulingOrder(String investorCode, Stock stock, int quantity,
			double orderPrice, double lowerThreshold, double higherThreshold) {
		System.out.println("HOMEBROKERBB: schedulingOrder");
	}

	@Override
	public double getCurrentPrice(Stock stock) {
		//System.out.println("HOMEBROKERBB: getCurrentPrice from " + provider.getName());
		return provider.getCotation(stock);
	}

}
