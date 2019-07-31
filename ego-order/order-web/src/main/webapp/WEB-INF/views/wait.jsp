<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<script src="http://localhost:8083/js/jquery.js"></script>
<meta charset="utf-8">
<title>Insert title here</title>
</head>
<body>
	<div id="notPayOrder">未完成的订单</div>
</body>
<script type="text/javascript">
// 查询未支付的订单，并且给用户展示出来
$(function(){
  $.get("http://localhost:8083/order/getOrders","status=0",function(res){
     if(res.status == 200){ // 查询成功
        // 给用户渲染列表
        var result = res.data.results; // list
        var html ="";
        $.each(result,function(index,item){
			html+= "<form action='http://localhost:8083/pay/toPay' method='post'><input name='orderSn' type='hidden' value='"+item.orderSn+"' /><lable>订单名称：</lable><input name='orderName' value='ego 商城"+item.userNote+"订单' /></br><lable>总金额：</lable><input name='totalAmount' value='"+item.totalAmount+"' /></br>  <label>支付方式：</label><input name='type' type='radio' value='1'>电脑支付 <input type='radio' name='type' value='2' checked='checked'>扫描支付<br/>	<input type='submit' value='立即支付'></form>" ;
		})


		 $("#notPayOrder").html(html);
     }
  })
})

</script>
</html>
