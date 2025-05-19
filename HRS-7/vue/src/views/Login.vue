<template>
  <div class="container">
    <div style="height: 60px; position: fixed; top: 0; display: flex;justify-items: center;padding-left: 20px">
      <img src="@/assets/imgs/logo.png" alt="" style="width: 50px">
      <span style="color: #409EFF; font-size: 24px;font-weight: bold;margin-left: 5px">医院挂号系统</span>
    </div>
    <div style="flex: 1;display: flex;justify-content: center">
      <img src="@/assets/imgs/bg.png" alt="" style="width: 80%">
    </div>

    <div style="flex: 1;display: flex;justify-content: center">
      <div
          style="width: 350px; position: relative; padding: 50px 30px; box-shadow: 0 0 10px rgba(0,0,0,.2); background-color: white; border-radius: 5px;">
        <div style="position: absolute;background-color:white; top:100px;z-index:999" v-if="slideVerifyShow">
          <slide-verify :l="42"
                        :r="10"
                        :w="300"
                        :h="155"
                        :accuracy="5"
                        :imgs="imgs"
                        slider-text="向右滑动"
                        @success="onSuccess"
                        @fail="onFail"
                        @refresh="onRefresh"
                        ref="slideVerifyRef"
          ></slide-verify>
        </div>
        <div style="text-align: center; font-size: 24px; font-weight: bold; margin-bottom: 30px; color: #333">欢 迎 登
          录
        </div>
        <el-form :model="form" :rules="rules" ref="formRef">
          <el-form-item prop="account">
            <el-input size="medium" prefix-icon="el-icon-user" placeholder="请输入账号"
                      v-model="form.account"></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input size="medium" prefix-icon="el-icon-lock" placeholder="请输入密码" show-password
                      v-model="form.password"></el-input>
          </el-form-item>

          <el-form-item>
            <el-button size="medium" style="width: 100%; background-color: #2a60c9; border-color: #2a60c9; color: white"
                       @click="login">登 录
            </el-button>
          </el-form-item>
          <div style="display: flex; align-items: center">
            <div style="flex: 1"></div>
            <div style="flex: 1; text-align: right">
              还没有账号？请 <a href="/register">注册</a>
            </div>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      form: {},
      rules: {
        account: [
          {required: true, message: '请输入账号', trigger: 'blur'},
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'},
        ]
      },
      imgs:[ //图形滑块验证的图片
        require('@/assets/imgs/hk1.jpg'),
        require('@/assets/imgs/hk2.jpg'),
        require('@/assets/imgs/hk3.jpg')
      ],
      slideVerifyShow:false  //默认滑块验证为不显示
    }
  },
  created() {

  },
  methods: {
    onSuccess(){
      this.$request.post('/login', this.form).then(res => {
        if (res.code === '200') {
          localStorage.setItem("xm-user", JSON.stringify(res.data))  // 存储用户数据
          this.$message.success('登录成功')

          setTimeout(() => {
            if (res.data.role === "ADMIN") {
              //this.$router.push('/')  // 跳转后台
              console.log('跳转后台')
              console.log(res.data.token)
              location.href = '/topAdmin'
            }
            else if(res.data.role === "HospitalADMIN"){
              location.href = '/hospitalAdmin' //本地跳转
            }else if(res.data.role === "USER"){
              //this.$router.push(('/front/home'))//路由跳转主页
              location.href = '/home' //本地跳转
            }else if(res.data.role === "DOCTOR"){
              console.log(res.data.token)
              //location.href = '/doctor/home'
              location.href = '/doctor'
            }
          })

        } else {
          this.$message.error(res.msg)
        }

      })
    },
    login() {
      this.$refs['formRef'].validate((valid) => {
        if (valid) {
          // 验证通过
          this.slideVerifyShow = true;  //滑块验证显示
        }
      })
    }
  }
}
</script>

<style scoped>
.container {
  height: 100vh;
  overflow: hidden;
//background-image: url("@/assets/imgs/bg.jpg"); background-size: 100%;
  display: flex;
  align-items: center;
//justify-content: center; //color: #666;
}

a {
  color: #2a60c9;
}
</style>