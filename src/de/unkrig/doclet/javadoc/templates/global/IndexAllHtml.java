
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
import com.sun.javadoc.RootDoc;

import de.unkrig.doclet.javadoc.templates.JavadocUtil;
import de.unkrig.notemplate.javadocish.Options;
import de.unkrig.notemplate.javadocish.templates.AbstractClassFrameHtml;

public
class IndexAllHtml extends AbstractClassFrameHtml implements GlobalDocument {

    @Override public void
    render(
        Options                   options,
        SortedSet<PackageDoc>     allPackages,
        final SortedSet<ClassDoc> allClassesAndInterfaces,
        final RootDoc             rootDoc
    ) {

        super.rClassFrameHtml(
            "Index",                   // title
            options,                   // options
            "stylesheet.css",          // stylesheetLink
            new String[] {             // nav1
                "Overview",   "overview-summary.html",
                "Package",    AbstractClassFrameHtml.DISABLED,
                "Class",      AbstractClassFrameHtml.DISABLED,
                "Tree",       "overview-tree.html",
                "Deprecated", "deprecated-list.html",
                "Index",      AbstractClassFrameHtml.HIGHLIT,
                "Help",       "help-doc.html",
            },
            new String[] {             // nav2
                "Prev", AbstractClassFrameHtml.DISABLED,
                "Next", AbstractClassFrameHtml.DISABLED,
            },
            new String[] {             // nav3
                "Frames",    "index.html?index-all.html",
                "No Frames", "index-all.html",
            },
            new String[] {             // nav4
                "All Classes", "allclasses-noframe.html",
            },
            null,                      // nav5
            null,                      // nav6
            new Runnable() {

                @Override
                public void
                run() {
                    IndexAllHtml.this.rBody(allClassesAndInterfaces, rootDoc);
                }
            }
        );
    }

    private void
    rBody(SortedSet<ClassDoc> allClassesAndInterfaces, RootDoc rootDoc) {

        Collection<Doc> allDocs = new ArrayList<Doc>();
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
"<dt>" + JavadocUtil.toHtml(rootDoc, doc, true, null, null, rootDoc) + "</dt>"
                );
                String fsod = JavadocUtil.firstSentenceOfDescription(doc, rootDoc);
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
//                "<dt><span class=\"strong\"><a href=\"./de/unkrig/commons/lang/protocol/ConsumerUtil.html#addToCollection(java.util.Collection)\">addToCollection(Collection&lt;T&gt;)</a></span> - Static method in class de.unkrig.commons.lang.protocol.<a href=\"./de/unkrig/commons/lang/protocol/ConsumerUtil.html\" title=\"class in de.unkrig.commons.lang.protocol\">ConsumerUtil</a></dt>",
//                "<dd>&nbsp;</dd>",
//                "<dt><span class=\"strong\"><a href=\"./de/unkrig/commons/lang/protocol/PredicateUtil.html#after(long)\">after(long)</a></span> - Static method in class de.unkrig.commons.lang.protocol.<a href=\"./de/unkrig/commons/lang/protocol/PredicateUtil.html\" title=\"class in de.unkrig.commons.lang.protocol\">PredicateUtil</a></dt>",
//                "<dd>",
//                "<div class=\"block\">Returns a <code>Predicate&lt;Object&gt;</code> that evaluates to <code>true</code> iff the current time is after the given",
//                " expiration time.</div>",
//                "</dd>",
//                "<dt><span class=\"strong\"><a href=\"./de/unkrig/commons/lang/protocol/ProducerUtil.html#alternate(T, T)\">alternate(T, T)</a></span> - Static method in class de.unkrig.commons.lang.protocol.<a href=\"./de/unkrig/commons/lang/protocol/ProducerUtil.html\" title=\"class in de.unkrig.commons.lang.protocol\">ProducerUtil</a></dt>",
//                "<dd>&nbsp;</dd>",
//                "<dt><span class=\"strong\"><a href=\"./de/unkrig/commons/lang/protocol/PredicateUtil.html#always()\">always()</a></span> - Static method in class de.unkrig.commons.lang.protocol.<a href=\"./de/unkrig/commons/lang/protocol/PredicateUtil.html\" title=\"class in de.unkrig.commons.lang.protocol\">PredicateUtil</a></dt>",
//                "<dd>&nbsp;</dd>",
//                "<dt><span class=\"strong\"><a href=\"./de/unkrig/commons/lang/protocol/PredicateUtil.html#and(de.unkrig.commons.lang.protocol.Predicate, de.unkrig.commons.lang.protocol.Predicate)\">and(Predicate&lt;? super T&gt;, Predicate&lt;? super T&gt;)</a></span> - Static method in class de.unkrig.commons.lang.protocol.<a href=\"./de/unkrig/commons/lang/protocol/PredicateUtil.html\" title=\"class in de.unkrig.commons.lang.protocol\">PredicateUtil</a></dt>",
//                "<dd>",
//                "<div class=\"block\">Returns a <a href=\"./de/unkrig/commons/lang/protocol/Predicate.html\" title=\"interface in de.unkrig.commons.lang.protocol\"><code>Predicate</code></a> which returns <code>true</code> iff both <code>p1</code> and <code>p2</code> return <code>true</code>",
//                " for any given <code>subject</code>.</div>",
//                "</dd>",
//                "<dt><span class=\"strong\"><a href=\"./de/unkrig/commons/lang/PrettyPrinter.html#ARRAY_ELLIPSIS\">ARRAY_ELLIPSIS</a></span> - Static variable in class de.unkrig.commons.lang.<a href=\"./de/unkrig/commons/lang/PrettyPrinter.html\" title=\"class in de.unkrig.commons.lang\">PrettyPrinter</a></dt>",
//                "<dd>",
//                "<div class=\"block\">If an array is larger than this threshold (10), then it is printed as",
//                " { <var>elem-0</var>, <var>elem-1</var>, <var>elem-2</var>, ... }</div>",
//                "</dd>",
            this.l(
"</dl>"
            );
        }

        // Top list of initials.
        for (Character initial : allDocsByInitial.keySet()) {
            this.p("<a href=\"#_" + initial + "_\">" + initial + "</a>&nbsp;");
        }


        this.l(
"</div>"
        );
    }
}
