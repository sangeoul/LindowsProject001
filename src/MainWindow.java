
import java.awt.*;
/*
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.PopupMenu;
*/
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.lang.Integer;
import java.lang.Math;

import java.net.URI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.imageio.ImageIO;


import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;

import java.util.Properties;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;





public class MainWindow {
	
	static GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook();
	
	static ArrayList<Integer> CommandString=new ArrayList<Integer>();
	static ArrayList<Integer> StartString=new ArrayList<Integer>();
	static ArrayList<Integer> FinishString=new ArrayList<Integer>();
	
	static int MAXSTARTSTRING=6;
	static int MAXFINISHSTRING=6;
	
	public static ArrayList<Integer> temp_savedstring=new ArrayList<Integer>();
	public static ArrayList<Integer> temp_keystring=new ArrayList<Integer>();

	static ArrayList<Integer> temp_startstring=new ArrayList<Integer>();

	static String LinknameString=new String();
	
	static boolean nameinput=false,startinput=false,finishinput=false;
	static boolean nameinputstart=false;

	public static ImageIcon foldericon;
	
	
	
	//��ο� ��ɾ� ����Ʈ�� ���� Properties ����. xml �� �̷�����ִ�.
	static class PathList extends Properties{
		
		private String propertypath = "./pathlist.xml";
		
		private File fileobject;
		private FileInputStream pfinput;
		private FileOutputStream pfoutput;
		
		public PathList() {
			fileobject= new File(propertypath);
			
			try {
				
				//Property ������ �ִ��� üũ�ϰ� ������ �����Ѵ�.
				if(!fileobject.exists()) {
					fileobject.createNewFile();
					pfoutput=new FileOutputStream(propertypath);
					this.setPath("testkey","C:\\");
					this.storeList();
				}
				
				this.loadList();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
		}
		public String getPath(String key) {
			return this.getProperty(key);
		}
		public void setPath(String key,String value) {
			this.setProperty(key, value);
		}
		
		public void loadList() {
			
			try {
				pfinput=new FileInputStream(propertypath);
				this.loadFromXML(pfinput);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		public void storeList() {
			
			try {
				pfoutput=new FileOutputStream(propertypath);
				this.storeToXML(pfoutput,"");
				
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
	}//End of static class PathList extends Properties
	
	//���� ������ ��� �ִ� Properties. xml�� �Ǿ� �ִ�.
	static class SettingProperties extends Properties{
		

		
		private String propertypath = "./properties.xml";
		
		private File fileobject;
		private FileInputStream pfinput;
		private FileOutputStream pfoutput;
		

		public SettingProperties(){
			fileobject= new File(propertypath);
			try {
				
				//Property ������ �ִ��� üũ�ϰ� ������ �����Ѵ�.
				if(!fileobject.exists()) {
					fileobject.createNewFile();
					pfoutput=new FileOutputStream(propertypath);
					this.resetProperties();
					this.saveProperties();
					
				}
				
				this.loadProperties();
				this.applyProperties();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		public String getKey(String key_) {
			return this.getProperty(key_);


		}
		public void setKey(String key_,String value_) {
			this.setProperty(key_, value_);
		}
		
		public void loadProperties() {
			

			try {
				pfinput=new FileInputStream(propertypath);
				this.loadFromXML(pfinput);
			
			}
			catch(Exception e) {
				e.printStackTrace();		
				
			}
		}
		public void applyProperties() {
			
			
			StartString=new ArrayList<Integer>();
			int startstringlength=Integer.parseInt(getKey("startkeylength"));
			StringTokenizer starttokens=new StringTokenizer(getKey("startkey"));
			for(int i=0;i<startstringlength;i++) {
				StartString.add(Integer.parseInt(starttokens.nextToken(".")));
			}
			
			FinishString=new ArrayList<Integer>();
			int finishstringlength=Integer.parseInt(getKey("finishkeylength"));
			StringTokenizer finishtokens=new StringTokenizer(getKey("finishkey"));
			for(int i=0;i<finishstringlength;i++) {
				FinishString.add(Integer.parseInt(finishtokens.nextToken(".")));
			}
		}
		public void saveProperties() {
			

			try {
				pfoutput=new FileOutputStream(propertypath);
				this.storeToXML(pfoutput,"");
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		public void resetProperties() {
			
			//����Ű ���� 2 / ����Ű : LShift(160) , LShift(160)
			setKey("startkeylength","2");
			setKey("startkey","162.160.");
			
			//����Ű ���� 2 / ����Ű : RShift(161) , RShift(161)
			setKey("finishkeylength","2");
			setKey("finishkey","161.");
		}
		
	}//End of static class SettingProperties extends Properties

	//������Ƽ ����
	static PathList pathlist=new PathList();
	static SettingProperties settingproperties = new SettingProperties();
	
	
	//MainFrame �̶�� Ŭ������ JFrame ����
	static class MainFrame extends JFrame {
		
		//Ʈ���� ������
		static SystemTray maintray = SystemTray.getSystemTray();
		static TrayIcon maintrayicon;
		

		
		
		public static PathSettingWindow pathlistwindow = new PathSettingWindow();
		public static PropertiesWindow propertieswindow = new PropertiesWindow();
		
		
		public static JLabel commandresult=new JLabel("result");
		public void setResult(String str_) {
			commandresult.setText(str_);
		}
		
		//â ����(JFrame)
		public MainFrame(){
			

			try{
				this.setIconImage(ImageIO.read(this.getClass().getResourceAsStream("iconHR.png")));
				foldericon=new ImageIcon(getClass().getResource("searchicon.png"));
			}
			catch(IOException er){
				System.err.println(er);
			}
			
	    	this.setTitle("���α׷� �����");
	    	this.setSize(400, 100);

	    	//â ���� ��ư�� ������ ���α׷� ��ü�� ���� (���߿� â�� �ݴ� ��ư�� ���λ���)
	    	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    	
	    	//Ʈ���̾����� ���� (Ʈ���̸� �����ϴ��� Ȯ��)
	    	if(SystemTray.isSupported()){
		    	this.initTray();
	    	}

	    	this.initMainFrameButton();
	    	this.setVisible(true);
			
		}//End Of MainFrame()
		
		//Ʈ���̸� Ŭ���ϰų� â�� Ʈ���̷� ���涧 â�� visible ����
		public void toggleFrame(){
			this.setVisible(!this.isVisible());
		}
		
    	//Ʈ���̾����� �ʱ⼳��
		private void initTray(){
			//������ �̹������� �� ����
			Image trayimage=null;
			try{
				trayimage = ImageIO.read(this.getClass().getResourceAsStream("iconHR.png"));
			}
			catch(IOException er){
				System.err.println(er);
			}
			System.out.println(this.getClass().getResourceAsStream("iconHR.png"));
			
			//TrayIcon �� �˾��޴� ����
			PopupMenu popup=new PopupMenu();
			
			//�˾��޴��� ����޴� ����
			MenuItem exitmenu=new MenuItem("�����ϱ�(Exit)");
			exitmenu.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					System.exit(0);
				}
			});
			
			//�˾��޴��� â���� �޴� ����
			MenuItem onframe=new MenuItem("â ����(Show Window)");
			onframe.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					toggleFrame();
				    maintray.remove(maintrayicon);
					
				}
			});
			
			//������ �޴��� �˾��޴��� ���
			popup.add(onframe);
			popup.add(exitmenu);
			
			//-------------
			
		     // TrayIcon�� �����մϴ�.
			maintrayicon=new TrayIcon(trayimage , "TrayName",popup);
			maintrayicon.setImageAutoSize(true);
			
		     // Ʈ���� ������ ��ü�� Ŭ�������� �Ͼ �̺�Ʈ�� ���� ������ �����մϴ�. ���� ������ TrayIconFrame �����찡 ������ ������ 
		     // �����ְ�, ��Ÿ�� ������ �����ݴϴ� :)
		     maintrayicon.addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent e) {
		             toggleFrame();
				     maintray.remove(maintrayicon);

		            }
		        });
		        
		    
		}//End Of initTray()
		
		
		//��ɾ� ��� ��ư�� ���� �׼Ǹ����� ���� �� ����
		public class CommandListButtonListener implements ActionListener{
			public void actionPerformed(ActionEvent e_) {
				if(!pathlistwindow.isVisible()) {
					pathlistwindow.show();
				}
			}
		}
		public ActionListener clbl__=new CommandListButtonListener();
		
		//���� â ���� ��ư�� ���� �׼Ǹ����� ���� �� ����
		public class PropertiesButtonListener implements ActionListener{
			public void actionPerformed(ActionEvent e_) {
				if(!propertieswindow.isVisible()) {
					propertieswindow.show();
				}
			}
		}
		public ActionListener prop__=new PropertiesButtonListener();
		
		//��ư ������
		private void initMainFrameButton(){
			
			
			JButton totraybtn=new JButton();
			JButton calllistwindow=new JButton("Command List");
			JButton button_properties=new JButton("Properties");
			
			//Ʈ���̸� �����ϴ� OS������ Ʈ���̷� ���� ��ư�� �����Ѵ�.
			if(SystemTray.isSupported()){
				totraybtn=new JButton("Tray");

				totraybtn.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						toggleFrame();
						try{
						maintray.add(maintrayicon);
						}
						catch (AWTException e1) 
					    {

							 e1.printStackTrace();
					    }
					}
				});
			}
			
			
			//Command List ��ư �׼Ǹ����� �߰�
			
			calllistwindow.addActionListener(clbl__);
			
			//Properties ��ư �׼Ǹ����� �߰�
			
			button_properties.addActionListener(prop__);

			JPanel settingpanel_ = new JPanel();
			
			commandresult.setHorizontalAlignment(0); // 0 : ��� ���� (center)
			
			settingpanel_.setLayout(new GridLayout(2,1));
			settingpanel_.add(calllistwindow);
			settingpanel_.add(button_properties);
			this.add(settingpanel_,"West");
			this.add(commandresult, "Center");
			if(SystemTray.isSupported()) {
				this.add(totraybtn,"East");
			}
			
		}//End Of initMainFrameButton()
		

	}//End Of MainFrame()

	
	
	//��ɾ� ����Ʈ â ����
	static class PathSettingWindow extends JFrame {
	
		JScrollPane listwindowScroll=new JScrollPane();
		
		String columnNames[] = { "Keyword","Path/URL" , "Search"};	
		
		ListTable listTable =new ListTable( new DefaultTableModel(new Object[][] {} , columnNames) ) /*{
		
			private static final long serialVersionUID = 1L;
			
			//�Ʒ� �������̵��� ���ؼ� icon �� ǥ���� �� �ְ� �Ѵ�. ��� ���� �� �� ���ͳݿ��� �ۿ�.
	        public Class getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
		}*/;
		ListPanel listpanel = new ListPanel();
		ButtonPanel buttonpanel = new ButtonPanel();
		
		
		
		//ListTable �� ListPanel �ȿ��� ���� Table�� �����̴�.
		class ListTable extends JTable {
			
			DefaultTableModel dtm =(DefaultTableModel)this.getModel();
			
			
            
			//����Ŭ����
			public ListTable(DefaultTableModel initdtm){
				this.setModel(initdtm);
				setListFromPathList(pathlist);
				
				this.addMouseListener(new tableMouseListener_());
			}
			
			//�� �߰�
			public void addRow(Object newdata[]) {
				DefaultTableModel tempdtm=(DefaultTableModel)this.getModel();
				tempdtm.addRow(newdata);
				this.changeSelection(this.getRowCount()-1,0,false,false);
				
			}
			public void deleteRow(int rownum) {
				
				DefaultTableModel tempdtm=(DefaultTableModel)this.getModel();
				tempdtm.removeRow(rownum);
				if(this.getRowCount()>0) {
					this.changeSelection(rownum,0,false,false);
				}
			}
			
			public void getList() {
				
				pathlist.clear();
				pathlist.list(System.out);
				for(int i=0;i<(this.getRowCount());i++) {
					if((String)this.getValueAt(i,0)!="") {
						pathlist.setProperty(((String)this.getValueAt(i,0)).toLowerCase(),(String)this.getValueAt(i,1) );
						}
				}
			}
			
			
			//ListTable �� �����ϱ� ���� Ŭ����
			
			public void sortThis(boolean dir__) {
				//String.compareTo();
				int tablesize=this.getRowCount();
				for(int i=0;i<tablesize;i++) {
					for(int j=i+1;j<tablesize;j++) {
						if(dir__) {
							String ivalue_=(String)this.getValueAt(i, 0);
							String jvalue_=(String)this.getValueAt(j, 0);
							//���Ѵ�
							if(ivalue_.compareTo(jvalue_)>0) {
								Object[] tempdata_=new Object[this.getColumnCount()];
								
								for(int k=0;k<this.getColumnCount();k++) {
									tempdata_[k]=this.getValueAt(i,k);
									this.setValueAt(this.getValueAt(j,k), i, k);
									this.setValueAt(tempdata_[k], j,k);
								}
								
							}
						}
						
					}
				}
			}
			
			
			public void setListFromPathList(PathList plist){
				//int listsize=Integer.parseInt(plist.getProperty("*rows"));
				Iterator<Object> check=plist.keySet().iterator();
				while(check.hasNext()) {
					String tempkey=(String)check.next();
					String tempvalue=(String)plist.getProperty(tempkey);

					try {
						this.addRow(new Object[] {tempkey,tempvalue,"Search"});
					}
					catch(Exception e_){
						System.err.println(e_);
						this.addRow(new Object[] {tempkey,tempvalue,"Er"});
					
					}
				}
				sortThis(true);
			}
			
			public void setPath(int rownum,String key_ , String value_) {
				
			}
			
			class tableMouseListener_ extends MouseAdapter {
				public void mouseClicked(MouseEvent e_) {
					
					//3��° �÷��� ��Ŭ�� �ϸ�
					if(listTable.getSelectedColumn()==2 && e_.getButton()==1) {
						
						JFileChooser jfc_ = new JFileChooser();
						jfc_.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
						
						//showOpenDialog���� Ȯ���� ������
						if(jfc_.showOpenDialog(new PathSettingWindow())==JFileChooser.APPROVE_OPTION) {
							
							//��� ĭ�� ��θ� ������ ������ ��η� ����
							listTable.setValueAt(jfc_.getSelectedFile().toString(), getSelectedRow(), 1);
							//�̸�ĭ�� ��ĭ�̸� ������ ���ϸ� �Է� //�̱���
							/*if(listTable.getValueAt(getSelectedRow(),0)=="") {
	
							}*/
						}
					}
				}
			}
			

			
		}// End Of class ListTable extends JTable
		
		
		
		//ListPanel Ű������ List �� ǥ���ϸ�, ���δ� Table �� �̷���� �ִ�.
		class ListPanel extends JPanel {
			
			int columnsnum;

			JScrollPane listScroll = new JScrollPane(listTable);
			
			public ListPanel(){
				
				pathlist.loadList();



				
				this.setLayout(new BorderLayout());
				listTable.getColumnModel().getColumn(0).setMinWidth(55);
				listTable.getColumnModel().getColumn(0).setMaxWidth(300);
				
				listTable.getColumnModel().getColumn(2).setMinWidth(50);
				listTable.getColumnModel().getColumn(2).setMaxWidth(50);
				listTable.setRowHeight(23); 
				
				this.add(listScroll);

			}
			
		}//End Of class ListPanel extends JPanel
		
		class ButtonPanel extends JPanel {
			JButton button_add = new JButton("Add");		
			JButton button_delete = new JButton("Delete");
			JButton button_OK = new JButton("OK");
			JButton button_cancel = new JButton("Cancel");

			
			//�� ��ư�� ���� �̺�Ʈ�� �����Ѵ�
			
			//button_OK �̺�Ʈ ����. ����Ʈ�� �����ϰ� â�� ����.
			public class OKButtonListener implements ActionListener{
				public void actionPerformed(ActionEvent e_) {
					listTable.getList();
					pathlist.storeList();
					dispose();
				}
			}
			//button_cancel �̺�Ʈ ����.����Ʈ�� �������� �ʰ� â�� ����.
			public class CancelButtonListener implements ActionListener{
				public void actionPerformed(ActionEvent e_) {
					 dispose();
				}
			}
			
			//button_delete�̺�Ʈ ����.
			public class DeleteButtonListener implements ActionListener{
				public void actionPerformed(ActionEvent e_) {
					if(listTable.getSelectedRow()>=0)
						 listTable.deleteRow(listTable.getSelectedRow());
				}
			}
			
			public class AddButtonListener implements ActionListener{
				public void actionPerformed(ActionEvent e_) {
					
						 listTable.addRow(new Object[] {"","","Search"});
				}
			}
			
			public ActionListener obl__=new OKButtonListener();
			public ActionListener cbl__=new CancelButtonListener();
			public ActionListener dbl__=new DeleteButtonListener();
			public ActionListener abl__=new AddButtonListener();

			
			
			public ButtonPanel() {
				
				this.add(button_add);
				this.add(button_delete);
				
				this.add(button_OK);
				this.add(button_cancel);
				
				//button_OK �̺�Ʈ ����. ����Ʈ�� �����ϰ� â�� ����.
				button_OK.addActionListener(obl__);
				
				//button_cancel �̺�Ʈ ����.����Ʈ�� �������� �ʰ� â�� ����.
				button_cancel.addActionListener(cbl__);
				
				//button_delete�̺�Ʈ ����.
				button_delete.addActionListener(dbl__);
				
				//button_add�̺�Ʈ ����.
				button_add.addActionListener(abl__);

			}
		}

		public PathSettingWindow(){
			//framepanel �̶�� �г��� ���� listpanel �� buttonpanel �� �����Ѵ�.
			JPanel framepanel = new JPanel();
			framepanel.setLayout(new BorderLayout());
			framepanel.add(listpanel);
			framepanel.add(buttonpanel,"South");
			
			
			
			this.setSize(450,300);
			this.add(framepanel);
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);	
		}
	}// End of static class PathSettingWindow extends JFrame
	
	
	static class PropertiesWindow extends JFrame{
		
		JButton button_startkey = new JButton("Setting command start key");
		JButton button_finishkey = new JButton("Setting command finish key");
		JButton button_dispose_=new JButton("Close");
		public static KeySettingDialog keysettingdialog=new KeySettingDialog(true);
		
		static class KeySettingDialog extends JFrame{
			
			JLabel infolabel=new JLabel("");
			JLabel keylabel = new JLabel("");
			String labeltext="";
			JButton button_setting = new JButton("Set key");
			JButton button_OK = new JButton("OK");
			JButton button_cancel = new JButton("Cancel");
			
			boolean settingkey=false;
			boolean issettingstartkey=true;

			//KeySettingDialog ���� ��ư�鿡 ���� �׼Ǹ����� ���� �� ����
			
			//Set key ��ư�� ���� �׼Ǹ����� ���� �� ����
			public class SetKeyButtonListener implements ActionListener {
				public void actionPerformed(ActionEvent e_) {
					//Ű �������� �ƴ� �� ��ư�� ������:��������(����Ű)
					if(!settingkey&&issettingstartkey) {
						
						//��ɾ �Է¹ް��ִ� �͵��� ��� �ʱ�ȭ�Ѵ�.
						CommandString=new ArrayList<Integer>();
						startinput=false;
						finishinput=false;
						nameinputstart=false;
						nameinput=false;
						
						button_setting.setText("Complete");
						settingkey=true;
						temp_savedstring=StartString;
						temp_keystring=new ArrayList<Integer>();
						keysettingdialog.setLabel("");
						
						//startstring �� �ӽ÷� �� Array�� ���� Ű�����߿� Ŀ�ǵ� �޴� ���� ����.
						temp_startstring=StartString;
						StartString=new ArrayList<Integer>();
						
						//info label �� ���� ����
						infolabel.setText("<html><p align=center>Start key(Max "+MAXSTARTSTRING+")<br>Type key to set, than click \"Complete\".</p></html>");

					}
					//Ű �������� �� ��ư�� ������:�����Ϸ�(����Ű)
					else if(settingkey&&issettingstartkey) {
						button_setting.setText("Set Key");
						settingkey=false;
						savetempkeys();
						//info label �� ���� ����
						infolabel.setText("<html><p align=center>Start key(Max "+MAXSTARTSTRING+")<br>Click \"OK\" to save.</p></html>");

					}
					
					
					
					//Ű �������� �ƴ� �� ��ư�� ������:��������(����Ű)
					if(!settingkey&&!issettingstartkey) {
						
						
						//��ɾ �Է¹ް��ִ� �͵��� ��� �ʱ�ȭ�Ѵ�.
						CommandString=new ArrayList<Integer>();
						startinput=false;
						finishinput=false;
						nameinputstart=false;
						nameinput=false;
						
						
						button_setting.setText("Complete");
						settingkey=true;
						temp_savedstring=FinishString;
						temp_keystring=new ArrayList<Integer>();
						setLabel("");
						
						//startstring �� �ӽ÷� �� Array�� ���� Ű�����߿� Ŀ�ǵ� �޴� ���� ����.
						temp_startstring=StartString;
						StartString=new ArrayList<Integer>();
						
						//info label �� ���� ����
						infolabel.setText("<html><p align=center>Finish key(Max "+MAXSTARTSTRING+")<br>Type key to set, than click \\\"Complete\\\".<br>Don't use any key that already used at start key set.</p></html>");
						
						
					}
					//Ű �������� �� ��ư�� ������:�����Ϸ�(����Ű)
					else if(settingkey&&!issettingstartkey) {
						button_setting.setText("Set Key");
						settingkey=false;
						savetempkeys();
						//info labe�� ���� ����
						infolabel.setText("<html><p align=center>Finish key(Max "+MAXFINISHSTRING+")<br>Click \"OK\" to save.</p></html>");
					}
				}
					
			}//End of public class SetKeyButtonListener implements ActionListener
			
			//OK ��ư�� ���� �׼Ǹ����� ���� �� ����
			public class OKButtonListener implements ActionListener {
				public void actionPerformed(ActionEvent e_) {
					savetempkeys();
					settingkey=false;
					dispose();
				}
			}//End of public class OKButtonListener implements ActionListener
			
			
			public class CancelButtonListener implements ActionListener{
				public void actionPerformed(ActionEvent e_) {
					//�׵����� ��������� ��� �ѹ��Ѵ�.
					if(issettingstartkey&&settingkey) {
						StartString=temp_savedstring;
					}
					else if(!issettingstartkey&&settingkey) {
						FinishString=temp_savedstring;
						StartString=temp_startstring;
					}
					temp_keystring=new ArrayList<Integer>();
					settingkey=false;
					dispose();
				}
			}
			
			public ActionListener skbl__=new SetKeyButtonListener();
			public ActionListener okbl__=new OKButtonListener();
			public ActionListener cbl__=new CancelButtonListener();

			
			public KeySettingDialog(boolean issk_){
				
				
				
				//â�� ���� �� ������ �߻����� �ʵ��� �Ѵ�.
				class CancelWhenClose extends WindowAdapter {
					
					public void windowClosing(WindowEvent e_) {

							//�׵����� ��������� ��� �ѹ��Ѵ�.
							if(issettingstartkey&&settingkey) {
								StartString=temp_savedstring;
							}
							else if(!issettingstartkey&&settingkey) {
								FinishString=temp_savedstring;
								StartString=temp_startstring;
							}
							temp_keystring=new ArrayList<Integer>();
							settingkey=false;
							
							//System.out.println("Debug : Closed");
							dispose();
						
					}
					
				}
				
				this.addWindowListener(new CancelWhenClose());
				
				//this.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
				
				issettingstartkey=issk_;
				
				//info label ���� ǥ��
				if(issettingstartkey) {
					infolabel.setText("<html><p align=center>Start key(Max "+MAXSTARTSTRING+")<br>Click \"Set key\" to set Key</p></html>");
				}
				else {
					infolabel.setText("<html><p align=center>Finish key(Max "+MAXFINISHSTRING+")<br>Click \"Set key\" to set key</p></html>");
				}
			
				//keylabel �� ���� Ű ǥ��
				loadLabel();
				//Ű���� properties���� �����´�.
				

				//���� �ִ� �г� ����
				JPanel toppanel=new JPanel();
				infolabel.setHorizontalAlignment(0);//0 : ��� ����(center)
				keylabel.setHorizontalAlignment(0);
				infolabel.setVerticalAlignment(0);//0 : ��� ����(center)
				
				toppanel.setLayout(new BorderLayout());
				toppanel.add(infolabel,"North");
				toppanel.add(keylabel,"South");

				
				//��ư�� �ִ� �г� ����
				JPanel bottompanel = new JPanel();
				bottompanel.add(button_setting);
				bottompanel.add(button_OK);
				bottompanel.add(button_cancel);
				
				
				//�׼Ǹ�����
				button_setting.addActionListener(skbl__);
				button_OK.addActionListener(okbl__);
				button_cancel.addActionListener(cbl__);
				
				//Ű ������ ���� Ű�Է¿� ���� �׼Ǹ�����
				keyboardHook.addKeyListener(new GlobalKeyAdapter() {
					
					public void keyPressed(GlobalKeyEvent ev_) {
						int keyc_=ev_.getVirtualKeyCode();
						
						//Ű�������̸� label �� ǥ���Ѵ�.
						if(settingkey&& temp_keystring.size()<6) {
							temp_keystring.add(keyc_);
							labeltext+=getKeyname(keyc_);
							setLabel(labeltext);
						}

					}
				});
				
				
				
				this.setSize(400,200);
				this.setLayout(new BorderLayout());
				this.add(infolabel,"North");
				this.add(keylabel);
				this.add(bottompanel,"South");
			}//End of public KeySettingDialog(boolean issk_)
			
			
			//keylabel�� ���� properties �κ��� �����͸� �޾ƿͼ� Ű�� ǥ���Ѵ�.
			public void loadLabel() {
				
				
				if(issettingstartkey) {
					try {
						
						temp_keystring=StartString;
						int keylength=Integer.parseInt(settingproperties.getKey("startkeylength"));
						StringTokenizer keyvtokens=new StringTokenizer(settingproperties.getKey("startkey"));
						for(int i=0;i<keylength;i++) {
							labeltext+=getKeyname(Integer.parseInt(keyvtokens.nextToken(".")));
						}
						
					}
					catch(Exception e_) {
					System.err.println(e_);
					}
				}
				else {
					try {
						temp_keystring=FinishString;
						int keylength=Integer.parseInt(settingproperties.getKey("finishkeylength"));
						StringTokenizer keyvtokens=new StringTokenizer(settingproperties.getKey("finishkey"));
						for(int i=0;i<keylength;i++) {
							labeltext+=getKeyname(Integer.parseInt(keyvtokens.nextToken(".")));
						}
				
					}
					catch(Exception e_) {
						System.err.println(e_);
					}
				}
				
				setLabel(labeltext);
			}//End of public void loadLabel()
			
			
			public void setLabel(String str_) {
				keylabel.setText(str_);
				labeltext=str_;
			}
			
			//�������̴� key �� �����Ѵ�. Complete ��ư���� �۵��ϰ� OK ��ư���� ȣ��ȴ�.
			public void savetempkeys() {
				
				//��ɾ �Է¹ް��ִ� �͵��� ��� �ʱ�ȭ�Ѵ�.
				CommandString=new ArrayList<Integer>();
				startinput=false;
				finishinput=false;
				nameinputstart=false;
				nameinput=false;
				
				
				String commandstring_="";
				if(temp_keystring.size()==0) {
					return;
				}
				//����Ű�� �����Ѵ�.
				if(issettingstartkey) {
					
					StartString=temp_keystring;
					settingproperties.setKey("startkeylength",Integer.toString(StartString.size()));
					for(int i=0;i<StartString.size();i++) {
						commandstring_+=Integer.toString(StartString.get(i));
						commandstring_+=".";
					}
					settingproperties.setKey("startkey", commandstring_);
				}
				
				//�Ϸ�Ű�� �����Ѵ�.
				else if(!issettingstartkey) {
					FinishString=temp_keystring;
					if(settingkey) {
						StartString=temp_startstring;
					}
					settingproperties.setKey("finishkeylength",Integer.toString(FinishString.size()));
					for(int i=0;i<FinishString.size();i++) {
						commandstring_+=Integer.toString(FinishString.get(i));
						commandstring_+=".";
					}
					settingproperties.setKey("finishkey", commandstring_);
				}
				
				settingproperties.saveProperties();
			}//End of savetempkeys()
			
			
			//Ű ���� int �� �޾Ƽ� label�� ǥ������ String �� �����Ѵ�.
			public String getKeyname(int keyc_) {
				
				String rstring_="";
				if(keyc_>=48 && keyc_<=58) {
					rstring_=Character.toString((char)keyc_);
				}
				else if(keyc_>=GlobalKeyEvent.VK_A && keyc_<=GlobalKeyEvent.VK_Z) {
					rstring_=Character.toString((char)keyc_).toLowerCase();
				}
				else if(keyc_>=GlobalKeyEvent.VK_NUMPAD0 && keyc_<=GlobalKeyEvent.VK_NUMPAD9) {
					rstring_=Character.toString((char)(keyc_-48))+"(NP)";
				}
				
				switch (keyc_) {
					
				case GlobalKeyEvent.VK_ESCAPE: rstring_="Esc";
				break;
				
				case GlobalKeyEvent.VK_F1: rstring_="F1";
				break;
				case GlobalKeyEvent.VK_F2: rstring_="F2";
				break;
				case GlobalKeyEvent.VK_F3: rstring_="F3";
				break;
				case GlobalKeyEvent.VK_F4: rstring_="F4";
				break;
				case GlobalKeyEvent.VK_F5: rstring_="F5";
				break;
				case GlobalKeyEvent.VK_F6: rstring_="F6";
				break;
				case GlobalKeyEvent.VK_F7: rstring_="F7";
				break;
				case GlobalKeyEvent.VK_F8: rstring_="F8";
				break;
				case GlobalKeyEvent.VK_F9: rstring_="F9";
				break;
				case GlobalKeyEvent.VK_F10: rstring_="F10";
				break;
				case GlobalKeyEvent.VK_F11: rstring_="F11";
				break;
				case GlobalKeyEvent.VK_F12: rstring_="F12";
				break;
				case GlobalKeyEvent.VK_OEM_MINUS: rstring_="-";
				break;
				case GlobalKeyEvent.VK_OEM_PLUS: rstring_="=";
				break;
				case GlobalKeyEvent.VK_BACK: rstring_="��(BS)";
				break;
				
				
				case GlobalKeyEvent.VK_TAB: rstring_="TAB";
				break;
				case GlobalKeyEvent.VK_OEM_4:rstring_="[";
				break;
				case GlobalKeyEvent.VK_OEM_6:rstring_="]";
				break;
				case GlobalKeyEvent.VK_RETURN:rstring_="Enter";
				break;
				
				
				case GlobalKeyEvent.VK_CAPITAL:rstring_="CpsLck";
				break;
				case GlobalKeyEvent.VK_OEM_1:rstring_=";";
				break;
				case GlobalKeyEvent.VK_OEM_7:rstring_="'";
				break;
				case GlobalKeyEvent.VK_OEM_5:rstring_="\\";
				break;
				

				case GlobalKeyEvent.VK_LSHIFT:rstring_="LSh";
				break;
				case GlobalKeyEvent.VK_OEM_COMMA:rstring_=",";
				break;
				case GlobalKeyEvent.VK_OEM_PERIOD:rstring_=".";
				break;
				case GlobalKeyEvent.VK_OEM_2:rstring_="/";
				break;
				case GlobalKeyEvent.VK_RSHIFT:rstring_="RSh";
				break;
				
				
				case GlobalKeyEvent.VK_LCONTROL:rstring_="LCtl";
				break;
				case GlobalKeyEvent.VK_LWIN:rstring_="Win";
				break;
				case GlobalKeyEvent.VK_LMENU:rstring_="LAlt";
				break;
				case GlobalKeyEvent.VK_SPACE: rstring_="Sp";
				break;
				case GlobalKeyEvent.VK_RMENU:rstring_="RAlt";
				break;
				case GlobalKeyEvent.VK_APPS:rstring_="Menu";
				break;
				case GlobalKeyEvent.VK_HANJA:rstring_="Han";
				break;
				case GlobalKeyEvent.VK_RCONTROL:rstring_="RCtl";
				break;
				
				
				case GlobalKeyEvent.VK_SNAPSHOT: rstring_="PrtScn";
				break;
				case GlobalKeyEvent.VK_SCROLL: rstring_="ScrLck";
				break;
				case GlobalKeyEvent.VK_PAUSE: rstring_="PsBrk";
				break;
				

				case GlobalKeyEvent.VK_INSERT:rstring_="Ins";
				break;
				case GlobalKeyEvent.VK_DELETE:rstring_="Del";
				break;
				case GlobalKeyEvent.VK_HOME:rstring_="Home";
				break;
				case GlobalKeyEvent.VK_END:rstring_="End";
				break;
				case GlobalKeyEvent.VK_PRIOR:rstring_="PgUp";
				break;
				case GlobalKeyEvent.VK_NEXT:rstring_="PgDn";
				break;
				
				case GlobalKeyEvent.VK_UP:rstring_="��";
				break;
				case GlobalKeyEvent.VK_LEFT:rstring_="��";
				break;
				case GlobalKeyEvent.VK_DOWN:rstring_="��";
				break;
				case GlobalKeyEvent.VK_RIGHT:rstring_="��";
				break;

				
				case GlobalKeyEvent.VK_NUMLOCK:rstring_="NLck";
				break;
				case GlobalKeyEvent.VK_DIVIDE:rstring_="/(NP)";
				break;
				case GlobalKeyEvent.VK_MULTIPLY:rstring_="*(NP)";
				break;
				case GlobalKeyEvent.VK_SUBTRACT:rstring_="-(NP)";
				break;
				case GlobalKeyEvent.VK_ADD:rstring_="+(NP)";
				break;
				case GlobalKeyEvent.VK_DECIMAL:rstring_=".(NP)";
				break;
					
				}
				return "["+rstring_+"]";
			}
			

			
		}// End of class KeySettingDialog extends JFrame
		

		
		//StartButton �� �׼Ǹ����� ���� �� ����
		public class StartButtonListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				keysettingdialog=new KeySettingDialog(true);
				keysettingdialog.issettingstartkey=true;
				keysettingdialog.show();
			}
		}
		StartButtonListener sbl_ = new StartButtonListener();

		//Finish Button �� �׼Ǹ����� ���� �� ����
		public class FinishButtonListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				keysettingdialog=new KeySettingDialog(false);
				keysettingdialog.issettingstartkey=false;
				keysettingdialog.show();
			}
		}
		FinishButtonListener fbl_ = new FinishButtonListener();
		

		
		public PropertiesWindow() {
			

			
			button_startkey.addActionListener(sbl_);
			button_finishkey.addActionListener(fbl_);
			button_dispose_.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e_) {
							dispose();
						}
					
			});
			
			this.setLayout(new GridLayout(3,1,10,10));
			this.add(button_startkey);
			this.add(button_finishkey);
			this.add(button_dispose_);
			this.setSize(300,150);
			

		}
		
	}//End of static class PropertiesWindow extends JFrame
	
	static MainFrame mainframe=new MainFrame();
	
	
	

	
  	public static void main(String[] args) {

  			    	
	    	
  			
	    	//PathSettingWindow listwindow = new PathSettingWindow();
			
  		
  		/*
			//StartString�ʱ�ȭ. ���߿� �� ���� �Լ��� �����ϱ� �ٶ�.
			StartString.add(GlobalKeyEvent.VK_LSHIFT);
			StartString.add(GlobalKeyEvent.VK_1);
			

			//FinishString�ʱ�ȭ. ���߿� �� ���� �Լ��� �����ϱ� �ٶ�.
			FinishString.add(GlobalKeyEvent.VK_RSHIFT);
			*/

			
			System.out.println("Global keyboard hook successfully started, press [escape] key to shutdown.");
			
			// might throw a UnsatisfiedLinkError if the native library fails to load or a RuntimeException if hooking fails 



			keyboardHook.addKeyListener(new GlobalKeyAdapter() {
				
				//��ɾ� �񱳿� Ŀ���� �Լ�.
				public boolean StringCompareCheck_(ArrayList<Integer> arls1_ ,ArrayList<Integer> arls2_){
					int shorterlen=Math.min(arls1_.size(),arls2_.size());
					
					for(int i=0;i<shorterlen;i++) {
						if(arls1_.get(i)!=arls2_.get(i)) {
							return false;
						}
					}
					return true;
				}
				
				//���α׷� ����� Ŀ���� �Լ�. URL���� �������� ���丮���� �˻��Ѵ�.
				public void executepath(String path_){
					
					if(path_.contains("http://") || path_.contains("https://")){
						if(Desktop.isDesktopSupported())
						{
							try {
								Desktop.getDesktop().browse(new URI(path_));
							}
							catch(Exception e_) {
								System.err.println(e_);
							}
						}
					}

					
					File thisfile_=new File(path_);
					
					if(thisfile_.isDirectory()) {
						try {
							Process oProcess = new ProcessBuilder("explorer",path_).start();
						}
						catch(Exception e_) {
							System.err.println(e_);
						}
					}
					else{
						try {
						Process oProcess = new ProcessBuilder("cmd","/c",path_).start();
						}
						catch(Exception e_) {
							System.err.println(e_);
						}
					}

					
					
					
					
				}
				@Override
				public void keyPressed(GlobalKeyEvent event) {
					
					
					int keyc=event.getVirtualKeyCode();

					System.out.println(keyc);
					
					//���� Ű �Է��� ���� else if ���� ������ �����Ͽ����Ƿ� ���� else if ������ ���� ���� ��������
					//if�� : �Է��� �����ϴ� ���ڸ� �����ؼ� �ν�(���� ���� Ű���� �ν��� �� �ֵ��� �����ϱ� ���� �Լ��� �����.)
					//StartString.size()>0 �κ��� Ű�������� �� �Է��� ���� �ʱ� ����.
					if(StartString.size()>0) {
					if(CommandString.size()!=0 && startinput ){
						
						
						CommandString.add(keyc);
						//���� �Էµ�  String �� �����ϴ� ���ں��� �� ����� ��� ����� ��ҽ�Ų��.
						if(CommandString.size()>StartString.size()){
							CommandString=new ArrayList<Integer>();
							startinput=false;
						} 
						//���� �Էµ� String�� ���� StartString�� ����ġ�� ��� ����� ��ҽ�Ų��.
						else if(StringCompareCheck_(CommandString,StartString)) {
							CommandString=new ArrayList<Integer>();
							startinput=false;
						}
						
						//StartString �� ��ġ�� ��� ����
						else if(CommandString.equals(StartString)){
							nameinputstart=true;
							startinput=false;
							
							CommandString=new ArrayList<Integer>();
							LinknameString=new String();
						}
						
						
					}
					
					//�Է��� �����ϴ� ���� ���� Ű�� �ν��ϸ� ���������� ���ڸ� �ν��ϵ��� �Ѵ�.
					else if(keyc==StartString.get(0)&&!startinput){
						CommandString.add(keyc);
						startinput=true;
						finishinput=false;
						nameinputstart=false;
						nameinput=false;
						
						//�������� Ű�� �ϳ����϶��� �ٷ� ����.
						if(StartString.size()==1){
							nameinputstart=true;
							startinput=false;
							CommandString=new ArrayList<Integer>();
							LinknameString=new String();
						}
					}
					}
					
					//���� Ű �Է��� ���� else if ���� ������ �����Ͽ����Ƿ� ���� else if ������ ���� ���� ��������
					//�Է��� ������ ���ڸ� �����ؼ� �ν�(���� ���� Ű���� �ν��� �� �ֵ��� �����ϱ� ���� �Լ��� �����)
					//StartString.size()>0 �� ���� Ű�Է��� �����ϰ� ���� �� �������� �ʵ��� �ϱ� ���Ѱ�.
					if(StartString.size()>0) {
					if(CommandString.size()!=0 && finishinput){
						CommandString.add(keyc);
						//���� �Էµ� String�� ������ ���ں��� �� ����� ��� �״�� ����� ��ҽ�Ų��.
						if(CommandString.size()>FinishString.size()){
							CommandString=new ArrayList<Integer>();
							finishinput=false;
						}
						//���� �Էµ� String�� ����FinishString�� ����ġ�� ��� ����� ��ҽ�Ų��.
						else if(StringCompareCheck_(CommandString,FinishString)) {
							CommandString=new ArrayList<Integer>();
							finishinput=false;
						}
						//�Է��� ������ ���� ���
						else if(CommandString.equals(FinishString)){
							nameinput=false;
							finishinput=false;
							CommandString=new ArrayList<Integer>();
							// �Էµ� string �� ���� ���� ����.
							//try{
								if(pathlist.getPath(LinknameString)!=null) {
									executepath(pathlist.getPath(LinknameString));
								//Process oProcess = new ProcessBuilder("cmd","/c",pathlist.getPath(LinknameString)).start();
								}
							//}
							//catch (IOException e){
							//	System.err.println(e);
							//}
							System.out.println(pathlist.getPath(LinknameString));
							mainframe.setResult("<html><p align=center>"+LinknameString+"<br>"+pathlist.getPath(LinknameString)+"</p></html>");
							LinknameString=new String();
						}
					}
					
					//�Է��� ������ ���� ���� Ű�� �ν��ϸ� ���������� ���ڸ� �ν��ϵ��� �Ѵ�.
					else if(keyc==FinishString.get(0)&&!finishinput&&nameinput&&StartString.size()>0){
						CommandString.add(keyc);
						finishinput=true;
						nameinput=false;
						//�� ���� Ű�� �ϳ����϶��� �ٷ� ������ �� �ֵ��� �ڵ带 ���⿡ �ۼ����ش�.
						if(FinishString.size()==1){
							nameinput=false;
							finishinput=false;
							CommandString=new ArrayList<Integer>();
							//�����. ���߿� ���� �ʿ�.
							//try{
							if(pathlist.getPath(LinknameString)!=null) {
								executepath(pathlist.getPath(LinknameString));
							//Process oProcess = new ProcessBuilder("cmd","/c",pathlist.getPath(LinknameString)).start();
							}
						//}
						//catch (IOException e){
						//	System.err.println(e);
						//}
							System.out.println(pathlist.getPath(LinknameString));
							if(pathlist.getPath(LinknameString)!=null) {
								mainframe.setResult("<html><p align=center>"+LinknameString+"<br>"+pathlist.getPath(LinknameString)+"</p></html>");
							}
							else {
								mainframe.setResult("<html><p align=center>There is no path matching with:<br>"+LinknameString+"</p></html>");
							}
							LinknameString=new String();
						}
					}
					
					//�Է����� �� a~z ������ Ű���� �޾Ƽ� �����Ѵ�.
					if(nameinput && keyc>=GlobalKeyEvent.VK_A && keyc<=GlobalKeyEvent.VK_Z){

						String nstring= Character.toString((char)event.getVirtualKeyCode()).toLowerCase();
						LinknameString+=nstring;
					}
					//a~z �� �ƴ� ��� 0~9�� �´��� ���� �����Ѵ�
					else if(nameinput && keyc>=GlobalKeyEvent.VK_0 && keyc<=GlobalKeyEvent.VK_9){
						String nstring=Character.toString((char)event.getVirtualKeyCode());
						LinknameString+=nstring;
					}
					//�������ڸ� �޾��� �� nameinput �� true �� ����� ���ڿ��� �޴´�.
					if(nameinputstart){
						
						mainframe.setResult("<html><p align=center>Receiving command...</p></html>");
						nameinputstart=false;
						nameinput=true;
					}

				}
				}
				//Ű�� ������ �Լ�
				public void keyReleased(GlobalKeyEvent event) {
					//System.out.print(event);
				}

			});
			
			

			

	        //GlobalScreen.addNativeKeyListener(new MainWindow());

	    }

}


