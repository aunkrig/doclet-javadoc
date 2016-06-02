
/*
 * de.unkrig.doclet.javadoc - A reimplementation of the JAVADOC utility for experimental purposes
 *
 * Copyright (c) 2015, Arno Unkrig
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *       following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *       following disclaimer in the documentation and/or other materials provided with the distribution.
 *    3. The name of the author may not be used to endorse or promote products derived from this software without
 *       specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package de.unkrig.doclet.javadoc.templates.global;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.RootDoc;

import de.unkrig.doclet.javadoc.templates.JavadocUtil;
import de.unkrig.notemplate.javadocish.Options;
import de.unkrig.notemplate.javadocish.templates.AbstractRightFrameHtml;

/**
 * Renders the single-page index, "./index-all.html".
 */
public
class IndexAllHtml extends AbstractRightFrameHtml implements GlobalDocument {

    @Override public void
    render(
        Options                   options,
        SortedSet<PackageDoc>     allPackages,
        final SortedSet<ClassDoc> allClassesAndInterfaces,
        final RootDoc             rootDoc
    ) {

        super.rRightFrameHtml(
            "Index",                             // windowTitle
            options,                             // options
            new String[] { "./stylesheet.css" }, // stylesheetLinks
            new String[] {                       // nav1
                "Overview",   "./overview-summary.html",
                "Package",    AbstractRightFrameHtml.DISABLED,
                "Class",      AbstractRightFrameHtml.DISABLED,
                "Tree",       "./overview-tree.html",
                "Deprecated", "./deprecated-list.html",
                "Index",      AbstractRightFrameHtml.HIGHLIT,
                "Help",       "./help-doc.html",
            },
            new String[] {                       // nav2
                "Prev",
                "Next",
            },
            new String[] {                       // nav3
                "Frames",    "./index.html?index-all.html",
                "No Frames", "index-all.html",
            },
            new String[] {                       // nav4
                "All Classes", "./allclasses-noframe.html",
            },
            null,                                // nav5
            null,                                // nav6
            () -> {                              // renderBody
                IndexAllHtml.this.rBody(allPackages, allClassesAndInterfaces, rootDoc);
            }
        );
    }

    private void
    rBody(SortedSet<PackageDoc> allPackages, SortedSet<ClassDoc> allClassesAndInterfaces, RootDoc rootDoc) {

        Collection<Doc> allDocs = new ArrayList<Doc>();

        for (PackageDoc pd : allPackages) {
            allDocs.add(pd);
        }

        for (ClassDoc cd : allClassesAndInterfaces) {
            for (MethodDoc md : cd.methods()) {
                allDocs.add(md);
            }
        }

        SortedMap<Character, SortedSet<Doc>> allDocsByInitial = new TreeMap<Character, SortedSet<Doc>>();
        for (Doc d : allDocs) {

            Character initial = Character.toUpperCase(d.name().charAt(0));

            SortedSet<Doc> docsOfInitial = allDocsByInitial.get(initial);
            if (docsOfInitial == null) {
                docsOfInitial = new TreeSet<Doc>();
                allDocsByInitial.put(initial, docsOfInitial);
            }

            docsOfInitial.add(d);
        }

        this.p("<div class=\"contentContainer\">");

        // Top list of initials.
        for (Character initial : allDocsByInitial.keySet()) {
            this.p("<a href=\"#_" + initial + "_\">" + initial + "</a>&nbsp;");
        }

        // Docs, grouped by initial.
        for (Entry<Character, SortedSet<Doc>> e : allDocsByInitial.entrySet()) {
            Character      initial       = e.getKey();
            SortedSet<Doc> docsOfInitial = e.getValue();

            this.l(
                "<a name=\"_" + initial + "_\">",
                "<!--   -->",
                "</a>",
                "<h2 class=\"title\">" + initial + "</h2>",
                "<dl>"
            );

            for (Doc doc : docsOfInitial) {
                this.l(
"<dt><span class=\"strong\">" + JavadocUtil.toHtml(rootDoc, doc, true, null, null, rootDoc) + "</span></dt>"
                );
                String fsod = JavadocUtil.firstSentenceOfDescription(
                    doc instanceof MethodDoc ? ((ProgramElementDoc) doc).containingClass() : doc,  // from
                    doc,                                                                           // to
                    rootDoc
                );
                if (fsod.isEmpty()) {
                    this.l(
"<dd>&nbsp;</dd>"
                    );
                } else {
                    this.l(
"<dd>",
"<div class=\"block\">" + fsod + "</div>",
"</dd>"
                    );
                }
            }
            this.l(
"</dl>"
            );
        }

        // Bottom list of initials.
        for (Character initial : allDocsByInitial.keySet()) {
            this.p("<a href=\"#_" + initial + "_\">" + initial + "</a>&nbsp;");
        }


        this.l(
"</div>"
        );
    }
}
