import angular from 'angular';
import '@uirouter/angularjs';

import '../style/app.css';


const MODULE_NAME = 'app';

let app = angular.module(MODULE_NAME, ['ui.router']);

app.directive('app', function(){
    return {
        template: require('./app.html'),
        controller: 'AppCtrl',
        controllerAs: 'app'
    }
});

app.controller('AppCtrl', function(){
    this.url = 'https://github.com/preboot/angular-webpack';
});

app.config(function($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.otherwise('/home');

    $stateProvider
        .state('home', {
            url: '/home',
            templateUrl: '../views/login_page.html'
        })

        .state('dashboard_admin', {
            url: '/dashboard_admin',
            templateUrl: '../views/internal_users/dashboard_admin.html'
        })

        .state('about', {
            // we'll get to this in a bit
        });

});


export default MODULE_NAME;