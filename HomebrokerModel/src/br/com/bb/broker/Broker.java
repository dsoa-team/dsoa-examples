package br.com.bb.broker;

import br.com.bb.stock.Stock;

public interface Broker {
	
	public boolean pursharseOrder(String investorCode, Stock stock, int quantity, double orderPrice);

}
