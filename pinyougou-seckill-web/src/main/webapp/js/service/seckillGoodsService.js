app.service("seckillGoodsService",function ($http) {
    //读取列表数据绑定到表单中
    this.findList=function () {
        return $http.get('seckillGoods/findList.do');
    }
    //显示秒杀商品详情页数据
    this.findOne=function (id) {
        return $http.get('seckillGoods/findOneFromRedis.do?id='+id);
    }
});