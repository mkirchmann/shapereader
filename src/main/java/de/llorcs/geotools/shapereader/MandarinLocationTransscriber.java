package de.llorcs.geotools.shapereader;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class MandarinLocationTransscriber {
	private static final String SOUTH="南";
	private static final String EAST="东";
	private static final String NORTH="北";
	private static final String WEST="西";
	private static final String THREE_TOWNS="三镇";
	private static final String AIRPORT="机场";
	private static final String UNIVERSITY="大学";
	private static final String NEW_AREA="新区";
	private static final String DEVELOPED_AREA="开发区";
	private static final String SCENIC_SPOT="景区";
	private static final String INDUSTRIAL_PARK="园区";
	private static final List<String> STREET=Arrays.asList("路", "街", "街道");
	private static final String ROAD="道";
	
	private static final String GARDEN="公园";
	private static final String SQUARE="广场";
	
	
	private static final String MAIN_ROAD="大道";
	private static final String MAIN_STREET="大街";
	private static final String ALLEYWAY="弄";
	private static final List<String> LANE=Arrays.asList("巷","小路","小径","巷","车道");
	private static final String CHANNEL="航道";
	private static final String RING_ROAD="环城公路";
	private static final List<String> HIGHWAY=Arrays.asList("公路", "高速公路");
	
	private static final List<String> ALLEY=Arrays.asList("胡同","弄堂");
	private static final List<String> PORT=Arrays.asList(/* "港",*/ "港口");
	
	private static final String TRAIN="火车";
	private static final String FAST_TRAIN="高铁";
	private static final String STATION="站";
	
	private static final List<String> TRAIN_STATION=Arrays.asList(STATION,TRAIN+STATION,FAST_TRAIN+STATION);
	
	private static final String SHANGHAI = "上海";
	private static final String HONGKONG = "香港";
	private static final String BEIJING = "北京";
	
	private final Map<String, String> dictionaryZnToLang;
	private MandarinTransliterator transliterator;
	private final Map<String, String> fixedBeginning;
	
	private MandarinLocationTransscriber(Map<String,String> dictionary, Map<String,String> fixedBeginning, MandarinTransliterator transliterator) {
		this.transliterator = transliterator;
		dictionaryZnToLang=Collections.unmodifiableMap(new HashMap<>(dictionary));
		this.fixedBeginning=Collections.unmodifiableMap(new HashMap<>(fixedBeginning));
	}
	
	public static MandarinLocationTransscriber toEnglish() {
		Map<String, String> tmpTrns = new HashMap<>();
		tmpTrns.put(SOUTH, "South");
		tmpTrns.put(EAST, "East");
		tmpTrns.put(WEST, "West");
		tmpTrns.put(NORTH, "North");
		tmpTrns.put(THREE_TOWNS, "Three Towns");
		tmpTrns.put(AIRPORT, "Airport");
		tmpTrns.put(UNIVERSITY, "University");
		tmpTrns.put(DEVELOPED_AREA, "Developed Area");
		tmpTrns.put(NEW_AREA, "New Area");
		tmpTrns.put(SCENIC_SPOT, "Scenic Spot");
		tmpTrns.put(INDUSTRIAL_PARK, "Industrial Park");
		tmpTrns.put(MAIN_ROAD, "Main Road");
		tmpTrns.put(MAIN_STREET, "Main Street");
		tmpTrns.put(CHANNEL, "Channel");
		tmpTrns.put(ROAD, "Road");
		tmpTrns.put(ALLEYWAY, "Alley Way");
		tmpTrns.put(RING_ROAD, "Ring Road");
		tmpTrns.put(SQUARE, "Square");
		tmpTrns.put(GARDEN, "Garden");
		
		ALLEY.stream().forEach(word->tmpTrns.put(word, "Alley"));
		LANE.stream().forEach(word->tmpTrns.put(word, "Lane"));
		STREET.stream().forEach(word->tmpTrns.put(word, "Street"));
		HIGHWAY.stream().forEach(word->tmpTrns.put(word, "Highway"));
		PORT.stream().forEach(word->tmpTrns.put(word, "Port"));
		TRAIN_STATION.stream().forEach(word->tmpTrns.put(word, "Train Station"));
		Map<String, String> fixedTowns = new HashMap<>();
		fixedTowns.put(SHANGHAI, "Shanghai");
		fixedTowns.put(HONGKONG, "Hongkong");
		
		return new MandarinLocationTransscriber(tmpTrns, fixedTowns , MandarinTransliterator.getDefault());
	}
	
	public static MandarinLocationTransscriber toGerman() {
		Map<String, String> tmpTrns = new HashMap<>();
		tmpTrns.put(SOUTH, "Süd");
		tmpTrns.put(EAST, "Ost");
		tmpTrns.put(WEST, "West");
		tmpTrns.put(NORTH, "Nord");
		tmpTrns.put(THREE_TOWNS, "Drei Städte");
		tmpTrns.put(AIRPORT, "Flughafen");
		tmpTrns.put(UNIVERSITY, "Universität");
		tmpTrns.put(DEVELOPED_AREA, "Entwickeltes Gebiet");
		tmpTrns.put(NEW_AREA, "Neues Gebiet");
		tmpTrns.put(SCENIC_SPOT, "Malerische Aussicht");
		tmpTrns.put(INDUSTRIAL_PARK, "Gewerbegebiet");
		tmpTrns.put(MAIN_ROAD, "Hauptstraße");
		tmpTrns.put(MAIN_STREET, "Hauptstraße");
		tmpTrns.put(CHANNEL, "Kanal");
		tmpTrns.put(ROAD, "Straße");
		tmpTrns.put(ALLEYWAY, "Alleestraße");
		tmpTrns.put(RING_ROAD, "Ringstraße");
		tmpTrns.put(SQUARE, "Platz");
		tmpTrns.put(GARDEN, "Garten");
		
		ALLEY.stream().forEach(word->tmpTrns.put(word, "Allee"));
		LANE.stream().forEach(word->tmpTrns.put(word, "Gasse"));
		STREET.stream().forEach(word->tmpTrns.put(word, "Straße"));
		HIGHWAY.stream().forEach(word->tmpTrns.put(word, "Autobahn"));
		PORT.stream().forEach(word->tmpTrns.put(word, "Hafen"));
		TRAIN_STATION.stream().forEach(word->tmpTrns.put(word, "Bahnhof"));
		Map<String, String> fixedTowns = new HashMap<>();
		fixedTowns.put(SHANGHAI, "Shanghai");
		fixedTowns.put(HONGKONG, "Hongkong");
		fixedTowns.put(BEIJING, "Peking");
		
		return new MandarinLocationTransscriber(tmpTrns, fixedTowns , MandarinTransliterator.getDefault());
	}
	
	public String translate(String chinese) {
		String beginning="";
		Set<Entry<String, String>> fixedBeginningEntrySet = fixedBeginning.entrySet();
		for (Entry<String, String> entry : fixedBeginningEntrySet) {
			String beg=entry.getKey();
			if (chinese.startsWith(beg)) {
				chinese=chinese.substring(beg.length());
				beginning=entry.getValue();
				break;
			}
		}
		
		
		Set<Entry<String,String>> entrySet = dictionaryZnToLang.entrySet();
		String winningEnding="";
		String winningEndingTranslation="";
		for (Entry<String, String> entry : entrySet) {
			String chineseWord=entry.getKey();
			if (chinese.endsWith(chineseWord) && winningEnding.length()<chineseWord.length()) {
				winningEnding=chineseWord;
				winningEndingTranslation=entry.getValue();
			}
		}
		if (!winningEnding.equals("")) {
			String restChinese = chinese.substring(0,chinese.length()-winningEnding.length());
			
			return clean(beginning+" "+transliterator.transcribe(restChinese)+" "+winningEndingTranslation);
		}
		
		return clean(beginning+" "+transliterator.transcribe(chinese));
	}

	private String clean(String string) {
		return string.trim().replace("  ", " ");
	}

}
