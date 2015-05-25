
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.ThrowsTag;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.javadoc.WildcardType;

import de.unkrig.commons.doclet.Tags;
import de.unkrig.commons.doclet.html.Html;
import de.unkrig.commons.file.FileUtil;
import de.unkrig.commons.lang.AssertionUtil;
import de.unkrig.commons.lang.ExceptionUtil;
import de.unkrig.commons.lang.StringUtil;
import de.unkrig.commons.lang.protocol.ConsumerWhichThrows;
import de.unkrig.commons.lang.protocol.Longjump;
import de.unkrig.commons.lang.protocol.Predicate;
import de.unkrig.commons.lang.protocol.Transformer;
import de.unkrig.commons.lang.protocol.TransformerUtil;
import de.unkrig.commons.nullanalysis.Nullable;
import de.unkrig.commons.util.collections.CollectionUtil;
import de.unkrig.commons.util.collections.IterableUtil;
import de.unkrig.commons.util.collections.MapUtil;
import de.unkrig.doclet.javadoc.Doccs.AbstractDocc;
import de.unkrig.doclet.javadoc.Doccs.ClassDocc;
import de.unkrig.doclet.javadoc.Doccs.Docc;
import de.unkrig.doclet.javadoc.Doccs.FieldDocc;
import de.unkrig.doclet.javadoc.Doccs.MethodDocc;
import de.unkrig.doclet.javadoc.Doccs.PackageDocc;
import de.unkrig.doclet.javadoc.Doccs.ParamTagg;
import de.unkrig.doclet.javadoc.Doccs.SeeTagg;
import de.unkrig.doclet.javadoc.Doccs.ThrowsTagg;
import de.unkrig.doclet.javadoc.templates.ClassFrame;
import de.unkrig.notemplate.NoTemplate;
import freemarker.template.Configuration;
import freemarker.template.Template;
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

    private static final Comparator<? super ClassDoc>
    NAME_COMPARATOR = new Comparator<ClassDoc>() {

        @Override public int
        compare(@Nullable ClassDoc cd1, @Nullable ClassDoc cd2) {
            assert cd1 != null;
            assert cd2 != null;

            return cd1.name().compareTo(cd2.name());
        }
    };

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
     * See <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/javadoc/doclet/overview.html">"Doclet
     * Overview"</a>.
     */
    public static boolean
    start(final RootDoc rootDoc) throws IOException {

        File    destination = new File(".");
        String  windowTitle = null;
        String  docTitle    = null;
        String  header      = null;
        String  footer      = null;
        String  top         = null;
        String  bottom      = null;
        boolean noTimestamp = false;

        for (String[] option : rootDoc.options()) {
            if ("-d".equals(option[0])) {
                destination = new File(option[1]);
            } else
            if ("-windowtitle".equals(option[0])) {
                windowTitle = option[1];
            } else
            if ("-doctitle".equals(option[0])) {
                docTitle = option[1];
            } else
            if ("-header".equals(option[0])) {
                header = option[1];
            } else
            if ("-footer".equals(option[0])) {
                footer = option[1];
            } else
            if ("-top".equals(option[0])) {
                top = option[1];
            } else
            if ("-bottom".equals(option[0])) {
                bottom = option[1];
            } else
            if ("-notimestamp".equals(option[0])) {
                noTimestamp = true;
            } else
            {

                // It is quite counterintuitive, but 'options()' returns ALL options, not only those which
                // qualified by 'optionLength()'.
                ;
            }
        }

        new JavadocDoclet(
            rootDoc,
            destination,
            windowTitle,
            docTitle,
            header,
            footer,
            top,
            bottom,
            noTimestamp
        ).start2();

        return true;
    }

    // ------------------------------

    private final RootDoc             rootDoc;
    private final File                destination;
    private final Map<String, Object> dataModel;

    private final Doccs doccs;

    private static final Html HTML = new Html(Html.STANDARD_LINK_MAKER);

    /**
     * @param destination Target directory where the document files are created
     * @param windowTitle    The argument of the "-windowtitle" command line option
     * @param docTitle
     * @param header         The argument of the "-header" command line option
     * @param footer         The argument of the "-footer" command line option
     * @param top            The argument of the "-top" command line option
     * @param bottom
     * @param noTimestamp    Whether the "-notimestamp" command line option is given

     */
    private
    JavadocDoclet(
        RootDoc           rootDoc,
        File              destination,
        @Nullable String  windowTitle,
        @Nullable String  docTitle,
        @Nullable String  header,
        @Nullable String  footer,
        @Nullable String  top,
        @Nullable String  bottom,
        boolean           noTimestamp
    ) {
        this.rootDoc     = rootDoc;
        this.destination = destination;

        {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("windowTitle",    windowTitle);
            m.put("docTitle",       docTitle);
            m.put("header",         header);
            m.put("footer",         footer);
            m.put("top",            top);
            m.put("bottom",         bottom);
            m.put("noTimestamp",    noTimestamp);
            m.put("generationDate", new Date());
            this.dataModel = Collections.unmodifiableMap(m);
        }

        this.doccs = new Doccs(rootDoc);
    }

    private void
    start2() throws IOException {

        Set<PackageDocc> allPackages;
        Set<ClassDocc>   allClassesAndInterfaces;
        {
            allPackages             = new HashSet<PackageDocc>();
            allClassesAndInterfaces = new HashSet<ClassDocc>();

            // Process all specified classes.
            for (final ClassDoc cd : this.rootDoc.specifiedClasses()) {

                allClassesAndInterfaces.add(this.wrapClass.transform(cd));

                allPackages.add(this.wrapPackage.transform(cd.containingPackage()));
            }

            // Process all specified packages.
            for (final PackageDoc pd : this.rootDoc.specifiedPackages()) {

                for (final ClassDoc cd : pd.allClasses()) {
                    allClassesAndInterfaces.add(this.wrapClass.transform(cd));
                }

                allPackages.add(this.wrapPackage.transform(pd));
            }
        }

        {
            Map<String, Object> dm = MapUtil.combine(this.dataModel, MapUtil.<String, Object>fromMappings(
                "allClassesAndInterfaces", allClassesAndInterfaces,
                "allPackages",             allPackages
            ));

            this.generate("index.html",              dm);
            this.generate("overview-frame.html",     dm);
            this.generate("stylesheet.css",          dm);
            this.generate("allclasses-frame.html",   dm);
            this.generate("allclasses-noframe.html", dm);
            this.generate("constant-values.html",    dm);
            this.generate("overview-summary.html",   dm);
        }

        for (PackageDocc packageDocc : allPackages) {
            String packageName = packageDocc.getName();

            String home = StringUtil.repeat(packageName.split("\\.").length, "../");
            {
                Map<String, Object> dm = MapUtil.combine(this.dataModel, MapUtil.<String, Object>fromMappings(
                    "home",    home,
                    "package", packageDocc
                ));

                this.generate(packageName.replace('.',  '/') + "/package-frame.html",   "package-frame.html.ftl",   dm);
                this.generate(packageName.replace('.',  '/') + "/package-summary.html", "package-summary.html.ftl", dm);
                this.generate(packageName.replace('.',  '/') + "/package-tree.html",    "package-tree.html.ftl",    dm);
            }

            Iterator<ClassDocc> it = packageDocc.getAllClasses().iterator();
            if (it.hasNext()) {
                ClassDocc previousClassDocc = null;
                ClassDocc classDocc         = it.next();
                for (;;) {
                    ClassDocc nextClassDocc = it.hasNext() ? it.next() : null;

                    ClassDoc classDoc = (ClassDoc) classDocc.getDoc();

                    // Create per-class document.
                    {
                        File file = new File(
                            this.destination,
                            packageName.replace('.',  '/') + '/' + classDoc.name() + ".html"
                        );

                        Writer w = new FileWriter(file);
                        try {
                            NoTemplate.newTemplate(ClassFrame.class, w).render(
                                this.rootDoc,
                                (Boolean) this.dataModel.get("noTimestamp"),
                                (Date) this.dataModel.get("generationDate"),
                                (String) this.dataModel.get("windowTitle"),
                                previousClassDocc == null ? null : (ClassDoc) previousClassDocc.getDoc(),
                                classDoc,
                                nextClassDocc == null ? null : (ClassDoc) nextClassDocc.getDoc(),
                                home,
                                (String) this.dataModel.get("top"),
                                (String) this.dataModel.get("bottom"),
                                (String) this.dataModel.get("header"),
                                (String) this.dataModel.get("footer")
                            );
                            w.close();
                        } finally {
                            try { w.close(); } catch (Exception e) {}
                        }
                    }
//                    this.generate(
//                        packageName.replace('.',  '/') + '/' + classDoc.name() + ".html",
//                        "class-frame.html.ftl",
//                        dm
//                    );

                    if (nextClassDocc == null) break;

                    previousClassDocc = classDocc;
                    classDocc         = nextClassDocc;
                }
            }
        }
    }

    abstract
    class MyAbstractDocc extends AbstractDocc {

        public
        MyAbstractDocc(Doc doc) { JavadocDoclet.this.doccs.super(doc); }

        @Override public SeeTagg[]
        getSeeTags() {
            List<SeeTagg> result = new ArrayList<Doccs.SeeTagg>();
            for (final SeeTag st : this.getDoc().seeTags()) {
                result.add(new SeeTagg() {

                    @Override public Docc
                    getReference() {

                        {
                            MemberDoc rm = st.referencedMember();
                            if (rm instanceof FieldDoc) {
                                return JavadocDoclet.this.wrapField.transform((FieldDoc) rm);
                            } else
                            if (rm instanceof MethodDoc) {
                                return JavadocDoclet.this.wrapMethod.transform((MethodDoc) rm);
                            } else
                            if (rm != null) {
                                throw new AssertionError();
                            }
                        }
                        {
                            ClassDoc rc = st.referencedClass();
                            if (rc != null) return JavadocDoclet.this.wrapClass.transform(rc);
                        }
                        {
                            PackageDoc rp = st.referencedPackage();
                            if (rp != null) {
                                return JavadocDoclet.this.wrapPackage.transform(rp);
                            }
                        }

                        throw new AssertionError();
                    }

                    @Override public String
                    getLabel() { return st.label(); }
                });
            }

            return result.toArray(new SeeTagg[result.size()]);
        }
    }

    private final Transformer<Type, ClassDocc>
    wrapType = TransformerUtil.cache(new Transformer<Type, ClassDocc>() {

        @Override
        public ClassDocc
        transform(final Type type) {

            if (type instanceof ClassDoc) {
                ClassDoc cd = (ClassDoc) type;
                return new MyClassDoc(
                    cd,                  // programElementDoc
                    cd.typeParameters(), // typeParameters
                    new Type[0],         // typeArguments
                    cd.superclassType(), // superclassType
                    cd.interfaceTypes()  // interfaceTypes
                );
            } else
            if (type instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) type;
                return new MyClassDoc(
                    pt.asClassDoc(),     // programElementDoc
                    new TypeVariable[0], // typeParameters
                    pt.typeArguments(),  // typeArguments
                    pt.superclassType(), // superclassType
                    pt.interfaceTypes()  // interfaceTypes
                );
            } else
            {
                throw new AssertionError(type.getClass());
            }
        }
    });

    private final Transformer<ClassDoc, ClassDocc>
    wrapClass = TransformerUtil.cache(new Transformer<ClassDoc, ClassDocc>() {

        @Override
        public ClassDocc
        transform(final ClassDoc classDoc) {

            return new MyClassDoc(
                classDoc,                  // programElementDoc
                classDoc.typeParameters(), // typeParameters
                new Type[0],               // typeArguments
                classDoc.superclassType(), // superclassType
                classDoc.interfaceTypes()  // interfaceTypes
            );
        }
    });

    class MyClassDoc extends MyAbstractDocc implements ClassDocc {

        private final ProgramElementDoc programElementDoc;
        private final TypeVariable[]    typeParameters;
        private final Type[]            typeArguments;
        @Nullable private final Type    superclassType;
        private final Type[]            interfaceTypes;

        public
        MyClassDoc(
            ProgramElementDoc programElementDoc,
            TypeVariable[]    typeParameters,
            Type[]            typeArguments,
            @Nullable Type    superclassType,
            Type[]            interfaceTypes
        ) {
            super(programElementDoc);
            this.programElementDoc = programElementDoc;
            this.typeParameters    = typeParameters;
            this.typeArguments     = typeArguments;
            this.superclassType    = superclassType;
            this.interfaceTypes    = interfaceTypes;
        }

        @Override public String
        toString() { return this.programElementDoc.toString(); }

        @Override public String
        toString(Doc ref) { return this.programElementDoc.name(); }

        @Override public String
        getTypeParameters() {

            if (this.typeParameters.length == 0) return "";

            StringBuilder sb = new StringBuilder("<").append(this.typeParameters[0].toString());
            for (int i = 1; i < this.typeParameters.length; i++) {
                sb.append(",").append(this.typeParameters[i].toString());
            }
            return sb.append('>').toString();
        }

        @Override public String
        getTypeArguments() {

            if (this.typeArguments.length == 0) return "";

            StringBuilder sb = new StringBuilder("<").append(this.typeArguments[0].toString());
            for (int i = 1; i < this.typeArguments.length; i++) {
                sb.append(", ").append(this.typeArguments[i].toString());
            }
            return sb.append('>').toString();
        }

        @Override public String
        getCategory() {
            return (
                this.programElementDoc.isAnnotationType() ? "annotation type" :
                this.programElementDoc.isClass()          ? "class"           :
                this.programElementDoc.isEnum()           ? "enum"            :
                this.programElementDoc.isInterface()      ? "interface"       :
                ExceptionUtil.<String>throwAssertionError(this.programElementDoc.getClass())
            );
        }

        @Override public String
        getSimpleName() { return this.programElementDoc.name(); }

        @Override public String
        getQualifiedName() { return this.programElementDoc.qualifiedName(); }

        @Override public String
        getHref() {
            StringBuilder sb = new StringBuilder();

            String cpn = this.programElementDoc.containingPackage().name();
            if (!cpn.isEmpty()) sb.append(cpn.replace('.', '/')).append('/');

            return sb.append(this.programElementDoc.name()).append(".html").toString();
        }

        @Override @Nullable public ClassDocc
        getSuperclass() {

            Type sct = this.superclassType;
            if (sct == null || "java.lang.Object".equals(this.programElementDoc.qualifiedName())) return null;

            return JavadocDoclet.this.wrapType.transform(sct);
        }

        @Override public List<ClassDocc>
        getSuperclassChain() {
            if (!this.programElementDoc.isClass()) return Collections.emptyList();

            List<ClassDocc> result = new ArrayList<ClassDocc>();
            for (ClassDocc scd = this.getSuperclass(); scd != null; scd = scd.getSuperclass()) {
                result.add(scd);
            }
            return result;
        }

        @Override public Collection<ClassDocc>
        getImplementedInterfaces() {
            if (!this.programElementDoc.isClass()) return Collections.emptyList();

            Collection<ClassDocc> result = new ArrayList<ClassDocc>();
            for (Type it : this.interfaceTypes) {
                ClassDocc i = JavadocDoclet.this.wrapType.transform(it);
                result.add(i);
                for (ClassDocc si : i.getAllSuperInterfaces()) {
                    if (!result.contains(si)) result.add(si);
                }
            }
            return result;
        }

        @Override public Collection<ClassDocc>
        getAllSuperInterfaces() {
            if (!this.programElementDoc.isInterface()) return Collections.emptyList();

            Collection<ClassDocc> result = new ArrayList<ClassDocc>();
            for (Type it : this.interfaceTypes) {
                ClassDocc i = JavadocDoclet.this.wrapType.transform(it);
                result.add(i);
                for (ClassDocc si : i.getAllSuperInterfaces()) {
                    if (!result.contains(si)) result.add(si);
                }
            }
            return result;
        }

        @Override public Collection<ClassDocc>
        getKnownSubinterfaces() {

            Collection<ClassDocc> result = new ArrayList<ClassDocc>();
            for (ClassDoc cd : JavadocDoclet.this.rootDoc.classes()) {
                if (
                    cd.isInterface()
                    && cd.subclassOf((ClassDoc) this.programElementDoc)
                    && cd != this.programElementDoc
                ) {
                    result.add(JavadocDoclet.this.wrapClass.transform(cd));
                }
            }
            return result;
        }

        @Override public Collection<ClassDocc>
        getBaseClassesAndInterfaces() {

            if (this.programElementDoc.isInterface()) return this.getAllSuperInterfaces();

            Collection<ClassDocc> result = this.getImplementedInterfaces();
            for (ClassDocc sc : this.getSuperclassChain()) {
                result.add(sc);
                for (ClassDocc i : sc.getImplementedInterfaces()) {
                    if (!result.contains(i)) result.add(i);
                }
            }

            return result;
        }

        @Override public Collection<MethodDocc>
        getMethods() {

            return IterableUtil.asCollection(
                IterableUtil.transform(
                    Arrays.asList(((ClassDoc) this.programElementDoc).methods()),
                    JavadocDoclet.this.wrapMethod
                )
            );
        }

        @Override public Collection<MethodDocc>
        getMethodsSorted() { return CollectionUtil.sorted(this.getMethods()); }

        @Override public Collection<FieldDocc>
        getFields() {

            return IterableUtil.asCollection(
                IterableUtil.transform(
                    Arrays.asList(((ClassDoc) this.programElementDoc).fields()),
                    JavadocDoclet.this.wrapField
                )
            );
        }

        @Override public Collection<FieldDocc>
        getFieldsSorted() { return CollectionUtil.sorted(this.getFields()); }

        @Override public Collection<FieldDocc>
        getConstants() {

            return IterableUtil.asCollection(IterableUtil.filter(
                this.getFields(),
                new Predicate<FieldDocc>() {
                    @Override public boolean evaluate(FieldDocc fieldDocc) { return fieldDocc.isConstant(); }
                }
            ));
        }


        @Override public Collection<ClassDocc>
        getNestedClassesAndInterfaces() {

            return IterableUtil.asCollection(
                IterableUtil.transform(
                    Arrays.asList(((ClassDoc) this.programElementDoc).innerClasses()),
                    JavadocDoclet.this.wrapClass
                )
            );
        }

        @Override @Nullable public String
        getTitle() {
            return this.getCategory() + " in " + this.programElementDoc.containingPackage().name();
        }
    }

    private <T extends ClassDoc> Collection<ClassDocc>
    wrapClasses(Iterable<T> classDocs) {
        return IterableUtil.asCollection(IterableUtil.transform(classDocs, this.wrapClass));
    }

    private final Transformer<FieldDoc, FieldDocc>
    wrapField = TransformerUtil.cache(new Transformer<FieldDoc, FieldDocc>() {

        @Override
        public FieldDocc
        transform(final FieldDoc fieldDoc) {

            class MyFieldDocc extends MyAbstractDocc implements FieldDocc {

                public MyFieldDocc() { super(fieldDoc); }

                @Override public String
                toString(Doc ref) {
                    ClassDoc cc = fieldDoc.containingClass();
                    if (
                        cc == ref
                        || (ref instanceof ProgramElementDoc && ((ProgramElementDoc) ref).containingClass() == cc)
                    ) return fieldDoc.name();

                    return fieldDoc.containingClass().simpleTypeName() + '.' + fieldDoc.name();
                }

                @Override public boolean
                isConstant() {
                    return fieldDoc.isStatic() && fieldDoc.isFinal() && fieldDoc.constantValueExpression() != null;
                }

                @Override public String
                getHref() {
                    ClassDoc   containingClass   = fieldDoc.containingClass();
                    PackageDoc containingPackage = fieldDoc.containingPackage();

                    StringBuilder sb = new StringBuilder();

                    String cpn = containingPackage.name();
                    if (!cpn.isEmpty()) sb.append(cpn.replace('.', '/')).append('/');

                    return sb.append(containingClass.name()).append(".html#").append(fieldDoc.name()).toString();
                }
            }

            return new MyFieldDocc();
        }
    });

    private final Transformer<MethodDoc, MethodDocc>
    wrapMethod = TransformerUtil.cache(new Transformer<MethodDoc, MethodDocc>() {

        @Override
        public MethodDocc
        transform(final MethodDoc methodDoc) {

            class MyMethodDocc extends MyAbstractDocc implements MethodDocc {

                public MyMethodDocc() { super(methodDoc); }

                @Override public String
                toString(Doc ref) {
                    StringBuilder sb = new StringBuilder();

                    ClassDoc cc = methodDoc.containingClass();
                    if (!(
                        cc == ref
                        || (ref instanceof ProgramElementDoc && ((ProgramElementDoc) ref).containingClass() == cc)
                    )) sb.append(methodDoc.containingClass().simpleTypeName()).append('.');

                    sb.append(methodDoc.name()).append('(');
                    for (int i = 0; i < methodDoc.parameters().length; i++) {
                        if (i > 0) sb.append(", ");
                        Type     pt = methodDoc.parameters()[i].type();
                        ClassDoc cd = pt.asClassDoc();
                        if (cd == null) {
                            sb.append(pt.toString());
                        } else
                        if ("java.lang".equals(cd.containingPackage().name())) {
                            sb.append(cd.name());
                        } else
                        {
                            sb.append(cd.qualifiedName());
                        }
                    }
                    sb.append(')');

                    return sb.toString();
                }

                @Override public ClassDocc
                getReturnType() { return JavadocDoclet.this.wrapType.transform(methodDoc.returnType()); }

                @Override public int
                compareTo(@Nullable Docc o) {

                    int result = super.compareTo(o);
                    if (result != 0) return result;

                    MethodDocc that = (MethodDocc) o;
                    assert that != null;

                    Parameter[] theseParameters = methodDoc.parameters();
                    Parameter[] thoseParameters = ((MethodDoc) that.getDoc()).parameters();
                    for (int i = 0; i < theseParameters.length && i < thoseParameters.length; i++) {
                        result = theseParameters[i].typeName().compareTo(thoseParameters[i].typeName());
                        if (result != 0) return result;
                    }

                    return theseParameters.length - thoseParameters.length;
                }

                @Override public String
                getHref() {
                    ClassDoc   containingClass   = methodDoc.containingClass();
                    PackageDoc containingPackage = methodDoc.containingPackage();

                    StringBuilder sb = new StringBuilder();

                    String cpn = containingPackage.name();
                    if (!cpn.isEmpty()) sb.append(cpn.replace('.', '/')).append('/');

                    sb.append(containingClass.name()).append(".html#");
                    sb.append(methodDoc.name());
                    if (methodDoc.parameters().length == 0) {
                        sb.append("--");
                    } else {
                        for (Parameter p : methodDoc.parameters()) {
                            sb.append('-').append(p.type().qualifiedTypeName());
                        }
                        sb.append('-');
                    }

                    return sb.toString();
                }

                @Override public String[]
                getFragments() {

                    // <a name="enableAssertionsFor-java.lang.Class-">
                    // but not <a name="enableAssertionsFor-java.lang.Class<?>-">
                    //
                    // <a name="enableAssertionsForThisClass--">
                    //
                    // <a name="notNull-java.lang.Object-">
                    // also <a name="notNull-T-">
                    //
                    // <a name="notNull-java.lang.Object-java.lang.String-">
                    // <a name="notNull-T-java.lang.String-">
                    // but not <a name="notNull-T-String-">
                    //
                    // <a name="fail--">
                    //
                    // <a name="fail-java.lang.String-">
                    // but not <a name="fail-String-">
                    //
                    // <a name="fail-java.lang.Throwable-">
                    // but not <a name="fail-Throwable-">
                    //
                    // <a name="fail-java.lang.String-java.lang.Throwable-">
                    // but not <a name="fail-String-Throwable-">

                    if (methodDoc.parameters().length == 0) {
                        return new String[] { methodDoc.name() + "--" };
                    }

                    StringBuilder sb1 = new StringBuilder(methodDoc.name());
                    StringBuilder sb2 = new StringBuilder(methodDoc.name());
                    for (Parameter p : methodDoc.parameters()) {
                        sb1.append('-');
                        sb2.append('-');

                        Type pt = p.type();

                        if (pt instanceof ParameterizedType) {

                            sb1.append(((ParameterizedType) pt).asClassDoc().qualifiedTypeName());
                            sb2.append(((ParameterizedType) pt).asClassDoc().qualifiedTypeName());
                        } else
                        if (pt instanceof WildcardType) {
                            Type firstBound = ((WildcardType) pt).extendsBounds()[0];

                            sb1.append(firstBound.toString());
                            sb2.append(firstBound.toString());
                        } else
                        if (pt instanceof TypeVariable) {
                            Type[] bounds = ((TypeVariable) pt).bounds();
                            sb1.append(bounds.length == 0 ? "java.lang.Object" : bounds[0].toString());
                            sb2.append(pt.qualifiedTypeName());
                        } else
                        {
                            // "type().qualifiedTypeName()" => "java.lang.Class", "java.lang.String",
                            // "java.lang.Throwable"
                            sb1.append(pt.qualifiedTypeName());
                            sb2.append(pt.qualifiedTypeName());
                        }
                    }
                    String result1 = sb1.append("-").toString();
                    String result2 = sb2.append("-").toString();
                    return result1.equals(result2) ? new String[] { result1 } : new String[] { result1, result2 };
                }

                @Override @Nullable public String
                getReturnValueDescription() {
                    try {
                        String rtd = Tags.optionalTag(methodDoc, "@return", JavadocDoclet.this.rootDoc);
                        if (rtd == null) return null;
                        return JavadocDoclet.HTML.fromJavadocText(rtd, methodDoc, JavadocDoclet.this.rootDoc);
                    } catch (Longjump l) {
                        return "???";
                    }
                }

                @Override public Collection<ThrowsTagg>
                getThrowsTags() {

                    List<ThrowsTagg> result = new ArrayList<Doccs.ThrowsTagg>();
                    for (final ThrowsTag tt : methodDoc.throwsTags()) {
                        result.add(new ThrowsTagg() {

                            @Override public String
                            getExceptionQualifiedName() { return tt.exception().qualifiedName(); }

                            @Override @Nullable public String
                            getExceptionComment() {

                                String ec = tt.exceptionComment();
                                if (ec == null) return null;

                                try {
                                    ec = JavadocDoclet.HTML.fromJavadocText(ec, methodDoc, JavadocDoclet.this.rootDoc);
                                } catch (Longjump l) {
                                    ;
                                }

                                return ec;
                            }
                        });
                    }

                    ADD_EXCEPTIONS_WITHOUT_TAG:
                    for (final Type et : methodDoc.thrownExceptionTypes()) {

                        for (ThrowsTagg tt : result) {
                            if (tt.getExceptionQualifiedName().equals(et.toString())) {
                                continue ADD_EXCEPTIONS_WITHOUT_TAG;
                            }
                        }

                        result.add(new ThrowsTagg() {
                            @Override public String           getExceptionQualifiedName() { return et.toString(); }
                            @Override @Nullable public String getExceptionComment()       { return null; }
                        });
                    }
                    return result;
                }

                @Override public Collection<ParamTagg>
                getParamTags() {

                    List<ParamTagg> result = new ArrayList<Doccs.ParamTagg>();
                    for (final ParamTag pt : methodDoc.paramTags()) {
                        result.add(new ParamTagg() {

                            @Override public String
                            getName() { return pt.parameterName(); }

                            @Override @Nullable public String
                            getParameterComment() {
                                String pc = pt.parameterComment();
                                if (pc == null) return null;

                                try {
                                    pc = JavadocDoclet.HTML.fromJavadocText(pc, methodDoc, JavadocDoclet.this.rootDoc);
                                } catch (Longjump l) {
                                    ;
                                }

                                return pc;
                            }
                        });
                    }
                    return result;
                }
            }

            return new MyMethodDocc();
        }
    });

    private final Transformer<PackageDoc, PackageDocc>
    wrapPackage = TransformerUtil.cache(new Transformer<PackageDoc, PackageDocc>() {

        @Override
        public PackageDocc
        transform(final PackageDoc packageDoc) {

            class MyPackageDocc extends MyAbstractDocc implements PackageDocc {

                public MyPackageDocc() { super(packageDoc); }

                @Override public String
                toString(Doc ref) { return packageDoc.name(); }

                @Override public Collection<ClassDocc>
                getClassesAndInterfacesWithConstants() {

                    return IterableUtil.asCollection(IterableUtil.filter(
                        JavadocDoclet.this.wrapClasses(Arrays.asList(packageDoc.allClasses())),
                        new Predicate<ClassDocc>() {

                            @Override public boolean
                            evaluate(ClassDocc classDocc) { return !classDocc.getConstants().isEmpty(); }
                        }
                    ));
                }

                @Override public Collection<ClassDocc>
                getAllClasses() {

                    ClassDoc[] allClasses = packageDoc.allClasses();
                    Arrays.sort(allClasses, JavadocDoclet.NAME_COMPARATOR);
                    return JavadocDoclet.this.wrapClasses(Arrays.asList(allClasses));
                }

                @Override public Collection<ClassDocc>
                getAnnotationTypes() {
                    return JavadocDoclet.this.wrapClasses(Arrays.asList(packageDoc.annotationTypes()));
                }

                @Override public Collection<ClassDocc>
                getClasses() {

                    ClassDoc[] ordinaryClasses = packageDoc.ordinaryClasses();
                    Arrays.sort(ordinaryClasses, JavadocDoclet.NAME_COMPARATOR);
                    return JavadocDoclet.this.wrapClasses(Arrays.asList(ordinaryClasses));
                }

                @Override public Collection<ClassDocc>
                getEnums() {
                    return JavadocDoclet.this.wrapClasses(Arrays.asList(packageDoc.enums()));
                }

                @Override public Collection<ClassDocc>
                getErrors() {
                    return JavadocDoclet.this.wrapClasses(Arrays.asList(packageDoc.errors()));
                }

                @Override public Collection<ClassDocc>
                getExceptions() {
                    return JavadocDoclet.this.wrapClasses(Arrays.asList(packageDoc.exceptions()));
                }

                @Override public Collection<ClassDocc>
                getInterfaces() {
                    return JavadocDoclet.this.wrapClasses(Arrays.asList(packageDoc.interfaces()));
                }

                @Override public String
                getHref() {

                    StringBuilder sb = new StringBuilder();

                    String packageName = packageDoc.name();
                    if (!packageName.isEmpty()) sb.append(packageName.replace('.', '/')).append('/');

                    return sb.append("package-summary.html").toString();
                }
            }

            return new MyPackageDocc();
        }
    });

    private void
    generate(String fileName, Map<String, Object> dataModel) throws IOException {

        this.generate(fileName, fileName + ".ftl", dataModel);
    }

    private void
    generate(String fileName, String templateName, final Map<String, Object> dataModel) throws IOException {

        final Template template = JavadocDoclet.FREEMARKER_CONFIGURATION.getTemplate(templateName);

        try {

            File file = new File(this.destination, fileName);
            System.out.println("Generating " + file + "...");

            FileUtil.printToFile(
                file,
                Charset.forName("ISO-8859-1"),
                new ConsumerWhichThrows<PrintWriter, Exception>() {
                    @Override public void consume(PrintWriter pw) throws Exception { template.process(dataModel, pw); }
                }
            );
        } catch (Exception e) {
            throw new IOException("Processing \"" + template + "\"", e);
        }
    }
}
