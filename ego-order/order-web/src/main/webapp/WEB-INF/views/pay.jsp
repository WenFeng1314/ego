<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<script src="http://localhost:8083/js/jquery.js"></script>
<script src="http://localhost:8083/js/qr.js"></script>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
  <div id="qrCode">
  </div>
</body>
<script>
  $(function(){
	  // 生成二维码
	  $("#qrCode").qrcode("${qrCode}");
	  // 查询订单是否完成
	  window.setInterval(function(){
		  $.get("http://localhost:8083/order/query","orderSn=${orderSn}",function(res){
			  if(res.status==200){
				 window.location.href="http://localhost:8083/order/ok?orderSn=${orderSn}"
			  }
		  })
    },1000);
	 
  })
</script>
</html>
