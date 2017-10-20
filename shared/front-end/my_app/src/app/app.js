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
import dashboardAdminController from './controllers/dashBoardAdminController';
import dashboardAdminEditUser from './controllers/dashBoardAdminEditUserController';

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

app.config(function($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.otherwise('/home');

    $stateProvider
        .state('home', {
            url: '/home',
            templateUrl: '../views/login_page.html',
            controller:loginController
        })

        .state('credit_debit', {
            url: '/credit_debit',
            templateUrl: '../views/external_users/credit_debit_external.html'
        })

        .state('fund_transfer_external_user', {
            url: '/fund_transfer_external_user',
            templateUrl: '../views/external_users/fund_transfer_external_user.html'
        })

        .state('payments_external_user', {
            url: '/payments_external_user',
            templateUrl: '../views/external_users/payments_external_user.html'
        })

        .state('requests_to_merchant', {
            url: '/requests_to_merchant',
            templateUrl: '../views/external_users/requests_to_merchant.html'
        })

        .state('settings_ext_customer', {
            url: '/settings_ext_customer',
            templateUrl: '../views/external_users/settings_ext_customer.html'
        })

        .state('statements_external_user', {
            url: '/statements_external_user',
            templateUrl: '../views/external_users/statements_external_user.html'
        })

        .state('critical_transactions', {
            url: '/critical_transactions',
            templateUrl: '../views/internal_users/critical_transactions.html'
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
            templateUrl: '../views/internal_users/dashboard_internal_employee.html'
        })

        .state('dashboard_internal_manager', {
            url: '/dashboard_internal_manager',
            templateUrl: '../views/internal_users/dashboard_internal_manager.html'
        })

        .state('dashboard_external_user', {
            url: '/dashboard_external_user',
            templateUrl: '../views/external_users/dashboard_external_user.html'
        })

        .state('edit_users', {
            url: '/edit_users',
            templateUrl: '../views/internal_users/edit_users.html'
        })

        .state('internal_emp_transactions', {
            url: '/internal_emp_transactions',
            templateUrl: '../views/internal_users/internal_emp_transactions.html'
        })

        .state('internal_mgr_transactions', {
            url: '/internal_mgr_transactions',
            templateUrl: '../views/internal_users/internal_mgr_transactions.html'
        })

        .state('manage_employee', {
            url: '/manage_employee',
            templateUrl: '../views/internal_users/manage_employee.html'
        })

        .state('pending_requests', {
            url: '/pending_requests',
            templateUrl: '../views/internal_users/pending_requests.html'
        })

        .state('request_admin', {
            url: '/request_admin',
            templateUrl: '../views/internal_users/Request_admin.html'
        })

        .state('request_manager', {
            url: '/request_manager',
            templateUrl: '../views/internal_users/Request_manager.html'
        })

        .state('settings_internal', {
            url: '/settings_internal',
            templateUrl: '../views/internal_users/settings_internal.html'
        })

        .state('system_log', {
            url: '/system_log',
            templateUrl: '../views/internal_users/system_log.html'
        });


});


export default MODULE_NAME;
