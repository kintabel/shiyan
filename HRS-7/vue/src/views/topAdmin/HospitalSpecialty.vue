<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>医院列表-评级</span>
      </div>

      <el-row :gutter="20">
        <el-col
            v-for="hospital in hospitalList"
            :key="hospital.id"
            :span="8"
            style="margin-bottom: 20px"
        >
          <el-card shadow="hover">
            <div slot="header" class="hospital-header">
              <span style="font-weight: bold">{{ hospital.name }}</span>
              <el-button
                  size="mini"
                  type="primary"
                  @click.stop="setSpecialty(hospital.id)"
                  :loading="hospital.id === ratingLoadingId"
              >更新擅长疾病</el-button>
            </div>
            <div class="hospital-info">
              <div><i class="el-icon-location"></i> {{ hospital.address }}</div>
              <div><i class="el-icon-phone"></i> {{ hospital.phone }}</div>
              <div><i class="el-icon-menu"></i>
                {{ hospital.specialty || '无' }}
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-pagination
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next"
          :total="total"
          style="margin-top: 20px"
      ></el-pagination>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'HospitalListSpecialty',
  data() {
    return {
      hospitalList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      ratingLoadingId: null // 正在评级的医院ID
    };
  },
  methods: {
    getHospitals() {
      this.loading = true;
      this.$request.get('/hospital/selectPage', {
        params: {
          pageNum: this.currentPage,
          pageSize: this.pageSize
        }
      })
          .then(response => {
            if (response.code == 200) {
              this.hospitalList = response.data.list;
              this.total = response.data.total;
            }
          })
          .finally(() => {
            this.loading = false;
          });
    },

    // 医院评级
    setSpecialty(hospitalId) {
      this.$confirm('确定要给该医院评定擅长疾病吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.ratingLoadingId = hospitalId;
        this.$request.post(`/admin/hospital-specialty/${hospitalId}`)
            .then(response => {
              if (response.code == 200) {
                this.$message.success('评定成功');
                this.getHospitals(); // 刷新列表
              }
            })
            .catch(error => {
              this.$message.error('评级失败');
            })
            .finally(() => {
              this.ratingLoadingId = null;
            });
      }).catch(() => {
        this.$message.info('已取消评级');
      });
    },


    handleCurrentChange(page) {
      this.currentPage = page;
      this.getHospitals();
    }
  },
  mounted() {
    this.getHospitals();
  }
};
</script>

<style scoped>
.hospital-info {
  line-height: 1.8;
  color: #666;
}
.hospital-info i {
  margin-right: 5px;
  color: #409EFF;
}
.hospital-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>