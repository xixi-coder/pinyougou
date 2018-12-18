app.controller("brandController",function($scope,$controller,brandService){
    	//继承
	
    	$controller("baseController",{$scope:$scope});
    	$scope.search=function(page,size){
    		brandService.search(page,size,$scope.searchEntity).success(
    	    		function(response){
    	    			$scope.list=response.rows;
    	    			$scope.paginationConf.totalItems=response.total;
    	    		}		
    	    		)};
    		
    	
    
    	
      //增加品牌 和修改 的数据保存
      $scope.save=function(){
    	  var object=null;
    	  if($scope.entity.id!=null){
    		  object=brandService.update($scope.entity);
    	  }else{
    		  object=brandService.add($scope.entity);
    		  
    	  }
    	 object.success(
    			  function(response){
    				  if(response.success){
    					  reloadList();
    				  }else{
    					  alter(response.message)
    				  }  
    			  }
    	  )
      }
      //修改返回的数据
      $scope.findOne=function(id){
    	brandService.findOne(id).success(
    	function(response){
    		$scope.entity=response;
    	}		
    	)
      }
      
      
      //删除
      $scope.del=function(){
    	  brandService.del($scope.selectIds).success(
    		function(response){
    			if(response.success){
    				$scope.reloadList();
    			}else{
    				alter(response.message);
    			}
    			
    		}	  
    	  
    	  )
    	  
      }
    
      });
    