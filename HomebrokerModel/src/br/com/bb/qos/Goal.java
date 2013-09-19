package br.com.bb.qos;

public class Goal {
	String serviceId;
	String operationId;
	String categoryId;
	String attributeId;
	Object threshold;
	public Goal(String serviceId, String operationId, String categoryId, String attributeId, Object threshold) {
		super();
		this.serviceId = serviceId;
		this.operationId = operationId;
		this.categoryId = categoryId;
		this.attributeId = attributeId;
		this.threshold = threshold;
	}
	
	public Goal(String serviceId, String categoryId, String attributeId, Object threshold) {
		super();
		this.serviceId = serviceId;
		this.categoryId = categoryId;
		this.attributeId = attributeId;
		this.threshold = threshold;
	}

	
	public String getServiceId() {
		return serviceId;
	}
	
	public String getOperationId() {
		return operationId;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public String getAttributeId() {
		return attributeId;
	}
	public Object getThreshold() {
		return threshold;
	}
	
	
}
