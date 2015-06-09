package de.unkrig.doclet.javadoc.templates.include;

import java.util.EnumSet;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;

import de.unkrig.commons.lang.StringUtil;
import de.unkrig.commons.nullanalysis.Nullable;
import de.unkrig.commons.util.collections.IterableUtil.ElementWithContext;
import de.unkrig.doclet.javadoc.JavadocDoclet.Options;
import de.unkrig.notemplate.NoTemplate;

public class TopNavBarHtml extends NoTemplate {

    public void
    renderForGlobalDocument(
        Options options,
        String  framesLink,
        String  noFramesLink,
        String  overviewLink,
        String  treeLink,
        String  deprecatedLink,
        boolean indexLinkHighlit,
        boolean helpLinkHighlit
    ) {
        this.render(
            "",               // home
            overviewLink,     // overviewLink
            null,             // packageLink
            false,            // classLinkHighlit
            treeLink,         // treeLink
            deprecatedLink,   // deprecatedLink
            indexLinkHighlit, // indexLinkHighlit
            helpLinkHighlit,  // helpLinkHighlit
            "Prev",           // prev
            "Next",           // next
            framesLink,       // framesLink
            noFramesLink,     // noFramesLink
            options,          // options
            null              // memberTypes
        );
    }

    public void
    renderForPackageDocument(
        ElementWithContext<PackageDoc> packagE,
        Options                        options,
        String                         documentName
    ) {

        String home = StringUtil.repeat(packagE.current().name().split("\\.").length, "../");
        String prev = (
            packagE.hasPrevious()
            ? "<a href=\"" + home + packagE.previous().name().replace('.',  '/') + "/" + documentName + "\">" + ("package-summary.html".equals(documentName) ? "Prev Package" : "Prev") + "</a>"
            : ("package-summary.html".equals(documentName) ? "Prev Package" : "Prev")
        );
        String next = (
            packagE.hasNext()
            ? "<a href=\"" + home + packagE.next().name().replace('.',  '/') + "/" + documentName + "\">" + ("package-summary.html".equals(documentName) ? "Next Package" : "Next") + "</a>"
            : ("package-summary.html".equals(documentName) ? "Next Package" : "Next")
        );
        this.render(
            home,                                                                                   // home
            home + "overview-summary.html",                                                         // overviewLink
            "package-summary.html".equals(documentName) ? "HIGHLIGHT" : "package-summary.html",     // packageLink
            false,                                                                                  // classLinkHighlit
            "package-tree.html".equals(documentName) ? "HIGHLIGHT" : "package-tree.html",           // treeLink
            home + "deprecated-list.html",                                                          // deprecatedLink
            false,                                                                                  // indexLinkHighlit
            false,                                                                                  // helpLinkHighlit
            prev,                                                                                   // prev
            next,                                                                                   // next
            home + "index.html?" + packagE.current().name().replace('.', '/') + "/" + documentName, // framesLink
            documentName,                                                                           // noFramesLink
            options,                                                                                // options
            null                                                                                    // memberTypes
        );
    }

    public
    enum MemberType {
        NESTED,
        FIELD,
        CONSTR,
        METHOD
    }

    public void
    renderForClassDocument(
        String              home,
        String              prev,
        ClassDoc            clasS,
        String              next,
        Options             options,
        EnumSet<MemberType> memberTypes
    ) {

        String framesLink = (
            home
            + "index.html?"
            + clasS.containingPackage().name().replace('.', '/')
            + '/'
            + clasS.name()
            + ".html"
        );

        this.render(
            home,
            home + "overview-summary.html", // overviewLink
            "package-summary.html",         // packageLink
            true,                           // classLinkHighlit
            "package-tree.html",            // treeLink
            home + "deprecated-list.html",  // deprecatedLink
            false,                          // indexLinkHighlit
            false,                          // helpLinkHighlit
            prev,                           // prev
            next,                           // next
            framesLink,                     // framesLink
            clasS.name() + ".html",         // noFramesLink
            options,                        // options
            memberTypes                     // memberTypes
        );
    }

    /**
     * @param packageLink {@code null}==disabled, {@code "HIGHLIGHT"}==highlit
     * @param treeLink    {@code null}==disabled, {@code "HIGHLIGHT"}==highlit
     */
    private void
    render(
        String                        home,
        String                        overviewLink,
        String                        packageLink,
        boolean                       classLinkHighlit,
        String                        treeLink,
        String                        deprecatedLink,
        boolean                       indexLinkHighlit,
        boolean                       helpLinkHighlit,
        String                        prev,
        String                        next,
        String                        framesLink,
        String                        noFramesLink,
        Options                       options,
        @Nullable EnumSet<MemberType> memberTypes
    ) {

        // "-top" command line option.
        if (options.top != null) {
            this.l(
options.top
            );
        }

        this.l(
"<!-- ========= START OF TOP NAVBAR ======= -->",
"<div class=\"topNav\"><a name=\"navbar_top\">",
"<!--   -->",
"</a><a href=\"#skip-navbar_top\" title=\"Skip navigation links\"></a><a name=\"navbar_top_firstrow\">",
"<!--   -->",
"</a>",
"<ul class=\"navList\" title=\"Navigation\">"
        );

        // "Overview" link.
        if (overviewLink == null) {
            this.l(
"<li class=\"navBarCell1Rev\">Overview</li>"
            );
        } else {
            this.l(
"<li><a href=\"" + overviewLink + "\">Overview</a></li>"
            );
        }

        // "Package" link.
        if (packageLink == null) {
            this.l(
"<li>Package</li>"
            );
        } else
        if (packageLink.equals("HIGHLIGHT")) {
            this.l(
"<li class=\"navBarCell1Rev\">Package</li>"
            );
        } else
        {
            this.l(
"<li><a href=\"" + packageLink + "\">Package</a></li>"
            );
        }

        // "Class" link.
        if (classLinkHighlit) {
            this.l(
"<li class=\"navBarCell1Rev\">Class</li>"
            );
        } else {
            this.l(
"<li>Class</li>"
            );
        }

        // "Tree" link.
        if (treeLink.equals("HIGHLIGHT")) {
            this.l(
"<li class=\"navBarCell1Rev\">Tree</li>"
            );
        } else
        {
            this.l(
"<li><a href=\"" + treeLink + "\">Tree</a></li>"
            );
        }

        // "Deprecated" link.
        if (deprecatedLink.equals("HIGHLIGHT")) {
            this.l(
"<li class=\"navBarCell1Rev\">Deprecated</li>"
            );
        } else {
            this.l(
"<li><a href=\"" + home + deprecatedLink + "\">Deprecated</a></li>"
            );
        }

        // "Index" link.
        if (indexLinkHighlit) {
            this.l(
"<li class=\"navBarCell1Rev\">Index</li>"
            );
        } else {
            this.l(
"<li><a href=\"" + home + "index-all.html\">Index</a></li>"
            );
        }

        // "Help" link.
        if (helpLinkHighlit) {
            this.l(
"<li class=\"navBarCell1Rev\">Help</li>"
            );
        } else {
            this.l(
"<li><a href=\"" + home + "help-doc.html\">Help</a></li>"
            );
        }

        this.l(
"</ul>"
        );

        // "-header" command line option.
        if (options.header != null) {
            this.l(
"<div class=\"aboutLanguage\"><em>" + options.header + "</em></div>"
            );
        }

        this.l(
"</div>",
"<div class=\"subNav\">",
"<ul class=\"navList\">",
"<li>" + prev + "</li>",
"<li>" + next + "</li>",
"</ul>",
"<ul class=\"navList\">",
"<li><a href=\"" + framesLink + "\" target=\"_top\">Frames</a></li>",
"<li><a href=\"" + noFramesLink + "\" target=\"_top\">No Frames</a></li>",
"</ul>",
"<ul class=\"navList\" id=\"allclasses_navbar_top\">",
"<li><a href=\"" + home + "allclasses-noframe.html\">All Classes</a></li>",
"</ul>",
"<div>",
"<script type=\"text/javascript\"><!--",
"  allClassesLink = document.getElementById(\"allclasses_navbar_top\");",
"  if(window==top) {",
"    allClassesLink.style.display = \"block\";",
"  }",
"  else {",
"    allClassesLink.style.display = \"none\";",
"  }",
"  //-->",
"</script>",
"</div>"
        );

        if (memberTypes != null) {
            this.l(
"<div>",
"<ul class=\"subNavList\">",
"<li>Summary:&nbsp;</li>"
            );
            if (memberTypes.contains(MemberType.NESTED)) {
                this.l(
"<li><a href=\"#nested_class_summary\">Nested</a>&nbsp;|&nbsp;</li>"
                );
            } else {
                this.l(
"<li>Nested&nbsp;|&nbsp;</li>"
                );
            }
            if (memberTypes.contains(MemberType.FIELD)) {
                this.l(
"<li><a href=\"#field_summary\">Field</a>&nbsp;|&nbsp;</li>"
                );
            } else {
                this.l(
"<li>Field&nbsp;|&nbsp;</li>"
                );
            }
            if (memberTypes.contains(MemberType.CONSTR)) {
                this.l(
"<li><a href=\"#constructor_summary\">Constr</a>&nbsp;|&nbsp;</li>"
                );
            } else {
                this.l(
"<li>Constr&nbsp;|&nbsp;</li>"
                );
            }
            if (memberTypes.contains(MemberType.METHOD)) {
                this.l(
"<li><a href=\"#method_summary\">Method</a></li>"
                );
            } else {
                this.l(
"<li>Method</li>"
                );
            }
            this.l(
"</ul>",
"<ul class=\"subNavList\">",
"<li>Detail:&nbsp;</li>"
            );
            if (memberTypes.contains(MemberType.FIELD)) {
                this.l(
"<li><a href=\"#field_detail\">Field</a>&nbsp;|&nbsp;</li>"
                );
            } else {
                this.l(
"<li>Field&nbsp;|&nbsp;</li>"
                );
            }
            if (memberTypes.contains(MemberType.CONSTR)) {
                this.l(
"<li><a href=\"#constructor_detail\">Constr</a>&nbsp;|&nbsp;</li>"
                );
            } else {
                this.l(
"<li>Constr&nbsp;|&nbsp;</li>"
                );
            }
            if (memberTypes.contains(MemberType.METHOD)) {
                this.l(
"<li><a href=\"#method_detail\">Method</a></li>"
                );
            } else {
                this.l(
"<li>Method</li>"
                );
            }
            this.l(
"</ul>",
"</div>"
            );
        }
        this.l(
"<a name=\"skip-navbar_top\">",
"<!--   -->",
"</a></div>",
"<!-- ========= END OF TOP NAVBAR ========= -->"
        );
    }
}
