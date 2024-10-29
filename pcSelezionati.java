package com.example.demo;

public class pcSelezionati extends pc {
	
	public int qnt;

	public pcSelezionati(String nome, String marca, String descrizione, double prezzo, String url, int qntmagazzino,
			int qntvenduti, int qnt) {
		super(nome, marca, descrizione, prezzo, url, qntmagazzino, qntvenduti);
		
		this.qnt = qnt;
		
	}
	
	
	

}
