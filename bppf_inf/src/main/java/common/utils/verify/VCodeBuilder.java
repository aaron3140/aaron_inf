package common.utils.verify;

public class VCodeBuilder {

	public static final int CODE_4_COUNT = 4; // 验证码字符个数

	public static final int CODE_16_COUNT = 16;

	private static final char[] CHAR_SEQ = {'A', 'B', 'C', 'D', 'E', 'F', 'G',
			'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
			'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9' };

	public VCodeBuilder() {
	}

	public String generate() {

		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < CODE_4_COUNT; i++) {

			buf.append(getNextChar());
		}

		return buf.toString();
	}

	public String generate16() {

		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < CODE_16_COUNT; i++) {

			buf.append(getNextChar());
		}

		return buf.toString();
	}

	public static char getNextChar() {
		return CHAR_SEQ[randInRange(0, CHAR_SEQ.length)];
	}

	public static int randInRange(int x, int y) {
		return x + (int) (Math.random() * (y - x));
	}

	public static double randInRange(double x, double y) {
		return x + Math.random() * (y - x);
	}
}
