app.service("cartService",function ($http) {
    //显示购物车列表
    this.findCartList=function () {
       return $http.post("/cart/findCartList.do");
    }
    //添加商品到购物车
    this.addGoodsToCartList=function(itemId,num){
        return $http.get("/cart/addGoodsToCartList.do?itemId="+itemId+"&num="+num);
    };
    //求合计(总金额和总数量)
    this.sum=function (cartList) {
        var totalValue={totalNum:0,totalMoney:0.00} //合计实体
        for (var i=0;i<cartList.length;i++){
            var cart=cartList[i];
            for (var j=0;j<cart.orderItemList.length;j++){
                //商品订单
                var orderItem=cart.orderItemList[j];
                totalValue.totalNum+=orderItem.num;
                totalValue.totalMoney+=orderItem.totalFee;
            }
        }
        return totalValue;
    }
    //查找addres列表
    this.findAddressList=function () {
        return $http.get("/address/findListByLoginUser.do");
    }
  //提交订单
    this.submitOrder=function (order) {
        return $http.post("/order/add.do")
    }
});