package de.llorcs.geotools.shapereader;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.NameImpl;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

import com.ibm.icu.text.Transliterator;

public class ShapeReader {
	
	private static final NameImpl ZH_NAME_IMPL = new NameImpl("NAME_ZH");
	
	
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
	private static final String INDUSTRY_PARK="园区";
	private static final String TRAIN="火车";
	private static final String FAST_TRAIN="高铁";
	private static final String STATION="站";
	
	private static final Map<String,String> translationZnToEn;
	static {
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
		tmpTrns.put(INDUSTRY_PARK, "Industry Park");
		translationZnToEn = Collections.unmodifiableMap(tmpTrns);
	}
	

	public static String CHINESE_TO_LATIN = "Han-Latin";
	public static String CHINESE_TO_LATIN_NO_ACCENTS = "Han-Latin; nfd; [:nonspacing mark:] remove; nfc";
	
	
	public static void main(String[] args) throws IOException {
		// read 
		// https://docs.geotools.org/latest/userguide/tutorial/quickstart/maven.html
		
		
		String path="/Users/michaelkirchmann/git/shapefile/src/main/resources/China_HSR_2016_stations";
		File file = new File(new File(path), "China_HSR_2016_stations.shp");
		
		
		
        Map<String, Object> map = new HashMap<>();
        map.put("url", file.toURI().toURL());

        DataStore dataStore = DataStoreFinder.getDataStore(map);
        String typeName = dataStore.getTypeNames()[0];

        FeatureSource<SimpleFeatureType, SimpleFeature> source =
                dataStore.getFeatureSource(typeName);
        Filter filter = Filter.INCLUDE; // ECQL.toFilter("BBOX(THE_GEOM, 10,20,30,40)")
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(filter);
        try (FeatureIterator<SimpleFeature> features = collection.features()) {
            while (features.hasNext()) {
                SimpleFeature feature = features.next();
                System.out.print(feature.getID());
                System.out.print(": ");
                System.out.println(feature.getDefaultGeometryProperty().getValue());
                
                
                Collection<Property> properties = feature.getProperties();
                analyzeProperties(properties);
            }
        }
	}

	protected static void analyzeProperties(Collection<? extends Property> value) {
		for (Property prop : value) {
			
			// System.out.println("Name: '"+prop.getName()+"', type: "+prop.getType());
			// System.out.println(prop.getValue());
			if (ZH_NAME_IMPL.equals(prop.getName())) {
				Map<Object, Object> userData = prop.getUserData();
				try {
					String rawName=(String)prop.getValue();
					byte[] rawBytes = rawName.getBytes("ISO_8859_1");
					String chinese=new String(rawBytes, "UTF-8");
					
					
					String translated=translate(chinese);
					System.out.println("Name (ZN): "+chinese+" - EN: "+translated);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				
			}
		}
	}

	private static String translate(String chinese) {
		
		String transliterated = transcribe(chinese);
		Set<Entry<String,String>> entrySet = translationZnToEn.entrySet();
		for (Entry<String, String> entry : entrySet) {
			if (chinese.endsWith(entry.getKey())) {
				String restChinese = chinese.substring(0,chinese.length()-entry.getKey().length());
				return transcribe(restChinese)+" "+entry.getValue();
			}			
		}
		return transcribe(chinese);
	}

	private static String transcribe(String chineseString) {
		if (chineseString==null || chineseString.equals("")) {
			return "";
		}
		Transliterator chineseToLatinTrans = Transliterator.getInstance(CHINESE_TO_LATIN);
		String result1 = chineseToLatinTrans.transliterate(chineseString);
		// System.out.println("Chinese to Latin:" + result1);
		
		Transliterator chineseToLatinNoAccentsTrans = Transliterator.getInstance(CHINESE_TO_LATIN_NO_ACCENTS);
		String result2 = chineseToLatinNoAccentsTrans.transliterate(chineseString);
		String intermediate=result2.replace(" ", "");
		String capitalizeFirst=intermediate.substring(0,1).toUpperCase();
		String lastPart=intermediate.substring(1);
		return capitalizeFirst+lastPart;
	}
}
