#!/usr/bin/env node

// Modules
var bodyParser = require('body-parser');
var express = require('express'); 
var mustacheExpress = require('mustache-express'); 
var pg = require('pg')

// Argument parsing
var argv = require('yargs')
  .usage('Usage: $0 [options]')

  .help('?')
  .alias('?', 'help')

  .alias('v', 'verbose')
  .describe('v', 'Be more verbose')
  .default('v', 1)
  .count('verbose')

  .alias('q', 'quiet')
  .describe('q', 'Be more quiet')
  .default('q', 0)
  .count('quiet')

  .alias('H', 'host')
  .describe('H', 'Host to bind this server to')
  .default('H', 'localhost')

  .alias('P', 'port')
  .describe('P', 'Port to listen on')
  .default('P', 3000)

  .alias('u', 'postgres-user')
  .describe('u', 'User to authenticate to postgres as')
  .default('u', 'nklugman')

  .alias('l', 'postgres-login')
  .describe('l', 'Login for postgres server')
  .default('l', 'gridwatch')

  .alias('h', 'postgres-host')
  .describe('h', 'Host for postgres server')
  .default('h', 'localhost')

  .alias('p', 'postgres-port')
  .describe('p', 'Port for postgres server')
  .default('p', 5432)

  .alias('d', 'postgres-database')
  .describe('d', 'Database on postgres to connect to')
  .default('d', 'capstone')

  .argv;

const VERBOSE_LEVEL = argv.verbose - argv.quiet;
function WARN()  { VERBOSE_LEVEL >= 0 && console.log.apply(console, arguments); }
function INFO()  { VERBOSE_LEVEL >= 1 && console.log.apply(console, arguments); }
function DEBUG() { VERBOSE_LEVEL >= 2 && console.log.apply(console, arguments); }

// Package constants
//const postgresURI = "postgres://postgres:gridwatch@localhost:5432/gridwatch";
//const postgresURI = "postgres://nklugman:gridwatch@localhost:5432/capstone";
const postgresURI = "postgres://" +
  argv['postgres-user'] +
  ":" +
  argv['postgres-login'] +
  "@" +
  argv['postgres-host'] +
  ":" +
  argv['postgres-port'] +
  "/" +
  argv['postgres-database'];
INFO("Will connect to " + postgresURI);


//////////////////////////////////////////////////////////////////////////////
// Begin applicaiton routes

// Grab an express server instance
var app = express();

// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// Legacy section
//
// TODO migrate into a versioned API

app.get('/newsfeed', function (req, res) {
  console.log("newsfeed");

    var time = req.query.time;

    var client = new pg.Client(postgresURI);
    
    client.connect();
    var queryExpr;
    console.log(time);
    if (!time || time == "null") {
      queryExpr = "SELECT * From news_feed";
    } else {
      queryExpr = "SELECT * From news_feed where time > \'" + time +"\'";
    }
    var query = client.query(queryExpr);
    query.on("row", function (row, result) {
         result.addRow(row);
    });
    query.on("end", function (result) {
        
     // On end JSONify and write the results to console and to HTML output
     console.log(JSON.stringify(result.rows, null, "    "));
        res.writeHead(200, {'Content-Type': 'text/plain'});
        res.write(JSON.stringify(result.rows) + "\n");
        res.end();
     });

});
app.get('/postpaid', function (req, res) {
  console.log("postpaid");

    var date = req.query.date;
    var account = req.query.account;
    var client = new pg.Client(postgresURI);
    client.connect();
    var queryExpr;
    if (!date || date == "null") {
      queryExpr = "SELECT * From postpaid where account = \'" + account+"\'";
    } else {
      queryExpr = "SELECT * From postpaid where month > \'" + date +"\'and account = \'" + account +"\'";
    }
    console.log(queryExpr);
    var query = client.query(queryExpr);
    query.on("row", function (row, result) {
         result.addRow(row);
    });
    query.on("end", function (result) {
        
     // On end JSONify and write the results to console and to HTML output
     console.log(JSON.stringify(result.rows, null, "    "));
        res.writeHead(200, {'Content-Type': 'text/plain'});
        res.write(JSON.stringify(result.rows) + "\n");
        res.end();
     });
  

});


// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// API v1
app.get('/api/v1/token_balance', function (req, res) {
  INFO("token_balance");

  res.writeHead(200, {'Content-Type': 'text/plain'});
  res.write("10\n");
  res.end();
});


//////////////////////////////////////////////////////////////////////////////
// Start application server

process.on('uncaughtException', (err) => {
  console.log('whoops! there was an error', err.stack);
});
var server = app.listen(argv.port, argv.host, function () {
  INFO('Server listening on ' + argv.host + ':' + argv.port);
});
