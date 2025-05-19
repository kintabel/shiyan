<template>
  <div class="hospital-detail-form">
    <!-- 数据加载中提示 -->
    <div v-if="isLoading" class="loading-container">
      <el-spinner />
      <span>正在加载数据...</span>
    </div>
    <div v-else>
      <el-form label-width="120px">
        <el-form-item label="医院名称">
          <span>{{ hospital.name }}</span>
        </el-form-item>
        <el-form-item label="医院地址">
          <span>{{ hospital.address }}</span>
        </el-form-item>
        <el-form-item label="联系电话">
          <span>{{ hospital.phone }}</span>
        </el-form-item>
        <el-form-item label="医院等级">
          <span>{{ getHospitalLevel(hospital.level) }}</span>
        </el-form-item>
        <el-form-item label="医院简介">
          <span>{{ hospital.introduction }}</span>
        </el-form-item>
        <el-form-item label="科室列表">
          <ul class="department-list">
            <li v-for="(department, index) in hospital.departments" :key="index">
              <!-- 使用 router-link 跳转到医生页面 -->
              <router-link :to="getDoctorRoute(department.id)" class="department-link">
                {{ department.name}}
              </router-link>
            </li>
          </ul>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'HospitalDetail',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      hospital: {
        name: '',
        address: '',
        phone: '',
        level: '',
        introduction: '',
        departments: [],
        isAppointment: this.$route.query.isAppointment,
      },
      isLoading: true
    };
  },
  created() {
    this.fetchHospitalDetail();
  },
  methods: {
    fetchHospitalDetail() {
      const hospitalId = this.$route.params.id; // 从路由参数中获取医院 ID
      this.$request.get(`/hospital/selectAllData?id=${hospitalId}`)
          .then(response => {
            if (response.data) {
              this.hospital = response.data;
            } else {
              console.error('获取医院详情数据格式错误:', response.data);
              this.$message.error('获取医院详情失败，请稍后重试');
            }
          })
          .catch(error => {
            console.error('获取医院详情失败:', error);
            this.$message.error('获取医院详情失败，请稍后重试');
          })
          .finally(() => {
            this.isLoading = false;
          });
    },
    // 根据 level 值返回对应的等级名称
    getHospitalLevel(level) {
      const levelMap = {
        1: '1级丙等',
        2: '1级乙等',
        3: '1级甲等',
        4: '2级丙等',
        5: '2级乙等',
        6: '2级甲等',
        7: '3级丙等',
        8: '3级乙等',
        9: '3级甲等'
      };
      return levelMap[level] || '未知等级'; // 如果 level 不在映射表中，返回 '未知等级'
    },
    // 生成跳转到医生页面的路由
    getDoctorRoute(departmentId) {
      const hospitalId = this.$route.params.id; // 获取当前医院 ID
      const isAppointment = this.$route.query.isAppointment;
      console.log(isAppointment)
      if(isAppointment){
        return {
          name:'AppointmentRegister',
          params:{
            hospitalId: hospitalId,
            departmentId: departmentId,
          },
        }
      }else{
        return {
          name: 'Doctor', // 路由名称
          params: {
            hospitalId: hospitalId, // 传递医院 ID
            departmentId: departmentId, // 传递科室 ID
          }
        };
      }
    }
  }
};
</script>

<style scoped>
.hospital-detail-form {
  padding: 20px;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 50px;
  margin-bottom: 20px;
}

.department-list {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.department-link {
  cursor: pointer;
  color: #409eff;
  text-decoration: none;
}

.department-link:hover {
  text-decoration: underline;
}
</style>