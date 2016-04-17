package fca.core.rule;

import java.util.Vector;

import fca.core.lattice.ConceptLattice;
import fca.core.lattice.FormalConcept;
import fca.core.util.BasicSet;

/**
 * Cette classe permet de g�n�rer la base g�n�rique Informative
 * @author Inconnu
 * @version 1.0
 */
public class InformativeBasisAlgorithm extends RuleAlgorithm {
	
	/**
	 * Constructeur pour g�n�rer la base g�n�rique Informative
	 * @param lat le treillis graphique o� l'on recherche les r�gles
	 * @param minSupp le support minimum
	 * @param minConf la confiance minimum
	 */
	public InformativeBasisAlgorithm(ConceptLattice lat, double minSupp, double minConf) {
		super(lat, minSupp, minConf);
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
	private void processNode(FormalConcept node, float objCount) { //generationRegleNoeud
		float antSupport = (node.getExtent().size()) / objCount;
		// Parcours des g�n�rateurs du concept courant
		Vector<BasicSet> generators = node.getGenerators();
		for (int i = 0; i < generators.size(); i++) {
			// S�lection du g�n�rateur courant
			BasicSet currGen = generators.elementAt(i);
			Vector<FormalConcept> children = node.getChildren();
			
			if ((children.size() != 0) && (node.getIntent().size() != 0)) {
				// Parcours des concepts enfants du concept courant
				for (int j = 0; j < children.size(); j++) {
					FormalConcept child = children.elementAt(j);
					float consSupport = (child.getExtent().size()) / objCount;
					
					BasicSet potentialCons = getReducedConsequence(child.getIntent(), node.getIntent());
					double ruleConf = consSupport / antSupport;
					
					// Si la confiance est >= � la confiance minimale, la r�gle est conserv�e
					if ((potentialCons.size() != 0) && (ruleConf >= minConfidence) && (consSupport >= minSupport)) {
						Rule newRule = new Rule(currGen, potentialCons, consSupport, ruleConf,1.0);
						rules.add(newRule);
					}
				}
			}
			double ruleSupport = ((double) node.getExtent().size()) / objCount;
			
			BasicSet potentialCons = getReducedConsequence(node.getIntent(), currGen);
			if ((potentialCons.size() != 0) && (ruleSupport >= minSupport)) {
				Rule newRule = new Rule(currGen, potentialCons, ruleSupport, 1.0,1.0);
				rules.add(newRule);
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
