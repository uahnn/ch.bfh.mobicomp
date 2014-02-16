package ch.quantasy.tinkerforge.tinker.util.implementation;

import java.util.Arrays;

import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
/**
 * This class wraps the {@link BrickletLCD20x4} and gives some convenience.
 * You will know if you need it... 
 * @author reto
 *
 */
public class LCD20x4Manager {
	
	/**
	 * Allows to abuse the backlight of the {@link BrickletLCD20x4} as a visual alert.
	 * @param lcd the {@link BrickletLCD20x4} that shall blink
	 * @param onTime How long shall the backlight be lit
	 * @param offTime How long shall the backlight be off
	 * @param amount How many times shall the blinking happen
	 */
	public static void visualAlert(final BrickletLCD20x4 lcd, final int onTime,
			final int offTime, final int amount) {
		if (lcd == null) {
			return;
		}
		new Thread() {
			@Override
			public void run() {
				try {
					final boolean initialStatus = lcd.isBacklightOn();
					for (int i = 0; i < amount*2; i++) {

						if (lcd.isBacklightOn()) {
							lcd.backlightOff();
							Thread.sleep(offTime);
						} else {
							lcd.backlightOn();
							Thread.sleep(onTime);
						}
					}
					if (initialStatus) {
						lcd.backlightOn();
					} else {
						lcd.backlightOff();
					}
				} catch (final Exception ex) {
				}
			}
		}.start();
	}

	/**
	 * A possible visual initialization of the {@link BrickletLCD20x4}
	 * Flickers and then lights by printing 'Hello'
	 * @param lcd
	 */
	public static void hello(final BrickletLCD20x4 lcd) {
		if (lcd == null) {
			return;
		}
		synchronized (lcd) {
			try {
				lcd.clearDisplay();
				LCD20x4Manager.visualAlert(lcd, 50, 50, 4);
				Thread.sleep(500);
				lcd.backlightOn();
				write(lcd,(short) 1, (short) 5, "...Hello...");
				Thread.sleep(3000);
				lcd.clearDisplay();
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * A possible visual shut-down of the {@link BrickletLCD20x4}
	 * Prints 'Good Bye' then flickers and finally blacks-out.
	 * @param lcd
	 */
	public static void goodBye(final BrickletLCD20x4 lcd) {
		if (lcd == null) {
			return;
		}
		synchronized (lcd) {
			try {
				lcd.clearDisplay();
				lcd.backlightOn();
				LCD20x4Manager.write(lcd, (short) 1, (short) 5, "...Good");
				LCD20x4Manager.write(lcd, (short) 2, (short) 5, "     Bye...");
				Thread.sleep(3000);
				LCD20x4Manager.visualAlert(lcd, 50, 50, 4);
				Thread.sleep(500);
				lcd.backlightOff();
				lcd.clearDisplay();
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Used to clear a complete line
	 * @param lcd The {@link BrickletLCD20x4} to be used
	 * @param lineNumber The targeted line-number (0,1,2,3) 
	 * @throws TimeoutException
	 * @throws NotConnectedException
	 */
	public static void clearLine(final BrickletLCD20x4 lcd,
			final short lineNumber) throws TimeoutException,
			NotConnectedException {
		if (lcd == null) {
			return;
		}
		synchronized (lcd) {
			LCD20x4Manager.clearPatch(lcd, lineNumber, (short) 0, (short) 19);
		}
	}

	/**
	 * Used to clear a patch of positions within a line
	 * @param lcd The {@link BrickletLCD20x4} to be used
	 * @param lineNumber The targeted line-number (0,1,2,3)
	 * @param start The targeted start-position (0-18) (must be smaller than end)
	 * @param end The targeted end-position (1-19) (must be bigger than start)
	 * @throws TimeoutException
	 * @throws NotConnectedException
	 */
	public static void clearPatch(final BrickletLCD20x4 lcd,
			final short lineNumber, final short start, final short end)
			throws TimeoutException, NotConnectedException {
		if (lcd == null) {
			return;
		}
		synchronized (lcd) {
			final char[] patch = new char[end - start];
			Arrays.fill(patch, ' ');
			lcd.writeLine(lineNumber, start, new String(patch));
		}
	}

	/**
	 * A convenience method to write a text (even with special symbols)
	 * @param lcd The {@link BrickletLCD20x4} to be used
	 * @param line The targeted line-number(0,1,2,3)
	 * @param position The position where the text shall start
	 * @param text The {@link String} to be written
	 * @throws TimeoutException
	 * @throws NotConnectedException
	 */
	public static final void write(final BrickletLCD20x4 lcd, final short line,
			final short position, final String text) throws TimeoutException,
			NotConnectedException {
		if (lcd == null) {
			return;
		}
		lcd.writeLine(line, position, LCD20x4Manager.utf16ToKS0066U(text));
	}

	// Maps a normal UTF-16 encoded string to the LCD charset
	// This has been copied from somewhere else... But I cannot remember from where.
	private static String utf16ToKS0066U(final String utf16) {
		String ks0066u = "";
		char c;

		for (int i = 0; i < utf16.length(); i++) {
			final int codePoint = utf16.codePointAt(i);

			if (Character.isHighSurrogate(utf16.charAt(i))) {
				// Skip low surrogate
				i++;
			}

			// ASCII subset from JIS X 0201
			if ((codePoint >= 0x0020) && (codePoint <= 0x007e)) {
				// The LCD charset doesn't include '\' and '~', use similar
				// characters instead
				switch (codePoint) {
				case 0x005c:
					c = (char) 0xa4;
					break; // REVERSE SOLIDUS maps to IDEOGRAPHIC COMMA
				case 0x007e:
					c = (char) 0x2d;
					break; // TILDE maps to HYPHEN-MINUS
				default:
					c = (char) codePoint;
					break;
				}
			}
			// Katakana subset from JIS X 0201
			else if ((codePoint >= 0xff61) && (codePoint <= 0xff9f)) {
				c = (char) (codePoint - 0xfec0);
			}
			// Special characters
			else {
				switch (codePoint) {
				case 0x00a5:
					c = (char) 0x5c;
					break; // YEN SIGN
				case 0x2192:
					c = (char) 0x7e;
					break; // RIGHTWARDS ARROW
				case 0x2190:
					c = (char) 0x7f;
					break; // LEFTWARDS ARROW
				case 0x00b0:
					c = (char) 0xdf;
					break; // DEGREE SIGN maps to KATAKANA SEMI-VOICED SOUND
							// MARK
				case 0x03b1:
					c = (char) 0xe0;
					break; // GREEK SMALL LETTER ALPHA
				case 0x00c4:
					c = (char) 0xe1;
					break; // LATIN CAPITAL LETTER A WITH DIAERESIS
				case 0x00e4:
					c = (char) 0xe1;
					break; // LATIN SMALL LETTER A WITH DIAERESIS
				case 0x00df:
					c = (char) 0xe2;
					break; // LATIN SMALL LETTER SHARP S
				case 0x03b5:
					c = (char) 0xe3;
					break; // GREEK SMALL LETTER EPSILON
				case 0x00b5:
					c = (char) 0xe4;
					break; // MICRO SIGN
				case 0x03bc:
					c = (char) 0xe4;
					break; // GREEK SMALL LETTER MU
				case 0x03c2:
					c = (char) 0xe5;
					break; // GREEK SMALL LETTER FINAL SIGMA
				case 0x03c1:
					c = (char) 0xe6;
					break; // GREEK SMALL LETTER RHO
				case 0x221a:
					c = (char) 0xe8;
					break; // SQUARE ROOT
				case 0x00b9:
					c = (char) 0xe9;
					break; // SUPERSCRIPT ONE maps to SUPERSCRIPT (minus) ONE
				case 0x00a4:
					c = (char) 0xeb;
					break; // CURRENCY SIGN
				case 0x00a2:
					c = (char) 0xec;
					break; // CENT SIGN
				case 0x2c60:
					c = (char) 0xed;
					break; // LATIN CAPITAL LETTER L WITH DOUBLE BAR
				case 0x00f1:
					c = (char) 0xee;
					break; // LATIN SMALL LETTER N WITH TILDE
				case 0x00d6:
					c = (char) 0xef;
					break; // LATIN CAPITAL LETTER O WITH DIAERESIS
				case 0x00f6:
					c = (char) 0xef;
					break; // LATIN SMALL LETTER O WITH DIAERESIS
				case 0x03f4:
					c = (char) 0xf2;
					break; // GREEK CAPITAL THETA SYMBOL
				case 0x221e:
					c = (char) 0xf3;
					break; // INFINITY
				case 0x03a9:
					c = (char) 0xf4;
					break; // GREEK CAPITAL LETTER OMEGA
				case 0x00dc:
					c = (char) 0xf5;
					break; // LATIN CAPITAL LETTER U WITH DIAERESIS
				case 0x00fc:
					c = (char) 0xf5;
					break; // LATIN SMALL LETTER U WITH DIAERESIS
				case 0x03a3:
					c = (char) 0xf6;
					break; // GREEK CAPITAL LETTER SIGMA
				case 0x03c0:
					c = (char) 0xf7;
					break; // GREEK SMALL LETTER PI
				case 0x0304:
					c = (char) 0xf8;
					break; // COMBINING MACRON
				case 0x00f7:
					c = (char) 0xfd;
					break; // DIVISION SIGN

				default:
				case 0x25a0:
					c = (char) 0xff;
					break; // BLACK SQUARE
				}
			}

			// Special handling for 'x' followed by COMBINING MACRON
			if (c == (char) 0xf8) {
				if (!ks0066u.endsWith("x")) {
					c = (char) 0xff; // BLACK SQUARE
				}

				if (ks0066u.length() > 0) {
					ks0066u = ks0066u.substring(0, ks0066u.length() - 1);
				}
			}

			ks0066u += c;
		}

		return ks0066u;
	}
}