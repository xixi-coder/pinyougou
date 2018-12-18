app.controller('indexController' ,function($scope,$controller,sellerService) {
    $controller('baseController', {$scope: $scope});//继承
    $scope.loginName=function () {
        sellerService.showName().success(
            function (response) {
                $scope.entity=response;
            }
        )
    }
});