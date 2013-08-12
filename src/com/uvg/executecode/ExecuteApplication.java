package com.uvg.executecode;

import java.util.HashMap;

import android.app.Application;

public class ExecuteApplication extends Application {
	private HashMap<String,String> datos;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		datos = new HashMap<String,String>();
	}
	
	public void saveValue(String key,String valor) {
		datos.put(key, valor);
	}
	
	public String getValue(String key) {
		return datos.get(key);
	}
	
	

}
