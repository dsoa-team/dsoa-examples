package br.com.homebroker.bb.client;

import java.io.IOException;

import org.osgi.framework.BundleContext;

import br.com.bb.homebroker.Homebroker;
import br.com.bb.stock.Stock;

public class HomebrokerClient {

	private Homebroker homebroker;
	private BundleContext ctx;

	public HomebrokerClient(BundleContext ctx) throws SecurityException, IOException {
		System.out.println("#### Constructor");
		this.ctx = ctx;
	}

	public void start() {
		System.out.println("============================ PRICE ALERT ========================================");
		System.out.println("Homebroker: " + homebroker);
		System.out.println(homebroker.getClass());
		System.out.println(homebroker.getClass().getName());
		
		/*try {
			ServiceReference[] refs = ctx.getServiceReferences(Homebroker.class.getName(), "(attribute.specification=*)");
			if (refs != null) {
				for (ServiceReference ref : refs) {
					String[] keys = ref.getPropertyKeys();
					for (String key : keys) {
						if (!"attribute.specification".equals("attribute.specification")) {
							System.out.println("Prop. Name: " + key);
							System.out.println("Prop. Value: " + ref.getProperty(key));
						} else {
							AttributeSpecification attSpec = (AttributeSpecification) ref.getProperty(key);
							List<Goal> goals = attSpec.getGoals();
							for (Goal goal : goals) {
								System.out.println("=====================================================");
								System.out.println(goal.getAttributeId());
								System.out.println(goal.getCategoryId());
								System.out.println(goal.getServiceId());
								System.out.println(goal.getOperationId());
								System.out.println(goal.getThreshold());
							}
						}
					}
				}
			} else {
				ServiceReference[] refs2 = ctx.getServiceReferences(Homebroker.class.getName(),null);
				if (refs2 != null) {
					for (ServiceReference ref : refs2) {
						String[] keys = ref.getPropertyKeys();

						for (String key : keys) {
							if (!"attribute.specification".equals("attribute.specification")) {
								System.out.println("Prop. Name: " + key);
								System.out.println("Prop. Value: " + ref.getProperty(key));
							}
						}
					}
				} else {
					System.out.println("============NULL================");
				}
			}
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}*/
		
		Homebroker home = null;
		try {
			home = (Homebroker)homebroker;
			home.schedulingOrder("Fabio", Stock.BB3, 10, 1.1, 1.0, 1.2);
		} catch(Throwable t) {
			t.printStackTrace();
		}
		
		for (int i = 1; i < 10; i++) {
			home = homebroker;
			System.out.println("Passo 0");
			home.schedulingOrder("Fabio", Stock.BB3, 10, 1.1, 1.0, 1.2);
			System.out.println("schedulingOrder 1");
			homebroker.schedulingOrder("Fabio", Stock.BB3, 10, 1.1, 1.0, 1.2);
			System.out.println("schedulingOrder 2");
			
			System.out.println("===================================================");
			System.out.println("priceAlert 1");
			home.priceAlert("ENDERECO", Stock.PETR3, 0, 1000);
			System.out.println("priceAlert 2");
			homebroker.priceAlert("ENDERECO", Stock.PETR3, 0, 1000);
			System.out.println("===================================================");
			
			System.out.println("Price: " + home.priceAlert("ENDERECO", Stock.PETR3, 0d, 1000d));
			System.out.println("Price: " + homebroker.priceAlert("ENDERECO", Stock.PETR3, 0, 1000));
			homebroker.schedulingOrder("Fabio", Stock.PETR3, 1000, 2, 1, 5);
		}
		System.out.println("==================================================================================");
	}

	public void stop() {
		System.out.println("==>> STOPPING...");
	}
}
