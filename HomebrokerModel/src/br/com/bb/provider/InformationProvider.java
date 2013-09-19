package br.com.bb.provider;

import br.com.bb.stock.Stock;

public interface InformationProvider {
	public String getName();
	public double getCotation(Stock stock);
}
