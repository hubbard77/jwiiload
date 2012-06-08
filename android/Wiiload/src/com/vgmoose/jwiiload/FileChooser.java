package com.vgmoose.jwiiload;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vgmoose.jwiiload.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

// FileChooser class based on the excellent tutorial at:
// http://www.dreamincode.net/forums/topic/190013-creating-simple-file-chooser/
// Seriously, it's awesome why don't more file browsers exist?

public class FileChooser extends ListActivity 
{
	private File currentDir;
	private FileArrayAdapter adapter;
	static boolean foldermode;
	String homeDir;
	boolean[] visible;
	//	private Stack<File> history;

	//	private 

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		homeDir = WiiloadActivity.homeDir;
		currentDir = new File(homeDir);
		fill(currentDir);

		if (foldermode)
		{
			boolean[] javasucks ={false,false,false,true,false};
			visible=javasucks;
		}
		else
			visible=WiiloadActivity.filetypes;
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
		WiiloadActivity.handler.sendEmptyMessage(1);
		finish();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		if (!foldermode)
		{
			menu.add(0, 1, 2, "Filetype Filter...");
			menu.add(0,2,2,"Set Home Folder");
		}
		else
		{
			menu.add(0,1,2,"Choose current folder");
			menu.add(0,2,2,"Exit Folder Select");
		}
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (!foldermode)
		{

			switch (item.getItemId()) {

			case 1:
				filter();
				return true;
			case 2:
				foldermode = true;
				Toast.makeText(this,"Choose a folder to serve as home directory.",Toast.LENGTH_LONG).show();
				Intent intent = new Intent(this, FileChooser.class);
				this.startActivity(intent);
				return true;
			}
		}
		else
		{
			switch (item.getItemId())
			{
			case 1:
				homeDir = currentDir.getPath();
				foldermode = false;
				WiiloadActivity.homeDir = homeDir;
				finish();
				return true;
			case 2:
				foldermode = false;
				finish();
				return true;
			}
		}
		return false;
	}

	public void filter()
	{
		final CharSequence[] items = {".dol Files", ".elf Files", ".zip Files", "Folders", "Everything Else"};
		final boolean[] states = visible;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Display which types of items?");
		builder.setMultiChoiceItems(items, states, new DialogInterface.OnMultiChoiceClickListener(){
			public void onClick(DialogInterface dialogInterface, int item, boolean state) {
			}
		});
		builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				SparseBooleanArray CheCked = ((AlertDialog)dialog).getListView().getCheckedItemPositions();
				if(CheCked.get(CheCked.keyAt(0)) == true){
					//	                Toast.makeText(Backup.this, "Item 1", Toast.LENGTH_SHORT).show();
				}
				if(CheCked.get(CheCked.keyAt(1)) == true){
					//	                Toast.makeText(Backup.this, "Item 2", Toast.LENGTH_SHORT).show();
				}
				if(CheCked.get(CheCked.keyAt(2)) == true){
					//	                Toast.makeText(Backup.this, "Item 3", Toast.LENGTH_SHORT).show();
				}
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		builder.create().show();
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
			{
				foldermode=false;
				return super.onKeyDown(keycode,event);
			}
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
