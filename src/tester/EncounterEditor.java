package tester;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class EncounterEditor implements ComponentListener, ActionListener {
	public static final int WindowWidth = 1200;
	public static final int WindowHeight = 1000;
	
	JLabel p2Label = new JLabel();
	JPanel optionsPanel, rightPanel, subOptionsPanel;
	Font labelFont;
	JTextArea introductoryText=new JTextArea(20,10);
	JTextArea[] optionsTextArea = new JTextArea[EncounterEngine.cOptions];
	JTextArea[] reactionsTextArea = new JTextArea[EncounterEngine.cReactions];
	JTextArea nothingBurgerArea;
	JFrame myFrame;
	JCheckBox[] antagonistCheckBox, protagonistCheckBox;
	JRadioButton[] antagonistRadio, protagonistRadio;
	ButtonGroup antagonistGroup, protagonistGroup;
	JButton saveButton, authorButton, testButton;
	JDialog testDialog;
	JFrame encounterSelector;
	JScrollPane eSelectorScrollPane;
	JPanel eSelectorPanel;
	JList<String> eSelectorJList;
	JPopupMenu pronounMenu;

	JScrollPane textScrollPane;
	JSpinner firstDaySpinner, lastDaySpinner;
	
	EncounterEditor theEditor;
	EncounterEngine theEngine;
	static boolean quitFlag, userInput;
	int iEncounter, iOption, iReaction;
	int thisDay;
	ArrayList<EncounterEngine.Encounter> encounters = new ArrayList<EncounterEngine.Encounter>();
	EncounterEngine.Encounter theEncounter;
	EncounterEngine.Option theOption;
	EncounterEngine.Reaction theReaction;
	String[] actorLabel = new String[EncounterEngine.cActors];
	FormatStuff commonFormat = new FormatStuff();	
	Color encounterColor, optionsColor, reactionColor, hilightColor;
	ArrayList<String> encounterTitles = new ArrayList<String>();
	String[] eTitlesArray;
	String[] factor = new String[12];
	String[] pronoun = new String[7];
	SillyPanel[] reactionPanel = new SillyPanel[EncounterEngine.cReactions];
	DeltaPanel deltaPBad_Good;
	DeltaPanel deltaPFalse_Honest;
	DeltaPanel deltaPTimid_Dominant;
	String fileName, theAbsolutePath;
	int theProtagonist, theAntagonist;
	ArrayList<Actor> actors;
	String addButtonType;
	int iDot, iMark;
	JTextArea selectedTextArea;
	float versionNumber;
	boolean braceFlag;
	
	Doodad encounterDoodad;
	OtherDoodad prerequisitesDoodad, disqualifiersDoodad;
	
// **********************************************************************
	public EncounterEditor() {
		quitFlag = false;
		}
// **********************************************************************
   ActionListener menuGuy = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
   		for (int i=0; (i<6); ++i) {
  			String thePronoun = e.getActionCommand();
  			if (e.getActionCommand().equals(pronoun[i])) {
  				selectedTextArea.replaceRange(thePronoun, iDot, iMark);
  			}
  		}
      }
    };
	MouseListener pronounGuy = new MouseListener()  {
			public void mouseClicked(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger() & ((e.getButton()==MouseEvent.BUTTON2) | (e.getButton()==MouseEvent.BUTTON3))) {
					pronounMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
			public void mouseReleased(MouseEvent e) {
				pronounMenu.setVisible(false);
			}
			public void mouseEntered(MouseEvent e) { }
			public void mouseExited(MouseEvent e) { }
		};
	CaretListener caretGuy = new CaretListener()  {
		public void caretUpdate(CaretEvent e) {
			if (userInput) {
				iDot = e.getDot();
				iMark = e.getMark();
				if (iMark<iDot) {
					int saveInt = iMark;
					iMark = iDot;
					iDot = saveInt;
				}
				selectedTextArea = (JTextArea)e.getSource();
			}
		}                                                                       
	};
	KeyListener keyGuy = new KeyListener() {
		public void keyPressed(KeyEvent e) {}
		public void keyReleased(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {
			if (braceFlag) {
				String thePronoun = "";
				switch (e.getKeyChar()) {
					case 'A': { thePronoun = pronoun[0]; break; }
					case 'P': { thePronoun = pronoun[1]; break; }
					case 'N': { thePronoun = pronoun[2]; break; }
					case 'O': { thePronoun = pronoun[3]; break; }
					case 'G': { thePronoun = pronoun[4]; break; }
					case 'J': { thePronoun = pronoun[5]; break; }
					case 'R': { thePronoun = pronoun[6]; break; }
				}
				selectedTextArea.replaceRange(thePronoun, iDot-1, iMark+1);
			}
			
			braceFlag = (e.getKeyChar() == '{');
		}
	};
// -----------------------------------------------------------------------
	public void initialize() {
		myFrame = new JFrame();
		versionNumber = 1.00f;
		myFrame.setTitle("Encounter Editor Version 1.01");
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myFrame.setSize(WindowWidth, WindowHeight);
		myFrame.setBackground(Color.white);
		myFrame.setVisible(true);
//		labelFont = new Font("Arial", Font.BOLD, 18);
		encounterColor = new Color(255,232,229);
		optionsColor = new Color(239,247,232);
		reactionColor = new Color(220,246,255);
		hilightColor = new Color(255, 180, 255);
		theEngine = new EncounterEngine();
		
		iEncounter = 0;
		iOption = 0;
		iReaction = 0;
		thisDay = 1;
		userInput = true;
		fileName = "";
		actors = new ArrayList<Actor>();
		selectedTextArea = null;
		iDot = -1;
		iMark = -1;
		
		actorLabel[0] = "Camiggdo";
		actorLabel[1] = "Skordokott";
		actorLabel[2] = "Zubenelgenubi";
		actorLabel[3] = "Koopie";
		actorLabel[4] = "Subotai";
		
		factor[0] = "Bad_Good";
		factor[1] = "False_Honest";
		factor[2] = "Timid_Dominant";
		factor[3] = "pBad_Good";
		factor[4] = "pFalse_Honest";
		factor[5] = "pTimid_Dominant";
		factor[6] = "-Bad_Good";
		factor[7] = "-False_Honest";
		factor[8] = "-Timid_Dominant";
		factor[9] = "-pBad_Good";
		factor[10] = "-pFalse_Honest";
		factor[11] = "-pTimid_Dominant";
		
		pronoun[0] = "{AntagonistName}";
		pronoun[1] = "{ProtagonistName}";
		pronoun[2] = "{he/she}";
		pronoun[3] = "{him/her}";
		pronoun[4] = "{his/hers}";
		pronoun[5] = "{his/her}";
		pronoun[6] = "{himself/herself}";
		
		antagonistCheckBox = new JCheckBox[EncounterEngine.cActors];
		protagonistCheckBox = new JCheckBox[EncounterEngine.cActors];
		
		antagonistRadio = new JRadioButton[EncounterEngine.cActors];
		protagonistRadio = new JRadioButton[EncounterEngine.cActors];
		
		pronounMenu = new JPopupMenu();
		JMenuItem tempJMenuItem;
		for (int i=0; (i<6); ++i) {
			tempJMenuItem = new JMenuItem(pronoun[i]);
			tempJMenuItem.addActionListener(menuGuy);
			pronounMenu.add(tempJMenuItem);
		}
		pronounMenu.setVisible(false);
		pronounMenu.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}			
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
			public void popupMenuCanceled(PopupMenuEvent e) {}
			});
				
		testDialog = new JDialog(myFrame, "test settings");
		testDialog.getContentPane().setLayout(new FlowLayout());
		testDialog.setVisible(false);
		testDialog.setLocation(400,400);
		testDialog.setSize(300,200);
		testDialog.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent e) {} 
			public void windowClosed(WindowEvent e) {} 
			public void windowClosing(WindowEvent e) {
				for (int i = 0; (i<EncounterEngine.cReactions); ++i) {
					reactionsTextArea[i].setBackground(Color.white);
				}
			} 
			public void windowDeactivated(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {} 
			public void windowIconified(WindowEvent e) {} 
			public void windowOpened(WindowEvent e) {
				theProtagonist = -1;
				theAntagonist = -1;
			} 
		});
		JPanel protagonistPanel = new JPanel();
		protagonistPanel.setSize(150,200);
		protagonistPanel.setLayout(new BoxLayout(protagonistPanel, BoxLayout.Y_AXIS));
		protagonistPanel.add(new JLabel("Protagonist"));
		JPanel antagonistPanel = new JPanel();
		antagonistPanel.setSize(150,200);
		antagonistPanel.setLayout(new BoxLayout(antagonistPanel, BoxLayout.Y_AXIS));
		antagonistPanel.add(new JLabel("Antagonist"));
		
		protagonistGroup = new ButtonGroup();
		antagonistGroup = new ButtonGroup();

		for (int i=0; (i<EncounterEngine.cActors); ++i) {
			antagonistRadio[i] = new JRadioButton(actorLabel[i]);	
			antagonistGroup.add(antagonistRadio[i]);
			antagonistRadio[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String temp = e.getActionCommand();
					int selectedIndex = -1;
					for (int i=0; (i<EncounterEngine.cActors); ++i) {
						if (temp.equals(actorLabel[i]))
							selectedIndex = i;
					}
					theAntagonist = selectedIndex;
					for (int i=0; (i<EncounterEngine.cActors); ++i) {
						protagonistRadio[i].setEnabled(true);
					}
					protagonistRadio[selectedIndex].setEnabled(false);
					runTest();
				}
			});
			antagonistPanel.add(antagonistRadio[i]);
			
			protagonistRadio[i] = new JRadioButton(actorLabel[i]);
			protagonistGroup.add(protagonistRadio[i]);
			protagonistRadio[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String temp = e.getActionCommand();
					int selectedIndex = -1;
					for (int i=0; (i<EncounterEngine.cActors); ++i) {
						if (temp.equals(actorLabel[i]))
							selectedIndex = i;
					}
					theProtagonist = selectedIndex; 
					for (int i=0; (i<EncounterEngine.cActors); ++i) {
						antagonistRadio[i].setEnabled(true);
					}
					antagonistRadio[selectedIndex].setEnabled(false);
					runTest();
				}
			});
			protagonistPanel.add(protagonistRadio[i]);
		}
		testDialog.add(protagonistPanel);
		testDialog.add(antagonistPanel);

		for (int i=0; (i<EncounterEngine.cActors); ++i) {
			antagonistCheckBox[i] = new JCheckBox(actorLabel[i]);			
			antagonistCheckBox[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String temp = e.getActionCommand();
					int selectedIndex = -1;
					for (int i=0; (i<EncounterEngine.cActors); ++i) {
						if (temp.equals(actorLabel[i]))
							selectedIndex = i;
					}
					theEncounter.setIsAllowedToBeAntagonist(!antagonistCheckBox[selectedIndex].isSelected(), selectedIndex);
				}
			});
			
			protagonistCheckBox[i] = new JCheckBox(actorLabel[i]);
			protagonistCheckBox[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String temp = e.getActionCommand();
					int selectedIndex = -1;
					for (int i=0; (i<EncounterEngine.cActors); ++i) {
						if (temp.equals(actorLabel[i]))
							selectedIndex = i;
					}
					theEncounter.setIsAllowedToBeProtagonist(!protagonistCheckBox[selectedIndex].isSelected(), selectedIndex);
				}
			});
		}

		JPanel outermostPanel = new JPanel();
		outermostPanel.setLayout(new BoxLayout(outermostPanel, BoxLayout.X_AXIS));
		outermostPanel.setVisible(true);
		outermostPanel.setBackground(Color.white);
		
		authorButton = new JButton("Author");
		authorButton.setActionCommand("author");
		authorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("author")) {
	      		  String newAuthor = (String)JOptionPane.showInputDialog(myFrame, (Object)"Author: "+theEncounter.getAuthor());
	      		  if (newAuthor!=null) {
	      			  if (newAuthor.length()>19) 
	      				  newAuthor = newAuthor.substring(0,18);
	      			  theEncounter.setAuthor(newAuthor);
	      			  authorButton.setText("by "+newAuthor);
	      		  }
				}
			}
		});
		
		saveButton = new JButton("Save");
		saveButton.setMnemonic(KeyEvent.VK_S);
		saveButton.setActionCommand("save");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("save")) {
					JFileChooser chooser = new JFileChooser(theAbsolutePath);
					File bFile = null;
					 int returnVal = chooser.showSaveDialog(myFrame);
					 if (returnVal == JFileChooser.APPROVE_OPTION) {
						 String temp2 = chooser.getSelectedFile().getPath();
						 if (!temp2.endsWith(".xml"))
							 temp2+=".xml";
						 takeDownOldEncounter();
					    bFile = new File(temp2);
					    saveEncounters(bFile.getAbsolutePath());
					 }
				}
			}
		});

		testButton = new JButton("Test");
		testButton.setActionCommand("test");
		testButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("test")) {
					testDialog.setVisible(true);
				}
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setVisible(true);
		buttonPanel.setBackground(Color.white);
		buttonPanel.setMaximumSize(new Dimension(240, 25));

		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(saveButton);
		buttonPanel.add(Box.createHorizontalStrut(30));
		buttonPanel.add(testButton);
		buttonPanel.add(Box.createHorizontalGlue());
		
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setVisible(true);
		leftPanel.setBackground(Color.white);

		JPanel authorPanel = new JPanel();
		authorPanel.setBackground(Color.white);
		authorPanel.setMaximumSize(new Dimension(400,30));
		authorPanel.add(authorButton);
		leftPanel.add(authorPanel);		
		leftPanel.add(Box.createVerticalStrut(5));
		leftPanel.add(buttonPanel);		
		leftPanel.add(Box.createVerticalStrut(5));
				
		encounterDoodad = new Doodad("Encounters", encounterTitles, encounterColor, 10, myFrame);
		leftPanel.add(encounterDoodad.getMainPanel());
		outermostPanel.add(leftPanel);
		
		optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		optionsPanel.setVisible(true);
		optionsPanel.setBackground(optionsColor);
		optionsPanel.setMaximumSize(new Dimension(500,1000));

		introductoryText.setFont(new Font("Times", Font.PLAIN, 16));
		introductoryText.setMargin(new Insets(10,10,10,10));
		introductoryText.setEditable(true);
		introductoryText.setLineWrap(true);
		introductoryText.setWrapStyleWord(true);
		introductoryText.setTabSize(4);
				
		introductoryText.addMouseListener(pronounGuy);
		introductoryText.addCaretListener(caretGuy);
		introductoryText.addKeyListener(keyGuy);
		
		textScrollPane = new JScrollPane(introductoryText);
		textScrollPane.setWheelScrollingEnabled(true);
		
		optionsPanel.add(textScrollPane);
		ArrayList<String> tempList = new ArrayList<String>();
		prerequisitesDoodad = new OtherDoodad("Prerequisites", tempList, encounterColor, 10, myFrame);
		disqualifiersDoodad = new OtherDoodad("Disqualifiers", tempList, encounterColor, 10, myFrame);
		JPanel predisPanel = new JPanel();
		predisPanel.setLayout(new BoxLayout(predisPanel, BoxLayout.X_AXIS));
		predisPanel.add(prerequisitesDoodad.getMainPanel());
		predisPanel.add(disqualifiersDoodad.getMainPanel());
		optionsPanel.add(predisPanel);
		
		// next in the stack: the actor exclusion checkbox lists
		JPanel outerExclusionPanel = new JPanel();
		outerExclusionPanel.setLayout(new BoxLayout(outerExclusionPanel, BoxLayout.Y_AXIS));
		outerExclusionPanel.setBackground(encounterColor);
		
		JPanel exclusionPanel = new JPanel();
		JPanel leftExclusionPanel = new JPanel();
		JPanel rightExclusionPanel = new JPanel();

		leftExclusionPanel.setLayout(new BoxLayout(leftExclusionPanel, BoxLayout.Y_AXIS));
		rightExclusionPanel.setLayout(new BoxLayout(rightExclusionPanel, BoxLayout.Y_AXIS));

		exclusionPanel.setBackground(encounterColor);
		leftExclusionPanel.setBackground(encounterColor);
		rightExclusionPanel.setBackground(encounterColor);
		
		for (int i=0; (i<EncounterEngine.cActors); ++i) {
			protagonistCheckBox[i].setBackground(encounterColor);
			leftExclusionPanel.add(protagonistCheckBox[i]);
			antagonistCheckBox[i].setBackground(encounterColor);
			rightExclusionPanel.add(antagonistCheckBox[i]);
		}
		exclusionPanel.add(leftExclusionPanel);
		exclusionPanel.add(Box.createHorizontalStrut(100));
		exclusionPanel.add(rightExclusionPanel);
		JLabel exclusionsTitle = new JLabel("Exclude as protagonist                 Exclude as antagonist");
		exclusionsTitle.setBackground(encounterColor);
		exclusionsTitle.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		exclusionsTitle.setFont(exclusionsTitle.getFont().deriveFont(14.0f));
		outerExclusionPanel.add(Box.createVerticalStrut(5));
		outerExclusionPanel.add(exclusionsTitle);
		outerExclusionPanel.add(exclusionPanel);
		optionsPanel.add(outerExclusionPanel);
		
		JPanel spinnerPanel = new JPanel();
		spinnerPanel.setLayout(new BoxLayout(spinnerPanel, BoxLayout.Y_AXIS));
		spinnerPanel.setBackground(encounterColor);
		SpinnerNumberModel firstDayModel = new SpinnerNumberModel();
		firstDayModel.setMaximum(20);
		firstDayModel.setMinimum(1);
		firstDayModel.setValue(1);
		firstDaySpinner = new JSpinner(firstDayModel);
		firstDaySpinner.setBackground(encounterColor);
		firstDaySpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
					theEncounter.setFirstDay(((SpinnerNumberModel)(firstDaySpinner.getModel())).getNumber().intValue());
			}
		});
		
		SpinnerNumberModel lastDayModel = new SpinnerNumberModel();
		lastDayModel.setMaximum(20);
		lastDayModel.setMinimum(1);
		lastDayModel.setValue(1);
		lastDaySpinner = new JSpinner(lastDayModel);
		lastDaySpinner.setBackground(encounterColor);
		lastDaySpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				theEncounter.setLastDay(((SpinnerNumberModel)(lastDaySpinner.getModel())).getNumber().intValue());
			}
		});
		JPanel spinnerLabelPanel = new JPanel();
		JPanel spinnerSpinnerPanel = new JPanel();

		spinnerLabelPanel.setBackground(encounterColor);
		spinnerSpinnerPanel.setBackground(encounterColor);

		spinnerLabelPanel.setLayout(new BoxLayout(spinnerLabelPanel, BoxLayout.X_AXIS));
		spinnerLabelPanel.add(Box.createHorizontalStrut(100));
		spinnerLabelPanel.add(new JLabel("Earliest Day"));
		spinnerLabelPanel.add(Box.createHorizontalStrut(100));
		spinnerLabelPanel.add(new JLabel("Latest Day"));
		spinnerLabelPanel.add(Box.createHorizontalStrut(100));

		spinnerSpinnerPanel.setLayout(new BoxLayout(spinnerSpinnerPanel, BoxLayout.X_AXIS));
		spinnerSpinnerPanel.add(Box.createHorizontalStrut(120));
		spinnerSpinnerPanel.add(firstDaySpinner);
		spinnerSpinnerPanel.add(Box.createHorizontalStrut(80));
		spinnerSpinnerPanel.add(lastDaySpinner);
		spinnerSpinnerPanel.add(Box.createHorizontalStrut(120));		
		
		spinnerPanel.add(spinnerLabelPanel);		
		spinnerPanel.add(spinnerSpinnerPanel);		
		optionsPanel.add(spinnerPanel);
		
		
		// now build the options list, a pile of five editable text areas.		
		JPanel middleOptionsPanel = new JPanel();
		middleOptionsPanel.setBackground(optionsColor);
		optionsPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		middleOptionsPanel.setLayout(new BoxLayout(middleOptionsPanel, BoxLayout.X_AXIS));
		optionsPanel.add(Box.createHorizontalStrut(40));
		subOptionsPanel = new JPanel();
		subOptionsPanel.setBackground(optionsColor);
		subOptionsPanel.setLayout(new BoxLayout(subOptionsPanel, BoxLayout.Y_AXIS));
		
		TitleBar optionsTitleBar = new TitleBar("Options", 140, optionsColor, myFrame);
		subOptionsPanel.add(optionsTitleBar.titlePanel);

		for (int i=0; (i<EncounterEngine.cOptions); ++i) {
			optionsTextArea[i] = new JTextArea("");
			optionsTextArea[i].setMargin(new Insets(8,8,8,8));
			optionsTextArea[i].setEditable(true);
			optionsTextArea[i].setLineWrap(true);
			optionsTextArea[i].setWrapStyleWord(true);
			optionsTextArea[i].setBackground(optionsColor);
			optionsTextArea[i].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black), new EmptyBorder(new Insets(5,5,5,5))));
			optionsTextArea[i].addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
				}
				public void mousePressed(MouseEvent e) {
					int clickX = e.getXOnScreen();
					int clickY = e.getYOnScreen();
					for (int j=0; (j<EncounterEngine.cOptions); ++j) {
						int left = optionsTextArea[j].getLocationOnScreen().x;
						int top = optionsTextArea[j].getLocationOnScreen().y;
						int right = left + (int)optionsTextArea[j].getSize().getWidth();
						int bottom = top + (int)optionsTextArea[j].getSize().getHeight();
						optionsTextArea[j].setBackground(optionsColor);
						optionsTextArea[j].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black), new EmptyBorder(new Insets(5,5,5,5))));
						// I'm doing this test by hand because the damned "Component.contains()" method doesn't work
						if ((clickX>left) & (clickX<right) & (clickY>top) & (clickY<bottom)) {
							optionsTextArea[j].setBackground(Color.white);
							optionsTextArea[j].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black, 2), new EmptyBorder(new Insets(3,3,3,3))));
							takeDownOldOption(iOption);
							iOption = j;
							theOption = theEncounter.getOption(iOption);
							setUpNewOption();
							selectedTextArea = (JTextArea)e.getSource();
						}							
					}
				}
				public void mouseReleased(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
			});
			optionsTextArea[i].addMouseListener(pronounGuy);
			optionsTextArea[i].addCaretListener(caretGuy);			
			optionsTextArea[i].addKeyListener(keyGuy);
			subOptionsPanel.add(optionsTextArea[i]);
			subOptionsPanel.add(Box.createVerticalStrut(5));
		}
		optionsTextArea[0].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black, 3), new EmptyBorder(new Insets(3,3,3,3))));
		
		middleOptionsPanel.add(subOptionsPanel);
		optionsPanel.add(middleOptionsPanel);

		// the empty space at the bottom of the Options panel
		nothingBurgerArea = new JTextArea();
		nothingBurgerArea.setLineWrap(true);
		nothingBurgerArea.setBackground(optionsColor);
		optionsPanel.add(nothingBurgerArea);	
		
		outermostPanel.add(optionsPanel);
		
		rightPanel = new JPanel();
		rightPanel.setBackground(reactionColor);
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setVisible(true);
		rightPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		rightPanel.setMaximumSize(new Dimension(500,1000));
		
		TitleBar rightTitleBar = new TitleBar("Reactions", 140, reactionColor, myFrame);
		rightPanel.add(rightTitleBar.titlePanel);
		JPanel subReactionsPanel = new JPanel();
		subReactionsPanel.setBackground(Color.white);
		subReactionsPanel.setLayout(new BoxLayout(subReactionsPanel, BoxLayout.Y_AXIS));
		
		for (int i=0; (i<EncounterEngine.cReactions); ++i) {
			reactionPanel[i] = new SillyPanel(i, reactionColor);
			rightPanel.add(reactionPanel[i]);
			rightPanel.add(Box.createVerticalStrut(5));
		}
		
		// add the panel holding the changes in relationship
		deltaPBad_Good = new DeltaPanel("pBad_Good", 0);		
		deltaPFalse_Honest = new DeltaPanel("pFalse_Honest", 1);
		deltaPTimid_Dominant = new DeltaPanel("pTimid_Dominant", 2);

		rightPanel.add(deltaPBad_Good.mainPanel);
		rightPanel.add(deltaPFalse_Honest.mainPanel);
		rightPanel.add(deltaPTimid_Dominant.mainPanel);
		
		outermostPanel.add(rightPanel);				
		myFrame.setContentPane(outermostPanel);
		
		myFrame.addWindowFocusListener(new WindowFocusListener() {
			public void windowGainedFocus(WindowEvent e) {
				if (e.getWindow()!=myFrame) 
					prerequisitesDoodad.getTitleBar().closeEncounterSelector(); 
			}
			public void windowLostFocus(WindowEvent e) { }			
		});
		ActionListener getTheFile = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try { loadEncounters(); } 
				catch (IOException e1) { e1.printStackTrace(); }
		   	catch (ParserConfigurationException e1) { e1.printStackTrace(); }
				catch (SAXException e1) { e1.printStackTrace(); }	
				
				for (int i=0; (i<encounters.size()); ++i) {
					encounterTitles.add(encounters.get(i).getTitle());
					encounterDoodad.entryList[i] = encounterTitles.get(i);
				}
      		encounterDoodad.getTheJList().setListData(encounterDoodad.entryList);
      		textScrollPane.getVerticalScrollBar().setValue(0);
      		encounterDoodad.getTheJList().setSelectedIndex(0);
			}
		};
		
		eSelectorJList = new JList<String>(eTitlesArray);			
		eSelectorJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		eSelectorJList.setLayoutOrientation(JList.VERTICAL);
		eSelectorJList.setVisibleRowCount(-1);
//		reqList.setMinimumSize(new Dimension(240, 200));
		eSelectorJList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int x = eSelectorJList.getSelectedIndex();
				if ((x>=0) & (x < encounters.size()) & e.getValueIsAdjusting()) {
					String selectedEncounter = encounters.get(x).getTitle();
					ArrayList<String> shortList;
					if (addButtonType.equals("Prerequisites"))
						shortList = theEncounter.getPrerequisites();
					else			   						
						shortList = theEncounter.getDisqualifiers();
					// make certain that this Encounter is not already on the list
					int m = 0;
					boolean gotcha = false;
					while (!gotcha & (m<shortList.size())) {
							gotcha = (selectedEncounter.equals(shortList.get(m)));
							++m;
						}
					if ((!gotcha & (e.getValueIsAdjusting()) & (x<encounters.size()))) {
						shortList.add(encounters.get(x).getTitle());
						
						// the goddamn toArray() method refuses to return a String[]
						// so I'll do it by hand. I hate Java!
						String[] temp = new String[EncounterEngine.maxPreDis];
						for (int n=0; (n<shortList.size()); ++n)
							temp[n] = shortList.get(n);
   					if (addButtonType.equals("Prerequisites"))
   						prerequisitesDoodad.getTheJList().setListData(temp);
   					else 
   						disqualifiersDoodad.getTheJList().setListData(temp);
   					
   					// shut down window if the Doodad is full
						if (shortList.size()==EncounterEngine.maxPreDis) {
	   					if (addButtonType.equals("Prerequisites"))
	   						prerequisitesDoodad.getTitleBar().getAddButton().setEnabled(false);
	   					else
	   						disqualifiersDoodad.getTitleBar().getAddButton().setEnabled(false);
						}
						if (shortList.size()>0) {
	   					if (addButtonType.equals("Prerequisites"))
	   						prerequisitesDoodad.getTitleBar().getDeleteButton().setEnabled(true);
	   					else
	   						disqualifiersDoodad.getTitleBar().getDeleteButton().setEnabled(true);
						}
					}
//					reqList.clearSelection();
//					localJFrame.removeAll();
					encounterSelector.setVisible(false);
				}
			}
		});

		encounterSelector = new JFrame();
		encounterSelector.setTitle("Choose an Encounter");
		encounterSelector.setVisible(false);
		encounterSelector.setMinimumSize(new Dimension(240, 400));
		encounterSelector.setLocation(700,50);
		eSelectorScrollPane = new JScrollPane(eSelectorJList);	   			
		eSelectorPanel = new JPanel();
		eSelectorPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		eSelectorPanel.setLayout(new BoxLayout(eSelectorPanel, BoxLayout.Y_AXIS));
		eSelectorPanel.add(eSelectorScrollPane);
		encounterSelector.getContentPane().add(eSelectorPanel);
		
		Timer openingTime = new Timer(500, getTheFile);
		openingTime.setRepeats(false);
		openingTime.start();
		myFrame.pack();
	}

// **********************************************************************
	public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("Author")) {
 		  String newAuthor = (String)JOptionPane.showInputDialog(myFrame, (Object)"Author: "+theEncounter.getAuthor());
 		  if (newAuthor!=null) {
 			  if (newAuthor.length()>19) 
 				  newAuthor = newAuthor.substring(0,18);
 			  theEncounter.setAuthor(newAuthor);
 			  authorButton.setText("by "+newAuthor);
 		  }
		}
		if (e.getActionCommand().equals("Test")) {
			testDialog.setVisible(true);
		}
		if (e.getActionCommand().equals("Save")) {
			JFileChooser chooser = new JFileChooser(theAbsolutePath);
			File bFile = null;
			 int returnVal = chooser.showSaveDialog(myFrame);
			 if (returnVal == JFileChooser.APPROVE_OPTION) {
				 String temp2 = chooser.getSelectedFile().getPath();
				 if (!temp2.endsWith(".xml"))
					 temp2+=".xml";
				 takeDownOldEncounter();
			    bFile = new File(temp2);
			    saveEncounters(bFile.getAbsolutePath());
			 }
		}		
	}
// **********************************************************************
	public void componentHidden(ComponentEvent e) { }
// **********************************************************************
	public void componentMoved(ComponentEvent e) { }
// **********************************************************************
	public void componentResized(ComponentEvent e) { myFrame.pack(); }
// **********************************************************************
	public void componentShown(ComponentEvent e) { }
// **********************************************************************
// **********************************************************************
	private class Doodad implements DocumentListener {
		private String[] entryList;
		private JList<String> theJList;
		JScrollPane theScrollPane;
		JPanel mainPanel;
		TitleBar baseTitleBar;		
// -----------------------------------------------------------------------
		Doodad(String pTitle, ArrayList<String> pEntryList, Color backgroundColor, int spacing, JFrame owner) {
			entryList = pEntryList.toArray(new String[EncounterEngine.maxEncounters]);
			theJList = new JList<String>(entryList);			
			theJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			theJList.setLayoutOrientation(JList.VERTICAL);
			theJList.setVisibleRowCount(-1);
			theJList.setSelectedIndex(0);
			theJList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (theJList.getSelectedIndex() < encounters.size()) {
						if (e.getValueIsAdjusting() & userInput & ((e.getFirstIndex()>0) | (e.getLastIndex()>0))) {
							if (theEncounter != null) {
								takeDownOldEncounter();
							}
							iEncounter = theJList.getSelectedIndex();
							iOption = 0;
							iReaction = 0;
							setUpNewEncounter(iEncounter);
						}
					}
					else {
						theJList.setSelectedIndex(iEncounter);
					}
				}
			});

			theScrollPane = new JScrollPane(theJList);
			theScrollPane.setBackground(backgroundColor);
			
			mainPanel = new JPanel();
			mainPanel.setBackground(backgroundColor);
			mainPanel.setPreferredSize(new Dimension(240, 200));
			mainPanel.setMaximumSize(new Dimension(240, 1000));
			mainPanel.setBorder(BorderFactory.createLineBorder(Color.black));
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
			
			baseTitleBar = new TitleBar(pTitle, spacing, backgroundColor, owner);
			mainPanel.add(baseTitleBar.titlePanel);

			mainPanel.add(theScrollPane);			
		}	
// -----------------------------------------------------------------------
		public JList<String> getTheJList() { return theJList; }
// -----------------------------------------------------------------------
		public JPanel getMainPanel() { return mainPanel; }
// -----------------------------------------------------------------------
		// now add the listener implementation
		public void changedUpdate(DocumentEvent e) {
		}

		public void insertUpdate(DocumentEvent e) {
		}

		public void removeUpdate(DocumentEvent e) {
		}
		
	}	
// **********************************************************************
	private class OtherDoodad {
		String[] entryList;
		JList<String> theJList;
		JScrollPane theScrollPane;
		JPanel mainPanel;
		TitleBar baseTitleBar;		
// -----------------------------------------------------------------------
		OtherDoodad(String pTitle, ArrayList<String> pEntryList, Color backgroundColor, int spacing, JFrame owner) {
			entryList = pEntryList.toArray(new String[EncounterEngine.maxPreDis]);
			theJList = new JList<String>(entryList);			
			theJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			theJList.setLayoutOrientation(JList.VERTICAL);
			theJList.setVisibleRowCount(-1);
			theJList.setSelectedIndex(0);
			 MouseListener mouseListener = new MouseAdapter() {
			     public void mouseClicked(MouseEvent e) {
			         if (e.isShiftDown()) {
			             System.out.println("shift is down");
			       }
			      if (e.isControlDown()) {
			          System.out.println("control is down");
			          }
			     }
			 };
			 theJList.addMouseListener(mouseListener);
			theJList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
				}
			});


			theScrollPane = new JScrollPane(theJList);
			theScrollPane.setBackground(backgroundColor);
			
			mainPanel = new JPanel();
			mainPanel.setBackground(backgroundColor);
//			mainPanel.setMinimumSize(new Dimension(240, 200));
			mainPanel.setPreferredSize(new Dimension(240, 200));
			mainPanel.setMaximumSize(new Dimension(240, 1000));
			mainPanel.setBorder(BorderFactory.createLineBorder(Color.black));
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
			
			baseTitleBar = new TitleBar(pTitle, spacing, backgroundColor, owner);
			mainPanel.add(baseTitleBar.titlePanel);
			
			mainPanel.add(theScrollPane);			
		}	
// -----------------------------------------------------------------------
		public JList<String> getTheJList() { return theJList; }
// -----------------------------------------------------------------------
		public TitleBar getTitleBar() { return baseTitleBar; }
// -----------------------------------------------------------------------
		private JPanel getMainPanel() { return mainPanel; }
// -----------------------------------------------------------------------
// -----------------------------------------------------------------------
// -----------------------------------------------------------------------
	}
// **********************************************************************
	private class BlenderPanel {
		JComboBox<String> leftComboBox, rightComboBox;
		JSlider centerSlider;
		JPanel mainPanel, upperPanel, middlePanel, bottomLine, sliderPanel;
		JLabel trait1, weighting, trait2;
		int myIndex;
		JLabel testResult;
	// -----------------------------------------------------------------------
		BlenderPanel(int index, Color bColor) {
			userInput=false;
			myIndex = index;
			mainPanel = new JPanel();
			upperPanel = new JPanel();
			sliderPanel = new JPanel();
			middlePanel = new JPanel();
			bottomLine = new JPanel();
			
			trait1 = new JLabel("Trait #1");
			weighting = new JLabel("0.00");
			weighting.setAlignmentX(JComponent.CENTER_ALIGNMENT);
			trait2 = new JLabel("Trait #2");
			testResult = new JLabel();
			testResult.setFont(Font.getFont("Monaco"));
			
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
			mainPanel.setAlignmentY(JComponent.CENTER_ALIGNMENT);
			mainPanel.setBackground(bColor);

			upperPanel.setBackground(bColor);
			sliderPanel.setBackground(bColor);
			middlePanel.setBackground(bColor);
			
			leftComboBox = new JComboBox<String>();
			leftComboBox.setBackground(Color.white);
			for (int i = 0; (i < EncounterEngine.cFactors); ++i) {
				leftComboBox.addItem(factor[i]);	
			}
			leftComboBox.setSelectedItem(0);
//			leftComboBox.setEditable(false);
			leftComboBox.setMaximumRowCount(12);
			leftComboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (userInput & (e.getActionCommand().equals("comboBoxChanged"))) {
						theOption.getReaction(myIndex).setFirstTrait((String)leftComboBox.getSelectedItem());
						runTest();
					}
				}
			});

			centerSlider = new JSlider(-99, 99, 0);
			centerSlider.setMaximumSize(new Dimension(198,30));
			centerSlider.setBackground(bColor);
			centerSlider.setValue(0);    // make absolutely certain that we're setting the initial value to 99!
			centerSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {	
					if (userInput) {
						theOption.getReaction(myIndex).setBias(((float)centerSlider.getValue())/100.0f);
						String temp = String.valueOf((float)(centerSlider.getValue())/100.0f);
						if (temp.length() == 3)
							temp+="0";
						weighting.setText(temp);
						runTest();
					}
				}
			});	
						
			// The upperPanel holds the labels for the controls
			upperPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
			upperPanel.add(trait1);
			upperPanel.add(Box.createHorizontalStrut(10));
			upperPanel.add(centerSlider);
			upperPanel.add(Box.createHorizontalStrut(10));
			upperPanel.add(trait2);
			upperPanel.setMaximumSize(new Dimension(500,30));
			upperPanel.setPreferredSize(new Dimension(500,20));
						
			rightComboBox = new JComboBox<String>();
			rightComboBox.setBackground(Color.white);
			for (int i = 0; (i < EncounterEngine.cFactors); ++i) {
				rightComboBox.addItem(factor[i]);	
			}
			rightComboBox.setSelectedItem(0);
			rightComboBox.setEditable(false);
//			rightComboBox.setPreferredSize(rightComboBox.getMinimumSize());
			rightComboBox.setMaximumRowCount(12);
			rightComboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (userInput & (e.getActionCommand().equals("comboBoxChanged"))) {
						theOption.getReaction(myIndex).setSecondTrait((String)rightComboBox.getSelectedItem());
						runTest();
					}
				}
			});
			
			middlePanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
			middlePanel.add(leftComboBox);
			middlePanel.add(Box.createRigidArea(new Dimension(100, 10)));
			middlePanel.add(rightComboBox);
//			middlePanel.setPreferredSize(new Dimension(500,20));
			
			bottomLine.setAlignmentX(JComponent.CENTER_ALIGNMENT);
			bottomLine.setMaximumSize(new Dimension(500, 3));
			bottomLine.setPreferredSize(new Dimension(500, 3));
			bottomLine.setBackground(Color.black);
			
			mainPanel.add(testResult);
			mainPanel.add(weighting);
			mainPanel.add(upperPanel);
			mainPanel.add(middlePanel);
			mainPanel.add(bottomLine);
			userInput=true;
		}
// -----------------------------------------------------------------------
		public JPanel getMainPanel() { return mainPanel; }
// -----------------------------------------------------------------------
		public String getTrait1() { return (String)leftComboBox.getSelectedItem(); }
// -----------------------------------------------------------------------
		public String getTrait2() { return (String)rightComboBox.getSelectedItem(); }
// -----------------------------------------------------------------------
		public int getBias() { return centerSlider.getValue(); }
// -----------------------------------------------------------------------
		public void setTestResult(float newValue) { 
			testResult.setText("Inclination value: "+commonFormat.myFormat(newValue)); 
			}
// -----------------------------------------------------------------------
//		public JComboBox getLeftComboBox() { return leftComboBox; }
// -----------------------------------------------------------------------
//		public JComboBox getRightComboBox() { return rightComboBox; }
// -----------------------------------------------------------------------
//		public JScrollBar getCenterScroller() {return centerScroller; }
// -----------------------------------------------------------------------
// -----------------------------------------------------------------------
		private void reviseReactions() {
//			userInput = false;
			EncounterEngine.Reaction localReaction = theOption.getReaction(myIndex);
			reactionsTextArea[myIndex].setText(localReaction.getText());
			int index = -1;
			for (int j=0; j<EncounterEngine.cFactors; ++j) {
				if (factor[j].equals(localReaction.getFirstTrait())) 
					index = j;
			}
			leftComboBox.setSelectedIndex(index);
			
			index = -1;
			for (int j=0; j<EncounterEngine.cFactors; ++j) {
				if (factor[j].equals(localReaction.getSecondTrait())) 
					index = j;
			}
			String temp = String.valueOf((float)(localReaction.getBias()));
			if (temp.length() == 3)
				temp+="0";
			weighting.setText(temp);
			rightComboBox.setSelectedIndex(index);
			centerSlider.setValue((int)(100.0f * localReaction.getBias()));
			
//			userInput = true;
		}
	}
// **********************************************************************
	private class SillyPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		BlenderPanel myBlenderPanel;
	// -----------------------------------------------------------------------
		SillyPanel(int index, Color bColor) {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			setAlignmentY(JComponent.CENTER_ALIGNMENT);
			setBackground(bColor);
			reactionsTextArea[index] = new JTextArea("");
			reactionsTextArea[index].setMargin(new Insets(4,0,4,0));
			reactionsTextArea[index].setEditable(true);
			reactionsTextArea[index].setLineWrap(true);
			reactionsTextArea[index].setWrapStyleWord(true);
			reactionsTextArea[index].setPreferredSize(new Dimension(438, 70));
			reactionsTextArea[index].setMaximumSize(new Dimension(466, 70));
			reactionsTextArea[index].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black),new EmptyBorder(new Insets(5,5,5,5))));

			reactionsTextArea[index].addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
				}
				public void mousePressed(MouseEvent e) {
					int clickX = e.getXOnScreen();
					int clickY = e.getYOnScreen();
					if (theEncounter != null) {
						for (int j=0; (j<EncounterEngine.cReactions); ++j) {
							int left = reactionsTextArea[j].getLocationOnScreen().x;
							int top = reactionsTextArea[j].getLocationOnScreen().y;
							int right = left + (int)reactionsTextArea[j].getSize().getWidth();
							int bottom = top + (int)reactionsTextArea[j].getSize().getHeight();
							reactionsTextArea[j].setBackground(optionsColor);
							reactionsTextArea[j].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black), new EmptyBorder(new Insets(5,5,5,5))));
							// I'm doing this test by hand because the damned "Component.contains()" method doesn't work
							if ((clickX>left) & (clickX<right) & (clickY>top) & (clickY<bottom)) {
								reactionsTextArea[j].setBackground(Color.white);
								reactionsTextArea[j].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black, 2), new EmptyBorder(new Insets(3,3,3,3))));
								iReaction = j;
							}							
						}
					}
				}
				public void mouseReleased(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
			});
			reactionsTextArea[index].addMouseListener(pronounGuy);
			
			reactionsTextArea[index].addCaretListener(caretGuy);
			reactionsTextArea[index].addKeyListener(keyGuy);
			add(reactionsTextArea[index]);
			myBlenderPanel = new BlenderPanel(index, bColor);
			add(myBlenderPanel.getMainPanel());
		}
// -----------------------------------------------------------------------
		public BlenderPanel getMyBlenderPanel() {return myBlenderPanel; }
	}
// **********************************************************************
	private class TitleBar implements ActionListener {
		JButton addButton, deleteButton;
		JLabel title;
		JPanel titlePanel;
		String type;		
	// -----------------------------------------------------------------------
		TitleBar(String pTitle, int spacing, Color backgroundColor, JFrame pOwner) {
			title = new JLabel(pTitle);
			title.setBackground(backgroundColor);

			addButton = new JButton("+");
			deleteButton = new JButton("-");
			
			addButton.setBackground(Color.white);
			deleteButton.setBackground(Color.white);
			
			addButton.setActionCommand("add"+pTitle);
			deleteButton.setActionCommand("del"+pTitle);
			
			addButton.addActionListener(this);
			deleteButton.addActionListener(this);

			titlePanel = new JPanel();
			titlePanel.setPreferredSize(new Dimension(500, 40));
			titlePanel.setMaximumSize(new Dimension(500, 40));
			titlePanel.setBackground(backgroundColor);
			titlePanel.setAlignmentY(JComponent.CENTER_ALIGNMENT);
			titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
			titlePanel.add(Box.createHorizontalStrut(10));
			// more magical special case code!!!
			if (!((pTitle.equals("Options") | (pTitle.equals("Reactions"))))) {
				titlePanel.add(addButton);
			}
			titlePanel.add(Box.createHorizontalStrut(spacing));
			title.setFont(title.getFont().deriveFont(18.0f));
			titlePanel.add(title);
			titlePanel.add(Box.createHorizontalStrut(spacing));
			if (!((pTitle.equals("Options") | (pTitle.equals("Reactions"))))) {
				titlePanel.add(deleteButton);
			}
			titlePanel.add(Box.createHorizontalStrut(10));	
		}
	    public void actionPerformed(ActionEvent e) {
	   	 String prefix = e.getActionCommand().substring(0, 3);
	   	 type = e.getActionCommand().substring(3);
	   	 addButtonType = type;
	        if (prefix.equals("add")) {
	      	  if (type.equals("Encounters")) {
	      		  String newTitle = (String)JOptionPane.showInputDialog(myFrame, (Object)"Name of new Encounter:");
	      		  boolean isTitleAlreadyTaken = false;
	      		  for (int i=0; (i<encounters.size()); ++i) {
	      			  if (encounters.get(i).getTitle().equals(newTitle)) 
	      				  isTitleAlreadyTaken = true;
	      		  }
	      		  if (isTitleAlreadyTaken) {
	      			  JOptionPane.showMessageDialog(myFrame,
	      					    "You snivelling fool!",
	      					    "That title is already taken",
	      					    JOptionPane.ERROR_MESSAGE);
	      		  }
	      		  else if (newTitle!=null) {
		      		  theEncounter = theEngine.getNewEncounter();
		      		  theEncounter.setTitle(newTitle);
		      		  encounters.add(theEncounter);
		      		  userInput = false; // this prevents the Doodad slider from activating
		      		  encounterDoodad.entryList[encounters.size()-1] = newTitle;
		      		  encounterDoodad.getTheJList().setListData(encounterDoodad.entryList);
	      			  encounterDoodad.getTheJList().setSelectedIndex(encounters.size()-1);
	      			  setUpNewEncounter(encounters.size()-1);
	      			  userInput = true;
	      		  }
	      	  }
	      	  if ((type.equals("Prerequisites")) | (type.equals("Disqualifiers"))) {
	      		  eTitlesArray = encounterTitles.toArray(new String[EncounterEngine.maxEncounters]);
	      		  eSelectorJList.setListData(eTitlesArray);
	      		  encounterSelector.setVisible(true);
	      	  }
	        }
	        if (prefix.equals("del")) {
	      	  if (type.equals("Encounters")) {
	      		  int n = encounters.size();
	      		  int m = encounterDoodad.getTheJList().getSelectedIndex();
	      		  encounters.remove(m);
	      		  for (int i=m; (i<n); ++i) {
	      			  encounterDoodad.entryList[i] = encounterDoodad.entryList[i+1];
	      		  }
	      		  encounterDoodad.theJList.setListData(encounterDoodad.entryList);
	      		  if (encounters.size() == 0) {
	      			  deleteButton.setEnabled(false);
	      		  }
	      	  }
	      	  if (type.equals("Prerequisites")) {
	      		  int m = prerequisitesDoodad.getTheJList().getSelectedIndex();
	      		  if (m>=0) {
	      			  theEncounter.getPrerequisites().remove(m);
	      			  prerequisitesDoodad.getTheJList().setListData(theEncounter.getPrerequisites().toArray(new String[EncounterEngine.maxPreDis]));
	      		  }
	      	  }
	      	  if (type.equals("Disqualifiers")) {
	      		  int m = disqualifiersDoodad.getTheJList().getSelectedIndex();
	      		  if (m>=0) {
		      		  theEncounter.getDisqualifiers().remove(m);
		      		  disqualifiersDoodad.getTheJList().setListData(theEncounter.getDisqualifiers().toArray(new String[EncounterEngine.maxPreDis]));
	      		  }
	      	  }
	        }
	    }		
// -----------------------------------------------------------------------
	    public void closeEncounterSelector() {
				encounterSelector.dispose();
	    }
	 // -----------------------------------------------------------------------
	    public JButton getDeleteButton() { return deleteButton; }
	 // -----------------------------------------------------------------------
	    public JButton getAddButton() { return addButton; }
	}
// **********************************************************************
	private class DeltaPanel {
		JPanel mainPanel;
		JPanel titlePanel;
		JLabel valueLabel;
		private JSlider biasSlider;
		int myIndex;
//-----------------------------------------------------------------------
		DeltaPanel(String title, int pIndex) {
			myIndex = pIndex;
			mainPanel = new JPanel();
			mainPanel.setPreferredSize(new Dimension(500, 40));
			mainPanel.setBackground(optionsColor);
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

			titlePanel = new JPanel();
			titlePanel.setPreferredSize(new Dimension(500, 40));
			titlePanel.setBackground(optionsColor);
			titlePanel.setAlignmentY(JComponent.LEFT_ALIGNMENT);
			titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
			titlePanel.add(Box.createHorizontalStrut(10));
			titlePanel.add(new JLabel("Blending change in "+title));
			
			biasSlider = new JSlider(-99, 99, 0);
			biasSlider.setMaximumSize(new Dimension(198,20));
			biasSlider.setPreferredSize(new Dimension(198,20));
			biasSlider.setBackground(optionsColor);
			biasSlider.setValue(0);    // make absolutely certain that we're setting the initial value to 99!
			biasSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {	
					switch (myIndex) {
						case 0: { theOption.setDeltaPBad_Good(biasSlider.getValue()); break; }
						case 1: { theOption.setDeltaPFalse_Honest(biasSlider.getValue()); break; }
						case 2: { theOption.setDeltaPTimid_Dominant(biasSlider.getValue()); break; }
					}
					valueLabel.setText(String.valueOf((float)(biasSlider.getValue())/100));
				}
			});			
			
			mainPanel.add(titlePanel);
			JPanel valuePanel = new JPanel();
			valuePanel.setPreferredSize(new Dimension(500, 50));
			valuePanel.setBackground(optionsColor);
			valuePanel.setAlignmentY(JComponent.LEFT_ALIGNMENT);
			valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.X_AXIS));
			valueLabel = new JLabel("0.0");
			valuePanel.add(valueLabel);
			valuePanel.add(Box.createHorizontalStrut(4));
			mainPanel.add(valuePanel);			
			
			JPanel biasPanel = new JPanel();
			biasPanel.setPreferredSize(new Dimension(500, 40));
			biasPanel.setBackground(optionsColor);
			biasPanel.setAlignmentY(JComponent.LEFT_ALIGNMENT);
			biasPanel.setLayout(new BoxLayout(biasPanel, BoxLayout.X_AXIS));
			biasPanel.add(Box.createHorizontalStrut(20));
			biasPanel.add(new JLabel("-1"));
			biasPanel.add(biasSlider);
			biasPanel.add(new JLabel("+1"));
			biasPanel.add(Box.createHorizontalStrut(20));
//			biasPanel.add(Box.createHorizontalGlue());
			
			mainPanel.add(biasPanel);
			mainPanel.add(Box.createVerticalStrut(20));
		}
//-----------------------------------------------------------------------
		public JSlider getBiasSlider() { return biasSlider; }
	}
// **********************************************************************
	public class FormatStuff {
	   public NumberFormat integerFormat=NumberFormat.getIntegerInstance();
	   public NumberFormat doubleFormat=NumberFormat.getInstance();
	   public NumberFormat floatFormat=NumberFormat.getInstance();
	   public NumberFormat currencyFormat=NumberFormat.getCurrencyInstance();
	   public NumberFormat percentFormat=NumberFormat.getPercentInstance();
	   public NumberFormat percentFormat2=NumberFormat.getPercentInstance();
	   public NumberFormat fractionFormat=NumberFormat.getInstance();
	   
		//-----------------------------------------------------------------------
	   public FormatStuff() {
			integerFormat.setMaximumFractionDigits(0);
			doubleFormat.setMaximumFractionDigits(3);
			floatFormat.setMaximumFractionDigits(2);
			currencyFormat.setMaximumFractionDigits(0);
			percentFormat.setMaximumFractionDigits(0);
			percentFormat2.setMaximumFractionDigits(2);
			fractionFormat.setMaximumFractionDigits(4);
	   }
		//-----------------------------------------------------------------------
		public String myFormat(String formatType, double inValue) {
			// formats a double value for the particular page
			if (formatType.equals("Integer"))
				return integerFormat.format(inValue);
			else if (formatType.equals("Double"))
				return doubleFormat.format(inValue);
			else if (formatType.equals("Currency"))
				return currencyFormat.format(inValue);
			else if (formatType.equals("Percent"))
				return percentFormat.format(inValue);
			else if (formatType.equals("Percent2"))
				return percentFormat2.format(inValue);
			else if (formatType.equals("Fraction"))
				return fractionFormat.format(inValue);
			else return "bad Format: "+formatType;
		}
		//-----------------------------------------------------------------------
		public String myFormat(float newValue) {
			String temp = commonFormat.floatFormat.format(newValue);
			int maxLength = 4;
			if (newValue<0) maxLength = 5;
			while (temp.length()<maxLength) temp+="0"; 
			return temp+" ";
		}

	}
// **********************************************************************
	private class Actor {
		String label;
		float trait[];
		float pTrait[][];
		boolean isMale;		
	//-----------------------------------------------------------------------
		private Actor(String tLabel) {
			label = tLabel;
			trait = new float[6];
			pTrait = new float[EncounterEngine.cActors][6];
			isMale = true;
		}
	//-----------------------------------------------------------------------
		public String getLabel() { return label; }
	//-----------------------------------------------------------------------
		public float getTrait(int iTrait) { return trait[iTrait]; }
	//-----------------------------------------------------------------------
		public void setTrait(int iTrait, float newValue) { trait[iTrait] = newValue; }
	//-----------------------------------------------------------------------
		public float getPTrait(int iTrait, int iActor) { return pTrait[iActor][iTrait]; }
	//-----------------------------------------------------------------------
		public void setPTrait(int iTrait, int iActor, float newValue) { pTrait[iActor][iTrait] = newValue; }
	//-----------------------------------------------------------------------
		public boolean getIsMale() {return isMale; }
	//-----------------------------------------------------------------------
		public void setIsMale(boolean newValue) { isMale = newValue; }
	//-----------------------------------------------------------------------
		
	}
// **********************************************************************
	private void takeDownOldEncounter() {
		theEncounter.setIntroText(introductoryText.getText());
		for (int i=0; (i<EncounterEngine.cOptions); ++i) {
			EncounterEngine.Option xOption = theEncounter.getOption(i);
			xOption.setText(optionsTextArea[i].getText());
			
			if (i==iOption) {
				xOption.setDeltaPBad_Good(deltaPBad_Good.getBiasSlider().getValue());
				xOption.setDeltaPFalse_Honest(deltaPFalse_Honest.getBiasSlider().getValue());
				xOption.setDeltaPTimid_Dominant(deltaPTimid_Dominant.getBiasSlider().getValue());
				
				for (int j=0; (j<EncounterEngine.cReactions); ++j) {
					xOption.getReaction(j).setText(reactionsTextArea[j].getText());
					xOption.getReaction(j).setFirstTrait(reactionPanel[j].getMyBlenderPanel().getTrait1());
					xOption.getReaction(j).setSecondTrait(reactionPanel[j].getMyBlenderPanel().getTrait2());
					xOption.getReaction(j).setBias(((float)reactionPanel[j].getMyBlenderPanel().getBias())/100.0f);
				}
			}
		}		
	}
// **********************************************************************
	private void setUpNewEncounter(int listIndex) {
		theEncounter = encounters.get(listIndex);
		theOption = theEncounter.getOption(iOption);
		theReaction = theOption.getReaction(iReaction);
		
		authorButton.setText("by "+theEncounter.getAuthor());
		
		introductoryText.setText(theEncounter.getIntroText());
		introductoryText.setCaretPosition(0);

		prerequisitesDoodad.getTheJList().setListData(theEncounter.getPrerequisites().toArray(new String[EncounterEngine.maxPreDis]));
		disqualifiersDoodad.getTheJList().setListData(theEncounter.getDisqualifiers().toArray(new String[EncounterEngine.maxPreDis]));
		
		if (theEncounter.getPrerequisites().size()==0) 
			prerequisitesDoodad.getTitleBar().getDeleteButton().setEnabled(false);
		if (theEncounter.getDisqualifiers().size()==0) 
			disqualifiersDoodad.getTitleBar().getDeleteButton().setEnabled(false);
			
		for (int i=0; (i<EncounterEngine.cActors); ++i) {
			protagonistCheckBox[i].setSelected(!theEncounter.getIsAllowedToBeProtagonist(i));
			antagonistCheckBox[i].setSelected(!theEncounter.getIsAllowedToBeAntagonist(i));
		}

		((SpinnerNumberModel)firstDaySpinner.getModel()).setValue((Object)theEncounter.getFirstDay());
		((SpinnerNumberModel)lastDaySpinner.getModel()).setValue((Object)theEncounter.getLastDay());

		for (int i=0; (i<EncounterEngine.cOptions); ++i) {
			optionsTextArea[i].setText(theEncounter.getOption(i).getText());
			optionsTextArea[i].setBorder(BorderFactory.createLineBorder(Color.black, 1));
			optionsTextArea[i].setBackground(optionsColor);
		}			
		optionsTextArea[iOption].setBackground(Color.white);
		optionsTextArea[iOption].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black,2), new EmptyBorder(new Insets(5,5,5,5))));
		iOption = 0;
		setUpNewOption();		
	}
// **********************************************************************
	private void takeDownOldOption(int optionIndex) {
		for (int i=0; (i<EncounterEngine.cOptions); ++i) {
			theEncounter.getOption(i).setText(optionsTextArea[i].getText());
		}			
		
		theOption.setDeltaPBad_Good((float)(deltaPBad_Good.getBiasSlider().getValue())/100.0f);
		theOption.setDeltaPFalse_Honest((float)(deltaPFalse_Honest.getBiasSlider().getValue())/100.0f);
		theOption.setDeltaPTimid_Dominant((float)(deltaPTimid_Dominant.getBiasSlider().getValue())/100.0f);

		for (int i=0; (i<EncounterEngine.cReactions); ++i) {
			theOption.getReaction(i).setText(reactionsTextArea[i].getText());
		}				
	}
// **********************************************************************
	private void setUpNewOption() {
		deltaPBad_Good.getBiasSlider().setValue((int)(theOption.getDeltaPBad_Good() * 100.0f));
		deltaPFalse_Honest.getBiasSlider().setValue((int)(theOption.getDeltaPFalse_Honest() * 100.0f));
		deltaPTimid_Dominant.getBiasSlider().setValue((int)(theOption.getDeltaPTimid_Dominant() * 100.0f));
		iReaction = 0;
		theReaction = theOption.getReaction(0);
		userInput = false;
		for (int i=0; (i<EncounterEngine.cReactions); ++i) {
			reactionsTextArea[i].setText(theOption.getReaction(i).getText());
			reactionsTextArea[i].setBackground(reactionColor);
			reactionPanel[i].myBlenderPanel.reviseReactions();
		}		
		reactionsTextArea[0].setBackground(Color.white);
		reactionsTextArea[0].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black,2), new EmptyBorder(new Insets(5,5,5,5))));
		userInput = true;
		
	}
// **********************************************************************
	public void runTest() {
		float bestInclination;
		int bestIndex;
		float x1, x2, bias, inclination;
		
		if ((theProtagonist>=0) & (theAntagonist>=0) & testDialog.isVisible()) {
			bestInclination = -1.00f;
			bestIndex = -1;
			for (int i = 0; (i<EncounterEngine.cReactions); ++i) {
				if (!theOption.getReaction(i).getText().equals("unused Reaction")) {
					reactionsTextArea[i].setBackground(Color.white);
					String first = theOption.getReaction(i).getFirstTrait();
					switch (first) {
						case "Bad_Good": { x1 = actors.get(theAntagonist).getTrait(0); break; }
						case "False_Honest": { x1 = actors.get(theAntagonist).getTrait(1); break; }
						case "Timid_Dominant": { x1 = actors.get(theAntagonist).getTrait(2); break; }
						case "pBad_Good": { x1 = actors.get(theAntagonist).getPTrait(0, theProtagonist); break; }
						case "pFalse_Honest": { x1 = actors.get(theAntagonist).getPTrait(1, theProtagonist); break; }
						case "pTimid_Dominant": { x1 = actors.get(theAntagonist).getPTrait(2, theProtagonist); break; }
						case "-Bad_Good": { x1 = -actors.get(theAntagonist).getTrait(0); break; }
						case "-False_Honest": { x1 = -actors.get(theAntagonist).getTrait(1); break; }
						case "-Timid_Dominant": { x1 = -actors.get(theAntagonist).getTrait(2); break; }
						case "-pBad_Good": { x1 = -actors.get(theAntagonist).getPTrait(0, theProtagonist); break; }
						case "-pFalse_Honest": { x1 = -actors.get(theAntagonist).getPTrait(1, theProtagonist); break; }
						case "-pTimid_Dominant": { x1 = -actors.get(theAntagonist).getPTrait(2, theProtagonist); break; }
						default: { x1 = -0.9999f; break; }
					}
					String second = theOption.getReaction(i).getSecondTrait();
					switch (second) {
						case "Bad_Good": { x2 = actors.get(theAntagonist).getTrait(0); break; }
						case "False_Honest": { x2 = actors.get(theAntagonist).getTrait(1); break; }
						case "Timid_Dominant": { x2 = actors.get(theAntagonist).getTrait(2); break; }
						case "pBad_Good": { x2 = actors.get(theAntagonist).getPTrait(0, theProtagonist); break; }
						case "pFalse_Honest": { x2 = actors.get(theAntagonist).getPTrait(1, theProtagonist); break; }
						case "pTimid_Dominant": { x2 = actors.get(theAntagonist).getPTrait(2, theProtagonist); break; }
						case "-Bad_Good": { x2 = -actors.get(theAntagonist).getTrait(0); break; }
						case "-False_Honest": { x2 = -actors.get(theAntagonist).getTrait(1); break; }
						case "-Timid_Dominant": { x2 = -actors.get(theAntagonist).getTrait(2); break; }
						case "-pBad_Good": { x2 = -actors.get(theAntagonist).getPTrait(0, theProtagonist); break; }
						case "-pFalse_Honest": { x2 = -actors.get(theAntagonist).getPTrait(1, theProtagonist); break; }
						case "-pTimid_Dominant": { x2 = -actors.get(theAntagonist).getPTrait(2, theProtagonist); break; }
						default: { x2 = -0.9999f; break; }
					}
					
					bias = theOption.getReaction(i).getBias();
					inclination = theEngine.blend(x1, x2, bias);
					if (inclination > bestInclination) {
						bestInclination = inclination;
						bestIndex = i;
					}
					reactionPanel[i].getMyBlenderPanel().setTestResult(inclination);
				}
			}
			reactionsTextArea[bestIndex].setBackground(hilightColor);
		}		
		
		
	}
// **********************************************************************
	/*
	private String decodeText(String inputString) {
		String output = inputString.replaceAll("=antagonist=",actorLabel[iAntagonist]);
		output = output.replaceAll("=Antagonist=",actorLabel[iAntagonist]);
		output = output.replaceAll("=Protagonist=", actorLabel[iProtagonist]);
		output = output.replaceAll("=protagonist=", actorLabel[iProtagonist]);
		output = output.replaceAll("=nominative=", isActorFemale[iAntagonist] ? "she":"he");
		output = output.replaceAll("=Nominative=", isActorFemale[iAntagonist] ? "She":"He");
		output = output.replaceAll("=Accusative=",isActorFemale[iAntagonist] ? "her":"him");
		output = output.replaceAll("=accusative=",isActorFemale[iAntagonist] ? "her":"him");
		output = output.replaceAll("=possessiveAdjective=", isActorFemale[iAntagonist] ? "her":"his");
		output = output.replaceAll("=PossessiveAdjective=", isActorFemale[iAntagonist] ? "Her":"His");
		output = output.replaceAll("=possessivePronoun=",isActorFemale[iAntagonist] ? "hers":"his");
		output = output.replaceAll("=PossessivePronoun=",isActorFemale[iAntagonist] ? "Hers":"His");
		
		return output;
	}
*/
// **********************************************************************
	private EncounterEngine.Encounter findEncounter(String title) {
		int i=0;
		if ((title.length() == 0) | (title.equals(" "))) return null;
		while ((i<encounters.size()) && !encounters.get(i).getTitle().equals(title)) { ++i; }
		if (i>=encounters.size()) {
			JOptionPane.showMessageDialog(myFrame,
				    "I could not find any Encounter with the title "+title,
				    "Fatal error",
				    JOptionPane.ERROR_MESSAGE);	
			System.exit(0);
		}
		return(encounters.get(i));
	}
// **********************************************************************
	private void loadEncounters() 
			throws IOException, ParserConfigurationException, SAXException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		builder = factory.newDocumentBuilder();		
		Document encounterDoc = null;
		
		JFileChooser chooser = new JFileChooser();
		ProtectionDomain pd = EncounterEditor.class.getProtectionDomain();
		CodeSource cs = pd.getCodeSource();
		URL location = cs.getLocation();
		chooser.setCurrentDirectory(new File(location.getFile()));
		FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files", "xml");
		File bFile = null;
		 chooser.setFileFilter(filter);
		 int returnVal = chooser.showOpenDialog(myFrame);
		 if(returnVal == JFileChooser.APPROVE_OPTION) {
		    bFile = chooser.getSelectedFile();
		    fileName = bFile.getName();
		    theAbsolutePath = bFile.getAbsolutePath();
		 }
		 else  {
				JOptionPane.showMessageDialog(myFrame,
					    "I have to quit because there's no Encounter file to work with",
					    "Fatal error",
					    JOptionPane.ERROR_MESSAGE);	
				System.exit(0);
		 }
		
		
//		File aFile = new File(System.getProperty("user.dir")+"/res/EncounterList.xml");
		InputStream is = new FileInputStream(bFile);

		encounterDoc = builder.parse(is);
		is.close();
		
		NodeList w = encounterDoc.getElementsByTagName("ListWrapper");
		Node x = w.item(0);
		String y = x.getAttributes().getNamedItem("version").getNodeValue();
		versionNumber = (Float.valueOf(y)).floatValue();

		NodeList actorList = encounterDoc.getElementsByTagName("Actor");
		for (int i = 0; (i < actorList.getLength()); ++i) {
			Node aNode = actorList.item(i);
			String tempLabel = aNode.getAttributes().getNamedItem("label").getNodeValue();
			if (tempLabel.length()>0) {
				actors.add(new Actor(tempLabel));
		 		NodeList childNodes = aNode.getChildNodes();
		 		for (int j = 0; (j < childNodes.getLength()); ++j) {
	 				if ((childNodes.item(j).getNodeName()).equals("isMale"))
	 					actors.get(i).setIsMale(Boolean.valueOf(childNodes.item(j).getTextContent()));
	 				if ((childNodes.item(j).getNodeName()).equals("Bad_Good"))
	 					actors.get(i).setTrait(0, Float.valueOf(childNodes.item(j).getTextContent()));
	 				if ((childNodes.item(j).getNodeName()).equals("False_Honest"))
	 					actors.get(i).setTrait(1, Float.valueOf(childNodes.item(j).getTextContent()));
	 				if ((childNodes.item(j).getNodeName()).equals("Timid_Dominant"))
	 					actors.get(i).setTrait(2, Float.valueOf(childNodes.item(j).getTextContent()));
		 		}
			}
	 		actors.get(i).setTrait(3, -actors.get(i).getTrait(0));
	 		actors.get(i).setTrait(4, -actors.get(i).getTrait(1));
	 		actors.get(i).setTrait(5, -actors.get(i).getTrait(2));

		}
		
		NodeList pTraitList = encounterDoc.getElementsByTagName("pTrait");
		for (int i = 0; (i < pTraitList.getLength()); ++i) {
			Node aNode = pTraitList.item(i);
			String kindLabel = aNode.getAttributes().getNamedItem("akind").getNodeValue();
			String fromLabel = aNode.getAttributes().getNamedItem("from").getNodeValue();
			String toLabel = aNode.getAttributes().getNamedItem("to").getNodeValue();
			float value = Float.valueOf(aNode.getAttributes().getNamedItem("value").getNodeValue());
			
			int iTrait = 0;
			while (!factor[iTrait].equals(kindLabel)) ++iTrait;
			int iActor = 0;
			while (!actors.get(iActor).getLabel().equals(fromLabel)) ++iActor;
			int jActor = 0;
			while (!actors.get(jActor).getLabel().equals(toLabel)) ++jActor;
			
			actors.get(iActor).setPTrait(iTrait, jActor, value);
		}
		
		NodeList encounterList = encounterDoc.getElementsByTagName("Encounter");
		for (int i = 0; (i < encounterList.getLength()); ++i) {
			Node aNode = encounterList.item(i);
			String testTitle = aNode.getAttributes().getNamedItem("title").getNodeValue();
			if (testTitle.length()>0) {
				encounters.add(theEngine.getNewEncounter());
				theEncounter = encounters.get(i);
				iOption = 0;
				theEncounter.setTitle(testTitle);
		 		NodeList childNodes = aNode.getChildNodes();
		 		for (int j = 0; (j < childNodes.getLength()); ++j) {
	 				if ((childNodes.item(j).getNodeName()).equals("Author"))
	 					theEncounter.setAuthor(childNodes.item(j).getTextContent());
	 				if ((childNodes.item(j).getNodeName()).equals("IntroText"))
	 					theEncounter.setIntroText(childNodes.item(j).getTextContent());
	 				
	 				else if ((childNodes.item(j).getNodeName()).equals("Prerequisites")) {
	 					theEncounter.getPrerequisites().clear();
	 					NodeList prereqChildNodes = childNodes.item(j).getChildNodes();
	 					for (int k=0; (k < prereqChildNodes.getLength()); ++k) {
	 						String temp = prereqChildNodes.item(k).getTextContent();
	 						if (temp.length()>2)
							theEncounter.getPrerequisites().add(temp);
	 					}
	 				}
	 				else if ((childNodes.item(j).getNodeName()).equals("Disqualifiers")) {
	 					theEncounter.getDisqualifiers().clear();
	 					NodeList disqualChildNodes = childNodes.item(j).getChildNodes();
	 					for (int k=0; (k < disqualChildNodes.getLength()); ++k) {
	 						String temp = disqualChildNodes.item(k).getTextContent();
	 						if (temp.length()>2)
	 							theEncounter.getDisqualifiers().add(temp);
	 					}
	 				}
	 				else if ((childNodes.item(j).getNodeName()).equals("ExcludeAntagonist")) {
	 					NodeList excludeAntagonistChildNodes = childNodes.item(j).getChildNodes();
	 					for (int k=0; (k<excludeAntagonistChildNodes.getLength()); ++k) {
	 						Node thisNode = excludeAntagonistChildNodes.item(k);
	 						if (thisNode.getNodeName().equals("Camiggdo"))
	 							theEncounter.setIsAllowedToBeAntagonist(!Boolean.valueOf(thisNode.getTextContent()),EncounterEngine.Camiggdo);
	 						else if (thisNode.getNodeName().equals("Skordokott"))
	 							theEncounter.setIsAllowedToBeAntagonist(!Boolean.valueOf(thisNode.getTextContent()),EncounterEngine.Skordokott);
	 						else if (thisNode.getNodeName().equals("Zubenelgenubi"))
	 							theEncounter.setIsAllowedToBeAntagonist(!Boolean.valueOf(thisNode.getTextContent()),EncounterEngine.Zubenelgenubi);
	 						else if (thisNode.getNodeName().equals("Koopie"))
	 							theEncounter.setIsAllowedToBeAntagonist(!Boolean.valueOf(thisNode.getTextContent()),EncounterEngine.Koopie);
	 						else if (thisNode.getNodeName().equals("Subotai"))
	 							theEncounter.setIsAllowedToBeAntagonist(!Boolean.valueOf(thisNode.getTextContent()),EncounterEngine.Subotai);
	 					}
	 				}
	 				else if ((childNodes.item(j).getNodeName()).equals("ExcludeProtagonist")) {
	 					NodeList excludeProtagonistChildNodes = childNodes.item(j).getChildNodes();
	 					for (int k=0; (k<excludeProtagonistChildNodes.getLength()); ++k) {
	 						Node thisNode = excludeProtagonistChildNodes.item(k);
	 						if (thisNode.getNodeName().equals("Camiggdo"))
	 							theEncounter.setIsAllowedToBeProtagonist(!Boolean.valueOf(thisNode.getTextContent()),EncounterEngine.Camiggdo);
	 						else if (thisNode.getNodeName().equals("Skordokott"))
	 							theEncounter.setIsAllowedToBeProtagonist(!Boolean.valueOf(thisNode.getTextContent()),EncounterEngine.Skordokott);
	 						else if (thisNode.getNodeName().equals("Zubenelgenubi"))
	 							theEncounter.setIsAllowedToBeProtagonist(!Boolean.valueOf(thisNode.getTextContent()),EncounterEngine.Zubenelgenubi);
	 						else if (thisNode.getNodeName().equals("Koopie"))
	 							theEncounter.setIsAllowedToBeProtagonist(!Boolean.valueOf(thisNode.getTextContent()),EncounterEngine.Koopie);
	 						else if (thisNode.getNodeName().equals("Subotai"))
	 							theEncounter.setIsAllowedToBeProtagonist(!Boolean.valueOf(thisNode.getTextContent()),EncounterEngine.Subotai);
	 					}
	 				}
	 				else if ((childNodes.item(j).getNodeName()).equals("DayWindow")) {
	 					NodeList dayLimitNodes = childNodes.item(j).getChildNodes();
	 					for (int k=0; (k < dayLimitNodes.getLength()); ++k) {
	 						if (dayLimitNodes.item(k).getNodeName().equals("Minimum")) 
	 							theEncounter.setFirstDay(Integer.parseInt(dayLimitNodes.item(k).getTextContent()));
	 						else if (dayLimitNodes.item(k).getNodeName().equals("Maximum")) 
	 							theEncounter.setLastDay(Integer.parseInt(dayLimitNodes.item(k).getTextContent()));
	 					}
	 				}
	 				else if ((childNodes.item(j).getNodeName()).equals("Option")) {
	 					NodeList optionChildNodes = childNodes.item(j).getChildNodes();
	 					theEncounter.setOption(theEncounter.getNewOption(), iOption);
	// 					theOption = theEncounter.options[iOption];
	 					iReaction = 0;
	 					for (int k=0; (k < optionChildNodes.getLength()); ++k) {
	 						String temp = optionChildNodes.item(k).getTextContent();
	 						String nodeName = optionChildNodes.item(k).getNodeName();
	 						if (nodeName.equals("OptionText")) {
	 							theEncounter.getOption(iOption).setText(temp);
	 						}
	 						else if (nodeName.equals("DeltaPBad_Good"))
	 							theEncounter.getOption(iOption).setDeltaPBad_Good(Float.parseFloat(temp));
	 						else if (nodeName.equals("DeltaPFalse_Honest"))
	 							theEncounter.getOption(iOption).setDeltaPFalse_Honest(Float.parseFloat(temp));
	 						else if (nodeName.equals("DeltaPTimid_Dominant"))
	 							theEncounter.getOption(iOption).setDeltaPTimid_Dominant(Float.parseFloat(temp));
	
	 						else if (nodeName.equals("Reaction")) {
	 		 					NodeList reactionChildNodes = optionChildNodes.item(k).getChildNodes();
	 		 					theEncounter.getOption(iOption).setReaction(iReaction, theEncounter.getOption(iOption).getNewReaction());
	 							theReaction = theEncounter.getOption(iOption).getReaction(iReaction++);
	 							for (int m=0; (m < reactionChildNodes.getLength()); ++m) {
	 		 						if (reactionChildNodes.item(m).getNodeName().equals("DesirableFormula")) {
	 		 		 					NodeList desirableChildNodes = reactionChildNodes.item(m).getChildNodes();
	 		 							for (int n=0; (n < desirableChildNodes.getLength()); ++n) {
	 	 		 		 					String content = desirableChildNodes.item(n).getTextContent();
	 		 		 						if (desirableChildNodes.item(n).getNodeName().equals("FirstTrait"))
	 		 		 						theReaction.setFirstTrait(content);
	 		 		 						if (desirableChildNodes.item(n).getNodeName().equals("SecondTrait"))
	 		 		 						theReaction.setSecondTrait(content);
	 		 		 						if (desirableChildNodes.item(n).getNodeName().equals("Bias"))
	 		 		 						theReaction.setBias(Float.parseFloat(content));
	 		 							}
	 		 						} 		 							
	 		 						else if (reactionChildNodes.item(m).getNodeName().equals("ReactionText")) {
	 		 							theReaction.setText(reactionChildNodes.item(m).getTextContent()); 
	 		 						}
	 							}
	 						}
	 					}
	 					++iOption;
	 				}
		 		}
			}
		}
		// Now we run through the entire set looking for misspellings of titles
		// If so, method "findEncounter" contains the error message
		for (int i=0; (i<encounters.size()); ++i) {
			theEncounter = encounters.get(i);
			
			ArrayList<String> prereq = theEncounter.getPrerequisites();
			// Check that all prerequisites are spelled correctly
			for (int j=0; (j<prereq.size()); ++j) {
				prereq.get(j).trim();
				findEncounter(prereq.get(j));
			}
			
			ArrayList<String> disqual = theEncounter.getDisqualifiers();
			//Check that all disqualifiers are spelled correctly
			for (int j=0; (j<disqual.size()); ++j) {
				disqual.get(j).trim();
				findEncounter(disqual.get(j));
			}
			
			// Check that this Encounter title is not duplicated
			for (int j=i+1; (j<encounters.size()); ++j) {
				if (theEncounter.getTitle().equals(encounters.get(j).getTitle())) {
					JOptionPane.showMessageDialog(myFrame,
						    "I found two encounters with this title: "+theEncounter.getTitle(),
						    "Fatal error",
						    JOptionPane.ERROR_MESSAGE);	
					System.exit(0);

				}
			}
		}
		theEncounter = null;
		encounterDoodad.theJList.setSelectedIndex(0);
		iOption = 0;
		iReaction = 0;
		setUpNewEncounter(0);
	}	
// **********************************************************************
	private void saveEncounters(String savedFileName) {
		
		takeDownOldEncounter(); // get the last changes made

		FileOutputStream outputStream = null;
		DocumentBuilder builder = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Element encounterList;

		try {
			outputStream = new FileOutputStream(savedFileName);
		} catch (Exception e) { }

		try {
			builder = factory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document doc = builder.newDocument();

		// write to doc here.
		encounterList = doc.createElement("ListWrapper");
		encounterList.setAttribute("version", String.valueOf(versionNumber));
		doc.appendChild(encounterList);
		
//		encounterList.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
//		encounterList.setAttribute("xsi:noNamespaceSchemaLocation", "Dictionary.xsd");
//		encounterList.setAttribute("version", String.valueOf(version));
//		encounterList.setAttribute("inactivityTimeout", String.valueOf(inactivityTimeout));
		
//		Element versionID = doc.createElement("dictionaryVersion");
//		versionID.setAttribute("Label", DeiktoLoader.dictionaryCurrentVersion);
//		encounterList.appendChild(versionID);

		// write copyright
//		Element cr = doc.createElement("copyright");
//		cr.setTextContent(getCopyright());
//		encounterList.appendChild(cr);
		
		Element actorList = doc.createElement("ActorList");
		encounterList.appendChild(actorList);
		for (Actor act:actors) {
			Element actorElement = doc.createElement("Actor");
			actorElement.setAttribute("label", act.getLabel());
			Element isMaleElement = doc.createElement("isMale");
			if (act.getIsMale())
				isMaleElement.setTextContent("true");
			else
				isMaleElement.setTextContent("false");
			actorElement.appendChild(isMaleElement);
			Element bad_GoodElement = doc.createElement("Bad_Good");
			if (Math.abs(act.getTrait(0)) <0.01f)
					act.setTrait(0, 0.0f);
			bad_GoodElement.setTextContent(String.valueOf(act.getTrait(0)));
			actorElement.appendChild(bad_GoodElement);
			Element false_HonestElement = doc.createElement("False_Honest");
			if (Math.abs(act.getTrait(1)) <0.01f)
				act.setTrait(1, 0.0f);
			false_HonestElement.setTextContent(String.valueOf(act.getTrait(1)));
			actorElement.appendChild(false_HonestElement);
			Element timid_DominantElement = doc.createElement("Timid_Dominant");
			if (Math.abs(act.getTrait(2)) <0.01f)
				act.setTrait(2, 0.0f);
			timid_DominantElement.setTextContent(String.valueOf(act.getTrait(2)));
			actorElement.appendChild(timid_DominantElement);
			actorList.appendChild(actorElement);
		}
		
		Element pTraitList = doc.createElement("PTraitList");
		encounterList.appendChild(pTraitList);
		for (int i=0; (i<EncounterEngine.cActors); ++i) {
			for (int j=0; (j<EncounterEngine.cActors); ++j) {
				if (i!=j) {
					for (int k=0; (k<3); ++k) {
						Element pTraitElement = doc.createElement("pTrait");
						pTraitElement.setAttribute("akind", factor[k]);
						pTraitElement.setAttribute("from", actors.get(i).getLabel());
						pTraitElement.setAttribute("to", actors.get(j).getLabel());
						pTraitElement.setAttribute("value", String.valueOf(actors.get(i).getPTrait(k, j)));
						pTraitList.appendChild(pTraitElement);
					}
				}
			}
		}
		
		Element eList = doc.createElement("EncounterList");
		encounterList.appendChild(eList);
		for (EncounterEngine.Encounter enc:encounters) {
			Element encounterElement = doc.createElement("Encounter");
			encounterElement.setAttribute("title", enc.getTitle());
			
			Element authorElement = doc.createElement("Author");
			authorElement.setTextContent(enc.getAuthor());
			encounterElement.appendChild(authorElement);
			
			Element prerequisitesElement = doc.createElement("Prerequisites");
			for (String st:enc.getPrerequisites()) {
				Element prereq = doc.createElement("Prereq");
				prereq.setTextContent(st);
				prerequisitesElement.appendChild(prereq);				
			}
			encounterElement.appendChild(prerequisitesElement);
			
			Element disqualifiersElement = doc.createElement("Disqualifiers");
			for (String st:enc.getDisqualifiers()) {
				Element disqual = doc.createElement("Disqual");
				disqual.setTextContent(st);
				disqualifiersElement.appendChild(disqual);				
			}
			encounterElement.appendChild(disqualifiersElement);
			
			Element excludeProtagonistElement = doc.createElement("ExcludeProtagonist");
			for (int i=0; (i<EncounterEngine.cActors); ++i) {
				Element actorElement = doc.createElement(actorLabel[i]);
				if (enc.getIsAllowedToBeProtagonist(i)) 
					actorElement.setTextContent("false");
				else
					actorElement.setTextContent("true");
				excludeProtagonistElement.appendChild(actorElement);
			}
			encounterElement.appendChild(excludeProtagonistElement);
			
			Element excludeAntagonistElement = doc.createElement("ExcludeAntagonist");
			for (int i=0; (i<EncounterEngine.cActors); ++i) {
				Element actorElement = doc.createElement(actorLabel[i]);
				if (enc.getIsAllowedToBeAntagonist(i)) 
					actorElement.setTextContent("false");
				else
					actorElement.setTextContent("true");
				excludeAntagonistElement.appendChild(actorElement);
			}
			encounterElement.appendChild(excludeAntagonistElement);
			
			Element dayWindow = doc.createElement("DayWindow");
			Element minimum = doc.createElement("Minimum");
			minimum.setTextContent(String.valueOf(enc.getFirstDay()));
			dayWindow.appendChild(minimum);
			Element maximum = doc.createElement("Maximum");
			maximum.setTextContent(String.valueOf(enc.getLastDay()));
			dayWindow.appendChild(maximum);
			encounterElement.appendChild(dayWindow);
			
			Element introText = doc.createElement("IntroText");
			introText.setTextContent(enc.getIntroText());
			encounterElement.appendChild(introText);
			
			for (int i=0; (i<EncounterEngine.cOptions); ++i) {
				Element eOption = doc.createElement("Option");
				Element optionText = doc.createElement("OptionText");
				optionText.setTextContent(enc.getOption(i).getText());
				eOption.appendChild(optionText);
				
				Element deltaPBad_Good = doc.createElement("DeltaPBad_Good");
				float x = (float)enc.getOption(i).getDeltaPBad_Good()/100.0f;
				if (Math.abs(x) < 0.01f) x = 0.0f;
				deltaPBad_Good.setTextContent(String.valueOf(x));
				eOption.appendChild(deltaPBad_Good);
				
				Element deltaPFalse_Honest = doc.createElement("DeltaPFalse_Honest");
				x = (float)enc.getOption(i).getDeltaPFalse_Honest()/100.0f;
				if (Math.abs(x) < 0.01f) x = 0.0f;
				deltaPFalse_Honest.setTextContent(String.valueOf(x));
				eOption.appendChild(deltaPFalse_Honest);
				
				Element deltaPTimid_Dominant = doc.createElement("DeltaPTimid_Dominant");
				x = (float)enc.getOption(i).getDeltaPTimid_Dominant()/100.0f;
				if (Math.abs(x) < 0.01f) x = 0.0f;
				deltaPTimid_Dominant.setTextContent(String.valueOf(x));
				eOption.appendChild(deltaPTimid_Dominant);
				
				for (int j=0; (j<EncounterEngine.cReactions); ++j) {
					Element eReaction = doc.createElement("Reaction");
					
					Element reactionText = doc.createElement("ReactionText");
					reactionText.setTextContent(enc.getOption(i).getReaction(j).getText());
					eReaction.appendChild(reactionText);
					
					Element desirableFormula = doc.createElement("DesirableFormula");

					Element firstTrait = doc.createElement("FirstTrait");
					firstTrait.setTextContent(enc.getOption(i).getReaction(j).getFirstTrait());
					desirableFormula.appendChild(firstTrait);

					Element secondTrait = doc.createElement("SecondTrait");
					secondTrait.setTextContent(enc.getOption(i).getReaction(j).getSecondTrait());
					desirableFormula.appendChild(secondTrait);

					Element bias = doc.createElement("Bias");
					bias.setTextContent(String.valueOf(enc.getOption(i).getReaction(j).getBias()));
					desirableFormula.appendChild(bias);
					
					eReaction.appendChild(desirableFormula);
					eOption.appendChild(eReaction);
				}
				encounterElement.appendChild(eOption);
			}
			eList.appendChild(encounterElement);
		}
			
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			tf.setAttribute("indent-number", new Integer(4));  //			
			Transformer transformer = tf.newTransformer();			
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			
			// initialize StreamResult with File object to save to file			
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(outputStream);
			transformer.transform(source,result);
		} 
		catch (TransformerConfigurationException e) {
			System.out.println("can't tranform the DOM model");
		}
		catch (TransformerException e) {
			System.out.println("can't transform the DOM model");
		}
	}
// ************************************************************
	public static void main(String args[]) {
		EncounterEditor theEditor=new EncounterEditor();

		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		}
		catch(Exception evt) {}
		while (!quitFlag) {
			theEditor.initialize();
			while (!quitFlag) { 
				try { Thread.sleep(10); } catch (Exception e) {} 
			}
		}
		System.exit(0);
	}
// ************************************************************
}
