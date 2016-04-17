package fca.core.context.binary;

import java.util.Iterator;
import java.util.Vector;

import fca.core.context.Context;
import fca.core.context.nested.NestedContext;
import fca.core.lattice.ConceptLattice;
import fca.core.util.BasicSet;
import fca.core.util.LogicalFormula;
import fca.exception.AlreadyExistsException;
import fca.exception.InvalidTypeException;
import fca.exception.LMLogger;
import fca.messages.CoreMessages;

public class BinaryContext extends Context {
	public static final String TRUE = "true"; //$NON-NLS-1$
	public static final String FALSE = ""; //$NON-NLS-1$
	
	protected ConceptLattice conceptLattice;
	
	protected int countTRUE;
	
	public BinaryContext(String name) {
		super(name);
		conceptLattice = null;
		countTRUE = 0;
	}
	
	public BinaryContext(String name, int objNb, int attNb) {
		super(name, objNb, attNb);
		conceptLattice = null;
	}
	
	@Override
	public void setValueAt(String value, int objIdx, int attIdx) throws InvalidTypeException {
		if (!value.equals(TRUE) && !value.equals(FALSE))
			throw new InvalidTypeException(CoreMessages.getString("Core.valueNotTrueNotFalse")); //$NON-NLS-1$
		else {
			if (!getValueAt(objIdx, attIdx).equals(TRUE) && value.equals(TRUE))
				countTRUE++;
			else if (getValueAt(objIdx, attIdx).equals(TRUE) && !value.equals(TRUE))
				countTRUE--;
			
			(values.elementAt(objIdx)).set(attIdx, value);
			isModified = true;
		}
	}
	
	@Override
	public void setValueAt(String value, String obj, String att) throws InvalidTypeException {
		int objIdx = objects.indexOf(obj);
		int attIdx = attributes.indexOf(att);
		
		if (objIdx > -1 && attIdx > -1) {
			if (!value.equals(TRUE) && !value.equals(FALSE))
				throw new InvalidTypeException(CoreMessages.getString("Core.valueNotTrueNotFalse")); //$NON-NLS-1$
			else {
				
				if (!getValueAt(objIdx, attIdx).equals(TRUE) && value.equals(TRUE))
					countTRUE++;
				else if (getValueAt(objIdx, attIdx).equals(TRUE) && !value.equals(TRUE))
					countTRUE--;
				
				(values.elementAt(objIdx)).set(attIdx, value);
				isModified = true;
			}
		} else
			throw new InvalidTypeException(CoreMessages.getString("Core.invalidContextCell") + " (" + CoreMessages.getString("Core.object") + " :" + obj + ", " + CoreMessages.getString("Core.attribute") + " :" + att + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
	}
	
	public void createLogicalAttribute(LogicalFormula formula, String newName) throws AlreadyExistsException, InvalidTypeException {
		if (formula.getElementCount() < 2)
			return;
		if (getAttributeIndex(newName) >= 0) {
			throw new AlreadyExistsException(CoreMessages.getString("Core.alreadyAttributeNamed") + " \"" + newName + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		
		addAttribute(newName);
		for (int i = 0; i < objects.size(); i++) {
			String obj = objects.elementAt(i);
			if (formula.accept(getAttributesFor(obj))) {
				setValueAt(TRUE, obj, newName);
				countTRUE++;
			}
		}
	}
	
	public void sortObjectsInClusters() {
		createClusters(0, objects.size() - 1, 0);
	}
	
	private void createClusters(int firstObjIdx, int lastObjIdx, int attIdx) {
		if (attIdx >= attributes.size())
			return;
		if (firstObjIdx >= lastObjIdx)
			return;
		
		int lastTrue = firstObjIdx - 1;
		for (int i = firstObjIdx; i <= lastObjIdx; i++)
			if (getValueAt(i, attIdx).equals(TRUE)) {
				lastTrue++;
				if (lastTrue != i)
					hasMovedObject(i, lastTrue);
			}
		
		createClusters(firstObjIdx, lastTrue, attIdx + 1);
		createClusters(lastTrue + 1, lastObjIdx, attIdx + 1);
	}
	
	public void addBinaryContext(BinaryContext bc, boolean overwrite) throws AlreadyExistsException, InvalidTypeException {
		/* Ajout des nouveaux objest */
		Vector<String> newObjects = bc.getObjects();
		for (int i = 0; i < newObjects.size(); i++) {
			String objName = newObjects.elementAt(i);
			try {
				addObject(objName);
			} catch (AlreadyExistsException e) {
				// Nothing to do there, already tested
				LMLogger.logWarning(e, false);
			}
		}
		isModified = true;
		
		/* Ajout des nouveaux attributs */
		Vector<String> newAttributes = bc.getAttributes();
		for (int i = 0; i < newAttributes.size(); i++) {
			String attName = newAttributes.elementAt(i);
			
			if (!overwrite) {
				int tryCount = 0;
				int idx = getAttributeIndex(attName);
				while (idx > -1) {
					tryCount++;
					idx = getAttributeIndex(attName + "_" + tryCount); //$NON-NLS-1$
				}
				
				if (tryCount > 0) {
					attName = attName + "_" + tryCount; //$NON-NLS-1$
					newAttributes.setElementAt(attName, i);
				}
			}
			
			addAttribute(attName);
		}
		
		/* Ajout des valeurs */
		for (int i = 0; i < newObjects.size(); i++) {
			String objName = newObjects.elementAt(i);
			for (int j = 0; j < newAttributes.size(); j++) {
				String attName = newAttributes.elementAt(j);
				String value = bc.getValueAt(i, j);
				if (value.equals(TRUE)) {
					int objIdx = getObjectIndex(objName);
					int attIdx = getAttributeIndex(attName);
					String oldValue = getValueAt(objIdx, attIdx);
					setValueAt(TRUE, objIdx, attIdx);
					if (!oldValue.equals(TRUE) && value.equals(TRUE))
						countTRUE++;
					else if (oldValue.equals(TRUE) && !value.equals(TRUE))
						countTRUE--;
				}
			}
		}
	}
	
	public NestedContext convertToNestedContext() throws AlreadyExistsException, InvalidTypeException {
		Decomposition dec = new Decomposition(this);
		Vector<BasicSet> classes = dec.getClasses();
		dec = null;
		
		NestedContext nestedCtx = null;
		for (int i = 0; i < classes.size(); i++) {
			BinaryContext ctx = new BinaryContext(CoreMessages.getString("Core.level") + " " + (i + 1)); //$NON-NLS-1$ //$NON-NLS-2$
			
			for (int j = 0; j < objects.size(); j++)
				ctx.addObject(getObjectAt(j));
			
			Iterator<String> attIt = (classes.elementAt(i)).iterator();
			while (attIt.hasNext()) {
				String att = attIt.next();
				ctx.addAttribute(att);
				Iterator<String> objIt = getObjectsFor(att).iterator();
				while (objIt.hasNext()) {
					ctx.setValueAt(TRUE, objIt.next(), att);
				}
			}
			
			if (nestedCtx == null)
				nestedCtx = new NestedContext(ctx);
			else
				nestedCtx.addNextContext(new NestedContext(ctx));
		}
		
		return nestedCtx;
	}
	
	/**
	 * @param cl
	 */
	public void setConceptLattice(ConceptLattice cl) {
		conceptLattice = cl;
	}
	
	/**
	 * @return le {@link ConceptLattice} du contexte binaire
	 */
	public ConceptLattice getConceptLattice() {
		return conceptLattice;
	}
	
	public double getDensity() {
		return ((double) countTRUE / (objects.size() * attributes.size()));
	}
	
	@Override
	public Object clone() {
		BinaryContext newBinCtx = new BinaryContext(contextName);
		for (int i = 0; i < objects.size(); i++) {
			try {
				newBinCtx.addObject(objects.elementAt(i));
			} catch (AlreadyExistsException aee) {
				// Never reach because the original BinaryContext is valid
				LMLogger.logSevere(aee, false);
			}
		}
		
		for (int i = 0; i < attributes.size(); i++) {
			try {
				newBinCtx.addAttribute(attributes.elementAt(i));
			} catch (AlreadyExistsException aee) {
				// Never reach because the original BinaryContext is valid
				LMLogger.logSevere(aee, false);
			}
		}
		
		for (int i = 0; i < values.size(); i++) {
			Vector<String> currentValues = values.elementAt(i);
			for (int j = 0; j < currentValues.size(); j++) {
				String val = currentValues.elementAt(j);
				if (val.equals(BinaryContext.TRUE)) {
					try {
						newBinCtx.setValueAt(BinaryContext.TRUE, i, j);
					} catch (InvalidTypeException ite) {
						// Never reach because the original BinaryContext is valid
						LMLogger.logSevere(ite, false);
					}
				}
			}
		}
		
		return newBinCtx;
	}
	
	@Override
	public String toString() {
		BasicSet objSet = new BasicSet();
		objSet.addAll(objects);
		BasicSet attSet = new BasicSet();
		attSet.addAll(attributes);
		
		String str = CoreMessages.getString("Core.contextName") + " : " + contextName + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		str = str + CoreMessages.getString("Core.objects") + " : " + objSet.toString() + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		str = str + CoreMessages.getString("Core.attributes") + " : " + attSet.toString() + "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		for (int i = 0; i < objects.size(); i++) {
			for (int j = 0; j < attributes.size(); j++) {
				if (getValueAt(i, j).equals(BinaryContext.TRUE))
					str = str + "X "; //$NON-NLS-1$
				else
					str = str + "0 "; //$NON-NLS-1$
			}
			str = str + "\n"; //$NON-NLS-1$
		}
		
		return str;
	}
}