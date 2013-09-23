package br.com.homebroker.bb.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.monitor.MonitorAdmin;
import org.osgi.service.monitor.MonitoringJob;
import org.osgi.service.monitor.StatusVariable;

import br.com.bb.homebroker.Homebroker;
import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventConsumer;
import br.ufpe.cin.dsoa.api.event.EventFilter;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.FilterExpression;
import br.ufpe.cin.dsoa.api.event.Property;
import br.ufpe.cin.dsoa.api.event.PropertyType;
import br.ufpe.cin.dsoa.api.event.Subscription;
import br.ufpe.cin.dsoa.api.service.Expression;
import br.ufpe.cin.dsoa.platform.event.EventProcessingService;
import br.ufpe.cin.dsoa.platform.event.EventTypeCatalog;
import br.ufpe.cin.dsoa.util.Constants;

public class HomebrokerClientv2 implements EventHandler {

	private Homebroker homebroker;
	
	private EventProcessingService epService;
	
	private EventTypeCatalog eventTypeCatalog;
	
	private MonitorAdmin monitorAdmin;
	
	private BundleContext ctx;
	
	private EventType eventType;
	private Random random;

	public HomebrokerClientv2(BundleContext ctx) throws SecurityException, IOException {
		System.out.println("#### Constructor");
		this.ctx = ctx;
	}

	public void start() {
		this.eventType = eventTypeCatalog.get("InvocationEvent");
		this.random = new Random();
		
		
		this.subscribeMonitorServiceAdmin();
		
		int size = 30;
		String service = "hb", operation = "priceAlert";
		
		for (int i = 0; i < size; i++) {
			Event e = this.getEvent(service, operation, i, i + 10);
			System.err.println(e);
			this.epService.publish(e);
			
			/*StatusVariable statusVariable = this.monitorAdmin.getStatusVariable("hb-m/priceAlert.AvgResponseTime");
			System.out.println(statusVariable);*/
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private void subscribeMonitorServiceAdmin(){
	
		Hashtable p = new Hashtable();
		p.put(EventConstants.EVENT_TOPIC, new String[] {"org/osgi/service/monitor"});
		p.put(EventConstants.EVENT_FILTER, "(mon.listener.id=test.dsoa.job)");
		
		ctx.registerService(EventHandler.class.getName(), this, p);
		
		MonitoringJob job  = this.monitorAdmin.startScheduledJob(
			"test.dsoa.job",
			new String[] {"hb-m/priceAlert.AvgResponseTime"}, 
			2, 
			0);
	}
	
	private void subscribeEsper() {
		PropertyType sourceType = eventType.getMetadataPropertyType(Constants.EVENT_SOURCE);
		FilterExpression filterExp = new FilterExpression(new Property("hb.priceAlert", sourceType), Expression.EQ);
		List<FilterExpression> filterList = new ArrayList<FilterExpression>();
		filterList.add(filterExp);
		EventFilter filter = new EventFilter(filterList);
		String id = sourceType + Constants.TOKEN + "qos.AvgResponseTime";
		
		EventType avgType = eventTypeCatalog.get("AvgResponseTimeEvent");
		System.out.println(">>>>>>>>>" + avgType);
		Subscription subscription = new Subscription(id, avgType , filter);
		
		epService.subscribe(new EventConsumer() {
			
			@Override
			public void handleEvent(Event arg0) {
				System.out.println("##################" + arg0);
			}
			
			@Override
			public String getId() {
				// TODO Auto-generated method stub
				return null;
			}
		}, subscription);
	}

	public void stop() {
		System.out.println("==>> STOPPING...");
	}
	
	
	public Event getEvent(String service, String operation, long requestTime, long responseTime) {

		Map<String, Property> metadata = new HashMap<String, Property>();
		Map<String, Property> data = new HashMap<String, Property>();

		String source = (operation == null) ? service : String.format("%s.%s", service, operation);

		// metadata
		for (PropertyType propertyType : eventType.getMetadataList()) {
			Object value = (propertyType.getFullname().equals("metadata_source")) ? source : genRandom(propertyType
					.getType());
			Property property = propertyType.createProperty(value);
			metadata.put(propertyType.getName(), property);
		}

		// data
		for (PropertyType propertyType : eventType.getDataList()) {
			Object value = null;

			if (propertyType.getFullname().equals("data_requestTimestamp")) {
				value = requestTime;
			} else if (propertyType.getFullname().equals("data_responseTimestamp")) {
				value = responseTime;
			} else {
				value = genRandom(propertyType.getType());
			}
			Property property = propertyType.createProperty(value);
			data.put(propertyType.getName(), property);
		}

		Event generetedEvent = new Event(eventType, metadata, data);
		
		return generetedEvent;
	}

	public Event getEvent(String service, String operation) {
		return getEvent(service, operation, (Long) genRandom(Long.class), 
				(Long) genRandom(Long.class));
	}

	private Object genRandom(Class<?> clazz) {

		Object rand = null;

		if (clazz == Boolean.class) {
			rand = random.nextBoolean();
		} else if (clazz == Integer.class) {
			rand = random.nextInt();
		} else if (clazz == Long.class) {
			rand = random.nextLong();
		} else if (clazz == Double.class) {
			rand = random.nextDouble();
		} else if (clazz == String.class) {
			rand = UUID.randomUUID().toString();
		} else {
			try {
				rand = clazz.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return rand;
	}

	@Override
	public void handleEvent(org.osgi.service.event.Event event) {
		
		String value = (String) event.getProperty("mon.statusvariable.value");
		String name = (String) event.getProperty("mon.statusvariable.name");
		
		System.out.println(String.format("%s = %s", name, value));
		
	}
}
