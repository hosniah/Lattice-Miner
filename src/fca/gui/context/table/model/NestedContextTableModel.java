package fca.gui.context.table.model;

import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;

import fca.core.context.binary.BinaryContext;
import fca.core.context.nested.NestedContext;
import fca.exception.AlreadyExistsException;
import fca.exception.LMLogger;
import fca.gui.context.table.NestedRowHeaderTable;
import fca.gui.context.table.SubContextTable;
import fca.gui.util.ColorSet;
import fca.messages.GUIMessages;

/**
 * Mod�le de table pour l'�diteur de contexte imbriqu�
 * @author Genevi�ve Roberge
 * @version 1.0
 */
public class NestedContextTableModel extends ContextTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5560714171623605184L;

	Vector<SubContextTable> editors; //Liste des �diteurs contenus dans les colonnes de ce mod�le
	
	NestedContext firstContext; //Contexte imbriqu� contenant tous les sous-contextes
	
	ColorSet colors; //Ensemble de couleurs � associer aux �diteurs
	
	private JTable rowHeader; //Table contenant les ent�tes des colonnes
	
	private JLabel contextLabel; //�tiquette contenant le nom du contexte imbriqu�
	
	/**
	 * Constructeur
	 * @param nesCtx Le NestedContext � partir duquel est construit ce mod�le
	 * @param levels Le int contenant le nombre de colonnes que doit contenir ce mod�le
	 */
	public NestedContextTableModel(NestedContext nesCtx, int levels) {
		super(nesCtx);
		
		firstContext = nesCtx;
		colors = new ColorSet();
		
		firstContext.setName(GUIMessages.getString("GUI.level1")); //$NON-NLS-1$
		SubContextTable firstEd = new SubContextTable(firstContext, colors.getNextColor());
		
		/*
		 * Ajout des relations manquantes � la relation de base pour qu'elle contienne le m�me
		 * nombre de niveaux que l'�diteur et construction de chacun des sous-�diteurs
		 */
		editors = new Vector<SubContextTable>();
		editors.add(firstEd);
		for (int i = 1; i < levels; i++) {
			NestedContext emptyCtx = new NestedContext(new BinaryContext(firstContext.getName(), 0, 0));
			Vector<String> objects = nesCtx.getObjects();
			for (int j = 0; j < objects.size(); j++) {
				String obj = new String(objects.elementAt(j));
				try {
					emptyCtx.addObject(obj);
				} catch (AlreadyExistsException e) {
					// If there, a message has already been show and log
					LMLogger.logWarning(e, false);
				}
			}
			firstContext.addNextContext(emptyCtx);
			emptyCtx.setName(GUIMessages.getString("GUI.level") + (i + 1)); //$NON-NLS-1$
			SubContextTable emptyEd = new SubContextTable(emptyCtx, colors.getNextColor());
			editors.add(emptyEd);
		}
		
		contextLabel = new JLabel(firstContext.getName());
		rowHeader = new NestedRowHeaderTable(new NestedRowHeaderModel(nesCtx));
	}
	
	/**
	 * Constructeur
	 * @param nesCtx Le NestedContext � partir de laquelle est construit ce mod�le qui contiendra le
	 *        m�me nombre de niveau que ce context
	 */
	public NestedContextTableModel(NestedContext nesCtx) {
		super(nesCtx);
		
		firstContext = nesCtx;
		firstContext.setName(GUIMessages.getString("GUI.level1")); //$NON-NLS-1$
		colors = new ColorSet();
		
		SubContextTable firstEd = new SubContextTable(firstContext, colors.getNextColor());
		
		/* Construction de chacun des sous-�diteurs */
		int level = 1;
		editors = new Vector<SubContextTable>();
		editors.add(firstEd);
		NestedContext currentContext = firstContext;
		while (currentContext.getNextContext() != null) {
			currentContext = currentContext.getNextContext();
			currentContext.setName(GUIMessages.getString("GUI.level") + (++level)); //$NON-NLS-1$
			SubContextTable currentEd = new SubContextTable(currentContext, colors.getNextColor());
			editors.add(currentEd);
		}
		
		contextLabel = new JLabel(nesCtx.getNestedContextName());
		rowHeader = new NestedRowHeaderTable(new NestedRowHeaderModel(nesCtx));
	}
	
	/**
	 * Permet d'obtenir le sous-�diteur d'une colonne donn�e
	 * @param colIdx Le int contenant la position de l'�diteur recherch�
	 * @return Le SubContextTable � la position indiqu�e
	 */
	public SubContextTable getEditorAt(int colIdx) {
		if (colIdx < editors.size())
			return editors.elementAt(colIdx);
		else
			return null;
	}
	
	/**
	 * Permet de conna�tre le nom d'une colonne choisie
	 * @param colIdx Le int contenant la position de la colonne recherch�e
	 * @return La String contenant le nom de la colonne indiqu�e
	 */
	@Override
	public String getColumnName(int colIdx) {
		return getEditorAt(colIdx).getBinaryContext().getName();
	}
	
	/**
	 * Permet de conna�tre le nombre de colonnes que contient ce mod�le
	 */
	@Override
	public int getColumnCount() {
		return editors.size();
	}
	
	/**
	 * Permet de conna�tre le nombre de rang�es que contient ce mod�le
	 */
	@Override
	public int getRowCount() {
		/*
		 * Un NestedContextTableModel a toujours une seule rang�e contenant un sous-�diteur pour
		 * chaque niveau
		 */
		return 1;
	}
	
	/**
	 * Ajoute un niveau vide � la suite des autres niveaux
	 */
	public void addLevel() {
		firstContext.setNestedLattice(null);
		
		/*
		 * Construction d'une nouvelle relation sans attributs mais avec les objets de la relation
		 * de base
		 */
		NestedContext emptyCtx = new NestedContext(new BinaryContext(GUIMessages.getString("GUI.newContext"), 0, 0)); //$NON-NLS-1$
		int rowNb = getEditorAt(0).getRowCount();
		for (int i = 0; i < rowNb; i++) {
			String obj = firstContext.getObjectAt(i);
			try {
				emptyCtx.addObject(new String(obj));
			} catch (AlreadyExistsException e) {
				// If there, a message has already been show and log
				LMLogger.logWarning(e, false);
			}
		}
		
		/* Ajout de la nouvelle relation vide et construction de son sous-�diteur */
		emptyCtx.setName(GUIMessages.getString("GUI.level") + (editors.size() + 1)); //$NON-NLS-1$
		firstContext.addNextContext(emptyCtx);
		SubContextTable emptyEd = new SubContextTable(emptyCtx, colors.getNextColor());
		editors.add(emptyEd);
	}
	
	/**
	 * Ajoute un niveau contenant une relation � la suite des autres niveaux
	 * @param binCtx Le BinaryContext qui sera repr�sent�e dans le niveau ajout�
	 */
	public void addLevel(BinaryContext binCtx) {
		NestedContext subCtx = new NestedContext(binCtx);
		subCtx.setName(GUIMessages.getString("GUI.level") + (editors.size() + 1)); //$NON-NLS-1$
		
		/* Ajout de la nouvelle relation et construction de son sous-�diteur */
		firstContext.addNextContext(subCtx);
		SubContextTable subEd = new SubContextTable(subCtx, colors.getNextColor());
		editors.add(subEd);
	}
	
	/**
	 * Supprime le dernier niveau du mod�le, s'il ne contient aucun attribut
	 */
	public void removeLevel() {
		firstContext.setNestedLattice(null);
		
		/* La premi�re relation ne peut pas �tre enlev�e */
		if (editors.size() > 1) {
			SubContextTable lastEditor = editors.elementAt(editors.size() - 1);
			if (lastEditor.getColumnCount() == 0) {
				firstContext.hasRemovedLastContext();
				editors.removeElementAt(editors.size() - 1);
				colors.backOneColor();
			}
		}
	}
	
	/**
	 * D�place une colone du mod�le vers une autre position
	 * @param fromIdx Le int contenant la position de la colonne � d�placer
	 * @param toIdx Le int contenant la position de destination de la colonne � d�plac�e
	 */
	public void moveLevel(int fromIdx, int toIdx) {
		firstContext.setNestedLattice(null);
		firstContext.moveLevel(fromIdx, toIdx);
	}
	
	/**
	 * Ajuste l'ent�te des rang�es
	 * @param header La JTable contenant la nouvelle ent�te pour les rang�es
	 */
	@Override
	public void setRowHeader(JTable header) {
		rowHeader = header;
	}
	
	/**
	 * Permet d'obtenir l'ent�te des rang�e de ce mod�le
	 * @return La JTable contenant l'ent�te des rang�es
	 */
	@Override
	public JTable getRowHeader() {
		return rowHeader;
	}
	
	/**
	 * Ajuste le nom de la relation repr�sent�e, en ajustant l'�tiquette contenant son nom
	 * @param label La String contenant le nouveau nom du contexte
	 */
	public void setContextLabel(String label) {
		contextLabel.setText(label);
		firstContext.setNestedContextName(label);
	}
	
	/**
	 * Permet d'obtenir l'�tiquette contenant le nom de la relation imbriqu�e repr�sent�e dans ce
	 * mod�le
	 * @return Le JLabel contenant le nom du contexte imbriqu�
	 */
	public JLabel getContextLabel() {
		return contextLabel;
	}
	
	/* (non-Javadoc)
	 * @see fca.gui.context.table.model.ContextTableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIdx, int colIdx) {
		if (colIdx < editors.size())
			return editors.elementAt(colIdx);
		else
			return null;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object value, int rowIdx, int colIdx) {
		if ((colIdx < editors.size()) && (value instanceof SubContextTable))
			editors.set(colIdx, (SubContextTable) value);
	}
	
	/**
	 * Permet d'obtenir la relation qui se trouve � un niveau (une colonne) donn�
	 * @param level Le int contenant le niveau du contexte recherch�
	 * @return La NestedContext au niveau indiqu�
	 */
	public NestedContext getNestedContextForLevel(int level) {
		if (firstContext == null)
			return null;
		
		NestedContext currentContext = firstContext;
		for (int i = 2; i < level; i++)
			currentContext = currentContext.getNextContext();
		
		return currentContext;
	}
	
	/* (non-Javadoc)
	 * @see fca.gui.context.table.model.ContextTableModel#getContextName()
	 */
	@Override
	public JButton getContextName() {
		return new JButton(firstContext.getNestedContextName());
	}
	
	/* (non-Javadoc)
	 * @see fca.gui.context.table.model.ContextTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIdx, int colIdx) {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see fca.gui.context.table.model.ContextTableModel#setColumnName(int, java.lang.String)
	 */
	@Override
	public void setColumnName(int colIdx, String name) {
	}
	
	/* (non-Javadoc)
	 * @see fca.gui.context.table.model.ContextTableModel#setRowName(int, java.lang.String)
	 */
	@Override
	public void setRowName(int rowIdx, String name) {
	}
	
	/* (non-Javadoc)
	 * @see fca.gui.context.table.model.ContextTableModel#getRowName(int)
	 */
	@Override
	public String getRowName(int rowIdx) {
		return ""; //$NON-NLS-1$
	}
	
}