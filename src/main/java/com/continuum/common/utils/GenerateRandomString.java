package com.continuum.common.utils;



import java.util.Random;

/**
 * @author Benetton QA
 *
 */
public class GenerateRandomString {
	private static final char[] Unicode = new char[222];
	private static final char[] AlphaNumricSymbols = new char[94];
	private static final char[] AlphaNumric = new char[62];
	private static final char[] Numric = new char[10];
	private static final char[] Alphabates = new char[52];
	private static final char[] symbols = new char[31];
	private static char[] buf;
	private static Random random = new Random();
	
	public static enum RANDOMTYPE {
		ALPHAONLY, NUMRICONLY, SYMBOLSONLY, ALPHANUMRIC, ALPHANYMRICSYMBOLS, UNICODEONLY, ALPHANYMRICSYMBOLSUNICODE;
	};
	
	private static final int[] UnicodeArray = { 33, 34, 35, 36, 37, 38, 39, 40,
		41, 42, 43, 44, 45, 46, 47, 58, 59, 60, 61, 62, 63, 64, 91, 92, 93,
		94, 95, 96, 123, 124, 125, 126, 161, 162, 163, 164, 165, 166, 167,
		168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180,
		181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193,
		194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206,
		207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219,
		220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232,
		233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245,
		246, 247, 248, 249, 250, 251, 252, 253, 254, 255 };
	
	static {
		for (int idx = 0; idx < 10; ++idx)
			Unicode[idx] = (char) ('0' + idx);
		for (int idx = 10; idx < 36; ++idx)
			Unicode[idx] = (char) ('a' + idx - 10);
		for (int idx = 36; idx < 62; ++idx)
			Unicode[idx] = (char) ('A' + idx - 36);
		for (int idx = 62; idx < 78; ++idx)
			Unicode[idx] = (char) (32 + idx - 62);
		for (int idx = 78; idx < 85; ++idx)
			Unicode[idx] = (char) (58 + idx - 78);
		for (int idx = 85; idx < 91; ++idx)
			Unicode[idx] = (char) (91 + idx - 85);
		for (int idx = 91; idx < 95; ++idx)
			Unicode[idx] = (char) (123 + idx - 91);
		for (int idx = 95; idx < 222; ++idx)
			Unicode[idx] = (char) UnicodeArray[idx - 95];
	}

	static {
		for (int idx = 0; idx < 10; ++idx)
			AlphaNumricSymbols[idx] = (char) ('0' + idx);
		for (int idx = 10; idx < 36; ++idx)
			AlphaNumricSymbols[idx] = (char) ('a' + idx - 10);
		for (int idx = 36; idx < 62; ++idx)
			AlphaNumricSymbols[idx] = (char) ('A' + idx - 36);
		for (int idx = 62; idx < 77; ++idx)
			AlphaNumricSymbols[idx] = (char) (33 + idx - 62);
		for (int idx = 77; idx < 84; ++idx)
			AlphaNumricSymbols[idx] = (char) (58 + idx - 77);
		for (int idx = 84; idx < 90; ++idx)
			AlphaNumricSymbols[idx] = (char) (91 + idx - 84);
		for (int idx = 90; idx < 94; ++idx)
			AlphaNumricSymbols[idx] = (char) (123 + idx - 90);
	}

	static {
		for (int idx = 0; idx < 10; ++idx)
			Numric[idx] = (char) ('0' + idx);
	}

	static {
		for (int idx = 0; idx < 26; ++idx)
			Alphabates[idx] = (char) ('a' + idx);
		for (int idx = 26; idx < 52; ++idx)
			Alphabates[idx] = (char) ('A' + idx - 26);
	}

	static {
		for (int idx = 0; idx < 26; ++idx)
			AlphaNumric[idx] = (char) ('a' + idx);
		for (int idx = 26; idx < 52; ++idx)
			AlphaNumric[idx] = (char) ('A' + idx - 26);
		for (int idx = 52; idx < 62; ++idx)
			AlphaNumric[idx] = (char) ('0' + idx - 52);

	}
	static {
		for (int idx = 0; idx < 15; ++idx)
			symbols[idx] = (char) (33 + idx);
		for (int idx = 15; idx < 22; ++idx)
			symbols[idx] = (char) (58 + idx - 15);
		for (int idx = 22; idx < 27; ++idx)
			symbols[idx] = (char) (91 + idx - 22);
		for (int idx = 27; idx < 31; ++idx)
			symbols[idx] = (char) (123 + idx - 27);
	}
	
	/**
	 * @param randomtype - Types can be ALPHAONLY, NUMRICONLY, SYMBOLSONLY, ALPHANUMRIC, ALPHANYMRICSYMBOLS, UNICODEONLY, ALPHANYMRICSYMBOLSUNICODE
	 * @param length - length of the string to be created
	 * @return - Text which can be used to send data
	 */
	public static String RandomStringGenrator(RANDOMTYPE randomtype, int length) {
		if (length < 1)
			throw new IllegalArgumentException("length < 1: " + length);
		buf = new char[length];
		switch (randomtype) {
		case ALPHAONLY:
			for (int idx = 0; idx < buf.length; ++idx)
				buf[idx] = Alphabates[random.nextInt(Alphabates.length)];
			break;
		case NUMRICONLY:
			for (int idx = 0; idx < buf.length; ++idx)
				buf[idx] = Numric[random.nextInt(Numric.length)];
			break;
		case ALPHANUMRIC:
			for (int idx = 0; idx < buf.length; ++idx)
				buf[idx] = AlphaNumric[random.nextInt(AlphaNumric.length)];
			break;
		case ALPHANYMRICSYMBOLS:
			for (int idx = 0; idx < buf.length; ++idx)
				buf[idx] = AlphaNumricSymbols[random.nextInt(AlphaNumricSymbols.length)];
			break;
		case SYMBOLSONLY:
			for (int idx = 0; idx < buf.length; ++idx)
				buf[idx] = symbols[random.nextInt(symbols.length)];
			break;
		case UNICODEONLY:
			for (int idx = 0; idx < buf.length; ++idx)
				buf[idx] = (char) UnicodeArray[random.nextInt(UnicodeArray.length)];
			break;
		case ALPHANYMRICSYMBOLSUNICODE:
			for (int idx = 0; idx < buf.length; ++idx)
				buf[idx] = Unicode[random.nextInt(Unicode.length)];
			break;
		default:
			System.out.println("The random type is unknown.");
			break;
		}
		return new String(buf);
	}
}
