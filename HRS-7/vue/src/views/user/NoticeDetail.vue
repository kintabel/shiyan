<template>
  <div class="app-container article-detail">
    <el-card>
      <div slot="header" class="clearfix">
        <el-button
            type="text"
            icon="el-icon-arrow-left"
            @click="$router.go(-1)"
        >返回公告 列表</el-button>
      </div>

      <div class="article-header">
        <h1 class="article-title">{{ article.title }}</h1>
        <div class="article-meta">
          <span class="publish-time">{{ article.publishTime }}</span>
        </div>
      </div>

      <div class="article-content" v-html="article.content"></div>

    </el-card>
  </div>
</template>

<script>
export default {
  name: 'NoticeDetail',
  data() {
    return {
      article: {
        title: '',
        content: '',
        category: '',
        publishTime: '',
        viewCount: 0,
        likeCount: 0
      },
      likeLoading: false
    };
  },
  methods: {
    getArticleDetail() {
      this.loading = true;
      this.$request.get('/user/getNotice', {
        params: { id: this.$route.params.id }
      })
          .then(response => {
            if (response.code == 200) {
              this.article = response.data;
            }
          })
          .finally(() => {
            this.loading = false;
          });
    },
  },
  mounted() {
    this.getArticleDetail();
  }
};
</script>

<style scoped>
.article-detail {
  max-width: 900px;
  margin: 0 auto;
}

.article-header {
  margin-bottom: 30px;
  border-bottom: 1px solid #eee;
  padding-bottom: 20px;
}

.article-title {
  font-size: 28px;
  color: #333;
  margin-bottom: 15px;
  line-height: 1.4;
}

.article-meta {
  color: #999;
  font-size: 14px;
}

.article-meta span {
  margin-right: 20px;
}

.article-meta i {
  margin-right: 5px;
}

.article-content {
  line-height: 1.8;
  font-size: 16px;
  color: #333;
}

.article-content >>> p {
  margin-bottom: 20px;
}

.article-footer {
  margin-top: 40px;
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #eee;
}
</style>