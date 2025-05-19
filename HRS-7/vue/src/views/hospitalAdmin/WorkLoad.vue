<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>医生工作量统计</span>
      </div>

      <el-row :gutter="20">
        <el-col :span="6">
          <el-card>
            <div slot="header" class="clearfix">
              <span>科室列表</span>
            </div>
            <el-tree
                :data="departments"
                :props="defaultProps"
                @node-click="handleDepartmentClick"
            ></el-tree>
          </el-card>
        </el-col>

        <el-col :span="18">
          <el-card>
            <div slot="header" class="clearfix">
              <span>医生列表 - {{ selectedDepartment ? selectedDepartment.name : '请选择科室' }}</span>
              <div style="float: right">
                <el-button
                    type="primary"
                    @click="generateStatistics"
                    :loading="loading"
                >
                  生成上月工作量统计
                </el-button>
                <el-button
                    type="primary"
                    @click="calculatePerformance"
                    :loading="loading"
                >
                  计算上个月绩效
                </el-button>
                <span class="month-display">统计月份：{{ lastMonth }}</span>
              </div>
            </div>

            <el-table
                v-loading="loading"
                :data="doctors"
                border
                style="width: 100%"
            >
              <el-table-column prop="name" label="姓名" width="120" />
              <el-table-column label="职称" width="120">
                <template #default="scope">
                  {{ getDoctorTitle(scope.row.status) }}
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'WorkloadStatistics',
  data() {
    return {
      user: JSON.parse(localStorage.getItem("xm-user") || '{}'),
      loading: false,
      selectedDepartment: null,
      departments: [],
      doctors: [],
      defaultProps: {
        children: 'children',
        label: 'name'
      }
    };
  },
  computed: {
    lastMonth() {
      const date = new Date();
      date.setMonth(date.getMonth() - 1);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      return `${year}-${month}`;
    },
  },
  methods: {
    handleDepartmentClick(data) {
      this.selectedDepartment = data;
      this.getDoctorsByDepartment();
    },
    generateStatistics() {
      this.loading = true;
      this.$request.post('/admin/workload-statistics', null, {
        params: {  // axios的params会转为URL查询参数
          adminId: this.user.id,
          yearMonth: this.lastMonth
        }
      })
          .then(response => {
            if (response.code == 200) {
              alert('工作量统计生成成功');
            } else {
              alert(response.message || '生成失败');
            }
          })
          .catch(error => {
            console.error('请求失败:', error);
            alert('请求失败，请稍后重试');
          })
          .finally(() => {
            this.loading = false;
          });
    },

    calculatePerformance(){
      this.loading = true;
      this.$request.post('/admin/calculate-performance', null, {
        params: {  // axios的params会转为URL查询参数
          adminId: this.user.id,
          yearMonth: this.lastMonth
        }
      })
          .then(response => {
            if (response.code == 200) {
              alert('绩效计算成功');
            } else {
              alert(response.message || '生成失败');
            }
          })
          .catch(error => {
            console.error('请求失败:', error);
            alert('请求失败，请稍后重试');
          })
          .finally(() => {
            this.loading = false;
          });
    },

    getDepartments() {
      this.$request.get('/hospital/departmentList', {
        params: {
          userId: this.user.id
        }
      })
          .then(response => {
            if (response.code == 200) {
              this.departments = response.data;
            } else {
              alert(response.data.message || '获取科室列表失败');
            }
          })
          .catch(error => {
            console.error('获取科室列表失败:', error);
            alert('请求失败，请稍后重试');
          });
    },
    getDoctorsByDepartment() {
      if (!this.selectedDepartment) return;
      console.log('获取医生列表:', this.selectedDepartment.id);
      this.$request.get('/doctor/selectDocByAdminIdAndDepId', {
        params: {
          userId: this.user.id,
          departmentId: this.selectedDepartment.id
        }
      })
          .then(response => {
            if (response.code == 200) {
              this.doctors = response.data;
              console.log('医生列表:', this.doctors);
            } else {
              alert(response.data.message || '获取医生列表失败');
            }
          })
          .catch(error => {
            console.error('获取医生列表失败:', error);
            alert('请求失败，请稍后重试');
          });
    },
    getDoctorTitle(status) {
      switch (status) {
        case 1: return '医师'
        case 2: return '副主治医师'
        case 3: return '主治医师'
        case 4: return '专家'
        case 5: return '急诊'
        default: return '未知职称'
      }
    }

  },
  mounted() {
    console.log('当前用户信息:', this.user);
    this.getDepartments();
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

.month-display {
  color: #666;
  font-size: 14px;
  margin-left: 10px;
}

.el-tree {
  background: transparent;
}

.el-table {
  margin-top: 10px;
}
</style>