package fca.core.util;

import java.util.Vector;

import fca.messages.CoreMessages;

public class LogicalFormula {
	/* �tat de l'analyse */
	private static final int STRING_NOT_OPEN = 0;
	private static final int AND_OR_CLOSE = 1;
	
	/* Codes d'erreur */
	private static final int OK = 0;
	private static final int MISS_OPEN = 1;
	private static final int MISS_CLOSE = 2;
	private static final int MISS_OP = 3;
	private static final int MISS_VAL = 4;
	private static final int OPEN_ELEMENT = 5;
	private static final int UNKNOWN_ELEMENT = 6;
	
	String formula = ""; //$NON-NLS-1$
	
	BasicSet validElements;
	//Vector formulaList;
	
	int elementCount = 0;
	
	int state = STRING_NOT_OPEN;
	
	int errorCode = 0;
	
	int errorLength = 0;
	
	int errorPos = -1;
	
	Vector<String> stack;
	Vector<String> opStack;
	Vector<Boolean> elemStack;
	
	public LogicalFormula(String form, BasicSet elements) {
		formula = form;
		validElements = elements;
		parse();
	}
	
	private void parse() {
		stack = new Vector<String>();
		
		int pos = 0;
		errorCode = OK;
		
		while (pos < formula.length() && errorCode == OK) {
			/* Ignore les espace */
			while (formula.charAt(pos) == ' ')
				pos++;
			
			if (state == STRING_NOT_OPEN) {
				if (formula.length() >= pos + 3 && formula.substring(pos, pos + 3).compareToIgnoreCase("AND") == 0) { //$NON-NLS-1$
					errorPos = pos;
					errorLength = 3;
					errorCode = MISS_VAL;
				}

				else if (formula.length() >= pos + 2 && formula.substring(pos, pos + 2).compareToIgnoreCase("OR") == 0) { //$NON-NLS-1$
					errorPos = pos;
					errorLength = 2;
					errorCode = MISS_VAL;
				}

				else if (formula.length() >= pos + 1 && formula.substring(pos, pos + 1).compareTo(")") == 0) { //$NON-NLS-1$
					errorPos = pos;
					errorLength = 1;
					errorCode = MISS_VAL;
				}

				else if (formula.length() >= pos + 3 && formula.substring(pos, pos + 3).compareToIgnoreCase("NOT") == 0) { //$NON-NLS-1$
					state = STRING_NOT_OPEN;
					pos += 3;
				}

				else if (formula.length() >= pos + 1 && formula.substring(pos, pos + 1).compareTo("(") == 0) { //$NON-NLS-1$
					state = STRING_NOT_OPEN;
					stack.add("("); //$NON-NLS-1$
					pos++;
				}

				else if (formula.length() >= pos + 1 && formula.substring(pos, pos + 1).compareTo("[") == 0) { //$NON-NLS-1$
					int startPos = pos + 1;
					pos++;
					while (pos < formula.length() && formula.substring(pos, pos + 1).compareTo("]") != 0) //$NON-NLS-1$
						pos++;
					
					String element = formula.substring(startPos, pos);
					
					if (pos == formula.length()) {
						errorPos = startPos;
						errorLength = pos - startPos;
						errorCode = OPEN_ELEMENT;
					}

					else {
						if (!validElements.contains(element)) {
							errorPos = startPos;
							errorLength = pos - startPos;
							errorCode = UNKNOWN_ELEMENT;
						} else {
							state = AND_OR_CLOSE;
							elementCount++;
							pos++;
						}
					}
				}

				else {
					errorPos = pos;
					errorLength = 1;
					errorCode = MISS_VAL;
				}
			}

			else { /* state = AND_OR_CLOSE */
				if (formula.length() >= pos + 3 && formula.substring(pos, pos + 3).compareToIgnoreCase("AND") == 0) { //$NON-NLS-1$
					state = STRING_NOT_OPEN;
					pos += 3;
				}

				else if (formula.length() >= pos + 2 && formula.substring(pos, pos + 2).compareToIgnoreCase("OR") == 0) { //$NON-NLS-1$
					state = STRING_NOT_OPEN;
					pos += 2;
				}

				else if (formula.length() >= pos + 1 && formula.substring(pos, pos + 1).compareTo(")") == 0) { //$NON-NLS-1$
					if (stack.size() < 1) {
						errorPos = pos;
						errorLength = 1;
						errorCode = MISS_OPEN;
					} else {
						stack.removeElementAt(stack.size() - 1);
						state = AND_OR_CLOSE;
						pos++;
					}
				}

				else if (formula.length() >= pos + 3 && formula.substring(pos, pos + 3).compareToIgnoreCase("NOT") == 0) { //$NON-NLS-1$
					errorPos = pos;
					errorLength = 3;
					errorCode = MISS_OP;
				}

				else if (formula.length() >= pos + 1 && formula.substring(pos, pos + 1).compareTo("(") == 0) { //$NON-NLS-1$
					errorPos = pos;
					errorLength = 1;
					errorCode = MISS_OP;
				}

				else if (formula.length() >= pos + 1 && formula.substring(pos, pos + 1).compareTo("[") == 0) { //$NON-NLS-1$
					int startPos = pos;
					pos++;
					while (pos < formula.length() && formula.substring(pos, pos + 1).compareTo("]") != 0) //$NON-NLS-1$
						pos++;
					
					errorPos = startPos;
					errorLength = pos - startPos + 1;
					errorCode = MISS_OP;
				}

				else {
					errorPos = pos;
					errorLength = 1;
					errorCode = MISS_OP;
				}
			}
			
		}
		
		if (errorCode == OK && stack.size() > 0) {
			errorPos = pos - 1;
			errorLength = 1;
			errorCode = MISS_CLOSE;
		}
	}
	
	public boolean isValid() {
		return errorCode == OK;
	}
	
	/**
	 * @return le code d'erreur
	 */
	public int getErrorCode() {
		return errorCode;
	}
	
	public String getErrorMessage() {
		if (errorCode == OK)
			return CoreMessages.getString("Core.validFormula"); //$NON-NLS-1$
		if (errorCode == MISS_OPEN)
			return CoreMessages.getString("Core.missingParenthesis")+" : ("; //$NON-NLS-1$ //$NON-NLS-2$
		if (errorCode == MISS_CLOSE)
			return CoreMessages.getString("Core.missingParenthesis")+" : )"; //$NON-NLS-1$ //$NON-NLS-2$
		if (errorCode == MISS_OP)
			return CoreMessages.getString("Core.missingOperator")+" : AND, OR"; //$NON-NLS-1$ //$NON-NLS-2$
		if (errorCode == MISS_VAL)
			return CoreMessages.getString("Core.missingValueForOperator")+" : ["+CoreMessages.getString("Core.value")+"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		if (errorCode == OPEN_ELEMENT)
			return CoreMessages.getString("Core.missingDelimiter")+" : ]"; //$NON-NLS-1$ //$NON-NLS-2$
		if (errorCode == UNKNOWN_ELEMENT)
			return CoreMessages.getString("Core.unknowElement"); //$NON-NLS-1$
		return CoreMessages.getString("Core.unknowError"); //$NON-NLS-1$
	}
	
	public int getErrorPosition() {
		return errorPos;
	}
	
	/**
	 * @return la taille de l'erreur
	 */
	public int getErrorLength() {
		return errorLength;
	}
	
	/**
	 * @return le nombre d'elements
	 */
	public int getElementCount() {
		return elementCount;
	}
	
	public boolean accept(BasicSet elements) {
		if (errorCode != OK)
			return false;
		
		opStack = new Vector<String>();
		elemStack = new Vector<Boolean>();
		int pos = 0;
		
		while (pos < formula.length()) {
			/* Ignore les espace */
			while (formula.charAt(pos) == ' ')
				pos++;
			
			/* Parenthese ouvrante */
			if (formula.length() >= pos + 1 && formula.substring(pos, pos + 1).compareTo("(") == 0) { //$NON-NLS-1$
				opStack.add("("); //$NON-NLS-1$
				pos++;
			}

			/* Parenthese fermante */
			else if (formula.length() >= pos + 1 && formula.substring(pos, pos + 1).compareTo(")") == 0) { //$NON-NLS-1$
				while ((opStack.lastElement()).compareTo("(") != 0) //$NON-NLS-1$
					evaluateLastOperator();
				
				opStack.remove(opStack.size() - 1);
				pos++;
			}

			/* Operande */
			else if (formula.length() >= pos + 1 && formula.substring(pos, pos + 1).compareTo("[") == 0) { //$NON-NLS-1$
				pos++;
				int startPos = pos;
				while (formula.substring(pos, pos + 1).compareTo("]") != 0) //$NON-NLS-1$
					pos++;
				
				String element = formula.substring(startPos, pos);
				boolean elementValue = elements.contains(element);
				elemStack.add(new Boolean(elementValue));
				pos++;
			}

			/* Operateur : OR */
			else if (formula.length() >= pos + 2 && formula.substring(pos, pos + 2).compareToIgnoreCase("OR") == 0) { //$NON-NLS-1$
				while (opStack.size() > 0 && (opStack.lastElement()).compareTo("(") != 0 //$NON-NLS-1$
						&& priority("OR") < priority(opStack.lastElement())) //$NON-NLS-1$
					evaluateLastOperator();
				
				opStack.add("OR"); //$NON-NLS-1$
				pos += 2;
			}

			/* Operateur : AND */
			else if (formula.length() >= pos + 3 && formula.substring(pos, pos + 3).compareToIgnoreCase("AND") == 0) { //$NON-NLS-1$
				while (opStack.size() > 0 && (opStack.lastElement()).compareTo("(") != 0 //$NON-NLS-1$
						&& priority("AND") < priority(opStack.lastElement())) //$NON-NLS-1$
					evaluateLastOperator();
				
				opStack.add("AND"); //$NON-NLS-1$
				pos += 3;
			}

			/* Operateur : NOT */
			else if (formula.length() >= pos + 3 && formula.substring(pos, pos + 3).compareToIgnoreCase("NOT") == 0) { //$NON-NLS-1$
				while (opStack.size() > 0 && (opStack.lastElement()).compareTo("(") != 0 //$NON-NLS-1$
						&& priority("NOT") < priority(opStack.lastElement())) //$NON-NLS-1$
					evaluateLastOperator();
				
				opStack.add("NOT"); //$NON-NLS-1$
				pos += 3;
			}
		}
		
		while (opStack.size() > 0)
			evaluateLastOperator();
		
		boolean result = (elemStack.lastElement()).booleanValue();
		
		return result;
	}
	
	private void evaluateLastOperator() {
		String lastOp = opStack.lastElement();
		if (lastOp.compareToIgnoreCase("NOT") == 0) { //$NON-NLS-1$
			boolean value = (elemStack.lastElement()).booleanValue();
			elemStack.remove(elemStack.size() - 1);
			elemStack.add(new Boolean(!value));
		} else if (lastOp.compareToIgnoreCase("AND") == 0) { //$NON-NLS-1$
			boolean value1 = (elemStack.lastElement()).booleanValue();
			elemStack.remove(elemStack.size() - 1);
			boolean value2 = (elemStack.lastElement()).booleanValue();
			elemStack.remove(elemStack.size() - 1);
			elemStack.add(new Boolean(value1 && value2));
		} else if (lastOp.compareToIgnoreCase("OR") == 0) { //$NON-NLS-1$
			boolean value1 = (elemStack.lastElement()).booleanValue();
			elemStack.remove(elemStack.size() - 1);
			boolean value2 = (elemStack.lastElement()).booleanValue();
			elemStack.remove(elemStack.size() - 1);
			elemStack.add(new Boolean(value1 || value2));
		}
		opStack.remove(opStack.size() - 1);
	}
	
	private int priority(String op) {
		if (op.compareToIgnoreCase("OR") == 0) //$NON-NLS-1$
			return 1;
		if (op.compareToIgnoreCase("AND") == 0) //$NON-NLS-1$
			return 2;
		if (op.compareToIgnoreCase("NOT") == 0) //$NON-NLS-1$
			return 3;
		return 4;
	}
}