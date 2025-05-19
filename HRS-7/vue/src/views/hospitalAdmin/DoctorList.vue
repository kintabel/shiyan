<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>医生信息管理</span>
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
                <el-button type="primary" @click="refreshDoctors" :loading="loading">
                  刷新列表
                </el-button>
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
              <el-table-column prop="phone" label="电话" width="150" />
              <el-table-column prop="gender" label="性别" width="80">
              </el-table-column>
              <el-table-column prop="introduction" label="简介" />
              <el-table-column prop="finalSalary" label="薪资"/>
              <el-table-column prop="evaluationCount" label="评价次数"/>
              <el-table-column prop="evaluation" label="评级"/>
              <el-table-column label="操作" width="120">
                <template #default="scope">
                  <el-button
                      size="mini"
                      type="success"
                      @click="handlePromotion(scope.row)"
                      :disabled="!canPromote(scope.row)"
                  >
                    升职
                  </el-button>
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
  name: 'DoctorManagement',
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
  methods: {
    handleDepartmentClick(data) {
      this.selectedDepartment = data;
      this.getDoctorsByDepartment();
    },

    refreshDoctors() {
      if (this.selectedDepartment) {
        this.getDoctorsByDepartment();
      } else {
        this.$message.warning('请先选择科室');
      }
    },

    getDepartments() {
      this.loading = true;
      this.$request.get('/hospital/departmentList', {
        params: {
          userId: this.user.id
        }
      })
          .then(response => {
            if (response.code == 200) {
              this.departments = response.data;
            } else {
              this.$message.error(response.data.message || '获取科室列表失败');
            }
          })
          .catch(error => {
            console.error('获取科室列表失败:', error);
            this.$message.error('请求失败，请稍后重试');
          })
          .finally(() => {
            this.loading = false;
          });
    },

    getDoctorsByDepartment() {
      if (!this.selectedDepartment) return;

      this.loading = true;
      this.$request.get('/doctor/selectDocByAdminIdAndDepId', {
        params: {
          userId: this.user.id,
          departmentId: this.selectedDepartment.id
        }
      })
          .then(response => {
            if (response.code == 200) {
              this.doctors = response.data;
            } else {
              this.$message.error(response.data.message || '获取医生列表失败');
            }
          })
          .catch(error => {
            console.error('获取医生列表失败:', error);
            this.$message.error('请求失败，请稍后重试');
          })
          .finally(() => {
            this.loading = false;
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
    },

    canPromote(doctor) {
      return doctor.evaluationCount > 100 && doctor.evaluation > 4.3;
    },

    // 处理升职操作
    handlePromotion(doctor) {
      this.$confirm(`确定要将 ${doctor.name} 升职吗?`, '升职确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true;
        this.$request.post('/doctor/promote', { doctorId: doctor.id })
            .then(response => {
              if (response.code == 200) {
                this.$message.success('升职成功');
                this.getDoctorsByDepartment(); // 刷新列表
              } else {
                this.$message.error(response.message || '升职失败');
              }
            })
            .catch(error => {
              console.error('升职失败:', error);
              this.$message.error('升职失败，请稍后重试');
            })
            .finally(() => {
              this.loading = false;
            });
      }).catch(() => {
        this.$message.info('已取消升职操作');
      });
    }

  },
  mounted() {
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

.el-tree {
  background: transparent;
}

.el-table {
  margin-top: 10px;
}
.el-button.is-disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>