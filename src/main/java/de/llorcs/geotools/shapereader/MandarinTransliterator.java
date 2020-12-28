package de.llorcs.geotools.shapereader;

import com.ibm.icu.text.Transliterator;

public class MandarinTransliterator {
	public static final String CHINESE_TO_LATIN = "Han-Latin";
	public static final String CHINESE_TO_LATIN_NO_ACCENTS = "Han-Latin; nfd; [:nonspacing mark:] remove; nfc";
	
	private Transliterator chineseToLatinTrans;
	
	public MandarinTransliterator(String mode) {
		chineseToLatinTrans = Transliterator.getInstance(mode);
	}
	
	public static MandarinTransliterator getDefault() {
		return new MandarinTransliterator(CHINESE_TO_LATIN_NO_ACCENTS);
	}
	
	public String transcribe(String chineseString) {
		if (chineseString==null || chineseString.equals("")) {
			return "";
		}
		
		String result2 = chineseToLatinTrans.transliterate(chineseString);
		String intermediate=result2.replace(" ", "");
		String capitalizeFirst=intermediate.substring(0,1).toUpperCase();
		String lastPart=intermediate.substring(1);
		return capitalizeFirst+lastPart;
	}
}
