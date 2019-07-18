
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

package de.unkrig.doclet.javadoc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.RootDoc;

import de.unkrig.commons.doclet.Docs;
import de.unkrig.commons.doclet.html.Html;
import de.unkrig.commons.lang.AssertionUtil;
import de.unkrig.commons.lang.StringUtil;
import de.unkrig.commons.lang.protocol.ConsumerWhichThrows;
import de.unkrig.commons.lang.protocol.Longjump;
import de.unkrig.commons.util.collections.ElementWithContext;
import de.unkrig.commons.util.collections.IterableUtil;
import de.unkrig.doclet.javadoc.templates.JavadocUtil;
import de.unkrig.doclet.javadoc.templates.clasS.ClassDetailHtml;
import de.unkrig.doclet.javadoc.templates.clasS.PerClassDocument;
import de.unkrig.doclet.javadoc.templates.global.AllclassesFrameHtml;
import de.unkrig.doclet.javadoc.templates.global.AllclassesNoframeHtml;
import de.unkrig.doclet.javadoc.templates.global.ConstantValuesHtml;
import de.unkrig.doclet.javadoc.templates.global.DeprecatedListHtml;
import de.unkrig.doclet.javadoc.templates.global.GlobalDocument;
import de.unkrig.doclet.javadoc.templates.global.HelpDocHtml;
import de.unkrig.doclet.javadoc.templates.global.IndexHtml;
import de.unkrig.doclet.javadoc.templates.global.OverviewFrameHtml;
import de.unkrig.doclet.javadoc.templates.global.OverviewSummaryHtml;
import de.unkrig.doclet.javadoc.templates.global.OverviewTreeHtml;
import de.unkrig.doclet.javadoc.templates.global.PackageList;
import de.unkrig.doclet.javadoc.templates.global.SerializedFormHtml;
import de.unkrig.doclet.javadoc.templates.global.StylesheetCss;
import de.unkrig.doclet.javadoc.templates.packagE.PackageDetailHtml;
import de.unkrig.doclet.javadoc.templates.packagE.PackageSummaryHtml;
import de.unkrig.doclet.javadoc.templates.packagE.PackageTreeHtml;
import de.unkrig.doclet.javadoc.templates.packagE.PerPackageDocument;
import de.unkrig.notemplate.NoTemplate;
import de.unkrig.notemplate.javadocish.IndexPages;
import de.unkrig.notemplate.javadocish.IndexPages.IndexEntry;
import de.unkrig.notemplate.javadocish.Options;
import de.unkrig.notemplate.javadocish.templates.AbstractRightFrameHtml;

/**
 * A doclet that generates documentation for Java packages, classes, and so forth.
 */
public final
class JavadocDoclet {

    static { AssertionUtil.enableAssertionsForThisClass(); }

    private final RootDoc               rootDoc;
    private final Options               options;
    private final SortedSet<PackageDoc> allPackages;
    private final SortedSet<ClassDoc>   allClassesAndInterfaces;

    /**
     * See <a href="https://docs.oracle.com/javase/6/docs/technotes/guides/javadoc/doclet/overview.html">"Doclet
     * Overview"</a>.
     */
    public static int
    optionLength(String option) {

        if ("-d".equals(option))           return 2;
        if ("-windowtitle".equals(option)) return 2;
        if ("-doctitle".equals(option))    return 2;
        if ("-header".equals(option))      return 2;
        if ("-footer".equals(option))      return 2;
        if ("-top".equals(option))         return 2;
        if ("-bottom".equals(option))      return 2;
        if ("-notimestamp".equals(option)) return 1;
        if ("-splitindex".equals(option))  return 1;

        // Options of the JAVADOC standard doclet that are NOT (yet) implemented by this doclet:
        //    -use
        //    -version
        //    -author
        //    -title title
        //    -link extdocURL
        //    -linkoffline extdocURL packagelistLoc
        //    -linksource
        //    -nodeprecated
        //    -nodeprecatedlist
        //    -nosince
        //    -notree
        //    -noindex
        //    -nohelp
        //    -nonavbar
        //    -helpfile file
        //    -stylesheetfile file
        //    -serialwarn
        //    -charset name
        //    -docencoding name
        //    -keywords
        //    -tag tagname:Xaoptcmf:"taghead"
        //    -taglet class
        //    -tagletpath tagletpathlist
        //    -docfilessubdirs
        //    -excludedocfilessubdir name1:name2...
        //    -noqualifier all | packagename1:packagename2:...
        //    -nocomment
        //    -sourcetab tabLength
        // See http://docs.oracle.com/javase/7/docs/technotes/tools/windows/javadoc.html#standard

        return 0;
    }

    public static LanguageVersion languageVersion() { return LanguageVersion.JAVA_1_5; }

    /**
     * See <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/javadoc/doclet/overview.html">"Doclet
     * Overview"</a>.
     */
    public static boolean
    start(final RootDoc rootDoc) throws IOException {

        Options options = new Options();

        for (String[] option : rootDoc.options()) {
            if ("-d".equals(option[0]))           { options.destination = new File(option[1]); } else
            if ("-windowtitle".equals(option[0])) { options.windowTitle = option[1];           } else
            if ("-doctitle".equals(option[0]))    { options.docTitle    = option[1];           } else
            if ("-header".equals(option[0]))      { options.header      = option[1];           } else
            if ("-footer".equals(option[0]))      { options.footer      = option[1];           } else
            if ("-top".equals(option[0]))         { options.top         = option[1];           } else
            if ("-bottom".equals(option[0]))      { options.bottom      = option[1];           } else
            if ("-notimestamp".equals(option[0])) { options.noTimestamp = true;                } else
            if ("-splitindex".equals(option[0]))  { options.splitIndex  = true;                } else
            {

                // It is quite counterintuitive, but 'options()' returns ALL options, not only those which
                // qualified by 'optionLength()'.
                ;
            }
        }

        new JavadocDoclet(rootDoc, options).start2();

        return true;
    }

    public
    JavadocDoclet(RootDoc rootDoc, Options options) {
        this.rootDoc = rootDoc;
        this.options = options;

        this.allPackages             = new TreeSet<PackageDoc>(Docs.DOCS_BY_NAME_COMPARATOR);
        this.allClassesAndInterfaces = new TreeSet<ClassDoc>(Docs.DOCS_BY_NAME_COMPARATOR);

        // Process all specified classes.
        for (final ClassDoc cd : this.rootDoc.specifiedClasses()) {

            this.allClassesAndInterfaces.add(cd);
            this.allPackages.add(cd.containingPackage());
        }

        // Process all specified packages.
        for (final PackageDoc pd : this.rootDoc.specifiedPackages()) {

            for (final ClassDoc cd : pd.allClasses()) {
                this.allClassesAndInterfaces.add(cd);
            }
            this.allPackages.add(pd);
        }
    }

    private void
    start2() throws IOException {

        // "Global" documents.
        this.generateGlobalDocument(IndexHtml.class,             "index.html");
        this.generateGlobalDocument(OverviewFrameHtml.class,     "overview-frame.html");
        this.generateGlobalDocument(StylesheetCss.class,         "stylesheet.css");
        this.generateGlobalDocument(AllclassesFrameHtml.class,   "allclasses-frame.html");
        this.generateGlobalDocument(AllclassesNoframeHtml.class, "allclasses-noframe.html");
        this.generateGlobalDocument(ConstantValuesHtml.class,    "constant-values.html");
        this.generateGlobalDocument(OverviewSummaryHtml.class,   "overview-summary.html");
        this.generateGlobalDocument(OverviewTreeHtml.class,      "overview-tree.html");
        this.generateGlobalDocument(DeprecatedListHtml.class,    "deprecated-list.html");
        this.generateGlobalDocument(HelpDocHtml.class,           "help-doc.html");
        this.generateGlobalDocument(PackageList.class,           "package-list");
        this.generateGlobalDocument(SerializedFormHtml.class,    "serialized-form.html");

        for (ElementWithContext<PackageDoc> packagE : IterableUtil.iterableWithContext(this.allPackages)) {

            // Per-package documents.
            this.generatePerPackageDocument(packagE, PackageDetailHtml.class,  "package-frame.html");
            this.generatePerPackageDocument(packagE, PackageSummaryHtml.class, "package-summary.html");
            this.generatePerPackageDocument(packagE, PackageTreeHtml.class,    "package-tree.html");

            ClassDoc[] acs = packagE.current().allClasses();
            Arrays.sort(acs, Docs.DOCS_BY_NAME_COMPARATOR);
            for (ElementWithContext<ClassDoc> clasS : IterableUtil.iterableWithContext(Arrays.asList(acs))) {

                // Create per-class document.
                this.generatePerClassDocument(clasS, ClassDetailHtml.class);
            }
        }

        // Generate the (alphabetical) index document(s).
        {
            Collection<Doc> allDocs = new ArrayList<Doc>();

            for (PackageDoc pd : this.allPackages) {
                allDocs.add(pd);
            }

            for (ClassDoc cd : this.allClassesAndInterfaces) {
                for (MethodDoc md : cd.methods()) {
                    allDocs.add(md);
                }
            }

            Collection<IndexEntry> indexEntries = new ArrayList<>();

            for (Doc doc : allDocs) {
                indexEntries.add(new IndexEntry() {

                    @Override public String
                    getShortDescription() {
                        return JavadocUtil.firstSentenceOfDescription(
                            doc instanceof MethodDoc ? ((ProgramElementDoc) doc).containingClass() : doc,  // from
                            doc,                                                                           // to
                            JavadocDoclet.this.rootDoc
                        );
                    }

                    @Override public String
                    getLink() {
                        return AssertionUtil.notNull(Longjump.catchLongjump(() -> Html.STANDARD_LINK_MAKER.makeLink(
                            JavadocDoclet.this.rootDoc,
                            doc,
                            JavadocDoclet.this.rootDoc
                        ).href, "???"));
                    }

                    @Override public String
                    getKey() {
                        return AssertionUtil.notNull(Longjump.catchLongjump(() -> Html.STANDARD_LINK_MAKER.makeLink(
                            JavadocDoclet.this.rootDoc,
                            doc,
                            JavadocDoclet.this.rootDoc
                        ).defaultLabelHtml, "???"));
                    }

                    @Override public String
                    getExplanation() { return ""; }
                });
            }

            IndexPages.createIndex(
                this.options.destination, // outputFile
                indexEntries,             // indexEntries
                this.options,             // options
                new String[] {            // nav1
                    "Overview",   "./overview-summary.html",
                    "Package",    AbstractRightFrameHtml.DISABLED,
                    "Class",      AbstractRightFrameHtml.DISABLED,
                    "Tree",       "./overview-tree.html",
                    "Deprecated", "./deprecated-list.html",
                    "Index",      AbstractRightFrameHtml.HIGHLIT,
                    "Help",       "./help-doc.html",
                }
            );
        }
    }

    /**
     * Creates a "global" document.
     */
    private <C extends NoTemplate & GlobalDocument> void
    generateGlobalDocument(
        Class<C>      perPackageTemplateClass,
        String        fileName
    ) throws IOException {

        NoTemplate.render(
            perPackageTemplateClass,                                      // templateClass
            new File(this.options.destination, fileName),                 // out
            new ConsumerWhichThrows<GlobalDocument, RuntimeException>() { // renderer

                @Override public void
                consume(GlobalDocument gd) {

                    gd.render(
                        JavadocDoclet.this.options,
                        JavadocDoclet.this.allPackages,
                        JavadocDoclet.this.allClassesAndInterfaces,
                        JavadocDoclet.this.rootDoc
                    );
                }
            }
        );
    }

    /**
     * Creates a per-package document.
     */
    private <C extends NoTemplate & PerPackageDocument> void
    generatePerPackageDocument(
        final ElementWithContext<PackageDoc> packagE,
        Class<C>                             perPackageTemplateClass,
        String                               fileName
    ) throws IOException {

        String       packageName = packagE.current().name();
        final String home        = StringUtil.repeat(packageName.split("\\.").length, "../");

        NoTemplate.render(
            perPackageTemplateClass,
            new File(this.options.destination, packageName.replace('.',  '/') + '/' + fileName),
            new ConsumerWhichThrows<PerPackageDocument, RuntimeException>() {

                @Override public void
                consume(PerPackageDocument ppd) {
                    ppd.render(home, packagE, JavadocDoclet.this.options, JavadocDoclet.this.rootDoc);
                }
            }
        );
    }

    private <C extends NoTemplate & PerClassDocument> void
    generatePerClassDocument(
        final ElementWithContext<ClassDoc> clasS,
        Class<C>                           perClassTemplateClass
    ) throws IOException {
        {
            String packageName = clasS.current().containingPackage().name();

            File file = new File(
                this.options.destination,
                packageName.replace('.',  '/') + '/' + clasS.current().name() + ".html"
            );

            final String home = StringUtil.repeat(packageName.split("\\.").length, "../");

            NoTemplate.render(
                perClassTemplateClass,
                file,
                new ConsumerWhichThrows<PerClassDocument, RuntimeException>() {

                    @Override public void
                    consume(PerClassDocument pcd) {
                        pcd.render(
                            home,
                            clasS,
                            JavadocDoclet.this.options,
                            JavadocDoclet.this.rootDoc
                        );
                    }
                }
            );
        }
    }
}
