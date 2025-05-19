<template>
  <div class="doctor-dashboard">
    <!-- 顶部横幅 -->
    <div class="doctor-header">
      <div class="doctor-header-left">
        <img src="../assets/imgs/logo.png" alt="医院 logo" />
        <div class="title">医生工作台</div>
      </div>
      <div class="doctor-header-right">
        <!-- 显示医生信息和操作 -->
        <el-dropdown>
          <div class="doctor-info">
            <img :src="user.avatar" alt="医生头像" class="doctor-avatar" />
            <span class="doctor-name">{{ user.name }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click.native="goToProfile">个人信息</el-dropdown-item>
              <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 导航栏 -->
    <div class="doctor-nav">
      <el-menu mode="horizontal" :default-active="activeMenu" router>
        <!-- 首页    -->
        <el-menu-item index="/doctor/home">
          <template #title>
            <i class="el-icon-s-home"></i>
            <span>首页</span>
          </template>
        </el-menu-item>
        <!-- 今日预约 -->
        <el-menu-item index="/doctor/today-appointments">
          <template #title>
            <i class="el-icon-date"></i>
            <span>今日预约</span>
          </template>
        </el-menu-item>

        <!-- 开药管理 -->
        <el-menu-item index="/doctor/prescribing-administration">
          <template #title>
            <i class="el-icon-s-order"></i>
            <span>开药管理</span>
          </template>
        </el-menu-item>

        <!-- 患者管理 -->
        <el-menu-item index="/doctor/patient-management">
          <template #title>
            <i class="el-icon-user"></i>
            <span>患者管理</span>
          </template>
        </el-menu-item>

        <!-- 排班管理 -->
        <el-menu-item index="/doctor/schedule-management">
          <template #title>
            <i class="el-icon-date"></i>
            <span>排班管理</span>
          </template>
        </el-menu-item>

        <!-- 病历管理 -->
        <el-menu-item index="/doctor/medical-records">
          <template #title>
            <i class="el-icon-document"></i>
            <span>病历管理</span>
          </template>
        </el-menu-item>

        <!-- 消息中心 -->
        <el-menu-item index="/doctor/message-center">
          <template #title>
            <i class="el-icon-message"></i>
            <span>消息中心</span>
          </template>
        </el-menu-item>

        <el-menu-item index="/doctor/logistics-application">
          <template #title>
            <i class="el-icon-edit-outline"></i>
            <span>物流申请</span>
          </template>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 主体内容 -->
    <div class="doctor-main">
      <router-view></router-view>
    </div>

  </div>
</template>

<script>
export default {
  name: "DoctorFront",
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
      localStorage.removeItem('xm-user'); // 清除医生登录信息
      this.$router.push('/login'); // 跳转到登录页面
    },
    goToProfile() {
      this.$router.push('/doctor/profile'); // 跳转到个人信息页面
    },
  },
};
</script>

<style scoped>
@import "@/assets/css/doctor-front.css";
</style>