<template>
  <div class="add-archive-container">
    <!-- 添加返回按钮 -->
    <el-button type="text" @click="$router.go(-1)">返回</el-button>
    <h4>住院信息管理</h4>

    <!-- 加载状态 -->
    <div v-if="isLoading" class="loading">加载中...</div>

    <!-- 错误提示 -->
    <div v-if="error" class="error">{{ error }}</div>

    <el-form ref="form" :model="form" label-width="120px" :rules="rules">
      <!-- 基本信息部分 -->
      <el-card class="box-card" shadow="hover">
        <div slot="header" class="clearfix">
          <span>基本信息</span>
        </div>

        <el-form-item label="疾病诊断" prop="disease">
          <el-input
              type="textarea"
              :rows="2"
              placeholder="请输入疾病诊断"
              v-model="form.disease">
          </el-input>
        </el-form-item>
      </el-card>

      <!-- 手术信息部分 -->
      <el-card class="box-card" shadow="hover" style="margin-top: 20px;">
        <div slot="header" class="clearfix">
          <span>手术信息</span>
        </div>
        <el-form-item label="手术信息" prop="surgery">
          <el-input
              type="textarea"
              :rows="2"
              placeholder="请输入手术信息"
              v-model="form.surgery">
          </el-input>
        </el-form-item>
      </el-card>

      <!-- 每日清单部分 -->
      <el-card class="box-card" shadow="hover" style="margin-top: 20px;">
        <div slot="header" class="clearfix">
          <span>每日清单</span>
          <el-button
              style="float: right; padding: 3px 0"
              type="text"
              @click="showAddDailyDialog = true">
            添加今日清单
          </el-button>
        </div>

        <el-table :data="dailyChecklists" stripe>
          <el-table-column prop="date" label="日期" width="150"></el-table-column>
          <el-table-column prop="medicine" label="每日开药"></el-table-column>
          <el-table-column prop="transfusion" label="输液"></el-table-column>
          <el-table-column prop="surgery" label="手术安排"></el-table-column>
          <el-table-column prop="surgeryTime" label="手术时间" width="180"></el-table-column>
          <el-table-column prop="dailySituation" label="每日情况"></el-table-column>
          <el-table-column prop="cost" label="每日开销" width="100"></el-table-column>
        </el-table>
      </el-card>

      <div class="form-footer">
        <el-button type="primary" @click="submitForm">保存</el-button>
        <el-button @click="cancel">取消</el-button>
      </div>
    </el-form>

    <!-- 添加每日清单对话框 -->
    <el-dialog title="添加今日清单" :visible.sync="showAddDailyDialog" width="50%">
      <el-form :model="newDailyChecklist" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="每日开药">
              <el-input v-model="newDailyChecklist.medicine" placeholder="请输入药品及用量"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="输液">
              <el-input v-model="newDailyChecklist.transfusion" placeholder="请输入输液信息"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手术安排">
              <el-input v-model="newDailyChecklist.surgery" placeholder="如有手术请填写" @change="handleSurgeryChange"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="newDailyChecklist.surgery && newDailyChecklist.surgery.trim() !== ''">
            <el-form-item label="手术时间">
              <el-date-picker
                  v-model="newDailyChecklist.surgeryTime"
                  type="datetime"
                  placeholder="选择手术时间"
                  value-format="yyyy-MM-dd HH:mm:ss">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="每日情况">
          <el-input
              type="textarea"
              :rows="2"
              placeholder="记录患者每日情况"
              v-model="newDailyChecklist.dailySituation">
          </el-input>
        </el-form-item>

        <el-form-item label="每日开销">
          <el-input-number v-model="newDailyChecklist.cost" :precision="2" :step="0.1" :min="0"></el-input-number>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showAddDailyDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAddDailyChecklist">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { parseTime } from '@/utils'

export default {
  name: 'ArchivesAddDoctor',
  data() {
    return {
      isLoading: false,
      error: '',
      showAddDailyDialog: false,
      hospitalizationId: this.$route.query.hospitalizationId || '',
      form: {
        disease: '',
        surgery: '',
      },
      dailyChecklists: [],
      newDailyChecklist: {
        hospitalizationIdOrinformationId: this.$route.query.hospitalizationId || '',
        date: parseTime(new Date(), '{y}-{m}-{d}'),
        medicine: '',
        transfusion: '',
        surgery: '',
        surgeryTime: null,
        dailySituation: '',
        cost: 0
      },
      rules: {
        disease: [
          { required: true, message: '请输入疾病诊断', trigger: 'blur' }
        ],
      }
    }
  },
  async created() {
    await this.fetchExistingData();
  },
  methods: {
    // 获取已有数据
    async fetchExistingData() {
      this.isLoading = true;
      this.error = '';
      try {
        // 获取疾病和手术信息
        const infoRes = await this.$request.get(`doctor/information/getByHospitalizationId/${this.hospitalizationId}`);
        if (infoRes.code == 200 && infoRes.data) {
          this.form.disease = infoRes.data.disease || '';
          this.form.surgery = infoRes.data.surgery || '';
        }

        // 获取全部每日清单
        const dailyRes = await this.$request.get(`doctor/daily/getByHospitalizationId/${this.hospitalizationId}`);
        if (dailyRes.code == 200 && dailyRes.data) {
          this.dailyChecklists = dailyRes.data.map(item => ({
            ...item,
            date: item.date || parseTime(new Date(), '{y}-{m}-{d}'),
            surgeryTime: item.surgeryTime || null
          }));
        }
      } catch (error) {
        console.error('获取数据失败:', error);
        this.error = '获取数据失败，请稍后重试';
      } finally {
        this.isLoading = false;
      }
    },

    // 处理手术安排变化
    handleSurgeryChange() {
      if (!this.newDailyChecklist.surgery || this.newDailyChecklist.surgery.trim() === '') {
        this.newDailyChecklist.surgeryTime = null;
      }
    },

    // 添加每日清单
    async handleAddDailyChecklist() {
      try {
        // 检查今天是否已有记录
        const todayRecord = this.dailyChecklists.find(item => item.date === this.newDailyChecklist.date);
        if (todayRecord) {
          this.$message.warning('今天已存在记录，请编辑现有记录');
          return;
        }

        // 提交单个每日清单
        const res = await this.$request.post('doctor/daily/add', {
          ...this.newDailyChecklist
        });

        if (res.code == 200) {
          this.$message.success('添加成功');
          this.showAddDailyDialog = false;
          await this.fetchExistingData(); // 重新加载全部清单
        } else {
          this.$message.error(res.message || '添加失败');
        }
      } catch (error) {
        console.error('添加失败:', error);
        this.$message.error('添加失败，请稍后重试');
      }
    },


    // 提交表单
    async submitForm() {
      try {
        await this.$refs.form.validate();

        // 提交基本信息
        const infoRes = await this.$request.post('doctor/information/update', {
          ...this.form,
          hospitalizationId: this.hospitalizationId
        });

        if (infoRes.code !== 200) {
          throw new Error(infoRes.message || '更新基本信息失败');
        }

        this.$message.success('保存成功');
        this.$router.go(-1);
      } catch (error) {
        this.$message.error(error.message || '提交失败');
        console.error('提交失败:', error);
      }
    },

    // 取消
    cancel() {
      this.$router.go(-1);
    }
  }
}
</script>

<style scoped>
.add-archive-container {
  padding: 20px;
}

.loading,
.error {
  text-align: center;
  margin-top: 20px;
  font-size: 16px;
  color: #666;
}

.error {
  color: #f56c6c;
}

.box-card {
  margin-bottom: 20px;
}

.form-footer {
  margin-top: 20px;
  text-align: center;
}
</style>