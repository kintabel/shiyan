<template>
  <div class="admin-dashboard">
    <!-- 顶部横幅 -->
    <div class="admin-header">
      <div class="admin-header-left">
        <img src="../assets/imgs/logo.png" alt="医院 logo" class="logo" />
        <div class="title">医院管理后台</div>
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
        <!-- 首页 -->
        <el-menu-item @click="goToNotice">
          <template #title>
            <i class="el-icon-s-home"></i>
            <span>医院信息管理</span>
          </template>
        </el-menu-item>

        <el-menu-item @click="goToDoctorList">
          <template #title>
            <i class="el-icon-user-solid"></i>
            <span>医生管理</span>
          </template>
        </el-menu-item>


        <el-menu-item @click="gotoWorkLoad">
          <template #title>
            <i class="el-icon-s-data"></i>
            <span>绩效核算</span>
          </template>
        </el-menu-item>

        <!-- 排班管理 -->
        <el-menu-item @click="goToScheduling" >
          <template #title>
            <i class="el-icon-date"></i>
            <span>排班管理</span>
          </template>
        </el-menu-item>

        <el-menu-item @click="goToRegistration">
          <template #title>
            <i class="el-icon-tickets"></i>
            <span>订单管理</span>
          </template>
        </el-menu-item>

        <el-menu-item index="/hospitalAdmin/warehouse-management">
          <template #title>
            <i class="el-icon-truck"></i>
            <span>仓库管理</span>
          </template>
        </el-menu-item>

        <el-menu-item @click="goToLogistics()">
          <template #title>
            <i class="el-icon-tickets"></i>
            <span>物流管理</span>
          </template>
        </el-menu-item>

        <!-- 科普文章 -->
        <el-menu-item @click="goToHealthArticle">
          <template #title>
            <i class="el-icon-document"></i>
            <span>科普文章</span>
          </template>
        </el-menu-item>

        <!-- 患者申诉 -->
        <el-menu-item @click="goToPatientAppeal()">
          <template #title>
            <i class="el-icon-warning"></i>
            <span>患者申诉</span>
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
  name: "HospitalAdminFront",
  data() {
    return {
      user: JSON.parse(localStorage.getItem("xm-user") || '{}'),
      activeMenu: "/hospitalAdmin/hospital-information-management"
    };
  },
  created() {
    const user = JSON.parse(localStorage.getItem("xm-user") || '{}');
    console.log(user.id);
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
    goToPatientAppeal() {
      this.$router.push({
        name: 'PatientAppealList',
        query: {
          userId : this.user.id,
        },
      }); // 跳转到患者申诉页面
    },

    goToScheduling() {
      this.$router.push({
        name: 'Scheduling',
        query: {
          userId : this.user.id,
        },
      });
    },
    gotoWorkLoad(){
      this.$router.push({
        name: 'WorkloadStatistics',
        query: {
          userId : this.user.id,
        }
      })
    },
    goToDoctorList(){
      this.$router.push({
        name: 'DoctorList',
        query: {
          userId : this.user.id,
        }
      })
    },

    goToLogistics(){
      this.$router.push({
        name: 'Logistics',
        query: {
          userId : this.user.id,
        }
      })
    },

    goToRegistration(){
      this.$router.push({
        name: 'RegisterManagement',
        query: {
          userId : this.user.id,
        }
      })
    },

    goToHealthArticle(){
      this.$router.push({
        name: 'HealthArticle',
        query: {
          userId : this.user.id,
        }
      })
    },
    goToNotice(){
      this.$router.push({
        name: 'AdminNotice',
        query: {
          userId : this.user.id,
        }
      })
    }
  },
};
</script>

<style scoped>
@import "@/assets/css/hospitalAdmin.css";
</style>