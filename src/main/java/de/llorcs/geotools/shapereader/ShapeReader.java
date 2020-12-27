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
import org.geotools.feature.NameImpl;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.util.ProgressListener;

public class ShapeReader {
	public static void main(String[] args) throws IOException {
		// read 
		// https://docs.geotools.org/latest/userguide/tutorial/quickstart/maven.html
		
		
		String path="/Users/michaelkirchmann/git/shapefile/src/main/resources/China_HSR_2016_stations";
		File shapeFile = new File(new File(path), "China_HSR_2016_stations.shp");
		// Now, let's set the parameters that we are going to use to tell the
		// DataStoreFactory which file to use and indicate that we need to store a
		// spatial index when we create our DataStore:

		FileDataStore store = FileDataStoreFinder.getDataStore(shapeFile);
		SimpleFeatureSource featureSource = store.getFeatureSource();

		SimpleFeatureCollection features = featureSource.getFeatures();
		NameImpl zhNameImpl = new NameImpl("NAME_ZH");
		FeatureVisitor visitor = new FeatureVisitor() {

			@Override
			public void visit(Feature feature) {
				System.out.println("-------------");
				// System.out.println(feature.getName());
				Collection<? extends Property> value = feature.getValue();
				for (Property prop : value) {
					// System.out.println("Name: '"+prop.getName()+"', type: "+prop.getType());
					// System.out.println(prop.getValue());
					if (zhNameImpl.equals(prop.getName())) {
						Map<Object, Object> userData = prop.getUserData();
						try {
							byte[] bytes = ((String)prop.getValue()).getBytes();
							String chinese=new String(bytes, "UTF-16");
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
			
		};

		features.accepts(visitor, new DefaultProgressListener());
		
		
		
		System.out.println(features);
		QueryCapabilities queryCapabilities = featureSource.getQueryCapabilities();
		System.out.println(queryCapabilities);
		DataAccess<SimpleFeatureType, SimpleFeature> dataStore2 = featureSource.getDataStore();
		FeatureSource<SimpleFeatureType, SimpleFeature> featureSource2 = dataStore2.getFeatureSource(featureSource.getName());
		System.out.println(featureSource2);
		
		Map<String, Serializable> params = new HashMap<>();
		params.put("url", shapeFile.toURI().toURL());
		params.put("create spatial index", Boolean.TRUE);
		// Let's create the DataStoreFactory using the parameters we just created, and
		// use that factory to create the DataStore:

		ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

		ShapefileDataStore dataStore = (ShapefileDataStore) dataStoreFactory.createDataStore(params);
//				dataStore.createSchema(CITY);
		
		
		List<Name> names = dataStore.getNames();
		System.out.println(names);
		ContentEntry entry = dataStore.getEntry(names.get(0));
		System.out.println(entry);
	}
}
