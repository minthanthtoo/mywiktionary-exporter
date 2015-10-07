package com.minthanthtoo.wordlists.wiktionary;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DbExporter {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/wiktionary"
			+ "?useUnicode=true&characterEncoding=UTF-8";

	static final String USER = "root";
	static final String PASS = "";

	static final String filepath = EntriesExtractor.outEntriesFilepath;

	public static void export() {
		Connection conn = null;
		PreparedStatement stmt = null;
		String l = null;
		try {
			Class.forName(JDBC_DRIVER);//* ငယ်(adj)+စာ(n)ရင်း(v)&#10;* &#10;* current&#10;&#10;
			//{{my-noun}}&#10;* compound noun&#10;*  ညှင်း(n)+လုံး(n)+ပြောက်(n)&#10;*  current&#10;&#10;#  ညှင်းပြောက်သဏ္ဌာန်အဆင်တစ်မျိုး။&#10;#:  အင်္ကျီစလေးကညှင်းလုံးပြောက်ဆင်လေးမို့ကြိုက်တယ်။&#10;
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			String query = "INSERT INTO word_entry(word,language,pronunciation,etymology,pos,pos_detail,src_core,src_detail,pos_c4,pos_c5,definition,example,synonyms,antonyms,related,count,translation,word_entry.references,extra) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement(query);
			BufferedReader r = new BufferedReader(new InputStreamReader(
					new FileInputStream(filepath)));
			Pattern p = Pattern.compile("([^,]*),?",Pattern.MULTILINE);
			Pattern _p = Pattern.compile("^\\* +(.*?)$",Pattern.MULTILINE);
			Pattern _p2 = Pattern.compile("^# +(.*?)$",Pattern.MULTILINE);
			Pattern _p3 = Pattern.compile("^#: +(.*?)$",Pattern.MULTILINE);
			while ((l = r.readLine()) != null) {
				int start = 0, end = l.indexOf(",", 1), index = 1;
				String word = l.substring(start, end);
				l=unescapeStringFromCSV(word, index, l);

				// exclude English words
				if (word.charAt(0) < 255)
					continue;
				Matcher m = p.matcher(l);
				while (m.find()) {
					System.out.println(index + ":" + m.group(1));
					if (index > 19)
						break;
					String outStr = "";
					switch (index) {
					case 2:// language
						int i = Math.min(l.indexOf("&#10;"), m.start());
						outStr = l.substring(0, i < 0 ? m.start() : i);
						stmt.setString(index, outStr);
						break;
					case 5:
						String lb = "&#10;";
						String[][] prefices = new String[][] {
								{ "*", "pos_detail" },
								{ "*", "src_core" },
								{ "*", "src_detail" },
								{ "*", "pos_c4" },
								{ "*", "pos_c5" },
								{ "#", "definition" },
								{ "#:", "example" } };
						int j=m.group().indexOf("\n");
						stmt.setString(index, m.group(1).substring(0,j>0?j:m.group(1).length()));
						if (word.charAt(0) < 255) {// English word
							for (String[] str : prefices) {
								stmt.setString(++index, "");
							}// skip Burmese fields
						} else {// Burmese word
							Matcher _m = _p.matcher(m.group(0));
							int lastMatchI=0;
							while (_m.find()) {
								stmt.setString(++index, _m.group());
								System.out.println(index + ":" + _m.group(1)+":"+_m.end());//m.group(1).substring(_m.end()));
								lastMatchI=_m.end();
							}
							while(index<5+6){
								stmt.setString(++index, "");
							}
							_m.usePattern(_p2);
							while (_m.find(lastMatchI)) {
								stmt.setString(++index, _m.group());
								System.out.println(index + ":" + _m.group(1)+":"+_m.end());//m.group(1).substring(_m.end()));
								lastMatchI=_m.end();
							}
							while(index<5+7){
								stmt.setString(++index, "");
							}
							_m.usePattern(_p3);
							while (_m.find(lastMatchI)) {
								stmt.setString(++index, _m.group());
								System.out.println(index + ":" + _m.group(1)+":"+_m.end());//m.group(1).substring(_m.end()));
								lastMatchI=_m.end();
							}
							while(index<5+8){
								stmt.setString(++index, "");
							}
						}
						break;
					case 10:
					default:
						outStr = unescapeStringFromCSV(word, index,
								m.group(1));
						stmt.setString(index, outStr);
					}
					// stmt.setString(index++, m.group());
					index++;
				}
				stmt.addBatch();
				if (true)
					continue;
				int c = m.groupCount();
				for (int i = 1; i < c; i++) {
					System.out.println(i + " : " + m.group(i));
				}
				while ((end = l.indexOf(",", start)) > 0) {
					// if(index<13)
					// String outStr = "";
					// switch (index) {
					// case 2:// language
					// int i = l.indexOf("&#10;");
					// outStr = l.substring(0, i < 0 ? l.length() : i);
					// stmt.setString(index, outStr);
					// break;
					// case 5:
					// String lb = "&#10;";
					// String[][] prefices = new String[][] {
					// { "*", "pos_detail" },
					// { "*", "src_core" },
					// { "*", "src_detail" },
					// { "*", "pos_c4" },
					// { "*", "pos_c5" },
					// { "#", "definition" },
					// { "#:", "example" } };
					// stmt.setString(index, outStr);
					// if (word.charAt(0) < 255) {// English word
					// for (String[] str : prefices) {
					// stmt.setString(++index, "");
					// }// skip Burmese fields
					// } else {// Burmese word
					// i = 0;
					// int j = 0;
					// for (String[] str : prefices) {
					// i = l.indexOf(str[0], j);
					// if (i > -1) {
					// j = l.indexOf(lb, i + 1);
					// outStr = l.substring(i, j < 0 ? end : j);
					// stmt.setString(++index, outStr);
					// j += lb.length();
					// } else {
					// stmt.setString(++index, "");
					// }
					// // System.out.println(word
					// // + ":"
					// // + i
					// // + ","
					// // + j
					// // + ":"
					// // +l);
					// // // + ((i < 0 || j < 0) ? "" : l.substring(
					// // // i, j)));
					// }
					// }
					// break;
					// case 10:
					// default:
					// outStr = unescapeStringFromCSV(word, index,
					// l.substring(start, end));
					// stmt.setString(index, outStr);
					// }
					start = end + 1;
					index++;
				}
				// stmt.setString(index, l.substring(start));
				stmt.addBatch();
				// System.out.println(stmt.toString());
			}
			r.close();
			System.out.println(stmt.toString());
			stmt.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println(l);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	private static String unescapeStringFromCSV(String word, int index,
			String str) {
		return str.replaceAll("&#10;", "\n").replaceAll("&#11;", "\n")
				.replaceAll("&#44;", ",");
	}
}
