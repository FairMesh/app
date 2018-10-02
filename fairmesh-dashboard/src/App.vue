<template>
  <div id="app">
      <PieGraph v-bind:data="ibm_db_data" v-bind:show_pie="!show"/>
  </div>
</template>

<script>
import PieGraph from "./components/PieGraph.vue";
const axios = require("axios");
export default {
  name: "app",
  components: {
    PieGraph
  },
  data: function() {
    return {
      ibm_db_data: [],
      show: false
    };
  },
  methods: {
    //  getData() {}
  },
  mounted: function() {
    // var temp = ["test"];
    var self = this;
    return axios
      .get("https://fairmesh-dashboard.mybluemix.net/api/getData")
      .then(function(resp) {
        console.log("Response", resp);
        self.show = true;
        self.ibm_db_data = resp.data;
      })
      .catch(function(error) {
        console.log("noooo issa error tingg fam");
        console.log(error);
      });
    // window.setInterval(() => {
    // 	this.show = !this.show;
    // }, 1000);
  }
};
</script>

<style>
#app {
  font-family: "Avenir", Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
