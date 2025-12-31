<template>
  <div v-if="patient">
    <el-page-header @back="$router.go(-1)" :content="patient.name + ' 的伤口记录'"></el-page-header>
    
    <el-card style="margin-top: 20px;">
        <div slot="header">
            <span>基本信息</span>
        </div>
        <el-descriptions :column="3" border>
            <el-descriptions-item label="姓名">{{ patient.name }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ patient.age }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ patient.gender === 1 ? '男' : '女' }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ patient.phone }}</el-descriptions-item>
            <el-descriptions-item label="病史">{{ patient.history }}</el-descriptions-item>
        </el-descriptions>
    </el-card>

    <!-- Reminders Section -->
    <el-card style="margin-top: 20px;">
        <div slot="header" style="display:flex; justify-content:space-between; align-items:center;">
            <span>提醒设置</span>
            <el-button type="primary" size="mini" @click="showAddReminder">新增提醒</el-button>
        </div>
        <el-table :data="reminders" border style="width: 100%">
            <el-table-column prop="content" label="提醒内容"></el-table-column>
            <el-table-column prop="remindTime" label="提醒时间"></el-table-column>
            <el-table-column prop="frequency" label="频率">
                <template slot-scope="scope">
                    {{ scope.row.frequency === 1 ? '每天' : (scope.row.frequency === 2 ? '每2天' : '单次') }}
                </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
                <template slot-scope="scope">
                    <el-button type="text" style="color:red;" @click="deleteReminder(scope.row.id)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-card>

    <div style="margin-top: 20px;">
        <h3>伤口分析时间轴</h3>
        <el-timeline>
            <el-timeline-item v-for="(activity, index) in activities" :key="index" :timestamp="activity.createTime" placement="top">
                <el-card>
                    <h4>AI 分析结果 (模型: {{activity.aiModel}})</h4>
                    <div style="display: flex; gap: 10px; margin: 10px 0;">
                        <el-image 
                            v-for="(img, i) in JSON.parse(activity.imagePaths || '[]')" 
                            :key="i"
                            style="width: 100px; height: 100px"
                            :src="img" 
                            :preview-src-list="[img]">
                        </el-image>
                    </div>
                    <el-tag :type="activity.status === 1 ? 'success' : (activity.status === 2 ? 'danger' : 'warning')">
                        {{ activity.status === 1 ? '完成' : (activity.status === 2 ? '失败' : '分析中') }}
                    </el-tag>
                    <p style="white-space: pre-wrap; margin-top: 10px;">{{ activity.analysisResult }}</p>
                    <el-button type="text" v-if="activity.status===2">重试</el-button>
                </el-card>
            </el-timeline-item>
        </el-timeline>
    </div>
  
<!-- Dialog -->
  <el-dialog title="新增提醒" :visible.sync="showDialog" width="30%">
      <el-form label-width="80px">
          <el-form-item label="内容">
              <el-input v-model="form.content"></el-input>
          </el-form-item>
          <el-form-item label="时间">
              <el-time-picker v-model="form.remindTime" value-format="HH:mm:ss" placeholder="选择时间"></el-time-picker>
          </el-form-item>
          <el-form-item label="频率">
              <el-select v-model="form.frequency">
                  <el-option label="每天" :value="1"></el-option>
                  <el-option label="每2天" :value="2"></el-option>
              </el-select>
          </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
          <el-button @click="showDialog = false">取消</el-button>
          <el-button type="primary" @click="submitReminder">确定</el-button>
      </span>
  </el-dialog>
</div>
  
</template>

<script>
export default {
    data() {
        return {
            patient: null,
            activities: [],
            reminders: [],
            showDialog: false,
            form: {
                content: '',
                remindTime: '',
                frequency: 1
            }
        }
    },
    created() {
        this.fetchDetails();
    },
    methods: {
        async fetchDetails() {
             const id = this.$route.params.id;
             // Fetch Info
             const pRes = await this.$http.get('/admin/patient/' + id);
             this.patient = pRes.data.data;
             
             // Fetch Wounds
             const wRes = await this.$http.get('/admin/patient/' + id + '/wounds');
             this.activities = wRes.data.data;

             this.fetchReminders();
        },
        async fetchReminders() {
            const id = this.$route.params.id;
            const res = await this.$http.get('/admin/reminder/patient/' + id);
            this.reminders = res.data.data;
        },
        showAddReminder() {
            this.form = { content: '', remindTime: '', frequency: 1 };
            this.showDialog = true;
        },
        async submitReminder() {
            if (!this.form.content || !this.form.remindTime) return this.$message.warning('请完善信息');
            const data = {
                ...this.form,
                patientId: this.patient.id
            };
            const res = await this.$http.post('/admin/reminder/create', data);
            if (res.data.code === 200) {
                this.$message.success('添加成功');
                this.showDialog = false;
                this.fetchReminders();
            } else {
                this.$message.error('失败');
            }
        },
        async deleteReminder(id) {
            await this.$confirm('确定删除吗?', '提示');
            const res = await this.$http.post('/admin/reminder/delete/' + id);
             if (res.data.code === 200) {
                this.$message.success('删除成功');
                this.fetchReminders();
            }
        }
    }
}
</script>
