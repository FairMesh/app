var d = null;
var data = [];
var xhr = new XMLHttpRequest();
xhr.withCredentials = true;

xhr.addEventListener('readystatechange', function() {
	if (this.readyState === 4) {
		console.log(this.responseText);
		data = this.responseText;
	}
});

xhr.open('GET', 'https://fairmesh-dashboard.mybluemix.net/api/getData');
xhr.setRequestHeader('Cache-Control', 'no-cache');
xhr.send(d);
console.log('reaches me');
console.log(data);
