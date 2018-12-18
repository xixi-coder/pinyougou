 var app =angular.module("pinyougou",[]);
  app.filter('trustHtml',['$sce',function ($sce) {
    //定义一个名称为trusthtml的过滤器,引入$sce服务strict contextual escaping进行安全检查
    return function (data) {//data是被过滤的内容
        return $sce.trustAsHtml(data);
    }
}]);