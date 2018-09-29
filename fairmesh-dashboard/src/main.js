import Vue from 'vue';
import App from './App.vue';
import Home from './components/Home.vue';
import About from './components/About.vue';

Vue.config.productionTip = false;

const routes = {
	'/': App,
	'/home': Home
};

new Vue({
	el: '#app',
	data: {
		currentRoute: window.location.pathname
	},
	computed: {
		ViewComponent() {
			return routes[this.currentRoute] || About;
		}
	},
	render(h) {
		return h(this.ViewComponent);
	}
});
