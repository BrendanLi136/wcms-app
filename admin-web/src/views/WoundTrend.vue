<template>
  <div>
    <h2>伤口演变分析</h2>
    
    <div style="margin-bottom: 20px;">
        <span>选择患者: </span>
        <el-select v-model="selectedPatientId" filterable placeholder="请选择患者" @change="loadTrend">
            <el-option
                v-for="item in patients"
                :key="item.id"
                :label="item.name + ' (' + item.phone + ')'"
                :value="item.id">
            </el-option>
        </el-select>
    </div>

    <div v-loading="loading">
        <!-- ECharts Line Chart -->
        <div id="trendChart" style="width: 100%; height: 400px; margin-bottom: 30px;"></div>

        <!-- Detail Dialog -->
        <el-dialog title="伤口详情" :visible.sync="dialogVisible" width="60%">
            <div v-if="currentRecord">
                <el-row :gutter="20">
                    <el-col :span="12">
                         <img v-for="(url, index) in parseImages(currentRecord.imagePaths)" 
                              :key="index" 
                              :src="formatUrl(url)" 
                              style="width: 100%; margin-bottom: 10px; border-radius: 4px;"/>
                    </el-col>
                    <el-col :span="12">
                        <p><strong>上传时间:</strong> {{ formatTime(currentRecord.createTime) }}</p>
                        <p><strong>分析结果:</strong> {{ currentRecord.analysisResult }}</p>
                        <p><strong>类型:</strong> {{ currentRecord.woundType || 'N/A' }}</p>
                        <el-divider></el-divider>
                        <p><strong>医生评估/结论:</strong></p>
                        <el-input 
                            type="textarea" 
                            :rows="4" 
                            v-model="evaluation"
                            placeholder="请输入专业评估意见...">
                        </el-input>
                        <div style="margin-top: 10px; text-align: right;">
                             <el-button type="primary" @click="saveEvaluation">保存结论</el-button>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </el-dialog>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
    data() {
        return {
            patients: [],
            selectedPatientId: null,
            loading: false,
            chart: null,
            records: [],
            dialogVisible: false,
            currentRecord: null,
            evaluation: ''
        }
    },
    created() {
        this.fetchPatients();
    },
    methods: {
        async fetchPatients() {
            try {
                // Fetch all patients (simple page request with large size for demo)
                const res = await this.$http.get('/admin/patient/list', { params: { size: 100 } });
                if(res.data.code === 200) {
                    this.patients = res.data.data.records;
                }
            } catch(e) { console.error(e); }
        },
        async loadTrend(val) {
            if(!val) return;
            this.loading = true;
            try {
                const res = await this.$http.get('/admin/wound/trend/' + val);
                if(res.data.code === 200) {
                    this.records = res.data.data;
                    this.initChart();
                }
            } finally {
                this.loading = false;
            }
        },
        initChart() {
            if(this.chart) {
                this.chart.dispose();
            }
            const chartDom = document.getElementById('trendChart');
            this.chart = echarts.init(chartDom);
            
            // Prepare Data
            const dates = this.records.map(r => this.formatTime(r.createTime));
            // Demo metric: let's assume we extract 'area' or just plot counts implicitly. 
            // For now, let's just plot points on a timeline where Y-axis has no real metric 
            // OR maybe we can parse 'analysisResult' if it had numbers.
            // As a fallback, we plot a dummy trend (e.g., healing score) or just interactive points.
            // Requirement says "Wound Pictures on timeline". 
            // ECharts supports custom formatted tooltips or 'scatter' plot with images on axis.
            
            // Simpler approach: Time Axis Line Chart. Y-axis is just dummy "Event". 
            
            const option = {
                title: { text: '伤口恢复时间轴' },
                tooltip: {
                    trigger: 'axis',
                    formatter: (params) => {
                        const index = params[0].dataIndex;
                        const r = this.records[index];
                        return `${r.createTime}<br/>点击查看详情`;
                    }
                },
                xAxis: {
                    type: 'category',
                    data: dates
                },
                yAxis: {
                    type: 'value',
                    name: '记录节点'
                },
                series: [{
                    data: this.records.map((_, i) => 1), // Dummy Y value to just put points on line
                    type: 'line',
                    symbolSize: 20,
                    cursor: 'pointer'
                }]
            };

            this.chart.setOption(option);
            
            this.chart.on('click', params => {
                const index = params.dataIndex;
                this.openDetail(this.records[index]);
            });
        },
        formatUrl(path) {
            if (!path) return '';
            // Assuming imagePaths is JSON array string like "[\"/uploads/xxx.jpg\"]"
             try {
                 const arr = JSON.parse(path);
                 if (Array.isArray(arr) && arr.length > 0) {
                      return this.$http.defaults.baseURL.replace('/api/admin', '') + arr[0]; 
                 }
             } catch(e) {
                 return path; // Fallback if plain string
             }
             return '';
        },
        parseImages(path) {
             try {
                 const arr = JSON.parse(path);
                 return Array.isArray(arr) ? arr : [path];
             } catch(e) {
                 return [path];
             }
        },
        formatTime(arr) {
            return String(arr).replace('T', ' ');
        },
        openDetail(record) {
            this.currentRecord = record;
            this.evaluation = record.doctorEvaluation || '';
            this.dialogVisible = true;
        },
        async saveEvaluation() {
            if(!this.currentRecord) return;
            try {
                await this.$http.post(`/admin/wound/${this.currentRecord.id}/evaluation`, {
                    evaluation: this.evaluation
                });
                this.$message.success('保存成功');
                this.currentRecord.doctorEvaluation = this.evaluation;
                // Optional: Refresh trend
            } catch(e) {
                this.$message.error('保存失败');
            }
        }
    }
}
</script>
