<template>
  <div class="login-container">
    <el-card class="login-box">
      <div slot="header" class="clearfix">
        <span>伤口数据分析系统 - 医生登录</span>
      </div>
      <el-form :model="form" ref="form" label-width="80px">
        <el-form-item label="账号">
          <el-input v-model="form.username"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" v-model="form.password"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" block>登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  data() {
    return {
      form: {
        username: 'admin',
        password: 'password'
      }
    }
  },
  methods: {
    async handleLogin() {
      try {
        const res = await this.$http.post('/admin/login', this.form);
        if (res.data.code === 200) {
            sessionStorage.setItem("user", JSON.stringify(res.data.data));
            this.$message.success('登录成功');
            this.$router.push('/');
        } else {
            this.$message.error(res.data.message);
        }
      } catch (e) {
        this.$message.error('登录失败');
      }
    }
  }
}
</script>

<style>
.login-container {
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #f0f2f5;
}
.login-box {
    width: 400px;
}
</style>
