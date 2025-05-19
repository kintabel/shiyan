package com.example.mapper;

import com.example.entity.DiskFiles;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FilesMapper {
    @Insert("insert into `cq-hospital`.files (name, user_id, file, type, create_time, size,disk_name) values (#{name}, #{userId}, #{file}, #{type}, #{createTime}, #{size},#{diskName})")
    void insert(DiskFiles files);

    @Select("select id from `cq-hospital`.files where name = #{fileName}")
    Integer selectByFileName(String fileName);

    @Select("select * from `cq-hospital`.files where name = #{fileName}")
    DiskFiles selectByName(String fileName);
}
