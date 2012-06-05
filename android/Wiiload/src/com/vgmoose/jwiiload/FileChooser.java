package com.vgmoose.jwiiload;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vgmoose.jwiiload.R;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

// FileChooser class based on the excellent tutorial at:
// http://www.dreamincode.net/forums/topic/190013-creating-simple-file-chooser/
// Seriously, it's awesome why don't more file browsers exist?

public class FileChooser extends ListActivity 
{
	private File currentDir;
	private FileArrayAdapter adapter;
//	private Stack<File> history;

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		currentDir = new File("/sdcard/");
		fill(currentDir);
//		history = new Stack<File>();
	}

	private void fill(File f)
	{
		File[]dirs = f.listFiles();
		this.setTitle(f.getAbsolutePath());
		List<Option>dir = new ArrayList<Option>();
		List<Option>fls = new ArrayList<Option>();
		try{
			for(File ff: dirs)
			{					
				if(ff.isDirectory())
					dir.add(new Option(ff.getName(),"Folder",ff.getAbsolutePath()));
				else if ((ff.getName().endsWith(".dol") || ff.getName().endsWith(".elf") || ff.getName().endsWith(".zip")) && WiiloadActivity.fext)
				{
					fls.add(new Option(ff.getName(),"File Size: "+ff.length(),ff.getAbsolutePath()));
				}
			}
		}catch(Exception e)
		{

		}
		Collections.sort(dir);
		Collections.sort(fls);
		dir.addAll(fls);
		if(!f.getName().equalsIgnoreCase(""))
			dir.add(0,new Option("..","Parent Directory",f.getParent()));

		adapter = new FileArrayAdapter(FileChooser.this,R.layout.file_view,dir);
		this.setListAdapter(adapter);

	}
	public class Option implements Comparable<Option>{
		private String name;
		private String data;
		private String path;

		public Option(String n,String d,String p)
		{
			name = n;
			data = d;
			path = p;
		}
		public String getName()
		{
			return name;
		}
		public String getData()
		{
			return data;
		}
		public String getPath()
		{
			return path;
		}
		@Override
		public int compareTo(Option o) {
			if(this.name != null)
				return this.name.toLowerCase().compareTo(o.getName().toLowerCase()); 
			else 
				throw new IllegalArgumentException();
		}
	}

	public class FileArrayAdapter extends ArrayAdapter<Option>{

		private Context c;
		private int id;
		private List<Option>items;

		public FileArrayAdapter(Context context, int textViewResourceId,
				List<Option> objects) 
		{
			super(context, textViewResourceId, objects);
			c = context;
			id = textViewResourceId;
			items = objects;
		}

		public Option getItem(int i)
		{
			return items.get(i);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(id, null);
			}
			final Option o = items.get(position);
			if (o != null) {
				TextView t1 = (TextView) v.findViewById(R.id.TextView01);
				TextView t2 = (TextView) v.findViewById(R.id.TextView02);

				if(t1!=null)
					t1.setText(o.getName());
				if(t2!=null)
					t2.setText(o.getData());

			}
			return v;
		}


	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Option o = adapter.getItem(position);
		if(o.getData().equalsIgnoreCase("folder")||o.getData().equalsIgnoreCase("parent directory")){
//				Log.d("Stack","Push "+currentDir);
//				history.push(currentDir);
				currentDir = new File(o.getPath());
				fill(currentDir);
		}
		else onFileClick(o);
	}
	
	private void onFileClick(final Option o)
    {
//    	Toast.makeText(this, "File Clicked: "+o.getName(), Toast.LENGTH_SHORT).show();
		WiiloadActivity.filename = new File(o.getPath());
		WiiloadActivity.updateName();
		finish();
    }

	
	public boolean onKeyDown(int keycode, KeyEvent event ) 
	{
		
		if(keycode == KeyEvent.KEYCODE_BACK)
		{
//			if (history.size()!=0) 
//			{
//				Log.d("Stack","Pop "+history.peek());
//				fill(history.pop());
//				return true;
//			}
			if(currentDir.getName().equalsIgnoreCase("sdcard") || currentDir.getName().equalsIgnoreCase(""))
				return super.onKeyDown(keycode,event);
			else
			{
				currentDir = new File(currentDir.getParent());
				fill(currentDir);
				return true;
			}
		}
		else return super.onKeyDown(keycode,event);  

	}


}
