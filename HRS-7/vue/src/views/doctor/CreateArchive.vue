<template>
  <div class="hospitalization">
    <!-- 添加返回按钮 -->
    <el-button type="text" @click="$router.go(-1)">返回</el-button>
    <h2>{{ reportType === '入院报告' ? '入院报告' : '出院报告' }}</h2>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">加载中...</div>

    <!-- 错误提示 -->
    <div v-if="error" class="error">{{ error }}</div>

    <!-- 住院档案表单 -->
    <div class="report-form">
      <el-form :model="report" :rules="rules" ref="reportForm" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="报告类型" prop="report_type">
              <el-select v-model="report.reportType" placeholder="请选择报告类型" @change="handleReportTypeChange">
                <el-option label="入院报告" value="入院报告"></el-option>
                <el-option label="出院报告" value="出院报告"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="患者性别" prop="patient_gender">
              <el-select v-model="report.patientGender" placeholder="请选择性别">
                <el-option label="男" value="男"></el-option>
                <el-option label="女" value="女"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="患者年龄" prop="patient_age">
              <el-input v-model.number="report.patientAge" placeholder="请输入年龄"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="患者身高(cm)" prop="patient_height">
              <el-input v-model="report.patientHeight" placeholder="请输入身高"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="患者体重(kg)" prop="patient_weight">
              <el-input v-model="report.patientWeight" placeholder="请输入体重"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="血压(mmHg)" prop="blood_pressure">
              <el-input v-model="report.bloodPressure" placeholder="如:120/80"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="心率(次/分)" prop="heart_rate">
              <el-input v-model.number="report.heartRate" placeholder="请输入心率"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="血常规" prop="complete_blood_count">
              <el-input v-model="report.completeBloodCount" placeholder="请输入血常规数据"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="尿常规" prop="urinalysis">
              <el-input v-model="report.urinalysis" placeholder="请输入尿常规数据"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="传染病" prop="contagious_disease">
              <el-input v-model="report.contagiousDisease" placeholder="请输入传染病情况"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item>
          <el-button type="primary" @click="submitForm">提交</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </div>


    </div>
</template>

<script>
export default {
  name: 'CreateArchive',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'), // 当前用户信息
      report: {
        orderId: this.$route.params.orderId,
        reportType: '入院',
        patientGender: '',
        patientAge: null,
        patientHeight: '',
        patientWeight: '',
        bloodPressure: '',
        heartRate: '',
        completeBloodCount: '',
        urinalysis: '',
        contagiousDisease: '',
      },
      rules: {
        reportType: [
          { required: true, message: '请选择报告类型', trigger: 'change' }
        ],
        patientGender: [
          { required: true, message: '请选择患者性别', trigger: 'change' }
        ],
        patientAge: [
          { required: true, message: '请输入患者年龄', trigger: 'blur' },
          { type: 'number', message: '年龄必须为数字值', trigger: 'blur' }
        ],
        bloodPressure: [
          { required: true, message: '请输入血压值', trigger: 'blur' }
        ],
        heartRate: [
          { required: true, message: '请输入心率', trigger: 'blur' }
        ]
      },
      historyRecords: [],
      loading: false,
      error: '',
      informationId: null // 从路由或参数中获取的住院信息ID
    };
  },
  computed: {
    reportType() {
      return this.report.reportType;
    }
  },
  created() {
  },
  methods: {


    // 报告类型变化处理
    handleReportTypeChange(value) {
      this.report.reportType = value;
    },

    // 提交表单
    submitForm() {
      this.$refs.reportForm.validate(valid => {
        if (valid) {
          this.loading = true;

          this.$request.post('/report/create', this.report)
              .then(response => {
                if (response.code == 200) {
                  this.$message.success('档案提交成功');
                  this.resetForm();
                } else {
                  this.$message.error(response.message || '档案提交失败');
                }
              })
              .catch(error => {
                console.error('档案提交失败:', error);
                this.$message.error('档案提交失败，请稍后重试');
              })
              .finally(() => {
                this.loading = false;
              });
        } else {
          return false;
        }
      });
    },

    // 重置表单
    resetForm() {
      this.$refs.reportForm.resetFields();
      // 重置后保留报告类型和住院信息ID
      this.report.reportType = '入院报告';
    },

  }
};
</script>

<style scoped>
.hospitalization {
  padding: 20px;
}

.loading,
.error {
  text-align: center;
  margin-top: 20px;
  font-size: 16px;
  color: #666;
}

.error {
  color: #f56c6c;
}

.report-form {
  margin-top: 20px;
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.history-records {
  margin-top: 30px;
}

/* 详情对话框样式 */
.report-detail-dialog {
  width: 60%;
  max-width: 800px;
}

.report-detail-dialog p {
  margin: 10px 0;
  line-height: 1.6;
}
</style>