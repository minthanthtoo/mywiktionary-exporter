package com.minthanthtoo.wordlists;

import com.minthanthtoo.wordlists.wiktionary.DbExporter;
import com.minthanthtoo.wordlists.wiktionary.EntriesExtractor;

public class Main {
	public static void main(String[] args) {
		long t1 = System.currentTimeMillis();
		EntriesExtractor.extract();
		DbExporter.export();
		long t2 = System.currentTimeMillis();
		System.out.println("TimeElapsed:" + (t2 - t1));
	}
}
