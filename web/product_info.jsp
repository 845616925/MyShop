<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>会员登录</title>
    <link rel="stylesheet" href="css/bootstrap.min.css" type="text/css"/>
    <script src="js/jquery-1.11.3.min.js" type="text/javascript"></script>
    <script src="js/bootstrap.min.js" type="text/javascript"></script>
    <!-- 引入自定义css文件 style.css -->
    <link rel="stylesheet" href="css/style.css" type="text/css"/>

    <style>
        body {
            margin-top: 20px;
            margin: 0 auto;
        }

        .carousel-inner .item img {
            width: 100%;
            height: 300px;
        }
    </style>

    <script type="text/javascript">
        function addCart() {
            var buyNum = $("#buyNum").val();
            location.href = "${pageContext.request.contextPath}/product?method=addProductToCart&pid=${product.pid}&buyNum=" + buyNum;


        }
    </script>


</head>

<body>
<!-- 引入header.jsp -->
<jsp:include page="/header.jsp"></jsp:include>

<div class="container">
    <div class="row">
        <div
                style="border: 1px solid #e4e4e4; width: 930px; margin-bottom: 10px; margin: 0 auto; padding: 10px; margin-bottom: 10px;">
            <a href="./index.htm">首页&nbsp;&nbsp;&gt;</a> <a href="./蔬菜分类.htm">蔬菜&nbsp;&nbsp;&gt;</a>
            <a>无公害蔬菜</a>
        </div>

        <div style="margin: 0 auto; width: 950px;">
            <div class="col-md-6">
                <img style="opacity: 1; width: 400px; height: 350px;" title=""
                     class="medium"
                     src="${pageContext.request.contextPath}/${product.pimage}">
            </div>

            <div class="col-md-6">
                <div>
                    <strong>${product.pname}</strong>
                </div>
                <div
                        style="border-bottom: 1px dotted #dddddd; width: 350px; margin: 10px 0 10px 0;">
                    <div>编号：${product.pid}</div>
                </div>

                <div style="margin: 10px 0 10px 0;">
                    亿家价: <strong style="color: #ef0101;">￥：${product.shop_price}元/件</strong> 参 考 价：
                    <del>￥${product.market_price}元/件</del>
                </div>

                <div style="margin: 10px 0 10px 0;">
                    促销: <a target="_blank" title="限时抢购 (2014-07-30 ~ 2015-01-01)"
                           style="background-color: #f07373;">限时抢购</a>
                </div>

                <div
                        style="padding: 10px; border: 1px solid #e7dbb1; width: 330px; margin: 15px 0 10px 0;; background-color: #fffee6;">
                    <div style="margin: 5px 0 10px 0;">白色</div>

                    <div style="border-bottom: 1px solid #faeac7; margin-top: 20px; padding-left: 10px;">
                        购买数量:
                        <input id="buyNum" name="buyNum" value="1" maxlength="4" size="10" type="text">
                    </div>

                    <div style="margin: 20px 0 10px 0;; text-align: center;">
                        <a href="javascript:void(0);" onclick="addCart()">
                            <input style="background: url('./images/product.gif') no-repeat scroll 0 -600px rgba(0, 0, 0, 0); height: 36px; width: 127px;"
                                   value="加入购物车" type="button">
                        </a> &nbsp;收藏商品
                    </div>
                </div>
                <c:if test="${empty currentPage}">
                    <a href="${pageContext.request.contextPath}">返回</a>
                </c:if>
                <c:if test="${!empty currentPage}">
                    <a href="${pageContext.request.contextPath}/product?method=productListByCid&?cid=${cid}&currentPage=${currentPage}">返回列表页面</a>
                </c:if>
            </div>
        </div>
        <div class="clear"></div>
        <div style="width: 950px; margin: 0 auto;">
            <div
                    style="background-color: #d3d3d3; width: 930px; padding: 10px 10px; margin: 10px 0 10px 0;">
                <strong>商品介绍</strong>
            </div>

            <div>
                <img
                        src="${pageContext.request.contextPath}/${product.pimage}">
            </div>
        </div>

    </div>
</div>


<!-- 引入footer.jsp -->
<jsp:include page="/footer.jsp"></jsp:include>

</body>

</html>