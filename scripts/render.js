#!/usr/bin/env node

var x = require("../compiled/client.render");

var args = process.argv.slice(2);
x.main.apply(null, args);
