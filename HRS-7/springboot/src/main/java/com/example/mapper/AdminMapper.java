package com.example.mapper;

import com.example.DTO.LeaveApplicationDTO;
import com.example.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 操作admin相关数据接口
*/
@Mapper
public interface AdminMapper {

    /**
      * 新增
    */
    int insert(Admin admin);

    /**
      * 删除
    */
    int deleteById(Integer id);

    /**
      * 修改
    */
    int updateById(Admin admin);

    /**
      * 根据ID查询
    */
    Admin selectById(Integer id);

    /**
      * 查询所有
    */
    List<Admin> selectAll(Admin admin);

    @Select("select * from `cq-hospital`.user where username = #{username}")
    Admin selectByUsername(String username);

    @Select("select * from `cq-hospital`.admin where account = #{account}")
    Account selectByAccount(String account);

    List<Integer> selectAllDoctorId(Integer adminId);

    //查找接待的患者数
    Integer selectReceivedPatientCount(Integer doctorId,String yearMonth);

    //查找请假数
    Integer selectLeaveApplicationCount(Integer doctorId,String yearMonth);

    void InsertWorkloadStatistics(Integer doctorId,String yearMonth,Integer receivedPatientCount,Integer leaveApplicationCount);

    void calculatePerformance(Integer doctorId,String yearMonth);

    @Select("select * from `cq-hospital`.logistics join `cq-hospital`.admin on admin.hospital_id = logistics.hospital_id where admin.id = #{adminId}")
    List<Logistics> selectLogistics(Integer adminId);

    @Update("update `cq-hospital`.logistics set status = 1,out_name = #{outName},out_address = #{outAddress},issued_time = #{date} where id = #{orderId}")
    void shipLogistics(Integer orderId, String outAddress, String outName, String date);


    List<RegistrationDetail> selectRegister(Integer adminId);

    @Insert("insert into `cq-hospital`.healtharticle(title, content, publish_time, view_count, like_count, category, status,hospital_id) VALUES (#{title}, #{content}, #{publishTime}, #{viewCount}, #{likeCount}, #{category}, #{status},#{hospitalId})")
    void addHealthArticle(HealthArticle healthArticle);

    @Select("select hospital_id from `cq-hospital`.admin where id = #{adminId}")
    Integer selectHospitalId(Integer hospitalId);

    @Insert("insert into `cq-hospital`.notice(title, content, publish_time, status,hospital_id) VALUES (#{title}, #{content}, #{publishTime}, #{status},#{hospitalId})")
    void addNotice(Notice notice);

    @Select("select * from `cq-hospital`.notice join `cq-hospital`.admin a on notice.hospital_id = a.hospital_id where a.id = #{id}")
    List<Notice> selectNotice(Integer id);

    @Update("UPDATE `cq-hospital`.notice SET status = CASE \n" +
            "                    WHEN status = 1 THEN 0\n" +
            "                    WHEN status = 0 THEN 1 END where id = #{id}")
    void toggleNewsStatus(Integer id);

    List<OperationLog> selectAllOperationLog(String module, String userName);

    @Select("select count(*) from `cq-hospital`.register where hospital_id = #{hospitalId}")
    Integer selectOrderCount(Integer hospitalId);


    Double selectRating(Integer hospitalId);

    @Update("update `cq-hospital`.hospital set level = #{i} where id = #{hospitalId}")
    void updateHospitalLevel(Integer hospitalId, int i);

    Double selectDepartmentRating(Integer hospitalId, Integer id);

    @Update("UPDATE `cq-hospital`.hospital SET specialty = #{specialty} WHERE  id = #{hospitalId}")
    void updateDepartmentSpecialty(Integer hospitalId, String specialty);

    void insertWorkloadStatistics(@Param("doctorId") Integer doctorId, @Param("yearMonth") String yearMonth, @Param("receivedPatientCount") Integer receivedPatientCount, @Param("leaveApplicationCount") Integer leaveApplicationCount, @Param("checkupCount") Integer checkupCount, @Param("surgeryCount") Integer surgeryCount);

    LeaveApplicationDTO selectWorkloadStatisticsByDoctorIdAndYearMonth(@Param("doctorId") Integer doctorId, @Param("yearMonth") String yearMonth);
}
