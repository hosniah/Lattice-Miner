package fca.gui.context;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fca.LatticeMiner;
import fca.core.context.Context;
import fca.core.context.binary.BinaryContext;
import fca.core.context.nested.NestedContext;
import fca.core.context.nested.RankingAlgo;
import fca.core.context.nested.SupervisedMultivaluedRankingAlgo;
import fca.core.context.nested.UnsupervisedMultivaluedRankingAlgo;
import fca.core.context.valued.ValuedContext;
import fca.core.lattice.ConceptLattice;
import fca.core.lattice.NestedLattice;
import fca.core.rule.InformativeBasisAlgorithm;
import fca.core.rule.Rule;
import fca.core.rule.RuleAlgorithm;
import fca.core.util.BasicSet;
import fca.exception.AlreadyExistsException;
import fca.exception.InvalidTypeException;
import fca.exception.LatticeMinerException;
import fca.exception.ReaderException;
import fca.exception.WriterException;
import fca.gui.Viewer;
import fca.gui.context.assistant.LevelAdditionAssistant;
import fca.gui.context.assistant.NestedContextCreationAssistant;
import fca.gui.context.assistant.ValuedContextConversionAssistant;
import fca.gui.context.panel.LogicalAttributePanel;
import fca.gui.context.panel.MergeAttributesPanel;
import fca.gui.context.panel.RemoveAttributesPanel;
import fca.gui.context.panel.RemoveObjectsPanel;
import fca.gui.context.table.BinaryContextTable;
import fca.gui.context.table.ContextTable;
import fca.gui.context.table.ContextTableScrollPane;
import fca.gui.context.table.NestedContextTable;
import fca.gui.context.table.ValuedContextTable;
import fca.gui.context.table.model.ContextTableModel;
import fca.gui.context.table.model.NestedContextTableModel;
import fca.gui.lattice.LatticeViewer;
import fca.gui.lattice.element.GraphicalLattice;
import fca.gui.lattice.element.LatticeStructure;
import fca.gui.rule.RuleViewer;
import fca.gui.util.DialogBox;
import fca.gui.util.ExampleFileFilter;
import fca.gui.util.constant.LMIcons;
import fca.gui.util.constant.LMPreferences;
import fca.io.context.reader.txt.GaliciaSLFBinaryContextReader;
import fca.io.context.reader.txt.LMBinaryContextReader;
import fca.io.context.reader.txt.LMNestedContextReader;
import fca.io.context.reader.txt.LMValuedContextReader;
import fca.io.context.reader.xml.CexBinaryContextReader;
import fca.io.context.reader.xml.GaliciaXMLBinaryContextReader;
import fca.io.context.writer.txt.GaliciaSLFBinaryContextWriter;
import fca.io.context.writer.txt.LMBinaryContextWriter;
import fca.io.context.writer.txt.LMNestedContextWriter;
import fca.io.context.writer.txt.LMValuedContextWriter;
import fca.io.context.writer.xml.CexBinaryContextWriter;
import fca.io.context.writer.xml.GaliciaXMLBinaryContextWriter;
import fca.messages.GUIMessages;

/**
 * Viewer de contextes
 * 
 * @author Geneviève Roberge
 * @author Ludovic Thomas
 * @version 1.0
 */
public class ContextViewer extends Viewer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6515899540960064345L;

	/** L'unique instance de ContextViewer */
	private static ContextViewer SINGLETON = null;

	private JFrame frame;

	private JPanel panel;

	private JTabbedPane panelTab;

	private Vector<ContextTableScrollPane> contextPanes;

	private Vector<String> contextFiles;

	private ButtonGroup contextGroup;

	private JMenuItem noContextItem;

	private JLabel currentContextName;

	private int currentContextIdx;

	private int untitledCount;

	private JButton saveBtn;

	private JButton openBtn;

	private JButton newBinCtxBtn;

	private JButton removeCtxBtn;

	private JButton newAttributeBtn;

	private JButton newObjectBtn;

	private JButton delAttributeBtn;

	private JButton delObjectBtn;

	private JButton showLatBtn;

	private JButton showRulesBtn;

	private JMenu fileMenu;

	private JMenu newContext;

	private JMenuItem newBinaryContext;

	private JMenuItem newValuedContext;

	private JMenuItem newNestedContext;

	private JMenuItem openContext;

	private JMenu openRecentContext;

	private JMenuItem saveContext;

	private JMenuItem saveAsContext;

	private JMenuItem saveAllContexts;

	private JMenuItem closeContext;

	private JMenuItem closeAllContexts;

	private JMenuItem quitViewer;

	private JMenu editMenu;

	private JMenuItem addEmptyLevel;

	private JMenuItem addContextLevel;

	private JMenuItem removeLevel;

	private JMenuItem orderLevels;

	private JMenuItem addObject;

	private JMenuItem addAttribute;

	private JMenuItem removeObject;

	private JMenuItem removeAttribute;

	private JMenuItem mergeAttributes;

	private JMenuItem logicalAttribute;

	private JMenuItem compareAttributes;

	private JMenuItem createClusters;

	private JMenuItem convertToBinary;

	private JMenuItem convertToNested;

	private JMenu latticeMenu;

	private JMenuItem showLatticeMenu;

	private JMenu rulesMenu;

	private JMenuItem showRulesMenu;

	private JMenu windowMenu;

	private JMenu aboutMenu;

	private JMenuItem about;

	private GridBagConstraints constraints;

	private ContextViewer() {
		frame = this;
		setTitle(GUIMessages.getString("GUI.latticeMiner")); //$NON-NLS-1$
		setDefaultLookAndFeelDecorated(false);

		// Pour le fonctionnement sous Mac OSX avec la barre de menu en haut...
		System.setProperty("apple.laf.useScreenMenuBar", "true");

		// On cherche le style du systeme d'exploitation pour l'utiliser sur LM
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		setJMenuBar(buildMenuBar());
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildToolBar(), BorderLayout.NORTH);
		getContentPane().add(buildPanel(), BorderLayout.CENTER);
		setActiveActions();

		addWindowListener(this);
		contextFiles = new Vector<String>();
		pack();
		setVisible(true);
	}

	/**
	 * @return l'unique instance du {@link ContextViewer}
	 */
	public static ContextViewer getContextViewer() {
		if (SINGLETON == null) {
			SINGLETON = new ContextViewer();
		}
		return SINGLETON;
	}

	private JMenuBar buildMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		FrameMenuListener menuListener = new FrameMenuListener();

		/* ==== FILE MENU ==== */
		fileMenu = new JMenu(GUIMessages.getString("GUI.file")); //$NON-NLS-1$
		fileMenu.setMnemonic(KeyEvent.VK_F);

		newContext = new JMenu(GUIMessages.getString("GUI.new")); //$NON-NLS-1$
		newContext.setMnemonic(KeyEvent.VK_N);

		newBinaryContext = new JMenuItem(GUIMessages
				.getString("GUI.binaryContext")); //$NON-NLS-1$
		newBinaryContext.addActionListener(menuListener);
		newBinaryContext.setMnemonic(KeyEvent.VK_B);
		newBinaryContext.setAccelerator(KeyStroke.getKeyStroke("ctrl shift B")); //$NON-NLS-1$
		newContext.add(newBinaryContext);

		newValuedContext = new JMenuItem(GUIMessages
				.getString("GUI.valuedContext")); //$NON-NLS-1$
		newValuedContext.addActionListener(menuListener);
		newValuedContext.setMnemonic(KeyEvent.VK_V);
		newValuedContext.setAccelerator(KeyStroke.getKeyStroke("ctrl shift V")); //$NON-NLS-1$
		newValuedContext.setEnabled(false);
		newContext.add(newValuedContext);

		newNestedContext = new JMenuItem(GUIMessages
				.getString("GUI.nestedContext")); //$NON-NLS-1$
		newNestedContext.addActionListener(menuListener);
		newNestedContext.setMnemonic(KeyEvent.VK_N);
		newNestedContext.setAccelerator(KeyStroke.getKeyStroke("ctrl shift N")); //$NON-NLS-1$
		newNestedContext.setEnabled(true);
		newContext.add(newNestedContext);
		fileMenu.add(newContext);

		openContext = new JMenuItem(GUIMessages.getString("GUI.open")); //$NON-NLS-1$
		openContext.addActionListener(menuListener);
		openContext.setMnemonic(KeyEvent.VK_O);
		openContext.setAccelerator(KeyStroke.getKeyStroke("ctrl O")); //$NON-NLS-1$
		fileMenu.add(openContext);
		openContext.setEnabled(true);

		openRecentContext = new JMenu(GUIMessages.getString("GUI.openRecent")); //$NON-NLS-1$
		openRecentContext.addActionListener(menuListener);
		fileMenu.add(openRecentContext);
		openRecentContext.setEnabled(false);

		// Rajoute les fichiers recements ouverts
		int nbRecent = setRecentFileMenuItem();
		if (nbRecent != 0)
			openRecentContext.setEnabled(true);

		fileMenu.addSeparator();

		saveContext = new JMenuItem(GUIMessages.getString("GUI.save")); //$NON-NLS-1$
		saveContext.addActionListener(menuListener);
		saveContext.setMnemonic(KeyEvent.VK_S);
		saveContext.setAccelerator(KeyStroke.getKeyStroke("ctrl S")); //$NON-NLS-1$
		saveContext.setEnabled(false);
		fileMenu.add(saveContext);

		saveAsContext = new JMenuItem(GUIMessages.getString("GUI.saveAs")); //$NON-NLS-1$
		saveAsContext.addActionListener(menuListener);
		saveAsContext.setEnabled(false);
		fileMenu.add(saveAsContext);

		saveAllContexts = new JMenuItem(GUIMessages.getString("GUI.saveAll")); //$NON-NLS-1$
		saveAllContexts.addActionListener(menuListener);
		saveAllContexts.setMnemonic(KeyEvent.VK_S);
		saveAllContexts.setAccelerator(KeyStroke.getKeyStroke("ctrl shift S")); //$NON-NLS-1$
		saveAllContexts.setEnabled(false);
		fileMenu.add(saveAllContexts);
		fileMenu.addSeparator();

		closeContext = new JMenuItem(GUIMessages.getString("GUI.close")); //$NON-NLS-1$
		closeContext.addActionListener(menuListener);
		closeContext.setMnemonic(KeyEvent.VK_C);
		closeContext.setAccelerator(KeyStroke.getKeyStroke("ctrl C")); //$NON-NLS-1$
		closeContext.setEnabled(false);
		fileMenu.add(closeContext);

		closeAllContexts = new JMenuItem(GUIMessages.getString("GUI.closeAll")); //$NON-NLS-1$
		closeAllContexts.addActionListener(menuListener);
		closeAllContexts.setMnemonic(KeyEvent.VK_C);
		closeAllContexts.setAccelerator(KeyStroke.getKeyStroke("ctrl shift C")); //$NON-NLS-1$
		closeAllContexts.setEnabled(false);
		fileMenu.add(closeAllContexts);
		fileMenu.addSeparator();

		quitViewer = new JMenuItem(GUIMessages.getString("GUI.quit")); //$NON-NLS-1$
		quitViewer.addActionListener(menuListener);
		quitViewer.setMnemonic(KeyEvent.VK_Q);
		quitViewer.setAccelerator(KeyStroke.getKeyStroke("ctrl Q")); //$NON-NLS-1$
		fileMenu.add(quitViewer);

		menuBar.add(fileMenu);

		/* ==== EDIT MENU ==== */
		editMenu = new JMenu(GUIMessages.getString("GUI.edit")); //$NON-NLS-1$
		editMenu.setMnemonic(KeyEvent.VK_E);

		addEmptyLevel = new JMenuItem(GUIMessages
				.getString("GUI.addEmptylevel")); //$NON-NLS-1$
		addEmptyLevel.addActionListener(menuListener);
		editMenu.add(addEmptyLevel);

		addContextLevel = new JMenuItem(GUIMessages
				.getString("GUI.addContextLevel")); //$NON-NLS-1$
		addContextLevel.addActionListener(menuListener);
		editMenu.add(addContextLevel);

		removeLevel = new JMenuItem(GUIMessages
				.getString("GUI.removeLastLevel")); //$NON-NLS-1$
		removeLevel.addActionListener(menuListener);
		editMenu.add(removeLevel);

		orderLevels = new JMenuItem(GUIMessages.getString("GUI.orderLevels")); //$NON-NLS-1$
		orderLevels.addActionListener(menuListener);
		editMenu.add(orderLevels);
		editMenu.addSeparator();

		addObject = new JMenuItem(GUIMessages.getString("GUI.addObject")); //$NON-NLS-1$
		addObject.addActionListener(menuListener);
		addObject.setMnemonic(KeyEvent.VK_O);
		addObject.setAccelerator(KeyStroke.getKeyStroke("ctrl shift O")); //$NON-NLS-1$
		editMenu.add(addObject);

		addAttribute = new JMenuItem(GUIMessages.getString("GUI.addAttribute")); //$NON-NLS-1$
		addAttribute.addActionListener(menuListener);
		addAttribute.setMnemonic(KeyEvent.VK_A);
		addAttribute.setDisplayedMnemonicIndex(4);
		addAttribute.setAccelerator(KeyStroke.getKeyStroke("ctrl shift A")); //$NON-NLS-1$
		editMenu.add(addAttribute);
		editMenu.addSeparator();

		removeObject = new JMenuItem(GUIMessages.getString("GUI.removeObjects")); //$NON-NLS-1$
		removeObject.addActionListener(menuListener);
		removeObject.setMnemonic(KeyEvent.VK_O);
		removeObject.setDisplayedMnemonicIndex(7);
		editMenu.add(removeObject);

		removeAttribute = new JMenuItem(GUIMessages
				.getString("GUI.removeAttributes")); //$NON-NLS-1$
		removeAttribute.addActionListener(menuListener);
		removeAttribute.setMnemonic(KeyEvent.VK_A);
		editMenu.add(removeAttribute);

		mergeAttributes = new JMenuItem(GUIMessages
				.getString("GUI.mergeAttributes")); //$NON-NLS-1$
		mergeAttributes.addActionListener(menuListener);
		mergeAttributes.setMnemonic(KeyEvent.VK_M);
		editMenu.add(mergeAttributes);

		logicalAttribute = new JMenuItem(GUIMessages
				.getString("GUI.addAttributeComposition")); //$NON-NLS-1$
		logicalAttribute.addActionListener(menuListener);
		editMenu.add(logicalAttribute);

		compareAttributes = new JMenuItem(GUIMessages
				.getString("GUI.compareAttributes")); //$NON-NLS-1$
		compareAttributes.addActionListener(menuListener);
		compareAttributes.setMnemonic(KeyEvent.VK_C);
		editMenu.add(compareAttributes);
		editMenu.addSeparator();

		createClusters = new JMenuItem(GUIMessages
				.getString("GUI.sortObjectInClusters")); //$NON-NLS-1$
		createClusters.addActionListener(menuListener);
		createClusters.setMnemonic(KeyEvent.VK_S);
		editMenu.add(createClusters);
		editMenu.addSeparator();

		convertToBinary = new JMenuItem(GUIMessages
				.getString("GUI.convertToBinaryContext")); //$NON-NLS-1$
		convertToBinary.addActionListener(menuListener);
		editMenu.add(convertToBinary);

		convertToNested = new JMenuItem(GUIMessages
				.getString("GUI.convertToNestedContext")); //$NON-NLS-1$
		convertToNested.addActionListener(menuListener);
		editMenu.add(convertToNested);

		menuBar.add(editMenu);

		/* ==== LATTICE MENU ==== */
		latticeMenu = new JMenu(GUIMessages.getString("GUI.lattice")); //$NON-NLS-1$
		latticeMenu.setMnemonic(KeyEvent.VK_T);

		showLatticeMenu = new JMenuItem(GUIMessages
				.getString("GUI.showLattice")); //$NON-NLS-1$
		showLatticeMenu.addActionListener(menuListener);
		showLatticeMenu.setMnemonic(KeyEvent.VK_L);
		showLatticeMenu.setAccelerator(KeyStroke.getKeyStroke("ctrl L")); //$NON-NLS-1$
		latticeMenu.add(showLatticeMenu);

		menuBar.add(latticeMenu);

		/* ==== RULES MENU ==== */
		rulesMenu = new JMenu(GUIMessages.getString("GUI.rules")); //$NON-NLS-1$
		rulesMenu.setMnemonic(KeyEvent.VK_R);

		showRulesMenu = new JMenuItem(GUIMessages.getString("GUI.showRules")); //$NON-NLS-1$
		showRulesMenu.addActionListener(menuListener);
		showRulesMenu.setMnemonic(KeyEvent.VK_R);
		showRulesMenu.setAccelerator(KeyStroke.getKeyStroke("ctrl R")); //$NON-NLS-1$
		showRulesMenu.setEnabled(false);
		rulesMenu.add(showRulesMenu);

		menuBar.add(rulesMenu);

		/* ==== WINDOW MENU ==== */
		windowMenu = new JMenu(GUIMessages.getString("GUI.window")); //$NON-NLS-1$
		windowMenu.setMnemonic(KeyEvent.VK_W);
		menuBar.add(windowMenu);

		/* ==== ABOUT MENU ==== */
		aboutMenu = new JMenu(GUIMessages.getString("GUI.about")); //$NON-NLS-1$
		aboutMenu.setMnemonic(KeyEvent.VK_A);
		menuBar.add(aboutMenu);

		about = new JMenuItem(GUIMessages.getString("GUI.aboutMore")); //$NON-NLS-1$
		about.setMnemonic(KeyEvent.VK_A);
		about.addActionListener(menuListener);
		aboutMenu.add(about);

		return menuBar;
	}

	/**
	 * Créer les {@link MenuItem} relatifs aux fichiers recements utilisés
	 * 
	 * @return le nombre de {@link MenuItem} réellement pris en compte
	 */
	private int setRecentFileMenuItem() {
		int nbRecentInMenu = 0;

		// Recupère les préférences de Lattice Miner
		Preferences preferences = LMPreferences.getPreferences();

		// Nom du noeud des preferences des fichiers recents
		int nbRecents = preferences.getInt(LMPreferences.NB_RECENTS, -1);

		if (nbRecents != -1) {

			int numNode = nbRecents;

			// Nom du noeud des preferences du "ième" fichier recent
			String irecentNode = LMPreferences.RECENTS + "/" + numNode; //$NON-NLS-1$
			String filename = preferences.get(irecentNode, ""); //$NON-NLS-1$

			// Charge les 5 derniers fichiers ouverts (s'ils existent)
			while (!filename.equals("") && nbRecentInMenu < 5) { //$NON-NLS-1$

				// Rajoute le MenuItem
				JMenuItem recentMenuItem;
				recentMenuItem = new JMenuItem(filename);
				recentMenuItem.addActionListener(new RecentMenuListener(
						filename));
				openRecentContext.add(recentMenuItem);
				nbRecentInMenu++;

				// Decremente numNode
				numNode = numNode - 1;
				if (numNode == -1)
					numNode = 9;

				// Charge le fichier suivant
				irecentNode = LMPreferences.RECENTS + "/" + numNode; //$NON-NLS-1$
				filename = preferences.get(irecentNode, ""); //$NON-NLS-1$
			}

		}

		return nbRecentInMenu;
	}

	private JPanel buildPanel() {
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(675, 400));
		panel.setLayout(new GridBagLayout());

		constraints = new GridBagConstraints();
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.NONE;

		panelTab = new JTabbedPane();
		panelTab.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int activeIndex = panelTab.getSelectedIndex();
				if (activeIndex != -1)
					selectContextAt(activeIndex);
			}
		});

		currentContextName = new JLabel(GUIMessages
				.getString("GUI.noContextLoaded")); //$NON-NLS-1$
		panel.add(currentContextName, constraints);

		constraints.gridy = 1;
		constraints.weighty = 1.0;
		constraints.weightx = 1.0;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;

		contextGroup = new ButtonGroup();
		contextPanes = new Vector<ContextTableScrollPane>();
		currentContextIdx = -1;
		untitledCount = 0;

		noContextItem = new JMenuItem(GUIMessages
				.getString("GUI.noContextLoaded")); //$NON-NLS-1$
		noContextItem.setEnabled(false);
		windowMenu.add(noContextItem);

		return panel;
	}

	private JToolBar buildToolBar() {
		JToolBar toolBar = new JToolBar(GUIMessages.getString("GUI.quickTools")); //$NON-NLS-1$
		ToolBarListener listener = new ToolBarListener();

		saveBtn = new JButton(LMIcons.getSave());
		saveBtn.addActionListener(listener);
		saveBtn.setToolTipText(GUIMessages.getString("GUI.save")); //$NON-NLS-1$
		saveBtn.setMnemonic(KeyEvent.VK_S);
		toolBar.add(saveBtn);

		openBtn = new JButton(LMIcons.getOpenFolder());
		openBtn.addActionListener(listener);
		openBtn.setToolTipText(GUIMessages.getString("GUI.openContext")); //$NON-NLS-1$
		openBtn.setMnemonic(KeyEvent.VK_O);
		toolBar.add(openBtn);

		newBinCtxBtn = new JButton(LMIcons.getNewBinContext());
		newBinCtxBtn.addActionListener(listener);
		newBinCtxBtn.setToolTipText(GUIMessages
				.getString("GUI.newBinaryContext")); //$NON-NLS-1$
		newBinCtxBtn.setMnemonic(KeyEvent.VK_B);
		toolBar.add(newBinCtxBtn);

		removeCtxBtn = new JButton(LMIcons.getCloseContext());
		removeCtxBtn.addActionListener(listener);
		removeCtxBtn.setToolTipText(GUIMessages
				.getString("GUI.closeCurrentContext")); //$NON-NLS-1$
		removeCtxBtn.setMnemonic(KeyEvent.VK_C);
		toolBar.add(removeCtxBtn);

		newObjectBtn = new JButton(LMIcons.getAddObject());
		newObjectBtn.addActionListener(listener);
		newObjectBtn.setToolTipText(GUIMessages.getString("GUI.addObject")); //$NON-NLS-1$
		toolBar.add(newObjectBtn);

		newAttributeBtn = new JButton(LMIcons.getAddAttribute());
		newAttributeBtn.addActionListener(listener);
		newAttributeBtn.setToolTipText(GUIMessages
				.getString("GUI.addAttribute")); //$NON-NLS-1$
		toolBar.add(newAttributeBtn);

		delObjectBtn = new JButton(LMIcons.getDelObject());
		delObjectBtn.addActionListener(listener);
		delObjectBtn.setToolTipText(GUIMessages.getString("GUI.removeObject")); //$NON-NLS-1$
		toolBar.add(delObjectBtn);

		delAttributeBtn = new JButton(LMIcons.getDelAttribute());
		delAttributeBtn.addActionListener(listener);
		delAttributeBtn.setToolTipText(GUIMessages
				.getString("GUI.removeAttribute")); //$NON-NLS-1$
		toolBar.add(delAttributeBtn);

		showLatBtn = new JButton(LMIcons.getShowLattice());
		showLatBtn.addActionListener(listener);
		showLatBtn.setToolTipText(GUIMessages.getString("GUI.showLattice")); //$NON-NLS-1$
		showLatBtn.setMnemonic(KeyEvent.VK_L);
		toolBar.add(showLatBtn);

		showRulesBtn = new JButton(LMIcons.getShowRulesLittle());
		showRulesBtn.addActionListener(listener);
		showRulesBtn.setToolTipText(GUIMessages.getString("GUI.showRules")); //$NON-NLS-1$
		showRulesBtn.setMnemonic(KeyEvent.VK_R);
		toolBar.add(showRulesBtn);

		return toolBar;
	}

	private void setActiveActions() {
		/* Elements du menu "File" */
		newBinaryContext.setEnabled(true);
		newValuedContext.setEnabled(true);
		newNestedContext.setEnabled(true);
		openContext.setEnabled(true);
		saveContext.setEnabled(true);
		saveAsContext.setEnabled(true);
		saveAllContexts.setEnabled(true);
		closeContext.setEnabled(true);
		closeAllContexts.setEnabled(true);
		quitViewer.setEnabled(true);

		if (contextPanes.size() == 0) {
			/* Boutons de la toolbar */
			saveBtn.setEnabled(false);
			openBtn.setEnabled(true);
			newBinCtxBtn.setEnabled(true);
			removeCtxBtn.setEnabled(false);
			newAttributeBtn.setEnabled(false);
			newObjectBtn.setEnabled(false);
			delAttributeBtn.setEnabled(false);
			delObjectBtn.setEnabled(false);
			showLatBtn.setEnabled(false);
			showRulesBtn.setEnabled(false);

			/* Elements du menu "Edit" */
			addEmptyLevel.setEnabled(false);
			addContextLevel.setEnabled(false);
			removeLevel.setEnabled(false);
			orderLevels.setEnabled(false);
			addObject.setEnabled(false);
			addAttribute.setEnabled(false);
			mergeAttributes.setEnabled(false);
			logicalAttribute.setEnabled(false);
			compareAttributes.setEnabled(false);
			removeObject.setEnabled(false);
			removeAttribute.setEnabled(false);
			createClusters.setEnabled(false);
			convertToBinary.setEnabled(false);
			convertToNested.setEnabled(false);

			/* Elements du menu "Lattice" */
			showLatticeMenu.setEnabled(false);

			/* Elements du menu "Rules" */
			showRulesMenu.setEnabled(false);
			return;
		}

		ContextTableScrollPane selectedPane = contextPanes
				.elementAt(currentContextIdx);
		Context currentContext = ((ContextTableModel) selectedPane
				.getContextTable().getModel()).getContext();

		if (currentContext instanceof NestedContext) {
			/* Boutons de la toolbar */
			saveBtn.setEnabled(true);
			openBtn.setEnabled(true);
			newBinCtxBtn.setEnabled(true);
			removeCtxBtn.setEnabled(true);
			newAttributeBtn.setEnabled(false);
			newObjectBtn.setEnabled(false);
			delAttributeBtn.setEnabled(false);
			delObjectBtn.setEnabled(false);
			showLatBtn.setEnabled(true);
			showRulesBtn.setEnabled(true);

			/* Elements du menu "Edit" */
			addEmptyLevel.setEnabled(true);
			addContextLevel.setEnabled(true);
			removeLevel.setEnabled(true);
			orderLevels.setEnabled(true);
			addObject.setEnabled(false);
			addAttribute.setEnabled(false);
			mergeAttributes.setEnabled(false);
			logicalAttribute.setEnabled(false);
			compareAttributes.setEnabled(false);
			removeObject.setEnabled(false);
			removeAttribute.setEnabled(false);
			createClusters.setEnabled(false);
			convertToBinary.setEnabled(true);
			convertToNested.setEnabled(false);

			/* Elements du menu "Lattice" */
			showLatticeMenu.setEnabled(true);

			/* Elements du menu "Rules" */
			showRulesMenu.setEnabled(true);
		}

		else if (currentContext instanceof BinaryContext) {
			/* Boutons de la toolbar */
			saveBtn.setEnabled(true);
			openBtn.setEnabled(true);
			newBinCtxBtn.setEnabled(true);
			removeCtxBtn.setEnabled(true);
			newAttributeBtn.setEnabled(true);
			newObjectBtn.setEnabled(true);
			delAttributeBtn.setEnabled(true);
			delObjectBtn.setEnabled(true);
			showLatBtn.setEnabled(true);
			showRulesBtn.setEnabled(true);

			/* Elements du menu "Edit" */
			addEmptyLevel.setEnabled(false);
			addContextLevel.setEnabled(false);
			removeLevel.setEnabled(false);
			orderLevels.setEnabled(false);
			addObject.setEnabled(true);
			addAttribute.setEnabled(true);
			mergeAttributes.setEnabled(true);
			logicalAttribute.setEnabled(true);
			removeObject.setEnabled(true);
			removeAttribute.setEnabled(true);
			createClusters.setEnabled(true);
			compareAttributes.setEnabled(true);
			convertToBinary.setEnabled(false);
			convertToNested.setEnabled(true);

			/* Elements du menu "Lattice" */
			showLatticeMenu.setEnabled(true);

			/* Elements du menu "Rules" */
			showRulesMenu.setEnabled(true);
		}

		else if (currentContext instanceof ValuedContext) {
			/* Boutons de la toolbar */
			saveBtn.setEnabled(true);
			openBtn.setEnabled(true);
			newBinCtxBtn.setEnabled(true);
			removeCtxBtn.setEnabled(true);
			newAttributeBtn.setEnabled(true);
			newObjectBtn.setEnabled(true);
			delAttributeBtn.setEnabled(true);
			delObjectBtn.setEnabled(true);
			showLatBtn.setEnabled(false);
			showRulesBtn.setEnabled(false);

			/* Elements du menu "Edit" */
			addEmptyLevel.setEnabled(false);
			addContextLevel.setEnabled(false);
			removeLevel.setEnabled(false);
			orderLevels.setEnabled(false);
			addObject.setEnabled(true);
			addAttribute.setEnabled(true);
			mergeAttributes.setEnabled(true);
			logicalAttribute.setEnabled(false);
			compareAttributes.setEnabled(false);
			removeObject.setEnabled(true);
			removeAttribute.setEnabled(true);
			createClusters.setEnabled(false);
			convertToBinary.setEnabled(true);
			convertToNested.setEnabled(false);

			/* Elements du menu "Lattice" */
			showLatticeMenu.setEnabled(false);

			/* Elements du menu "Rules" */
			showRulesMenu.setEnabled(false);
		}

		else {
			/* Boutons de la toolbar */
			saveBtn.setEnabled(false);
			openBtn.setEnabled(false);
			newBinCtxBtn.setEnabled(false);
			removeCtxBtn.setEnabled(false);
			newAttributeBtn.setEnabled(false);
			newObjectBtn.setEnabled(false);
			delAttributeBtn.setEnabled(false);
			delObjectBtn.setEnabled(false);
			showLatBtn.setEnabled(false);
			showRulesBtn.setEnabled(false);

			/* Elements du menu "Edit" */
			addEmptyLevel.setEnabled(false);
			addContextLevel.setEnabled(false);
			removeLevel.setEnabled(false);
			orderLevels.setEnabled(false);
			addObject.setEnabled(false);
			addAttribute.setEnabled(false);
			mergeAttributes.setEnabled(false);
			logicalAttribute.setEnabled(false);
			compareAttributes.setEnabled(false);
			removeObject.setEnabled(false);
			removeAttribute.setEnabled(false);
			createClusters.setEnabled(false);
			convertToBinary.setEnabled(false);
			convertToNested.setEnabled(false);

			/* Elements du menu "Lattice" */
			showLatticeMenu.setEnabled(false);

			/* Elements du menu "Rules" */
			showRulesMenu.setEnabled(false);
		}
	}

	/**
	 * @return le panneau actuellement selectionné
	 */
	public ContextTableScrollPane getSelectedPane() {
		if (contextPanes.size() == 0)
			return null;

		else
			return contextPanes.elementAt(currentContextIdx);
	}

	/**
	 * Permet de sélectionner un contexte en particulier
	 * 
	 * @param idx
	 *            l'index du {@link ContextTableScrollPane} a mettre en avant
	 */
	private void selectContextAt(int idx) {
		if (idx >= contextPanes.size())
			return;

		// Recupere le panneau souhaité
		currentContextIdx = idx;
		ContextTableScrollPane selectedPane = contextPanes
				.elementAt(currentContextIdx);

		// Recherche le nom du context
		Context currentContext = ((ContextTableModel) selectedPane
				.getContextTable().getModel()).getContext();
		if (currentContext instanceof NestedContext)
			currentContextName
					.setText(GUIMessages.getString("GUI.context") + " : " + ((NestedContext) currentContext).getNestedContextName()); //$NON-NLS-1$ //$NON-NLS-2$
		else
			currentContextName
					.setText(GUIMessages.getString("GUI.context") + " : " + currentContext.getName()); //$NON-NLS-1$ //$NON-NLS-2$

		// Rajoute si necesaire le panelTab
		if (!panel.isAncestorOf(panelTab)) {
			panel.add(panelTab, constraints);
		}

		// Rajoute l'onglet s'il n'existe pas
		int tabIndex = panelTab.indexOfComponent(selectedPane);
		if (tabIndex == -1) {
			panelTab.addTab(currentContextName.getText(), selectedPane);
			tabIndex = panelTab.indexOfComponent(selectedPane);
		}
		panelTab.setSelectedIndex(tabIndex);

		((JRadioButtonMenuItem) windowMenu.getMenuComponent(currentContextIdx))
				.setSelected(true);

		setActiveActions();
		frame.repaint();
	}

	/**
	 * Permet d'ajouter un contexte binaire au {@link ContextViewer}
	 * 
	 * @param binCtx
	 *            le contexte binaire a ajouter
	 */
	public void addBinaryContext(BinaryContext binCtx) {

		BinaryContextTable newTable = new BinaryContextTable(binCtx);
		ContextTableScrollPane newScrollPane = new ContextTableScrollPane(
				newTable);
		contextPanes.add(newScrollPane);

		// MenuItem pour le contexte courant
		JRadioButtonMenuItem contextBtn;
		contextBtn = new JRadioButtonMenuItem(binCtx.getName());
		contextBtn.addActionListener(new ViewTableListener(newScrollPane));
		contextGroup.add(contextBtn);
		windowMenu.add(contextBtn);

		// Supprime le MenuItem "No Context" s'il existe
		if (windowMenu.isMenuComponent(noContextItem))
			windowMenu.remove(noContextItem);
		contextBtn.setSelected(true);

		// Rajoute le raccourci "Ctrl + Num" au context
		int num = windowMenu.getMenuComponentCount();
		contextBtn.setAccelerator(KeyStroke.getKeyStroke("ctrl NUMPAD" + num)); //$NON-NLS-1$

		// Selectionne effectivement le contexte
		selectContextAt(contextPanes.size() - 1);
	}

	private void createBinaryContext() {
		String newName = DialogBox
				.showInputQuestion(
						panel,
						GUIMessages.getString("GUI.enterNewContextName"), GUIMessages.getString("GUI.createNewBinaryContext")); //$NON-NLS-1$ //$NON-NLS-2$

		if (newName != null && newName.length() == 0) {
			untitledCount++;
			newName = GUIMessages.getString("GUI.untitled") + "_" + untitledCount; //$NON-NLS-1$ //$NON-NLS-2$
		}

		int objNb = -1;
		while (objNb == -1 && newName != null) {
			try {
				String str = DialogBox
						.showInputQuestion(
								panel,
								GUIMessages
										.getString("GUI.enterNumberOfObjects"), GUIMessages.getString("GUI.createNewBinaryContext")); //$NON-NLS-1$ //$NON-NLS-2$
				if (str != null)
					objNb = Integer.parseInt(str);
				else
					objNb = -2;
			} catch (NumberFormatException nfe) {
				objNb = -1;
			}
		}

		int attNb = -1;
		while (attNb == -1 && objNb != -2 && newName != null) {
			try {
				String str = DialogBox
						.showInputQuestion(
								panel,
								GUIMessages
										.getString("GUI.enterNumberOfAttributes"), GUIMessages.getString("GUI.createNewBinaryContext")); //$NON-NLS-1$ //$NON-NLS-2$
				if (str != null)
					attNb = Integer.parseInt(str);
				else
					attNb = -2;
			} catch (NumberFormatException nfe) {
				attNb = -1;
			}
		}

		if (attNb >= 0) {
			// if(contextPanes.size() == 0)
			// windowMenu.removeAll();

			BinaryContext newBinCtx = new BinaryContext(newName, objNb, attNb);
			addBinaryContext(newBinCtx);
		}

		else {
			DialogBox
					.showMessageInformation(
							panel,
							GUIMessages
									.getString("GUI.noContextHasBeenCreated"), GUIMessages.getString("GUI.noContextCreated")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Permet d'ajouter un contexte valué au {@link ContextViewer}
	 * 
	 * @param valCtx
	 *            le contexte binaire a ajouter
	 */
	public void addValuedContext(ValuedContext valCtx) {

		ValuedContextTable newTable = new ValuedContextTable(valCtx);
		ContextTableScrollPane newScrollPane = new ContextTableScrollPane(
				newTable);
		contextPanes.add(newScrollPane);

		JRadioButtonMenuItem contextBtn;
		contextBtn = new JRadioButtonMenuItem(valCtx.getName());
		contextBtn.addActionListener(new ViewTableListener(newScrollPane));
		contextGroup.add(contextBtn);
		windowMenu.add(contextBtn);
		if (windowMenu.isMenuComponent(noContextItem))
			windowMenu.remove(noContextItem);
		contextBtn.setSelected(true);

		int num = windowMenu.getMenuComponentCount();
		contextBtn.setAccelerator(KeyStroke.getKeyStroke("ctrl NUMPAD" + num)); //$NON-NLS-1$

		selectContextAt(contextPanes.size() - 1);
	}

	private void createValuedContext() {
		String newName = DialogBox
				.showInputQuestion(
						panel,
						GUIMessages.getString("GUI.enterNewContextName"), GUIMessages.getString("GUI.createNewValuedContext")); //$NON-NLS-1$ //$NON-NLS-2$

		if (newName != null && newName.length() == 0) {
			untitledCount++;
			newName = GUIMessages.getString("GUI.untitled") + "_" + untitledCount; //$NON-NLS-1$ //$NON-NLS-2$
		}

		int objNb = -1;
		while (objNb == -1 && newName != null) {
			try {
				String str = DialogBox
						.showInputQuestion(
								panel,
								GUIMessages
										.getString("GUI.enterNumberOfObjects"), GUIMessages.getString("GUI.createNewValuedContext")); //$NON-NLS-1$ //$NON-NLS-2$
				if (str != null)
					objNb = Integer.parseInt(str);
				else
					objNb = -2;
			} catch (NumberFormatException nfe) {
				objNb = -1;
			}
		}

		int attNb = -1;
		while (attNb == -1 && objNb != -2 && newName != null) {
			try {
				String str = DialogBox
						.showInputQuestion(
								panel,
								GUIMessages
										.getString("GUI.enterNumberOfAttributes"), GUIMessages.getString("GUI.createNewValuedContext")); //$NON-NLS-1$ //$NON-NLS-2$
				if (str != null)
					attNb = Integer.parseInt(str);
				else
					attNb = -2;
			} catch (NumberFormatException nfe) {
				attNb = -1;
			}
		}

		if (attNb >= 0) {
			// if(contextPanes.size() == 0)
			// windowMenu.removeAll();

			ValuedContext newValCtx = new ValuedContext(newName, objNb, attNb);
			addValuedContext(newValCtx);
		}

		else {
			DialogBox
					.showMessageInformation(
							panel,
							GUIMessages
									.getString("GUI.noContextHasBeenCreated"), GUIMessages.getString("GUI.noContextCreated")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Permet d'ajouter un contexte imbriqué au {@link ContextViewer}
	 * 
	 * @param nestedCtx
	 *            le contexte binaire a ajouter
	 */
	public void addNestedContext(NestedContext nestedCtx) {

		NestedContextTable newTable = new NestedContextTable(nestedCtx);
		ContextTableScrollPane newScrollPane = new ContextTableScrollPane(
				newTable);
		contextPanes.add(newScrollPane);

		JRadioButtonMenuItem contextBtn;
		contextBtn = new JRadioButtonMenuItem(nestedCtx.getNestedContextName());
		contextBtn.addActionListener(new ViewTableListener(newScrollPane));
		contextGroup.add(contextBtn);
		if (windowMenu.isMenuComponent(noContextItem))
			windowMenu.remove(noContextItem);
		windowMenu.add(contextBtn);

		int num = windowMenu.getMenuComponentCount();
		contextBtn.setAccelerator(KeyStroke.getKeyStroke("ctrl NUMPAD" + num)); //$NON-NLS-1$

		contextBtn.setSelected(true);
		selectContextAt(contextPanes.size() - 1);
	}

	private void createNestedContext() {
		Vector<BinaryContext> contexts = new Vector<BinaryContext>();
		for (int i = 0; i < contextPanes.size(); i++) {
			ContextTableScrollPane scrollPane = contextPanes.elementAt(i);
			Context ctx = ((ContextTableModel) scrollPane.getContextTable()
					.getModel()).getContext();
			if ((ctx instanceof BinaryContext)
					&& !(ctx instanceof NestedContext))
				contexts.add((BinaryContext) ctx);
		}

		new NestedContextCreationAssistant(this, contexts);
	}

	/**
	 * Genere une fenetre pour pouvoir ouvrir un contexte existant en fichier et
	 * verifie les types et genere le contexte en fonction du type de fichier
	 * via les readers
	 */
	private void openContext() {
		JFileChooser fileChooser = new JFileChooser(LMPreferences
				.getLastDirectory());

		// Propriétés du fileChooser
		fileChooser.setApproveButtonText(GUIMessages
				.getString("GUI.openButton")); //$NON-NLS-1$
		fileChooser.setDialogTitle(GUIMessages.getString("GUI.openAContext")); //$NON-NLS-1$
		fileChooser.setAcceptAllFileFilterUsed(true);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		// Gere les extensions compatibles (cex, lmv, lmn, lmb)
		ExampleFileFilter filterCex = new ExampleFileFilter(
				"cex", GUIMessages.getString("GUI.conceptExplorerBinaryFormat")); //$NON-NLS-1$ //$NON-NLS-2$
		fileChooser.addChoosableFileFilter(filterCex);
		ExampleFileFilter filterGaliciaBinSLF = new ExampleFileFilter(
				"slf", GUIMessages.getString("GUI.galiciaSLFBinaryFormat")); //$NON-NLS-1$ //$NON-NLS-2$
		fileChooser.addChoosableFileFilter(filterGaliciaBinSLF);
		ExampleFileFilter filterGaliciaBin = new ExampleFileFilter(
				"bin.xml", GUIMessages.getString("GUI.galiciaXMLBinaryFormat")); //$NON-NLS-1$ //$NON-NLS-2$
		fileChooser.addChoosableFileFilter(filterGaliciaBin);
		ExampleFileFilter filterValued = new ExampleFileFilter(
				"lmv", GUIMessages.getString("GUI.LatticeMinerValuedFormat")); //$NON-NLS-1$ //$NON-NLS-2$
		fileChooser.addChoosableFileFilter(filterValued);
		ExampleFileFilter filterNested = new ExampleFileFilter(
				"lmn", GUIMessages.getString("GUI.LatticeMinerNestedFormat")); //$NON-NLS-1$ //$NON-NLS-2$
		fileChooser.addChoosableFileFilter(filterNested);
		ExampleFileFilter filterBinary = new ExampleFileFilter(
				"lmb", GUIMessages.getString("GUI.LatticeMinerBinaryFormat")); //$NON-NLS-1$ //$NON-NLS-2$
		fileChooser.addChoosableFileFilter(filterBinary);
		ExampleFileFilter filterLM = new ExampleFileFilter(new String[] {
				"lmb", "lmn", "lmv" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				GUIMessages.getString("GUI.LatticeMinerFormats")); //$NON-NLS-1$
		fileChooser.addChoosableFileFilter(filterLM);

		// La boite de dialogue
		int returnVal = fileChooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File contextFile = fileChooser.getSelectedFile();
			openContextFile(contextFile);

			// Sauvegarde le path utilisé
			LMPreferences.setLastDirectory(fileChooser.getCurrentDirectory()
					.getAbsolutePath());
		}
	}

	private void openContextFile(File contextFile) {

		String contextFileAbsolutePath = contextFile.getAbsolutePath();

		// Verifie que le fichier existe
		if (!contextFile.exists()) {
			DialogBox
					.showMessageError(
							this,
							GUIMessages.getString("GUI.fileDoesntExist"), GUIMessages.getString("GUI.errorWhileReadingFile")); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}

		// Verifie que le fichier n'est pas déjà ouvert
		else if (contextFiles.contains(contextFileAbsolutePath)) {
			DialogBox.showMessageInformation(this, GUIMessages
					.getString("GUI.correspondingTabSelected"), //$NON-NLS-1$
					GUIMessages.getString("GUI.contextAlreadyOpen")); //$NON-NLS-1$
			for (ContextTableScrollPane openPane : contextPanes) {
				String paneFile = ((ContextTableModel) openPane
						.getContextTable().getModel()).getContext()
						.getContextFile().getAbsolutePath();
				if (paneFile.equals(contextFileAbsolutePath))
					selectContextAt(contextPanes.indexOf(openPane));
			}
			return;
		}

		try {

			// Recupere l'extension du fichier
			String fileType = ExampleFileFilter.getExtension(contextFile);

			if (fileType.equals("lmb")) { //$NON-NLS-1$
				LMBinaryContextReader contextReader = new LMBinaryContextReader(
						contextFile);
				BinaryContext binCtx = (BinaryContext) contextReader
						.getContext();
				addBinaryContext(binCtx);
				binCtx.setModified(false);
			}

			else if (fileType.equals("cex")) { //$NON-NLS-1$
				CexBinaryContextReader contextReader = new CexBinaryContextReader(
						contextFile);
				BinaryContext binCtx = (BinaryContext) contextReader
						.getContext();
				addBinaryContext(binCtx);
				binCtx.setModified(false);
			}

			else if (fileType.equals("bin.xml")) { //$NON-NLS-1$
				GaliciaXMLBinaryContextReader contextReader = new GaliciaXMLBinaryContextReader(
						contextFile);
				BinaryContext binCtx = (BinaryContext) contextReader
						.getContext();
				addBinaryContext(binCtx);
				binCtx.setModified(false);
			}

			else if (fileType.equals("slf")) { //$NON-NLS-1$
				GaliciaSLFBinaryContextReader contextReader = new GaliciaSLFBinaryContextReader(
						contextFile);
				BinaryContext binCtx = (BinaryContext) contextReader
						.getContext();
				addBinaryContext(binCtx);
				binCtx.setModified(false);
			}

			else if (fileType.equals("lmn")) { //$NON-NLS-1$
				LMNestedContextReader contextReader = new LMNestedContextReader(
						contextFile);
				NestedContext nesCtx = (NestedContext) contextReader
						.getContext();
				addNestedContext(nesCtx);
				nesCtx.setModified(false);
			}

			else if (fileType.equals("lmv")) { //$NON-NLS-1$
				LMValuedContextReader contextReader = new LMValuedContextReader(
						contextFile);
				ValuedContext valCtx = (ValuedContext) contextReader
						.getContext();
				addValuedContext(valCtx);
				valCtx.setModified(false);
			}

			else {
				DialogBox.showMessageError(this, GUIMessages
						.getString("GUI.fileDoesntContainAKnownContextFormat"), //$NON-NLS-1$
						GUIMessages.getString("GUI.wrongContextFormat")); //$NON-NLS-1$
				return;
			}

			contextFiles.add(contextFileAbsolutePath);

		} catch (FileNotFoundException e) {
			DialogBox
					.showMessageError(
							this,
							GUIMessages.getString("GUI.fileCannotBeFound"), GUIMessages.getString("GUI.errorWithFile")); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		} catch (ReaderException e) {
			DialogBox.showMessageError(this, e);
			return;
		}
	}

	private boolean hasSaveCurrentContext() {
		if (contextPanes.size() == 0)
			return false;

		ContextTableScrollPane selectedPane = contextPanes
				.elementAt(currentContextIdx);
		Context currentContext = ((ContextTableModel) selectedPane
				.getContextTable().getModel()).getContext();

		// Sauvegarde pour la première fois
		if (currentContext.getContextFile() == null) {
			return hasSaveCurrentContextAs();
		}
		// Sauvegarde sur un fichier déjà existant
		else {

			File fileName = currentContext.getContextFile();

			// Sauvegarde le context
			saveDependingOnFileType(fileName, currentContext);

			return true;
		}
	}

	private boolean hasSaveCurrentContextAs() {
		if (contextPanes.size() == 0)
			return false;

		ContextTableScrollPane selectedPane = contextPanes
				.elementAt(currentContextIdx);
		Context currentContext = ((ContextTableModel) selectedPane
				.getContextTable().getModel()).getContext();

		JFileChooser fileChooser = new JFileChooser(LMPreferences
				.getLastDirectory());

		// Propriétés du fileChooser
		fileChooser.setApproveButtonText(GUIMessages.getString("GUI.save")); //$NON-NLS-1$
		fileChooser.setDialogTitle(GUIMessages
				.getString("GUI.saveCurrentContext")); //$NON-NLS-1$
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		// Gere les extensions compatibles (lmv, lmn, lmb)
		// et met un nom de fichier par défaut
		if (currentContext instanceof ValuedContext) {
			ExampleFileFilter filterValued = new ExampleFileFilter(
					"lmv", GUIMessages.getString("GUI.LatticeMinerValuedFormat")); //$NON-NLS-1$ //$NON-NLS-2$
			fileChooser.addChoosableFileFilter(filterValued);
			fileChooser
					.setSelectedFile(new File(
							currentContext.getName()
									+ GUIMessages
											.getString("GUI.LatticeMinerValuedFormatDefaultName"))); //$NON-NLS-1$
		} else if (currentContext instanceof NestedContext) {
			ExampleFileFilter filterNested = new ExampleFileFilter(
					"lmn", GUIMessages.getString("GUI.LatticeMinerNestedFormat")); //$NON-NLS-1$ //$NON-NLS-2$
			fileChooser.addChoosableFileFilter(filterNested);
			fileChooser
					.setSelectedFile(new File(
							((NestedContext) currentContext)
									.getNestedContextName()
									+ GUIMessages
											.getString("GUI.LatticeMinerNestedFormatDefaultName"))); //$NON-NLS-1$
		} else {
			ExampleFileFilter filterGaliciaBinSLF = new ExampleFileFilter(
					"slf", GUIMessages.getString("GUI.galiciaSLFBinaryFormat")); //$NON-NLS-1$ //$NON-NLS-2$
			fileChooser.addChoosableFileFilter(filterGaliciaBinSLF);
			ExampleFileFilter filterGaliciaBin = new ExampleFileFilter(
					"bin.xml", GUIMessages.getString("GUI.galiciaXMLBinaryFormat")); //$NON-NLS-1$ //$NON-NLS-2$
			fileChooser.addChoosableFileFilter(filterGaliciaBin);
			ExampleFileFilter filterCex = new ExampleFileFilter(
					"cex", GUIMessages.getString("GUI.conceptExplorerBinaryFormat")); //$NON-NLS-1$ //$NON-NLS-2$
			fileChooser.addChoosableFileFilter(filterCex);
			ExampleFileFilter filterBinary = new ExampleFileFilter(
					"lmb", GUIMessages.getString("GUI.LatticeMinerBinaryFormat")); //$NON-NLS-1$ //$NON-NLS-2$
			fileChooser.addChoosableFileFilter(filterBinary);
			fileChooser
					.setSelectedFile(new File(
							currentContext.getName()
									+ GUIMessages
											.getString("GUI.LatticeMinerBinaryFormatDefaultName"))); //$NON-NLS-1$
		}

		// La boite de dialogue
		int returnVal = fileChooser.showSaveDialog(panel);

		if (returnVal == JFileChooser.APPROVE_OPTION) {

			// Ferme le contexte precédent et ouvre le nouveau
			boolean hasBeenClosed = false;
			if (currentContext.getContextFile() != null) {
				hasBeenClosed = hasClosedCurrentContext(false);
			}

			File fileName = fileChooser.getSelectedFile();

			// Recupere les extensions du filtre
			ExampleFileFilter currentFilter = (ExampleFileFilter) fileChooser
					.getFileFilter();
			ArrayList<String> extensions = currentFilter.getExtensionsList();

			// Recupere l'extension du fichier
			String oldFileType = ExampleFileFilter.getExtension(fileName);
			String newFileType = oldFileType;

			// Compare l'extension du fichier du fichier avec celle du filtre
			if (extensions != null && !extensions.contains(oldFileType)) {
				newFileType = extensions.get(0);

				// Creer le nouveau fichier avec la bonne extension
				String oldFileName = fileName.getAbsolutePath();
				int posOldExt = oldFileName.lastIndexOf("."); //$NON-NLS-1$

				String newFileName = oldFileName + "." + newFileType; //$NON-NLS-1$
				if (posOldExt != -1)
					newFileName = newFileName.substring(0, posOldExt)
							+ "." + newFileType; //$NON-NLS-1$

				fileName = new File(newFileName);
			}

			if (fileName.exists()) {
				int overwrite = DialogBox.showDialogWarning(panel, GUIMessages
						.getString("GUI.doYouWantToOverwriteFile"), //$NON-NLS-1$
						GUIMessages.getString("GUI.selectedFileAlreadyExist")); //$NON-NLS-1$

				if (overwrite == DialogBox.NO) {
					DialogBox
							.showMessageInformation(
									panel,
									GUIMessages
											.getString("GUI.contextHasNotBeenSaved"), GUIMessages.getString("GUI.notSaved")); //$NON-NLS-1$ //$NON-NLS-2$
					return false;
				}
			}

			// Sauvegarde le context
			saveDependingOnFileType(fileName, currentContext);

			// Sauvergarde le path utilisé
			LMPreferences.setLastDirectory(fileChooser.getCurrentDirectory()
					.getAbsolutePath());

			// Si l'ancien contexte a été fermé on ouvre le nouveau
			if (hasBeenClosed) {
				openContextFile(fileName);
			}

			return true;
		} else {
			return false;
		}

	}

	/**
	 * Permet de sauvegarder un contexte dans un fichier selon le type du
	 * fichier
	 * 
	 * @param fileName
	 *            le fichier où l'on souhaite sauvegarder
	 * @param currentContext
	 *            le contexte que l'on souhaite sauvegarder
	 */
	private void saveDependingOnFileType(File fileName, Context currentContext) {

		try {
			// Recupere l'extension du fichier
			String fileType = ExampleFileFilter.getExtension(fileName);

			if (fileType.equals("lmn")) { //$NON-NLS-1$
				new LMNestedContextWriter(fileName,
						(NestedContext) currentContext);
			}

			else if (fileType.equals("lmb")) { //$NON-NLS-1$
				new LMBinaryContextWriter(fileName,
						(BinaryContext) currentContext);
			}

			else if (fileType.equals("lmv")) { //$NON-NLS-1$
				new LMValuedContextWriter(fileName,
						(ValuedContext) currentContext);
			}

			else if (fileType.equals("cex")) { //$NON-NLS-1$
				new CexBinaryContextWriter(fileName,
						(BinaryContext) currentContext);
			}

			else if (fileType.equals("bin.xml")) { //$NON-NLS-1$
				new GaliciaXMLBinaryContextWriter(fileName,
						(BinaryContext) currentContext);
			}

			else if (fileType.equals("slf")) { //$NON-NLS-1$
				new GaliciaSLFBinaryContextWriter(fileName,
						(BinaryContext) currentContext);
			}

			else {
				DialogBox.showMessageError(panel, GUIMessages
						.getString("GUI.fileDoesntContainAKnownContextFormat"), //$NON-NLS-1$
						GUIMessages.getString("GUI.wrongContextFormat")); //$NON-NLS-1$
				return;
			}
			DialogBox
					.showMessageInformation(
							panel,
							GUIMessages
									.getString("GUI.contextHasBeenSuccessfullySaved"), GUIMessages.getString("GUI.saveSuccess")); //$NON-NLS-1$ //$NON-NLS-2$

		} catch (IOException ioe) {
			DialogBox
					.showMessageError(
							panel,
							GUIMessages.getString("GUI.contextCouldnotBeSaved"), GUIMessages.getString("GUI.errorWithFile")); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (WriterException e) {
			DialogBox.showMessageError(panel, e);
		}
	}

	private void saveAllContexts() {
		for (int i = 0; i < contextPanes.size(); i++) {
			selectContextAt(i);
			hasSaveCurrentContext();
		}
	}

	private boolean hasClosedCurrentContext(boolean askForConfirmation) {
		if (contextPanes.size() == 0)
			return true;

		// ATTENTION : Le TabPanel mets a jour le currentContextIdx en
		// selectionnant un nouveau,
		// donc il faut sauvegarder l'index du tab que l'on souhaite supprimer
		int contextIdxToClose = currentContextIdx;

		ContextTableScrollPane selectedPane = contextPanes
				.elementAt(contextIdxToClose);
		Context currentContext = ((ContextTableModel) selectedPane
				.getContextTable().getModel()).getContext();

		if (askForConfirmation) {
			if (currentContext.getContextFile() == null) {
				int answer = DialogBox
						.showDialogWarningCancel(
								panel,
								GUIMessages
										.getString("GUI.doYouWantToSaveNewContext"), //$NON-NLS-1$
								GUIMessages.getString("GUI.context") + " : " + currentContext.getName()); //$NON-NLS-1$ //$NON-NLS-2$

				if (answer == DialogBox.YES) {
					if (!hasSaveCurrentContextAs()) {
						return false;
					}
				} else if (answer == DialogBox.CANCEL) {
					return false;
				}
			}

			else if (currentContext.isModified()) {
				int answer = DialogBox
						.showDialogWarningCancel(
								panel,
								GUIMessages
										.getString("GUI.doYouWantToSaveModifications"), //$NON-NLS-1$
								GUIMessages.getString("GUI.context") + " : " + currentContext.getName()); //$NON-NLS-1$ //$NON-NLS-2$

				if (answer == DialogBox.YES) {
					if (!hasSaveCurrentContext()) {
						return false;
					}
				} else if (answer == DialogBox.CANCEL) {
					return false;
				}
			}
		}

		// Supprime l'onglet
		panelTab.removeTabAt(panelTab.indexOfComponent(selectedPane));

		// Mets a jour par rapport au fichier du contexte
		if (currentContext.getContextFile() != null) {
			String fileAbsolutePath = currentContext.getContextFile()
					.getAbsolutePath();

			// Enlève le fichier des fichiers ouverts
			contextFiles.remove(fileAbsolutePath);

			// Rajoute le fichier à la liste des fichiers récemments ouverts
			addRecentFilePreferences(fileAbsolutePath);
		}

		// Mets à jour la numérotation et les onglets
		if (contextPanes.size() == 1) {
			contextPanes = new Vector<ContextTableScrollPane>();
			currentContextIdx = -1;

			panel.remove(panelTab);

			contextGroup = new ButtonGroup();
			windowMenu.removeAll();
			windowMenu.add(noContextItem);

			currentContextName.setText(GUIMessages
					.getString("GUI.noContextLoaded")); //$NON-NLS-1$
			setActiveActions();
			frame.repaint();
		} else {
			contextPanes.removeElementAt(contextIdxToClose);

			JRadioButtonMenuItem currentCtxBtn = (JRadioButtonMenuItem) windowMenu
					.getMenuComponent(contextIdxToClose);
			windowMenu.remove(currentCtxBtn);
			contextGroup.remove(currentCtxBtn);

			// Selectionne le contexte precedent
			int newIdx = contextIdxToClose - 1 > 0 ? contextIdxToClose - 1 : 0;
			selectContextAt(newIdx);

			// Replace les Windows pour avoir les raccourcis claviers valides
			for (int i = 0; i < windowMenu.getMenuComponentCount(); i++) {
				JRadioButtonMenuItem courant = (JRadioButtonMenuItem) windowMenu
						.getMenuComponent(i);
				courant.setAccelerator(KeyStroke
						.getKeyStroke("ctrl NUMPAD" + (i + 1))); //$NON-NLS-1$
			}
		}

		return true;
	}

	/**
	 * Rajoute un fichier récemment ouvert à le liste dans les préférences
	 * 
	 * @param fileAbsolutePath
	 *            le path absolu du fichier
	 */
	private void addRecentFilePreferences(String fileAbsolutePath) {
		// Recupère les préférences de Lattice Miner
		Preferences preferences = LMPreferences.getPreferences();

		// Verifie que le fichier en cours de fermeture n'est pas déjà le
		// premier de le liste
		JMenuItem firstItem = openRecentContext.getItem(0);
		if (firstItem != null && firstItem.getText().equals(fileAbsolutePath))
			return;

		// Nombre de fichiers recents et rajoute un pour celui-ci
		int nbRecents = preferences.getInt(LMPreferences.NB_RECENTS, 0);
		nbRecents = (nbRecents + 1) % 10;

		String currentRecent = LMPreferences.RECENTS + "/" + nbRecents; //$NON-NLS-1$
		preferences.put(currentRecent, fileAbsolutePath);

		// Rajoute le MenuItem
		JMenuItem recentMenuItem;
		recentMenuItem = new JMenuItem(fileAbsolutePath);
		recentMenuItem.addActionListener(new RecentMenuListener(
				fileAbsolutePath));
		openRecentContext.add(recentMenuItem, 0);

		// Si encore aucun Recent File on active le menu
		if (!openRecentContext.isEnabled())
			openRecentContext.setEnabled(true);

		// Si plus de cinq élément dans le menu, on supprime le dernier
		if (openRecentContext.getMenuComponentCount() > 5)
			openRecentContext.remove(5);

		preferences.putInt(LMPreferences.NB_RECENTS, nbRecents);
	}

	private boolean hasClosedAllContexts() {
		boolean allClosed = true;
		while (allClosed && (contextPanes.size() != 0)) {
			allClosed = allClosed && hasClosedCurrentContext(true);
		}
		return allClosed;
	}

	private void addContextLevelToCurrentContext() {
		if (currentContextIdx < 0)
			return;

		Vector<BinaryContext> contexts = new Vector<BinaryContext>();
		for (int i = 0; i < contextPanes.size(); i++) {
			ContextTableScrollPane scrollPane = contextPanes.elementAt(i);
			Context ctx = ((ContextTableModel) scrollPane.getContextTable()
					.getModel()).getContext();
			if ((ctx instanceof BinaryContext)
					&& !(ctx instanceof NestedContext))
				contexts.add((BinaryContext) ctx);
		}

		ContextTableScrollPane selectedPane = contextPanes
				.elementAt(currentContextIdx);
		NestedContextTable currentTable = (NestedContextTable) selectedPane
				.getContextTable();
		int level = ((NestedContextTableModel) currentTable.getModel())
				.getColumnCount();

		new LevelAdditionAssistant(level + 1, this, contexts);
	}

	private void addEmptyLevelToCurrentContext() {
		if (currentContextIdx < 0)
			return;

		ContextTableScrollPane selectedPane = contextPanes
				.elementAt(currentContextIdx);
		NestedContextTable currentTable = (NestedContextTable) selectedPane
				.getContextTable();
		currentTable.addLevel();
		currentTable.validate();
	}

	private void removeLastLevelFromCurrentContext() {
		if (currentContextIdx < 0)
			return;

		ContextTableScrollPane selectedPane = contextPanes
				.elementAt(currentContextIdx);
		NestedContextTable currentTable = (NestedContextTable) selectedPane
				.getContextTable();
		currentTable.removeLevel();
		currentTable.validate();
	}

	private void orderCurrentContextLevels() {
		if (currentContextIdx < 0)
			return;

		ContextTableScrollPane selectedPane = contextPanes
				.elementAt(currentContextIdx);
		NestedContextTable currentTable = (NestedContextTable) selectedPane
				.getContextTable();
		Context ctx = ((ContextTableModel) currentTable.getModel())
				.getContext();

		if (!(ctx instanceof NestedContext))
			return;
		NestedContext nesCtx = (NestedContext) ctx;

		String[] possibleValues = {
				GUIMessages.getString("GUI.noClassificationLevel"), GUIMessages.getString("GUI.classificationlevelAtFirstLevel"), //$NON-NLS-1$ //$NON-NLS-2$
				GUIMessages.getString("GUI.classificationlevelAtLastlevel") }; //$NON-NLS-1$
		String selectedValue = (String) DialogBox
				.showInputQuestion(
						panel,
						GUIMessages
								.getString("GUI.whatKindOfOrdeningWouldYouLike"), //$NON-NLS-1$
						GUIMessages.getString("GUI.kindOfOrdening"), possibleValues, possibleValues[0]); //$NON-NLS-1$

		RankingAlgo algo = null;
		boolean classificationAtLast = false;
		if (selectedValue.equals(GUIMessages
				.getString("GUI.noClassificationLevel"))) { //$NON-NLS-1$
			algo = new UnsupervisedMultivaluedRankingAlgo(nesCtx);
		}

		else if (selectedValue.equals(GUIMessages
				.getString("GUI.classificationlevelAtFirstLevel"))) { //$NON-NLS-1$
			Vector<BinaryContext> contextList = nesCtx
					.convertToBinaryContextList();

			String[] levelValues = new String[contextList.size()];
			for (int i = 0; i < contextList.size(); i++)
				levelValues[i] = (contextList.elementAt(i)).getName();

			String selectedLevel = (String) DialogBox
					.showInputQuestion(
							panel,
							GUIMessages
									.getString("GUI.whichLevelShouldBeTheClassificationLevel"), GUIMessages.getString("GUI.classificationLevel"), levelValues, //$NON-NLS-1$ //$NON-NLS-2$
							levelValues[0]);

			int levelIdx = -1;
			for (int i = 0; i < contextList.size(); i++) {
				String ctxName = (contextList.elementAt(i)).getName();
				if (selectedLevel.equals(ctxName)) {
					levelIdx = i;
					break;
				}
			}

			algo = new SupervisedMultivaluedRankingAlgo(nesCtx, levelIdx);
		}

		else if (selectedValue.equals(GUIMessages
				.getString("GUI.classificationlevelAtLastlevel"))) { //$NON-NLS-1$
			Vector<BinaryContext> contextList = nesCtx
					.convertToBinaryContextList();

			String[] levelValues = new String[contextList.size()];
			for (int i = 0; i < contextList.size(); i++)
				levelValues[i] = (contextList.elementAt(i)).getName();

			String selectedLevel = (String) DialogBox
					.showInputQuestion(
							panel,
							GUIMessages
									.getString("GUI.whichLevelShouldBeTheClassificationLevel"), GUIMessages.getString("GUI.classificationLevel"), levelValues, //$NON-NLS-1$ //$NON-NLS-2$
							levelValues[0]);

			int levelIdx = -1;
			for (int i = 0; i < contextList.size(); i++) {
				String ctxName = (contextList.elementAt(i)).getName();
				if (selectedLevel.equals(ctxName)) {
					levelIdx = i;
					break;
				}
			}

			algo = new SupervisedMultivaluedRankingAlgo(nesCtx, levelIdx);
			classificationAtLast = true;
		}

		if (algo != null) {
			String[] ordering = algo.getOrdering();
			NestedContext currentContext = nesCtx;

			/* Construit le vecteur donnant l'ordre actuel des contextes */
			Vector<String> currentOrder = new Vector<String>();
			// int idx = 0;
			do {
				currentOrder.add(currentContext.getFirstLevelBinaryContext()
						.getName());
				currentContext = currentContext.getNextContext();
			} while (currentContext != null);

			/* Réordonne les contextes */
			for (int i = 0; i < ordering.length; i++) {
				String currentName = ordering[i];

				/*
				 * Recherche de la position actuelle du prochain contexte à
				 * placer
				 */
				int currentIdx = -1;
				for (int j = 0; j < currentOrder.size(); j++) {
					String ctxName = currentOrder.elementAt(j);
					if (currentName.equals(ctxName)) {
						currentIdx = j;
						break;
					}
				}

				/* Place le contexte à sa position finale */
				currentOrder.removeElementAt(currentIdx);
				currentOrder.insertElementAt(currentName, i);
				nesCtx.moveLevel(currentIdx, i);
			}

			if (classificationAtLast)
				nesCtx.moveLevel(0, currentOrder.size() - 1);

			currentTable.setModelFromContext(ctx);
			currentTable.validate();
		}
	}

	private void addObjectToCurrentContext() {
		if (currentContextIdx < 0)
			return;

		ContextTableScrollPane selectedPane = contextPanes
				.elementAt(currentContextIdx);
		ContextTable currentTable = selectedPane.getContextTable();

		Context currentContext = ((ContextTableModel) selectedPane
				.getContextTable().getModel()).getContext();
		currentContext.addObject();
		currentTable.setModelFromContext(currentContext);
		selectedPane.setRowHeaderView(((ContextTableModel) currentTable
				.getModel()).getRowHeader());
	}

	private void addAttributeToCurrentContext() {
		if (currentContextIdx < 0)
			return;

		ContextTableScrollPane selectedPane = contextPanes
				.elementAt(currentContextIdx);
		ContextTable currentTable = selectedPane.getContextTable();

		Context currentContext = ((ContextTableModel) selectedPane
				.getContextTable().getModel()).getContext();
		currentContext.addAttribute();
		currentTable.setModelFromContext(currentContext);
	}

	private void removeObjectFromCurrentContext() {
		if (currentContextIdx < 0)
			return;

		ContextTableScrollPane selectedPane = contextPanes
				.elementAt(currentContextIdx);
		RemoveObjectsPanel remPanel = new RemoveObjectsPanel(selectedPane, this);
		remPanel.open();
	}

	private void removeAttributeFromCurrentContext() {
		if (currentContextIdx < 0)
			return;

		ContextTableScrollPane selectedPane = contextPanes
				.elementAt(currentContextIdx);
		RemoveAttributesPanel remPanel = new RemoveAttributesPanel(
				selectedPane, this);
		remPanel.open();
	}

	private void mergeAttributesInCurrentContext() {
		if (currentContextIdx < 0)
			return;

		ContextTableScrollPane selectedPane = contextPanes
				.elementAt(currentContextIdx);
		MergeAttributesPanel mergePanel = new MergeAttributesPanel(
				selectedPane, this);
		mergePanel.open();
	}

	private void createLogicalAttributeInCurrentContext() {
		if (currentContextIdx < 0)
			return;

		ContextTableScrollPane selectedPane = contextPanes
				.elementAt(currentContextIdx);
		LogicalAttributePanel logicalPanel = new LogicalAttributePanel(
				selectedPane, this);
		logicalPanel.open();
	}

	private void createObjectClustersInCurrentContext() {
		if (currentContextIdx < 0)
			return;

		ContextTableScrollPane selectedPane = contextPanes
				.elementAt(currentContextIdx);
		ContextTable currentTable = selectedPane.getContextTable();

		BinaryContext currentContext = (BinaryContext) ((ContextTableModel) selectedPane
				.getContextTable().getModel()).getContext();

		currentContext.sortObjectsInClusters();
		currentTable.setModelFromContext(currentContext);
	}

	private void convertCurrentContextToBinaryContext()
			throws AlreadyExistsException, InvalidTypeException {
		if (currentContextIdx < 0)
			return;

		ContextTableScrollPane selectedPane = contextPanes
				.elementAt(currentContextIdx);
		// ContextTable currentTable = selectedPane.getContextTable();

		Context currentContext = ((ContextTableModel) selectedPane
				.getContextTable().getModel()).getContext();

		if (currentContext instanceof NestedContext) {
			String[] possibleValues = {
					GUIMessages.getString("GUI.oneBinaryContextForAllLevels"), GUIMessages.getString("GUI.ABinaryContextForEachLevel") }; //$NON-NLS-1$ //$NON-NLS-2$
			String selectedValue = (String) DialogBox
					.showInputQuestion(
							panel,
							GUIMessages
									.getString("GUI.howManyContextsShouldBeCreated"), //$NON-NLS-1$
							GUIMessages.getString("GUI.numberOfConcepts"), possibleValues, possibleValues[0]); //$NON-NLS-1$

			/* Creation d'un seul contexte binaire */
			if (selectedValue.equals(GUIMessages
					.getString("GUI.oneBinaryContextForAllLevels"))) { //$NON-NLS-1$
				BinaryContext binCtx = ((NestedContext) currentContext)
						.convertToBinaryContext();
				binCtx.setName(((NestedContext) currentContext)
						.getNestedContextName()
						+ GUIMessages.getString("GUI._conv")); //$NON-NLS-1$
				addBinaryContext(binCtx);
			}

			/*
			 * Creation d'un contexte binaire pour chacun des niveaux de la
			 * relation imbriquee
			 */
			else {
				Vector<BinaryContext> contextList = ((NestedContext) currentContext)
						.convertToBinaryContextList();
				for (int i = 0; i < contextList.size(); i++) {
					BinaryContext binCtx = contextList.elementAt(i);
					binCtx.setName(((NestedContext) currentContext)
							.getNestedContextName()
							+ GUIMessages.getString("GUI._level") + (i + 1)); //$NON-NLS-1$
					addBinaryContext(binCtx);
				}
			}
		}

		else if (currentContext instanceof BinaryContext) {
			DialogBox
					.showMessageInformation(
							panel,
							GUIMessages
									.getString("GUI.contextIsAlreadyABinaryContext"), GUIMessages.getString("GUI.noConversionNeeded")); //$NON-NLS-1$ //$NON-NLS-2$
		}

		else if (currentContext instanceof ValuedContext) {
			new ValuedContextConversionAssistant(
					(ValuedContext) currentContext, this);
		}
	}

	private void convertCurrentContextToNestedContext()
			throws AlreadyExistsException, InvalidTypeException {
		if (currentContextIdx < 0)
			return;

		ContextTableScrollPane selectedPane = contextPanes
				.elementAt(currentContextIdx);
		// ContextTable currentTable = selectedPane.getContextTable();

		Context currentContext = ((ContextTableModel) selectedPane
				.getContextTable().getModel()).getContext();

		if (currentContext instanceof BinaryContext) {
			/*
			 * Creation d'un seul contexte à niveaux avec nombre de niveaux non
			 * spécifie
			 */
			NestedContext nestedCtx = ((BinaryContext) currentContext)
					.convertToNestedContext();
			nestedCtx.setNestedContextName(((BinaryContext) currentContext)
					.getName()
					+ GUIMessages.getString("GUI._nested")); //$NON-NLS-1$
			addNestedContext(nestedCtx);
		}

		else {
			DialogBox
					.showMessageWarning(
							panel,
							GUIMessages
									.getString("GUI.OnlyABinaryContextCanBeConvertedToANestedContext"), //$NON-NLS-1$
							GUIMessages.getString("GUI.NoConversionAvailable")); //$NON-NLS-1$
		}
	}

	private void compareAttributesInCurrentContext() {
		if (currentContextIdx < 0)
			return;

		ContextTableScrollPane selectedPane = contextPanes
				.elementAt(currentContextIdx);
		// ContextTable currentTable = selectedPane.getContextTable();

		Context currentContext = ((ContextTableModel) selectedPane
				.getContextTable().getModel()).getContext();

		if (currentContext instanceof BinaryContext) {
			String[] possibleValues = new String[currentContext
					.getAttributeCount()];
			for (int i = 0; i < currentContext.getAttributeCount(); i++)
				possibleValues[i] = currentContext.getAttributeAt(i);

			String firstAttribute = (String) DialogBox
					.showInputQuestion(
							panel,
							GUIMessages.getString("GUI.selectFirstAttribute"), //$NON-NLS-1$
							GUIMessages.getString("GUI.firstAttribute"), possibleValues, possibleValues[0]); //$NON-NLS-1$
			if (firstAttribute == null)
				return;

			String secondAttribute = (String) DialogBox
					.showInputQuestion(
							panel,
							GUIMessages.getString("GUI.selectSecondAttribute"), //$NON-NLS-1$
							GUIMessages.getString("GUI.secondAttribute"), possibleValues, possibleValues[0]); //$NON-NLS-1$
			if (secondAttribute == null)
				return;

			BasicSet differentObjects = currentContext.compareAttributes(
					firstAttribute, secondAttribute);
			if (differentObjects == null) {
				DialogBox.showMessageInformation(panel, GUIMessages
						.getString("GUI.atLeastOneChosenAttributeDoesntExist"), //$NON-NLS-1$
						GUIMessages.getString("GUI.resultOfComparaison")); //$NON-NLS-1$
			} else {
				DialogBox.showMessageInformation(panel, differentObjects
						.toString(), GUIMessages
						.getString("GUI.resultOfComparaison")); //$NON-NLS-1$
			}
		}

		else {
			DialogBox
					.showMessageWarning(
							panel,
							GUIMessages
									.getString("GUI.attributeComparaisonIsAvailableOnlyForBinaryAttributes"), //$NON-NLS-1$
							GUIMessages.getString("GUI.noComparaisonAvailable")); //$NON-NLS-1$
		}
	}

	private void showCurrentLattice() throws AlreadyExistsException,
			InvalidTypeException {
		ContextTableScrollPane selectedPane = contextPanes
				.elementAt(currentContextIdx);
		// ContextTable currentTable = selectedPane.getContextTable();

		Context currentContext = ((ContextTableModel) selectedPane
				.getContextTable().getModel()).getContext();

		if (currentContext instanceof NestedContext) {
			NestedContext context = (NestedContext) currentContext;

			if (context.getLevelCount() > 5) {
				DialogBox.showMessageError(panel, GUIMessages
						.getString("GUI.contextContainsTooManyLevels"), //$NON-NLS-1$
						GUIMessages.getString("GUI.tooManylevels")); //$NON-NLS-1$
				return;
			}

			// final ContextViewer thisViewer = this;
			// Thread waitingThread = new Thread(){
			// public void run(){
			// WaitingFrame wf = new WaitingFrame("Lattice", thisViewer);
			// }
			// };

			Vector<ConceptLattice> lattices = new Vector<ConceptLattice>();
			Vector<LatticeStructure> structures = new Vector<LatticeStructure>();
			int levelIdx = 0;
			while (context != null) {
				levelIdx++;

				ConceptLattice lattice = new ConceptLattice(context);
				if (lattice.size() > 50) {
					// wf.setVisible(false);
					// wf.dispose();
					// waitingThread.stop();
					DialogBox
							.showMessageError(
									panel,
									GUIMessages
											.getString("GUI.theLatticeAtLevel") + " " + levelIdx //$NON-NLS-1$ //$NON-NLS-2$
											+ " " + GUIMessages.getString("GUI.containsTooManyConcepts") + " (" + lattice.size() //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
											+ " " + GUIMessages.getString("GUI.concepts") + ").\n" + GUIMessages.getString("GUI.pleaseRemoveFewAttributes"), GUIMessages.getString("GUI.tooManyConcepts")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					return;
				}

				lattices.add(lattice);

				LatticeStructure structure = new LatticeStructure(lattice,
						context, LatticeStructure.BEST);
				structures.add(structure);

				context = context.getNextContext();
			}

			NestedLattice nestedLattice = new NestedLattice(null, lattices,
					null, ((NestedContext) currentContext)
							.getNestedContextName());
			GraphicalLattice graphLattice = new GraphicalLattice(nestedLattice,
					null, structures);
			LatticeViewer latticeViewer = new LatticeViewer(graphLattice);

			latticeViewer.addWindowListener(this);
			latticeViewer.pack();
			latticeViewer.setVisible(true);

			// waitingThread.stop();
			// wf.setVisible(false);
			// wf.dispose();
		}

		else if (currentContext instanceof BinaryContext) {
			// final ContextViewer thisViewer = this;
			// Thread waitingThread = new Thread(){
			// public void run(){
			// WaitingFrame wf = new WaitingFrame("Lattice", thisViewer);
			// wf.setVisible(true);
			// wf.show();
			// }
			// };
			// WaitingFrame wf = new WaitingFrame("Lattice", this);
			// wf.repaint();

			ConceptLattice lattice = new ConceptLattice(
					(BinaryContext) currentContext);

			if (lattice.size() > 100) {
				// wf.setVisible(false);
				// wf.dispose();
				// waitingThread.stop();
				DialogBox
						.showMessageError(
								panel,
								GUIMessages
										.getString("GUI.theLatticeCreatedContainsTooManyConcepts") + " (" + lattice.size() //$NON-NLS-1$ //$NON-NLS-2$
										+ " " + GUIMessages.getString("GUI.concepts") + ").\n" + GUIMessages.getString("GUI.pleaseRemoveFewAttributes"), GUIMessages.getString("GUI.tooManyConcepts")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				return;
			}

			LatticeStructure struct = new LatticeStructure(lattice,
					(BinaryContext) currentContext, LatticeStructure.BEST);
			GraphicalLattice graphLattice = new GraphicalLattice(lattice,
					struct);

			LatticeViewer latticeViewer = new LatticeViewer(graphLattice);
			latticeViewer.addWindowListener(this);
			latticeViewer.pack();
			latticeViewer.setVisible(true);

			// wf.setVisible(false);
			// wf.dispose();
			// waitingThread.stop();
		}
	}

	private void showCurrentRules() throws AlreadyExistsException,
			InvalidTypeException {
		ContextTableScrollPane selectedPane = contextPanes
				.elementAt(currentContextIdx);
		// ContextTable currentTable = selectedPane.getContextTable();

		Context currentContext = ((ContextTableModel) selectedPane
				.getContextTable().getModel()).getContext();

		if (!(currentContext instanceof BinaryContext)
				&& !(currentContext instanceof NestedContext)) {
			DialogBox
					.showMessageWarning(
							panel,
							GUIMessages
									.getString("GUI.ruleViewerIsAvailableOnlyForBinaryAndNested"), //$NON-NLS-1$
							GUIMessages
									.getString("GUI.ruleViewerIsNotAvailable")); //$NON-NLS-1$
			return;
		}

		BinaryContext binCtx = null;
		// String contextName = null;
		if (currentContext instanceof NestedContext)
			binCtx = ((NestedContext) currentContext).convertToBinaryContext();
		else
			binCtx = (BinaryContext) currentContext;

		ConceptLattice lattice = binCtx.getConceptLattice();
		if (lattice == null)
			lattice = new ConceptLattice(binCtx);

		String suppStr = (String) DialogBox
				.showInputQuestion(
						this,
						GUIMessages.getString("GUI.enterMinimumSupport") + " (%)", //$NON-NLS-1$ //$NON-NLS-2$
						GUIMessages.getString("GUI.minimumSupportForRules"), null, "50"); //$NON-NLS-1$ //$NON-NLS-2$
		if (suppStr == null)
			return;

		double minSupp = -1;
		while (minSupp < 0 || minSupp > 100) {
			try {
				minSupp = Double.parseDouble(suppStr);
			} catch (NumberFormatException ex) {
				DialogBox.showMessageWarning(panel, GUIMessages
						.getString("GUI.valueMustBeBetween0And100"), //$NON-NLS-1$
						GUIMessages.getString("GUI.wrongSupportValue")); //$NON-NLS-1$
				minSupp = -1;
				suppStr = (String) DialogBox
						.showInputQuestion(
								this,
								GUIMessages
										.getString("GUI.enterMinimumSupport") + " (%)", //$NON-NLS-1$ //$NON-NLS-2$
								GUIMessages
										.getString("GUI.minimumSupportForRules"), null, "50"); //$NON-NLS-1$ //$NON-NLS-2$
				if (suppStr == null)
					return;
			}
		}

		String confStr = (String) DialogBox
				.showInputQuestion(
						this,
						GUIMessages.getString("GUI.enterMinimumConfidence") + " (%)", //$NON-NLS-1$ //$NON-NLS-2$
						GUIMessages.getString("GUI.minimumConfidenceForRules"), null, "50"); //$NON-NLS-1$ //$NON-NLS-2$
		if (confStr == null)
			return;

		double minConf = -1;
		while (minConf < 0 || minConf > 100) {
			try {
				minConf = Double.parseDouble(confStr);
			} catch (NumberFormatException ex) {
				DialogBox.showMessageWarning(panel, GUIMessages
						.getString("GUI.valueMustBeBetween0And100"), //$NON-NLS-1$
						GUIMessages.getString("GUI.wrongConfidenceValue")); //$NON-NLS-1$
				minConf = -1;
				confStr = (String) DialogBox
						.showInputQuestion(
								this,
								GUIMessages
										.getString("GUI.enterMinimumConfidence") + " (%)", //$NON-NLS-1$ //$NON-NLS-2$
								GUIMessages
										.getString("GUI.minimumConfidenceForRules"), null, "50"); //$NON-NLS-1$ //$NON-NLS-2$
				if (confStr == null)
					return;
			}
		}

		RuleAlgorithm algo = new InformativeBasisAlgorithm(lattice,
				minSupp / 100.0, minConf / 100.0);
		Vector<Rule> rules = algo.getRules();

		new RuleViewer(rules, binCtx.getName(), minSupp / 100.0,
				minConf / 100.0, null);
	}

	private class ToolBarListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			try {
				if (ae.getSource() == saveBtn) {
					hasSaveCurrentContext();
				}

				else if (ae.getSource() == openBtn) {
					openContext();
				}

				else if (ae.getSource() == newBinCtxBtn) {
					createBinaryContext();
				}

				else if (ae.getSource() == removeCtxBtn) {
					hasClosedCurrentContext(true);
				}

				else if (ae.getSource() == newObjectBtn) {
					addObjectToCurrentContext();
				}

				else if (ae.getSource() == newAttributeBtn) {
					addAttributeToCurrentContext();
				}

				else if (ae.getSource() == delObjectBtn) {
					removeObjectFromCurrentContext();
				}

				else if (ae.getSource() == delAttributeBtn) {
					removeAttributeFromCurrentContext();
				}

				else if (ae.getSource() == showLatBtn) {
					showCurrentLattice();
				}

				else if (ae.getSource() == showRulesBtn) {
					showCurrentRules();
				}
			} catch (LatticeMinerException e) {
				DialogBox.showMessageError(SINGLETON, e);
			}
		}
	}

	private class FrameMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			try {
				if (ae.getSource() == newBinaryContext) {
					createBinaryContext();
				}

				else if (ae.getSource() == newValuedContext) {
					createValuedContext();
				}

				else if (ae.getSource() == newNestedContext) {
					createNestedContext();
				}

				else if (ae.getSource() == openContext) {
					openContext();
				}

				else if (ae.getSource() == saveContext) {
					hasSaveCurrentContext();
				}

				else if (ae.getSource() == saveAsContext) {
					hasSaveCurrentContextAs();
				}

				else if (ae.getSource() == saveAllContexts) {
					saveAllContexts();
				}

				else if (ae.getSource() == closeContext) {
					hasClosedCurrentContext(true);
				}

				else if (ae.getSource() == closeAllContexts) {
					hasClosedAllContexts();
				}

				else if (ae.getSource() == quitViewer) {
					if (hasClosedAllContexts()) {
						frame.setVisible(false);
						frame.dispose();
					}
				}

				else if (ae.getSource() == addEmptyLevel) {
					addEmptyLevelToCurrentContext();
				}

				else if (ae.getSource() == addContextLevel) {
					addContextLevelToCurrentContext();
				}

				else if (ae.getSource() == removeLevel) {
					removeLastLevelFromCurrentContext();
				}

				else if (ae.getSource() == orderLevels) {
					orderCurrentContextLevels();
				}

				else if (ae.getSource() == addObject) {
					addObjectToCurrentContext();
				}

				else if (ae.getSource() == addAttribute) {
					addAttributeToCurrentContext();
				}

				else if (ae.getSource() == removeObject) {
					removeObjectFromCurrentContext();
				}

				else if (ae.getSource() == removeAttribute) {
					removeAttributeFromCurrentContext();
				}

				else if (ae.getSource() == mergeAttributes) {
					mergeAttributesInCurrentContext();
				}

				else if (ae.getSource() == logicalAttribute) {
					createLogicalAttributeInCurrentContext();
				}

				else if (ae.getSource() == compareAttributes) {
					compareAttributesInCurrentContext();
				}

				else if (ae.getSource() == createClusters) {
					createObjectClustersInCurrentContext();
				}

				else if (ae.getSource() == convertToBinary) {
					convertCurrentContextToBinaryContext();
				}

				else if (ae.getSource() == convertToNested) {
					convertCurrentContextToNestedContext();
				}

				else if (ae.getSource() == showLatticeMenu) {
					showCurrentLattice();
				}

				else if (ae.getSource() == showRulesMenu) {
					showCurrentRules();
				} else if (ae.getSource() == about) {
					new AboutScreen();
				}
			} catch (LatticeMinerException e) {
				DialogBox.showMessageError(SINGLETON, e);
			}
		}
	}

	/* Redéfinition adaptée à la fenêtre racine */
	@Override
	public void windowClosing(WindowEvent e) {
		if (e.getSource() == this) {
			if (hasClosedAllContexts())
				System.exit(0);
		} else if (e.getSource() instanceof JFrame) {
			((JFrame) e.getSource()).dispose();
		}
	}

	/**
	 * Implémente l'action a éxécuter lors d'un clic sur un {@link MenuItem} du
	 * menu "Window", à savoir faire basculer de context dans le
	 * {@link ContextViewer}
	 */
	private class ViewTableListener implements ActionListener {
		private ContextTableScrollPane table;

		/**
		 * Constructeur de l'observateur sur un {@link MenuItem} du menu
		 * "Window"
		 * 
		 * @param ctsp
		 *            le panneau qu'il faudra ouvrir lors du clic
		 */
		public ViewTableListener(ContextTableScrollPane ctsp) {
			table = ctsp;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public void actionPerformed(ActionEvent ae) {
			int idx = contextPanes.indexOf(table);
			selectContextAt(idx);
		}
	}

	/**
	 * Implémente l'action a éxécuter lors d'un clic sur un {@link MenuItem} du
	 * menu "OpenRecent", à savoir ouvrir un nouveau context avec le fichier
	 * souhaité
	 * 
	 * @author Ludovic Thomas
	 */
	private class RecentMenuListener implements ActionListener {
		private String fileAbsolutePath;

		/**
		 * Constructeur de l'observateur sur un {@link MenuItem} du menu
		 * "OpenRecent"
		 * 
		 * @param fileAbsolutePath
		 *            le path du fichier qu'il faudra ouvrir lors du clic
		 */
		public RecentMenuListener(String fileAbsolutePath) {
			this.fileAbsolutePath = fileAbsolutePath;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public void actionPerformed(ActionEvent e) {
			openContextFile(new File(fileAbsolutePath));
		}

	}

	/**
	 * Implémente la fenêtre d'information de Lattice Miner
	 */
	public class AboutScreen {

		/**
		 * Constructeur de la fenêtre d'information de Lattice Miner
		 */
		public AboutScreen() {

			String title = GUIMessages.getString("GUI.aboutLatticeMiner"); //$NON-NLS-1$

			String about = GUIMessages.getString("GUI.latticeMinerPlatform") + "\n" + GUIMessages.getString("GUI.releaseLatticeMiner") + " : " + LatticeMiner.LATTICE_MINER_VERSION + " \n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
					+ "\n" //$NON-NLS-1$
					+ GUIMessages.getString("GUI.copyrightLatticeMiner") + " \n" //$NON-NLS-1$ //$NON-NLS-2$
					+ GUIMessages.getString("GUI.companyLatticeMiner") + ": " + GUIMessages.getString("GUI.companyLatticeMiner1") + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					+ GUIMessages.getString("GUI.companyLatticeMiner2") + "\n" //$NON-NLS-1$ //$NON-NLS-2$
					+ GUIMessages.getString("GUI.companyLatticeMiner3") + "\n" + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					+ GUIMessages.getString("GUI.contributorsLatticeMiner") + ": " + GUIMessages.getString("GUI.contributorsLatticeMinerValue") + "\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					+ GUIMessages
							.getString("GUI.principalnitiatorLatticeMiner") + ": " + GUIMessages.getString("GUI.principalnitiatorLatticeMinerValue"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			DialogBox.showMessageInformation(new JFrame(title), about, title);
		}
	}

}