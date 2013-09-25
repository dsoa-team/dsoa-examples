package br.com.homebroker.bb;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;

import br.com.bb.homebroker.Homebroker;
import br.com.bb.provider.InformationProvider;
import br.com.bb.stock.Stock;

public class HomebrokerBB implements Homebroker, InformationProvider {

	private BundleContext ctx;


	public HomebrokerBB(BundleContext ctx) throws SecurityException, IOException{
		System.out.println("#### Constructor");
		this.ctx = ctx;
	}

	/*
	 * BundleActivator Callback
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start() {
		System.out.println("==>> Activating HomebrokerBB application...");
		System.out.println("==>> HomebrokerBB activated!");
		Dictionary dict = new Hashtable();
		dict.put("service.pid", "Homebroker-BB");
		dict.put("service.description", "Homebroker do Banco do Brasil");
		dict.put("metric.qos.ResponseTime.priceAlert", "190");
		dict.put("managed", "true");
		
		/*Goal goal1 = new Goal("Homebroker-BB", "getCotation", "qos.performance", "avgResponseTime", new Double(500));
		Object v = (Object) new Double(500);
		Goal goal2 = new Goal("Homebroker-BB", "qos.performance", "avgResponseTime", v);
		List<Goal> goals =  new ArrayList<Goal>();
		goals.add(goal1);
		goals.add(goal2);
		AttributeSpecification spec = new AttributeSpecification(goals);
		dict.put("attribute.specification", spec);*/
		
		ctx.registerService(Homebroker.class.getName(), this, dict);
	}

	/*
	 * BundleActivator Callback
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void stop() {
		System.out.println("==>> Deactivating HomebrokerBB application...");
		System.out.println("==>> HomebrokerBB deactivated!");
	}

	
	public double priceAlert(String address, Stock stock, double lowerThreshold,
			double higherThreshold) {
		System.out.println("HOMEBROKERBB: priceAlert");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return 1.0;
	}


	public void schedulingOrder(String investorCode, Stock stock, int quantity,
			double orderPrice, double lowerThreshold, double higherThreshold) {
		System.out.println("HOMEBROKERBB: schedulingOrder");
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "InformationProviderBB";
	}

	@Override
	public double getCotation(Stock stock) {
		// TODO Auto-generated method stub
		return 100;
	}



}
