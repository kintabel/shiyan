<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>健康科普医院列表</span>
      </div>

      <el-row :gutter="20">
        <el-col
            v-for="hospital in hospitalList"
            :key="hospital.id"
            :span="8"
            style="margin-bottom: 20px"
        >
          <el-card shadow="hover" @click.native="viewArticles(hospital.id)">
            <div slot="header">
              <span style="font-weight: bold">{{ hospital.name }}</span>
            </div>
            <div class="hospital-info">
              <div><i class="el-icon-location"></i> {{ hospital.address }}</div>
              <div><i class="el-icon-phone"></i> {{ hospital.phone }}</div>
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
  name: 'HospitalList',
  data() {
    return {
      hospitalList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0
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
    viewArticles(hospitalId) {
      this.$router.push({
        name: 'UserHealthArticles',
        params: { hospitalId }
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
</style>