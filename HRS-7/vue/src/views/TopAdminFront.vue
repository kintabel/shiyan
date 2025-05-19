<template>
  <div class="admin-dashboard">
    <!-- 顶部横幅 -->
    <div class="admin-header">
      <div class="admin-header-left">
        <img src="../assets/imgs/logo.png" alt="医院 logo" class="logo" />
        <div class="title">顶级管理后台</div>
      </div>
      <div class="admin-header-right">
        <!-- 显示医生信息和操作 -->
        <el-dropdown>
          <div class="admin-info">
            <img :src="user.avatar" alt="管理员头像" class="admin-avatar" />
            <span class="admin-name">{{ user.name }}</span>
            <i class="el-icon-arrow-down"></i>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click.native="goToProfile">
                <i class="el-icon-user"></i>
                <span>个人信息</span>
              </el-dropdown-item>
              <el-dropdown-item @click.native="logout">
                <i class="el-icon-switch-button"></i>
                <span>退出登录</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 导航栏 -->
    <div class="admin-nav">
      <el-menu mode="horizontal" :default-active="activeMenu" router>



        <el-menu-item index="/topAdmin/hospital-rating">
          <template #title>
            <i class="el-icon-star-on"></i>
            <span>医院评级</span>
          </template>
        </el-menu-item>

        <el-menu-item index="/topAdmin/operation-log-tracking">
          <template #title>
            <i class="el-icon-s-data"></i>
            <span>操作日志追踪</span>
          </template>
        </el-menu-item>

        <el-menu-item index="/topAdmin/hospitals-specialize-in-diseases">
          <template #title>
            <i class="el-icon-date"></i>
            <span>医院擅长疾病</span>
          </template>
        </el-menu-item>

      </el-menu>
    </div>

    <!-- 主体内容 -->
    <div class="hospitalAdmin-main">
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
export default {
  name: "TopAdminFront",
  data() {
    return {
      user: JSON.parse(localStorage.getItem("xm-user") || '{}'),
    };
  },
  created() {
    const user = JSON.parse(localStorage.getItem("xm-user") || '{}');
    if (!user.id) {
      this.$router.push('/login'); // 如果未登录，跳转到登录页面
    }
  },
  methods: {
    logout() {
      localStorage.removeItem('xm-user');
      this.$router.push('/login'); // 跳转到登录页面
    },
    goToProfile() {
      this.$router.push('/hospitalAdmin/profile'); // 跳转到个人信息页面
    },
  },
};
</script>

<style scoped>
@import "@/assets/css/hospitalAdmin.css";
</style>