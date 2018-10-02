import Vue from "vue";
import App from "./App.vue";
import VueChartkick from "vue-chartkick";
import Chart from "chart.js";
import * as VueGoogleMaps from "vue2-google-maps";

Vue.config.productionTip = false;

Vue.use(VueChartkick, { adapter: Chart });
Vue.use(VueGoogleMaps, {
  load: {
    key: "AIzaSyD64FxzbB5yCjm1YW0urEEwrsP76KNYinI",
    libraries: "places"
  }
});

new Vue({
  render: h => h(App)
}).$mount("#app");
