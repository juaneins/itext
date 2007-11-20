/*
 * $Id$
 * $Name$
 *
 * Copyright 2001, 2002 by Mark Hall
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
 * LGPL license (the ?GNU LIBRARY GENERAL PUBLIC LICENSE?), in which case the
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

package com.lowagie.text.rtf.text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.lowagie.text.Chapter;
import com.lowagie.text.rtf.RtfBasicElement;
import com.lowagie.text.rtf.document.RtfDocument;


/**
 * The RtfChapter wraps a Chapter element.
 * INTERNAL CLASS
 * 
 * @version $Id$
 * @author Mark Hall (mhall@edu.uni-klu.ac.at)
 * @author Thomas Bickel (tmb99@inode.at)
 */
public class RtfChapter extends RtfSection {

    /**
     * Constructs a RtfChapter for a given Chapter
     * 
     * @param doc The RtfDocument this RtfChapter belongs to
     * @param chapter The Chapter this RtfChapter is based on
     */
    public RtfChapter(RtfDocument doc, Chapter chapter) {
        super(doc, chapter);
    }

    /**
     * Writes the RtfChapter and its contents
     * 
     * @return A byte array containing the RtfChapter and its contents 
     * @deprecated As of iText 2.0.6 or earlier, replaced by
     * {@link #writeContent(OutputStream)}, scheduled for removal at or after 2.1.0
     */
    public byte[] write() {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
        	writeContent(result);
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        return result.toByteArray();
    }
    
    /**
     * Writes the RtfChapter and its contents
     */    
    public void writeContent(final OutputStream result) throws IOException
    {
        if(this.document.getLastElementWritten() != null && !(this.document.getLastElementWritten() instanceof RtfChapter)) {
            result.write("\\page".getBytes());
        }
        result.write("\\sectd".getBytes());
        //.result.write(document.getDocumentHeader().writeSectionDefinition());
        document.getDocumentHeader().writeSectionDefinition(result);
        if(this.title != null) {
            //.result.write(this.title.write());
            this.title.writeContent(result);
        }
        for(int i = 0; i < items.size(); i++) {
        	RtfBasicElement rbe = (RtfBasicElement)items.get(i);
            //.result.write((rbe).write());
        	rbe.writeContent(result);
        }
        result.write("\\sect".getBytes());
    }        
    
}
