    //品牌服务
      app.service("brandService",function($http){
    	  //分页,条件查询
    	  this.search=function(page,size,searchEntity){
    		 return $http.post("../brand/search.do?pagenum="+page+"&pagesize="+size,searchEntity)
    	  }
    	  //增加
    	  this.add=function(entity){
    		  
    		  return $http.post("../brand/add.do",entity)
    		  
    	  }
    	  this.update=function(entity){
    		  
    		  return $http.post("../brand/update.do",entity)
    	  }
    	  
		 this.findOne=function(id){
    		  
    		  return $http.get("../brand/findOne.do?id="+id)
    	  }
		 
		 this.del=function(selectids){
		    return $http.post("../brand/delete.do",selectids)
		 }
		 this.selectOptionList=function () {
			 return $http.post("../brand/selectOptionList.do")
         }
      });