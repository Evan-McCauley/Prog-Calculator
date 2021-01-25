// Evan McCauley

import javax.swing.*;

// a template for a JButton with a calculator action 
abstract class CalcButton extends JButton{
	CalcButton(String text) {
		super(text);
	}
	
	void calculatorAction(Calculator calculator) {}
}
