import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;

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
	JLabel text1 = new JLabel("",SwingConstants.CENTER);
	private final JFileChooser fileselect = new JFileChooser();

	public GUI()
	{
		TextField wiiip = new TextField("");
		JButton button5= new JButton("Browse...");
		
		JFrame frame = new JFrame("jWiiload");
		
		 JMenuBar menuBar = new JMenuBar();
		 JMenu file = new JMenu("File");
		 JMenuItem browse = new JMenuItem("Open...");

		 JMenu menu = new JMenu("Wii Options");
		 JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem("Auto-Locate");

		 JMenuItem menuItem = new JMenuItem("Change Port",
	            KeyEvent.VK_T);
		 JMenuItem menuItem2 = new JMenuItem("Set Arguments",
	            KeyEvent.VK_T);
		
		 JMenu help = new JMenu("Help");
		 JMenuItem h1 = new JMenuItem("Reset Defaults");
		 JMenuItem h2 = new JMenuItem("Online Help");
		 JMenuItem h3 = new JMenuItem("About");	
		
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

		FlowLayout fl = new FlowLayout();

		content.setLayout(fl);

		content.add(text1);

		content.add(wiiip);
		content.add(button5);

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
	public void itemStateChanged(ItemEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
