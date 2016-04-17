package fca.core.lattice.operator.search;

import fca.core.lattice.ConceptLattice;
import fca.core.lattice.FormalConcept;
import fca.core.lattice.operator.Operator;
import fca.core.util.BasicSet;

/**
 * Op�rateur de recherche approximative pour l'intention pour un treillis plat
 * @author Ludovic Thomas
 * @version 1.0
 */
public class SearchApproximateIntentSimple extends Operator<ConceptLattice, BasicSet, FormalConcept> {
	
	/**
	 * Constructeur de l'op�rateur de recherche approximative pour l'intention pour un treillis plat
	 * @param data le treillis imbriqu� o� l'on doit faire la recherche
	 */
	public SearchApproximateIntentSimple(ConceptLattice data) {
		super(data, "SearchApproximateIntentSimple"); //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * @see fca.lattice.conceptual.operators.Operator#perform(java.lang.Object)
	 */
	@Override
	public FormalConcept perform(BasicSet entry) {
		return performRec(entry, data.getBottomConcept());
	}
	
	/**
	 * Recherche recursive pour une extension donn�e et un concept
	 * @param intent l'intention recherch�e
	 * @param fc le concept courant dans la recherche recursive
	 * @return le concept trouv�
	 */
	private FormalConcept performRec(BasicSet intent, FormalConcept fc) {
		FormalConcept result = fc;
		for (FormalConcept parent : fc.getParents()) {
			BasicSet intentParent = parent.getIntent();
			if ((intent.size() <= intentParent.size()) && (intentParent.isIncluding(intent))) {
				result = performRec(intent, parent);
			}
		}
		return result;
	}
	
}
