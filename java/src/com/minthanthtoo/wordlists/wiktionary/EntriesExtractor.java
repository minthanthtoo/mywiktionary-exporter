package com.minthanthtoo.wordlists.wiktionary;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

//<page>
//  <title>သက်တင်</title>
//  <ns>0</ns>
//  <id>1991</id>
//  <revision>
//    <id>313879</id>
//    <parentid>213977</parentid>
//    <timestamp>2011-04-23T12:58:53Z</timestamp>
//    <contributor>
//      <username>MgSunBot</username>
//      <id>833</id>
//    </contributor>
//    <comment>Automated import of articles - append on top</comment>
//    <model>wikitext</model>
//    <format>text/x-wiki</format>
//    <text xml:space="preserve">=={{=my=}}==
//'''သက်တင်'''
//===အသံထွက်===
//* {{MLCTS|/thé tin/}}
//===ရင်းမြစ်===
//* 
//===နာမ်===
//{{my-noun}}
//* simple noun
//* သက်တင်
//* သက်တင်(n)
//* သက်တံ,(n)
//* archaic
//
//# နေနှင့်ဆန့်ကျင်ဘက်ဖြစ်သောကောင်းကင်တစ်နေရာတွင်အရောင် ခုနှစ်မျိုးရှိလေးကိုင်းသဏ္ဌာန်ဖြစ်နေသောအရာ။
//#: သက်တင်တွင်အရောင်ခုနစ်မျိုးကိုတွေ့နိုင်သည်။
//====အရေအတွက်====
//* ခု
//====ကြောင်းတူသံကွဲများ====
//* သက်တံ,စို့,(all nouns)
//====ဆန့်ကျင်ဘက်စကားလုံးများ====
//* 
//====ဆက်စပ်အသုံးများ====
//* 
//===ဘာသာပြန်===
//====အင်္ဂလိပ်====
//* rainbow.
//
//==ကိုးကား==
//# Burmese Orthography, Myanmar Language Commission, 1993.
//
//[[en:သက်တင်]]
//
//{{-my-}}
//{{-my-နာမ္-}} &lt;!--Noun--&gt;
//'''သက္တင္'''
//#[[သက္တံ]] ။ [[စို႕]] 
//
//==ဘာသာျပန္ &lt;!--Translation--&gt;==
//{{(}}
//*{{en}}: [[rainbow]]
//*{{de}}: [[Regenbogen]]
//{{-}}
//*{{is}}: [[regnbogi]]
//*{{es}}: [[arco iris]]
//{{)}}
//
//[[es:သက္တင္]]
//[[is:သက္တင္]]
//[[th:သက္တင္]]</text>
//    <sha1>kdzeo5ossh5bu5fo28an0ytxsiv5myz</sha1>
//  </revision>
//</page>
public class EntriesExtractor extends DefaultHandler {
	static String root="";//"/storage/extSdCard/MT/20150925/WordListExtractor/";
	public static final String srcFilepath = root+ // "data/mywiktioary.samples.txt";
	"data/mywiktionary20150901pagesarticlesmultistream.xml";
	public static final String outEntriesFilepath = srcFilepath+".entries"; 
	public static final int countLimit = Integer.MAX_VALUE;
	int pageCount = 0;
	InputStream in;
	OutputStreamWriter out, out2;
	String matchStr = "page", lastStr = "";
	Vector<String> tagStack = new Vector<String>();
	boolean lockPage = false;
	int itemCount = 0, itemCountLast = 0;
	StringBuilder chars = new StringBuilder();
	Map<String, String> collector = new TreeMap<String, String>(
			new StrComparator());
	Map<String, Integer> collected = new TreeMap<String, Integer>(
			new StrComparator());
	Map<String, Integer> collected2 = new TreeMap<String, Integer>(
			new StrComparator());
	Map<String, TreeMap<String, Integer>> childs = new TreeMap<String, TreeMap<String, Integer>>(
			new StrComparator());
	TreeMap<String, Integer> hierarchy = new TreeMap<String, Integer>(
			new StrComparator());
	TreeMap<String, Integer> hierarchyPattern = new TreeMap<String, Integer>(
			new StrComparator());
	// title, ns, id, redirect, restrictions, <revision>, parentid, timestamp,
	// <contributor>, ip, username, minor, comment, model, format, text, sha1
	String[] str = new String[17];
	Map<String, Integer> tagsMap = new HashMap<String, Integer>();
	int lastTagIndex = -1;

	class StrComparator implements Comparator<String> {
		@Override
		public int compare(String arg0, String arg1) {
			int r = 0, l = Math.min(arg0.length(), arg1.length());
			for (int i = 0; i < l; i++) {
				r = Character.compare(arg0.charAt(i), arg1.charAt(i));
				if (r != 0)
					return r;
			}
			return (arg0.length() == arg1.length()) ? 0 : -1;
		}
	}

	/**
	 * @formatter:off <pre>
	 * =={{=en=}}==
	 * 	===ဝါစင်္ဂ===
	 * 		====ကြောင်းတူသံကွဲများ====
	 * 		====ဆက်စပ်အသုံးများ====
	 * 		====ဆန့်ကျင်ဘက်စကားလုံးများ====
	 * 		====အရေအတွက်====
	 * 	===ဘာသာပြန်===
	 * 	===ရင်းမြစ်===
	 * 	===အသံထွက်===
	 * 		====အင်္ဂလိပ်====
	 * ==ကိုးကား==
	 * </pre>
	 * @formatter:on
	 */
	Object[] arr = new Object[] {
			"{{=my=}}",
			new Object[] {
					"ဝါစင်္ဂ",
					new Object[] {
							"ကြောင်းတူသံကွဲများ",
							"ဆက်စပ်အသုံးများ",
							"ဆန့်ကျင်ဘက်စကားလုံးများ",
							"အရေအတွက်" },
					"ရင်းမြစ်",
					"အသံထွက်",
					"ဘာသာပြန်",
					new Object[] { "အင်္ဂလိပ်" } },
			"ကိုးကား" };
	Map<String, Parent> parentMap = new TreeMap<>();
	String[] entryMyan = new String[] {
		"id",
			"language",
			"pronunciation",
			"etymology",
			"POS",
			"synonyms",
			"antonyms",
			"related",
			"count",
			"translation",
			"references",
			"extra" };
	String[] entryEng = new String[] {
			"language",
			"pronunciation",
			"POS",
			"synonyms",
			"antonyms",
			"related",
			"extra" };

	class Parent {
		String name;
		int freq;
	}

	static Map<String, Integer> headerMap = new TreeMap<>();
	static {
		// headerMap.put("=={{=my=}}==", 1);
		// headerMap.put("====ကြောင်းတူသံကွဲများ====", 1 | (1 << 8) | (0 <<
		// 16));
		// headerMap.put("====ဆက်စပ်အသုံးများ====", 1 | (1 << 8) | (1 << 16));
		// headerMap.put("====ဆန့်ကျင်ဘက်စကားလုံးများ====", 1 | (1 << 8)
		// | (2 << 16));
		// headerMap.put("====အရေအတွက်====", 1 | (1 << 8) | (3 << 16));
		// headerMap.put("====အင်္ဂလိပ်====", 1 | (1 << 8) | (5 << 16));
		headerMap.put("{{=en=}}", 1);
		headerMap.put("{{=my=}}", 1);
		headerMap.put("အင်္ဂလိပ်", 1);
		headerMap.put("အသံထ", 2);
		headerMap.put("ရင်းမြစ်", 3);
		headerMap.put("မူလ", 3);
		headerMap.put("မူရင်း", 3);
		headerMap.put("နာမ", 4);
		headerMap.put("ကြိယာ", 4);
		headerMap.put("အီဒီယမ်", 4);
		headerMap.put("ကြောင်းတူ", 5);
		headerMap.put("ကြောင်း​တူ", 5);
		headerMap.put("ဆန့်ကျင်", 6);
		headerMap.put("ဆက်စပ်", 7);
		headerMap.put("အရေအတွက်", 8);
		headerMap.put("ဘာသာပြန်", 9);
		headerMap.put("ကိုးကား", 10);
	}

	public EntriesExtractor(InputStream in, OutputStream os,
			OutputStream os2) {
		this.in = in;
		this.out = new OutputStreamWriter(os);
		this.out2 = new OutputStreamWriter(os2);

		tagsMap.put("title", 0);
		tagsMap.put("ns", 1);
		tagsMap.put("page_id", 2);
		tagsMap.put("redirect", 3);
		tagsMap.put("restrictions", 4);
		tagsMap.put("revision_id", 5);
		tagsMap.put("parentid", 6);
		tagsMap.put("timestamp", 7);
		tagsMap.put("contributor_id", 8);
		tagsMap.put("ip", 9);
		tagsMap.put("username", 10);
		tagsMap.put("minor", 11);
		tagsMap.put("comment", 12);
		tagsMap.put("model", 13);
		tagsMap.put("format", 14);
		tagsMap.put("text", 15);
		tagsMap.put("sha1", 16);
	}

	@Override
	public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
		if (lockPage) {
			// System.out.println("C:" + String.valueOf(arg0.length) + ":" +
			// arg1
			// + ":" + arg2 + ":" + String.valueOf(arg0, arg1, arg2));
			chars.append(arg0, arg1, arg2);
			for (int i = 0; i < arg2; i++)
				switch (arg0[i]) {
				case '\n':
					break;
				}
		}
		super.characters(arg0, arg1, arg2);
	}

	@Override
	public void startDocument() throws SAXException {
		System.out.println("START");
		try {
			out2.append("\n" + escapeStringForCSV( entryMyan[0]));
			for (int j = 1; j < entryMyan.length; j++)
				out2.append("," + escapeStringForCSV(entryMyan[j]));
			out2.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.startDocument();
	}

	@Override
	public void startElement(String arg0, String arg1, String arg2,
			Attributes arg3) throws SAXException {
		// System.out.println("sE:" + arg0 + ":" + arg1 + ":" + arg2);
		chars.setLength(0);
		if (arg2.equals(matchStr)) {
			lockPage = true;
			if (++pageCount > countLimit)
				try {
					this.in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			Arrays.fill(str, "");
		} else if (lockPage) {
			if (arg2.equals("redirect")) {
				chars.append(arg3.getValue("title"));
			}
			itemCount++;
		}
		lastStr = arg2;
		tagStack.add(arg2);
		super.startElement(arg0, arg1, arg2, arg3);
	}

	@Override
	public void endDocument() throws SAXException {
		System.out.println("END");
		System.out
				.println("# " + "loop-headers" + ";" + "frequency" + ";"
						+ "max-occurrence-count" + ";"
						+ "childs and their frequencies");
		for (Map.Entry<String, Integer> e : collected.entrySet())
			System.out
					.println(e.getKey() + ";" + e.getValue() + ";"
							+ collected2.get(e.getKey()) + ";"
							+ childs.get(e.getKey()));
		System.out.println("# " + "pattern" + ";" + "frequency");
		for (Entry<String, Integer> e : hierarchyPattern.entrySet())
			System.out.println(e.getKey() + ";" + e.getValue() + ";"
					+ collector.get(e.getKey()));
		super.endDocument();
	}

	@Override
	public void endElement(String arg0, String arg1, String arg2)
			throws SAXException {
		String txt = "";
		if (arg2.equals(matchStr)) {
			lockPage = false;
			chars.setLength(0);
			for (String s : str)
				chars.append(s + ",");
			chars.append(itemCount);
			// chars.setLength(chars.length()-1);//remove last "," char
			txt = chars.toString();
			// if (itemCount != itemCountLast) {
			// itemCountLast = itemCount;
			// txt += "<diffLength/>";
			// }
			txt += "\n";
			itemCount = 0;
		} else if (lockPage) {
			if (arg2.equals(lastStr)) {

				if (arg2.equals("text")) {
					Map<String, Integer> allHeaders = new TreeMap<String, Integer>();
					Map<String, Integer> loopingHeaders = new TreeMap<String, Integer>();
					Map<String, List<String>> _childs = new TreeMap<String, List<String>>();
					Vector<String> stack = new Vector<String>();
					StringBuilder pattern = new StringBuilder();
					int level = 0;
					int headerIndex = entryMyan.length-1;// extra index
					char flowDir;
					BufferedReader r = new BufferedReader(new StringReader(
							chars.toString()));
					chars.setLength(0);
					Arrays.fill(entryMyan, "");
					entryMyan[0]=str[0];//revision id or word id
					try {
						String s;
						StringBuilder sectionStr = new StringBuilder();
						boolean foundSection=false;
						while ((s = r.readLine()) != null) {
							int _headerIndex = entryMyan.length-1;// extra index
							if (s.startsWith("==") && s.endsWith("==")) {
								foundSection=true;
								char _flowDir;
								// find current header-index
								for (Entry<String, Integer> e : headerMap
										.entrySet()) {
									if (s.contains(e.getKey())) {
										_headerIndex = e.getValue();
										break;
									}
								}
								// find current level
								int _level, len = s.length();
								for (_level = 0; _level < len; _level++)
									if (s.charAt(_level) == '=')
										;
									else
										break;
								if (_level == len)
									_level /= 2;
								if (_headerIndex// current header-index
										== 1
										// fix header-index for 'language'
										// section
										// below 'translation' section
										&& headerIndex == 9) {// previous
																// header-index
									// merge 'language' section(current)
									// into
									// 'translation' section(previous)
									_headerIndex = 9;
									sectionStr.append(s);
								} else {
									// already filled section
									// so flush previous entry & move on to next
									// entry
									if (entryMyan[_headerIndex].length() > 0) {
										out2.append("\n"
												+ escapeStringForCSV(entryMyan[0]));
										for (int i = 1; i < entryMyan.length; i++)
											out2.append(","
													+ escapeStringForCSV(entryMyan[i]));
										out2.flush();
										Arrays.fill(entryMyan, "");
										entryMyan[0] = str[0];// revision id or
																// word id
									}
									// record previous section
									entryMyan[headerIndex] = sectionStr
											.toString();
									sectionStr.setLength(0);
									if (_headerIndex == 1||_headerIndex == 4) {
										sectionStr.append(s.substring(_level,
												s.length() - _level)+"\n");
									}
								}
								if(false)
								switch (_headerIndex) {// current header-index
								case 1:
									// fix header-index for 'language' section
									// below 'translation' section
									switch (headerIndex) {// previous
															// header-index
									case 9:// merge 'language' section(current)
											// into
											// 'translation' section(previous)
										_headerIndex = 9;
										sectionStr.append(s);
										break;
									default:
										sectionStr.append(s.substring(_level,
												s.length() - _level));
									}
									break;
								default:
									// already filled section
									// so flush previous entry & move on to next
									// entry
									if (entryMyan[_headerIndex].length() > 0) {
										out2.append("\n" + escapeStringForCSV(entryMyan[0]));
										for (int i = 1; i < entryMyan.length; i++)
											out2.append("," +escapeStringForCSV(entryMyan[i]));
										out2.flush();
										Arrays.fill(entryMyan, "");
										entryMyan[0]=str[0];//revision id or word id
									}
									// record previous section
									entryMyan[headerIndex] = sectionStr
											.toString();
									sectionStr.setLength(0);
								}

								// next header
								if (level == _level) {
									if (stack.size() > 1) {
										String parent = stack
												.get(stack.size() - 2);
										List<String> c = _childs.get(parent);
										c.add(s);
										// record child
										{
											// child frequency
											TreeMap<String, Integer> e = childs
													.get(parent);
											if (e == null) {
												e = new TreeMap<>();
												childs.put(parent, e);
											}
											Integer i = e.get(s);
											if (i == null)
												i = 0;
											e.put(s, i + 1);
											// hierarchy patterns
											String _pattern = stack.toString();
											i = hierarchy.get(_pattern);
											if (i == null)
												i = 0;
											hierarchy.put(_pattern, i + 1);
										}
									}
									if (stack.size() > 0)
										stack.set(stack.size() - 1, s);// next
									// flow-pattern:Next
									_flowDir = 'n';
									// if (pattern.charAt(pattern.length() - 1)
									// != 'n')
									pattern.append(String.valueOf(_level) + 'n');

								} else if (level < _level) {// sub-header
									if (level > 0 && _level - level > 1) {
										// _level=level+1;
										System.err.println("X:"
												+ str[0]
												+ ":"
												+ ((stack.size() > 0) ? stack
														.lastElement() : null)
												+ "-->" + s);
									}
									if (stack.size() > 0) {
										String parent = stack.lastElement();
										List<String> c = _childs.get(parent);
										if (c == null) {
											c = new ArrayList<String>();
											_childs.put(parent, c);
										} else {
											c.clear();
										}
										c.add(s);

										// record child
										{
											// child frequency
											TreeMap<String, Integer> e = childs
													.get(parent);
											if (e == null) {
												e = new TreeMap<>();
												childs.put(parent, e);
											}
											Integer i = e.get(s);
											if (i == null)
												i = 0;
											e.put(s, i + 1);
											// hierarchy patterns
											String _pattern = stack.toString();
											i = hierarchy.get(_pattern);
											if (i == null)
												i = 0;
											hierarchy.put(_pattern, i + 1);
										}

										// increment
										Integer i = loopingHeaders.get(parent);
										if (i == null)
											i = 0;
										loopingHeaders.put(parent, i + 1);
									}
									stack.add(s);// down
									// flow-pattern:Down
									_flowDir = 'd';
									// if (pattern.length() == 0
									// || pattern
									// .charAt(pattern.length() - 1) != 'd')
									pattern.append(String.valueOf(_level) + 'd');
								} else {// parent header
									// String parent = stack.get(stack.size() -
									// 2);
									int lastLev, l;
									do {
										String last = stack.size() < 1 ? null
												: stack.lastElement();
										l = (last == null) ? 0 : last.length();
										for (lastLev = 0; lastLev < l; lastLev++)
											if (last.charAt(lastLev) == '=')
												;
											else
												break;
										if (lastLev == l)
											lastLev /= 2;
										if (lastLev == _level)
											break;
										if (lastLev < _level) {
											System.err.println("X:" + str[0]
													+ ":" + s + "<--" + last);
											break;
										}
										try {
											stack.remove(stack.size() - 1);// up
										} catch (Exception ex) {
											ex.printStackTrace();
										}
										if (stack.size() > 0) {
											String parent = stack.lastElement();
											for (String c : _childs.get(parent)) {
												// not-carried over
												loopingHeaders.remove(c);
												// record children collectively
												// TreeMap<String, Integer> e =
												// childs
												// .get(parent);
												// if (e == null) {
												// e = new TreeMap<>();
												// childs.put(parent, e);
												// }
												// Integer i = e.get(c);
												// if (i == null)
												// i = 0;
												// e.put(c, i + 1);
											}
										}
									} while (lastLev > _level);
									if (stack.size() > 1) {
										List<String> c = _childs.get(stack
												.get(stack.size() - 2));
										c.add(s);
									}
									if (stack.size() > 0)
										stack.set(stack.size() - 1, s);// up
									else
										stack.add(s);
									// flow-pattern:Up
									_flowDir = 'u';
									// if (pattern.charAt(pattern.length() - 1)
									// != 'u')
									pattern.append(String.valueOf(_level) + 'u');
								}
								// increment
								Integer i = allHeaders.get(s);
								if (i == null)
									i = 0;
								allHeaders.put(s, i + 1);

								level = _level;
								flowDir = _flowDir;
								headerIndex = _headerIndex;
							} else {// content-line of each section
								// record entry
								sectionStr.append(s + "&#10;");
							}
						}// end of all lines of 'text'

						// write entry
						if(foundSection)
						{
							out2.append("\n" + escapeStringForCSV( entryMyan[0]));
							for (int j = 1; j < entryMyan.length; j++)
								out2.append("," + escapeStringForCSV(entryMyan[j]));
							out2.flush();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					// increment:pattern
					Integer i = hierarchyPattern.get(pattern.toString());
					if (i == null)
						i = 0;
					hierarchyPattern.put(pattern.toString(), i + 1);
					collector.put(pattern.toString(), allHeaders.toString());
					for (Entry<String, Integer> e : loopingHeaders.entrySet()) {
						if (e.getValue() > 0) {
							i = collected.get(e.getKey());
							if (i == null)
								i = 0;
							collected.put(e.getKey(), i + e.getValue());
							i = collected2.get(e.getKey());
							if (i == null)
								i = 0;
							collected2.put(e.getKey(),
									Math.max(e.getValue(), i));
						}
					}
				}// end of 'text' tag

				Integer i = tagsMap.get(arg2);
				if (i == null)
					i = tagsMap.get(tagStack.get(tagStack.size() - 2) + "_"
							+ arg2);

				lastTagIndex = i.intValue();
				str[lastTagIndex] =escapeStringForCSV( chars.toString());
			}
			chars.setLength(0);
		}

		try {
			out.append(txt);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		tagStack.remove(arg2);
		// System.out.println("eE:" + arg0 + ":" + arg1 + ":" + arg2);
		super.endElement(arg0, arg1, arg2);
	}

	private String escapeStringForCSV(String str) {
		return str.replaceAll("\n", "&#10;")
				.replaceAll("\r","&#11;")
				.replaceAll(",", "&#44;");
	}

	public static void extract() {
		try {
			extract(new FileInputStream(srcFilepath), new FileOutputStream(
					srcFilepath + ".out", false), new FileOutputStream(outEntriesFilepath, false));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void extract(InputStream in, OutputStream out,
			OutputStream out2) {
		SAXParserFactory f = SAXParserFactory.newInstance();
		try {
			SAXParser parser = f.newSAXParser();
			XMLReader r = parser.getXMLReader();
			DefaultHandler h = new EntriesExtractor(in, out, out2);
			parser.parse(in, h);
			out.flush();
			out.close();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
