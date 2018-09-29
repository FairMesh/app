<template>
  <div id="app">
    <Navigation></Navigation>
    <img alt="Vue logo" src="./assets/logo.png">
    <router-view/>
    <HelloWorld msg="Welcome to Your Vue.js App"/>
  </div>
</template>

<script>
import HelloWorld from './components/HelloWorld.vue';
import Navigation from './components/Navigation';

export default {
	name: 'app',
	components: {
		HelloWorld: HelloWorld,
		Navigation: Navigation
	}
};
</script>

<style>
#app {
	font-family: 'Avenir', Helvetica, Arial, sans-serif;
	-webkit-font-smoothing: antialiased;
	-moz-osx-font-smoothing: grayscale;
	text-align: center;
	color: #2c3e50;
	margin-top: 60px;
}
</style>
