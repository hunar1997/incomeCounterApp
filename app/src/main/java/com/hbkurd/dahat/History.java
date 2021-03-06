package com.hbkurd.dahat;

import com.hbkurd.dahat.functions;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.*;
import java.util.*;
import android.text.*;
import android.widget.AdapterView.*;
import android.graphics.*;

public class History extends Activity
{
	functions fn = new functions();
	
	List<List<Integer>> data;
	List<List<String>> content;
	ArrayAdapter<List<String>> adapter;
	
	Button showDayButton;
	ListView historyList;
	TextView dayText;
	
	int state=0;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		
		String h = getIntent().getStringExtra("data");
		historyList = (ListView) findViewById(R.id.historyList);
		data = fn.decode(h);
		
		content = new ArrayList<List<String>>();
		
		adapter = new MyAdapter(History.this, content);
		
		historyList.setAdapter(adapter);
		
		dayText=(TextView)findViewById(R.id.dayId);
		
		showDays();
		
		showDayButton = (Button) findViewById(R.id.showDays);
		showDayButton.setVisibility(Button.GONE);
		showDayButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				showDays();
				showDayButton.setVisibility(Button.GONE);
			}
		});
		
		historyList.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> l, View v, int position, long id)
				{
					if(data.get(position).size()>0){
						if(state==0){
							showSells(position);			
							showDayButton.setVisibility(View.VISIBLE);
						}
					}
				}
			});
	}
	
	public void showDays(){
		state=0;
		content.clear();
		adapter.notifyDataSetChanged();
		
		for(int i=0;i<data.size();i++){
			int sum=0;
			
			content.add(new ArrayList<String>());
			for(int j=1;j<data.get(i).size();j++)
				sum += data.get(i).get(j);
			content.get(i).add( fn.parseDate(data.get(i).get(0)) );
			content.get(i).add( sum+"" );
		}
		
		dayText.setText("ڕۆژێک هەڵبژێرە");	
		adapter.notifyDataSetChanged();
	}
	
	public void showSells(int position){
		state=1;
		content.clear();
		adapter.notifyDataSetChanged();
		for(int i=1;i<data.get(position).size();i++){
			content.add(new ArrayList<String>());
			content.get(i-1).add(data.get(position).get(i).toString());
			content.get(i-1).add("nop");
		}
		if(data.get(position).size()<2){
			content.add(new ArrayList<String>());
			content.get(0).add("e");
			content.get(0).add("nop");
		}
		adapter.notifyDataSetChanged();
		
		dayText.setText("تۆماری "+fn.parseDate(data.get(position).get(0)));
	}
	
	public void back(View view){
		finish();
	}
}

class MyAdapter extends ArrayAdapter<List<String>>
{
	public MyAdapter(Context context, List<List<String>> values) 
	{
		super(context, R.layout.hlist, values);
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.hlist, parent, false);
		TextView status = (TextView) view.findViewById(R.id.status);
		
		String text="";
		
		if(!getItem(position).get(1).equals("nop")){
			int now = Integer.parseInt(getItem(position).get(1));
			int before = (position>0)? Integer.parseInt(getItem(position-1).get(1)) :0;
			text = now +"  ";
			
			status.setVisibility(View.VISIBLE);
		
			if(position==0){
				status.setText("");
				status.setTextColor(Color.parseColor("#000000"));
			}else if(now>before){
				status.setText("▲");
				status.setTextColor(Color.parseColor("#48ff00"));
			}else if(now<before){
				status.setText("▼");
				status.setTextColor(Color.parseColor("#ff0000"));
			}else{
				status.setText("=");
				status.setTextColor(Color.parseColor("#ffffff"));
			}
		}else{
			status.setVisibility(View.GONE);
		}
		
		text += getItem(position).get(0);
		TextView prise= (TextView) view.findViewById(R.id.thePrise);
		if(text.equals("e")){
			prise.setText("تۆمار بەتاڵە");
			view.findViewById(R.id.dinar).setVisibility(View.GONE);
		}else
			prise.setText(text);
			
		return view;
	}
}
