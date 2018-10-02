<template>
<div class = 'container'>
<div class="row">
	<div class="col-lg-4">
				<h4>Resources</h4>
				<!-- <p>{{data}} </p> -->
				<div>
					<img v-if="show_pie" src="../assets/lg.double-ring-spinner.gif" class="loading" />
					<pie-chart :donut="true" :data="chartData" id="overAllPieChart"></pie-chart>            
				</div>
		<!-- /# card -->
	</div>
	<div class="col-lg-4">
		<h4>Medical Supply Requests</h4>
		<img v-if="show_pie" src="../assets/lg.double-ring-spinner.gif" class="loading" />
		<column-chart :data="medBar" id="medBarChart"></column-chart>
	</div>


	<div class="col-lg-4">
		<h4>Food Request</h4>
		<img v-if="show_pie" src="../assets/lg.double-ring-spinner.gif" class="loading" />
		<column-chart :data="foodBar" id="foodBarChart"></column-chart>
	</div>
</div>
  <div class="container" style="height: 100%">
    <h4>Aid Map</h4>
  	<img v-if="show_pie" src="assets/lg.double-ring-spinner.gif" class="loading" />
    <GmapMap :center="mapCenter" :zoom="13" map-type-id="terrain" style="width: 100%; height: 400PX" /> 

  </div>
</div>

</template>

<script>
export default {
  name: "Graphs",
  props: {
    data: Array,
    show_pie: Boolean
  },
  computed: {
    chartData: function() {
      // Filters source data into number of requests in categories (e.g. food, med supplies, etc)
      return this.data.reduce((acc, cur) => {
        const key = cur.label;
        if (key in acc) acc[key] += cur.value;
        else acc[key] = cur.value;
        return acc;
      }, {});
    },
    foodBar: function() {
      // Filters data into different types of food (e.g. food types: pasta, tomato, cucumber, halal, etc)
      const d = this.data.filter(x => x.label === "food");
      return d.reduce((acc, cur) => {
        const key = cur.type;
        if (key in acc) acc[key] += cur.value;
        else acc[key] = cur.value;
        return acc;
      }, {});
    },
    medBar: function() {
      // Filters source data into different types of food ....
      const d = this.data.filter(x => x.label === "medicine");
      return d.reduce((acc, cur) => {
        const key = cur.type;
        if (key in acc) acc[key] += cur.value;
        else acc[key] = cur.value;
        return acc;
      }, {});
    },

    mapCenter: function() {
      // Calculates center of displayed map based on the location of datapoints
      const d = this.data.filter(x => x.long != 0 && x.lat != 0);
      let longArray = [];
      let latArray = [];
      d.forEach(function(e) {
        longArray.push(parseFloat(e.long));
        latArray.push(parseFloat(e.lat));
      });
      let mLong =
        Math.min.apply(Math, longArray) +
        0.5 *
          (Math.max.apply(Math, longArray) - Math.min.apply(Math, longArray));
      let mLat =
        Math.min.apply(Math, latArray) +
        0.5 * (Math.max.apply(Math, latArray) - Math.min.apply(Math, latArray));
      let mc = { lat: 0, lng: 0 };
      mc.lat = mLat;
      mc.lng = mLong;
      return mc;
    }
  }
};
</script>

<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}

.container {
  padding: 50px;
}
.col-lg-4 {
  padding: 1.5em;
}
</style>
