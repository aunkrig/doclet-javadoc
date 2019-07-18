
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
 *    3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote
 *       products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package de.unkrig.doclet.javadoc.templates.include;

import java.util.Collection;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.TypeVariable;

import de.unkrig.commons.doclet.Docs;
import de.unkrig.commons.nullanalysis.Nullable;
import de.unkrig.doclet.javadoc.templates.JavadocUtil;
import de.unkrig.notemplate.NoTemplate;

/**
 * Renders the tree part of the "Hierarchy For All Packages" page ("./overview-tree.html") and the "Hierarchy For
 * Package my.package" pages ("./my/package/package-tree.html").
 */
public
class HierarchiesHtml extends NoTemplate {

    /**
     * Renders the tree part of the "Hierarchy For All Packages" page ("./overview-tree.html") and the "Hierarchy For
     * Package my.package" pages ("./my/package/package-tree.html").
     */
    public void
    render(String home, Collection<ClassDoc> classes, Collection<ClassDoc> interfaces) {

        // Class hierarchy (key "null" maps to "[ Object ]".
        SortedMap<ClassDoc, SortedSet<ClassDoc>>
        subclasses = new TreeMap<ClassDoc, SortedSet<ClassDoc>>(Docs.DOCS_BY_NAME_COMPARATOR);
        for (ClassDoc c : classes) {
            for (;;) {
                ClassDoc            sc  = c.superclass();
                SortedSet<ClassDoc> scs = subclasses.get(sc);
                if (scs == null) {
                    scs = new TreeSet<ClassDoc>(Docs.DOCS_BY_NAME_COMPARATOR);
                    subclasses.put(sc, scs);
                }
                if (scs.contains(c)) break;
                scs.add(c);
                if (sc == null) break;

                c = sc;
            }
        }
        if (!subclasses.isEmpty()) {
            this.p("<h2 title=\"Class Hierarchy\">Class Hierarchy</h2>");
            this.pTree(home, null, subclasses);
        }

        // Interface hierarchy.
        SortedMap<ClassDoc, SortedSet<ClassDoc>>
        subinterfaces = new TreeMap<ClassDoc, SortedSet<ClassDoc>>(Docs.DOCS_BY_NAME_COMPARATOR);

        for (ClassDoc i : interfaces) {
            this.addInterface(i, subinterfaces);
        }
        if (!subinterfaces.isEmpty()) {
            this.p("<h2 title=\"Interface Hierarchy\">Interface Hierarchy</h2>");
            this.pTree(home, null, subinterfaces);
        }
    }

    private void
    addInterface(
        ClassDoc                                 interfacE,
        SortedMap<ClassDoc, SortedSet<ClassDoc>> subinterfaces
    ) {

        ClassDoc[] extendedInterfaces = interfacE.interfaces();

        if (extendedInterfaces.length == 0) extendedInterfaces = new ClassDoc[] { null };

        for (ClassDoc ei : extendedInterfaces) {
            SortedSet<ClassDoc> sis = subinterfaces.get(ei);
            if (sis == null) {
                sis = new TreeSet<ClassDoc>(Docs.DOCS_BY_NAME_COMPARATOR);
                subinterfaces.put(ei, sis);
            }
            if (sis.contains(interfacE)) continue;
            sis.add(interfacE);
            if (ei != null) this.addInterface(ei, subinterfaces);
        }
    }

    private void
    pTree(String home, @Nullable ClassDoc supertype, SortedMap<ClassDoc, SortedSet<ClassDoc>> subtypesOf) {

        SortedSet<ClassDoc> subtypes = subtypesOf.get(supertype);
        if (subtypes == null) return;

        this.l();
        this.l("<ul>");
        for (ClassDoc subtype : subtypes) {
            this.p("<li type=\"circle\">");
            this.pType(home, subtype, supertype, true, true);

            this.pTree(home, subtype, subtypesOf);

            this.l("</li>");
        }
        this.l("</ul>");
    }

    /**
     * {@link JavadocUtil#toHtml(com.sun.javadoc.Type, com.sun.javadoc.Doc, String, int)} is not sufficient for
     * printing class/interface hierarchies.
     */
    private void
    pType(String home, ClassDoc type, @Nullable ClassDoc supertype, boolean strong, boolean withInterfaces) {

        if (type.isIncluded()) {
            this.p(
                type.containingPackage().name()
                + ".<a href=\""
                + home
                + type.containingPackage().name().replace('.', '/')
                + "/"
                + type.name()
                + ".html\" title=\""
                + JavadocUtil.category(type)
                + " in "
                + type.containingPackage().name()
                + "\">"
            );
            if (strong) this.p("<span class=\"strong\">");
            this.p(type.name());
            if (strong) this.p("</span>");
            this.p("</a>");
            if (type.typeParameters().length > 0) {
                this.p("&lt;");
                Once first = NoTemplate.once();
                for (TypeVariable tp : type.typeParameters()) {
                    if (!first.once()) this.p(",");
                    this.p(tp.typeName());
                }
                this.p("&gt;");
            }
        } else {
            this.p(type.qualifiedName());
        }

        if (withInterfaces) {
            Once first = NoTemplate.once();
            for (ClassDoc i : type.interfaces()) {
                if (i == supertype) continue;

                if (first.once()) {
                    this.p(type.isInterface() ? " (also extends " : " (implements ");
                } else {
                    this.p(", ");
                }
                this.pType(home, i, type, false, false);
            }
            if (!first.once()) this.p(")");
        }
    }
}
