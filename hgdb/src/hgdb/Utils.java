/**
 * 
 */
package hgdb;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;

import org.hypergraphdb.HGConfiguration;
import org.hypergraphdb.handle.SequentialUUIDHandleFactory;
import org.hypergraphdb.storage.bje.BJEConfig;


public class Utils {
	private static final long MEGABYTE = 1024L * 1024L;
	public static void print(String message) {
		System.out.println(message);
	}
	
	public static void printRAM(FileWriter fw) throws IOException {
		long heapMaxSize = Runtime.getRuntime().maxMemory();
		long heapSize = Runtime.getRuntime().totalMemory();
		long heapFreeSize = Runtime.getRuntime().freeMemory();
		
		NumberFormat format = NumberFormat.getInstance();
		print("Max Heap size = "+format.format(heapMaxSize/MEGABYTE)+"MB \n", fw);
		print("Current Heap size = "+format.format(heapSize/MEGABYTE)+"MB \n", fw);
		print("Free Heap size = "+format.format(heapFreeSize/MEGABYTE)+"MB \n", fw);

	}	
	public static void printRAM() throws IOException {
		long heapMaxSize = Runtime.getRuntime().maxMemory();
		long heapSize = Runtime.getRuntime().totalMemory();
		long heapFreeSize = Runtime.getRuntime().freeMemory();
		
		NumberFormat format = NumberFormat.getInstance();
		System.out.print("******************************************\n");
		System.out.print("Max Heap size = "+format.format(heapMaxSize/MEGABYTE)+"MB \n");
		System.out.print("Current Heap size = "+format.format(heapSize/MEGABYTE)+"MB \n");
		System.out.print("Free Heap size = "+format.format(heapFreeSize/MEGABYTE)+"MB \n");
		System.out.print("******************************************\n");
		
	}
	public static void print(String to_be_printed, FileWriter fw) throws IOException {
		System.out.print(to_be_printed);
		fw.write(to_be_printed);
	}
	public static HGConfiguration setConfig() {
		// setting the configurations
		HGConfiguration config = new HGConfiguration();
		SequentialUUIDHandleFactory handleFactory = new SequentialUUIDHandleFactory(System.currentTimeMillis(), 0);
		config.setHandleFactory(handleFactory);
		config.setTransactional(false);
//		BJEConfig storeConfig = (BJEConfig) config.getStoreImplementation().getConfiguration();
//		storeConfig.getEnvironmentConfig().setCacheSize(1024 * 1024 * 1000);
		
		return config;		 
	}
	
	public static List<String> getAllLines(String fileName){
		List<String> lines = Collections.emptyList(); 
	    try
	    { 
	      lines = 
	       Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8); 
	    } 
	    catch (IOException e) 
	    { 
	    	e.printStackTrace(); 
	    } 
	    return lines;
	}
	
	public static long getFolderSize(File folder) {
	    long length = 0;
	    File[] files = folder.listFiles();
	 
	    int count = files.length;
	 
	    for (int i = 0; i < count; i++) {
	        if (files[i].isFile()) {
	        	System.out.println(files[i].getName());
	            length += files[i].length();
	        }
	        else {
	            length += getFolderSize(files[i]);
	        }
	    }
	    return length;
	}
	
	public static void deleteFolder(File folder) {
		String[]entries = folder.list();
		for(String s: entries){
		    File currentFile = new File(folder.getPath(),s);
		    currentFile.delete();
		}
		folder.delete();
	}
}