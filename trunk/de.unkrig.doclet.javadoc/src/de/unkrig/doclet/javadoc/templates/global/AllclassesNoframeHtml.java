
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

package de.unkrig.doclet.javadoc.templates.global;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

import de.unkrig.commons.doclet.Docs;
import de.unkrig.commons.lang.AssertionUtil;
import de.unkrig.doclet.javadoc.templates.JavadocUtil;
import de.unkrig.notemplate.javadocish.Options;
import de.unkrig.notemplate.javadocish.templates.AbstractBottomLeftFrameHtml;

/**
 * Renders a list of ALL classes that appears in the "NO FRAMES" mode when the user hits "All Classes",
 * "./allclasses-noframe.html".
 */
public
class AllclassesNoframeHtml extends AbstractBottomLeftFrameHtml implements GlobalDocument {

    static { AssertionUtil.enableAssertionsForThisClass(); }

    /**
     * Renders the "all classes frame", i.e. the document that appears in the bottom left frame when NO package is
     * selected in the top left frame.
     */
    @Override public void
    render(
        Options               options,
        SortedSet<PackageDoc> allPackages,
        SortedSet<ClassDoc>   allClassesAndInterfaces,
        RootDoc               rootDoc
    ) {

        this.rBottomLeftFrameHtml(
            "All Classes",                     // windowTitle
            options,                           // options
            new String[] { "stylesheet.css" }, // styleSheetLinks
            "All Classes",                     // heading
            null,                              // headingLink
            null,                              // renderIndexHeader
            () -> {                            // renderIndexContainer
                this.l(
"      <ul>"
                );
                List<ClassDoc> cais = new ArrayList<ClassDoc>(allClassesAndInterfaces);
                Collections.sort(cais, Docs.DOCS_BY_NAME_COMPARATOR);
                for (ClassDoc coi : cais) {
                    this.l(
"        <li>" + JavadocUtil.toHtml(coi, null, "", 12, null) + "</li>"
                    );
                }
                this.l(
"      </ul>"
                );
            }
        );
    }
}
