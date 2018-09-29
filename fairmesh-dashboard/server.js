var express = require('express'),
	app = express();

require('dotenv').config();

var ibmdb = require('ibm_db'),
	cn = process.env.DB_CON_INFO;
var req = require('request');

var port = process.env.PORT || 8080;

const bodyParser = require('body-parser');

app.use(bodyParser.json());
app.use(express.static(__dirname + '/dist'));
app.use(
	bodyParser.urlencoded({
		extended: true
	})
);

app.listen(port);
console.log('Listening on port ', port);

app.post('/api/uploadMessage', function(request, response) {
	var options = {
		method: 'GET',
		url: 'https://gateway.watsonplatform.net/natural-language-understanding/api/v1/analyze',
		qs: {
			version: '2018-03-19',
			text: request.body['message'],
			features: 'keywords,entities',
			'entities.model': process.env.WATSON_MODEL
		},
		headers: {
			'Cache-Control': 'no-cache',
			'Content-Type': 'application/json'
		},
		auth: {
			user: process.env.WATSON_USER,
			pass: process.env.WATSON_PASS
		}
	};

	req(options, function(error, resp, body) {
		if (error) throw new Error(error);

		var watson_resp = JSON.parse(resp.body);
		if (watson_resp.hasOwnProperty('entities')) {
			var entities = watson_resp.entities;
		} else {
			var entities = [];
		}
		//console.log(entities);
		ibmdb.open(cn, function(err, conn) {
			if (err) {
				return console.log(err);
			}

			var q = `SELECT ID FROM FAIRMESH_2 WHERE SENDER='${
				request.body['sender']
			}' AND INTERNAL_RECIEVED_TIME='${request.body['time'].replace(/\./g, '')}' AND RECIPIENT='${
				request.body['recipient']
			}'`;
			console.log(q);
			conn.query(q, function(e, result) {
				if (e) {
					console.log(e);
				} else {
					if (result.length == 0) {
						console.log('true');
						var query = `INSERT INTO FAIRMESH_2 (RAW_TEXT, SENDER, INTERNAL_RECIEVED_TIME, LATITUDE, LONGITUDE, RECIPIENT${
							entities.length > 0 ? ', RESOURCES' : ''
						}) VALUES (`;
						Object.keys(request.body).forEach(function(key) {
							if ('type'.localeCompare(key) != 0) {
								if ('time'.localeCompare(key) == 0) {
									query += "'" + request.body[key].replace(/\./g, '') + "',";
								} else if ('message'.localeCompare(key) == 0) {
									query += "'" + request.body[key].replace(/'/g, "''") + "',";
								} else {
									query += "'" + request.body[key] + "',";
								}
							}
						});
						if (entities.length > 0) {
							query += "'" + JSON.stringify(entities) + "',";
						}
						query = query.substring(0, query.length - 1) + ')';
						console.log(query);
						conn.queryResult(query, function(err, result) {
							if (err) {
								console.log(err);
								response.sendStatus(500);
							} else {
								console.log('success!');
								// console.log('data = ', result.fetchAllSync());
								conn.closeSync();
								response.sendStatus(200);
							}
						});
					} else {
						console.log('false');
					}
				}
			});
		});
	});
});

app.get('/api/getData', function(request, response) {
	ibmdb.open(cn, function(err, conn) {
		if (err) {
			return console.log(err);
		}
		var query = 'SELECT RESOURCES, LATITUDE, LONGITUDE, ID FROM FAIRMESH_2 WHERE RESOURCES IS NOT NULL';
		conn.query(query, function(error, result) {
			if (error) {
				console.log(error);
				response.sendStatus(500);
			} else {
				//console.log(result);
				var clean_data = [];
				result.forEach(function(entry) {
					JSON.parse(entry.RESOURCES).forEach(function(resource) {
						var ind = {
							lat: entry.LATITUDE,
							long: entry.LONGITUDE,
							label: resource.disambiguation.subtype[0],
							value: resource.count,
							type: resource.text
						};
						clean_data.push(ind);
					});
				});
				response.json(clean_data);
			}
		});
	});
});
