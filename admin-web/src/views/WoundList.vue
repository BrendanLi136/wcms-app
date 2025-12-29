<template>
  <div>
    <h2>伤口记录管理</h2>
    
    <el-table :data="tableData" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="patientName" label="患者姓名" width="120"></el-table-column>
      <el-table-column prop="createTime" label="上传时间" width="180">
          <template slot-scope="scope">
              {{ formatTime(scope.row.createTime) }}
          </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
           <el-tag :type="scope.row.status === 1 ? 'success' : (scope.row.status === 2 ? 'danger' : 'warning')">
                {{ scope.row.status === 1 ? '完成' : (scope.row.status === 2 ? '失败' : '分析中') }}
           </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="aiModel" label="模型" width="100"></el-table-column>
      <el-table-column prop="analysisResult" label="分析结果" show-overflow-tooltip></el-table-column>
      <el-table-column fixed="right" label="操作" width="150">
        <template slot-scope="scope">
          <el-button @click="handleEdit(scope.row)" type="text" size="small">编辑</el-button>
          <el-button v-if="scope.row.status === 2" @click="handleRetry(scope.row)" type="text" size="small" style="color: #E6A23C;">重试</el-button>
          <el-button @click="handleDelete(scope.row)" type="text" size="small" style="color: red;">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div style="margin-top: 20px; text-align: right;">
        <el-pagination
            background
            layout="prev, pager, next"
            :total="total"
            :page-size="size"
            @current-change="handlePageChange">
        </el-pagination>
    </div>

    <!-- Edit Dialog -->
    <el-dialog title="编辑分析结果" :visible.sync="dialogVisible">
        <el-form :model="currentForm">
            <el-form-item label="分析结果">
                <el-input type="textarea" :rows="6" v-model="currentForm.analysisResult"></el-input>
            </el-form-item>
             <el-form-item label="状态">
                <el-select v-model="currentForm.status">
                    <el-option label="分析中" :value="0"></el-option>
                    <el-option label="完成" :value="1"></el-option>
                    <el-option label="失败" :value="2"></el-option>
                </el-select>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitEdit">确定</el-button>
        </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
    data() {
        return {
            tableData: [],
            total: 0,
            page: 1,
            size: 10,
            dialogVisible: false,
            currentForm: {}
        }
    },
    created() {
        this.fetchData();
    },
    methods: {
        async fetchData() {
            try {
                const res = await this.$http.get('/admin/wound/list', {
                    params: { page: this.page, size: this.size }
                });
                if(res.data.code === 200) {
                    this.tableData = res.data.data.records;
                    this.total = res.data.data.total;
                }
            } catch(e) {
                console.error(e);
            }
        },
        handlePageChange(val) {
            this.page = val;
            this.fetchData();
        },
        formatTime(arr) {
            // Spring Boot Default Jackson Array [yyyy, MM, dd, HH, mm, ss] if configured weirdly, 
            // BUT application.yml set pattern. 
            // However, LocalDateTime in JSON usually string "2023-01-01 12:00:00" if proper config.
            return String(arr).replace('T', ' '); 
        },
        handleEdit(row) {
            this.currentForm = { ...row }; // Copy
            this.dialogVisible = true;
        },
        async submitEdit() {
            try {
                const res = await this.$http.put('/admin/wound/' + this.currentForm.id, this.currentForm);
                if(res.data.code === 200) {
                    this.$message.success('更新成功');
                    this.dialogVisible = false;
                    this.fetchData();
                } else {
                     this.$message.error('更新失败');
                }
            } catch(e) {
                 this.$message.error('System Error');
            }
        },
        async handleRetry(row) {
             try {
                 await this.$http.post('/admin/wound/' + row.id + '/retry');
                 this.$message.success('已触发重试');
                 this.fetchData();
             } catch(e) {
                 this.$message.error('Retry Failed');
             }
        },
        async handleDelete(row) {
             this.$confirm('确定删除该记录?', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
             .then(async () => {
                 await this.$http.delete('/admin/wound/' + row.id);
                 this.$message.success('删除成功');
                 this.fetchData();
             });
        }
    }
}
</script>
