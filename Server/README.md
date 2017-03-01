Utility App Support Server
==========================

This server acts as a REST-ful endpoint for application calls from the utility
app. It is responsible for acting as a bridge between the app and any other
services the app may need data from.

For development, this server can provide fake data.


Usage
-----

```
$ ./app.js
Will connect to postgres://nklugman:gridwatch@localhost:5432/capstone
Server listening on localhost:3000
```

```
$ ./app.js -?
Usage: app.js [options]

Options:
  -?, --help               Show help                                   [boolean]
  -v, --verbose            Be more verbose                  [count] [default: 1]
  -q, --quiet              Be more quiet                    [count] [default: 0]
  -H, --host               Host to bind this server to    [default: "localhost"]
  -P, --port               Port to listen on                     [default: 3000]
  -u, --postgres-user      User to authenticate to postgres as
                                                           [default: "nklugman"]
  -l, --postgres-login     Login for postgres server      [default: "gridwatch"]
  -h, --postgres-host      Host for postgres server       [default: "localhost"]
  -p, --postgres-port      Port for postgres server              [default: 5432]
  -d, --postgres-database  Database on postgres to connect to
                                                           [default: "capstone"]
```
