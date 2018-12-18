//服务层
app.service('indexService',function($http){
	//显示登录名
	this.showName=function () {
        return $http.post('../login/name.do');
    }
});
