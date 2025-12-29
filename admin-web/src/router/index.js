import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
import PatientList from '../views/PatientList.vue'
import PatientDetail from '../views/PatientDetail.vue'

Vue.use(VueRouter)

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/',
        component: Home,
        children: [
            {
                path: '',
                redirect: '/patients'
            },
            {
                path: 'patients',
                name: 'PatientList',
                component: PatientList
            },
            {
                path: 'patients/:id',
                name: 'PatientDetail',
                component: PatientDetail
            },
            {
                path: 'wounds',
                name: 'WoundList',
                component: () => import(/* webpackChunkName: "wound" */ '../views/WoundList.vue')
            },
            {
                path: 'trend',
                name: 'WoundTrend',
                component: () => import(/* webpackChunkName: "trend" */ '../views/WoundTrend.vue')
            }
        ]
    }
]

const router = new VueRouter({
    routes
})

// Navigation Guard
router.beforeEach((to, from, next) => {
    const user = sessionStorage.getItem("user");
    if (to.path !== '/login' && !user) {
        next('/login');
    } else {
        next();
    }
})

export default router
