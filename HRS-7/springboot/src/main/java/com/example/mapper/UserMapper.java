package com.example.mapper;

import com.example.DTO.EvaluationDTO;
import com.example.DTO.WaitingQueuesDTO;
import com.example.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 操作user相关数据接口
*/
@Mapper
public interface UserMapper {

    /**
      * 新增
    */
    int insert(User user);

    /**
      * 删除
    */
    int deleteById(Integer id);

    /**
      * 修改
    */
    int updateById(User user);

    /**
      * 根据ID查询
    */
    User selectById(Integer id);

    /**
      * 查询所有
    */
    List<User> selectAll(User user);

    @Select("select * from `cq-hospital`.user where username = #{username}")
    User selectByUsername(String username);

    List<RegistrationDetail> selectRegistrations(Integer userId);

    @Select("select * from `cq-hospital`.user where account = #{account}")
    Account selectByAccount(String account);

    List<Hospitalization> selectHospitalizationById(Integer userId);

    HospitalizationInformation selectHospitalizationInformationById(Integer hospitalizationId);

    Report selectReportById(Integer reportId);

    @Select("select * from `cq-hospital`.daily_checklist join `cq-hospital`.information i on i.id = daily_checklist.information_id where i.in_report_id = #{id}")
    List<DailyCheckList> getDailyCheckListByHospitalizationId(Integer id);


    void insertEvaluation(@Param("evaluation") EvaluationDTO evaluation);

    List<EvaluationDTO> selectEvaluationsByDoctorId(@Param("doctorId") Integer doctorId);

    Double selectAverageEvaluationByDoctorId(@Param("doctorId") Integer doctorId);

    List<WaitingQueuesDTO> selectWaitingQueues(Integer doctorId,String registerTime,String today);

    @Select("select * from `cq-hospital`.register where user_id = #{userId} and register_time like concat('%', #{formattedDate}, '%')")
    Register selectRegisterToday(Integer userId, String formattedDate);

    @Select("select * from `cq-hospital`.healtharticle where hospital_id = #{hospitalId}")
    List<HealthArticle> selectHealthArticle(Integer hospitalId);

    @Select("select * from `cq-hospital`.healtharticle where id = #{articleId}")
    HealthArticle getHealthArticle(Integer articleId);

    @Update("update `cq-hospital`.healtharticle set view_count = view_count + 1 where id = #{articleId}")
    void updateViews(Integer articleId);

    @Update("update `cq-hospital`.healtharticle set like_count = like_count + 1 where id = #{id}")
    void likeArticle(Integer id);

    @Select("select * from `cq-hospital`.notice where hospital_id = #{hospitalId} and status = 0")
    List<Notice> selectNotice(Integer hospitalId);

    @Select("select * from `cq-hospital`.notice where id = #{id}")
    Notice getNotice(Integer id);
}