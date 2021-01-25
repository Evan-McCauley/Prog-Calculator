// Evan McCauley

import java.awt.Color;
import java.awt.Font;
import java.math.BigInteger;

// a JButton with a value and a standardized associated calculator action
class CalcValueButton extends CalcButton {
	private static final BigInteger QWORD_MAX = BigInteger.valueOf(2).pow(63).subtract(BigInteger.ONE);
	private static final BigInteger QWORD_MIN = BigInteger.valueOf(-2).pow(63);
	private static final BigInteger QWORD_TRUE_MAX = BigInteger.valueOf(2).pow(64).subtract(BigInteger.ONE);
	private static final BigInteger DWORD_MAX = BigInteger.valueOf(2).pow(31).subtract(BigInteger.ONE);
	private static final BigInteger DWORD_MIN = BigInteger.valueOf(-2).pow(31);
	private static final BigInteger DWORD_TRUE_MAX = BigInteger.valueOf(2).pow(32).subtract(BigInteger.ONE);
	private static final BigInteger WORD_MAX = BigInteger.valueOf(2).pow(15).subtract(BigInteger.ONE);
	private static final BigInteger WORD_MIN = BigInteger.valueOf(-2).pow(15);
	private static final BigInteger WORD_TRUE_MAX = BigInteger.valueOf(2).pow(16).subtract(BigInteger.ONE);
	private static final BigInteger BYTE_MAX = BigInteger.valueOf(2).pow(7).subtract(BigInteger.ONE);
	private static final BigInteger BYTE_MIN = BigInteger.valueOf(-2).pow(7);
	private static final BigInteger BYTE_TRUE_MAX = BigInteger.valueOf(2).pow(8).subtract(BigInteger.ONE);
	
	private final int value;
	
	CalcValueButton(int v){
		super(Integer.toString(v, 16).toUpperCase());
		value = v;
		setFont(new Font(null, Font.PLAIN, 30));
		setBackground(Color.LIGHT_GRAY);
	}
	
	void calculatorAction(Calculator calculator) {
		// check if this button is currently inactive
		if(value >= calculator.currentBase) {
			return;
		}
		
		// overwrite check
		if(calculator.overwriteCurrent) {
			calculator.setCurrentValue(0);
			calculator.overwriteCurrent = false;
		}
		
		// calculate newValue
		BigInteger newValue = BigInteger.valueOf(calculator.currentValue);
		BigInteger base = BigInteger.valueOf(calculator.currentBase);
		BigInteger digit = BigInteger.valueOf((calculator.currentValue > -1) ? value : -value);
		newValue = newValue.multiply(base);
		newValue = newValue.add(digit);
		
		// check functionButtons[0] (QWORD, etc.)
		if(calculator.functionButtons[0].getText().equalsIgnoreCase("QWORD")) {
			// check if newValue is within the QWORD range
			if(calculator.currentBase == 10) {
				if(outOfBounds(newValue, QWORD_MAX, QWORD_MIN)) {
					return;
				}
			}
			else {
				if(outOfBounds(newValue, QWORD_TRUE_MAX, BigInteger.ONE.negate())) {
					return;
				}
			}
		}
		else if(calculator.functionButtons[0].getText().equalsIgnoreCase("DWORD")) {
			// check if newValue is within the DWORD range
			if(calculator.currentBase == 10) {
				if(outOfBounds(newValue, DWORD_MAX, DWORD_MIN)) {
					return;
				}
			}
			else {
				if(outOfBounds(newValue, DWORD_TRUE_MAX, BigInteger.ONE.negate())) {
					return;
				}
			}
		}
		else if(calculator.functionButtons[0].getText().equalsIgnoreCase("WORD")) {
			// check if newValue is within the WORD range
			if(calculator.currentBase == 10) {
				if(outOfBounds(newValue, WORD_MAX, WORD_MIN)) {
					return;
				}
			}
			else {
				if(outOfBounds(newValue, WORD_TRUE_MAX, BigInteger.ONE.negate())) {
					return;
				}
			}
		}
		else {
			// check if newValue is within the BYTE range
			if(calculator.currentBase == 10) {
				if(outOfBounds(newValue, BYTE_MAX, BYTE_MIN)) {
					return;
				}
			}
			else {
				if(outOfBounds(newValue, BYTE_TRUE_MAX, BigInteger.ONE.negate())) {
					return;
				}
			}
		}
		
		// set newValue
		calculator.setCurrentValue(newValue.longValue());
	}
	
	private static boolean outOfBounds(BigInteger value, BigInteger max, BigInteger min) {
		int compMAX = value.compareTo(max);
		int compMIN = value.compareTo(min);
		if(compMAX > 0 || compMIN < 0) {
			// value is outside of bounds
			return true;
		}
		else {
			return false;
		}
	}
}
