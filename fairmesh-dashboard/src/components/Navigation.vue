<template>
  <div>
    <h2>Nav Bar</h2>
    <nav>
      <router-link to="/Home">Home</router-link>
    </nav>
  </div>
</template>

<script>
export default {
	name: 'Navigation',
	data() {
		return {
			links: [
				{
					id: 0,
					text: 'Landing Page',
					page: '/HelloWorld'
				},
				{
					id: 1,
					text: 'Home',
					page: '/Home'
				},
				{
					id: 2,
					text: 'About',
					page: '/About'
				}
			]
		};
	}
};
</script>
<style>
.spacing {
	margin-right: 10px;
}
</style>