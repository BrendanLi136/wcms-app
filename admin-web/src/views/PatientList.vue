<template>
  <div>
    <h2>患者管理</h2>
    <el-form :inline="true" class="demo-form-inline">
      <el-form-item label="姓名">
        <el-input v-model="search" placeholder="患者姓名"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="fetchData">查询</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="name" label="姓名" width="120"></el-table-column>
      <el-table-column prop="gender" label="性别" width="80">
        <template slot-scope="scope">
            {{ scope.row.gender === 1 ? '男' : '女' }}
        </template>
      </el-table-column>
      <el-table-column prop="age" label="年龄" width="80"></el-table-column>
      <el-table-column prop="phone" label="手机号" width="150"></el-table-column>
      <el-table-column prop="history" label="病史"></el-table-column>
      <el-table-column fixed="right" label="操作" width="100">
        <template slot-scope="scope">
          <el-button @click="handleClick(scope.row)" type="text" size="small">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
    data() {
        return {
            tableData: [],
            search: ''
        }
    },
    created() {
        this.fetchData();
    },
    methods: {
        async fetchData() {
            try {
                const res = await this.$http.get('/admin/patient/list', {
                    params: { name: this.search }
                });
                if(res.data.code === 200) {
                    this.tableData = res.data.data.records;
                }
            } catch(e) {
                console.error(e);
            }
        },
        handleClick(row) {
            this.$router.push('/patients/' + row.id);
        }
    }
}
</script>
