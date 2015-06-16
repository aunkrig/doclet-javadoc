
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

package de.unkrig.doclet.javadoc;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

import de.unkrig.commons.doclet.Docs;
import de.unkrig.commons.lang.AssertionUtil;
import de.unkrig.commons.lang.StringUtil;
import de.unkrig.commons.lang.protocol.ConsumerWhichThrows;
import de.unkrig.commons.nullanalysis.Nullable;
import de.unkrig.commons.util.collections.IterableUtil;
import de.unkrig.commons.util.collections.IterableUtil.ElementWithContext;
import de.unkrig.doclet.javadoc.templates.clasS.AbstractPerClassDocument;
import de.unkrig.doclet.javadoc.templates.clasS.ClassFrameHtml;
import de.unkrig.doclet.javadoc.templates.global.AbstractGlobalDocument;
import de.unkrig.doclet.javadoc.templates.global.AllclassesFrameHtml;
import de.unkrig.doclet.javadoc.templates.global.AllclassesNoframeHtml;
import de.unkrig.doclet.javadoc.templates.global.ConstantValuesHtml;
import de.unkrig.doclet.javadoc.templates.global.DeprecatedListHtml;
import de.unkrig.doclet.javadoc.templates.global.HelpDocHtml;
import de.unkrig.doclet.javadoc.templates.global.IndexAllHtml;
import de.unkrig.doclet.javadoc.templates.global.IndexHtml;
import de.unkrig.doclet.javadoc.templates.global.OverviewFrameHtml;
import de.unkrig.doclet.javadoc.templates.global.OverviewSummaryHtml;
import de.unkrig.doclet.javadoc.templates.global.OverviewTreeHtml;
import de.unkrig.doclet.javadoc.templates.global.PackageList;
import de.unkrig.doclet.javadoc.templates.global.SerializedFormHtml;
import de.unkrig.doclet.javadoc.templates.global.StylesheetCss;
import de.unkrig.doclet.javadoc.templates.packagE.AbstractPerPackageDocument;
import de.unkrig.doclet.javadoc.templates.packagE.PackageFrameHtml;
import de.unkrig.doclet.javadoc.templates.packagE.PackageSummaryHtml;
import de.unkrig.doclet.javadoc.templates.packagE.PackageTreeHtml;
import de.unkrig.notemplate.NoTemplate;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

/**
 * A doclet that generates documentation for Java packages, classes, and so forth.
 */
public final
class JavadocDoclet {

    static { AssertionUtil.enableAssertionsForThisClass(); }

    private static final Configuration FREEMARKER_CONFIGURATION = new Configuration(Configuration.VERSION_2_3_22);
    static {
        JavadocDoclet.FREEMARKER_CONFIGURATION.setClassForTemplateLoading(JavadocDoclet.class, "templates");
        JavadocDoclet.FREEMARKER_CONFIGURATION.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

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

        return 0;
    }

    public static LanguageVersion languageVersion() { return LanguageVersion.JAVA_1_5; }

    /**
     * Container for the command line options of the JAVADOC doclet.
     */
    public static
    class Options {

        /** The argument of the "-d" command line option. */
        public File destination = new File(".");

        /** The argument of the "-windowtitle" command line option. */
        @Nullable public String windowTitle;

        /** The argument of the "-doctitle" command line option. */
        @Nullable public String docTitle;

        /** The argument of the "-header" command line option. */
        @Nullable public String header;

        /** The argument of the "-footer" command line option. */
        @Nullable public String footer;

        /** The argument of the "-top" command line option. */
        @Nullable public String top;

        /** The argument of the "-bottom" command line option. */
        @Nullable public String bottom;

        /** Whether the "-notimestamp" command line option is given. */
        public boolean noTimestamp;

        /** The "generation" date that is rendered into the head of all documents. */
        public final Date generationDate = new Date();
    }
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
        this.generateGlobalDocument(IndexAllHtml.class,          "index-all.html");
        this.generateGlobalDocument(PackageList.class,           "package-list");
        this.generateGlobalDocument(SerializedFormHtml.class,    "serialized-form.html");

        for (ElementWithContext<PackageDoc> packagE : IterableUtil.iterableWithContext(this.allPackages)) {

//            String       packageName = packageDoc.name();
//            final String home        = StringUtil.repeat(packageName.split("\\.").length, "../");

            // Per-package documents.
            this.generatePerPackageDocument(packagE, PackageFrameHtml.class,   "package-frame.html");
            this.generatePerPackageDocument(packagE, PackageSummaryHtml.class, "package-summary.html");
            this.generatePerPackageDocument(packagE, PackageTreeHtml.class,    "package-tree.html");

            ClassDoc[] acs = packagE.current().allClasses();
            Arrays.sort(acs, Docs.DOCS_BY_NAME_COMPARATOR);
            Iterator<ClassDoc> it = Arrays.asList(acs).iterator();
            if (it.hasNext()) {
                ClassDoc previousClassDoc = null;
                ClassDoc classDoc         = it.next();
                for (;;) {
                    ClassDoc nextClassDoc = it.hasNext() ? it.next() : null;

                    // Create per-class document.
                    this.generatePerClassDocument(
                        previousClassDoc,
                        classDoc,
                        nextClassDoc,
                        ClassFrameHtml.class
                    );

                    if (nextClassDoc == null) break;

                    previousClassDoc = classDoc;
                    classDoc         = nextClassDoc;
                }
            }
        }
    }

    /**
     * Creates a "global" document.
     */
    private <C extends AbstractGlobalDocument> void
    generateGlobalDocument(
        Class<C>      perPackageTemplateClass,
        String        fileName
    ) throws IOException {

        NoTemplate.render(
            perPackageTemplateClass,                                              // templateClass
            new File(this.options.destination, fileName),                         // out
            new ConsumerWhichThrows<AbstractGlobalDocument, RuntimeException>() { // renderer

                @Override public void
                consume(AbstractGlobalDocument gd) {

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
    private <C extends AbstractPerPackageDocument> void
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
            new ConsumerWhichThrows<AbstractPerPackageDocument, RuntimeException>() {

                @Override public void
                consume(AbstractPerPackageDocument ppd) {
                    ppd.render(home, packagE, JavadocDoclet.this.options, JavadocDoclet.this.rootDoc);
                }
            }
        );
    }

    private <C extends AbstractPerClassDocument> void
    generatePerClassDocument(
        @Nullable final ClassDoc previousClassDoc,
        final ClassDoc           classDoc,
        @Nullable final ClassDoc nextClassDoc,
        Class<C>                 perClassTemplateClass
    ) throws IOException {
        {
            String packageName = classDoc.containingPackage().name();

            File file = new File(
                this.options.destination,
                packageName.replace('.',  '/') + '/' + classDoc.name() + ".html"
            );

            final String home = StringUtil.repeat(packageName.split("\\.").length, "../");

            NoTemplate.render(
                perClassTemplateClass,
                file,
                new ConsumerWhichThrows<AbstractPerClassDocument, RuntimeException>() {

                    @Override public void
                    consume(AbstractPerClassDocument pcd) {
                        pcd.render(
                            home,
                            previousClassDoc,
                            classDoc,
                            nextClassDoc,
                            JavadocDoclet.this.options,
                            JavadocDoclet.this.rootDoc
                        );
                    }
                }
            );
        }
    }
}
