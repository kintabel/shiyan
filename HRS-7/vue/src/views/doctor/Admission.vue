<template>
  <div class="admission-container">
    <h2>入院办理</h2>

    <el-card class="patient-info">
      <div slot="header">
        <span>患者信息</span>
      </div>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="姓名">{{ patientInfo.userName }}</el-descriptions-item>
        <el-descriptions-item label="医院">{{ patientInfo.hospitalName }}</el-descriptions-item>
        <el-descriptions-item label="科室">{{ patientInfo.departmentName }}</el-descriptions-item>
        <el-descriptions-item label="主治医生">{{ patientInfo.doctorName }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-crd class="admission-form">
      <div slot="header">
        <span>入院信息</span>
      </div>
      <el-form ref="form" :model="formData" label-width="120px">
        <!-- 入院时间 -->
        <el-form-item label="入院日期" prop="admissionDate">
          <el-date-picker
              v-model="formData.admissionDate"
              type="date"
              placeholder="选择入院日期"
              style="width: 100%"
              value-format="yyyy-MM-dd"
              :default-value="new Date()"
          ></el-date-picker>
        </el-form-item>

        <!-- 入院类型 -->
        <el-form-item label="入院类型" prop="admissionType">
          <el-select
              v-model="formData.admissionType"
              placeholder="请选择入院类型"
              style="width: 100%"
          >
            <el-option label="普通入院" value="普通"></el-option>
            <el-option label="急诊入院" value="急诊"></el-option>
            <el-option label="VIP入院" value="VIP"></el-option>
          </el-select>
        </el-form-item>

        <!-- 预交金额 -->
        <el-form-item label="预交金额" prop="prepayment">
          <el-input-number
              v-model="formData.prepayment"
              :min="1000"
              :step="500"
              style="width: 100%"
              placeholder="请输入预交金额"
          ></el-input-number>
        </el-form-item>

        <!-- 病情描述 -->
        <el-form-item label="手术" prop="condition">
          <el-input
              v-model="formData.surgery"
              type="textarea"
              :rows="3"
              placeholder="简要描述手术"
          ></el-input>
        </el-form-item>
      </el-form>
    </el-crd>

    <div class="action-buttons">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确认办理</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Admission',
  data() {
    return {
      patientInfo: {
        id: this.$route.params.id,
        userName: this.$route.params.userName || '',
        hospitalName: this.$route.params.hospitalName || '',
        departmentName: this.$route.params.departmentName || '',
        doctorName: this.$route.params.doctorName || ''
      },
      formData: {
        admissionDate: this.formatDate(new Date()), // 默认当天，格式化为YYYY-MM-DD
        admissionType: '普通',
        prepayment: 3000,
        surgery: ''
      }
    }
  },
  methods: {
    // 提交表单
    handleSubmit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          const admissionData = {
            ...this.formData,
            ...this.patientInfo
          }
          console.log(admissionData)
          this.$request.post('/report/admission', admissionData)
              .then(response => {
                this.$message.success('入院办理成功')
                this.$router.go(-1) // 返回上一页
              })
              .catch(error => {
                console.error('入院办理失败:', error)
                this.$message.error(error.response?.data?.message || '入院办理失败')
              })
        }
      })
    },

    formatDate(date) {
      const d = new Date(date)
      const year = d.getFullYear()
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },

    // 取消
    handleCancel() {
      this.$confirm('确定要取消入院办理吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$router.go(-1) // 返回上一页
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.admission-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.patient-info,
.admission-form {
  margin-bottom: 20px;
}

.action-buttons {
  text-align: center;
  margin-top: 20px;
}

.el-descriptions {
  margin-top: 10px;
}
</style>