<template>
  <div id="app">

      <Graphs v-bind:data="ibm_db_data" v-bind:show_pie="!show"/>

  </div>
</template>

<script>
import Graphs from "./components/Graphs.vue";

const axios = require("axios");
export default {
  name: "app",
  components: {
    Graphs

  },
  data: function() {
    return {
      ibm_db_data: [],
      show: false
    };
  },
  mounted: function() {
    // fetch data from server every 10 seconds
    window.setInterval(() => {

    var self = this;
    return axios
      .get("https://fairmesh-dashboard.mybluemix.net/api/getData")
      .then(function(resp) {

        self.show = true;
        self.ibm_db_data = resp.data;
      })
      .catch(function(error) {

        console.log("Failed to fetch data");
        console.log(error);
      });
    }, 10000);

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
