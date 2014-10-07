var express = require('express');
var app = express();
var nap = require('nodealarmproxy');
var config = require('./config.js');
var https = require('https');

var alarm = nap.initConfig({ password:config.password,
	serverpassword:config.serverpassword,
	actualhost:config.host,
	actualport:config.port,
	serverhost:'0.0.0.0',
	serverport:config.port,
	zone:7,
	partition:1,
	proxyenable:true
});

app.get('/', function(req, res){
	console.log('req');
  res.send('hello world');
});

app.get('/status', function(req, res){
  nap.getCurrent(function(currentstate){
  	console.log(currentstate);
  	var smartURL = "https://graph.api.smartthings.com/api/smartapps/installations/"+config.app_id+"/panel/"+
  		currentstate.partition['1'].code.substring(0,3)+"/partition1/X?access_token="+config.access_token;
	console.log('partition smartURL:',smartURL);
	https.get(smartURL, function(res) {
		console.log("Got response: " + res.statusCode);
	}).on('error', function(e) {
		console.log("Got error: " + e.message);
	});
  	for (var zone in currentstate.zone) {
  		smartURL = "https://graph.api.smartthings.com/api/smartapps/installations/"+config.app_id+"/panel/"+currentstate.zone[zone].code.substring(0,3)+"/zone"+zone+"/X?access_token="+config.access_token;
		console.log('smartURL:',smartURL)
		https.get(smartURL, function(res) {
			console.log("Got response: " + res.statusCode);
		}).on('error', function(e) {
			console.log("Got error: " + e.message);
		});
  	}
  });
  res.send('status update');
});

app.get('/arm', function(req,res){
	console.log('received arm request from /arm');
	nap.manualCommand('0331'+config.alarm_pin,function(){
  		console.log('armed armed armed armed');
  		res.send('arming');
  	});
});

app.get('/disarm', function(req,res){
	console.log('received arm request from /disarm');
	nap.manualCommand('0401'+config.alarm_pin,function(){
  		res.send('disarmed');
  	});
});

app.get('/nightarm', function(req,res){
	console.log('received night arm request from /nightarm');
	nap.manualCommand('0711*9'+config.alarm_pin,function(){
		res.send('nightarm');
  	});
});

var zonenum = '1';

var watchevents = config.watchevents;


alarm.on('data', function(data) {
	console.log('npmtest data here:',data);

});

alarm.on('zone', function(data) {
	if (watchevents.indexOf(data.code) != -1) {
		var smartURL = "https://graph.api.smartthings.com/api/smartapps/installations/"+config.app_id+"/panel/"+data.code+"/zone"+data.zone+"/X?access_token="+config.access_token;
		console.log('smartURL:',smartURL);
		https.get(smartURL, function(res) {
			console.log("Got response: " + res.statusCode);
		}).on('error', function(e) {
			console.log("Got error: " + e.message);
		});
	}
	console.log('zone data:',data);
});

alarm.on('partition', function(data) {
	if (watchevents.indexOf(data.code) != -1) {
		var smartURL = "https://graph.api.smartthings.com/api/smartapps/installations/"+config.app_id+"/panel/"+data.code+"/partition"+data.partition+"/X?access_token="+config.access_token;

		if (data.mode) {
			smartURL = "https://graph.api.smartthings.com/api/smartapps/installations/"+config.app_id+"/panel/"+data.code+"/partition"+data.partition+"/"+data.mode+"?access_token="+config.access_token;
		} else {
			smartURL = "https://graph.api.smartthings.com/api/smartapps/installations/"+config.app_id+"/panel/"+data.code+"/partition"+data.partition+"/X?access_token="+config.access_token;
		}
		console.log('smartURL:',smartURL);
		https.get(smartURL, function(res) {
			console.log("Got response: " + res.statusCode);
		}).on('error', function(e) {
			console.log("Got error: " + e.message);
		});
	}
	console.log('partition data:',data);

});

app.listen(8086,'0.0.0.0');