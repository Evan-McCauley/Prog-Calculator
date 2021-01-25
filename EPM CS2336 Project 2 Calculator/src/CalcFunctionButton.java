// Evan McCauley

import java.awt.Font;

class CalcFunctionButton extends CalcButton {
	CalcFunctionButton(String text) {
		super(text);
		setFont(new Font(null, Font.PLAIN, 30));
		setBackground(Calculator.VL_GRAY);
	}
	
	void calculatorAction(Calculator calculator) {
		// QWORD etc.
		if(this == calculator.functionButtons[0]) {
			if(this.getText().equalsIgnoreCase("QWORD")) {
				this.setText("DWORD");
				calculator.setCurrentValue((int) calculator.currentValue);
			}
			else if(this.getText().equalsIgnoreCase("DWORD")) {
				this.setText("WORD");
				calculator.setCurrentValue((short) calculator.currentValue);
			}
			else if(this.getText().equalsIgnoreCase("WORD")) {
				this.setText("BYTE");
				calculator.setCurrentValue((byte) calculator.currentValue);
			}
			else {
				this.setText("QWORD");
				calculator.setCurrentValue(calculator.currentValue);
			}
			return;
		}
		else if(this == calculator.functionButtons[1]) {
			// switch between alternate texts for lsh and rsh
			if(calculator.lsh.getText().equalsIgnoreCase("Lsh")) {
				calculator.lsh.setText("RoL");
				calculator.rsh.setText("RoR");
			}
			else {
				calculator.lsh.setText("Lsh");
				calculator.rsh.setText("Rsh");
			}
		}
		else if(this == calculator.functionButtons[2]) {
			calculator.modulus();
		}
		else if(this == calculator.functionButtons[3]) {
			calculator.clearCurrentExpression();
		}
		else if(this == calculator.functionButtons[4]) {
			calculator.clearAll();
		}
		else if(this == calculator.functionButtons[5]) {
			calculator.backspace();
		}
		else if(this == calculator.functionButtons[6]) {
			calculator.divide();
		}
		else if(this == calculator.functionButtons[7]) {
			calculator.multiply();
		}
		else if(this == calculator.functionButtons[8]) {
			calculator.substract();
		}
		else if(this == calculator.functionButtons[9]) {
			calculator.add();
		}
		else if(this == calculator.functionButtons[10]) {
			calculator.evaluateAll();
		}
		else if(this == calculator.functionButtons[11]) {
			calculator.leftParen();
		}
		else if(this == calculator.functionButtons[12]) {
			calculator.rightParen();
		}
		else if(this == calculator.functionButtons[13]) {
			calculator.negate();
		}
	}
}
