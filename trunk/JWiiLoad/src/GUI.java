import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.TextField;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class GUI extends JFrame 
{	
	static JLabel textLabel = new JLabel("What.",SwingConstants.CENTER);
	static JLabel text1 = new JLabel("",SwingConstants.CENTER);
	static JLabel text2 = new JLabel("",SwingConstants.CENTER);

	static JButton button1 = new JButton("Enter IP");
	static JButton button2 = new JButton("Arguments");

	static JButton button5= new JButton("Browse...");

	static JFrame frame = new JFrame("jWiiload");
	
	static TextField wiiip = new TextField("Enter Wii IP");
	
	private static final JFileChooser fileselect = new JFileChooser();

	
	public GUI()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
	public static File chooseFile()
	{
		fileselect.showOpenDialog(null);
		File filename = fileselect.getSelectedFile();
		
		return filename;
	}
	
	public static int showLost()
	{
		String[] selections = {"Retry","Stop"};
		return JOptionPane.showOptionDialog(frame,"No Wii found.\nIs the Homebrew Channel running?","Error",JOptionPane.ERROR_MESSAGE, 0, null,selections,null);

	}
	
	public static int showRate()
	{
		String[] selections = {"Retry","Stop"};
		return JOptionPane.showOptionDialog(frame,"Rate Limit Exceeded.\nPlease wait a little while, then try again.","Error", JOptionPane.ERROR_MESSAGE, 0, null, selections, null);

	}
	
	public static void createWindow()
	{
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		text1.setPreferredSize(new Dimension(200, 20));

		Container content = frame.getContentPane();

		FlowLayout fl = new FlowLayout();

		content.setLayout(fl);

		content.add(text1);

		content.add(wiiip);
		content.add(button5);

		frame.setSize(200,400);
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setVisible(true);
	}
}
