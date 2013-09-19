package br.com.bb.homebroker;

import br.com.bb.stock.Stock;

public interface Homebroker {
	
	public double priceAlert(String address, Stock stock, double lowerThreshold, double higherThreshold);
	public void schedulingOrder(String investorCode, Stock stock,int quantity, double orderPrice, double lowerThreshold, double higherThreshold);
	
}
