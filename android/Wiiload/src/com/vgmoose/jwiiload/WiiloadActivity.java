package com.vgmoose.jwiiload;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.zip.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WiiloadActivity extends Activity implements OnClickListener {

	public static Context getAppContext() {
		return context;
	}

	private static Context context;

	static Socket socket;
	static String host;
	static int port = 4299;

	static Button b;
	static Button c;
	
	static String ip2;

	static String s;
	static String arguments = "";
	static File filename;

	static File compressed;
	static  boolean stopscan = false;

	static TextView status;
	static String lastip = "0.0.0.0";

	public static void tripleScan()
	{
		stopscan = false;
		for (int x=0; x<3; x++)
		{
			scan(x);
			if (host!=null)
				break;
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		RelativeLayout a = new RelativeLayout(context);
		status = new TextView(context);
		updateStatus("Ready to send data");
		b = new Button(context);
		c = new Button(context);
		b.setText("Choose File");
		c.setText("Auto-Locate");
		a.addView(b);
		a.addView(status);
		setContentView(a);
		
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		ip2 = intToIp(ipAddress);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(350, 100);
		params.topMargin = 200;

		a.addView(c,params);  //This button doesn't work, returns 127.0.0.1 address

		b.setOnClickListener(this);
		c.setOnClickListener(this);


	}

	public void onClick(View v) {
		if (v==b)
		{
			Intent intent = new Intent(this, FileChooser.class);
			this.startActivity(intent);
		}
		else if (v==c)
		{
			new Thread()
			{
				@Override
				public void run()
				{
					tripleScan();
				}
			}.start();
		}
	}

	public static void compressData()
	{
		try
		{
			// Compress the file to send it faster
			updateStatus("Compressing data...");
			compressed = compressFile(filename);
			updateStatus("Data compressed!");

		} catch(Exception e){
			// Fall back in case compressed file can't be written
			compressed = filename;
		}
	}



	public static void wiisend()
	{

		try
		{
			// Open socket to wii with host and port and setup output stream
			if (host==null)
				socket = new Socket(host, port);

			updateStatus("Talking to Wii...");

			OutputStream os = socket.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);

			updateStatus("Preparing data...");

			byte max = 0;
			byte min = 5;

			short argslength = (short) (filename.getName().length()+arguments.length()+1);

			int clength = (int) (compressed.length());  // compressed filesize
			int ulength = (int) (filename.length());        // uncompressed filesize

			// Setup input stream for sending bytes later of compressed file
			InputStream is = new FileInputStream(compressed);
			BufferedInputStream bis = new BufferedInputStream(is);

			byte b[]=new byte[128*1024];
			int numRead=0;

			updateStatus("Talking to Wii...");

			dos.writeBytes("HAXX");

			dos.writeByte(max);
			dos.writeByte(min);

			dos.writeShort(argslength);

			dos.writeInt(clength);  // writeLong() sends 8 bytes, writeInt() sends 4
			dos.writeInt(ulength);

			//dos.size();   // Number of bytes sent so far, should be 16

			updateStatus("Sending "+filename.getName());
			Log.d("NETWORK","Sending "+filename.getName()+"...");
			dos.flush();

			while ( ( numRead=bis.read(b)) > 0) {
				dos.write(b,0,numRead);
				dos.flush();
			}
			dos.flush();

			updateStatus("Talking to Wii...");
			if (arguments.length()!=0)
				Log.d("NETWORK","Sending arguments...");
			else
				Log.d("NETWORK","Finishing up...");

			dos.writeBytes(filename.getName()+"\0");

			String[] argue = arguments.split(" ");

			for (String x : argue)
				dos.writeBytes(x+"\0");

			updateStatus("All done!");
			Log.d("NETWORK","\nFile transfer successful!");

			lastip = host;

			if (compressed!=filename)
				compressed.delete();

		}
		catch (Exception ce)
		{
			updateStatus("No Wii found");
			//                    int a=0;

			if (host==null)
				host="";

			Log.d("NETWORK","No Wii found at "+host+"!");

			//                    if (!cli)
			//                    {
			//                            if (host.equals("rate"))
			//                                    a = framey.showRate();
			//                            else
			//                                    a= framey.showLost();
			//                    }
			//                  
			//                    if (a==0)
			//                    {
			//                            tripleScan();
			//                            wiisend();
			//                    }

		}
	}
	
	static void updateStatus(String s)
	{
		Log.d("STRING",s);
	}

	public String intToIp(int i) {

	   return ((i >> 24 ) & 0xFF ) + "." +
	               ((i >> 16 ) & 0xFF) + "." +
	               ((i >> 8 ) & 0xFF) + "." +
	               ( i & 0xFF) ;
	}

	static void scan(int t)
	{                       
		host=null;

		updateStatus("Finding Wii...");
		Log.d("NETWORK","Searching for a Wii...");
		String output = null;

		InetAddress localhost=null;

		try {
			localhost = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			Log.d("NETWORK","Auto-locate not supported on this system.");
			updateStatus("Auto-locate not supported.");
			System.exit(8);

			e1.printStackTrace();
		}

		// this code assumes IPv4 is used
		byte[] ip = localhost.getAddress();
		
		
		Log.d("ip2",ip2);

		for (int i = 1; i <= 254; i++)
		{
			try
			{
				ip[3] = (byte)i; 
				InetAddress address = InetAddress.getByAddress(ip);

				if (address.isReachable(10*t))
				{
					output = address.toString().substring(1);
					Log.d("NETWORK",output + " is on the network");

					// Attempt to open a socket
					try
					{
						socket = new Socket(output,port);
						Log.d("NETWORK","and is potentially a Wii!");
						updateStatus("Wii found!");
						//                                                   wiiip.setText(output);

						host=output;
						return;
					} catch (Exception e) {
						//e.printStackTrace();
					}

				}
			} catch (ConnectException e) {
				updateStatus("Rate limited");
				host="rate";
				e.printStackTrace();
				return;
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

			if (stopscan)
			{
				updateStatus("Scan aborted");
				Log.d("NETWORK","Scan aborted");
				break;
			}
		} 

		return;

	}

	public static File compressFile(File raw) throws IOException
	{
		File compressed = new File(filename+".wiiload.gz");
		InputStream in = new FileInputStream(raw);
		OutputStream out =
			new DeflaterOutputStream(new FileOutputStream(compressed));
		byte[] buffer = new byte[1000];
		int len;
		while((len = in.read(buffer)) > 0) {
			out.write(buffer, 0, len);
		}
		in.close();
		out.close();
		return compressed;
	}
}