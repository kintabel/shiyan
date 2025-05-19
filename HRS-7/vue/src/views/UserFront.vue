<template>
  <div class="hospital-container">
    <!-- 顶部横幅 -->
    <div class="hospital-header">
      <div class="hospital-header-left">
        <img src="@/assets/imgs/logo.png" alt="医院 logo"/>
        <div class="title">非著名的沧秋医院</div>
      </div>
      <div class="hospital-header-right">
        <!-- 根据用户是否登录显示不同内容 -->
        <template v-if="user.id">
          <!-- 登录后显示头像和下拉菜单 -->
          <el-dropdown>
            <img :src="user.avatar" alt="用户头像" class="user-avatar"/>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click.native="goToPerson">查看个人信息</el-dropdown-item>
                <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
                <el-dropdown-item @click.native="selectRegistration">查看挂号信息</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <!-- 未登录显示登录按钮 -->
          <el-button type="text" @click="$router.push('/login')">登录</el-button>
        </template>
      </div>
    </div>

    <!-- 导航栏 -->
    <div class="hospital-nav">
      <el-menu mode="horizontal" :default-openeds="[]" router style="border: none" :default-active="$route.path">
        <!-- 首页 -->
        <el-menu-item index="/home">
          <template #title>
            <i class="el-icon-date"></i>
            <span>首页</span>
          </template>
        </el-menu-item>

        <!-- 挂号 -->
        <el-submenu index="register">
          <template #title>
            <i class="el-icon-medal"></i>
            <span>挂号</span>
          </template>
          <el-menu-item @click="goToAppointmentRegistration(1)">预约挂号</el-menu-item>
          <el-menu-item index="/Same-day-registration/reservation">当日挂号</el-menu-item>
        </el-submenu>


        <!-- 在线问诊 -->
        <el-menu-item index="/userOnlineChat">
          <template #title>
            <i class="el-icon-user-solid"></i>
            <span>在线问诊</span>
          </template>
        </el-menu-item>

        <!-- 个人档案管理 -->
        <el-menu-item   @click="goToArchieve()">个人档案管理</el-menu-item>

        <!-- 候诊队列查询 -->
        <el-menu-item @click="goToWaitingQueue">
          <template #title>
            <i class="el-icon-guide"></i>
            <span>候诊队列查询</span>
          </template>
        </el-menu-item>

        <!-- 医院公告 -->
        <el-menu-item index="/hospital-announcement">
          <template #title>
            <i class="el-icon-bell"></i>
            <span>医院公告</span>
          </template>
        </el-menu-item>

        <!-- 健康科普 -->
        <el-menu-item index="/health-article">
          <template #title>
            <i class="el-icon-view"></i>
            <span>健康科普</span>
          </template>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 主体内容 -->
    <div class="hospital-main">
      <!-- 侧边栏 -->
      <div class="hospital-main-left">

      </div>

      <!-- 数据表格 -->
      <div class="hospital-main-right">
        <router-view @update:user="updateUser"/>
      </div>
    </div>

    <!-- 底部信息 -->
    <div class="hospital-footer">
      <p>联系电话：010 - 20040520</p>
      <p>地址：天津市 XX 区 找得到算你厉害</p>
      <p>版权所有 &copy; 2025 沧秋sir</p>
    </div>
  </div>
</template>

<script>
export default {
  name: "HospitalManager",
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
    };
  },
  created() {
    if (!this.user.id) {
      this.$router.push('/login');
    }
  },
  methods: {
    updateUser() {
      this.user = JSON.parse(localStorage.getItem('xm-user') || '{}'); // 重新获取下用户的最新信息
    },
    goToPerson() {
      if (this.user.role === 'ADMIN') {
        this.$router.push('/adminPerson');
      } else {
        this.$router.push('/person'); // 普通用户跳转到个人信息页面
      }
    },
    logout() {
      localStorage.removeItem('xm-user'); // 清除用户信息
      console.log('用户已退出登录'); // 调试日志
      this.$router.push("/login").catch(err => {
        console.error('路由跳转失败:', err); // 捕获路由跳转错误
      });
    },
    selectRegistration() {
      this.$router.push("/selectRegistration");
    },
    goToAppointmentRegistration(id) {
      if (id = 1) {
        this.$router.push({
          path: '/Same-day-registration/reservation',
          query: {
            isAppointment: true
          }
        });
      } else {
        this.$router.push({
          path: '/Same-day-registration/reservation',
          query: {
            isAppointment: false
          }
        })
      }
    },
    goToArchieve() {
      this.$router.push("/archives-management");
    },
    goToWaitingQueue() {
      this.$router.push("/Waiting-queues")
    }
  },
};
</script>

<style scoped>
@import "@/assets/css/user-front.css";

.user-avatar {
  cursor: pointer;
  width: 40px;
  height: 40px;
  border-radius: 50%;
}
</style>