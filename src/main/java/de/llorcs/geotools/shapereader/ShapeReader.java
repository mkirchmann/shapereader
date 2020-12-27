package de.llorcs.geotools.shapereader;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataAccess;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.QueryCapabilities;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.store.ContentEntry;
import org.geotools.data.util.DefaultProgressListener;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.NameImpl;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.util.ProgressListener;

public class ShapeReader {
	
	private static final NameImpl ZH_NAME_IMPL = new NameImpl("NAME_ZH");
	
	
	private static final String SOUTH="南";
	private static final String EAST="东";
	private static final String NORTH="北";
	private static final String WEST="西";
	private static final String TRAIN="火车";
	private static final String FAST_TRAIN="高铁";
	private static final String STATION="站";
	
	
	
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
					System.out.println("Name (ZN): "+chinese);
					if (chinese.endsWith("火车站")) {
						
					} else if (chinese.endsWith("高铁站")) {
						
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				
			}
		}
	}
}
