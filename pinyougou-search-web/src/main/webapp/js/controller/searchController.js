app.controller("searchController",function ($scope,searchService,$location) {
    //定义搜索对象的结构
    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40,'sortField':'','sort':''};
    //搜索
    $scope.search=function () {
        //由于在输入框中输入页码数字时,pageNo会变成字符串
        $scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap=response;
                buildPageLabel();
            }
        )
    };
    //添加搜索项 改变searchMap的值
    $scope.addSearchItem=function (key, value) {
        if (key == 'category' || key == 'brand'||key=='price') {//如果用户点击的是分类或品牌
            $scope.searchMap[key] = value;
        } else {//如果点击的是规格
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();
    };
        //点击撤销搜索项
        $scope.removeSearchItem=function (key) {
            if (key=='brand'||key=='category'||key=='price'){
                //如果点击的是分类或品牌
                $scope.searchMap[key]="";
            }else {
                //如果点击的是规格选项则移除此属性
                delete $scope.searchMap.spec[key];
            }
            //更新完搜索项,重新查询
            $scope.search();
        };
   //构建分页标签
   buildPageLabel=function () {
      $scope.pageLabel=[];
     var firstpage=1;//开始页码
     var lastpage=$scope.resultMap.totalPages;//截止页码
       $scope.firstDot=true;//前面有点
       $scope.lastDot=true;//后面有点
     if ($scope.resultMap.totalPages>5) {
         if ($scope.searchMap.pageNo <= 3) {
             lastpage = 5;
             $scope.firstDot=false;
      }else if($scope.searchMap.pageNo>= $scope.resultMap.totalPages-2){
             firstpage = $scope.resultMap.totalPages - 4;
             lastpage = $scope.resultMap.totalPages;
             $scope.lastDot=false;
         } else {
             firstpage = $scope.searchMap.pageNo - 2;
             lastpage = $scope.searchMap.pageNo + 2;
             $scope.firstDot=true;//前面有点
             $scope.lastDot=true;//后面有点
         }
     }else {
         $scope.firstDot=false;//前面有点
         $scope.lastDot=false;//后面有点
     }
     //构建页码
       for (var i =firstpage;i<=lastpage;i++){
         $scope.pageLabel.push(i);
       }
    };

    //点击页码查询
    $scope.queryByPage=function (pageNo) {
        //页码验证
        if(pageNo<1||pageNo>$scope.resultMap.totalPages){
            return
        }
        $scope.searchMap.pageNo=pageNo;
        $scope.search();
    };
    //判断当前页是否是第一页
    $scope.isTopPage=function () {
        if ($scope.searchMap.pageNo==1){
            return true;
        }else {
            return false;
        }
    };
    //判断当前页是否是最后一页
    $scope.isEndPage=function () {
        if ($scope.searchMap.pageNo==$scope.resultMap.totalPages){
            return true;
        }else {
            return false;
        }
    };

    //排序
    $scope.searchSort=function (sortField,sort) {
        $scope.searchMap.sort=sort;
        $scope.searchMap.sortField=sortField;
        $scope.search();
    };
    //判断关键字是不是品牌
    $scope.keywordsIsBrand=function () {
        for(var i=0;i<$scope.resultMap.brandList.length;i++){
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
                return true;
            }
        }
        return false;
    };
    //加载portal-web index.html的搜索关键字
    $scope.loadkeywords=function () {
       $scope.searchMap.keywords= $location.search()['keywords']
        $scope.search();
    }
});