package fca.core.rule;

import java.util.Vector;

import fca.core.lattice.ConceptLattice;
import fca.core.lattice.FormalConcept;
import fca.core.util.BasicSet;

/**
 * Cette classe permet de g�n�rer la base g�n�rique de Pasquier (r�gles exactes) � partir des
 * concepts fr�quents et de ses g�n�rateurs associ�s. Les g�n�rateurs ont �t� pr�alablement
 * construits par l'algorithme JEN. R�gle : g�n�rateur --> ConceptFr�quent \ g�n�rateur confiance =
 * 1.0 support = support(ConceptFrequent)
 * @author Inconnu
 * @version 1.0
 */
public class GenericBasisAlgorithm extends RuleAlgorithm {
	
	/**
	 * Constructeur pour g�n�rer la base g�n�rique de Pasquier
	 * @param lat le treillis graphique o� l'on recherche les r�gles
	 * @param minSupp le support minimum
	 */
	public GenericBasisAlgorithm(ConceptLattice lat, double minSupp) {
		super(lat, minSupp, 1.0);
		lattice.findGenerators();
		run();
	}
	
	/**
	 * Permet d'obtenir la consequence reduite de la regle
	 * @param consequence la consequence de la regle
	 * @param antecedent l'antecedent de la regle
	 * @return la consequence reduite de la regle
	 */
	private BasicSet getReducedConsequence(BasicSet consequence, BasicSet antecedent) {
		BasicSet reducedConsequence = new BasicSet();
		
		/* Parcours de tous les items de la cons�quence non r�duite de la r�gle */
		for (String item : consequence) {
			
			/*
			 * Si l'item n'est pas inclus dans l'ant�c�dent, il fait partie de la cons�quence
			 * r�duite
			 */
			if (!antecedent.contains(item))
				reducedConsequence.add(item);
		}
		
		return reducedConsequence;
	}
	
	/**
	 * G�n�ration de l'ensemble des r�gles g�n�r�es � partir du concept rentr� en param�tre
	 * @param node noeud de base
	 * @param objCount nombre d'objets
	 */
	private void processNode(FormalConcept node, float objCount) {
		if (node.getIntent().size() > 1) {
			// Parcours des g�n�rateurs du concept courant
			Vector<BasicSet> generators = node.getGenerators();
			for (int i = 0; i < generators.size(); i++) {
				BasicSet currGen = generators.elementAt(i);
				double ruleSupport = ((double) node.getExtent().size()) / objCount;
				
				BasicSet potentialCons = getReducedConsequence(node.getIntent(), currGen);
				if ((potentialCons.size() != 0) && (ruleSupport >= minSupport)) {
					Rule newRule = new Rule(currGen, potentialCons, ruleSupport, 1.0,1.0);
					rules.add(newRule);
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see fca.rule.RuleAlgorithm#run()
	 */
	@Override
	public void run() {
		int objCount = lattice.getTopConcept().getExtent().size();
		
		// Parcours de l'ensemble des concepts courants
		Vector<FormalConcept> concepts = lattice.getConcepts();
		for (int i = 0; i < concepts.size(); i++) {
			FormalConcept currConcept = concepts.elementAt(i);
			processNode(currConcept, objCount);
		}
	}
	
}
