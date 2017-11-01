package com.travel.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.travel.data.Pair;

public class FileUtils {
	static public final Log log = LogFactory.getLog(FileUtils.class);
	static public Object globalFileMuntex = new Object();

	public static String readStrFromFile(String fileName) {
		StringBuilder sb = new StringBuilder();
		synchronized (globalFileMuntex) {

			File file = new File(fileName);
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream(file), "UTF-8"));
				String line = br.readLine();
				while (line != null) {
					sb.append(line.trim());
					line = br.readLine();
				}
			} catch (Exception e) {
				log.info("exception.", e);
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						log.info("exception.", e);
					}
				}
			}
		}
		return sb.toString();
	}

	public static List<Integer> readIntegerLinesFromFile(String fileName) {
		List<Integer> results = new LinkedList<Integer>();
		synchronized (globalFileMuntex) {

			File file = new File(fileName);
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream(file), "UTF-8"));
				String line = br.readLine();
				while (line != null) {
					results.add(Integer.parseInt(line.trim()));
					line = br.readLine();
				}
			} catch (Exception e) {
				log.info("exception.", e);
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						log.info("exception.", e);
					}
				}
			}
		}
		return results;
	}

	public static Set<Integer> readIntegerSetLinesFromFile(String fileName) {
		Set<Integer> resultSet = new HashSet<Integer>();
		synchronized (globalFileMuntex) {
			File file = new File(fileName);
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream(file), "UTF-8"));
				String line = br.readLine();
				while (line != null) {
					resultSet.add(Integer.parseInt(line.trim()));
					line = br.readLine();
				}
			} catch (Exception e) {
				log.info("exception.", e);
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						log.info("exception.", e);
					}
				}
			}
		}
		return resultSet;
	}

	static public List<String> readLinesFromFile(String filename) {
		List<String> list = new ArrayList<String>();
		synchronized (globalFileMuntex) {
			File file = new File(filename);
			if (!file.exists()) {
				log.error("file doesn't exist:" + filename);
				return list;
			}
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(filename));
				String line = null;
				while ((line = br.readLine()) != null) {
					line = line.trim();
					if (!line.isEmpty() && !line.equals("\\N"))
						list.add(line);
				}
			} catch (IOException e) {
				log.warn("exception", e);
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						log.warn("exception", e);
					}
				}
			}
		}
		return list;
	}

	static public void writeFile(List<String> contents, String file) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file, true));
			for (String str : contents) {
				writer.write(str);
				writer.newLine();
			}
		} catch (Exception e) {
			log.warn("exception", e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					log.warn("exception", e);
				}
			}
		}
	}

	static public void writeFile(String content, String file) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file, true));
			writer.write(content);
			writer.newLine();
		} catch (Exception e) {
			log.warn("exception", e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					log.warn("exception", e);
				}
			}
		}
	}

	static public void writePair2File(List<Pair> contents, String file) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file, true));
			for (Pair p : contents) {
				writer.write(p.key + ":" + p.value);
				writer.newLine();
			}
		} catch (Exception e) {
			log.warn("exception", e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					log.warn("exception", e);
				}
			}
		}
	}

	public static void writeObj2File(String filename, Object obj) {
		try {
			ObjectOutputStream outObj = new ObjectOutputStream(
					new FileOutputStream(filename));
			outObj.writeObject(obj);
			outObj.close();
		} catch (Exception e) {
			log.warn("exception", e);
		}
	}

	public static Object readOjbFromFile(String filename) {
		try {
			ObjectInputStream objIn = new ObjectInputStream(
					new FileInputStream(filename));
			Object obj = objIn.readObject();
			objIn.close();
			return obj;
		} catch (Exception e) {
			log.warn("exception", e);
		}
		return null;
	}

	public static Map<String, String> readAsMap(String filename) {
		Map<String, String> map = new HashMap<String, String>();
		List<String> lines = readLinesFromFile(filename);
		for (String l : lines) {
			if (l.startsWith("#"))
				continue;
			String[] f = l.split("=");
			map.put(f[0], f[1]);
		}
		return map;
	}

	public static boolean delete(String path) {
		File f = new File(path);
		if (f.exists()) {
			return f.delete();
		}
		return false;
	}

	public static boolean mkdir(String path) {
		File f = new File(path);
		if (!f.exists()) {
			return f.mkdirs();
		}
		return false;
	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

	public static boolean deleteAndMkdir(String path) {
		return deleteDir(new File(path)) && mkdir(path);
	}
}
