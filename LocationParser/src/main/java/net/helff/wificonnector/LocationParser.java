package net.helff.wificonnector;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class LocationParser {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		formatJson();
		//parsePrinters();
	}
	
	public static void formatJson() throws Exception {
		Map<String, String> idMap = new HashMap<String, String>();
		Gson g = new Gson();
		List result = g.fromJson(new InputStreamReader(LocationParser.class.getResourceAsStream("/locations.txt")), List.class);
		for(Object key : result) {
			Map<String, String> entry = (Map<String, String>)key;
			String id = entry.get("id");
			String newId = id;
			String floor = id.substring(id.indexOf('_')+1, id.lastIndexOf('_'));
			if(floor.equals("EG")) {
				floor = "0";
				//id = id.replace("_EG_", "_0_");
			}
			String text = entry.get("text");
			if(text.startsWith("W") || text.startsWith("T")) {
				String block = text.substring(1, 2);
				entry.put("block", block);
				String p = text.substring(2,3);
				if(block.equals("A")) {
					if(p.equals("1")) {
						entry.put("position", "West");
					}
					if(p.equals("2")) {
						entry.put("position", "Mitte");
					}
					if(p.equals("3")) {
						entry.put("position", "Ost");
					}
				} else if(block.equals("B")) {
					if(p.equals("1")) {
						entry.put("position", "West");
					}
					if(p.equals("2")) {
						entry.put("position", "Ost");
					}
				} else {
					if(p.equals("1")) {
						entry.put("position", "Nord");
					}
					if(p.equals("2")) {
						entry.put("position", "Süd");
					}
				}
				newId = "GBR_" + floor + "_" + text;
			} else if(!text.startsWith("X")) {
				entry.put("block", text.substring(0, 1));
				entry.put("position", text);
				newId = "GBR_" + floor + "_" + text;
			}
			idMap.put(id, newId);
			entry.put("floor", floor);
			entry.put("id", newId);
			//System.out.println(key+",");
		}
		System.out.println(g.toJson(result));
		System.out.println("\n\n\n");
		
		List conns = g.fromJson(new InputStreamReader(LocationParser.class.getResourceAsStream("/connections.txt")), List.class);
		for(Object key : conns) {
			Map<String, String> entry = (Map<String, String>)key;
			entry.put("start", idMap.get(entry.get("start")));
			entry.put("end", idMap.get(entry.get("end")));
			entry.put("distance", entry.get("distance").replace(',', '.'));
			//System.out.println(key+",");
		}
		System.out.println(g.toJson(conns));
		System.out.println("\n\n\n");
		List wifi = g.fromJson(new InputStreamReader(LocationParser.class.getResourceAsStream("/gbr.locations")), List.class);
		for(Object key : wifi) {
			Map<String, Object> entry = (Map<String, Object>)key;
			//System.out.println(entry);
			if(entry.get("type").toString().equals("1.0")) {
				String block = (String)entry.get("block");
				String floor = entry.get("floor").toString();
				floor = floor.substring(0, 1);
				String location = "GBR_" + floor + "_W" + block;
				String position = (String)entry.get("position");
				if(block.equals("A")) {
					if(position.startsWith("West")) {
						location = location + "1"; 
					}
					if(position.startsWith("Mitte")) {
						location = location + "2"; 
					}
					if(position.startsWith("Ost")) {
						location = location + "3"; 
					}
				} else if(block.equals("B")) {
					if(position.startsWith("West")) {
						location = location + "1"; 
					}
					if(position.startsWith("Ost")) {
						location = location + "2"; 
					}
				} else {
					if(position.startsWith("Nord")) {
						location = location + "1"; 
					}
					if(position.startsWith("Süd")) {
						location = location + "2"; 
					}
				}
				System.out.println("{\"id\":\"" + entry.get("id") + "\",\"location\":\"" + location + "\"},");
			}
		}
		
	}
	
	public static void addQuotes() throws Exception {
		LineNumberReader r = new LineNumberReader(new InputStreamReader(LocationParser.class.getResourceAsStream("/connections.txt")));
		String line = r.readLine();
		while(line != null) {
			int colon = line.indexOf(':')+1;
			int comma = line.indexOf(',');
			String first = line.substring(0, colon);
			String last = line.substring(comma);
			String location = line.substring(colon, comma);
			System.out.println(first + "\"" + location + "\"" + last);
			// next line
			line = r.readLine();
		}
		
	}
	
	public static void parsePrinters() throws Exception {
		List p = new ArrayList();
		LineNumberReader r = new LineNumberReader(new InputStreamReader(LocationParser.class.getResourceAsStream("/printers.txt")));
		String line = r.readLine();
		while(line != null) {
			Map<String, Object> result = new HashMap<String, Object>();
			String[] t = line.split(";");
			String floor = t[0];
			String room = t[1];
			String name = t[3];
			result.put("name", name);
			result.put("location", "GBR_" + floor + "_" + room);
			List features = new ArrayList();
			features.add("BW");
			features.add("A4");
			List<String> tl = Arrays.asList(t);
			if(tl.contains("MFP")) {
				features.add("Copy");
			}
			if(tl.contains("A3")) {
				features.add("A3");
			}
			if(tl.contains("Farbe")) {
				features.add("Color");
			}
			result.put("capabilities", features);
			p.add(result);
			System.out.println(result + ",");
			line = r.readLine();
		}
		Gson g = new Gson();
		System.out.println(g.toJson(p));
	}

}
