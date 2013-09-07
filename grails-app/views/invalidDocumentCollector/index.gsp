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
  <title>Exceptions catched during crawling</title>
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
  <h3>These sources has problems in it</h3>
    <g:each in="${documents.entrySet()}" var="entry">
      <h4>${entry.key}</h4>
      <ul class="hidden">
        <g:each in="${entry.value}" var="document">
          <li>@${document.key}:${document.value}</li>
        </g:each>
      </ul>
    </g:each>
</body>
</html>
