package com.ade.labnetai.utils;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.ai.document.Document;

/**
 *  This particular class is used to compare the document using the rules that we defined.
 */
public class DocumentComparator implements Comparator<Document> {
	private static final Pattern SPLITPATTERN = Pattern.compile("^(.*)_([a-zA-Z]+)_(\\d+)$");
	
	@Override
	public int compare(Document document1, Document document2) {
		String id1 = document1.getId();
		String id2 = document2.getId();
		
		Matcher matcher1 = SPLITPATTERN.matcher(id1);
		Matcher matcher2 = SPLITPATTERN.matcher(id2);
		
		if (matcher1.matches() && matcher2.matches()) {
		    String prefix1 = matcher1.group(1);
		    String prefix2 = matcher2.group(1);

		    int prefixCompare = prefix1.compareTo(prefix2);
		    if (prefixCompare != 0) return prefixCompare;

		    int seg1 = Integer.parseInt(matcher1.group(3));
		    int seg2 = Integer.parseInt(matcher2.group(3));

		    return Integer.compare(seg1, seg2);
		}
		return id1.compareTo(id2);
	}
}