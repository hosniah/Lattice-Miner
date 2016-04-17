package fca.core.context;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import fca.core.util.BasicSet;
import fca.exception.AlreadyExistsException;
import fca.exception.InvalidTypeException;
import fca.messages.CoreMessages;

/**
 * Classe abstraite représentant un contexte général
 * 
 * @author Geneviève Roberge
 * @version 1.0
 */
public abstract class Context {

	/**
	 * Nom du contexte
	 */
	protected String contextName;

	/** Objets du contexte */
	protected Vector<String> objects;

	/** Attributs du contexte */
	protected Vector<String> attributes;

	/**
	 * Valeurs du contexte
	 */
	protected Vector<Vector<String>> values;

	/**
	 * Fichier associé au contexte. <code>null</code> si pas encore enregistré
	 */
	protected File contextFile;

	/**
	 * Specifie si le contexte a été modifié ou non, en vue d'être sauvegardé
	 */
	protected boolean isModified;

	/**
	 * Type énuméré des headers de context pour la sauvegarde
	 */
	public static enum HeaderType {

		/** L'entête du contexte binaire pour Lattice Miner */
		LM_BINARY("LM_BINARY_CONTEXT"), //$NON-NLS-1$

		/** L'entête du contexte imbrique pour Lattice Miner */
		LM_NESTED("LM_NESTED_CONTEXT"), //$NON-NLS-1$

		/** L'entête du contexte valué pour Lattice Miner */
		LM_VALUED("LM_VALUED_CONTEXT"), //$NON-NLS-1$

		/** L'entête du contexte binaire pour Galicia (Format SLF) */
		SLF_BINARY("[Lattice]"); //$NON-NLS-1$

		private String value;

		/**
		 * Constructeur privé du type enum {@link HeaderType}
		 * 
		 * @param value
		 *            la valeur de l'entête
		 */
		HeaderType(String value) {
			this.value = value;
		}

		/**
		 */
		/**
		 * @return l'entete correspondante
		 */
		public String getValue() {
			return value;
		}

		/**
		 * Methode tostring surchargee
		 * 
		 * @see java.lang.Enum#toString()
		 */
		public String toString() {
			return value;
		}
	};

	/**
	 * Constructeur d'un contexte
	 * 
	 * @param name
	 *            le nom du contexte
	 */
	public Context(String name) {
		if (name != null && name.length() > 0)
			contextName = name;
		else
			contextName = CoreMessages.getString("Core.untitled"); //$NON-NLS-1$

		objects = new Vector<String>();
		attributes = new Vector<String>();
		values = new Vector<Vector<String>>();
		contextFile = null;
		isModified = true;
	}
	
	
	/**
	 * Constructeur d'un contexte
	 * @param name le nom du contexte
	 * @param objNb le nombre d'objet
	 * @param attNb le nombre d'attribut
	 */
	public Context(String name, int objNb, int attNb) {
		if (name != null && name.length() > 0)
			contextName = name;
		else
			contextName = CoreMessages.getString("Core.untitled"); //$NON-NLS-1$

		objects = new Vector<String>();
		for (int i = 0; i < objNb; i++)
			objects.add("" + (i + 1) + ""); //$NON-NLS-1$ //$NON-NLS-2$

		attributes = new Vector<String>();
		for (int i = 0; i < attNb; i++) {
			String attName = ""; //$NON-NLS-1$
			String attLetter = String.valueOf((char) ('a' + (i % 26)));

			int loopCount = (int) Math.floor(i / 26.0);
			for (int j = 0; j <= loopCount; j++)
				attName = attName + attLetter;

			attributes.add(attName);
		}

		values = new Vector<Vector<String>>();
		for (int i = 0; i < objNb; i++) {
			Vector<String> currValues = new Vector<String>();
			for (int j = 0; j < attNb; j++)
				currValues.add(new String("")); //$NON-NLS-1$
			values.add(currValues);
		}

		isModified = true;
	}

	
	/**
	 * Recuperation du nom du contexte
	 * @return le nom du contexte
	 */
	public String getName() {
		return contextName;
	}

	
	/**
	 * Enregistrement du nom du contexte
	 * @param n le nouveau nom
	 */
	public void setName(String n) {
		contextName = n;
		isModified = true;
	}

	
	/**
	 * Recuperation du nombre d'objet
	 * @return le nombre d'objet
	 */
	public int getObjectCount() {
		return objects.size();
	}

	
	/**
	 * Recuperation du nombre d'attribut
	 * @return le nombre d'attribut
	 */
	public int getAttributeCount() {
		return attributes.size();
	}

	
	/**
	 * Ajout d'un objet
	 */
	public void addObject() {
		int objNb = 1;
		String obj = CoreMessages.getString("Core.new_") + objNb; //$NON-NLS-1$

		while (objects.contains(obj)) {
			objNb++;
			obj = CoreMessages.getString("Core.new_") + objNb; //$NON-NLS-1$
		}

		objects.add(obj);
		Vector<String> currValues = new Vector<String>();
		for (int i = 0; i < attributes.size(); i++)
			currValues.add(new String("")); //$NON-NLS-1$
		values.add(currValues);

		isModified = true;
	}

	
	/**
	 * Ajout d'un objet
	 * @param obj l'objet qu'on ajoute
	 * @throws AlreadyExistsException
	 */
	public void addObject(String obj) throws AlreadyExistsException {
		if (!objects.contains(obj)) {
			objects.add(obj);
			Vector<String> currValues = new Vector<String>();
			for (int i = 0; i < attributes.size(); i++)
				currValues.add(new String("")); //$NON-NLS-1$
			values.add(currValues);
			isModified = true;
		} else
			throw new AlreadyExistsException(
					CoreMessages.getString("Core.object") + " " + obj + CoreMessages.getString("Core.alreadyPresent")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	
	/**
	 * Ajout des copies d'un objet
	 * @param origName le nom de l'objet a copier
	 * @param names la base des objets dnas laquelle il y a les copies
	 * @throws AlreadyExistsException
	 */
	public void addObjectCopies(String origName, Vector<String> names)
			throws AlreadyExistsException {
		int objIdx = objects.indexOf(origName);

		if (objIdx >= 0) {
			for (int i = 0; i < names.size(); i++) {
				String name = names.elementAt(i);
				if (!objects.contains(name)) {
					objects.add(name);
					Vector<String> currValues = new Vector<String>();
					currValues.addAll(values.elementAt(objIdx));
					values.add(currValues);
					isModified = true;
				} else
					throw new AlreadyExistsException(
							CoreMessages.getString("Core.object") + " " + name + CoreMessages.getString("Core.alreadyPresent")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
	}

	
	/**
	 * Ajout d'un attribut
	 */
	public void addAttribute() {
		int attNb = 0;
		String att = CoreMessages.getString("Core.new_"); //$NON-NLS-1$
		String attLetter = String.valueOf((char) ('a' + (attNb % 26)));

		int loopCount = (int) Math.floor(attNb / 26.0);
		for (int j = 0; j <= loopCount; j++)
			att = att + attLetter;

		while (attributes.contains(att)) {
			attNb++;
			att = CoreMessages.getString("Core.new_"); //$NON-NLS-1$
			attLetter = String.valueOf((char) ('a' + (attNb % 26)));

			loopCount = (int) Math.floor(attNb / 26.0);
			for (int j = 0; j <= loopCount; j++)
				att = att + attLetter;
		}

		attributes.add(att);
		for (int i = 0; i < objects.size(); i++) {
			Vector<String> currValues = values.elementAt(i);
			currValues.add(new String("")); //$NON-NLS-1$
		}

		isModified = true;
	}

	
	/**
	 * Ajout d'un attribut
	 * @param att l'attribut a ajouter
	 * @throws AlreadyExistsException
	 */
	public void addAttribute(String att) throws AlreadyExistsException {
		if (!attributes.contains(att)) {
			attributes.add(att);
			for (int i = 0; i < objects.size(); i++) {
				Vector<String> currValues = values.elementAt(i);
				currValues.add(new String("")); //$NON-NLS-1$
			}
			isModified = true;
		} else
			throw new AlreadyExistsException(
					CoreMessages.getString("Core.attribute") + " " + att + CoreMessages.getString("Core.alreadyPresent")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	
	/**
	 * Suppression d'un objet
	 * @param obj l'objet a supprimer
	 */
	public void removeObject(String obj) {
		int objIdx = objects.indexOf(obj);

		if (objIdx >= 0) {
			objects.removeElementAt(objIdx);
			values.removeElementAt(objIdx);
			isModified = true;
		}
	}

	
	/**
	 * Suppression d'un attribut
	 * @param att l'attribut a supprimer
	 */
	public void removeAttribute(String att) {
		int attIdx = attributes.indexOf(att);

		if (attIdx >= 0) {
			attributes.removeElementAt(attIdx);

			for (int i = 0; i < values.size(); i++) {
				Vector<String> currValues = values.elementAt(i);
				currValues.removeElementAt(attIdx);
			}
			isModified = true;
		}
	}

	
	/**
	 * Fusion d'attributs
	 * @param attSet l'ensemble d'attribut
	 * @param newName le nom de l'attribut a ajouter
	 * @throws AlreadyExistsException
	 * @throws InvalidTypeException
	 */
	public void mergeAttributes(BasicSet attSet, String newName)
			throws AlreadyExistsException, InvalidTypeException {
		if (attSet.size() < 2)
			return;
		if (getAttributeIndex(newName) >= 0) {
			throw new AlreadyExistsException(
					CoreMessages.getString("Core.alreadyAttributeNamed") + " \"" + newName + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		String currAtt = CoreMessages.getString("Core.noValue"); //$NON-NLS-1$
		String currentValue = CoreMessages.getString("Core.noValue"); //$NON-NLS-1$
		String newValue = CoreMessages.getString("Core.noValue"); //$NON-NLS-1$

		int currAttIdx;
		int newAttIdx;
		int objIdx;

		Iterator<String> objIt = attSet.iterator();
		Iterator<String> attIt = attSet.iterator();

		addAttribute(newName);
		newAttIdx = getAttributeIndex(newName);

		while (attIt.hasNext()) {
			currAtt = attIt.next();
			currAttIdx = getAttributeIndex(currAtt);
			BasicSet objSet = getObjectsFor(currAtt);

			objIt = objSet.iterator();
			while (objIt.hasNext()) {
				objIdx = getObjectIndex(objIt.next());
				currentValue = getValueAt(objIdx, currAttIdx);
				newValue = getValueAt(objIdx, newAttIdx);

				if (newValue.equals("")) { //$NON-NLS-1$
					newValue = currentValue;
					setValueAt(newValue, objIdx, newAttIdx);
				}

				else if (!newValue.equals(currentValue)) {
					newValue = newValue + "&" + currentValue; //$NON-NLS-1$
					setValueAt(newValue, objIdx, newAttIdx);
				}
			}
		}
	}

	
	/**
	 * Verifie si l'objet a ete deplace
	 * @param startIdx indice de depart
	 * @param endIdx indice d'arrivee
	 * @return true si l'objet a ete deplace, false sinon
	 */
	public boolean hasMovedObject(int startIdx, int endIdx) {
		if (startIdx >= objects.size() || startIdx < 0
				|| endIdx >= objects.size() || endIdx < 0 || startIdx == endIdx)
			return false;

		Vector<String> tempValues = values.elementAt(startIdx);
		String objectName = objects.elementAt(startIdx);
		objects.remove(startIdx);
		values.remove(startIdx);
		objects.insertElementAt(objectName, endIdx);
		values.insertElementAt(tempValues, endIdx);
		return true;
	}

	
	/**
	 * Enregistrement d'un objet
	 * @param obj l'objet a enregistrer
	 * @param idx l'indice ou enregistrer l'objet
	 * @throws AlreadyExistsException
	 */
	public void setObjectAt(String obj, int idx) throws AlreadyExistsException {
		if (!objects.contains(obj)) {
			objects.set(idx, obj);
			isModified = true;
		} else
			throw new AlreadyExistsException(CoreMessages
					.getString("Core.alreadyObjectNamed") + " \"" + obj + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	
	/**
	 * Enregistrement d'un attribut
	 * @param att l'attribut a enregistrer
	 * @param idx l'indice ou enregistrer l'attribut
	 * @throws AlreadyExistsException
	 */
	public void setAttributeAt(String att, int idx)
			throws AlreadyExistsException {
		if (!attributes.contains(att)) {
			attributes.set(idx, att);
			isModified = true;
		} else
			throw new AlreadyExistsException(CoreMessages
					.getString("Core.alreadyAttributeNamed") + att + "\""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	
	/**
	 * Enregistrement d'une valeur
	 * @param value la valeur a enregistrer
	 * @param objIdx la position de l'objet
	 * @param attIdx la position de l'attribut
	 * @throws InvalidTypeException
	 */
	public abstract void setValueAt(String value, int objIdx, int attIdx)
			throws InvalidTypeException;

	
	/**
	 * Enregistrement d'une valeur
	 * @param value la valeur a enregistrer
	 * @param obj l'objet
	 * @param att l'attribut
	 * @throws InvalidTypeException
	 */
	public abstract void setValueAt(String value, String obj, String att)
			throws InvalidTypeException;

	
	/**
	 * Recuperation des objets
	 * @return les objets
	 */
	public Vector<String> getObjects() {
		return objects;
	}

	
	/**
	 * Recuperation des attributs
	 * @return les attributs
	 */
	public Vector<String> getAttributes() {
		return attributes;
	}

	
	/**
	 * Recuperation d'un objet a une position donnee
	 * @param idx la position ou recuperer l'objet
	 * @return l'objet
	 */
	public String getObjectAt(int idx) {
		return objects.elementAt(idx);
	}

	
	/**
	 * Recuperation d'un attribut
	 * @param idx indice de l'attribut a recuperer
	 * @return l'attribut
	 */
	public String getAttributeAt(int idx) {
		return attributes.elementAt(idx);
	}

	
	/**
	 * Recuperation d'une valeur
	 * @param objIdx l'indice de l'objet
	 * @param attIdx l'indice de l'attribut
	 * @return la valeur
	 */
	public String getValueAt(int objIdx, int attIdx) {
		return (values.elementAt(objIdx)).elementAt(attIdx);
	}

	
	/**
	 * Recuperer l'indice d'un objet
	 * @param obj l'objet dont on veut l'indice
	 * @return l'indice
	 */
	public int getObjectIndex(String obj) {
		return objects.indexOf(obj);
	}

	
	/**
	 * Recuperer l'attribut d'un objet
	 * @param att l'attribut dont on veut l'indice
	 * @return l'indice
	 */
	public int getAttributeIndex(String att) {
		return attributes.indexOf(att);
	}

	
	/**
	 * Recupere la liste des attributs pour un indice d'objet donne
	 * @param objIdx l'indice de l'objet
	 * @return la liste des attributs
	 */
	public BasicSet getAttributesFor(int objIdx) {
		BasicSet attSet = new BasicSet();
		if (objIdx > -1 && objIdx < objects.size()) {
			Vector<String> objValues = values.elementAt(objIdx);
			for (int i = 0; i < objValues.size(); i++) {
				if (!(objValues.elementAt(i).equals(""))) //$NON-NLS-1$
					attSet.add(attributes.elementAt(i));
			}
		}
		return attSet;
	}

	
	/**
	 * Recupere la liste des attributs pour un objet donne
	 * @param obj l'objet
	 * @return la liste des attributs
	 */
	public BasicSet getAttributesFor(String obj) {
		return getAttributesFor(objects.indexOf(obj));
	}

	
	/**
	 * Recupere la liste des objets ayant un attribut donne
	 * @param att l'attribut
	 * @return la liste des objets
	 */
	public BasicSet getObjectsFor(String att) {
		BasicSet objSet = new BasicSet();

		int attIdx = attributes.indexOf(att);
		if (attIdx > -1) {
			for (int i = 0; i < values.size(); i++) {
				Vector<String> objValues = values.elementAt(i);
				if (!(objValues.elementAt(attIdx)).equals("")) //$NON-NLS-1$
					objSet.add(objects.elementAt(i));
			}
		}
		return objSet;
	}

	
	/**
	 * Fonction qui compare deux attributs et retourne les objets différents entre les deux
	 * @param att1 le premier attribut
	 * @param att2 le second attribut
	 * @return Les objets differents
	 */
	public BasicSet compareAttributes(String att1, String att2) {
		int idx1 = getAttributeIndex(att1);
		int idx2 = getAttributeIndex(att2);

		if (idx1 < 0 || idx2 < 0)
			return null;

		BasicSet differentObjects = new BasicSet();

		for (int i = 0; i < values.size(); i++) {
			Vector<String> objValues = values.elementAt(i);
			if (!(objValues.elementAt(idx1)).equals(objValues.elementAt(idx2)))
				differentObjects.add(new String(objects.elementAt(i)));
		}

		return differentObjects;
	}

	
	/**
	 * Le contexte est il modifie?
	 * @return true si oui false sinon
	 */
	public boolean isModified() {
		return isModified;
	}

	
	/**
	 * Modifie le fait que le context soit modifie
	 * @param m le boolean que precise la modification ou non
	 */
	public void setModified(boolean m) {
		isModified = m;
	}

	/**
	 * @return le fichier associé au context. <code>null</code> s'il n'y en a
	 *         pas
	 */
	public File getContextFile() {
		return contextFile;
	}

	/**
	 * @param fileName
	 */
	public void setContextFile(File fileName) {
		contextFile = fileName;
	}
}