package br.com.bb.stock;

public enum Stock {
	PETR3 (1,"Petrobras ON3","PETR3"),
	PETR4 (2,"Petrobras PN4","PETR4"),
	VALE3 (3,"Vale ON3", "VALE3"),
	VALE4 (4,"Vale PN4", "VALE4"),
	BB3   (5,"Banco do Brasil ON3", "BB3");
	
	private final int id;
	private final String name;
	private final String code;
	
	Stock(int id, String name, String abreviation) {
		this.id = id;
		this.name = name;
		this.code = abreviation;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCode() {
		return code;
	}
}
