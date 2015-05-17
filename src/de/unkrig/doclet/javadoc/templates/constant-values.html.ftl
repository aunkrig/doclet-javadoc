[#ftl]
[#setting locale="en_US"]
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!-- NewPage -->
<html lang="de">
<head>
<!-- Generated by de.unkrig.doclet.javadoc${noTimestamp???string("", " on " + generationDate?datetime)} -->
<title>Constant Field Values${(" (" + windowTitle + ")")!""}</title>
[#if !noTimestamp??]
<meta name="date" content="${generationDate?date?string["yyyy-MM-dd"]}">
[/#if]
<link rel="stylesheet" type="text/css" href="stylesheet.css" title="Style">
<script type="text/javascript" src="script.js"></script>
</head>
<body>
<script type="text/javascript"><!--
    try {
        if (location.href.indexOf('is-external=true') == -1) {
            parent.document.title="Constant Field Values${(" (" + windowTitle + ")")!""}";
        }
    }
    catch(err) {
    }
//-->
</script>
<noscript>
<div>JavaScript is disabled on your browser.</div>
</noscript>
[#if top??]
TOP
[/#if]
<!-- ========= START OF TOP NAVBAR ======= -->
<div class="topNav"><a name="navbar.top">
<!--   -->
</a>
<div class="skipNav"><a href="#skip.navbar.top" title="Skip navigation links">Skip navigation links</a></div>
<a name="navbar.top.firstrow">
<!--   -->
</a>
<ul class="navList" title="Navigation">
<li><a href="overview-summary.html">Overview</a></li>
<li>Package</li>
<li>Class</li>
<li><a href="overview-tree.html">Tree</a></li>
<li><a href="deprecated-list.html">Deprecated</a></li>
<li><a href="index-all.html">Index</a></li>
<li><a href="help-doc.html">Help</a></li>
</ul>
[#if header??]
<div class="aboutLanguage">${header}</div>
[/#if]
</div>
<div class="subNav">
<ul class="navList">
<li>Prev</li>
<li>Next</li>
</ul>
<ul class="navList">
<li><a href="index.html?constant-values.html" target="_top">Frames</a></li>
<li><a href="constant-values.html" target="_top">No&nbsp;Frames</a></li>
</ul>
<ul class="navList" id="allclasses_navbar_top">
<li><a href="allclasses-noframe.html">All&nbsp;Classes</a></li>
</ul>
<div>
<script type="text/javascript"><!--
  allClassesLink = document.getElementById("allclasses_navbar_top");
  if(window==top) {
    allClassesLink.style.display = "block";
  }
  else {
    allClassesLink.style.display = "none";
  }
  //-->
</script>
</div>
<a name="skip.navbar.top">
<!--   -->
</a></div>
<!-- ========= END OF TOP NAVBAR ========= -->
<div class="header">
<h1 title="Constant Field Values" class="title">Constant Field Values</h1>
<h2 title="Contents">Contents</h2>
<ul>
[#list allPackages?sort_by("name") as p]
 [#if p.classesAndInterfacesWithConstants?size > 0]
<li><a href="#${p.doc.name()}">${p.doc.name()}.*</a></li>
 [/#if]
[/#list]
</ul>
</div>
<div class="constantValuesContainer">[#rt]
[#list allPackages?sort_by("name") as p]
 [#if p.classesAndInterfacesWithConstants?size > 0]
<a name="${p.doc.name()}">
<!--   -->
</a>
<h2 title="${p.doc.name()}">${p.doc.name()}.*</h2>
<ul class="blockList">
  [#list p.classesAndInterfacesWithConstants?sort_by("simpleName") as coi]
<li class="blockList">
<table class="constantsSummary" border="0" cellpadding="3" cellspacing="0" summary="Constant Field Values table, listing constant fields, and values">
<caption><span>${p.doc.name()}.<a href="${coi.doc.qualifiedName()?replace(".", "/")}.html" title="${coi.doc.isInterface()?string("interface", "class")} in ${p.doc.name()}">${coi.doc.name()}</a></span><span class="tabEnd">&nbsp;</span></caption>
<tr>
<th class="colFirst" scope="col">Modifier and Type</th>
<th scope="col">Constant Field</th>
<th class="colLast" scope="col">Value</th>
</tr>
<tbody>
   [#list coi.constants as c]
<tr class="${[ "altColor", "rowColor" ][c_index % 2]}">
<td class="colFirst"><a name="${coi.doc.qualifiedName()}.${c.doc.name()}">
<!--   -->
</a><code>${c.doc.modifiers()?replace(" ", "&nbsp;")}&nbsp;${c.doc.type()}</code></td>
<td><code><a href="${coi.doc.qualifiedName()?replace(".", "/")}.html#${c.doc.name()}">${c.doc.name()}</a></code></td>
<td class="colLast"><code>${c.doc.constantValue()?string.computer!""}</code></td>
</tr>
   [/#list]
</tbody>
</table>
</li>
  [/#list]
</ul>
 [/#if]
[/#list]
</div>
<!-- ======= START OF BOTTOM NAVBAR ====== -->
<div class="bottomNav"><a name="navbar.bottom">
<!--   -->
</a>
<div class="skipNav"><a href="#skip.navbar.bottom" title="Skip navigation links">Skip navigation links</a></div>
<a name="navbar.bottom.firstrow">
<!--   -->
</a>
<ul class="navList" title="Navigation">
<li><a href="overview-summary.html">Overview</a></li>
<li>Package</li>
<li>Class</li>
<li><a href="overview-tree.html">Tree</a></li>
<li><a href="deprecated-list.html">Deprecated</a></li>
<li><a href="index-all.html">Index</a></li>
<li><a href="help-doc.html">Help</a></li>
</ul>
[#if footer??]
<div class="aboutLanguage">${footer}</div>
[/#if]
</div>
<div class="subNav">
<ul class="navList">
<li>Prev</li>
<li>Next</li>
</ul>
<ul class="navList">
<li><a href="index.html?constant-values.html" target="_top">Frames</a></li>
<li><a href="constant-values.html" target="_top">No&nbsp;Frames</a></li>
</ul>
<ul class="navList" id="allclasses_navbar_bottom">
<li><a href="allclasses-noframe.html">All&nbsp;Classes</a></li>
</ul>
<div>
<script type="text/javascript"><!--
  allClassesLink = document.getElementById("allclasses_navbar_bottom");
  if(window==top) {
    allClassesLink.style.display = "block";
  }
  else {
    allClassesLink.style.display = "none";
  }
  //-->
</script>
</div>
<a name="skip.navbar.bottom">
<!--   -->
</a></div>
<!-- ======== END OF BOTTOM NAVBAR ======= -->
[#if bottom??]
<p class="legalCopy"><small>${bottom}</small></p>
[/#if]
</body>
</html>
