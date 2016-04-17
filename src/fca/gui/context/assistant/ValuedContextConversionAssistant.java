package fca.gui.context.assistant;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import fca.core.context.binary.BinaryContext;
import fca.core.context.valued.ValuedContext;
import fca.core.context.valued.ValuedContextConversionModel;
import fca.gui.context.ContextViewer;
import fca.gui.util.DialogBox;
import fca.messages.GUIMessages;

/**
 * Cette classes se charge de l'interface graphique permettant � l'utilisateur de choisir ses
 * param�tres de conversion pour cr�er un contexte binaire � partir d'un contexte valu�
 * @author Genevi�ve Roberge
 * @version 1.0
 */
public class ValuedContextConversionAssistant extends JFrame implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2987991488001508569L;

	private ContextViewer viewer;
	
	/* Le contexte valu� et son mod�le de conversion */

	private ValuedContext context; //Le contexte valu� � convertir
	
	private ValuedContextConversionModel model; //Le mod�le de conversion cr��
	
	/* Composants graphiques */

	private JFrame frame; //Cette fen�tre
	
	private JPanel panel; //Le panneau de la fen�tre contenant les composants graphiques
	
	private JLabel header; //L'ent�te de la fen�tre
	
	private JRadioButton selection; //Bouton de cr�ation d'un attribut par s�lection de valeurs
	
	private JRadioButton interval; //Bouton de cr�ation d'un attribut par intervalle de valeurs
	private Vector<String> remainingValues; //Liste des valeurs disponibles
	
	private JList remainingList; //Composant graphique contenant la liste des valeurs disponibles
	
	private JPanel selectionPanel; //Panneau pour la cr�ation d'un attribut par s�lection de valeurs
	private Vector<String> selectedValues; //Liste des valeurs choisies
	
	private JList selectedList; //Composant graphique contenant la liste des valeurs choisies
	
	private JPanel intervalPanel; //Panneau pour la cr�ation d'un attribut par intervalle de valeurs
	
	private JTextField startValue; //Borne inf�rieure d'un intervalle
	
	private JTextField endValue; //Borne sup�rieure d'un intervalle
	
	private JTextField attributeName; //Nom donn� au nouvel attribut en cr�ation
	
	private JButton addToList; //Bouton d'ajout d'une valeur dans un nouvel attribut
	
	private JButton removeFromList; //Bouton de suppression d'une valeur dans un nouvel attribut
	
	private JButton addGroup; //Bouton d'ajout d'un nouvel attribut
	private Vector<String> newAttributesList; //Liste des nouveaux attributs cr��s
	
	private JList createdAttributes; //Composant graphique affichant les nouveaux attributs cr��s
	
	private JButton removeAttribute; //Bouton de suppression d'un nouvel attribut
	
	private JButton previousAttribute; //Bouton de navigation vers l'attribut valu� pr�c�dent
	
	private JButton nextAttribute; //Bouton de navigation vers l'attribut valu� suivant
	
	private JButton finish; //Bouton de fin de construction du mod�le de conversion
	
	/* Variables d'�tat */

	int oldAttributesIndex; //Attribut valu� actuellement en traitement
	
	int newAttributeCount; //Nombre de nouveaux attributs binaires pour l'attribut valu� courant
	
	/**
	 * Constructeur
	 * @param c Le ValuedContext � convertir
	 * @param cv Le ContextViewer qui doit �tre rempli
	 */
	public ValuedContextConversionAssistant(ValuedContext c, ContextViewer cv) {
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setDefaultLookAndFeelDecorated(false);
		setTitle(GUIMessages.getString("GUI.valuedRelationToBinaryRelation")); //$NON-NLS-1$
		
		context = c;
		viewer = cv;
		
		model = new ValuedContextConversionModel(context);
		frame = this;
		oldAttributesIndex = 0;
		newAttributeCount = 0;
		
		/*----Composants de la fen�tre----*/
		String oldAttributeName = ""; //$NON-NLS-1$
		oldAttributeName = context.getAttributeAt(0);
		
		/* Ent�te de la fen�tre */
		JPanel headerPanel = new JPanel();
		headerPanel.setPreferredSize(new Dimension(400, 20));
		header = new JLabel(GUIMessages.getString("GUI.attribute")+" : " + oldAttributeName); //$NON-NLS-1$ //$NON-NLS-2$
		headerPanel.add(header);
		
		/* Boutons pour le type de s�paration des valeurs de l'attribut */
		JPanel splitPanel = new JPanel();
		splitPanel.setPreferredSize(new Dimension(400, 50));
		TitledBorder splitTitle = new TitledBorder(BorderFactory.createEtchedBorder(), GUIMessages.getString("GUI.splitAttributeBy")); //$NON-NLS-1$
		splitPanel.setBorder(splitTitle);
		splitPanel.setLayout(new GridBagLayout());
		
		selection = new JRadioButton(GUIMessages.getString("GUI.selectedValues"), true); //$NON-NLS-1$
		selection.addActionListener(this);
		interval = new JRadioButton(GUIMessages.getString("GUI.interval"), false); //$NON-NLS-1$
		interval.addActionListener(this);
		ButtonGroup separationType = new ButtonGroup();
		separationType.add(selection);
		separationType.add(interval);
		
		/* Positionnement des composants graphiques */
		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(5, 5, 5, 5);
		gc.gridy = 0;
		gc.weightx = 1;
		gc.gridwidth = 1;
		gc.gridheight = 1;
		gc.fill = GridBagConstraints.NONE;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.WEST;
		splitPanel.add(selection, gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.WEST;
		splitPanel.add(interval, gc);
		
		/* Panneau pour la cr�ation d'attributs par s�lection de valeurs */
		selectionPanel = new JPanel();
		selectionPanel.setLayout(new GridBagLayout());
		
		TreeSet<String> values = new TreeSet<String>();
		for (int i = 0; i < context.getObjectCount(); i++)
			values.add(context.getValueAt(i, oldAttributesIndex));
		
		Iterator<String> valuesIterator = values.iterator();
		remainingValues = new Vector<String>();
		remainingValues.addAll(values);
		String[] remainingValuesArray = new String[values.size()];
		for (int i = 0; valuesIterator.hasNext(); i++)
			remainingValuesArray[i] = valuesIterator.next();
		
		remainingList = new JList(remainingValues);
		remainingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane remainingScrollPane = new JScrollPane(remainingList, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		remainingScrollPane.setPreferredSize(new Dimension(100, 60));
		
		selectedValues = new Vector<String>();
		String[] selectedValuesArray = new String[0];
		selectedList = new JList(selectedValuesArray);
		selectedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane selectedScrollPane = new JScrollPane(selectedList, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		selectedScrollPane.setPreferredSize(new Dimension(100, 60));
		
		addToList = new JButton(">>"); //$NON-NLS-1$
		addToList.addActionListener(this);
		removeFromList = new JButton("<<"); //$NON-NLS-1$
		removeFromList.addActionListener(this);
		
		/* Positionnement des composants graphiques */
		gc = new GridBagConstraints();
		gc.insets = new Insets(5, 5, 5, 5);
		
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridheight = 2;
		gc.gridwidth = 1;
		gc.weightx = 1;
		gc.weighty = 0;
		gc.anchor = GridBagConstraints.NORTHEAST;
		gc.fill = GridBagConstraints.BOTH;
		selectionPanel.add(remainingScrollPane, gc);
		
		gc.gridx = 1;
		gc.gridy = 0;
		gc.gridheight = 1;
		gc.gridwidth = 1;
		gc.weightx = 0;
		gc.weighty = 1;
		gc.anchor = GridBagConstraints.SOUTH;
		gc.fill = GridBagConstraints.NONE;
		selectionPanel.add(addToList, gc);
		
		gc.gridx = 1;
		gc.gridy = 1;
		gc.gridheight = 1;
		gc.gridwidth = 1;
		gc.weightx = 0;
		gc.weighty = 1;
		gc.anchor = GridBagConstraints.NORTH;
		gc.fill = GridBagConstraints.NONE;
		selectionPanel.add(removeFromList, gc);
		
		gc.gridx = 2;
		gc.gridy = 0;
		gc.gridheight = 2;
		gc.gridwidth = 1;
		gc.weightx = 1;
		gc.weighty = 0;
		gc.anchor = GridBagConstraints.NORTHWEST;
		gc.fill = GridBagConstraints.BOTH;
		selectionPanel.add(selectedScrollPane, gc);
		
		/* Panneau pour la cr�ation d'attributs par intervalles de valeurs */
		intervalPanel = new JPanel();
		intervalPanel.setLayout(new GridBagLayout());
		
		JLabel startLabel = new JLabel(GUIMessages.getString("GUI.startValue")+" : "); //$NON-NLS-1$ //$NON-NLS-2$
		startValue = new JTextField(15);
		
		JLabel endLabel = new JLabel(GUIMessages.getString("GUI.endValue")+" : "); //$NON-NLS-1$ //$NON-NLS-2$
		endValue = new JTextField(15);
		
		/* Positionnement des composants graphiques */
		gc = new GridBagConstraints();
		gc.insets = new Insets(5, 5, 5, 5);
		
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridheight = 1;
		gc.gridwidth = 1;
		gc.weightx = 0;
		gc.weighty = 0;
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.NONE;
		intervalPanel.add(startLabel, gc);
		
		gc.gridx = 1;
		gc.gridy = 0;
		gc.gridheight = 1;
		gc.gridwidth = 1;
		gc.weightx = 0;
		gc.weighty = 0;
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.HORIZONTAL;
		intervalPanel.add(startValue, gc);
		
		gc.gridx = 0;
		gc.gridy = 1;
		gc.gridheight = 1;
		gc.gridwidth = 1;
		gc.weightx = 0;
		gc.weighty = 0;
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.NONE;
		intervalPanel.add(endLabel, gc);
		
		gc.gridx = 1;
		gc.gridy = 1;
		gc.gridheight = 1;
		gc.gridwidth = 1;
		gc.weightx = 0;
		gc.weighty = 0;
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.HORIZONTAL;
		intervalPanel.add(endValue, gc);
		
		/* Panneau g�n�ral pour la cr�ation des nouveaux attributs */
		JPanel attributeCreationPanel = new JPanel();
		TitledBorder attributeCreationTitle = new TitledBorder(BorderFactory.createEtchedBorder(),
				GUIMessages.getString("GUI.attributesCreation")); //$NON-NLS-1$
		attributeCreationPanel.setBorder(attributeCreationTitle);
		attributeCreationPanel.setLayout(new GridBagLayout());
		
		JLabel attributeNameLabel = new JLabel(GUIMessages.getString("GUI.name")+GUIMessages.getString("GUI.15")); //$NON-NLS-1$ //$NON-NLS-2$
		attributeName = new JTextField(oldAttributeName + "_1"); //$NON-NLS-1$
		
		addGroup = new JButton(GUIMessages.getString("GUI.addAttribute")); //$NON-NLS-1$
		addGroup.addActionListener(this);
		
		/*
		 * Si l'attribut valu� ne contient que des valeurs num�riques, la s�paration par intervalles
		 * est affich�e par d�faut
		 */
		if (context.isNumericAttribute(oldAttributesIndex)) {
			selectionPanel.setVisible(false);
			intervalPanel.setVisible(true);
			interval.setSelected(true);
		}

		/*
		 * Si l'attribut valu� ne contient pas que des valeurs num�riques, la s�paration par
		 * intervalles n'est pas disponible
		 */
		else {
			intervalPanel.setVisible(false);
			selectionPanel.setVisible(true);
			selection.setSelected(true);
			interval.setEnabled(false);
		}
		
		/* Positionnement des composants graphiques */
		gc = new GridBagConstraints();
		gc.insets = new Insets(5, 5, 5, 5);
		
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridheight = 1;
		gc.gridwidth = 3;
		gc.weightx = 1;
		gc.weighty = 0;
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.HORIZONTAL;
		attributeCreationPanel.add(selectionPanel, gc);
		
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridheight = 1;
		gc.gridwidth = 3;
		gc.weightx = 1;
		gc.weighty = 0;
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.HORIZONTAL;
		attributeCreationPanel.add(intervalPanel, gc);
		
		gc.gridx = 0;
		gc.gridy = 1;
		gc.gridheight = 1;
		gc.gridwidth = 1;
		gc.weightx = 0;
		gc.weighty = 0;
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.NONE;
		attributeCreationPanel.add(attributeNameLabel, gc);
		
		gc.gridx = 1;
		gc.gridy = 1;
		gc.gridheight = 1;
		gc.gridwidth = 1;
		gc.weightx = 1;
		gc.weighty = 0;
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.HORIZONTAL;
		attributeCreationPanel.add(attributeName, gc);
		
		gc.gridx = 2;
		gc.gridy = 1;
		gc.gridheight = 1;
		gc.gridwidth = 1;
		gc.weightx = 0;
		gc.weighty = 0;
		gc.anchor = GridBagConstraints.NORTHEAST;
		gc.fill = GridBagConstraints.NONE;
		attributeCreationPanel.add(addGroup, gc);
		
		/* Panneau pour la visualisation des nouveaux attributs */
		JPanel newAttributesPanel = new JPanel();
		TitledBorder newAttributesTitle = new TitledBorder(BorderFactory.createEtchedBorder(), GUIMessages.getString("GUI.attributesCreated")); //$NON-NLS-1$
		newAttributesPanel.setBorder(newAttributesTitle);
		newAttributesPanel.setLayout(new GridBagLayout());
		
		newAttributesList = new Vector<String>();
		String[] newAttributesArray = new String[0];
		createdAttributes = new JList(newAttributesArray);
		JScrollPane newAttributesScrollPane = new JScrollPane(createdAttributes,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		newAttributesScrollPane.setPreferredSize(new Dimension(375, 60));
		
		removeAttribute = new JButton(GUIMessages.getString("GUI.removeAttribute")); //$NON-NLS-1$
		removeAttribute.addActionListener(this);
		
		/* Positionnement des composants graphiques */
		gc = new GridBagConstraints();
		gc.insets = new Insets(5, 5, 5, 5);
		
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridheight = 1;
		gc.gridwidth = 1;
		gc.weightx = 1;
		gc.weighty = 0;
		gc.anchor = GridBagConstraints.CENTER;
		gc.fill = GridBagConstraints.BOTH;
		newAttributesPanel.add(newAttributesScrollPane, gc);
		
		gc.gridx = 0;
		gc.gridy = 1;
		gc.gridheight = 1;
		gc.gridwidth = 1;
		gc.weightx = 1;
		gc.weighty = 0;
		gc.anchor = GridBagConstraints.EAST;
		gc.fill = GridBagConstraints.NONE;
		newAttributesPanel.add(removeAttribute, gc);
		
		/* Panneau pour la navigation entre les attributs et la fin de la construction du mod�le */
		JPanel bottomControlsPanel = new JPanel();
		bottomControlsPanel.setPreferredSize(new Dimension(400, 30));
		bottomControlsPanel.setLayout(new GridBagLayout());
		
		previousAttribute = new JButton(GUIMessages.getString("GUI.previous")); //$NON-NLS-1$
		previousAttribute.addActionListener(this);
		previousAttribute.setPreferredSize(new Dimension(90, 25));
		finish = new JButton(GUIMessages.getString("GUI.finishAutoComplete")); //$NON-NLS-1$
		finish.addActionListener(this);
		nextAttribute = new JButton(GUIMessages.getString("GUI.next")); //$NON-NLS-1$
		nextAttribute.addActionListener(this);
		nextAttribute.setPreferredSize(new Dimension(90, 25));
		
		previousAttribute.setEnabled(false);
		if (context.getAttributeCount() > 1)
			nextAttribute.setEnabled(true);
		else
			nextAttribute.setEnabled(false);
		
		/* Positionnement des composants graphiques */
		gc = new GridBagConstraints();
		gc.gridy = 0;
		gc.gridwidth = 1;
		gc.gridheight = 1;
		gc.fill = GridBagConstraints.NONE;
		
		gc.insets = new Insets(2, 5, 2, 2);
		gc.gridx = 0;
		gc.weightx = 0;
		gc.anchor = GridBagConstraints.WEST;
		bottomControlsPanel.add(previousAttribute, gc);
		
		gc.insets = new Insets(2, 2, 2, 2);
		gc.gridx = 1;
		gc.weightx = 1;
		gc.anchor = GridBagConstraints.CENTER;
		bottomControlsPanel.add(finish, gc);
		
		gc.insets = new Insets(2, 2, 2, 5);
		gc.gridx = 2;
		gc.weightx = 0;
		gc.anchor = GridBagConstraints.EAST;
		bottomControlsPanel.add(nextAttribute, gc);
		
		/*----Ajout des composants � la fen�tre----*/
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(headerPanel);
		panel.add(splitPanel);
		panel.add(attributeCreationPanel);
		panel.add(newAttributesPanel);
		panel.add(bottomControlsPanel);
		
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
	}
	
	/**
	 * Remplit le panneau de base avec les informations de l'attribut valu� � la position donn�e,
	 * c'est-�-dire son nom, la liste des valeurs encore disponibles et la liste des attributs
	 * binaires cr��s
	 * @param oldIdx Le int contenant la position de l'attribut valu� recherch�
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	// getNewAttributeValues non type
	public void fillPanelWithAttribute(int oldIdx) {
		oldAttributesIndex = oldIdx;
		
		/* Ajustement de l'ent�te et du nom du prochain attribut */
		String oldAttributeName = ""; //$NON-NLS-1$
		oldAttributeName = context.getAttributeAt(oldAttributesIndex);
		header.setText(GUIMessages.getString("GUI.attribute")+" : " + oldAttributeName); //$NON-NLS-1$ //$NON-NLS-2$
		
		attributeName.setText(oldAttributeName + "_" + (model.getNewAttributesNames(oldIdx).size() + 1)); //$NON-NLS-1$
		newAttributeCount = model.getNewAttributesNames(oldIdx).size();
		
		/* Vide la liste des attributs choisis */
		selectedValues = new Vector<String>();
		String[] selectedValuesArray = new String[0];
		selectedList.setListData(selectedValuesArray);
		
		/* Remplit la liste des attributs disponibles */
		TreeSet<String> values = new TreeSet<String>();
		for (int i = 0; i < context.getObjectCount(); i++)
			values.add(context.getValueAt(i, oldAttributesIndex));
		
		for (int i = 0; i < model.getNewAttributesNames(oldIdx).size(); i++) {
			Vector newAttValues = model.getNewAttributeValues(oldIdx, i);
			for (int j = 0; j < newAttValues.size(); j++) {
				if (values.contains(newAttValues.elementAt(j)))
					values.remove(newAttValues.elementAt(j));
			}
		}
		
		remainingValues = new Vector<String>();
		remainingValues.addAll(values);
		remainingList.setListData(remainingValues);
		
		/* Remplit la liste des attributs cr��s */
		newAttributesList = new Vector<String>();
		String[] newAttributesArray = new String[model.getNewAttributesNames(oldIdx).size()];
		/* Construction des cha�nes repr�sentant les nouveau attributs (nom + description) */
		for (int i = 0; i < model.getNewAttributesNames(oldIdx).size(); i++) {
			String newAtt = model.getNewAttributesNames(oldIdx).elementAt(i) + " : { "; //$NON-NLS-1$
			Vector newAttValues = model.getNewAttributeValues(oldIdx, i);
			if (newAttValues.size() > 0)
				newAtt = newAtt + newAttValues.elementAt(0);
			
			for (int j = 1; j < newAttValues.size(); j++)
				newAtt = newAtt + ", " + (String) newAttValues.elementAt(i); //$NON-NLS-1$
			
			newAtt = newAtt + " }"; //$NON-NLS-1$
			newAttributesList.add(new String(newAtt));
			newAttributesArray[i] = newAtt;
		}
		createdAttributes.setListData(newAttributesArray);
		
		/* Rend visible le bon panneau (intervalle/selection) */
		if (model.getNewAttributesType(oldIdx) == ValuedContextConversionModel.INTERVAL) {
			selectionPanel.setVisible(false);
			intervalPanel.setVisible(true);
			interval.setEnabled(true);
			interval.setSelected(true);
			
			startValue.setText(""); //$NON-NLS-1$
			endValue.setText(""); //$NON-NLS-1$
		}

		else if (model.getNewAttributesType(oldIdx) == ValuedContextConversionModel.EMPTY
				&& context.isNumericAttribute(oldIdx)) {
			selectionPanel.setVisible(false);
			intervalPanel.setVisible(true);
			interval.setEnabled(true);
			interval.setSelected(true);
			
			startValue.setText(""); //$NON-NLS-1$
			endValue.setText(""); //$NON-NLS-1$
		}

		else {
			intervalPanel.setVisible(false);
			if (!context.isNumericAttribute(oldIdx))
				interval.setEnabled(false);
			selectionPanel.setVisible(true);
			selection.setSelected(true);
		}
	}
	
	/* ========== INTERFACE ACTIONLISTENER ========== */
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	// getNewAttributeValues non type
	public void actionPerformed(ActionEvent e) {
		/* Bouton de fin de construction du mod�le de conversion */
		if (e.getSource() == finish) {
			model.completeConversionModel();
			
			String[] possibleValues = { GUIMessages.getString("GUI.oneBinaryContextForAllValuedAttributes"), //$NON-NLS-1$
					GUIMessages.getString("GUI.aBinaryContextForEachValuedAttribute") }; //$NON-NLS-1$
			String selectedValue = (String) DialogBox.showInputQuestion(this, GUIMessages.getString("GUI.howManyRelations"), //$NON-NLS-1$
					GUIMessages.getString("GUI.numberOfRelations"), possibleValues, possibleValues[0]); //$NON-NLS-1$
			
			/* Creation d'un seul contexte binaire */
			if (selectedValue.equals(GUIMessages.getString("GUI.oneBinaryContextForAllValuedAttributes"))) { //$NON-NLS-1$
				BinaryContext binCtx = context.convertToBinaryContext(model);
				binCtx.setName(context.getName() + GUIMessages.getString("GUI._conv")); //$NON-NLS-1$
				viewer.addBinaryContext(binCtx);
			}

			/* Creation d'un contexte binaire pour chacun des attributs du contexte value */
			else {
				Vector<BinaryContext> contexts = context.convertToBinaryContexts(model);
				for (int i = 0; i < contexts.size(); i++) {
					BinaryContext binCtx = contexts.elementAt(i);
					binCtx.setName(context.getAttributeAt(i));
					viewer.addBinaryContext(binCtx);
				}
			}
			
			frame.setVisible(false);
			frame.dispose();
		}

		/* Bouton d'affichage du panneau de cr�ation d'attributs par s�lection de valeurs */
		else if (e.getSource() == selection) {
			int option = DialogBox.OK;
			if (newAttributesList.size() > 0)
				option = DialogBox.showDialogWarning(this, GUIMessages.getString("GUI.allAttributeInformationWillBeLost"), //$NON-NLS-1$
						GUIMessages.getString("GUI.areYouSureToContinue")); //$NON-NLS-1$
			
			if (option == DialogBox.NO)
				interval.setSelected(true);
			
			else {
				intervalPanel.setVisible(false);
				selectionPanel.setVisible(true);
				
				startValue.setText(""); //$NON-NLS-1$
				endValue.setText(""); //$NON-NLS-1$
				
				newAttributesList = new Vector<String>();
				createdAttributes.setListData(newAttributesList);
				
				newAttributeCount = 0;
			}
		}

		/* Bouton d'affichage du panneau de cr�ation d'attributs par intervalle de valeurs */
		else if (e.getSource() == interval) {
			int option = DialogBox.OK;
			if (newAttributesList.size() > 0)
				option = DialogBox.showDialogWarning(this, GUIMessages.getString("GUI.allAttributeInformationWillBeLost"), //$NON-NLS-1$
						GUIMessages.getString("GUI.areYouSureToContinue")); //$NON-NLS-1$
			
			if (option == DialogBox.NO)
				selection.setSelected(true);
			
			else {
				selectionPanel.setVisible(false);
				intervalPanel.setVisible(true);
				
				newAttributesList = new Vector<String>();
				createdAttributes.setListData(newAttributesList);
				
				selectedValues = new Vector<String>();
				selectedList.setListData(selectedValues);
				
				remainingValues = new Vector<String>();
				remainingList.setListData(remainingValues);
				
				newAttributeCount = 0;
			}
		}

		/* Bouton d'ajout d'une valeurs dans l'attribut binaire en cr�ation */
		else if (e.getSource() == addToList) {
			if (remainingList.getSelectedIndex() != -1) {
				int index = remainingList.getSelectedIndex();
				String selectedValue = remainingValues.elementAt(index);
				
				remainingValues.remove(index);
				remainingList.setListData(remainingValues);
				
				selectedValues.add(selectedValue);
				selectedList.setListData(selectedValues);
			}
		}

		/* Bouton de suppression d'une valeurs dans l'attribut binaire en cr�ation */
		else if (e.getSource() == removeFromList) {
			if (selectedList.getSelectedIndex() != -1) {
				int index = selectedList.getSelectedIndex();
				String selectedValue = selectedValues.elementAt(index);
				
				selectedValues.remove(index);
				selectedList.setListData(selectedValues);
				
				remainingValues.add(selectedValue);
				remainingList.setListData(remainingValues);
			}
		}

		/* Bouton d'ajout d'un attribut binaire pour l'attribut valu� courant */
		else if (e.getSource() == addGroup) {
			/* Ajout d'un groupe d'attributs choisis */
			if (selection.isSelected()) {
				if (selectedValues.size() > 0) {
					/* Ajout effecif de la nouvelle s�paration dans le mod�le */
					model.addAttribute(oldAttributesIndex, attributeName.getText(), new Vector<String>(selectedValues));
					
					/*
					 * Construction des cha�nes repr�sentant les nouveau attributs (nom +
					 * description)
					 */
					String newAttribute = attributeName.getText() + " : { " + selectedValues.elementAt(0); //$NON-NLS-1$
					for (int i = 1; i < selectedValues.size(); i++)
						newAttribute = newAttribute + ", " + selectedValues.elementAt(i); //$NON-NLS-1$
					newAttribute = newAttribute + " }"; //$NON-NLS-1$
					newAttributesList.add(new String(newAttribute));
					createdAttributes.setListData(newAttributesList);
					
					selectedValues = new Vector<String>();
					selectedList.setListData(selectedValues);
					
					String oldAttributeName = ""; //$NON-NLS-1$
					oldAttributeName = context.getAttributeAt(oldAttributesIndex);
					attributeName.setText(oldAttributeName + "_" + (++newAttributeCount + 1)); //$NON-NLS-1$
				}

				else
					DialogBox.showMessageError(this, GUIMessages.getString("GUI.noValuedForThisNewAttribute"), //$NON-NLS-1$
							GUIMessages.getString("GUI.problemWithThisAttribute")); //$NON-NLS-1$
			}

			/* Ajout d'un intervalle d'attributs */
			else {
				/* Les 2 valeurs ont �t� remplies */
				if (!startValue.getText().equals("") && !endValue.getText().equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
					double startInt = Double.MIN_VALUE;
					double endInt = Double.MAX_VALUE;
					try {
						startInt = Double.parseDouble(startValue.getText());
						endInt = Double.parseDouble(endValue.getText());
					} catch (NumberFormatException ex) {
						DialogBox.showMessageError(this, GUIMessages.getString("GUI.valuesForIntervalNotNumerical"), //$NON-NLS-1$
								GUIMessages.getString("GUI.notAValidData")); //$NON-NLS-1$
					}
					
					if (startInt != Double.MIN_VALUE && endInt != Double.MAX_VALUE) {
						if (startInt >= endInt)
							DialogBox.showMessageError(this, GUIMessages.getString("GUI.startValueUpperThanEndValue"), //$NON-NLS-1$
									GUIMessages.getString("GUI.notAValidData")); //$NON-NLS-1$
						
						else {
							/* Ajout effecif de la nouvelle s�paration dans le mod�le */
							model.addAttribute(oldAttributesIndex, attributeName.getText(), startInt, endInt);
							
							String newAttribute = attributeName.getText() + " : [ " + startValue.getText() + ", " //$NON-NLS-1$ //$NON-NLS-2$
									+ endValue.getText() + "  ["; //$NON-NLS-1$
							newAttributesList.add(new String(newAttribute));
							createdAttributes.setListData(newAttributesList);
							
							String oldAttributeName = ""; //$NON-NLS-1$
							oldAttributeName = context.getAttributeAt(oldAttributesIndex);
							attributeName.setText(oldAttributeName + "_" + (++newAttributeCount + 1)); //$NON-NLS-1$
							
							startValue.setText(""); //$NON-NLS-1$
							endValue.setText(""); //$NON-NLS-1$
						}
					}
				}

				/* Les 2 valeurs sont vides : intervalle contenant toutes les valeurs */
				else if (startValue.getText().equals("") && endValue.getText().equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
					double startInt = Double.MIN_VALUE;
					double endInt = Double.MAX_VALUE;
					
					/* Ajout effecif de la nouvelle s�paration dans le mod�le */
					model.addAttribute(oldAttributesIndex, attributeName.getText(), startInt, endInt);
					
					String newAttribute = attributeName.getText() + " : [ "+GUIMessages.getString("GUI.minValue")+", "+GUIMessages.getString("GUI.maxValue")+" ]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					newAttributesList.add(new String(newAttribute));
					createdAttributes.setListData(newAttributesList);
					
					String oldAttributeName = ""; //$NON-NLS-1$
					oldAttributeName = context.getAttributeAt(oldAttributesIndex);
					attributeName.setText(oldAttributeName + "_" + (++newAttributeCount + 1)); //$NON-NLS-1$
					
					startValue.setText(""); //$NON-NLS-1$
					endValue.setText(""); //$NON-NLS-1$
				}

				/* Pas de minimum entr� : intervalle commen�ant � la plus petite valeur */
				else if (startValue.getText().equals("") && !endValue.getText().equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
					double startInt = Double.MIN_VALUE;
					double endInt = Double.MAX_VALUE;
					
					try {
						endInt = Double.parseDouble(endValue.getText());
					} catch (NumberFormatException ex) {
						DialogBox.showMessageError(this, GUIMessages.getString("GUI.valuesForIntervalNotNumerical"), //$NON-NLS-1$
								GUIMessages.getString("GUI.notAValidData")); //$NON-NLS-1$
					}
					
					/* Ajout effecif de la nouvelle s�paration dans le mod�le */
					model.addAttribute(oldAttributesIndex, attributeName.getText(), startInt, endInt);
					
					String newAttribute = attributeName.getText() + " : [ "+GUIMessages.getString("GUI.minValue")+", " + endValue.getText() + "  ["; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					newAttributesList.add(new String(newAttribute));
					createdAttributes.setListData(newAttributesList);
					
					String oldAttributeName = ""; //$NON-NLS-1$
					oldAttributeName = context.getAttributeAt(oldAttributesIndex);
					attributeName.setText(oldAttributeName + "_" + (++newAttributeCount + 1)); //$NON-NLS-1$
					
					startValue.setText(""); //$NON-NLS-1$
					endValue.setText(""); //$NON-NLS-1$
				}

				/* Pas de maximum entr� : intervalle terminant � la plus grande valeur */
				else {
					double startInt = Double.MIN_VALUE;
					double endInt = Double.MAX_VALUE;
					
					try {
						startInt = Double.parseDouble(startValue.getText());
					} catch (NumberFormatException ex) {
						DialogBox.showMessageError(this, GUIMessages.getString("GUI.valuesForIntervalNotNumerical"), //$NON-NLS-1$
								GUIMessages.getString("GUI.notAValidData")); //$NON-NLS-1$
					}
					
					/* Ajout effecif de la nouvelle s�paration dans le mod�le */
					model.addAttribute(oldAttributesIndex, attributeName.getText(), startInt, endInt);
					
					String newAttribute = attributeName.getText() + " : [ " + startValue.getText() + ", "+GUIMessages.getString("GUI.maxValue")+" ]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					newAttributesList.add(new String(newAttribute));
					createdAttributes.setListData(newAttributesList);
					
					String oldAttributeName = ""; //$NON-NLS-1$
					oldAttributeName = context.getAttributeAt(oldAttributesIndex);
					attributeName.setText(oldAttributeName + "_" + (++newAttributeCount + 1)); //$NON-NLS-1$
					
					startValue.setText(""); //$NON-NLS-1$
					endValue.setText(""); //$NON-NLS-1$
				}
			}
		}

		/* Bouton de suppression d'un attribut binaire cr�� pour l'attribut valu� courant */
		else if (e.getSource() == removeAttribute) {
			if (newAttributesList.size() > 0) {
				int index = createdAttributes.getSelectedIndex();
				if (index > -1) {
					if (selection.isSelected()) {
						/* Ajout des valeurs lib�r�es dans la liste des valeurs disponibles */
						Vector attValues = model.getNewAttributeValues(oldAttributesIndex, index);
						for (int i = 0; i < attValues.size(); i++)
							remainingValues.add((String) attValues.elementAt(i));
						remainingList.setListData(remainingValues);
					}
					
					/* Suppression de l'attribut binaire de la liste des attributs cr��s */
					newAttributesList.remove(index);
					createdAttributes.setListData(newAttributesList);
					
					/* Enl�ve l'attribut du mod�le de conversion */
					model.removeAttribute(oldAttributesIndex, index);
				}
			}
		}

		/* Bouton de navigation vers l'attribut valu� suivant */
		else if (e.getSource() == nextAttribute) {
			if (oldAttributesIndex + 1 < context.getAttributeCount()) {
				oldAttributesIndex++;
				fillPanelWithAttribute(oldAttributesIndex);
				
				if (oldAttributesIndex < context.getAttributeCount() - 1)
					nextAttribute.setEnabled(true);
				else
					nextAttribute.setEnabled(false);
				
				if (oldAttributesIndex > 0)
					previousAttribute.setEnabled(true);
				else
					previousAttribute.setEnabled(false);
			}
		}

		/* Bouton de navigation vers l'attribut valu� pr�c�dant */
		else if (e.getSource() == previousAttribute) {
			if (oldAttributesIndex > 0) {
				oldAttributesIndex--;
				fillPanelWithAttribute(oldAttributesIndex);
				
				if (oldAttributesIndex < context.getAttributeCount() - 1)
					nextAttribute.setEnabled(true);
				else
					nextAttribute.setEnabled(false);
				
				if (oldAttributesIndex > 0)
					previousAttribute.setEnabled(true);
				else
					previousAttribute.setEnabled(false);
			}
		}
	}
	
}