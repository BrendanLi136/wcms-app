<template>
  <div class="config-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>AI 模型配置</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="handleAdd">新增配置</el-button>
      </div>
      
      <el-table :data="configs" border style="width: 100%">
        <el-table-column prop="modelUrl" label="模型地址" show-overflow-tooltip></el-table-column>
        <el-table-column prop="modelType" label="模型类型/名"></el-table-column>
        <el-table-column prop="apiKey" label="API Key">
            <template slot-scope="scope">
                {{ scope.row.apiKey ? '********' : '未设置' }}
            </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注"></el-table-column>
        <el-table-column label="状态" width="100">
            <template slot-scope="scope">
                <el-tag :type="scope.row.isActive === 1 ? 'success' : 'info'">
                    {{ scope.row.isActive === 1 ? '启用' : '禁用' }}
                </el-tag>
            </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
            <template slot-scope="scope">
                <el-button type="text" @click="handleEdit(scope.row)">编辑</el-button>
                <el-button type="text" style="color:red" @click="handleDelete(scope.row.id)">删除</el-button>
            </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="50%">
        <el-form :model="form" label-width="100px">
            <el-form-item label="模型地址">
                <el-input v-model="form.modelUrl" placeholder="例如 https://api.deepseek.com/v1"></el-input>
            </el-form-item>
            <el-form-item label="模型类型">
                <el-input v-model="form.modelType" placeholder="例如 deepseek-chat"></el-input>
            </el-form-item>
            <el-form-item label="API Key">
                <el-input v-model="form.apiKey" type="password" show-password></el-input>
            </el-form-item>
            <el-form-item label="备注">
                <el-input v-model="form.remark"></el-input>
            </el-form-item>
            <el-form-item label="启用状态">
                <el-switch v-model="form.isActive" :active-value="1" :inactive-value="0"></el-switch>
            </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
            <el-button @click="dialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="submitForm">确 定</el-button>
        </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
    data() {
        return {
            configs: [],
            dialogVisible: false,
            dialogTitle: '',
            form: {
                id: null,
                modelUrl: '',
                modelType: '',
                apiKey: '',
                remark: '',
                isActive: 0
            }
        }
    },
    created() {
        this.fetchConfigs();
    },
    methods: {
        async fetchConfigs() {
            const res = await this.$http.get('/admin/ai-config/list');
            this.configs = res.data.data;
        },
        handleAdd() {
            this.dialogTitle = '新增配置';
            this.form = { id: null, modelUrl: '', modelType: '', apiKey: '', remark: '', isActive: 0 };
            this.dialogVisible = true;
        },
        handleEdit(row) {
            this.dialogTitle = '编辑配置';
            this.form = { ...row };
            this.dialogVisible = true;
        },
        async submitForm() {
            if(!this.form.modelUrl || !this.form.modelType || !this.form.apiKey) {
                return this.$message.warning('请填写必填项');
            }
            const res = await this.$http.post('/admin/ai-config/save', this.form);
            if(res.data.code === 200) {
                this.$message.success('保存成功');
                this.dialogVisible = false;
                this.fetchConfigs();
            }
        },
        async handleDelete(id) {
            await this.$confirm('确定删除此配置吗?', '警告', { type: 'warning' });
            const res = await this.$http.delete('/admin/ai-config/' + id);
            if(res.data.code === 200) {
                this.$message.success('删除成功');
                this.fetchConfigs();
            }
        }
    }
}
</script>

<style scoped>
.config-container {
    padding: 20px;
}
</style>
