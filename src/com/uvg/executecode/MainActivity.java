package com.uvg.executecode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
//http://wiki.domo.com/confluence/display/DC50/Table+Content+Expressions+with+JEval
import android.widget.EditText;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MainActivity extends Activity implements OnClickListener{
	Button set1,set2,set3;
	Button evaluate;
	EditText val1,val2,val3, evalText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		//Evaluator mEvaluator = new Evaluator();
		set1 = (Button) findViewById(R.id.btnOp1);
		set2 = (Button) findViewById(R.id.btnOp2);
		set3 = (Button) findViewById(R.id.btnOp3);
		val1 = (EditText) findViewById(R.id.txtOp1);
		val2 = (EditText) findViewById(R.id.txtOp2);
		val3  = (EditText) findViewById(R.id.txtOp3);
		evaluate = (Button) findViewById(R.id.btnEval);
		evalText  = (EditText) findViewById(R.id.txtEval);
		set1.setOnClickListener(this);
		set2.setOnClickListener(this);
		set3.setOnClickListener(this);
		evaluate.setOnClickListener(this);
		/*
		try {
			Log.e("Evaluation",mEvaluator.evaluate("cos(4.55)"));
		} catch (EvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnOp1 :
			((ExecuteApplication) getApplication()).saveValue("nombre", val1.getText().toString());
			val1.setText("");
			break;
		case R.id.btnOp2 :
			((ExecuteApplication) getApplication()).saveValue("habitante", val2.getText().toString());
			val2.setText("");
			break;
		case R.id.btnOp3 :
			((ExecuteApplication) getApplication()).saveValue("edad", val3.getText().toString());
			val3.setText("");
			break;
		case R.id.btnEval :
			String toExecute,response="";
			//Sentencia SQL para crear la tabla de Usuarios
		 
			toExecute = evalText.getText().toString();
			toExecute = replaceString(toExecute);
			
			String lHasValidSQL = "";
					
			lHasValidSQL = HasValidSQL(toExecute);
			
			if(lHasValidSQL.length() == 0)
			{
				response = evaluar(toExecute);
			}
			else
			{
				SQLiteOpenHelper lHelper = new SQLiteOpenHelper(getApplicationContext(), "ExecuteCode", null, 1 ) {
					
					@Override
					public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onCreate(SQLiteDatabase db) {
						// TODO Auto-generated method stub
						db.execSQL("CREATE TABLE Personas (edad INTEGER, nombre TEXT)");
						
						db.execSQL("insert into Personas(edad, nombre) values (32, 'Julio')");
						
					}
				};
				
				SQLiteDatabase lDB = lHelper.getWritableDatabase();
			
				Cursor lCursor = null;
				
				if(lDB != null)
				{
					//lDB.execSQL(lHasValidSQL);
					lCursor = lDB.rawQuery(lHasValidSQL, null);
					
					if(lCursor.moveToFirst())
					{
						response = lCursor.getString(0);
						
					}
				}
				
				lDB.close();
				
			}
			
			Toast.makeText(this, "Respuesta : " + response, 10000).show();
			break;
		}
		
	}
	
	public String replaceString(String hil) {
		//hil = "hola @uno esta es @dos una @tres prueba @cuatro";
		String replace, value, newhil;
		Pattern pattern = Pattern.compile("@[A-Za-z0-9]+");
		Matcher matcher = pattern.matcher(hil);
		newhil=hil;
		while(matcher.find()) {
			replace = hil.substring(matcher.start(),matcher.end());
			value = ((ExecuteApplication) getApplication()).getValue(replace.substring(1));
			if (!isNumeric(value)) { value = "'" + value + "'";}
			newhil =newhil.replace(replace, value);
		}
		Log.e("HIL",newhil);
		return newhil;
	}
	public String evaluar(String hil) {
		Evaluator mEvaluator= new Evaluator();
		String respuesta = "";
		try {
			respuesta = mEvaluator.evaluate(hil);
		} catch (EvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return respuesta;
	}
	
	public String HasValidSQL(String str)
	{
		Pattern pattern = Pattern.compile("\\%\\{.+\\}");
		Matcher matcher = pattern.matcher(str);
		
		String lQuery = "";
		
		if(matcher.find())
		{
			lQuery = str.substring(matcher.start() + 2, matcher.end() - 1);
			
		}
		
		return lQuery;
		
	}
	
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}

}
