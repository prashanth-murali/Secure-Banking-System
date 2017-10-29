import angular from 'angular';
import '@uirouter/angularjs';

import '../style/app.css';
import 'angular-material/angular-material.css'

import 'angular-aria';
import 'angular-animate';
import 'angular-material';

import 'angular-base64';
import 'ngstorage';


import loginController from './controllers/loginController.js';
import authService from './services/authService';
import common from './services/common';
import dashboardAdminController from './controllers/dashBoardAdminController';
import dashboardAdminEditUser from './controllers/dashBoardAdminEditUserController';
import externalUserController from './controllers/externalUserController';
import internalEmployeeController from './controllers/internalEmployeeController';
import settingsAdminController from './controllers/settingsAdminController';
import logFileController from './controllers/logFileController';
const MODULE_NAME = 'app';

let app = angular.module(MODULE_NAME, ['ui.router', 'ngMaterial', 'base64', 'ngStorage']);

app.directive('app', function(){
    return {
        template: require('./app.html'),
        controller: 'AppCtrl',
        controllerAs: 'app'
    }
});

function addDirective(name,url) {
    app.directive(name, function(){
        return {
            templateUrl: url
        }
    });
}

addDirective('dashboardAdminEditUser','../views/internal_users/dashboard_admin_edit_user_c.html');

app.directive('sideBar', function(){
    return {
        templateUrl: '../views/side_bar_internal_employee.html'
    }
});

app.directive('dashboardAdminUsers', function(){
    return {
        templateUrl: '../views/internal_users/dashboard_admin_users.html'
    }
});

app.directive('sideBarAdmin', function(){
    return {
        templateUrl: '../views/side_bar_internal_admin.html'
    }
});

app.directive('sideBarManager', function(){
    return {
        templateUrl: '../views/side_bar_internal_manager.html'
    }
});

app.directive('sideBarCustomer', function(){
    return {
        templateUrl: '../views/side_bar_external_customer.html'
    }
});

app.directive('sideBarMerchant', function(){
    return {
        templateUrl: '../views/side_bar_external_merchant.html'
    }
});

app.controller('AppCtrl', function(){
    this.url = 'https://github.com/preboot/angular-webpack';
});

app.factory('authService', authService);
app.factory('common', common);

app.config(['$stateProvider', '$urlRouterProvider',function($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.otherwise('/home');

    $stateProvider
        .state('home', {
            url: '/home',
            templateUrl: '../views/login_page.html',
            controller:loginController
        })

        .state('credit_debit', {
            url: '/credit_debit',
            templateUrl: '../views/external_users/credit_debit_external.html',
            controller: externalUserController
        })

        .state('fund_transfer_external_user', {
            url: '/fund_transfer_external_user',
            templateUrl: '../views/external_users/fund_transfer_external_user.html',
            controller: externalUserController
        })

        .state('fund_transfer_external_merchant', {
            url: '/fund_transfer_external_merchant',
            templateUrl: '../views/external_users/fund_transfer_external_merchant.html',
            controller: externalUserController
        })

        .state('payments_external_user', {
            url: '/payments_external_user',
            templateUrl: '../views/external_users/payments_external_user.html',
            controller: paymentExternalUserController
        })

        .state('requests_to_merchant', {
            url: '/requests_to_merchant',
            templateUrl: '../views/external_users/requests_to_merchant.html',
            controller: ['common','$scope', function(common,$scope){
                $scope.goBack = common.goBack;
            }]
        })

        .state('settings_ext_customer', {
            url: '/settings_ext_customer',
            templateUrl: '../views/external_users/settings_ext_customer.html',
            controller: ['common','$scope', function(common,$scope){
                $scope.goBack = common.goBack;
            }]
        })

        .state('statements_external_user', {
            url: '/statements_external_user',
            templateUrl: '../views/external_users/statements_external_user.html',
            controller: externalUserController
        })

        .state('dashboard_admin', {
            url: '/dashboard_admin',
            templateUrl: '../views/internal_users/dashboard_admin.html',
            controller: dashboardAdminController
        })

        .state('dashboard_admin_edit_user', {
            url: '/dashboard_admin_edit_user',
            templateUrl: '../views/internal_users/dashboard_admin_edit_user.html',
            controller: dashboardAdminEditUser, // change
            params:{user:null}
        })

        .state('dashboard_internal_employee', {
            url: '/dashboard_internal_employee',
            templateUrl: '../views/internal_users/dashboard_internal_employee.html',
            controller: internalEmployeeController
        })

        .state('dashboard_internal_manager', {
            url: '/dashboard_manager',
            templateUrl: '../views/internal_users/dashboard_internal_manager.html',
            controller: internalEmployeeController
        })

        .state('dashboard_external_user', {
            url: '/dashboard_external_user',
            templateUrl: '../views/external_users/dashboard_external_user.html',
            controller: externalUserController
        })

        .state('dashboard_external_merchant', {
            url: '/dashboard_merchant',
            templateUrl: '../views/external_users/dashboard_external_merchant.html',
            controller: externalUserController
        })

        .state('edit_users', {
            url: '/edit_users',
            templateUrl: '../views/internal_users/edit_users.html',
            controller: internalEmployeeController
        })

        .state('internal_emp_transactions', {
            url: '/internal_emp_transactions',
            templateUrl: '../views/internal_users/internal_emp_transactions.html',
            controller: internalEmployeeController
        })

        .state('internal_mgr_transactions', {
            url: '/internal_mgr_transactions',
            templateUrl: '../views/internal_users/internal_mgr_transactions.html',
            controller: internalEmployeeController
        })

        .state('manage_employee', {
            url: '/manage_employee',
            templateUrl: '../views/internal_users/manage_employee.html',
            controller: dashboardAdminController
        })

        .state('pending_requests', {
            url: '/pending_requests',
            templateUrl: '../views/internal_users/pending_requests.html',
            controller: internalEmployeeController

        })

        .state('request_admin', {
            url: '/request_admin',
            templateUrl: '../views/internal_users/Request_admin.html',
            controller: ['common','$scope', function(common,$scope){
                $scope.goBack = common.goBack;
            }]
        })

        .state('request_manager', {
            url: '/request_manager',
            templateUrl: '../views/internal_users/Request_manager.html',
            controller: ['common','$scope', function(common,$scope){
                $scope.goBack = common.goBack;
            }]
        })

        .state('create_user', {
            url: '/create_user',
            templateUrl: '../views/internal_users/create_user.html',
            controller: internalEmployeeController
        })

        .state('settings_admin', {
            url: '/settings_admin',
            templateUrl: '../views/internal_users/settings_admin.html',
            controller: settingsAdminController
        })

        .state('settings_internal', {
            url: '/settings_internal',
            templateUrl: '../views/internal_users/settings_internal.html',
            controller: settingsAdminController
        })

        .state('create_account', {
            url: '/create_account',
            templateUrl: '../views/internal_users/create_account.html',
            controller: internalEmployeeController
        })

        .state('system_log', {
            url: '/system_log',
            templateUrl: '../views/internal_users/system_log.html',
            controller: logFileController
        });


}]);


export default MODULE_NAME;
