<template>
 <div class="col-lg-6">
    <div class="card">
        <div class="card-title">
            <h4>Resources</h4>
            <!-- <p>{{data}} </p> -->
        </div>
        <div class="sales-chart">
            <div id="tr">
                <img v-if="show_bar" src="../assets/lg.double-ring-spinner.gif" class="loading" />
            <pie-chart :data="chartData"></pie-chart>            </div>
        </div>
    </div>
    <!-- /# card -->
</div>
</template>

<script>
export default {
	name: 'BarGraph',
	props: {
		bardata: Array,
        show_bar: Boolean, 
        // resource_class: String
	},
	computed: {
		chartData: function() {
            console.log("BarGraph computed run")
            let d = this.data;
            // console.log(d);
			let cleanData = [];
			let seen_types = [];
			d.forEach(function(e) {
                if (e.label == 'food') {
                    if (seen_types.indexOf(e.type) > -1) {
                        cleanData[seen_types.indexOf(e.type)].value += e.value;
                        // console.log("if block happened")
                    } else {
                        cleanData.push(e);
                        seen_types.push(e.type);
                        // console.log("else block happened")
                    }
            }
            });
			let cleanArray = [];
			cleanData.forEach(function(e) {
				cleanArray.push([e.type, e.value]);
			});
			return cleanArray;
		}
	}
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
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
</style>
