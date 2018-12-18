app.controller("baseController",function($scope){
		//刷新列表
    	  $scope.reloadList=function(){
    		  $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage)
    	  }
    	//分页控件配置currentPage:当前页   totalItems :总记录数  itemsPerPage:每页记录数  perPageOptions :分页选项  onChange:当页码变更后自动触发的方法 
			$scope.paginationConf = {
				currentPage: 1,
				totalItems: 10,
				itemsPerPage: 10,
				perPageOptions: [10, 20, 30, 40, 50],
				onChange: function(){
					$scope.reloadList();
				}
			};
        
    	  
    	//条件查询,分页显示
    	$scope.searchEntity={};
    	
    	
       //更新单选框
      $scope.selectIds=[];
      $scope.updateSelection=function($event,id){
    	  if($event.target.checked){//如果单选框是被选择
    		  $scope.selectIds.push(id);
    	  }else{
    		  var idx = $scope.selectIds.indexOf(id);//获取元素所在的索引
    		  $scope.selectIds.splice(idx, 1);//删除,第一个参数索引位置,第二个参数删除的个数
    	  }
      };


     //优化模板列表(提取json字符串中的某个属性,并用逗号分隔开,返回拼接的字符串)
	  $scope.jsonToString=function (jsonString,key) {
		  var json =JSON.parse(jsonString);//将json类型的字符串转换成json对象(集合)
		  var value="";
		  for (var i=0 ;i<json.length;i++){
		  	if (i>0){
		  		value+=","
			}
			value+=json[i][key]
		  }
		  return value;
      }
    //在list集合中根据某key的查询对象
    $scope.searchObjectByKey=function (list,key,keyValue) {
        for(var i =0;i<list.length;i++){
            if(list[i][key]==keyValue){
                return list[i];
            }
        }
        return null;
    }

})