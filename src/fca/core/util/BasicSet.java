package fca.core.util;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * Ensemble de données (attributs ou objets)
 * @author Geneviève Roberge
 * @version 1.0
 */
public class BasicSet extends TreeSet<String> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -206703893373734732L;

	/**
	 * Réalise l'union de deux BasicSet
	 * @param set l'ensemble avec lequel faire l'union
	 * @return l'union des deux BasicSet
	 */
	public BasicSet union(BasicSet set) {
		BasicSet unionSet = (BasicSet) this.clone();
		unionSet.addAll(set);
		return unionSet;
	}
	
	/**
	 * Réalise l'intersection de deux BasicSet
	 * @param set l'ensemble avec lequel faire l'intersection
	 * @return l'intersection des deux BasicSet
	 */
	public BasicSet intersection(BasicSet set) {
		BasicSet intersectionSet = (BasicSet) this.clone();
		intersectionSet.retainAll(set);
		return intersectionSet;
	}
	
	/**
	 * Réalise la difference de deux BasicSet
	 * @param set l'ensemble avec lequel faire la difference
	 * @return la difference des deux BasicSet
	 */
	public BasicSet difference(BasicSet set) {
		BasicSet differenceSet = (BasicSet) this.clone();
		differenceSet.removeAll(set);
		return differenceSet;
	}
	
	/**
	 * Verifie que le BasicSet contient (inclu) le BasicSet demandé
	 * @param set le BasicSet dont on vérifie l'inclusion
	 * @return vrai si le BasicSet paramètre est inclu
	 */
	public boolean isIncluding(BasicSet set) {
		return this.containsAll(set);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractSet#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object setObj) {
		if (!(setObj instanceof BasicSet))
			return false;
		
		BasicSet set = (BasicSet) setObj;
		return (this.containsAll(set) && set.containsAll(this));
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.TreeSet#clone()
	 */
	@Override
	public Object clone() {
		BasicSet newBasicSet = new BasicSet();
		newBasicSet.addAll(this);
		return newBasicSet;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#toString()
	 */
	@Override
	public String toString() {
		String str = "{"; //$NON-NLS-1$
		
		Iterator<String> it = iterator();
		if (it.hasNext())
			str = str + it.next();
		
		while (it.hasNext())
			str = str + ", " + it.next(); //$NON-NLS-1$
		
		str = str + "}"; //$NON-NLS-1$
		return str;
	}
}
