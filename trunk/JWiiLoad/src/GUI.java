import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class GUI extends JFrame implements ActionListener, ItemListener
{			
	private JLabel text1 = new JLabel("",SwingConstants.CENTER);
	private final JFileChooser fileselect = new JFileChooser();

	private JMenu menu = new JMenu("Wii Options");
	private JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem("Auto-Locate");

	private JMenuItem menuItem = new JMenuItem("Change Port",
			KeyEvent.VK_T);
	private JMenuItem menuItem2 = new JMenuItem("Set Arguments",
			KeyEvent.VK_T);
	private JMenuItem h1 = new JMenuItem("Reset Defaults");
	private JMenuItem h2 = new JMenuItem("Online Help");
	private JMenuItem h3 = new JMenuItem("About");	

	private JMenuItem browse = new JMenuItem("Open...");
	private TextField wiiip = new TextField("");
	private JButton send = new JButton("Send");
	private JButton button5= new JButton("Browse...");




	public GUI()
	{
		JMenu help = new JMenu("Help");

		JFrame frame = new JFrame("jWiiload");

		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");



		send.setEnabled(false);

		menuBar.add(file);
		menuBar.add(menu);
		menuBar.add(help);
		menu.add(cbMenuItem);
		menu.add(menuItem);

		help.add(h1);
		help.add(h2);
		help.add(h3);

		file.add(browse);

		cbMenuItem.setMnemonic(KeyEvent.VK_C);

		if (JWiiLoad.autosend)
			cbMenuItem.setSelected(true);
		else
			cbMenuItem.setSelected(false);
		menu.add(menuItem2);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		text1.setPreferredSize(new Dimension(200, 20));

		Container content = frame.getContentPane();

		browse.addActionListener(this);
		h1.addActionListener(this);
		h2.addActionListener(this);
		h3.addActionListener(this);
		
		cbMenuItem.addActionListener(this);
		send.addActionListener(this);
		menuItem.addActionListener(this);
		menuItem2.addActionListener(this);
		
		button5.addActionListener(this);


		FlowLayout fl = new FlowLayout();

		content.setLayout(fl);

		content.add(text1);

		content.add(wiiip);
		content.add(button5);
		frame.add(send);


		frame.setSize(200,400);
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setVisible(true);
		frame.setJMenuBar(menuBar);
	}

	public void setText(String s)
	{
		text1.setText(s);
	}
	
	public void setButton(boolean b)
	{
		send.setEnabled(b);
	}

	public File chooseFile()
	{
		fileselect.showOpenDialog(null);
		File filename = fileselect.getSelectedFile();

		return filename;
	}

	public int showLost()
	{
		String[] selections = {"Retry","Stop"};
		return JOptionPane.showOptionDialog(this,"No Wii found.\nIs the Homebrew Channel running?","Error",JOptionPane.ERROR_MESSAGE, 0, null,selections,null);

	}

	public int showRate()
	{
		String[] selections = {"Retry","Stop"};
		return JOptionPane.showOptionDialog(this,"Rate Limit Exceeded.\nPlease wait a little while, then try again.","Error", JOptionPane.ERROR_MESSAGE, 0, null, selections, null);

	}

	@Override
	public void itemStateChanged(ItemEvent e) {}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == h1)
		{
			JWiiLoad.lastip = null;
			JWiiLoad.autosend = true;	
			JWiiLoad.port = 4299;
			JWiiLoad.arguments = "";
			cbMenuItem.setSelected(true);
		}
		else if (e.getSource() == h2)
			goOnline("http://code.google.com/p/jwiiload/");
		else if (e.getSource() == h3)
		{
			String[] selections = {"Visit Site","Close"};
			URL url =  getClass().getResource("jwiiload.png");
			System.out.println(url);
			
			Image image1 = Toolkit.getDefaultToolkit().getImage(url);

			ImageIcon image = new ImageIcon(image1);
			
			int a = JOptionPane.showOptionDialog(this,"JWiiload 1.0\nby Ricky Ayoub (VGMoose)       ","About jWiiload", 0, 0, image, selections, null);
			if (a==0) goOnline("http://www.rickyayoub.com");
		}
		else if (e.getSource() == cbMenuItem)
			JWiiLoad.autosend = cbMenuItem.getState();
		else if (e.getSource() == browse || e.getSource() == button5)
		{
			JWiiLoad.filename = chooseFile();
			if (JWiiLoad.filename!=null)
				send.setEnabled(true);
			else
				send.setEnabled(false);
		}


	}

	public void goOnline(String site)
	{
		try {
			java.awt.Desktop.getDesktop().browse(new URI(site));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
