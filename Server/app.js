//Adding module
var express = require('express'); 
var mustacheExpress = require('mustache-express'); 
var bodyParser = require('body-parser');
var pg = require('pg')
var conString = "postgres://postgres:gridwatch@localhost:5432/gridwatch";
//Initialize
var app = express();

app.get('/newsfeed', function (req, res) {
  console.log("newsfeed");
  var time = req.query.time;
  var client = new pg.Client(conString);
  client.connect();
  var query = client.query("SELECT * From newsfeed where time > \'" + time +"\'");
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

// Start up server on port 3000 on host localhost or Heroku
var server = app.listen(3000, "192.168.1.5", function () {
  console.log('Server on localhost listening on port 3000');
});
