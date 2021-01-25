// Evan McCauley

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class Calculator extends JFrame implements ActionListener, MouseListener{
	// internal parameters relating to the calculator model but not specifically display
	int currentBase;
	long currentValue;
	Stack<Long> valueStack;
	Stack<String> opStack;
	int leftParenCount;
	boolean parens;
	boolean overwriteCurrent;
	
	// UI elements that are active
	JLabel expression;
	JLabel primaryField;
	JLabel hexLabel;
	JLabel decLabel;
	JLabel octLabel;
	JLabel binLabel;
	JLabel hexField;
	JLabel decField;
	JLabel octField;
	JLabel binField;
	JButton lsh, rsh;
	CalcValueButton[] valueButtons; // holds the value buttons (0-F) in order
	CalcFunctionButton[] functionButtons; // holds the function buttons: multiply, QWORD (and variants), clear, etc; all identified by their text fields
	
	// for button color
	static final Color VL_GRAY = new Color(224, 224, 224);
	
	// called to set current value and all fields
	void setCurrentValue(long value) {
		// set currentValue
		currentValue = value;
		
		// set fields
		if(functionButtons[0].getText().equalsIgnoreCase("QWORD")) {
			if(currentBase == 16) {primaryField.setText(Long.toHexString(value));}
			else if(currentBase == 10) {primaryField.setText(Long.toString(value));}
			else if(currentBase == 8) {primaryField.setText(Long.toOctalString(value));}
			else {primaryField.setText(Long.toBinaryString(value));}
			hexField.setText(Long.toHexString(value));
			decField.setText(Long.toString(value));
			octField.setText(Long.toOctalString(value));
			binField.setText(Long.toBinaryString(value));
		}
		else if(functionButtons[0].getText().equalsIgnoreCase("DWORD")) {
			int intValue = (int) value;
			if(currentBase == 16) {primaryField.setText(Integer.toHexString(intValue));}
			else if(currentBase == 10) {primaryField.setText(Integer.toString(intValue));}
			else if(currentBase == 8) {primaryField.setText(Integer.toOctalString(intValue));}
			else {primaryField.setText(Integer.toBinaryString(intValue));}
			hexField.setText(Integer.toHexString(intValue));
			decField.setText(Integer.toString(intValue));
			octField.setText(Integer.toOctalString(intValue));
			binField.setText(Integer.toBinaryString(intValue));
		}
		else if(functionButtons[0].getText().equalsIgnoreCase("WORD")) {
			short shortValue = (short) value;
			if(currentBase == 16) {primaryField.setText(Integer.toHexString(Short.toUnsignedInt(shortValue)));}
			else if(currentBase == 10) {primaryField.setText(Integer.toString(shortValue));}
			else if(currentBase == 8) {primaryField.setText(Integer.toOctalString(Short.toUnsignedInt(shortValue)));}
			else {primaryField.setText(Integer.toBinaryString(Short.toUnsignedInt(shortValue)));}
			hexField.setText(Integer.toHexString(Short.toUnsignedInt(shortValue)));
			decField.setText(Integer.toString(shortValue));
			octField.setText(Integer.toOctalString(Short.toUnsignedInt(shortValue)));
			binField.setText(Integer.toBinaryString(Short.toUnsignedInt(shortValue)));
		}
		else {
			byte byteValue = (byte) value;
			if(currentBase == 16) {primaryField.setText(Integer.toHexString(Byte.toUnsignedInt(byteValue)));}
			else if(currentBase == 10) {primaryField.setText(Integer.toString(byteValue));}
			else if(currentBase == 8) {primaryField.setText(Integer.toOctalString(Byte.toUnsignedInt(byteValue)));}
			else {primaryField.setText(Integer.toBinaryString(Byte.toUnsignedInt(byteValue)));}
			hexField.setText(Integer.toHexString(Byte.toUnsignedInt(byteValue)));
			decField.setText(Integer.toString(byteValue));
			octField.setText(Integer.toOctalString(Byte.toUnsignedInt(byteValue)));
			binField.setText(Integer.toBinaryString(Byte.toUnsignedInt(byteValue)));
		}
	}
	
	public static void main(String[] args) {
		Calculator window = new Calculator();
		window.setTitle("Evan's Calculator");
		window.setSize(600, 900);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setBackground(Color.WHITE);
	}
	
	public Calculator() {
		// setup model parameters
		currentBase = 10;
		currentValue = 0;
		valueStack = new Stack<>();
		opStack = new Stack<>();
		leftParenCount = 0;
		parens = false;
		overwriteCurrent = true;
		
		// JFrame layout setup
		GridBagLayout gbl = new GridBagLayout();
		gbl.columnWeights = new double[6];
		gbl.columnWidths = new int[6];
		for(int i = 0; i < 6; i++) {
			gbl.columnWeights[i] = 1.0;
			gbl.columnWidths[i] = 200;
		}
		gbl.rowWeights = new double[14];
		for(int i = 0; i < 14; i++) {
			gbl.rowWeights[i] = 1.0;
		}
		this.setLayout(gbl);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		// menu bar (0-5, 0)
		JLabel menuButton = new JLabel("\u2261", SwingConstants.CENTER);
		menuButton.setFont(new Font(null, Font.PLAIN, 50));
		c.gridx = 0;
		c.gridy = 0;
		this.add(menuButton, c);
		c.gridwidth = GridBagConstraints.REMAINDER;
		JLabel pageTitle = new JLabel("Programmer");
		pageTitle.setFont(new Font(null, Font.PLAIN, 50));
		c.gridx = 1;
		c.gridy = 0;
		this.add(pageTitle, c);
		
		// expression field (0-5, 1)
		expression = new JLabel(" ", SwingConstants.RIGHT);
		expression.setFont(new Font(null, Font.PLAIN, 30));
		c.gridx = 0;
		c.gridy = 1;
		this.add(expression, c);
		
		// primary field (0-5, 2)
		primaryField = new JLabel("0", SwingConstants.RIGHT);
		primaryField.setFont(new Font(null, Font.PLAIN, 50));
		c.gridx = 0;
		c.gridy = 2;
		this.add(primaryField, c);
		
		// all fields (0-5, 3-6)
		c.gridwidth = 1;
		c.gridx = 0;
		hexLabel = new JLabel("HEX");
		hexLabel.setFont(new Font(null, Font.PLAIN, 30));
		c.gridy = 3;
		this.add(hexLabel, c);
		decLabel = new JLabel("DEC");
		decLabel.setFont(new Font(null, Font.PLAIN, 30));
		c.gridy = 4;
		this.add(decLabel, c);
		octLabel = new JLabel("OCT");
		octLabel.setFont(new Font(null, Font.PLAIN, 30));
		c.gridy = 5;
		this.add(octLabel, c);
		binLabel = new JLabel("BIN");
		binLabel.setFont(new Font(null, Font.PLAIN, 30));
		c.gridy = 6;
		this.add(binLabel, c);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 1;
		hexField = new JLabel("0");
		hexField.setFont(new Font(null, Font.PLAIN, 24));
		c.gridy = 3;
		this.add(hexField, c);
		decField = new JLabel("0");
		decField.setFont(new Font(null, Font.PLAIN, 24));
		c.gridy = 4;
		this.add(decField, c);
		octField = new JLabel("0");
		octField.setFont(new Font(null, Font.PLAIN, 24));
		c.gridy = 5;
		this.add(octField, c);
		binField = new JLabel("0");
		binField.setFont(new Font(null, Font.PLAIN, 24));
		c.gridy = 6;
		this.add(binField, c);
		
		// nonfunctional buttons (0-1, 7), (4-5, 7), (0-5, 8), (4, 13)
		c.gridwidth = 1;
		
		c.gridy = 7;
		JButton v1 = new JButton("V1");
		v1.setFont(new Font(null, Font.PLAIN, 30));
		v1.setBackground(VL_GRAY);
		c.gridx = 0;
		this.add(v1, c);
		JButton v2 = new JButton("V2");
		v2.setFont(new Font(null, Font.PLAIN, 30));
		v2.setBackground(VL_GRAY);
		c.gridx = 1;
		this.add(v2, c);
		JButton ms = new JButton("Ms");
		ms.setFont(new Font(null, Font.PLAIN, 30));
		ms.setBackground(VL_GRAY);
		c.gridx = 4;
		this.add(ms, c);
		JButton m = new JButton("M\u25be");
		m.setFont(new Font(null, Font.PLAIN, 30));
		m.setBackground(VL_GRAY);
		c.gridx = 5;
		this.add(m, c);
		
		c.gridy = 8;
		lsh = new JButton("Lsh");
		lsh.setFont(new Font(null, Font.PLAIN, 30));
		lsh.setBackground(VL_GRAY);
		c.gridx = 0;
		this.add(lsh, c);
		rsh = new JButton("Rsh");
		rsh.setFont(new Font(null, Font.PLAIN, 30));
		rsh.setBackground(VL_GRAY);
		c.gridx = 1;
		this.add(rsh, c);
		JButton or = new JButton("Or");
		or.setFont(new Font(null, Font.PLAIN, 30));
		or.setBackground(VL_GRAY);
		c.gridx = 2;
		this.add(or, c);
		JButton xor = new JButton("Xor");
		xor.setFont(new Font(null, Font.PLAIN, 30));
		xor.setBackground(VL_GRAY);
		c.gridx = 3;
		this.add(xor, c);
		JButton not = new JButton("Not");
		not.setFont(new Font(null, Font.PLAIN, 30));
		not.setBackground(VL_GRAY);
		c.gridx = 4;
		this.add(not, c);
		JButton and = new JButton("And");
		and.setFont(new Font(null, Font.PLAIN, 30));
		and.setBackground(VL_GRAY);
		c.gridx = 5;
		this.add(and, c);
		JButton period = new JButton(".");
		period.setFont(new Font(null, Font.PLAIN, 30));
		period.setBackground(VL_GRAY);
		period.setForeground(Color.GRAY);
		c.gridx = 4;
		c.gridy = 13;
		this.add(period, c);
		
		// value buttons (0-4, 10-12), (3, 13)
		// create all
		valueButtons = new CalcValueButton[16];
		for(int i = 0; i < 16; i++) {
			valueButtons[i] = new CalcValueButton(i);
		}
		setCurrentBase(currentBase);
		
		// add 0
		c.gridx = 3;
		c.gridy = 13;
		this.add(valueButtons[0], c);
		
		// add 1-9
		int num = 1;
		for(int y = 12; y > 9; y--) {
			for(int x = 2; x < 5; x++) {
				c.gridx = x;
				c.gridy = y;
				this.add(valueButtons[num], c);
				num++;
			}
		}
		// add A-F
		for(int y = 10; y < 13; y++) {
			for(int x = 0; x < 2; x++) {
				c.gridx = x;
				c.gridy = y;
				this.add(valueButtons[num], c);
				num++;
			}
		}
		
		// function buttons (2-3, 7), (0-4, 9), (5, 9-13), (0-2, 13)
		functionButtons = new CalcFunctionButton[14];
		
		// QWORD
		functionButtons[0] = new CalcFunctionButton("QWORD");
		c.gridwidth = 2;
		c.gridx = 2;
		c.gridy = 7;
		this.add(functionButtons[0], c);
		c.gridwidth = 1;
		
		// (0-4, 9)
		c.gridy = 9;
		functionButtons[1] = new CalcFunctionButton("\u2191");
		c.gridx = 0;
		this.add(functionButtons[1], c);
		functionButtons[2] = new CalcFunctionButton("Mod");
		c.gridx = 1;
		this.add(functionButtons[2], c);
		functionButtons[3] = new CalcFunctionButton("CE");
		c.gridx = 2;
		this.add(functionButtons[3], c);
		functionButtons[4] = new CalcFunctionButton("C");
		c.gridx = 3;
		this.add(functionButtons[4], c);
		functionButtons[5] = new CalcFunctionButton("\u232b");
		c.gridx = 4;
		this.add(functionButtons[5], c);
		
		// (5, 9-13)
		c.gridx = 5;
		functionButtons[6] = new CalcFunctionButton("\u00f7");
		c.gridy = 9;
		this.add(functionButtons[6], c);
		functionButtons[7] = new CalcFunctionButton("\u00d7");
		c.gridy = 10;
		this.add(functionButtons[7], c);
		functionButtons[8] = new CalcFunctionButton("-");
		c.gridy = 11;
		this.add(functionButtons[8], c);
		functionButtons[9] = new CalcFunctionButton("+");
		c.gridy = 12;
		this.add(functionButtons[9], c);
		functionButtons[10] = new CalcFunctionButton("=");
		c.gridy = 13;
		this.add(functionButtons[10], c);
		
		// (0-2, 13)
		c.gridy = 13;
		functionButtons[11] = new CalcFunctionButton("(");
		c.gridx = 0;
		this.add(functionButtons[11], c);
		functionButtons[12] = new CalcFunctionButton(")");
		c.gridx = 1;
		this.add(functionButtons[12], c);
		functionButtons[13] = new CalcFunctionButton("\u00b1");
		c.gridx = 2;
		this.add(functionButtons[13], c);
		
		// add value buttons to action listener
		for(int i = 0; i < valueButtons.length; i++) {
			valueButtons[i].addActionListener(this);
		}
		
		// add function buttons to action listener
		for(int i = 0; i < 14; i++) {
			functionButtons[i].addActionListener(this);
		}
		
		// add fields to mouse listener
		hexLabel.addMouseListener(this);
		decLabel.addMouseListener(this);
		octLabel.addMouseListener(this);
		binLabel.addMouseListener(this);
		hexField.addMouseListener(this);
		decField.addMouseListener(this);
		octField.addMouseListener(this);
		binField.addMouseListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try{
			((CalcButton) e.getSource()).calculatorAction(this);
			return;
		}
		catch (RuntimeException ex) {
			ex.printStackTrace();
		}
	}
	
	void modulus() {
		if(currentValue == 0 && (opStack.peek() == "%" || opStack.peek() == "/")) {
			return;
		}
		
		valueStack.push(currentValue);
		if(parens) {
			expression.setText(expression.getText() + " % ");
			parens = false;
		}
		else {
			expression.setText(expression.getText() + currentValue + " % ");
		}
		evaluateToAddSubLP();
		opStack.push("%");
		setCurrentValue(valueStack.peek());
		currentValue = 0;
	}
	
	void clearCurrentExpression() {
		setCurrentValue(0);
		checkDeletePExpression();
	}
	
	// delete current parens expression if there is one
	void checkDeletePExpression() {
		if(parens) {
			String text = expression.getText();
			int i = text.length() - 1;
			for(int parenDepth = 0; i > -1; i--) {
				char ch = text.charAt(i);
				if(ch == ')') {
					parenDepth++;
				}
				else if(ch == '(') {
					parenDepth--;
				}
				
				if(parenDepth == 0) {
					break;
				}
			}
			text = text.substring(0, i);
			if(text.length() > 0) {
				expression.setText(text);
			}
			else {
				expression.setText(" ");
			}
			parens = false;
		}
	}
	
	void clearAll() {
		opStack.clear();
		valueStack.clear();
		expression.setText(" ");
		setCurrentValue(0);
	}
	
	void backspace() {
		if(!parens && !overwriteCurrent) {
			setCurrentValue(currentValue / currentBase);
		}
	}
	
	void divide() {
		if(currentValue == 0 && (opStack.peek() == "%" || opStack.peek() == "/")) {
			return;
		}
		
		valueStack.push(currentValue);
		if(parens) {
			expression.setText(expression.getText() + " \u00f7 ");
			parens = false;
		}
		else {
			expression.setText(expression.getText() + currentValue + " \u00f7 ");
		}
		evaluateToAddSubLP();
		opStack.push("/");
		setCurrentValue(valueStack.peek());
		currentValue = 0;
	}
	
	void multiply() {
		if(currentValue == 0 && (opStack.peek() == "%" || opStack.peek() == "/")) {
			return;
		}
		
		valueStack.push(currentValue);
		if(parens) {
			expression.setText(expression.getText() + " \u00d7 ");
			parens = false;
		}
		else {
			expression.setText(expression.getText() + currentValue + " \u00d7 ");
		}
		evaluateToAddSubLP();
		opStack.push("*");
		setCurrentValue(valueStack.peek());
		currentValue = 0;
	}
	
	void substract() {
		if(currentValue == 0 && (opStack.peek() == "%" || opStack.peek() == "/")) {
			return;
		}
		
		valueStack.push(currentValue);
		if(parens) {
			expression.setText(expression.getText() + " - ");
			parens = false;
		}
		else {
			expression.setText(expression.getText() + currentValue + " - ");
		}
		evaluateToLP();
		opStack.push("-");
		setCurrentValue(valueStack.peek());
		currentValue = 0;
	}
	
	void add() {
		if(currentValue == 0 && (opStack.peek() == "%" || opStack.peek() == "/")) {
			return;
		}
		
		valueStack.push(currentValue);
		if(parens) {
			expression.setText(expression.getText() + " + ");
			parens = false;
		}
		else {
			expression.setText(expression.getText() + currentValue + " + ");
		}
		evaluateToLP();
		opStack.push("+");
		setCurrentValue(valueStack.peek());
		currentValue = 0;
	}
	
	private void evaluateToLP() {
		try {
			String currentOp = opStack.pop();
			while(true) {
				if(currentOp.equals("/")) {
					long rightValue = valueStack.pop();
					valueStack.push(valueStack.pop() / rightValue);
				}
				else if(currentOp.equals("*")) {
					long rightValue = valueStack.pop();
					valueStack.push(valueStack.pop() * rightValue);
				}
				else if(currentOp.equals("-")) {
					long rightValue = valueStack.pop();
					valueStack.push(valueStack.pop() - rightValue);
				}
				else if(currentOp.equals("+")) {
					long rightValue = valueStack.pop();
					valueStack.push(valueStack.pop() + rightValue);
				}
				else if(currentOp.equals("%")) {
					long rightValue = valueStack.pop();
					valueStack.push(valueStack.pop() % rightValue);
				}
				else {
					// left paren
					opStack.push(currentOp);
					break;
				}
				currentOp = opStack.pop();
			}
		}
		catch (EmptyStackException e) {}
	}
	
	private void evaluateToAddSubLP() {
		try {
			String currentOp = opStack.pop();
			while(true) {
				if(currentOp.equals("/")) {
					long rightValue = valueStack.pop();
					valueStack.push(valueStack.pop() / rightValue);
				}
				else if(currentOp.equals("*")) {
					long rightValue = valueStack.pop();
					valueStack.push(valueStack.pop() * rightValue);
				}
				else if(currentOp.equals("-")) {
					opStack.push(currentOp);
					break;
				}
				else if(currentOp.equals("+")) {
					opStack.push(currentOp);
					break;
				}
				else if(currentOp.equals("%")) {
					long rightValue = valueStack.pop();
					valueStack.push(valueStack.pop() % rightValue);
				}
				else {
					// left paren
					opStack.push(currentOp);
					break;
				}
				currentOp = opStack.pop();
			}
		}
		catch (EmptyStackException e) {}
	}
	
	void evaluateAll() {
		if(currentValue == 0 && (opStack.peek() == "%" || opStack.peek() == "/")) {
			return;
		}
		
		valueStack.push(currentValue);
		try {
			String currentOp = opStack.pop();
			while(true) {
				if(currentOp.equals("/")) {
					long rightValue = valueStack.pop();
					valueStack.push(valueStack.pop() / rightValue);
				}
				else if(currentOp.equals("*")) {
					long rightValue = valueStack.pop();
					valueStack.push(valueStack.pop() * rightValue);
				}
				else if(currentOp.equals("-")) {
					long rightValue = valueStack.pop();
					valueStack.push(valueStack.pop() - rightValue);
				}
				else if(currentOp.equals("+")) {
					long rightValue = valueStack.pop();
					valueStack.push(valueStack.pop() + rightValue);
				}
				else if(currentOp.equals("%")) {
					long rightValue = valueStack.pop();
					valueStack.push(valueStack.pop() % rightValue);
				}
				currentOp = opStack.pop();
			}
		}
		catch (EmptyStackException e) {}
		leftParenCount = 0;
		expression.setText(" ");
		setCurrentValue(valueStack.pop());
		overwriteCurrent = true;
	}
	
	void leftParen() {
		opStack.push("(");
		String text = expression.getText();
		if(text.endsWith(" ") || text.endsWith("(")) {
			expression.setText(text + "(");
		}
		else {
			expression.setText(text + " (");
		}
		leftParenCount++;
	}
	
	void rightParen() {
		if(leftParenCount > 0) {
			valueStack.push(currentValue);
			if(parens) {
				expression.setText(expression.getText() + ")");
			}
			else {
				expression.setText(expression.getText() + currentValue + ")");
				parens = true;
			}
			String currentOp = opStack.pop();
			while(currentOp != "(") {
				if(currentOp.equals("/")) {
					long rightValue = valueStack.pop();
					valueStack.push(valueStack.pop() / rightValue);
				}
				else if(currentOp.equals("*")) {
					long rightValue = valueStack.pop();
					valueStack.push(valueStack.pop() * rightValue);
				}
				else if(currentOp.equals("-")) {
					long rightValue = valueStack.pop();
					valueStack.push(valueStack.pop() - rightValue);
				}
				else if(currentOp.equals("+")) {
					long rightValue = valueStack.pop();
					valueStack.push(valueStack.pop() + rightValue);
				}
				else if(currentOp.equals("%")) {
					long rightValue = valueStack.pop();
					valueStack.push(valueStack.pop() % rightValue);
				}
				currentOp = opStack.pop();
			}
			leftParenCount--;
			setCurrentValue(valueStack.pop());
		}
	}
	
	void negate() {
		setCurrentValue(0 - currentValue);
	}
	
	void setCurrentBase(int base) {
		currentBase = base;
		for(int i = 0; i < base; i++) {
			valueButtons[i].setForeground(Color.BLACK);
		}
		for(int i = base; i < 16; i++) {
			valueButtons[i].setForeground(Color.GRAY);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent me) {
		try{
			JLabel label = (JLabel) me.getSource();
			if(label.equals(hexLabel) || label.equals(hexField)) {
				setCurrentBase(16);
				setCurrentValue(currentValue);
			}
			else if(label.equals(decLabel) || label.equals(decField)) {
				setCurrentBase(10);
				setCurrentValue(currentValue);
			}
			else if(label.equals(octLabel) || label.equals(octField)) {
				setCurrentBase(8);
				setCurrentValue(currentValue);
			}
			else if(label.equals(binLabel) || label.equals(binField)) {
				setCurrentBase(2);
				setCurrentValue(currentValue);
			}
			return;
		}
		catch (RuntimeException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
