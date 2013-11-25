<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <style>
    h4 {
     cursor: hand;
    }
    ul {
      font-family: "Courier New", sherif
    }
  </style>
  <title>Invalid documents list</title>
  <script language="javascript" src="../js/jquery.js"/></script>
  <script language="javascript" src="../js/jquery-ui-1.10.2.custom.min.js"></script>
  <script language="javascript">
    $(function(){
        $('.hidden').toggle()
        $('h4').click(function(){
           $(this).next('ul').toggle()
        })
    })
  </script>
</head>
<body>
  <g:if test="${documents.entrySet().size()}">
    <h3>These sources has problems in it</h3>
  </g:if>

  <g:else>
    <h3>No sources with problems found so far...</h3>
  </g:else>

    <g:each in="${documents.entrySet()}" var="entry">
      <h4>${entry.key?entry.key:'null'}</h4>
      <ul class="hidden">
        <g:each in="${entry.value}" var="document">
          <li>@${document.key}:<a href="${document.value}" target="_blank">${document.value}</a></li>
        </g:each>
      </ul>
    </g:each>
</body>
</html>
