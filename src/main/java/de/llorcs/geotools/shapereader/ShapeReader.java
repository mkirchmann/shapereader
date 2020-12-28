package de.llorcs.geotools.shapereader;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

public class ShapeReader {
	
	private static final NameImpl ZH_NAME_IMPL = new NameImpl("NAME_ZH");
	private FeatureSource<SimpleFeatureType, SimpleFeature> source;
	
	ShapeReader(File file) throws IOException {
		Map<String, Object> map = new HashMap<>();
        map.put("url", file.toURI().toURL());

        DataStore dataStore = DataStoreFinder.getDataStore(map);
        String typeName = dataStore.getTypeNames()[0];

        source = dataStore.getFeatureSource(typeName);
	}
	
	
	public static void main(String[] args) throws IOException {
		// read 
		// https://docs.geotools.org/latest/userguide/tutorial/quickstart/maven.html
		if (args.length==0) {
			usageAndExit();
		}
		
		File file = new File(args[0]);
		
		if (!file.exists()) {
			System.out.println("File not found: "+file);
			usageAndExit();
		}
		
		new ShapeReader(file).walkSource();
        
	}

	public void walkSource() throws IOException {
		
		MandarinLocationTransscriber zhToEnglish = MandarinLocationTransscriber.toEnglish();
		MandarinLocationTransscriber zhToGerman = MandarinLocationTransscriber.toGerman();
		
		Filter filter = Filter.INCLUDE; // ECQL.toFilter("BBOX(THE_GEOM, 10,20,30,40)")
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(filter);
        try (FeatureIterator<SimpleFeature> features = collection.features()) {
            while (features.hasNext()) {
                SimpleFeature feature = features.next();
                System.out.print(feature.getID());
                System.out.print(": ");
                System.out.println(feature.getDefaultGeometryProperty().getValue());
                
                
                Collection<Property> properties = feature.getProperties();
                Optional<String> optZhName = findZhName(properties);
                String names = optZhName.map(chinese->{
                	String english=zhToEnglish.translate(chinese);
                	String german=zhToGerman.translate(chinese);
                	return "ZH: "+chinese+", EN: "+english+", DE: "+german;
                }).orElse("No Name found.");
                System.out.println(names);
            }
        }
	}

	private Optional<String> findZhName(Collection<Property> properties) {
		return properties.stream().filter(prop->ZH_NAME_IMPL.equals(prop.getName())).map(prop->{
			try {
				// re-interpreting the string in UTF-8.
				String rawName=(String)prop.getValue();
				byte[] rawBytes = rawName.getBytes("ISO_8859_1");
				return new String(rawBytes, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}).findFirst();
	}


	private static void usageAndExit() {
		System.out.println("Run with 1 argument with the path to the .shp file.");
		System.exit(-1);
	}
	
}
