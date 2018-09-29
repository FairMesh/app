// var data = [
//   { "label":"food" , "type":"pasta" , "value":3 , "lat": 35.1855969290049, "long": -90.00970741110248 },
//   { "label":"food" , "type":"pasta" , "value":3 , "lat": 35.21347742763461, "long": -90.00572813948821 },
//   { "label":"food" , "type":"pasta" , "value":3 , "lat": 35.01423069930661, "long": -89.94573837530717 },
//   { "label":"food" , "type":"water" , "value": 6, "lat": 35.15545119210866, "long": -89.90024966248608 },
//   { "label":"food" , "type":"water" , "value": 6, "lat": 35.199875025107374, "long": -90.01378377068147 },
//   { "label":"food" , "type":"water" , "value": 6, "lat": 35.240500375565894, "long": -89.93073917212509 },
//   { "label":"food" , "type":"water" , "value": 10, "lat": 35.148311038226595, "long": -90.13999420983697 },
//   { "label":"food" , "type":"water" , "value": 10, "lat": 35.188888109342535, "long": -90.02999933671809 },
//   { "label":"food" , "type":"water" , "value": 10, "lat": 35.07671204036667, "long": -89.9617496921127 },
//   { "label":"shelter" , "type":"shelter" , "value":1 , "lat": 35.087173532211764, "long": -89.99249436275421 },
//   { "label":"shelter" , "type":"shelter" , "value":1 , "lat": 35.05418646198019, "long": -90.09808362178858 },
//   { "label":"shelter" , "type":"shelter" , "value":1 , "lat": 35.06714996112299, "long": -90.00101415613311 },
//   { "label":"shelter" , "type":"shelter" , "value":1 , "lat": 35.01451356683817, "long": -90.06810644033968 },
//   { "label":"shelter" , "type":"shelter" , "value":1 , "lat": 35.22802855884473, "long": -89.92724979770044 },
//   { "label":"shelter" , "type":"shelter" , "value":1 , "lat": 35.16531665016951, "long": -89.8978868256975 },
//   { "label":"shelter" , "type":"shelter" , "value":1 , "lat": 35.06167328688178, "long": -90.06494342802985 },
//   { "label":"shelter" , "type":"shelter" , "value":1 , "lat": 35.23419987673139, "long": -90.03277092241346 },
//   { "label":"shelter" , "type":"shelter" , "value":1 , "lat": 35.04579122716009, "long": -90.0115164845494 },
//   { "label":"shelter" , "type":"shelter" , "value":1 , "lat": 35.137656349562285, "long": -90.03857115850712 },
//   { "label": "medicine" , "type": "oxygen tanks", "value":7 , "lat": 35.02885654331775, "long": -89.88951617649091 },
//   { "label": "medicine" , "type": "oxygen tanks", "value":7 , "lat": 35.08398110677632, "long": -90.05301616410708 },
//   { "label": "medicine" , "type": "oxygen tanks", "value":7 , "lat": 35.20612901601374, "long": -90.1395926162571 },
//   { "label": "medicine" , "type": "oxygen tanks", "value":7 , "lat": 35.046900727366626, "long": -89.90669245689509 },
//   { "label": "medicine" , "type": "oxygen tanks", "value":7 , "lat": 35.044737276652306, "long": -90.04214171487585 },
//   { "label": "medicine" , "type": "oxygen tanks", "value":7 , "lat": 35.156619930104824, "long": -89.95976845453136 },
//   { "label": "medicine" , "type": "insulin", "value":4 , "lat": 35.1225473907869, "long": -89.9044047005541 },
//   { "label": "medicine" , "type": "insulin", "value":4 , "lat": 35.04395816828896, "long": -89.97835650678361 },
//   { "label": "medicine" , "type": "insulin", "value":4 , "lat": 35.021641703868724, "long": -90.121298035835 },
//   { "label": "medicine" , "type": "insulin", "value":4 , "lat": 35.03054241168517, "long": -90.06896530651184 },
//   { "label": "medicine" , "type": "asprin", "value":2 , "lat": 35.01952753660551, "long": -89.90005941946968 },
//   { "label": "medicine" , "type": "asprin", "value":2 , "lat": 35.236323322571366, "long": -89.98252772420797 },
//   { "label": "medicine" , "type": "asprin", "value":2 , "lat": 35.020607738527914, "long": -89.98534109876641 },
//   { "label": "medicine" , "type": "asprin", "value":2 , "lat": 35.010009255268066, "long": -90.1108431379481 },
//   { "label": "medicine" , "type": "bandages", "value":4 , "lat": 35.21017252890905, "long": -89.86271145530559 },
//   { "label": "medicine" , "type": "bandages", "value":4 , "lat": 35.06799063493247, "long": -90.09950019846876 },
//   { "label": "medicine" , "type": "bandages", "value":4 , "lat": 35.17682586358417, "long": -90.11223725823812 },
//   { "label": "medicine" , "type": "bandages", "value":4 , "lat": 35.21094133743996, "long": -89.87577582491804 },
//   { "label": "medicine" , "type": "bandages", "value":4 , "lat": 35.109206969293695, "long": -90.07190986389524 },
//   { "label": "medicine" , "type": "bandages", "value":4 , "lat": 35.19386758683379, "long": -90.16847495289865 },

// ]

var barColorArray = [
	'rgb(114, 147, 203)',
	'rgb(225, 151, 76)',
	'rgb(132, 186, 91)',
	'rgb(211, 94, 96)',
	'rgb(128, 133, 133)',
	'rgb(144, 103, 167)',
	'rgb(171, 104, 87)',
	'rgb(204, 194, 16)'
];
var myMap;

$.ajax({
	type: 'GET',
	url: `${process.env.TARGET_URL}/api/getData`,
	success: function(data) {
		$('.loading').hide();
		var charts = new Vue({
			data: {
				allData: data,
				newData: [],
				labelList: ['food']
			},
			methods: {
				makeBarChart: function(type, loc) {
					let svg;
					let xMin, xMax, yMin, yMax;
					let breakPtSize = document.getElementById(loc).offsetWidth - 30;
					let svgWidth = breakPtSize;
					let svgHeight = 0.8 * svgWidth;
					let graphWidth = svgWidth * 0.8;
					let graphHeight = svgHeight * 0.8;
					let nameArray = [];
					let dataArray = [];
					let vOffset = 0.1 * svgHeight;
					let hOffset = 0.1 * svgWidth;

					let chartData = [];
					charts.allData.forEach(function(entry) {
						let flag = false;
						chartData.forEach(function(e) {
							if (entry.label === type && e.type === entry.type) {
								e.value += entry.value;
								flag = true;
							}
						});
						if (!flag && entry.label === type) {
							let newItem = { type: null, value: null };
							newItem.type = entry.type;
							newItem.value = entry.value;
							chartData.push(newItem);
						}
					});

					chartData.forEach(function(entry) {
						dataArray.push(entry.value);
						nameArray.push(entry.type);
					});
					barInterval = graphWidth / (dataArray.length * 6 - 1);
					barWidth = barInterval * 5;

					let dataMax = d3.max(dataArray);

					let yAxis = d3
						.scaleLinear()
						.domain([0, dataMax])
						.range([graphHeight, 0]);
					let xAxis = d3
						.scalePoint()
						.domain(nameArray)
						.range([0, graphWidth - barWidth]);

					let y = d3.axisLeft(yAxis);
					let x = d3.axisBottom(xAxis);

					let graphID = 'svg' + loc;

					if (document.getElementById(graphID) === null) {
						svg = d3
							.select('#' + loc)
							.append('svg')
							.attr('height', svgHeight)
							.attr('width', svgWidth)
							.attr('id', graphID);
					} else {
						d3.select('#' + graphID).remove();
						svg = d3
							.select('#' + loc)
							.append('svg')
							.attr('height', svgHeight)
							.attr('width', svgWidth)
							.attr('id', graphID);
					}

					svg
						.selectAll('rect')
						.data(dataArray)
						.enter()
						.append('rect')
						.attr('height', function(d) {
							return graphHeight - yAxis(d);
						})
						.attr('width', barWidth)
						.attr('fill', function(d, i) {
							return barColorArray[i];
						})
						.attr('x', function(d, i) {
							return 6 * i * barInterval;
						})
						.attr('y', function(d) {
							return yAxis(d);
						})
						.attr('transform', `translate(${hOffset}, ${vOffset})`);

					svg
						.append('g')
						.attr('class', 'axis y')
						.call(y)
						.attr('transform', `translate(${hOffset / 2}, ${vOffset})`);
					svg
						.append('g')
						.attr('class', 'axis x')
						.call(x)
						.attr('transform', `translate(${hOffset + barWidth / 2}, ${svgHeight - vOffset / 2})`);
				},

				makePieChart: function(loc) {
					let chartData = [];
					charts.allData.forEach(function(entry) {
						let flag = false;
						chartData.forEach(function(e) {
							if (e.label === entry.label) {
								e.value += entry.value;
								flag = true;
							}
						});
						if (!flag) {
							let newItem = { label: null, value: null };
							newItem.label = entry.label;
							newItem.value = entry.value;
							chartData.push(newItem);
						}
					});

					let svg;
					let breakPtSize = document.getElementById(loc).offsetWidth - 30;
					let w = breakPtSize,
						h = breakPtSize * 0.8,
						r = breakPtSize / 3;

					let graphID = 'svg' + loc;

					if (document.getElementById(graphID) != null) {
						document.getElementById(graphID).remove();
					}

					let vis = d3
						.select('#' + loc)
						.append('svg')
						.data([chartData])
						.attr('width', w)
						.attr('height', h)
						.attr('id', graphID)
						.append('g')
						.attr('transform', 'translate(' + 0.5 * w + ', ' + 0.5 * h + ')');

					var arc = d3
						.arc()
						.outerRadius(r)
						.innerRadius(0);
					var pie = d3.pie().value(function(d) {
						return d.value;
					});

					var arcs = vis
						.selectAll('g.slice')
						.data(pie)
						.enter()
						.append('g')
						.attr('class', 'slice');

					arcs
						.append('svg:path')
						.attr('fill', function(d, i) {
							return barColorArray[i];
						})
						.attr('d', arc);

					arcs
						.append('svg:text')
						.attr('transform', function(d) {
							d.innerRadius = 0;
							d.outerRadius = r;
							return 'translate(' + arc.centroid(d) + ')';
						})
						.attr('text-anchor', 'middle')
						.text(function(d, i) {
							return chartData[i].label;
						});
				},

				makePinMap: function(loc) {
					let breakPtSize = document.getElementById(loc).offsetWidth - 30;
					document.getElementById(loc).style.height = `${breakPtSize * 0.8}PX`;

					myMap = L.map(loc).setView([35.130829, -90.021705], 11);

					let layer1 = L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
						attribution:
							'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
						maxZoom: 18,
						id: 'mapbox.streets',
						accessToken: 'pk.eyJ1Ijoic3poYW8iLCJhIjoiY2ptaHlwaG54MGYxMDNwbzJtbzhiemg1aCJ9.kc3ygX0kETIvStQsqhzDQA'
					});

					myMap.addLayer(layer1);

					charts.allData.forEach(function(entry) {
						let i = 0;
						if (charts.labelList.indexOf(entry.label) > -1) {
							let index = charts.labelList.indexOf(entry.label);
							let color = barColorArray[index];
							let circle = L.circle([entry.lat, entry.long], {
								color: '#000000',
								fillColor: color,
								fillOpacity: 1,
								radius: 150
							});
							if (entry.type === 'oxygen tanks') {
								circle.options.color = '#FF0000';
								circle.bindPopup('In need of oxygen tanks');
							}
							circle.addTo(myMap);
						} else {
							charts.labelList.push(entry.label);
							let index = charts.labelList.indexOf(entry.label);
							let color = barColorArray[index];

							let circle = L.circle([entry.lat, entry.long], {
								color: '#000000',
								fillColor: color,
								fillOpacity: 1,
								radius: 150
							});
							if (entry.type === 'oxygen tanks') {
								circle.options.color = '#FF0000';
								circle.bindPopup('In need of oxygen tankss');
							}
							circle.addTo(myMap);
						}
					});
				},
				updatePinMap: function(loc) {
					let breakPtSize = document.getElementById(loc).offsetWidth - 30;
					document.getElementById(loc).style.height = `${breakPtSize * 0.8}PX`;

					if (charts.newData.length > 0) {
						let entry = charts.newData[0];
						if (charts.labelList.indexOf(entry.label) > -1) {
							let index = charts.labelList.indexOf(entry.label);
							let color = barColorArray[index];
							let circle = L.circle([entry.lat, entry.long], {
								color: '#000000',
								fillColor: color,
								fillOpacity: 1,
								radius: 150
							});
							if (entry.type === 'oxygen tanks') {
								circle.options.color = '#FF0000';
								circle.bindPopup('In need of oxygen tankss');
							}
							circle.addTo(myMap);
						} else {
							charts.labelList.push(entry.label);
							let index = charts.labelList.indexOf(entry.label);
							let color = barColorArray[index];
							if (entry.type === 'oxygen tanks') {
								circle.options.color = '#FF0000';
								circle.bindPopup('In need of oxygen tankss');
							}

							let circle = L.circle([entry.lat, entry.long], {
								color: '#000000',
								fillColor: color,
								fillOpacity: 1,
								radius: 150
							}).addTo(myMap);
						}
						charts.newData.shift();
					}
				}
			}
		});
		charts.makePinMap('bl');

		setInterval(function() {
			charts.makeBarChart('medicine', 'tr');
			charts.makePieChart('tl');
			charts.makeBarChart('food', 'br');
			charts.updatePinMap('bl');
		}, 1000);

		setInterval(function() {
			// var updatedData = [];
			// var xhr = new XMLHttpRequest();
			// xhr.withCredentials = true;
			// xhr.addEventListener('readystatechange', function() {
			// 	if (this.readyState === 4) {
			// 		updatedData = this.responseText;
			// 	}
			// });
			// xhr.open('GET', 'https://fairmesh-dashboard.mybluemix.net/api/getData');
			// xhr.setRequestHeader('Cache-Control', 'no-cache');
			// xhr.send(updatedData);
			// updatedData.forEach(function(e) {
			// 	charts.allData.push(e);
			// 	charts.newData.push(e);
			// });
		}, 1300);
	},
	error: function(response) {
		alert(response);
	}
});
