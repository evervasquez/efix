package com.emergentes.efix.model;

public class Aulas {
	String id;
	String piso;
	
	public Aulas(String id, String piso) {
		this.id=id;
		this.piso = piso;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPiso() {
		return piso;
	}
	public void setPiso(String piso) {
		this.piso = piso;
	}
}
