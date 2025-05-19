<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>物流信息管理</span>
        <div style="float: right">
          <el-button type="primary" @click="refreshLogistics" :loading="loading">
            刷新列表
          </el-button>
        </div>
      </div>

      <el-table
          v-loading="loading"
          :data="logisticsList"
          border
          style="width: 100%"
      >
        <el-table-column prop="order" label="订单号" width="180" />
        <el-table-column prop="userName" label="收货人" width="120" />
        <el-table-column label="联系电话" width="150">
          <template #default="scope">
            {{ formatPhone(scope.row.phone) }}
          </template>
        </el-table-column>
        <el-table-column prop="inAddress" label="收货地址" />
        <el-table-column prop="applicationTime" label="申请时间" width="180" />
        <el-table-column label="状态" width="120">
          <template #default="scope">
            {{ getStatusText(scope.row.status) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button
                v-if="scope.row.status === 0"
                size="mini"
                type="primary"
                @click="showShipDialog(scope.row)"
            >
              发货
            </el-button>
            <span v-else-if="scope.row.status === 1">
              已发货
            </span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 发货对话框 -->
    <el-dialog title="发货信息" :visible.sync="shipDialogVisible" width="50%">
      <el-form :model="shipForm" :rules="shipRules" ref="shipForm" label-width="100px">
        <el-form-item label="发货地址" prop="outAddress">
          <el-input v-model="shipForm.outAddress" placeholder="请输入发货地址"></el-input>
        </el-form-item>
        <el-form-item label="发货人" prop="outName">
          <el-input v-model="shipForm.outName" placeholder="请输入发货人姓名"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="shipDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleShip" :loading="shipLoading">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'LogisticsManagement',
  data() {
    return {
      user: JSON.parse(localStorage.getItem("xm-user") || '{}'),
      loading: false,
      logisticsList: [],
      currentLogistics: null, // 当前操作的物流记录
      shipDialogVisible: false, // 发货对话框显示状态
      shipLoading: false, // 发货操作加载状态
      shipForm: {
        outAddress: '',
        outName: ''
      },
      shipRules: {
        outAddress: [
          { required: true, message: '请输入发货地址', trigger: 'blur' },
          { min: 5, max: 100, message: '长度在 5 到 100 个字符', trigger: 'blur' }
        ],
        outName: [
          { required: true, message: '请输入发货人姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ]
      }
    };
  },
  methods: {
    refreshLogistics() {
      this.getLogisticsList();
    },

    getLogisticsList() {
      this.loading = true;
      this.$request.get('/admin/logistics', {
        params: {
          adminId: this.user.id
        }
      })
          .then(response => {
            if (response.code == 200) {
              this.logisticsList = response.data;
            } else {
              this.$message.error(response.data.message || '获取物流信息失败');
            }
          })
          .catch(error => {
            console.error('获取物流信息失败:', error);
            this.$message.error('请求失败，请稍后重试');
          })
          .finally(() => {
            this.loading = false;
          });
    },

    formatPhone(phone) {
      if (phone && phone.length === 11) {
        return phone.substring(0, 3) + '****' + phone.substring(7);
      }
      return phone;
    },

    getStatusText(status) {
      switch (status) {
        case 0: return '待发货'
        case 1: return '已发货'
        default: return '未知状态'
      }
    },

    // 显示发货对话框
    showShipDialog(logistics) {
      this.currentLogistics = logistics;
      this.shipForm = {
        outAddress: '',
        outName: ''
      };
      this.shipDialogVisible = true;
      this.$nextTick(() => {
        this.$refs.shipForm.clearValidate();
      });
    },

    // 处理发货操作
    handleShip() {
      this.$refs.shipForm.validate(valid => {
        if (valid) {
          this.shipLoading = true;
          console.log(this.currentLogistics.id, this.shipForm.outAddress, this.shipForm.outName)
          this.$request.post('admin/logistics/ship', null,{
            params:{
              orderId: this.currentLogistics.id,
              outAddress: this.shipForm.outAddress,
              outName: this.shipForm.outName
            }
          })
              .then(response => {
                if (response.code == 200) {
                  this.$message.success('发货成功');
                  this.shipDialogVisible = false;
                  this.getLogisticsList(); // 刷新列表
                } else {
                  this.$message.error(response.message || '发货失败');
                }
              })
              .catch(error => {
                console.error('发货失败:', error);
                this.$message.error('发货失败，请稍后重试');
              })
              .finally(() => {
                this.shipLoading = false;
              });
        }
      });
    }
  },
  mounted() {
    this.getLogisticsList();
  }
};
</script>

<style scoped>
.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both;
}

.app-container {
  padding: 20px;
}

.el-table {
  margin-top: 10px;
}
</style>