<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <el-button
            type="text"
            icon="el-icon-arrow-left"
            @click="$router.go(-1)"
        >返回医院列表</el-button>
        <span style="margin-left: 20px">{{ currentHospital }} - 健康科普文章</span>
      </div>

      <el-table
          :data="articleList"
          style="width: 100%"
          @row-click="viewArticleDetail"
      >
        <el-table-column prop="title" label="文章标题" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="publishTime" label="发布时间" width="160" />
      </el-table>

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
  name: 'HealthArticles',
  data() {
    return {
      articleList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      currentHospital: '',
      hospitalId: null
    };
  },
  methods: {
    getArticles() {
      this.loading = true;
      this.$request.get('/user/selectHealthArticlesPage', {
        params: {
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          hospitalId: this.hospitalId
        }
      })
          .then(response => {
            if (response.code == 200) {
              this.articleList = response.data.list;
              this.total = response.data.total;
            }
          })
          .finally(() => {
            this.loading = false;
          });
    },
    viewArticleDetail(row) {
      this.$router.push({
        name: 'UserArticleDetail',
        params: { id: row.id }
      });
    },
    handleCurrentChange(page) {
      this.currentPage = page;
      this.getArticles();
    },
    getHospitalName() {
      this.$request.get('/hospital/getHospitalName', {
        params: {
          hospitalId: this.hospitalId
        }
      })
          .then(response => {
            if (response.code == 200) {
             this.currentHospital = response.data;
            }
          })
          .finally(() => {
            this.loading = false;
          });
    }
  },
  created() {
    this.hospitalId = this.$route.params.hospitalId;
    this.getArticles();
    this.getHospitalName();
  }
};
</script>

<style scoped>
.el-table >>> tbody tr {
  cursor: pointer;
}
</style>