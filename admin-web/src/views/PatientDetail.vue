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
                            :src="'http://localhost:8080' + img" 
                            :preview-src-list="['http://localhost:8080' + img]">
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
  </div>
</template>

<script>
export default {
    data() {
        return {
            patient: null,
            activities: []
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
        }
    }
}
</script>
