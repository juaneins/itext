/*
 * $Id$
 * $Name$
 *
 * Copyright 2005 by Bruno Lowagie.
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is 'iText, a free JAVA-PDF library'.
 *
 * The Initial Developer of the Original Code is Bruno Lowagie. Portions created by
 * the Initial Developer are Copyright (C) 1999, 2000, 2001, 2002 by Bruno Lowagie.
 * All Rights Reserved.
 * Co-Developer of the code is Paulo Soares. Portions created by the Co-Developer
 * are Copyright (C) 2000, 2001, 2002 by Paulo Soares. All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 *
 * Alternatively, the contents of this file may be used under the terms of the
 * LGPL license (the "GNU LIBRARY GENERAL PUBLIC LICENSE"), in which case the
 * provisions of LGPL are applicable instead of those above.  If you wish to
 * allow use of your version of this file only under the terms of the LGPL
 * License and not to allow others to use your version of this file under
 * the MPL, indicate your decision by deleting the provisions above and
 * replace them with the notice and other provisions required by the LGPL.
 * If you do not delete the provisions above, a recipient may use your version
 * of this file under either the MPL or the GNU LIBRARY GENERAL PUBLIC LICENSE.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the MPL as stated above or under the terms of the GNU
 * Library General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library general Public License for more
 * details.
 *
 * If you didn't download this code from the following link, you should check if
 * you aren't using an obsolete version:
 * http://www.lowagie.com/iText/
 */
package com.lowagie.tools.plugins;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import com.lowagie.text.Anchor;
import com.lowagie.text.Chapter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Header;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Section;
import com.lowagie.text.html.HtmlTags;
import com.lowagie.text.html.HtmlWriter;
import com.lowagie.text.markup.MarkupTags;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.SimpleBookmark;
import com.lowagie.tools.Executable;
import com.lowagie.tools.arguments.FileArgument;
import com.lowagie.tools.arguments.PdfFilter;
import com.lowagie.tools.arguments.ToolArgument;

/**
 * Allows you to encrypt an existing PDF file.
 */
public class HtmlBookmarks extends AbstractTool {
	
	static {
		addVersion("$Id$");
	}
	
	/**
	 * Constructs an Encrypt object.
	 */
	public HtmlBookmarks() {
		arguments.add(new FileArgument(this, "srcfile", "The file you want to inspect", false, new PdfFilter()));
		arguments.add(new ToolArgument(this, "ownerpassword", "The owner password if the file is encrypt", String.class.getName()));
		arguments.add(new ToolArgument(this, "css", "The path to a CSS file", String.class.getName()));
	}

	/**
	 * @see com.lowagie.tools.plugins.AbstractTool#createFrame()
	 */
	protected void createFrame() {
		internalFrame = new JInternalFrame("Html Bookmarks", true, true, true);
		internalFrame.setSize(500, 300);
		internalFrame.setJMenuBar(getMenubar());
		internalFrame.getContentPane().add(getConsole(40, 30));
	}
	
	/**
	 * @see com.lowagie.tools.plugins.AbstractTool#execute()
	 */
	public void execute() {
		try {
			if (getValue("srcfile") == null) throw new InstantiationException("You need to choose a sourcefile");
			File src = (File)getValue("srcfile");
			PdfReader reader;
			if (getValue("ownerpassword") == null) {
				reader = new PdfReader(src.getAbsolutePath());
			}
			else {
				reader = new PdfReader(src.getAbsolutePath(), ((String)getValue("ownerpassword")).getBytes());
			}
            File directory = src.getParentFile();
            String name = src.getName();
            name = name.substring(0, name.lastIndexOf("."));
            File html = new File(directory, name + "_index.html");
			Document document = new Document();
			HtmlWriter.getInstance(document, new FileOutputStream(html));
			Object css = getValue("css");
			if (css != null) {
				document.add(new Header(HtmlTags.STYLESHEET, css.toString()));
			}
			Object title = reader.getInfo().get("Title");
			if (title == null)
				document.addTitle("Index for " + src.getName());
			else
				document.addKeywords("Index for '" + title + "'");
			Object keywords = reader.getInfo().get("Keywords");
			if (keywords != null)
				document.addKeywords((String)keywords);
			Object description = reader.getInfo().get("Subject");
			if (keywords != null)
				document.addSubject((String)description);
			document.open();
			Paragraph t;
			if (title == null)
				t = new Paragraph("Index for " + src.getName());
			else
				t = new Paragraph("Index for '" + title + "'");
			t.setMarkupAttribute(MarkupTags.HTML_ATTR_CSS_CLASS, "title");
			document.add(t);
			if (description != null) {
				Paragraph d = new Paragraph((String) description);
				d.setMarkupAttribute(MarkupTags.HTML_ATTR_CSS_CLASS, "description");
				document.add(d);
			}
			List list = SimpleBookmark.getBookmark(reader);
			if (list == null) {
				document.add(new Paragraph("This document has no bookmarks."));
			}
			else {
				HashMap c;
				for (Iterator i = list.iterator(); i.hasNext(); ) {
					c = (HashMap) i.next(); 
					Chapter chapter = (Chapter)createBookmark(src.getName(), null, c);
					ArrayList kids = (ArrayList) c.get("Kids");
					if (kids != null) {
						for (Iterator k = kids.iterator(); k.hasNext(); ) {
							addBookmark(src.getName(), chapter, (HashMap)k.next());
						}
					}
					document.add(chapter);
				}
			}
			document.close();
			Executable.launchBrowser(html.getAbsolutePath());
		}
		catch(Exception e) {
			e.printStackTrace();
        	JOptionPane.showMessageDialog(internalFrame,
        		    e.getMessage(),
        		    e.getClass().getName(),
        		    JOptionPane.ERROR_MESSAGE);
            System.err.println(e.getMessage());
		}
	}
	
	/**
	 * Recursive method to write Bookmark titles to the System.out. 
	 * @param pdf the path to the PDF file
	 * @param section the section to which the bookmarks should be added
	 * @param bookmark a HashMap containing a Bookmark (and possible kids)
	 */
	private static void addBookmark(String pdf, Section section, HashMap bookmark) {
		Section s = createBookmark(pdf, section, bookmark);
		ArrayList kids = (ArrayList) bookmark.get("Kids");
		if (kids == null) return;
		for (Iterator i = kids.iterator(); i.hasNext(); ) {
			addBookmark(pdf, s, (HashMap)i.next());
		}
	}
	
	/**
	 * Adds a line with the title and an anchor.
	 * @param pdf the link to the PDF file
	 * @param section the section that gets the line
	 * @param bookmark the bookmark that has the data for the line
	 * @return a subsection of section
	 */
	private static Section createBookmark(String pdf, Section section, HashMap bookmark) {
		Section s;
		Paragraph title = new Paragraph((String)bookmark.get("Title"));
		System.out.println((String)bookmark.get("Title"));
		String action = (String)bookmark.get("Action");
		if ("GoTo".equals(action)) {
			if (bookmark.get("Page") != null) {
				String page = (String)bookmark.get("Page");
				StringTokenizer tokens = new StringTokenizer(page);
				String token = tokens.nextToken();
				Anchor anchor = new Anchor(" page" + token);
				anchor.setReference(pdf + "#page=" + token);
				title.add(anchor);
			}
		}
		else if ("URI".equals(action)) {
			String url = (String)bookmark.get("URI");
			Anchor anchor = new Anchor(" Goto URL");
			anchor.setReference(url);
			title.add(anchor);
		}
		else if ("GoToR".equals(action)) {
			String remote = (String)bookmark.get("File");
			Anchor anchor = new Anchor(" goto " + remote);
			if (bookmark.get("Named") != null) {
				String named = (String)bookmark.get("Named");
				remote = remote + "#nameddest=" + named;
			}
			else if (bookmark.get("Page") != null) {
				String page = (String)bookmark.get("Page");
				StringTokenizer tokens = new StringTokenizer(page);
				String token = tokens.nextToken();
				anchor.add(new Chunk(" page " + token));
				remote = remote + "#page=" + token;
			}
			anchor.setReference(remote);
			title.add(anchor);
		}
		if (section == null) {
			s = new Chapter(title, 0);
		}
		else {
			s = section.addSection(title);
		}
		s.setNumberDepth(0);
		return s;
	}

	/**
	 * @see com.lowagie.tools.plugins.AbstractTool#valueHasChanged(com.lowagie.tools.arguments.ToolArgument)
	 */
	public void valueHasChanged(ToolArgument arg) {
		if (internalFrame == null) {
			// if the internal frame is null, the tool was called from the commandline
			return;
		}
		// represent the changes of the argument in the internal frame
	}
	
    /**
     * Encrypts an existing PDF file.
     * @param args
     */
    public static void main(String[] args) {
    	HtmlBookmarks tool = new HtmlBookmarks();
    	if (args.length < 1) {
    		System.err.println(tool.getUsage());
    	}
    	tool.setArguments(args);
        tool.execute();
    }

	/**
	 * @see com.lowagie.tools.plugins.AbstractTool#getDestPathPDF()
	 */
	protected File getDestPathPDF() throws InstantiationException {
		throw new InstantiationException("There is no file to show.");
	}

}