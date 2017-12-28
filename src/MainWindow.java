
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
	
	
	
	//경로와 명령어 리스트를 담은 Properties 정의. xml 로 이루어져있다.
	static class PathList extends Properties{
		
		private String propertypath = "./pathlist.xml";
		
		private File fileobject;
		private FileInputStream pfinput;
		private FileOutputStream pfoutput;
		
		public PathList() {
			fileobject= new File(propertypath);
			
			try {
				
				//Property 파일이 있는지 체크하고 없으면 생성한다.
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
	
	//유저 설정을 담고 있는 Properties. xml로 되어 있다.
	static class SettingProperties extends Properties{
		

		
		private String propertypath = "./properties.xml";
		
		private File fileobject;
		private FileInputStream pfinput;
		private FileOutputStream pfoutput;
		

		public SettingProperties(){
			fileobject= new File(propertypath);
			try {
				
				//Property 파일이 있는지 체크하고 없으면 생성한다.
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
			
			//시작키 길이 2 / 시작키 : LShift(160) , LShift(160)
			setKey("startkeylength","2");
			setKey("startkey","162.160.");
			
			//종료키 길이 2 / 종료키 : RShift(161) , RShift(161)
			setKey("finishkeylength","2");
			setKey("finishkey","161.");
		}
		
	}//End of static class SettingProperties extends Properties

	//프로퍼티 정의
	static PathList pathlist=new PathList();
	static SettingProperties settingproperties = new SettingProperties();
	
	
	//MainFrame 이라는 클래스로 JFrame 구성
	static class MainFrame extends JFrame {
		
		//트레이 아이콘
		static SystemTray maintray = SystemTray.getSystemTray();
		static TrayIcon maintrayicon;
		

		
		
		public static PathSettingWindow pathlistwindow = new PathSettingWindow();
		public static PropertiesWindow propertieswindow = new PropertiesWindow();
		
		
		public static JLabel commandresult=new JLabel("result");
		public void setResult(String str_) {
			commandresult.setText(str_);
		}
		
		//창 구성(JFrame)
		public MainFrame(){
			

			try{
				this.setIconImage(ImageIO.read(this.getClass().getResourceAsStream("iconHR.png")));
				foldericon=new ImageIcon(getClass().getResource("searchicon.png"));
			}
			catch(IOException er){
				System.err.println(er);
			}
			
	    	this.setTitle("프로그램 실행기");
	    	this.setSize(400, 100);

	    	//창 종료 버튼을 누르면 프로그램 자체를 종료 (나중에 창만 닫는 버튼은 따로생성)
	    	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    	
	    	//트레이아이콘 적용 (트레이를 지원하는지 확인)
	    	if(SystemTray.isSupported()){
		    	this.initTray();
	    	}

	    	this.initMainFrameButton();
	    	this.setVisible(true);
			
		}//End Of MainFrame()
		
		//트레이를 클릭하거나 창을 트레이로 숨길때 창의 visible 설정
		public void toggleFrame(){
			this.setVisible(!this.isVisible());
		}
		
    	//트레이아이콘 초기설정
		private void initTray(){
			//아이콘 이미지설정 및 생성
			Image trayimage=null;
			try{
				trayimage = ImageIO.read(this.getClass().getResourceAsStream("iconHR.png"));
			}
			catch(IOException er){
				System.err.println(er);
			}
			System.out.println(this.getClass().getResourceAsStream("iconHR.png"));
			
			//TrayIcon 의 팝업메뉴 생성
			PopupMenu popup=new PopupMenu();
			
			//팝업메뉴의 종료메뉴 구성
			MenuItem exitmenu=new MenuItem("종료하기(Exit)");
			exitmenu.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					System.exit(0);
				}
			});
			
			//팝업메뉴의 창여는 메뉴 구성
			MenuItem onframe=new MenuItem("창 열기(Show Window)");
			onframe.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					toggleFrame();
				    maintray.remove(maintrayicon);
					
				}
			});
			
			//구성된 메뉴를 팝업메뉴에 등록
			popup.add(onframe);
			popup.add(exitmenu);
			
			//-------------
			
		     // TrayIcon을 생성합니다.
			maintrayicon=new TrayIcon(trayimage , "TrayName",popup);
			maintrayicon.setImageAutoSize(true);
			
		     // 트레이 아이콘 자체를 클릭했을때 일어날 이벤트에 대한 동작을 구현합니다. 현재 동작은 TrayIconFrame 윈도우가 숨겨져 있으면 
		     // 보여주고, 나타나 있으면 숨겨줍니다 :)
		     maintrayicon.addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent e) {
		             toggleFrame();
				     maintray.remove(maintrayicon);

		            }
		        });
		        
		    
		}//End Of initTray()
		
		
		//명령어 목록 버튼에 대한 액션리스너 구현 및 선언
		public class CommandListButtonListener implements ActionListener{
			public void actionPerformed(ActionEvent e_) {
				if(!pathlistwindow.isVisible()) {
					pathlistwindow.show();
				}
			}
		}
		public ActionListener clbl__=new CommandListButtonListener();
		
		//설정 창 열기 버튼에 대한 액션리스너 구현 및 선언
		public class PropertiesButtonListener implements ActionListener{
			public void actionPerformed(ActionEvent e_) {
				if(!propertieswindow.isVisible()) {
					propertieswindow.show();
				}
			}
		}
		public ActionListener prop__=new PropertiesButtonListener();
		
		//버튼 구성부
		private void initMainFrameButton(){
			
			
			JButton totraybtn=new JButton();
			JButton calllistwindow=new JButton("Command List");
			JButton button_properties=new JButton("Properties");
			
			//트레이를 지원하는 OS에서만 트레이로 가는 버튼을 생성한다.
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
			
			
			//Command List 버튼 액션리스너 추가
			
			calllistwindow.addActionListener(clbl__);
			
			//Properties 버튼 액션리스너 추가
			
			button_properties.addActionListener(prop__);

			JPanel settingpanel_ = new JPanel();
			
			commandresult.setHorizontalAlignment(0); // 0 : 가운데 정렬 (center)
			
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

	
	
	//명령어 리스트 창 구성
	static class PathSettingWindow extends JFrame {
	
		JScrollPane listwindowScroll=new JScrollPane();
		
		String columnNames[] = { "Keyword","Path/URL" , "Search"};	
		
		ListTable listTable =new ListTable( new DefaultTableModel(new Object[][] {} , columnNames) ) /*{
		
			private static final long serialVersionUID = 1L;
			
			//아래 오버라이딩을 통해서 icon 을 표현할 수 있게 한다. 사실 나도 잘 모름 인터넷에서 퍼옴.
	        public Class getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
		}*/;
		ListPanel listpanel = new ListPanel();
		ButtonPanel buttonpanel = new ButtonPanel();
		
		
		
		//ListTable 은 ListPanel 안에서 쓰일 Table의 형태이다.
		class ListTable extends JTable {
			
			DefaultTableModel dtm =(DefaultTableModel)this.getModel();
			
			
            
			//생성클래스
			public ListTable(DefaultTableModel initdtm){
				this.setModel(initdtm);
				setListFromPathList(pathlist);
				
				this.addMouseListener(new tableMouseListener_());
			}
			
			//줄 추가
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
			
			
			//ListTable 을 정렬하기 위한 클래스
			
			public void sortThis(boolean dir__) {
				//String.compareTo();
				int tablesize=this.getRowCount();
				for(int i=0;i<tablesize;i++) {
					for(int j=i+1;j<tablesize;j++) {
						if(dir__) {
							String ivalue_=(String)this.getValueAt(i, 0);
							String jvalue_=(String)this.getValueAt(j, 0);
							//비교한다
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
					
					//3번째 컬럼을 왼클릭 하면
					if(listTable.getSelectedColumn()==2 && e_.getButton()==1) {
						
						JFileChooser jfc_ = new JFileChooser();
						jfc_.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
						
						//showOpenDialog에서 확인을 누르면
						if(jfc_.showOpenDialog(new PathSettingWindow())==JFileChooser.APPROVE_OPTION) {
							
							//경로 칸의 경로를 선택한 파일의 경로로 설정
							listTable.setValueAt(jfc_.getSelectedFile().toString(), getSelectedRow(), 1);
							//이름칸이 빈칸이면 선택한 파일명 입력 //미구현
							/*if(listTable.getValueAt(getSelectedRow(),0)=="") {
	
							}*/
						}
					}
				}
			}
			

			
		}// End Of class ListTable extends JTable
		
		
		
		//ListPanel 키워드의 List 를 표현하며, 내부는 Table 로 이루어져 있다.
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

			
			//각 버튼에 대한 이벤트를 지정한다
			
			//button_OK 이벤트 지정. 리스트를 저장하고 창을 종료.
			public class OKButtonListener implements ActionListener{
				public void actionPerformed(ActionEvent e_) {
					listTable.getList();
					pathlist.storeList();
					dispose();
				}
			}
			//button_cancel 이벤트 지정.리스트를 저장하지 않고 창을 종료.
			public class CancelButtonListener implements ActionListener{
				public void actionPerformed(ActionEvent e_) {
					 dispose();
				}
			}
			
			//button_delete이벤트 지정.
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
				
				//button_OK 이벤트 지정. 리스트를 저장하고 창을 종료.
				button_OK.addActionListener(obl__);
				
				//button_cancel 이벤트 지정.리스트를 저장하지 않고 창을 종료.
				button_cancel.addActionListener(cbl__);
				
				//button_delete이벤트 지정.
				button_delete.addActionListener(dbl__);
				
				//button_add이벤트 지정.
				button_add.addActionListener(abl__);

			}
		}

		public PathSettingWindow(){
			//framepanel 이라는 패널을 만들어서 listpanel 과 buttonpanel 을 정렬한다.
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

			//KeySettingDialog 안의 버튼들에 대한 액션리스너 구현 및 선언
			
			//Set key 버튼에 대한 액션리스너 구현 및 선언
			public class SetKeyButtonListener implements ActionListener {
				public void actionPerformed(ActionEvent e_) {
					//키 설정중이 아닐 때 버튼이 눌리면:설정시작(시작키)
					if(!settingkey&&issettingstartkey) {
						
						//명령어를 입력받고있던 것들을 모두 초기화한다.
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
						
						//startstring 을 임시로 빈 Array로 만들어서 키설정중에 커맨드 받는 것을 방지.
						temp_startstring=StartString;
						StartString=new ArrayList<Integer>();
						
						//info label 의 설명 수정
						infolabel.setText("<html><p align=center>Start key(Max "+MAXSTARTSTRING+")<br>Type key to set, than click \"Complete\".</p></html>");

					}
					//키 설정중일 때 버튼이 눌리면:설정완료(시작키)
					else if(settingkey&&issettingstartkey) {
						button_setting.setText("Set Key");
						settingkey=false;
						savetempkeys();
						//info label 의 설명 수정
						infolabel.setText("<html><p align=center>Start key(Max "+MAXSTARTSTRING+")<br>Click \"OK\" to save.</p></html>");

					}
					
					
					
					//키 설정중이 아닐 때 버튼이 눌리면:설정시작(종료키)
					if(!settingkey&&!issettingstartkey) {
						
						
						//명령어를 입력받고있던 것들을 모두 초기화한다.
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
						
						//startstring 을 임시로 빈 Array로 만들어서 키설정중에 커맨드 받는 것을 방지.
						temp_startstring=StartString;
						StartString=new ArrayList<Integer>();
						
						//info label 의 설명 수정
						infolabel.setText("<html><p align=center>Finish key(Max "+MAXSTARTSTRING+")<br>Type key to set, than click \\\"Complete\\\".<br>Don't use any key that already used at start key set.</p></html>");
						
						
					}
					//키 설정중일 때 버튼이 눌리면:설정완료(종료키)
					else if(settingkey&&!issettingstartkey) {
						button_setting.setText("Set Key");
						settingkey=false;
						savetempkeys();
						//info labe의 설명 수정
						infolabel.setText("<html><p align=center>Finish key(Max "+MAXFINISHSTRING+")<br>Click \"OK\" to save.</p></html>");
					}
				}
					
			}//End of public class SetKeyButtonListener implements ActionListener
			
			//OK 버튼에 대한 액션리스너 구현 및 선언
			public class OKButtonListener implements ActionListener {
				public void actionPerformed(ActionEvent e_) {
					savetempkeys();
					settingkey=false;
					dispose();
				}
			}//End of public class OKButtonListener implements ActionListener
			
			
			public class CancelButtonListener implements ActionListener{
				public void actionPerformed(ActionEvent e_) {
					//그동안의 변경사항을 모두 롤백한다.
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
				
				
				
				//창을 닫을 때 문제가 발생하지 않도록 한다.
				class CancelWhenClose extends WindowAdapter {
					
					public void windowClosing(WindowEvent e_) {

							//그동안의 변경사항을 모두 롤백한다.
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
				
				//info label 설명 표시
				if(issettingstartkey) {
					infolabel.setText("<html><p align=center>Start key(Max "+MAXSTARTSTRING+")<br>Click \"Set key\" to set Key</p></html>");
				}
				else {
					infolabel.setText("<html><p align=center>Finish key(Max "+MAXFINISHSTRING+")<br>Click \"Set key\" to set key</p></html>");
				}
			
				//keylabel 에 기존 키 표시
				loadLabel();
				//키값을 properties에서 가져온다.
				

				//라벨이 있는 패널 구성
				JPanel toppanel=new JPanel();
				infolabel.setHorizontalAlignment(0);//0 : 가운데 정렬(center)
				keylabel.setHorizontalAlignment(0);
				infolabel.setVerticalAlignment(0);//0 : 가운데 정렬(center)
				
				toppanel.setLayout(new BorderLayout());
				toppanel.add(infolabel,"North");
				toppanel.add(keylabel,"South");

				
				//버튼이 있는 패널 구성
				JPanel bottompanel = new JPanel();
				bottompanel.add(button_setting);
				bottompanel.add(button_OK);
				bottompanel.add(button_cancel);
				
				
				//액션리스너
				button_setting.addActionListener(skbl__);
				button_OK.addActionListener(okbl__);
				button_cancel.addActionListener(cbl__);
				
				//키 설정을 위한 키입력에 대한 액션리스너
				keyboardHook.addKeyListener(new GlobalKeyAdapter() {
					
					public void keyPressed(GlobalKeyEvent ev_) {
						int keyc_=ev_.getVirtualKeyCode();
						
						//키세팅중이면 label 에 표시한다.
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
			
			
			//keylabel에 현재 properties 로부터 데이터를 받아와서 키를 표시한다.
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
			
			//설정중이던 key 를 저장한다. Complete 버튼에도 작동하고 OK 버튼에도 호출된다.
			public void savetempkeys() {
				
				//명령어를 입력받고있던 것들을 모두 초기화한다.
				CommandString=new ArrayList<Integer>();
				startinput=false;
				finishinput=false;
				nameinputstart=false;
				nameinput=false;
				
				
				String commandstring_="";
				if(temp_keystring.size()==0) {
					return;
				}
				//시작키를 저장한다.
				if(issettingstartkey) {
					
					StartString=temp_keystring;
					settingproperties.setKey("startkeylength",Integer.toString(StartString.size()));
					for(int i=0;i<StartString.size();i++) {
						commandstring_+=Integer.toString(StartString.get(i));
						commandstring_+=".";
					}
					settingproperties.setKey("startkey", commandstring_);
				}
				
				//완료키를 저장한다.
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
			
			
			//키 값을 int 로 받아서 label에 표시해줄 String 을 리턴한다.
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
				case GlobalKeyEvent.VK_BACK: rstring_="←(BS)";
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
				
				case GlobalKeyEvent.VK_UP:rstring_="▲";
				break;
				case GlobalKeyEvent.VK_LEFT:rstring_="◀";
				break;
				case GlobalKeyEvent.VK_DOWN:rstring_="▼";
				break;
				case GlobalKeyEvent.VK_RIGHT:rstring_="▶";
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
		

		
		//StartButton 의 액션리스너 구현 및 선언
		public class StartButtonListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				keysettingdialog=new KeySettingDialog(true);
				keysettingdialog.issettingstartkey=true;
				keysettingdialog.show();
			}
		}
		StartButtonListener sbl_ = new StartButtonListener();

		//Finish Button 의 액션리스너 구현 및 선언
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
			//StartString초기화. 나중에 더 나은 함수로 개선하기 바람.
			StartString.add(GlobalKeyEvent.VK_LSHIFT);
			StartString.add(GlobalKeyEvent.VK_1);
			

			//FinishString초기화. 나중에 더 나은 함수로 개선하기 바람.
			FinishString.add(GlobalKeyEvent.VK_RSHIFT);
			*/

			
			System.out.println("Global keyboard hook successfully started, press [escape] key to shutdown.");
			
			// might throw a UnsatisfiedLinkError if the native library fails to load or a RuntimeException if hooking fails 



			keyboardHook.addKeyListener(new GlobalKeyAdapter() {
				
				//명령어 비교용 커스텀 함수.
				public boolean StringCompareCheck_(ArrayList<Integer> arls1_ ,ArrayList<Integer> arls2_){
					int shorterlen=Math.min(arls1_.size(),arls2_.size());
					
					for(int i=0;i<shorterlen;i++) {
						if(arls1_.get(i)!=arls2_.get(i)) {
							return false;
						}
					}
					return true;
				}
				
				//프로그램 실행용 커스텀 함수. URL인지 파일인지 디렉토리인지 검사한다.
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
					
					//최초 키 입력을 뒤의 else if 문에 보내서 구현하였으므로 뒤쪽 else if 문부터 보는 것이 보기편함
					//if문 : 입력을 시작하는 인자를 연속해서 인식(여러 개의 키순을 인식할 수 있도록 구현하기 위해 함수가 길어짐.)
					//StartString.size()>0 부분은 키설정중일 때 입력을 받지 않기 위함.
					if(StartString.size()>0) {
					if(CommandString.size()!=0 && startinput ){
						
						
						CommandString.add(keyc);
						//현재 입력된  String 이 시작하는 인자보다 더 길어질 경우 명령을 취소시킨다.
						if(CommandString.size()>StartString.size()){
							CommandString=new ArrayList<Integer>();
							startinput=false;
						} 
						//현재 입력된 String이 기존 StartString과 불일치할 경우 명령을 취소시킨다.
						else if(StringCompareCheck_(CommandString,StartString)) {
							CommandString=new ArrayList<Integer>();
							startinput=false;
						}
						
						//StartString 과 일치할 경우 수행
						else if(CommandString.equals(StartString)){
							nameinputstart=true;
							startinput=false;
							
							CommandString=new ArrayList<Integer>();
							LinknameString=new String();
						}
						
						
					}
					
					//입력을 시작하는 가장 최초 키를 인식하면 연속적으로 인자를 인식하도록 한다.
					else if(keyc==StartString.get(0)&&!startinput){
						CommandString.add(keyc);
						startinput=true;
						finishinput=false;
						nameinputstart=false;
						nameinput=false;
						
						//시작인자 키가 하나뿐일때는 바로 동작.
						if(StartString.size()==1){
							nameinputstart=true;
							startinput=false;
							CommandString=new ArrayList<Integer>();
							LinknameString=new String();
						}
					}
					}
					
					//최초 키 입력을 뒤쪽 else if 문에 보내서 구현하였으므로 뒤쪽 else if 문부터 보는 것이 보기편함
					//입력을 끝내는 인자를 연속해서 인식(여러 개의 키순을 인식할 수 있도록 구현하기 위해 함수가 길어짐)
					//StartString.size()>0 의 경우는 키입력을 설정하고 있을 때 반응하지 않도록 하기 위한것.
					if(StartString.size()>0) {
					if(CommandString.size()!=0 && finishinput){
						CommandString.add(keyc);
						//현재 입력된 String이 끝내는 인자보다 더 길어질 경우 그대로 명령을 취소시킨다.
						if(CommandString.size()>FinishString.size()){
							CommandString=new ArrayList<Integer>();
							finishinput=false;
						}
						//현재 입력된 String이 기존FinishString과 불일치할 경우 명령을 취소시킨다.
						else if(StringCompareCheck_(CommandString,FinishString)) {
							CommandString=new ArrayList<Integer>();
							finishinput=false;
						}
						//입력이 완전히 끝날 경우
						else if(CommandString.equals(FinishString)){
							nameinput=false;
							finishinput=false;
							CommandString=new ArrayList<Integer>();
							// 입력된 string 에 따라서 파일 실행.
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
					
					//입력을 끝내는 가장 최초 키를 인식하면 연속적으로 인자를 인식하도록 한다.
					else if(keyc==FinishString.get(0)&&!finishinput&&nameinput&&StartString.size()>0){
						CommandString.add(keyc);
						finishinput=true;
						nameinput=false;
						//끝 인자 키가 하나뿐일때는 바로 동작할 수 있도록 코드를 여기에 작성해준다.
						if(FinishString.size()==1){
							nameinput=false;
							finishinput=false;
							CommandString=new ArrayList<Integer>();
							//실행부. 나중에 개선 필요.
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
					
					//입력중일 때 a~z 사이의 키값만 받아서 저장한다.
					if(nameinput && keyc>=GlobalKeyEvent.VK_A && keyc<=GlobalKeyEvent.VK_Z){

						String nstring= Character.toString((char)event.getVirtualKeyCode()).toLowerCase();
						LinknameString+=nstring;
					}
					//a~z 가 아닐 경우 0~9에 맞는지 보고 저장한다
					else if(nameinput && keyc>=GlobalKeyEvent.VK_0 && keyc<=GlobalKeyEvent.VK_9){
						String nstring=Character.toString((char)event.getVirtualKeyCode());
						LinknameString+=nstring;
					}
					//시작인자를 받았을 때 nameinput 을 true 로 만들어 문자열을 받는다.
					if(nameinputstart){
						
						mainframe.setResult("<html><p align=center>Receiving command...</p></html>");
						nameinputstart=false;
						nameinput=true;
					}

				}
				}
				//키훅 디버깅용 함수
				public void keyReleased(GlobalKeyEvent event) {
					//System.out.print(event);
				}

			});
			
			

			

	        //GlobalScreen.addNativeKeyListener(new MainWindow());

	    }

}


