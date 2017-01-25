package br.com.homebroker.bb;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.FileHandler;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.osgi.framework.ServiceReference;

import br.com.bb.broker.Broker;
import br.com.bb.homebroker.Homebroker;
import br.com.bb.provider.InformationProvider;
import br.com.bb.stock.Stock;

public class CopyOfHomebrokerBB2 implements Homebroker {

	private InformationProvider informationProvider;
	private Broker broker;
	private Logger logger;
	private FileHandler fh;
	private FileHandler fhRegUnreg;
	private Formatter formatter;

	private Map<String, List<Order>> orderMap = new TreeMap<String, List<Order>>();

	private boolean active = false;
	private boolean execute;
	private Thread monitoringThread;
	private StockMonitor monitor = new StockMonitor();

	public CopyOfHomebrokerBB2() throws SecurityException, IOException{
		//System.out.println("passou aqui ####");
		this.logger  = Logger.getLogger("ResponseTimeClient");
		this.logger.setUseParentHandlers(false);
		this.formatter = new Formatter() {
			@Override
			public String format(LogRecord record) {
				StringBuilder builder = new StringBuilder(1000);
				builder.append(formatMessage(record));
				builder.append("\n");
				return builder.toString();
			}
		};
		
		
		this.fh = new FileHandler("logs/logResponseTimeClient.txt");
		this.fh.setFormatter(this.formatter);
		this.fh.setFilter(new Filter() {
			public boolean isLoggable(LogRecord record) {
				String msg = record.getMessage();
				if (msg.startsWith("ResponseTimeClient")) {
					return true;		
				}
				return false;
			}
		});
		
		this.fhRegUnreg = new FileHandler("logRegister.txt");
		this.fhRegUnreg.setFormatter(this.formatter);
		this.fhRegUnreg.setFilter(new Filter() {
			public boolean isLoggable(LogRecord record) {
				String msg = record.getMessage();
				if (msg.startsWith("Registered") || msg.startsWith("Unregistered")) {
					return true;		
				}
				return false;
			}
		});
		
		this.logger.addHandler(fh);
		this.logger.addHandler(fhRegUnreg);

	}

	public void registered(ServiceReference ref) {
		System.out.println("==>> Homebroker application is been registered...");
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
		StringBuilder builder = new StringBuilder(1000);
		builder.append("Registered").append("   - ");
		//builder.append(df.format(new Date(System.currentTimeMillis())));
		builder.append(System.currentTimeMillis());
		logger.log(Level.INFO, builder.toString());
		
		
		//System.out.println("we brow");
		setExecute(true);
		monitoringThread = new Thread(monitor);
		monitoringThread.start();
		System.out.println("==>> Homebroker application HAS been registered!");
	}

	public void unregistered(ServiceReference ref) {
		System.out.println("==>> Homebroker application is been UNregistered...");
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
		StringBuilder builder = new StringBuilder(1000);
		builder.append("Unregistered").append(" - ");
		//builder.append(df.format(new Date(System.currentTimeMillis())));
		builder.append(System.currentTimeMillis());
		logger.log(Level.INFO, builder.toString());
		
		setExecute(false);
		System.out.println("==>> Homebroker application HAS been UNregistered!");
	}

	public boolean isExecute() {
		return execute;
	}

	public void setExecute(boolean execute) {
		this.execute = execute;
	}

	public boolean isActive() {
		return active;
	}

	@Override
	public double priceAlert(String address, Stock stock, double lowerThreshold,
			double higherThreshold) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void schedulingOrder(String investorCode, Stock stock, int quantity,
			double orderPrice, double lowerThreshold, double upperThreshold) {
		//System.out.println("===>> Homebroker: Order received: InvestorCode: " + investorCode);
		synchronized (orderMap) {
			List<Order> ordersPerStock = orderMap.get(stock.getCode());
			if (ordersPerStock == null) {
				ordersPerStock = new LinkedList<Order>();
				orderMap.put(stock.getCode(), ordersPerStock);
			}
			Order order = new Order(investorCode, stock, quantity, orderPrice,lowerThreshold, upperThreshold);
			ordersPerStock.add(order);
			//System.out.println("===>> Homebroker: Order received: " + order);
		}
	}

	public void start() {
		System.out.println("==>> Activating HomebrokerBB application...");
		this.active = true;
		System.out.println("==>> HomebrokerBB activated!");
	}

	public void stop() {
		System.out.println("==>> Deactivating HomebrokerBB application...");
		this.active = false;
		System.out.println("==>> HomebrokerBB deactivated!");
	}

	class Order {

		private String investorCode;
		private double upperThreshold;
		private Stock stock;
		private int quantity;
		private double orderPrice;
		private double lowerThreshold;

		public Order(String investorCode, Stock stock, int quantity,
				double orderPrice, double lowerThreshold, double upperThreshold) {

			this.investorCode = investorCode;
			this.upperThreshold = upperThreshold;
			this.stock = stock;
			this.quantity = quantity;
			this.orderPrice = orderPrice;
			this.lowerThreshold = lowerThreshold;
		}

		public String getInvestorCode() {
			return investorCode;
		}

		public double getUpperThreshold() {
			return upperThreshold;
		}

		public Stock getStock() {
			return stock;
		}

		public int getQuantity() {
			return quantity;
		}

		public double getOrderPrice() {
			return orderPrice;
		}

		public double getLowerThreshold() {
			return lowerThreshold;
		}

		public boolean isValid(double price) {
			boolean valid = false;
			if (price >= lowerThreshold && price <= upperThreshold) {
				valid = true;
			}
			return valid;
		}

		@Override
		public String toString() {
			return "Order [investorCode=" + investorCode + ", upperThreshold="
			+ upperThreshold + ", stock=" + stock + ", quantity="
			+ quantity + ", orderPrice=" + orderPrice
			+ ", lowerThreshold=" + lowerThreshold + "]";
		}

	}

	class StockMonitor implements Runnable {
		@Override
		public void run() {
			while (isExecute()) {
				while (isActive()) {
					for (Stock stock : Stock.values()) {
						try {
							long startTime = System.nanoTime();
							double currentPrice = CopyOfHomebrokerBB2.this.informationProvider
							.getCotation(stock);
							double responseTime = ((System.nanoTime() - startTime) / 1000000d);
							// Timestamp - Response time - cotação
							System.out.println(("=====>> Homebroker ("+ responseTime+ "): " + stock.getCode() + " - "+ stock.getName() + ": " + currentPrice));
							
							StringBuilder builder = new StringBuilder(1000);
							builder.append("ResponseTimeClient").append(" - ");
							//builder.append(df.format(new Date(System.currentTimeMillis()))).append(" - ");
							builder.append(System.currentTimeMillis()).append(" - ");
							builder.append(responseTime);
							logger.log(Level.INFO, builder.toString());
							
							
							List<Order> orders = orderMap.get(stock.getCode());
							List<Order> processedOrders = new ArrayList<Order>();
							if (orders != null && !orders.isEmpty()) {
								for (Order order : orders) {
									if (order != null && order.isValid(currentPrice)) {
										processedOrders.add(order);
									}
								}
							}
							if (!processedOrders.isEmpty()) {
								for (Order processedOrder : processedOrders) {
									broker.pursharseOrder(
											processedOrder.getInvestorCode(),
											processedOrder.getStock(),
											processedOrder.getQuantity(),
											processedOrder.getOrderPrice());
									//System.out.println("=====>> Homebroker: Order Processed:" + processedOrder);
									orders.remove(processedOrder);
								}
							}
						} catch (RuntimeException ex) {
							System.out.println("==============>>> ALERTA <<<==============");
							ex.printStackTrace();

						} catch (Exception exp) {
							exp.printStackTrace();
						}
					}
					try {
						Thread.sleep(700);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.out.println("MORRIIIIIIIIIIIIIIIIIIIIIIIIII");
			}
		}
	}

	@Override
	public double getCurrentPrice(Stock stock) {
		// TODO Auto-generated method stub
		return 0;
	}
}
