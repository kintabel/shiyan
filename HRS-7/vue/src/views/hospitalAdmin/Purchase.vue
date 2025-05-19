<template>
  <div class="purchase-page">
    <h2 class="page-title">进货管理</h2>
    <el-form :model="purchaseForm" :rules="purchaseRules" ref="purchaseFormRef" label-width="120px">
      <el-form-item label="物品名称">
        <span>{{ purchaseForm.name }}</span>
      </el-form-item>
      <el-form-item label="进货数量" prop="quantity">
        <el-input v-model.number="purchaseForm.quantity" placeholder="请输入进货数量" type="number"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitPurchase">提交进货</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
    <div v-if="message" class="message">{{ message }}</div>
    <div v-if="supplyList.length > 0">
      <h3>供应商列表</h3>
      <table class="supply-table">
        <thead>
        <tr>
          <th>名称</th>
          <th>价格</th>
          <th>电话</th>
          <th>地址</th>
          <th>负责人</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(supply, index) in supplyList" :key="index" @click="selectSupplier(supply)">
          <td>{{ supply.name }}</td>
          <td>{{ supply.price }}</td>
          <td>{{ supply.phone }}</td>
          <td>{{ supply.address }}</td>
          <td>{{ supply.leader }}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
export default {
  name: "Purchase",
  data() {
    return {
      purchaseForm: {
        name: "",
        quantity: 0,
        supplierName: null,
        supplierPrice: null,
        supplierPhone: null,
        supplierAddress: null,
        supplierLeader: null,
        hospitalAdminId: this.$route.query.hospitalAdminId || null,
        department: this.$route.query.department || null
      },
      purchaseRules: {
        quantity: [
          { required: true, message: "请输入进货数量", trigger: "blur" },
          { type: "number", min: 1, message: "进货数量必须为正整数", trigger: "blur" }
        ]
      },
      message: "",
      supplyList: [],
      selectedSupplier: null
    };
  },
  created() {
    const name = this.$route.query.name;
    this.purchaseForm.name = name;
    this.fetchSupplyDetail(name);
  },
  methods: {
    fetchSupplyDetail(name) {
      this.$request.get("/stock/selectSupplier", {
        params: {
          name: name
        }
      }).then(response => {
        if (response.code == 200) {
          this.supplyList = response.data;
          console.log(this.supplyList);
        } else {
          this.error = response.data.message || "获取数据失败";
        }
      })
          .catch(error => {
            console.error("获取数据失败:", error);
            this.error = "获取数据失败，请稍后重试";
          })
          .finally(() => {
            this.loading = false;
          });
    },

    selectSupplier(supply) {
      this.selectedSupplier = supply;
      this.purchaseForm.supplierName = supply.name;
      this.purchaseForm.supplierPrice = supply.price;
      this.purchaseForm.supplierPhone = supply.phone;
      this.purchaseForm.supplierAddress = supply.address;
      this.purchaseForm.supplierLeader = supply.leader;
      console.log(this.selectedSupplier);
    },

    submitPurchase() {
      this.$refs.purchaseFormRef.validate((valid) => {
        if (valid && this.selectedSupplier) {
          const formData = {
            ...this.purchaseForm,
          };
          console.log(formData);
          this.$request.post("/stock/purchase", formData).then(response => {
            if (response.code == 200) {
              this.message = "进货成功";
              // 清空表单数据
              this.purchaseForm.name = "";
              this.purchaseForm.quantity = 0;
              this.purchaseForm.supplierName = null;
              this.purchaseForm.supplierPrice = null;
              this.purchaseForm.supplierPhone = null;
              this.purchaseForm.supplierAddress = null;
              this.purchaseForm.supplierLeader = null;
              this.selectedSupplier = null;
            } else {
              this.message = "进货失败：" + response.data.message;
            }
          }).catch(error => {
            console.error("进货请求失败:", error);
            this.message = "进货请求失败，请稍后重试";
          });
        } else if (!this.selectedSupplier) {
          this.message = "请先选择一个供应商";
        }
      });
    },

    resetForm() {
      this.$refs.purchaseFormRef.resetFields();
      this.message = "";
      this.purchaseForm.supplierId = null;
      this.purchaseForm.supplierName = null;
      this.purchaseForm.supplierPrice = null;
      this.purchaseForm.supplierPhone = null;
      this.purchaseForm.supplierAddress = null;
      this.purchaseForm.supplierLeader = null;
      this.selectedSupplier = null;
    }
  }
};
</script>

<style scoped>
.purchase-page {
  padding: 20px;
  font-family: Arial, sans-serif;
}

.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: bold;
}

.message {
  margin-top: 10px;
  color: green;
}

.supply-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
}

.supply-table th,
.supply-table td {
  padding: 10px;
  border: 1px solid #ddd;
  text-align: left;
}

.supply-table th {
  background-color: #f2f2f2;
  font-weight: bold;
}

.supply-table tr:nth-child(even) {
  background-color: #f9f9f9;
}

.supply-table tr:hover {
  background-color: #f1f1f1;
}
</style>