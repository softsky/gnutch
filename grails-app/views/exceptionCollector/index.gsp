<%@ page contentType="text/html"%>
<%@ page import="org.apache.commons.lang.exception.ExceptionUtils" %>
<html>
  <head>
    <title>Exceptions catched during crawling</title>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>

    <script type="text/javascript">
      $(function(){
         $(".stacktrace").hide()
         $("a").click(function(){
           $(this).parents("li").siblings("pre").toggle()
           return false;
         })
      })
    </script>
  </head>
  <body>

    <g:each var="entry" in="${exceptions.entrySet()}">
      Catched <b>${entry.key.name}</b> for these URLs:
      <ol>
        <g:each var="ex" in="${entry.value.entrySet()}">
          <li><a href="${ex.key}">${ex.key}</a>: ${ex.value.message}</li>
            <pre class="stacktrace">
              ${ExceptionUtils.getStackTrace(ex.value)}
            </pre>
        </g:each>
      </ol>
    </g:each>

  </body>
</html>
