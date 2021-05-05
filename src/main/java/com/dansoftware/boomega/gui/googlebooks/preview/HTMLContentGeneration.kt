@file:JvmName("HTMLContentGeneration")

package com.dansoftware.boomega.gui.googlebooks.preview

import com.dansoftware.boomega.service.googlebooks.Volume
import com.dansoftware.boomega.i18n.I18N

fun generateHTMLContent(volume: Volume?) =
    """
<!DOCTYPE html "-//W3C//DTD XHTML 1.0 Strict//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <title></title>
    <script type="text/javascript" src="https://www.google.com/books/jsapi.js"></script>
    <script type="text/javascript">
      google.books.load();

      function onNotFound() {
            const prevHTML = document.getElementsByTagName("BODY")[0].innerHTML;
            document.getElementsByTagName("BODY")[0].innerHTML = prevHTML + '${I18N.getValue("google.book.preview.notavailable")}';
      }

      function initialize() {
        var viewer = new google.books.DefaultViewer(document.getElementById('viewerCanvas'));
        viewer.load('${volume?.id}', onNotFound);
      }

      google.books.setOnLoadCallback(initialize);
    </script>
  </head>
  <body style='overflow-x: hidden; overflow-y: hidden;'>
    <div id="viewerCanvas" style="resize: horizontal; height: 98vh;"></div>
  </body>
</html>
    """