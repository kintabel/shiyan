<template>
  <div class="prescribing-administration">
    <h2>开药管理</h2>
    <el-button type="text" @click="$router.go(-1)">返回</el-button>

    <!-- 开药表单 -->
    <el-form :model="prescriptionForm" label-width="120px" class="prescription-form">
      <!-- 药品名称下拉框 -->
      <el-form-item label="药品名称">
        <el-select
            v-model="prescriptionForm.drugName"
            placeholder="请选择药品"
            filterable
            clearable
            @change="handleDrugChange"
        >
          <el-option
              v-for="drug in drugOptions"
              :key="drug.id"
              :label="drug.name"
              :value="drug.name"
          >
            <span>{{ drug.name }}</span>
            <span style="float: right; color: #8492a6; font-size: 13px">
              库存: {{ drug.stock }}
            </span>
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="药品数量">
        <el-input-number
            v-model="prescriptionForm.quantity"
            :min="1"
            :max="getMaxQuantity(prescriptionForm.drugName)"
        ></el-input-number>
      </el-form-item>

      <el-form-item label="用药说明">
        <el-input
            type="textarea"
            v-model="prescriptionForm.instructions"
            placeholder="请输入用药说明"
        ></el-input>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleAddPrescription">添加药品</el-button>
        <el-button type="info" @click="handleResetForm">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 已开药品列表 -->
    <el-table :data="prescriptionList" stripe class="prescription-table">
      <el-table-column prop="drugName" label="药品名称" width="180"></el-table-column>
      <el-table-column prop="quantity" label="数量" width="100"></el-table-column>
      <el-table-column prop="instructions" label="用药说明"></el-table-column>
      <el-table-column label="操作" width="120">
        <template slot-scope="scope">
          <el-button type="danger" size="small" @click="handleDeletePrescription(scope.row)"
          >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <!-- 提交按钮 -->
    <div class="submit-section">
      <el-button type="success" @click="handleSubmitPrescription">提交开药</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PrescribingAdministration',
  data() {
    return {
      user: JSON.parse(localStorage.getItem("xm-user") || '{}'),
      // 开药表单数据
      prescriptionForm: {
        drugId: '', // 药品ID
        drugName: '', // 药品名称
        quantity: 1, // 药品数量
        instructions: '', // 用药说明
      },
      // 已开药品列表
      prescriptionList: [],
      // 药物库存选项
      drugOptions: [],
    };
  },
  created() {
    this.fetchDrugStock(); // 加载药物库存信息
  },
  methods: {
    // 获取药物库存信息
    fetchDrugStock() {
      console.log(this.$route.params.id)
      console.log(this.user.id)
      this.$request
          .get('/stock/selectByDocId', {
            params: {
             doctorId: this.user.id,
            },
          })
          .then((response) => {
            if (response.data && response.data.length > 0) {
              this.drugOptions = response.data.filter((drug) => drug.type === 1); // 只显示药物类型
            }
          })
          .catch((error) => {
            console.error('获取药物库存信息失败:', error);
            this.$message.error('获取药物库存信息失败，请稍后重试');
          });
    },

    // 处理药物选择
    handleDrugChange(selectedDrugName) {
      const selectedDrug = this.drugOptions.find((drug) => drug.name === selectedDrugName);
      if (selectedDrug) {
        this.prescriptionForm.drugName = selectedDrug.name;
        this.prescriptionForm.quantity = 1; // 重置数量
      }
    },

    // 获取药品的最大数量（基于库存）
    getMaxQuantity(drugName) {
      const selectedDrug = this.drugOptions.find((drug) => drug.name === drugName);
      return selectedDrug ? selectedDrug.stock : 100; // 默认最大值为100
    },

    // 添加药品到列表
    handleAddPrescription() {
      if (!this.prescriptionForm.drugName || !this.prescriptionForm.instructions) {
        this.$message.warning('请填写完整的药品信息');
        return;
      }
      this.prescriptionList.push({ ...this.prescriptionForm });
      this.handleResetForm(); // 添加后重置表单
    },

    // 重置表单
    handleResetForm() {
      this.prescriptionForm = {
        drugName: '',
        quantity: 1,
        instructions: '',
      };
    },

    // 删除药品
    handleDeletePrescription(row) {
      this.prescriptionList = this.prescriptionList.filter(
          (item) => item.drugName !== row.drugName
      );
      this.$message.success('删除成功');
    },

    // 提交开药
    handleSubmitPrescription() {
      if (this.prescriptionList.length === 0) {
        this.$message.warning('请至少添加一种药品');
        return;
      }
      this.$confirm('是否确认提交开药信息？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
          .then(() => {
            // 组合要发送的数据
            const dataToSend = {
              prescriptions: this.prescriptionList,
              order: this.$route.params.id
            };
            this.$request.post('/prescribe/submit', dataToSend)
                .then(response => {
                  if (response.code == 200) {
                    this.$message.success('开药信息提交成功');
                    this.prescriptionList = []; // 清空列表
                  } else {
                    this.$message.error(response.data.message || '开药信息提交失败');
                  }
                })
                .catch(error => {
                  console.error('开药信息提交失败:', error);
                  this.$message.error('开药信息提交失败，请稍后重试');
                });
          })
          .catch(() => {
            this.$message.info('已取消提交');
          });
    },
  },
};
</script>

<style scoped>
.prescribing-administration {
  padding: 20px;
}

.prescription-form {
  margin-bottom: 20px;
}

.prescription-table {
  margin-bottom: 20px;
}

.submit-section {
  text-align: right;
}
</style>