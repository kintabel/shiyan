import Vue from 'vue';
import VueRouter from 'vue-router';

Vue.use(VueRouter);

// 解决导航栏或者底部导航tabBar中的vue-router在3.0版本以上频繁点击菜单报错的问题。
const originalPush = VueRouter.prototype.push;
VueRouter.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err);
};

const routes = [
    {
        path: '/',
        name: 'User',
        component: () => import('../views/UserFront.vue'),
        redirect: '/home', // 重定向到主页
        children: [
            {
                path: 'Same-day-registration/reservation',
                name: 'Reservation',
                meta: {name: '当日挂号'},
                component: () => import('@/views/user/Reservation')
            },
            {
                path: 'appointment-registration/hospital/:id',
                name: 'HospitalDetail',
                meta: {name: '医院详情'},
                component: () => import('@/views/user/HospitalDetail')
            },
            {
                path: '/doctor/:hospitalId/:departmentId',
                name: 'Doctor',
                mata: {name: '医生列表'},
                component: () => import('@/views/user/Doctor.vue')
            },
            {
                path: '/registration/:doctorId',
                name: 'Registration',
                mata: {name: '预约挂号-选择医生'},
                component: () => import('@/views/user/Registration.vue')
            },
            {
                path: '/submit-registration',
                name: 'SubmitRegistration',
                mata: {name: '预约挂号-提交订单'},
                component: () => import('@/views/user/SubmitRegistration.vue')
            },
            {
                path: '/registration-success',
                name: 'RegistrationSuccess',
                mata: {name: '预约挂号-提交订单成功'},
                component: () => import('@/views/user/RegistrationSuccess.vue')
            },
            {
                path: '/registration-detail',
                name: 'OrderDetail',
                mata: {name: '预约挂号-订单详情'},
                component: () => import('@/views/user/RegistrationDetail.vue')
            },
            {
                path: 'appointmentRegister',
                name: 'AppointmentRegister',
                meta: {name: '预约挂号'},
                component: () => import('@/views/user/AppointmentRegister.vue')
            },
            {path: 'appeal', name: 'Appeal', meta: {name: '申诉'}, component: () => import('@/views/user/Appeal.vue')},
            {path: '403', name: 'NoAuth', meta: {name: '无权限'}, component: () => import('@/views/user/403')},
            {path: 'home', name: 'Home', meta: {name: '系统首页'}, component: () => import('@/views/user/Home')},
            {path: 'admin', name: 'Admin', meta: {name: '管理员信息'}, component: () => import('@/views/user/Admin')},
            {
                path: 'person',
                name: 'Person',
                meta: {name: '个人信息'},
                component: () => import('@/views/user/Person.vue')
            },
            {
                path: 'password',
                name: 'Password',
                meta: {name: '修改密码'},
                component: () => import('@/views/user/Password')
            },
            {
                path: 'selectRegistration',
                name: 'SelectRegistration',
                meta: {name: '查看挂号信息'},
                component: () => import('@/views/user/SelectRegistration.vue')
            },
            {
                path: 'selectRegistrationDetail',
                name: 'SelectRegistrationDetail',
                meta: {name: '查看挂号详情'},
                component: () => import('@/views/user/SelectRegistrationDetail.vue')
            },
            {path: 'notice', name: 'Notice', meta: {name: '公告信息'}, component: () => import('@/views/user/Notice')},
            {
                path: 'archives-management',
                name: 'ArchivesManagement',
                meta: {name: '档案管理'},
                component: () => import('@/views/user/ArchivesManagement')
            },
            {
                path: '/archives-management/archives-view',
                name: 'ArchivesView',
                meta: {name: '档案查看'},
                component: () => import('@/views/user/ArchivesView.vue')
            },
            {
                path: '/archives-management/archives-view/report',
                name: 'Report',
                meta: {name: '报告'},
                component: () => import('@/views/user/hospitalReport.vue')
            },
            {
                path: '/Waiting-queues',
                name: 'WaitingQueues',
                meta: {name: '排队信息'},
                component: () => import('@/views/user/WaitingQueue.vue')
            },
            {
                path: '/health-article',
                name: 'HealthArticleHospitalList',
                meta: {name: '健康文章'},
                component: () => import('@/views/user/HealthArticleHospitalList.vue')
            },
            {
                path: 'userHealthArticle',
                name: 'UserHealthArticles',
                meta: {name: '用户健康文章'},
                component: () => import('@/views/user/HealthArticleList.vue')
            },
            {
                path: 'userArticleDetail',
                name: 'UserArticleDetail',
                meta: {name: '文章详情'},
                component: () => import('@/views/user/HealthArticleDetail.vue')
            },
            {
                path: '/hospital-announcement',
                name: 'NoticeHospitalList',
                meta: {name: '医院公告'},
                component: () => import('@/views/user/NoticeHospitalList.vue')
            },
            {
                path: 'userNotice',
                name: 'UserNotice',
                meta: {name: '用户公告'},
                component: () => import('@/views/user/NoticeList.vue')
            },
            {
                path: 'userNoticeDetail',
                name: 'UserNoticeDetail',
                meta: {name: '公告详情'},
                component: () => import('@/views/user/NoticeDetail.vue')
            },
            {
                path: '/userOnlineChat',
                name: 'UserOnlineChat',
                meta: {name: '在线问诊'},
                component: () => import('@/views/user/UserChat.vue')
            }
        ],
    },
    {
        path: '/hospitalAdmin',
        name: 'HospitalAdmin',
        component: () => import('../views/HospitalAdminFront.vue'),
        redirect: '/hospitalAdmin/home', // 重定向到主页
        children: [
            {
                path: 'home',
                name: 'Home',
                meta: {name: '系统首页'},
                component: () => import('@/views/hospitalAdmin/Home')
            },
            {
                path: 'person',
                name: 'Person',
                meta: {name: '个人信息'},
                component: () => import('@/views/hospitalAdmin/Person')
            },
            {
                path: 'patientAppealList',
                name: "PatientAppealList",
                meta: {name: '患者申诉列表'},
                component: () => import('@/views/hospitalAdmin/PatientAppealList')
            },
            {
                path: 'appealDetail',
                name: "AppealDetail",
                meta: {name: '患者申诉详情'},
                component: () => import('@/views/hospitalAdmin/AppealDetail')
            },
            {
                path: '/hospitalAdmin/warehouse-management',
                name: 'WarehouseManagement',
                meta: {name: '仓库管理'},
                component: () => import('@/views/hospitalAdmin/WarehouseManagement')
            },
            {
                path: 'purchase',
                name: 'Purchase',
                meta: {name: '进货'},
                component: () => import('@/views/hospitalAdmin/Purchase.vue')
            },
            {
                path: 'scheduling',
                name: 'Scheduling',
                meta: {name: '排班'},
                component: () => import('@/views/hospitalAdmin/Scheduling.vue')
            },
            {
                path: 'workloadStatistics',
                name: 'WorkloadStatistics',
                meta: {name: '工作量统计'},
                component: () => import('@/views/hospitalAdmin/WorkLoad.vue')
            },
            {
                path: 'doctorList',
                name: 'DoctorList',
                meta: {name: '医生列表'},
                component: () => import('@/views/hospitalAdmin/DoctorList.vue')
            },
            {
                path: '/hospitalAdmin/logistics',
                name: 'Logistics',
                meta: {name: '物流管理'},
                component: () => import('@/views/hospitalAdmin/Logistics.vue')
            },
            {
                path: 'registerManagement',
                name: 'RegisterManagement',
                meta: {name: '挂号管理'},
                component: () => import('@/views/hospitalAdmin/Registration.vue')
            },
            {
                path: 'healthArticle',
                name: 'HealthArticle',
                meta: {name: '健康文章'},
                component: () => import('@/views/hospitalAdmin/HealthArticle.vue')
            },
            {
                path: 'adminNotice',
                name: 'AdminNotice',
                meta: {name: '公告信息'},
                component: () => import('@/views/hospitalAdmin/Notice.vue')
            }
        ],
    },
    {
        path: '/topAdmin',
        name: 'TopAdmin',
        component: () => import('../views/TopAdminFront.vue'),
        redirect: '/topAdmin/home', // 重定向到主页
        children: [
            {path: 'home', name: 'Home', meta: {name: '系统首页'}, component: () => import('@/views/topAdmin/Home')},
            {
                path: 'person',
                name: 'Person',
                meta: {name: '个人信息'},
                component: () => import('@/views/topAdmin/Person')
            },
            {
                path: '/topAdmin/operation-log-tracking',
                name: 'OperationLogTracking',
                meta: {name: '操作日志追踪'},
                component: () => import('@/views/topAdmin/OperationLog.vue')
            },
            {
                path: '/topAdmin/hospital-rating',
                name: 'HospitalListRating',
                meta: {name: '医院评分'},
                component: () => import('@/views/topAdmin/HospitalRating.vue')
            },
            {
                path: '/topAdmin/hospitals-specialize-in-diseases',
                name: 'HospitalListSpecialty',
                meta: {name: '医院擅长疾病'},
                component: () => import('@/views/topAdmin/HospitalSpecialty.vue')
            }
        ],
    },
    {
        path: '/doctor',
        name: 'Doctor',
        component: () => import('../views/DoctorFront.vue'), // 医生工作台的主组件
        redirect: '/doctor/home', // 默认重定向到今日预约
        children: [
            {
                path: 'home',
                name: 'DoctorHome',
                meta: {name: '医生工作台'},
                component: () => import('@/views/doctor/Home.vue'), // 医生工作台的主组件
            },
            {
                path: 'today-appointments',
                name: 'TodayAppointments',
                meta: {name: '今日预约'},
                component: () => import('@/views/doctor/TodayAppointments.vue'),
            },
            {
                path: 'prescribing-administration',
                name: 'PrescribingAdministration',
                meta: {name: '处方管理'},
                component: () => import('@/views/doctor/PrescribingAdministration.vue'),
            },
            {
                path: 'prescription',
                name: 'Prescription',
                meta: {name: '开处方'},
                component: () => import('@/views/doctor/Prescription.vue'),
            },
            {
                path: 'prescription-detail',
                name: 'PrescribeDetail',
                meta: {name: '处方详情'},
                component: () => import('@/views/doctor/PrescribeDetail.vue'),
            },
            {
                path: 'registration-detail',
                name: 'RegistrationDetail',
                meta: {name: '挂号详情'},
                component: () => import('@/views/doctor/RegistrationDetail.vue'),
            },
            {
                path: 'consultation',
                name: 'Consultation',
                meta: {name: '会诊管理'},
                component: () => import('@/views/doctor/Consultation.vue'),
            },
            {
                path: 'patient-management',
                name: 'PatientManagement',
                meta: {name: '患者管理'},
                component: () => import('@/views/doctor/PatientManagement.vue'),
            },
            {
                path: 'schedule-management',
                name: 'ScheduleManagement',
                meta: {name: '排班管理'},
                component: () => import('@/views/doctor/ScheduleManagement.vue'),
            },
            {
                path: 'medical-records',
                name: 'MedicalRecords',
                meta: {name: '病历管理'},
                component: () => import('@/views/doctor/MedicalRecords.vue'),
            },
            {
                path: 'message-center',
                name: 'MessageCenter',
                meta: {name: '消息中心'},
                component: () => import('@/views/doctor/DoctorChat.vue'),
            },
            {
                path: 'profile',
                name: 'DoctorProfile',
                meta: {name: '个人信息'},
                component: () => import('@/views/doctor/DoctorProfile.vue'),
            },
            {
                path: 'createArchive',
                name: 'CreateArchive',
                meta: {name: '创建档案'},
                component: () => import('@/views/doctor/CreateArchive.vue'),
            },
            {
                path: 'admission',
                name: 'Admission',
                meta: {name: '入院办理'},
                component: () => import('@/views/doctor/Admission.vue')
            },
            {
                path: 'archivesViewDoctor',
                name: 'ArchivesViewDoctor',
                meta: {name: '档案管理'},
                component: () => import('@/views/doctor/ArchivesView.vue')
            },
            {
                path: 'archivesAddDoctor',
                name: 'ArchivesAddDoctor',
                meta: {name: '添加档案信息'},
                component: () => import('@/views/doctor/ArchiveAddDoctor.vue')
            },
            {
                path: '/archives-management/archives-view/doctor-report',
                name: 'HospitalReport-doctor',
                meta: {name: '报告'},
                component: () => import('@/views/doctor/HospitalReport.vue')
            },
            {
                path: '/doctor/logistics-application',
                name: 'LogisticsApplication',
                meta: {name: '物流计费'},
                component: () => import('@/views/doctor/LogisticsApplication.vue')
            }
        ],
    },
    {path: '/login', name: 'Login', meta: {name: '登录'}, component: () => import('../views/Login.vue')},
    {path: '/register', name: 'Register', meta: {name: '注册'}, component: () => import('../views/Register.vue')},
    {path: '*', name: 'NotFound', meta: {name: '无法访问'}, component: () => import('../views/404.vue')},
];

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes,
});

export default router;