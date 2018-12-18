app.service("uploadService",function ($http) {
this.uploadFile=function () {
    //创建空的表单数据对象
    var formData=new FormData();
    //表单数据对象中添加数据
    formData.append("file",file.files[0]);
    return $http({
        method:'POST',
        url:"../upload.do",
        data:formData,
        /**
         * njularjs 对于 post 和 get 请求默认的 Content-Type header 是 application/json。通过设置
         ‘Content-Type’: undefined，这样浏览器会帮我们把 Content-Type 设置为 multipart/form-data.
         */
        headers:{'Content-Type':undefined},
        /**
         * 通过设置 transformRequest: angular.identity ，anjularjs transformRequest function 将序列化
         我们的 formdata object.
         */
        transformRequest:angular.identity
    })

}
});