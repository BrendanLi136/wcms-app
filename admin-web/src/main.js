import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import axios from 'axios'

Vue.config.productionTip = false
Vue.use(ElementUI)

// Axios Config
axios.defaults.baseURL = process.env.VUE_APP_BASE_API || 'http://localhost:8080/api'
axios.defaults.withCredentials = true // Important for Session
Vue.prototype.$http = axios

new Vue({
    router,
    render: h => h(App)
}).$mount('#app')
