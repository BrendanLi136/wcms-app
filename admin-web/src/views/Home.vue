<template>
  <el-container style="height: 100vh;">
    <el-aside width="200px" style="background-color: #304156">
        <el-menu
            default-active="1"
            class="el-menu-vertical-demo"
            background-color="#304156"
            text-color="#fff"
            active-text-color="#409EFF"
            router>
            <el-menu-item index="/patients">
                <i class="el-icon-user"></i>
                <span slot="title">患者管理</span>
            </el-menu-item>
            <el-menu-item index="/wounds">
                <i class="el-icon-document"></i>
                <span slot="title">伤口记录管理</span>
            </el-menu-item>
            <el-menu-item index="/trend">
                <i class="el-icon-data-line"></i>
                <span slot="title">伤口演变分析</span>
            </el-menu-item>
        </el-menu>
    </el-aside>
    <el-container>
      <el-header style="text-align: right; font-size: 12px; border-bottom: 1px solid #ccc; line-height: 60px;">
        <span style="font-size: 16px; margin-right: 20px;">WCMS 医生工作台</span>
        <el-dropdown @command="handleCommand">
          <i class="el-icon-setting" style="margin-right: 15px"></i>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <span>{{ user.nickname || 'Doctor' }}</span>
      </el-header>
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
    data() {
        return {
            user: {}
        }
    },
    created() {
        const u = sessionStorage.getItem("user");
        if(u) this.user = JSON.parse(u);
    },
    methods: {
        handleCommand(cmd) {
            if(cmd === 'logout') {
                sessionStorage.removeItem("user");
                this.$router.push('/login');
            }
        }
    }
}
</script>

<style>
.el-header {
  background-color: #fff;
  color: #333;
}
.el-aside {
  color: #333;
}
</style>
