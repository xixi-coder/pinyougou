 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,uploadService,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	};
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	};
	
	//查询实体 
	$scope.findOne=function(){
	   var id= $location.search()['id'];
        if (id==null){
	       return
        }
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//将introduction回写到富文本编辑器
				editor.html($scope.entity.goodsDesc.introduction);
				//将string转换为json类型的数组对象
				$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                $scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
                for (var i=0;i<$scope.entity.itemList.length;i++){
                    $scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec)
                }
			}
		);				
	};

	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	};
	
	$scope.searchEntity={};//定义搜索对象 

    //搜索
    $scope.search=function(page,rows){
        goodsService.search(page,rows,$scope.searchEntity).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    };
//保存
    $scope.save=function(){
        var serviceObject;//服务层对象
        if($scope.entity.goods.id!=null){//如果有ID
            serviceObject=goodsService.update($scope.entity); //修改
        }else{
            serviceObject=goodsService.add($scope.entity);//增加
        }
        //提取富文本编辑器的内容
        $scope.entity.goodsDesc.introduction=editor.html();
        serviceObject.success(
            function(response){
                if(response.success){
                    alert("增改成功");
                    $scope.entity={};
                    //清空富文本编辑器
                    editor.html("");
                    location.href="goods.html"
                }else{
                    alert(response.message);
                }
            }
        );
    };

    //图片上传
    $scope.uploadFile=function(){
        uploadService.uploadFile().success(function (response) {
            if(response.success){
            	//如果上传成功，取出 url
                $scope.image_entity.url=response.message;//设置文件地址
            }else{
                alert(response.message);
            }
        })
	};
   //定义页面实体结构
	$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};
	//添加图片列表
	$scope.add_image_entity=function () {
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity)
    };
   //删除图片列表
    $scope.remove_image_entity=function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }

    //一级分类下拉选择框
	$scope.selectItemCat1List=function () {
		itemCatService.findByParentId(0).success(
			function (response) {
                $scope.itemCat1List=response;
            }
		)
    };
    //二级分类下拉选择框
	//watch 第一个参数要监控的变量,后面是发生变化后执行的函数
	$scope.$watch('entity.goods.category1Id',function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat2List=response;
            }
        )
    });
    //三级分类下拉选择框
    $scope.$watch('entity.goods.category2Id',function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat3List=response;
            }
        )
    });
	//读取模板id
    $scope.$watch('entity.goods.category3Id',function (newValue, oldValue) {
    	itemCatService.findOne(newValue).success(
    		function (response) {
				$scope.entity.goods.typeTemplateId=response.typeId;
            }
		)
	});
	//读取模板id后读取品牌列表,扩展属性
    $scope.$watch('entity.goods.typeTemplateId',function (newValue, oldValue) {
    	typeTemplateService.findOne(newValue).success(
    		function (response) {
                    $scope.typeTemplate=response;
                    $scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);
                    if ($location.search()['id']==null){
                        $scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems);
                    }
            }
		);
    	//根据模板id获取规格列表和对应的规格选项列表
        typeTemplateService.findSpecList(newValue).success(
        	function (response) {
			   $scope.specList=response;
            }
		)
	});
    //
    $scope.updateSpecAttribute=function ($event,name,value) {
		var object=$scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',name);
		if(object!=null){
			if ($event.target.checked){
				//勾选
				object.attributeValue.push(value);
			}else {
				//取消勾选
				//移除选项
				object.attributeValue.splice(object.attributeValue.indexOf(value),1);
				//如果所有选项都取消,移除规格记录
				if (object.attributeValue.length==0){
					$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1)
				}
			}
		}else {
			$scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]})
		}
    };
//创建sku列表
// $scope.entity.goodsDesc.specificationItems
// [{"attributeName":"网络","attributeValue":["移动3G","移动4G"]},{"attributeName":"机身内存","attributeValue":["16G","32G"]}]
//entity.itemList [{"spec":{"网络":"移动3G"},"price":0,"num":99999,"status":"0","isDefault":"0"},
// {"spec":{"网络":"移动4G"},"price":0,"num":99999,"status":"0","isDefault":"0"}]


    //[{"spec":{"网络":"移动3G"},"price":0,"num":99999,"status":"0","isDefault":"0"},
    // {"spec":{"网络":"移动4G"},"price":0,"num":99999,"status":"0","isDefault":"0"}]
	//创建sku列表
    $scope.createItemList=function () {
        $scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0'}];
        var items= $scope.entity.goodsDesc.specificationItems;
        for(var i=0;i<items.length;i++){
            $scope.entity.itemList = addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue)
        }
    };
    //list=[{spec:{},price:0,num:99999,status:'0',isDefault:'0'}]
    //columnName=网络   columnValues=["移动3G","移动4G"]
    addColumn=function (list,columnName,columnValues) {
      	var newList=[];
      	for (var i=0;i<list.length;i++){
      		var oldRow=list[i]; //oldRow=[{spec:{},price:0,num:99999,status:'0',isDefault:'0'}]
      		for (var j=0;j<columnValues.length;j++){
      			//深度克隆
      			var newRow=JSON.parse(JSON.stringify(oldRow));
      			newRow.spec[columnName]=columnValues[j];
      			newList.push(newRow);
    		}
		}
    	return newList;
  };

    $scope.status=['未审核','已审核','审核未通过','关闭'];//商品状态
    $scope.ItemcatList=[];
    //将分类id显示为分类名称
    $scope.findItemCatList=function () {
        itemCatService.findAll().success(
            function (response) {
            for(var i=0;i<response.length;i++){
                $scope.ItemcatList[response[i].id]=response[i].name
            }
        })
    };
		//规格框是否被勾选
    //$scope.entity.goodsDesc.specificationItems
// [{"attributeName":"网络","attributeValue":["移动3G","移动4G"]},{"attributeName":"机身内存","attributeValue":["16G","32G"]}]
    $scope.checkAttributeValue=function(specName,optionName){
      var items=  $scope.entity.goodsDesc.specificationItems;
       var object= $scope.searchObjectByKey(items,"attributeName",specName);
      if (object==null){
      return false;
	  }else {
          //如果在$scope.entity.goodsDesc.specificationItems中存在的attributeName中存在specName
          if(object.attributeValue.indexOf(optionName)>=0){
              //如果在$scope.entity.goodsDesc.specificationItems中存在的attributeValue中存在optionName
              return true
          }else {
              return false;
          }
	  }
	}
});
